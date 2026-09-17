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
		Assert.assertEquals(
			"ci-merge-com-liferay-osb-asah-private-7.0.x-abc1234def5678",
			ReflectionTestUtil.invoke(
				MergeCentralGitSubrepositoryUtil.class, "_getMergeBranchName",
				new Class<?>[] {String.class, String.class, String.class},
				"7.0.x", "com-liferay-osb-asah-private", "abc1234def5678"));
	}

	@Test
	public void testGetMergeBranchNamePrefix() throws Exception {
		Assert.assertEquals(
			"ci-merge-com-liferay-osb-asah-private-7.0.x",
			ReflectionTestUtil.invoke(
				MergeCentralGitSubrepositoryUtil.class,
				"_getMergeBranchNamePrefix", new Class<?>[] {String.class},
				"ci-merge-com-liferay-osb-asah-private-7.0.x-abc1234def"));
	}

	@Test
	public void testGetRemote() throws Exception {
		File gitrepoFile = new File("modules/integrations/mulesoft/.gitrepo");

		Properties present = new Properties();

		present.setProperty(
			"remote", "git@github.com:liferay/liferay-portal.git");

		Assert.assertEquals(
			"git@github.com:liferay/liferay-portal.git",
			ReflectionTestUtil.invoke(
				MergeCentralGitSubrepositoryUtil.class, "_getRemote",
				new Class<?>[] {Properties.class, File.class}, present,
				gitrepoFile));

		Assert.assertNull(
			ReflectionTestUtil.invoke(
				MergeCentralGitSubrepositoryUtil.class, "_getRemote",
				new Class<?>[] {Properties.class, File.class}, new Properties(),
				gitrepoFile));
	}

	@Test
	public void testIsBlacklisted() throws Exception {
		String gitRemote =
			"git@github.com:liferay/com-liferay-osb-asah-private.git";
		String httpsRemote =
			"https://github.com/liferay/com-liferay-osb-asah-private.git";
		List<String> blacklist = Arrays.asList("com-liferay-osb-asah-private");

		Assert.assertFalse(
			(boolean)ReflectionTestUtil.invoke(
				MergeCentralGitSubrepositoryUtil.class, "_isBlacklisted",
				new Class<?>[] {String.class, List.class}, gitRemote,
				Collections.emptyList()));
		Assert.assertFalse(
			(boolean)ReflectionTestUtil.invoke(
				MergeCentralGitSubrepositoryUtil.class, "_isBlacklisted",
				new Class<?>[] {String.class, List.class}, httpsRemote,
				blacklist));
		Assert.assertFalse(
			(boolean)ReflectionTestUtil.invoke(
				MergeCentralGitSubrepositoryUtil.class, "_isBlacklisted",
				new Class<?>[] {String.class, List.class},
				"git@github.com:liferay/other-repo.git", blacklist));
		Assert.assertTrue(
			(boolean)ReflectionTestUtil.invoke(
				MergeCentralGitSubrepositoryUtil.class, "_isBlacklisted",
				new Class<?>[] {String.class, List.class}, gitRemote,
				blacklist));
	}

}