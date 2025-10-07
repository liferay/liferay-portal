/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.gradle.plugins.defaults.internal;

import com.liferay.gradle.plugins.defaults.internal.util.FileUtil;
import com.liferay.gradle.plugins.defaults.internal.util.GradleUtil;
import com.liferay.gradle.plugins.defaults.task.WriteFindBugsProjectTask;
import com.liferay.gradle.plugins.jasper.jspc.CompileJSPTask;
import com.liferay.gradle.plugins.jasper.jspc.JspCPlugin;

import java.io.File;

import java.nio.file.Path;

import java.util.Set;
import java.util.concurrent.Callable;

import org.gradle.api.Action;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.Transformer;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.ConfigurationContainer;
import org.gradle.api.artifacts.DependencySet;
import org.gradle.api.file.FileCollection;
import org.gradle.api.file.SourceDirectorySet;
import org.gradle.api.logging.Logger;
import org.gradle.api.plugins.Convention;
import org.gradle.api.plugins.JavaBasePlugin;
import org.gradle.api.plugins.JavaPlugin;
import org.gradle.api.plugins.JavaPluginConvention;
import org.gradle.api.provider.Property;
import org.gradle.api.reporting.ReportingExtension;
import org.gradle.api.specs.Spec;
import org.gradle.api.tasks.JavaExec;
import org.gradle.api.tasks.SourceSet;
import org.gradle.api.tasks.SourceSetContainer;
import org.gradle.api.tasks.TaskProvider;
import org.gradle.api.tasks.compile.JavaCompile;
import org.gradle.language.base.plugins.LifecycleBasePlugin;

/**
 * @author Andrea Di Giorgi
 */
public class FindSecurityBugsPlugin implements Plugin<Project> {

	public static final String FIND_SECURITY_BUGS_CONFIGURATION_NAME =
		"findSecurityBugs";

	public static final String FIND_SECURITY_BUGS_PLUGINS_CONFIGURATION_NAME =
		"findSecurityBugsPlugins";

	public static final String FIND_SECURITY_BUGS_TASK_NAME =
		"findSecurityBugs";

	public static final Plugin<Project> INSTANCE = new FindSecurityBugsPlugin();

	public static final String PRINT_FIND_SECURITY_BUGS_REPORT_TASK_NAME =
		"printFindSecurityBugsReport";

	public static final String WRITE_FIND_BUGS_PROJECT_TASK_NAME =
		"writeFindBugsProject";

	@Override
	public void apply(Project project) {

		// Configurations

		ConfigurationContainer configurationContainer =
			project.getConfigurations();

		Configuration findSecurityBugsConfiguration =
			configurationContainer.create(
				FIND_SECURITY_BUGS_CONFIGURATION_NAME);
		Configuration findSecurityBugsPluginsConfiguration =
			configurationContainer.create(
				FIND_SECURITY_BUGS_PLUGINS_CONFIGURATION_NAME);

		_configureConfigurationFindSecurityBugs(
			project, findSecurityBugsConfiguration);
		_configureConfigurationFindSecurityBugsPlugins(
			project, findSecurityBugsPluginsConfiguration);

		// Conventions

		Convention convention = project.getConvention();

		JavaPluginConvention javaPluginConvention = convention.getPlugin(
			JavaPluginConvention.class);

		// Tasks

		TaskProvider<JavaExec> findSecurityBugsTaskProvider =
			GradleUtil.addTaskProvider(
				project, FIND_SECURITY_BUGS_TASK_NAME, JavaExec.class);
		TaskProvider<Task> printFindSecurityBugsReportTaskProvider =
			GradleUtil.addTaskProvider(
				project, PRINT_FIND_SECURITY_BUGS_REPORT_TASK_NAME, Task.class);
		TaskProvider<WriteFindBugsProjectTask>
			writeFindBugsProjectTaskProvider = GradleUtil.addTaskProvider(
				project, WRITE_FIND_BUGS_PROJECT_TASK_NAME,
				WriteFindBugsProjectTask.class);

		TaskProvider<Task> checkTaskProvider = GradleUtil.getTaskProvider(
			project, LifecycleBasePlugin.CHECK_TASK_NAME);
		TaskProvider<Task> classesTaskProvider = GradleUtil.getTaskProvider(
			project, JavaPlugin.CLASSES_TASK_NAME);
		TaskProvider<JavaCompile> compileJSPTaskProvider =
			GradleUtil.getTaskProvider(
				project, JspCPlugin.COMPILE_JSP_TASK_NAME, JavaCompile.class);
		TaskProvider<CompileJSPTask> generateJSPJavaTaskProvider =
			GradleUtil.getTaskProvider(
				project, JspCPlugin.GENERATE_JSP_JAVA_TASK_NAME,
				CompileJSPTask.class);

		_configureTaskCheckProvider(
			checkTaskProvider, findSecurityBugsTaskProvider);
		_configureTaskFindSecurityBugsProvider(
			project, findSecurityBugsConfiguration,
			findSecurityBugsPluginsConfiguration, findSecurityBugsTaskProvider,
			printFindSecurityBugsReportTaskProvider,
			writeFindBugsProjectTaskProvider);
		_configureTaskPrintFindSecurityBugsReportProvider(
			findSecurityBugsTaskProvider,
			printFindSecurityBugsReportTaskProvider);
		_configureTaskWriteFindBugsProjectProvider(
			project, javaPluginConvention, classesTaskProvider,
			compileJSPTaskProvider, generateJSPJavaTaskProvider,
			writeFindBugsProjectTaskProvider);
	}

