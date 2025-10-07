/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.gradle.plugins.jasper.jspc;

import com.liferay.gradle.util.GradleUtil;

import java.io.File;

import java.util.concurrent.Callable;

import org.gradle.api.Action;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.dsl.DependencyHandler;
import org.gradle.api.file.ConfigurableFileCollection;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.plugins.JavaBasePlugin;
import org.gradle.api.plugins.JavaLibraryPlugin;
import org.gradle.api.plugins.JavaPlugin;
import org.gradle.api.plugins.PluginContainer;
import org.gradle.api.plugins.WarPlugin;
import org.gradle.api.plugins.WarPluginConvention;
import org.gradle.api.tasks.Copy;
import org.gradle.api.tasks.SourceSet;
import org.gradle.api.tasks.bundling.War;
import org.gradle.api.tasks.compile.JavaCompile;

/**
 * @author Andrea Di Giorgi
 * @author Gregory Amerson
 */
public class JspCPlugin implements Plugin<Project> {

	public static final String COMPILE_JSP_TASK_NAME = "compileJSP";

	public static final String CONFIGURATION_NAME = "jspC";

	public static final String GENERATE_JSP_JAVA_TASK_NAME = "generateJSPJava";

	@Override
	public void apply(Project project) {
		GradleUtil.applyPlugin(project, JavaLibraryPlugin.class);

		Configuration jspCConfiguration = _addConfigurationJspC(project);

		CompileJSPTask generateJSPJavaTask = _addTaskGenerateJSPJava(
			project, jspCConfiguration);

		final JavaCompile compileJSPTask = _addTaskCompileJSP(
			generateJSPJavaTask, jspCConfiguration);

		project.afterEvaluate(
			new Action<Project>() {

				@Override
				public void execute(Project project) {
					_addDependenciesJspC(project);
					_configureTaskCompileJSP(compileJSPTask);
				}

			});
	}

	private Configuration _addConfigurationJspC(Project project) {
		Configuration configuration = GradleUtil.addConfiguration(
			project, CONFIGURATION_NAME);

		configuration.setDescription(
			"Configures the classpath of the JSP compilation tasks.");
		configuration.setVisible(false);

		return configuration;
	}

	private void _addDependenciesJspC(Project project) {
		GradleUtil.addDependency(
			project, CONFIGURATION_NAME, "jakarta.servlet.jsp.jstl",
			"jakarta.servlet.jsp.jstl-api", "3.0.2");
		GradleUtil.addDependency(
			project, CONFIGURATION_NAME, "org.apache.tomcat", "tomcat-jasper",
			"10.1.43");
		GradleUtil.addDependency(
			project, CONFIGURATION_NAME, "org.glassfish.web",
			"jakarta.servlet.jsp.jstl", "3.0.1", false);
		GradleUtil.addDependency(
			project, CONFIGURATION_NAME, "org.osgi", "osgi.core", "6.0.0");

		DependencyHandler dependencyHandler = project.getDependencies();

		JavaCompile javaCompile = (JavaCompile)GradleUtil.getTask(
			project, JavaPlugin.COMPILE_JAVA_TASK_NAME);

		ConfigurableFileCollection configurableFileCollection = project.files(
			javaCompile);

		configurableFileCollection.builtBy(javaCompile);

		dependencyHandler.add(CONFIGURATION_NAME, configurableFileCollection);

		Copy copy = (Copy)GradleUtil.getTask(
			project, JavaPlugin.PROCESS_RESOURCES_TASK_NAME);

		configurableFileCollection = project.files(copy);

		configurableFileCollection.builtBy(copy);

		dependencyHandler.add(CONFIGURATION_NAME, configurableFileCollection);

		Configuration configuration = GradleUtil.getConfiguration(
			project, CONFIGURATION_NAME);

		SourceSet sourceSet = GradleUtil.getSourceSet(
			project, SourceSet.MAIN_SOURCE_SET_NAME);

		Configuration compileClasspathConfiguration =
			GradleUtil.getConfiguration(
				project, sourceSet.getCompileClasspathConfigurationName());

		configuration.extendsFrom(compileClasspathConfiguration);
	}

	private JavaCompile _addTaskCompileJSP(
		CompileJSPTask generateJSPJavaTask, Configuration jspCConfiguration) {

		JavaCompile javaCompile = GradleUtil.addTask(
			generateJSPJavaTask.getProject(), COMPILE_JSP_TASK_NAME,
			JavaCompile.class);

		javaCompile.setClasspath(jspCConfiguration);
		javaCompile.setDescription("Compile JSP files to check for errors.");
		javaCompile.setGroup(JavaBasePlugin.VERIFICATION_GROUP);
		javaCompile.setSource(generateJSPJavaTask.getOutputs());

		Project project = generateJSPJavaTask.getProject();

		PluginContainer pluginContainer = project.getPlugins();

		pluginContainer.withType(
			WarPlugin.class,
			new Action<WarPlugin>() {

				@Override
				public void execute(WarPlugin warPlugin) {
					_configureTaskCompileJSPForWarPlugin(javaCompile);
				}

			});

		return javaCompile;
	}

	private CompileJSPTask _addTaskGenerateJSPJava(
		Project project, Configuration jspCConfiguration) {

		final CompileJSPTask compileJSPTask = GradleUtil.addTask(
			project, GENERATE_JSP_JAVA_TASK_NAME, CompileJSPTask.class);

		compileJSPTask.setDescription(
			"Compiles JSP files to Java source files to check for errors.");

		compileJSPTask.setDestinationDir(
			new Callable<File>() {

				@Override
				public File call() throws Exception {
					Project project = compileJSPTask.getProject();

					return new File(project.getBuildDir(), "jspc");
				}

			});

		compileJSPTask.setJspCClasspath(jspCConfiguration);

		PluginContainer pluginContainer = project.getPlugins();

		pluginContainer.withType(
			WarPlugin.class,
			new Action<WarPlugin>() {

				@Override
				public void execute(WarPlugin warPlugin) {
					_configureTaskGenerateJSPJavaForWarPlugin(compileJSPTask);
				}

			});

		return compileJSPTask;
	}

	private void _configureTaskCompileJSP(JavaCompile compileJSPTask) {
		JavaCompile javaCompile = (JavaCompile)GradleUtil.getTask(
			compileJSPTask.getProject(), JavaPlugin.COMPILE_JAVA_TASK_NAME);

		compileJSPTask.dependsOn(javaCompile);

		DirectoryProperty directoryProperty =
			compileJSPTask.getDestinationDirectory();

		if (directoryProperty.getOrNull() == null) {
			directoryProperty.set(compileJSPTask.getTemporaryDir());
		}
	}

	private void _configureTaskCompileJSPForWarPlugin(
		JavaCompile compileJSPTask) {

		War war = (War)GradleUtil.getTask(
			compileJSPTask.getProject(), WarPlugin.WAR_TASK_NAME);

		war.dependsOn(compileJSPTask);

		war.from(compileJSPTask);
	}

	private void _configureTaskGenerateJSPJavaForWarPlugin(
		final CompileJSPTask compileJSPTask) {

		compileJSPTask.setWebAppDir(
			new Callable<File>() {

				@Override
				public File call() throws Exception {
					WarPluginConvention warPluginConvention =
						GradleUtil.getConvention(
							compileJSPTask.getProject(),
							WarPluginConvention.class);

					return warPluginConvention.getWebAppDir();
				}

			});
	}

}