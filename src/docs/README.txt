
What is this?
-------------

This project is a Mark Logic connection adapter
for the aXe XQuery plugin for the jEdit editor.

jEdit is an open source editor that's hosted on
SourceForge (http://jedit.sourceforge.net).  jEdit
depends heavily on plugins to extend its functionality.

Pieter Wellens and Wim Le Page have written a plugin
that supports XQuery execution from within jEdit.
Their plugin is known as aXe (Advanced XML Editor).
The aXe XQuery plugin accepts adapters (plugins to
the XQuery plugin) that provide connectivity to
various XQuery engines.  See their site at
http://plantijn.ruca.ua.ac.be/~wellenslepage/ for
full details.

The code in this project is an adapter that allows
the aXe XQuery plugin to run queries against a Mark
Logic Content Interaction Server engine.

This code is open source and licensed under the
Apache 2.0 license.  For more information on the
license, visit http://www.apache.org/licenses/LICENSE-2.0


Where did this come from?
-------------------------

This project is hosted on the Mark Logic Developer
Support site http://xqzone.marklogic.com.  If you
did not get this code from there, please visit the
site to make sure you have the most current version.

xq:zone hosts other XQuery/Mark Logic open source
projects as well.


How do I install this?
----------------------

The first thing you need is a recent version of jEdit.
The aXe XQuery plugin requires a jEdit version of at
least 4.2pre14 (http://www.jedit.org/index.php?page=download).

The aXe XQuery plugin files are hosted at
http://plantijn.ruca.ua.ac.be/~wellenslepage/download.php
but the good news is that the aXe XQuery plugin will
be downloaded and installed automatically by the steps
below.

Some of the aXe jar files are under Subversion control
in this project but you should not use them for the
installation.  The jars here are used to compile
and test the Mark Logic adapter and may not be the most
current.

Follow these steps to install and setup jEdit
with the XQuery plugin:

* Download the latest version of jEdit from
  http://www.jedit.org/index.php?page=download

* Follow the installation instructions to install
  jEdit on your platform.

  If you already have jEdit (version >= 4.2pre14)
  installed, you can skip ahead to the Install
  The Mark Logic Adapter step.

* Run jEdit.  Select Plugins -> Plugin Manager

  On the popup window, select the Install tab.

  Locate the XQuery Plugin in the list (in the
  HTML and XML Category).

  Select the XQuery checkbox.  Several other
  plugins should also be automatically be checked.

  Select any other plugins you'd also like to install.

* Click the Install button and wait for jEdit to
  download and install the plugin jar files.

  Be patient, the jEdit plugin site is often heavily
  loaded and may be very slow.

  When downloading is complete, exit jEdit.

* Install The Mark Logic Adapter

  Unpack the contents of MLJeditXQuery.zip from
  this distribution and move the jar files to
  your user-specific jEdit jars directory.  There
  are four jar files in there: MarkLogicAdapter.jar,
  xdbc.jar, xdmp.jar and jdom.jar.

  On Windows this is usually the directory
  c:\Documents and Settings\{username}\jEdit\jars,
  where {username} is your login user name.
  On unix, linux or OS X, this directory is at
  $HOME/.jedit/jars.

  Note: Plugin jars may also go in the jars directory
  under the jEdit application directory, but the XQuery
  plugin only searches the user-specific directory for
  its adapters.

  Note 2: Yes, you can use this adapter on Mac OS X
  or any other jEdit-supported platform.  It talks
  to the CIS server over the network with XDBC.

* Setup XQuery syntax highlighting

  If you want XQuery syntax highlighting enabled, fetch
  the syntax specification file from the XQuery site
  at http://plantijn.ruca.ua.ac.be/~wellenslepage/downloads/
  (see the very bottom of the page).

  Copy the xq.xml file to c:\Program Files\jEdit\modes
  directory.  On the unix/linux OSs, this will be where
  you installed jEdit in the first step above.

  Edit the file named "catalog" in that directory and
  append the following line at the bottom, immediately
  above the line that reads "</MODES>":

  <MODE NAME="xq" FILE="xq.xml" FILE_NAME_GLOB="*.{xq,xqy}" />

* Restart jEdit

  Select the Plugins -> Plugin Options... menu item.
  On the popup window, find the XQuery Plugin item in
  the tree listing on the left.  Expand the item if
  you don't see the General and Options sub-items.

  Click the General item.  The main window on the right
  will display a drop-down list of adapters in the window
  on the right, near the bottom.

  Select MarkLogic from the list.  If you don't see a
  MarkLogic option in the drop-down, then the adapter
  code (MarkLogicAdapter.jar) was not properly installed.
  Make sure it's in the same directory as SaxonAdapter.jar.

  Click the Adapter item on the left, under XQuery Plugin.
  A Mark Logic connection configuration window should
  appear on the right side.  If not, try closing the
  dialog and selecting the Plugin Options... menu again.

  Fill in the connection parameters with the information
  needed to connect to your Content Interaction Server.
  An XDBC server listener must be configured on the CIS
  instance you want to connect to.  XDBC servers are
  configured in the Content Interaction Server admin
  control panel.  Contact the adminstrator if you're
  trying to connect to a server that's not on your local
  machine.

* Test your Connection

  Select the Plugins -> Xquery Plugin -> Xquery menu item.
  A small popup window should come up, usually to the right
  of your main edit window.

  Clear the Base URI text field, it's not used.

  Select "No Context" option for XML Context and "Use Pane"
  for XQuery Input.

  In the small text area at the bottom of the XQuery window,
  enter (including the quotes) "Hello World"

  Click the "XML + XQuery =" button at the bottom of the
  XQuery window.  If the execution is successful, a new
  untitled edit buffer will appear in the main jEdit window
  with the text Hello World (without quotes).

* You're Done, Get to work.

  You can select Use Buffer to send the content of
  the currently active edit buffer to be run on the server.
  This is helpul when developing XQuery code.

  You can also select Use File to execute the content of
  a file.  This can be useful when you want to capture
  the output of an external XQuery script.

  If you encounter problems you can't resolve, or have
  question or comments, join the mailing list on the
  xq:zone website: http://xqzone.marklogic.com/discuss/
  or search the archives.

---
Rh 7/16/2003
Mark Logic Corporation