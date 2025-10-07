/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser;

import java.io.File;
import java.io.IOException;

import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author Michael Hashimoto
 */
public class BuildDatabaseUtil {

	public static void clear() {
		File buildDir = _getBuildDir(null);

		File buildDatabaseFile = new File(
			buildDir, BuildDatabase.FILE_NAME_BUILD_DATABASE_JSON);

		if (buildDatabaseFile.exists()) {
			buildDatabaseFile.delete();
		}
	}

	public static void downloadBuildDatabase(String buildURL) {
		String buildDirPath = JenkinsResultsParserUtil.getBuildDirPath(
			buildURL);

		if (buildDirPath == null) {
			return;
		}

		File buildDir = new File(buildDirPath);

		File buildDatabaseFile = new File(
			buildDir, BuildDatabase.FILE_NAME_BUILD_DATABASE_JSON);

		buildDatabaseFile.delete();

		try {
			System.out.println(
				"Downloading " + buildURL + " to " + buildDatabaseFile);

			JenkinsResultsParserUtil.write(
				buildDatabaseFile,
				JenkinsResultsParserUtil.toString(
					JenkinsResultsParserUtil.getBuildArtifactURL(
						buildURL,
						BuildDatabase.FILE_NAME_BUILD_DATABASE_JSON)));
		}
		catch (IOException ioException) {
			throw new RuntimeException(
				"Unable to write build-database.json", ioException);
		}
	}

	public static BuildDatabase getBuildDatabase() {
		return getBuildDatabase(null);
	}

	public static BuildDatabase getBuildDatabase(Build build) {
		TopLevelBuild topLevelBuild = null;

		if (build != null) {
			topLevelBuild = build.getTopLevelBuild();
		}

		if ((build instanceof TopLevelBuild) || (topLevelBuild == null)) {
			File buildDir = _getBuildDir(build);

			if (topLevelBuild instanceof JenkinsTopLevelBuild) {
				buildDir = _getBuildDir(topLevelBuild);
			}

			synchronized (_buildDatabases) {
				BuildDatabase buildDatabase = _buildDatabases.get(buildDir);

				if (buildDatabase != null) {
					return buildDatabase;
				}

				_downloadBuildDatabaseFile(buildDir, build);

				buildDatabase = new DefaultBuildDatabase(buildDir);

				_buildDatabases.put(buildDir, buildDatabase);

				return buildDatabase;
			}
		}

		return getBuildDatabase(topLevelBuild);
	}

	private static void _downloadBuildDatabaseFile(File buildDir, Build build) {
		if (!buildDir.exists()) {
			buildDir.mkdirs();
		}

		File buildDatabaseFile = new File(
			buildDir, BuildDatabase.FILE_NAME_BUILD_DATABASE_JSON);

		if (buildDatabaseFile.exists()) {
			return;
		}

		if (JenkinsResultsParserUtil.isCloudCINode()) {
			String s3BucketDistPath = System.getenv("S3_BUCKET_DIST_PATH");

			if (!JenkinsResultsParserUtil.isNullOrEmpty(s3BucketDistPath)) {
				_downloadBuildDatabaseFileFromS3Bucket(
					buildDatabaseFile, System.getenv("S3_BUCKET_DIST_PATH"));
			}
		}
		else {
			String distNodes = System.getenv("DIST_NODES");
			String distPath = System.getenv("DIST_PATH");

			if (!JenkinsResultsParserUtil.isNullOrEmpty(distNodes) &&
				!JenkinsResultsParserUtil.isNullOrEmpty(distPath)) {

				_downloadBuildDatabaseFileFromDistNodes(
					buildDatabaseFile, distNodes, distPath);
			}
		}

		if (buildDatabaseFile.exists() || (build == null)) {
			return;
		}

		if (build.isFromArchive()) {
			try {
				JenkinsResultsParserUtil.write(
					buildDatabaseFile,
					JenkinsResultsParserUtil.toString(
						build.getBuildURL() + "/build-database.json"));
			}
			catch (IOException ioException) {
				throw new RuntimeException(
					"Unable to write build-database.json", ioException);
			}

			return;
		}

		if (build instanceof TopLevelBuild) {
			_downloadBuildDatabaseFileFromTopLevelBuild(
				buildDatabaseFile, (TopLevelBuild)build);

			if (!JenkinsResultsParserUtil.isCINode()) {
				File defaultBuildDir = new File(
					JenkinsResultsParserUtil.getBuildDirPath());

				if (!defaultBuildDir.exists()) {
					defaultBuildDir.mkdirs();
				}

				File defaultBuildDatabaseFile = new File(
					defaultBuildDir,
					BuildDatabase.FILE_NAME_BUILD_DATABASE_JSON);

				try {
					Files.copy(
						buildDatabaseFile.toPath(),
						defaultBuildDatabaseFile.toPath(),
						StandardCopyOption.REPLACE_EXISTING);
				}
				catch (IOException ioException) {
					throw new RuntimeException(ioException);
				}
			}
		}
	}

