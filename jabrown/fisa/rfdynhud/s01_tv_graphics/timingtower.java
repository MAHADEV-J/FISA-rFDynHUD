package jabrown.fisa.rfdynhud.s01_tv_graphics;

import java.awt.Color;
import java.awt.Font;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;

import net.ctdp.rfdynhud.gamedata.FinishStatus;
import net.ctdp.rfdynhud.gamedata.GamePhase;
import net.ctdp.rfdynhud.gamedata.LiveGameData;
import net.ctdp.rfdynhud.gamedata.ScoringInfo;
import net.ctdp.rfdynhud.gamedata.SessionLimit;
import net.ctdp.rfdynhud.gamedata.VehicleScoringInfo;
import net.ctdp.rfdynhud.gamedata.VehicleState;
import net.ctdp.rfdynhud.gamedata.YellowFlagState;
import net.ctdp.rfdynhud.input.InputAction;
import net.ctdp.rfdynhud.properties.ColorProperty;
import net.ctdp.rfdynhud.properties.DelayProperty;
import net.ctdp.rfdynhud.properties.ImagePropertyWithTexture;
import net.ctdp.rfdynhud.properties.IntProperty;
import net.ctdp.rfdynhud.properties.PropertiesContainer;
import net.ctdp.rfdynhud.properties.PropertyLoader;
import net.ctdp.rfdynhud.render.DrawnString;
import net.ctdp.rfdynhud.render.DrawnString.Alignment;
import net.ctdp.rfdynhud.render.DrawnStringFactory;
import net.ctdp.rfdynhud.render.TextureImage2D;
import net.ctdp.rfdynhud.util.PropertyWriter;
import net.ctdp.rfdynhud.util.SubTextureCollector;
import net.ctdp.rfdynhud.valuemanagers.Clock;
import net.ctdp.rfdynhud.values.FloatValue;
import net.ctdp.rfdynhud.values.IntValue;
import net.ctdp.rfdynhud.values.StringValue;
import net.ctdp.rfdynhud.widgets.base.widget.Widget;
import jabrown.fisa.rfdynhud.s01_tv_graphics._util.JABrownFISAWidgetSets01_tv_graphics;

/**
 * @author J.A. Brown
 * created in 2023
 * 
 */

public class timingtower extends Widget
{
    private TextureImage2D texGainedPlaces = null;
    private final ImagePropertyWithTexture imgPos = new ImagePropertyWithTexture( "imgPos", "prunn/f1_2011/tower/race_bg4.png" );
    private final ImagePropertyWithTexture imgPosFirst = new ImagePropertyWithTexture( "imgPos", "prunn/f1_2011/tower/race_bg_first4.png" );
    private final ImagePropertyWithTexture imgPositive = new ImagePropertyWithTexture( "imgTime", "prunn/f1_2011/tower/bg_gap.png" );
    private final ImagePropertyWithTexture imgNegative = new ImagePropertyWithTexture( "imgTime", "prunn/f1_2011/tower/bg_gap.png" );
    private final ImagePropertyWithTexture imgNeutral = new ImagePropertyWithTexture( "imgTime", "prunn/f1_2011/tower/bg_gap.png" );
    private final ColorProperty fontColor1 = new ColorProperty("fontColor1", JABrownFISAWidgetSets01_tv_graphics.FONT_COLOR1_NAME);
    private final ColorProperty fontColor2 = new ColorProperty( "fontColor2", JABrownFISAWidgetSets01_tv_graphics.FONT_COLOR2_NAME );
    private final float invisibleTime = 10.0f;
    private float appearTime = -1f;
    private Color statusColor = new Color(87, 89, 89, 255);
    private DrawnString dsStatus = null;
    private DrawnString[] dsPos = null;
    private DrawnString[] dsName = null;
    private DrawnString[] dsTime = null;
    private IntProperty fontyoffset = new IntProperty("Y Font Offset", 0);
    private IntProperty fontxposoffset = new IntProperty("X Position Font Offset", 0);
    private IntProperty fontxnameoffset = new IntProperty("X Name Font Offset", 0);
    private IntProperty fontxtimeoffset = new IntProperty("X Time Font Offset", 0);
    private final IntProperty numVeh = new IntProperty( "numberOfVehicles", 20 );
    private int[] startedPositions = null;
    private final IntValue currentLap = new IntValue();
    private short shownData = 0; //0-4-gaps 1-place gained 2-pit stops
    private final IntValue drawnCars = new IntValue();
    private final IntValue carsOnLeadLap = new IntValue();
    private boolean startedPositionsInitialized = false;
    private IntValue[] positions = null;
    private short[] gainedPlaces = null;
    private StringValue[] names = null;
    private StringValue[] gaps = null;
    private short[] lapsDown = null;
    private ColorProperty GainedFontColor;
    private float hideTime = -1f;
    private static final InputAction ToggleGapsOrStops = new InputAction ("ToggleGapsOrStops", false); //defines an input action
    private FloatValue currentSector = new FloatValue();
   
