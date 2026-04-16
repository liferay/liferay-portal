/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser;

import java.io.File;
import java.io.IOException;

import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.concurrent.TimeoutException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Kenji Heigel
 */
public class CloudBucketUtil {

	public static final String GCP_BUCKET_PATH_JENKINS_CI_DATA =
		"gs://jenkins-ci-data";

	public static final String GCP_BUCKET_PATH_LIFERAY_RELEASE_CANDIDATES =
		"gs://liferay-releases-candidates";

	public static final String GCP_BUCKET_PATH_PATCHER_SHARED =
		"gs://patcher-shared";

	public static final String GCP_BUCKET_PATH_TESTRAY_RESULTS =
		"gs://testray-results";

	public static void copyGCPFile(String destination, String source)
		throws IOException {

		_executeCommands(
			_getGCPAuthenticationCommand(destination, source),
			_getFileTransferCommand("gcloud storage cp", destination, source));
	}

	public static void copyS3ToS3(String s3DestinationPath, String s3SourcePath)
		throws IOException, TimeoutException {

		String replacedS3DestinationPath = _replaceS3ObjectPath(
			s3DestinationPath);

		_executeAWSCommands(
			_getFileTransferCommand(
				"aws s3 cp --no-progress", replacedS3DestinationPath,
				s3SourcePath));

		System.out.println(
			JenkinsResultsParserUtil.combine(
				"Copied ", s3SourcePath, " to ", replacedS3DestinationPath));

		if (!s3SourcePath.endsWith(_CHECKSUM_FILE_EXTENSION)) {
			if (!s3DestinationPath.equals(replacedS3DestinationPath)) {
				createS3ObjectRef(replacedS3DestinationPath);
			}

			String s3ChecksumSourcePath =
				s3SourcePath + _CHECKSUM_FILE_EXTENSION;

			if (_exists(s3ChecksumSourcePath)) {
				copyS3ToS3(
					s3DestinationPath + _CHECKSUM_FILE_EXTENSION,
					s3ChecksumSourcePath);
			}
		}
	}

	public static void createS3ObjectRef(String s3ObjectPath) {
		_validateS3ObjectPath(s3ObjectPath);

		createS3ObjectRef(s3ObjectPath, s3ObjectPath);
	}

	public static void createS3ObjectRef(
		String sourceS3ObjectPath, String destinationS3ObjectPath) {

		_validateS3ObjectPath(sourceS3ObjectPath);
		_validateS3ObjectPath(destinationS3ObjectPath);

		File destinationS3ObjectFile = _getS3ObjectRefFile(
			destinationS3ObjectPath);

		try {
			File parentFile = destinationS3ObjectFile.getParentFile();

			parentFile.mkdirs();

			if (destinationS3ObjectFile.exists()) {
				JenkinsResultsParserUtil.delete(destinationS3ObjectFile);
			}

			JenkinsResultsParserUtil.write(
				destinationS3ObjectFile, sourceS3ObjectPath);

			System.out.println(
				JenkinsResultsParserUtil.combine(
					"Created ",
					JenkinsResultsParserUtil.getCanonicalPath(
						destinationS3ObjectFile),
					" with ref to ", sourceS3ObjectPath));
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}
	}

