
What is this?
-------------

This project is a Mark Logic plugin for the
jEdit text editor.  jEdit is an open source
editor that's hosted on SourceForge
(http://jedit.sourceforge.net).  jEdit depends
heavily on plugins to extend its functionality.

This plugin implements a console that lets you
submit the current content of an edit buffer
to a Content Interaction Server to be executed
as an XQuery script.  The output of the query
is stored in another buffer of your choosing.

This code is open source and licensed under the
Apache 2.0 license.  For more information on the
license, visit http://www.apache.org/licenses/LICENSE-2.0


Where did this come from?
-------------------------

This project is hosted on the Mark Logic Developer
Community site http://xqzone.marklogic.com.  If you
did not get this code from there, please visit the
site to make sure you have the most current version.

xq:zone hosts other XQuery/Mark Logic open source
projects as well.


How do I install this?
----------------------

The first thing you need is a recent version of jEdit.
This plugin depeds on features in the 4.2 version of
jEdit.  As of this writing, version 4.2final has been
released.

This plugin also depends on a few other standard plugins
which must first be installed.

Follow these steps to install and setup jEdit
with the XQuery plugin:

* Download the latest version of jEdit from
  http://www.jedit.org/index.php?page=download

* Follow the installation instructions to install
  jEdit on your platform.

  If you already have jEdit (version >= 4.2)
  installed, you can skip ahead to the Install
  The Mark Logic Plugin step.

* Run jEdit.  Select Plugins -> Plugin Manager

  In the popup window, select the Install tab.

  Scroll through the list and select the following
  plugins:

    * ErrorList
    * InfoViewer
    * XML
    * XML Indenter

  Note: Plugins are listed alphabetically within
  categories.

  Select any other plugins you'd also like to install.

* Click the Install button and wait for jEdit to
  download and install the plugin jar files.

  Be patient, the jEdit plugin site is often heavily
  loaded and may be very slow.

* When downloading is complete, exit jEdit.

* Install The Mark Logic Plugin

  Unpack the contents of MLJeditXQuery-x.x.x.zip
  from this distribution and move the jar files to
  your jEdit application jars directory.  There
  are several jar files in there to install:

     * MarkLogicPlugin.jar
     * xqrunner.jar
     * xdbc.jar
     * xdmp.jar
     * jdom.jar
     * forms_rt.jar

  On Windows the application directory is usually
  c:\Program Files\jEdit\jars, or wherever you selected
  during the install.  On linux/unix/Mac it will be the
  "jars" subdirectory at the location you selected
  during the install.

  Note: Plugin jars may also go in a user-specific
  jars directory (c:\Program File\.jEdit\jars on
  Windows, $HOME/.jedit/jars on linux/unix).  If you
  have older versions of Mark Logic jar files there,
  please remove them at this point.  In the future,
  when we register the Mark Logic Plugin with the
  jEdit site, it will auto-download to the application
  jars directory.

  Note 2: This version of the plugin comes with the
  CIS 2.2 xdbc/xdmp jar files.  If you have older
  ones installed in a jEdit directory please remove
  them now.

  Note 3: Yes, you can use this plugin on Mac OS X
  or any other jEdit-supported platform.  It talks
  to the CIS server over the network with XDBC.

* Setup XQuery Syntax Highlighting

  If you want XQuery syntax highlighting enabled, install
  the XQuery file mode descriptor.

  Included in the MarkLogic-x.x.x.zip distribution
  jar file is a file named xq.xml.  Copy this file
  to jEdit's modes directory (on Windows this is
  c:\Program Files\jEdit\modes).

  Edit the file named "catalog" in that directory and
  append the following line at the bottom, immediately
  above the line that reads "</MODES>":

  <MODE NAME="xq" FILE="xq.xml" FILE_NAME_GLOB="*.{xq,xqy,xquery}" />

* Startup jEdit

* Open the Mark Logic Console

  Select the Plugins -> Mark Logic -> Mark Logic Console
  menu item.  A floating window should popup with the
  Mark Logic logo in the lower right corner.  If you
  don't see Mark Logic in the Plugins menu, then the
  jar files have not been installed properly.

  Select the Help tab at the top of that window for
  information about how to configure and use the Mark
  Logic plugin.

* Test your Connection

  Once you've configured the plugin with information
  about your XDBC server try a test to make sure it's
  connecting.

  Select an empty buffer and type "Hello World" (with
  the quotes).  In the console, choose that buffer from
  the source drop-down.  For the destination, select
  New Buffer.

  Click the Run Query button.

  A new buffer should open up and be set to Hello World
  (without quotes).  If so, the query was successful.
  If not, check your connection information and try
  again.


* You're Done, Get to work.

  If you encounter problems you can't resolve, or have
  question or comments, join the mailing list on the
  xq:zone website: http://xqzone.marklogic.com/discuss/
  or search the archives.


What's Missing?
---------------

Additional features are planned, but I'm releasing the
plugin now to get it out in the world and get some
feedback.  These are some of the things that I plan
to do, but haven't been able get done yet:

* Fully support CIS 2.2 features.

* Actually implement the Server Profiles feature

* More reliably cancel running queries (this is
  not fully implemented yet).

* Add a panel to the console to define external
  variables that will be sent with the query.

* Add Document Load/Fetch/Browse panel to aid in
  managing documents in CIS

* Deal with CLOBs and BLOBs.

* In-process HTML rendering of query output
  (This may be limited in usefullness because
  the Java JEditorPane widget doesn't cope very
  well with complex HTML.  For now, use the
  Auto Save option for query output and point
  your favorite browser at the file.  Then hit
  reload on the browser every time you run
  the query)

If you have additional ideas, post them to the
xq:zone mailing list and/or implement the new
features yourself and sent me the code.

---
Rh 9/21/2003
Mark Logic Corporation