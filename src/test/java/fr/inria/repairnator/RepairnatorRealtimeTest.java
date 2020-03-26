package fr.inria.repairnator;

import static org.junit.Assert.*;

import org.junit.Test;

import fr.inria.jtravis.entities.Build;
import fr.inria.jtravis.entities.Repository;
import fr.inria.spirals.repairnator.realtime.DockerPipelineRunner;
import fr.inria.spirals.repairnator.dockerpool.RunnablePipelineContainer;
import fr.inria.spirals.repairnator.InputBuildId;
import fr.inria.spirals.repairnator.config.RepairnatorConfig;
import fr.inria.spirals.repairnator.serializer.engines.SerializerEngine;

import java.util.Arrays;
import java.util.HashSet;


import static fr.inria.spirals.repairnator.config.RepairnatorConfig.PIPELINE_MODE;

public class RepairnatorRealtimeTest 
{
	// a failing build from tailp/Travisplay , branch failForRepairnator
    public final int easyFailingBuild = 569514744;

    @Test
    public void testDockerPipelineRunner() throws Exception {
        RepairnatorConfig.getInstance().setRepairTools(new HashSet<>(Arrays.asList(new String[]{"NPEFix"})));
        DockerPipelineRunner d = new DockerPipelineRunner();
        d.initRunner();
        RunnablePipelineContainer runner = d.submitBuild(DockerPipelineRunner.REPAIRNATOR_PIPELINE_DOCKER_IMAGE_NAME, new InputBuildId(RepairnatorConfig.getInstance().getJTravis().build().fromId(easyFailingBuild).get().getId()));
        runner.run();
        assertEquals(0, runner.getExitStatus().statusCode().longValue());
    }

}
