1. Prerequisites

   A Java 8+ JRE or JDK
   To install a JDK on Windows, download the x64 installer or ziped archive from the official Oracle
   website or OpenJDK website, run Setup and configure the JAVA_HOME environment variable and add
   %JAVA_HOME%\bin to the Path variable. If all goes well, you can run "java -version" from any command
   prompt. The latest JDK-25 works well.
  
2. Installation and Removing

   If you got the program as a Zip-Archive, unzip it into an empty Folder. Drag the Jar file onto your
   Desktop, while pressing the Alt-Key, to create a link there. Take care that you don't move or copy
   the file. Open the Properties of the Link. On the Link-Tab you can choose the icon of the Application
   directory. The Application itself don't make any use of your Registry. If you wish the removing, just
   delete the directory, included files and link on the desktop.
   After installation take a look at LibraryTag.prp to edit properties.

3. Usage

   This is a Pre-Release, so not all MusicBrainz Functions are supported. The Application should run
   on any Plattform, where a Java 8+ Runtime is available. This includes Linux's, Mac's , Unix's,
   Windows.
   Start the program. Copy a valid Artist-MBID from MusicBrainz website using ctrl-c and paste it into
   the Input-Field using ctrl-v. Press the go button or press enter. Requested data will be printed in
   the Output Area.
   In the Output Area mouse select a Release-MBID and copy it using ctrl-c. Paste it into the
   Input-Field using ctrl-v and press Enter. The Releases will be listed at the end of the Output area.
   Again select, copy and paste a Release-MBID. Again the Recordings will be listed in the Output area.
   As last step you can do so for the Recording-MBID.

3.1 Finding Media Library Files

    For finding available media files, LibraryTag has a directory-specification how a consistent Library
    should be organized. The structure which we use are <Drive>\<FileType>\<Artist*>\<*Album>\<*Tracks>.
    For example valid path on Windows would be:
    m:\FLAC\The Beatles\1964-01 Beatles For Sale\08 - Eight Days A Week.flac
    or
    m:\MP3\Yes\1973-LL Yessongs\06 - Mood For A Day.flac
    or on Linux and Unixes
    <mountpoint>/OGG/King Crimson/1981-08 Discipline/Frame by Frame.ogg

    If your Library doesn't meet the specification you have 2 chances:
    1: Create Symbolic Links using the Windows tool mklink on the specified drive.
    2: Re-Organize your Library

4. License

   See LICENSE files in the Licenses subdirectory
