/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser;

import java.io.File;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.Properties;
import java.util.TreeSet;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import org.mockito.Mockito;

/**
 * @author Brittney Nguyen
 */
public class PortalGitWorkingDirectoryTest
	extends com.liferay.jenkins.results.parser.Test {

	@Test
	public void testGetFilteredEnvironment() throws Exception {
		mockEnvironment(
			Collections.singletonMap("MASTER_NETWORK_NAME", "aws-network"));

		Properties buildProperties = new Properties();

		buildProperties.setProperty(
			"ant.opts.default[ee-7.4.x]", "-Xmx8g -Dee74");
		buildProperties.setProperty(
			"ant.opts.default[master]", "-Xmx4g -Dmaster");
		buildProperties.setProperty(
			"java.jdk.default.runtime[ee-7.4.x]", "/opt/java/jdk-zulu8-ee74");
		buildProperties.setProperty(
			"java.jdk.default.runtime[master]", "/opt/java/jdk-zulu11-master");

		JenkinsResultsParserUtil.setBuildProperties(buildProperties);

		Map<String, String> masterFilteredEnvironment = _getFilteredEnvironment(
			"master");

		testEquals(
			"-Xmx4g -Dmaster", masterFilteredEnvironment.get("ANT_OPTS"));
		testEquals(
			"-Xmx4g -Dmaster", masterFilteredEnvironment.get("JAVA_OPTS"));
		testEquals(
			"/opt/java/jdk-zulu11-master",
			masterFilteredEnvironment.get("JAVA_HOME"));

		Map<String, String> releaseFilteredEnvironment =
			_getFilteredEnvironment("ee-7.4.x");

		testEquals("-Xmx8g -Dee74", releaseFilteredEnvironment.get("ANT_OPTS"));
		testEquals(
			"-Xmx8g -Dee74", releaseFilteredEnvironment.get("JAVA_OPTS"));
		testEquals(
			"/opt/java/jdk-zulu8-ee74",
			releaseFilteredEnvironment.get("JAVA_HOME"));
	}

	@Test
	public void testGetJSUnitFiles() throws Exception {
		File portalDir = temporaryFolder.newFolder("portal");

		_writeFile(portalDir, "modules/apps/foo/.tsc/Foo.test.js");
		_writeFile(portalDir, "modules/apps/foo/build/Foo.test.js");
		_writeFile(portalDir, "modules/apps/foo/dist/Foo.test.js");
		_writeFile(portalDir, "modules/apps/foo/node_modules/Foo.test.js");
		_writeFile(portalDir, "modules/apps/foo/test/Foo.test.java");
		_writeFile(portalDir, "modules/apps/foo/test/Foo.test.js");
		_writeFile(portalDir, "modules/apps/foo/test/Foo.test.jsx");
		_writeFile(portalDir, "modules/apps/foo/test/Foo.test.ts");
		_writeFile(portalDir, "modules/apps/foo/test/Foo.test.tsx");
		_writeFile(portalDir, "portal-web/test/Foo.test.js");
		_writeFile(portalDir, "workspaces/bar/test/Bar.test.js");

		JenkinsResultsParserUtil.write(
			new File(portalDir, "modules/apps/foo/src/Foo.js"),
			RandomTestUtil.randomString());

		File portalPrivateDir = temporaryFolder.newFolder("portal-private");

		_writeFile(portalPrivateDir, "modules/dxp/baz/test/Baz.test.js");

		PortalGitWorkingDirectory portalGitWorkingDirectory = Mockito.mock(
			PortalGitWorkingDirectory.class);

		Mockito.doCallRealMethod(
		).when(
			portalGitWorkingDirectory
		).getJSUnitFiles();

		Mockito.doReturn(
			portalPrivateDir
		).when(
			portalGitWorkingDirectory
		).getPortalPrivateDir();

		Mockito.doReturn(
			portalDir
		).when(
			portalGitWorkingDirectory
		).getWorkingDirectory();

		testEquals(
			new TreeSet<>(
				Arrays.asList(
					new File(portalDir, "modules/apps/foo/test/Foo.test.js"),
					new File(portalDir, "modules/apps/foo/test/Foo.test.jsx"),
					new File(portalDir, "modules/apps/foo/test/Foo.test.ts"),
					new File(portalDir, "modules/apps/foo/test/Foo.test.tsx"),
					new File(portalDir, "workspaces/bar/test/Bar.test.js"),
					new File(
						portalPrivateDir, "modules/dxp/baz/test/Baz.test.js"))),
			new TreeSet<>(portalGitWorkingDirectory.getJSUnitFiles()));
	}

	@Rule
	public TemporaryFolder temporaryFolder = new TemporaryFolder();

	private Map<String, String> _getFilteredEnvironment(
			String upstreamBranchName)
		throws Exception {

		PortalGitWorkingDirectory portalGitWorkingDirectory = Mockito.mock(
			PortalGitWorkingDirectory.class);

		Mockito.doCallRealMethod(
		).when(
			portalGitWorkingDirectory
		).getFilteredEnvironment();

		Mockito.when(
			portalGitWorkingDirectory.getUpstreamBranchName()
		).thenReturn(
			upstreamBranchName
		);

		return portalGitWorkingDirectory.getFilteredEnvironment();
	}

	private void _writeFile(File baseDir, String filePath) throws Exception {
		JenkinsResultsParserUtil.write(
			new File(baseDir, filePath), "describe('Foo', () => {});");
	}

}