	private FindSecurityBugsPlugin() {
	}

	private void _configureConfigurationFindSecurityBugs(
		final Project project, Configuration findSecurityBugsConfiguration) {

		findSecurityBugsConfiguration.defaultDependencies(
			new Action<DependencySet>() {

				@Override
				public void execute(DependencySet dependencySet) {
					GradleUtil.addDependency(
						project, FIND_SECURITY_BUGS_CONFIGURATION_NAME,
						"com.github.spotbugs", "spotbugs", "4.9.3");
				}

			});

		findSecurityBugsConfiguration.setDescription(
			"Configures FindBugs for the '" + FIND_SECURITY_BUGS_TASK_NAME +
				"' task.");
		findSecurityBugsConfiguration.setVisible(false);
	}

	private void _configureConfigurationFindSecurityBugsPlugins(
		final Project project,
		Configuration findSecurityBugsPluginsConfiguration) {

		findSecurityBugsPluginsConfiguration.defaultDependencies(
			new Action<DependencySet>() {

				@Override
				public void execute(DependencySet dependencySet) {
					GradleUtil.addDependency(
						project, FIND_SECURITY_BUGS_PLUGINS_CONFIGURATION_NAME,
						"com.liferay", "com.h3xstream.findsecbugs", _VERSION);
				}

			});

		findSecurityBugsPluginsConfiguration.setDescription(
			"Configures FindSecurityBugs.");
		findSecurityBugsPluginsConfiguration.setVisible(false);
	}

	private void _configureTaskCheckProvider(
		TaskProvider<Task> checkTaskProvider,
		final TaskProvider<JavaExec> findSecurityBugsTaskProvider) {

		checkTaskProvider.configure(
			new Action<Task>() {

				@Override
				public void execute(Task checkTask) {
					checkTask.dependsOn(findSecurityBugsTaskProvider);
				}

			});
	}

