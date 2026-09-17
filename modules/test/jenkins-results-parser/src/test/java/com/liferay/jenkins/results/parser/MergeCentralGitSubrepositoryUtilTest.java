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

import org.mockito.ArgumentCaptor;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

/**
 * @author Charlotte Wong
 */
public class MergeCentralGitSubrepositoryUtilTest
	extends com.liferay.jenkins.results.parser.Test {

	@Test
	public void testCreateGitSubrepositoryMergePullRequests() throws Exception {
		File centralWorkingDirectory = File.createTempFile(
			"merge-central-subrepository-", null);

		centralWorkingDirectory.delete();

		centralWorkingDirectory.mkdir();

		File modulesDir = new File(centralWorkingDirectory, "modules");

		_writeGitrepoFile(modulesDir, "blacklisted", _SSH_REMOTE_URL);

		File brokenDir = _writeGitrepoFile(modulesDir, "broken", null);

		Assert.assertEquals(
			JenkinsResultsParserUtil.combine(
				"Skipped these subrepositories with no \"remote\" key:\n",
				brokenDir.getPath()),
			_createGitSubrepositoryMergePullRequests(centralWorkingDirectory));
	}

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

	private String _createGitSubrepositoryMergePullRequests(
			File centralWorkingDirectory)
		throws Exception {

		Properties buildProperties = new Properties();

		buildProperties.setProperty(
			"email.list[merge-central-subrepository]", "ci@liferay.com");

		GitWorkingDirectory gitWorkingDirectory = Mockito.mock(
			GitWorkingDirectory.class);

		Mockito.when(
			gitWorkingDirectory.getWorkingDirectory()
		).thenReturn(
			centralWorkingDirectory
		);

		ArgumentCaptor<String> argumentCaptor = ArgumentCaptor.forClass(
			String.class);

		try (MockedStatic<GitWorkingDirectoryFactory>
				gitWorkingDirectoryFactoryMockedStatic = Mockito.mockStatic(
					GitWorkingDirectoryFactory.class);
			MockedStatic<JenkinsResultsParserUtil>
				jenkinsResultsParserUtilMockedStatic = Mockito.mockStatic(
					JenkinsResultsParserUtil.class, Mockito.CALLS_REAL_METHODS);
			MockedStatic<NotificationUtil> notificationUtilMockedStatic =
				Mockito.mockStatic(NotificationUtil.class)) {

			gitWorkingDirectoryFactoryMockedStatic.when(
				() -> GitWorkingDirectoryFactory.newGitWorkingDirectory(
					Mockito.anyString(), Mockito.anyString())
			).thenReturn(
				gitWorkingDirectory
			);

			jenkinsResultsParserUtilMockedStatic.when(
				JenkinsResultsParserUtil::getBuildProperties
			).thenReturn(
				buildProperties
			);

			jenkinsResultsParserUtilMockedStatic.when(
				() -> JenkinsResultsParserUtil.getBuildPropertyAsList(
					Mockito.anyBoolean(), Mockito.anyString())
			).thenReturn(
				Arrays.asList("com-liferay-osb-asah-private")
			);

			MergeCentralGitSubrepositoryUtil.
				createGitSubrepositoryMergePullRequests(
					centralWorkingDirectory.getPath(), "7.0.x", "liferay",
					"liferay", "7.0.x");

			notificationUtilMockedStatic.verify(
				() -> NotificationUtil.sendEmail(
					argumentCaptor.capture(), Mockito.anyString(),
					Mockito.anyString(), Mockito.anyString()));
		}

		return argumentCaptor.getValue();
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

	private File _writeGitrepoFile(File modulesDir, String name, String remote)
		throws Exception {

		File gitrepoDir = new File(modulesDir, name);

		gitrepoDir.mkdirs();

		String content = "[subrepo]\n\tmode = pull\n";

		if (remote != null) {
			content = JenkinsResultsParserUtil.combine(
				content, "\tremote = ", remote, "\n");
		}

		JenkinsResultsParserUtil.write(
			new File(gitrepoDir, ".gitrepo"), content);

		return gitrepoDir;
	}

	private static final String _HTTPS_REMOTE_URL =
		"https://github.com/liferay/com-liferay-osb-asah-private.git";

	private static final String _SSH_REMOTE_URL =
		"git@github.com:liferay/com-liferay-osb-asah-private.git";

}