Thanks to:
	Credits for the block texture go to rhodox. Go to http://painterlypack.net/
	and fling some ducats at him.
	Ricard Ziegler for the help debugging on OpenGLES1.0 devices and for the 
	impetus behind sensor-based steering (necessity truly is the mother of 
	invention - he has a single-touch device)

Installation:

	1) Install the apk as usual with
			"adb -d install MineDroid.apk"
	or download it from your phone's browser and tap it in the download list to
	install.
	2) Copy your minecraft saved worlds onto your phone's storage
	3) Launch the app and hit "Scan for worlds" - it'll search through the phone 
	storage and list any worlds it finds. You can stop the search at any time by
	hitting back - you will not lose the worlds found so far.
	4) Tap the world you want to explore.
	The starting position of the camera is taken from the level.dat file and 
	the nether is not supported, so make sure you save your game in the over-world
	before you copy the save across

Controls:

	The squares in the corners are thumbstick, left for movement, right for steering
	Tap or swipe upwards on the rectangle above the right thumbstick to jump,
	long-press or swipe downwards to crouch.
	
	Tap on the hot bar to select a tool.
	Tap either thumbstick to use the held item at the center of the screen, tap 
	on the screen to target elsewhere.
	Appropriate tool use (e.g.: a shovel on dirt or a pick on stone) is made easy:
	a single tap initiates the block-breaking, which will continue as long as the
	player is in range of the target block. Inappropriate tool use is made difficult:
	the touch must be held on target until it is broken.
	
	Blocks can be dragged from the hot bar and placed immediately, without
	having to select it as a held item
	
	The menu button will give an extensive tree of configuration options.
	Save a configuration with the name "default" (note all lower-case), and it'll
	be applied automatically at startup
	
Sensor-based steering

	You can also use your phone as a magic window into you minecraft world with
	sensor-based steering. Enable it in the settings menu (hit the menu key, go 
	to "BlockView/Interface/Sensor Steering").
	It uses the accelerometer and magnetometer to detect the phone's orientation,
	so it'll get glitchy if you rotate too fast or get close to ferrous objects.
	
Glitchy controls?

	If the touchstick controls are acting all glitchy and rubbish, you might be 
	falling foul of the problem illustrated in this video:
	
	http://www.youtube.com/watch?v=hVlsRCMltDg
	
	It seems that some phones have screens that are good enough for pinch-to-zoom
	style multitouch gestures, but can't actually track two touches reliably.
	This is a hardware limitation, and there's not a lot that can be done to
	rectify it in software. In the settings menus, to
	
	/BlockView/Interface/Left stick/Pad area
	
	The default setting is "0.0, 0.0, 150.0, 150.0" change the second number to
	330, i.e.: so that it reads "0.0, 330.0, 150.0, 150.0". This'll put the left 
	thumbstick in the upper-left corner. Remember to save the settings with the
	name "default".
	
Getting the source:

	The source is organised in three Eclipse projects:
	
	http://code.google.com/p/rugl/source/browse/#svn/trunk/droid
	
	You'll need the DroidRUGL project from that repository, 
	and also the projects at
	
	minedroid.googlecode.com
	preflect.googlecode.com
	
	For those with an Eclipse compiler setup as prissy as mine, the missing 
	javadoc warnings are Google's fault.
	
Getting the source, step-by-step:

	1) Download and install "Eclipse IDE for Java Developers" from
		http://www.eclipse.org/downloads/
	2) Open Eclipse and create a new workspace
	3) Install the subclipse SVN plugin for eclipse. Instructions at
		http://subclipse.tigris.org/servlets/ProjectProcess?pageID=p4wYuA
	4) Install the Android development tools and ADT eclipse plugin.
		http://developer.android.com/sdk/installing.html
	5) Add the SVN repositories to subclipse:
		In Eclipse, "Window" menu - "Open Perspective" - "Other"
		Choose "SVN Repository Exploring"
		Right-click in the currently-blank "SVN Repositories" tab. "New" - 
			"Repository Location..."
		The URLs for the repositories are 
			"https://rugl.googlecode.com/svn/trunk"
			"https://preflect.googlecode.com/svn/trunk"
			"https://minedroid.googlecode.com/svn/trunk"
		Add each of these
	6) Check out the projects:
		6.1) Click on "https://rugl.googlecode.com/svn/trunk", to open it. Click
			on "droid", then right-click on "DroidRUGL". Select "Check out..."
			from the menu, hit OK in the dialog box.
		6.2) From "https://preflect.googlecode.com/svn/trunk", you need the 
			"Preflect" project
		6.3) From "https://minedroid.googlecode.com/svn/trunk", you need the 
			"MineDroid" project
	7) You've now got local copies of the code - huzzah!
			
Stuff that should be done that I can't do by myself

	* Try this on loads of different phones, find and squash the inevitable bugs,
		work around the performance oddities, get exasperated at broken library
		implementations

Stuff that should be done that I'm not in a huge rush to do:

	* More block types
	* Odd-shaped blocks - steps, liquids
	* Play around with render distance and fog parameters - need to see as far as 
		possible into generated chunklets while still hiding ungenerated. 
		Maybe make it dynamic?
	* Decrease size of chunklets - tradeoff between more culled geometry and 
		greater rendering overhead
	* Proper occlusion culling, block or chunklet-based - it'll be complex and 
		I'm not sure it'll prove worthwhile.
	
Stuff that I'm not going to do, and that I don't think should be done:

	* Minecraft game stuff, to whit:
	* terrain generation
	* mobs
	* chunk updates - liquid flow, sand/gravel motion, fire, lighting, etc
	* items, inventory, crafting
	* etc
	
What's the point?

	My hope is that MineDroid can reduce the development risk for an official 
	Mojang-made android port by demonstrating:
	* that performance is good enough
	* that touch-screen interfaces work well enough
	* that device fragmentation is manageable
	Failing that, it'll be easier to make an android port once Minecraft is 
	open-sourced.