/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.gradle.plugins.defaults;

import aQute.bnd.osgi.Constants;
import aQute.bnd.version.Version;

import com.github.spotbugs.snom.SpotBugsPlugin;
import com.github.spotbugs.snom.SpotBugsReport;
import com.github.spotbugs.snom.SpotBugsTask;

import com.liferay.gradle.plugins.JspCDefaultsPlugin;
import com.liferay.gradle.plugins.LiferayBasePlugin;
import com.liferay.gradle.plugins.LiferayOSGiPlugin;
import com.liferay.gradle.plugins.baseline.BaselinePlugin;
import com.liferay.gradle.plugins.cache.CacheExtension;
import com.liferay.gradle.plugins.cache.CachePlugin;
import com.liferay.gradle.plugins.cache.task.TaskCache;
import com.liferay.gradle.plugins.defaults.internal.BaselineDefaultsPlugin;
import com.liferay.gradle.plugins.defaults.internal.FindSecurityBugsPlugin;
import com.liferay.gradle.plugins.defaults.internal.JSDocDefaultsPlugin;
import com.liferay.gradle.plugins.defaults.internal.JaCoCoPlugin;
import com.liferay.gradle.plugins.defaults.internal.LiferayRelengPlugin;
import com.liferay.gradle.plugins.defaults.internal.PublishPluginDefaultsPlugin;
import com.liferay.gradle.plugins.defaults.internal.SpotBugsDefaultsPlugin;
import com.liferay.gradle.plugins.defaults.internal.WhipDefaultsPlugin;
import com.liferay.gradle.plugins.defaults.internal.util.BackupFilesBuildAdapter;
import com.liferay.gradle.plugins.defaults.internal.util.CopyrightUtil;
import com.liferay.gradle.plugins.defaults.internal.util.FileUtil;
import com.liferay.gradle.plugins.defaults.internal.util.GitRepo;
import com.liferay.gradle.plugins.defaults.internal.util.GitUtil;
import com.liferay.gradle.plugins.defaults.internal.util.GradlePluginsDefaultsUtil;
import com.liferay.gradle.plugins.defaults.internal.util.GradleUtil;
import com.liferay.gradle.plugins.defaults.internal.util.IncrementVersionClosure;
import com.liferay.gradle.plugins.defaults.internal.util.NameSuffixFileSpec;
import com.liferay.gradle.plugins.defaults.internal.util.StringUtil;
import com.liferay.gradle.plugins.defaults.internal.util.XMLUtil;
import com.liferay.gradle.plugins.defaults.task.CheckOSGiBundleStateTask;
import com.liferay.gradle.plugins.defaults.task.InstallCacheTask;
import com.liferay.gradle.plugins.defaults.task.ReplaceRegexTask;
import com.liferay.gradle.plugins.defaults.task.WriteArtifactPublishCommandsTask;
import com.liferay.gradle.plugins.defaults.task.WritePropertiesTask;
import com.liferay.gradle.plugins.dependency.checker.DependencyCheckerExtension;
import com.liferay.gradle.plugins.dependency.checker.DependencyCheckerPlugin;
import com.liferay.gradle.plugins.extensions.BundleExtension;
import com.liferay.gradle.plugins.extensions.LiferayExtension;
import com.liferay.gradle.plugins.extensions.LiferayOSGiExtension;
import com.liferay.gradle.plugins.jasper.jspc.CompileJSPTask;
import com.liferay.gradle.plugins.jasper.jspc.JspCPlugin;
import com.liferay.gradle.plugins.jsdoc.JSDocPlugin;
import com.liferay.gradle.plugins.jsdoc.JSDocTask;
import com.liferay.gradle.plugins.lang.builder.LangBuilderPlugin;
import com.liferay.gradle.plugins.node.task.PublishNodeModuleTask;
import com.liferay.gradle.plugins.patcher.PatchTask;
import com.liferay.gradle.plugins.rest.builder.BuildRESTTask;
import com.liferay.gradle.plugins.rest.builder.RESTBuilderPlugin;
import com.liferay.gradle.plugins.service.builder.ServiceBuilderPlugin;
import com.liferay.gradle.plugins.source.formatter.SourceFormatterPlugin;
import com.liferay.gradle.plugins.test.integration.TestIntegrationBasePlugin;
import com.liferay.gradle.plugins.test.integration.TestIntegrationTomcatExtension;
import com.liferay.gradle.plugins.tlddoc.builder.TLDDocBuilderPlugin;
import com.liferay.gradle.plugins.tlddoc.builder.task.TLDDocTask;
import com.liferay.gradle.plugins.upgrade.table.builder.UpgradeTableBuilderPlugin;
import com.liferay.gradle.plugins.util.BndUtil;
import com.liferay.gradle.plugins.util.PortalTools;
import com.liferay.gradle.plugins.whip.WhipPlugin;
import com.liferay.gradle.plugins.wsdd.builder.BuildWSDDTask;
import com.liferay.gradle.plugins.wsdd.builder.WSDDBuilderPlugin;
import com.liferay.gradle.plugins.wsdl.builder.WSDLBuilderPlugin;
import com.liferay.gradle.plugins.xsd.builder.XSDBuilderPlugin;
import com.liferay.gradle.util.Validator;
import com.liferay.gradle.util.copy.ExcludeExistingFileAction;
import com.liferay.gradle.util.copy.RenameDependencyClosure;
import com.liferay.gradle.util.copy.ReplaceLeadingPathAction;
import com.liferay.gradle.util.copy.StripPathSegmentsAction;
import com.liferay.portal.tools.wsdd.builder.WSDDBuilderArgs;

import groovy.lang.Closure;

import groovy.time.Duration;
import groovy.time.TimeCategory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import java.net.URL;
import java.net.URLConnection;

import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import javax.xml.bind.DatatypeConverter;
import javax.xml.parsers.DocumentBuilder;

import org.apache.commons.io.FileUtils;

import org.gradle.StartParameter;
import org.gradle.api.Action;
import org.gradle.api.GradleException;
import org.gradle.api.JavaVersion;
import org.gradle.api.NamedDomainObjectContainer;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.UncheckedIOException;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.ConfigurationContainer;
import org.gradle.api.artifacts.Dependency;
import org.gradle.api.artifacts.DependencyResolveDetails;
import org.gradle.api.artifacts.DependencySet;
import org.gradle.api.artifacts.DependencySubstitutions;
import org.gradle.api.artifacts.ExcludeRule;
import org.gradle.api.artifacts.ExternalDependency;
import org.gradle.api.artifacts.ExternalModuleDependency;
import org.gradle.api.artifacts.LenientConfiguration;
import org.gradle.api.artifacts.ModuleDependency;
import org.gradle.api.artifacts.ModuleVersionSelector;
import org.gradle.api.artifacts.ProjectDependency;
import org.gradle.api.artifacts.ResolutionStrategy;
import org.gradle.api.artifacts.ResolvableDependencies;
import org.gradle.api.artifacts.ResolveException;
import org.gradle.api.artifacts.ResolvedConfiguration;
import org.gradle.api.artifacts.component.ComponentSelector;
import org.gradle.api.artifacts.dsl.ArtifactHandler;
import org.gradle.api.artifacts.dsl.DependencyHandler;
import org.gradle.api.artifacts.dsl.RepositoryHandler;
import org.gradle.api.artifacts.repositories.ArtifactRepository;
import org.gradle.api.artifacts.repositories.MavenArtifactRepository;
import org.gradle.api.artifacts.repositories.PasswordCredentials;
import org.gradle.api.component.SoftwareComponent;
import org.gradle.api.component.SoftwareComponentContainer;
import org.gradle.api.execution.TaskExecutionGraph;
import org.gradle.api.file.ConfigurableFileTree;
import org.gradle.api.file.CopySpec;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.file.DuplicatesStrategy;
import org.gradle.api.file.FileCollection;
import org.gradle.api.file.FileCopyDetails;
import org.gradle.api.file.FileTree;
import org.gradle.api.file.SourceDirectorySet;
import org.gradle.api.internal.GradleInternal;
import org.gradle.api.internal.artifacts.publish.ArchivePublishArtifact;
import org.gradle.api.internal.project.ProjectInternal;
import org.gradle.api.invocation.Gradle;
import org.gradle.api.logging.LogLevel;
import org.gradle.api.logging.Logger;
import org.gradle.api.plugins.ApplicationPlugin;
import org.gradle.api.plugins.BasePlugin;
import org.gradle.api.plugins.BasePluginExtension;
import org.gradle.api.plugins.JavaPlugin;
import org.gradle.api.plugins.JavaPluginConvention;
import org.gradle.api.plugins.quality.Pmd;
import org.gradle.api.plugins.quality.PmdExtension;
import org.gradle.api.plugins.quality.PmdPlugin;
import org.gradle.api.provider.Property;
import org.gradle.api.provider.Provider;
import org.gradle.api.publish.PublicationContainer;
import org.gradle.api.publish.PublishingExtension;
import org.gradle.api.publish.maven.MavenArtifact;
import org.gradle.api.publish.maven.MavenPublication;
import org.gradle.api.publish.maven.plugins.MavenPublishPlugin;
import org.gradle.api.publish.maven.tasks.GenerateMavenPom;
import org.gradle.api.publish.plugins.PublishingPlugin;
import org.gradle.api.resources.ResourceHandler;
import org.gradle.api.resources.TextResourceFactory;
import org.gradle.api.specs.Spec;
import org.gradle.api.specs.Specs;
import org.gradle.api.tasks.Copy;
import org.gradle.api.tasks.SourceSet;
import org.gradle.api.tasks.SourceSetContainer;
import org.gradle.api.tasks.SourceSetOutput;
import org.gradle.api.tasks.StopActionException;
import org.gradle.api.tasks.TaskCollection;
import org.gradle.api.tasks.TaskContainer;
import org.gradle.api.tasks.TaskInputs;
import org.gradle.api.tasks.TaskOutputs;
import org.gradle.api.tasks.VerificationTask;
import org.gradle.api.tasks.bundling.Jar;
import org.gradle.api.tasks.compile.CompileOptions;
import org.gradle.api.tasks.compile.JavaCompile;
import org.gradle.api.tasks.javadoc.Javadoc;
import org.gradle.api.tasks.testing.JUnitXmlReport;
import org.gradle.api.tasks.testing.Test;
import org.gradle.api.tasks.testing.TestTaskReports;
import org.gradle.execution.ProjectConfigurer;
import org.gradle.external.javadoc.CoreJavadocOptions;
import org.gradle.external.javadoc.StandardJavadocDocletOptions;
import org.gradle.internal.service.ServiceRegistry;
import org.gradle.language.base.plugins.LifecycleBasePlugin;
import org.gradle.plugins.ide.api.XmlFileContentMerger;
import org.gradle.plugins.ide.eclipse.model.Classpath;
import org.gradle.plugins.ide.eclipse.model.ClasspathEntry;
import org.gradle.plugins.ide.eclipse.model.EclipseClasspath;
import org.gradle.plugins.ide.eclipse.model.EclipseModel;
import org.gradle.plugins.ide.eclipse.model.SourceFolder;
import org.gradle.plugins.ide.idea.IdeaPlugin;
import org.gradle.plugins.ide.idea.model.IdeaModel;
import org.gradle.plugins.ide.idea.model.IdeaModule;
import org.gradle.process.ExecSpec;
import org.gradle.util.CollectionUtils;
import org.gradle.util.GUtil;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * @author Andrea Di Giorgi
 * @author Charles Wu
 */
public class LiferayOSGiDefaultsPlugin implements Plugin<Project> {

	public static final String CHECK_OSGI_BUNDLE_STATE_TASK_NAME =
		"checkOSGiBundleState";

	public static final String COMMIT_CACHE_TASK_NAME = "commitCache";

	public static final String COPY_LIBS_TASK_NAME = "copyLibs";

	public static final String DEFAULT_REPOSITORY_URL =
		GradlePluginsDefaultsUtil.DEFAULT_REPOSITORY_URL;

	public static final String DEPLOY_APP_SERVER_LIB_TASK_NAME =
		"deployAppServerLib";

	public static final String DEPLOY_CONFIGS_TASK_NAME = "deployConfigs";

	public static final String DEPLOY_TOOL_TASK_NAME = "deployTool";

	public static final String DOWNLOAD_COMPILED_JSP_TASK_NAME =
		"downloadCompiledJSP";

	public static final String INSTALL_CACHE_TASK_NAME = "installCache";

	public static final String JAR_JAVADOC_TASK_NAME = "jarJavadoc";

	public static final String JAR_JSDOC_TASK_NAME = "jarJSDoc";

	public static final String JAR_JSP_TASK_NAME = "jarJSP";

	public static final String JAR_SOURCES_TASK_NAME = "jarSources";

	public static final String JAR_TLDDOC_TASK_NAME = "jarTLDDoc";

	public static final String PORTAL_TEST_CONFIGURATION_NAME = "portalTest";

	public static final String PORTAL_TEST_SNAPSHOT_CONFIGURATION_NAME =
		"portalTestSnapshot";

	public static final String RELEASE_PORTAL_ROOT_DIR_PROPERTY_NAME =
		"release.versions.test.other.dir";

	public static final String SNAPSHOT_IF_STALE_PROPERTY_NAME =
		"snapshotIfStale";

	public static final String SYNC_RELEASE_PROPERTY_NAME = "syncRelease";

	public static final String SYNC_VERSIONS_TASK_NAME = "syncVersions";

	public static final String UPDATE_FILE_SNAPSHOT_VERSIONS_TASK_NAME =
		"updateFileSnapshotVersions";

	public static final String UPDATE_FILE_VERSIONS_TASK_NAME =
		"updateFileVersions";