	private static void _downloadBuildDatabaseFileFromDistNodes(
		File buildDatabaseFile, String distNodes, String distPath) {

		if (buildDatabaseFile.exists()) {
			return;
		}

		List<String> distNodesList = new ArrayList<>(
			Arrays.asList(distNodes.split(",")));

		while (!distNodesList.isEmpty()) {
			try {
				String distNode = _getRandomDistNode(distNodesList);

				distNodesList.remove(distNode);

				String[] commands = new String[2];

				commands[0] = JenkinsResultsParserUtil.combine(
					"mkdir -p ",
					JenkinsResultsParserUtil.escapeForBash(
						JenkinsResultsParserUtil.getCanonicalPath(
							buildDatabaseFile.getParentFile())));

				if (JenkinsResultsParserUtil.isOSX()) {
					commands[1] = JenkinsResultsParserUtil.combine(
						"timeout 1200 rsync -Iq \"root@", distNode, ":",
						JenkinsResultsParserUtil.escapeForBash(distPath), "/",
						BuildDatabase.FILE_NAME_BUILD_DATABASE_JSON, "\" ",
						JenkinsResultsParserUtil.escapeForBash(
							JenkinsResultsParserUtil.getCanonicalPath(
								buildDatabaseFile)));
				}
				else if (JenkinsResultsParserUtil.isWindows()) {
					commands[0] = JenkinsResultsParserUtil.combine(
						"mkdir -p ",
						JenkinsResultsParserUtil.getCanonicalPath(
							buildDatabaseFile.getParentFile()));

					distPath = distPath.replaceAll(
						"C:.*TEMP/dist", "/tmp/dist");

					File bashFile = new File(
						"C:/tmp/jenkins/" +
							JenkinsResultsParserUtil.getCurrentTimeMillis() +
								".sh");

					JenkinsResultsParserUtil.write(
						bashFile,
						JenkinsResultsParserUtil.combine(
							"#!/bin/sh\nscp \"", distNode, ":",
							JenkinsResultsParserUtil.escapeForBash(distPath),
							"/", BuildDatabase.FILE_NAME_BUILD_DATABASE_JSON,
							"\" ",
							JenkinsResultsParserUtil.escapeForBash(
								JenkinsResultsParserUtil.getCanonicalPath(
									buildDatabaseFile))));

					commands[1] =
						"/bin/sh " +
							JenkinsResultsParserUtil.getCanonicalPath(bashFile);
				}
				else {
					commands[1] = JenkinsResultsParserUtil.combine(
						"if ! ", "timeout 1200 rsync -Iq \"", distNode, ":",
						JenkinsResultsParserUtil.escapeForBash(distPath), "/",
						BuildDatabase.FILE_NAME_BUILD_DATABASE_JSON, "\" ",
						JenkinsResultsParserUtil.escapeForBash(
							JenkinsResultsParserUtil.getCanonicalPath(
								buildDatabaseFile)),
						"; then ", "timeout 1200 rsync -Iq ", distNode, ":",
						JenkinsResultsParserUtil.escapeForBash(distPath), "/",
						BuildDatabase.FILE_NAME_BUILD_DATABASE_JSON, " ",
						JenkinsResultsParserUtil.escapeForBash(
							JenkinsResultsParserUtil.getCanonicalPath(
								buildDatabaseFile)),
						"; fi");
				}

				Process process = JenkinsResultsParserUtil.executeBashCommands(
					true, new File("."), 10 * 60 * 1000, commands);

				if (process.exitValue() != 0) {
					String errorText = JenkinsResultsParserUtil.readInputStream(
						process.getErrorStream());

					throw new RuntimeException(
						JenkinsResultsParserUtil.combine(
							"Unable to download ",
							BuildDatabase.FILE_NAME_BUILD_DATABASE_JSON, "\n\n",
							errorText));
				}

				if (!buildDatabaseFile.exists()) {
					System.out.println(
						JenkinsResultsParserUtil.combine(
							"Unable to get ",
							BuildDatabase.FILE_NAME_BUILD_DATABASE_JSON,
							" from ", distNode, ", retrying..."));

					continue;
				}

				if (!_isValidBuildDatabaseFile(buildDatabaseFile)) {
					JenkinsResultsParserUtil.delete(buildDatabaseFile);

					System.out.println(
						JenkinsResultsParserUtil.combine(
							"Invalid ",
							JenkinsResultsParserUtil.getCanonicalPath(
								buildDatabaseFile),
							" from ", distNode, ", retrying..."));

					continue;
				}

				System.out.println(
					JenkinsResultsParserUtil.combine(
						"Downloaded ",
						JenkinsResultsParserUtil.getCanonicalPath(
							buildDatabaseFile),
						" from ", distNode));

				break;
			}
			catch (IOException | RuntimeException | TimeoutException
						exception) {

				if (distNodesList.isEmpty()) {
					throw new RuntimeException(
						JenkinsResultsParserUtil.combine(
							"Unable to get ",
							BuildDatabase.FILE_NAME_BUILD_DATABASE_JSON,
							" file"),
						exception);
				}

				System.out.println(
					"Unable to execute bash commands, retrying...");

				exception.printStackTrace();

				JenkinsResultsParserUtil.sleep(3000);
			}
		}
	}

