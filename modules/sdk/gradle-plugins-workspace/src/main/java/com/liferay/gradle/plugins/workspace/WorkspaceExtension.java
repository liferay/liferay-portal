/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.gradle.plugins.workspace;

import com.liferay.gradle.plugins.workspace.configurator.ClientExtensionProjectConfigurator;
import com.liferay.gradle.plugins.workspace.configurator.ExtProjectConfigurator;
import com.liferay.gradle.plugins.workspace.configurator.ModulesProjectConfigurator;
import com.liferay.gradle.plugins.workspace.configurator.PluginsProjectConfigurator;
import com.liferay.gradle.plugins.workspace.configurator.RootProjectConfigurator;
import com.liferay.gradle.plugins.workspace.configurator.ThemesProjectConfigurator;
import com.liferay.gradle.plugins.workspace.configurator.WarsProjectConfigurator;
import com.liferay.gradle.plugins.workspace.internal.util.GradleUtil;
import com.liferay.gradle.plugins.workspace.internal.util.StringUtil;
import com.liferay.gradle.util.Validator;
import com.liferay.portal.tools.bundle.support.constants.BundleSupportConstants;
import com.liferay.release.util.ReleaseEntry;
import com.liferay.release.util.ReleaseUtil;

import groovy.lang.Closure;
import groovy.lang.MissingPropertyException;

import java.io.File;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.initialization.Settings;
import org.gradle.api.invocation.Gradle;
import org.gradle.api.logging.Logger;

/**
 * @author David Truong
 * @author Andrea Di Giorgi
 * @author Simon Jiang
 * @author Gregory Amerson
 * @author Drew Brokke
 */
public class WorkspaceExtension {

