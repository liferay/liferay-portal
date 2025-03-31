/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.gradle.plugins;

import aQute.bnd.gradle.BeanProperties;
import aQute.bnd.gradle.BndUtils;
import aQute.bnd.gradle.BundleTaskExtension;
import aQute.bnd.header.Parameters;
import aQute.bnd.osgi.Builder;
import aQute.bnd.osgi.Constants;
import aQute.bnd.osgi.Processor;
import aQute.bnd.version.MavenVersion;
import aQute.bnd.version.Version;

import aQute.lib.utf8properties.UTF8Properties;

import com.liferay.gradle.plugins.css.builder.CSSBuilderPlugin;
import com.liferay.gradle.plugins.extensions.BundleExtension;
import com.liferay.gradle.plugins.extensions.LiferayExtension;
import com.liferay.gradle.plugins.extensions.LiferayOSGiExtension;
import com.liferay.gradle.plugins.internal.AlloyTaglibDefaultsPlugin;
import com.liferay.gradle.plugins.internal.DBSupportDefaultsPlugin;
import com.liferay.gradle.plugins.internal.JavadocFormatterDefaultsPlugin;
import com.liferay.gradle.plugins.internal.RESTBuilderDefaultsPlugin;
import com.liferay.gradle.plugins.internal.ServiceBuilderDefaultsPlugin;
import com.liferay.gradle.plugins.internal.TLDFormatterDefaultsPlugin;
import com.liferay.gradle.plugins.internal.TestIntegrationDefaultsPlugin;
import com.liferay.gradle.plugins.internal.UpgradeTableBuilderDefaultsPlugin;
import com.liferay.gradle.plugins.internal.WSDDBuilderDefaultsPlugin;
import com.liferay.gradle.plugins.internal.util.FileUtil;
import com.liferay.gradle.plugins.internal.util.GradleUtil;
import com.liferay.gradle.plugins.internal.util.IncludeResourceCompileIncludeInstruction;
import com.liferay.gradle.plugins.internal.util.copy.RenameDependencyAction;
import com.liferay.gradle.plugins.jasper.jspc.JspCPlugin;
import com.liferay.gradle.plugins.javadoc.formatter.JavadocFormatterPlugin;
import com.liferay.gradle.plugins.lang.builder.LangBuilderPlugin;
import com.liferay.gradle.plugins.node.NodePlugin;
import com.liferay.gradle.plugins.node.task.DownloadNodeModuleTask;
import com.liferay.gradle.plugins.node.task.NpmInstallTask;
import com.liferay.gradle.plugins.source.formatter.SourceFormatterPlugin;
import com.liferay.gradle.plugins.task.DirectDeployTask;
import com.liferay.gradle.plugins.test.integration.TestIntegrationPlugin;
import com.liferay.gradle.plugins.tld.formatter.TLDFormatterPlugin;
import com.liferay.gradle.plugins.tlddoc.builder.TLDDocBuilderPlugin;
import com.liferay.gradle.plugins.util.BndUtil;
import com.liferay.gradle.plugins.wsdd.builder.BuildWSDDTask;
import com.liferay.gradle.plugins.wsdd.builder.WSDDBuilderPlugin;
import com.liferay.gradle.plugins.wsdl.builder.WSDLBuilderPlugin;
import com.liferay.gradle.util.StringUtil;
import com.liferay.gradle.util.Validator;

import groovy.lang.Closure;

import java.io.File;

import java.nio.charset.StandardCharsets;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.gradle.api.Action;
import org.gradle.api.GradleException;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.ConfigurationContainer;
import org.gradle.api.artifacts.DependencySet;
import org.gradle.api.artifacts.ProjectDependency;
import org.gradle.api.file.CopySpec;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.file.FileCollection;
import org.gradle.api.file.RegularFile;
import org.gradle.api.file.SourceDirectorySet;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import org.gradle.api.plugins.ApplicationPlugin;
import org.gradle.api.plugins.ApplicationPluginConvention;
import org.gradle.api.plugins.BasePlugin;
import org.gradle.api.plugins.BasePluginConvention;
import org.gradle.api.plugins.Convention;
import org.gradle.api.plugins.ExtensionContainer;
import org.gradle.api.plugins.JavaLibraryPlugin;
import org.gradle.api.plugins.JavaPlugin;
import org.gradle.api.plugins.JavaPluginConvention;
import org.gradle.api.plugins.PluginContainer;
import org.gradle.api.provider.Property;
import org.gradle.api.provider.Provider;
import org.gradle.api.specs.Spec;
import org.gradle.api.tasks.Copy;
import org.gradle.api.tasks.Delete;
import org.gradle.api.tasks.JavaExec;
import org.gradle.api.tasks.SourceSet;
import org.gradle.api.tasks.SourceSetContainer;
import org.gradle.api.tasks.SourceSetOutput;
import org.gradle.api.tasks.TaskContainer;
import org.gradle.api.tasks.TaskInputs;
import org.gradle.api.tasks.TaskOutputs;
import org.gradle.api.tasks.TaskProvider;
import org.gradle.api.tasks.bundling.Jar;
import org.gradle.api.tasks.bundling.War;
import org.gradle.api.tasks.bundling.Zip;
import org.gradle.api.tasks.compile.CompileOptions;
import org.gradle.api.tasks.compile.JavaCompile;
import org.gradle.api.tasks.javadoc.Javadoc;
import org.gradle.api.tasks.testing.Test;
import org.gradle.plugins.ide.eclipse.EclipsePlugin;

/**
 * @author Andrea Di Giorgi
 * @author Raymond Augé
 */
public class LiferayOSGiPlugin implements Plugin<Project> {

	public static final String AUTO_CLEAN_PROPERTY_NAME = "autoClean";

