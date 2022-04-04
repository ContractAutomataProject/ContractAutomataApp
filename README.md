[![License: GPL v3](https://img.shields.io/badge/License-GPLv3-blue.svg)](https://www.gnu.org/licenses/gpl-3.0)


<h1>Contract Automata App</h1>

This is the GUI application of CAT, and represents an example of usage of the CAT Library
 (https://github.com/ContractAutomataProject/ContractAutomataLib) to build a tool for visualizing 
  the automata, edit them, and use the main operations (e.g., composition, synthesise).

The main application is in the file App.java, under io.github.contractautomata.catapp package. 
Check the release for a runnable jar.
The application is based on an existing framework called *mxGraph* for
editing graphs in Java. 
The GUI is implemented by adapting the BasicGraphEditor available 
in mxGraph.
The other classes are also modifications of the BasicGraphEditor example 
of mxGraph. 
The main operations using the CAT Library are under the package 
 io.github.contractautomata.catapp.actions.
For more info check https://jgraph.github.io/mxgraph/docs/manual_javavis.html.

The App has been developed using Eclipse/IntelliJ and tested on Windows machines. 

<h2>License</h2>

The tool is available under this license,
https://github.com/ContractAutomataProject/CAT_App/blob/master/license.html


<h2>Tutorials and Videos</h2>

The playlist of video tutorials and presentations about the tool is available at

https://www.youtube.com/playlist?list=PLory_2tIDsJvZB2eVlpji-baIz0320TwM

<!--The first video tutorial (https://youtu.be/LAzCEQtYOhU) shows the usage of the tool for composing automata and compute orchestrations of product lines, using the examples published in JSCP2020.
The directory demoJSCP contains an executable jar and the models used in this tutorial.
The second video tutorial (https://youtu.be/W0BHlgQEhIk) shows the computation of orchestrations and choreographies for the examples published in LMCS2020.
The directory demoLMCS2020 contains an executable jar and the models used in this tutorial.
The third video tutorial (https://youtu.be/QJjT7f7vlZ4) shows the recent refactoring and improvements of the tool published in Coordination2021.-->

<h2>Contacts</h2>

If you have any question or want to help contact me on davide.basile@isti.cnr.it.
