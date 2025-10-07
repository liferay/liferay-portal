/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.project.templates.simulation.panel.entry;

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
public class ProjectTemplatesSimulationPanelEntryTest
	implements BaseProjectTemplatesTestCase {

	@ClassRule
	public static final MavenExecutor mavenExecutor = new MavenExecutor();

	@Parameterized.Parameters(name = "Testcase-{index}: testing {1} {0}")
	public static Iterable<Object[]> data() {
		return Arrays.asList(
			new Object[][] {
				{"dxp", "7.0.10.17"}, {"dxp", "7.1.10.7"}, {"dxp", "7.2.10.7"},
				{"portal", "7.3.7"}, {"portal", "7.4.3.56"},
				{"dxp", "7.4.13.u72"}, {"dxp", "2024.q1.1"},
				{"dxp", "2025.q3.1"}
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

	public ProjectTemplatesSimulationPanelEntryTest(
		String liferayProduct, String liferayVersion) {

		_liferayProduct = liferayProduct;
		_liferayVersion = liferayVersion;
	}

	@Test
	public void testBuildTemplateSimulationPanelEntry() throws Exception {
		String template = "simulation-panel-entry";
		String name = "simulator";
		String packageName = "test.simulator";

		File gradleWorkspaceDir = buildWorkspace(
			temporaryFolder, "gradle", "gradleWS", _liferayVersion,
			mavenExecutor);

		String liferayWorkspaceProduct = _getLiferayWorkspaceProduct();

		if (liferayWorkspaceProduct != null) {
			writeGradlePropertiesInWorkspace(
				gradleWorkspaceDir,
				"liferay.workspace.product=" + liferayWorkspaceProduct);
		}

		File gradleWorkspaceModulesDir = new File(
			gradleWorkspaceDir, "modules");

		File gradleProjectDir = buildTemplateWithGradle(
			gradleWorkspaceModulesDir, template, name, "--liferay-product",
			_liferayProduct, "--liferay-version", _liferayVersion,
			"--package-name", packageName);

		testExists(gradleProjectDir, "bnd.bnd");

		if (_liferayProduct.equals("dxp")) {
			testContains(
				gradleProjectDir, "build.gradle", DEPENDENCY_RELEASE_DXP_API);
		}
		else {
			testContains(
				gradleProjectDir, "build.gradle",
				DEPENDENCY_RELEASE_PORTAL_API);
		}

		String simulationPanelAppFilePath =
			"src/main/java/test/simulator/application/list" +
				"/SimulatorSimulationPanelApp.java";

		testContains(
			gradleProjectDir, simulationPanelAppFilePath,
			"public class SimulatorSimulationPanelApp",
			"extends BaseJSPPanelApp");

		if (VersionUtil.isJakartaCompatibleVersion(_liferayVersion)) {
			testFileUpdatedForJakarta(
				gradleProjectDir, simulationPanelAppFilePath);
		}

		testNotContains(gradleProjectDir, "build.gradle", "version: \"[0-9].*");

		File mavenWorkspaceDir = buildWorkspace(
			temporaryFolder, "maven", "mavenWS", _liferayVersion,
			mavenExecutor);

		if (_liferayVersion.startsWith("20") ||
			(_liferayVersion.startsWith("7.4") &&
			 _liferayProduct.equals("dxp"))) {

			String workspaceLiferayVersion = _liferayVersion;

			if (_liferayVersion.startsWith(
					UNRELEASED_QUARTERLY_LIFERAY_VERSION)) {

				workspaceLiferayVersion = WORKSPACE_QUARTERLY_LIFERAY_VERSION;
			}

			updateMavenPomProperties(
				mavenWorkspaceDir, "liferay.bom.version", "liferay.bom.version",
				workspaceLiferayVersion);

			updateMavenPomElementText(
				mavenWorkspaceDir, "//artifactId[text()='release.portal.bom']",
				"release.dxp.bom");
			updateMavenPomElementText(
				mavenWorkspaceDir,
				"//artifactId[text()='release.portal.bom.compile.only']",
				"release.dxp.bom.compile.only");
			updateMavenPomElementText(
				mavenWorkspaceDir,
				"//artifactId[text()='release.portal.bom.third.party']",
				"release.dxp.bom.third.party");
		}

		File mavenModulesDir = new File(mavenWorkspaceDir, "modules");

		boolean newTemplate = false;

		if (_liferayVersion.equals("7.4.13.u72") ||
			VersionUtil.isLiferayQuarterlyVersion(_liferayVersion)) {

			newTemplate = true;
		}

		File mavenProjectDir = buildTemplateWithMaven(
			mavenModulesDir, mavenModulesDir, template, name, "com.test",
			mavenExecutor, "-DclassName=Simulator",
			"-DliferayProduct=" + _liferayProduct,
			"-DliferayVersion=" + _liferayVersion,
			"-DnewTemplate=" + newTemplate, "-Dpackage=" + packageName);

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

	private String _getLiferayWorkspaceProduct() {
		if (_liferayVersion.startsWith("20")) {
			return "dxp-2024.q1.1";
		}
		else if (_liferayVersion.startsWith("7.0")) {
			return "dxp-7.0-sp17";
		}
		else if (_liferayVersion.startsWith("7.1")) {
			return "dxp-7.1-sp7";
		}
		else if (_liferayVersion.startsWith("7.2")) {
			return "dxp-7.2-sp7";
		}
		else if (_liferayVersion.startsWith("7.3")) {
			return "portal-7.3-ga8";
		}
		else if (_liferayVersion.startsWith("7.4")) {
			if (_liferayProduct.equals("dxp")) {
				return "dxp-7.4-u72";
			}

			return "portal-7.4-ga56";
		}

		return null;
	}

	private static URI _gradleDistribution;

	private final String _liferayProduct;
	private final String _liferayVersion;

}