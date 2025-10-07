/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser.scancode;

import com.google.api.gax.paging.Page;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;

import com.liferay.jenkins.results.parser.JenkinsResultsParserUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import java.net.MalformedURLException;
import java.net.URL;

import java.nio.charset.StandardCharsets;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;

/**
 * @author Brittney Nguyen
 */
public class ScanCodeCloudBucket {

	public static ScanCodeCloudBucket getInstance() {
		String name = null;

		try {
			name = JenkinsResultsParserUtil.getBuildProperty(
				"scancode.cloud.bucket");

			if (JenkinsResultsParserUtil.isNullOrEmpty(name)) {
				name = JenkinsResultsParserUtil.getBuildProperty(
					"scancode.s3.bucket");
			}
		}
		catch (IOException ioException) {
			System.out.println(
				"WARNING: Unable to get bucket name from mirrors");
		}

		return getInstance(name);
	}

	public static ScanCodeCloudBucket getInstance(String name) {
		if (JenkinsResultsParserUtil.isNullOrEmpty(name)) {
			name = DEFAULT_BUCKET_NAME;
		}

		ScanCodeCloudBucket scanCodeCloudBucket = _scanCodeCloudBuckets.get(
			name);

		if (scanCodeCloudBucket == null) {
			scanCodeCloudBucket = new ScanCodeCloudBucket(name);

			_scanCodeCloudBuckets.put(name, scanCodeCloudBucket);
		}

		return scanCodeCloudBucket;
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
				"WARNING: SCANCODE_GOOGLE_APPLICATION_CREDENTIALS is not set");

			_hasGoogleApplicationCredentials = false;

			return _hasGoogleApplicationCredentials;
		}

		File googleApplicationCredentialsFile = new File(
			googleApplicationCredentials);

		if (!googleApplicationCredentialsFile.exists()) {
			System.out.println(
				JenkinsResultsParserUtil.combine(
					"WARNING: SCANCODE_GOOGLE_APPLICATION_CREDENTIALS=",
					googleApplicationCredentials, " does not exist"));

			_hasGoogleApplicationCredentials = false;

			return _hasGoogleApplicationCredentials;
		}

		try {
			ScanCodeCloudBucket scanCodeCloudBucket = getInstance(name);

			scanCodeCloudBucket._getBucket();

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

	public ScanCodeCloudObject createScanCodeCloudObject(
		String key, File file) {

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

			ScanCodeCloudObject scanCodeCloudObject =
				ScanCodeCloudObjectFactory.newScanCodeCloudObject(blob, this);

			_cloudBucketURL = scanCodeCloudObject.getURLString();

			System.out.println(
				JenkinsResultsParserUtil.combine(
					"Created Cloud object ", scanCodeCloudObject.getURLString(),
					" in ",
					JenkinsResultsParserUtil.toDurationString(
						JenkinsResultsParserUtil.getCurrentTimeMillis() -
							start)));

			return scanCodeCloudObject;
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}
	}

	public ScanCodeCloudObject createScanCodeCloudObject(
		String key, String value) {

		long start = JenkinsResultsParserUtil.getCurrentTimeMillis();

		BlobId blobId = BlobId.of(getName(), key);

		BlobInfo.Builder blobInfoBuilder = BlobInfo.newBuilder(blobId);

		BlobInfo blobInfo = blobInfoBuilder.build();

		Storage storage = _getStorage();

		Blob blob = storage.create(
			blobInfo, value.getBytes(StandardCharsets.UTF_8));

		ScanCodeCloudObject scanCodeCloudObject =
			ScanCodeCloudObjectFactory.newScanCodeCloudObject(blob, this);

		_cloudBucketURL = scanCodeCloudObject.getURLString();

		System.out.println(
			JenkinsResultsParserUtil.combine(
				"Created ScanCode Cloud Object ",
				scanCodeCloudObject.getURLString(), " in ",
				JenkinsResultsParserUtil.toDurationString(
					JenkinsResultsParserUtil.getCurrentTimeMillis() - start)));

		return scanCodeCloudObject;
	}

	public List<ScanCodeCloudObject> createScanCodeCloudObjects(File dir) {
		List<ScanCodeCloudObject> scanCodeCloudObjects = new ArrayList<>();

		if ((dir == null) || !dir.isDirectory()) {
			return scanCodeCloudObjects;
		}

		for (File file : JenkinsResultsParserUtil.findFiles(dir, ".*")) {
			ScanCodeCloudObject scanCodeCloudObject = createScanCodeCloudObject(
				JenkinsResultsParserUtil.getPathRelativeTo(file, dir), file);

			scanCodeCloudObjects.add(scanCodeCloudObject);
		}

		return scanCodeCloudObjects;
	}

	public void deleteScanCodeCloudObject(
		ScanCodeCloudObject scanCodeCloudObject) {

		scanCodeCloudObject.delete();
	}

	public void deleteScanCodeCloudObject(String key) {
		deleteScanCodeCloudObject(getScanCodeCloudObject(key));
	}

	public void deleteScanCodeCloudObjects(
		List<ScanCodeCloudObject> scanCodeCloudObjects) {

		for (ScanCodeCloudObject scanCodeCloudObject : scanCodeCloudObjects) {
			deleteScanCodeCloudObject(scanCodeCloudObject);
		}
	}

	public String getCloudBucketURL() {
		return _cloudBucketURL;
	}

	public String getName() {
		return _name;
	}

	public String getScanCodeCloudBaseURL() {
		return JenkinsResultsParserUtil.combine(
			"https://storage.cloud.google.com/", getName());
	}

	public ScanCodeCloudObject getScanCodeCloudObject(String key) {
		Bucket bucket = _getBucket();

		Blob blob = bucket.get(key);

		if (blob == null) {
			return null;
		}

		return ScanCodeCloudObjectFactory.newScanCodeCloudObject(blob, this);
	}

	public List<ScanCodeCloudObject> getScanCodeCloudObjects() {
		List<ScanCodeCloudObject> scanCodeCloudObjects = new ArrayList<>();

		Storage storage = _getStorage();

		Page<Blob> blobPage = storage.list(getName());

		for (Blob blob : blobPage.iterateAll()) {
			scanCodeCloudObjects.add(
				ScanCodeCloudObjectFactory.newScanCodeCloudObject(blob, this));
		}

		return scanCodeCloudObjects;
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

	protected static final String DEFAULT_BUCKET_NAME = "scancode-results";

	private ScanCodeCloudBucket(String name) {
		_name = name;
	}

	private Bucket _getBucket() {
		Storage storage = _getStorage();

		return storage.get(getName());
	}

	private Storage _getStorage() {
		Storage storage = null;

		try {
			String credentials = JenkinsResultsParserUtil.getBuildProperty(
				"scancode.credentials.file");

			storage = StorageOptions.newBuilder(
			).setCredentials(
				ServiceAccountCredentials.fromStream(
					new FileInputStream(credentials))
			).build(
			).getService();
		}
		catch (Exception exception) {
			exception.printStackTrace();
		}

		return storage;
	}

	private static final Pattern _fileNamePattern = Pattern.compile(
		".*\\.(?!gz)(?<fileExtension>([^\\.]+))(?<gzipFileExtension>\\.gz)?");
	private static Boolean _hasGoogleApplicationCredentials;
	private static final Map<String, ScanCodeCloudBucket>
		_scanCodeCloudBuckets = new HashMap<>();

	private String _cloudBucketURL;
	private final String _name;

}