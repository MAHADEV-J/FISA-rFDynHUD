package net.ctdp.rfdynhud.gamedata;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Random;
import net.ctdp.rfdynhud.editor.EditorPresets;
import net.ctdp.rfdynhud.util.ThreeLetterCodeManager;

public class VehicleScoringInfo {
  private final ScoringInfo scoringInfo;
  
  private final ProfileInfo profileInfo;
  
  private final LiveGameData gameData;
  
  _VehicleScoringInfoCapsule data = null;
  
  private String originalName = null;
  
  private String name = null;
  
  private String nameUC = null;
  
  private String nameShort = null;
  
  private String nameShortUC = null;
  
  private String nameTLC = null;
  
  private String nameTLCUC = null;
  
  private int nameId = 0;
  
  private Integer nameID = null;
  
  private short place = -1;
  
  private int lastTLCMgrUpdateId = -1;
  
  private String vehClass = null;
  
  private static int nextClassId = 1;
  
  private int classId = 0;
  
  private Integer classID = null;
  
  private String vehicleName = null;
  
  private VehicleInfo vehicleInfo = null;
  
  short placeByClass = -1;
  
  int numVehiclesInClass = -1;
  
  float timeBehindNextByClass = 0.0F;
  
  int lapsBehindNextByClass = -1;
  
  float timeBehindLeaderByClass = 0.0F;
  
  int lapsBehindLeaderByClass = -1;
  
  VehicleScoringInfo classLeaderVSI = null;
  
  VehicleScoringInfo classNextInFrontVSI = null;
  
  VehicleScoringInfo classNextBehindVSI = null;
  
  private float lapDistance = -1.0F;
  
  private int oldLap = -1;
  
  private int lap = -1;
  
  private int stintStartLap = -1;
  
  private float stintLength = 0.0F;
  
  private int pitState = -1;
  
  final ArrayList<Laptime> laptimes = new ArrayList<Laptime>();
  
  Laptime cachedFastestNormalLaptime = null;
  
  Laptime cachedFastestHotLaptime = null;
  
  private Laptime fastestLaptime = null;
  
  private Laptime secondFastestLaptime = null;
  
  Laptime oldAverageLaptime = null;
  
  Laptime averageLaptime = null;
  
  private Laptime editor_lastLaptime = null;
  
  private Laptime editor_currLaptime = null;
  
  private Laptime editor_fastestLaptime = null;
  
  float topspeed = 0.0F;
  
  float engineRPM = -1.0F;
  
  float engineMaxRPM = -1.0F;
  
  int engineBoostMapping = -1;
  
  int gear = -1000;
  
  private static final HashMap<String, Integer> classToIDMap = new HashMap<String, Integer>();
  
  public final ScoringInfo getScoringInfo() {
    return this.scoringInfo;
  }
  
  public final boolean isValid() {
    return (this.data != null);
  }
  
  private void updateClassID() {
    String vehClass = getVehicleClass();
    Integer id = classToIDMap.get(vehClass);
    if (id == null) {
      id = Integer.valueOf(nextClassId++);
      classToIDMap.put(vehClass, id);
    } 
    this.classId = id.intValue();
    this.classID = id;
  }
  
