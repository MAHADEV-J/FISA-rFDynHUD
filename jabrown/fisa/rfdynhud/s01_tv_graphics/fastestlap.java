package jabrown.fisa.rfdynhud.s01_tv_graphics;

import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.openmali.types.twodee.Rect2i;

import jabrown.fisa.rfdynhud.s01_tv_graphics._util.JABrownFISAWidgetSets01_tv_graphics;
import net.ctdp.rfdynhud.gamedata.GamePhase;
import net.ctdp.rfdynhud.gamedata.Laptime;
import net.ctdp.rfdynhud.gamedata.LiveGameData;
import net.ctdp.rfdynhud.gamedata.ScoringInfo;
import net.ctdp.rfdynhud.gamedata.VehicleScoringInfo;
import net.ctdp.rfdynhud.gamedata.YellowFlagState;
import net.ctdp.rfdynhud.properties.FontProperty;
import net.ctdp.rfdynhud.properties.IntProperty;
import net.ctdp.rfdynhud.properties.PropertyLoader;
import net.ctdp.rfdynhud.render.DrawnString;
import net.ctdp.rfdynhud.render.DrawnStringFactory;
import net.ctdp.rfdynhud.render.Texture2DCanvas;
import net.ctdp.rfdynhud.render.TextureImage2D;
import net.ctdp.rfdynhud.render.DrawnString.Alignment;
import net.ctdp.rfdynhud.util.PropertyWriter;
import net.ctdp.rfdynhud.util.SubTextureCollector;
import net.ctdp.rfdynhud.valuemanagers.Clock;
import net.ctdp.rfdynhud.values.FloatValue;
import net.ctdp.rfdynhud.values.IntValue;
import net.ctdp.rfdynhud.widgets.base.widget.Widget;

public class fastestlap extends Widget {

	//what it looks like
	private DrawnString dsDriverPos = null;
    private DrawnString dsDriverName = null;
    private DrawnString dsTeamName = null;
    private DrawnString dsCarMake = null;
    private DrawnString dsCarModel = null;
    private DrawnString dsCaption = null;
    private DrawnString dsLaptime = null;
    private Rect2i rectangle = null;
    private Rect2i square = null;
    private final FontProperty largeFont = new FontProperty("LargeFont", JABrownFISAWidgetSets01_tv_graphics.FISA_S01_TV_GRAPHICS_LARGE_FONT.getKey());
    private final FontProperty captionFont = new FontProperty("CaptionFont", JABrownFISAWidgetSets01_tv_graphics.FISA_S01_TV_GRAPHICS_CAPTION_FONT.getKey());
    private final FontProperty smallFont = new FontProperty("SmallFont", JABrownFISAWidgetSets01_tv_graphics.FISA_S01_TV_GRAPHICS_SMALL_FONT.getKey());
    private IntProperty aspectRatioYOffset = new IntProperty("Y Offset (From Below)", 0);
    private IntProperty aspectRatioXOffset = new IntProperty("X Offset", 0);
	
	//the data it needs
    private IntValue driverPos = null;
    private String driverName = null;
    private String teamName = null;
    private String carMake = null;
    private String carModel = null;
    private String caption = null;
    private FloatValue laptime = null;
	
