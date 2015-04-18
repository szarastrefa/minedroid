# Getting the source #

The source is organised in three Eclipse projects: You'll need the DroidRUGL project from [this](http://code.google.com/p/rugl/source/browse/#svn/trunk/droid) repository, and also the projects from [here](http://minedroid.googlecode.com) and [here](http://preflect.googlecode.com).

## Step-by-Step ##

  1. Download and install "Eclipse IDE for Java Developers" from http://www.eclipse.org/downloads/
  1. Open Eclipse and create a new workspace
  1. Install the subclipse SVN plugin for eclipse. Instructions at http://subclipse.tigris.org/servlets/ProjectProcess?pageID=p4wYuA
  1. Install the Android development tools and ADT eclipse plugin. http://developer.android.com/sdk/installing.html
  1. Add the SVN repositories to subclipse:
    * "https://rugl.googlecode.com/svn/trunk"
    * "https://preflect.googlecode.com/svn/trunk"
    * "https://minedroid.googlecode.com/svn/trunk"
    1. In Eclipse, "Window" menu - "Open Perspective" - "Other"
    1. Choose "SVN Repository Exploring"
    1. Right-click in the currently-blank "SVN Repositories" tab. "New" -  "Repository Location..."
    1. Fill in the URL and hit OK.
  1. Check out the projects:
    1. Click on "https://rugl.googlecode.com/svn/trunk", to open it. Click on "droid", then right-click on "DroidRUGL". Select "Check out..." from the menu, hit OK in the dialog box.
    1. From "https://preflect.googlecode.com/svn/trunk", you need the "Preflect" project
    1. From "https://minedroid.googlecode.com/svn/trunk", you need the "MineDroid" project
  1. You've now got local copies of the code - huzzah!