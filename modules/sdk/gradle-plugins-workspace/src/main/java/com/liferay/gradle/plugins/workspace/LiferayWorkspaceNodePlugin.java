/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.gradle.plugins.workspace;

import aQute.bnd.version.Version;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.liferay.gradle.plugins.node.NodeExtension;
import com.liferay.gradle.plugins.node.NodePlugin;
import com.liferay.gradle.plugins.workspace.internal.util.GradleUtil;
import com.liferay.gradle.plugins.workspace.internal.util.StringUtil;
import com.liferay.gradle.util.Validator;
import com.liferay.release.util.ResourceUtil;

import java.io.File;

import java.time.temporal.ChronoUnit;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.gradle.api.GradleException;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.logging.Logger;

/**
 * @author Drew Brokke
 * @author Simon Jiang
 */
public class LiferayWorkspaceNodePlugin implements Plugin<Project> {

	public static final Plugin<Project> INSTANCE =
		new LiferayWorkspaceNodePlugin();

	public static final String NODE_LTS_PROPERTY_NAME =
		WorkspacePlugin.PROPERTY_PREFIX + "node.lts.codename";

	@Override
	public void apply(Project project) {
		GradleUtil.applyPlugin(project, NodePlugin.class);

		_configureLTS(project);
	}

	private LiferayWorkspaceNodePlugin() {
		int maxAge = 7;

		String refreshNodeReleases = System.getProperty(
			"liferay.workspace.refresh.node.releases");

		if (refreshNodeReleases != null) {
			maxAge = 0;
		}

		File nodeCacheDir = new File(
			System.getProperty("user.home"), ".liferay/node");

		File indexJsonFile = new File(nodeCacheDir, "index.json");

		_nodeInfos = ResourceUtil.readJSON(
			NodeInfos.class,
			ResourceUtil.getLocalFileResolver(
				indexJsonFile, maxAge, ChronoUnit.DAYS),
			ResourceUtil.getURLResolver(
				nodeCacheDir, "https://nodejs.org/dist/index.json"),
			ResourceUtil.getLocalFileResolver(indexJsonFile),
			ResourceUtil.getClassLoaderResolver("/.node_info.json"));

		if (_nodeInfos == null) {
			throw new GradleException(
				"Unable to read Node release information");
		}
	}

	private void _configureLTS(Project project) {
		_getLTSNodeInfoOptional(
			project
		).ifPresent(
			nodeInfo -> {
				NodeExtension nodeExtension = GradleUtil.getExtension(
					project, NodeExtension.class);

				String nodeVersion = nodeInfo.getNodeVersion();
				String npmVersion = nodeInfo.getNpmVersion();

				Logger logger = project.getLogger();

				if (logger.isInfoEnabled()) {
					String lts = nodeInfo.getLts();

					logger.info(
						"Using {} LTS Node version: {}", StringUtil.quote(lts),
						nodeVersion);
					logger.info(
						"Using {} LTS NPM version: {}", StringUtil.quote(lts),
						npmVersion);
				}

				nodeExtension.setNodeVersion(nodeVersion);
				nodeExtension.setNpmVersion(npmVersion);
			}
		);
	}

	private Optional<NodeInfo> _getLTSNodeInfoOptional(Project project) {
		String lts = _getLts(project);

		if (Validator.isNull(lts)) {
			return Optional.empty();
		}

		Optional<NodeInfo> nodeInfoOptional = _nodeInfos.stream(
		).filter(
			nodeInfo -> Objects.equals(nodeInfo.getLts(), lts)
		).max(
			(first, second) -> {
				Version firstVersion = Version.parseVersion(
					first.getNodeVersion());
				Version secondVersion = Version.parseVersion(
					second.getNodeVersion());

				return firstVersion.compareTo(secondVersion);
			}
		);

		if (!nodeInfoOptional.isPresent()) {
			Logger logger = project.getLogger();

			if (logger.isErrorEnabled()) {
				logger.error(
					"Property {} must be one of: {}",
					StringUtil.quote(NODE_LTS_PROPERTY_NAME),
					_nodeInfos.stream(
					).map(
						NodeInfo::getLts
					).distinct(
					).filter(
						nodeInfoLts -> !Objects.equals(nodeInfoLts, "false")
					).sorted(
					).collect(
						Collectors.joining(", ")
					));
			}
		}

		return nodeInfoOptional;
	}

	private String _getLts(Project project) {
		return GradleUtil.getProperty(
			project, NODE_LTS_PROPERTY_NAME, (String)null);
	}

	private final NodeInfos _nodeInfos;

	@JsonIgnoreProperties(ignoreUnknown = true)
	private static class NodeInfo {

		public String getLts() {
			return _lts;
		}

		public String getNodeVersion() {
			return _nodeVersion.substring(1);
		}

		public String getNpmVersion() {
			return _npmVersion;
		}

		@JsonProperty("lts")
		private String _lts;

		@JsonProperty("version")
		private String _nodeVersion;

		@JsonProperty("npm")
		private String _npmVersion;

	}

	private static class NodeInfos extends ArrayList<NodeInfo> {
	}

}