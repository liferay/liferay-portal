/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.project.templates.panel.app;

import com.liferay.maven.executor.MavenExecutor;
import com.liferay.project.templates.BaseProjectTemplatesTestCase;
import com.liferay.project.templates.extensions.util.Validator;
import com.liferay.project.templates.extensions.util.VersionUtil;
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
public class ProjectTemplatesPanelAppTest
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

	public ProjectTemplatesPanelAppTest(
		String liferayProduct, String liferayVersion) {

		_liferayProduct = liferayProduct;
		_liferayVersion = liferayVersion;
	}

	@Test
	public void testBuildTemplatePanelApp() throws Exception {
		String template = "panel-app";
		String name = "gradle.test";

		File gradleWorkspaceDir = buildWorkspace(
			temporaryFolder, "gradle", "gradleWS", _liferayVersion,
			mavenExecutor);

		String liferayWorkspaceProduct = getLiferayWorkspaceProduct(
			_liferayVersion);

		if (liferayWorkspaceProduct != null) {
			writeGradlePropertiesInWorkspace(
				gradleWorkspaceDir,
				"liferay.workspace.product=" + liferayWorkspaceProduct);
		}

		File gradleWorkspaceModulesDir = new File(
			gradleWorkspaceDir, "modules");

		String className = "Foo";

		File gradleProjectDir = buildTemplateWithGradle(
			gradleWorkspaceModulesDir, template, name, "--class-name",
			className, "--liferay-product", _liferayProduct,
			"--liferay-version", _liferayVersion);

		testExists(gradleProjectDir, "bnd.bnd");

		testExists(
			gradleProjectDir,
			"src/main/resources/META-INF/resources/css/main.scss");

		testContains(
			gradleProjectDir, "bnd.bnd",
			"Export-Package: gradle.test.constants");

		testGradlePortalReleaseDependency(gradleProjectDir, _liferayVersion);

		testContains(
			gradleProjectDir,
			"src/main/java/gradle/test/constants/FooPortletKeys.java",
			"public class FooPortletKeys", "public static final String FOO");

		String packagePrefix = getJavaxOrJakartaPackagePrefix(_liferayVersion);

		testContains(
			gradleProjectDir,
			"src/main/java/gradle/test/application/list/FooPanelApp.java",
			"public class FooPanelApp extends BasePanelApp",
			"target = \"(" + packagePrefix + ".portlet.name=\"");
		testContains(
			gradleProjectDir,
			"src/main/java/gradle/test/portlet/FooPortlet.java",
			packagePrefix + ".portlet.display-name=Foo",
			packagePrefix + ".portlet.name=\" + FooPortletKeys.FOO",
			"public class FooPortlet extends MVCPortlet");
		testContains(
			gradleProjectDir, "src/main/resources/content/Language.properties",
			packagePrefix + ".portlet.title.gradle_test_FooPortlet=Foo",
			"foo.caption=Hello from Foo!");

		testNotContains(gradleProjectDir, "build.gradle", "version: \"[0-9].*");

		if (VersionUtil.isLiferayQuarterlyVersion(_liferayVersion) ||
			_liferayVersion.startsWith("7.4")) {

			testContains(
				gradleProjectDir,
				"src/main/java/gradle/test/application/list/FooPanelApp.java",
				"public Portlet getPortlet(");
			testNotContains(
				gradleProjectDir,
				"src/main/java/gradle/test/application/list/FooPanelApp.java",
				"public void setPortlet(");
		}
		else {
			testContains(
				gradleProjectDir,
				"src/main/java/gradle/test/application/list/FooPanelApp.java",
				"public void setPortlet(");
			testNotContains(
				gradleProjectDir,
				"src/main/java/gradle/test/application/list/FooPanelApp.java",
				"public Portlet getPortlet(");
		}

		if (VersionUtil.isJakartaCompatibleVersion(_liferayVersion)) {
			testPortletUpdatedForJakarta(gradleProjectDir, name, className);
		}

		File mavenWorkspaceDir = buildWorkspace(
			temporaryFolder, "maven", "mavenWS", _liferayVersion,
			mavenExecutor);

		File mavenModulesDir = new File(mavenWorkspaceDir, "modules");

		File mavenProjectDir = buildTemplateWithMaven(
			mavenModulesDir, mavenModulesDir, template, name, "com.test",
			mavenExecutor, "-DclassName=Foo",
			"-DliferayProduct=" + _liferayProduct,
			"-DliferayVersion=" + _liferayVersion, "-Dpackage=gradle.test");

		if (!_liferayVersion.startsWith("7.0")) {
			testContains(
				mavenProjectDir, "bnd.bnd",
				"-contract: JavaPortlet,JavaServlet");
		}

		if (isBuildProjects()) {
			File gradleOutputDir = new File(gradleProjectDir, "build/libs");
			File mavenOutputDir = new File(mavenProjectDir, "target");

			buildProjects(
				_gradleDistribution, mavenExecutor, gradleWorkspaceDir,
				mavenProjectDir, gradleOutputDir, mavenOutputDir,
				":modules:" + name + GRADLE_TASK_PATH_BUILD);
		}
	}

	@Rule
	public TemporaryFolder temporaryFolder = new TemporaryFolder();

	private static URI _gradleDistribution;

	private final String _liferayProduct;
	private final String _liferayVersion;

}