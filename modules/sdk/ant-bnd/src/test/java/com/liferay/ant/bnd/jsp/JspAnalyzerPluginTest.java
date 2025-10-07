/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.ant.bnd.jsp;

import aQute.bnd.osgi.Builder;
import aQute.bnd.osgi.Constants;
import aQute.bnd.osgi.EmbeddedResource;
import aQute.bnd.osgi.Jar;
import aQute.bnd.osgi.Packages;
import aQute.bnd.osgi.Resource;

import aQute.lib.io.IO;

import java.io.InputStream;

import java.net.URL;

import java.time.Instant;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Gregory Amerson
 */
public class JspAnalyzerPluginTest {

	@Test
	public void testAddTaglibRequirements() throws Exception {
		List<String> expectedTaglibURIs = Arrays.asList(
			"http://java.sun.com/portlet_2_0", "http://liferay.com/tld/aui",
			"http://liferay.com/tld/portlet", "http://liferay.com/tld/security",
			"http://liferay.com/tld/theme", "http://liferay.com/tld/ui",
			"http://liferay.com/tld/util");

		_testAddTaglibRequirements(
			expectedTaglibURIs, "dependencies/imports_without_comments.jsp",
			"jakarta.tags.core");
		_testAddTaglibRequirements(
			expectedTaglibURIs,
			"dependencies/imports_without_comments_with_javax.jsp",
			"http://java.sun.com/jsp/jstl/core");
	}

	@Test
	public void testGetTaglibURIsWithComments() throws Exception {
		JspAnalyzerPlugin jspAnalyzerPlugin = new JspAnalyzerPlugin();

		URL url = getResource("dependencies/imports_with_comments.jsp");

		InputStream inputStream = url.openStream();

		String content = IO.collect(inputStream);

		Set<String> taglibURIs = jspAnalyzerPlugin.getTaglibURIs(content);

		Assert.assertNotNull(taglibURIs);

		int size = taglibURIs.size();

		Assert.assertEquals(3, size);
	}

	@Test
	public void testGetTaglibURIsWithoutComments() throws Exception {
		JspAnalyzerPlugin jspAnalyzerPlugin = new JspAnalyzerPlugin();

		URL url = getResource("dependencies/imports_without_comments.jsp");

		InputStream inputStream = url.openStream();

		String content = IO.collect(inputStream);

		Set<String> taglibURIs = jspAnalyzerPlugin.getTaglibURIs(content);

		Assert.assertNotNull(taglibURIs);

		int size = taglibURIs.size();

		Assert.assertEquals(8, size);
	}

	@Test
	public void testImplicitImports() throws Exception {
		List<String> jakartaFQNs = Arrays.asList(
			"jakarta.servlet", "jakarta.servlet.http");
		List<String> javaxFQNs = Arrays.asList(
			"javax.servlet", "javax.servlet.http");

		_testImplicitImports(
			"dependencies/imports_without_comments_with_javax.jsp", javaxFQNs,
			jakartaFQNs);
		_testImplicitImports(
			"dependencies/imports_without_comments.jsp", jakartaFQNs,
			javaxFQNs);
	}

	@Test
	public void testImportsWithMultiplesAndStatics() throws Exception {
		JspAnalyzerPlugin jspAnalyzerPlugin = new JspAnalyzerPlugin();

		URL url = getResource(
			"dependencies/imports_without_multipackages_and_statics.jsp");

		InputStream inputStream = url.openStream();

		String content = IO.collect(inputStream);

		Builder builder = new Builder();

		builder.build();

		jspAnalyzerPlugin.addApiUses(builder, content);

		Packages referredPackages = builder.getReferred();

		Assert.assertTrue(referredPackages.containsFQN("jakarta.portlet"));
		Assert.assertTrue(
			referredPackages.containsFQN("jakarta.portlet.filter"));
		Assert.assertTrue(
			referredPackages.containsFQN("jakarta.portlet.tck.beans"));
		Assert.assertTrue(
			referredPackages.containsFQN("jakarta.portlet.tck.constants"));
		Assert.assertTrue(referredPackages.containsFQN("jakarta.servlet"));
		Assert.assertTrue(referredPackages.containsFQN("jakarta.servlet.http"));
		Assert.assertTrue(referredPackages.containsFQN("java.io"));
		Assert.assertTrue(referredPackages.containsFQN("java.util"));
		Assert.assertTrue(referredPackages.containsFQN("java.util.logging"));
	}

