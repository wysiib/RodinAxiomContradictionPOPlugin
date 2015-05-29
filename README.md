# Camille
An experimental plugin for Rodin that generates a new PO stating that there should be an assignement to the constants making (a subset of) the axioms true.

It can be used to verify that there is no contradiction in the axioms.

[![Build Status](https://travis-ci.org/wysiib/RodinAxiomContradictionPOPlugin.svg)](https://travis-ci.org/wysiib/RodinAxiomContradictionPOPlugin)


## Building
Maven 3 is required to build the project:
  <pre>
  cd de.stups.hhu.rodinaxiompos.parent
  mvn clean verify
  </pre>  

This will produce an updatesite in de.stups.hhu.rodinaxiompos.repository/target

We autmatically produce nightly builds that can be installed using the update site located at  http://nightly.cobra.cs.uni-duesseldorf.de/rodin_axiom_pos/


## Contributing/Bugs
Pull requests are very welcome. Suggestions for new extensions and known bugs are tracked on [Github](https://github.com/wysiib/RodinAxiomContradictionPOPlugin/issues)
