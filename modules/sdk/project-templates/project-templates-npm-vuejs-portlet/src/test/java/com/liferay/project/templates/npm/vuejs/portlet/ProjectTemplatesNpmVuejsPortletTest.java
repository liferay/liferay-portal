/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.project.templates.npm.vuejs.portlet;

import com.liferay.maven.executor.MavenExecutor;
import com.liferay.project.templates.BaseProjectTemplatesTestCase;
import com.liferay.project.templates.extensions.util.Validator;
import com.liferay.project.templates.util.FileTestUtil;

import java.net.URI;

import java.util.Arrays;
import java.util.Properties;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

/**
 * @author Lawrence Lee
 */
@RunWith(Parameterized.class)
public class ProjectTemplatesNpmVuejsPortletTest
	implements BaseProjectTemplatesTestCase {

	@ClassRule
	public static final MavenExecutor mavenExecutor = new MavenExecutor();

	@Parameterized.Parameters(
		name = "Testcase-{index}: testing {0}, {1}, {2}, {3}, {4}"
	)
	public static Iterable<Object[]> data() {
		return Arrays.asList(
			new Object[][] {
				{"foo", "foo", "Foo", "dxp", "7.0.10.17", "yarn"},
				{"foo", "foo", "Foo", "dxp", "7.1.10.7", "yarn"},
				{"foo", "foo", "Foo", "dxp", "7.2.10.7", "yarn"},
				{"foo", "foo", "Foo", "portal", "7.3.7", "npm"},
				{"foo", "foo", "Foo", "portal", "7.4.3.56", "npm"},
				{"foo-bar", "foo.bar", "FooBar", "dxp", "7.0.10.17", "yarn"},
				{"foo-bar", "foo.bar", "FooBar", "dxp", "7.1.10.7", "yarn"},
				{"foo-bar", "foo.bar", "FooBar", "dxp", "7.2.10.7", "yarn"},
				{"foo-bar", "foo.bar", "FooBar", "dxp", "2024.q1.1", "yarn"},
				{"foo-bar", "foo.bar", "FooBar", "dxp", "2025.q3.1", "yarn"},
				{"foo-bar", "foo.bar", "FooBar", "portal", "7.3.7", "npm"},
				{"foo-bar", "foo.bar", "FooBar", "portal", "7.4.3.56", "npm"}
			});
	}

	@BeforeClass
	public static void setUpClass() throws Exception {
		String gradleDistribution = System.getProperty("gradle.distribution");

		if (Validator.isNull(gradleDistribution)) {
			Properties properties = FileTestUtil.readProperties(
				"gradle-wrapper/gradle/wrapper/gradle-wrapper.properties");

			gradleDistribution = properties.getProperty("distributionUrl");
		}

		Assert.assertTrue(gradleDistribution.contains(GRADLE_WRAPPER_VERSION));

		_gradleDistribution = URI.create(gradleDistribution);
	}

	public ProjectTemplatesNpmVuejsPortletTest(
		String name, String packageName, String className,
		String liferayProduct, String liferayVersion,
		String nodePackageManager) {

		_name = name;
		_packageName = packageName;
		_className = className;
		_liferayProduct = liferayProduct;
		_liferayVersion = liferayVersion;
		_nodePackageManager = nodePackageManager;
	}

	@Test
	public void testBuildTemplateNpmVuejsPortlet() throws Exception {
		String template = "npm-vuejs-portlet";

		testBuildTemplateNpm(
			temporaryFolder, mavenExecutor, template, _name, _packageName,
			_className, _liferayProduct, _liferayVersion, _nodePackageManager,
			_gradleDistribution);
	}

	@Rule
	public TemporaryFolder temporaryFolder = new TemporaryFolder();

	private static URI _gradleDistribution;

	private final String _className;
	private final String _liferayProduct;
	private final String _liferayVersion;
	private final String _name;
	private final String _nodePackageManager;
	private final String _packageName;

}