	public static final String AUTO_UPDATE_XML_TASK_NAME = "autoUpdateXml";

	public static final String CLEAN_DEPLOYED_PROPERTY_NAME = "cleanDeployed";

	public static final String COMPILE_INCLUDE_CONFIGURATION_NAME =
		"compileInclude";

	public static final String DEPLOY_DEPENDENCIES_TASK_NAME =
		"deployDependencies";

	public static final String PLUGIN_NAME = "liferayOSGi";

	public static final String ZIP_ZIPPABLE_RESOURCES_TASK_NAME =
		"zipZippableResources";

	@Override
	public void apply(final Project project) {

		// Plugins

		_applyPlugins(project);

		// Extensions

		ExtensionContainer extensionContainer = project.getExtensions();

		final LiferayOSGiExtension liferayOSGiExtension =
			new LiferayOSGiExtension(project);

		extensionContainer.add(
			LiferayOSGiExtension.class, PLUGIN_NAME, liferayOSGiExtension);

		final BundleExtension bundleExtension = BndUtil.getBundleExtension(
			extensionContainer);
		LiferayExtension liferayExtension = extensionContainer.getByType(
			LiferayExtension.class);

		_configureExtensionBundle(project, bundleExtension);
		_configureExtensionLiferay(project, liferayExtension);

		// Conventions

		final Convention convention = project.getConvention();

		BasePluginConvention basePluginConvention = convention.getPlugin(
			BasePluginConvention.class);
		JavaPluginConvention javaPluginConvention = convention.getPlugin(
			JavaPluginConvention.class);

		SourceSetContainer javaSourceSetContainer =
			javaPluginConvention.getSourceSets();

		final SourceSet javaMainSourceSet = javaSourceSetContainer.getByName(
			SourceSet.MAIN_SOURCE_SET_NAME);

		_configureConventionBasePlugin(bundleExtension, basePluginConvention);
		_configureConventionJavaPlugin(project, javaMainSourceSet);

		// Configurations

		ConfigurationContainer configurationContainer =
			project.getConfigurations();

		final Configuration compileIncludeConfiguration =
			configurationContainer.maybeCreate(
				COMPILE_INCLUDE_CONFIGURATION_NAME);

		Configuration compileOnlyConfiguration =
			configurationContainer.getByName(
				JavaPlugin.COMPILE_ONLY_CONFIGURATION_NAME);

		_configureConfigurationCompileInclude(compileIncludeConfiguration);
		_configureConfigurationCompileOnly(
			compileIncludeConfiguration, compileOnlyConfiguration);

		// Tasks

		final TaskProvider<Copy> deployDependenciesTaskProvider =
			GradleUtil.addTaskProvider(
				project, DEPLOY_DEPENDENCIES_TASK_NAME, Copy.class);
		TaskProvider<DirectDeployTask> directDeployTaskProvider =
			GradleUtil.addTaskProvider(
				project, AUTO_UPDATE_XML_TASK_NAME, DirectDeployTask.class);
		TaskProvider<Task> zipZippableResourcesTaskProvider =
			GradleUtil.addTaskProvider(
				project, ZIP_ZIPPABLE_RESOURCES_TASK_NAME, Task.class);

		TaskProvider<Delete> cleanTaskProvider = GradleUtil.getTaskProvider(
			project, BasePlugin.CLEAN_TASK_NAME, Delete.class);
		TaskProvider<JavaCompile> compileTestJavaTaskProvider =
			GradleUtil.getTaskProvider(
				project, JavaPlugin.COMPILE_TEST_JAVA_TASK_NAME,
				JavaCompile.class);
		TaskProvider<Copy> deployTaskProvider = GradleUtil.getTaskProvider(
			project, LiferayBasePlugin.DEPLOY_TASK_NAME, Copy.class);
		TaskProvider<Jar> jarTaskProvider = GradleUtil.getTaskProvider(
			project, JavaPlugin.JAR_TASK_NAME, Jar.class);
		TaskProvider<Javadoc> javadocTaskProvider = GradleUtil.getTaskProvider(
			project, JavaPlugin.JAVADOC_TASK_NAME, Javadoc.class);
		TaskProvider<Test> testTaskProvider = GradleUtil.getTaskProvider(
			project, JavaPlugin.TEST_TASK_NAME, Test.class);

		List<TaskProvider<Zip>> zippableResourcesTaskProviders =
			new ArrayList<>();

		File[] zippableResourcesDirs = FileUtil.getDirectories(
			project.file("src/main/zippableResources"));

		for (File zippableResourcesDir : zippableResourcesDirs) {
			String taskName = GradleUtil.getTaskName(
				ZIP_ZIPPABLE_RESOURCES_TASK_NAME, zippableResourcesDir);

			TaskProvider<Zip> zippableResourcesTaskProvider =
				GradleUtil.addTaskProvider(project, taskName, Zip.class);

			zippableResourcesTaskProviders.add(zippableResourcesTaskProvider);

			_configureTaskZippableResources(
				project, zippableResourcesTaskProvider, zippableResourcesDir);
		}

		_configureTaskAutoUpdateXmlProvider(
			project, liferayExtension, liferayOSGiExtension,
			directDeployTaskProvider, jarTaskProvider);
		_configureTaskCleanProvider(project, cleanTaskProvider);
		_configureTaskDeployProvider(
			deployTaskProvider, deployDependenciesTaskProvider);
		_configureTaskDeployDependenciesProvider(
			liferayExtension, deployDependenciesTaskProvider);
		_configureTaskJarProvider(
			project, bundleExtension, jarTaskProvider,
			zipZippableResourcesTaskProvider);
		_configureTaskJavadocProvider(bundleExtension, javadocTaskProvider);
		_configureTaskTestProvider(testTaskProvider);
		_configureTaskZipZippableResources(
			project, zippableResourcesTaskProviders,
			zipZippableResourcesTaskProvider);

		_configureTaskCleanProvider(
			liferayExtension, cleanTaskProvider, deployTaskProvider,
			jarTaskProvider);
		_configureTaskDeployProvider(
			project, liferayExtension, deployTaskProvider, jarTaskProvider,
			false);

		// Containers

		TaskContainer taskContainer = project.getTasks();

		taskContainer.withType(
			BuildWSDDTask.class,
			new Action<BuildWSDDTask>() {

				@Override
				public void execute(BuildWSDDTask buildWSDDTask) {
					TaskProvider<Jar> buildWSDDJarTaskProvider =
						GradleUtil.addTaskProvider(
							project, buildWSDDTask.getName() + "Jar",
							Jar.class);

					_configureTaskBuildWSDDJarProvider(
						project, bundleExtension, liferayExtension,
						liferayOSGiExtension, javaMainSourceSet, buildWSDDTask,
						buildWSDDJarTaskProvider, cleanTaskProvider,
						deployTaskProvider);
				}

			});

		taskContainer.configureEach(
			new Action<Task>() {

				@Override
				public void execute(Task task) {
					if (task instanceof JavaCompile) {
						_configureTaskJavaCompile((JavaCompile)task, true);
					}

					if (task instanceof Test) {
						_configureTaskTest((Test)task);
					}
				}

			});

		PluginContainer pluginContainer = project.getPlugins();

		pluginContainer.configureEach(
			new Action<Plugin>() {

				@Override
				public void execute(Plugin plugin) {
					if (plugin instanceof ApplicationPlugin) {
						_configurePluginApplication(
							project, bundleExtension,
							compileIncludeConfiguration, convention);
					}
				}

			});

		// Other

		_configureProject(project, bundleExtension);

		project.afterEvaluate(
			new Action<Project>() {

				@Override
				public void execute(Project project) {
					_configureExtensionBundleAfterEvaluate(
						bundleExtension, liferayOSGiExtension,
						compileIncludeConfiguration);
					_configureTaskCompileTestJavaProviderAfterEvaluate(
						project, compileTestJavaTaskProvider);
					_configureTaskDeployDependenciesProviderAfterEvaluate(
						deployDependenciesTaskProvider);
				}

			});
	}

