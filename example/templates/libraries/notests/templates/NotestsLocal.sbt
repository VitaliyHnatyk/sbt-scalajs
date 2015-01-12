val notests_default = "SymLinkedBuild" // "CommonBaseBuild" "SharedBuild" "SymLinkedBuild"

val notests_rdf = System.setProperty("notests-rdf.build", notests_default)

val notests_db = System.setProperty("notests-db.build",  notests_default)

