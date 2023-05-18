package jabrown.fisa.rfdynhud.s01_tv_graphics;

import java.awt.Color;
import java.awt.Font;
import java.io.IOException;

import org.openmali.types.twodee.Rect2i;

import jabrown.fisa.rfdynhud.s01_tv_graphics._util.JABrownFISAWidgetSets01_tv_graphics;
import net.ctdp.rfdynhud.gamedata.LiveGameData;
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
    private final FontProperty largeFont = new FontProperty("LargeFont", JABrownFISAWidgetSets01_tv_graphics.FISA_S01_TV_GRAPHICS_LARGE_FONT.getKey());
    private final FontProperty captionFont = new FontProperty("CaptionFont", JABrownFISAWidgetSets01_tv_graphics.FISA_S01_TV_GRAPHICS_CAPTION_FONT.getKey());
    private final FontProperty smallFont = new FontProperty("SmallFont", JABrownFISAWidgetSets01_tv_graphics.FISA_S01_TV_GRAPHICS_SMALL_FONT.getKey());
    
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
        dsCaption = drawnStringFactory.newDrawnString( "dsCaption", 240, 24, Alignment.LEFT, false, captionFont.getFont(), isFontAntiAliased(), getFontColor() );
    	dsInformation = drawnStringFactory.newDrawnString( "dsInformation", 240, 60, Alignment.LEFT, false, largeFont.getFont(), isFontAntiAliased(), getFontColor() );
    	captionText = "RACE CONTROL";
    	scOut = "SAFETY CAR";
    	scIn = "SAFETY CAR IN THIS LAP";
    	redFlag = "RACE STOPPED";
    	informationText = "NOT SPECIFIED";
    }
    
    public void toggleInformationText(String text)
    {
    	informationText = text;
    }
    
    public void toggleSafetyCarOut()
    {
    	if(informationText != scOut)
    	{
    		toggleInformationText(scOut);
    	}
    }
    
    public void toggleSafetyCarIn()
    {
    	if(informationText != scIn)
    	{
    		toggleInformationText(scIn);
    	}
    }
    
    public void toggleRedFlag()
    {
    	if(informationText != redFlag)
    	{
    		toggleInformationText(redFlag);
    	}
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
    	
    	textureCanvas.setColor(Color.YELLOW);
    	int flagHeight = (5 * height) / 12;
    	int flagWidth = (3 * flagHeight / 2);
    	int flagOffsetX = offsetX + 28;
    	int flagOffsetY = offsetY + 28;
    	Rect2i flag = new Rect2i(flagOffsetX, flagOffsetY, flagWidth, flagHeight);
    	textureCanvas.fillRect(flag);
    }
    
    @Override
    protected void drawWidget( Clock clock, boolean needsCompleteRedraw, LiveGameData gameData, boolean isEditorMode, TextureImage2D texture, int offsetX, int offsetY, int width, int height )
    {
        if ( needsCompleteRedraw )
        {
        	dsCaption.draw( offsetX, offsetY, captionText, texture );
            dsInformation.draw( offsetX, offsetY, informationText, texture );
        }
    }
}