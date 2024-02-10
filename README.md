# FISA-rFDynHUD
This is a repository for some new widgets for rFDynHUD that I created for FISA broadcasts.\
I don't expect anyone to contribute to this, but if you have knowledge of rFDynHUD or Java in general then feel free.\
The code in here probably sucks, but at least it does (more or less) what it's supposed to do. I'm not that great at Java.

## Licensing information
Look, I didn't really know what licence to pick, so I went with this GAPL whatever licence because I want this shit to be open-source, but I also want you to mention me whenever you use or change my code. (Actually I was looking for something like CC-BY, but it wasn't available.)\
So the BOTTOM LINE is: you are FREE to use, modify, and distribute this code in any way, even commercially if you want for all I care, PROVIDED THAT:
* you mention that this code is ©, and was originally written by, J.A. Brown;
* you mention that it is freely available at this GitHub (link to it: https://github.com/MAHADEV-J/FISA-rFDynHUD);
* you mention it whenever you make changes to it;
* you distribute it in an OPEN-SOURCE way, i.e. you always include the full source code in your distribution.

## Known bugs
* The gap to leader in the timing tower widget only updates once per sector - EXCEPT on the formation lap, where it updates constantly. Updating constantly is what I want it to do. But it seems that this is possibly how the rFactor API works.
* For lapped cars, the number of laps they are down is not always shown correctly. Most of the time it's too low. This is because of a bug in the code of rFDynHUD itself. This is the best I could do to make a workaround for that bug. My only hope of getting it to show correctly now is to look into the code of some of Ákos Fődi's F1 TV graphics widget sets to see if there's a solution in there that will make it work. But I'm not counting on it.
* When cars finish making their pit stop and drive away slowly, sometimes the gap to leader part of the timing tower flashes "PIT STOP" instead of "IN PIT" for a split second. This is probably because the rFDynHUD API determines whether a car is pitting based on whether or not it is stationary, so if a car stops to let another car past, rFDynHUD thinks it's pitting. There's probably not much that can be done about this, but then again it doesn't really matter.\
For more information, follow me on Twitter: https://twitter.com/jaybeebrtweets. I (sometimes) comment on my progress there.

## To-do list
* Create individual car timing widget
* Make weather widget a part of timing tower widget (medium low priority)
* Make timing tower disappear on formation lap (low priority)
* Deepen investigation into what is causing the game to crash sometimes
* See if you can fix the yellow-green flag image not showing on safety car inlap (not that important); until that works, just display yellow flag image instead
* Add functionality to the session status row for races that have a time limit (not very high priority until it rains)
* Look into timer class for race control widget visibility automatic toggling (not very high priority)
* Show a flag icon next to the name of drivers who have finished (low priority)
* Create new widget with old-fashioned thick timing bar with two columns (probably won't be implemented until after first FISA WSCC race)
* Add an area to the timing tower that becomes visible during pit stops and shows how long the pit stop is (optional, very low priority)
* Get rid of "magic numbers" throughout the code (i.e., create more variables)

## Changelog
* Branch "fastestlap": added a new widget that appears when a new fastest lap is set, displaying data for the driver who set it.
* Branch "sessionstatus": added a row for session status to the widget.
* Branch "pitstop": made the timing row update with the text "IN PIT" when a car is in the pitlane but not pitting, and "PIT STOP" when the car is actually pitting.\
For more information, see the commit history.
* Branch "racecontrol": added a new widget that displays messages from race control. It can display three different messages: "SAFETY CAR" (when the safety car comes out); "SAFETY CAR IN THIS LAP" (on the last lap the safety car is out); and "RACE STOPPED" (currently only when it rains on ovals). It displays these messages automatically. When these messages appear, the timing tower widget disappears, and when they disappear, it appears again.
* Branch "weather": added a new widget that shows the weather conditions and temperature.

### old changes below
Previously, all these changes were stored as comments in the file timingtower.java. I'm adding them all to this readme so I can remove them from there. It was about time I added version control as a better way to track changes.

* 20230320 1506: removed references to visible time and visible end
* 20230320 1514: changed updateVisibility function to always loop through cars
* 20230320 1522: removed references to random multiplier
* 20230320 1528: changed background images
* 20230320 1530: tried to remove positions gained/lost indicator graphic (using comments, for testing purposes)
* 20230327 1353: added gap to leader; set "data" to 0 everywhere
* 20230327 1406: added gap for lapped cars (not tested yet)
* 20230327 1411: added gap for retired cars (not tested yet)
* 20230403 1138: changed positions array to array of IntValues instead of short numbers
* 20230403 1201: added call to FillArrayValues in drawWidget
* 20230403 1218: removed call to FillArrayValues from there again and moved it to UpdateVisibility (l. 276)
* 20230403 1229: replaced one call to FillArrayValues from UpdateVisibility with code from Marvin's timing tower
* 20230403 1239: changed conditions in UpdateVisibility and removed call to forceCompleteRedraw in drawWidget
* 20230403 1246: turned call to FillArrayValues in UpdateVisiblity back on
* 20230403 1447: created an extra check in UpdateVisibility that basically duplicates what FillArrayValues already does 
* 20230403 1807: IT WORKS, THAT LAST THING MADE IT WORK; corrected string values
* 20230403 1820: limited amount of cars shown to 20 (did not work)
* 20230403 1824: created extra makeshift variable that should cause only first 20 cars to be shown (l. 282)
* 20230501 1421: last solution didn't work; instead changed FillArrayValues function to show only first 20 cars (l. 172)
* 20230507 0922: last solution didn't work either; instead changed posOffset value in DrawWidget to (l. 374)
* 20230507 1005: that worked; changed loop in FillArrayValues back to how it was before; changed FillArrayVAlues and DrawWidget to hopefully show leader all the time and update whenever gaps change
* 20230507 1005: now it seems to update positions only when there's an overtake and gaps only once a lap
* 20230507 1022: changed updateVisibility to hopefully update gaps all the time too
* 20230507 1033: that didn't work; added l. 320 to hopefully force leader gap to be shown as "Leader"
* 20230507 1045: that worked; changed gap changed update logic in updateVisibility
* 20230507 1051: that didn't work; removed carsOnLeadLap check in updateVisibility
* 20230507 1450: didn't work either; changed updateVisibility again using a seemingly unnecessary boolean check
* 20230507 1518: nope; added debugging string on l. 240
* 20230507 1524: tried again on l. 317
* 20230507 1531: copied code from FillArrayValues to updateVisibility
* 20230507 1535: commented out gap update code from FillArrayValues
* 20230507 1535: this didn't work and it's now also broken in editor mode, so reverted last change
* 20230507 1543: removed unnecessary boolean in updateVisibility; also commented out call to ClearArrayValues
* 20230507 1546: now it's completely invisible, so reverted last change
* 20230507 1548: added call to ClearArrayValues in another place (l. 343)
* 20230507 1822: added debugging info
* 20230507 1822: removed debugging info again; the problem was NOT the call to FillArrayValues
* 20230507 1830: changed current lap number on l. 278
* 20230507 1902: changed current lap number back; commented out forceCompleteRedraw and FillArrayValues in updateVisibility
* 20230507 1916: reverted last change
* 20230507 1942: changed getLapsCompleted to getCurrentLap in updateVisibility (l. 278)
* 20230507 1946: works the same
* 20230507 2114: commented out a lot of code in UpdateVisibility in an attempt to make lap 1+ more like formation lap
* 20230508 0846: commented out even more code in UpdateVisibility and added call to FillArrayValues in DrawWidget
* 20230508 0852: made a small adjustment to DrawWidget
* 20230508 0859: made some more small adjustments to DrawWidget and UpdateVisibility
* 20230508 0938: uploaded all this crap to GitHub
