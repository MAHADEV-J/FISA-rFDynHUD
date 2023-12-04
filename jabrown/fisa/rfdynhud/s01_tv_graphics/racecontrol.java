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
import net.ctdp.rfdynhud.gamedata.LiveGameData;
import net.ctdp.rfdynhud.gamedata.ScoringInfo;
import net.ctdp.rfdynhud.gamedata.YellowFlagState;
import net.ctdp.rfdynhud.input.InputAction;
import net.ctdp.rfdynhud.properties.FontProperty;
import net.ctdp.rfdynhud.properties.PropertiesContainer;
import net.ctdp.rfdynhud.properties.PropertyLoader;
import net.ctdp.rfdynhud.render.DrawnString;
import net.ctdp.rfdynhud.render.DrawnString.Alignment;
import net.ctdp.rfdynhud.render.DrawnStringFactory;
import net.ctdp.rfdynhud.render.Texture2DCanvas;
import net.ctdp.rfdynhud.render.TextureImage2D;
import net.ctdp.rfdynhud.util.PropertyWriter;
import net.ctdp.rfdynhud.util.SubTextureCollector;
import net.ctdp.rfdynhud.valuemanagers.Clock;
import net.ctdp.rfdynhud.values.FloatValue;
import net.ctdp.rfdynhud.widgets.base.widget.Widget;

/**
 * @author J.A. Brown
 */
public class racecontrol extends Widget
{
	private DrawnString dsCaption = null;
    private DrawnString dsInformation = null;
    private String captionText = "";
    private String informationText = "";
    private String scOut = "";
    private String scIn = "";
    private String redFlag = "";
    private String greenFlag = "";
    private File goingGreen = new File("");
    private final FontProperty largeFont = new FontProperty("LargeFont", JABrownFISAWidgetSets01_tv_graphics.FISA_S01_TV_GRAPHICS_LARGE_FONT.getKey());
    private final FontProperty captionFont = new FontProperty("CaptionFont", JABrownFISAWidgetSets01_tv_graphics.FISA_S01_TV_GRAPHICS_CAPTION_FONT.getKey());
    private final FontProperty smallFont = new FontProperty("SmallFont", JABrownFISAWidgetSets01_tv_graphics.FISA_S01_TV_GRAPHICS_SMALL_FONT.getKey());
    private final float visibleTime = 10.0f;
    private float disappearTime = -1f;
    private static final InputAction ToggleSafetyCarOut = new InputAction ("ToggleSafetyCarOut", false); //defines an input action
    private static final InputAction ToggleSafetyCarIn = new InputAction ("ToggleSafetyCarIn", false); //defines an input action
    private static final InputAction ToggleRedFlag = new InputAction ("ToggleRedFlag", false); //defines an input action
    private static final InputAction Hide = new InputAction ("Hide", false); //defines an input action
    private Boolean visible = false;
    private int informationToShow = 5;
    private FloatValue currentSector = null;
    
