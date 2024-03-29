/**
 * ========================================
 * JCommon : a free Java report library
 * ========================================
 *
 * Project Info:  http://www.jfree.org/jcommon/
 * Project Lead:  Thomas Morgner;
 *
 * (C) Copyright 2000-2006, by Object Refinery Limited and Contributors.
 *
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA 02111-1307, USA.
 *
 * [Java is a trademark or registered trademark of Sun Microsystems, Inc.
 * in the United States and other countries.]
 *
 * ------------
 * DrawablePanel.java
 * ------------
 * (C) Copyright 2002-2006, by Object Refinery Limited.
 *
 * Original Author:  Thomas Morgner;
 * Contributor(s):   -;
 *
 * $Id: DrawablePanel.java,v 1.1 2006/06/28 17:14:36 taqua Exp $
 *
 * Changes
 * -------
 *
 *
 */
package org.jfree.ui;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import javax.swing.JComponent;

/**
 * A component, that accepts a drawable and which draws that drawable.
 *
 * @author Thomas Morgner
 */
public class DrawablePanel extends JComponent
{
  private Drawable drawable;

  public DrawablePanel()
  {
    setOpaque(false);
  }

  public Drawable getDrawable()
  {
    return drawable;
  }

  public void setDrawable(final Drawable drawable)
  {
    this.drawable = drawable;
  }

  /**
   * Returns true if this component is completely opaque.
   * <p/>
   * An opaque component paints every pixel within its rectangular bounds. A
   * non-opaque component paints only a subset of its pixels or none at all,
   * allowing the pixels underneath it to "show through".  Therefore, a
   * component that does not fully paint its pixels provides a degree of
   * transparency.
   * <p/>
   * Subclasses that guarantee to always completely paint their contents should
   * override this method and return true.
   *
   * @return true if this component is completely opaque
   * @see #setOpaque
   */
  public boolean isOpaque()
  {
    if (drawable == null)
    {
      return false;
    }
    return super.isOpaque();
  }

  /**
   * Calls the UI delegate's paint method, if the UI delegate is
   * non-<code>null</code>.  We pass the delegate a copy of the
   * <code>Graphics</code> object to protect the rest of the paint code from
   * irrevocable changes (for example, <code>Graphics.translate</code>).
   * <p/>
   * If you override this in a subclass you should not make permanent changes to
   * the passed in <code>Graphics</code>. For example, you should not alter the
   * clip <code>Rectangle</code> or modify the transform. If you need to do
   * these operations you may find it easier to create a new
   * <code>Graphics</code> from the passed in <code>Graphics</code> and
   * manipulate it. Further, if you do not invoker super's implementation you
   * must honor the opaque property, that is if this component is opaque, you
   * must completely fill in the background in a non-opaque color. If you do not
   * honor the opaque property you will likely see visual artifacts.
   * <p/>
   * The passed in <code>Graphics</code> object might have a transform other
   * than the identify transform installed on it.  In this case, you might get
   * unexpected results if you cumulatively apply another transform.
   *
   * @param g the <code>Graphics</code> object to protect
   * @see #paint
   * @see javax.swing.plaf.ComponentUI
   */
  protected void paintComponent(Graphics g)
  {
    if (drawable == null)
    {
      return;
    }

    Rectangle bounds = getBounds();

    final Graphics2D g2 = (Graphics2D) g.create
            (bounds.x, bounds.y, bounds.width, bounds.height);

    drawable.draw(g2, bounds);
    g2.dispose();
  }

}
