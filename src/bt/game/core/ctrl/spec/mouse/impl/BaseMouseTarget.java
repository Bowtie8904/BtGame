package bt.game.core.ctrl.spec.mouse.impl;

import bt.game.core.ctrl.spec.mouse.intf.MouseTarget;
import bt.game.core.scene.intf.Scene;
import bt.game.util.unit.Unit;
import org.dyn4j.geometry.Geometry;
import org.dyn4j.geometry.Shape;

import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

/**
 * A defualt implementation of the {@link MouseTarget} interface which uses a rectangular shape as the target area.
 *
 * @author &#8904
 */
public class BaseMouseTarget implements MouseTarget
{
    protected Unit x;
    protected Unit y;
    protected Unit z;
    protected Unit w;
    protected Unit h;
    protected Shape shape;
    protected Scene scene;
    protected boolean affectedByCamera;

    public BaseMouseTarget(Scene scene, Unit x, Unit y, Unit z, Unit w, Unit h)
    {
        this.scene = scene;
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
        this.z = z;
        this.affectedByCamera = true;
        createShape();
    }

    protected void createShape()
    {
        this.shape = Geometry.createRectangle(this.w.pixels(),
                                              this.h.pixels());
        this.shape.translate(this.x.pixels() + (this.w.pixels() / 2),
                             this.y.pixels() + (this.h.pixels() / 2));
    }

    /**
     * @see bt.game.core.ctrl.spec.mouse.intf.MouseTarget#onRightClick()
     */
    @Override
    public void onRightClick(MouseEvent e, Unit x, Unit y)
    {
    }

    /**
     * @see bt.game.core.ctrl.spec.mouse.intf.MouseTarget#onLeftClick()
     */
    @Override
    public void onLeftClick(MouseEvent e, Unit x, Unit y)
    {
    }

    /**
     * @see bt.game.core.ctrl.spec.mouse.intf.MouseTarget#onDrag(bt.game.util.unit.Unit, bt.game.util.unit.Unit)
     */
    @Override
    public void onDrag(MouseEvent e, Unit xOffset, Unit yOffset)
    {
    }

    /**
     * @see bt.game.core.ctrl.spec.mouse.intf.MouseTarget#onHover()
     */
    @Override
    public void onHover()
    {
    }

    /**
     * @see bt.game.core.ctrl.spec.mouse.intf.MouseTarget#afterHover()
     */
    @Override
    public void afterHover()
    {
    }

    /**
     * @see bt.game.core.ctrl.spec.mouse.intf.MouseTarget#onMouseWheelMove(int)
     */
    @Override
    public void onMouseWheelMove(MouseWheelEvent e, int clicks)
    {
    }

    /**
     * @see bt.game.core.ctrl.spec.mouse.intf.MouseTarget#getZ()
     */
    @Override
    public Unit getZ()
    {
        return this.z;
    }

    public void setZ(Unit z)
    {
        this.z = z;
    }

    public Unit getW()
    {
        return this.w;
    }

    public void setW(Unit w)
    {
        this.w = w;
        createShape();
    }

    public Unit getH()
    {
        return this.h;
    }

    public void setH(Unit h)
    {
        this.h = h;
        createShape();
    }

    public Unit getX()
    {
        return this.x;
    }

    public void setX(Unit x)
    {
        this.x = x;
        createShape();
    }

    public Unit getY()
    {
        return this.y;
    }

    public void setY(Unit y)
    {
        this.y = y;
        createShape();
    }

    /**
     * @see bt.game.core.ctrl.spec.mouse.intf.MouseTarget#getShape()
     */
    @Override
    public Shape getShape()
    {
        return this.shape;
    }

    /**
     * @see bt.game.core.ctrl.spec.mouse.intf.MouseTarget#getScene()
     */
    @Override
    public Scene getScene()
    {
        return this.scene;
    }

    /**
     * @see bt.game.core.ctrl.spec.mouse.intf.MouseTarget#affectedByCamera()
     */
    @Override
    public boolean affectedByCamera()
    {
        return this.affectedByCamera;
    }
}