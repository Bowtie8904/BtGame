package bt.game.core.ctrl.spec.mouse;

import bt.game.core.container.abstr.GameContainer;
import bt.game.core.ctrl.spec.mouse.intf.MouseTarget;
import bt.game.core.scene.cam.Camera;
import bt.game.core.scene.intf.Scene;
import bt.game.util.unit.Unit;
import bt.scheduler.Threads;
import org.dyn4j.geometry.Vector2;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

/**
 * @author &#8904
 */
public class MouseController extends MouseAdapter
{
    private static MouseController instance;

    public static MouseController get()
    {
        return MouseController.instance;
    }

    private Consumer<MouseTarget> onRightClick;
    private Consumer<MouseTarget> onLeftClick;
    private Comparator<MouseTarget> zComparator;
    private List<MouseTarget> mouseTargets;
    private GameContainer component;
    private int lastClickMouseX;
    private int lastClickMouseY;
    private double mouseX;
    private double mouseY;
    private MouseTarget lastClickedTarget;
    private MouseTarget lastHoveredTarget;

    public MouseController(GameContainer component)
    {
        MouseController.instance = this;
        this.component = component;
        this.component.addMouseListener(this);
        this.component.addMouseMotionListener(this);
        this.component.addMouseWheelListener(this);
        this.mouseTargets = new CopyOnWriteArrayList<>();

        this.zComparator = new Comparator<MouseTarget>()
        {
            @Override
            public int compare(MouseTarget o1, MouseTarget o2)
            {
                if (o1.getZ().units() == o2.getZ().units())
                {
                    return 0;
                }
                else if (o1.getZ().units() < o2.getZ().units())
                {
                    return -1;
                }
                else if (o1.getZ().units() > o2.getZ().units())
                {
                    return 1;
                }
                return 0;
            }
        }.reversed();
    }

    public Unit getMouseX()
    {
        return Unit.forPixels(this.mouseX);
    }

    public Unit getMouseY()
    {
        return Unit.forPixels(this.mouseY);
    }

    public synchronized void addMouseTarget(MouseTarget target)
    {
        this.mouseTargets.add(target);
    }

    public synchronized void removeMouseTarget(MouseTarget target)
    {
        this.mouseTargets.remove(target);
    }

    public void setGlobalOnRightClick(Consumer<MouseTarget> onClick)
    {
        this.onRightClick = onClick;
    }

    public void setGlobalOnLeftClick(Consumer<MouseTarget> onClick)
    {
        this.onLeftClick = onClick;
    }

    public void clearMouseTargets()
    {
        this.mouseTargets.clear();
    }

    public void clearMouseTargets(Scene scene)
    {
        this.mouseTargets.stream()
                         .filter(m -> m.getScene().equals(scene))
                         .forEach(this.mouseTargets::remove);
    }

    public void checkHover()
    {
        if (this.lastClickedTarget != null)
        {
            // dont switch to another hover target if we havnt released our last click yet, i.e. are still dragging the
            // target around
            return;
        }

        if (this.component.getMousePosition() != null)
        {
            Point p = this.component.getMousePosition();
            if (p != null)
            {
                double mx = p.getX();
                double my = p.getY();

                double camX = 0;
                double camY = 0;

                double x = 0;
                double y = 0;

                if (Camera.currentCamera != null)
                {
                    camX = Camera.currentCamera.getX().pixels();
                    camY = Camera.currentCamera.getY().pixels();
                }

                this.mouseX = mx + camX;
                this.mouseY = my + camY;

                boolean foundOne = false;

                sortTargets();

                for (MouseTarget target : this.mouseTargets)
                {
                    if (target.affectedByCamera())
                    {
                        x = mx + camX;
                        y = my + camY;
                    }
                    else
                    {
                        x = mx;
                        y = my;
                    }

                    if (target.getShape()
                              .contains(new Vector2(x,
                                                    y)))
                    {
                        if (!target.equals(this.lastHoveredTarget))
                        {
                            if (this.lastHoveredTarget != null)
                            {
                                this.lastHoveredTarget.afterHover();
                            }

                            this.lastHoveredTarget = target;
                            target.onHover();
                        }
                        foundOne = true;
                        break;
                    }
                }

                if (!foundOne && this.lastHoveredTarget != null)
                {
                    this.lastHoveredTarget.afterHover();
                    this.lastHoveredTarget = null;
                }
            }
        }
        else
        {
            if (this.lastHoveredTarget != null)
            {
                this.lastHoveredTarget.afterHover();
            }
        }
    }