  void applyEditorPresets(EditorPresets editorPresets) {
    if (editorPresets == null)
      return; 
    if (isPlayer()) {
      this.name = editorPresets.getDriverName();
      this.originalName = this.name;
      this.data.setDriverName(this.name);
      this.nameUC = null;
      this.nameShort = null;
      this.nameShortUC = null;
      this.nameTLC = null;
      this.nameTLCUC = null;
      this.nameID = this.data.refreshID(true);
      this.nameId = this.nameID.intValue();
    } 
    int lc = getLapsCompleted();
    if (this.laptimes.size() < lc || this.laptimes.get(lc - 1) == null || ((Laptime)this.laptimes.get(lc - 1)).getSector1() != editorPresets.getLastSector1Time() || ((Laptime)this.laptimes.get(lc - 1)).getSector2() != editorPresets.getLastSector2Time(false) || ((Laptime)this.laptimes.get(lc - 1)).getSector3() != editorPresets.getLastSector3Time()) {
      this.fastestLaptime = null;
      this.secondFastestLaptime = null;
      Random rnd = new Random(System.nanoTime());
      float ls1 = isPlayer() ? editorPresets.getLastSector1Time() : this.data.getLastSector1();
      float ls2 = isPlayer() ? editorPresets.getLastSector2Time(false) : (this.data.getLastSector2() - this.data.getLastSector1());
      float ls3 = isPlayer() ? editorPresets.getLastSector3Time() : (this.data.getLastLapTime() - this.data.getLastSector2());
      for (int l = 1; l <= lc; l++) {
        Laptime lt;
        float s1 = ls1 + ((l == lc) ? 0.0F : (-0.33F * rnd.nextFloat() * 0.66F));
        float s2 = ls2 + ((l == lc) ? 0.0F : (-0.33F * rnd.nextFloat() * 0.66F));
        float s3 = ls3 + ((l == lc) ? 0.0F : (-0.33F * rnd.nextFloat() * 0.66F));
        if (l > this.laptimes.size() || this.laptimes.get(l - 1) == null) {
          lt = new Laptime(getDriverId(), l, s1, s2, s3, false, (l == 1), true);
          if (l > this.laptimes.size()) {
            this.laptimes.add(lt);
          } else {
            this.laptimes.set(l - 1, lt);
          } 
        } else {
          lt = this.laptimes.get(l - 1);
          lt.sector1 = s1;
          lt.sector2 = s2;
          lt.sector3 = s3;
          lt.updateLaptimeFromSectors();
        } 
        if (l == 1 || lt.getLapTime() < this.fastestLaptime.getLapTime()) {
          this.secondFastestLaptime = this.fastestLaptime;
          this.fastestLaptime = lt;
        } 
      } 
      this.editor_fastestLaptime = this.fastestLaptime;
      LaptimesRecorder.calcAvgLaptime(this);
      this.oldAverageLaptime = this.averageLaptime;
    } 
    float cs1 = isPlayer() ? editorPresets.getCurrentSector1Time() : this.data.getCurrentSector1();
    float cs2 = isPlayer() ? editorPresets.getCurrentSector2Time(false) : (this.data.getCurrentSector2() - this.data.getCurrentSector1());
    if (this.laptimes.size() < lc + 1) {
      Laptime lt = new Laptime(getDriverId(), lc + 1, cs1, cs2, -1.0F, false, false, false);
      lt.isInLap = null;
      this.laptimes.add(lt);
    } else {
      Laptime lt = this.laptimes.get(lc);
      lt.sector1 = cs1;
      lt.sector2 = cs2;
      lt.updateLaptimeFromSectors();
    } 
    this.editor_lastLaptime = this.laptimes.get(lc - 1);
    this.editor_currLaptime = this.laptimes.get(lc);
    this.topspeed = editorPresets.getTopSpeed(getPlace(false) - 1);
  }
  
  void onDataUpdated() {
    this.place = -1;
    this.lapDistance = -1.0F;
    this.vehClass = null;
    this.classId = 0;
    this.classID = null;
    this.vehicleName = null;
    this.vehicleInfo = null;
    this.oldLap = this.lap;
    this.lap = getLapsCompleted() + 1;
    if (isPlayer() && this.gameData.getTelemetryData().isUpdatedInTimeScope()) {
      this.engineRPM = this.gameData.getTelemetryData().getEngineRPM();
      this.engineMaxRPM = this.gameData.getTelemetryData().getEngineMaxRPM();
      this.engineBoostMapping = this.gameData.getTelemetryData().getEngineBoostMapping();
      this.gear = this.gameData.getTelemetryData().getCurrentGear();
    } else {
      this.engineRPM = -1.0F;
      this.engineMaxRPM = -1.0F;
      this.engineBoostMapping = -1;
      this.gear = -1000;
    } 
  }
  
  public void readFromStream(InputStream in) throws IOException {
    this.data.loadFromStream(in);
    onDataUpdated();
  }
  