	public static void deleteS3ObjectRefsOlderThan(long ageSeconds)
		throws IOException {

		File s3BucketDir = new File(
			JenkinsResultsParserUtil.getBuildProperty(
				"cloud.ci.s3.bucket.object.refs.dir"));

		Files.walkFileTree(
			s3BucketDir.toPath(),
			new SimpleFileVisitor<Path>() {

				@Override
				public FileVisitResult postVisitDirectory(
					Path path, IOException ioException) {

					File dir = path.toFile();

					if (!dir.exists()) {
						return FileVisitResult.CONTINUE;
					}

					File[] files = dir.listFiles();

					if ((files == null) || (files.length == 0)) {
						if (!_isOlderThan(path, ageSeconds)) {
							return FileVisitResult.CONTINUE;
						}

						JenkinsResultsParserUtil.delete(dir);

						System.out.println("Delete " + path);

						return FileVisitResult.CONTINUE;
					}

					return FileVisitResult.CONTINUE;
				}

				@Override
				public FileVisitResult preVisitDirectory(
					Path path, BasicFileAttributes basicFileAttributes) {

					File dir = path.toFile();

					File[] files = dir.listFiles();

					if ((files == null) || (files.length == 0)) {
						if (!_isOlderThan(basicFileAttributes, ageSeconds)) {
							return FileVisitResult.CONTINUE;
						}

						JenkinsResultsParserUtil.delete(dir);

						System.out.println("Delete " + path);

						return FileVisitResult.CONTINUE;
					}

					return FileVisitResult.CONTINUE;
				}

				@Override
				public FileVisitResult visitFile(
					Path path, BasicFileAttributes basicFileAttributes) {

					if (!_isOlderThan(basicFileAttributes, ageSeconds)) {
						return FileVisitResult.CONTINUE;
					}

					JenkinsResultsParserUtil.delete(path.toFile());

					System.out.println("Delete " + path);

					return FileVisitResult.CONTINUE;
				}

			});
	}

	public static void downloadS3File(File destinationFile, String s3SourcePath)
		throws IOException {

		s3SourcePath = _replaceS3ObjectPath(s3SourcePath);

		if (destinationFile.exists()) {
			destinationFile.delete();
		}

		long start = System.currentTimeMillis();

		_executeAWSCommands(
			_getFileTransferCommand(
				"aws s3 cp --no-progress", destinationFile.getCanonicalPath(),
				s3SourcePath));

		System.out.println(
			JenkinsResultsParserUtil.combine(
				"Downloaded ", destinationFile.getPath(), " with file size ",
				JenkinsResultsParserUtil.toFileSizeString(
					destinationFile.length()),
				" from ", s3SourcePath, " in ",
				JenkinsResultsParserUtil.toDurationString(
					System.currentTimeMillis() - start)));

		String destinationFileName = destinationFile.getName();

		if (!destinationFileName.endsWith(_CHECKSUM_FILE_EXTENSION) &&
			!destinationFileName.equals("build-database.json")) {

			_validateChecksumFile(destinationFile, s3SourcePath);
		}
	}

	public static String getSignedURL(int duration, String file, String url)
		throws IOException, TimeoutException {

		if (JenkinsResultsParserUtil.isNullOrEmpty(file) ||
			JenkinsResultsParserUtil.isNullOrEmpty(url)) {

			return null;
		}

		StringBuilder sb = new StringBuilder();

		sb.append("gcloud storage sign-url ");
		sb.append(url);
		sb.append(" --private-key-file=");
		sb.append(file);
		sb.append(" --duration=");
		sb.append(duration);
		sb.append("m");

		Process process = JenkinsResultsParserUtil.executeBashCommands(
			true, _getGCPAuthenticationCommand(url, url), sb.toString());

		Matcher matcher = _signedURLPattern.matcher(
			JenkinsResultsParserUtil.readInputStream(process.getInputStream()));

		if (matcher.find()) {
			return matcher.group(0);
		}

		return null;
	}

	public static boolean isS3ObjectOlderThan(
		String s3ObjectPath, long maxAgeSeconds) {

		File s3ObjectRefFile = _getS3ObjectRefFile(s3ObjectPath);

		if ((s3ObjectRefFile != null) && s3ObjectRefFile.exists() &&
			_isOlderThan(s3ObjectRefFile, maxAgeSeconds)) {

			return true;
		}

		return false;
	}

