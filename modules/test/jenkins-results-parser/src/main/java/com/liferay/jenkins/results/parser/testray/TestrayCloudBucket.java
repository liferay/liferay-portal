/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser.testray;

import com.google.api.gax.paging.Page;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;

import com.liferay.jenkins.results.parser.JenkinsResultsParserUtil;
import com.liferay.jenkins.results.parser.ParallelExecutor;

import java.io.File;
import java.io.IOException;

import java.net.MalformedURLException;
import java.net.URL;

import java.nio.charset.StandardCharsets;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeoutException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;

/**
 * @author Michael Hashimoto
 */
public class TestrayCloudBucket {

	public static TestrayCloudBucket getInstance() {
		String name = null;

		try {
			name = JenkinsResultsParserUtil.getBuildProperty(
				"testray.cloud.bucket");
		}
		catch (IOException ioException) {
			System.out.println(
				"WARNING: Unable to get bucket name from mirrors.");
		}

		return getInstance(name);
	}

	public static TestrayCloudBucket getInstance(String name) {
		if (JenkinsResultsParserUtil.isNullOrEmpty(name)) {
			name = DEFAULT_BUCKET_NAME;
		}

		TestrayCloudBucket testrayCloudBucket = _testrayCloudBuckets.get(name);

		if (testrayCloudBucket == null) {
			testrayCloudBucket = new TestrayCloudBucket(name);

			_testrayCloudBuckets.put(name, testrayCloudBucket);
		}

		return testrayCloudBucket;
	}

	public static boolean hasGoogleApplicationCredentials() {
		return hasGoogleApplicationCredentials(null);
	}

	public static boolean hasGoogleApplicationCredentials(String name) {
		if (_hasGoogleApplicationCredentials != null) {
			return _hasGoogleApplicationCredentials;
		}

		String googleApplicationCredentials = System.getenv(
			"GOOGLE_APPLICATION_CREDENTIALS");

		if (JenkinsResultsParserUtil.isNullOrEmpty(
				googleApplicationCredentials)) {

			System.out.println(
				"WARNING: GOOGLE_APPLICATION_CREDENTIALS is not set");

			_hasGoogleApplicationCredentials = false;

			return _hasGoogleApplicationCredentials;
		}

		File googleApplicationCredentialsFile = new File(
			googleApplicationCredentials);

		if (!googleApplicationCredentialsFile.exists()) {
			System.out.println(
				JenkinsResultsParserUtil.combine(
					"WARNING: GOOGLE_APPLICATION_CREDENTIALS=",
					googleApplicationCredentials, " does not exist"));

			_hasGoogleApplicationCredentials = false;

			return _hasGoogleApplicationCredentials;
		}

		try {
			TestrayCloudBucket testrayCloudBucket = getInstance(name);

			testrayCloudBucket._getBucket();

			System.out.println(
				JenkinsResultsParserUtil.combine(
					"INFO: Using GOOGLE_APPLICATION_CREDENTIALS=",
					googleApplicationCredentials));

			_hasGoogleApplicationCredentials = true;
		}
		catch (Exception exception) {
			exception.printStackTrace();

			System.out.println(
				JenkinsResultsParserUtil.combine(
					"WARNING: GOOGLE_APPLICATION_CREDENTIALS=",
					googleApplicationCredentials,
					" is configured incorrectly"));

			_hasGoogleApplicationCredentials = false;
		}

		return _hasGoogleApplicationCredentials;
	}

	public TestrayCloudObject createTestrayCloudObject(String key, File file) {
		long start = JenkinsResultsParserUtil.getCurrentTimeMillis();

		BlobId blobId = BlobId.of(getName(), key);

		String fileName = file.getName();

		Matcher matcher = _fileNamePattern.matcher(fileName);

		BlobInfo.Builder blobInfoBuilder = BlobInfo.newBuilder(blobId);

		if (matcher.find()) {
			String fileExtension = matcher.group("fileExtension");

			if (fileExtension.equals("html")) {
				blobInfoBuilder.setContentType("text/html");
			}
			else if (fileExtension.equals("jpg")) {
				blobInfoBuilder.setContentType("image/jpeg");
			}
			else if (fileExtension.equals("json") ||
					 fileExtension.equals("txt")) {

				blobInfoBuilder.setContentType("text/plain");
			}
			else if (fileExtension.equals("xml")) {
				blobInfoBuilder.setContentType("text/xml");
			}

			String gzipFileExtension = matcher.group("gzipFileExtension");

			if (!JenkinsResultsParserUtil.isNullOrEmpty(gzipFileExtension)) {
				blobInfoBuilder.setContentEncoding("gzip");
			}
		}

		BlobInfo blobInfo = blobInfoBuilder.build();

		try {
			Storage storage = _getStorage();

			Blob blob = storage.create(
				blobInfo, FileUtils.readFileToByteArray(file));

			TestrayCloudObject testrayCloudObject =
				TestrayCloudObjectFactory.newTestrayCloudObject(this, blob);

			System.out.println(
				JenkinsResultsParserUtil.combine(
					"Created Cloud Object ", testrayCloudObject.getURLString(),
					" in ",
					JenkinsResultsParserUtil.toDurationString(
						JenkinsResultsParserUtil.getCurrentTimeMillis() -
							start)));

			return testrayCloudObject;
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}
	}

