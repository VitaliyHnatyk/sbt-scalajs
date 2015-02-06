[![Stories in Ready](https://badge.waffle.io/inthenow/sbt-scalajs.png?label=ready&title=Ready)](https://waffle.io/inthenow/sbt-scalajs)
sbt-scalajs
=======

[![Gitter](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/InTheNow/sbt-scalajs?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)

An SBT plugin for [Scala,js](http://www.scala-js.org/).

This plugin wraps the plugin supplied with scalajs as an auto-plugin and adds some pre-defined settings for cross-compiling 
projects. Otherwise, usage and settings is a per the original.

Also provided is the functionality of "modules" that:

1. Provide a root module that creates a project that aggregates only artifacts for all sub projects, by target type
2. Group related projects together where there is more than target type
3. Add extra pre-defined settings to single target modules, the same as for the cross build modules
4. Add support for scala version specific code, also by target type
5. Name artifacts with prefixes and by target type
6. Define "how" cross builds with shared code is done:
   * SymLinkedBuild - Symbolic links are created from the shared source to the platform specific code. Hence, there is just
     one artifact per target type. This option works great with IntelliJ IDEA on Linux/OS X
   * CommonBaseBuild - Shared code is compiled into its own library for each target by setting the sbt project to the
     same directory. Hence, there are two artifacts per target type. This option works great with IntelliJ IDEA on all platforms
   * SbtLinkedBuild - Shared source is linked by sbt to the platform specific code. Hence, there is just
     one artifact per target type. This option works great with Eclipse on all platforms
   * SharedBuild - Shared code is compiled into its own library for each target.  Hence, there are
     two artifacts per target type. This option works great with Eclipse on all platforms
7. Build types can be user defined in their local ".sbt" project. This allows developers to choose a build type to their
   personal preference and for their choice of OS/IDE.
8. CommonJs targets are supported.
9. Additional target types can be defined.

Concept
=======

A typical scala project will have a "src" directory - and not much else. Simple.

A cross build project may have a few extra src directories for platform (ie. JVM/JS) specific code. Not much extra, but
enough to complicate the build for many users.

Now add in a mix of many sub-modules of which some cross-compile, aggregate release artifacts, users that all work on different
 OS/IDE's. Then add common standards for all the modules. And finally make it easy for users to use. That's the goal of this
  plugin - if you have a simple build requirements then this probably isn't for you.

In order to cater for the different build types, a common structure is used to define a module.

Root Module
-----------

For a multi module build, specify a CrossModule of type "RootBuild". Make sure all modules are in sub-directories. Example

    lazy val rootModule = CrossModule(RootBuild, id = "banana", defaultSettings = buildSettings)
    lazy val root       = rootModule.project(Module, rootJvm, rootJs)
    lazy val rootJvm    = rootModule.project(Jvm, rdfJvm, dbJvm, jena)
    lazy val rootJs     = rootModule.project(Js, rdfJs, dbJs)

This will create a root project called "bananaRoot" that will not be published. It will also create a Jvm and Js project
that will build the specified projects and create two aggregate artifacts, banana_js and banana_jvm

Modules
-------
For each module, create a CrossModule. Example:

    lazy val dbModule = CrossModule(
      build           = SharedBuild,    // the default build type - can be overriden in the users project in ~/.sbt/<ver>
      id              = "db",           // the id of the module and prefix for all sub-projects
      baseDir         = "DB",           // the base directory for all the sub-projects
      defaultSettings = buildSettings,  // setting common to _all_ sub-projects
      modulePrefix    = "banana-")      // prefix for all artifacts

And then five projects:

1. An aggregate project that will build the source projects. This is not published
2. A JVM project with its settings and other SBT project descriptors. Takes as an argument the shared jvm project. If the
   build type is SymLinkedBuild or LinkedSourceBuild then the settings from the shared project are copied over and the
   shared code is compiled into this project.
3. A JS project, as per the JVM project in 2)
4. A jvm shared project. If the build type is SymLinkedBuild or LinkedSourceBuild then this project is not actually built
5. A js shared project, as per 4)

Example:

    lazy val db          = dbModule.project(Module, dbJvm, dbJs)
    lazy val dbJvm       = dbModule.project(Jvm, dbSharedJvm)
    lazy val dbJs        = dbModule.project(Js, dbSharedJs)
    lazy val dbSharedJvm = dbModule.project(Jvm, Shared).settings(libraryDependencies +=  scalaz)
    lazy val dbSharedJs  = dbModule.project(Js, Shared).settings(sclalajsQuery ++ scalaz_js:_*)

How it works
------------

The module is just a factory for sbt projects, the "buildType" argument specifying the type of factory to create and
has a project method that creates a new Sbt.Project. All
Buildtypes implement just four methods that define the id, base directory

Current
options detailed above


Usage
=====

To use this plugin, add a Resolver to the plugin and use the addSbtPlugin command within your project's `plugins.sbt` file:
 
    addSbtPlugin("com.github.inthenow" % "sbt-scalajs" % "0.56.10")

Your project's build file also needs to enable sbt-scalajs plugins. For example with build.sbt:

    lazy val root = (project in file(".")).enablePlugins(SbtScalajs)

For more details, please see the templates in the example directory. Also within each template, is also a template directory
with examples of user override files.
    
Cross Compiling
=====================

Multi_IDE Project
--------------

Many scala projects have a simple structure that will work with standard IDE's, typically of the following form:

Root:

    + .
    --+ .project
    --+ .idea
    --+ .idea_modules

Project:

    ...  
    Project(id       = "MyProject",  
            base     = ".",  
            settings = Seq(version      := "1.0",  
                           scalaVersion := "2.11.2"   
                           )    
            )    

Source:

    + src
    --+ main
    ----+ scala
    --+ test
    ----+ scala
 
Compiles to:

    + target
    --+ scala-2.11 
    ----+ classes  
    ----+ test-classes  

Publishes to:
    
    + MyProject_2.11
    --+ 1.0
    ----+ MyProject_2.11-1.0.jar

IntelliJ will add a project definition in the root folder, as will eclipse. If there are sub-projects, the form is merely
repeated in in each sub-directory. Note, however, that there is one important difference: for the sub-project, eclipse will
put the new `.project` file in the sub-directory of the sub-project. 

This is an important rule: Eclipse can only have one project per directory. It can however, import files from outside of the project.


Cross Compile, Multi_IDE Project
--------------------------------

We will now expand the above project to include a sub-project, and will also expand the build so that our project cross-compiles
 to an older version of scala, to keep our customers happy that have not been able to upgrade to the new compiler version. 

Root:

    + .
    --+ .project
    --+ .idea
    --+ .idea_modules
    
build scala

    ...  
    Project(id       = "MyProject",  
            base     = ".",  
            settings = Seq(version            := "1.0",  
                           scalaVersion       := "2.11.2"  
                           crossScalaVersions := Seq("2.11.2", "2.10.4"),
                           )    
            )    

Source:

    + src
    --+ main
    ----+ scala
    --+ test
    ----+ scala
    
Compiles to:

    + target
    --+ scala-2.10 
        ----+ classes  
        ----+ test-classes 
    --+ scala-2.11 
    ----+ classes  
    ----+ test-classes  

Publishes to:
    
    + MyProject_2.10
    --+ 1.0
    ----+ MyProject_2.10-1.0.jar
    + MyProject_2.11
    --+ 1.0
    ----+ MyProject_2.11-1.0.jar

So, we add one line of code to our build file and we get a new artifact published. One source tree, one project, 
two artifacts. This is just how we want things to be!

And then comes a feature request, a pretty easy it turns out as the new macro features in 2.11 allow us to easily...  stop!
  
What about 2.10? These new features are not available, we know our cross-compile will not work. And what if there turns out to be 
 a bug in the 2.10 build that was neatly fixed in 2.11. If we patch 2.10, the 2.11 will get the same "hack". And if we branch 
 the whole project, we really do have two code bases to maintain, manually, in parallel. That just will not work.
 
So, as done in most cross-compiling projects over the last few decades, we split the project into three
 
 1. A common base, or shared, project. This contains all the code common to all the targeted platforms. This should be the
 largest project *by far*, and if it isn't then you should really not be cross-compiling. As it is used twice, 
 it has to be compiled twice.
 2. 2.10 version. This contains *only* the code for 2.10. Ideally, this should even be empty.  
 3. 2.11 version. This contains *only* the code for 2.11. Ideally, this should even be empty. 
 
An obvious question at this point is: "What is the point of having two empty projects?" To answer that, we have to look at 
the structure. But first, let us recap. Before we had one project and one source tree. Now we have three source trees, and four
targets. But recall that eclipse has the rule of one project per directory - that means we need three projects. But if our
root project is now to be the "master" project for all three, then we must also put the common source into its own project.

So we end up with three subdirectories:

 1. MyProject_base
 2. MyProject_2.10
 2. MyProject_2.11

The root directory still has an eclipse project that serves as an aggregation project
Root:

Publishes to:
    
    + MyProject_2.10
    --+ 1.0
    ----+ MyProject_2.10-1.0.jar
    ----+ MyProject_2.10-1.0.pom.....(includes MyProject_base_2.10)
     + MyProject_2.11
    --+ 1.0
    ----+ MyProject_2.11-1.0.jar
    ----+ MyProject_2.11-1.0.pom.....(includes MyProject_base_2.11)
    + MyProject_base_2.11
    --+ 1.0
    ----+ MyProject_base_2.10-1.0.jar
    + MyProject_2.11
    --+ 1.0
    ----+ MyProject_base_2.11-1.0.jar

As to why we need an empty project? Well the not so cool answer is that we really have to, to keep the IDE's happy. **But**,
it does not have to be a "real" project - if there is no source, just set the base to ".MyProject_2.10" and the IDE's will 
add an empty project there. But if you do need to add some source at some point, move it to  "MyProject_2.10". The real 
value here is that clients do not know nor care, they just include MyProject_2.10 - they don't even have to know about base 
(partly why base is prefixed with an underscore, more on that later)
 
Cross Compile, scalajs Multi_IDE Project
----------------------------------------

We will now look at a final example, cross compiling our source to javascript using the scalajs compiler add-in. In many ways,
this is just the same as before: we want one code base, many targets. For simplicity, we will remove the split of the scala
 versions as they can always be added later.
 
One important difference here is that sbt cannot do the cross-compile for us, we have to do it ourselves (as scalajs comes 
from the scala team, I suspect that this might change). We do this by adding another project for the js project - but it will 
*always* be an empty project. So we now have the following structure:

So we end up with three (+1) subdirectories:

 1. MyProject_base_jvm  (shared code)
 2. MyProject_jvm
 3. MyProject_js
 4. .MyProject_base_js   (dummy target only project)
 
The root directory still has an eclipse project that serves as an aggregation project
Root:

Publishes to:
    
    + MyProject_2.10
      --+ 1.0
      ----+ MyProject_2.10-1.0.pom.....(includes MyProject_jvm_2.10 and MyProject_js_sjs0.5_2.10)
    + MyProject_jvm_2.10
    --+ 1.0
    ----+ MyProject_jvm_2.10-1.0.jar
    ----+ MyProject_jvm_2.10-1.0.pom.....(includes MyProject_base_jvm_2.10)
    + MyProject_base_jvm_2.10
     --+ 1.0
     ----+ MyProject_base_jvm_2.10-1.0.jar
     ----+ MyProject_base_jvm_2.10-1.0.pom 
     + MyProject_js_sjs0.5_2.10
     --+ 1.0
     ----+ MyProject_js_sjs0.5_2.10-1.0.jar
     ----+ MyProject_js_sjs0.5_2.10-1.0.pom.....(includes MyProject_base_js_sjs0.5_2.10)
     + MyProject_base_js_sjs0.5_2.10
      --+ 1.0
      ----+ MyProject_base_js_sjs0.5_2.10-1.0.jar
      ----+ MyProject_base_js_sjs0.5_2.10-1.0.pom 
  
    + MyProject_2.11
      --+ 1.0
      ----+ MyProject_2.11-1.0.pom.....(includes MyProject_jvm_2.11 and MyProject_js_sjs0.5_2.11)
    + MyProject_jvm_2.11
    --+ 1.0
    ----+ MyProject_jvm_2.11-1.0.jar
    ----+ MyProject_jvm_2.11-1.0.pom.....(includes MyProject_base_jvm_2.11)
    + MyProject_base_jvm_2.11
     --+ 1.0
     ----+ MyProject_base_jvm_2.11-1.0.jar
     ----+ MyProject_base_jvm_2.11-1.0.pom 
     + MyProject_js_sjs0.5_2.11
     --+ 1.0
     ----+ MyProject_js_sjs0.5_2.11-1.0.jar
     ----+ MyProject_js_sjs0.5_2.11-1.0.pom.....(includes MyProject_base_js_sjs0.5_2.11)
     + MyProject_base_js_sjs0.5_2.11
      --+ 1.0
      ----+ MyProject_base_js_sjs0.5_2.11-1.0.jar
      ----+ MyProject_base_js_sjs0.5_2.11-1.0.pom   

From the above, we see that sbt adds cross-compilation tags with an undersore, and the version with a hyphen. Here, we have
 continued that convention with for all of the sub-module details, leaving hyphens just for the underlying project name and 
 version.
   
As final thought, if we were to move these three source directories, this would enable us to create an new aggregate root
project that would include both the MyProject_2.10 and MyProject_2.11 artifacts - useful for client projects that are themselves
cross-compiled.

Examples
========
* See the [Play multi module project](https://github.com/InTheNow/sbt-scalajs/tree/master/example/play-multi-module) in the example directory
* For a cross-compiling project, see [scalatest-jasmine](https://github.com/InTheNow/scalatest-jasmine)
* For a multi-ide, multi-module, cross-compiling and cross-testing project, see [banana-rdf](https://github.com/w3c/banana-rdf)

Experimental
============

Another (not yet fully functional) plugin **SbtScalajsWeb**  is also included that adds support for [SbtWeb](https://github.com/sbt/sbt-web)

Licence
=======

Copyright &copy; 2014 Alistair Johnson

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
