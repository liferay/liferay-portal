/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.gradle.plugins;

import com.liferay.gradle.plugins.extensions.AppServer;
import com.liferay.gradle.plugins.extensions.LiferayExtension;
import com.liferay.gradle.plugins.internal.LangBuilderDefaultsPlugin;
import com.liferay.gradle.plugins.internal.util.FileUtil;
import com.liferay.gradle.plugins.internal.util.GradleUtil;
import com.liferay.gradle.plugins.task.DirectDeployTask;
import com.liferay.gradle.plugins.task.DockerCopyTask;
import com.liferay.gradle.plugins.util.PortalTools;
import com.liferay.gradle.util.Validator;

import java.io.File;

import java.net.URL;

import java.util.concurrent.Callable;

import org.gradle.api.Action;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.ConfigurationContainer;
import org.gradle.api.artifacts.DependencyResolveDetails;
import org.gradle.api.artifacts.DependencySet;
import org.gradle.api.artifacts.ModuleVersionSelector;
import org.gradle.api.artifacts.ResolutionStrategy;
import org.gradle.api.file.FileCollection;
import org.gradle.api.file.FileTree;
import org.gradle.api.logging.Logger;
import org.gradle.api.plugins.BasePlugin;
import org.gradle.api.plugins.ExtensionContainer;
import org.gradle.api.tasks.Copy;
import org.gradle.api.tasks.TaskContainer;
import org.gradle.api.tasks.TaskProvider;

/**
 * @author Andrea Di Giorgi
 */
public class LiferayBasePlugin implements Plugin<Project> {

	public static final String DEPLOY_TASK_NAME = "deploy";

	public static final String DOCKER_COPY_TASK_NAME = "dockerCopy";

	public static final String PORTAL_CONFIGURATION_NAME = "portal";

	@Override
	public void apply(Project project) {

		// Plugins

		GradleUtil.applyPlugin(project, NodeDefaultsPlugin.class);

		LangBuilderDefaultsPlugin.INSTANCE.apply(project);
		SourceFormatterDefaultsPlugin.INSTANCE.apply(project);

		// Extensions

		ExtensionContainer extensionContainer = project.getExtensions();

		final LiferayExtension liferayExtension = extensionContainer.create(
			LiferayPlugin.PLUGIN_NAME, LiferayExtension.class, project);

		// Configurations

		ConfigurationContainer configurationContainer =
			project.getConfigurations();

		Configuration portalConfiguration = configurationContainer.create(
			PORTAL_CONFIGURATION_NAME);

		_configureConfigurationPortal(
			project, liferayExtension, portalConfiguration);

		// Tasks

		TaskProvider<Copy> deployTaskProvider = GradleUtil.addTaskProvider(
			project, DEPLOY_TASK_NAME, Copy.class);

		_configureTaskDeployProvider(
			project, liferayExtension, deployTaskProvider);

		String dockerContainerId = GradleUtil.getTaskPrefixedProperty(
			project.getPath(), deployTaskProvider.getName(),
			"docker.container.id");
		String dockerFilesDir = GradleUtil.getTaskPrefixedProperty(
			project.getPath(), deployTaskProvider.getName(),
			"docker.files.dir");

		if (Validator.isNotNull(dockerContainerId)) {
			TaskProvider<DockerCopyTask> dockerCopyTaskProvider =
				GradleUtil.addTaskProvider(
					project, DOCKER_COPY_TASK_NAME, DockerCopyTask.class);

			_configureTaskDeployProvider(
				liferayExtension, deployTaskProvider, dockerCopyTaskProvider,
				dockerContainerId, dockerFilesDir);
		}

		// Containers

		TaskContainer taskContainer = project.getTasks();

		taskContainer.withType(
			DirectDeployTask.class,
			new Action<DirectDeployTask>() {

				@Override
				public void execute(DirectDeployTask directDeployTask) {
					_configureTaskDirectDeploy(
						directDeployTask, liferayExtension);
				}

			});

		// Other

		GradleUtil.applyScript(
			project, _getScriptLiferayExtension(project), project);

		configurationContainer.all(
			new Action<Configuration>() {

				@Override
				public void execute(Configuration configuration) {
					ResolutionStrategy resolutionStrategy =
						configuration.getResolutionStrategy();

					resolutionStrategy.eachDependency(
						new Action<DependencyResolveDetails>() {

							@Override
							public void execute(
								DependencyResolveDetails
									dependencyResolveDetails) {

								ModuleVersionSelector moduleVersionSelector =
									dependencyResolveDetails.getRequested();

								String version =
									moduleVersionSelector.getVersion();

								if (version.equals("default")) {
									dependencyResolveDetails.useVersion(
										liferayExtension.getDefaultVersion(
											moduleVersionSelector));
								}
							}

						});
				}

			});
	}

