/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.project.templates;

import com.liferay.maven.executor.MavenExecutor;
import com.liferay.project.templates.extensions.util.FileUtil;
import com.liferay.project.templates.extensions.util.Validator;
import com.liferay.project.templates.extensions.util.WorkspaceUtil;
import com.liferay.project.templates.util.FileTestUtil;

import java.io.File;

import java.net.URI;

import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;

import java.util.Collections;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/**
 * @author Lawrence Lee
 * @author Gregory Amerson
 * @author Andrea Di Giorgi
 */
public class ProjectTemplatesTest implements BaseProjectTemplatesTestCase {

	@ClassRule
	public static final MavenExecutor mavenExecutor = new MavenExecutor();

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

	@Test
	public void testBuildTemplateInWorkspace() throws Exception {
		_testBuildTemplateWithWorkspace(
			null, "hello-world-portlet",
			"build/libs/hello.world.portlet-1.0.0.jar",
			"--dependency-management-enabled");
	}

	@Test
	public void testBuildTemplateInWorkspaceWithPackageName() throws Exception {
		_testBuildTemplateWithWorkspace(
			"", "barfoo", "build/libs/barfoo-1.0.0.jar",
			"--dependency-management-enabled");
	}

	@Test(expected = IllegalArgumentException.class)
	public void testBuildTemplateLiferayVersionInvalid62() throws Exception {
		_buildTemplateWithGradle(
			"mvc-portlet", "test", "--liferay-version", "6.2");
	}

	@Test(expected = IllegalArgumentException.class)
	public void testBuildTemplateLiferayVersionInvalid70test()
		throws Exception {

		_buildTemplateWithGradle(
			"mvc-portlet", "test", "--liferay-version", "7.0test");
	}

	@Test(expected = IllegalArgumentException.class)
	public void testBuildTemplateLiferayVersionInvalidTest() throws Exception {
		_buildTemplateWithGradle(
			"mvc-portlet", "test", "--liferay-version", "test.test");
	}

	@Test
	public void testBuildTemplateLiferayVersionValid70() throws Exception {
		_buildTemplateWithGradle(
			"mvc-portlet", "test", "--liferay-version", "7.0.6-2");
	}

	@Test
	public void testBuildTemplateLiferayVersionValid70Maven() throws Exception {
		_buildTemplateWithMaven(
			"mvc-portlet", "test", "-DclassName=Foo", "-DliferayVersion=7.0.6",
			"-Dpackage=foo.portlet");
	}

	@Test
	public void testBuildTemplateLiferayVersionValid80() throws Exception {
		_buildTemplateWithGradle(
			"mvc-portlet", "test", "--liferay-version", "8.0.0");
	}

	@Test
	public void testBuildTemplateLiferayVersionValid712() throws Exception {
		_buildTemplateWithGradle(
			"mvc-portlet", "test", "--liferay-version", "7.1.2");
	}

	@Test
	public void testBuildTemplateLiferayVersionValid7200() throws Exception {
		_buildTemplateWithGradle(
			"mvc-portlet", "test", "--liferay-version", "7.20.0");
	}

	@Test
	public void testBuildTemplateLiferayVersionValid7200Maven()
		throws Exception {

		_buildTemplateWithMaven(
			"mvc-portlet", "test", "-DclassName=Foo", "-DliferayVersion=7.20.0",
			"-Dpackage=foo.portlet");
	}

	@Test
	public void testBuildTemplateLiferayVersionValid7310() throws Exception {
		_buildTemplateWithGradle(
			"mvc-portlet", "test", "--liferay-version", "7.3.10");
	}

	@Test
	public void testBuildTemplateLiferayVersionValid7310Maven()
		throws Exception {

		_buildTemplateWithMaven(
			"mvc-portlet", "test", "-DclassName=Foo", "-DliferayVersion=7.3.10",
			"-Dpackage=foo.portlet");
	}

	@Test
	public void testBuildTemplateLiferayVersionValidLiferay10()
		throws Exception {

		_buildTemplateWithGradle(
			"mvc-portlet", "test", "--liferay-version", "10.0.0");
	}