	private static void _downloadBuildDatabaseFileFromS3Bucket(
		File buildDatabaseFile, String path) {

		if (buildDatabaseFile.exists()) {
			return;
		}

		File parentDir = buildDatabaseFile.getParentFile();

		parentDir.mkdirs();

		String buildDatabaseFilePath =
			JenkinsResultsParserUtil.getCanonicalPath(buildDatabaseFile);

		Retryable<Object> retryable = new Retryable<Object>(true, 3, 5, true) {

			@Override
			public Object execute() {
				try {
					_deleteBuildDatabaseFiles();

					_downloadBuildDatabaseFiles();

					if (!_isValidBuildDatabaseFile(buildDatabaseFile)) {
						_deleteBuildDatabaseFiles();

						throw new RuntimeException(
							JenkinsResultsParserUtil.combine(
								"Invalid ", buildDatabaseFilePath, " from ",
								path));
					}

					System.out.println(
						JenkinsResultsParserUtil.combine(
							"Downloaded ", path, " to ",
							buildDatabaseFilePath));
				}
				catch (Exception exception) {
					if (JenkinsResultsParserUtil.isCloudCINode()) {
						exception.printStackTrace();
					}

					throw new RuntimeException(
						JenkinsResultsParserUtil.combine(
							"Unable to get ",
							BuildDatabase.FILE_NAME_BUILD_DATABASE_JSON,
							" file from ", path),
						exception);
				}

				return null;
			}

			@Override
			protected String getRetryMessage(int retryCount) {
				return JenkinsResultsParserUtil.combine(
					"Unable to download ", path, " to ",
					JenkinsResultsParserUtil.getCanonicalPath(
						buildDatabaseFile),
					": ", super.getRetryMessage(retryCount));
			}

			private void _deleteBuildDatabaseFiles() {
				if (buildDatabaseFile.exists()) {
					JenkinsResultsParserUtil.delete(buildDatabaseFile);
				}
			}

			private void _downloadBuildDatabaseFiles() {
				try {
					CloudBucketUtil.downloadS3File(
						buildDatabaseFile,
						path + "/" +
							BuildDatabase.FILE_NAME_BUILD_DATABASE_JSON);
				}
				catch (IOException ioException) {
					throw new RuntimeException(
						"Unable to download build database files", ioException);
				}
			}

		};

		try {
			retryable.executeWithRetries();
		}
		catch (Exception exception) {
			throw new RuntimeException(exception);
		}
	}