	@Override
	@SuppressWarnings("serial")
	public void apply(final Project project) {
		String portalVersion = PortalTools.getPortalVersion(project);

		final File portalRootDir = GradleUtil.getRootDir(
			project.getRootProject(), "portal-impl");

		GradleUtil.applyPlugin(project, LiferayOSGiPlugin.class);

		BundleExtension bundleExtension = BndUtil.getBundleExtension(
			project.getExtensions());

		BasePluginExtension basePluginExtension = GradleUtil.getExtension(
			project, BasePluginExtension.class);
		final LiferayExtension liferayExtension = GradleUtil.getExtension(
			project, LiferayExtension.class);

		GitRepo gitRepo = GitRepo.getGitRepo(project.getProjectDir());
		final boolean testProject = GradlePluginsDefaultsUtil.isTestProject(
			project);

		File versionOverrideFile = _getVersionOverrideFile(project, gitRepo);

		boolean syncReleaseVersions = _syncReleaseVersions(
			project, portalRootDir, versionOverrideFile, testProject);

		_applyDependencyVersionOverrides(project, portalRootDir);

		_applyVersionOverrides(project, bundleExtension, versionOverrideFile);

		Gradle gradle = project.getGradle();

		gradle.addBuildListener(_backupFilesBuildAdapter);

		StartParameter startParameter = gradle.getStartParameter();

		List<String> taskNames = startParameter.getTaskNames();

		final boolean publishing = _isPublishing(project);

		boolean deployToAppServerLibs = false;
		boolean deployToTools = false;

		if (FileUtil.exists(project, ".lfrbuild-app-server-lib")) {
			deployToAppServerLibs = true;
		}
		else if (FileUtil.exists(project, ".lfrbuild-tool")) {
			deployToTools = true;
		}

		_applyPlugins(project, bundleExtension);

		_applyConfigScripts(project);

		_addDependenciesPmd(project);

		if (testProject || _hasTests(project)) {
			GradleUtil.applyPlugin(project, WhipPlugin.class);

			WhipDefaultsPlugin.INSTANCE.apply(project);

			Configuration portalConfiguration = GradleUtil.getConfiguration(
				project, LiferayBasePlugin.PORTAL_CONFIGURATION_NAME);
			Configuration portalTestConfiguration = _addConfigurationPortalTest(
				project);
			Configuration portalTestSnapshotConfiguration =
				_addConfigurationPortalTestSnapshot(project);

			_addDependenciesPortalTest(project, portalVersion);
			_addDependenciesPortalTestSnapshot(project);

			_configureConfigurationTest(
				project, JavaPlugin.TEST_COMPILE_CLASSPATH_CONFIGURATION_NAME);
			_configureConfigurationTest(
				project, JavaPlugin.TEST_RUNTIME_ONLY_CONFIGURATION_NAME);
			_configureEclipse(
				project, portalConfiguration, portalTestConfiguration);
			_configureIdea(
				project, portalConfiguration, portalTestConfiguration);
			_configureSourceSetTest(
				project, portalConfiguration, portalTestConfiguration,
				portalTestSnapshotConfiguration);
			_configureSourceSetTestIntegration(
				project, portalConfiguration, portalTestConfiguration);

			if (Boolean.getBoolean("junit.code.coverage") ||
				GradleUtil.getProperty(project, "junit.code.coverage", false)) {

				JaCoCoPlugin.INSTANCE.apply(project);
			}
		}

		Task baselineTask = GradleUtil.getTask(
			project, BaselinePlugin.BASELINE_TASK_NAME);
		Jar jar = (Jar)GradleUtil.getTask(project, JavaPlugin.JAR_TASK_NAME);
		Task syncVersionsTask = _addTaskSyncVersions(project);

		baselineTask.finalizedBy(syncVersionsTask);

		if (syncReleaseVersions) {
			_configureTaskBaselineSyncReleaseVersions(
				baselineTask, versionOverrideFile);
		}

		if (!testProject) {
			_addTaskCheckOSGiBundleState(project, bundleExtension);
		}

		InstallCacheTask installCacheTask = _addTaskInstallCache(
			project, portalRootDir);

		_addTaskCommitCache(project, installCacheTask);

		_addTaskCopyLibs(project);

		Copy deployConfigsTask = _addTaskDeployConfigs(
			project, liferayExtension);

		if (deployToAppServerLibs) {
			_addTaskAlias(
				project, DEPLOY_APP_SERVER_LIB_TASK_NAME,
				LiferayBasePlugin.DEPLOY_TASK_NAME);
		}
		else if (deployToTools) {
			_addTaskAlias(
				project, DEPLOY_TOOL_TASK_NAME,
				LiferayBasePlugin.DEPLOY_TASK_NAME);
		}

		final Jar jarJSPsTask = _addTaskJarJSP(project);
		final Jar jarJavadocTask = _addTaskJarJavadoc(project);
		final Jar jarJSDocTask = _addTaskJarJSDoc(project);
		final Jar jarSourcesTask = _addTaskJarSources(project, testProject);
		final Jar jarTLDDocTask = _addTaskJarTLDDoc(project);

		final ReplaceRegexTask updateFileVersionsTask =
			_addTaskUpdateFileVersions(project);
		final ReplaceRegexTask updateVersionTask = _addTaskUpdateVersion(
			project);

		File appBndFile = _getAppBndFile(project, portalRootDir);

		_configureBasePlugin(basePluginExtension, portalRootDir);
		_configureBundleDefaultInstructions(project, publishing);
		_configureConfigurations(
			project, appBndFile, liferayExtension, publishing);
		_configureDependencyChecker(project);
		_configureDeployDir(
			project, liferayExtension, deployToAppServerLibs, deployToTools);
		_configureEclipse(project);
		_configureJavaPlugin(project, portalRootDir);
		_configureLocalPortalTool(
			project, portalRootDir, LangBuilderPlugin.CONFIGURATION_NAME,
			_LANG_BUILDER_PORTAL_TOOL_NAME);
		_configureLocalPortalTool(
			project, portalRootDir, SourceFormatterPlugin.CONFIGURATION_NAME,
			_SOURCE_FORMATTER_PORTAL_TOOL_NAME);
		_configurePmd(project);
		_configureProject(project);
		GradlePluginsDefaultsUtil.configureRepositories(project, portalRootDir);

		if (PortalTools.PORTAL_VERSION_7_0_X.equals(portalVersion) ||
			PortalTools.PORTAL_VERSION_7_1_X.equals(portalVersion) ||
			PortalTools.PORTAL_VERSION_7_2_X.equals(portalVersion) ||
			PortalTools.PORTAL_VERSION_7_3_X.equals(portalVersion)) {

			_configureSourceSetMain(project);
		}

		_configureTaskDeploy(project, deployConfigsTask);
		_configureTaskJar(jar, testProject);
		_configureTaskJavadoc(project, bundleExtension);
		_configureTaskTest(project);
		_configureTaskTestIntegration(project);
		_configureTaskTlddoc(project, portalRootDir);
		_configureTasksCheckOSGiBundleState(project, liferayExtension);
		_configureTasksJavaCompile(project);
		_configureTasksJspC(project, bundleExtension);
		_configureTasksPmd(project);
		_configureTasksSpotBugs(project);
		_configureTestIntegrationTomcat(project);

		_addTaskUpdateFileSnapshotVersions(project);

		if (publishing) {
			GenerateMavenPom generatePomFileForMavenPublicationTask =
				(GenerateMavenPom)GradleUtil.getTask(
					project, "generatePomFileForMavenPublication");

			_configureTaskGeneratePomFileForMavenPublication(
				project, generatePomFileForMavenPublicationTask);

			jar.dependsOn(generatePomFileForMavenPublicationTask);

			Task generateJSPJavaTask = GradleUtil.fetchTask(
				project, JspCPlugin.GENERATE_JSP_JAVA_TASK_NAME);

			if (generateJSPJavaTask != null) {
				generateJSPJavaTask.mustRunAfter(
					generatePomFileForMavenPublicationTask);
			}

			Task javadocTask = GradleUtil.fetchTask(
				project, JavaPlugin.JAVADOC_TASK_NAME);

			if (javadocTask != null) {
				javadocTask.mustRunAfter(
					generatePomFileForMavenPublicationTask);
			}

			_configureTasksEnabledIfStaleSnapshot(
				project, testProject,
				MavenPublishPlugin.PUBLISH_LOCAL_LIFECYCLE_TASK_NAME,
				PublishingPlugin.PUBLISH_LIFECYCLE_TASK_NAME);
		}

		GradleUtil.withPlugin(
			project, RESTBuilderPlugin.class,
			new Action<RESTBuilderPlugin>() {

				@Override
				public void execute(RESTBuilderPlugin restBuilderPlugin) {
					_configureLocalPortalTool(
						project, portalRootDir,
						RESTBuilderPlugin.CONFIGURATION_NAME,
						_REST_BUILDER_PORTAL_TOOL_NAME);

					_configureTaskBuildREST(project);
				}

			});

		GradleUtil.withPlugin(
			project, ServiceBuilderPlugin.class,
			new Action<ServiceBuilderPlugin>() {

				@Override
				public void execute(ServiceBuilderPlugin serviceBuilderPlugin) {
					_configureLocalPortalTool(
						project, portalRootDir,
						ServiceBuilderPlugin.CONFIGURATION_NAME,
						_SERVICE_BUILDER_PORTAL_TOOL_NAME);
				}

			});

		GradleUtil.withPlugin(
			project, WSDDBuilderPlugin.class,
			new Action<WSDDBuilderPlugin>() {

				@Override
				public void execute(WSDDBuilderPlugin wsddBuilderPlugin) {
					_configureTaskBuildWSDD(project);
				}

			});

		project.afterEvaluate(
			new Action<Project>() {

				@Override
				public void execute(Project project) {
					_configureArtifacts(
						project, jarJSDocTask, jarJSPsTask, jarJavadocTask,
						jarSourcesTask, jarTLDDocTask);
					_configurePublishing(
						project, jarJSDocTask, jarJSPsTask, jarJavadocTask,
						jarSourcesTask, jarTLDDocTask);
					_configureTaskJarSources(jarSourcesTask);
					_configureTaskUpdateFileVersions(
						updateFileVersionsTask, portalRootDir);

					GradlePluginsDefaultsUtil.setProjectSnapshotVersion(
						project, _SNAPSHOT_PROPERTY_NAMES);

					if (GradleUtil.hasPlugin(project, CachePlugin.class)) {
						_configureTaskUpdateVersionForCachePlugin(
							updateVersionTask);
					}

					_configureTaskCompileJSP(
						project, jarJSPsTask, liferayExtension);

					// setProjectSnapshotVersion must be called before
					// configureTaskPublish, because the latter one needs
					// to know if we are publishing a snapshot or not.

					_configureTaskPublish(
						project, testProject, updateFileVersionsTask,
						updateVersionTask);

					_configureProjectBndProperties(project, liferayExtension);
				}

			});

		if (taskNames.contains("eclipse") || taskNames.contains("idea")) {
			_forceProjectDependenciesEvaluation(project);
		}

		TaskExecutionGraph taskExecutionGraph = gradle.getTaskGraph();

		taskExecutionGraph.whenReady(
			new Closure<Void>(project) {

				@SuppressWarnings("unused")
				public void doCall(TaskExecutionGraph taskExecutionGraph) {
					Task jarTask = GradleUtil.getTask(
						project, JavaPlugin.JAR_TASK_NAME);

					if (taskExecutionGraph.hasTask(jarTask)) {
						_configureBundleInstructions(project, bundleExtension);
					}
				}

			});
	}

	private Configuration _addConfigurationPortalTest(Project project) {
		Configuration configuration = GradleUtil.addConfiguration(
			project, PORTAL_TEST_CONFIGURATION_NAME);

		configuration.setDescription(
			"Configures Liferay portal test utility artifacts for this " +
				"project.");
		configuration.setVisible(false);

		return configuration;
	}

	private Configuration _addConfigurationPortalTestSnapshot(Project project) {
		Configuration configuration = GradleUtil.addConfiguration(
			project, PORTAL_TEST_SNAPSHOT_CONFIGURATION_NAME);

		configuration.setDescription(
			"Configures Liferay portal test snapshot artifacts for this " +
				"project.");
		configuration.setVisible(false);

		return configuration;
	}

	private void _addDependenciesPmd(Project project) {
		String version = PortalTools.getVersion(project, _PMD_PORTAL_TOOL_NAME);

		if (Validator.isNotNull(version)) {
			GradleUtil.addDependency(
				project, "pmd", PortalTools.GROUP, _PMD_PORTAL_TOOL_NAME,
				version);
		}
	}

	private void _addDependenciesPortalTest(
		Project project, String portalVersion) {

		GradleUtil.addDependency(
			project, PORTAL_TEST_CONFIGURATION_NAME, _GROUP_PORTAL,
			"com.liferay.portal.test", "default");

		if (PortalTools.PORTAL_VERSION_7_0_X.equals(portalVersion) ||
			PortalTools.PORTAL_VERSION_7_1_X.equals(portalVersion) ||
			PortalTools.PORTAL_VERSION_7_2_X.equals(portalVersion)) {

			GradleUtil.addDependency(
				project, PORTAL_TEST_CONFIGURATION_NAME, _GROUP_PORTAL,
				"com.liferay.portal.test.integration", "default");
		}
	}

	private void _addDependenciesPortalTestSnapshot(Project project) {
		GradleUtil.addDependency(
			project, PORTAL_TEST_SNAPSHOT_CONFIGURATION_NAME, _GROUP_PORTAL,
			"com.liferay.portal.impl", "default");
		GradleUtil.addDependency(
			project, PORTAL_TEST_SNAPSHOT_CONFIGURATION_NAME, _GROUP_PORTAL,
			"com.liferay.portal.kernel", "default");
	}

	private Task _addTaskAlias(
		Project project, String taskName, String originalTaskName) {

		Task task = project.task(taskName);

		Task originalTask = GradleUtil.getTask(project, originalTaskName);

		task.dependsOn(originalTask);
		task.setDescription("Alias for " + originalTask);
		task.setGroup(originalTask.getGroup());

		return task;
	}

	private CheckOSGiBundleStateTask _addTaskCheckOSGiBundleState(
		Project project, final BundleExtension bundleExtension) {

		CheckOSGiBundleStateTask checkOSGiBundleStateTask = GradleUtil.addTask(
			project, CHECK_OSGI_BUNDLE_STATE_TASK_NAME,
			CheckOSGiBundleStateTask.class);

		checkOSGiBundleStateTask.setBundleSymbolicName(
			new Callable<String>() {

				@Override
				public String call() throws Exception {
					return bundleExtension.getInstruction(
						Constants.BUNDLE_SYMBOLICNAME);
				}

			});

		checkOSGiBundleStateTask.setDescription(
			"Checks the state of the deployed OSGi bundle.");
		checkOSGiBundleStateTask.setGroup(
			LifecycleBasePlugin.VERIFICATION_GROUP);

		return checkOSGiBundleStateTask;
	}

	private Task _addTaskCommitCache(
		Project project, final InstallCacheTask installCacheTask) {

		Task task = project.task(COMMIT_CACHE_TASK_NAME);

		task.dependsOn(installCacheTask);

		task.doLast(
			new Action<Task>() {

				@Override
				public void execute(Task task) {
					File cachedVersionDir =
						installCacheTask.getCacheDestinationDir();

					File cachedArtifactDir = cachedVersionDir.getParentFile();

					File[] cachedVersionDirs = FileUtil.getDirectories(
						cachedArtifactDir);

					if (cachedVersionDirs.length != 2) {
						throw new StopActionException(
							"Skipping old cached version deletion");
					}

					File oldCachedVersionDir = cachedVersionDirs[0];

					if (cachedVersionDir.equals(oldCachedVersionDir)) {
						oldCachedVersionDir = cachedVersionDirs[1];
					}

					Logger logger = task.getLogger();

					Project project = task.getProject();

					boolean deleted = project.delete(oldCachedVersionDir);

					if (!deleted && logger.isWarnEnabled()) {
						logger.warn(
							"Unable to delete old cached version in " +
								oldCachedVersionDir);
					}
				}

			});

		task.doLast(
			new Action<Task>() {

				@Override
				public void execute(Task task) {
					Project project = task.getProject();

					project.exec(
						new Action<ExecSpec>() {

							@Override
							public void execute(ExecSpec execSpec) {
								execSpec.setCommandLine("git", "add", ".");

								File cachedVersionDir =
									installCacheTask.getCacheDestinationDir();

								execSpec.setWorkingDir(
									cachedVersionDir.getParentFile());
							}

						});
				}

			});

		task.doLast(
			new Action<Task>() {

				@Override
				public void execute(Task task) {
					Project project = task.getProject();

					final String commitSubject = GitUtil.getGitResult(
						project, "log", "-1", "--pretty=%s");

					project.exec(
						new Action<ExecSpec>() {

							@Override
							public void execute(ExecSpec execSpec) {
								String message = _CACHE_COMMIT_MESSAGE;

								int index = commitSubject.indexOf(' ');

								if (index != -1) {
									message =
										commitSubject.substring(0, index + 1) +
											_CACHE_COMMIT_MESSAGE;
								}

								execSpec.setCommandLine(
									"git", "commit", "-m", message);
							}

						});
				}

			});

		task.setDescription(
			"Installs and commits the project to the local Gradle cache for " +
				"testing.");
		task.setGroup(PublishingPlugin.PUBLISH_TASK_GROUP);

		return task;
	}

	private Copy _addTaskCopyLibs(Project project) {
		Copy copy = GradleUtil.addTask(
			project, COPY_LIBS_TASK_NAME, Copy.class);

		File libDir = _getLibDir(project);

		copy.eachFile(new ExcludeExistingFileAction(libDir));

		Configuration compileOnlyConfiguration = GradleUtil.getConfiguration(
			project, JavaPlugin.COMPILE_ONLY_CONFIGURATION_NAME);
		Configuration runtimeOnlyConfiguration = GradleUtil.getConfiguration(
			project, JavaPlugin.RUNTIME_ONLY_CONFIGURATION_NAME);

		copy.from(compileOnlyConfiguration, runtimeOnlyConfiguration);

		copy.into(libDir);

		Closure<String> renameDependencyClosure = new RenameDependencyClosure(
			project, compileOnlyConfiguration.getName(),
			runtimeOnlyConfiguration.getName());

		copy.rename(renameDependencyClosure);

		copy.setEnabled(false);

		Task classesTask = GradleUtil.getTask(
			project, JavaPlugin.CLASSES_TASK_NAME);

		classesTask.dependsOn(copy);

		return copy;
	}

	private Copy _addTaskDeployConfigs(
		Project project, final LiferayExtension liferayExtension) {

		Copy copy = GradleUtil.addTask(
			project, DEPLOY_CONFIGS_TASK_NAME, Copy.class);

		GradleUtil.setProperty(
			copy, LiferayOSGiPlugin.AUTO_CLEAN_PROPERTY_NAME, false);

		copy.from("configs");
		copy.into(
			new Callable<File>() {

				@Override
				public File call() throws Exception {
					return new File(
						liferayExtension.getLiferayHome(), "osgi/configs");
				}

			});

		copy.setDescription("Deploys additional configuration files.");

		TaskOutputs taskOutputs = copy.getOutputs();

		taskOutputs.upToDateWhen(
			new Spec<Task>() {

				@Override
				public boolean isSatisfiedBy(Task task) {
					return false;
				}

			});

		return copy;
	}

	private Copy _addTaskDownloadCompiledJSP(
		final JavaCompile compileJSPTask, final Jar jarJSPsTask,
		Properties artifactProperties, LiferayExtension liferayExtension) {

		final String artifactJspcURL = artifactProperties.getProperty(
			"artifact.jspc.url");

		if (Validator.isNull(artifactJspcURL)) {
			return null;
		}

		final Project project = compileJSPTask.getProject();

		Copy copy = GradleUtil.addTask(
			project, DOWNLOAD_COMPILED_JSP_TASK_NAME, Copy.class);

		copy.exclude("META-INF/MANIFEST.MF");

		copy.from(
			new Callable<FileTree>() {

				@Override
				public FileTree call() throws Exception {
					File file;

					try {
						file = FileUtil.get(project, artifactJspcURL);
					}
					catch (Exception exception) {
						String message = exception.getMessage();

						if (!message.equals("HTTP Authorization failure")) {
							throw exception;
						}

						int start = artifactJspcURL.lastIndexOf('/');

						start = artifactJspcURL.indexOf('-', start) + 1;

						Provider<String> archiveClassifierProvider =
							jarJSPsTask.getArchiveClassifier();
						Provider<String> archiveExtensionProvider =
							jarJSPsTask.getArchiveExtension();

						String classifier = archiveClassifierProvider.get();
						String extension = archiveExtensionProvider.get();

						int end =
							artifactJspcURL.length() - classifier.length() -
								extension.length() - 2;

						String version = artifactJspcURL.substring(start, end);

						DependencyHandler dependencyHandler =
							project.getDependencies();

						Map<String, Object> args = new HashMap<>();

						args.put("classifier", classifier);
						args.put("ext", extension);
						args.put("group", project.getGroup());
						args.put(
							"name", GradleUtil.getArchivesBaseName(project));
						args.put("version", version);

						Dependency dependency = dependencyHandler.create(args);

						ConfigurationContainer configurationContainer =
							project.getConfigurations();

						Configuration configuration =
							configurationContainer.detachedConfiguration(
								dependency);

						file = configuration.getSingleFile();
					}

					return project.zipTree(file);
				}

			});

		copy.into(
			new Callable<File>() {

				@Override
				public File call() throws Exception {
					return compileJSPTask.getDestinationDir();
				}

			});

		copy.into(
			new Callable<File>() {

				@Override
				public File call() throws Exception {
					File liferayHomeDir = liferayExtension.getLiferayHome();

					int index = artifactJspcURL.lastIndexOf('/');

					String dirName = artifactJspcURL.substring(
						index + 1, artifactJspcURL.length() - 9);

					return new File(liferayHomeDir, "work/" + dirName);
				}

			});

		copy.setDescription(
			"Downloads the latest compiled JSP classes for this project.");
		copy.setIncludeEmptyDirs(false);

		return copy;
	}