    @Override
    public void onRealtimeEntered( LiveGameData gameData, boolean isEditorMode )
    {
        super.onRealtimeEntered( gameData, isEditorMode );
        String cpid = "Y29weXJpZ2h0QFBydW5uMjAxMQ";
        if(!isEditorMode)
            log(cpid);
        drawnCars.reset();
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
    	
        int maxNumItems = numVeh.getValue();
        dsPos = new DrawnString[maxNumItems];
        dsName = new DrawnString[maxNumItems];
        dsTime = new DrawnString[maxNumItems];
                
        int fh = TextureImage2D.getStringHeight( "0%C", getFontProperty() );
        int rowHeight = fh + 2; //makes the row height dependent on the font height
        int fw2 = Math.round(width * 0.38f);
        //int fw3 = width - rowHeight - fw2;
        //int fw3b = fw3 * 3 / 4;

        imgPos.updateSize( rowHeight+fw2, rowHeight, isEditorMode );
        imgPosFirst.updateSize( rowHeight+fw2, rowHeight, isEditorMode );
        imgPositive.updateSize( width*90/100, rowHeight, isEditorMode );
        imgNegative.updateSize( width*90/100, rowHeight, isEditorMode );
        imgNeutral.updateSize( width*90/100, rowHeight, isEditorMode );
        texGainedPlaces = imgNeutral.getImage().getScaledTextureImage( width*40/100, rowHeight, texGainedPlaces, isEditorMode );
        
        Color whiteFontColor = fontColor2.getColor();
        
        int top = ( rowHeight - fh ) / 2;
        
        //dsStatus = drawnStringFactory.newDrawnString( "dsStatus", rowHeight+86 + fontxnameoffset.getValue(), top + fontyoffset.getValue(), Alignment.CENTER, false, getFont(), isFontAntiAliased(), whiteFontColor );
        dsStatus = drawnStringFactory.newDrawnString( "dsStatus", (imgPos.getTexture().getWidth() + texGainedPlaces.getWidth()) / 2, top + fontyoffset.getValue(), Alignment.CENTER, false, getFont(), isFontAntiAliased(), whiteFontColor );
        top += rowHeight;
        for(int i=0;i < maxNumItems;i++)
        { 
            dsPos[i] = drawnStringFactory.newDrawnString( "dsPos", rowHeight/2 + 5 + fontxposoffset.getValue(), top + fontyoffset.getValue(), Alignment.CENTER, false, getFont(), isFontAntiAliased(), whiteFontColor );
            dsName[i] = drawnStringFactory.newDrawnString( "dsName", rowHeight+16 + fontxnameoffset.getValue(), top + fontyoffset.getValue(), Alignment.LEFT, false, getFont(), isFontAntiAliased(), whiteFontColor );
            dsTime[i] = drawnStringFactory.newDrawnString( "dsTime", rowHeight + fw2 + width*21/100 + fontxtimeoffset.getValue(), top + fontyoffset.getValue(), Alignment.RIGHT, false, getFont(), isFontAntiAliased(), whiteFontColor );
            
            top += rowHeight;
        }
        
        
    }
    private void clearArrayValues(int maxNumCars)
    {
        positions = new IntValue[maxNumCars];
        gainedPlaces = new short[maxNumCars];
        gaps = new StringValue[maxNumCars];
        names = new StringValue[maxNumCars];
        lapsDown = new short[maxNumCars];
        
        for(int i=0;i<maxNumCars;i++)
        {
            positions[i] = new IntValue();
            positions[i].update(-1);
            gainedPlaces[i] = 0;
            gaps[i] = new StringValue();
            names[i] = new StringValue();
            lapsDown[i] = 0; //reset to 0 every time to prevent problems in case cars unlap themselves
        }
    }
    private void FillArrayValues(int numToShow, ScoringInfo scoringInfo, int data, boolean isEditorMode, LiveGameData gameData)
    {
    	
        if(isEditorMode)
        {
            data = 0;
            numToShow = numVeh.getValue();
        }
        int negrand[] = new int[2];
        negrand[0] = 1;
        negrand[1] = -1;
        
        for(int i=0;i<numToShow;i++)
        {
            
            if(positions[i].getValue() == -1)
            {
                
            
                VehicleScoringInfo vsi = scoringInfo.getVehicleScoringInfo( i );
                short place = vsi.getPlace( false );
                positions[i].update(place);
                names[i].update(vsi.getDriverNameTLC());
                gaps[i].update(String.valueOf(vsi.getLapsBehindLeader(false)));
                
                switch(data) //0-4-gaps 1-place gained 2-pit stops
                {
                    case 1: //places
                            int startedfrom=0;
                            for(int p=0; p < scoringInfo.getNumVehicles(); p++)
                            {
                                if( vsi.getDriverId() == startedPositions[p] )
                                {
                                    startedfrom = p+1;
                                    break;
                                } 
                            }
                            if(isEditorMode)
                            {
                                gainedPlaces[i] = (short)(( Math.random() * 2 )*negrand[(short)(Math.random() * 2)] );
                                gaps[i].update(String.valueOf( Math.abs( gainedPlaces[i]) ));
                                
                            }
                            else
                            {
                                gainedPlaces[i] = (short)( startedfrom - vsi.getPlace( false ) );
                                gaps[i].update(String.valueOf( Math.abs( startedfrom - vsi.getPlace( false )) ) + "    ");
                            }
                            break;
                    case 2: //number of pit stops
                    	if(vsi.getNumPitstopsMade() == 1)
                    	{
                    		gaps[i].update("1 STOP");
                    	}
                    	else
                    	{
                    		gaps[i].update(String.valueOf(vsi.getNumPitstopsMade()) + " STOPS");	
                    	}
                    	break;
                    default: //gaps
                            if(vsi.getFinishStatus() == FinishStatus.DNF)
                            {
                            	gaps[i].update("OUT");
                            }
                            if(vsi.getLapsBehindLeader(false) == 0 || isEditorMode)
                            {
                            	DecimalFormat decimalFormat = new DecimalFormat("0.000");
                            	decimalFormat.setRoundingMode(RoundingMode.DOWN);
                                gaps[i].update("+" + String.valueOf(decimalFormat.format(Math.abs( vsi.getTimeBehindLeader( false )))));
                            }
                            
                            //The following loop deals with cars being lapped. It's a workaround for the bug in rFDynHUD's code.
                            //It doesn't work 100% of the time (often it will show numbers of laps down that are too low),
                            //but at least it gets rid of the ugly "+0.000". It also gets rid of the weirdness of showing
                            //a car that is behind a lapped car as being fewer laps down than the car it is behind.
                            //--------------------------------------------------------------------------------------------------
                            if(vsi.getPlace(false) != 1) //necessary to avoid problems when checking the car in front
                            {
                            	//for cases where cars get lapped for the first time:
                            	if(lapsDown[i] == 0)
                            	{
                            		if(vsi.getTimeBehindLeader(false) == 0)
                            		{
                            			lapsDown[i] = 1;
                            		}
                            	}
                            	//for all subsequent cases:
                            	if(vsi.getLapsBehindLeader(false) != 0) //check if this is the first car to be lapped
                            	{
                            		lapsDown[i] = (short)vsi.getLapsBehindLeader(false);
                            	}
                            	if(lapsDown[i-1] != 0)
                            	{
                            		//add the laps down of the next car in front:
                            		lapsDown[i] = (short)(lapsDown[i-1] + vsi.getLapsBehindNextInFront(false));
                            	}
                            	if(lapsDown[i] != 0)
                            	{
                            		if(lapsDown[i] == 1)
                            		{
                            			gaps[i].update("+1 LAP");
                            		}
                            		else
                            		{
                            			gaps[i].update("+" + String.valueOf(lapsDown[i]) + " LAPS");
                            		}
                            	}
                            }
                            
                            //another loop to show when a car is in the pits
                            if (VehicleState.get(vsi, 0).isInPitlane())
                            {
                            	gaps[i].update("IN PIT");
                            }
                            if (VehicleState.get(vsi, 0).isPitting())
                            {
                            	gaps[i].update("PIT STOP");
                            }
                            gaps[0].update("Leader");
                            if(scoringInfo.getLeadersVehicleScoringInfo().getLapsCompleted() >= scoringInfo.getMaxLaps() || scoringInfo.getGamePhase() == GamePhase.SESSION_OVER)
                            {
                            	gaps[0].update("Winner");
                            }
                            break;
                }
            }
        }
    }
    private void initStartedFromPositions( ScoringInfo scoringInfo )
    {
        startedPositions = new int[scoringInfo.getNumVehicles()];
        
        for(int j=0;j < scoringInfo.getNumVehicles(); j++)
            startedPositions[j] = scoringInfo.getVehicleScoringInfo( j ).getDriverId();
        
        startedPositionsInitialized = true;
    }
    
