/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.gradle.plugins.app.javadoc.builder;

import com.liferay.gradle.util.FileUtil;
import com.liferay.gradle.util.GradleUtil;
import com.liferay.gradle.util.Validator;

import groovy.lang.Closure;

import java.io.File;

import java.nio.charset.StandardCharsets;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.Callable;

import org.gradle.api.Action;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.file.FileCollection;
import org.gradle.api.file.FileTree;
import org.gradle.api.file.SourceDirectorySet;
import org.gradle.api.internal.ConventionMapping;
import org.gradle.api.invocation.Gradle;
import org.gradle.api.logging.Logger;
import org.gradle.api.plugins.BasePlugin;
import org.gradle.api.plugins.JavaBasePlugin;
import org.gradle.api.plugins.JavaPlugin;
import org.gradle.api.plugins.ReportingBasePlugin;
import org.gradle.api.provider.Property;
import org.gradle.api.reporting.ReportingExtension;
import org.gradle.api.specs.Spec;
import org.gradle.api.tasks.SourceSet;
import org.gradle.api.tasks.TaskContainer;
import org.gradle.api.tasks.bundling.Jar;
import org.gradle.api.tasks.javadoc.Javadoc;
import org.gradle.external.javadoc.CoreJavadocOptions;
import org.gradle.external.javadoc.StandardJavadocDocletOptions;

/**
 * @author Andrea Di Giorgi
 */
public class AppJavadocBuilderPlugin implements Plugin<Project> {

	public static final String APP_JAVADOC_TASK_NAME = "appJavadoc";

	public static final String JAR_APP_JAVADOC_TASK_NAME = "jarAppJavadoc";

	public static final String PLUGIN_NAME = "appJavadocBuilder";

	@Override
	public void apply(Project project) {
		GradleUtil.applyPlugin(project, BasePlugin.class);
		GradleUtil.applyPlugin(project, ReportingBasePlugin.class);

		final AppJavadocBuilderExtension appJavadocBuilderExtension =
			GradleUtil.addExtension(
				project, PLUGIN_NAME, AppJavadocBuilderExtension.class);

		ReportingExtension reportingExtension = GradleUtil.getExtension(
			project, ReportingExtension.class);

		final Javadoc appJavadocTask = _addTaskAppJavadoc(
			project, reportingExtension);

		_addTaskJarAppJavadoc(appJavadocTask);

		Gradle gradle = project.getGradle();

		gradle.afterProject(
			new Closure<Void>(project) {

				@SuppressWarnings("unused")
				public void doCall(Project subproject) {
					Set<Project> subprojects =
						appJavadocBuilderExtension.getSubprojects();

					if (subprojects.contains(subproject)) {
						_configureTaskAppJavadoc(
							appJavadocTask, appJavadocBuilderExtension,
							subproject);
					}
				}

			});

		project.afterEvaluate(
			new Action<Project>() {

				@Override
				public void execute(Project project) {
					_configureTaskAppJavadoc(
						appJavadocTask, appJavadocBuilderExtension);
				}

			});
	}

	private Javadoc _addTaskAppJavadoc(
		Project project, final ReportingExtension reportingExtension) {

		final Javadoc javadoc = GradleUtil.addTask(
			project, APP_JAVADOC_TASK_NAME, Javadoc.class);

		javadoc.setDescription(
			"Generates Javadoc API documentation for the app.");
		javadoc.setGroup(JavaBasePlugin.DOCUMENTATION_GROUP);

		ConventionMapping conventionMapping = javadoc.getConventionMapping();

		conventionMapping.map(
			"destinationDir",
			new Callable<File>() {

				@Override
				public File call() throws Exception {
					Project project = javadoc.getProject();

					return new File(project.getBuildDir(), "docs/javadoc");
				}

			});

		conventionMapping.map(
			"title",
			new Callable<String>() {

				@Override
				public String call() throws Exception {
					return reportingExtension.getApiDocTitle();
				}

			});

		StandardJavadocDocletOptions standardJavadocDocletOptions =
			(StandardJavadocDocletOptions)javadoc.getOptions();

		standardJavadocDocletOptions.setEncoding(StandardCharsets.UTF_8.name());

		// Replace default map to sort groups alphabetically

		standardJavadocDocletOptions.setGroups(
			new TreeMap<String, List<String>>());

		return javadoc;
	}

