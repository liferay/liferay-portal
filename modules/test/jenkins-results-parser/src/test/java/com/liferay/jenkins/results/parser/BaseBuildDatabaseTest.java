/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import java.util.Collections;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;

import org.json.JSONObject;

import org.junit.Assert;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Michael Hashimoto
 */
public class BaseBuildDatabaseTest
	extends com.liferay.jenkins.results.parser.Test {

	@Test
	public void testPutPropertyConcurrent() throws Exception {
		BuildDatabase buildDatabase = new BaseBuildDatabase(
			BuildDatabaseTestUtil.newBuildDir()) {
		};

		String propertiesKey = RandomTestUtil.randomString();
		String propertyName = RandomTestUtil.randomString();
		String propertyValue = RandomTestUtil.randomString();

		Thread putPropertyThread = new Thread(
			() -> buildDatabase.putProperty(
				propertiesKey, propertyName, propertyValue));

		CountDownLatch countDownLatch = new CountDownLatch(1);

		Workspace workspace = Mockito.mock(Workspace.class);

		Mockito.doAnswer(
			invocation -> {
				countDownLatch.countDown();

				_waitForBlocked(putPropertyThread);

				return new JSONObject();
			}
		).when(
			workspace
		).getJSONObject();

		String workspaceKey = RandomTestUtil.randomString();

		Thread putWorkspaceThread = new Thread(
			() -> buildDatabase.putWorkspace(workspaceKey, workspace));

		putPropertyThread.setDaemon(true);
		putWorkspaceThread.setDaemon(true);

		putWorkspaceThread.start();

		countDownLatch.await();

		putPropertyThread.start();

		putPropertyThread.join(_TIMEOUT);
		putWorkspaceThread.join(_TIMEOUT);

		Assert.assertFalse(putPropertyThread.isAlive());
		Assert.assertFalse(putWorkspaceThread.isAlive());

		Properties properties = buildDatabase.getProperties(propertiesKey);

		Assert.assertEquals(
			propertyValue, properties.getProperty(propertyName));

		Assert.assertTrue(buildDatabase.hasWorkspace(workspaceKey));
	}

	@Test
	public void testUploadBuildDatabaseFileToCloudBucket() {
		_testUploadBuildDatabaseFileToCloudBucket(null, " ");
		_testUploadBuildDatabaseFileToCloudBucket(null, "");
		_testUploadBuildDatabaseFileToCloudBucket(null, null);

		String s3BucketDistPath = RandomTestUtil.randomString();

		_testUploadBuildDatabaseFileToCloudBucket(
			s3BucketDistPath + "/" +
				BuildDatabase.FILE_NAME_BUILD_DATABASE_JSON,
			s3BucketDistPath);
	}

	private void _testUploadBuildDatabaseFileToCloudBucket(
		String expectedPath, String s3BucketDistPath) {

		mockEnvironment(
			Collections.singletonMap("S3_BUCKET_DIST_PATH", s3BucketDistPath));

		BaseBuildDatabase baseBuildDatabase = Mockito.mock(
			BaseBuildDatabase.class);

		Mockito.doCallRealMethod(
		).when(
			baseBuildDatabase
		).uploadBuildDatabaseFileToCloudBucket();

		PrintStream printStream = System.out;

		ByteArrayOutputStream byteArrayOutputStream =
			new ByteArrayOutputStream();

		System.setOut(new PrintStream(byteArrayOutputStream));

		try {
			baseBuildDatabase.uploadBuildDatabaseFileToCloudBucket();
		}
		finally {
			System.setOut(printStream);
		}

		String output = byteArrayOutputStream.toString();

		if (expectedPath == null) {
			Assert.assertTrue(output.startsWith("WARNING:"));

			Mockito.verify(
				baseBuildDatabase, Mockito.never()
			).uploadBuildDatabaseFileToCloudBucket(
				Mockito.anyString()
			);

			return;
		}

		Mockito.verify(
			baseBuildDatabase
		).uploadBuildDatabaseFileToCloudBucket(
			expectedPath
		);
	}

	private void _waitForBlocked(Thread thread) throws Exception {
		long timeout = System.currentTimeMillis() + _TIMEOUT;

		while ((thread.getState() != Thread.State.BLOCKED) &&
			   (System.currentTimeMillis() < timeout)) {

			Thread.sleep(10);
		}
	}

	private static final long _TIMEOUT = 10000;

}