	public static boolean isS3ObjectPathAvailable(String s3ObjectPath) {
		if (!_isValidS3ObjectPath(s3ObjectPath)) {
			return false;
		}

		if (isS3ObjectRefAvailable(s3ObjectPath)) {
			return true;
		}

		try {
			String listS3Files = listS3Files(s3ObjectPath, true);

			if (!JenkinsResultsParserUtil.isNullOrEmpty(listS3Files.trim())) {
				return true;
			}
		}
		catch (IOException | TimeoutException exception) {
		}

		return false;
	}

	public static boolean isS3ObjectRefAvailable(String s3ObjectPath) {
		if (!_isValidS3ObjectPath(s3ObjectPath)) {
			System.out.println(
				"WARNING: Invalid s3 object path: " + s3ObjectPath);

			return false;
		}

		File s3ObjectRefFile = _getS3ObjectRefFile(s3ObjectPath);

		return s3ObjectRefFile.exists();
	}

	public static String listGCPFiles(String path, String... args)
		throws IOException, TimeoutException {

		StringBuilder sb = new StringBuilder();

		sb.append("gcloud storage ls ");

		for (String arg : args) {
			sb.append(arg);
			sb.append(" ");
		}

		sb.append(_escapeParentheses(path));

		Process process = JenkinsResultsParserUtil.executeBashCommands(
			true, _getGCPAuthenticationCommand(path, path), sb.toString());

		return JenkinsResultsParserUtil.readInputStream(
			process.getInputStream());
	}

	public static String listS3Files(String path)
		throws IOException, TimeoutException {

		return listS3Files(path, false);
	}

	public static String listS3Files(String path, boolean file)
		throws IOException, TimeoutException {

		if (!path.endsWith("/") && !file) {
			path += "/";
		}

		Process process = JenkinsResultsParserUtil.executeBashCommands(
			true, "aws s3 ls " + _escapeParentheses(path));

		return JenkinsResultsParserUtil.readInputStream(
			process.getInputStream());
	}

	public static String readS3Object(String s3ObjectPath) throws IOException {
		String suffix = ".temp";

		if (s3ObjectPath.endsWith(".gz")) {
			suffix = ".temp.gz";
		}

		File s3TempFile = File.createTempFile("s3-", suffix);

		try {
			downloadS3File(s3TempFile, s3ObjectPath);

			return JenkinsResultsParserUtil.read(s3TempFile);
		}
		finally {
			JenkinsResultsParserUtil.delete(s3TempFile);
		}
	}

	public static void syncGCPFiles(String destination, String source)
		throws IOException {

		_executeCommands(
			_getGCPAuthenticationCommand(destination, source),
			_getFileTransferCommand(
				"gcloud storage rsync --recursive", destination, source));
	}

	public static void syncS3Files(String destination, String source)
		throws IOException, TimeoutException {

		_executeAWSCommands(
			_getFileTransferCommand(
				"aws s3 sync --no-progress", destination, source));

		Matcher destinationS3ObjectPathMatcher = _s3ObjectPathPattern.matcher(
			destination);

		if (destinationS3ObjectPathMatcher.find()) {
			Matcher listS3FilesMatcher = _listS3FilesPattern.matcher(
				listS3Files(destination));

			while (listS3FilesMatcher.find()) {
				String fileName = listS3FilesMatcher.group("fileName");

				if (!fileName.endsWith(_CHECKSUM_FILE_EXTENSION) &&
					_VALIDATE_CHECKSUM) {

					_createChecksumFile(
						destination + "/" + fileName,
						new File(source + "/" + fileName));
				}

				createS3ObjectRef(destination + "/" + fileName);
			}
		}

		Matcher sourceS3ObjectPathMatcher = _s3ObjectPathPattern.matcher(
			source);

		if (sourceS3ObjectPathMatcher.find()) {
			Matcher listS3FilesMatcher = _listS3FilesPattern.matcher(
				listS3Files(source));

			while (listS3FilesMatcher.find()) {
				String fileName = listS3FilesMatcher.group("fileName");

				if (!fileName.endsWith(_CHECKSUM_FILE_EXTENSION) &&
					!fileName.equals("build-database.json")) {

					_validateChecksumFile(
						new File(destination + "/" + fileName),
						source + "/" + fileName);
				}

				createS3ObjectRef(source + "/" + fileName);
			}
		}

		System.out.println("Synced " + source + " to " + destination);
	}

