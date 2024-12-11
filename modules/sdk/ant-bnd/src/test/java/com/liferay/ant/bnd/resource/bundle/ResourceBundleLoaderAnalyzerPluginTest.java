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

import java.io.InputStream;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Gregory Amerson
 */
public class ResourceBundleLoaderAnalyzerPluginTest {

	@Test
	public void testAggregateResourceBundlesInstructionEmpty()
		throws Exception {

		ResourceBundleLoaderAnalyzerPlugin resourceBundleLoaderAnalyzerPlugin =
			new ResourceBundleLoaderAnalyzerPlugin();

		InputStream inputStream =
			ResourceBundleLoaderAnalyzerPluginTest.class.getResourceAsStream(
				"dependencies/resources.test1.jar");

		try (Jar jar = new Jar("dot", inputStream);
			Analyzer analyzer = new Analyzer()) {

			analyzer.setBundleSymbolicName("resources.test");
			analyzer.setJar(jar);
			analyzer.setProperty("-liferay-aggregate-resource-bundles", "");

			Assert.assertTrue(
				resourceBundleLoaderAnalyzerPlugin.analyzeJar(analyzer));

			Parameters requireCapabilityHeaders = new Parameters(
				analyzer.getProperty(Constants.REQUIRE_CAPABILITY));

			Collection<Attrs> requires = requireCapabilityHeaders.values();

			Assert.assertTrue(requires.toString(), requires.isEmpty());

			Parameters provideCapabilityHeaders = new Parameters(
				analyzer.getProperty(Constants.PROVIDE_CAPABILITY));

			List<Map.Entry<String, Attrs>> provides = new ArrayList<>(
				provideCapabilityHeaders.entrySet());

			Assert.assertEquals(provides.toString(), 1, provides.size());

			Map.Entry<String, Attrs> entry = provides.get(0);

			Assert.assertEquals(
				ResourceBundleLoaderAnalyzerPlugin.LIFERAY_LANGUAGE_RESOURCES,
				entry.getKey());

			Attrs attrs = entry.getValue();

			Assert.assertEquals(2, attrs.size());

			Assert.assertEquals(
				"resources.test", attrs.get("bundle.symbolic.name"));
			Assert.assertEquals(
				"content.Language", attrs.get("resource.bundle.base.name"));
		}
	}

	@Test
	public void testAggregateResourceBundlesInstructionMultiple()
		throws Exception {

		ResourceBundleLoaderAnalyzerPlugin resourceBundleLoaderAnalyzerPlugin =
			new ResourceBundleLoaderAnalyzerPlugin();

		InputStream inputStream =
			ResourceBundleLoaderAnalyzerPluginTest.class.getResourceAsStream(
				"dependencies/resources.test1.jar");

		try (Jar jar = new Jar("dot", inputStream);
			Analyzer analyzer = new Analyzer()) {

			analyzer.setBundleSymbolicName("resources.test");
			analyzer.setJar(jar);
			analyzer.setProperty(
				"-liferay-aggregate-resource-bundles",
				"resources.lang1,resources.lang2,resources.lang3");

			Assert.assertTrue(
				resourceBundleLoaderAnalyzerPlugin.analyzeJar(analyzer));

			Parameters requireCapabilityHeaders = new Parameters(
				analyzer.getProperty(Constants.REQUIRE_CAPABILITY));

			Collection<Attrs> requires = requireCapabilityHeaders.values();

			Assert.assertEquals(requires.toString(), 3, requires.size());

			String provideCapabilityProperty = analyzer.getProperty(
				Constants.PROVIDE_CAPABILITY);

			Assert.assertFalse(
				provideCapabilityProperty.contains("service.ranking:"));

			Parameters provideCapabilityHeaders = new Parameters(
				provideCapabilityProperty);

			List<Map.Entry<String, Attrs>> provides = new ArrayList<>(
				provideCapabilityHeaders.entrySet());

			Assert.assertEquals(provides.toString(), 2, provides.size());

			Map.Entry<String, Attrs> aggregateEntry = provides.get(0);

			Assert.assertEquals(
				ResourceBundleLoaderAnalyzerPlugin.LIFERAY_LANGUAGE_RESOURCES,
				aggregateEntry.getKey());

			Attrs aggregateEntryAttrs = aggregateEntry.getValue();

			Assert.assertEquals(6, aggregateEntryAttrs.size());

			Assert.assertEquals("true", aggregateEntryAttrs.get("aggregate"));

			StringBuilder sb = new StringBuilder();

			sb.append(
				"(&(bundle.symbolic.name=resources.test)(!(aggregate=true))),");
			sb.append("(bundle.symbolic.name=resources.lang1),");
			sb.append("(bundle.symbolic.name=resources.lang2),");
			sb.append("(bundle.symbolic.name=resources.lang3)");

			Assert.assertEquals(
				sb.toString(),
				aggregateEntryAttrs.get("resource.bundle.aggregate"));

			Assert.assertEquals(
				"resources.test",
				aggregateEntryAttrs.get("bundle.symbolic.name"));
			Assert.assertEquals(
				"content.Language",
				aggregateEntryAttrs.get("resource.bundle.base.name"));
			Assert.assertEquals(
				"resources.test",
				aggregateEntryAttrs.get("servlet.context.name"));

			Map.Entry<String, Attrs> liferayResourceBundleEntry = provides.get(
				1);

			Assert.assertEquals(
				ResourceBundleLoaderAnalyzerPlugin.LIFERAY_LANGUAGE_RESOURCES +
					"~",
				liferayResourceBundleEntry.getKey());

			Attrs liferayResourceBundleAttrs =
				liferayResourceBundleEntry.getValue();

			Assert.assertEquals(2, liferayResourceBundleAttrs.size());

			Assert.assertEquals(
				"resources.test",
				liferayResourceBundleAttrs.get("bundle.symbolic.name"));
			Assert.assertEquals(
				"content.Language",
				liferayResourceBundleAttrs.get("resource.bundle.base.name"));
		}
	}

