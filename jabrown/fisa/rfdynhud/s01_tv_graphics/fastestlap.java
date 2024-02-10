package jabrown.fisa.rfdynhud.s01_tv_graphics;

import java.awt.Color;
import java.awt.Font;
import java.io.IOException;

import org.openmali.types.twodee.Rect2i;

import jabrown.fisa.rfdynhud.s01_tv_graphics._util.JABrownFISAWidgetSets01_tv_graphics;
import net.ctdp.rfdynhud.gamedata.Laptime;
import net.ctdp.rfdynhud.gamedata.LiveGameData;
import net.ctdp.rfdynhud.gamedata.ScoringInfo;
import net.ctdp.rfdynhud.gamedata.VehicleScoringInfo;
import net.ctdp.rfdynhud.properties.DelayProperty;
import net.ctdp.rfdynhud.properties.FloatProperty;
import net.ctdp.rfdynhud.properties.FontProperty;
import net.ctdp.rfdynhud.properties.ImageProperty;
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
public class fastestlap extends Widget
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
    private IntProperty margin = new IntProperty("Margin", 20);
    private IntProperty vMargin = new IntProperty("Vertical Margin", 20);
    private IntProperty modelAdjustment = new IntProperty("Model Y Offset Adjustment", 0);
    private IntProperty testNumber = new IntProperty("Test Position Number", 4);
    private IntProperty posAdjustment = new IntProperty("Position Number X Offset Adjustment", 0);
    private ImageProperty imgDriverFlag = new ImageProperty("imgDriverFlag", "");
    private ImageProperty imgTeamFlag = new ImageProperty("imgTeamFlag", "");
    private ImageProperty imgCarNumber = new ImageProperty("imgCarNumber", "");
    private ImageProperty imgCarClass = new ImageProperty("imgCarClass", "");
    private TextureImage2D driverFlag = null;
    private TextureImage2D teamFlag = null;
    private TextureImage2D numberIcon = null;
    private TextureImage2D classIcon = null;
	private int normalFontHeight = 0;
	private int modelFontHeight = 0;
	private int teamFontHeight = 0;
	private int captionFontHeight = 0;
	private int posHeight = 0;
	private int posWidth = 0;
	private int posXOffset = 0;
	private int posYOffset = 0;
	private int freeSpace = 0;
	private int line3YOffsetBig = 0;
	private int line3YOffsetSmall = 0;
	private int line2YOffset = 0;
	private int captionYOffset = 0;
	private int line1YOffset = 0;
	private int posNumYOffset = 0;
	private int posNumXOffset = 0;
	private int leftXOffset = 0;
	private int rightXOffset = 0;
	private final DelayProperty visibleTime;
	private long visibleEnd;
	
	//the data it needs
    private final IntValue driverPos = new IntValue(0);
    private String posString = "4";
    private String driverName = null;
    private String driverNat = null;
    private String teamName = null;
    private String teamNat = null;
    private String carMake = null;
    private String carModel = null;
    private String carNumber = null;
    private String carClass = null;
    private String caption = null;
    public Laptime lap = null;
    private final FloatValue laptime = new FloatValue( -1f, 0.1f );

    public fastestlap()
    {
        super( JABrownFISAWidgetSets01_tv_graphics.INSTANCE, JABrownFISAWidgetSets01_tv_graphics.WIDGET_PACKAGE_S01_TV_GRAPHICS, 20.0f, 32.5f );
        visibleTime = new DelayProperty("visibleTime", net.ctdp.rfdynhud.properties.DelayProperty.DisplayUnits.SECONDS, 5);
        visibleEnd = 0;
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
        writer.writeProperty(margin, "Horizontal margin between edge of screen and text");
        writer.writeProperty(vMargin, "Vertical margin between text and position square");
        writer.writeProperty(modelAdjustment, "Car model Y offset adjustment (from below).");
        writer.writeProperty(testNumber, "Position (for testing layout).");
        writer.writeProperty(posAdjustment, "Position number X offset adjustment (from right).");
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
        else if (loader.loadProperty(margin));
        else if (loader.loadProperty(vMargin));
        else if (loader.loadProperty(modelAdjustment));
        else if (loader.loadProperty(testNumber));
        else if (loader.loadProperty(posAdjustment));
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
        propsCont.addProperty(margin);
        propsCont.addProperty(vMargin);
        propsCont.addProperty(modelAdjustment);
        propsCont.addProperty(testNumber);
        propsCont.addProperty(posAdjustment);
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
    	normalFontHeight = (int) Math.ceil(TextureImage2D.getStringHeight("0%C", normalFont) * lineHeight.getValue());
    	modelFontHeight = (int) Math.ceil(TextureImage2D.getStringHeight("0%C", modelFont) * lineHeight.getValue());
    	teamFontHeight = (int) Math.ceil(TextureImage2D.getStringHeight("0%C", teamFont) * lineHeight.getValue());
    	captionFontHeight = (int) Math.ceil(TextureImage2D.getStringHeight("0%C", captionFont) * lineHeight.getValue());
    	posHeight = normalFontHeight * 3;
    	posWidth = Math.max(posHeight, TextureImage2D.getStringWidth("222", posFont) + 2 * padding.getValue()); //width of the "square"
    	posXOffset = aspectRatioXOffset.getValue() + vMargin.getValue() / 2;
    	
    	freeSpace = height - aspectRatioYOffset.getValue() - margin.getValue();
    	line3YOffsetBig = freeSpace - normalFontHeight;
    	line3YOffsetSmall = freeSpace - modelFontHeight;
    	line2YOffset = line3YOffsetBig - teamFontHeight;
    	captionYOffset = line3YOffsetBig - captionFontHeight;
    	line1YOffset = line2YOffset - normalFontHeight;
    	posNumYOffset = line1YOffset + padding.getValue();
    	posNumXOffset = posXOffset + (posWidth - TextureImage2D.getStringWidth("222", posFont) / 2);
    	leftXOffset = posXOffset + posWidth + vMargin.getValue();
    	rightXOffset = width - aspectRatioXOffset.getValue() - vMargin.getValue();
    	
    	dsDriverPos = drawnStringFactory.newDrawnString( "dsDriverPos", posNumXOffset - posAdjustment.getValue(), posNumYOffset, Alignment.CENTER, false, posFont.getFont(), isFontAntiAliased(), getFontColor() );
        dsDriverName = drawnStringFactory.newDrawnString( "dsDriverName", leftXOffset, line1YOffset, Alignment.LEFT, false, normalFont.getFont(), isFontAntiAliased(), getFontColor() );
    	dsTeamName = drawnStringFactory.newDrawnString( "dsTeamName", leftXOffset, line2YOffset, Alignment.LEFT, false, teamFont.getFont(), isFontAntiAliased(), getFontColor() );
    	dsCarMake = drawnStringFactory.newDrawnString( "dsCarMake", leftXOffset, line3YOffsetBig, Alignment.LEFT, false, normalFont.getFont(), isFontAntiAliased(), getFontColor() );
    	dsCarModel = drawnStringFactory.newDrawnString( "dsCarModel", leftXOffset, line3YOffsetSmall - modelAdjustment.getValue(), Alignment.LEFT, false, modelFont.getFont(), isFontAntiAliased(), getFontColor() );
    	dsCaption = drawnStringFactory.newDrawnString( "dsCaption", rightXOffset, captionYOffset, Alignment.RIGHT, false, captionFont.getFont(), isFontAntiAliased(), getFontColor() );
    	dsLaptime = drawnStringFactory.newDrawnString( "dsLaptime", rightXOffset, line3YOffsetBig, Alignment.RIGHT, false, normalFont.getFont(), isFontAntiAliased(), getFontColor() );
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
        
        if(isEditorMode)
        {
        	return true;
        }
        
        //it only becomes visible once at least 1 lap has been completed by the leader
        if(laptime.hasChanged() && laptime.isValid() && scoringInfo.getLeadersVehicleScoringInfo().getLapsCompleted() > 0)
        {
        	forceCompleteRedraw(true);
            visibleEnd = scoringInfo.getSessionNanos() + visibleTime.getDelayNanos();
        	return true;
        }
    	
        //fastest race laps never get set under safety car conditions, so no extra checks needed
        if(scoringInfo.getSessionNanos() < visibleEnd )
        {
            forceCompleteRedraw(true);
            return true;
        }
    	
    	return false;
    }
    
    protected void getDriverInfo(LiveGameData gameData, boolean isEditorMode)
    {
    	ScoringInfo scoringInfo = gameData.getScoringInfo();
    	VehicleScoringInfo fastestCar = scoringInfo.getFastestLapVSI();
    	
    	carModel = "TEST";
    	
    	driverName = fastestCar.getDriverNameShort().toUpperCase();
    	String driverData = JABrownFISAWidgetSets01_tv_graphics.getDriverData(fastestCar.getDriverName(), gameData.getFileSystem().getConfigFolder());
    	driverPos.update(fastestCar.getPlace(false));
    	if( laptime.isValid() && (fastestCar.getVehicleInfo() != null || fastestCar.isPlayer()) )
    	{
    		driverNat = driverData.split(";")[0];
        	teamName = driverData.split(";")[1];
        	teamNat = driverData.split(";")[2];
        	carMake = driverData.split(";")[3];
        	carModel = driverData.split(";")[4];
        	carNumber = driverData.split(";")[5];
        	posString = driverPos.getValueAsString();
    	}
    	else
    	{
    		teamName = "TEAM";
    		carMake = "CAR";
    	}
    	if(isEditorMode)
    	{
    		driverNat = "ITA";
    		teamName = "EXTREMELY LONG SPONSOR NAME VERY LONG TEAM NAME MOTORSPORTS";
    		teamNat = "NED";
    		carMake = "MASERATI";
    		carModel = "QUATTROPORTE";
    		carNumber = "FOKOF";
    		posString = testNumber.getValue().toString();
    	}
    	
    	if(driverNat == "NOT IN THIS RACE")
    	{
    		imgDriverFlag = new ImageProperty("imgDriverFlag", "fisa/flags/ita.png");
    	}
    	else
    	{
    		imgDriverFlag = new ImageProperty("imgDriverFlag", "fisa/flags/" + driverNat.toLowerCase() + ".png");
    	}
    	float aspectRatio = imgDriverFlag.getImage().getBaseAspect();
    	int flagHeight = 16;
    	int flagWidth = (int) Math.round((float) flagHeight * aspectRatio);
    	driverFlag = imgDriverFlag.getImage().getScaledTextureImage(flagWidth, flagHeight, driverFlag, isEditorMode);
    	
    	if(teamNat == "NOT IN THIS RACE")
    	{
    		imgTeamFlag = new ImageProperty("imgTeamFlag", "fisa/flags/ned.png");
    	}
    	else
    	{
    		imgTeamFlag = new ImageProperty("imgTeamFlag", "fisa/flags/" + teamNat.toLowerCase() + ".png");
    	}
    	aspectRatio = imgTeamFlag.getImage().getBaseAspect(); 
    	flagHeight = 12;
    	flagWidth = (int) Math.round((float) flagHeight * aspectRatio);
    	teamFlag = imgTeamFlag.getImage().getScaledTextureImage(flagWidth, flagHeight, teamFlag, isEditorMode);
    	
    	if(carNumber == "NOT IN THIS RACE" || carNumber == "FOKOF")
    	{
    		imgCarNumber = new ImageProperty("imgCarNumber", "fisa/numbers/9.png");
    	}
    	else
    	{
    		imgCarNumber = new ImageProperty("imgCarNumber", "fisa/numbers/" + carNumber.toLowerCase() + ".png");
    	}
    	numberIcon = imgCarNumber.getImage().getTextureImage();
    	
    	carClass = fastestCar.getVehicleClass();
    	if(isEditorMode)
    	{
    		carClass = "SC1";
    	}
    	imgCarClass = new ImageProperty("classIcon", "fisa/class/" + carClass.toLowerCase() + ".png");
    	classIcon = imgCarClass.getImage().getTextureImage();
    }
    
    @Override
    protected void drawBackground( LiveGameData gameData, boolean isEditorMode, TextureImage2D texture, int offsetX, int offsetY, int width, int height, boolean isRoot )
    {
    	super.drawBackground(gameData, isEditorMode, texture, offsetX, offsetY, width, height, isRoot);    	
    	
    	getDriverInfo(gameData, isEditorMode);
    	
    	texture.clear(offsetX, offsetY, width, height, true, null);
    	Texture2DCanvas textureCanvas = texture.getTextureCanvas();
    	textureCanvas.setColor(new Color(0, 0, 0, 80));
    	rectangle = new Rect2i(offsetX, offsetY, width, height); //the whole thing
    	textureCanvas.fillRect(rectangle); //make the whole thing transparent black (background)
    		
    	posYOffset = (offsetY + height) - aspectRatioYOffset.getValue() - margin.getValue() - posHeight;
    	
    	square = new Rect2i(posXOffset, posYOffset, posWidth, posHeight); //the "square" (not really a square) that the position number is drawn on
    	
    	ScoringInfo scoringInfo = gameData.getScoringInfo();
    	VehicleScoringInfo fastestCar = scoringInfo.getFastestLapVSI();
    	if(fastestCar.getPlace(false) == 1)
    	{
    		textureCanvas.setColor(new Color(161, 9, 11, 255)); //make the square red if the car is in first place
    	}
    	else
    	{
        	textureCanvas.setColor(new Color(87, 89, 89, 255)); //otherwise grey
    	}

    	textureCanvas.fillRect(square);
    }
    
    @Override
    protected void drawWidget( Clock clock, boolean needsCompleteRedraw, LiveGameData gameData, boolean isEditorMode, TextureImage2D texture, int offsetX, int offsetY, int width, int height )
    {
        if ( needsCompleteRedraw || laptime.hasChanged() )
        {
        	dsDriverPos.draw( offsetX, offsetY, posString, texture );
            dsDriverName.draw( offsetX, offsetY, driverName, texture );
            //TODO: don't understand why it's 6 pixels too high
            texture.drawImage(driverFlag, offsetX + leftXOffset + TextureImage2D.getStringWidth(driverName + "  ", normalFont), offsetY + line1YOffset + 6, false, null);
            dsTeamName.draw(offsetX, offsetY, teamName, texture);
            //TODO: don't understand why it's 4 pixels too high
            texture.drawImage(teamFlag, offsetX + leftXOffset + TextureImage2D.getStringWidth(teamName + "  ", teamFont), offsetY + line2YOffset + 4, false, null);
            dsCarMake.draw(offsetX, offsetY, carMake, texture);
            dsCarModel.draw(dsCarMake.getLastWidth() + TextureImage2D.getStringWidth("  ", normalFont), offsetY, carModel, texture, true);
            texture.drawImage(numberIcon, offsetX + leftXOffset + dsCarMake.getLastWidth() + TextureImage2D.getStringWidth("  ",  normalFont) + dsCarModel.getLastWidth() + TextureImage2D.getStringWidth("   ", modelFont), offsetY + line3YOffsetBig + 6, false, null);
            texture.drawImage(classIcon, offsetX + leftXOffset + dsCarMake.getLastWidth() + TextureImage2D.getStringWidth("  ", normalFont) + dsCarModel.getLastWidth() + TextureImage2D.getStringWidth(" ", modelFont) + numberIcon.getWidth() + TextureImage2D.getStringWidth("  ", modelFont), offsetY + line3YOffsetBig + 6, false, null);
        	dsCaption.draw( offsetX, offsetY, caption, texture );
            dsLaptime.draw( offsetX, offsetY, TimingUtil.getTimeAsLaptimeString(laptime.getValue()), texture );
        }
    }
}