	public WorkspaceExtension(Settings settings) {
		_gradle = settings.getGradle();

		_product = _getProperty(settings, "product", (String)null);

		_projectConfigurators.add(
			new ClientExtensionProjectConfigurator(settings));
		_projectConfigurators.add(new ExtProjectConfigurator(settings));
		_projectConfigurators.add(new ModulesProjectConfigurator(settings));
		_projectConfigurators.add(new PluginsProjectConfigurator(settings));
		_projectConfigurators.add(new ThemesProjectConfigurator(settings));
		_projectConfigurators.add(new WarsProjectConfigurator(settings));

		_appServerTomcatVersion = GradleUtil.getProperty(
			settings, "app.server.tomcat.version");
		_bundleCacheDir = _getProperty(
			settings, "bundle.cache.dir", _BUNDLE_CACHE_DIR);
		_bundleChecksumSHA512 = _getProperty(
			settings, "bundle.checksum.sha512");
		_bundleDistIncludeMetadata = _getProperty(
			settings, "bundle.dist.include.metadata",
			_BUNDLE_DIST_INCLUDE_METADATA);
		_bundleDistRootDirName = _getProperty(
			settings, "bundle.dist.root.dir", _BUNDLE_DIST_ROOT_DIR_NAME);
		_bundleUrl = _getProperty(settings, "bundle.url");
		_configsDir = _getProperty(
			settings, "configs.dir",
			BundleSupportConstants.DEFAULT_CONFIGS_DIR_NAME);
		_dirExcludesGlobs = StringUtil.split(
			GradleUtil.toString(_getProperty(settings, "dir.excludes.globs")));
		_dockerDir = _getProperty(settings, "docker.dir", _DOCKER_DIR);
		_dockerImageLiferay = _getProperty(settings, "docker.image.liferay");
		_dockerLocalRegistryAddress = _getProperty(
			settings, "docker.local.registry.address");
		_dockerPullPolicy = _getProperty(
			settings, "docker.pull.policy", _DOCKER_PULL_POLICY);
		_dockerUserAccessToken = _getProperty(
			settings, "docker.user.access.token");
		_dockerUserName = _getProperty(settings, "docker.username");
		_environment = _getProperty(
			settings, "environment",
			BundleSupportConstants.DEFAULT_ENVIRONMENT);
		_homeDir = _getProperty(
			settings, "home.dir",
			BundleSupportConstants.DEFAULT_LIFERAY_HOME_DIR_NAME);
		_javaEEUseJakarta = _getProperty(
			settings, "java.ee.use.jakarta", false);
		_nodePackageManager = _getProperty(
			settings, "node.package.manager", _NODE_PACKAGE_MANAGER);
		_targetPlatformVersion = _getProperty(
			settings, "target.platform.version");
		_virtualInstanceId = GradleUtil.getProperty(
			settings, "liferay.virtual.instance.id");

		_gradle.projectsEvaluated(
			new Closure<Void>(_gradle) {

				@SuppressWarnings("unused")
				public void doCall() {
					Project rootProject = _gradle.getRootProject();

					Logger logger = rootProject.getLogger();

					if (!logger.isLifecycleEnabled()) {
						return;
					}

					String product = getProduct();

					if (product == null) {
						logger.lifecycle(
							"The property `liferay.workspace.product` has " +
								"not been set. It is recommended to set this " +
									"property in gradle.properties in the " +
										"workspace directory. See LPS-111700.");

						return;
					}

					String overridePropertyInfo =
						"The %s property is currently overriding the default " +
							"value managed by the liferay.workspace.product " +
								"setting.";

					if (!Objects.equals(
							getAppServerTomcatVersion(),
							ReleaseUtil.getFromReleaseEntry(
								product,
								ReleaseEntry::getAppServerTomcatVersion))) {

						logger.lifecycle(
							String.format(
								overridePropertyInfo,
								"app.server.tomcat.version"));
					}

					if (!Objects.equals(
							getBundleChecksumSHA512(),
							ReleaseUtil.getFromReleaseEntry(
								product,
								ReleaseEntry::getBundleChecksumSHA512))) {

						logger.lifecycle(
							String.format(
								overridePropertyInfo,
								"liferay.workspace.bundle.checksum.sha512"));
					}

					if (!Objects.equals(
							getBundleUrl(),
							ReleaseUtil.getFromReleaseEntry(
								product, ReleaseEntry::getBundleURL))) {

						logger.lifecycle(
							String.format(
								overridePropertyInfo,
								"liferay.workspace.bundle.url"));
					}

					if (!Objects.equals(
							getDockerImageLiferay(),
							ReleaseUtil.getFromReleaseEntry(
								product,
								ReleaseEntry::getLiferayDockerImage))) {

						logger.lifecycle(
							String.format(
								overridePropertyInfo,
								"liferay.workspace.docker.image.liferay"));
					}

					if (!Objects.equals(
							getTargetPlatformVersion(),
							ReleaseUtil.getFromReleaseEntry(
								product,
								ReleaseEntry::getTargetPlatformVersion))) {

						logger.lifecycle(
							String.format(
								overridePropertyInfo,
								"liferay.workspace.target.platform.version"));
					}
				}

			});

		_rootProjectConfigurator = new RootProjectConfigurator(settings);
	}

	public String getAppServerTomcatVersion() {
		if (Objects.isNull(_appServerTomcatVersion)) {
			return ReleaseUtil.getFromReleaseEntry(
				getProduct(), ReleaseEntry::getAppServerTomcatVersion);
		}

		return GradleUtil.toString(_appServerTomcatVersion);
	}

	public File getBundleCacheDir() {
		return GradleUtil.toFile(_gradle.getRootProject(), _bundleCacheDir);
	}

	public String getBundleChecksumSHA512() {
		if (Objects.isNull(_bundleChecksumSHA512)) {
			return ReleaseUtil.getFromReleaseEntry(
				getProduct(), ReleaseEntry::getBundleChecksumSHA512);
		}

		return GradleUtil.toString(_bundleChecksumSHA512);
	}

	public String getBundleDistRootDirName() {
		return GradleUtil.toString(_bundleDistRootDirName);
	}

	public String getBundleUrl() {
		if (Objects.isNull(_bundleUrl)) {
			return ReleaseUtil.getFromReleaseEntry(
				getProduct(), ReleaseEntry::getBundleURL);
		}

		return GradleUtil.toString(_bundleUrl);
	}

	public File getConfigsDir() {
		return GradleUtil.toFile(_gradle.getRootProject(), _configsDir);
	}

