package jabrown.fisa.rfdynhud.s01_tv_graphics;

import java.awt.Color;
import java.awt.Font;

import jabrown.fisa.rfdynhud.s01_tv_graphics._util.JABrownFISAWidgetSets01_tv_graphics;
import net.ctdp.rfdynhud.gamedata.LiveGameData;
import net.ctdp.rfdynhud.render.DrawnString;
import net.ctdp.rfdynhud.render.DrawnString.Alignment;
import net.ctdp.rfdynhud.render.DrawnStringFactory;
import net.ctdp.rfdynhud.render.TextureImage2D;
import net.ctdp.rfdynhud.util.SubTextureCollector;
import net.ctdp.rfdynhud.valuemanagers.Clock;
import net.ctdp.rfdynhud.widgets.base.widget.Widget;

public class racecontrol extends Widget
{
	private DrawnString ds = null;
	
	public racecontrol()
    {
        super( JABrownFISAWidgetSets01_tv_graphics.INSTANCE, JABrownFISAWidgetSets01_tv_graphics.WIDGET_PACKAGE_S01_TV_GRAPHICS, 100.0f, 25.0f );
        
        getBackgroundProperty().setColorValue( "#00000000" );
        getFontProperty().setFont( JABrownFISAWidgetSets01_tv_graphics.FISA_S01_TV_GRAPHICS_FONT_NAME );
        getFontColorProperty().setColor( JABrownFISAWidgetSets01_tv_graphics.FONT_COLOR1_NAME );
    }
	
    /**
     * {@inheritDoc}
     */
    @Override
    public void prepareForMenuItem()
    {
    	super.prepareForMenuItem();
    	
        getFontProperty().setFont( "Dialog", Font.PLAIN, 6, false, true );
    }
	
    /**
     * {@inheritDoc}
     */
    @Override
    protected void initSubTextures( LiveGameData gameData, boolean isEditorMode, int widgetInnerWidth, int widgetInnerHeight, SubTextureCollector collector )
    {
    }
	
	@Override
	protected void initialize( LiveGameData gameData, boolean isEditorMode, DrawnStringFactory drawnStringFactory, TextureImage2D texture, int width, int height )
	{
		ds = drawnStringFactory.newDrawnString("ds", 240, 80, Alignment.LEFT, false, getFont(), isFontAntiAliased(), getFontColor() );
	}
	
	@Override
    protected void drawWidget( Clock clock, boolean needsCompleteRedraw, LiveGameData gameData, boolean isEditorMode, TextureImage2D texture, int offsetX, int offsetY, int width, int height )
    {
		if( needsCompleteRedraw )
		{
			texture.clear(new Color(0, 0, 0, 0), false, null);
			ds.draw( 240, 60, "SAFETY CAR", null );
		}
    }
}