	public static void touchS3File(String s3Path) throws IOException {
		s3Path = _replaceS3ObjectPath(s3Path);

		long start = System.currentTimeMillis();

		_executeAWSCommands(
			_getFileTransferCommand(
				JenkinsResultsParserUtil.combine(
					"aws s3 cp --metadata timestamp=",
					JenkinsResultsParserUtil.getDistinctTimeStamp(),
					" --no-progress"),
				s3Path, s3Path));

		System.out.println(
			JenkinsResultsParserUtil.combine(
				"Touched ", s3Path, " in ",
				JenkinsResultsParserUtil.toDurationString(
					System.currentTimeMillis() - start)));

		if (!s3Path.endsWith(_CHECKSUM_FILE_EXTENSION)) {
			String s3ChecksumPath = s3Path + _CHECKSUM_FILE_EXTENSION;

			try {
				if (_exists(s3ChecksumPath)) {
					touchS3File(s3ChecksumPath);
				}
			}
			catch (TimeoutException timeoutException) {
				throw new IOException(timeoutException);
			}
		}
	}

	public static void uploadS3File(String s3DestinationPath, File sourceFile)
		throws IOException {

		String replacedS3DestinationPath = _replaceS3ObjectPath(
			s3DestinationPath);

		String sourceFileName = sourceFile.getName();

		long start = System.currentTimeMillis();

		_executeAWSCommands(
			_getFileTransferCommand(
				"aws s3 cp --no-progress", replacedS3DestinationPath,
				sourceFile.getCanonicalPath()));

		System.out.println(
			JenkinsResultsParserUtil.combine(
				"Uploaded ", sourceFile.getPath(), " with file size ",
				JenkinsResultsParserUtil.toFileSizeString(sourceFile.length()),
				" to ", replacedS3DestinationPath, " in ",
				JenkinsResultsParserUtil.toDurationString(
					System.currentTimeMillis() - start)));

		if (!s3DestinationPath.equals(replacedS3DestinationPath)) {
			createS3ObjectRef(replacedS3DestinationPath);

			return;
		}

		if (!sourceFileName.endsWith(_CHECKSUM_FILE_EXTENSION) &&
			!sourceFileName.equals("build-database.json") &&
			_VALIDATE_CHECKSUM) {

			_createChecksumFile(replacedS3DestinationPath, sourceFile);
		}
	}

	public static void uploadS3Object(
			String s3ObjectContent, String s3ObjectPath)
		throws IOException {

		File s3TempFile = File.createTempFile("s3-", ".temp");

		File s3TempGzipFile = null;

		if (s3ObjectPath.endsWith(".gz")) {
			s3TempGzipFile = File.createTempFile("s3-", ".temp.gz");
		}

		try {
			JenkinsResultsParserUtil.write(s3TempFile, s3ObjectContent);

			if (s3ObjectPath.endsWith(".gz") && (s3TempGzipFile != null)) {
				JenkinsResultsParserUtil.gzip(s3TempFile, s3TempGzipFile);

				uploadS3File(s3ObjectPath, s3TempGzipFile);

				return;
			}

			uploadS3File(s3ObjectPath, s3TempFile);
		}
		finally {
			JenkinsResultsParserUtil.delete(s3TempFile);

			if (s3TempGzipFile != null) {
				JenkinsResultsParserUtil.delete(s3TempGzipFile);
			}
		}
	}

	private static void _createChecksumFile(
			String s3DestinationPath, File sourceFile)
		throws IOException {

		if (!sourceFile.exists()) {
			return;
		}

		File sourceChecksumFile = new File(
			sourceFile.getParentFile(),
			sourceFile.getName() + _CHECKSUM_FILE_EXTENSION);

		JenkinsResultsParserUtil.writeSHAFile(sourceFile, sourceChecksumFile);

		uploadS3File(
			s3DestinationPath + _CHECKSUM_FILE_EXTENSION, sourceChecksumFile);

		sourceChecksumFile.delete();
	}

