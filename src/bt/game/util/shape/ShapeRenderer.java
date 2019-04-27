/*
 * Copyright (c) 2010-2016 William Bittle  http://www.dyn4j.org/
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification, are permitted 
 * provided that the following conditions are met:
 * 
 *   * Redistributions of source code must retain the above copyright notice, this list of conditions 
 *     and the following disclaimer.
 *   * Redistributions in binary form must reproduce the above copyright notice, this list of conditions 
 *     and the following disclaimer in the documentation and/or other materials provided with the 
 *     distribution.
 *   * Neither the name of dyn4j nor the names of its contributors may be used to endorse or 
 *     promote products derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR 
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND 
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR 
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL 
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, 
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER 
 * IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT 
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package bt.game.util.shape;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;

import org.dyn4j.collision.Fixture;
import org.dyn4j.dynamics.Body;
import org.dyn4j.geometry.Capsule;
import org.dyn4j.geometry.Circle;
import org.dyn4j.geometry.Ellipse;
import org.dyn4j.geometry.HalfEllipse;
import org.dyn4j.geometry.Polygon;
import org.dyn4j.geometry.Segment;
import org.dyn4j.geometry.Shape;
import org.dyn4j.geometry.Slice;
import org.dyn4j.geometry.Vector2;

import bt.game.util.unit.Unit;

/**
 * Graphics2D renderer for dyn4j shape types.
 * 
 * @author William Bittle
 * @version 3.1.7
 * @since 3.1.5
 */
public final class ShapeRenderer
{
    /**
     * Renders the given shape to the given graphics context using the given scale and color.
     * 
     * @param g
     *            the graphics context
     * @param shape
     *            the shape to render
     * @param scale
     *            the scale to render the shape (pixels per dyn4j unit (typically meter))
     * @param color
     *            the color
     */
    public static final void render(Graphics2D g, Shape shape, Color color)
    {
        // no-op
        if (shape == null)
            return;

        // just default the color
        if (color == null)
            color = Color.ORANGE;

        if (shape instanceof Circle)
        {
            ShapeRenderer.render(g, (Circle)shape, color);
        }
        else if (shape instanceof Polygon)
        {
            ShapeRenderer.render(g, (Polygon)shape, color);
        }
        else if (shape instanceof Segment)
        {
            ShapeRenderer.render(g, (Segment)shape, color);
        }
        else if (shape instanceof Capsule)
        {
            ShapeRenderer.render(g, (Capsule)shape, color);
        }
        else if (shape instanceof Ellipse)
        {
            ShapeRenderer.render(g, (Ellipse)shape, color);
        }
        else if (shape instanceof Slice)
        {
            ShapeRenderer.render(g, (Slice)shape, color);
        }
        else if (shape instanceof HalfEllipse)
        {
            ShapeRenderer.render(g, (HalfEllipse)shape, color);
        }
        else
        {
            // unknown shape
        }
    }

    public static final void render(Graphics2D g, Body body, Color color)
    {
        AffineTransform ot = g.getTransform();
        AffineTransform lt = new AffineTransform();
        lt.translate(body.getTransform().getTranslationX() * Unit.getRatio(),
                body.getTransform().getTranslationY() * Unit.getRatio());
        lt.rotate(body.getTransform().getRotation());
        g.transform(lt);

        for (Fixture f : body.getFixtures())
        {
            ShapeRenderer.render(g, f.getShape(), color);
        }

        g.setTransform(ot);
    }

    /**
     * Renders the given {@link Circle} to the given graphics context using the given scale and color.
     * 
     * @param g
     *            the graphics context
     * @param circle
     *            the circle to render
     * @param scale
     *            the scale to render the shape (pixels per dyn4j unit (typically meter))
     * @param color
     *            the color
     */
    public static final void render(Graphics2D g, Circle circle, Color color)
    {
        double radius = circle.getRadius();
        Vector2 center = circle.getCenter();

        double radius2 = 2.0 * radius;
        Ellipse2D.Double c = new Ellipse2D.Double(
                (center.x - radius) * Unit.getRatio(),
                (center.y - radius) * Unit.getRatio(),
                radius2 * Unit.getRatio(),
                radius2 * Unit.getRatio());

        // fill the shape
        g.setColor(color);
        // g.fill(c);
        // draw the outline
        g.setColor(getOutlineColor(color));
        g.draw(c);

        // draw a line so that rotation is visible
        Line2D.Double l = new Line2D.Double(
                center.x * Unit.getRatio(),
                center.y * Unit.getRatio(),
                (center.x + radius) * Unit.getRatio(),
                center.y * Unit.getRatio());
        g.draw(l);
    }

