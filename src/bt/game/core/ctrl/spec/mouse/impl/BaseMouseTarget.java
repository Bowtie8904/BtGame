package bt.game.core.ctrl.spec.mouse.impl;

import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

import org.dyn4j.geometry.Geometry;
import org.dyn4j.geometry.Shape;

import bt.game.core.ctrl.spec.mouse.intf.MouseTarget;
import bt.game.core.scene.intf.Scene;
import bt.game.util.unit.Unit;

/**
 * @author &#8904
 *
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
        this.shape = Geometry.createRectangle(w.pixels(),
                                              h.pixels());
        this.shape.translate(x.pixels() + (w.pixels() / 2),
                             y.pixels() + (h.pixels() / 2));
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