	private static String _escapeParentheses(String s) {
		s = s.replace("(", "\\(");
		s = s.replace(")", "\\)");

		return s;
	}

	private static void _executeAWSCommands(String... commands) {
		List<String> awsCommands = new ArrayList<>();

		Retryable retryable = new Retryable(3, 30, true) {

			@Override
			public Object execute() {
				String[] awsCommands = _getAWSCommands(commands);

				try {
					_executeCommands(awsCommands);
				}
				catch (Exception exception) {
					for (String awsCommand : awsCommands) {
						if (awsCommand.contains(_CHECKSUM_FILE_EXTENSION)) {
							return null;
						}
					}

					_firstExecution = false;

					throw exception;
				}

				return null;
			}

			private String[] _getAWSCommands(String[] commands) {
				awsCommands.clear();

				for (String command : commands) {
					Matcher awsCommandMatcher = _awsCommandPattern.matcher(
						command);

					if (!awsCommandMatcher.find()) {
						awsCommands.add(command);
					}

					StringBuilder sb = new StringBuilder();

					sb.append("aws s3 ");
					sb.append(awsCommandMatcher.group("command"));
					sb.append(" ");

					if (!_firstExecution) {
						sb.append("--debug ");
					}

					sb.append(awsCommandMatcher.group("options"));

					if (!_firstExecution) {
						sb.append(" 2> ");

						File awsLogDir = new File(
							JenkinsResultsParserUtil.getBuildDirPath(), "aws");

						awsLogDir.mkdirs();

						String awsLogDirPath =
							JenkinsResultsParserUtil.getCanonicalPath(
								awsLogDir);

						awsLogDirPath = awsLogDirPath.replaceAll(
							"\\(", "\\\\(");
						awsLogDirPath = awsLogDirPath.replaceAll(
							"\\)", "\\\\)");

						sb.append(awsLogDirPath);

						sb.append("/aws-");
						sb.append(
							JenkinsResultsParserUtil.getDistinctTimeStamp());
						sb.append(".log");
					}

					awsCommands.add(sb.toString());
				}

				return awsCommands.toArray(new String[0]);
			}

			private boolean _firstExecution = true;

		};

		try {
			retryable.executeWithRetries();
		}
		catch (Exception exception) {
			NotificationUtil.sendSlackNotification(
				JenkinsResultsParserUtil.combine(
					"Build URL: ", System.getenv("BUILD_URL"), "\n\n",
					exception.getMessage()),
				"ci-aws-notifications", ":aws:",
				JenkinsResultsParserUtil.combine(
					"Failed to run commands: ",
					JenkinsResultsParserUtil.join(" ; ", awsCommands)),
				"AWS CI Commands");
		}
	}

	private static void _executeCommands(String... commands) {
		try {
			Process process = JenkinsResultsParserUtil.executeBashCommands(
				1000 * 60 * 10, commands);

			System.out.println(
				JenkinsResultsParserUtil.readInputStream(
					process.getInputStream()));

			if (process.exitValue() != 0) {
				System.out.println(
					JenkinsResultsParserUtil.readInputStream(
						process.getErrorStream()));

				throw new RuntimeException("Unable to sync directories");
			}
		}
		catch (IOException | TimeoutException exception) {
			System.out.println("Unable to sync directories");

			throw new RuntimeException(exception);
		}
	}

	private static boolean _exists(String s3FilePath)
		throws IOException, TimeoutException {

		return !JenkinsResultsParserUtil.isNullOrEmpty(
			listS3Files(s3FilePath, true));
	}

