/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser;

import java.io.File;
import java.io.FileInputStream;

import java.util.List;
import java.util.Properties;

import org.junit.Assert;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Charlotte Wong
 */
public class CentralGitSubrepositoryTest
	extends com.liferay.jenkins.results.parser.Test {

	@Test
	public void testGetGitSubrepositoryName() {
		_testGetGitSubrepositoryName(
			"liferay-portal", "git@github.com:liferay/liferay-portal.git");
		_testGetGitSubrepositoryName(
			"com-liferay-osb-asah-private",
			"git@github.com:liferay/com-liferay-osb-asah-private.git");
	}

	@Test
	public void testGetGitSubrepositoryUsername() {
		_testGetGitSubrepositoryUsername(
			"liferay", "git@github.com:liferay/liferay-portal.git");
		_testGetGitSubrepositoryUsername(
			"brianchandotcom",
			"git@github.com:brianchandotcom/liferay-portal.git");
	}

	@Test
	public void testIsAutoPullEnabled() {
		_testIsAutoPullEnabled(null, false, null);
		_testIsAutoPullEnabled(null, false, "push");
		_testIsAutoPullEnabled(null, false, "pull");
		_testIsAutoPullEnabled("false", false, "pull");
		_testIsAutoPullEnabled("true", true, "pull");
	}

	@Test
	public void testIsAutoPullEnabledMasterBranchInvariant() throws Exception {
		File modulesDir = new File(
			"../../../modules"
		).getCanonicalFile();

		List<File> gitrepoFiles = JenkinsResultsParserUtil.findFiles(
			modulesDir, ".gitrepo");

		for (File gitrepoFile : gitrepoFiles) {
			Properties gitrepoProperties = new Properties();

			try (FileInputStream fileInputStream = new FileInputStream(
					gitrepoFile)) {

				gitrepoProperties.load(fileInputStream);
			}

			CentralGitSubrepository mock = Mockito.mock(
				CentralGitSubrepository.class);

			ReflectionTestUtil.setFieldValue(
				mock, "_gitrepoProperties", gitrepoProperties);

			Mockito.doCallRealMethod(
			).when(
				mock
			).isAutoPullEnabled();

			Assert.assertFalse(
				"Expected isAutoPullEnabled() false for " +
					gitrepoFile.getPath(),
				mock.isAutoPullEnabled());
		}
	}

	@Test
	public void testIsGitSubrepositoryUpstreamCommitMerged() throws Exception {
		String upstreamCommit = RandomTestUtil.randomString();

		_testIsGitSubrepositoryUpstreamCommitMerged("", false, upstreamCommit);
		_testIsGitSubrepositoryUpstreamCommitMerged(
			RandomTestUtil.randomString(), false, upstreamCommit);
		_testIsGitSubrepositoryUpstreamCommitMerged(
			upstreamCommit, true, upstreamCommit);
	}

	private void _testGetGitSubrepositoryName(
		String expectedName, String remote) {

		Properties gitrepoProperties = new Properties();

		gitrepoProperties.setProperty("remote", remote);

		CentralGitSubrepository mock = Mockito.mock(
			CentralGitSubrepository.class);

		ReflectionTestUtil.setFieldValue(
			mock, "_gitrepoProperties", gitrepoProperties);

		Assert.assertEquals(
			expectedName,
			ReflectionTestUtil.invoke(
				mock, "_getGitSubrepositoryName", new Class<?>[0]));
	}

	private void _testGetGitSubrepositoryUsername(
		String expectedUsername, String remote) {

		Properties gitrepoProperties = new Properties();

		gitrepoProperties.setProperty("remote", remote);

		CentralGitSubrepository mock = Mockito.mock(
			CentralGitSubrepository.class);

		ReflectionTestUtil.setFieldValue(
			mock, "_gitrepoProperties", gitrepoProperties);

		Assert.assertEquals(
			expectedUsername,
			ReflectionTestUtil.invoke(
				mock, "_getGitSubrepositoryUsername", new Class<?>[0]));
	}

	private void _testIsAutoPullEnabled(
		String autopull, boolean expected, String mode) {

		Properties gitrepoProperties = new Properties();

		gitrepoProperties.setProperty(
			"remote", "git@github.com:liferay/liferay-portal.git");

		if (autopull != null) {
			gitrepoProperties.setProperty("autopull", autopull);
		}

		if (mode != null) {
			gitrepoProperties.setProperty("mode", mode);
		}

		CentralGitSubrepository mock = Mockito.mock(
			CentralGitSubrepository.class);

		ReflectionTestUtil.setFieldValue(
			mock, "_gitrepoProperties", gitrepoProperties);

		Mockito.doCallRealMethod(
		).when(
			mock
		).isAutoPullEnabled();

		Assert.assertEquals(expected, mock.isAutoPullEnabled());
	}

	private void _testIsGitSubrepositoryUpstreamCommitMerged(
			String commit, boolean expected, String upstreamCommit)
		throws Exception {

		Properties gitrepoProperties = new Properties();

		gitrepoProperties.setProperty(
			"remote", "git@github.com:liferay/liferay-portal.git");

		if (!commit.isEmpty()) {
			gitrepoProperties.setProperty("commit", commit);
		}

		CentralGitSubrepository mock = Mockito.mock(
			CentralGitSubrepository.class);

		ReflectionTestUtil.setFieldValue(
			mock, "_gitrepoProperties", gitrepoProperties);

		ReflectionTestUtil.setFieldValue(
			mock, "_gitSubrepositoryUpstreamCommit", upstreamCommit);

		Mockito.doCallRealMethod(
		).when(
			mock
		).isGitSubrepositoryUpstreamCommitMerged();

		Mockito.doCallRealMethod(
		).when(
			mock
		).getGitSubrepositoryUpstreamCommit();

		Assert.assertEquals(
			expected, mock.isGitSubrepositoryUpstreamCommitMerged());
	}

}