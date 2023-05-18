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
import net.ctdp.rfdynhud.widgets.lessons._util.LessonsWidgetSet;

/**
 * This Widget is something like the minimum, that you need to code to implement your first Widget.
 * 
 * As you can see, it is not much code, though it also doesn't do much.
 * 
 * This Widget will simply display the standard background and border with the text "Hello World".
 * 
 * @author Marvin Froehlich (CTDP)
 */
public class racecontrol extends Widget
{
    /*
     * DrawnString objects are used to efficiently and easily draw texts on your Widget.
     * They take care about clearing the exact area from the previous value, if the text
     * needs to be redrawn (when it has changed).
     * 
     * The instance is usually created in the initialize() method implementation (see below).
     */
    private DrawnString ds = null;
    
    public racecontrol()
    {
        /*
         * In the constructor we pass in the default size of the Widget.
         * We don't need to define a location, since this is done by the editor.
         * In the below notation, we use percents, which is important
         * to create proper sizes for all screen resolutions.
         */
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
        /*
         * This method is more or less only invoked once before the Widget is first rendered.
         * 
         * Ok, here we instantiate our DrawnString, that we will use to draw
         * the "Hello World" text.
         * 
         * You cannot instantiate a DrawnString with 'new', but have to use the DrawnStringFactory passed in here.
         * The reason is, that instances need to be cleaned up, when they are discarded. And this is done automatically
         * by the factory.
         * 
         * Instances are identified by their name (first parameter), which is only used by the factory the clean up a replaced instance.
         * So you can use any abbreviated string here. It just need to be the same, if you create a new instance, that replaces another one.
         * 
         * The string also need a position, which is to be understood relatively to the Widget (not absolute on the screen). We use top-left (0,0) here.
         * 
         * The alignment parameter tells the system to position the left end of the text at the given x-coordinate.
         * 
         * And we want to top edge of the text to be positioned at our y location (0), so we pass in a 'false' to tell it not to use the text's base line.
         * 
         * The other three parameters should be self explanatory.
         * Their values come from some standard Poroperties, that any Widget has.
         */
        ds = drawnStringFactory.newDrawnString( "ds", 240, 60, Alignment.LEFT, false, getFont(), isFontAntiAliased(), getFontColor() );
    }
    
    @Override
    protected void drawBackground( LiveGameData gameData, boolean isEditorMode, TextureImage2D texture, int offsetX, int offsetY, int width, int height, boolean isRoot )
    {
    	super.drawBackground(gameData, isEditorMode, texture, offsetX, offsetY, width, height, isRoot);
    	
    	texture.clear(offsetX, offsetY, width, height, true, null);
    	Texture2DCanvas textureCanvas = texture.getTextureCanvas();
    	textureCanvas.setColor(new Color(0, 0, 0, 80));
//    	textureCanvas.setAntialiazingEnabled(true);
//    	final int triangleWidth = Math.round(height / 2f);
//    	int[] xPoints = new int[] {offsetX, offsetX + triangleWidth, offsetX + triangleWidth, offsetX};
//    	int[] yPoints = new int[] {offsetY + height, offsetY + height, offsetY, offsetY + height};
//    	textureCanvas.fillPolygon(xPoints, yPoints, xPoints.length);
//    	textureCanvas.setAntialiazingEnabled(false);
    	Rect2i rectangle = new Rect2i(offsetX, offsetY, width, height);
    	textureCanvas.fillRect(rectangle);
//    	textureCanvas.setAntialiazingEnabled(true);
//    	xPoints = new int[] {offsetX + width - triangleWidth, offsetX + width, offsetX + width - triangleWidth, offsetX + width - triangleWidth};
//    	yPoints = new int[] {offsetY + height, offsetY, offsetY, offsetY + height};
//    	textureCanvas.fillPolygon(xPoints, yPoints, xPoints.length);
    }
    
    @Override
    protected void drawWidget( Clock clock, boolean needsCompleteRedraw, LiveGameData gameData, boolean isEditorMode, TextureImage2D texture, int offsetX, int offsetY, int width, int height )
    {
        /*
         * Here we draw our Widget.
         * 
         * Since this method is called every time, the whole HUD is rerendered,
         * we need to do something, so that our text is only drawn when it has actually changed.
         * 
         * Since we use a static text, we only need to ask, if the whole Widget is to be redrawn,
         * which also needs our text to be drawn.
         * 
         * The DrawnString needs to know the (inner) Widget location to compute the actual text location
         * from the widget relative location, that we defined up in the initialize() method
         * and a target texture to draw on.
         */
        if ( needsCompleteRedraw )
        {
        	//texture.clear(new Color(255, 0, 0, 80), false, null);
            ds.draw( offsetX, offsetY, "SAFETY CAR", texture );
        }
    }
}