	private static String _getFileTransferCommand(
		String command, String destination, String source) {

		StringBuilder sb = new StringBuilder();

		sb.append(command);
		sb.append(" ");
		sb.append(_escapeParentheses(source));
		sb.append(" ");
		sb.append(_escapeParentheses(destination));

		return sb.toString();
	}

	private static String _getGCPAuthenticationCommand(
			String destination, String source)
		throws IOException {

		StringBuilder sb = new StringBuilder();

		sb.append("gcloud auth activate-service-account --key-file ");

		String gcpApplicationCredentialFilePath = null;

		if (destination.startsWith(GCP_BUCKET_PATH_JENKINS_CI_DATA) ||
			destination.startsWith(
				GCP_BUCKET_PATH_LIFERAY_RELEASE_CANDIDATES) ||
			source.startsWith(GCP_BUCKET_PATH_JENKINS_CI_DATA) ||
			source.startsWith(GCP_BUCKET_PATH_LIFERAY_RELEASE_CANDIDATES)) {

			gcpApplicationCredentialFilePath = _buildProperties.getProperty(
				"google.application.crendential.file[jenkins]");
		}
		else if (destination.startsWith(GCP_BUCKET_PATH_PATCHER_SHARED) ||
				 source.startsWith(GCP_BUCKET_PATH_PATCHER_SHARED)) {

			gcpApplicationCredentialFilePath = _buildProperties.getProperty(
				"google.application.crendential.file[patcher]");
		}
		else if (destination.startsWith(GCP_BUCKET_PATH_TESTRAY_RESULTS) ||
				 source.startsWith(GCP_BUCKET_PATH_TESTRAY_RESULTS)) {

			gcpApplicationCredentialFilePath = _buildProperties.getProperty(
				"google.application.crendential.file[testray]");
		}

		if (gcpApplicationCredentialFilePath != null) {
			File gcpApplicationCredentialFile = new File(
				gcpApplicationCredentialFilePath);

			if (gcpApplicationCredentialFile.exists()) {
				sb.append(gcpApplicationCredentialFilePath);

				return sb.toString();
			}
		}

		throw new IOException("Unable to find GCP application credential file");
	}

	private static File _getS3ObjectRefFile(String s3ObjectPath) {
		Matcher s3ObjectPathMatcher = _s3ObjectPathPattern.matcher(
			s3ObjectPath);

		if (!s3ObjectPathMatcher.find()) {
			throw new RuntimeException(
				"Invalid S3 object path: " + s3ObjectPath);
		}

		StringBuilder sb = new StringBuilder();

		try {
			sb.append(
				JenkinsResultsParserUtil.getBuildProperty(
					"cloud.ci.s3.bucket.object.refs.dir"));
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}

		sb.append("/");
		sb.append(s3ObjectPathMatcher.group("objectPath"));
		sb.append(".s3.ref");

		return new File(sb.toString());
	}

	private static boolean _isOlderThan(
		BasicFileAttributes basicFileAttributes, long ageSeconds) {

		FileTime lastModifiedFileTime = basicFileAttributes.lastModifiedTime();

		Instant lastModifiedInstant = lastModifiedFileTime.toInstant();

		Instant instant = Instant.now();

		return lastModifiedInstant.isBefore(
			instant.minus(ageSeconds, ChronoUnit.SECONDS));
	}

	private static boolean _isOlderThan(File file, long ageSeconds) {
		if ((file == null) || !file.exists()) {
			return false;
		}

		return _isOlderThan(file.toPath(), ageSeconds);
	}

	private static boolean _isOlderThan(Path path, long ageSeconds) {
		if (path == null) {
			return false;
		}

		try {
			if (_isOlderThan(
					Files.readAttributes(path, BasicFileAttributes.class),
					ageSeconds)) {

				return true;
			}
		}
		catch (IOException ioException) {
		}

		return false;
	}

