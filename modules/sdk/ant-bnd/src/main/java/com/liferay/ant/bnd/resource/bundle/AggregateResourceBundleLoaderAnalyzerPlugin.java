/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.ant.bnd.resource.bundle;

import aQute.bnd.header.Attrs;
import aQute.bnd.header.Parameters;
import aQute.bnd.osgi.Analyzer;
import aQute.bnd.osgi.Constants;
import aQute.bnd.service.AnalyzerPlugin;

import aQute.libg.filters.AndFilter;
import aQute.libg.filters.Filter;
import aQute.libg.filters.LiteralFilter;
import aQute.libg.filters.NotFilter;
import aQute.libg.filters.SimpleFilter;

import java.util.Set;

/**
 * @author Gregory Amerson
 */
public class AggregateResourceBundleLoaderAnalyzerPlugin
	implements AnalyzerPlugin {

	@Override
	public boolean analyzeJar(Analyzer analyzer) throws Exception {
		Parameters parameters = new SortedParameters(
			analyzer.getProperty("-liferay-aggregate-resource-bundles"));

		if (parameters.isEmpty()) {
			return false;
		}

		Set<String> aggregateResourceBundles = parameters.keySet();

		addProvideCapabilities(analyzer, aggregateResourceBundles);
		addRequireCapabilities(analyzer, aggregateResourceBundles);

		return true;
	}

	protected void addProvideCapabilities(
		Analyzer analyzer, Set<String> aggregateResourceBundles) {

		Parameters provideCapabilityHeaders = new SortedParameters(
			analyzer.getProperty(Constants.PROVIDE_CAPABILITY));

		Attrs attrs = new Attrs();

		attrs.put("aggregate", "true");
		attrs.put("bundle.symbolic.name", analyzer.getBsn());
		attrs.put("module.only", "true");

		StringBuilder resourceBundleAggregate = new StringBuilder();

		AndFilter andFilter = new AndFilter();

		andFilter.addChild(
			new SimpleFilter("bundle.symbolic.name", analyzer.getBsn()));
		andFilter.addChild(
			new NotFilter(new LiteralFilter("(aggregate=true)")));

		andFilter.append(resourceBundleAggregate);

		for (String aggregateResourceBundle : aggregateResourceBundles) {
			resourceBundleAggregate.append(',');

			Filter filter = new SimpleFilter(
				"bundle.symbolic.name", aggregateResourceBundle);

			filter.append(resourceBundleAggregate);
		}

		attrs.put(
			"resource.bundle.aggregate:String",
			resourceBundleAggregate.toString());

		attrs.put("resource.bundle.base.name", "content.Language");

		String servletContextName = analyzer.getProperty("Web-ContextPath");

		if (servletContextName != null) {
			servletContextName = servletContextName.substring(1);
		}
		else {
			servletContextName = analyzer.getBsn();
		}

		attrs.put("servlet.context.name", servletContextName);

		attrs.put("service.ranking", "1");

		Parameters parameters = new Parameters();

		parameters.add(
			ResourceBundleLoaderAnalyzerPlugin.LIFERAY_LANGUAGE_RESOURCES,
			attrs);

		provideCapabilityHeaders.mergeWith(parameters, false);

		analyzer.setProperty(
			Constants.PROVIDE_CAPABILITY, provideCapabilityHeaders.toString());
	}

	protected void addRequireCapabilities(
		Analyzer analyzer, Set<String> aggregateResourceBundles) {

		Parameters requireCapabilityHeaders = new SortedParameters(
			analyzer.getProperty(Constants.REQUIRE_CAPABILITY));

		Parameters parameters = new Parameters();

		for (String aggregateResourceBundle : aggregateResourceBundles) {
			Attrs attrs = new Attrs();

			Filter filter = new SimpleFilter(
				"bundle.symbolic.name", aggregateResourceBundle);

			attrs.put("filter:", filter.toString());

			parameters.add(
				ResourceBundleLoaderAnalyzerPlugin.LIFERAY_LANGUAGE_RESOURCES,
				attrs);
		}

		requireCapabilityHeaders.mergeWith(parameters, false);

		analyzer.setProperty(
			Constants.REQUIRE_CAPABILITY, requireCapabilityHeaders.toString());
	}

}