	private void _applyPlugins(Project project) {
		GradleUtil.applyPlugin(project, LiferayBasePlugin.class);

		GradleUtil.applyPlugin(project, JavaLibraryPlugin.class);

		GradleUtil.applyPlugin(project, CSSBuilderPlugin.class);

		GradleUtil.applyPlugin(project, NodePlugin.class);

		GradleUtil.applyPlugin(project, EclipsePlugin.class);
		GradleUtil.applyPlugin(project, JavadocFormatterPlugin.class);
		GradleUtil.applyPlugin(project, JspCPlugin.class);
		GradleUtil.applyPlugin(project, LangBuilderPlugin.class);
		GradleUtil.applyPlugin(project, SourceFormatterPlugin.class);
		GradleUtil.applyPlugin(project, TLDDocBuilderPlugin.class);
		GradleUtil.applyPlugin(project, TLDFormatterPlugin.class);
		GradleUtil.applyPlugin(project, TestIntegrationPlugin.class);

		AlloyTaglibDefaultsPlugin.INSTANCE.apply(project);
		CSSBuilderDefaultsPlugin.INSTANCE.apply(project);
		DBSupportDefaultsPlugin.INSTANCE.apply(project);
		EclipseDefaultsPlugin.INSTANCE.apply(project);
		IdeaDefaultsPlugin.INSTANCE.apply(project);
		JavadocFormatterDefaultsPlugin.INSTANCE.apply(project);
		JspCDefaultsPlugin.INSTANCE.apply(project);
		RESTBuilderDefaultsPlugin.INSTANCE.apply(project);
		ServiceBuilderDefaultsPlugin.INSTANCE.apply(project);
		TLDFormatterDefaultsPlugin.INSTANCE.apply(project);
		TestIntegrationDefaultsPlugin.INSTANCE.apply(project);
		UpgradeTableBuilderDefaultsPlugin.INSTANCE.apply(project);
		WSDDBuilderDefaultsPlugin.INSTANCE.apply(project);
		WatchOSGiPlugin.INSTANCE.apply(project);
	}

	private void _configureConfigurationCompileInclude(
		Configuration compileIncludeConfiguration) {

		compileIncludeConfiguration.setDescription(
			"Additional dependencies to include in the final JAR.");
		compileIncludeConfiguration.setVisible(false);
	}

	private void _configureConfigurationCompileOnly(
		Configuration compileIncludeConfiguration,
		Configuration compileOnlyConfiguration) {

		compileOnlyConfiguration.extendsFrom(compileIncludeConfiguration);
	}

	private void _configureConventionBasePlugin(
		BundleExtension bundleExtension,
		BasePluginConvention basePluginConvention) {

		String bundleSymbolicName = bundleExtension.getInstruction(
			Constants.BUNDLE_SYMBOLICNAME);

		if (Validator.isNull(bundleSymbolicName)) {
			return;
		}

		Parameters parameters = new Parameters(bundleSymbolicName);

		Set<String> keys = parameters.keySet();

		Iterator<String> iterator = keys.iterator();

		bundleSymbolicName = iterator.next();

		basePluginConvention.setArchivesBaseName(bundleSymbolicName);
	}

