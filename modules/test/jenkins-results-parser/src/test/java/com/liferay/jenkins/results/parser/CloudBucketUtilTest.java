/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser;

import java.io.File;
import java.io.IOException;

import java.util.Properties;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import org.mockito.MockedStatic;
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
		buildProperties.setProperty(
			"jenkins.tmp.dir",
			JenkinsResultsParserUtil.combine(
				JenkinsResultsParserUtil.getCanonicalPath(
					temporaryFolder.getRoot()),
				"/"));

		JenkinsResultsParserUtil.setBuildProperties(buildProperties);
	}

	@Test
	public void testDeleteS3ObjectRefsOlderThan() throws Exception {
		Shell shell = mockShell();

		String newS3ObjectPath = _randomS3ObjectPath();

		File newS3ObjectRefFile = _writeS3ObjectRefFile(
			newS3ObjectPath, newS3ObjectPath);

		newS3ObjectRefFile.setLastModified(
			_getLastModified(_MAX_AGE_SECONDS - 60));

		String oldS3ObjectPath = _randomS3ObjectPath();

		File oldS3ObjectRefFile = _writeS3ObjectRefFile(
			oldS3ObjectPath, oldS3ObjectPath);

		oldS3ObjectRefFile.setLastModified(
			_getLastModified(_MAX_AGE_SECONDS + 2));

		CloudBucketUtil.deleteS3ObjectRefsOlderThan(_MAX_AGE_SECONDS);

		Assert.assertTrue(
			newS3ObjectRefFile.getPath(), newS3ObjectRefFile.exists());
		Assert.assertFalse(
			oldS3ObjectRefFile.getPath(), oldS3ObjectRefFile.exists());

		Mockito.verifyNoInteractions(shell);
	}

	@Test
	public void testDownloadS3File() throws Exception {
		_enableChecksumValidation();

		_testDownloadS3File(1, "build-database.json");
		_testDownloadS3File(2, RandomTestUtil.randomString());
	}

	@Test
	public void testExecuteAWSCommandsRetries() throws Exception {
		Shell shell = mockShell();

		Mockito.doReturn(
			new Shell.ExecutionResult(1, "Unable to locate credentials", "")
		).when(
			shell
		).doExecute(
			Mockito.argThat(
				executionRequest -> hasCommand(executionRequest, "aws s3 cp"))
		);

		try (MockedStatic<JenkinsResultsParserUtil> mockedStatic =
				Mockito.mockStatic(
					JenkinsResultsParserUtil.class, Mockito.CALLS_REAL_METHODS);
			MockedStatic<NotificationUtil> notificationMockedStatic =
				Mockito.mockStatic(NotificationUtil.class)) {

			mockedStatic.when(
				() -> JenkinsResultsParserUtil.sleep(Mockito.anyLong())
			).thenAnswer(
				invocation -> null
			);

			CloudBucketUtil.uploadS3File(
				_randomS3ObjectPath(), temporaryFolder.newFile());

			mockedStatic.verify(
				() -> JenkinsResultsParserUtil.sleep(Mockito.anyLong()),
				Mockito.times(3));

			Mockito.verify(
				shell, Mockito.times(4)
			).doExecute(
				Mockito.argThat(
					executionRequest -> hasCommand(
						executionRequest, "aws s3 cp"))
			);

			notificationMockedStatic.verify(
				() -> NotificationUtil.sendSlackNotification(
					Mockito.anyString(), Mockito.anyString(),
					Mockito.anyString(), Mockito.anyString(),
					Mockito.anyString()));
		}
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
	public void testIsS3ObjectPathAvailable() throws Exception {
		String s3ObjectPath = _randomS3ObjectPath();

		_testIsS3ObjectPathAvailable(false, s3ObjectPath, "", s3ObjectPath);
		_testIsS3ObjectPathAvailable(
			true, s3ObjectPath, RandomTestUtil.randomString(), s3ObjectPath);

		String targetS3ObjectPath = _randomS3ObjectPath();

		_writeS3ObjectRefFile(s3ObjectPath, targetS3ObjectPath);

		_testIsS3ObjectPathAvailable(
			false, targetS3ObjectPath, "", s3ObjectPath);

		Shell shell = mockShell();

		testEquals(
			false,
			CloudBucketUtil.isS3ObjectPathAvailable(
				RandomTestUtil.randomString()));

		_writeS3ObjectRefFile(targetS3ObjectPath, s3ObjectPath);

		testEquals(
			false, CloudBucketUtil.isS3ObjectPathAvailable(s3ObjectPath));

		Mockito.verifyNoInteractions(shell);
	}

	@Test
	public void testReplaceS3ObjectPath() throws Exception {
		String s3ObjectPath = _randomS3ObjectPath();

		testEquals(s3ObjectPath, _replaceS3ObjectPath(s3ObjectPath));

		_writeS3ObjectRefFile(s3ObjectPath, s3ObjectPath);

		testEquals(s3ObjectPath, _replaceS3ObjectPath(s3ObjectPath));

		String targetS3ObjectPath = _randomS3ObjectPath();

		_writeS3ObjectRefFile(s3ObjectPath, targetS3ObjectPath);

		testEquals(targetS3ObjectPath, _replaceS3ObjectPath(s3ObjectPath));

		String middleS3ObjectPath = _randomS3ObjectPath();

		_writeS3ObjectRefFile(middleS3ObjectPath, targetS3ObjectPath);
		_writeS3ObjectRefFile(s3ObjectPath, middleS3ObjectPath);

		testEquals(targetS3ObjectPath, _replaceS3ObjectPath(s3ObjectPath));
	}

	@Test
	public void testReplaceS3ObjectPathFailure() throws Exception {
		String s3ObjectPath = _randomS3ObjectPath();

		File s3ObjectRefFile = _writeS3ObjectRefFile(s3ObjectPath, "");

		_testReplaceS3ObjectPathFailure(
			JenkinsResultsParserUtil.combine(
				"Unable to resolve empty S3 object reference file ",
				JenkinsResultsParserUtil.getCanonicalPath(s3ObjectRefFile)),
			s3ObjectPath);

		String s3ObjectRefFileContent = JenkinsResultsParserUtil.combine(
			RandomTestUtil.randomString(), " ", _randomS3ObjectPath(), " ",
			RandomTestUtil.randomString());

		_writeS3ObjectRefFile(s3ObjectPath, s3ObjectRefFileContent);

		_testReplaceS3ObjectPathFailure(
			JenkinsResultsParserUtil.combine(
				"Invalid S3 object path: ", s3ObjectRefFileContent, " in ",
				JenkinsResultsParserUtil.getCanonicalPath(s3ObjectRefFile)),
			s3ObjectPath);

		String targetS3ObjectPath = _randomS3ObjectPath();

		_writeS3ObjectRefFile(s3ObjectPath, targetS3ObjectPath);
		_writeS3ObjectRefFile(targetS3ObjectPath, s3ObjectPath);

		_testReplaceS3ObjectPathFailure(
			JenkinsResultsParserUtil.combine(
				"Unable to resolve circular S3 object reference ", s3ObjectPath,
				" -> ", targetS3ObjectPath, " -> ", s3ObjectPath),
			s3ObjectPath);
	}

	@Test
	public void testReplaceS3ObjectPathRetries() throws Exception {
		String s3ObjectPath = _randomS3ObjectPath();

		_writeS3ObjectRefFile(s3ObjectPath, s3ObjectPath);

		try (MockedStatic<JenkinsResultsParserUtil> mockedStatic =
				Mockito.mockStatic(
					JenkinsResultsParserUtil.class,
					Mockito.CALLS_REAL_METHODS)) {

			mockedStatic.when(
				() -> JenkinsResultsParserUtil.read(Mockito.any(File.class))
			).thenThrow(
				new IOException()
			).thenCallRealMethod();

			mockedStatic.when(
				() -> JenkinsResultsParserUtil.sleep(Mockito.anyLong())
			).thenAnswer(
				invocation -> null
			);

			String actualS3ObjectPath = ReflectionTestUtil.invoke(
				CloudBucketUtil.class, "_replaceS3ObjectPath",
				new Class<?>[] {String.class}, s3ObjectPath);

			mockedStatic.verify(
				() -> JenkinsResultsParserUtil.read(Mockito.any(File.class)),
				Mockito.times(2));
			mockedStatic.verify(
				() -> JenkinsResultsParserUtil.sleep(Mockito.anyLong()));

			testEquals(s3ObjectPath, actualS3ObjectPath);
		}
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

		_enableChecksumValidation();

		_testUploadS3File(1, "build-database.json");
		_testUploadS3File(2, RandomTestUtil.randomString());
	}

	@Test
	public void testValidateChecksumFile() throws Exception {
		_enableChecksumValidation();

		Shell shell = mockShell();

		setShellCommandOutput("sha512sum", shell, RandomTestUtil.randomSHA());

		File destinationFile = temporaryFolder.newFile();

		File destinationChecksumFile = new File(
			destinationFile.getParentFile(),
			destinationFile.getName() + ".sha512");

		JenkinsResultsParserUtil.writeSHAFile(
			destinationFile, destinationChecksumFile);

		_validateChecksumFile(destinationFile, _randomS3ObjectPath());

		Assert.assertTrue(
			"Deleted a file whose checksum matched", destinationFile.exists());

		JenkinsResultsParserUtil.write(
			destinationChecksumFile, RandomTestUtil.randomSHA());

		try {
			_validateChecksumFile(destinationFile, _randomS3ObjectPath());

			Assert.fail("Accepted a file whose checksum did not match");
		}
		catch (IOException ioException) {
			String message = ioException.getMessage();

			Assert.assertTrue(
				message, message.contains(destinationFile.getName()));
		}

		Assert.assertFalse(
			"Kept a file whose checksum did not match",
			destinationFile.exists());
	}

	@Rule
	public TemporaryFolder temporaryFolder = new TemporaryFolder();

	private void _enableChecksumValidation() throws Exception {
		Properties buildProperties =
			JenkinsResultsParserUtil.getBuildProperties();

		buildProperties.setProperty(
			"cloud.ci.s3.bucket.validate.checksum.enabled", "true");

		JenkinsResultsParserUtil.setBuildProperties(buildProperties);
	}

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

	private String _replaceS3ObjectPath(String s3ObjectPath) throws Exception {
		try (MockedStatic<JenkinsResultsParserUtil> mockedStatic =
				Mockito.mockStatic(
					JenkinsResultsParserUtil.class,
					Mockito.CALLS_REAL_METHODS)) {

			mockedStatic.when(
				() -> JenkinsResultsParserUtil.sleep(Mockito.anyLong())
			).thenAnswer(
				invocation -> null
			);

			return ReflectionTestUtil.invoke(
				CloudBucketUtil.class, "_replaceS3ObjectPath",
				new Class<?>[] {String.class}, s3ObjectPath);
		}
	}

	private void _testDownloadS3File(int commandCount, String fileName)
		throws Exception {

		Shell shell = mockShell();

		setShellCommandOutput("aws s3 cp", shell, "");

		String s3ObjectPath = _randomS3ObjectPath();

		CloudBucketUtil.downloadS3File(
			new File(temporaryFolder.getRoot(), fileName), s3ObjectPath);

		_verifyS3Copies(commandCount, s3ObjectPath, shell);
	}

	private void _testGetNewestS3ObjectLastModified(
			long expected, String objectsOutput, String s3ObjectPath)
		throws Exception {

		Shell shell = mockShell();

		setShellCommandOutput(
			"aws s3api list-objects-v2", shell, objectsOutput);

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

	private void _testIsS3ObjectPathAvailable(
			boolean expected, String expectedS3ObjectPath, String s3FilesOutput,
			String s3ObjectPath)
		throws Exception {

		Shell shell = mockShell();

		setShellCommandOutput("aws s3 ls", shell, s3FilesOutput);

		testEquals(
			expected, CloudBucketUtil.isS3ObjectPathAvailable(s3ObjectPath));

		Mockito.verify(
			shell
		).doExecute(
			Mockito.argThat(
				executionRequest -> hasCommand(
					executionRequest, "aws s3 ls", expectedS3ObjectPath))
		);
	}

	private void _testReplaceS3ObjectPathFailure(
			String expectedMessage, String s3ObjectPath)
		throws Exception {

		try {
			_replaceS3ObjectPath(s3ObjectPath);

			Assert.fail(expectedMessage);
		}
		catch (RuntimeException runtimeException) {
			testEquals(expectedMessage, runtimeException.getMessage());
		}
	}

	private void _testUploadS3File(int commandCount, String fileName)
		throws Exception {

		Shell shell = mockShell();

		setShellCommandOutput("aws s3 cp", shell, "");
		setShellCommandOutput("sha512sum", shell, RandomTestUtil.randomSHA());

		String s3ObjectPath = _randomS3ObjectPath();

		CloudBucketUtil.uploadS3File(
			s3ObjectPath, temporaryFolder.newFile(fileName));

		_verifyS3Copies(commandCount, s3ObjectPath, shell);
	}

	private void _validateChecksumFile(
			File destinationFile, String s3SourcePath)
		throws Exception {

		ReflectionTestUtil.invoke(
			CloudBucketUtil.class, "_validateChecksumFile",
			new Class<?>[] {File.class, String.class}, destinationFile,
			s3SourcePath);
	}

	private void _verifyS3Copies(
			int commandCount, String s3ObjectPath, Shell shell)
		throws Exception {

		Mockito.verify(
			shell, Mockito.times(commandCount)
		).doExecute(
			Mockito.argThat(
				executionRequest -> hasCommand(executionRequest, "aws s3 cp"))
		);

		Mockito.verify(
			shell, Mockito.times(commandCount - 1)
		).doExecute(
			Mockito.argThat(
				executionRequest -> hasCommand(
					executionRequest, "aws s3 cp", s3ObjectPath + ".sha512"))
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