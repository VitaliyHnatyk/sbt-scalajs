package notests

import com.inthenow.sbt.scalajs._

/**
 * Override different ways to build the project
 */
package  object build {

    val t = SharedBuild //  CommonBaseBuild SharedBuild SymLinkedBuild

    Seq("rdf", "db").map{m =>  CrossBuildOps.setBuildType(m, t)}
}