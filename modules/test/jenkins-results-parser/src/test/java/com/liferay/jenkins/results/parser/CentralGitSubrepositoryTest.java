/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser;

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
			"com-liferay-osb-asah-private",
			"git@github.com:liferay/com-liferay-osb-asah-private.git");
		_testGetGitSubrepositoryName(
			"liferay-portal", "git@github.com:liferay/liferay-portal.git");
	}

	@Test
	public void testGetGitSubrepositoryUsername() {
		_testGetGitSubrepositoryUsername(
			"brianchandotcom",
			"git@github.com:brianchandotcom/liferay-portal.git");
		_testGetGitSubrepositoryUsername(
			"liferay", "git@github.com:liferay/liferay-portal.git");
	}

	@Test
	public void testIsAutoPullEnabled() {
		_testIsAutoPullEnabled(null, false, null);
		_testIsAutoPullEnabled(null, false, "pull");
		_testIsAutoPullEnabled(null, false, "push");
		_testIsAutoPullEnabled("false", false, "pull");
		_testIsAutoPullEnabled("true", false, "push");
		_testIsAutoPullEnabled("true", true, "pull");
	}

	@Test
	public void testIsGitSubrepositoryUpstreamCommitMerged() throws Exception {
		String upstreamCommit = RandomTestUtil.randomString();

		_testIsGitSubrepositoryUpstreamCommitMerged(
			null, false, upstreamCommit);
		_testIsGitSubrepositoryUpstreamCommitMerged(
			RandomTestUtil.randomString(), false, upstreamCommit);
		_testIsGitSubrepositoryUpstreamCommitMerged(
			upstreamCommit, true, upstreamCommit);
	}

	private void _testGetGitSubrepositoryName(
		String expectedName, String remote) {

		Properties gitrepoProperties = new Properties();

		gitrepoProperties.setProperty("remote", remote);

		CentralGitSubrepository centralGitSubrepository = Mockito.mock(
			CentralGitSubrepository.class);

		ReflectionTestUtil.setFieldValue(
			centralGitSubrepository, "_gitrepoProperties", gitrepoProperties);

		Assert.assertEquals(
			expectedName,
			ReflectionTestUtil.invoke(
				centralGitSubrepository, "_getGitSubrepositoryName",
				new Class<?>[0]));
	}

	private void _testGetGitSubrepositoryUsername(
		String expectedUsername, String remote) {

		Properties gitrepoProperties = new Properties();

		gitrepoProperties.setProperty("remote", remote);

		CentralGitSubrepository centralGitSubrepository = Mockito.mock(
			CentralGitSubrepository.class);

		ReflectionTestUtil.setFieldValue(
			centralGitSubrepository, "_gitrepoProperties", gitrepoProperties);

		Assert.assertEquals(
			expectedUsername,
			ReflectionTestUtil.invoke(
				centralGitSubrepository, "_getGitSubrepositoryUsername",
				new Class<?>[0]));
	}

	private void _testIsAutoPullEnabled(
		String autopull, boolean expected, String mode) {

		Properties gitrepoProperties = new Properties();

		if (autopull != null) {
			gitrepoProperties.setProperty("autopull", autopull);
		}

		if (mode != null) {
			gitrepoProperties.setProperty("mode", mode);
		}

		CentralGitSubrepository centralGitSubrepository = Mockito.mock(
			CentralGitSubrepository.class);

		ReflectionTestUtil.setFieldValue(
			centralGitSubrepository, "_gitrepoProperties", gitrepoProperties);

		Mockito.doCallRealMethod(
		).when(
			centralGitSubrepository
		).isAutoPullEnabled();

		Assert.assertEquals(
			expected, centralGitSubrepository.isAutoPullEnabled());
	}

	private void _testIsGitSubrepositoryUpstreamCommitMerged(
			String commit, boolean expected, String upstreamCommit)
		throws Exception {

		Properties gitrepoProperties = new Properties();

		if (commit != null) {
			gitrepoProperties.setProperty("commit", commit);
		}

		CentralGitSubrepository centralGitSubrepository = Mockito.mock(
			CentralGitSubrepository.class);

		ReflectionTestUtil.setFieldValue(
			centralGitSubrepository, "_gitrepoProperties", gitrepoProperties);

		ReflectionTestUtil.setFieldValue(
			centralGitSubrepository, "_gitSubrepositoryUpstreamCommit",
			upstreamCommit);

		Mockito.doCallRealMethod(
		).when(
			centralGitSubrepository
		).getGitSubrepositoryUpstreamCommit();

		Mockito.doCallRealMethod(
		).when(
			centralGitSubrepository
		).isGitSubrepositoryUpstreamCommitMerged();

		Assert.assertEquals(
			expected,
			centralGitSubrepository.isGitSubrepositoryUpstreamCommitMerged());
	}

}