	public List<String> getDirExcludesGlobs() {
		return GradleUtil.toStringList(_dirExcludesGlobs);
	}

	public String getDockerContainerId() {
		return GradleUtil.toString(_dockerContainerId);
	}

	public File getDockerDir() {
		return GradleUtil.toFile(_gradle.getRootProject(), _dockerDir);
	}

	public String getDockerImageId() {
		return GradleUtil.toString(_dockerImageId);
	}

	public String getDockerImageLiferay() {
		if (Objects.isNull(_dockerImageLiferay)) {
			return ReleaseUtil.getFromReleaseEntry(
				getProduct(), ReleaseEntry::getLiferayDockerImage);
		}

		return GradleUtil.toString(_dockerImageLiferay);
	}

	public String getDockerLocalRegistryAddress() {
		return GradleUtil.toString(_dockerLocalRegistryAddress);
	}

	public boolean getDockerPullPolicy() {
		return GradleUtil.toBoolean(_dockerPullPolicy);
	}

	public String getDockerUserAccessToken() {
		return GradleUtil.toString(_dockerUserAccessToken);
	}

	public String getDockerUserName() {
		return GradleUtil.toString(_dockerUserName);
	}

	public String getEnvironment() {
		return GradleUtil.toString(_environment);
	}

	public File getHomeDir() {
		return GradleUtil.toFile(_gradle.getRootProject(), _homeDir);
	}

	public boolean getJavaEEUseJakarta() {
		return GradleUtil.toBoolean(_javaEEUseJakarta);
	}

	public String getNodePackageManager() {
		return GradleUtil.toString(_nodePackageManager);
	}

	public String getProduct() {
		return GradleUtil.toString(_product);
	}

	public Iterable<ProjectConfigurator> getProjectConfigurators() {
		return Collections.unmodifiableSet(_projectConfigurators);
	}

	public Plugin<Project> getRootProjectConfigurator() {
		return _rootProjectConfigurator;
	}

	public String getTargetPlatformVersion() {
		if (Objects.isNull(_targetPlatformVersion)) {
			return ReleaseUtil.getFromReleaseEntry(
				getProduct(), ReleaseEntry::getTargetPlatformVersion);
		}

		return GradleUtil.toString(_targetPlatformVersion);
	}

	public String getVirtualInstanceId() {
		return GradleUtil.toString(_virtualInstanceId);
	}

	public boolean isBundleDistIncludeMetadata() {
		return GradleUtil.toBoolean(_bundleDistIncludeMetadata);
	}

	public ProjectConfigurator propertyMissing(String name) {
		for (ProjectConfigurator projectConfigurator : _projectConfigurators) {
			if (name.equals(projectConfigurator.getName())) {
				return projectConfigurator;
			}
		}

		throw new MissingPropertyException(name, ProjectConfigurator.class);
	}

	public void setBundleCacheDir(Object bundleCacheDir) {
		_bundleCacheDir = bundleCacheDir;
	}

	public void setBundleChecksumSHA512(Object bundleChecksumSHA512) {
		_bundleChecksumSHA512 = bundleChecksumSHA512;
	}

	public void setBundleDistIncludeMetadata(Object bundleDistIncludeMetadata) {
		_bundleDistIncludeMetadata = bundleDistIncludeMetadata;
	}

	public void setBundleDistRootDirName(Object bundleDistRootDirName) {
		_bundleDistRootDirName = bundleDistRootDirName;
	}

	public void setBundleUrl(Object bundleUrl) {
		_bundleUrl = bundleUrl;
	}

	public void setConfigsDir(Object configsDir) {
		_configsDir = configsDir;
	}

	public void setDirExcludesGlobs(Iterable<String> dirExcludesGlobs) {
		_dirExcludesGlobs = dirExcludesGlobs;
	}

	public void setDockerContainerId(Object dockerContainerId) {
		_dockerContainerId = dockerContainerId;
	}

	public void setDockerDir(Object dockerDir) {
		_dockerDir = dockerDir;
	}

	public void setDockerImageId(Object dockerImageId) {
		_dockerImageId = dockerImageId;
	}

