/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.ant.bnd.resource.bundle;

import aQute.bnd.osgi.Analyzer;
import aQute.bnd.service.AnalyzerPlugin;

/**
 * @author Carlos Sierra Andrés
 * @author Gregory Amerson
 */
public class ResourceBundleLoaderAnalyzerPlugin implements AnalyzerPlugin {

	@Override
	public boolean analyzeJar(Analyzer analyzer) throws Exception {
		boolean modified = false;

		for (AnalyzerPlugin analyzerPlugin : _analyzerPlugins) {
			if (!analyzerPlugin.analyzeJar(analyzer)) {
				continue;
			}

			modified = true;
		}

		return modified;
	}

	protected static String getHeaderName(Analyzer analyzer) {
		String portalVersion = _getPortalVersion(analyzer);

		if ((portalVersion != null) &&
			(portalVersion.equals("7.3.x") || portalVersion.equals("7.2.x") ||
			 portalVersion.equals("7.1.x") || portalVersion.equals("7.0.x"))) {

			return HEADER_NAME_LIFERAY_RESOURCE_BUNDLE;
		}

		return HEADER_NAME_LIFERAY_LANGUAGE_RESOURCES;
	}

	protected static final String HEADER_NAME_LIFERAY_LANGUAGE_RESOURCES =
		"liferay.language.resources";

	protected static final String HEADER_NAME_LIFERAY_RESOURCE_BUNDLE =
		"liferay.resource.bundle";

	private static String _getPortalVersion(Analyzer analyzer) {
		String portalVersion = null;

		for (String propertyName : _PORTAL_VERSION_PROPERTY_NAMES) {
			portalVersion = analyzer.getProperty(propertyName);

			if (portalVersion != null) {
				break;
			}
		}

		if (portalVersion != null) {
			portalVersion = portalVersion.trim();
			portalVersion = portalVersion.toLowerCase();

			int pos = portalVersion.indexOf('-');

			if (pos != -1) {
				portalVersion = portalVersion.substring(0, pos);
			}

			if (portalVersion.isEmpty() || portalVersion.equals("latest") ||
				portalVersion.equals("master")) {

				portalVersion = null;
			}
		}

		return portalVersion;
	}

	private static final String[] _PORTAL_VERSION_PROPERTY_NAMES = {
		"git.working.branch.name", "portal.version"
	};

	private final AnalyzerPlugin[] _analyzerPlugins = {
		new AggregateResourceBundleLoaderAnalyzerPlugin(),
		new ProvidesResourceBundleLoaderAnalyzerPlugin()
	};

}