	private InstallCacheTask _addTaskInstallCache(
		final Project project, File portalRootDir) {

		InstallCacheTask installCacheTask = GradleUtil.addTask(
			project, INSTALL_CACHE_TASK_NAME, InstallCacheTask.class);

		installCacheTask.dependsOn(
			BasePlugin.CLEAN_TASK_NAME +
				StringUtil.capitalize(installCacheTask.getName()),
			MavenPublishPlugin.PUBLISH_LOCAL_LIFECYCLE_TASK_NAME);

		installCacheTask.doFirst(
			new Action<Task>() {

				@Override
				public void execute(Task task) {
					String result = GitUtil.getGitResult(
						task.getProject(), "status", "--porcelain", ".");

					if (Validator.isNotNull(result)) {
						throw new GradleException(
							"Unable to install project to the local Gradle " +
								"cache, commit changes first");
					}
				}

			});

		installCacheTask.setArtifactGroup(
			new Callable<Object>() {

				@Override
				public Object call() throws Exception {
					return project.getGroup();
				}

			});

		installCacheTask.setArtifactName(
			new Callable<String>() {

				@Override
				public String call() throws Exception {
					return GradleUtil.getArchivesBaseName(project);
				}

			});

		installCacheTask.setArtifactVersion(
			new Callable<Object>() {

				@Override
				public Object call() throws Exception {
					return project.getVersion();
				}

			});

		if (portalRootDir != null) {
			installCacheTask.setCacheFormat(InstallCacheTask.CacheFormat.MAVEN);
			installCacheTask.setCacheRootDir(
				new File(
					portalRootDir,
					GradlePluginsDefaultsUtil.TMP_MAVEN_REPOSITORY_DIR_NAME));
		}

		installCacheTask.setDescription(
			"Installs the project to the local Gradle cache for testing.");
		installCacheTask.setGroup(PublishingPlugin.PUBLISH_TASK_GROUP);

		GradleUtil.setProperty(
			installCacheTask, LiferayOSGiPlugin.AUTO_CLEAN_PROPERTY_NAME,
			false);

		return installCacheTask;
	}

	private Jar _addTaskJarJavadoc(Project project) {
		Jar jar = GradleUtil.addTask(project, JAR_JAVADOC_TASK_NAME, Jar.class);

		jar.setDescription(
			"Assembles a jar archive containing the Javadoc files for this " +
				"project.");
		jar.setGroup(BasePlugin.BUILD_GROUP);

		Property<String> property = jar.getArchiveClassifier();

		property.set("javadoc");

		Javadoc javadoc = (Javadoc)GradleUtil.getTask(
			project, JavaPlugin.JAVADOC_TASK_NAME);

		jar.from(javadoc);

		return jar;
	}

	private Jar _addTaskJarJSDoc(Project project) {
		Jar jar = GradleUtil.addTask(project, JAR_JSDOC_TASK_NAME, Jar.class);

		jar.setDescription(
			"Assembles a jar archive containing the Javascript API " +
				"documentation files for this project.");
		jar.setDuplicatesStrategy(DuplicatesStrategy.INCLUDE);
		jar.eachFile(new StripPathSegmentsAction(2));
		jar.setGroup(BasePlugin.BUILD_GROUP);
		jar.setIncludeEmptyDirs(false);

		Property<String> property = jar.getArchiveClassifier();

		property.set("jsdoc");

		JSDocTask jsDocTask = (JSDocTask)GradleUtil.getTask(
			project, JSDocPlugin.JSDOC_TASK_NAME);

		jar.from(jsDocTask);

		return jar;
	}

	private Jar _addTaskJarJSP(Project project) {
		Jar jar = GradleUtil.addTask(project, JAR_JSP_TASK_NAME, Jar.class);

		jar.setDescription(
			"Assembles a jar archive containing the compiled JSP classes for " +
				"this project.");
		jar.setDuplicatesStrategy(DuplicatesStrategy.INCLUDE);
		jar.setGroup(BasePlugin.BUILD_GROUP);
		jar.setIncludeEmptyDirs(false);

		Property<String> property = jar.getArchiveClassifier();

		property.set("jspc");

		JavaCompile javaCompile = (JavaCompile)GradleUtil.getTask(
			project, JspCPlugin.COMPILE_JSP_TASK_NAME);

		jar.from(javaCompile);

		return jar;
	}

	private Jar _addTaskJarSources(Project project, boolean testProject) {
		Jar jar = _addTaskJarSources(
			project, JAR_SOURCES_TASK_NAME, testProject);

		jar.setDescription(
			"Assembles a jar archive containing the main source files.");

		Property<String> property = jar.getArchiveClassifier();

		property.set("sources");

		return jar;
	}

	private Jar _addTaskJarSources(
		Project project, String taskName, boolean testProject) {

		Jar jar = GradleUtil.addTask(project, taskName, Jar.class);

		final File compileIncludeSourcesDir = new File(
			project.getBuildDir(), "compile-include-sources");

		Action<Task> copyCompileIncludeSourcesAction = new Action<Task>() {

			@Override
			public void execute(Task task) {
				try {
					FileUtils.deleteDirectory(compileIncludeSourcesDir);
				}
				catch (IOException ioException) {
					throw new UncheckedIOException(ioException);
				}

				_copyCompileIncludeSources(project, compileIncludeSourcesDir);
			}

		};

		jar.doFirst(copyCompileIncludeSourcesAction);

		jar.from(compileIncludeSourcesDir);

		jar.setDuplicatesStrategy(DuplicatesStrategy.EXCLUDE);
		jar.setGroup(BasePlugin.BUILD_GROUP);

		File docrootDir = project.file("docroot");

		if (docrootDir.exists()) {
			jar.from(docrootDir);
		}
		else {
			SourceSet sourceSet = GradleUtil.getSourceSet(
				project, SourceSet.MAIN_SOURCE_SET_NAME);

			jar.from(sourceSet.getAllSource());

			if (testProject) {
				sourceSet = GradleUtil.getSourceSet(
					project, SourceSet.TEST_SOURCE_SET_NAME);

				jar.from(sourceSet.getAllSource());

				sourceSet = GradleUtil.getSourceSet(
					project,
					TestIntegrationBasePlugin.TEST_INTEGRATION_SOURCE_SET_NAME);

				jar.from(sourceSet.getAllSource());
			}
		}

		if (FileUtil.exists(project, ".lfrbuild-releng-skip-source")) {
			jar.setEnabled(false);
		}

		return jar;
	}

	private Jar _addTaskJarTLDDoc(Project project) {
		Jar jar = GradleUtil.addTask(project, JAR_TLDDOC_TASK_NAME, Jar.class);

		jar.setDescription(
			"Assembles a jar archive containing the Tag Library " +
				"Documentation files for this project.");
		jar.setGroup(BasePlugin.BUILD_GROUP);

		Property<String> property = jar.getArchiveClassifier();

		property.set("taglibdoc");

		TLDDocTask tldDocTask = (TLDDocTask)GradleUtil.getTask(
			project, TLDDocBuilderPlugin.TLDDOC_TASK_NAME);

		jar.from(tldDocTask);

		return jar;
	}

	private ReplaceRegexTask _addTaskSyncVersions(final Project project) {
		ReplaceRegexTask replaceRegexTask = GradleUtil.addTask(
			project, SYNC_VERSIONS_TASK_NAME, ReplaceRegexTask.class);

		_configureTaskReplaceRegexJSMatches(replaceRegexTask);

		replaceRegexTask.setDescription(
			"Updates the version in additional project files based on the " +
				"current Bundle-Version.");

		if (!FileUtil.exists(project, "bnd.bnd")) {
			replaceRegexTask.setEnabled(false);
		}

		replaceRegexTask.setReplacement(
			new Callable<String>() {

				@Override
				public String call() throws Exception {
					File bndFile = project.file("bnd.bnd");

					Properties properties = GUtil.loadProperties(bndFile);

					return properties.getProperty(Constants.BUNDLE_VERSION);
				}

			});

		return replaceRegexTask;
	}

	private ReplaceRegexTask _addTaskUpdateFileSnapshotVersions(
		final Project project) {

		ReplaceRegexTask replaceRegexTask = GradleUtil.addTask(
			project, UPDATE_FILE_SNAPSHOT_VERSIONS_TASK_NAME,
			ReplaceRegexTask.class);

		replaceRegexTask.setDescription(
			"Updates the project version in external files to the latest " +
				"snapshot.");

		String regex = _getModuleSnapshotDependencyRegex(project);

		File rootDir = project.getRootDir();

		Map<String, Object> args = new HashMap<>();

		args.put("dir", rootDir.getParentFile());

		args.put("include", "**/build.gradle");

		replaceRegexTask.setMatches(
			Collections.singletonMap(
				regex, (FileCollection)project.fileTree(args)));

		replaceRegexTask.setReplacement(
			new Callable<String>() {

				@Override
				public String call() throws Exception {
					return _getNexusLatestSnapshotVersion(project);
				}

			});

		return replaceRegexTask;
	}

	@SuppressWarnings({"serial", "unchecked"})
	private ReplaceRegexTask _addTaskUpdateFileVersions(final Project project) {
		final ReplaceRegexTask replaceRegexTask = GradleUtil.addTask(
			project, UPDATE_FILE_VERSIONS_TASK_NAME, ReplaceRegexTask.class);

		GradleUtil.setProperty(
			replaceRegexTask, _UPDATE_FILE_VERSIONS_EXACT_VERSION_PROPERTY_NAME,
			Boolean.FALSE);

		replaceRegexTask.pre(
			new Closure<String>(project) {

				@SuppressWarnings("unused")
				public String doCall(String content, File file) {
					String fileName = file.getName();

					if (!fileName.equals("build.gradle") ||
						GradlePluginsDefaultsUtil.isTestProject(
							file.getParentFile())) {

						return content;
					}

					File markerDir = GradleUtil.getRootDir(
						file.getParentFile(),
						".lfrbuild-releng-skip-update-file-versions");

					if (markerDir != null) {
						return content;
					}

					GitRepo contentGitRepo = GitRepo.getGitRepo(
						file.getParentFile());

					if ((contentGitRepo != null) && contentGitRepo.readOnly) {
						return content;
					}

					StringBuilder sb = new StringBuilder();

					sb.append('(');
					sb.append(JavaPlugin.COMPILE_ONLY_CONFIGURATION_NAME);
					sb.append(") ");
					sb.append(Pattern.quote(_getProjectDependency(project)));

					String replament = Matcher.quoteReplacement(
						_getModuleDependency(project, true));

					return content.replaceAll(sb.toString(), "$1 " + replament);
				}

			});

		replaceRegexTask.replaceOnlyIf(
			new Closure<Boolean>(project) {

				@SuppressWarnings("unused")
				public Boolean doCall(
					String group, String replacement, String content,
					File contentFile) {

					GitRepo contentGitRepo = GitRepo.getGitRepo(
						contentFile.getParentFile());

					if ((contentGitRepo != null) && contentGitRepo.readOnly) {
						return false;
					}

					String projectPath = project.getPath();

					if (!projectPath.startsWith(":apps:") &&
						!projectPath.startsWith(":core:") &&
						!projectPath.startsWith(":dxp:apps:") &&
						!projectPath.startsWith(":dxp:core:") &&
						!projectPath.startsWith(":private:apps:") &&
						!projectPath.startsWith(":private:core:")) {

						return true;
					}

					boolean exactVersion = GradleUtil.getProperty(
						replaceRegexTask,
						_UPDATE_FILE_VERSIONS_EXACT_VERSION_PROPERTY_NAME,
						false);

					if (exactVersion) {
						return true;
					}

					Version groupVersion = _getVersion(group);
					Version replacementVersion = _getVersion(replacement);

					if ((groupVersion == null) ||
						(replacementVersion == null) ||
						(groupVersion.getMajor() !=
							replacementVersion.getMajor())) {

						return true;
					}

					return false;
				}

			});

		replaceRegexTask.setDescription(
			"Updates the project version in external files.");

		replaceRegexTask.setReplacement(
			new Callable<Object>() {

				@Override
				public Object call() throws Exception {
					return project.getVersion();
				}

			});

		return replaceRegexTask;
	}

	private ReplaceRegexTask _addTaskUpdateVersion(Project project) {
		ReplaceRegexTask replaceRegexTask = GradleUtil.addTask(
			project, LiferayRelengPlugin.UPDATE_VERSION_TASK_NAME,
			ReplaceRegexTask.class);

		_configureTaskReplaceRegexJSMatches(replaceRegexTask);

		replaceRegexTask.match(_BUNDLE_VERSION_REGEX, "bnd.bnd");

		replaceRegexTask.onlyIf(
			new Spec<Task>() {

				@Override
				public boolean isSatisfiedBy(Task task) {
					Project project = task.getProject();

					String version = String.valueOf(project.getVersion());

					return !version.contains("LIFERAY-PATCHED-");
				}

			});

		replaceRegexTask.setDescription(
			"Updates the project version in the " + Constants.BUNDLE_VERSION +
				" header.");

		replaceRegexTask.setReplacement(
			IncrementVersionClosure.MICRO_INCREMENT);

		return replaceRegexTask;
	}

	private void _applyConfigScripts(Project project) {
		GradleUtil.applyScript(
			project,
			"com/liferay/gradle/plugins/defaults/dependencies" +
				"/config-maven-publish.gradle",
			project);
	}

	private void _applyDependencyVersionOverrides(
		Project project, File portalRootDir) {

		if (portalRootDir == null) {
			return;
		}

		File file = new File(
			portalRootDir, "modules/.dependency-version-overrides.properties");

		if (!file.exists()) {
			return;
		}

		Properties properties = GUtil.loadProperties(file);

		String dependencies = properties.getProperty("dependencies");

		if (Validator.isNull(dependencies)) {
			return;
		}

		String includeDirs = properties.getProperty("include.dirs");

		if (!_containsProject(project, includeDirs, portalRootDir)) {
			return;
		}

		ConfigurationContainer configurationContainer =
			project.getConfigurations();

		Action<Configuration> action = new Action<Configuration>() {

			@Override
			public void execute(Configuration configuration) {
				ResolutionStrategy resolutionStrategy =
					configuration.getResolutionStrategy();

				DependencySubstitutions dependencySubstitutions =
					resolutionStrategy.getDependencySubstitution();

				for (String dependency : dependencies.split(",")) {
					String[] tokens = dependency.split("->");

					if (tokens.length != 2) {
						continue;
					}

					DependencySubstitutions.Substitution substitution =
						dependencySubstitutions.substitute(
							dependencySubstitutions.module(tokens[0]));

					substitution.using(
						dependencySubstitutions.module(tokens[1]));
				}
			}

		};

		configurationContainer.all(action);
	}

	private void _applyPlugins(
		Project project, BundleExtension bundleExtension) {

		if (Validator.isNotNull(bundleExtension.getInstruction("Main-Class"))) {
			GradleUtil.applyPlugin(project, ApplicationPlugin.class);
		}

		GradleUtil.applyPlugin(project, BaselinePlugin.class);
		GradleUtil.applyPlugin(project, DependencyCheckerPlugin.class);
		GradleUtil.applyPlugin(project, IdeaPlugin.class);
		GradleUtil.applyPlugin(project, JSDocPlugin.class);
		GradleUtil.applyPlugin(project, MavenPublishPlugin.class);
		GradleUtil.applyPlugin(project, PmdPlugin.class);
		GradleUtil.applyPlugin(project, SpotBugsPlugin.class);

		if (FileUtil.exists(project, "rest-config.yaml")) {
			GradleUtil.applyPlugin(project, RESTBuilderPlugin.class);
		}

		if (FileUtil.exists(project, "service.xml")) {
			GradleUtil.applyPlugin(project, ServiceBuilderPlugin.class);
			GradleUtil.applyPlugin(project, UpgradeTableBuilderPlugin.class);
			GradleUtil.applyPlugin(project, WSDDBuilderPlugin.class);
		}

		if (FileUtil.exists(project, "wsdl")) {
			GradleUtil.applyPlugin(project, WSDLBuilderPlugin.class);
		}

		if (FileUtil.exists(project, "xsd")) {
			GradleUtil.applyPlugin(project, XSDBuilderPlugin.class);
		}

		BaselineDefaultsPlugin.INSTANCE.apply(project);
		FindSecurityBugsPlugin.INSTANCE.apply(project);
		JSDocDefaultsPlugin.INSTANCE.apply(project);
		PublishPluginDefaultsPlugin.INSTANCE.apply(project);
		SpotBugsDefaultsPlugin.INSTANCE.apply(project);
	}

	private void _applyVersionOverrideJson(Project project, String fileName)
		throws IOException {

		File file = project.file(fileName);

		if (!file.exists()) {
			return;
		}

		Path path = file.toPath();

		_backupFilesBuildAdapter.backUp(path);

		String json = new String(
			Files.readAllBytes(path), StandardCharsets.UTF_8);

		Matcher matcher = GradlePluginsDefaultsUtil.jsonVersionPattern.matcher(
			json);

		if (!matcher.find()) {
			return;
		}

		json =
			json.substring(0, matcher.start(1)) + project.getVersion() +
				json.substring(matcher.end(1));

		Files.write(path, json.getBytes(StandardCharsets.UTF_8));
	}