    /**
     * Renders the given {@link Polygon} to the given graphics context using the given scale and color.
     * 
     * @param g
     *            the graphics context
     * @param polygon
     *            the polygon to render
     * @param scale
     *            the scale to render the shape (pixels per dyn4j unit (typically meter))
     * @param color
     *            the color
     */
    public static final void render(Graphics2D g, Polygon polygon, Color color)
    {
        Vector2[] vertices = polygon.getVertices();
        int l = vertices.length;

        // create the awt polygon
        Path2D.Double p = new Path2D.Double();
        p.moveTo(vertices[0].x * Unit.getRatio(), vertices[0].y * Unit.getRatio());
        for (int i = 1; i < l; i ++ )
        {
            p.lineTo(vertices[i].x * Unit.getRatio(), vertices[i].y * Unit.getRatio());
        }
        p.closePath();

        // fill the shape
        g.setColor(color);
        // g.fill(p);
        // draw the outline
        g.setColor(getOutlineColor(color));
        g.draw(p);
    }

    /**
     * Renders the given {@link Segment} to the given graphics context using the given scale and color.
     * 
     * @param g
     *            the graphics context
     * @param segment
     *            the segment to render
     * @param scale
     *            the scale to render the shape (pixels per dyn4j unit (typically meter))
     * @param color
     *            the color
     */
    public static final void render(Graphics2D g, Segment segment, Color color)
    {
        Vector2[] vertices = segment.getVertices();

        Line2D.Double l = new Line2D.Double(
                vertices[0].x * Unit.getRatio(),
                vertices[0].y * Unit.getRatio(),
                vertices[1].x * Unit.getRatio(),
                vertices[1].y * Unit.getRatio());

        // draw the outline
        g.setColor(getOutlineColor(color));
        g.draw(l);
    }

    /**
     * Renders the given {@link Capsule} to the given graphics context using the given scale and color.
     * 
     * @param g
     *            the graphics context
     * @param capsule
     *            the capsule to render
     * @param scale
     *            the scale to render the shape (pixels per dyn4j unit (typically meter))
     * @param color
     *            the color
     */
    public static final void render(Graphics2D g, Capsule capsule, Color color)
    {
        // get the local rotation and translation
        double rotation = capsule.getRotation();
        Vector2 center = capsule.getCenter();

        // save the old transform
        AffineTransform oTransform = g.getTransform();
        // translate and rotate
        g.translate(center.x * Unit.getRatio(), center.y * Unit.getRatio());
        g.rotate(rotation);

        double width = capsule.getLength();
        double radius = capsule.getCapRadius();
        double radius2 = radius * 2.0;

        Arc2D.Double arcL = new Arc2D.Double(
                -(width * 0.5) * Unit.getRatio(),
                -radius * Unit.getRatio(),
                radius2 * Unit.getRatio(),
                radius2 * Unit.getRatio(),
                90.0,
                180.0,
                Arc2D.OPEN);
        Arc2D.Double arcR = new Arc2D.Double(
                (width * 0.5 - radius2) * Unit.getRatio(),
                -radius * Unit.getRatio(),
                radius2 * Unit.getRatio(),
                radius2 * Unit.getRatio(),
                -90.0,
                180.0,
                Arc2D.OPEN);

        // connect the shapes
        Path2D.Double path = new Path2D.Double();
        path.append(arcL, true);
        path.append(new Line2D.Double(arcL.getEndPoint(), arcR.getStartPoint()), true);
        path.append(arcR, true);
        path.append(new Line2D.Double(arcR.getEndPoint(), arcL.getStartPoint()), true);

        // set the color
        g.setColor(color);
        // fill the shape
        // g.fill(path);
        // set the color
        g.setColor(getOutlineColor(color));
        // draw the shape
        g.draw(path);

        // re-instate the old transform
        g.setTransform(oTransform);
    }

