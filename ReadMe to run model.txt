Change line 25 in dist_gen/distgen.rs/repast.simphony.data.logging.outputter.engine.DefaultOutputterDescriptorAction_0.xml
from <fileName>/Users/Jason/Documents/Dropbox/TU Delft/Thesis/Data/ModelOutput.csv</fileName>
to the location on your hard drive where you want the output data to be saved

Change line 22 in dist_gen/distgen.rs/repast.simphony.data.logging.outputter.engine.DefaultOutputterDescriptorAction_1.xml
from <fileName>/Users/Jason/Documents/Dropbox/TU Delft/Thesis/Data/CostOutput.csv</fileName>
to the location on your hard drive where you want the output data to be saved

run from the command line with the following:
java -cp /Applications/eclipse/plugins/repast.simphony.batch_1.2.0/bin:/Applications/eclipse/plugins/repast.simphony.runtime_1.2.0/lib/*:/Applications/eclipse/plugins/repast.simphony.core_1.2.0/lib/*:/Applications/eclipse/plugins/repast.simphony.core_1.2.0/bin:/Applications/eclipse/plugins/repast.simphony.bin_and_src_1.2.0/*:/Applications/eclipse/plugins/repast.simphony.score.runtime_1.2.0/lib/*:/Applications/eclipse/plugins/repast.simphony.data_1.2.0/lib/*:/Users/Jason/Documents/Dropbox/TU\ Delft/Thesis/Code/dist_gen/bin repast.simphony.batch.BatchMain -params /Users/Jason/Documents/Dropbox/TU\ Delft/Thesis/Code/dist_gen/batch/batch_params.xml /Users/Jason/Documents/Dropbox/TU\ Delft/Thesis/Code/dist_gen/distgen.rs

changing /Applications/eclipse/plugins/ to the location where your Repast plugins are
and changing /Users/Jason/Documents/Dropbox/TU\ Delft/Thesis/Code/dist_gen/ to the location where you put the "dist_gen" folder

for PC users the "/" need to be "\" and the ":" need to be ";"