	private void _configureConfigurationPortal(
		final Project project, final LiferayExtension liferayExtension,
		Configuration portalConfiguration) {

		portalConfiguration.setDescription(
			"Configures the classpath from the local Liferay bundle.");
		portalConfiguration.setVisible(false);

		portalConfiguration.defaultDependencies(
			new Action<DependencySet>() {

				@Override
				public void execute(DependencySet dependencySet) {
					File appServerClassesPortalDir = new File(
						liferayExtension.getAppServerPortalDir(),
						"WEB-INF/classes");

					GradleUtil.addDependency(
						project, PORTAL_CONFIGURATION_NAME,
						appServerClassesPortalDir);

					File appServerLibPortalDir = new File(
						liferayExtension.getAppServerPortalDir(),
						"WEB-INF/lib");

					FileTree appServerLibPortalDirJarFiles =
						FileUtil.getJarsFileTree(
							project, appServerLibPortalDir);

					GradleUtil.addDependency(
						project, PORTAL_CONFIGURATION_NAME,
						appServerLibPortalDirJarFiles);

					File appServerShieldedContainerLibPortalDir = new File(
						liferayExtension.getAppServerPortalDir(),
						"WEB-INF/shielded-container-lib");

					FileTree appServerShieldedContainerLibPortalDirJarFiles =
						FileUtil.getJarsFileTree(
							project, appServerShieldedContainerLibPortalDir);

					GradleUtil.addDependency(
						project, PORTAL_CONFIGURATION_NAME,
						appServerShieldedContainerLibPortalDirJarFiles);

					FileTree appServerLibGlobalDirJarFiles =
						FileUtil.getJarsFileTree(
							project,
							liferayExtension.getAppServerLibGlobalDir(),
							"mail.jar");

					GradleUtil.addDependency(
						project, PORTAL_CONFIGURATION_NAME,
						appServerLibGlobalDirJarFiles);

					GradleUtil.addDependency(
						project, PORTAL_CONFIGURATION_NAME, "com.liferay",
						"net.sf.jargs", "1.0");
					GradleUtil.addDependency(
						project, PORTAL_CONFIGURATION_NAME,
						"com.thoughtworks.qdox", "qdox", "1.12.1");
					GradleUtil.addDependency(
						project, PORTAL_CONFIGURATION_NAME,
						"jakarta.activation", "jakarta.activation-api",
						"2.1.3");
					GradleUtil.addDependency(
						project, PORTAL_CONFIGURATION_NAME,
						"com.liferay.jakarta.portlet",
						"com.liferay.jakarta.portlet-api", "4.0.0");
					GradleUtil.addDependency(
						project, PORTAL_CONFIGURATION_NAME, "jakarta.servlet",
						"jakarta.servlet-api", "6.0.0");
					GradleUtil.addDependency(
						project, PORTAL_CONFIGURATION_NAME,
						"jakarta.servlet.jsp", "jakarta.servlet.jsp-api",
						"3.1.1");

					AppServer appServer = liferayExtension.getAppServer();

					appServer.addAdditionalDependencies(
						PORTAL_CONFIGURATION_NAME);
				}

			});
	}

