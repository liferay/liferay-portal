/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.ant.bnd.jsp;

import aQute.bnd.osgi.Builder;
import aQute.bnd.osgi.Constants;
import aQute.bnd.osgi.Packages;

import aQute.lib.io.IO;

import java.io.InputStream;

import java.net.URL;

import java.util.HashSet;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Gregory Amerson
 */
public class JspAnalyzerPluginTest {

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

		Assert.assertTrue(referredPackages.containsFQN("java.io"));
		Assert.assertTrue(referredPackages.containsFQN("java.util"));
		Assert.assertTrue(referredPackages.containsFQN("java.util.logging"));
		Assert.assertTrue(referredPackages.containsFQN("jakarta.portlet"));
		Assert.assertTrue(
			referredPackages.containsFQN("jakarta.portlet.filter"));
		Assert.assertTrue(
			referredPackages.containsFQN("jakarta.portlet.tck.beans"));
		Assert.assertTrue(
			referredPackages.containsFQN("jakarta.portlet.tck.constants"));
		Assert.assertTrue(referredPackages.containsFQN("jakarta.servlet"));
		Assert.assertTrue(referredPackages.containsFQN("jakarta.servlet.http"));
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

		Assert.assertTrue(referredPackages.containsFQN("java.io"));
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

}