	private void _applyVersionOverrides(
		Project project, BundleExtension bundleExtension,
		File versionOverrideFile) {

		if ((versionOverrideFile == null) || !versionOverrideFile.exists()) {
			return;
		}

		final Properties versionOverrides = GUtil.loadProperties(
			versionOverrideFile);

		// Bundle-Version

		String bundleVersion = versionOverrides.getProperty(
			Constants.BUNDLE_VERSION);

		if (Validator.isNotNull(bundleVersion)) {
			bundleExtension.instruction(
				Constants.BUNDLE_VERSION, bundleVersion);

			project.setVersion(bundleVersion);

			try {
				for (String fileName :
						GradlePluginsDefaultsUtil.JSON_VERSION_FILE_NAMES) {

					_applyVersionOverrideJson(project, fileName);
				}
			}
			catch (IOException ioException) {
				throw new UncheckedIOException(ioException);
			}
		}

		// Dependencies

		ConfigurationContainer configurationContainer =
			project.getConfigurations();

		Action<Configuration> action = new Action<Configuration>() {

			@Override
			public void execute(Configuration configuration) {
				ResolutionStrategy resolutionStrategy =
					configuration.getResolutionStrategy();

				DependencySubstitutions dependencySubstitutions =
					resolutionStrategy.getDependencySubstitution();

				for (String key : versionOverrides.stringPropertyNames()) {
					if (key.indexOf(_DEPENDENCY_KEY_SEPARATOR) == -1) {
						continue;
					}

					String dependencyNotation = key.replace(
						_DEPENDENCY_KEY_SEPARATOR, ':');

					ComponentSelector componentSelector =
						dependencySubstitutions.module(dependencyNotation);

					DependencySubstitutions.Substitution substitution =
						dependencySubstitutions.substitute(componentSelector);

					ComponentSelector newComponentSelector;

					String value = versionOverrides.getProperty(key);

					if (value.indexOf(':') != -1) {
						newComponentSelector = dependencySubstitutions.project(
							value);
					}
					else {
						newComponentSelector = dependencySubstitutions.module(
							dependencyNotation + ":" + value);
					}

					substitution.using(newComponentSelector);
				}
			}

		};

		configurationContainer.all(action);

		GradleInternal gradleInternal = (GradleInternal)project.getGradle();

		ServiceRegistry serviceRegistry = gradleInternal.getServices();

		ProjectConfigurer projectConfigurer = serviceRegistry.get(
			ProjectConfigurer.class);

		for (String key : versionOverrides.stringPropertyNames()) {
			if (key.indexOf(_DEPENDENCY_KEY_SEPARATOR) == -1) {
				continue;
			}

			String value = versionOverrides.getProperty(key);

			if (value.indexOf(':') == -1) {
				continue;
			}

			ProjectInternal dependencyProject =
				(ProjectInternal)project.findProject(value);

			if (dependencyProject != null) {
				projectConfigurer.configure(dependencyProject);
			}
		}

		// Package versions

		final Copy copy = (Copy)GradleUtil.getTask(
			project, JavaPlugin.PROCESS_RESOURCES_TASK_NAME);

		copy.filesMatching(
			"**/packageinfo",
			new Action<FileCopyDetails>() {

				@Override
				@SuppressWarnings("serial")
				public void execute(final FileCopyDetails fileCopyDetails) {
					fileCopyDetails.filter(
						new Closure<Void>(copy) {

							@SuppressWarnings("unused")
							public String doCall(String line) {
								if (Validator.isNull(line)) {
									return line;
								}

								String packagePath = fileCopyDetails.getPath();

								packagePath = packagePath.substring(
									0, packagePath.lastIndexOf('/'));

								packagePath = packagePath.replace('/', '.');

								String versionOverride =
									versionOverrides.getProperty(packagePath);

								if (Validator.isNotNull(versionOverride)) {
									return "version " + versionOverride;
								}

								return line;
							}

						});
				}

			});
	}

	@SuppressWarnings("serial")
	private void _configureArtifacts(
		Project project, Jar jarJSDocTask, Jar jarJSPTask, Jar jarJavadocTask,
		Jar jarSourcesTask, Jar jarTLDDocTask) {

		ArtifactHandler artifactHandler = project.getArtifacts();

		if (!GradlePluginsDefaultsUtil.isSnapshot(
				project, _SNAPSHOT_PROPERTY_NAMES)) {

			SourceSet sourceSet = GradleUtil.getSourceSet(
				project, SourceSet.MAIN_SOURCE_SET_NAME);

			if (FileUtil.hasFiles(sourceSet.getResources(), _jspSpec)) {
				artifactHandler.add(
					Dependency.ARCHIVES_CONFIGURATION, jarJSPTask);
			}
		}

		Spec<File> spec = new Spec<File>() {

			@Override
			public boolean isSatisfiedBy(File file) {
				String fileName = file.getName();

				return !fileName.equals("MANIFEST.MF");
			}

		};

		if (FileUtil.hasSourceFiles(jarSourcesTask, spec)) {
			artifactHandler.add(
				Dependency.ARCHIVES_CONFIGURATION, jarSourcesTask);
		}

		Task javadocTask = GradleUtil.getTask(
			project, JavaPlugin.JAVADOC_TASK_NAME);

		if (FileUtil.hasSourceFiles(javadocTask, _javaSpec)) {
			artifactHandler.add(
				Dependency.ARCHIVES_CONFIGURATION, jarJavadocTask);
		}

		Task jsDocTask = GradleUtil.getTask(
			project, JSDocPlugin.JSDOC_TASK_NAME);

		TaskInputs taskInputs = jsDocTask.getInputs();

		FileCollection fileCollection = taskInputs.getFiles();

		FileTree fileTree = fileCollection.getAsFileTree();

		fileCollection = fileTree.filter(_jsdocSpec);

		if (!fileCollection.isEmpty()) {
			artifactHandler.add(
				Dependency.ARCHIVES_CONFIGURATION, jarJSDocTask);
		}

		Task tldDocTask = GradleUtil.getTask(
			project, TLDDocBuilderPlugin.TLDDOC_TASK_NAME);

		if (FileUtil.hasSourceFiles(tldDocTask, _tldSpec)) {
			artifactHandler.add(
				Dependency.ARCHIVES_CONFIGURATION, jarTLDDocTask);
		}

		if (GradleUtil.hasPlugin(project, WSDDBuilderPlugin.class)) {
			BuildWSDDTask buildWSDDTask = (BuildWSDDTask)GradleUtil.getTask(
				project, WSDDBuilderPlugin.BUILD_WSDD_TASK_NAME);

			if (buildWSDDTask.getEnabled()) {
				Task buildWSDDJarTask = GradleUtil.getTask(
					project, buildWSDDTask.getName() + "Jar");

				artifactHandler.add(
					Dependency.ARCHIVES_CONFIGURATION, buildWSDDJarTask,
					new Closure<Void>(project) {

						@SuppressWarnings("unused")
						public void doCall(
							ArchivePublishArtifact archivePublishArtifact) {

							archivePublishArtifact.setClassifier("wsdd");
						}

					});
			}
		}
	}

	private void _configureBasePlugin(
		BasePluginExtension basePluginExtension, File portalRootDir) {

		if (portalRootDir == null) {
			return;
		}

		File dir = new File(portalRootDir, "tools/sdk/dist");

		DirectoryProperty directoryProperty =
			basePluginExtension.getDistsDirectory();

		directoryProperty.set(dir);

		directoryProperty = basePluginExtension.getLibsDirectory();

		directoryProperty.set(dir);
	}

	private void _configureBundleDefaultInstructions(
		Project project, boolean publishing) {

		LiferayOSGiExtension liferayOSGiExtension = GradleUtil.getExtension(
			project, LiferayOSGiExtension.class);

		Map<String, Object> bundleDefaultInstructions = new HashMap<>();

		bundleDefaultInstructions.put(Constants.BUNDLE_VENDOR, "Liferay, Inc.");
		bundleDefaultInstructions.put(
			Constants.DONOTCOPY,
			"(" + LiferayOSGiExtension.DONOTCOPY_DEFAULT + "|.touch)");
		bundleDefaultInstructions.put(Constants.SOURCES, "false");

		if (publishing) {
			bundleDefaultInstructions.put(
				"Git-Descriptor",
				"${system-allow-fail;git describe --dirty --always}");
			bundleDefaultInstructions.put(
				"Git-SHA", "${system-allow-fail;git rev-list -1 HEAD}");
		}

		File packageJsonFile = project.file("package.json");

		if (packageJsonFile.exists()) {
			bundleDefaultInstructions.put(
				Constants.INCLUDERESOURCE + ".packagejson",
				FileUtil.getRelativePath(project, packageJsonFile));
		}

		liferayOSGiExtension.bundleDefaultInstructions(
			bundleDefaultInstructions);
	}

	private void _configureBundleInstructions(
		Project project, BundleExtension bundleExtension) {

		String projectPath = project.getPath();

		if (projectPath.startsWith(":apps:") ||
			projectPath.startsWith(":dxp:apps:") ||
			projectPath.startsWith(":private:apps:")) {

			String exportPackage = GradleUtil.toString(
				bundleExtension.getInstruction(Constants.EXPORT_PACKAGE));

			if (Validator.isNotNull(exportPackage)) {
				exportPackage = "!com.liferay.*.kernel.*," + exportPackage;

				bundleExtension.instruction(
					Constants.EXPORT_PACKAGE, exportPackage);
			}
		}

		if (!bundleExtension.containsKey(Constants.EXPORT_CONTENTS) &&
			!bundleExtension.containsKey("-check")) {

			bundleExtension.instruction("-check", "EXPORTS");
		}
	}

	private void _configureConfiguration(Configuration configuration) {
		DependencySet dependencySet = configuration.getDependencies();

		dependencySet.withType(
			ExternalDependency.class,
			new Action<ExternalDependency>() {

				@Override
				public void execute(ExternalDependency externalDependency) {
					String version = externalDependency.getVersion();

					if (Validator.isNotNull(version) &&
						version.endsWith(
							GradlePluginsDefaultsUtil.
								SNAPSHOT_VERSION_SUFFIX)) {

						throw new GradleException(
							"Please use a timestamp version for " +
								externalDependency);
					}
				}

			});

		dependencySet.withType(
			ModuleDependency.class,
			new Action<ModuleDependency>() {

				@Override
				public void execute(ModuleDependency moduleDependency) {
					String name = moduleDependency.getName();

					if (name.equals(
							"com.liferay.arquillian.arquillian-container-" +
								"liferay") ||
						name.equals(
							"com.liferay.arquillian.extension.junit.bridge")) {

						moduleDependency.exclude(
							Collections.singletonMap("group", _GROUP_PORTAL));
					}
					else if (name.equals("easyconf")) {
						moduleDependency.exclude(
							Collections.singletonMap("group", "xdoclet"));
						moduleDependency.exclude(
							Collections.singletonMap("group", "xpp3"));
					}
				}

			});
	}

	private void _configureConfigurationDefault(Project project) {
		final Configuration defaultConfiguration = GradleUtil.getConfiguration(
			project, Dependency.DEFAULT_CONFIGURATION);

		Configuration providedConfiguration = GradleUtil.getConfiguration(
			project, JavaPlugin.COMPILE_ONLY_CONFIGURATION_NAME);

		DependencySet dependencySet = providedConfiguration.getDependencies();

		dependencySet.withType(
			ProjectDependency.class,
			new Action<ProjectDependency>() {

				@Override
				public void execute(ProjectDependency projectDependency) {
					defaultConfiguration.exclude(
						Collections.singletonMap(
							"module", projectDependency.getName()));
				}

			});
	}

	private void _configureConfigurationJspC(
		final Project project, LiferayExtension liferayExtension) {

		final Logger logger = project.getLogger();

		final boolean compilingJSP = GradleUtil.hasStartParameterTask(
			project, JspCPlugin.COMPILE_JSP_TASK_NAME);
		final boolean jspPrecompileFromSource = GradleUtil.getProperty(
			project, "jsp.precompile.from.source", true);

		final File taglibDependencyDir = new File(
			liferayExtension.getLiferayHome(), "osgi");
		final File utilTaglibDependencyDir =
			liferayExtension.getAppServerPortalDir();

		GradleInternal gradleInternal = (GradleInternal)project.getGradle();

		ServiceRegistry serviceRegistry = gradleInternal.getServices();

		final ProjectConfigurer projectConfigurer = serviceRegistry.get(
			ProjectConfigurer.class);

		final Configuration configuration = GradleUtil.getConfiguration(
			project, JspCPlugin.CONFIGURATION_NAME);

		DependencySet dependencySet = configuration.getAllDependencies();

		dependencySet.withType(
			ExternalModuleDependency.class,
			new Action<ExternalModuleDependency>() {

				@Override
				public void execute(
					ExternalModuleDependency externalModuleDependency) {

					String group = externalModuleDependency.getGroup();
					String name = externalModuleDependency.getName();

					if (_isTaglibDependency(group, name)) {
						String projectName = name.substring(12);

						projectName = projectName.replace('.', '-');

						projectName = projectName.replaceFirst(
							"exportimport", "export-import");

						Project taglibProject = GradleUtil.getProject(
							project.getRootProject(), projectName);

						if (taglibProject != null) {
							LogLevel logLevel = LogLevel.INFO;

							if (compilingJSP) {
								logLevel = LogLevel.LIFECYCLE;
							}

							logger.log(
								logLevel,
								"Compiling JSP files of {} with {} as " +
									"dependency in place of '{}:{}:{}'",
								project, taglibProject, group, name,
								externalModuleDependency.getVersion());

							projectConfigurer.configure(
								(ProjectInternal)taglibProject);

							GradleUtil.substituteModuleDependencyWithProject(
								configuration, externalModuleDependency,
								taglibProject);
						}
						else if (taglibDependencyDir.exists() &&
								 jspPrecompileFromSource) {

							Map<String, String> args = new HashMap<>();

							args.put("group", group);
							args.put("module", name);

							configuration.exclude(args);
						}
					}
					else if (utilTaglibDependencyDir.exists() &&
							 _isUtilTaglibDependency(group, name)) {

						Map<String, String> args = new HashMap<>();

						args.put("group", group);
						args.put("module", name);

						configuration.exclude(args);
					}
				}

			});

		ResolvableDependencies resolvableDependencies =
			configuration.getIncoming();

		Action<ResolvableDependencies> action =
			new Action<ResolvableDependencies>() {

				@Override
				public void execute(
					ResolvableDependencies resolvableDependencies) {

					for (ExcludeRule excludeRule :
							configuration.getExcludeRules()) {

						String group = excludeRule.getGroup();
						String name = excludeRule.getModule();

						File file = null;

						if (_isTaglibDependency(group, name)) {
							String fileName = name + ".jar";

							try {
								file = FileUtil.findFile(
									taglibDependencyDir, fileName);
							}
							catch (IOException ioException) {
								throw new UncheckedIOException(ioException);
							}

							if (file == null) {
								throw new GradleException(
									"Unable to find " + fileName + " in " +
										taglibDependencyDir);
							}
						}
						else if (_isUtilTaglibDependency(group, name)) {
							file = new File(
								utilTaglibDependencyDir,
								"WEB-INF/shielded-container-lib" +
									"/util-taglib.jar");

							if (!file.exists()) {
								file = new File(
									utilTaglibDependencyDir,
									"WEB-INF/lib/util-taglib.jar");
							}

							if (!file.exists()) {
								throw new GradleException(
									"Unable to find " + file);
							}
						}
						else {
							return;
						}

						if (logger.isLifecycleEnabled()) {
							logger.lifecycle(
								"Compiling JSP files of {} with {} as " +
									"dependency in place of '{}:{}'",
								project, file.getAbsolutePath(), group, name);
						}

						GradleUtil.addDependency(
							project, configuration.getName(), file);
					}
				}

			};

		resolvableDependencies.beforeResolve(action);
	}

	private void _configureConfigurationNoCache(Configuration configuration) {
		ResolutionStrategy resolutionStrategy =
			configuration.getResolutionStrategy();

		resolutionStrategy.cacheChangingModulesFor(0, TimeUnit.SECONDS);
		resolutionStrategy.cacheDynamicVersionsFor(0, TimeUnit.SECONDS);
	}

	private void _configureConfigurations(
		Project project, File appBndFile, LiferayExtension liferayExtension,
		boolean publishing) {

		_configureConfigurationDefault(project);
		_configureConfigurationJspC(project, liferayExtension);

		String projectPath = project.getPath();

		if (projectPath.startsWith(":dxp:apps:osb:")) {
			_configureDependenciesReleaseAPI(
				project, JavaPlugin.API_CONFIGURATION_NAME);
			_configureDependenciesReleaseAPI(
				project, JavaPlugin.COMPILE_CLASSPATH_CONFIGURATION_NAME);
			_configureDependenciesReleaseAPI(
				project, JspCPlugin.CONFIGURATION_NAME);
		}

		if (projectPath.startsWith(":apps:") ||
			projectPath.startsWith(":core:") ||
			projectPath.startsWith(":dxp:apps:") ||
			projectPath.startsWith(":dxp:core:") ||
			projectPath.startsWith(":private:apps:") ||
			projectPath.startsWith(":private:core:")) {

			_configureConfigurationTransitive(
				project, JavaPlugin.API_CONFIGURATION_NAME, false);
			_configureConfigurationTransitive(
				project, JavaPlugin.COMPILE_CLASSPATH_CONFIGURATION_NAME,
				false);

			_configureDependenciesGroupPortal(
				project, appBndFile, JavaPlugin.API_CONFIGURATION_NAME,
				publishing);
			_configureDependenciesGroupPortal(
				project, appBndFile,
				JavaPlugin.COMPILE_CLASSPATH_CONFIGURATION_NAME, publishing);
		}

		_configureDependenciesTransitive(
			project, LiferayOSGiPlugin.COMPILE_INCLUDE_CONFIGURATION_NAME,
			false);

		ConfigurationContainer configurationContainer =
			project.getConfigurations();

		configurationContainer.all(
			new Action<Configuration>() {

				@Override
				public void execute(Configuration configuration) {
					_configureConfiguration(configuration);
				}

			});
	}

	private void _configureConfigurationTest(Project project, String name) {
		final Configuration configuration = GradleUtil.getConfiguration(
			project, name);

		ResolutionStrategy resolutionStrategy =
			configuration.getResolutionStrategy();

		resolutionStrategy.eachDependency(
			new Action<DependencyResolveDetails>() {

				@Override
				public void execute(
					DependencyResolveDetails dependencyResolveDetails) {

					ModuleVersionSelector moduleVersionSelector =
						dependencyResolveDetails.getRequested();

					String target = _getEasyConfDependencyTarget(
						moduleVersionSelector.getGroup(),
						moduleVersionSelector.getName());

					if (Validator.isNotNull(target) &&
						GradleUtil.hasDependency(
							configuration.getAllDependencies(), "easyconf",
							"easyconf")) {

						dependencyResolveDetails.useTarget(target);
					}
				}

				private String _getEasyConfDependencyTarget(
					String group, String name) {

					String target = null;

					if (group.equals("commons-configuration") &&
						name.equals("commons-configuration")) {

						target =
							"commons-configuration:commons-configuration:1.10";
					}
					else if (group.equals("xerces") && name.equals("xerces")) {
						target = "xerces:xercesImpl:2.12.1";
					}
					else if (group.equals("xml-apis") &&
							 name.equals("xml-apis")) {

						target = "xml-apis:xml-apis:1.4.01";
					}

					return target;
				}

			});
	}

	private void _configureConfigurationTransitive(
		Project project, String name, boolean transitive) {

		Configuration configuration = GradleUtil.getConfiguration(
			project, name);

		configuration.setTransitive(transitive);
	}

