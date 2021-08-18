package fr.inria.repairnator;

import static org.junit.Assert.*;

import org.junit.Test;

import fr.inria.jtravis.entities.Build;
import fr.inria.jtravis.entities.Repository;
import fr.inria.spirals.repairnator.realtime.DockerPipelineRunner;
import fr.inria.spirals.repairnator.dockerpool.RunnablePipelineContainer;
import fr.inria.spirals.repairnator.config.RepairnatorConfig;
import fr.inria.spirals.repairnator.serializer.engines.SerializerEngine;
import fr.inria.spirals.repairnator.realtime.ActiveMQPipelineRunner;
import fr.inria.spirals.repairnator.realtime.RTScanner;
import fr.inria.spirals.repairnator.states.LauncherMode;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.ArrayList;

import static fr.inria.spirals.repairnator.config.RepairnatorConfig.PIPELINE_MODE;

public class RealtimeTest 
{
	// a failing build from tailp/Travisplay , branch failForRepairnator
    public final int easyFailingBuild = 569514744;

    /**
     * Note this test might fail locally if you don't have activeMQ
     * In that case this test can be temporarily be commented out
     * Also this build is taken from Tailp/travisplay, so if
     * fetch another fail build from there or from another repo
     * if 560996872 disappears in the future.
     */
    /*
    // for some obscure reasons @Ignore is ignored
    @Ignore
    @Test
     */
    @Test
    public void tactiveMQRunnerConnection()
    {
        int buildId = 560996872;
        RepairnatorConfig config = RepairnatorConfig.getInstance();
        config.setLauncherMode(LauncherMode.REPAIR);
        config.setPipelineMode(PIPELINE_MODE.KUBERNETES.name());
        config.setActiveMQUrl("tcp://localhost:61616");
        config.setActiveMQSubmitQueueName("testing");

        Optional<Build> optionalBuild = config.getJTravis().build().fromId(buildId);
        assertTrue(optionalBuild.isPresent());

        ActiveMQPipelineRunner runner = new ActiveMQPipelineRunner();
        RTScanner rtScanner = new RTScanner("test", new ArrayList<SerializerEngine>(), runner);
        rtScanner.submitBuildToExecution(optionalBuild.get());
        assertEquals("560996872",runner.receiveBuildFromQueue());
    }

}
