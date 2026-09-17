/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser;

import java.io.File;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Charlotte Wong
 */
public class MergeCentralGitSubrepositoryUtilTest
	extends com.liferay.jenkins.results.parser.Test {

	@Test
	public void testGetMergeBranchName() throws Exception {
		String gitSubrepositoryUpstreamCommit = RandomTestUtil.randomSHA();

		Assert.assertEquals(
			"ci-merge-com-liferay-osb-asah-private-7.0.x-" +
				gitSubrepositoryUpstreamCommit,
			ReflectionTestUtil.invoke(
				MergeCentralGitSubrepositoryUtil.class, "_getMergeBranchName",
				new Class<?>[] {String.class, String.class, String.class},
				"7.0.x", "com-liferay-osb-asah-private",
				gitSubrepositoryUpstreamCommit));
	}

	@Test
	public void testGetMergeBranchNamePrefix() throws Exception {
		Assert.assertEquals(
			"ci-merge-com-liferay-osb-asah-private-7.0.x",
			ReflectionTestUtil.invoke(
				MergeCentralGitSubrepositoryUtil.class,
				"_getMergeBranchNamePrefix", new Class<?>[] {String.class},
				"ci-merge-com-liferay-osb-asah-private-7.0.x-" +
					RandomTestUtil.randomSHA()));
	}

	@Test
	public void testGetRemote() throws Exception {
		File gitrepoFile = new File(RandomTestUtil.randomString());

		Properties gitrepoProperties = new Properties();

		gitrepoProperties.setProperty("remote", _SSH_REMOTE_URL);

		Assert.assertEquals(
			_SSH_REMOTE_URL,
			ReflectionTestUtil.invoke(
				MergeCentralGitSubrepositoryUtil.class, "_getRemote",
				new Class<?>[] {Properties.class, File.class},
				gitrepoProperties, gitrepoFile));

		Assert.assertNull(
			ReflectionTestUtil.invoke(
				MergeCentralGitSubrepositoryUtil.class, "_getRemote",
				new Class<?>[] {Properties.class, File.class}, new Properties(),
				gitrepoFile));
	}

	@Test
	public void testIsBlacklisted() throws Exception {
		_testIsBlacklisted(
			false, _SSH_REMOTE_URL, Collections.<String>emptyList());

		List<String> subrepoMergeBlacklist = Arrays.asList(
			"com-liferay-osb-asah-private");

		_testIsBlacklisted(
			false, "git@github.com:liferay/other-repo.git",
			subrepoMergeBlacklist);
		_testIsBlacklisted(false, _HTTPS_REMOTE_URL, subrepoMergeBlacklist);
		_testIsBlacklisted(true, _SSH_REMOTE_URL, subrepoMergeBlacklist);
	}

	private void _testIsBlacklisted(
			boolean expected, String remote, List<String> subrepoMergeBlacklist)
		throws Exception {

		Assert.assertEquals(
			expected,
			(boolean)ReflectionTestUtil.invoke(
				MergeCentralGitSubrepositoryUtil.class, "_isBlacklisted",
				new Class<?>[] {String.class, List.class}, remote,
				subrepoMergeBlacklist));
	}

	private static final String _HTTPS_REMOTE_URL =
		"https://github.com/liferay/com-liferay-osb-asah-private.git";

	private static final String _SSH_REMOTE_URL =
		"git@github.com:liferay/com-liferay-osb-asah-private.git";

}