	private void _configureTaskDeployProvider(
		final LiferayExtension liferayExtension,
		final TaskProvider<Copy> deployTaskProvider,
		final TaskProvider<DockerCopyTask> dockerCopyTaskProvider,
		final String dockerContainerId, final String dockerFilesDir) {

		deployTaskProvider.configure(
			new Action<Copy>() {

				@Override
				public void execute(Copy deployCopy) {
					if (dockerContainerId != null) {
						deployCopy.finalizedBy(dockerCopyTaskProvider);

						deployCopy.setEnabled(false);
					}
					else if (dockerFilesDir != null) {
						deployCopy.into(
							new Callable<File>() {

								@Override
								public File call() throws Exception {
									String relativePath = FileUtil.relativize(
										liferayExtension.getDeployDir(),
										liferayExtension.getLiferayHome());

									return new File(
										dockerFilesDir, relativePath);
								}

							});
					}
				}

			});

		dockerCopyTaskProvider.configure(
			new Action<DockerCopyTask>() {

				@Override
				public void execute(DockerCopyTask dockerCopyTask) {
					dockerCopyTask.dependsOn(deployTaskProvider);

					dockerCopyTask.setContainerId(dockerContainerId);

					dockerCopyTask.setDeployDir(
						new Callable<String>() {

							@Override
							public String call() throws Exception {
								StringBuilder sb = new StringBuilder();

								sb.append(dockerCopyTask.getLiferayHome());
								sb.append('/');

								String relativePath = FileUtil.relativize(
									liferayExtension.getDeployDir(),
									liferayExtension.getLiferayHome());

								sb.append(relativePath);

								String deployDir = sb.toString();

								return deployDir.replace('\\', '/');
							}

						});

					dockerCopyTask.setDescription(
						"Deploys the project to the Docker container.");
					dockerCopyTask.setGroup(BasePlugin.BUILD_GROUP);

					dockerCopyTask.setSourceFile(
						new Callable<File>() {

							@Override
							public File call() throws Exception {
								Copy deployCopy = deployTaskProvider.get();

								FileCollection fileCollection =
									deployCopy.getSource();

								return fileCollection.getSingleFile();
							}

						});
				}

			});
	}

	private void _configureTaskDeployProvider(
		final Project project, final LiferayExtension liferayExtension,
		TaskProvider<Copy> deployTaskProvider) {

		deployTaskProvider.configure(
			new Action<Copy>() {

				@Override
				public void execute(Copy deployCopy) {
					deployCopy.doLast(
						new Action<Task>() {

							@Override
							public void execute(Task task) {
								Logger logger = task.getLogger();

								if (logger.isLifecycleEnabled()) {
									Copy copy = (Copy)task;

									logger.lifecycle(
										"Files of {} deployed to {}", project,
										copy.getDestinationDir());
								}
							}

						});

					deployCopy.into(
						new Callable<File>() {

							@Override
							public File call() throws Exception {
								return liferayExtension.getDeployDir();
							}

						});

					deployCopy.setDescription(
						"Assembles the project and deploys it to Liferay.");
					deployCopy.setGroup(BasePlugin.BUILD_GROUP);
				}

			});
	}

	private void _configureTaskDirectDeploy(
		DirectDeployTask directDeployTask,
		final LiferayExtension liferayExtension) {

		directDeployTask.setAppServerDir(
			new Callable<File>() {

				@Override
				public File call() throws Exception {
					return liferayExtension.getAppServerDir();
				}

			});

		directDeployTask.setAppServerLibGlobalDir(
			new Callable<File>() {

				@Override
				public File call() throws Exception {
					return liferayExtension.getAppServerLibGlobalDir();
				}

			});

		directDeployTask.setAppServerPortalDir(
			new Callable<File>() {

				@Override
				public File call() throws Exception {
					return liferayExtension.getAppServerPortalDir();
				}

			});

		directDeployTask.setAppServerType(
			new Callable<String>() {

				@Override
				public String call() throws Exception {
					return liferayExtension.getAppServerType();
				}

			});
	}

	private String _getScriptLiferayExtension(Project project) {
		StringBuilder sb = new StringBuilder();

		sb.append("com/liferay/gradle/plugins/dependencies/config-liferay");

		String portalVersion = PortalTools.getPortalVersion(project);

		if (Validator.isNotNull(portalVersion)) {
			sb.append('-');
			sb.append(portalVersion);
		}

		sb.append(".gradle");

		ClassLoader classLoader = LiferayBasePlugin.class.getClassLoader();

		URL url = classLoader.getResource(sb.toString());

		if (url != null) {
			return sb.toString();
		}

		return "com/liferay/gradle/plugins/dependencies/config-liferay.gradle";
	}

}