  public void writeToStream(OutputStream out) throws IOException {
    this.data.writeToStream(out);
  }
  
  private void updateStintLength() {
    int currentLap = getLapsCompleted() + 1;
    boolean isInPits = isInPits();
    boolean isStanding = (Math.abs(getScalarVelocityMPS()) < 0.1F);
    float trackPos = getNormalizedLapDistance();
    if (this.stintStartLap < 0 || (isInPits && this.stintStartLap != currentLap && isStanding) || this.stintStartLap > currentLap)
      this.stintStartLap = currentLap; 
    int oldPitState = this.pitState;
    if (oldPitState == -1) {
      if (isInPits && isStanding) {
        this.pitState = 2;
      } else if (isInPits) {
        this.pitState = 1;
      } else {
        this.pitState = 0;
      } 
    } else {
      if (oldPitState == 2 && !isInPits)
        this.stintStartLap = currentLap; 
      if (isInPits) {
        if (isStanding && oldPitState != 2) {
          this.pitState = 2;
        } else if (oldPitState == 0) {
          this.pitState = 1;
        } 
      } else if (oldPitState != 0) {
        this.pitState = 0;
      } 
    } 
    if (!isPlayer() || this.gameData.isInRealtimeMode()) {
      this.stintLength = (currentLap - this.stintStartLap) + trackPos;
    } else {
      this.stintLength = 0.0F;
    } 
  }
  
  void updateSomeData() {
    updateStintLength();
  }
  
  void resetExtrapolatedValues() {
    this.lapDistance = -1.0F;
  }
  
  void resetDerivateData() {
    this.stintStartLap = -1;
    this.oldLap = -1;
    this.laptimes.clear();
    if (this.laptimes.size() > 0) {
      this.fastestLaptime = null;
      this.secondFastestLaptime = null;
    } 
    this.oldAverageLaptime = null;
    this.averageLaptime = null;
  }
  
  void onSessionStarted() {
    resetDerivateData();
    this.fastestLaptime = null;
    this.secondFastestLaptime = null;
  }
  
  void onSessionEnded() {
    resetDerivateData();
    this.fastestLaptime = null;
    this.secondFastestLaptime = null;
  }
  
  void setDriverName(String originalName, String name, Integer id) {
    this.originalName = originalName;
    this.name = name;
    this.nameUC = null;
    this.nameShort = null;
    this.nameShortUC = null;
    this.nameTLC = null;
    this.nameTLCUC = null;
    this.nameID = id;
    this.nameId = id.intValue();
  }
  
  public final String getDriverName(boolean upperCase) {
    if (upperCase) {
      if (this.nameUC == null)
        this.nameUC = this.name.toUpperCase(); 
      return this.nameUC;
    } 
    return this.name;
  }
  
  public final String getDriverName() {
    return getDriverName(false);
  }
  
  public final String getDriverNameShort(boolean upperCase) {
    if (this.nameShort == null || this.lastTLCMgrUpdateId < ThreeLetterCodeManager.getUpdateId()) {
      String driverName = this.originalName;
      this.nameShort = ThreeLetterCodeManager.getShortForm(driverName, getDriverID(), this.scoringInfo.getThreeLetterCodeGenerator());
      this.lastTLCMgrUpdateId = ThreeLetterCodeManager.getUpdateId();
    } 
    if (upperCase) {
      if (this.nameShortUC == null)
        this.nameShortUC = this.nameShort.toUpperCase(); 
      return this.nameShortUC;
    } 
    return this.nameShort;
  }
  
  public final String getDriverNameShort() {
    return getDriverNameShort(false);
  }
  
  public final String getDriverNameTLC(boolean upperCase) {
    if (this.nameTLC == null || this.lastTLCMgrUpdateId < ThreeLetterCodeManager.getUpdateId()) {
      String driverName = this.originalName;
      this.nameTLC = ThreeLetterCodeManager.getThreeLetterCode(driverName, getDriverID(), this.scoringInfo.getThreeLetterCodeGenerator());
      this.lastTLCMgrUpdateId = ThreeLetterCodeManager.getUpdateId();
    } 
    if (upperCase) {
      if (this.nameTLCUC == null)
        this.nameTLCUC = this.nameTLC.toUpperCase(); 
      return this.nameTLCUC;
    } 
    return this.nameTLC;
  }
  
