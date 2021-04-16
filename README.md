<h1>Contract Automata Tool</h1>

The Contract Automata Tool is an ongoing basic research activity about implementing 
and experimenting with new developments in the theoretical framework of contract automata.
Contract automata are a formalism developed in the research area of foundations for services and distributed 
computing.
They are used for specifying services' interface, called behavioral contracts, 
 as finite-state automata, with functionalities for composing contracts and generating the 
 orchestration or choreography of a composition of services, and with extensions to modalities (MSCA) and product 
 lines (FMCA).


<h2>Usage</h2>
This is the GUI application.
It has been developed using Eclipse and tested on Windows machines. 
The GUI is based on the GraphEditor of mxGraph and allows to visualize the automata, edit them, and 
use the main operations.

The main application is in the file App.java, under com.mxgraph.examples.swing.editor package. 
A precompiled App.jar file is available in the root of the project.

<h2>License</h2>
The tool is available under Creative Common License 4.0,
https://github.com/davidebasile/CAT_App/blob/master/license.html


<h2>Tutorials</h2>

A first video tutorial is available at https://youtu.be/LAzCEQtYOhU and it shows the usage of the tool for composing automata and compute orchestrations of product lines, using the examples published in JSCP2020.
The directory demoJSCP contains an executable jar and the models used in this tutorial.

The second video tutorial, available at https://youtu.be/W0BHlgQEhIk, shows the computation of orchestrations and choreographies for the examples published in LMCS2020.
The directory demoLMCS2020 contains an executable jar and the models used in this tutorial.

The third video tutorial, available at https://youtu.be/QJjT7f7vlZ4, shows the recent refactoring and improvements of the tool published in Coordination2021.

<h2>Packages</h2>



**com.mxgraph** This package contains the Java class
`App.java`. This implements the GUI
of the tool, and it is based on an existing framework called *mxGraph* for
editing graphs in Java. 
The GUI is implemented starting from the BasicGraphEditor available 
in mxGraph.
The other classes are also modifications of the BasicGraphEditor example 
of mxGraph. 
For more info check https://jgraph.github.io/mxgraph/docs/manual_javavis.html.

The contract automata API has been imported as a Jar. 

Check the repository:
 https://github.com/davidebasile/ContractAutomataTool
 
<h2>Contacts</h2>

If you have any question or want to help contact me on davide.basile@isti.cnr.it.
