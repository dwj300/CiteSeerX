ParsCit README
IGC

ParsCit is a utility for extracting citations from research papers
based on Conditional Random Fields and heuristic regularization.

Installation:

In order to use the ParsCit web service you will need the following
modules in your perl library:

Log::Log4perl
Log::Dispatch

Edit lib/ParsCit/Config.pm to provide values appropriate for your
environment.  Also edit wsdl/ParsCitService.wsdl to reflect any
changes to your service URL.

Command Line Usage:

Command line utilities are provided for extracting citations from
text documents.  By default, text files are expected to be encoded
in UTF-8, but the expected encoding can be adjusted using perl
command line switches.  To run ParsCit on a single document, execute
the following command:

citeExtract.pl textfile [outfile]

If "outfile" is specified, the XML output will be written to that
file; otherwise, the XML will be printed to STDOUT.

There is also a web service interface available, using the
SOAP::Lite perl module.  To start the service, just execute:

parscit-service.pl

By default, the service will start on port 10555, but this is
configurable in the ParsCit::Config library module.  A WSDL file is
provided with the distribution that outlines the message details
expected by the ParsCit service.  If the service port is changed,
the WSDL file must also be modified to reflect that change.  Expected
parameters in the input message are "filePath" (a path to the text
file to parse) and "repositoryID".  The ParsCit service is designed
for deployment in an environment where text files may be located on
file systems mounted from arbitrary machines on the network.  Thus,
"repositoryID" provides a means to map a given shared file system to
it's mount point.  Repository mappings are configurable in the Config
module.  The "filePath" parameter provides a path to the text file
relative to the repository mount point.  The local file system may be
specified using the reserved repository ID "LOCAL".  In that case, an
absolute path to the text file may be specified.

A perl client is also provided that demonstrates how to use the
service.  Execute the client with the following command:

parscit-client.pl filePath repositoryID

If the call is successful, the XML output will be printed to STDOUT.

API:

The ParsCit libraries may be used directly from external perl
applications.  The interface module is ParsCit::Controller.  If XML
output is desired, use the

ParsCit::Controller::extractCitations($filePath)

subroutine.  If it is desirable to have faster, more structured access
to citation data from the external code, it may be more convenient to
use the

ParsCit::Controller::extractCitationsImpl($filePath)

subroutine instead.  Rather than returning the data in XML
representation, the parameters returned are a status code (code > 0
indicates success), an error message (blank if no error), a reference
to a list of ParsCit::Citation objects containing the parsed citation
data, and a reference to the body text identified during pre-processing.

If the ParsCit library is used from external Perl applications, remember
to use the "-CSD" perl option for global unicode stream support (or
otherwise handle encoding) or risk string corruption.
