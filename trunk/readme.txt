JETT - Java Excel Template Translator
----------------

JETT can be found online at http://jett.sourceforge.net.

Description
----------------

JETT is Java Excel Template Translator.  Give it a Map of "beans" -- mapping
variable names to your own data objects, specify a pre-existing Excel template
file, and JETT will create a new Excel spreadsheet, populating your data into
it.  The data can come from any source.  Mark up your template spreadsheet
first, with JEXL Expressions (e.g. "${myVariable}") and XML-like Tags (e.g.
<jt:forEach items="${myList}" var="item">) for control over how JETT translates
your template spreadsheet and populates your data.  You can even create your
own Tags to implement custom processing logic.  JETT works with .xls and .xlsx
Excel files.

Example
----------------

Here is a quick example of how to use JETT:

- Excel template spreadsheet:

+----------------+----------------+
|${var}          |${var2}!        |
+----------------+----------------+

- Java code to use JETT:

Map<String, Object> beans = new HashMap<String, Object>();
beans.put("var", "Hello");
beans.put("var2", "World");
ExcelTransformer transformer = new ExcelTransformer();
try
{
   transformer.transform("template.xlsx", "result.xlsx", beans);
}
catch (IOException e)
{
   System.err.println("I/O error occurred: " + e.getMessage());
}
catch (InvalidFormatException e)
{
   System.err.println("Spreadsheet was in invalid format: " + e.getMessage());
}

- The resultant Excel spreadsheet:

+----------------+----------------+
|Hello           |World!          |
+----------------+----------------+

Installation
----------------

To use JETT, you may download the latest distribution from
http://sourceforge.net/projects/jett/files/.  The only module is "jett-core".
Place the "jett-core" jar library in your classpath.

See "Dependencies" below for the list of dependencies.

If you are using Maven 2+, then you may place the following dependency in your
pom.xml.  Since 0.3.0, JETT has been available in the Maven 2 Central
Repository.

<dependency>
    <groupId>net.sf.jett</groupId>
    <artifactId>jett-core</artifactId>
    <version>0.9.0</version>
</dependency>

Dependencies
----------------

JETT can be used with Java 1.5+.  JETT depends on several external libraries:

The following libraries are required, as they are used by JETT:
- Apache POI 3.10 (http://poi.apache.org/download.html) (or higher)
   - poi-3.10-FINAL.jar
   - poi-ooxml-3.10-FINAL.jar
   - poi-ooxml-schemas-3.10-FINAL.jar
   - Apache POI, in turn, depends on the following libraries: XML Beans 2.3.0, Dom4j 1.6.1, and StAX 1.0.1.
- XML Beans 2.3.0
   - xmlbeans-2.3.0.jar (Comes with Apache POI distribution)
- Dom4J 1.6.1
   - dom4j-1.6.1.jar (Comes with Apache POI distribution)
- StAX 1.0.1
   - stax-api-1.0.1.jar (Comes with Apache POI distribution)
- Apache Commons JEXL 2.1.1 (http://commons.apache.org/jexl/download_jexl.cgi)
   - commons-jexl-2.1.1.jar
   - Apache Commons JEXL 2.1.1 in turn depends on Commons Logging 1.1.1.
- Apache Commons Logging 1.1.1 (http://commons.apache.org/logging/download_logging.cgi)
   - commons-logging-1.1.1.jar
- SourceForge's jAgg 0.9.0 (http://sourceforge.net/projects/jagg/files/) (or higher)
   - jagg-core-0.9.0.jar
   - Full disclosure: I built jAgg also.
- JUnit 4.8.2 (for testing only)
   - junit-4.8.2.jar
- HSQLDB 1.8.0.10 (for testing only)
   - hsqldb-1.8.0.10.jar

Build Instructions
----------------

If you would like to build JETT yourself, do the following:
1. Get the source code.
  a. Download the JETT latest distribution from
     http://sourceforge.net/projects/jett/files/.
     This contains the source code from the latest release.
  OR
  b. Checkout the latest source code from the trunk using Subversion using the
     Subversion URL http://svn.code.sf.net/p/jett/code-0/trunk.

2. Get Maven 2 or higher from http://maven.apache.org/ and install it.

3. Run Maven to build JETT.
   mvn clean install

Contacts
----------------

For issues, bugs, suggestions, and feature requests, please send an email to
the "jett-users" mailing list: jett-users@lists.sourceforge.net.

Licensing
----------------

JETT is licensed under the "GNU Lesser General Public License Version 3" at
http://www.gnu.org/copyleft/lesser.html.

Author
----------------

My name is Randy Gettman.  For a while I used jXLS for translating Excel
template spreadsheets using Java.  At the time, jXLS suffered from poor
performance when supplying lots of data.  I was inspired to create JETT after
this experience so that I could create spreadsheets from templates quickly, and
with some features that jXLS didn't have.  By the time I released JETT, jXLS
had fixed a few performance issues, so they perform similarly.  However, JETT
has a number of features that jXLS doesn't have (jXLS has a few features that
JETT doesn't (yet) have.)  The bottom line is that I wanted a jXLS with more
features, and JETT is the result.