  public final String getDriverNameTLC() {
    return getDriverNameTLC(true);
  }
  
  public final int getDriverId() {
    return this.nameId;
  }
  
  public final Integer getDriverID() {
    return this.nameID;
  }
  
  public int hashCode() {
    return getDriverId();
  }
  
  public boolean equals(Object o) {
    if (!(o instanceof VehicleScoringInfo))
      return false; 
    return (getDriverId() == ((VehicleScoringInfo)o).getDriverId());
  }
  
  public final String getVehicleName() {
    if (this.vehicleName == null)
      this.vehicleName = this.data.getVehicleName(); 
    return this.vehicleName;
  }
  
  public final VehicleInfo getVehicleInfo() {
    if (this.vehicleInfo == null)
      this.vehicleInfo = this.gameData.getModInfo().getVehicleInfoForDriver(this); 
    return this.vehicleInfo;
  }
  
  public final short getLapsCompleted() {
    return this.data.getLapsCompleted();
  }
  
  public final short getCurrentLap() {
    if (isInPits() && getStintLength() < 0.5F)
      return getLapsCompleted(); 
    return (short)(getLapsCompleted() + 1);
  }
  
  public final boolean isLapJustStarted() {
    return (this.lap != this.oldLap);
  }
  
  public final SessionLimit getSessionLimit(SessionLimit preference) {
    int maxLaps = this.scoringInfo.getMaxLaps();
    if (maxLaps > 1073741823)
      maxLaps = 0; 
    float endTime = this.scoringInfo.getEndTime();
    if (maxLaps > 0 && maxLaps < 10000) {
      if (endTime > 0.0F && endTime < 999999.0F) {
        Laptime avgLaptime = getAverageLaptime();
        if (avgLaptime == null) {
          if (preference == null)
            return SessionLimit.LAPS; 
          return preference;
        } 
        int timeLaps = (int)(endTime / avgLaptime.getLapTime());
        if (timeLaps < maxLaps)
          return SessionLimit.TIME; 
      } 
      return SessionLimit.LAPS;
    } 
    if (endTime > 0.0F && endTime < 999999.0F)
      return SessionLimit.TIME; 
    return null;
  }
  
  public final SessionLimit getSessionLimit() {
    return getSessionLimit(null);
  }
  
  public final int getEstimatedMaxLaps() {
    if (this.scoringInfo.getSessionType().isRace() && this.scoringInfo.getLeadersVehicleScoringInfo().getFinishStatus().isFinished())
      return this.scoringInfo.getLeadersVehicleScoringInfo().getLapsCompleted(); 
    short lapsCompleted = getLapsCompleted();
    int maxLaps = this.scoringInfo.getMaxLaps();
    if (maxLaps > 1073741823)
      maxLaps = 0; 
    float endTime = this.scoringInfo.getEndTime();
    if (lapsCompleted == 0 || endTime < 0.0F || endTime > 999999.0F) {
      if (maxLaps > 0)
        return maxLaps; 
      return -1;
    } 
    Laptime avgLaptime = getAverageLaptime();
    if (avgLaptime == null) {
      if (maxLaps > 0)
        return maxLaps; 
      return -1;
    } 
    float restTime = endTime - getLapStartTime();
    int timeLaps = lapsCompleted + (int)(restTime / avgLaptime.getLapTime()) + 1;
    if (maxLaps <= 0 || timeLaps < maxLaps)
      return timeLaps; 
    return maxLaps;
  }
  
  public final float getLapsRemaining(int maxLaps) {
    if (maxLaps < 0)
      return -1.0F; 
    int lr = maxLaps - getLapsCompleted();
    if (getFinishStatus().isFinished())
      return lr; 
    return lr - getNormalizedLapDistance();
  }
  
  public final byte getSector() {
    return this.data.getSector();
  }
  