    public racecontrol()
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
    	currentSector = new FloatValue();
    	int aspectRatioOffset = 160;
    	int padding = 4;
    	int margin = TextureImage2D.getStringHeight("0%C", largeFont) / 2;
    	int flagHeight = (5 * height) / 12;
    	int flagWidth = (3 * flagHeight / 2);
        dsCaption = drawnStringFactory.newDrawnString( "dsCaption", aspectRatioOffset + padding + flagWidth + padding + margin + 3, margin / 2, Alignment.LEFT, false, captionFont.getFont(), isFontAntiAliased(), getFontColor() );
    	dsInformation = drawnStringFactory.newDrawnString( "dsInformation", aspectRatioOffset + padding + flagWidth + padding + margin, 2 * margin + padding, Alignment.LEFT, false, largeFont.getFont(), isFontAntiAliased(), getFontColor() );
    	captionText = "RACE CONTROL";
    	scOut = "SAFETY CAR";
    	scIn = "SAFETY CAR IN THIS LAP";
    	redFlag = "RACE STOPPED";
    	greenFlag = "GREEN FLAG";
    	goingGreen = new File("fisa/goinggreen.png");
    	switch (informationToShow)
    	{
    		case 0:
    			informationText = scOut;
    			break;
    		case 1:
    			informationText = scIn;
    			break;
    		case 2:
    			informationText = redFlag;
    			break;
    		case 3:
    			informationText = greenFlag;
    			break;
    		default:
    			informationText = "NOT SPECIFIED";
    	}
    }
    
    public void toggleInformationText(String text)
    {
    	informationText = text;
    }
    
    @Override
    public InputAction[] getInputActions()
    {
    	//Registers this action as an action that a key input can be bound to.
    	return (new InputAction[] {ToggleSafetyCarOut, ToggleSafetyCarIn, ToggleRedFlag, Hide});
    }
    
    @Override
    public Boolean onBoundInputStateChanged(InputAction action, boolean state, int modifierMask, long when, LiveGameData gameData, boolean isEditorMode)
    {
    	Boolean result = super.onBoundInputStateChanged(action, state, modifierMask, when, gameData, isEditorMode);
    	
    	if(action == ToggleSafetyCarOut)
    	{
    		ToggleSafetyCarOut();
    		visible = true;
    	}
    	if(action == ToggleSafetyCarIn)
    	{
    		ToggleSafetyCarIn();
    		visible = true;
    	}
    	if(action == ToggleRedFlag)
    	{
    		ToggleRedFlag();
    		visible = true;
    	}
    	if(action == Hide)
    	{
    		visible = false;
    	}
    	
    	return result;
    }
    
    public void ToggleSafetyCarOut()
    {
    	informationToShow = 0;
		toggleInformationText(scOut);
    	forceCompleteRedraw(true);
    }
    
    public void ToggleSafetyCarIn()
    {
   		informationToShow = 1;
   		toggleInformationText(scIn);
    	forceCompleteRedraw(true);
    }
    
    public void ToggleRedFlag()
    {
   		informationToShow = 2;
   		toggleInformationText(redFlag);
    	forceCompleteRedraw(true);
    }
    
    @Override
    protected Boolean updateVisibility ( LiveGameData gameData, boolean isEditorMode )
    {
    	//visible = false;
    	//visible = super.updateVisibility(gameData, isEditorMode);
//    	Boolean bongo = false;
//    	
        ScoringInfo scoringInfo = gameData.getScoringInfo();
//    	
        if (scoringInfo.getYellowFlagState() == YellowFlagState.PENDING)
        {
        	ToggleSafetyCarOut();
//        	bongo = true;	
        }
        if (scoringInfo.getYellowFlagState() == YellowFlagState.LAST_LAP)
        {
        	ToggleSafetyCarIn();
//        	if (scoringInfo.getLeadersVehicleScoringInfo().getLapDistance() < gameData.getTrackInfo().getTrack().getSector1Length())
//        	{
//            	bongo = true;	
//        	}
//        	else
//        	{
//        		bongo = false;
//        	}
        }
        if (scoringInfo.getOnPathWetness() >= 0.5f) //when it's raining on ovals
        {
        	ToggleRedFlag();
//        	bongo = true;
        }
        if (scoringInfo.getYellowFlagState() == YellowFlagState.RESUME)
        {
        	informationToShow = 3;
        	toggleInformationText(greenFlag);
        	forceCompleteRedraw(true);
        }
        if (scoringInfo.getYellowFlagState() == YellowFlagState.NONE && scoringInfo.getGamePhase() == GamePhase.GREEN_FLAG)
        {
        	informationToShow = 3;
        	toggleInformationText(greenFlag);
        	forceCompleteRedraw(true);
        }
        
//        visible = true;
    	return true;
        
//        if (bongo == true)
//        {
//        	//disappearTime = gameData.getScoringInfo().getSessionTime() + visibleTime;
//        	visible = true;
//        	forceCompleteRedraw(true);
//        }
        
//        if (gameData.getScoringInfo().getSessionTime() < disappearTime)
//        {
//        	visible = true;
//        }
//        else
//        {
//        	visible = false;
//        }

        
//    	if(visible == true || isEditorMode)
//    	{
//    		return true;
//    	}
//    	
//    	return false;
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
    
    	int aspectRatioOffset = 80;
    	int margin = TextureImage2D.getStringHeight("0%C", largeFont) / 2;
    	int padding = 4;
    	
    	int flagHeight = (5 * height) / 12;
    	int flagWidth = (3 * flagHeight / 2);
    	int flagOffsetX = offsetX + aspectRatioOffset + padding;
    	int flagOffsetY = offsetY + 2 * margin;
    	Rect2i flag = new Rect2i(flagOffsetX, flagOffsetY, flagWidth, flagHeight);
    	
    	if(informationToShow == 0)
    	{
    		textureCanvas.setColor(Color.YELLOW);
        	textureCanvas.fillRect(flag);
    	}
    	else if(informationToShow == 1)
    	{
    		textureCanvas.setColor(Color.YELLOW);
    		//perhaps if this fails because the file is not found, we get an error or the game crashes.
    		//so this file MUST be in "Plugins/rfDynHUD/config/data/images/fisa"
    		try {
				Image yellowGreenFlag = ImageIO.read(goingGreen);
				textureCanvas.drawImage(yellowGreenFlag, flagOffsetX, flagOffsetY, flagWidth, flagHeight);
			} catch (IOException e) {
				//e.printStackTrace();
			}
    		textureCanvas.fillRect(flag);
    	}
    	else if(informationToShow == 2)
    	{
    		textureCanvas.setColor(Color.RED);
    		textureCanvas.fillRect(flag);
    	}
    	else if(informationToShow == 3)
    	{
    		textureCanvas.setColor(new Color(0, 204, 0, 255));
    		textureCanvas.fillRect(flag);
    	}
    	else
    	{
    		textureCanvas.setColor(new Color(0, 0, 0, 0));
    		textureCanvas.fillRect(flag);
    	}
    }
    
    @Override
    protected void drawWidget( Clock clock, boolean needsCompleteRedraw, LiveGameData gameData, boolean isEditorMode, TextureImage2D texture, int offsetX, int offsetY, int width, int height )
    {
        if ( needsCompleteRedraw || isVisible() )
        {
        	dsCaption.draw( offsetX, offsetY, captionText, texture );
            dsInformation.draw( offsetX, offsetY, informationText, texture );
        }
    }
}