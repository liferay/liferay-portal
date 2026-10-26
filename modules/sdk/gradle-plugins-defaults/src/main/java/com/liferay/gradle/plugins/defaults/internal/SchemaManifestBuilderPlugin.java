/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.gradle.plugins.defaults.internal;

import com.liferay.gradle.plugins.defaults.internal.util.FileUtil;
import com.liferay.gradle.plugins.defaults.internal.util.GradleUtil;

import org.gradle.api.Action;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.ConfigurationContainer;
import org.gradle.api.artifacts.DependencySet;
import org.gradle.api.plugins.BasePlugin;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.JavaExec;
import org.gradle.api.tasks.TaskProvider;

/**
 * @author Luis Ortiz
 */
public class SchemaManifestBuilderPlugin implements Plugin<Project> {

	public static final String BUILD_SCHEMA_MANIFEST_TASK_NAME =
		"buildSchemaManifest";

	public static final Plugin<Project> INSTANCE =
		new SchemaManifestBuilderPlugin();

	public static final String SCHEMA_MANIFEST_BUILDER_CONFIGURATION_NAME =
		"schemaManifestBuilder";

	@Override
	public void apply(Project project) {

		// Configurations

		ConfigurationContainer configurationContainer =
			project.getConfigurations();

		Configuration schemaManifestBuilderConfiguration =
			configurationContainer.create(
				SCHEMA_MANIFEST_BUILDER_CONFIGURATION_NAME);

		_configureConfigurationSchemaManifestBuilder(
			project, schemaManifestBuilderConfiguration);

		// Tasks

		TaskProvider<JavaExec> buildSchemaManifestTaskProvider =
			GradleUtil.addTaskProvider(
				project, BUILD_SCHEMA_MANIFEST_TASK_NAME, JavaExec.class);

		_configureTaskBuildSchemaManifestProvider(
			project, schemaManifestBuilderConfiguration,
			buildSchemaManifestTaskProvider);
	}

	private SchemaManifestBuilderPlugin() {
	}

	private void _configureConfigurationSchemaManifestBuilder(
		final Project project,
		Configuration schemaManifestBuilderConfiguration) {

		schemaManifestBuilderConfiguration.defaultDependencies(
			new Action<DependencySet>() {

				@Override
				public void execute(DependencySet dependencySet) {
					GradleUtil.addDependency(
						project, SCHEMA_MANIFEST_BUILDER_CONFIGURATION_NAME,
						"com.liferay",
						"com.liferay.portal.tools.schema.manifest.builder",
						"1.0.0");
				}

			});

		schemaManifestBuilderConfiguration.setDescription(
			"Configures Liferay Schema Manifest Builder for this project.");
		schemaManifestBuilderConfiguration.setVisible(false);
	}

	private void _configureTaskBuildSchemaManifestProvider(
		final Project project,
		final Configuration schemaManifestBuilderConfiguration,
		TaskProvider<JavaExec> buildSchemaManifestTaskProvider) {

		buildSchemaManifestTaskProvider.configure(
			new Action<JavaExec>() {

				@Override
				public void execute(JavaExec buildSchemaManifestJavaExec) {
					buildSchemaManifestJavaExec.args(
						"schema.manifest.base.dir=" +
							FileUtil.getAbsolutePath(project.getProjectDir()));

					Property<String> mainClass =
						buildSchemaManifestJavaExec.getMainClass();

					mainClass.set(
						"com.liferay.portal.tools.schema.manifest.builder." +
							"SchemaManifestBuilder");

					buildSchemaManifestJavaExec.setClasspath(
						schemaManifestBuilderConfiguration);
					buildSchemaManifestJavaExec.setDescription(
						"Runs Liferay Schema Manifest Builder to build the " +
							"database changes manifest.");
					buildSchemaManifestJavaExec.setGroup(
						BasePlugin.BUILD_GROUP);
				}

			});
	}

}