  public final FinishStatus getFinishStatus() {
    return this.data.getFinishStatus();
  }
  
  public final float getLapDistance() {
    if (this.lapDistance < 0.0F) {
      this.lapDistance = this.data.getLapDistance() + getScalarVelocityMPS() * this.scoringInfo.getExtrapolationTime();
      while (this.lapDistance < 0.0F)
        this.lapDistance += this.scoringInfo.getTrackLength(); 
      this.lapDistance %= this.scoringInfo.getTrackLength();
    } 
    return this.lapDistance;
  }
  
  public final float getNormalizedLapDistance() {
    return getLapDistance() / this.scoringInfo.getTrackLength();
  }
  
  public final int getStintStartLap() {
    return this.stintStartLap;
  }
  
  public final float getStintLength() {
    return this.stintLength;
  }
  
  public final Laptime getLaptime(int lap) {
    if (lap < 1 || this.laptimes == null || lap > this.laptimes.size())
      return null; 
    return this.laptimes.get(lap - 1);
  }
  
  void setFastestLaptime(Laptime laptime) {
    if (laptime == this.fastestLaptime)
      return; 
    if (laptime == null || !laptime.isFinished() || laptime.getLapTime() < 0.0F) {
      this.secondFastestLaptime = null;
    } else {
      this.secondFastestLaptime = this.fastestLaptime;
    } 
    this.fastestLaptime = laptime;
  }
  
  final Laptime _getFastestLaptime() {
    return this.fastestLaptime;
  }
  
  public final Laptime getFastestLaptime() {
    if (isPlayer() && DataCache.checkSessionType(this.scoringInfo)) {
      Laptime cached = Laptime.isHotlap(this.gameData) ? this.cachedFastestHotLaptime : this.cachedFastestNormalLaptime;
      if (cached != null && (this.fastestLaptime == null || cached.getLapTime() < this.fastestLaptime.getLapTime()))
        return cached; 
    } 
    return this.fastestLaptime;
  }
  
  public final Laptime getSecondFastestLaptime() {
    return this.secondFastestLaptime;
  }
  
  public final Laptime getOldAverageLaptime() {
    return this.oldAverageLaptime;
  }
  
  public final Laptime getAverageLaptime() {
    return this.averageLaptime;
  }
  
  public final float getPathLateral() {
    return this.data.getPathLateral();
  }
  
  public final float getTrackEdge() {
    return this.data.getTrackEdge();
  }
  
  public final float getBestSector1() {
    if (this.editor_fastestLaptime != null)
      return this.editor_fastestLaptime.getSector1(); 
    return this.data.getBestSector1();
  }
  
  public final float getBestSector2(boolean includingSector1) {
    if (this.editor_fastestLaptime != null)
      return this.editor_fastestLaptime.getSector2(includingSector1); 
    float sec2 = this.data.getBestSector2();
    if (!includingSector1 && sec2 > 0.0F)
      sec2 -= getBestSector1(); 
    return sec2;
  }
  
  public final float getBestLapTime() {
    if (this.editor_fastestLaptime != null)
      return this.editor_fastestLaptime.getLapTime(); 
    return this.data.getBestLapTime();
  }
  
  public final float getBestSector3() {
    if (this.editor_fastestLaptime != null)
      return this.editor_fastestLaptime.getSector3(); 
    float lt = getBestLapTime();
    if (lt > 0.0F)
      lt -= getBestSector2(true); 
    return lt;
  }
  
  public final float getLastSector1() {
    if (this.editor_lastLaptime != null)
      return this.editor_lastLaptime.getSector1(); 
    return this.data.getLastSector1();
  }
  
  public final float getLastSector2(boolean includingSector1) {
    if (this.editor_lastLaptime != null)
      return this.editor_lastLaptime.getSector2(includingSector1); 
    float sec2 = this.data.getLastSector2();
    if (!includingSector1)
      sec2 -= getLastSector1(); 
    return sec2;
  }
  
  public final float getLastLapTime() {
    if (this.editor_lastLaptime != null)
      return this.editor_lastLaptime.getLapTime(); 
    return this.data.getLastLapTime();
  }
  