	public void setDockerImageLiferay(Object dockerImageLiferay) {
		_dockerImageLiferay = dockerImageLiferay;
	}

	public void setDockerLocalRegistryAddress(
		Object dockerLocalRegistryAddress) {

		_dockerLocalRegistryAddress = dockerLocalRegistryAddress;
	}

	public void setDockerPullPolicy(Object dockerPullPolicy) {
		_dockerPullPolicy = dockerPullPolicy;
	}

	public void setDockerUserAccessToken(Object dockerUserAccessToken) {
		_dockerUserAccessToken = dockerUserAccessToken;
	}

	public void setDockerUserName(Object dockerUserName) {
		_dockerUserName = dockerUserName;
	}

	public void setEnvironment(Object environment) {
		_environment = environment;
	}

	public void setHomeDir(Object homeDir) {
		_homeDir = homeDir;
	}

	public void setJavaEEUseJakarta(Object javaEEUseJakarta) {
		_javaEEUseJakarta = javaEEUseJakarta;
	}

	public void setNodePackageManager(Object nodePackageManager) {
		_nodePackageManager = nodePackageManager;
	}

	public void setProduct(Object product) {
		_product = product;
	}

	public void setTargetPlatformVersion(Object targetPlatformVersion) {
		_targetPlatformVersion = targetPlatformVersion;
	}

	public void setVirtualInstanceId(Object virtualInstanceId) {
		_virtualInstanceId = virtualInstanceId;
	}

	private Object _getProperty(Object object, String keySuffix) {
		return GradleUtil.getProperty(
			object, WorkspacePlugin.PROPERTY_PREFIX + keySuffix);
	}

	private boolean _getProperty(
		Object object, String keySuffix, boolean defaultValue) {

		return GradleUtil.getProperty(
			object, WorkspacePlugin.PROPERTY_PREFIX + keySuffix, defaultValue);
	}

	private Object _getProperty(
		Object object, String keySuffix, File defaultValue) {

		Object value = GradleUtil.getProperty(
			object, WorkspacePlugin.PROPERTY_PREFIX + keySuffix);

		if ((value instanceof String) && Validator.isNull((String)value)) {
			value = null;
		}

		if (value == null) {
			return defaultValue;
		}

		return value;
	}

	private String _getProperty(
		Object object, String keySuffix, String defaultValue) {

		return GradleUtil.getProperty(
			object, WorkspacePlugin.PROPERTY_PREFIX + keySuffix, defaultValue);
	}

	private static final File _BUNDLE_CACHE_DIR = new File(
		System.getProperty("user.home"),
		BundleSupportConstants.DEFAULT_BUNDLE_CACHE_DIR_NAME);

	private static final boolean _BUNDLE_DIST_INCLUDE_METADATA = false;

	private static final String _BUNDLE_DIST_ROOT_DIR_NAME = null;

	private static final File _DOCKER_DIR = new File(
		Project.DEFAULT_BUILD_DIR_NAME + File.separator + "docker");

	private static final boolean _DOCKER_PULL_POLICY = true;

	private static final String _NODE_PACKAGE_MANAGER = "yarn";

	private final Object _appServerTomcatVersion;
	private Object _bundleCacheDir;
	private Object _bundleChecksumSHA512;
	private Object _bundleDistIncludeMetadata;
	private Object _bundleDistRootDirName;
	private Object _bundleUrl;
	private Object _configsDir;
	private Iterable<String> _dirExcludesGlobs;
	private Object _dockerContainerId;
	private Object _dockerDir;
	private Object _dockerImageId;
	private Object _dockerImageLiferay;
	private Object _dockerLocalRegistryAddress;
	private Object _dockerPullPolicy;
	private Object _dockerUserAccessToken;
	private Object _dockerUserName;
	private Object _environment;
	private final Gradle _gradle;
	private Object _homeDir;
	private Object _javaEEUseJakarta;
	private Object _nodePackageManager;
	private Object _product;
	private final Set<ProjectConfigurator> _projectConfigurators =
		new LinkedHashSet<>();
	private final Plugin<Project> _rootProjectConfigurator;
	private Object _targetPlatformVersion;
	private Object _virtualInstanceId;

}