    @Override
    public InputAction[] getInputActions()
    {
    	//Registers this action as an action that a key input can be bound to.
    	return (new InputAction[] {ToggleGapsOrStops});
    }
    
    @Override
    public Boolean onBoundInputStateChanged(InputAction action, boolean state, int modifierMask, long when, LiveGameData gameData, boolean isEditorMode)
    {
    	Boolean result = super.onBoundInputStateChanged(action, state, modifierMask, when, gameData, isEditorMode);
    	
    	if(action == ToggleGapsOrStops)
    	{
    			ToggleGapsOrStops();
    	}
    	
    	return result;
    }
    
    public void ToggleGapsOrStops()
    {
    	//Toggles between gaps and pitstops.
		if(shownData == 0)
    	{
    		shownData = 2;
    	}
    	else
    	{
    		shownData = 0;
    	}
    }
    
    @Override
    protected Boolean updateVisibility( LiveGameData gameData, boolean isEditorMode )
    {
        //super.updateVisibility( gameData, isEditorMode );
        Boolean result = true;
        Boolean bongo = true;
        
        ScoringInfo scoringInfo = gameData.getScoringInfo();
        
        clearArrayValues(Math.min(20, scoringInfo.getNumVehicles()));
        FillArrayValues(Math.min(20, scoringInfo.getNumVehicles()), scoringInfo, shownData, isEditorMode, gameData);
        
        if (scoringInfo.getYellowFlagState() == YellowFlagState.PENDING)
        {
        	bongo = false;
        }
        if (scoringInfo.getGamePhase() == GamePhase.FULL_COURSE_YELLOW && scoringInfo.getYellowFlagState() != YellowFlagState.LAST_LAP)
        {
        	//if (scoringInfo.getLeadersVehicleScoringInfo().getLapDistance() < gameData.getTrackInfo().getTrack().getSector1Length())
        	//{
            	bongo = false;	
        	//}
        	//else
        	//{
        		//bongo = true;
        	//}
        }
        if (scoringInfo.getYellowFlagState() == YellowFlagState.LAST_LAP)
        {
        	//leaving this in here in case we want to go back to it later
        	//if (scoringInfo.getLeadersVehicleScoringInfo().getLapDistance() < gameData.getTrackInfo().getTrack().getSector1Length())
        	//{
            	bongo = false;	
        	//}
        	//else
        	//{
        		//bongo = true;
        	//}
        }
        if (scoringInfo.getYellowFlagState() == YellowFlagState.RESUME)
        {
        	bongo = false;
        	//bongo = true;
        }
        if (scoringInfo.getOnPathWetness() >= 0.2f) //when it's raining on ovals
        {
        	bongo = false;
        }
        
        if (bongo == false)
        {
        	//appearTime = scoringInfo.getSessionTime() + invisibleTime;
        	result = false;
        	forceCompleteRedraw(true);
        }
        
        return result;
        
    }
    @Override
    protected void drawBackground( LiveGameData gameData, boolean isEditorMode, TextureImage2D texture, int offsetX, int offsetY, int width, int height, boolean isRoot )
    {
        super.drawBackground( gameData, isEditorMode, texture, offsetX, offsetY, width, height, isRoot );
        
        ScoringInfo scoringInfo = gameData.getScoringInfo();
        int maxNumItems = numVeh.getValue();
        int fh = TextureImage2D.getStringHeight( "0%C", getFontProperty() );
        int rowHeight = fh + 2; //makes the row height dependent on the font height
        int fw2 = Math.round(width * 0.38f);
        int drawncars = Math.min( scoringInfo.getNumVehicles(), maxNumItems );
        short posOffset;
        if(isEditorMode)
            shownData = 0;
        
//        if (scoringInfo.getGamePhase() == GamePhase.FULL_COURSE_YELLOW || scoringInfo.getSectorYellowFlag(1) || scoringInfo.getSectorYellowFlag(2) || scoringInfo.getSectorYellowFlag(3))
//		{
//			statusColor = new Color(64, 240, 240, 255);
//		}
//		else if (scoringInfo.getLeadersVehicleScoringInfo().getCurrentLap() == scoringInfo.getMaxLaps())
//		{
//			statusColor = new Color(255, 255, 255, 255);
//		}
//		else if (scoringInfo.getGamePhase() == GamePhase.GREEN_FLAG)
//		{
//			statusColor = new Color(0, 240, 40);
//		}
        //texture.clear(statusColor, offsetX, offsetY, rowHeight+fw2, rowHeight, false, null);
        //texture.clear(statusColor, offsetX + imgPos.getTexture().getWidth() - width*8/100, offsetY, imgPos.getTexture().getWidth(), rowHeight, false, null);
        texture.clear(statusColor, offsetX, offsetY, imgPos.getTexture().getWidth() + texGainedPlaces.getWidth() - 4, rowHeight, false, null);
        
        for(int i=0;i < drawncars;i++)
        {
            if(positions[i].getValue() != -1 || isEditorMode)
            {
                if(i==0)
                    texture.clear( imgPosFirst.getTexture(), offsetX, offsetY+rowHeight*(i+1), false, null );
                else
                    texture.clear( imgPos.getTexture(), offsetX, offsetY+rowHeight*(i+1), false, null );
            
            
                //if( shownData == 0)
                //{
                     
                    //if(carsOnLeadLap.getValue() > numVeh.getValue() && i != 0)
                        //posOffset = (short)( carsOnLeadLap.getValue() - numVeh.getValue() );
                    //else
                        //posOffset = 0;
                    
                        
                    
                    //if(gainedPlaces[i + posOffset] > 0)
                        //texGainedPlaces = imgPositive.getImage().getScaledTextureImage( width*38/100, rowHeight, texGainedPlaces, isEditorMode );
                    //else 
                        //if(gainedPlaces[i + posOffset] < 0)
                            //texGainedPlaces = imgNegative.getImage().getScaledTextureImage( width*38/100, rowHeight, texGainedPlaces, isEditorMode );
                        //else
                            texGainedPlaces = imgNeutral.getImage().getScaledTextureImage( width*38/100, rowHeight, texGainedPlaces, isEditorMode );
                    
                    //texture.drawImage( texGainedPlaces, offsetX + imgPos.getTexture().getWidth() - width*8/100, offsetY+rowHeight*(i+1), true, null );

                            texture.drawImage( texGainedPlaces, offsetX + imgPos.getTexture().getWidth(), offsetY+rowHeight*(i+1), true, null );        
                                       
                //}
            }
        }
        
    }
    
    
    @Override
    protected void drawWidget( Clock clock, boolean needsCompleteRedraw, LiveGameData gameData, boolean isEditorMode, TextureImage2D texture, int offsetX, int offsetY, int width, int height )
    {
        ScoringInfo scoringInfo = gameData.getScoringInfo();
        
        int drawncars = Math.min( scoringInfo.getNumVehicles(), numVeh.getValue() );
        short posOffset = 0; // the top position that should be drawn on screen (0-indexed)
        
        String status = "FORMATION LAP"; //this doesn't work because it's immediately overridden by code further down, but whatever
        if (scoringInfo.getGamePhase() == GamePhase.FORMATION_LAP || scoringInfo.getGamePhase() == GamePhase.BEFORE_SESSION_HAS_BEGUN || scoringInfo.getLeadersVehicleScoringInfo().getCurrentLap() < 1)
        {
        	status = "FORMATION LAP";
        }
        else if (scoringInfo.getLeadersVehicleScoringInfo().getLapsCompleted() >= scoringInfo.getMaxLaps() || scoringInfo.getGamePhase() == GamePhase.SESSION_OVER)
        {
        	status = "FINISH";
        }
        else
        {
        	status = "LAP " + String.valueOf(scoringInfo.getLeadersVehicleScoringInfo().getCurrentLap()) + " / " + String.valueOf(scoringInfo.getMaxLaps());
        }
        
        //Loop to determine session status row font and background colours depending on flag
        //----------------------------------------------------------------------------------
        Color statusFontColor = fontColor2.getColor();
        //if final lap: black on white
        if (scoringInfo.getLeadersVehicleScoringInfo().getCurrentLap() == scoringInfo.getMaxLaps()) 
        {
        	statusColor = Color.WHITE;
        	statusFontColor = Color.BLACK;
        	forceCompleteRedraw(true);
        }
        //if caution or local yellow: black on yellow
        if (scoringInfo.getGamePhase() == GamePhase.FULL_COURSE_YELLOW || scoringInfo.getSectorYellowFlag(1) || scoringInfo.getSectorYellowFlag(2) || scoringInfo.getSectorYellowFlag(3))
		{
        	statusColor = Color.YELLOW;
        	statusFontColor = Color.BLACK;
        	forceCompleteRedraw(true); //apparently this is necessary
		}
        //TODO: add some code for when it goes to green flag after yellow (think this through later) colour: 33, 119, 28
        //if finished or normal: white on grey
        if (scoringInfo.getLeadersVehicleScoringInfo().getFinishStatus() == FinishStatus.FINISHED || (scoringInfo.getGamePhase() == GamePhase.GREEN_FLAG && scoringInfo.getLeadersVehicleScoringInfo().getCurrentLap() != scoringInfo.getMaxLaps()))
        {
        	statusColor = new Color(87, 89, 89, 255);
        	statusFontColor = fontColor2.getColor();
        	forceCompleteRedraw(true);
        }
        //if race stopped: white on red
        if (scoringInfo.getGamePhase() == GamePhase.SESSION_STOPPED)
        {
        	statusColor = Color.RED;
        	statusFontColor = fontColor2.getColor();
        	forceCompleteRedraw(true);
        }
        
        //Loop to show laps or time remaining depending on which it is
        //------------------------------------------------------------
        //laps remaining
        if (scoringInfo.getLeadersVehicleScoringInfo().getSessionLimit() == SessionLimit.LAPS)
        {
        	dsStatus.draw(offsetX, offsetY, status, statusFontColor, texture);
        }
        //time remaining or laps remaining running out of time
        if (scoringInfo.getLeadersVehicleScoringInfo().getSessionLimit() == SessionLimit.TIME || scoringInfo.getLeadersVehicleScoringInfo().getSessionLimit() == SessionLimit.LAPS && scoringInfo.getEstimatedMaxLaps(scoringInfo.getLeadersVehicleScoringInfo()) < scoringInfo.getMaxLaps())
        {
        	//show time remaining
        }
        
        for(int i=0;i < drawncars;i++)
        { 
        	if ( needsCompleteRedraw || positions[i].hasChanged() || gaps[i].hasChanged() )
            {
        		//if(carsOnLeadLap.getValue() > numVeh.getValue() && i != 0)
        			//posOffset = (short)( carsOnLeadLap.getValue() - numVeh.getValue() );
        		//else
        			//posOffset = 0;
        		
        		if(positions[i + posOffset].getValue() != -1)
        			dsPos[i].draw( offsetX, offsetY, String.valueOf(positions[i + posOffset]), texture );
        		else
        			dsPos[i].draw( offsetX, offsetY, "", texture );
            
        		if(gainedPlaces[i + posOffset] >= 0)
        			GainedFontColor = fontColor2;
        		else
        			GainedFontColor = fontColor1;
            
        		dsName[i].draw( offsetX, offsetY, names[i + posOffset].getValue(), texture );
        		                    
        		dsTime[i].draw( offsetX, offsetY, gaps[i + posOffset].getValue(), GainedFontColor.getColor(), texture );
            }
        }
    }
    
    
    @Override
    public void saveProperties( PropertyWriter writer ) throws IOException
    {
        super.saveProperties( writer );
        
        writer.writeProperty( fontColor2, "" );
        writer.writeProperty( fontColor1, "" );
        writer.writeProperty( numVeh, "" );
        writer.writeProperty( fontyoffset, "" );
        writer.writeProperty( fontxposoffset, "" );
        writer.writeProperty( fontxnameoffset, "" );
        writer.writeProperty( fontxtimeoffset, "" );
    }
    
