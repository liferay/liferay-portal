/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.project.templates.rest;

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
public class ProjectTemplatesRestTest implements BaseProjectTemplatesTestCase {

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

	public ProjectTemplatesRestTest(
		String liferayProduct, String liferayVersion) {

		_liferayProduct = liferayProduct;
		_liferayVersion = liferayVersion;
	}

	@Test
	public void testBuildTemplateRest() throws Exception {
		String template = "rest";
		String name = "my-rest";

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

		File gradleProjectDir = buildTemplateWithGradle(
			gradleWorkspaceModulesDir, template, name, "--liferay-product",
			_liferayProduct, "--liferay-version", _liferayVersion);

		testExists(gradleProjectDir, "bnd.bnd");

		testGradlePortalReleaseDependency(gradleProjectDir, _liferayVersion);

		if (!_liferayVersion.startsWith("7.0") &&
			!VersionUtil.isJakartaCompatibleVersion(_liferayVersion)) {

			testContains(
				gradleProjectDir, "build.gradle",
				"compileOnly group: \"org.osgi\", name: " +
					"\"org.osgi.service.jaxrs");
		}

		if (VersionUtil.isJakartaCompatibleVersion(_liferayVersion)) {
			testContains(
				gradleProjectDir, "build.gradle",
				"group: \"com.liferay\", name: \"org.osgi.service.jaxrs\", " +
					"version: \"1.0.0.JAKARTA-LIFERAY-PATCHED-1\"");
		}

		if (_liferayVersion.startsWith("7.0")) {
			testContains(
				gradleProjectDir,
				"src/main/resources/configuration" +
					"/com.liferay.portal.remote.cxf.common.configuration." +
						"CXFEndpointPublisherConfiguration-cxf.properties",
				"contextPath=/my-rest");
			testContains(
				gradleProjectDir,
				"src/main/resources/configuration/com.liferay.portal.remote." +
					"rest.extender.configuration.RestExtenderConfiguration-" +
						"rest.properties",
				"contextPaths=/my-rest",
				"jaxRsApplicationFilterStrings=(component.name=" +
					"my.rest.application.MyRestApplication)");
		}
		else {
			testNotExists(
				gradleProjectDir,
				"src/main/resources/configuration" +
					"/com.liferay.portal.remote.cxf.common.configuration." +
						"CXFEndpointPublisherConfiguration-cxf.properties");
			testNotExists(
				gradleProjectDir,
				"src/main/resources/configuration/com.liferay.portal.remote." +
					"rest.extender.configuration.RestExtenderConfiguration-" +
						"rest.properties");
			testNotExists(gradleProjectDir, "src/main/resources/configuration");
		}

		String applicationFilePath =
			"src/main/java/my/rest/application/MyRestApplication.java";

		testContains(
			gradleProjectDir, applicationFilePath,
			"public class MyRestApplication extends Application");

		if (VersionUtil.isJakartaCompatibleVersion(_liferayVersion)) {
			testFileUpdatedForJakarta(gradleProjectDir, applicationFilePath);
		}

		testNotContains(gradleProjectDir, "build.gradle", "version: \"[0-9].*");

		File mavenWorkspaceDir = buildWorkspace(
			temporaryFolder, "maven", "mavenWS", _liferayVersion,
			mavenExecutor);

		File mavenModulesDir = new File(mavenWorkspaceDir, "modules");

		File mavenProjectDir = buildTemplateWithMaven(
			mavenModulesDir, mavenModulesDir, template, name, "com.test",
			mavenExecutor, "-DclassName=MyRest",
			"-DliferayProduct=" + _liferayProduct,
			"-DliferayVersion=" + _liferayVersion, "-Dpackage=my.rest");

		testContains(
			mavenProjectDir, "bnd.bnd",
			"-plugin.metatype: com.liferay.ant.bnd.metatype.MetatypePlugin");

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