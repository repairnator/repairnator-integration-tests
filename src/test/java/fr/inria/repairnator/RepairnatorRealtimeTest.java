package fr.inria.repairnator;

import static org.junit.Assert.*;

import org.junit.Test;

import fr.inria.jtravis.JTravis;
import fr.inria.jtravis.entities.Build;
import fr.inria.jtravis.entities.BuildTool;
import fr.inria.jtravis.entities.Job;
import fr.inria.jtravis.entities.Log;
import fr.inria.jtravis.entities.Repository;
import fr.inria.jtravis.entities.StateType;
import fr.inria.spirals.repairnator.realtime.DockerPipelineRunner;
import fr.inria.spirals.repairnator.dockerpool.RunnablePipelineContainer;
import fr.inria.spirals.repairnator.InputBuildId;
import fr.inria.spirals.repairnator.config.RepairnatorConfig;
import fr.inria.spirals.repairnator.notifier.EndProcessNotifier;
import fr.inria.spirals.repairnator.realtime.counter.PullRequestCounter;
import fr.inria.spirals.repairnator.realtime.notifier.TimedSummaryNotifier;
import fr.inria.spirals.repairnator.realtime.serializer.BlacklistedSerializer;
import fr.inria.spirals.repairnator.serializer.engines.SerializerEngine;
import fr.inria.spirals.repairnator.states.LauncherMode;
import fr.inria.spirals.repairnator.realtime.RTScanner;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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


    @Test
    public void testRepositoryWithoutSuccessfulBuildIsNotInteresting() {
        String slug = "surli/failingProject";
        RepairnatorConfig.getInstance().setLauncherMode(LauncherMode.REPAIR);
        Optional<Repository> repositoryOptional = RepairnatorConfig.getInstance().getJTravis().repository().fromSlug(slug);
        assertTrue(repositoryOptional.isPresent());

        RTScanner rtScanner = new RTScanner("test", new ArrayList<SerializerEngine>());
        boolean result = rtScanner.isRepositoryInteresting(repositoryOptional.get().getId());
        assertFalse(result);
    }

    @Test
    public void testRepositoryWithoutCheckstyleIsNotInteresting() {
        String slug = "surli/test-repairnator";
        RepairnatorConfig.getInstance().setLauncherMode(LauncherMode.CHECKSTYLE);
        Optional<Repository> repositoryOptional = RepairnatorConfig.getInstance().getJTravis().repository().fromSlug(slug);
        assertTrue(repositoryOptional.isPresent());

        RTScanner rtScanner = new RTScanner("test", new ArrayList<SerializerEngine>());
        boolean result = rtScanner.isRepositoryInteresting(repositoryOptional.get().getId());
        assertFalse(result);
    }

    @Test
    public void testRepositoryWithoutCheckstyleIsInteresting() {
        String slug = "repairnator/embedded-cassandra";
        RepairnatorConfig.getInstance().setLauncherMode(LauncherMode.CHECKSTYLE);
        Optional<Repository> repositoryOptional = RepairnatorConfig.getInstance().getJTravis().repository().fromSlug(slug);
        assertTrue(repositoryOptional.isPresent());

        RTScanner rtScanner = new RTScanner("test", new ArrayList<SerializerEngine>());
        boolean result = rtScanner.isRepositoryInteresting(repositoryOptional.get().getId());
        assertTrue(result);
    }

    @Test
    public void testRepositoryWithSuccessfulBuildIsInteresting() {
        String slug = "INRIA/spoon";
        RepairnatorConfig.getInstance().setLauncherMode(LauncherMode.REPAIR);
        Optional<Repository> repositoryOptional = RepairnatorConfig.getInstance().getJTravis().repository().fromSlug(slug);
        assertTrue(repositoryOptional.isPresent());

        RTScanner rtScanner = new RTScanner("test", new ArrayList<SerializerEngine>());
        boolean result = rtScanner.isRepositoryInteresting(repositoryOptional.get().getId());
        assertTrue(result);
    }

    @Test
    public void testRepositoryWithoutJavaLanguageIsNotInteresting() {
        String slug = "rails/rails";
        RepairnatorConfig.getInstance().setLauncherMode(LauncherMode.REPAIR);
        Optional<Repository> repositoryOptional = RepairnatorConfig.getInstance().getJTravis().repository().fromSlug(slug);
        assertTrue(repositoryOptional.isPresent());

        RTScanner rtScanner = new RTScanner("test", new ArrayList<SerializerEngine>());
        boolean result = rtScanner.isRepositoryInteresting(repositoryOptional.get().getId());
        assertFalse(result);
    }
}