	private Jar _addTaskJarAppJavadoc(Javadoc javadoc) {
		Jar jar = GradleUtil.addTask(
			javadoc.getProject(), JAR_APP_JAVADOC_TASK_NAME, Jar.class);

		jar.from(javadoc);
		jar.setDescription(
			"Assembles a jar archive containing the Javadoc files for this " +
				"app.");
		jar.setGroup(BasePlugin.BUILD_GROUP);

		Property<String> property = jar.getArchiveClassifier();

		property.set("javadoc");

		return jar;
	}

	private void _configureTaskAppJavadoc(
		Javadoc javadoc,
		AppJavadocBuilderExtension appJavadocBuilderExtension) {

		if (appJavadocBuilderExtension.isDoclintDisabled()) {
			CoreJavadocOptions coreJavadocOptions =
				(CoreJavadocOptions)javadoc.getOptions();

			coreJavadocOptions.addStringOption("Xdoclint:none", "-quiet");
		}
	}

	private void _configureTaskAppJavadoc(
		Javadoc javadoc, AppJavadocBuilderExtension appJavadocBuilderExtension,
		Project subproject) {

		Logger logger = javadoc.getLogger();

		TaskContainer taskContainer = subproject.getTasks();

		Task task = taskContainer.findByName(JavaPlugin.JAVADOC_TASK_NAME);

		if (!(task instanceof Javadoc)) {
			if (logger.isInfoEnabled()) {
				logger.info(
					"Excluding {} from {} because it is not a valid Java " +
						"project",
					subproject, javadoc, JavaPlugin.JAVADOC_TASK_NAME);
			}

			return;
		}

		Spec<Project> spec = appJavadocBuilderExtension.getOnlyIf();

		if (!spec.isSatisfiedBy(subproject)) {
			if (logger.isInfoEnabled()) {
				logger.info(
					"Explicitly excluding {} from {}", subproject, javadoc);
			}

			return;
		}

		Javadoc subprojectJavadoc = (Javadoc)task;

		javadoc.dependsOn(subprojectJavadoc);

		FileCollection classpath = javadoc.getClasspath();

		javadoc.setClasspath(classpath.plus(subprojectJavadoc.getClasspath()));

		FileTree subprojectSource = subprojectJavadoc.getSource();

		javadoc.source(subprojectSource);

		StandardJavadocDocletOptions standardJavadocDocletOptions =
			(StandardJavadocDocletOptions)javadoc.getOptions();

		if (appJavadocBuilderExtension.isCopyTags()) {
			StandardJavadocDocletOptions
				subprojectStandardJavadocDocletOptions =
					(StandardJavadocDocletOptions)
						subprojectJavadoc.getOptions();

			standardJavadocDocletOptions.tags(
				subprojectStandardJavadocDocletOptions.getTags());
		}

		if (appJavadocBuilderExtension.isGroupPackages()) {
			SourceSet sourceSet = GradleUtil.getSourceSet(
				subproject, SourceSet.MAIN_SOURCE_SET_NAME);

			SourceDirectorySet sourceDirectorySet = sourceSet.getAllJava();

			Closure<String> closure =
				appJavadocBuilderExtension.getGroupNameClosure();

			String groupName = closure.call(subproject);

			Set<String> packageNames = _getPackageNames(
				subprojectSource, sourceDirectorySet.getSrcDirs());

			if (Validator.isNotNull(groupName) && !packageNames.isEmpty()) {
				standardJavadocDocletOptions.group(
					groupName, packageNames.toArray(new String[0]));
			}
		}
	}

	private String _getPackageName(File file, Set<File> srcDirs) {
		File dir = null;

		for (File srcDir : srcDirs) {
			if (FileUtil.isChild(file, srcDir)) {
				dir = srcDir;

				break;
			}
		}

		if (dir == null) {
			return null;
		}

		String relativePath = FileUtil.relativize(file, dir);

		relativePath = relativePath.substring(
			0, relativePath.lastIndexOf(File.separatorChar));

		return relativePath.replace(File.separatorChar, '.');
	}

	private Set<String> _getPackageNames(
		Iterable<File> files, Set<File> srcDirs) {

		Set<String> packageNames = new HashSet<>();

		for (File file : files) {
			String packageName = _getPackageName(file, srcDirs);

			if (Validator.isNull(packageName)) {
				continue;
			}

			packageNames.add(packageName);
		}

		return packageNames;
	}

}