	private void _configureDependenciesGroupPortal(
		final Project project, final File appBndFile, String configurationName,
		boolean publishing) {

		final Logger logger = project.getLogger();

		final Configuration configuration = GradleUtil.getConfiguration(
			project, configurationName);

		DependencySet dependencySet = configuration.getAllDependencies();

		dependencySet.withType(
			ExternalModuleDependency.class,
			new Action<ExternalModuleDependency>() {

				@Override
				public void execute(
					ExternalModuleDependency externalModuleDependency) {

					String group = externalModuleDependency.getGroup();

					if (!group.equals(_GROUP_PORTAL)) {
						return;
					}

					String version = externalModuleDependency.getVersion();

					if ((version == null) || !version.equals("default")) {
						return;
					}

					String name = externalModuleDependency.getName();

					String newNotation = null;

					String compatVersion = GradleUtil.getProperty(
						project, "build.compat.version." + name, (String)null);

					if (Validator.isNotNull(compatVersion)) {
						boolean fixDeliveryMethodCore = false;

						if (appBndFile != null) {
							Properties properties = GUtil.loadProperties(
								appBndFile);

							String value = properties.getProperty(
								"Liferay-Releng-Fix-Delivery-Method");

							if (Objects.equals(value, "core")) {
								fixDeliveryMethodCore = true;
							}
						}

						if (!fixDeliveryMethodCore) {
							StringBuilder sb = new StringBuilder();

							sb.append(group);
							sb.append(':');
							sb.append(name);
							sb.append(':');
							sb.append(compatVersion);

							newNotation = sb.toString();
						}
					}

					if (Validator.isNull(newNotation) && publishing) {
						String newVersion = GradleUtil.getProperty(
							project, name + ".version", (String)null);

						if (Validator.isNotNull(newVersion)) {
							StringBuilder sb = new StringBuilder();

							sb.append(group);
							sb.append(':');
							sb.append(name);
							sb.append(":(,");

							int x = newVersion.lastIndexOf("-SNAPSHOT");

							if (x != -1) {
								sb.append(newVersion.substring(0, x));
							}
							else {
								sb.append(newVersion);
							}

							sb.append(")");

							newNotation = sb.toString();
						}
					}

					if (Validator.isNull(newNotation)) {
						return;
					}

					StringBuilder sb = new StringBuilder();

					sb.append(group);
					sb.append(':');
					sb.append(name);
					sb.append(':');

					if (Validator.isNotNull(version)) {
						sb.append(version);
					}

					String oldNotation = sb.toString();

					if (logger.isLifecycleEnabled()) {
						logger.lifecycle(
							"Compiling files of {} with '{}' in place of '{}'",
							project, newNotation, oldNotation);
					}

					ResolutionStrategy resolutionStrategy =
						configuration.getResolutionStrategy();

					DependencySubstitutions dependencySubstitutions =
						resolutionStrategy.getDependencySubstitution();

					DependencySubstitutions.Substitution substitution =
						dependencySubstitutions.substitute(
							dependencySubstitutions.module(oldNotation));

					substitution.using(
						dependencySubstitutions.module(newNotation));
				}

			});
	}

	private void _configureDependenciesReleaseAPI(
		final Project project, String configurationName) {

		final Logger logger = project.getLogger();

		final Configuration configuration = GradleUtil.fetchConfiguration(
			project, configurationName);

		if (configuration == null) {
			return;
		}

		DependencySet dependencySet = configuration.getAllDependencies();

		dependencySet.withType(
			ExternalModuleDependency.class,
			new Action<ExternalModuleDependency>() {

				@Override
				public void execute(
					ExternalModuleDependency externalModuleDependency) {

					String group = externalModuleDependency.getGroup();

					if (!group.equals("com.liferay.portal")) {
						return;
					}

					String version = externalModuleDependency.getVersion();

					if (version != null) {
						return;
					}

					String name = externalModuleDependency.getName();

					if (!Objects.equals(name, "release.dxp.api") &&
						!Objects.equals(name, "release.portal.api")) {

						return;
					}

					String releaseAPIVersion = null;

					File rootDir = GradleUtil.getRootDir(
						project.getProjectDir(), "release-api.properties");

					if (rootDir != null) {
						Properties properties = GUtil.loadProperties(
							new File(rootDir, "release-api.properties"));

						releaseAPIVersion = properties.getProperty(
							"release.api.version");
					}

					StringBuilder sb = new StringBuilder();

					sb.append(group);
					sb.append(':');
					sb.append(name);
					sb.append(':');

					if (Validator.isNull(releaseAPIVersion)) {
						sb.append('+');
					}
					else {
						sb.append(releaseAPIVersion);
					}

					String newNotation = sb.toString();

					sb.setLength(0);

					sb.append(group);
					sb.append(':');
					sb.append(name);
					sb.append(':');

					if (Validator.isNotNull(version)) {
						sb.append(version);
					}

					String oldNotation = sb.toString();

					if (logger.isLifecycleEnabled()) {
						logger.lifecycle(
							"Compiling files of {} with '{}' in place of '{}'",
							project, newNotation, oldNotation);
					}

					ResolutionStrategy resolutionStrategy =
						configuration.getResolutionStrategy();

					DependencySubstitutions dependencySubstitutions =
						resolutionStrategy.getDependencySubstitution();

					DependencySubstitutions.Substitution substitution =
						dependencySubstitutions.substitute(
							dependencySubstitutions.module(oldNotation));

					substitution.using(
						dependencySubstitutions.module(newNotation));
				}

			});
	}

	private void _configureDependenciesTransitive(
		Project project, String configurationName, final boolean transitive) {

		Configuration configuration = GradleUtil.getConfiguration(
			project, configurationName);

		DependencySet dependencySet = configuration.getAllDependencies();

		dependencySet.withType(
			ModuleDependency.class,
			new Action<ModuleDependency>() {

				@Override
				public void execute(ModuleDependency moduleDependency) {
					moduleDependency.setTransitive(transitive);
				}

			});
	}

	private void _configureDependencyChecker(Project project) {
		DependencyCheckerExtension dependencyCheckerExtension =
			GradleUtil.getExtension(project, DependencyCheckerExtension.class);

		Map<String, Object> args = new HashMap<>();

		args.put("configuration", SourceFormatterPlugin.CONFIGURATION_NAME);
		args.put("group", _GROUP);
		args.put("maxAge", _PORTAL_TOOL_MAX_AGE);
		args.put("name", _SOURCE_FORMATTER_PORTAL_TOOL_NAME);
		args.put("throwError", Boolean.TRUE);

		dependencyCheckerExtension.maxAge(args);
	}

	private void _configureDeployDir(
		final Project project, final LiferayExtension liferayExtension,
		final boolean deployToAppServerLibs, final boolean deployToTools) {

		liferayExtension.setDeployDir(
			new Callable<File>() {

				@Override
				public File call() throws Exception {
					if (deployToAppServerLibs) {
						return new File(
							liferayExtension.getAppServerPortalDir(),
							"WEB-INF/lib");
					}

					if (deployToTools) {
						return new File(
							liferayExtension.getLiferayHome(),
							"tools/" + project.getName());
					}

					if (FileUtil.exists(project, ".lfrbuild-static")) {
						return new File(
							liferayExtension.getLiferayHome(), "osgi/static");
					}

					return new File(
						liferayExtension.getLiferayHome(), "osgi/portal");
				}

			});
	}

	@SuppressWarnings("serial")
	private void _configureEclipse(Project project) {
		EclipseModel eclipseModel = GradleUtil.getExtension(
			project, EclipseModel.class);

		EclipseClasspath eclipseClasspath = eclipseModel.getClasspath();

		XmlFileContentMerger xmlFileContentMerger = eclipseClasspath.getFile();

		xmlFileContentMerger.whenMerged(
			new Closure<Void>(project) {

				@SuppressWarnings("unused")
				public void doCall(Classpath classpath) {
					for (ClasspathEntry classpathEntry :
							classpath.getEntries()) {

						if (!(classpathEntry instanceof SourceFolder)) {
							continue;
						}

						SourceFolder sourceFolder =
							(SourceFolder)classpathEntry;

						File archetypeResourcesDir = new File(
							sourceFolder.getDir(), "archetype-resources");

						if (archetypeResourcesDir.isDirectory()) {
							List<String> excludes = sourceFolder.getExcludes();

							excludes.add("**/*.java");
						}
					}
				}

			});
	}

	private void _configureEclipse(
		Project project, Configuration portalConfiguration,
		Configuration portalTestConfiguration) {

		EclipseModel eclipseModel = GradleUtil.getExtension(
			project, EclipseModel.class);

		EclipseClasspath eclipseClasspath = eclipseModel.getClasspath();

		Collection<Configuration> plusConfigurations =
			eclipseClasspath.getPlusConfigurations();

		plusConfigurations.add(portalConfiguration);
		plusConfigurations.add(portalTestConfiguration);
	}

	private void _configureIdea(
		Project project, Configuration portalConfiguration,
		Configuration portalTestConfiguration) {

		IdeaModel ideaModel = GradleUtil.getExtension(project, IdeaModel.class);

		IdeaModule ideaModule = ideaModel.getModule();

		Map<String, Map<String, Collection<Configuration>>> scopes =
			ideaModule.getScopes();

		Map<String, Collection<Configuration>> testScope = scopes.get("TEST");

		Collection<Configuration> plusConfigurations = testScope.get("plus");

		plusConfigurations.add(portalConfiguration);
		plusConfigurations.add(portalTestConfiguration);
	}

	private void _configureJavaPlugin(Project project, File portalRootDir) {
		JavaPluginConvention javaPluginConvention = GradleUtil.getConvention(
			project, JavaPluginConvention.class);

		String javaVersionOverride = GradleUtil.getProperty(
			project, "java.version.override", (String)null);
		String javaVersionOverrideIncludeDirs = GradleUtil.getProperty(
			project, "java.version.override.include.dirs", (String)null);

		if (Validator.isNotNull(javaVersionOverride) &&
			_containsProject(
				project, javaVersionOverrideIncludeDirs, portalRootDir)) {

			javaPluginConvention.setSourceCompatibility(javaVersionOverride);
			javaPluginConvention.setTargetCompatibility(javaVersionOverride);
		}
		else if (project.hasProperty("java.version.source.compatibility") ||
				 project.hasProperty("java.version.target.compatibility")) {

			javaPluginConvention.setSourceCompatibility(
				GradleUtil.getProperty(
					project, "java.version.source.compatibility",
					(String)null));
			javaPluginConvention.setTargetCompatibility(
				GradleUtil.getProperty(
					project, "java.version.target.compatibility",
					(String)null));
		}
		else {
			javaPluginConvention.setSourceCompatibility(_JAVA_VERSION);
			javaPluginConvention.setTargetCompatibility(_JAVA_VERSION);
		}

		File testResultsDir = project.file("test-results/unit");

		javaPluginConvention.setTestResultsDirName(
			FileUtil.relativize(testResultsDir, project.getBuildDir()));
	}

	private void _configureLocalPortalTool(
		Project project, File portalRootDir, String configurationName,
		String portalToolName) {

		if (GradleUtil.getProperty(
				project, portalToolName + ".ignore.local", true)) {

			return;
		}

		if (portalToolName.startsWith("com.liferay.portal.tools.") &&
			portalToolName.endsWith(".builder")) {

			String projectName = portalToolName.substring(12);

			projectName = projectName.replace('.', '-');

			File dir = new File(portalRootDir, "modules/util/" + projectName);

			if (!dir.exists()) {
				return;
			}

			int length = portalToolName.length();

			String taskNameSuffix = portalToolName.substring(25, length - 8);

			String buildTaskName = "build" + taskNameSuffix;

			Gradle gradle = project.getGradle();

			Set<String> taskNames = GradleUtil.getTaskNames(
				project, gradle.getStartParameter());

			Stream<String> taskNamesStream = taskNames.stream();

			if (!taskNamesStream.anyMatch(buildTaskName::equalsIgnoreCase)) {
				return;
			}
		}

		String portalRootDirValue = System.getProperty("portal.root.dir");

		if (Validator.isNotNull(portalRootDirValue)) {
			portalRootDir = new File(portalRootDirValue);
		}

		Logger logger = project.getLogger();

		File dir = new File(
			portalRootDir, "tools/sdk/dependencies/" + portalToolName + "/lib");

		if (!dir.exists()) {
			if (logger.isWarnEnabled()) {
				logger.warn(
					"Unable to find {}, using default version of {}", dir,
					portalToolName);
			}

			return;
		}

		if (logger.isInfoEnabled()) {
			logger.info("Using local portal tool: {}", dir);
		}

		Configuration configuration = GradleUtil.getConfiguration(
			project, configurationName);

		Map<String, String> args = new HashMap<>();

		args.put("group", PortalTools.GROUP);
		args.put("module", portalToolName);

		configuration.exclude(args);

		FileTree fileTree = FileUtil.getJarsFileTree(project, dir);

		GradleUtil.addDependency(project, configuration.getName(), fileTree);
	}

	private void _configurePmd(Project project) {
		String ruleSet;

		try {
			ruleSet = FileUtil.read(
				"com/liferay/gradle/plugins/defaults/dependencies" +
					"/standard-rules.xml");
		}
		catch (IOException ioException) {
			throw new UncheckedIOException(ioException);
		}

		PmdExtension pmdExtension = GradleUtil.getExtension(
			project, PmdExtension.class);

		ResourceHandler resourceHandler = project.getResources();

		TextResourceFactory textResourceFactory = resourceHandler.getText();

		pmdExtension.setRuleSetConfig(textResourceFactory.fromString(ruleSet));

		pmdExtension.setRuleSets(Collections.emptyList());
	}

	private void _configureProject(Project project) {
		project.setGroup(GradleUtil.getProjectGroup(project, _GROUP));
	}

	private void _configureProjectBndProperties(
		Project project, LiferayExtension liferayExtension) {

		File appServerPortalDir = liferayExtension.getAppServerPortalDir();

		GradleUtil.setProperty(
			project, "app.server.portal.dir",
			project.relativePath(appServerPortalDir));

		File appServerLibPortalDir = new File(
			appServerPortalDir, "WEB-INF/lib");

		GradleUtil.setProperty(
			project, "app.server.lib.portal.dir",
			project.relativePath(appServerLibPortalDir));
	}

	private void _configurePublishing(
		final Project project, Jar jarJSDocTask, Jar jarJSPTask,
		Jar jarJavadocTask, Jar jarSourcesTask, Jar jarTLDDocTask) {

		PublishingExtension publishingExtension = GradleUtil.getExtension(
			project, PublishingExtension.class);

		publishingExtension.publications(
			new Action<PublicationContainer>() {

				@Override
				public void execute(PublicationContainer publicationContainer) {
					MavenPublication mavenPublication =
						publicationContainer.maybeCreate(
							"maven", MavenPublication.class);

					SoftwareComponentContainer softwareComponentContainer =
						project.getComponents();

					SoftwareComponent softwareComponent =
						softwareComponentContainer.findByName("java");

					mavenPublication.from(softwareComponent);

					mavenPublication.setArtifactId(
						GradleUtil.getArchivesBaseName(project));
					mavenPublication.setGroupId(
						String.valueOf(project.getGroup()));

					if (!GradlePluginsDefaultsUtil.isSnapshot(
							project, _SNAPSHOT_PROPERTY_NAMES)) {

						SourceSet sourceSet = GradleUtil.getSourceSet(
							project, SourceSet.MAIN_SOURCE_SET_NAME);

						if (FileUtil.hasFiles(
								sourceSet.getResources(), _jspSpec)) {

							mavenPublication.artifact(jarJSPTask);
						}
					}

					Spec<File> spec = new Spec<File>() {

						@Override
						public boolean isSatisfiedBy(File file) {
							String fileName = file.getName();

							return !fileName.equals("MANIFEST.MF");
						}

					};

					if (FileUtil.hasSourceFiles(jarSourcesTask, spec)) {
						mavenPublication.artifact(jarSourcesTask);
					}

					Task javadocTask = GradleUtil.getTask(
						project, JavaPlugin.JAVADOC_TASK_NAME);

					if (FileUtil.hasSourceFiles(javadocTask, _javaSpec)) {
						mavenPublication.artifact(jarJavadocTask);
					}

					Task jsDocTask = GradleUtil.getTask(
						project, JSDocPlugin.JSDOC_TASK_NAME);

					TaskInputs taskInputs = jsDocTask.getInputs();

					FileCollection fileCollection = taskInputs.getFiles();

					FileTree fileTree = fileCollection.getAsFileTree();

					fileCollection = fileTree.filter(_jsdocSpec);

					if (!fileCollection.isEmpty()) {
						mavenPublication.artifact(jarJSDocTask);
					}

					Task tldDocTask = GradleUtil.getTask(
						project, TLDDocBuilderPlugin.TLDDOC_TASK_NAME);

					if (FileUtil.hasSourceFiles(tldDocTask, _tldSpec)) {
						mavenPublication.artifact(jarTLDDocTask);
					}

					if (GradleUtil.hasPlugin(
							project, WSDDBuilderPlugin.class)) {

						BuildWSDDTask buildWSDDTask =
							(BuildWSDDTask)GradleUtil.getTask(
								project,
								WSDDBuilderPlugin.BUILD_WSDD_TASK_NAME);

						if (buildWSDDTask.getEnabled()) {
							Task buildWSDDJarTask = GradleUtil.getTask(
								project, buildWSDDTask.getName() + "Jar");

							mavenPublication.artifact(
								buildWSDDJarTask,
								new Action<MavenArtifact>() {

									@Override
									public void execute(
										MavenArtifact mavenArtifact) {

										mavenArtifact.setClassifier("wsdd");
									}

								});
						}
					}
				}

			});
	}

	private void _configureSourceSetClassesDir(
		Project project, SourceSet sourceSet, String classesDirName) {

		if (FileUtil.isChild(
				FileUtil.getJavaClassesDir(sourceSet), project.getBuildDir())) {

			File javaClassesDir = project.file(classesDirName);

			SourceDirectorySet javaSourceDirectorySet = sourceSet.getJava();

			DirectoryProperty directoryProperty =
				javaSourceDirectorySet.getDestinationDirectory();

			directoryProperty.set(javaClassesDir);

			SourceSetOutput sourceSetOutput = sourceSet.getOutput();

			sourceSetOutput.setResourcesDir(javaClassesDir);
		}
	}

	private void _configureSourceSetMain(Project project) {
		SourceSet sourceSet = GradleUtil.getSourceSet(
			project, SourceSet.MAIN_SOURCE_SET_NAME);

		_configureSourceSetClassesDir(project, sourceSet, "classes");
	}