	private void _configureConventionJavaPlugin(
		Project project, SourceSet javaMainSourceSet) {

		File docrootDir = project.file("docroot");

		if (!docrootDir.exists()) {
			return;
		}

		File javaClassesDir = new File(docrootDir, "WEB-INF/classes");

		SourceDirectorySet javaSourceDirectorySet = javaMainSourceSet.getJava();

		DirectoryProperty directoryProperty =
			javaSourceDirectorySet.getDestinationDirectory();

		directoryProperty.set(javaClassesDir);

		SourceSetOutput sourceSetOutput = javaMainSourceSet.getOutput();

		sourceSetOutput.setResourcesDir(javaClassesDir);

		File srcDir = new File(docrootDir, "WEB-INF/src");

		Set<File> srcDirs = Collections.singleton(srcDir);

		javaSourceDirectorySet.setSrcDirs(srcDirs);

		SourceDirectorySet resourcesSourceDirectorySet =
			javaMainSourceSet.getResources();

		resourcesSourceDirectorySet.setSrcDirs(srcDirs);
	}

	private void _configureExtensionBundle(
		Project project, BundleExtension bundleExtension) {

		File file = project.file("bnd.bnd");

		if (!file.exists()) {
			return;
		}

		UTF8Properties utf8Properties = new UTF8Properties();

		try (Processor processor = new Processor()) {
			utf8Properties.load(file, processor);

			Enumeration<Object> enumeration = utf8Properties.keys();

			while (enumeration.hasMoreElements()) {
				String key = (String)enumeration.nextElement();

				String value = utf8Properties.getProperty(key);

				bundleExtension.put(key, value);
			}
		}
		catch (Exception exception) {
			throw new GradleException("Could not read " + file, exception);
		}
	}

	private void _configureExtensionBundleAfterEvaluate(
		BundleExtension bundleExtension,
		final LiferayOSGiExtension liferayOSGiExtension,
		final Configuration compileIncludeConfiguration) {

		IncludeResourceCompileIncludeInstruction
			includeResourceCompileIncludeInstruction =
				new IncludeResourceCompileIncludeInstruction(
					new Callable<Iterable<File>>() {

						@Override
						public Iterable<File> call() throws Exception {
							return compileIncludeConfiguration;
						}

					},
					new Callable<Boolean>() {

						@Override
						public Boolean call() throws Exception {
							return liferayOSGiExtension.
								isExpandCompileInclude();
						}

					});

		bundleExtension.instruction(
			Constants.INCLUDERESOURCE + "." +
				compileIncludeConfiguration.getName(),
			includeResourceCompileIncludeInstruction);

		Map<String, Object> bundleDefaultInstructions =
			liferayOSGiExtension.getBundleDefaultInstructions();

		for (Map.Entry<String, Object> entry :
				bundleDefaultInstructions.entrySet()) {

			String key = entry.getKey();

			if (!bundleExtension.containsKey(key)) {
				bundleExtension.instruction(key, entry.getValue());
			}
		}
	}

	private void _configureExtensionLiferay(
		final Project project, final LiferayExtension liferayExtension) {

		liferayExtension.setDeployDir(
			new Callable<File>() {

				@Override
				public File call() throws Exception {
					File dir = new File(
						liferayExtension.getAppServerParentDir(),
						"osgi/modules");

					return GradleUtil.getProperty(
						project, "auto.deploy.dir", dir);
				}

			});
	}

	private void _configurePluginApplication(
		Project project, BundleExtension bundleExtension,
		final Configuration compileIncludeConfiguration,
		Convention convention) {

		// Conventions

		ApplicationPluginConvention applicationPluginConvention =
			convention.getPlugin(ApplicationPluginConvention.class);

		String mainClassName = bundleExtension.getInstruction("Main-Class");

		if (Validator.isNotNull(mainClassName)) {
			applicationPluginConvention.setMainClassName(mainClassName);
		}

		// Tasks

		TaskProvider<JavaExec> runTaskProvider = GradleUtil.getTaskProvider(
			project, ApplicationPlugin.TASK_RUN_NAME, JavaExec.class);

		runTaskProvider.configure(
			new Action<JavaExec>() {

				@Override
				public void execute(JavaExec runJavaExec) {
					runJavaExec.classpath(compileIncludeConfiguration);
				}

			});
	}

	private void _configureProject(
		Project project, BundleExtension bundleExtension) {

		String description = bundleExtension.getInstruction(
			Constants.BUNDLE_DESCRIPTION);

		if (Validator.isNull(description)) {
			description = bundleExtension.getInstruction(Constants.BUNDLE_NAME);
		}

		if (Validator.isNotNull(description)) {
			project.setDescription(description);
		}

		String version = bundleExtension.getInstruction(
			Constants.BUNDLE_VERSION);

		if (Validator.isNotNull(version)) {
			project.setVersion(version);
		}
	}