    // TODO replace Points with primitives
    @Override
    public void mousePressed(MouseEvent e)
    {
        this.lastClickMouseX = e.getX();
        this.lastClickMouseY = e.getY();

        Point pBase = new Point(this.lastClickMouseX,
                                this.lastClickMouseY);
        Point p;
        Point pCam = null;

        if (Camera.currentCamera != null)
        {
            pCam = new Point((int)(this.lastClickMouseX + Camera.currentCamera.getX().pixels()),
                             (int)(this.lastClickMouseY + Camera.currentCamera.getY().pixels()));
        }
        else
        {
            pCam = pBase;
        }

        double camX = 0;
        double camY = 0;

        if (Camera.currentCamera != null)
        {
            camX = Camera.currentCamera.getX().pixels();
            camY = Camera.currentCamera.getY().pixels();
        }

        if (e.getButton() == MouseEvent.BUTTON1 || e.getButton() == MouseEvent.BUTTON3)
        {
            sortTargets();

            for (MouseTarget target : this.mouseTargets)
            {
                if (target.affectedByCamera())
                {
                    p = pCam;
                }
                else
                {
                    p = pBase;
                }

                if (target.getShape()
                          .contains(new Vector2(p.x,
                                                p.y)))
                {
                    final Point finalPoint = p;

                    if (e.getButton() == MouseEvent.BUTTON1)
                    {
                        this.lastClickedTarget = target; // used for dragging. only used with left mouse button
                        onLeftClick(target);

                        Threads.get().executeCached(() ->
                                                    {
                                                        target.onLeftClick(e,
                                                                           Unit.forPixels(finalPoint.x),
                                                                           Unit.forPixels(finalPoint.y));
                                                    });
                    }
                    else if (e.getButton() == MouseEvent.BUTTON3)
                    {
                        onRightClick(target);
                        Threads.get().executeCached(() ->
                                                    {
                                                        target.onRightClick(e,
                                                                            Unit.forPixels(finalPoint.x),
                                                                            Unit.forPixels(finalPoint.y));
                                                    });
                    }
                    return;
                }
            }
        }
    }

    @Override
    public void mouseReleased(MouseEvent e)
    {
        if (e.getButton() == MouseEvent.BUTTON1)
        {
            this.lastClickedTarget = null;
        }
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e)
    {
        if (this.lastClickedTarget != null)
        {
            // dont switch to another target if we havnt released our last click yet, i.e. are still dragging the
            // target around
            this.lastClickedTarget.onMouseWheelMove(e,
                                                    e.getWheelRotation());
        }
        else
        {
            this.lastClickMouseX = e.getX();
            this.lastClickMouseY = e.getY();

            Point p = new Point(this.lastClickMouseX,
                                this.lastClickMouseY);

            if (Camera.currentCamera != null)
            {
                p = new Point((int)(this.lastClickMouseX + Camera.currentCamera.getX().pixels()),
                              (int)(this.lastClickMouseY + Camera.currentCamera.getY().pixels()));
            }

            sortTargets();

            for (MouseTarget target : this.mouseTargets)
            {
                if (target.getShape()
                          .contains(new Vector2(p.x,
                                                p.y)))
                {
                    target.onMouseWheelMove(e,
                                            e.getWheelRotation());
                    return;
                }
            }
        }
    }

    @Override
    public void mouseDragged(MouseEvent e)
    {
        if (this.lastClickedTarget != null)
        {
            this.lastClickedTarget.onDrag(e,
                                          Unit.forPixels(e.getX() - this.lastClickMouseX),
                                          Unit.forPixels(e.getY() - this.lastClickMouseY));

            this.lastClickMouseX = e.getX();
            this.lastClickMouseY = e.getY();
        }
    }

    private synchronized void sortTargets()
    {
        this.mouseTargets.sort(this.zComparator);
    }

    private void onRightClick(MouseTarget target)
    {
        if (this.onRightClick != null)
        {
            this.onRightClick.accept(target);
        }
    }

    private void onLeftClick(MouseTarget target)
    {
        if (this.onLeftClick != null)
        {
            this.onLeftClick.accept(target);
        }
    }

    /**
     * Sets the comparator that is used to sort mousetargets before checking hoverring.
     *
     * <p>
     * If the cursor is hovering over multiple targets, the one with the lowest index in the list after applying the
     * comparator will be used.
     * </p>
     *
     * @param comp
     */
    public void setTargetComparator(Comparator<MouseTarget> comp)
    {
        this.zComparator = comp;
    }
}