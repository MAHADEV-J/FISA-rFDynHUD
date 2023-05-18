package jabrown.fisa.rfdynhud.s01_tv_graphics;

import java.awt.Color;
import java.awt.Font;

import org.openmali.types.twodee.Rect2i;

import jabrown.fisa.rfdynhud.s01_tv_graphics._util.JABrownFISAWidgetSets01_tv_graphics;
import net.ctdp.rfdynhud.gamedata.LiveGameData;
import net.ctdp.rfdynhud.render.DrawnString;
import net.ctdp.rfdynhud.render.DrawnString.Alignment;
import net.ctdp.rfdynhud.render.DrawnStringFactory;
import net.ctdp.rfdynhud.render.Texture2DCanvas;
import net.ctdp.rfdynhud.render.TextureImage2D;
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
        dsCaption = drawnStringFactory.newDrawnString( "dsCaption", 240, 24, Alignment.LEFT, false, getFont(), isFontAntiAliased(), getFontColor() );
    	dsInformation = drawnStringFactory.newDrawnString( "dsInformation", 240, 60, Alignment.LEFT, false, getFont(), isFontAntiAliased(), getFontColor() );
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