	private void _configureTaskAutoUpdateXmlProvider(
		final Project project, final LiferayExtension liferayExtension,
		final LiferayOSGiExtension liferayOSGiExtension,
		TaskProvider<DirectDeployTask> directDeployTaskProvider,
		TaskProvider<Jar> jarTaskProvider) {

		directDeployTaskProvider.configure(
			new Action<DirectDeployTask>() {

				@Override
				public void execute(final DirectDeployTask directDeployTask) {
					directDeployTask.setAppServerDeployDir(
						directDeployTask.getTemporaryDir());
					directDeployTask.setAppServerType("tomcat");

					final Jar jar = jarTaskProvider.get();

					directDeployTask.setWebAppFile(
						new Callable<File>() {

							@Override
							public File call() throws Exception {
								return FileUtil.replaceExtension(
									jar.getArchivePath(), War.WAR_EXTENSION);
							}

						});

					directDeployTask.setWebAppType("portlet");

					directDeployTask.doFirst(
						new Action<Task>() {

							@Override
							public void execute(Task task) {
								File jarFile = jar.getArchivePath();

								jarFile.renameTo(
									directDeployTask.getWebAppFile());
							}

						});

					directDeployTask.doLast(
						new Action<Task>() {

							@Override
							public void execute(Task task) {
								Logger logger = task.getLogger();

								project.delete("liferay/logs");

								File liferayDir = project.file("liferay");

								boolean deleted = liferayDir.delete();

								if (!deleted && logger.isInfoEnabled()) {
									logger.info(
										"Unable to delete " + liferayDir);
								}
							}

						});

					directDeployTask.doLast(
						new Action<Task>() {

							@Override
							public void execute(Task task) {
								Property<String> property =
									jar.getArchiveFileName();

								String deployedPluginDirName =
									FileUtil.stripExtension(property.get());

								File deployedPluginDir = new File(
									directDeployTask.getAppServerDeployDir(),
									deployedPluginDirName);

								if (!deployedPluginDir.exists()) {
									deployedPluginDir = new File(
										directDeployTask.
											getAppServerDeployDir(),
										project.getName());
								}

								if (!deployedPluginDir.exists()) {
									_logger.warn(
										"Unable to automatically update " +
											"web.xml in " +
												jar.getArchivePath());

									return;
								}

								FileUtil.touchFiles(
									project, deployedPluginDir, 0,
									"WEB-INF/liferay-web.xml",
									"WEB-INF/web.xml", "WEB-INF/tld/*");

								deployedPluginDirName = project.relativePath(
									deployedPluginDir);

								String[][] filesets = {
									{
										project.relativePath(
											liferayExtension.
												getAppServerPortalDir()),
										"WEB-INF/tld/c.tld"
									},
									{
										deployedPluginDirName,
										"WEB-INF/liferay-web.xml," +
											"WEB-INF/web.xml"
									},
									{deployedPluginDirName, "WEB-INF/tld/*"}
								};

								File warFile = directDeployTask.getWebAppFile();

								FileUtil.jar(
									project, warFile, "preserve", true,
									filesets);

								warFile.renameTo(jar.getArchivePath());
							}

						});

					directDeployTask.onlyIf(
						new Spec<Task>() {

							@Override
							public boolean isSatisfiedBy(Task task) {
								if (liferayOSGiExtension.isAutoUpdateXml() &&
									FileUtil.exists(
										project,
										"docroot/WEB-INF/portlet.xml")) {

									return true;
								}

								return false;
							}

						});

					TaskInputs taskInputs = directDeployTask.getInputs();

					taskInputs.file((Callable<File>)jar::getArchivePath);
				}

			});

		jarTaskProvider.configure(
			new Action<Jar>() {

				@Override
				public void execute(Jar jar) {
					jar.finalizedBy(directDeployTaskProvider);
				}

			});
	}

	private void _configureTaskBuildWSDDJarProvider(
		final Project project, final BundleExtension bundleExtension,
		LiferayExtension liferayExtension,
		final LiferayOSGiExtension liferayOSGiExtension,
		final SourceSet javaMainSourceSet, final BuildWSDDTask buildWSDDTask,
		TaskProvider<Jar> buildWSDDJarTaskProvider,
		TaskProvider<Delete> cleanTaskProvider,
		TaskProvider<Copy> deployTaskProvider) {

		buildWSDDJarTaskProvider.configure(
			new Action<Jar>() {

				@Override
				public void execute(Jar buildWSDDJar) {
					buildWSDDJar.setActions(Collections.emptyList());

					buildWSDDJar.dependsOn(buildWSDDTask);

					buildWSDDJar.doLast(
						new Action<Task>() {

							@Override
							public void execute(Task task) {
								Logger logger = project.getLogger();

								Properties beanProperties =
									new BeanProperties();

								beanProperties.put("project", project);
								beanProperties.put("task", task);

								try (Builder builder = new Builder(
										new Processor(beanProperties, false))) {

									Map<String, String> properties =
										_getBuilderProperties(
											project, bundleExtension,
											liferayOSGiExtension,
											buildWSDDTask);

									File buildFile = project.getBuildFile();

									builder.setBase(buildFile.getParentFile());

									builder.putAll(properties, true);

									SourceDirectorySet sourceDirectorySet =
										javaMainSourceSet.getJava();

									SourceSetOutput sourceSetOutput =
										javaMainSourceSet.getOutput();

									FileCollection buildDirs = project.files(
										sourceDirectorySet.
											getClassesDirectory(),
										sourceSetOutput.getResourcesDir());

									Set<File> buildDirsFiles =
										buildDirs.getFiles();

									builder.setClasspath(
										buildDirsFiles.toArray(new File[0]));

									builder.setProperty(
										"project.buildpath",
										buildDirs.getAsPath());

									if (logger.isDebugEnabled()) {
										logger.lifecycle(
											"BND Builder Classpath {}: {}",
											project.getName(),
											buildDirs.getAsPath());
									}

									SourceDirectorySet allSource =
										javaMainSourceSet.getAllSource();

									Set<File> srcDirs = allSource.getSrcDirs();

									Stream<File> stream = srcDirs.stream();

									FileCollection sourceDirs = project.files(
										stream.filter(
											File::exists
										).collect(
											Collectors.toList()
										));

									builder.setProperty(
										"project.sourcepath",
										sourceDirs.getAsPath());

									Set<File> sourceDirsFiles =
										sourceDirs.getFiles();

									builder.setSourcepath(
										sourceDirsFiles.toArray(new File[0]));

									if (logger.isDebugEnabled()) {
										logger.debug(
											"BND Builder Sourcepath {}: {}",
											project.getName(),
											builder.getSourcePath());
									}

									String bundleSymbolicName =
										builder.getProperty(
											Constants.BUNDLE_SYMBOLICNAME);

									if (Validator.isNull(bundleSymbolicName) ||
										Constants.EMPTY_HEADER.equals(
											bundleSymbolicName)) {

										builder.setProperty(
											Constants.BUNDLE_SYMBOLICNAME,
											project.getName());
									}

									String bundleVersion = builder.getProperty(
										Constants.BUNDLE_VERSION);

									if ((Validator.isNull(bundleVersion) ||
										 Constants.EMPTY_HEADER.equals(
											 bundleVersion)) &&
										(project.getVersion() != null)) {

										Object version = project.getVersion();

										MavenVersion mavenVersion =
											MavenVersion.parseString(
												version.toString());

										Version osgiVersion =
											mavenVersion.getOSGiVersion();

										builder.setProperty(
											Constants.BUNDLE_VERSION,
											osgiVersion.toString());
									}

									if (logger.isDebugEnabled()) {
										logger.debug(
											"BND Builder Properties {}: {}",
											project.getName(), properties);
									}

									aQute.bnd.osgi.Jar bndJar = builder.build();

									if (!builder.isOk()) {
										BndUtils.logReport(builder, logger);

										new GradleException(
											buildWSDDTask + " failed");
									}

									TaskOutputs taskOutputs = task.getOutputs();

									FileCollection fileCollection =
										taskOutputs.getFiles();

									bndJar.write(
										fileCollection.getSingleFile());

									BndUtils.logReport(builder, logger);

									if (!builder.isOk()) {
										new GradleException(
											buildWSDDTask + " failed");
									}
								}
								catch (Exception exception) {
									throw new GradleException(
										buildWSDDTask + " failed", exception);
								}
							}

						});

					String taskName = buildWSDDTask.getName();

					Property<String> property =
						buildWSDDJar.getArchiveAppendix();

					if (taskName.equals(
							WSDDBuilderPlugin.BUILD_WSDD_TASK_NAME)) {

						property.set("wsdd");
					}
					else {
						property.set("wsdd-" + taskName);
					}

					buildWSDDTask.finalizedBy(buildWSDDJar);
				}

			});

		_configureTaskCleanProvider(
			liferayExtension, cleanTaskProvider, deployTaskProvider,
			buildWSDDJarTaskProvider);
		_configureTaskDeployProvider(
			project, liferayExtension, deployTaskProvider,
			buildWSDDJarTaskProvider, true);
	}