	private static void _downloadBuildDatabaseFileFromTopLevelBuild(
		File buildDatabaseFile, TopLevelBuild topLevelBuild) {

		String buildDatabaseURL = JenkinsResultsParserUtil.getLocalURL(
			JenkinsResultsParserUtil.getBuildArtifactURL(
				topLevelBuild.getBuildURL(), buildDatabaseFile.getName()));

		if (!JenkinsResultsParserUtil.isCINode()) {
			try {
				JenkinsResultsParserUtil.write(
					buildDatabaseFile,
					JenkinsResultsParserUtil.toString(buildDatabaseURL));
			}
			catch (IOException ioException) {
				ioException.printStackTrace();
			}

			return;
		}

		if (buildDatabaseFile.exists()) {
			return;
		}

		String buildDatabaseFilePath = buildDatabaseURL.replaceAll(
			".*/(userContent/.*)", "/opt/java/jenkins/$1");

		buildDatabaseFilePath = buildDatabaseFilePath.replace("%28", "(");
		buildDatabaseFilePath = buildDatabaseFilePath.replace("%29", ")");

		JenkinsMaster jenkinsMaster = topLevelBuild.getJenkinsMaster();

		try {
			Process process = JenkinsResultsParserUtil.executeBashCommands(
				JenkinsResultsParserUtil.combine(
					"ssh ", jenkinsMaster.getName(), " ls \"",
					JenkinsResultsParserUtil.escapeForBash(
						buildDatabaseFilePath),
					"\""));

			if (process.exitValue() != 0) {
				return;
			}
		}
		catch (IOException | TimeoutException exception) {
			return;
		}

		try {
			_downloadBuildDatabaseFileFromDistNodes(
				buildDatabaseFile, jenkinsMaster.getName(),
				buildDatabaseFilePath.replaceAll("(.*)/[^/]+", "$1"));

			return;
		}
		catch (Exception exception) {
			exception.printStackTrace();
		}

		try {
			JenkinsResultsParserUtil.write(
				buildDatabaseFile,
				JenkinsResultsParserUtil.toString(buildDatabaseURL));
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}
	}

	private static File _getBuildDir(Build build) {
		if (build != null) {
			return new File(build.getBuildDirPath());
		}

		String buildDir = System.getenv("BUILD_DIR");

		if (!JenkinsResultsParserUtil.isNullOrEmpty(buildDir)) {
			return new File(buildDir);
		}

		return new File(JenkinsResultsParserUtil.getBuildDirPath());
	}

	private static String _getCurrentNetworkName() {
		String masterHostname = System.getenv("MASTER_HOSTNAME");

		JenkinsMaster jenkinsMaster = JenkinsMaster.getInstance(masterHostname);

		return jenkinsMaster.getNetworkName();
	}

	private static String _getRandomDistNode(List<String> distNodes) {
		if (distNodes.isEmpty()) {
			return null;
		}

		String currentNetworkName = _getCurrentNetworkName();

		List<String> currentNetworkDistNodes = new ArrayList<>();
		List<String> externalNetworkDistNodes = new ArrayList<>();

		for (String distNode : distNodes) {
			if (JenkinsResultsParserUtil.isJenkinsSlaveInNetwork(
					distNode, currentNetworkName)) {

				currentNetworkDistNodes.add(distNode);

				continue;
			}

			externalNetworkDistNodes.add(distNode);
		}

		if (!currentNetworkDistNodes.isEmpty()) {
			return JenkinsResultsParserUtil.getRandomString(
				currentNetworkDistNodes);
		}

		return JenkinsResultsParserUtil.getRandomString(
			externalNetworkDistNodes);
	}

	private static boolean _isValidBuildDatabaseFile(File buildDatabaseFile) {
		String buildDatabaseFileContent;

		try {
			buildDatabaseFileContent = JenkinsResultsParserUtil.read(
				buildDatabaseFile);
		}
		catch (IOException ioException) {
			return false;
		}

		if (JenkinsResultsParserUtil.isNullOrEmpty(buildDatabaseFileContent)) {
			return false;
		}

		JSONObject buildDatabaseJSONObject;

		try {
			buildDatabaseJSONObject = new JSONObject(buildDatabaseFileContent);
		}
		catch (JSONException jsonException) {
			return false;
		}

		String jobVariant = System.getenv("JOB_VARIANT");

		if (JenkinsResultsParserUtil.isNullOrEmpty(jobVariant)) {
			return true;
		}

		JSONObject propertiesJSONObject = buildDatabaseJSONObject.optJSONObject(
			"properties");

		if ((propertiesJSONObject == null) ||
			!propertiesJSONObject.has(jobVariant + "/start.properties")) {

			return false;
		}

		return true;
	}

	private static final Map<File, BuildDatabase> _buildDatabases =
		new HashMap<>();

}