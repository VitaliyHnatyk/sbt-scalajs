package notests

import com.inthenow.sbt.scalajs._

/**
 * Define different ways to build the project
 */
package  object build {

    trait Shared {
        type rdf = XSharedBuild
        type db = XSharedBuild
        type jena = SBuildJvm
    }

    trait Common {
        type rdf = XCommonBaseBuild
        type db = XCommonBaseBuild
        type jena = SBuildJvm
    }

    trait SymLinked {
        type rdf = XLinkedBuild
        type db  = XLinkedBuild
        type jena = SBuildJvm
    }
}