	private void _configureTaskCleanProvider(
		final LiferayExtension liferayExtension,
		TaskProvider<Delete> cleanTaskProvider,
		final TaskProvider<Copy> deployTaskProvider,
		final TaskProvider<Jar> jarTaskProvider) {

		cleanTaskProvider.configure(
			new Action<Delete>() {

				@Override
				public void execute(Delete cleanDelete) {
					cleanDelete.delete(
						new Callable<File>() {

							@Override
							public File call() throws Exception {
								boolean cleanDeployed = GradleUtil.getProperty(
									cleanDelete, CLEAN_DEPLOYED_PROPERTY_NAME,
									true);

								if (!cleanDeployed) {
									return null;
								}

								Copy deployCopy = deployTaskProvider.get();
								Jar jar = jarTaskProvider.get();

								Closure<String> deployedFileNameClosure =
									liferayExtension.
										getDeployedFileNameClosure();

								return new File(
									deployCopy.getDestinationDir(),
									deployedFileNameClosure.call(jar));
							}

						});
				}

			});
	}

	private void _configureTaskCleanProvider(
		final Project project, TaskProvider<Delete> cleanTaskProvider) {

		cleanTaskProvider.configure(
			new Action<Delete>() {

				@Override
				public void execute(Delete cleanDelete) {
					Closure<Set<String>> c = new Closure<Set<String>>(project) {

						@SuppressWarnings("unused")
						public Set<String> doCall(Delete delete) {
							Set<String> cleanTaskNames = new HashSet<>();

							Project project = delete.getProject();

							for (Task task : project.getTasks()) {
								String taskName = task.getName();

								if (taskName.equals(
										LiferayBasePlugin.DEPLOY_TASK_NAME) ||
									taskName.equals("buildSoy") ||
									taskName.equals("eclipseClasspath") ||
									taskName.equals("eclipseProject") ||
									taskName.equals("ideaModule") ||
									(task instanceof DownloadNodeModuleTask) ||
									(task instanceof NpmInstallTask)) {

									continue;
								}

								if (GradleUtil.hasPlugin(
										project, _CACHE_PLUGIN_ID) &&
									taskName.startsWith("save") &&
									taskName.endsWith("Cache")) {

									continue;
								}

								if (GradleUtil.hasPlugin(
										project, WSDLBuilderPlugin.class) &&
									taskName.startsWith(
										WSDLBuilderPlugin.BUILD_WSDL_TASK_NAME +
											"Generate")) {

									continue;
								}

								boolean autoClean = GradleUtil.getProperty(
									task, AUTO_CLEAN_PROPERTY_NAME, true);

								if (!autoClean) {
									continue;
								}

								TaskOutputs taskOutputs = task.getOutputs();

								if (!taskOutputs.getHasOutput()) {
									continue;
								}

								cleanTaskNames.add(
									BasePlugin.CLEAN_TASK_NAME +
										StringUtil.capitalize(taskName));
							}

							return cleanTaskNames;
						}

					};

					cleanDelete.dependsOn(c);
				}

			});
	}

	private void _configureTaskCompileTestJavaProviderAfterEvaluate(
		Project project,
		TaskProvider<JavaCompile> compileTestJavaTaskProvider) {

		compileTestJavaTaskProvider.configure(
			new Action<JavaCompile>() {

				@Override
				public void execute(JavaCompile javaCompile) {
					Configuration testImplementationConfiguration =
						GradleUtil.getConfiguration(
							project,
							JavaPlugin.TEST_IMPLEMENTATION_CONFIGURATION_NAME);

					DependencySet dependencySet =
						testImplementationConfiguration.getDependencies();

					for (ProjectDependency projectDependency :
							dependencySet.withType(ProjectDependency.class)) {

						Project dependencyProject =
							projectDependency.getDependencyProject();

						javaCompile.mustRunAfter(
							dependencyProject.getPath() + ":" +
								JavaPlugin.PROCESS_RESOURCES_TASK_NAME);
					}
				}

			});
	}