	private void _configureTaskFindSecurityBugsProvider(
		final Project project,
		final Configuration findSecurityBugsConfiguration,
		final Configuration findSecurityBugsPluginsConfiguration,
		TaskProvider<JavaExec> findSecurityBugsTaskProvider,
		final TaskProvider<Task> printFindSecurityBugsReportTaskProvider,
		final TaskProvider<WriteFindBugsProjectTask>
			writeFindBugsProjectTaskProvider) {

		findSecurityBugsTaskProvider.configure(
			new Action<JavaExec>() {

				@Override
				public void execute(JavaExec findSecurityBugsJavaExec) {
					findSecurityBugsJavaExec.finalizedBy(
						printFindSecurityBugsReportTaskProvider);

					findSecurityBugsJavaExec.args(
						"-bugCategories", "SECURITY", "-effort:max",
						"-exitcode", "-html", "-medium", "-progress",
						"-timestampNow");

					File excludeDir = GradleUtil.getRootDir(
						project, _FIND_SECURITY_BUGS_EXCLUDE_FILE_NAME);

					if (excludeDir != null) {
						File excludeFile = new File(
							excludeDir, _FIND_SECURITY_BUGS_EXCLUDE_FILE_NAME);

						findSecurityBugsJavaExec.args(
							"-exclude", FileUtil.getAbsolutePath(excludeFile));
					}

					File includeDir = GradleUtil.getRootDir(
						project, _FIND_SECURITY_BUGS_INCLUDE_FILE_NAME);

					if (includeDir != null) {
						File includeFile = new File(
							includeDir, _FIND_SECURITY_BUGS_INCLUDE_FILE_NAME);

						findSecurityBugsJavaExec.args(
							"-include", FileUtil.getAbsolutePath(includeFile));
					}

					final WriteFindBugsProjectTask writeFindBugsProjectTask =
						writeFindBugsProjectTaskProvider.get();

					findSecurityBugsJavaExec.args(
						"-project",
						new Object() {

							@Override
							public String toString() {
								return FileUtil.getAbsolutePath(
									writeFindBugsProjectTask.getOutputFile());
							}

						});

					findSecurityBugsJavaExec.doFirst(
						new Action<Task>() {

							@Override
							public void execute(Task task) {
								JavaExec javaExec = (JavaExec)task;

								Logger logger = javaExec.getLogger();

								File outputFile = _reportsFileGetter.transform(
									javaExec);

								File outputDir = outputFile.getParentFile();

								outputDir.mkdirs();

								javaExec.args(
									"-outputFile",
									FileUtil.getAbsolutePath(outputFile));

								javaExec.args(
									"-pluginList",
									findSecurityBugsPluginsConfiguration.
										getAsPath());

								if (logger.isLifecycleEnabled()) {
									logger.lifecycle(
										"Using Find Security Bugs version " +
											_VERSION);
								}
							}

						});

					findSecurityBugsJavaExec.dependsOn(
						writeFindBugsProjectTask);

					findSecurityBugsJavaExec.onlyIf(
						new Spec<Task>() {

							@Override
							public boolean isSatisfiedBy(Task task) {
								FileCollection fileCollection =
									writeFindBugsProjectTask.getClasspath();

								if (fileCollection == null) {
									return true;
								}

								Set<File> files = fileCollection.getFiles();

								return _containsClassOrJar(
									files.toArray(new File[0]));
							}

							private boolean _containsClassOrJar(File[] files) {
								for (File file : files) {
									if (!file.exists()) {
										continue;
									}

									if (file.isFile()) {
										String fileName = file.getName();

										if (fileName.endsWith(".class") ||
											fileName.endsWith(".jar")) {

											return true;
										}
									}
									else if (_containsClassOrJar(
												file.listFiles())) {

										return true;
									}
								}

								return false;
							}

						});

					Property<String> mainClass =
						findSecurityBugsJavaExec.getMainClass();

					mainClass.set("edu.umd.cs.findbugs.FindBugs2");

					findSecurityBugsJavaExec.setClasspath(
						findSecurityBugsConfiguration);
					findSecurityBugsJavaExec.setDebug(
						Boolean.getBoolean("findSecurityBugs.debug"));
					findSecurityBugsJavaExec.setDescription(
						"Runs FindSecurityBugs on this project.");
					findSecurityBugsJavaExec.setGroup(
						JavaBasePlugin.VERIFICATION_GROUP);
					findSecurityBugsJavaExec.setIgnoreExitValue(true);

					findSecurityBugsJavaExec.systemProperty(
						"findsecbugs.injection.customconfigfile." +
							"PathTraversalDetector",
						"liferay-config/liferay-PathTraversalDetector-" +
							"PATH_TRAVERSAL_IN.txt|PATH_TRAVERSAL_IN:" +
								"liferay-config/liferay-" +
									"PathTraversalDetector-" +
										"PATH_TRAVERSAL_OUT.txt|" +
											"PATH_TRAVERSAL_OUT");
					findSecurityBugsJavaExec.systemProperty(
						"findsecbugs.injection.customconfigfile." +
							"SqlInjectionDetector",
						"liferay-config/liferay-SqlInjectionDetector.txt|" +
							"SQL_INJECTION_HIBERNATE");
					findSecurityBugsJavaExec.systemProperty(
						"findsecbugs.injection.customconfigfile.SSRFDetector",
						"liferay-config/liferay-SSRFDetector.txt|" +
							"URLCONNECTION_SSRF_FD");
					findSecurityBugsJavaExec.systemProperty(
						"findsecbugs.injection.customconfigfile.XssJspDetector",
						"liferay-config/liferay-XssJspDetector.txt|" +
							"XSS_JSP_PRINT");
					findSecurityBugsJavaExec.systemProperty(
						"findsecbugs.injection.customconfigfile." +
							"XssServletDetector",
						"liferay-config/liferay-XssServletDetector.txt|" +
							"XSS_SERVLET");

					findSecurityBugsJavaExec.systemProperty(
						"findsecbugs.taint.outputconfigs", "true");

					String customConfigFile = "liferay-config/liferay.txt";

					File directoryFile = project.file(".");

					Path directoryPath = directoryFile.toPath();

					while (directoryPath != null) {
						File falsePositivesTxtFile = new File(
							directoryPath.toFile(),
							"find-security-bugs-false-positives.txt");

						if (falsePositivesTxtFile.exists()) {
							String absolutePath = FileUtil.getAbsolutePath(
								falsePositivesTxtFile);

							customConfigFile +=
								File.pathSeparator + absolutePath;
						}

						directoryPath = directoryPath.getParent();
					}

					findSecurityBugsJavaExec.systemProperty(
						"findsecbugs.taint.customconfigfile", customConfigFile);
				}

			});
	}

