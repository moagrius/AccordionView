AccordionView
=============

AccordionView is an android widget that manages accordion-style display.  The widget manages the layout and display state of arbitrary Views so is completely customizable.

AccordionView is a direct subclass of ScrollView.

Usage
=============

The widget is a single Java class file, so can easily be included into your project by copying the package folder.

A jar will be provided shortly.

After including AccordionView, usage is simple:

Instaniate a new AccordionView normally, probably inside an Activity's onCreate:
<pre>AccordionView accordionView = new AccordionView( this );</pre>

Then pass it pairs of Views with addItem:
<pre>accordionView.addItem( someView, anotherView );</pre>

The first argument View will be the title, or activator.  The second argument View will be the content.  The content will be hidden until the activator is tapped.

AccordionView manages display state, clipping, animation, transtions, scroll position.

Access-style getters and setters are available for several configurable values, including options to enable or disable transtions, control durations, allow single or multiple items to be open at once, whether the container should scroll to an open itme, etc.

The <a target="_blank" href="http://moagrius.github.io/AccordionView/com/qozix/widgets/AccordionView.html">javadocs</a> detail the public API.

Documentation
=============

Javadocs are <a target="_blank" href="http://moagrius.github.io/AccordionView/com/qozix/widgets/AccordionView.html">here</a>

Demo
=============

A demo application will be available shortly.