	@Test
	public void testPageImportsWithComments() throws Exception {
		JspAnalyzerPlugin jspAnalyzerPlugin = new JspAnalyzerPlugin();

		URL url = getResource("dependencies/page_imports_with_comments.jsp");

		InputStream inputStream = url.openStream();

		String content = IO.collect(inputStream);

		Builder builder = new Builder();

		builder.build();

		jspAnalyzerPlugin.addApiUses(builder, content);

		Packages referredPackages = builder.getReferred();

		Assert.assertFalse(referredPackages.containsFQN("jakarta.portlet"));
		Assert.assertFalse(
			referredPackages.containsFQN("jakarta.portlet.filter"));
		Assert.assertFalse(
			referredPackages.containsFQN("jakarta.portlet.tck.beans"));
		Assert.assertTrue(
			referredPackages.containsFQN("jakarta.portlet.tck.constants"));
		Assert.assertFalse(referredPackages.containsFQN("jakarta.servlet"));
		Assert.assertFalse(
			referredPackages.containsFQN("jakarta.servlet.http"));
		Assert.assertTrue(referredPackages.containsFQN("java.io"));
	}

	@Test
	public void testRemoveDuplicateTaglibRequirements() throws Exception {
		JspAnalyzerPlugin jspAnalyzerPlugin = new JspAnalyzerPlugin();

		URL url = getResource("dependencies/imports_without_comments.jsp");

		InputStream inputStream = url.openStream();

		String content = IO.collect(inputStream);

		Builder builder = new Builder();

		builder.build();

		Set<String> taglibURIs = new HashSet<>();

		jspAnalyzerPlugin.addTaglibRequirements(builder, content, taglibURIs);

		String requireCapability1 = builder.getProperty(
			Constants.REQUIRE_CAPABILITY);

		jspAnalyzerPlugin.addTaglibRequirements(builder, content, taglibURIs);

		String requireCapability2 = builder.getProperty(
			Constants.REQUIRE_CAPABILITY);

		Assert.assertEquals(requireCapability1, requireCapability2);
	}

	protected URL getResource(String path) {
		Class<?> clazz = getClass();

		return clazz.getResource(path);
	}

	private void _testAddTaglibRequirements(
			List<String> expectedURIs, String jspPath, String unexpectedURI)
		throws Exception {

		JspAnalyzerPlugin jspAnalyzerPlugin = new JspAnalyzerPlugin();

		Builder builder = new Builder();

		builder.build();

		URL url = getResource(jspPath);

		String content = null;

		try (InputStream inputStream = url.openStream()) {
			content = IO.collect(inputStream);
		}

		jspAnalyzerPlugin.addTaglibRequirements(
			builder, content, new HashSet<>());

		String requireCapability = builder.getProperty(
			Constants.REQUIRE_CAPABILITY);

		for (String expectedURI : expectedURIs) {
			Assert.assertTrue(requireCapability.contains(expectedURI));
		}

		Assert.assertFalse(requireCapability.contains(unexpectedURI));
	}

	private void _testImplicitImports(
			String jspPath, List<String> expectedFQNs,
			List<String> notExpectedFQNs)
		throws Exception {

		try (Jar jar = new Jar("test.jar")) {
			Builder builder = new Builder();

			Map<String, Resource> resources = jar.getResources();

			Instant instant = Instant.now();

			resources.put(
				"resources/init.jsp",
				new EmbeddedResource(
					IO.read(getResource(jspPath)), instant.toEpochMilli()));

			builder.setJar(jar);

			builder.setProperty("-jsp", "*.jsp");

			JspAnalyzerPlugin jspAnalyzerPlugin = new JspAnalyzerPlugin();

			jspAnalyzerPlugin.analyzeJar(builder);

			Packages referred = builder.getReferred();

			for (String expectedFQN : expectedFQNs) {
				Assert.assertTrue(
					"Expected: " + expectedFQN,
					referred.containsFQN(expectedFQN));
			}

			for (String notExpectedFQN : notExpectedFQNs) {
				Assert.assertFalse(
					"Not expected: " + notExpectedFQN,
					referred.containsFQN(notExpectedFQN));
			}
		}
	}

}