    @Override
    public void loadProperty( PropertyLoader loader )
    {
        super.loadProperty( loader );
        
        if ( loader.loadProperty( fontColor2 ) );
        else if ( loader.loadProperty( fontColor1 ) );
        else if ( loader.loadProperty( numVeh ) );
        else if ( loader.loadProperty( fontyoffset ) );
        else if ( loader.loadProperty( fontxposoffset ) );
        else if ( loader.loadProperty( fontxnameoffset ) );
        else if ( loader.loadProperty( fontxtimeoffset ) );
    }
    
    @Override
    protected void addFontPropertiesToContainer( PropertiesContainer propsCont, boolean forceAll )
    {
        propsCont.addGroup( "Colors and Fonts" );
        
        super.addFontPropertiesToContainer( propsCont, forceAll );
        propsCont.addProperty( fontColor1 );
        
        propsCont.addProperty( fontColor2 );
    }
    
    @Override
    public void getProperties( PropertiesContainer propsCont, boolean forceAll )
    {
        super.getProperties( propsCont, forceAll );
        
        propsCont.addGroup( "Specific" );
        propsCont.addProperty( numVeh );
        propsCont.addGroup( "Font Displacement" );
        propsCont.addProperty( fontyoffset );
        propsCont.addProperty( fontxposoffset );
        propsCont.addProperty( fontxnameoffset );
        propsCont.addProperty( fontxtimeoffset );
    }
    
    @Override
    protected boolean canHaveBorder()
    {
        return ( false );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void prepareForMenuItem()
    {
        super.prepareForMenuItem();
        
        getFontProperty().setFont( "Dialog", Font.PLAIN, 6, false, true );
    }
    
    public timingtower()
    {
        super( JABrownFISAWidgetSets01_tv_graphics.INSTANCE, JABrownFISAWidgetSets01_tv_graphics.WIDGET_PACKAGE_S01_TV_GRAPHICS, 20.0f, 32.5f );
        
        getBackgroundProperty().setColorValue( "#00000000" );
        getFontProperty().setFont( JABrownFISAWidgetSets01_tv_graphics.FISA_S01_TV_GRAPHICS_FONT_NAME );
        getFontColorProperty().setColor( JABrownFISAWidgetSets01_tv_graphics.FONT_COLOR1_NAME );
    }
}