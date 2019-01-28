# alienvault-reports
This is a command line utility to create a json-like report about github project issues that goes to standard out.<p>
This project uses maven to create the runnable jar file that is the 'executable'.<p>
The project also requires Java 1.8 for compilation and execution.<p>
In order to compile the utility, run the tests, and create code coverage reports for it, run the below command in this directory:<p>
mvn clean install<p>
Code coverage reports can be seen at: ./target/site/jacoco/index.html<p>
<p>
The build of the utility will be created in the target directory.<p>
To run the utility, a command similar to below can be used:
java -jar gitIssueReport-jar-with-dependencies.jar AUTHTOKEN:{some personal github auth token} {user repo}<p>
For example:<p>
java -jar gitIssueReport-jar-with-dependencies.jar AUTHTOKEN:{your token} webbervida/icanhazissues<p>