	@Test
	public void testAggregateResourceBundlesInstructionWebContextPath()
		throws Exception {

		ResourceBundleLoaderAnalyzerPlugin resourceBundleLoaderAnalyzerPlugin =
			new ResourceBundleLoaderAnalyzerPlugin();

		InputStream inputStream =
			ResourceBundleLoaderAnalyzerPluginTest.class.getResourceAsStream(
				"dependencies/blade.language.web.jar");

		try (Jar jar = new Jar("dot", inputStream);
			Analyzer analyzer = new Analyzer()) {

			analyzer.setBundleSymbolicName("blade.language.web");
			analyzer.setJar(jar);
			analyzer.setProperty(
				"-liferay-aggregate-resource-bundles", "blade.language");
			analyzer.setProperty("Web-ContextPath", "/blade-language-web");

			Assert.assertTrue(
				resourceBundleLoaderAnalyzerPlugin.analyzeJar(analyzer));

			Parameters provideCapabilityHeaders = new Parameters(
				analyzer.getProperty(Constants.PROVIDE_CAPABILITY));

			List<Map.Entry<String, Attrs>> provides = new ArrayList<>(
				provideCapabilityHeaders.entrySet());

			Map.Entry<String, Attrs> entry = provides.get(0);

			Attrs attrs = entry.getValue();

			Assert.assertEquals(
				"blade-language-web", attrs.get("servlet.context.name"));
		}
	}

	@Test
	public void testProvideLiferayResourceBundleCapabilityAdded()
		throws Exception {

		ResourceBundleLoaderAnalyzerPlugin resourceBundleLoaderAnalyzerPlugin =
			new ResourceBundleLoaderAnalyzerPlugin();

		InputStream inputStream =
			ResourceBundleLoaderAnalyzerPluginTest.class.getResourceAsStream(
				"dependencies/resources.test1.jar");

		try (Jar jar = new Jar("dot", inputStream);
			Analyzer analyzer = new Analyzer()) {

			analyzer.setJar(jar);

			Assert.assertTrue(
				resourceBundleLoaderAnalyzerPlugin.analyzeJar(analyzer));

			Parameters provideCapabilityHeaders = new Parameters(
				analyzer.getProperty(Constants.PROVIDE_CAPABILITY));

			Attrs attrs = provideCapabilityHeaders.get(
				ResourceBundleLoaderAnalyzerPlugin.LIFERAY_LANGUAGE_RESOURCES);

			Assert.assertNotNull(attrs);

			Assert.assertTrue(attrs.containsKey("bundle.symbolic.name"));
			Assert.assertTrue(attrs.containsKey("resource.bundle.base.name"));
		}
	}

	@Test
	public void testProvideLiferayResourceBundleCapabilityNotAdded()
		throws Exception {

		ResourceBundleLoaderAnalyzerPlugin resourceBundleLoaderAnalyzerPlugin =
			new ResourceBundleLoaderAnalyzerPlugin();

		InputStream inputStream =
			ResourceBundleLoaderAnalyzerPluginTest.class.getResourceAsStream(
				"dependencies/resources.test2.jar");

		try (Jar jar = new Jar("dot", inputStream);
			Analyzer analyzer = new Analyzer()) {

			analyzer.setJar(jar);

			Assert.assertFalse(
				resourceBundleLoaderAnalyzerPlugin.analyzeJar(analyzer));

			Parameters provideCapabilityHeaders = new Parameters(
				analyzer.getProperty(Constants.PROVIDE_CAPABILITY));

			Attrs attrs = provideCapabilityHeaders.get(
				ResourceBundleLoaderAnalyzerPlugin.LIFERAY_LANGUAGE_RESOURCES);

			Assert.assertNull(attrs);
		}
	}

}