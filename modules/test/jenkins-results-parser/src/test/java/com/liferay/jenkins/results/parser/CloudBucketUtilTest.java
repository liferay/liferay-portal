/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser;

import java.io.File;

import java.util.Properties;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import org.mockito.Mockito;

/**
 * @author Kenji Heigel
 */
public class CloudBucketUtilTest
	extends com.liferay.jenkins.results.parser.Test {

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		Properties buildProperties = new Properties();

		buildProperties.setProperty(
			"cloud.ci.s3.bucket.object.refs.dir",
			JenkinsResultsParserUtil.getCanonicalPath(
				temporaryFolder.newFolder("s3-object-refs")));

		JenkinsResultsParserUtil.setBuildProperties(buildProperties);
	}

	@Test
	public void testDeleteS3ObjectRefsOlderThan() throws Exception {
		Shell shell = mockShell();

		String newS3ObjectPath = _randomS3ObjectPath();
		String oldS3ObjectPath = _randomS3ObjectPath();

		File newS3ObjectRefFile = _writeS3ObjectRefFile(
			newS3ObjectPath, newS3ObjectPath);
		File oldS3ObjectRefFile = _writeS3ObjectRefFile(
			oldS3ObjectPath, oldS3ObjectPath);

		newS3ObjectRefFile.setLastModified(
			_getLastModified(_MAX_AGE_SECONDS - 60));
		oldS3ObjectRefFile.setLastModified(
			_getLastModified(_MAX_AGE_SECONDS + 2));

		CloudBucketUtil.deleteS3ObjectRefsOlderThan(_MAX_AGE_SECONDS);

		Assert.assertFalse(
			oldS3ObjectRefFile.getPath(), oldS3ObjectRefFile.exists());
		Assert.assertTrue(
			newS3ObjectRefFile.getPath(), newS3ObjectRefFile.exists());

		Mockito.verifyNoInteractions(shell);
	}

	@Test
	public void testGetNewestS3ObjectLastModified() throws Exception {
		String s3ObjectPath = _randomS3ObjectPath();

		_testGetNewestS3ObjectLastModified(
			1757001600000L, "2025-09-04T16:00:00+00:00", s3ObjectPath);
		_testGetNewestS3ObjectLastModified(
			Long.MIN_VALUE, "None", s3ObjectPath);
		_testGetNewestS3ObjectLastModified(
			Long.MIN_VALUE, RandomTestUtil.randomString(), s3ObjectPath);
	}

	@Test
	public void testIsS3ObjectOlderThan() throws Exception {
		String s3ObjectPath = _randomS3ObjectPath();

		testEquals(
			false,
			CloudBucketUtil.isS3ObjectOlderThan(
				s3ObjectPath, _MAX_AGE_SECONDS));

		File s3ObjectRefFile = _writeS3ObjectRefFile(
			s3ObjectPath, s3ObjectPath);

		s3ObjectRefFile.setLastModified(
			_getLastModified(_MAX_AGE_SECONDS - 60));

		testEquals(
			false,
			CloudBucketUtil.isS3ObjectOlderThan(
				s3ObjectPath, _MAX_AGE_SECONDS));

		s3ObjectRefFile.setLastModified(_getLastModified(_MAX_AGE_SECONDS + 2));

		testEquals(
			true,
			CloudBucketUtil.isS3ObjectOlderThan(
				s3ObjectPath, _MAX_AGE_SECONDS));
	}

	@Test
	public void testUploadS3File() throws Exception {
		String s3ObjectPath = _randomS3ObjectPath();
		String targetS3ObjectPath = _randomS3ObjectPath();

		_writeS3ObjectRefFile(s3ObjectPath, targetS3ObjectPath);

		Shell shell = mockShell();

		setShellCommandOutput("aws s3 cp", shell, "");

		CloudBucketUtil.uploadS3File(s3ObjectPath, temporaryFolder.newFile());

		testEquals(
			targetS3ObjectPath, read(_getS3ObjectRefFile(targetS3ObjectPath)));

		Mockito.verify(
			shell
		).doExecute(
			Mockito.argThat(
				executionRequest -> hasCommand(
					executionRequest, "aws s3 cp", targetS3ObjectPath))
		);
	}

	@Rule
	public TemporaryFolder temporaryFolder = new TemporaryFolder();

	private long _getLastModified(long ageSeconds) {
		return System.currentTimeMillis() - (ageSeconds * 1000);
	}

	private File _getS3ObjectRefFile(String s3ObjectPath) throws Exception {
		return ReflectionTestUtil.invoke(
			CloudBucketUtil.class, "_getS3ObjectRefFile",
			new Class<?>[] {String.class}, s3ObjectPath);
	}

	private String _randomS3ObjectPath() {
		return JenkinsResultsParserUtil.combine(
			"s3://", RandomTestUtil.randomString(), "/",
			RandomTestUtil.randomString());
	}

	private void _testGetNewestS3ObjectLastModified(
			long expected, String listObjectsOutput, String s3ObjectPath)
		throws Exception {

		Shell shell = mockShell();

		setShellCommandOutput(
			"aws s3api list-objects-v2", shell, listObjectsOutput);

		testEquals(
			expected,
			CloudBucketUtil.getNewestS3ObjectLastModified(s3ObjectPath));

		Mockito.verify(
			shell
		).doExecute(
			Mockito.argThat(
				executionRequest -> hasCommand(
					executionRequest, "sort_by(Contents, &LastModified)[-1]",
					s3ObjectPath.replaceFirst("s3://[^/]+/", "")))
		);
	}

	private File _writeS3ObjectRefFile(
			String s3ObjectPath, String s3ObjectRefFileContent)
		throws Exception {

		File s3ObjectRefFile = _getS3ObjectRefFile(s3ObjectPath);

		JenkinsResultsParserUtil.write(s3ObjectRefFile, s3ObjectRefFileContent);

		return s3ObjectRefFile;
	}

	private static final long _MAX_AGE_SECONDS = 3600;

}