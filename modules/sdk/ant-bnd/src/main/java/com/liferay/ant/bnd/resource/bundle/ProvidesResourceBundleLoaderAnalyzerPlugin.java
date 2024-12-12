/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.ant.bnd.resource.bundle;

import aQute.bnd.header.Attrs;
import aQute.bnd.header.Parameters;
import aQute.bnd.osgi.Analyzer;
import aQute.bnd.osgi.Constants;
import aQute.bnd.osgi.Jar;
import aQute.bnd.service.AnalyzerPlugin;

/**
 * @author Carlos Sierra Andrés
 * @author Gregory Amerson
 */
public class ProvidesResourceBundleLoaderAnalyzerPlugin
	implements AnalyzerPlugin {

	@Override
	public boolean analyzeJar(Analyzer analyzer) throws Exception {
		Jar jar = analyzer.getJar();

		if (!jar.exists("content/Language.properties")) {
			return false;
		}

		Parameters provideCapabilityHeaders = new Parameters(
			analyzer.getProperty(Constants.PROVIDE_CAPABILITY));

		Parameters parameters = new Parameters();

		Attrs attrs = new Attrs();

		attrs.put("bundle.symbolic.name", analyzer.getBsn());
		attrs.put("module.only", "true");
		attrs.put("resource.bundle.base.name", "content.Language");

		parameters.add(
			ResourceBundleLoaderAnalyzerPlugin.LIFERAY_LANGUAGE_RESOURCES,
			attrs);

		if (provideCapabilityHeaders.containsKey(
				ResourceBundleLoaderAnalyzerPlugin.
					LIFERAY_LANGUAGE_RESOURCES)) {

			provideCapabilityHeaders.add(
				ResourceBundleLoaderAnalyzerPlugin.LIFERAY_LANGUAGE_RESOURCES,
				attrs);
		}
		else {
			provideCapabilityHeaders.mergeWith(parameters, false);
		}

		analyzer.setProperty(
			Constants.PROVIDE_CAPABILITY, provideCapabilityHeaders.toString());

		return true;
	}

}