/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.project.templates.rest.builder;

import com.liferay.maven.executor.MavenExecutor;
import com.liferay.project.templates.BaseProjectTemplatesTestCase;
import com.liferay.project.templates.extensions.util.FileUtil;
import com.liferay.project.templates.extensions.util.Validator;
import com.liferay.project.templates.extensions.util.VersionUtil;
import com.liferay.project.templates.util.FileTestUtil;

import java.io.File;

import java.net.URI;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import java.util.Arrays;
import java.util.Properties;
import java.util.concurrent.Callable;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

/**
 * @author Gregory Amerson
 * @author Lawrence Lee
 */
@RunWith(Parameterized.class)
public class ProjectTemplatesRESTBuilderWorkspaceTest
	implements BaseProjectTemplatesTestCase {

	@ClassRule
	public static final MavenExecutor mavenExecutor = new MavenExecutor();

	@Parameterized.Parameters(
		name = "Testcase-{index}: testing {0}, {1}, {2}, {3}"
	)
	public static Iterable<Object[]> data() {
		return Arrays.asList(
			new Object[][] {
				{"guestbook", "com.liferay.docs.guestbook", "dxp", "7.1.10.7"},
				{"guestbook", "com.liferay.docs.guestbook", "dxp", "7.2.10.7"},
				{"guestbook", "com.liferay.docs.guestbook", "dxp", "2024.q1.1"},
				{"guestbook", "com.liferay.docs.guestbook", "dxp", "2025.q3.1"},
				{"guestbook", "com.liferay.docs.guestbook", "portal", "7.3.7"},
				{
					"guestbook", "com.liferay.docs.guestbook", "portal",
					"7.4.3.56"
				},
				{
					"backend-integration", "com.liferay.docs.guestbook", "dxp",
					"7.1.10.7"
				},
				{
					"backend-integration", "com.liferay.docs.guestbook", "dxp",
					"7.2.10.7"
				},
				{
					"backend-integration", "com.liferay.docs.guestbook", "dxp",
					"2024.q1.1"
				},
				{
					"backend-integration", "com.liferay.docs.guestbook", "dxp",
					"2025.q3.1"
				},
				{
					"backend-integration", "com.liferay.docs.guestbook",
					"portal", "7.3.7"
				},
				{
					"backend-integration", "com.liferay.docs.guestbook",
					"portal", "7.4.3.56"
				},
				{"sample", "com.test.sample", "dxp", "7.1.10.7"},
				{"sample", "com.test.sample", "dxp", "7.2.10.7"},
				{"sample", "com.test.sample", "dxp", "2024.q1.1"},
				{"sample", "com.test.sample", "dxp", "2025.q3.1"},
				{"sample", "com.test.sample", "portal", "7.3.7"},
				{"sample", "com.test.sample", "portal", "7.4.3.56"}
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

	public ProjectTemplatesRESTBuilderWorkspaceTest(
		String name, String packageName, String liferayProduct,
		String liferayVersion) {

		_name = name;
		_packageName = packageName;
		_liferayProduct = liferayProduct;
		_liferayVersion = liferayVersion;
	}

	@Test
	public void testBuildTemplateRESTBuilderWorkspace() throws Exception {
		String template = "rest-builder";

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

		if (_liferayVersion.startsWith("7.0")) {
			writeGradlePropertiesInWorkspace(
				gradleWorkspaceDir, "liferay.workspace.product=portal-7.0-ga7");
		}
		else if (_liferayVersion.startsWith("7.1")) {
			writeGradlePropertiesInWorkspace(
				gradleWorkspaceDir, "liferay.workspace.product=dxp-7.1-sp7");
			updateGradlePropertiesInWorkspace(
				gradleWorkspaceDir, "liferay.workspace.target.platform.version",
				"7.1.10.7");
		}
		else if (_liferayVersion.startsWith("7.2")) {
			writeGradlePropertiesInWorkspace(
				gradleWorkspaceDir, "liferay.workspace.product=dxp-7.2-sp7");
			updateGradlePropertiesInWorkspace(
				gradleWorkspaceDir, "liferay.workspace.target.platform.version",
				"7.2.10.7");
		}
		else if (_liferayVersion.startsWith("7.3")) {
			writeGradlePropertiesInWorkspace(
				gradleWorkspaceDir, "liferay.workspace.product=portal-7.3-ga8");
		}
		else {
			writeGradlePropertiesInWorkspace(
				gradleWorkspaceDir,
				"liferay.workspace.product=portal-7.4-ga16");
		}

		File gradleWorkspaceModulesDir = new File(
			gradleWorkspaceDir, "modules");

		if (_name.contains("sample")) {
			gradleWorkspaceModulesDir = new File(
				gradleWorkspaceDir, "modules/nested/path");

			Assert.assertTrue(gradleWorkspaceModulesDir.mkdirs());
		}

		File gradleProjectDir = buildTemplateWithGradle(
			gradleWorkspaceModulesDir, template, _name, "--liferay-product",
			_liferayProduct, "--liferay-version", _liferayVersion,
			"--package-name", _packageName);

		if (_name.contains("sample")) {
			testContains(
				gradleProjectDir, "sample-impl/build.gradle",
				"api project(\":modules:nested:path:sample:sample-api\")");
		}

		if (_liferayVersion.startsWith("7.1")) {
			testContains(
				gradleProjectDir, _name + "-api/build.gradle",
				DEPENDENCY_RELEASE_DXP_API);
			testContains(
				gradleProjectDir, _name + "-impl/build.gradle",
				DEPENDENCY_RELEASE_DXP_API);

			testNotContains(
				gradleProjectDir, _name + "-api/build.gradle",
				"compileOnly group: \"org.apache.felix\", name: \"org.apache." +
					"felix.http.servlet-api\", version: \"1.1.2\"");
			testNotContains(
				gradleProjectDir, _name + "-impl/build.gradle",
				"compileOnly group: \"org.apache.felix\", name: \"org.apache." +
					"felix.http.servlet-api\", version: \"1.1.2\"");
		}
		else if (_liferayVersion.startsWith("7.2")) {
			testContains(
				gradleProjectDir, _name + "-api/build.gradle",
				DEPENDENCY_RELEASE_DXP_API);
			testContains(
				gradleProjectDir, _name + "-impl/build.gradle",
				DEPENDENCY_RELEASE_DXP_API);

			testNotContains(
				gradleProjectDir, _name + "-api/build.gradle",
				"compileOnly group: \"javax.servlet\", name: " +
					"\"javax.servlet-api\"");
			testNotContains(
				gradleProjectDir, _name + "-impl/build.gradle",
				"compileOnly group: \"javax.servlet\", name: " +
					"\"javax.servlet-api\"");
		}
		else if (VersionUtil.isLiferayQuarterlyVersion(_liferayVersion)) {
			testContains(
				gradleProjectDir, _name + "-api/build.gradle",
				DEPENDENCY_RELEASE_DXP_API);
			testContains(
				gradleProjectDir, _name + "-impl/build.gradle",
				DEPENDENCY_RELEASE_DXP_API);

			if (VersionUtil.isJakartaCompatibleVersion(_liferayVersion)) {
				testContains(
					gradleProjectDir, _name + "-client/build.gradle",
					"group: \"jakarta.annotation\", name: " +
						"\"jakarta.annotation-api\", version: \"2.1.1\"",
					"group: \"jakarta.xml.bind\", name: " +
						"\"jakarta.xml.bind-api\", version: \"4.0.2\"");
			}
		}
		else {
			testContains(
				gradleProjectDir, _name + "-api/build.gradle",
				DEPENDENCY_RELEASE_PORTAL_API);
			testContains(
				gradleProjectDir, _name + "-impl/build.gradle",
				DEPENDENCY_RELEASE_PORTAL_API);
		}

		File mavenWorkspaceDir = buildWorkspace(
			temporaryFolder, "maven", "mavenWS", _liferayVersion,
			mavenExecutor);

		if (_liferayVersion.startsWith("7.1")) {
			updateMavenPomProperties(
				mavenWorkspaceDir, "liferay.bom.version", "liferay.bom.version",
				"7.1.10.7");
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
		else if (_liferayVersion.startsWith("7.2")) {
			updateMavenPomProperties(
				mavenWorkspaceDir, "liferay.bom.version", "liferay.bom.version",
				"7.2.10.7");
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

		if (_name.contains("sample")) {
			mavenModulesDir = new File(
				mavenWorkspaceDir, "modules/nested/path");

			Assert.assertTrue(mavenModulesDir.mkdirs());
		}

		File mavenProjectDir = buildTemplateWithMaven(
			mavenModulesDir, mavenModulesDir, template, _name, "com.test",
			mavenExecutor, "-DbuildType=maven",
			"-DliferayProduct=" + _liferayProduct,
			"-DliferayVersion=" + _liferayVersion, "-Dpackage=" + _packageName);

		File projectDir = new File(mavenModulesDir, _name);

		if (_liferayVersion.startsWith("7.1")) {
			updateMavenPomProperties(
				projectDir, "liferay.bom.version", "liferay.bom.version",
				"7.1.10.7");
		}
		else if (_liferayVersion.startsWith("7.2")) {
			updateMavenPomProperties(
				projectDir, "liferay.bom.version", "liferay.bom.version",
				"7.2.10.7");
		}

		if (isBuildProjects()) {
			String projectPath;

			if (_name.contains("sample")) {
				projectPath = ":modules:nested:path:sample";
			}
			else {
				projectPath = ":modules:" + _name;
			}

			if (_liferayVersion.startsWith("7.1") ||
				_liferayVersion.startsWith("7.2")) {

				_testBuildTemplateRESTBuilder(
					gradleProjectDir, mavenProjectDir, gradleWorkspaceDir,
					_name, _packageName, projectPath);
			}
		}
	}

	@Rule
	public TemporaryFolder temporaryFolder = new TemporaryFolder();

	private void _testBuildTemplateRESTBuilder(
			File gradleProjectDir, File mavenProjectDir, final File rootProject,
			String name, String packageName, final String projectPath)
		throws Exception {

		String apiProjectName = name + "-api";

		testContains(
			gradleProjectDir, apiProjectName + "/bnd.bnd", "Export-Package:\\",
			packageName + ".dto.v1_0,\\", packageName + ".resource.v1_0");

		String implProjectName = name + "-impl";

		_testChangeOpenAPIYAML(
			gradleProjectDir, implProjectName,
			new Callable<Void>() {

				@Override
				public Void call() throws Exception {
					executeGradle(
						rootProject, _gradleDistribution,
						projectPath + ":" + implProjectName +
							_GRADLE_TASK_PATH_BUILD_REST);

					return null;
				}

			});

		executeGradle(
			rootProject, _gradleDistribution,
			projectPath + ":" + implProjectName + GRADLE_TASK_PATH_BUILD);

		File gradleApiBundleFile = testExists(
			gradleProjectDir,
			apiProjectName + "/build/libs/" + packageName + ".api-1.0.0.jar");

		File gradleImplBundleFile = testExists(
			gradleProjectDir,
			implProjectName + "/build/libs/" + packageName + ".impl-1.0.0.jar");

		if (!name.contains("sample")) {
			_testChangeOpenAPIYAML(
				mavenProjectDir, implProjectName,
				new Callable<Void>() {

					@Override
					public Void call() throws Exception {
						executeMaven(
							new File(mavenProjectDir, implProjectName),
							mavenExecutor, MAVEN_GOAL_BUILD_REST);

						return null;
					}

				});

			executeMaven(mavenProjectDir, mavenExecutor, MAVEN_GOAL_PACKAGE);

			File mavenApiBundleFile = testExists(
				mavenProjectDir,
				apiProjectName + "/target/" + name + "-api-1.0.0.jar");
			File mavenImplBundleFile = testExists(
				mavenProjectDir,
				implProjectName + "/target/" + name + "-impl-1.0.0.jar");

			testBundlesDiff(gradleApiBundleFile, mavenApiBundleFile);
			testBundlesDiff(gradleImplBundleFile, mavenImplBundleFile);
		}
	}

	private void _testChangeOpenAPIYAML(
			File projectDir, String implProjectName,
			Callable<Void> buildRESTCallable)
		throws Exception {

		File file = testExists(
			projectDir, implProjectName + "/rest-openapi.yaml");

		Path path = file.toPath();

		String content = FileUtil.read(path);

		String newContent = content.concat(
			"\n"
		).concat(
			FileTestUtil.read("components-and-paths.yaml")
		);

		Files.write(path, newContent.getBytes(StandardCharsets.UTF_8));

		buildRESTCallable.call();
	}

	private static final String _GRADLE_TASK_PATH_BUILD_REST = ":buildREST";

	private static URI _gradleDistribution;

	private final String _liferayProduct;
	private final String _liferayVersion;
	private final String _name;
	private final String _packageName;

}