	private void _configureSourceSetTest(
		Project project, Configuration portalConfiguration,
		Configuration portalTestConfiguration,
		Configuration portalTestSnapshotConfiguration) {

		SourceSet sourceSet = GradleUtil.getSourceSet(
			project, SourceSet.TEST_SOURCE_SET_NAME);

		_configureSourceSetClassesDir(project, sourceSet, "test-classes/unit");

		Configuration compileClasspathConfiguration =
			GradleUtil.getConfiguration(
				project, JavaPlugin.COMPILE_CLASSPATH_CONFIGURATION_NAME);

		sourceSet.setCompileClasspath(
			FileUtil.join(
				compileClasspathConfiguration, sourceSet.getCompileClasspath(),
				portalTestConfiguration));
		sourceSet.setRuntimeClasspath(
			FileUtil.join(
				portalTestSnapshotConfiguration, compileClasspathConfiguration,
				portalConfiguration, sourceSet.getRuntimeClasspath(),
				portalTestConfiguration));
	}

	private void _configureSourceSetTestIntegration(
		Project project, Configuration portalConfiguration,
		Configuration portalTestConfiguration) {

		SourceSet sourceSet = GradleUtil.getSourceSet(
			project,
			TestIntegrationBasePlugin.TEST_INTEGRATION_SOURCE_SET_NAME);

		_configureSourceSetClassesDir(
			project, sourceSet, "test-classes/integration");

		Configuration compileOnlyConfiguration = GradleUtil.getConfiguration(
			project, JavaPlugin.COMPILE_ONLY_CONFIGURATION_NAME);

		sourceSet.setCompileClasspath(
			FileUtil.join(
				portalConfiguration, compileOnlyConfiguration,
				sourceSet.getCompileClasspath(), portalTestConfiguration));
		sourceSet.setRuntimeClasspath(
			FileUtil.join(
				portalConfiguration, compileOnlyConfiguration,
				sourceSet.getRuntimeClasspath(), portalTestConfiguration));
	}

	private void _configureTaskBaselineSyncReleaseVersions(
		Task task, final File versionOverrideFile) {

		Action<Task> action = new Action<Task>() {

			@Override
			public void execute(Task task) {
				try {
					_execute(task.getProject());
				}
				catch (IOException ioException) {
					throw new UncheckedIOException(ioException);
				}
			}

			private void _execute(Project project) throws IOException {
				boolean hasPackageInfoFiles = _hasPackageInfoFiles(project);

				if (versionOverrideFile != null) {

					// Get versions fixed by baseline

					Properties versions = _getVersions(
						project.getProjectDir(), null);

					// Reset to committed versions

					if (hasPackageInfoFiles) {
						GitUtil.executeGit(
							project, "checkout", "--", "bnd.bnd",
							"**/packageinfo");
					}
					else {
						GitUtil.executeGit(
							project, "checkout", "--", "bnd.bnd");
					}

					// Keep only the version overrides that are different
					// from the committed versions

					Properties committedVersions = _getVersions(
						project.getProjectDir(), null);

					_removeDuplicates(versions, committedVersions);

					// Re-add dependency version overrides

					if (versionOverrideFile.exists()) {
						Properties versionOverrides = GUtil.loadProperties(
							versionOverrideFile);

						for (String key :
								versionOverrides.stringPropertyNames()) {

							if (key.indexOf(_DEPENDENCY_KEY_SEPARATOR) == -1) {
								continue;
							}

							versions.setProperty(
								key, versionOverrides.getProperty(key));
						}
					}

					// Save version override file

					boolean addVersionOverrideFile = false;

					String versionOverrideRelativePath = project.relativePath(
						versionOverrideFile);

					if (Validator.isNotNull(
							GitUtil.getGitResult(
								project, "ls-files",
								versionOverrideRelativePath))) {

						addVersionOverrideFile = true;
					}

					_saveVersions(
						project.getProjectDir(), versions, versionOverrideFile);

					if (versionOverrideFile.exists()) {
						addVersionOverrideFile = true;
					}

					if (addVersionOverrideFile) {
						GitUtil.executeGit(
							project, "add", versionOverrideRelativePath);
					}
				}
				else if (hasPackageInfoFiles) {
					GitUtil.executeGit(
						project, "add", "bnd.bnd", "**/packageinfo");
				}
				else {
					GitUtil.executeGit(project, "add", "bnd.bnd");
				}

				String message = project.getName() + " packageinfo";

				GitUtil.commit(project, message, true);
			}

			private boolean _hasPackageInfoFiles(Project project) {
				Map<String, Object> args = new HashMap<>();

				args.put("dir", project.getProjectDir());
				args.put("include", "src/main/resources/**/packageinfo");

				FileTree fileTree = project.fileTree(args);

				return !fileTree.isEmpty();
			}

			private void _removeDuplicates(
				Properties properties, Properties otherProperties) {

				Set<Map.Entry<Object, Object>> entrySet = properties.entrySet();

				Iterator<Map.Entry<Object, Object>> iterator =
					entrySet.iterator();

				while (iterator.hasNext()) {
					Map.Entry<Object, Object> entry = iterator.next();

					String key = (String)entry.getKey();

					if (Objects.equals(
							entry.getValue(),
							otherProperties.getProperty(key))) {

						iterator.remove();
					}
				}
			}

		};

		task.doLast(action);

		if (task instanceof VerificationTask) {
			VerificationTask verificationTask = (VerificationTask)task;

			verificationTask.setIgnoreFailures(true);
		}
	}

	private void _configureTaskBuildREST(final Project project) {
		BuildRESTTask buildRESTTask = (BuildRESTTask)GradleUtil.getTask(
			project, RESTBuilderPlugin.BUILD_REST_TASK_NAME);

		buildRESTTask.setCopyrightFile(
			new Callable<File>() {

				@Override
				public File call() throws Exception {
					File copyrightDir = new File(
						project.getBuildDir(), "/copyright");

					Files.createDirectories(copyrightDir.toPath());

					File copyrightFile = new File(
						copyrightDir, "copyright.txt");

					String copyright = CopyrightUtil.getCopyright(
						project.getProjectDir());

					Files.write(
						copyrightFile.toPath(),
						copyright.getBytes(StandardCharsets.UTF_8));

					return copyrightFile;
				}

			});
	}

	private void _configureTaskBuildWSDD(final Project project) {
		BuildWSDDTask buildWSDDTask = (BuildWSDDTask)GradleUtil.getTask(
			project, WSDDBuilderPlugin.BUILD_WSDD_TASK_NAME);

		buildWSDDTask.setOutputDir(
			new Callable<File>() {

				@Override
				public File call() throws Exception {
					File dir = new File(project.getBuildDir(), "wsdd/output");

					dir.mkdirs();

					return dir;
				}

			});

		buildWSDDTask.setServerConfigFile(
			new Callable<File>() {

				@Override
				public File call() throws Exception {
					return new File(
						project.getBuildDir(),
						"wsdd/" + WSDDBuilderArgs.SERVER_CONFIG_FILE_NAME);
				}

			});

		boolean remoteServices = false;

		try {
			remoteServices = _hasRemoteServices(buildWSDDTask);
		}
		catch (Exception exception) {
			throw new GradleException(
				"Unable to read " + buildWSDDTask.getInputFile(), exception);
		}

		if (!remoteServices) {
			buildWSDDTask.setEnabled(false);
			buildWSDDTask.setFinalizedBy(Collections.emptySet());
		}
	}

	private void _configureTaskCheckOSGiBundleState(
		CheckOSGiBundleStateTask checkOSGiBundleState,
		final LiferayExtension liferayExtension) {

		checkOSGiBundleState.setJmxPort(
			new Callable<Integer>() {

				@Override
				public Integer call() throws Exception {
					return liferayExtension.getJmxRemotePort();
				}

			});
	}

	private void _configureTaskCompileJSP(
		Project project, Jar jarJSPsTask, LiferayExtension liferayExtension) {

		boolean compileJspInclude = GradleUtil.getProperty(
			project, JspCDefaultsPlugin.COMPILE_JSP_INCLUDE_PROPERTY_NAME,
			false);

		if (!compileJspInclude) {
			return;
		}

		JavaCompile javaCompile = (JavaCompile)GradleUtil.getTask(
			project, JspCPlugin.COMPILE_JSP_TASK_NAME);

		Properties artifactProperties = null;

		TaskContainer taskContainer = project.getTasks();

		WritePropertiesTask recordArtifactTask =
			(WritePropertiesTask)taskContainer.findByName(
				LiferayRelengPlugin.RECORD_ARTIFACT_TASK_NAME);

		if (recordArtifactTask != null) {
			File artifactPropertiesFile = recordArtifactTask.getOutputFile();

			if (artifactPropertiesFile.exists()) {
				artifactProperties = GUtil.loadProperties(
					artifactPropertiesFile);
			}
		}

		boolean jspPrecompileFromSource = GradleUtil.getProperty(
			project, "jsp.precompile.from.source", true);

		if (!jspPrecompileFromSource && (artifactProperties != null)) {
			Task generateJSPJavaTask = GradleUtil.getTask(
				project, JspCPlugin.GENERATE_JSP_JAVA_TASK_NAME);

			generateJSPJavaTask.setEnabled(false);

			Copy copy = _addTaskDownloadCompiledJSP(
				javaCompile, jarJSPsTask, artifactProperties, liferayExtension);

			if (copy != null) {
				javaCompile.setActions(Collections.emptyList());
				javaCompile.setDependsOn(Collections.singleton(copy));
			}

			_configureTaskGenerateJSPJava(project, liferayExtension);
		}
	}

	private void _configureTaskDeploy(Project project, Copy deployConfigsTask) {
		final Task deployTask = GradleUtil.getTask(
			project, LiferayBasePlugin.DEPLOY_TASK_NAME);

		String projectName = project.getName();

		if (projectName.endsWith("-demo")) {
			_configureTaskDeployDemo(project, deployTask);
		}

		deployTask.finalizedBy(deployConfigsTask);

		GradleUtil.withPlugin(
			project, WSDDBuilderPlugin.class,
			new Action<WSDDBuilderPlugin>() {

				@Override
				public void execute(WSDDBuilderPlugin wsddBuilderPlugin) {
					if (FileUtil.exists(
							deployTask.getProject(), ".lfrbuild-deploy-wsdd")) {

						deployTask.dependsOn(
							WSDDBuilderPlugin.BUILD_WSDD_TASK_NAME);
					}
				}

			});
	}

	private void _configureTaskDeployDemo(
		Project project, final Task deployTask) {

		project.afterEvaluate(
			new Action<Project>() {

				@Override
				public void execute(Project project) {
					Configuration configuration = GradleUtil.fetchConfiguration(
						project, JavaPlugin.COMPILE_ONLY_CONFIGURATION_NAME);

					if (configuration == null) {
						return;
					}

					DependencySet dependencySet =
						configuration.getAllDependencies();

					for (ProjectDependency projectDependency :
							dependencySet.withType(ProjectDependency.class)) {

						Project dependencyProject =
							projectDependency.getDependencyProject();

						String name = dependencyProject.getName();

						if (!name.endsWith("-demo-data-creator-api")) {
							continue;
						}

						String path = dependencyProject.getPath();

						deployTask.finalizedBy(
							path + ':' + LiferayBasePlugin.DEPLOY_TASK_NAME);

						dependencyProject = project.findProject(
							path.substring(0, path.length() - 3) + "impl");

						if (dependencyProject != null) {
							deployTask.finalizedBy(
								dependencyProject.getPath() + ':' +
									LiferayBasePlugin.DEPLOY_TASK_NAME);
						}
					}
				}

			});
	}

	private void _configureTaskGenerateJSPJava(
		Project project, LiferayExtension liferayExtension) {

		boolean compileJspInclude = GradleUtil.getProperty(
			project, JspCDefaultsPlugin.COMPILE_JSP_INCLUDE_PROPERTY_NAME,
			false);

		if (!compileJspInclude) {
			return;
		}

		Task generateJSPJavaTask = GradleUtil.getTask(
			project, JspCPlugin.GENERATE_JSP_JAVA_TASK_NAME);

		String dirName = null;

		TaskContainer taskContainer = project.getTasks();

		WritePropertiesTask recordArtifactTask =
			(WritePropertiesTask)taskContainer.findByName(
				LiferayRelengPlugin.RECORD_ARTIFACT_TASK_NAME);

		if (recordArtifactTask != null) {
			String artifactURL = null;

			File artifactPropertiesFile = recordArtifactTask.getOutputFile();

			if (artifactPropertiesFile.exists()) {
				Properties artifactProperties = GUtil.loadProperties(
					artifactPropertiesFile);

				artifactURL = artifactProperties.getProperty("artifact.url");
			}

			if (Validator.isNotNull(artifactURL)) {
				int index = artifactURL.lastIndexOf('/');

				dirName = artifactURL.substring(
					index + 1, artifactURL.length() - 4);
			}
		}

		boolean jspPrecompileFromSource = GradleUtil.getProperty(
			project, "jsp.precompile.from.source", true);

		if (Validator.isNull(dirName) || jspPrecompileFromSource) {
			dirName =
				GradleUtil.getArchivesBaseName(project) + "-" +
					project.getVersion();
		}

		final File jspPrecompileDir = new File(
			liferayExtension.getLiferayHome(), "work/" + dirName);

		Action<Task> taskAction = new Action<Task>() {

			@Override
			public void execute(Task task) {
				final CompileJSPTask compileJSPTask = (CompileJSPTask)task;

				Action<CopySpec> copySpecAction = new Action<CopySpec>() {

					@Override
					public void execute(CopySpec copySpec) {
						copySpec.from(compileJSPTask.getDestinationDir());
						copySpec.into(jspPrecompileDir);
					}

				};

				project.copy(copySpecAction);
			}

		};

		generateJSPJavaTask.doLast(taskAction);
	}

	private void _configureTaskGeneratePomFileForMavenPublication(
		Project project, GenerateMavenPom generateMavenPom) {

		final String artifactId = GradleUtil.getArchivesBaseName(project);
		final String groupId = String.valueOf(project.getGroup());

		StringBuilder sb = new StringBuilder();

		sb.append(
			FileUtil.getJavaClassesDir(
				GradleUtil.getSourceSet(
					project, SourceSet.MAIN_SOURCE_SET_NAME)));
		sb.append("/META-INF/maven/");
		sb.append(groupId);
		sb.append('/');
		sb.append(artifactId);

		final String dirName = sb.toString();

		generateMavenPom.setDestination(new File(dirName, "pom.xml"));

		generateMavenPom.doLast(
			new Action<Task>() {

				@Override
				public void execute(Task task) {
					File file = new File(dirName, "pom.properties");

					Properties properties = new Properties();

					properties.setProperty("artifactId", artifactId);
					properties.setProperty("groupId", groupId);
					properties.setProperty(
						"version", String.valueOf(project.getVersion()));

					FileUtil.writeProperties(file, properties);
				}

			});
	}

	private void _configureTaskJar(Jar jar, boolean testProject) {
		if (testProject) {
			jar.dependsOn(JavaPlugin.TEST_CLASSES_TASK_NAME);

			SourceSet sourceSet = GradleUtil.getSourceSet(
				jar.getProject(),
				TestIntegrationBasePlugin.TEST_INTEGRATION_SOURCE_SET_NAME);

			jar.dependsOn(sourceSet.getClassesTaskName());
		}

		jar.setDuplicatesStrategy(DuplicatesStrategy.EXCLUDE);
	}

	private void _configureTaskJarSources(final Jar jarSourcesTask) {
		final Project project = jarSourcesTask.getProject();

		TaskContainer taskContainer = project.getTasks();

		taskContainer.withType(
			PatchTask.class,
			new Action<PatchTask>() {

				@Override
				@SuppressWarnings("serial")
				public void execute(final PatchTask patchTask) {
					jarSourcesTask.from(
						new Callable<FileCollection>() {

							@Override
							public FileCollection call() throws Exception {
								return project.zipTree(
									patchTask.getOriginalLibSrcFile());
							}

						},
						new Closure<Void>(project) {

							@SuppressWarnings("unused")
							public void doCall(CopySpec copySpec) {
								String originalLibSrcDirName =
									patchTask.getOriginalLibSrcDirName();

								if (originalLibSrcDirName.equals(".")) {
									return;
								}

								Map<Object, Object> leadingPathReplacementsMap =
									new HashMap<>();

								leadingPathReplacementsMap.put(
									originalLibSrcDirName, "");

								copySpec.eachFile(
									new ReplaceLeadingPathAction(
										leadingPathReplacementsMap));

								copySpec.include(originalLibSrcDirName + "/");
								copySpec.setIncludeEmptyDirs(false);
							}

						});

					jarSourcesTask.from(
						new Callable<File>() {

							@Override
							public File call() throws Exception {
								return patchTask.getPatchesDir();
							}

						},
						new Closure<Void>(project) {

							@SuppressWarnings("unused")
							public void doCall(CopySpec copySpec) {
								copySpec.into("META-INF/patches");
							}

						});
				}

			});
	}

	private void _configureTaskJavaCompile(JavaCompile javaCompile) {
		CompileOptions compileOptions = javaCompile.getOptions();

		String lintArguments = GradleUtil.getTaskPrefixedProperty(
			javaCompile, "lint");

		if (Validator.isNotNull(lintArguments)) {
			List<String> compilerArgs = compileOptions.getCompilerArgs();

			for (String lintArgument : lintArguments.split(",")) {
				compilerArgs.add("-Xlint:" + lintArgument);
			}
		}

		compileOptions.setEncoding(StandardCharsets.UTF_8.name());
		compileOptions.setWarnings(false);
	}

	private void _configureTaskJavadoc(
		Project project, BundleExtension bundleExtension) {

		Javadoc javadoc = (Javadoc)GradleUtil.getTask(
			project, JavaPlugin.JAVADOC_TASK_NAME);

		_configureTaskJavadocFilter(bundleExtension, javadoc);
		_configureTaskJavadocOptions(javadoc);
		_configureTaskJavadocTitle(bundleExtension, javadoc);

		JavaVersion javaVersion = JavaVersion.current();

		if (javaVersion.isJava8Compatible()) {
			CoreJavadocOptions coreJavadocOptions =
				(CoreJavadocOptions)javadoc.getOptions();

			coreJavadocOptions.addStringOption("Xdoclint:none", "-quiet");
		}
	}