    public fastestlap()
    {
        super( JABrownFISAWidgetSets01_tv_graphics.INSTANCE, JABrownFISAWidgetSets01_tv_graphics.WIDGET_PACKAGE_S01_TV_GRAPHICS, 20.0f, 32.5f );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void prepareForMenuItem()
    {
        super.prepareForMenuItem();
        
        getFontProperty().setFont( "Dialog", Font.PLAIN, 9, false, true );
        largeFont.setFont(JABrownFISAWidgetSets01_tv_graphics.FISA_S01_TV_GRAPHICS_LARGE_FONT_NAME, Font.BOLD, 64, true, true);
        captionFont.setFont(JABrownFISAWidgetSets01_tv_graphics.FISA_S01_TV_GRAPHICS_CAPTION_FONT_NAME, Font.BOLD, 24, true, true);
        smallFont.setFont(JABrownFISAWidgetSets01_tv_graphics.FISA_S01_TV_GRAPHICS_SMALL_FONT_NAME, Font.BOLD, 22, true, true);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void saveProperties( PropertyWriter writer ) throws IOException
    {
        super.saveProperties( writer );
        
        writer.writeProperty( largeFont, "Large font that is used for position number." );
        writer.writeProperty( captionFont, "Font that is used for driver name, car name, caption, and laptime." );
        writer.writeProperty( smallFont, "Small font that is used for team name." );
        writer.writeProperty( aspectRatioXOffset, "Flag X offset." );
        writer.writeProperty( aspectRatioYOffset, "Flag Y offset (from below)." );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void loadProperty( PropertyLoader loader )
    {
        super.loadProperty( loader );
        
        if ( loader.loadProperty( largeFont ) );
        else if ( loader.loadProperty( captionFont ) );
        else if ( loader.loadProperty( smallFont ) );
        else if ( loader.loadProperty( aspectRatioXOffset ) );
        else if ( loader.loadProperty( aspectRatioYOffset ) );
    }
    
	@Override
    protected void initSubTextures( LiveGameData gameData, boolean isEditorMode, int widgetInnerWidth, int widgetInnerHeight, SubTextureCollector collector )
	{
		// TODO Auto-generated method stub
	}
    
	@Override
    protected void initialize( LiveGameData gameData, boolean isEditorMode, DrawnStringFactory drawnStringFactory, TextureImage2D texture, int width, int height )
    {
		caption = "FASTEST LAP";
		dsDriverPos = drawnStringFactory.newDrawnString( "dsDriverPos", aspectRatioXOffset.getValue(), height - aspectRatioYOffset.getValue() - 140, Alignment.LEFT, false, largeFont.getFont(), isFontAntiAliased(), getFontColor() );
        dsDriverName = drawnStringFactory.newDrawnString( "dsDriverName", aspectRatioXOffset.getIntValue() + aspectRatioXOffset.getValue(), height - aspectRatioYOffset.getValue() - 160, Alignment.LEFT, false, captionFont.getFont(), isFontAntiAliased(), getFontColor() );
    	dsTeamName = drawnStringFactory.newDrawnString( "dsTeamName", aspectRatioXOffset.getValue() + aspectRatioXOffset.getValue(), height - aspectRatioYOffset.getValue() - 140, Alignment.LEFT, false, largeFont.getFont(), isFontAntiAliased(), getFontColor() );
    	dsCarMake = drawnStringFactory.newDrawnString( "dsCarMake", aspectRatioXOffset.getValue() + aspectRatioXOffset.getValue(), height - aspectRatioYOffset.getValue() - 120, Alignment.LEFT, false, largeFont.getFont(), isFontAntiAliased(), getFontColor() );
    	dsCarModel = drawnStringFactory.newDrawnString( "dsCarModel", aspectRatioXOffset.getValue() + aspectRatioXOffset.getValue(), height - aspectRatioYOffset.getValue() - 100, Alignment.LEFT, false, largeFont.getFont(), isFontAntiAliased(), getFontColor() );
    	dsCaption = drawnStringFactory.newDrawnString( "dsCaption", aspectRatioXOffset.getValue() + aspectRatioXOffset.getValue(), height - aspectRatioYOffset.getValue() - 80, Alignment.LEFT, false, largeFont.getFont(), isFontAntiAliased(), getFontColor() );
    	dsLaptime = drawnStringFactory.newDrawnString( "dsLaptime", aspectRatioXOffset.getValue() + aspectRatioXOffset.getValue(), height - aspectRatioYOffset.getValue() - 60, Alignment.LEFT, false, largeFont.getFont(), isFontAntiAliased(), getFontColor() );
	}

	
   @Override
   protected Boolean updateVisibility ( LiveGameData gameData, boolean isEditorMode )
   {
	   super.updateVisibility(gameData, isEditorMode);
	   
		//get fastest laptime
	   ScoringInfo scoringInfo = gameData.getScoringInfo();
	   laptime.update(scoringInfo.getFastestLaptime().getLapTime());
		
		//get car that set fastest lap and take info from it
	   if(laptime.hasChanged())
	   {
		   VehicleScoringInfo fastestDriver = scoringInfo.getFastestLapVSI();
		   driverPos.update(fastestDriver.getPlace(false));
		   driverName = fastestDriver.getDriverNameShort(true);
		   teamName = "TEST";
		   carMake = fastestDriver.getVehicleName();
		   carModel = "";   
	   }
	   
	   if(isEditorMode)
	   {
		   driverName = "DRIVER";
		   carMake = "CAR";
		   carModel = "TEST";
	   }
	   
	   return true;
   }
	
	@Override
    protected void drawBackground( LiveGameData gameData, boolean isEditorMode, TextureImage2D texture, int offsetX, int offsetY, int width, int height, boolean isRoot )
    {
    	super.drawBackground(gameData, isEditorMode, texture, offsetX, offsetY, width, height, isRoot);
    	
    	texture.clear(offsetX, offsetY, width, height, true, null);
    	Texture2DCanvas textureCanvas = texture.getTextureCanvas();
    	textureCanvas.setColor(new Color(0, 0, 0, 80));
    	Rect2i rectangle = new Rect2i(offsetX, offsetY, width, height);
    	textureCanvas.fillRect(rectangle);
    
    	int padding = 4;
    	int margin = TextureImage2D.getStringHeight("0%C", largeFont) / 2;
    	
    	//make square red when driver is first, otherwise grey
    }
	
	@Override
    protected void drawWidget( Clock clock, boolean needsCompleteRedraw, LiveGameData gameData, boolean isEditorMode, TextureImage2D texture, int offsetX, int offsetY, int width, int height )
    {
        if ( needsCompleteRedraw || isVisible() )
        {
        	dsDriverPos.draw( offsetX, offsetY, driverPos.toString(), texture );
            dsDriverName.draw( offsetX, offsetY, driverName, texture );
            dsTeamName.draw(offsetX, offsetY, teamName, texture);
            dsCarMake.draw(offsetX, offsetY, carMake, texture);
            dsCarModel.draw(offsetX, offsetY, carModel, texture);
            dsCaption.draw(offsetX, offsetY, caption, texture);
            dsLaptime.draw(offsetX, offsetY, laptime.getValueAsString(3), texture);
        }
    }
}