    /**
     * Renders the given {@link Ellipse} to the given graphics context using the given scale and color.
     * 
     * @param g
     *            the graphics context
     * @param ellipse
     *            the ellipse to render
     * @param scale
     *            the scale to render the shape (pixels per dyn4j unit (typically meter))
     * @param color
     *            the color
     */
    public static final void render(Graphics2D g, Ellipse ellipse, Color color)
    {
        // get the local rotation and translation
        double rotation = ellipse.getRotation();
        Vector2 center = ellipse.getCenter();

        // save the old transform
        AffineTransform oTransform = g.getTransform();
        g.translate(center.x * Unit.getRatio(), center.y * Unit.getRatio());
        g.rotate(rotation);

        double width = ellipse.getWidth();
        double height = ellipse.getHeight();
        Ellipse2D.Double c = new Ellipse2D.Double(
                (-width * 0.5) * Unit.getRatio(),
                (-height * 0.5) * Unit.getRatio(),
                width * Unit.getRatio(),
                height * Unit.getRatio());

        // fill the shape
        g.setColor(color);
        // g.fill(c);
        // draw the outline
        g.setColor(getOutlineColor(color));
        g.draw(c);

        // re-instate the old transform
        g.setTransform(oTransform);
    }

    /**
     * Renders the given {@link Slice} to the given graphics context using the given scale and color.
     * 
     * @param g
     *            the graphics context
     * @param slice
     *            the slice to render
     * @param scale
     *            the scale to render the shape (pixels per dyn4j unit (typically meter))
     * @param color
     *            the color
     */
    public static final void render(Graphics2D g, Slice slice, Color color)
    {
        double radius = slice.getSliceRadius();
        double theta2 = slice.getTheta() * 0.5;

        // get the local rotation and translation
        double rotation = slice.getRotation();
        Vector2 circleCenter = slice.getCircleCenter();

        // save the old transform
        AffineTransform oTransform = g.getTransform();
        // translate and rotate
        g.translate(circleCenter.x * Unit.getRatio(), circleCenter.y * Unit.getRatio());
        g.rotate(rotation);

        // to draw the arc, java2d wants the top left x,y
        // as if you were drawing a circle
        Arc2D a = new Arc2D.Double(-radius * Unit.getRatio(),
                -radius * Unit.getRatio(),
                2.0 * radius * Unit.getRatio(),
                2.0 * radius * Unit.getRatio(),
                -Math.toDegrees(theta2),
                Math.toDegrees(2.0 * theta2),
                Arc2D.PIE);

        // fill the shape
        g.setColor(color);
        // g.fill(a);
        // draw the outline
        g.setColor(getOutlineColor(color));
        g.draw(a);

        // re-instate the old transform
        g.setTransform(oTransform);
    }

    /**
     * Renders the given {@link HalfEllipse} to the given graphics context using the given scale and color.
     * 
     * @param g
     *            the graphics context
     * @param halfEllipse
     *            the halfEllipse to render
     * @param scale
     *            the scale to render the shape (pixels per dyn4j unit (typically meter))
     * @param color
     *            the color
     */
    public static final void render(Graphics2D g, HalfEllipse halfEllipse, Color color)
    {
        double width = halfEllipse.getWidth();
        double height = halfEllipse.getHeight();

        // get the local rotation and translation
        double rotation = halfEllipse.getRotation();
        Vector2 center = halfEllipse.getEllipseCenter();

        // save the old transform
        AffineTransform oTransform = g.getTransform();
        // translate and rotate
        g.translate(center.x * Unit.getRatio(), center.y * Unit.getRatio());
        g.rotate(rotation);

        // to draw the arc, java2d wants the top left x,y
        // as if you were drawing a circle
        Arc2D a = new Arc2D.Double(
                (-width * 0.5) * Unit.getRatio(),
                -height * Unit.getRatio(),
                width * Unit.getRatio(),
                height * 2.0 * Unit.getRatio(),
                0,
                -180.0,
                Arc2D.PIE);

        // fill the shape
        g.setColor(color);
        // g.fill(a);
        // draw the outline
        g.setColor(getOutlineColor(color));
        g.draw(a);

        // re-instate the old transform
        g.setTransform(oTransform);
    }

    /**
     * Returns the outline color for the given color.
     * 
     * @param color
     *            the fill color
     * @return Color
     */
    private static final Color getOutlineColor(Color color)
    {
        Color oc = color.darker();
        return new Color(oc.getRed(), oc.getGreen(), oc.getBlue(), color.getAlpha());
    }

    /**
     * Returns a random color.
     * 
     * @return Color
     */
    public static final Color getRandomColor()
    {
        return new Color(
                (float)Math.random() * 0.5f + 0.5f,
                (float)Math.random() * 0.5f + 0.5f,
                (float)Math.random() * 0.5f + 0.5f);
    }
}
