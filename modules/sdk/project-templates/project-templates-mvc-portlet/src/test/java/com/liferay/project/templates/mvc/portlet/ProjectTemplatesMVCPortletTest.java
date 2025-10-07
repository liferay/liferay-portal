/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.project.templates.mvc.portlet;

import com.liferay.maven.executor.MavenExecutor;
import com.liferay.project.templates.BaseProjectTemplatesTestCase;
import com.liferay.project.templates.extensions.util.Validator;
import com.liferay.project.templates.util.FileTestUtil;

import java.io.File;

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
public class ProjectTemplatesMVCPortletTest
	implements BaseProjectTemplatesTestCase {

	@ClassRule
	public static final MavenExecutor mavenExecutor = new MavenExecutor();

	@Parameterized.Parameters(name = "Testcase-{index}: testing {1} {0}")
	public static Iterable<Object[]> data() {
		return Arrays.asList(
			new Object[][] {
				{"dxp", "7.0.10.17"}, {"dxp", "7.1.10.7"}, {"dxp", "7.2.10.7"},
				{"portal", "7.3.7"}, {"portal", "7.4.3.56"},
				{"dxp", "2024.q1.1"}, {"dxp", "2025.q3.1"}
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

	public ProjectTemplatesMVCPortletTest(
		String liferayProduct, String liferayVersion) {

		_liferayProduct = liferayProduct;
		_liferayVersion = liferayVersion;
	}

	@Test
	public void testBuildTemplateMVCPortlet() throws Exception {
		File gradleProjectDir = testBuildTemplatePortlet(
			temporaryFolder, "mvc-portlet", "foo", "foo", _liferayProduct,
			_liferayVersion, mavenExecutor, _gradleDistribution);

		testContains(
			gradleProjectDir, "bnd.bnd", "Export-Package: foo.constants");
		testContains(
			gradleProjectDir, "src/main/java/foo/constants/FooPortletKeys.java",
			"public class FooPortletKeys", "public static final String FOO");

		String packagePrefix = getJavaxOrJakartaPackagePrefix(_liferayVersion);

		testContains(
			gradleProjectDir, "src/main/java/foo/portlet/FooPortlet.java",
			packagePrefix + ".portlet.display-name=Foo",
			packagePrefix + ".portlet.name=\" + FooPortletKeys.FOO",
			"public class FooPortlet extends MVCPortlet {");
		testContains(
			gradleProjectDir, "src/main/resources/content/Language.properties",
			packagePrefix + ".portlet.title.foo_FooPortlet=Foo",
			"foo.caption=Hello from Foo!");
	}

	@Rule
	public TemporaryFolder temporaryFolder = new TemporaryFolder();

	private static URI _gradleDistribution;

	private final String _liferayProduct;
	private final String _liferayVersion;

}