	public TestrayCloudObject createTestrayCloudObject(
		String key, String value) {

		long start = JenkinsResultsParserUtil.getCurrentTimeMillis();

		BlobId blobId = BlobId.of(getName(), key);

		BlobInfo.Builder blobInfoBuilder = BlobInfo.newBuilder(blobId);

		BlobInfo blobInfo = blobInfoBuilder.build();

		Storage storage = _getStorage();

		Blob blob = storage.create(
			blobInfo, value.getBytes(StandardCharsets.UTF_8));

		TestrayCloudObject testrayCloudObject =
			TestrayCloudObjectFactory.newTestrayCloudObject(this, blob);

		System.out.println(
			JenkinsResultsParserUtil.combine(
				"Created Cloud Object ", testrayCloudObject.getURLString(),
				" in ",
				JenkinsResultsParserUtil.toDurationString(
					JenkinsResultsParserUtil.getCurrentTimeMillis() - start)));

		return testrayCloudObject;
	}

	public List<TestrayCloudObject> createTestrayCloudObjects(File dir) {
		List<TestrayCloudObject> testrayCloudObjects = new ArrayList<>();

		if ((dir == null) || !dir.isDirectory()) {
			return testrayCloudObjects;
		}

		for (File file : JenkinsResultsParserUtil.findFiles(dir, ".*")) {
			TestrayCloudObject testrayCloudObject = createTestrayCloudObject(
				JenkinsResultsParserUtil.getPathRelativeTo(file, dir), file);

			testrayCloudObjects.add(testrayCloudObject);
		}

		return testrayCloudObjects;
	}

	public void deleteTestrayCloudObject(String key) {
		deleteTestrayCloudObject(getTestrayCloudObject(key));
	}

	public void deleteTestrayCloudObject(
		TestrayCloudObject testrayCloudObject) {

		testrayCloudObject.delete();
	}

	public void deleteTestrayCloudObjects(
		List<TestrayCloudObject> testrayCloudObjects) {

		for (TestrayCloudObject testrayCloudObject : testrayCloudObjects) {
			deleteTestrayCloudObject(testrayCloudObject);
		}
	}

	public void downloadTestrayCloudObjects(File baseDir, List<String> keys)
		throws TimeoutException {

		List<Callable<File>> callables = new ArrayList<>();

		for (final String key : keys) {
			Callable<File> callable = new Callable<File>() {

				@Override
				public File call() {
					File file = new File(
						baseDir, key.replaceAll("\\.gz\\s*$", ""));

					if (file.exists()) {
						return null;
					}

					TestrayCloudObject testrayCloudObject =
						getTestrayCloudObject(key);

					if ((testrayCloudObject == null) ||
						!testrayCloudObject.exists()) {

						return null;
					}

					try {
						testrayCloudObject.downloadTo(file);

						return file;
					}
					catch (Exception exception) {
						System.out.println(
							"Unable to download: " +
								testrayCloudObject.getURLString());
					}

					return null;
				}

			};

			callables.add(callable);
		}

		ParallelExecutor<File> parallelExecutor = new ParallelExecutor<>(
			callables, _threadPoolExecutor, "downloadTestrayCloudObjects");

		parallelExecutor.execute();
	}

	public String getName() {
		return _name;
	}

	public String getTestrayCloudBaseURL() {
		return JenkinsResultsParserUtil.combine(
			"https://storage.cloud.google.com/", getName());
	}

	public TestrayCloudObject getTestrayCloudObject(String key) {
		Bucket bucket = _getBucket();

		Blob blob = bucket.get(key);

		if (blob == null) {
			return null;
		}

		return TestrayCloudObjectFactory.newTestrayCloudObject(this, blob);
	}

	public List<TestrayCloudObject> getTestrayCloudObjects() {
		return _getTestrayCloudObjects(new Storage.BlobListOption[0]);
	}

	public List<TestrayCloudObject> getTestrayCloudObjects(
		String directoryPrefix) {

		Storage.BlobListOption[] blobListOptions = {
			Storage.BlobListOption.prefix(directoryPrefix),
			Storage.BlobListOption.currentDirectory()
		};

		return _getTestrayCloudObjects(blobListOptions);
	}

	public URL getURL() {
		try {
			return new URL(
				JenkinsResultsParserUtil.combine(
					"https://console.cloud.google.com/storage/browser/",
					getName(), "?authuser=0"));
		}
		catch (MalformedURLException malformedURLException) {
			throw new RuntimeException(malformedURLException);
		}
	}

	protected static final String DEFAULT_BUCKET_NAME = "testray-results";

	private TestrayCloudBucket(String name) {
		_name = name;
	}

	private Bucket _getBucket() {
		Storage storage = _getStorage();

		return storage.get(getName());
	}

	private Storage _getStorage() {
		StorageOptions storageOptions = StorageOptions.getDefaultInstance();

		return storageOptions.getService();
	}

	private List<TestrayCloudObject> _getTestrayCloudObjects(
		Storage.BlobListOption[] blobListOptions) {

		List<TestrayCloudObject> testrayCloudObjects = new ArrayList<>();

		Storage storage = _getStorage();

		Page<Blob> blobPage = storage.list(getName(), blobListOptions);

		for (Blob blob : blobPage.iterateAll()) {
			testrayCloudObjects.add(
				TestrayCloudObjectFactory.newTestrayCloudObject(this, blob));
		}

		return testrayCloudObjects;
	}

	private static final Pattern _fileNamePattern = Pattern.compile(
		".*\\.(?!gz)(?<fileExtension>([^\\.]+))(?<gzipFileExtension>\\.gz)?");
	private static Boolean _hasGoogleApplicationCredentials;
	private static final Map<String, TestrayCloudBucket> _testrayCloudBuckets =
		new HashMap<>();
	private static final ThreadPoolExecutor _threadPoolExecutor =
		JenkinsResultsParserUtil.getNewThreadPoolExecutor(16, true);

	private final String _name;

}