	private void _configureTaskJavadocFilter(
		BundleExtension bundleExtension, Javadoc javadoc) {

		String exportPackage = bundleExtension.getInstruction(
			Constants.EXPORT_PACKAGE);

		if (Validator.isNull(exportPackage)) {
			javadoc.exclude("**/");

			return;
		}

		String[] exportPackageArray = exportPackage.split(",");

		for (String pattern : exportPackageArray) {
			pattern = pattern.trim();

			boolean excludePattern = false;

			int start = 0;

			if (pattern.startsWith("!")) {
				excludePattern = true;

				start = 1;
			}

			int end = pattern.indexOf(';');

			if (end == -1) {
				end = pattern.length();
			}

			pattern = pattern.substring(start, end);

			pattern = "**/" + pattern.replace('.', '/');

			if (pattern.endsWith("/*")) {
				pattern = pattern.substring(0, pattern.length() - 1);
			}
			else {
				pattern += "/*";
			}

			if (excludePattern) {
				javadoc.exclude(pattern);
			}
			else {
				javadoc.include(pattern);
			}
		}
	}

	private void _configureTaskJavadocOptions(Javadoc javadoc) {
		StandardJavadocDocletOptions standardJavadocDocletOptions =
			(StandardJavadocDocletOptions)javadoc.getOptions();

		standardJavadocDocletOptions.setEncoding(StandardCharsets.UTF_8.name());

		Project project = javadoc.getProject();

		File overviewFile = null;

		SourceSet sourceSet = GradleUtil.getSourceSet(
			project, SourceSet.MAIN_SOURCE_SET_NAME);

		SourceDirectorySet sourceDirectorySet = sourceSet.getJava();

		for (File dir : sourceDirectorySet.getSrcDirs()) {
			File file = new File(dir, "overview.html");

			if (file.exists()) {
				overviewFile = file;

				break;
			}
		}

		if (overviewFile != null) {
			standardJavadocDocletOptions.setOverview(
				project.relativePath(overviewFile));
		}

		standardJavadocDocletOptions.tags("generated");
	}

	private void _configureTaskJavadocTitle(
		BundleExtension bundleExtension, Javadoc javadoc) {

		Project project = javadoc.getProject();

		StringBuilder sb = new StringBuilder();

		sb.append("Module ");
		sb.append(GradleUtil.getArchivesBaseName(project));
		sb.append(' ');
		sb.append(project.getVersion());
		sb.append(" - ");
		sb.append(bundleExtension.getInstruction(Constants.BUNDLE_NAME));

		javadoc.setTitle(sb.toString());
	}

	private void _configureTaskPmd(Pmd pmd) {
		pmd.setClasspath(null);
	}

	private void _configureTaskPublish(
		Project project, boolean testProject,
		ReplaceRegexTask updateFileVersionsTask,
		ReplaceRegexTask updateVersionTask) {

		Task publishTask = GradleUtil.getTask(
			project, PublishingPlugin.PUBLISH_LIFECYCLE_TASK_NAME);

		if (testProject) {
			publishTask.setDependsOn(Collections.emptySet());
			publishTask.setEnabled(false);
			publishTask.setFinalizedBy(Collections.emptySet());

			return;
		}

		TaskContainer taskContainer = project.getTasks();

		TaskCollection<PublishNodeModuleTask> publishNodeModuleTasks =
			taskContainer.withType(PublishNodeModuleTask.class);

		publishTask.dependsOn(publishNodeModuleTasks);

		if ((GradleUtil.getRootDir(project, ".lfrbuild-master-only") != null) &&
			!GradlePluginsDefaultsUtil.isSnapshot(project)) {

			publishTask.finalizedBy(updateFileVersionsTask);
		}

		if (!GradlePluginsDefaultsUtil.isSnapshot(project)) {
			publishTask.finalizedBy(updateVersionTask);
		}
	}

	private void _configureTaskReplaceRegexJSMatches(
		ReplaceRegexTask replaceRegexTask) {

		Project project = replaceRegexTask.getProject();

		for (String fileName :
				GradlePluginsDefaultsUtil.JSON_VERSION_FILE_NAMES) {

			File file = project.file(fileName);

			if (file.exists()) {
				replaceRegexTask.match(
					GradlePluginsDefaultsUtil.jsonVersionPattern.pattern(),
					file);
			}
		}
	}

	private void _configureTasksCheckOSGiBundleState(
		Project project, final LiferayExtension liferayExtension) {

		TaskContainer taskContainer = project.getTasks();

		taskContainer.withType(
			CheckOSGiBundleStateTask.class,
			new Action<CheckOSGiBundleStateTask>() {

				@Override
				public void execute(
					CheckOSGiBundleStateTask checkOSGiBundleState) {

					_configureTaskCheckOSGiBundleState(
						checkOSGiBundleState, liferayExtension);
				}

			});
	}

	private void _configureTasksEnabledIfStaleSnapshot(
		Project project, boolean testProject, String... taskNames) {

		boolean snapshotIfStale = false;

		if (project.hasProperty(SNAPSHOT_IF_STALE_PROPERTY_NAME)) {
			snapshotIfStale = GradleUtil.getProperty(
				project, SNAPSHOT_IF_STALE_PROPERTY_NAME, true);
		}

		if (!snapshotIfStale || (!testProject && _isSnapshotStale(project))) {
			return;
		}

		for (String taskName : taskNames) {
			Task task = GradleUtil.getTask(project, taskName);

			task.setDependsOn(Collections.emptySet());
			task.setEnabled(false);
			task.setFinalizedBy(Collections.emptySet());
		}
	}

	private void _configureTasksJavaCompile(Project project) {
		TaskContainer taskContainer = project.getTasks();

		taskContainer.withType(
			JavaCompile.class,
			new Action<JavaCompile>() {

				@Override
				public void execute(JavaCompile javaCompile) {
					_configureTaskJavaCompile(javaCompile);
				}

			});
	}

	private void _configureTasksJspC(
		Project project, BundleExtension bundleExtension) {

		String fragmentHost = bundleExtension.getInstruction(
			Constants.FRAGMENT_HOST);

		if (Validator.isNotNull(fragmentHost)) {
			Task compileJSPTask = GradleUtil.getTask(
				project, JspCPlugin.COMPILE_JSP_TASK_NAME);

			compileJSPTask.setEnabled(false);

			Task generateJSPJavaTask = GradleUtil.getTask(
				project, JspCPlugin.GENERATE_JSP_JAVA_TASK_NAME);

			generateJSPJavaTask.setEnabled(false);
		}
	}

	private void _configureTasksPmd(Project project) {
		TaskContainer taskContainer = project.getTasks();

		taskContainer.withType(
			Pmd.class,
			new Action<Pmd>() {

				@Override
				public void execute(Pmd pmd) {
					_configureTaskPmd(pmd);
				}

			});
	}

	private void _configureTaskSpotBugs(SpotBugsTask spotBugsTask) {
		Property<String> maxHeapSizeProperty = spotBugsTask.getMaxHeapSize();

		maxHeapSizeProperty.set("3g");

		Property<Boolean> showProgressProperty = spotBugsTask.getShowProgress();

		showProgressProperty.set(true);

		String name = spotBugsTask.getName();
		Project project = spotBugsTask.getProject();

		if (name.startsWith("spotbugs")) {
			name = GUtil.toLowerCamelCase(name.substring(8));

			JavaPluginConvention javaPluginConvention =
				GradleUtil.getConvention(project, JavaPluginConvention.class);

			SourceSetContainer sourceSetContainer =
				javaPluginConvention.getSourceSets();

			SourceSet sourceSet = sourceSetContainer.findByName(name);

			if (sourceSet != null) {
				ConfigurableFileTree configurableFileTree = project.fileTree(
					FileUtil.getJavaClassesDir(sourceSet));

				configurableFileTree.setBuiltBy(
					Collections.singleton(sourceSet.getOutput()));

				configurableFileTree.setIncludes(
					Collections.singleton("**/*.class"));

				spotBugsTask.setClasses(configurableFileTree);
			}
		}

		NamedDomainObjectContainer<SpotBugsReport> namedDomainObjectContainer =
			spotBugsTask.getReports();

		final SpotBugsReport spotBugsReport = namedDomainObjectContainer.create(
			"html");

		spotBugsReport.setEnabled(true);
		spotBugsReport.setStylesheet("fancy-hist.xsl");

		spotBugsTask.doFirst(
			new Action<Task>() {

				@Override
				public void execute(Task task) {
					Project project = task.getProject();

					Logger logger = project.getLogger();

					if (logger.isLifecycleEnabled()) {
						File file = spotBugsReport.getDestination();

						logger.lifecycle(
							"Creating report: {}", FileUtil.getUrl(file));
					}
				}

			});
	}

	private void _configureTasksSpotBugs(Project project) {
		TaskContainer taskContainer = project.getTasks();

		taskContainer.withType(
			SpotBugsTask.class,
			new Action<SpotBugsTask>() {

				@Override
				public void execute(SpotBugsTask spotBugsTask) {
					_configureTaskSpotBugs(spotBugsTask);
				}

			});
	}

	private void _configureTaskTest(Project project) {
		Test test = (Test)GradleUtil.getTask(
			project, JavaPlugin.TEST_TASK_NAME);

		_configureTaskTestIgnoreFailures(test);
		_configureTaskTestJvmArgs(test, "junit.java.unit.gc");
		_configureTaskTestOutputs(test);

		test.setEnableAssertions(false);

		project.afterEvaluate(
			new Action<Project>() {

				@Override
				public void execute(Project project) {
					_configureTaskTestIncludes(test);
				}

			});
	}

	private void _configureTaskTestIgnoreFailures(Test test) {
		String ignoreFailures = GradleUtil.getTaskPrefixedProperty(
			test, "ignore.failures");

		if (Validator.isNotNull(ignoreFailures)) {
			test.setIgnoreFailures(Boolean.parseBoolean(ignoreFailures));
		}
		else {
			test.setIgnoreFailures(true);
		}
	}

	private void _configureTaskTestIncludes(Test test) {
		Set<String> includes = test.getIncludes();

		if (includes.isEmpty()) {
			test.setIncludes(Collections.singleton("**/*Test.class"));
		}
	}

	private void _configureTaskTestIntegration(Project project) {
		Test test = (Test)GradleUtil.getTask(
			project, TestIntegrationBasePlugin.TEST_INTEGRATION_TASK_NAME);

		_configureTaskTestIgnoreFailures(test);
		_configureTaskTestJvmArgs(test, "junit.java.integration.gc");
		_configureTaskTestOutputs(test);

		test.systemProperty("org.apache.maven.offline", Boolean.TRUE);

		File resultsDir = project.file("test-results/integration");

		DirectoryProperty directoryProperty = test.getBinaryResultsDirectory();

		directoryProperty.set(new File(resultsDir, "binary/testIntegration"));

		TestTaskReports testTaskReports = test.getReports();

		JUnitXmlReport jUnitXmlReport = testTaskReports.getJunitXml();

		jUnitXmlReport.setDestination(resultsDir);
	}

	private void _configureTaskTestJvmArgs(Test test, String propertyName) {
		String jvmArgs = GradleUtil.getProperty(
			test.getProject(), propertyName, (String)null);

		if (Validator.isNotNull(jvmArgs)) {
			test.jvmArgs((Object[])jvmArgs.split("\\s+"));
		}
	}

	private void _configureTaskTestOutputs(Test test) {
		TaskOutputs taskOutputs = test.getOutputs();

		taskOutputs.upToDateWhen(
			new Spec<Task>() {

				@Override
				public boolean isSatisfiedBy(Task task) {
					return false;
				}

			});
	}

	private void _configureTaskTlddoc(Project project, File portalRootDir) {
		if (portalRootDir == null) {
			return;
		}

		TLDDocTask tldDocTask = (TLDDocTask)GradleUtil.getTask(
			project, TLDDocBuilderPlugin.TLDDOC_TASK_NAME);

		File xsltDir = new File(portalRootDir, "tools/styles/taglibs");

		tldDocTask.setXsltDir(xsltDir);
	}

	private void _configureTaskUpdateFileVersions(
		ReplaceRegexTask updateFileVersionsTask, File portalRootDir) {

		Project project = updateFileVersionsTask.getProject();

		String regex = _getModuleDependencyRegex(project);

		File dir = project.getRootDir();

		if (portalRootDir != null) {
			dir = new File(portalRootDir, "modules");
		}

		Map<String, Object> args = new HashMap<>();

		args.put("dir", dir);

		List<String> excludes = new ArrayList<>();

		Collections.addAll(
			excludes, "**/bin/", "**/build/", "**/classes/", "**/node_modules/",
			"**/node_modules_cache/", "**/test-classes/", "**/tmp/");

		String property = GradleUtil.getProperty(
			project, "gradle.update.file.versions.excludes", (String)null);

		if (Validator.isNotNull(property)) {
			Collections.addAll(excludes, property.split(","));
		}

		args.put("excludes", excludes);
		args.put(
			"includes", Arrays.asList("**/*.gradle", "**/sdk/*/README.md"));

		updateFileVersionsTask.match(regex, project.fileTree(args));
	}

	private void _configureTaskUpdateVersionForCachePlugin(
		ReplaceRegexTask updateVersionTask) {

		Project project = updateVersionTask.getProject();

		CacheExtension cacheExtension = GradleUtil.getExtension(
			project, CacheExtension.class);

		for (TaskCache taskCache : cacheExtension.getTasks()) {
			String regex = "\"" + project.getName() + "@(.+?)\\/";

			Map<String, Object> args = new HashMap<>();

			args.put("dir", taskCache.getCacheDir());
			args.put("includes", Arrays.asList("config.json", "**/*.js"));

			FileTree fileTree = project.fileTree(args);

			updateVersionTask.match(regex, fileTree);

			updateVersionTask.finalizedBy(taskCache.getRefreshDigestTaskName());
		}
	}

	private void _configureTestIntegrationTomcat(Project project) {
		TestIntegrationTomcatExtension testIntegrationTomcatExtension =
			GradleUtil.getExtension(
				project, TestIntegrationTomcatExtension.class);

		testIntegrationTomcatExtension.setOverwriteCopyTestModules(false);
	}

	private boolean _containsProject(
		Project project, String dirNames, File portalRootDir) {

		if (Validator.isNull(dirNames) || (portalRootDir == null)) {
			return false;
		}

		File portalModulesDir = new File(portalRootDir, "modules");

		Path portalModulesPath = portalModulesDir.toPath();

		File projectDir = project.getProjectDir();

		Path projectPath = projectDir.toPath();

		for (String dirName : dirNames.split(",")) {
			if (projectPath.startsWith(portalModulesPath.resolve(dirName))) {
				return true;
			}
		}

		return false;
	}

	private void _copyCompileIncludeSources(Project project, File outputDir) {
		Logger logger = project.getLogger();

		ConfigurationContainer configurationContainer =
			project.getConfigurations();

		Set<Dependency> dependencies = new HashSet<>();

		Configuration configuration = GradleUtil.getConfiguration(
			project, LiferayOSGiPlugin.COMPILE_INCLUDE_CONFIGURATION_NAME);

		DependencySet dependencySet = configuration.getDependencies();

		Iterator<Dependency> iterator = dependencySet.iterator();

		while (iterator.hasNext()) {
			Dependency dependency = iterator.next();

			Map<String, Object> args = new HashMap<>();

			args.put("classifier", "sources");
			args.put("group", dependency.getGroup());
			args.put("name", dependency.getName());
			args.put("transitive", false);
			args.put("version", dependency.getVersion());

			DependencyHandler dependencyHandler = project.getDependencies();

			dependencies.add(dependencyHandler.create(args));
		}

		Configuration detachedConfiguration =
			configurationContainer.detachedConfiguration(
				dependencies.toArray(new Dependency[0]));

		ResolvedConfiguration resolvedConfiguration =
			detachedConfiguration.getResolvedConfiguration();

		LenientConfiguration lenientConfiguration =
			resolvedConfiguration.getLenientConfiguration();

		for (File file : lenientConfiguration.getFiles(Specs.satisfyAll())) {
			final FileTree fileTree = project.zipTree(file);

			Action<CopySpec> copySpecAction = new Action<CopySpec>() {

				@Override
				public void execute(CopySpec copySpec) {
					copySpec.from(fileTree);
					copySpec.include("**/*.java");
					copySpec.into(outputDir);
					copySpec.setIncludeEmptyDirs(false);
				}

			};

			try {
				project.copy(copySpecAction);
			}
			catch (RuntimeException runtimeException) {
				if (logger.isInfoEnabled()) {
					logger.info("Unable to copy {}", file);
				}
			}
		}
	}

	private void _forceProjectDependenciesEvaluation(Project project) {
		GradleInternal gradleInternal = (GradleInternal)project.getGradle();

		ServiceRegistry serviceRegistry = gradleInternal.getServices();

		final ProjectConfigurer projectConfigurer = serviceRegistry.get(
			ProjectConfigurer.class);

		EclipseModel eclipseModel = GradleUtil.getExtension(
			project, EclipseModel.class);

		EclipseClasspath eclipseClasspath = eclipseModel.getClasspath();

		for (Configuration configuration :
				eclipseClasspath.getPlusConfigurations()) {

			DependencySet dependencySet = configuration.getAllDependencies();

			dependencySet.withType(
				ProjectDependency.class,
				new Action<ProjectDependency>() {

					@Override
					public void execute(ProjectDependency projectDependency) {
						Project dependencyProject =
							projectDependency.getDependencyProject();

						projectConfigurer.configure(
							(ProjectInternal)dependencyProject);
					}

				});
		}
	}

	private File _getAppBndFile(Project project, File portalRootDir) {
		File dir = GradleUtil.getRootDir(project, _APP_BND_FILE_NAME);

		if (dir != null) {
			return new File(dir, _APP_BND_FILE_NAME);
		}

		File modulesDir = new File(portalRootDir, "modules");

		File startDir = new File(modulesDir, "private");

		if (!FileUtil.isChild(project.getProjectDir(), startDir)) {
			startDir = new File(modulesDir, "dxp");

			if (!FileUtil.isChild(project.getProjectDir(), startDir)) {
				return null;
			}
		}

		String path = FileUtil.relativize(project.getProjectDir(), startDir);

		if (File.separatorChar != '/') {
			path = path.replace(File.separatorChar, '/');
		}

		while (true) {
			File file = new File(modulesDir, path + "/" + _APP_BND_FILE_NAME);

			if (file.exists()) {
				return file;
			}

			int index = path.lastIndexOf('/');

			if (index == -1) {
				return null;
			}

			path = path.substring(0, index);
		}
	}