	@Test
	public void testBuildTemplateLiferayVersionValidLiferayQuarterly()
		throws Exception {

		_buildTemplateWithGradle(
			"mvc-portlet", "test", "--liferay-version", "2024.q1.1");
	}

	@Test(expected = IllegalArgumentException.class)
	public void testBuildTemplateOnExistingDirectory() throws Exception {
		File destinationDir = temporaryFolder.newFolder("gradle");

		buildTemplateWithGradle(destinationDir, "api", "dup-api");
		buildTemplateWithGradle(destinationDir, "api", "dup-api");
	}

	@Test
	public void testBuildTemplatePortletInWorkspace() throws Exception {
		_testBuildTemplateWithWorkspace(
			"portlet", "foo.test", "build/libs/foo.test-1.0.0.jar",
			"--dependency-management-enabled");
	}

	@Test
	public void testBuildTemplatePortletWithPortletName() throws Exception {
		_testBuildTemplateWithWorkspace(
			"portlet", "portlet", "build/libs/portlet-1.0.0.jar",
			"--dependency-management-enabled");
	}

	@Test
	public void testBuildTemplateWithGradle() throws Exception {
		buildTemplateWithGradle(
			temporaryFolder.newFolder(), null, "foo-portlet", false, false);
		buildTemplateWithGradle(
			temporaryFolder.newFolder(), null, "foo-portlet", false, true);
		buildTemplateWithGradle(
			temporaryFolder.newFolder(), null, "foo-portlet", true, false);
		buildTemplateWithGradle(
			temporaryFolder.newFolder(), null, "foo-portlet", true, true);
	}

	@Test
	public void testListTemplates() throws Exception {
		Map<String, String> expectedTemplates = new TreeMap<>();

		try (DirectoryStream<Path> directoryStream =
				FileTestUtil.getProjectTemplatesDirectoryStream()) {

			for (Path path : directoryStream) {
				String fileName = String.valueOf(path.getFileName());

				String template = fileName.substring(
					FileTestUtil.PROJECT_TEMPLATE_DIR_PREFIX.length());

				if (template.equals(WorkspaceUtil.WORKSPACE)) {
					continue;
				}

				Properties properties = FileUtil.readProperties(
					path.resolve("bnd.bnd"));

				String bundleDescription = properties.getProperty(
					"Bundle-Description");

				expectedTemplates.put(template, bundleDescription);
			}
		}

		Assert.assertEquals(expectedTemplates, ProjectTemplates.getTemplates());
	}

	@Test
	public void testListTemplatesWithCustomArchetypesDir() throws Exception {
		File customArchetypesDir = temporaryFolder.newFolder();

		Path customArchetypesDirPath = customArchetypesDir.toPath();

		String jarName = "custom.template.jar";

		Files.write(
			customArchetypesDirPath.resolve(jarName),
			FileTestUtil.readAllBytes(
				"com/liferay/project/templates/dependencies/" + jarName));

		Map<String, String> customTemplatesMap = ProjectTemplates.getTemplates(
			Collections.singletonList(customArchetypesDir));

		Map<String, String> templatesMap = ProjectTemplates.getTemplates();

		Assert.assertEquals(
			customTemplatesMap.toString(), templatesMap.size() + 1,
			customTemplatesMap.size());
	}

	@Rule
	public TemporaryFolder temporaryFolder = new TemporaryFolder();

	private File _buildTemplateWithGradle(
			String template, String name, String... args)
		throws Exception {

		return buildTemplateWithGradle(temporaryFolder, template, name, args);
	}

	private File _buildTemplateWithMaven(
			String template, String name, String... args)
		throws Exception {

		String groupId = "com.liferay";

		return buildTemplateWithMaven(
			temporaryFolder, template, name, groupId, mavenExecutor, args);
	}

	private void _testBuildTemplateWithWorkspace(
			String template, String name, String jarFilePath, String... args)
		throws Exception {

		testBuildTemplateWithWorkspace(
			temporaryFolder, _gradleDistribution, template, name, jarFilePath,
			args);
	}

	private static URI _gradleDistribution;

}