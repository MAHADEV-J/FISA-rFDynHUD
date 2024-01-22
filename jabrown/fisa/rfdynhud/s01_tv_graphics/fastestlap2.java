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
import net.ctdp.rfdynhud.properties.FloatProperty;
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
    private final FontProperty posFont = new FontProperty("posFont", JABrownFISAWidgetSets01_tv_graphics.FISA_S01_TV_GRAPHICS_LARGE_FONT.getKey());
    private final FontProperty normalFont = new FontProperty("normalFont", JABrownFISAWidgetSets01_tv_graphics.FISA_S01_TV_GRAPHICS_CAPTION_FONT.getKey());
    private final FontProperty teamFont = new FontProperty("teamFont", JABrownFISAWidgetSets01_tv_graphics.FISA_S01_TV_GRAPHICS_SMALL_FONT.getKey());
    private final FontProperty modelFont = new FontProperty("modelFont", JABrownFISAWidgetSets01_tv_graphics.FISA_S01_TV_GRAPHICS_CAPTION_FONT.getKey());
    private final FontProperty captionFont = new FontProperty("captionFont", JABrownFISAWidgetSets01_tv_graphics.FISA_S01_TV_GRAPHICS_CAPTION_FONT.getKey());
    private IntProperty aspectRatioYOffset = new IntProperty("Y Offset (From Below)", 0);
    private IntProperty aspectRatioXOffset = new IntProperty("X Offset", 0);
    private FloatProperty lineHeight = new FloatProperty("Line Height", 1.0f);
    private IntProperty padding = new IntProperty("Padding", 4);
	
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
        posFont.setFont(JABrownFISAWidgetSets01_tv_graphics.FISA_S01_TV_GRAPHICS_LARGE_FONT_NAME, Font.BOLD, 64, true, true);
        normalFont.setFont(JABrownFISAWidgetSets01_tv_graphics.FISA_S01_TV_GRAPHICS_CAPTION_FONT_NAME, Font.BOLD, 26, true, true);
        teamFont.setFont(JABrownFISAWidgetSets01_tv_graphics.FISA_S01_TV_GRAPHICS_SMALL_FONT_NAME, Font.BOLD, 22, true, true);
        modelFont.setFont(JABrownFISAWidgetSets01_tv_graphics.FISA_S01_TV_GRAPHICS_CAPTION_FONT_NAME, Font.BOLD, 24, true, true);
        captionFont.setFont(JABrownFISAWidgetSets01_tv_graphics.FISA_S01_TV_GRAPHICS_CAPTION_FONT_NAME, Font.BOLD, 30, true, true);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void saveProperties( PropertyWriter writer ) throws IOException
    {
        super.saveProperties( writer );
        
        writer.writeProperty( posFont, "Large font that is used for position number." );
        writer.writeProperty( normalFont, "Font that is used for driver name, car name, and laptime." );
        writer.writeProperty(modelFont, "Font that is used for car model.");
        writer.writeProperty( teamFont, "Small font that is used for team name." );
        writer.writeProperty( captionFont, "Font that is used for caption." );
        writer.writeProperty( aspectRatioXOffset, "Flag X offset." );
        writer.writeProperty( aspectRatioYOffset, "Flag Y offset (from below)." );
        writer.writeProperty( lineHeight, "Line height.");
        writer.writeProperty(padding, "Padding around position number.");
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void loadProperty( PropertyLoader loader )
    {
        super.loadProperty( loader );
        
        if ( loader.loadProperty( posFont ) );
        else if ( loader.loadProperty(normalFont));
        else if (loader.loadProperty(modelFont));
        else if ( loader.loadProperty( teamFont ) );
        else if ( loader.loadProperty( captionFont ) );
        else if ( loader.loadProperty( aspectRatioXOffset ) );
        else if ( loader.loadProperty( aspectRatioYOffset ) );
        else if ( loader.loadProperty(lineHeight));
        else if (loader.loadProperty(padding));
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void getProperties( PropertiesContainer propsCont, boolean forceAll )
    {
        super.getProperties( propsCont, forceAll );
        
        propsCont.addGroup("Extra Properties");
        
        propsCont.addProperty(posFont);
        propsCont.addProperty(normalFont);
        propsCont.addProperty(modelFont);
        propsCont.addProperty(teamFont);
        propsCont.addProperty(captionFont);
        propsCont.addProperty( aspectRatioXOffset );
        propsCont.addProperty( aspectRatioYOffset );
        propsCont.addProperty(lineHeight);
        propsCont.addProperty(padding);
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
    	int margin = TextureImage2D.getStringHeight("0%C", posFont) / 2;
    	int normalFontHeight = TextureImage2D.getStringHeight("0%C", normalFont);
    	int teamFontHeight = TextureImage2D.getStringHeight("0%C", teamFont);
    	int captionFontHeight = TextureImage2D.getStringHeight("0%C", captionFont);
    	int posHeight = (5 * height) / 12;
    	int posWidth = posHeight + TextureImage2D.getStringWidth("8", posFont);
    	
    	int posYOffset = height - aspectRatioYOffset.getValue() - padding.getValue() - margin - (posHeight / 3);
    	int line1YOffset = height - aspectRatioYOffset.getValue() - margin - lineHeight.getValue() * normalFontHeight - lineHeight.getValue() * teamFontHeight - (lineHeight.getValue() / 2) * normalFontHeight;
    	int line2YOffset = height - aspectRatioYOffset.getValue() - margin - lineHeight.getValue() * normalFontHeight - (lineHeight.getValue() / 2) * teamFontHeight;
    	int captionYOffset = height - aspectRatioYOffset.getValue() - margin - lineHeight.getValue() * normalFontHeight - (lineHeight.getValue() / 2) * captionFontHeight; 
    	int line3YOffset = height - aspectRatioYOffset.getValue() - margin - (lineHeight.getValue() / 2) * normalFontHeight;
    	
    	dsDriverPos = drawnStringFactory.newDrawnString( "dsDriverPos", aspectRatioXOffset.getValue() + padding.getValue() + posWidth / 2, posYOffset, Alignment.CENTER, true, posFont.getFont(), isFontAntiAliased(), getFontColor() );
        dsDriverName = drawnStringFactory.newDrawnString( "dsDriverName", aspectRatioXOffset.getIntValue() + 2 * padding.getValue() + posWidth + margin, line1YOffset, Alignment.LEFT, true, normalFont.getFont(), isFontAntiAliased(), getFontColor() );
    	dsTeamName = drawnStringFactory.newDrawnString( "dsTeamName", aspectRatioXOffset.getValue() + 2 * padding.getValue() + posWidth + margin, line2YOffset, Alignment.LEFT, true, teamFont.getFont(), isFontAntiAliased(), getFontColor() );
    	dsCarMake = drawnStringFactory.newDrawnString( "dsCarMake", aspectRatioXOffset.getValue() + 2 * padding.getValue() + posWidth + margin, line3YOffset, Alignment.LEFT, true, normalFont.getFont(), isFontAntiAliased(), getFontColor() );
    	dsCarModel = drawnStringFactory.newDrawnString( "dsCarModel", aspectRatioXOffset.getValue() + 2 * padding.getValue() + posWidth + margin, line3YOffset, Alignment.LEFT, true, modelFont.getFont(), isFontAntiAliased(), getFontColor() );
    	dsCaption = drawnStringFactory.newDrawnString( "dsCaption", width - aspectRatioXOffset.getValue() - padding.getValue(), captionYOffset, Alignment.RIGHT, true, captionFont.getFont(), isFontAntiAliased(), getFontColor() );
    	dsLaptime = drawnStringFactory.newDrawnString( "dsLaptime", width - aspectRatioXOffset.getValue() - padding.getValue(), line3YOffset, Alignment.RIGHT, true, captionFont.getFont(), isFontAntiAliased(), getFontColor() );
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
    
    	int margin = TextureImage2D.getStringHeight("0%C", posFont) / 2;
    	int normalFontHeight = TextureImage2D.getStringHeight("0%C", normalFont);
    	int teamFontHeight = TextureImage2D.getStringHeight("0%C", teamFont);
    	int captionFontHeight = TextureImage2D.getStringHeight("0%C", captionFont);
    	
    	int posHeight = 2 * lineHeight.getValue() * normalFontHeight - lineHeight.getValue() * teamFontHeight + 2 * padding.getValue();
    	int posWidth = posHeight + TextureImage2D.getStringWidth("8", posFont);
    	int posOffsetX = offsetX + aspectRatioXOffset.getValue() + padding.getValue();
    	int posOffsetY = (offsetY + height) - aspectRatioYOffset.getValue() - margin - posHeight; 
    	
    	Rect2i square = new Rect2i(posOffsetX, posOffsetY, posWidth, posHeight);
    	
    	ScoringInfo scoringInfo = gameData.getScoringInfo();
    	VehicleScoringInfo fastestCar = scoringInfo.getFastestLapVSI();
    	if(fastestCar.getPlace(false) == 1)
    	{
    		textureCanvas.setColor(new Color(161, 9, 11, 255));
    	}
    	else
    	{
        	textureCanvas.setColor(new Color(87, 89, 89, 255));    		
    	}

    	textureCanvas.fillRect(square);
    }
    
    @Override
    protected void drawWidget( Clock clock, boolean needsCompleteRedraw, LiveGameData gameData, boolean isEditorMode, TextureImage2D texture, int offsetX, int offsetY, int width, int height )
    {
    	ScoringInfo scoringInfo = gameData.getScoringInfo();
    	VehicleScoringInfo fastestCar = scoringInfo.getFastestLapVSI();
    	
    	carModel = "TEST";
        if ( needsCompleteRedraw || laptime.hasChanged() )
        {
        	driverName = fastestCar.getDriverNameShort().toUpperCase();
        	String driverData = JABrownFISAWidgetSets01_tv_graphics.getDriverData(fastestCar.getDriverName(), gameData.getFileSystem().getConfigFolder());
        	driverPos.update(fastestCar.getPlace(false));
        	if(fastestCar.getVehicleInfo() != null)
        	{
            	teamName = driverData.split(";")[1];
            	carMake = driverData.split(";")[3];
            	carModel = driverData.split(";")[4];
        	}
        	else
        	{
        		teamName = "TEAM";
        		carMake = "CAR";
        	}
        	if(isEditorMode)
        	{
        		teamName = "EXTREMELY LONG SPONSOR NAME VERY LONG TEAM NAME MOTORSPORTS";
        		carMake = "MASERATI";
        		carModel = "QUATTROPORTE";
        	}
        	dsDriverPos.draw( offsetX, offsetY, driverPos.getValueAsString(), texture );
            dsDriverName.draw( offsetX, offsetY, driverName, texture );
            dsTeamName.draw(offsetX, offsetY, teamName, texture);
            dsCarMake.draw(offsetX, offsetY, carMake, texture);
            dsCarModel.draw(dsCarMake.getLastWidth() + TextureImage2D.getStringWidth(" ", captionFont), offsetY, carModel, texture, true);
        	dsCaption.draw( offsetX, offsetY, caption, texture );
            dsLaptime.draw( offsetX, offsetY, TimingUtil.getTimeAsLaptimeString(laptime.getValue()), texture );
        }
    }
}