  public final Laptime getLastLaptime() {
    if (this.editor_lastLaptime != null)
      return this.editor_lastLaptime; 
    return getLaptime(getLapsCompleted());
  }
  
  public final float getLastSector3() {
    if (this.editor_lastLaptime != null)
      return this.editor_lastLaptime.getSector3(); 
    return getLastLapTime() - getLastSector2(true);
  }
  
  public final float getCurrentSector1() {
    if (this.editor_currLaptime != null)
      return this.editor_currLaptime.getSector1(); 
    return this.data.getCurrentSector1();
  }
  
  public final float getCurrentSector2(boolean includingSector1) {
    if (this.editor_currLaptime != null)
      return this.editor_currLaptime.getSector2(includingSector1); 
    float sec2 = this.data.getCurrentSector2();
    if (!includingSector1 && sec2 > 0.0F)
      sec2 -= getCurrentSector1(); 
    return sec2;
  }
  
  public final float getCurrentLaptime() {
    if (!this.scoringInfo.getSessionType().isRace() && getStintLength() < 1.0F)
      return -1.0F; 
    return this.scoringInfo.getSessionTime() - getLapStartTime();
  }
  
  public final short getNumPitstopsMade() {
    return this.data.getNumPitstopsMade();
  }
  
  public short getNumberOfScheduledPitstops() {
    if (!isPlayer())
      return -1; 
    return (this.gameData.getTelemetryData()).data.getNumberOfScheduledPitstops();
  }
  
  public final short getNumOutstandingPenalties() {
    return this.data.getNumOutstandingPenalties();
  }
  
  public final boolean isPlayer() {
    return this.data.isPlayer();
  }
  
  public final VehicleControl getVehicleControl() {
    return this.data.getVehicleControl();
  }
  
  public final boolean isInPits() {
    return this.data.isInPits();
  }
  
  public final int getNumVehiclesInSameClass() {
    this.scoringInfo.updateClassScoring();
    return this.numVehiclesInClass;
  }
  
  public final short getPlace(boolean byClass) {
    if (byClass) {
      this.scoringInfo.updateClassScoring();
      return this.placeByClass;
    } 
    if (this.place < 0)
      this.place = this.data.getPlace(); 
    return this.place;
  }
  
  public final VehicleScoringInfo getLeaderByClass() {
    this.scoringInfo.updateClassScoring();
    return this.classLeaderVSI;
  }
  
  public final VehicleScoringInfo getNextInFront(boolean byClass) {
    if (byClass) {
      this.scoringInfo.updateClassScoring();
      return this.classNextInFrontVSI;
    } 
    short place = getPlace(false);
    if (place <= 1)
      return null; 
    return this.scoringInfo.getVehicleScoringInfo(place - 2);
  }
  
  public final VehicleScoringInfo getNextBehind(boolean byClass) {
    if (byClass) {
      this.scoringInfo.updateClassScoring();
      return this.classNextBehindVSI;
    } 
    short place = getPlace(false);
    if (place >= this.scoringInfo.getNumVehicles())
      return null; 
    return this.scoringInfo.getVehicleScoringInfo(place + 0);
  }
  
  public final String getVehicleClass() {
    if (this.vehClass == null)
      this.vehClass = this.data.getVehicleClass(); 
    return this.vehClass;
  }
  
  void setVehClass(String vehClass) {
    this.vehClass = vehClass;
  }
  
  public final int getVehicleClassId() {
    if (this.classId <= 0)
      updateClassID(); 
    return this.classId;
  }
  
  public final Integer getVehicleClassID() {
    if (this.classID == null)
      updateClassID(); 
    return this.classID;
  }
  
  public final float getTimeBehindNextInFront(boolean byClass) {
    if (byClass) {
      this.scoringInfo.updateClassScoring();
      return this.timeBehindNextByClass;
    } 
    return this.data.getTimeBehindNextInFront();
  }
  