	private void _configureTaskDeployDependenciesProvider(
		final LiferayExtension liferayExtension,
		TaskProvider<Copy> deployDependenciesTaskProvider) {

		deployDependenciesTaskProvider.configure(
			new Action<Copy>() {

				@Override
				public void execute(Copy deployDependenciesCopy) {
					boolean keepVersions = Boolean.getBoolean(
						"deploy.dependencies.keep.versions");

					GradleUtil.setProperty(
						deployDependenciesCopy,
						LiferayOSGiPlugin.AUTO_CLEAN_PROPERTY_NAME, false);
					GradleUtil.setProperty(
						deployDependenciesCopy, "keepVersions", keepVersions);

					String renameSuffix = ".jar";

					if (keepVersions) {
						renameSuffix = "-$1.jar";
					}

					GradleUtil.setProperty(
						deployDependenciesCopy, "renameSuffix", renameSuffix);

					deployDependenciesCopy.into(
						(Callable<File>)liferayExtension::getDeployDir);

					deployDependenciesCopy.setDescription(
						"Deploys additional dependencies.");

					TaskOutputs taskOutputs =
						deployDependenciesCopy.getOutputs();

					taskOutputs.upToDateWhen(
						new Spec<Task>() {

							@Override
							public boolean isSatisfiedBy(Task task) {
								return false;
							}

						});
				}

			});
	}

	private void _configureTaskDeployDependenciesProviderAfterEvaluate(
		TaskProvider<Copy> deployDependenciesTaskProvider) {

		deployDependenciesTaskProvider.configure(
			new Action<Copy>() {

				@Override
				public void execute(Copy deployDependenciesCopy) {
					deployDependenciesCopy.eachFile(
						new RenameDependencyAction(
							Boolean.getBoolean(
								"deploy.dependencies.keep.versions")));
				}

			});
	}

	private void _configureTaskDeployProvider(
		final Project project, final LiferayExtension liferayExtension,
		TaskProvider<Copy> deployTaskProvider,
		final TaskProvider<Jar> jarTaskProvider, boolean lazy) {

		deployTaskProvider.configure(
			new Action<Copy>() {

				@Override
				public void execute(Copy deployCopy) {
					final Jar jar = jarTaskProvider.get();

					Object sourcePath = jar;

					if (lazy) {
						sourcePath = new Callable<File>() {

							@Override
							public File call() throws Exception {
								return jar.getArchivePath();
							}

						};
					}

					Closure<Void> copySpecClosure = new Closure<Void>(project) {

						@SuppressWarnings("unused")
						public void doCall(CopySpec copySpec) {
							copySpec.rename(
								new Closure<String>(project) {

									public String doCall(String fileName) {
										Closure<String> closure =
											liferayExtension.
												getDeployedFileNameClosure();

										return closure.call(jar);
									}

								});
						}

					};

					deployCopy.from(sourcePath, copySpecClosure);
				}

			});
	}

	private void _configureTaskDeployProvider(
		TaskProvider<Copy> deployTaskProvider,
		final TaskProvider<Copy> deployDependenciesTaskProvider) {

		deployTaskProvider.configure(
			new Action<Copy>() {

				@Override
				public void execute(Copy deployCopy) {
					deployCopy.finalizedBy(deployDependenciesTaskProvider);
				}

			});
	}

	private void _configureTaskJarProvider(
		final Project project, final BundleExtension bundleExtension,
		TaskProvider<Jar> jarTaskProvider,
		final TaskProvider<Task> zipZippableResourcesTaskProvider) {

		jarTaskProvider.configure(
			new Action<Jar>() {

				@Override
				public void execute(Jar jar) {
					jar.dependsOn(zipZippableResourcesTaskProvider);

					Convention convention = jar.getConvention();

					Map<String, Object> plugins = convention.getPlugins();

					final BundleTaskExtension bundleTaskExtension =
						new BundleTaskExtension(jar);

					plugins.put("bundle", bundleTaskExtension);

					jar.setDescription(
						"Assembles a bundle containing the main classes.");

					jar.doFirst(
						new Action<Task>() {

							@Override
							public void execute(Task task) {
								for (Map.Entry<String, Object> entry :
										bundleExtension.entrySet()) {

									bundleExtension.instruction(
										entry.getKey(),
										GradleUtil.toString(entry.getValue()));
								}

								Map<String, ?> projectProperties =
									project.getProperties();

								for (Map.Entry<String, ?> entry :
										projectProperties.entrySet()) {

									String key = entry.getKey();
									Object value = entry.getValue();

									Matcher matcher = _keyPattern.matcher(key);

									if (matcher.matches() &&
										(value instanceof String)) {

										bundleExtension.instruction(
											key, entry.getValue());
									}
								}

								bundleTaskExtension.setBnd(bundleExtension);
							}

						});

					jar.doLast(bundleTaskExtension.buildAction());

					File bndFile = project.file("bnd.bnd");

					if (!bndFile.exists()) {
						return;
					}

					TaskInputs taskInputs = jar.getInputs();

					taskInputs.file(bndFile);
				}

			});
	}

	private void _configureTaskJavaCompile(
		JavaCompile javaCompile, boolean fork) {

		if (GradleUtil.isRunningInsideDaemon()) {
			CompileOptions compileOptions = javaCompile.getOptions();

			compileOptions.setFork(fork);
		}
	}

