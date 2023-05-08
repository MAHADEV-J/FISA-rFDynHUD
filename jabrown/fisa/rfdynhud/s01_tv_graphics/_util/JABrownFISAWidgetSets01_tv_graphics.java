/**
 * Copyright (C) 2009-2010 Cars and Tracks Development Project (CTDP).
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 * 
 * @author J.A. Brown
 * released 2023
 * 
 */
package jabrown.fisa.rfdynhud.s01_tv_graphics._util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import net.ctdp.rfdynhud.util.FontUtils;
import net.ctdp.rfdynhud.util.RFDHLog;
import net.ctdp.rfdynhud.widgets.WidgetsConfiguration;
import net.ctdp.rfdynhud.widgets.base.widget.Widget;
import net.ctdp.rfdynhud.widgets.base.widget.WidgetPackage;
import net.ctdp.rfdynhud.widgets.base.widget.WidgetSet;

public class JABrownFISAWidgetSets01_tv_graphics extends WidgetSet
{
    /*
     *  @author J.A. Brown
     * released 2023
     */
    private JABrownFISAWidgetSets01_tv_graphics()
    {
        super( composeVersion( 0, 1, 0 ) );
    }
    public static final JABrownFISAWidgetSets01_tv_graphics INSTANCE = new JABrownFISAWidgetSets01_tv_graphics();
    
    public static final WidgetPackage WIDGET_PACKAGE = new WidgetPackage( INSTANCE, "FISA", INSTANCE.getIcon( "fisa.png" ) );
    public static final WidgetPackage WIDGET_PACKAGE_S01_TV_GRAPHICS = new WidgetPackage( INSTANCE, "FISA/S01 TV Graphics", INSTANCE.getIcon( "fisa.png" ), INSTANCE.getIcon( "wscc.png" ) );
    //public static final WidgetPackage WIDGET_PACKAGE_F1_2011_Race = new WidgetPackage( INSTANCE, "Prunn/F1 2011/Race", INSTANCE.getIcon( "com/prunn/rfdynhud/widgets/prunn/prunn.png" ), INSTANCE.getIcon( "com/prunn/rfdynhud/widgets/prunn/f1_2011.png" ), INSTANCE.getIcon( "com/prunn/rfdynhud/widgets/prunn/2011.png" ) );
    
    public static final String FONT_COLOR1_NAME = "FontColor1";
    public static final String FONT_COLOR2_NAME = "FontColor2";
    public static final String FONT_COLOR3_NAME = "FontColor3";
    public static final String FONT_COLOR4_NAME = "FontColor4";
    public static final String GAP_FONT_COLOR1_NAME = "GapFontColor1";
    public static final String GAP_FONT_COLOR2_NAME = "GapFontColor2";
    public static final String FISA_S01_TV_GRAPHICS_FONT_NAME = "fisa_s01Font";
    public static final String POS_FONT_NAME = "PosFont";
    
    public String getDefaultNamedColorValue( String name )
    {
        if(name.equals("StandardFontColor"))
            return "#FFFFFF";
        if ( name.equals( FONT_COLOR1_NAME ) )
            return ( "#FFFFFF" );
        if ( name.equals( FONT_COLOR2_NAME ) )
            return ( "#F1CB25" );
        if ( name.equals( FONT_COLOR3_NAME ) )
            return ( "#41D741" );
        if ( name.equals( FONT_COLOR4_NAME ) )
            return ( "#AC27CE" );
        if ( name.equals( GAP_FONT_COLOR1_NAME ) )
            return ( "#FFFFFF" );
        if ( name.equals( GAP_FONT_COLOR2_NAME ) )
            return ( "#FFFFFF" );
        
        return ( null );
    }
    
    @SuppressWarnings( "unchecked" )
    public static final <W extends Widget> W getWidgetByClass( Class<W> clazz, boolean includeSubclasses, WidgetsConfiguration widgetsConfig )
    {
        int n = widgetsConfig.getNumWidgets();
        
        if ( includeSubclasses )
        {
            for ( int i = 0; i < n; i++ )
            {
                Widget w = widgetsConfig.getWidget( i );
                
                if ( clazz.isAssignableFrom( w.getClass() ) )
                    return ( (W)w );
            }
        }
        else
        {
            for ( int i = 0; i < n; i++ )
            {
                Widget w = widgetsConfig.getWidget( i );
                
                if ( clazz == w.getClass() )
                    return ( (W)w );
            }
        }
        
        return ( null );
    }
}