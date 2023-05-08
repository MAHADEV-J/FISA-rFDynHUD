package jabrown.fisa.rfdynhud.s01_tv_graphics;

import java.awt.Font;

import jabrown.fisa.rfdynhud.s01_tv_graphics._util.JABrownFISAWidgetSets01_tv_graphics;
import net.ctdp.rfdynhud.gamedata.LiveGameData;
import net.ctdp.rfdynhud.render.DrawnString;
import net.ctdp.rfdynhud.render.DrawnString.Alignment;
import net.ctdp.rfdynhud.render.DrawnStringFactory;
import net.ctdp.rfdynhud.render.TextureImage2D;
import net.ctdp.rfdynhud.util.SubTextureCollector;
import net.ctdp.rfdynhud.valuemanagers.Clock;
import net.ctdp.rfdynhud.widgets.base.widget.Widget;
import net.ctdp.rfdynhud.widgets.lessons._util.LessonsWidgetSet;

public class helloWorld extends Widget
{

    /*
     * DrawnString objects are used to efficiently and easily draw texts on your Widget.
     * They take care about clearing the exact area from the previous value, if the text
     * needs to be redrawn (when it has changed).
     * 
     * The instance is usually created in the initialize() method implementation (see below).
     */
    private DrawnString ds = null;
    
    public helloWorld()
    {
        /*
         * In the constructor we pass in the default size of the Widget.
         * We don't need to define a location, since this is done by the editor.
         * In the below notation, we use percents, which is important
         * to create proper sizes for all screen resolutions.
         */
        super( JABrownFISAWidgetSets01_tv_graphics.INSTANCE, JABrownFISAWidgetSets01_tv_graphics.WIDGET_PACKAGE_S01_TV_GRAPHICS, 10.0f, 5.0f );
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
        ds = drawnStringFactory.newDrawnString( "ds", 0, 0, Alignment.LEFT, false, getFont(), isFontAntiAliased(), getFontColor() );
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
            ds.draw( offsetX, offsetY, "Hello World", texture );
        }
    }
	
}