  public final int getLapsBehindNextInFront(boolean byClass) {
    if (byClass) {
      this.scoringInfo.updateClassScoring();
      return this.lapsBehindNextByClass;
    } 
    return this.data.getLapsBehindNextInFront();
  }
  
  public final float getTimeBehindLeader(boolean byClass) {
    if (byClass) {
      this.scoringInfo.updateClassScoring();
      return this.timeBehindLeaderByClass;
    } 
    return this.data.getTimeBehindLeader();
  }
  
  public final int getLapsBehindLeader(boolean byClass) {
    if (byClass) {
      this.scoringInfo.updateClassScoring();
      return this.lapsBehindLeaderByClass;
    } 
    return this.data.getLapsBehindLeader();
  }
  
  public final float getLapStartTime() {
    return this.data.getLapStartTime();
  }
  
  public final void getWorldPosition(TelemVect3 position) {
    this.data.getWorldPosition(position);
  }
  
  public final float getWorldPositionX() {
    return this.data.getWorldPositionX();
  }
  
  public final float getWorldPositionY() {
    return this.data.getWorldPositionY();
  }
  
  public final float getWorldPositionZ() {
    return this.data.getWorldPositionZ();
  }
  
  public final float getEngineRPM() {
    return this.engineRPM;
  }
  
  public final float getEngineMaxRPM() {
    return this.engineMaxRPM;
  }
  
  public final int getEngineBoostMapping() {
    return this.engineBoostMapping;
  }
  
  public final int getCurrentGear() {
    return this.gear;
  }
  
  public final void getLocalVelocity(TelemVect3 localVel) {
    this.data.getLocalVelocity(localVel);
  }
  
  public final float getScalarVelocityMPS() {
    return this.data.getScalarVelocity();
  }
  
  public final float getScalarVelocityMPH() {
    float mps = getScalarVelocityMPS();
    return mps * 2.237F;
  }
  
  public final float getScalarVelocityKPH() {
    float mps = getScalarVelocityMPS();
    return mps * 3.6F;
  }
  
  public final float getScalarVelocity() {
    if (this.profileInfo.getSpeedUnits() == ProfileInfo.SpeedUnits.MPH)
      return getScalarVelocityMPH(); 
    return getScalarVelocityKPH();
  }
  
  public final float getTopspeed() {
    return this.topspeed;
  }
  
  public final void getLocalAcceleration(TelemVect3 localAccel) {
    this.data.getLocalAcceleration(localAccel);
  }
  
  public final void getOrientationX(TelemVect3 oriX) {
    this.data.getOrientationX(oriX);
  }
  
  public final void getOrientationY(TelemVect3 oriY) {
    this.data.getOrientationY(oriY);
  }
  
  public final void getOrientationZ(TelemVect3 oriZ) {
    this.data.getOrientationZ(oriZ);
  }
  
  public final void getLocalRotation(TelemVect3 localRot) {
    this.data.getLocalRotation(localRot);
  }
  
  public final void getLocalRotationalAcceleration(TelemVect3 localRotAccel) {
    this.data.getLocalRotationalAcceleration(localRotAccel);
  }
  
  public String toString() {
    if (this.data == null)
      return String.valueOf(getClass().getSimpleName()) + " (invalid)"; 
    return String.valueOf(getClass().getSimpleName()) + " (\"" + getDriverName() + "\", " + getDriverId() + ")";
  }
  
  VehicleScoringInfo(ScoringInfo scoringInfo, ProfileInfo profileInfo, LiveGameData gameData) {
    this.scoringInfo = scoringInfo;
    this.profileInfo = profileInfo;
    this.gameData = gameData;
  }
  
  public static final class VSIPlaceComparator implements Comparator<VehicleScoringInfo> {
    public static final VSIPlaceComparator INSTANCE = new VSIPlaceComparator();
    
    public int compare(VehicleScoringInfo vsi1, VehicleScoringInfo vsi2) {
      if (vsi1.getPlace(false) < vsi2.getPlace(false))
        return -1; 
      if (vsi1.getPlace(false) > vsi2.getPlace(false))
        return 1; 
      return 0;
    }
  }
}
