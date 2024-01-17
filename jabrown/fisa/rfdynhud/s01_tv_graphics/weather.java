package jabrown.fisa.rfdynhud.s01_tv_graphics;

import java.awt.Font;
import java.io.IOException;
import java.math.RoundingMode;
import java.text.DecimalFormat;

import jabrown.fisa.rfdynhud.s01_tv_graphics._util.JABrownFISAWidgetSets01_tv_graphics;
import net.ctdp.rfdynhud.gamedata.LiveGameData;
import net.ctdp.rfdynhud.gamedata.ScoringInfo;
import net.ctdp.rfdynhud.properties.ImagePropertyWithTexture;
import net.ctdp.rfdynhud.properties.IntProperty;
import net.ctdp.rfdynhud.properties.PropertiesContainer;
import net.ctdp.rfdynhud.properties.PropertyLoader;
import net.ctdp.rfdynhud.render.DrawnStringFactory;
import net.ctdp.rfdynhud.render.TextureImage2D;
import net.ctdp.rfdynhud.render.DrawnString;
import net.ctdp.rfdynhud.render.DrawnString.Alignment;
import net.ctdp.rfdynhud.util.PropertyWriter;
import net.ctdp.rfdynhud.util.SubTextureCollector;
import net.ctdp.rfdynhud.valuemanagers.Clock;
import net.ctdp.rfdynhud.values.FloatValue;
import net.ctdp.rfdynhud.widgets.base.widget.Widget;

public class weather extends Widget {

	private DrawnString dsTempC = null;
	private DrawnString dsTempF = null;
	private DecimalFormat decimalFormat = new DecimalFormat("0.00");
	private FloatValue rainingSeverity = new FloatValue();
    private FloatValue onPathWetness = new FloatValue();
    private FloatValue offPathWetness = new FloatValue();
    private FloatValue cloudDarkness = new FloatValue();
    
    private ImagePropertyWithTexture imgSunny = new ImagePropertyWithTexture("imgSunny", "prunn/rfe/Sunny.png");
    private ImagePropertyWithTexture imgSlightDrizzle = new ImagePropertyWithTexture("SlightDrizzle", "prunn/rfe/SlightDrizzle.png");
    private ImagePropertyWithTexture imgDrizzle = new ImagePropertyWithTexture("imgDrizzle", "prunn/rfe/Drizzle.png");
    private ImagePropertyWithTexture imgThunderstorms = new ImagePropertyWithTexture("imgDrizzle", "prunn/rfe/Thunderstorms.png");
    private ImagePropertyWithTexture imgDark = new ImagePropertyWithTexture("imgDrizzle", "prunn/rfe/Dark.png");
    
    private IntProperty fontyoffset = new IntProperty("Y Font Offset", 0);
    private IntProperty fontxoffset = new IntProperty("X Font Offset", 0);
	
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
    	int lineHeight = (int) ((int)TextureImage2D.getStringHeight( "0%C", getFontProperty() ) * 1.15);
        dsTempC = drawnStringFactory.newDrawnString( "ds", width - fontxoffset.getValue(), fontyoffset.getValue(), Alignment.RIGHT, false, getFont(), isFontAntiAliased(), getFontColor() );
        dsTempF = drawnStringFactory.newDrawnString( "ds", width - fontxoffset.getValue(), fontyoffset.getValue() + lineHeight, Alignment.RIGHT, false, getFont(), isFontAntiAliased(), getFontColor() );
    	decimalFormat.setRoundingMode(RoundingMode.HALF_EVEN);
        imgSunny.updateSize( width/2, height, isEditorMode );
        imgSlightDrizzle.updateSize( width/2, height, isEditorMode );
        imgDrizzle.updateSize( width/2, height, isEditorMode );
        imgThunderstorms.updateSize( width/2, height, isEditorMode );
        imgDark.updateSize( width/2, height, isEditorMode );
    }
    
    @Override
    protected Boolean updateVisibility( LiveGameData gameData, boolean isEditorMode )
    {
        super.updateVisibility( gameData, isEditorMode );
           
        rainingSeverity.update( gameData.getScoringInfo().getRainingSeverity() );
        onPathWetness.update( gameData.getScoringInfo().getOnPathWetness() );
        offPathWetness.update( gameData.getScoringInfo().getOffPathWetness() );
        cloudDarkness.update( gameData.getScoringInfo().getCloudDarkness() );
        if((rainingSeverity.hasChanged() || cloudDarkness.hasChanged()) && !isEditorMode)
        {
                forceCompleteRedraw( true );
        }
        return true;
    }
    
    @Override
    protected void drawBackground( LiveGameData gameData, boolean isEditorMode, TextureImage2D texture, int offsetX, int offsetY, int width, int height, boolean isRoot )
    {
        super.drawBackground( gameData, isEditorMode, texture, offsetX, offsetY, width, height, isRoot );
        int lineHeight = TextureImage2D.getStringHeight( "0%C", getFontProperty() );
        int padding = 4;
        if(rainingSeverity.getValue() > 0.3f || isEditorMode)
            texture.drawImage( imgThunderstorms.getTexture(), offsetX + padding, offsetY + padding, false, null );
        else if(rainingSeverity.getValue() > 0.02f)
            texture.drawImage( imgDrizzle.getTexture(), offsetX + padding, offsetY + padding, false, null );
        else if(rainingSeverity.getValue() > 0.0f)
            texture.drawImage( imgSlightDrizzle.getTexture(), offsetX + padding, offsetY + padding, false, null );
        else if(cloudDarkness.getValue() > 0.8f)
            texture.drawImage( imgDark.getTexture(), offsetX + padding, offsetY + padding, false, null );
        else
            texture.drawImage( imgSunny.getTexture(), offsetX + padding, offsetY + padding, false, null );
    }
    
    @Override
    protected void drawWidget( Clock clock, boolean needsCompleteRedraw, LiveGameData gameData, boolean isEditorMode, TextureImage2D texture, int offsetX, int offsetY, int width, int height )
    {
    	ScoringInfo scoringInfo = gameData.getScoringInfo();
    	int lineHeight = (int) ((int)TextureImage2D.getStringHeight( "0%C", getFontProperty() ) * 1.15);
        dsTempC.draw( offsetX, offsetY, String.valueOf(decimalFormat.format(scoringInfo.getAmbientTemperatureC())) + " °C", texture);
        dsTempF.draw( offsetX, offsetY, String.valueOf(decimalFormat.format(scoringInfo.getAmbientTemperatureF())) + " °F", texture);
    }
    
    @Override
    public void saveProperties( PropertyWriter writer ) throws IOException
    {
        super.saveProperties( writer );
        
        writer.writeProperty( fontyoffset, "" );
        writer.writeProperty( fontxoffset, "" );
    }
    
    @Override
    public void loadProperty( PropertyLoader loader )
    {
        super.loadProperty( loader );
        
        if ( loader.loadProperty( fontyoffset ) );
        else if ( loader.loadProperty( fontxoffset ) );
    }
    
    @Override
    public void getProperties( PropertiesContainer propsCont, boolean forceAll )
    {
        super.getProperties( propsCont, forceAll );
        
        propsCont.addGroup( "Font Displacement" );
        propsCont.addProperty( fontyoffset );
        propsCont.addProperty( fontxoffset );
    }
	
}
