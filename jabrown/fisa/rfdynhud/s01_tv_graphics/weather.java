package jabrown.fisa.rfdynhud.s01_tv_graphics;

import java.awt.Font;
import java.math.RoundingMode;
import java.text.DecimalFormat;

import jabrown.fisa.rfdynhud.s01_tv_graphics._util.JABrownFISAWidgetSets01_tv_graphics;
import net.ctdp.rfdynhud.gamedata.LiveGameData;
import net.ctdp.rfdynhud.gamedata.ScoringInfo;
import net.ctdp.rfdynhud.render.DrawnStringFactory;
import net.ctdp.rfdynhud.render.TextureImage2D;
import net.ctdp.rfdynhud.render.DrawnString;
import net.ctdp.rfdynhud.render.DrawnString.Alignment;
import net.ctdp.rfdynhud.util.SubTextureCollector;
import net.ctdp.rfdynhud.valuemanagers.Clock;
import net.ctdp.rfdynhud.widgets.base.widget.Widget;

public class weather extends Widget {

	private DrawnString dsTemp = null;
	private DrawnString dsWind = null;
	private DecimalFormat decimalFormat = new DecimalFormat("0.00");
	
    public weather()
    {
        super( JABrownFISAWidgetSets01_tv_graphics.INSTANCE, JABrownFISAWidgetSets01_tv_graphics.WIDGET_PACKAGE_S01_TV_GRAPHICS, 20.0f, 5.0f );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void prepareForMenuItem()
    {
        super.prepareForMenuItem();
        
        getFontProperty().setFont( JABrownFISAWidgetSets01_tv_graphics.FISA_S01_TV_GRAPHICS_SMALL_FONT_NAME, Font.BOLD, 22, true, true );
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
        dsTemp = drawnStringFactory.newDrawnString( "ds", 0, 0, Alignment.LEFT, false, getFont(), isFontAntiAliased(), getFontColor() );
        dsWind = drawnStringFactory.newDrawnString( "ds", 0, 0, Alignment.LEFT, false, getFont(), isFontAntiAliased(), getFontColor() );
    	decimalFormat.setRoundingMode(RoundingMode.HALF_EVEN); 
    }
    
    @Override
    protected void drawWidget( Clock clock, boolean needsCompleteRedraw, LiveGameData gameData, boolean isEditorMode, TextureImage2D texture, int offsetX, int offsetY, int width, int height )
    {
    	ScoringInfo scoringInfo = gameData.getScoringInfo();
    	int lineHeight = (int) ((int)TextureImage2D.getStringHeight( "0%C", getFontProperty() ) * 1.15);
        dsTemp.draw( offsetX, offsetY, "Temperature: " + String.valueOf(decimalFormat.format(scoringInfo.getAmbientTemperatureC())) + "° C (" + String.valueOf(decimalFormat.format(scoringInfo.getAmbientTemperatureF())) + "° F)", texture );
        dsWind.draw( offsetX, offsetY + lineHeight, "Rain Intensity: " + String.valueOf(decimalFormat.format(scoringInfo.getRainingSeverity() * 100)) + "%", texture );
    }
	
}