	private void _configureTaskPrintFindSecurityBugsReportProvider(
		final TaskProvider<JavaExec> findSecurityBugsTaskProvider,
		TaskProvider<Task> printFindSecurityBugsReportTaskProvider) {

		printFindSecurityBugsReportTaskProvider.configure(
			new Action<Task>() {

				@Override
				public void execute(Task printFindSecurityBugsReportTask) {
					printFindSecurityBugsReportTask.doLast(
						new Action<Task>() {

							@Override
							public void execute(Task task) {
								Logger logger = task.getLogger();

								if (logger.isLifecycleEnabled()) {
									JavaExec findSecurityBugsJavaExec =
										findSecurityBugsTaskProvider.get();

									File outputFile =
										_reportsFileGetter.transform(
											findSecurityBugsJavaExec);

									logger.lifecycle(
										"Find Security Bugs report saved to {}",
										outputFile.getAbsolutePath());
								}
							}

						});

					printFindSecurityBugsReportTask.setDescription(
						"Prints the path of the Find Security Bugs report.");
				}

			});
	}

	private void _configureTaskWriteFindBugsProjectProvider(
		final Project project, final JavaPluginConvention javaPluginConvention,
		final TaskProvider<Task> classesTaskProvider,
		final TaskProvider<JavaCompile> compileJSPTaskProvider,
		final TaskProvider<CompileJSPTask> generateJSPJavaTaskProvider,
		TaskProvider<WriteFindBugsProjectTask>
			writeFindBugsProjectTaskProvider) {

		writeFindBugsProjectTaskProvider.configure(
			new Action<WriteFindBugsProjectTask>() {

				@Override
				public void execute(
					WriteFindBugsProjectTask writeFindBugsProjectTask) {

					writeFindBugsProjectTask.dependsOn(classesTaskProvider);
					writeFindBugsProjectTask.dependsOn(
						generateJSPJavaTaskProvider);
					writeFindBugsProjectTask.dependsOn(compileJSPTaskProvider);

					SourceSet mainSourceSet = _getSourceSet(
						javaPluginConvention, SourceSet.MAIN_SOURCE_SET_NAME);

					SourceDirectorySet javaSourceDirectorySet =
						mainSourceSet.getJava();

					final JavaCompile compileJSPJavaCompile =
						compileJSPTaskProvider.get();

					writeFindBugsProjectTask.setAuxClasspath(
						project.files(
							mainSourceSet.getCompileClasspath(),
							compileJSPJavaCompile.getClasspath()));
					writeFindBugsProjectTask.setClasspath(
						project.files(
							new Callable<File>() {

								@Override
								public File call() throws Exception {
									return compileJSPJavaCompile.
										getDestinationDir();
								}

							},
							new Callable<File>() {

								@Override
								public File call() throws Exception {
									return FileUtil.getJavaClassesDir(
										mainSourceSet);
								}

							}));

					writeFindBugsProjectTask.setDescription(
						"Writes the FindBugs project file.");
					writeFindBugsProjectTask.setOutputFile(
						new Callable<File>() {

							@Override
							public File call() throws Exception {
								return new File(
									project.getBuildDir(), "findbugs.xml");
							}

						});
					writeFindBugsProjectTask.setProjectName(project.getName());

					CompileJSPTask generateJSPJavaCompileJSPTask =
						generateJSPJavaTaskProvider.get();

					writeFindBugsProjectTask.setSrcDirs(
						project.files(
							javaSourceDirectorySet.getSrcDirs(),
							generateJSPJavaCompileJSPTask.getDestinationDir()));
				}

			});
	}

	private SourceSet _getSourceSet(
		JavaPluginConvention javaPluginConvention, String name) {

		SourceSetContainer sourceSetContainer =
			javaPluginConvention.getSourceSets();

		return sourceSetContainer.getByName(name);
	}

	private static final String _FIND_SECURITY_BUGS_EXCLUDE_FILE_NAME =
		"fsb-exclude.xml";

	private static final String _FIND_SECURITY_BUGS_INCLUDE_FILE_NAME =
		"fsb-include.xml";

	private static final String _VERSION = "1.13.0.LIFERAY-PATCHED-1";

	private static final Transformer<File, Task> _reportsFileGetter =
		new Transformer<File, Task>() {

			@Override
			public File transform(Task task) {
				ReportingExtension reportingExtension = GradleUtil.getExtension(
					task.getProject(), ReportingExtension.class);

				return new File(
					reportingExtension.getBaseDir(),
					task.getName() + "/reports.html");
			}

		};

}