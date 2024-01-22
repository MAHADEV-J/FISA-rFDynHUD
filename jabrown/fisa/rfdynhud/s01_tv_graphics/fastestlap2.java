package jabrown.fisa.rfdynhud.s01_tv_graphics;

import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.io.File;
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
import net.ctdp.rfdynhud.input.InputAction;
import net.ctdp.rfdynhud.properties.FontProperty;
import net.ctdp.rfdynhud.properties.IntProperty;
import net.ctdp.rfdynhud.properties.PropertiesContainer;
import net.ctdp.rfdynhud.properties.PropertyLoader;
import net.ctdp.rfdynhud.render.DrawnString;
import net.ctdp.rfdynhud.render.DrawnString.Alignment;
import net.ctdp.rfdynhud.render.DrawnStringFactory;
import net.ctdp.rfdynhud.render.Texture2DCanvas;
import net.ctdp.rfdynhud.render.TextureImage2D;
import net.ctdp.rfdynhud.util.PropertyWriter;
import net.ctdp.rfdynhud.util.SubTextureCollector;
import net.ctdp.rfdynhud.util.TimingUtil;
import net.ctdp.rfdynhud.valuemanagers.Clock;
import net.ctdp.rfdynhud.values.FloatValue;
import net.ctdp.rfdynhud.values.IntValue;
import net.ctdp.rfdynhud.widgets.base.widget.Widget;

/**
 * @author J.A. Brown
 */
public class fastestlap2 extends Widget
{
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
    private final IntValue driverPos = new IntValue(0);
    private String driverName = null;
    private String teamName = null;
    private String carMake = null;
    private String carModel = null;
    private String caption = null;
    public Laptime lap = null;
    private final FloatValue laptime = new FloatValue( -1f, 0.1f );
	
    private Boolean visible = false;

    public fastestlap2()
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
        
        writer.writeProperty( largeFont, "Large font that is used for race control messages." );
        writer.writeProperty( captionFont, "Font that is used for race control message caption." );
        writer.writeProperty( smallFont, "Small font that is used for timing." );
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
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void getProperties( PropertiesContainer propsCont, boolean forceAll )
    {
        super.getProperties( propsCont, forceAll );
        
        propsCont.addGroup("Extra Properties");
        
        propsCont.addProperty(largeFont);
        propsCont.addProperty(captionFont);
        propsCont.addProperty(smallFont);
        propsCont.addProperty( aspectRatioXOffset );
        propsCont.addProperty( aspectRatioYOffset );
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
    	int padding = 4;
    	int margin = TextureImage2D.getStringHeight("0%C", largeFont) / 2;
    	int flagHeight = (5 * height) / 12;
    	int flagWidth = (3 * flagHeight / 2);
    	dsDriverPos = drawnStringFactory.newDrawnString( "dsDriverPos", aspectRatioXOffset.getValue(), height - aspectRatioYOffset.getValue() - 140, Alignment.LEFT, false, largeFont.getFont(), isFontAntiAliased(), getFontColor() );
        dsDriverName = drawnStringFactory.newDrawnString( "dsDriverName", aspectRatioXOffset.getIntValue() + aspectRatioXOffset.getValue(), height - aspectRatioYOffset.getValue() - 160, Alignment.LEFT, false, captionFont.getFont(), isFontAntiAliased(), getFontColor() );
    	dsTeamName = drawnStringFactory.newDrawnString( "dsTeamName", aspectRatioXOffset.getValue() + aspectRatioXOffset.getValue(), height - aspectRatioYOffset.getValue() - 140, Alignment.LEFT, false, smallFont.getFont(), isFontAntiAliased(), getFontColor() );
    	dsCarMake = drawnStringFactory.newDrawnString( "dsCarMake", aspectRatioXOffset.getValue() + aspectRatioXOffset.getValue(), height - aspectRatioYOffset.getValue() - 120, Alignment.LEFT, false, captionFont.getFont(), isFontAntiAliased(), getFontColor() );
    	dsCarModel = drawnStringFactory.newDrawnString( "dsCarModel", aspectRatioXOffset.getValue() + aspectRatioXOffset.getValue(), height - aspectRatioYOffset.getValue() - 100, Alignment.LEFT, false, captionFont.getFont(), isFontAntiAliased(), getFontColor() );
    	dsCaption = drawnStringFactory.newDrawnString( "dsCaption", aspectRatioXOffset.getValue() + aspectRatioXOffset.getValue(), height - aspectRatioYOffset.getValue() - 80, Alignment.LEFT, false, captionFont.getFont(), isFontAntiAliased(), getFontColor() );
    	dsLaptime = drawnStringFactory.newDrawnString( "dsLaptime", aspectRatioXOffset.getValue() + aspectRatioXOffset.getValue(), height - aspectRatioYOffset.getValue() - 60, Alignment.LEFT, false, captionFont.getFont(), isFontAntiAliased(), getFontColor() );
    	caption = "FASTEST LAP";
    }
    
    @Override
    protected Boolean updateVisibility ( LiveGameData gameData, boolean isEditorMode )
    {
        ScoringInfo scoringInfo = gameData.getScoringInfo();
        lap = scoringInfo.getFastestLaptime();
        if(lap == null || !lap.isFinished())
        {
        	laptime.update(-1f);
        }
        else
        {
            laptime.update(lap.getLapTime());	
        }
        
        //it only becomes visible once at least 1 lap has been completed by the leader
        if(laptime.hasChanged() && laptime.isValid() && scoringInfo.getLeadersVehicleScoringInfo().getLapsCompleted() > 0)
        {
        	forceCompleteRedraw(true);
        	visible = true;
        }

    	if (visible == true || isEditorMode)
    	{
    		return true;
    	}
    	
    	return false;
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
    	
    	int posHeight = (5 * height) / 12;
    	int posWidth = posHeight;
    	int posOffsetX = offsetX + aspectRatioXOffset.getValue() + padding;
    	int posOffsetY = (offsetY + height) - aspectRatioYOffset.getValue();
    }
    
    @Override
    protected void drawWidget( Clock clock, boolean needsCompleteRedraw, LiveGameData gameData, boolean isEditorMode, TextureImage2D texture, int offsetX, int offsetY, int width, int height )
    {
    	ScoringInfo scoringInfo = gameData.getScoringInfo();
    	VehicleScoringInfo fastestCar = scoringInfo.getFastestLapVSI();
    	
        if ( needsCompleteRedraw || laptime.hasChanged() )
        {
        	driverName = fastestCar.getDriverNameShort();
        	driverPos.update(fastestCar.getPlace(false));
        	if(fastestCar.getVehicleInfo() != null)
        	{
        		carMake = fastestCar.getVehicleName();	
        	}
        	else
        	{
        		carMake = "CAR";
        	}
        	dsDriverPos.draw( offsetX, offsetY, driverPos.getValueAsString(), texture );
            dsDriverName.draw( offsetX, offsetY, driverName, texture );
            dsTeamName.draw(offsetX, offsetY, "TEAM", texture);
            dsCarMake.draw(offsetX, offsetY, carMake, texture);
            dsCarModel.draw(offsetX, offsetY, "TEST", texture);
        	dsCaption.draw( offsetX, offsetY, caption, texture );
            dsLaptime.draw( offsetX, offsetY, TimingUtil.getTimeAsLaptimeString(laptime.getValue()), texture );
        }
    }
}