	private static boolean _isValidS3ObjectPath(String s3ObjectPath) {
		if (s3ObjectPath == null) {
			return false;
		}

		Matcher s3ObjectPathMatcher = _s3ObjectPathPattern.matcher(
			s3ObjectPath);

		return s3ObjectPathMatcher.find();
	}

	private static String _replaceS3ObjectPath(String s3ObjectPath) {
		Matcher s3ObjectPathMatcher = _s3ObjectPathPattern.matcher(
			s3ObjectPath);

		if (s3ObjectPathMatcher.find()) {
			File s3ObjectRefFile = _getS3ObjectRefFile(s3ObjectPath);

			if (s3ObjectRefFile.exists()) {
				Retryable<String> retryable = new Retryable<String>(
					true, 5, 30, true) {

					@Override
					public String execute() {
						try {
							String s3ObjectRefFileContent =
								JenkinsResultsParserUtil.read(s3ObjectRefFile);

							if (Objects.equals(
									s3ObjectRefFileContent, s3ObjectPath)) {

								return s3ObjectRefFileContent;
							}

							return _replaceS3ObjectPath(s3ObjectRefFileContent);
						}
						catch (IOException ioException) {
							System.out.println(
								"Unable to read " + s3ObjectRefFile);

							throw new RuntimeException(ioException);
						}
					}

				};

				return retryable.executeWithRetries();
			}
		}

		return s3ObjectPath.trim();
	}

	private static void _validateChecksumFile(
			File destinationFile, String s3SourcePath)
		throws IOException {

		if (!_VALIDATE_CHECKSUM) {
			return;
		}

		File destinationChecksumFile = new File(
			destinationFile.getParentFile(),
			destinationFile.getName() + _CHECKSUM_FILE_EXTENSION);

		if (!destinationChecksumFile.exists()) {
			try {
				downloadS3File(
					destinationChecksumFile,
					s3SourcePath + _CHECKSUM_FILE_EXTENSION);
			}
			catch (RuntimeException runtimeException) {
				System.out.println(
					"Unable to download " + s3SourcePath +
						_CHECKSUM_FILE_EXTENSION);

				return;
			}
		}

		if (destinationChecksumFile.exists()) {
			try {
				if (!JenkinsResultsParserUtil.isMatchingSHAFile(
						destinationFile, destinationChecksumFile)) {

					destinationFile.delete();

					throw new IOException(
						destinationFile.getName() +
							" content failed checksum validation");
				}
			}
			finally {
				destinationChecksumFile.delete();
			}
		}
	}

	private static void _validateS3ObjectPath(String s3ObjectPath) {
		if (!_isValidS3ObjectPath(s3ObjectPath)) {
			throw new RuntimeException(
				"Invalid S3 object path: " + s3ObjectPath);
		}
	}

	private static final String _CHECKSUM_FILE_EXTENSION = ".sha512";

	private static final boolean _VALIDATE_CHECKSUM;

	private static final Pattern _awsCommandPattern = Pattern.compile(
		"aws s3 (?<command>[^\\s]+)\\s+(?<options>.+)");
	private static final Properties _buildProperties;
	private static final Pattern _listS3FilesPattern = Pattern.compile(
		"\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2} +\\d+ (?<fileName>.+)");
	private static final Pattern _s3ObjectPathPattern = Pattern.compile(
		"s3://(?<bucketName>[^/]+)/(?<objectPath>.+)");
	private static final Pattern _signedURLPattern = Pattern.compile(
		"https:\\/\\/([a-zA-Z\\d-]+\\.)?storage\\." +
			"(cloud\\.google\\.com|googleapis\\.com)\\/.*");

	static {
		_buildProperties = new Properties() {
			{
				try {
					putAll(JenkinsResultsParserUtil.getBuildProperties());
				}
				catch (IOException ioException) {
					throw new RuntimeException(ioException);
				}
			}
		};

		_VALIDATE_CHECKSUM = Boolean.parseBoolean(
			_buildProperties.getProperty(
				"cloud.ci.s3.bucket.validate.checksum.enabled"));
	}

}