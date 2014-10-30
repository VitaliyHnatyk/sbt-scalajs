# Example for SbtScalajs and SbtScalajsWeb

To run the tests, make sure that ChromeDriver is in your PATH

# Overview

This is a test Play application that has three submodules
 
 1. A UI module, based on scalajs.
 2. An appshared module that contains all the views, etc shared amongst the whole paly application.
 3. A security module that also uses the UI module and the appshared module

To test the application:

```
sbt test
```

To run the application:

```
sbt
run
```