	private void _configureTaskJavadocProvider(
		BundleExtension bundleExtension,
		TaskProvider<Javadoc> javadocTaskProvider) {

		String bundleName = bundleExtension.getInstruction(
			Constants.BUNDLE_NAME);
		String bundleVersion = bundleExtension.getInstruction(
			Constants.BUNDLE_VERSION);

		if (Validator.isNull(bundleName) || Validator.isNull(bundleVersion)) {
			return;
		}

		javadocTaskProvider.configure(
			new Action<Javadoc>() {

				@Override
				public void execute(Javadoc javadoc) {
					String title = String.format(
						"%s %s API", bundleName, bundleVersion);

					javadoc.setTitle(title);
				}

			});
	}

	private void _configureTaskTest(Test test) {
		test.setDefaultCharacterEncoding(StandardCharsets.UTF_8.name());
	}

	private void _configureTaskTestProvider(
		TaskProvider<Test> testTaskProvider) {

		testTaskProvider.configure(
			new Action<Test>() {

				@Override
				public void execute(Test test) {
					test.jvmArgs(
						"-Djava.net.preferIPv4Stack=true",
						"-Duser.timezone=GMT");

					test.setForkEvery(1L);
				}

			});
	}

	private void _configureTaskZippableResources(
		final Project project, TaskProvider<Zip> zippableResourcesTaskProvider,
		final File zippableResourcesDir) {

		zippableResourcesTaskProvider.configure(
			new Action<Zip>() {

				@Override
				public void execute(Zip zippableResourcesZip) {
					Provider<RegularFile> provider =
						zippableResourcesZip.getArchiveFile();

					RegularFile regularFile = provider.get();

					File zippableResourcesFile = regularFile.getAsFile();

					StringBuilder sb = new StringBuilder();

					sb.append("Assembles ");
					sb.append(project.relativePath(zippableResourcesFile));
					sb.append(" with the contents of the ");
					sb.append(project.relativePath(zippableResourcesDir));
					sb.append(" directory.");

					zippableResourcesZip.setDescription(sb.toString());

					zippableResourcesZip.from(zippableResourcesDir);

					Property<String> property =
						zippableResourcesZip.getArchiveFileName();

					property.set(zippableResourcesDir.getName() + ".zip");

					DirectoryProperty directoryProperty =
						zippableResourcesZip.getDestinationDirectory();

					directoryProperty.set(project.file("classes"));
				}

			});
	}

	private void _configureTaskZipZippableResources(
		final Project project,
		final List<TaskProvider<Zip>> zippableResourcesTaskProviders,
		TaskProvider<Task> zipZippableResourcesTaskProvider) {

		zipZippableResourcesTaskProvider.configure(
			new Action<Task>() {

				@Override
				public void execute(Task zipZippableResourcesTask) {
					File zippableResourcesDir = project.file(
						"src/main/zippableResources");

					StringBuilder sb = new StringBuilder();

					sb.append("Assembles Zip files from the subdirectories ");
					sb.append(project.relativePath(zippableResourcesDir));
					sb.append('.');

					zipZippableResourcesTask.setDescription(sb.toString());

					for (TaskProvider<Zip> zippableResourcesTaskProvider :
							zippableResourcesTaskProviders) {

						zipZippableResourcesTask.dependsOn(
							zippableResourcesTaskProvider);
					}
				}

			});
	}

	private Map<String, String> _getBuilderProperties(
		Project project, BundleExtension bundleExtension,
		LiferayOSGiExtension liferayOSGiExtension,
		BuildWSDDTask buildWSDDTask) {

		Map<String, String> properties = GradleUtil.toStringMap(
			liferayOSGiExtension.getBundleDefaultInstructions());

		Map<String, ?> projectProperties = project.getProperties();

		for (Map.Entry<String, ?> entry : projectProperties.entrySet()) {
			String key = entry.getKey();
			Object value = entry.getValue();

			if (Character.isLowerCase(key.charAt(0)) && (value != null)) {
				properties.put(key, GradleUtil.toString(value));
			}
		}

		properties.remove(Constants.DONOTCOPY);
		properties.remove(
			LiferayOSGiExtension.
				BUNDLE_DEFAULT_INSTRUCTION_LIFERAY_SERVICE_XML);

		String bundleName = bundleExtension.getInstruction(
			Constants.BUNDLE_NAME);

		if (Validator.isNotNull(bundleName)) {
			properties.put(
				Constants.BUNDLE_NAME, bundleName + " WSDD descriptors");
		}

		String bundleSymbolicName = bundleExtension.getInstruction(
			Constants.BUNDLE_SYMBOLICNAME);

		properties.put(
			Constants.BUNDLE_SYMBOLICNAME, bundleSymbolicName + ".wsdd");
		properties.put(Constants.FRAGMENT_HOST, bundleSymbolicName);

		properties.put(
			Constants.IMPORT_PACKAGE, "jakarta.servlet,jakarta.servlet.http");

		StringBuilder sb = new StringBuilder();

		sb.append("WEB-INF/=");
		sb.append(
			FileUtil.getRelativePath(
				project, buildWSDDTask.getServerConfigFile()));
		sb.append(',');
		sb.append(
			FileUtil.getRelativePath(project, buildWSDDTask.getOutputDir()));
		sb.append(";filter:=*.wsdd");

		properties.put(Constants.INCLUDE_RESOURCE, sb.toString());

		return properties;
	}

	private static final String _CACHE_PLUGIN_ID = "com.liferay.cache";

	private static final Logger _logger = Logging.getLogger(
		LiferayOSGiPlugin.class);

	private static final Pattern _keyPattern = Pattern.compile(
		"[a-z][\\p{Alnum}-_.]*");

}