	private long _getArtifactLastModifiedTime(Project project) {
		DependencyHandler dependencyHandler = project.getDependencies();

		Map<String, Object> dependencyNotation = new HashMap<>();

		dependencyNotation.put("group", project.getGroup());
		dependencyNotation.put("name", GradleUtil.getArchivesBaseName(project));
		dependencyNotation.put("version", "latest.integration");

		Dependency dependency = dependencyHandler.create(dependencyNotation);

		ConfigurationContainer configurationContainer =
			project.getConfigurations();

		Configuration configuration =
			configurationContainer.detachedConfiguration(dependency);

		configuration.setTransitive(false);

		_configureConfigurationNoCache(configuration);

		File file = CollectionUtils.single(configuration.resolve());

		if (GradleUtil.isFromMavenLocal(project, file)) {
			throw new GradleException(
				"Please delete " + file.getParent() + " and try again");
		}

		try (JarFile jarFile = new JarFile(file)) {
			Manifest manifest = jarFile.getManifest();

			Attributes attributes = manifest.getMainAttributes();

			String lastModified = attributes.getValue(
				Constants.BND_LASTMODIFIED);

			return Long.valueOf(lastModified);
		}
		catch (IOException ioException) {
			throw new UncheckedIOException(ioException);
		}
	}

	private File _getLibDir(Project project) {
		File docrootDir = project.file("docroot");

		if (docrootDir.exists()) {
			return new File(docrootDir, "WEB-INF/lib");
		}

		return project.file("lib");
	}

	private String _getModuleDependency(
		Project project, boolean roundToMinorVersion) {

		StringBuilder sb = new StringBuilder();

		sb.append("group: \"");
		sb.append(project.getGroup());
		sb.append("\", name: \"");
		sb.append(GradleUtil.getArchivesBaseName(project));
		sb.append("\", version: \"");

		String versionString = String.valueOf(project.getVersion());

		if (roundToMinorVersion) {
			Version version = _getVersion(versionString);

			if (version != null) {
				version = new Version(
					version.getMajor(), version.getMinor(), 0);

				versionString = version.toString();
			}
		}

		sb.append(versionString);

		sb.append('"');

		return sb.toString();
	}

	private String _getModuleDependencyRegex(Project project) {
		StringBuilder sb = new StringBuilder();

		sb.append("group: \"");
		sb.append(project.getGroup());
		sb.append("\", name: \"");
		sb.append(GradleUtil.getArchivesBaseName(project));
		sb.append("\", version: \"");

		return Pattern.quote(sb.toString()) + "(\\d.+)\"";
	}

	private String _getModuleSnapshotDependencyRegex(Project project) {
		StringBuilder sb = new StringBuilder();

		sb.append("group: \"");
		sb.append(project.getGroup());
		sb.append("\", name: \"");
		sb.append(GradleUtil.getArchivesBaseName(project));
		sb.append("\", version: \"");

		return Pattern.quote(sb.toString()) +
			"(\\d+\\.\\d+\\.\\d+-\\d{8}\\.\\d{6}-\\d+)\"";
	}

	private String _getNexusLatestSnapshotVersion(Project project)
		throws Exception {

		String authorization = null;
		String urlString = null;

		PublishingExtension publishingExtension = GradleUtil.getExtension(
			project, PublishingExtension.class);

		RepositoryHandler repositoryHandler =
			publishingExtension.getRepositories();

		Iterator<ArtifactRepository> iterator = repositoryHandler.iterator();

		while (iterator.hasNext()) {
			ArtifactRepository artifactRepository = iterator.next();

			if (artifactRepository instanceof MavenArtifactRepository) {
				MavenArtifactRepository mavenArtifactRepository =
					(MavenArtifactRepository)artifactRepository;

				String repositoryURIString = String.valueOf(
					mavenArtifactRepository.getUrl());

				int start = repositoryURIString.indexOf(
					"/content/repositories/");

				if (start == -1) {
					continue;
				}

				StringBuilder sb = new StringBuilder();

				sb.append(repositoryURIString, 0, start);
				sb.append("/service/local/artifact/maven/resolve?g=");
				sb.append(project.getGroup());
				sb.append("&a=");
				sb.append(GradleUtil.getArchivesBaseName(project));
				sb.append("&v=LATEST&r=");

				start += 22;

				int end = repositoryURIString.indexOf('/', start);

				if (end == -1) {
					end = repositoryURIString.length();
				}

				sb.append(repositoryURIString, start, end);

				urlString = sb.toString();

				PasswordCredentials passwordCredentials =
					mavenArtifactRepository.getCredentials();

				authorization =
					passwordCredentials.getUsername() + ":" +
						passwordCredentials.getPassword();

				break;
			}
		}

		if (urlString == null) {
			throw new GradleException("Unable to get Nexus repository name");
		}

		URL url = new URL(urlString);

		URLConnection urlConnection = url.openConnection();

		authorization =
			"Basic " +
				DatatypeConverter.printBase64Binary(authorization.getBytes());

		urlConnection.setRequestProperty("Authorization", authorization);

		try (InputStream inputStream = urlConnection.getInputStream()) {
			DocumentBuilder documentBuilder = XMLUtil.getDocumentBuilder();

			Document document = documentBuilder.parse(inputStream);

			Element artifactResolutionElement = document.getDocumentElement();

			NodeList versionNodeList =
				artifactResolutionElement.getElementsByTagName("version");

			Element versionElement = (Element)versionNodeList.item(0);

			return versionElement.getTextContent();
		}
	}

	private String _getProjectDependency(Project project) {
		return "project(\"" + project.getPath() + "\")";
	}

	private Version _getVersion(Object version) {
		try {
			return Version.parseVersion(String.valueOf(version));
		}
		catch (IllegalArgumentException illegalArgumentException) {
			return null;
		}
	}

	private File _getVersionOverrideFile(Project project, GitRepo gitRepo) {
		if ((gitRepo == null) || !gitRepo.readOnly) {
			return null;
		}

		String fileName =
			".version-override-" + project.getName() + ".properties";

		return new File(gitRepo.dir.getParentFile(), fileName);
	}

	private Properties _getVersions(
			File projectDir, Properties versionOverrides)
		throws IOException {

		final Properties versions = new Properties();

		if (versionOverrides != null) {
			versions.putAll(versionOverrides);
		}

		String bundleVersion = versions.getProperty(Constants.BUNDLE_VERSION);

		if (Validator.isNull(bundleVersion)) {
			Properties bundleProperties = GUtil.loadProperties(
				new File(projectDir, "bnd.bnd"));

			bundleVersion = bundleProperties.getProperty(
				Constants.BUNDLE_VERSION);

			if (Validator.isNotNull(bundleVersion)) {
				versions.setProperty(Constants.BUNDLE_VERSION, bundleVersion);
			}
		}

		File packageInfoRootDir = new File(projectDir, "src/main/resources");

		final Path packageInfoRootDirPath = packageInfoRootDir.toPath();

		if (Files.notExists(packageInfoRootDirPath)) {
			return versions;
		}

		Files.walkFileTree(
			packageInfoRootDirPath,
			new SimpleFileVisitor<Path>() {

				@Override
				public FileVisitResult preVisitDirectory(
						Path dirPath, BasicFileAttributes basicFileAttributes)
					throws IOException {

					Path packageInfoPath = dirPath.resolve("packageinfo");

					if (Files.notExists(packageInfoPath)) {
						return FileVisitResult.CONTINUE;
					}

					String packagePath = String.valueOf(
						packageInfoRootDirPath.relativize(dirPath));

					packagePath = packagePath.replace(File.separatorChar, '.');

					String packageVersion = versions.getProperty(packagePath);

					if (Validator.isNotNull(packageVersion)) {
						return FileVisitResult.CONTINUE;
					}

					packageVersion = new String(
						Files.readAllBytes(packageInfoPath),
						StandardCharsets.UTF_8);

					packageVersion = packageVersion.trim();
					packageVersion = packageVersion.substring(8);

					versions.setProperty(packagePath, packageVersion);

					return FileVisitResult.CONTINUE;
				}

			});

		return versions;
	}

	private boolean _hasRemoteServices(BuildWSDDTask buildWSDDTask)
		throws Exception {

		if (FileUtil.exists(buildWSDDTask.getProject(), "server-config.wsdd")) {
			return true;
		}

		File serviceXmlFile = buildWSDDTask.getInputFile();

		if (!serviceXmlFile.exists()) {
			return false;
		}

		DocumentBuilder documentBuilder = XMLUtil.getDocumentBuilder();

		Document document = documentBuilder.parse(serviceXmlFile);

		Element serviceBuilderElement = document.getDocumentElement();

		NodeList entityNodeList = serviceBuilderElement.getElementsByTagName(
			"entity");

		for (int i = 0; i < entityNodeList.getLength(); i++) {
			Element entityElement = (Element)entityNodeList.item(i);

			String remoteService = entityElement.getAttribute("remote-service");

			if (Validator.isNull(remoteService) ||
				Boolean.parseBoolean(remoteService)) {

				return true;
			}
		}

		return false;
	}

	private boolean _hasTests(Project project) {
		SourceSet sourceSet = GradleUtil.getSourceSet(
			project, SourceSet.TEST_SOURCE_SET_NAME);

		SourceDirectorySet sourceDirectorySet = sourceSet.getAllSource();

		if (!sourceDirectorySet.isEmpty()) {
			return true;
		}

		sourceSet = GradleUtil.getSourceSet(
			project,
			TestIntegrationBasePlugin.TEST_INTEGRATION_SOURCE_SET_NAME);

		sourceDirectorySet = sourceSet.getAllSource();

		return !sourceDirectorySet.isEmpty();
	}

	private boolean _isPublishing(Project project) {
		Gradle gradle = project.getGradle();

		StartParameter startParameter = gradle.getStartParameter();

		List<String> taskNames = startParameter.getTaskNames();

		if (taskNames.contains(
				MavenPublishPlugin.PUBLISH_LOCAL_LIFECYCLE_TASK_NAME) ||
			taskNames.contains(PublishingPlugin.PUBLISH_LIFECYCLE_TASK_NAME)) {

			return true;
		}

		return false;
	}

	private boolean _isSnapshotStale(Project project) {
		Logger logger = project.getLogger();

		long lastModifiedTime;

		try {
			lastModifiedTime = _getArtifactLastModifiedTime(project);
		}
		catch (ResolveException resolveException) {
			if (logger.isInfoEnabled()) {
				logger.info(
					"Unable to get artifact last modified time for " + project +
						", a new snapshot will be published",
					resolveException);
			}

			return true;
		}

		// Remove milliseconds from Unix epoch

		lastModifiedTime = lastModifiedTime / 1000;

		String result = GitUtil.getGitResult(
			project, "log", "--format=%s", "--since=" + lastModifiedTime, ".");

		String[] lines = result.split("\\r?\\n");

		for (String line : lines) {
			if (logger.isInfoEnabled()) {
				logger.info(line);
			}

			if (Validator.isNull(line)) {
				continue;
			}

			if (!line.contains(
					WriteArtifactPublishCommandsTask.IGNORED_MESSAGE_PATTERN)) {

				return true;
			}
		}

		return false;
	}

	private boolean _isTaglibDependency(String group, String name) {
		if (group.equals(_GROUP) && name.startsWith("com.liferay.") &&
			name.contains(".taglib")) {

			return true;
		}

		return false;
	}

	private boolean _isUtilTaglibDependency(String group, String name) {
		if (group.equals("com.liferay.portal") &&
			name.equals("com.liferay.util.taglib")) {

			return true;
		}

		return false;
	}

	private void _saveVersions(
			File projectDir, Properties versions, File versionOverrideFile)
		throws IOException {

		if (versionOverrideFile != null) {
			if (versions.isEmpty()) {
				versionOverrideFile.delete();
			}
			else {
				FileUtil.writeProperties(versionOverrideFile, versions);
			}
		}

		Path projectDirPath = projectDir.toPath();

		String version = versions.getProperty(Constants.BUNDLE_VERSION);

		if (Validator.isNotNull(version)) {
			FileUtil.replace(
				projectDirPath.resolve("bnd.bnd"), _BUNDLE_VERSION_REGEX,
				version);
		}

		Path packageInfoRootDirPath = projectDirPath.resolve(
			"src/main/resources");

		for (String key : versions.stringPropertyNames()) {
			if ((key.indexOf(_DEPENDENCY_KEY_SEPARATOR) != -1) ||
				key.equals(Constants.BUNDLE_VERSION)) {

				continue;
			}

			version = versions.getProperty(key);

			if (Validator.isNull(version)) {
				continue;
			}

			String packageInfo = "version " + version;

			Path packageDirPath = packageInfoRootDirPath.resolve(
				key.replace('.', '/'));

			Path packageInfoPath = packageDirPath.resolve("packageinfo");

			// Avoid unnecessary update if packageinfo has a trailing empty line

			String oldPackageInfo = new String(
				Files.readAllBytes(packageInfoPath), StandardCharsets.UTF_8);

			if (packageInfo.equals(oldPackageInfo.trim())) {
				continue;
			}

			Files.createDirectories(packageDirPath);

			Files.write(
				packageInfoPath, packageInfo.getBytes(StandardCharsets.UTF_8));
		}
	}

	private boolean _syncReleaseVersions(
		Project project, File portalRootDir, File versionOverrideFile,
		boolean testProject) {

		boolean syncRelease = false;

		if (project.hasProperty(SYNC_RELEASE_PROPERTY_NAME)) {
			syncRelease = GradleUtil.getProperty(
				project, SYNC_RELEASE_PROPERTY_NAME, true);
		}

		if ((portalRootDir == null) || !syncRelease || testProject ||
			!GradleUtil.hasStartParameterTask(
				project, BaselinePlugin.BASELINE_TASK_NAME)) {

			return false;
		}

		File releasePortalRootDir = GradleUtil.getProperty(
			project, RELEASE_PORTAL_ROOT_DIR_PROPERTY_NAME, (File)null);

		if (releasePortalRootDir == null) {
			throw new GradleException(
				"Please set the property \"" +
					RELEASE_PORTAL_ROOT_DIR_PROPERTY_NAME + "\".");
		}

		Logger logger = project.getLogger();

		String relativePath = FileUtil.relativize(
			project.getProjectDir(), portalRootDir);

		File releaseProjectDir = new File(releasePortalRootDir, relativePath);

		if (!releaseProjectDir.exists()) {
			if (logger.isInfoEnabled()) {
				logger.info(
					"Unable to synchronize release versions of {}, {} does " +
						"not exist",
					project, releaseProjectDir);
			}

			return false;
		}

		if (logger.isLifecycleEnabled()) {
			logger.lifecycle(
				"Synchronizing release versions of {} with {}", project,
				releaseProjectDir);
		}

		Properties releaseVersions = null;

		Properties versions = null;

		if ((versionOverrideFile != null) && versionOverrideFile.exists()) {
			versions = GUtil.loadProperties(versionOverrideFile);
		}

		try {
			releaseVersions = _getVersions(releaseProjectDir, null);
			versions = _getVersions(project.getProjectDir(), versions);

			for (String key : releaseVersions.stringPropertyNames()) {
				if ((key.indexOf(_DEPENDENCY_KEY_SEPARATOR) != -1) ||
					!versions.containsKey(key)) {

					continue;
				}

				Version releaseVersion = Version.parseVersion(
					releaseVersions.getProperty(key));
				Version version = Version.parseVersion(
					versions.getProperty(key));

				if (releaseVersion.compareTo(version) > 0) {
					versions.setProperty(key, releaseVersion.toString());
				}
			}

			_saveVersions(
				project.getProjectDir(), versions, versionOverrideFile);
		}
		catch (IOException ioException) {
			throw new UncheckedIOException(ioException);
		}

		// Reload Bundle-Version in case it is changed, so the project
		// configuration can proceed with the new version

		String bundleVersion = versions.getProperty(Constants.BUNDLE_VERSION);

		if (Validator.isNotNull(bundleVersion)) {
			project.setVersion(bundleVersion);
		}

		return true;
	}

	private static final String _APP_BND_FILE_NAME = "app.bnd";

	private static final String _BUNDLE_VERSION_REGEX =
		Constants.BUNDLE_VERSION + ": (.+)(?:\\s|$)";

	private static final String _CACHE_COMMIT_MESSAGE = "FAKE GRADLE CACHE";

	private static final char _DEPENDENCY_KEY_SEPARATOR = '/';

	private static final String _GROUP = "com.liferay";

	private static final String _GROUP_PORTAL = "com.liferay.portal";

	private static final JavaVersion _JAVA_VERSION = JavaVersion.VERSION_1_8;

	private static final String _LANG_BUILDER_PORTAL_TOOL_NAME =
		"com.liferay.lang.builder";

	private static final String _PMD_PORTAL_TOOL_NAME = "com.liferay.pmd";

	private static final Duration _PORTAL_TOOL_MAX_AGE = TimeCategory.getDays(
		30);

	private static final String _REST_BUILDER_PORTAL_TOOL_NAME =
		"com.liferay.portal.tools.rest.builder";

	private static final String _SERVICE_BUILDER_PORTAL_TOOL_NAME =
		"com.liferay.portal.tools.service.builder";

	private static final String[] _SNAPSHOT_PROPERTY_NAMES = {
		SNAPSHOT_IF_STALE_PROPERTY_NAME
	};

	private static final String _SOURCE_FORMATTER_PORTAL_TOOL_NAME =
		"com.liferay.source.formatter";

	private static final String
		_UPDATE_FILE_VERSIONS_EXACT_VERSION_PROPERTY_NAME = "exactVersion";

	private static final BackupFilesBuildAdapter _backupFilesBuildAdapter =
		new BackupFilesBuildAdapter();
	private static final Spec<File> _javaSpec = new NameSuffixFileSpec(".java");
	private static final Spec<File> _jsdocSpec = new NameSuffixFileSpec(
		".es.js", ".jsdoc", ".jsx");
	private static final Spec<File> _jspSpec = new NameSuffixFileSpec(
		".jsp", ".jspf");
	private static final Spec<File> _tldSpec = new NameSuffixFileSpec(".tld");

}