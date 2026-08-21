/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.ant.mirrors.get;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;

import java.nio.charset.StandardCharsets;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;

import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipFile;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.taskdefs.Checksum;
import org.apache.tools.tar.TarEntry;
import org.apache.tools.tar.TarInputStream;

/**
 * @author Peter Yoo
 */
public class MirrorsGetTask extends Task {

	@Override
	public void execute() throws BuildException {
		try {
			_execute();
		}
		catch (IOException ioException) {
			throw new BuildException(ioException);
		}
	}

	public void setDest(File dest) {
		String destPath = dest.getPath();

		if (destPath.matches(".*\\$\\{.+\\}.*")) {
			Project project = getProject();

			_dest = new File(project.replaceProperties(destPath));
		}
		else {
			_dest = dest;
		}
	}

	public void setForce(boolean force) {
		_force = force;
	}

	public void setIgnoreErrors(boolean ignoreErrors) {
		_ignoreErrors = ignoreErrors;
	}

	public void setPassword(String password) {
		if (_password == null) {
			_password = password;
		}
	}

	public void setRetries(int retries) {
		_retries = retries;
	}

	public void setSkipChecksum(boolean skipChecksum) {
		_skipChecksum = skipChecksum;
	}

	public void setSrc(String src) {
		Matcher matcher = _basicAuthenticationURLPattern.matcher(src);

		try {
			src = URLDecoder.decode(src, StandardCharsets.UTF_8.name());
		}
		catch (UnsupportedEncodingException unsupportedEncodingException) {
			unsupportedEncodingException.printStackTrace();
		}

		if (matcher.matches()) {
			_username = matcher.group(2);
			_password = matcher.group(3);

			src = matcher.group(1) + matcher.group(4);
		}

		Project project = getProject();

		_src = project.replaceProperties(src);

		if (_src.startsWith("file:")) {
			return;
		}

		matcher = _gsURLPattern.matcher(_src);

		if (matcher.matches()) {
			_fileName = matcher.group("fileName");

			_gcpBucketName = matcher.group("bucketName");

			Map<String, Object> properties = project.getProperties();

			for (String propertyName : properties.keySet()) {
				Matcher bucketHostNamePropertyMatcher =
					_gcpBucketHostNamePropertyPattern.matcher(propertyName);

				if (!bucketHostNamePropertyMatcher.matches() ||
					!Objects.equals(
						_gcpBucketName,
						bucketHostNamePropertyMatcher.group("bucketName"))) {

					continue;
				}

				_hostName = project.getProperty(propertyName);

				break;
			}

			if (_hostName == null) {
				_hostName = "storage.googleapis.com";
			}

			_path = matcher.group("path");

			return;
		}

		matcher = _httpURLPattern.matcher(_src);

		if (!matcher.find()) {
			throw new RuntimeException("Invalid src attribute: " + _src);
		}

		_fileName = matcher.group("fileName");

		_hostName = matcher.group("hostName");

		Matcher releaseHostNameMatcher = _releaseHostNamePattern.matcher(
			_hostName);
		Matcher testHostNameMatcher = _testHostNamePattern.matcher(_hostName);

		if (releaseHostNameMatcher.matches()) {
			_hostName =
				"release.liferay.com/" + releaseHostNameMatcher.group("id");
		}
		else if (testHostNameMatcher.matches()) {
			_hostName += ".liferay.com";
		}

		_path = _normalizePath(matcher.group("path"));

		if (Objects.equals(_hostName, "storage.googleapis.com")) {
			int index = _path.indexOf("/");

			if (index != -1) {
				_gcpBucketName = _path.substring(0, index);

				_path = _path.substring(index + 1);
			}
			else {
				_gcpBucketName = _path;

				_path = "";
			}
		}
	}

	public void setSSL(boolean ssl) {
		_ssl = ssl;
	}

	public void setTryLocalNetwork(boolean tryLocalNetwork) {
		_tryLocalNetwork = tryLocalNetwork;
	}

	public void setUsername(String username) {
		if (_username == null) {
			_username = username;
		}
	}

	public void setVerbose(boolean verbose) {
		_verbose = verbose;
	}

	private boolean _addToCache(File cacheFile, File tempFile)
		throws IOException {

		try {
			Files.createLink(cacheFile.toPath(), tempFile.toPath());

			return true;
		}
		catch (FileAlreadyExistsException fileAlreadyExistsException) {
			return false;
		}
		catch (IOException | UnsupportedOperationException exception) {
			if (cacheFile.exists()) {
				return false;
			}

			return _renameFile(cacheFile, tempFile);
		}
	}

	private void _copyFile(File sourceFile, File targetFile)
		throws IOException {

		StringBuilder sb = new StringBuilder();

		sb.append("Copying ");
		sb.append(sourceFile.getPath());
		sb.append(" to ");
		sb.append(targetFile.getPath());
		sb.append(".");

		System.out.println(sb.toString());

		URI sourceFileURI = sourceFile.toURI();

		long time = System.currentTimeMillis();

		int size = _toFile(sourceFileURI.toURL(), targetFile);

		if (_verbose) {
			sb = new StringBuilder();

			sb.append("Copied ");
			sb.append(size);
			sb.append(" bytes in ");
			sb.append(System.currentTimeMillis() - time);
			sb.append(" milliseconds.");

			System.out.println(sb.toString());
		}
	}

	private File _createReadLink(File cacheFile, File linkFile) {
		try {
			Files.createLink(linkFile.toPath(), cacheFile.toPath());

			return linkFile;
		}
		catch (NoSuchFileException noSuchFileException) {
			return null;
		}
		catch (IOException | UnsupportedOperationException exception) {
			if (cacheFile.exists()) {
				return cacheFile;
			}

			return null;
		}
	}

	private void _deleteExpiredTempFiles(File cacheFile) {
		File parentFile = cacheFile.getParentFile();

		if (parentFile == null) {
			return;
		}

		File[] files = parentFile.listFiles();

		if (files == null) {
			return;
		}

		Pattern pattern = _getOrphanPattern(cacheFile.getName());
		long thresholdTime = System.currentTimeMillis() - _MAX_AGE_MILLIS;

		for (File file : files) {
			Matcher matcher = pattern.matcher(file.getName());

			if (!matcher.matches()) {
				continue;
			}

			long time = Long.parseLong(matcher.group("timestamp"));

			if (time > thresholdTime) {
				continue;
			}

			file.delete();
		}
	}

	private void _deleteFile(File file) {
		if (!file.exists()) {
			return;
		}

		if (!file.isDirectory()) {
			file.delete();

			return;
		}

		for (File childFile : file.listFiles()) {
			_deleteFile(childFile);
		}

		file.delete();
	}

	private void _downloadFile(URL sourceURL, File targetFile)
		throws IOException {

		StringBuilder sb = new StringBuilder();

		sb.append("Downloading ");
		sb.append(sourceURL.toExternalForm());
		sb.append(" to ");
		sb.append(targetFile.getPath());
		sb.append(".");

		System.out.println(sb.toString());

		long time = System.currentTimeMillis();

		int size = 0;

		try {
			size = _toFile(sourceURL, targetFile);
		}
		catch (IOException ioException) {
			_deleteFile(targetFile);

			if (!_ignoreErrors) {
				throw ioException;
			}
		}

		if (_verbose) {
			sb = new StringBuilder();

			sb.append("Downloaded ");
			sb.append(sourceURL.toExternalForm());
			sb.append(". ");
			sb.append(size);
			sb.append(" bytes in ");
			sb.append(System.currentTimeMillis() - time);
			sb.append(" milliseconds.");

			System.out.println(sb.toString());
		}

		if (!_isValidMD5(
				targetFile, new URL(sourceURL.toExternalForm() + ".md5"))) {

			_deleteFile(targetFile);

			throw new IOException(
				targetFile.getAbsolutePath() + " failed checksum");
		}

		if (_isTarGzFileName(targetFile.getName()) &&
			!_isTarGzFile(targetFile)) {

			_deleteFile(targetFile);

			throw new IOException(
				targetFile.getAbsolutePath() + " is an invalid TAR GZ file");
		}

		if (_isZipFileName(targetFile.getName()) && !_isZipFile(targetFile)) {
			_deleteFile(targetFile);

			throw new IOException(
				targetFile.getAbsolutePath() + " is an invalid ZIP file");
		}

		if (_is7zFileName(targetFile.getName()) && !_is7zFile(targetFile)) {
			_deleteFile(targetFile);

			throw new IOException(
				targetFile.getAbsolutePath() + " is an invalid 7z file");
		}
	}

	private void _downloadFile(URL sourceURL, File targetFile, int retries)
		throws IOException {

		if (retries > 0) {
			for (int i = 0; i < retries; i++) {
				try {
					_downloadFile(sourceURL, targetFile);

					return;
				}
				catch (IOException ioException) {
					System.out.println(
						"Unable to connect to " + sourceURL +
							", will retry in 10 seconds.");

					try {
						Thread.sleep(10000);
					}
					catch (InterruptedException interruptedException) {
					}
				}
			}
		}

		_downloadFile(sourceURL, targetFile);
	}

	private void _downloadGCPFile(File targetFile) {
		String gsURL = _getGSURL();

		if (gsURL == null) {
			return;
		}

		File gcpCredentialsFile = _getGCPCredentialsFile();

		try {
			if (gcpCredentialsFile != null) {
				Process process = _executeCommands(
					new String[] {
						"gcloud", "auth", "login", "--cred-file",
						gcpCredentialsFile.toString(), "--quiet"
					});

				if (process.exitValue() != 0) {
					System.out.println(
						"Unable to authenticate with credential file.");
				}
			}

			System.out.println(
				"Downloading " + gsURL + " to " + targetFile + ".");

			Process process = _executeCommands(
				new String[] {
					"gcloud", "storage", "cp", gsURL, targetFile.toString()
				});

			if (process.exitValue() != 0) {
				System.out.println(
					"Unable to download file from " + gsURL + ".");

				_deleteFile(targetFile);
			}
		}
		catch (InterruptedException | IOException | RuntimeException
					exception) {

			System.out.println(
				"Unable to download file: " + exception.getMessage());
		}
	}

	private void _downloadMirrorsCacheTempFile(File mirrorsCacheTempFile)
		throws IOException {

		if (_tryLocalNetwork) {
			File mirrorsMountFile = _getMirrorsMountFile();

			if (mirrorsMountFile.isFile()) {
				try {
					_copyFile(mirrorsMountFile, mirrorsCacheTempFile);
				}
				catch (IOException ioException) {
					_deleteFile(mirrorsCacheTempFile);

					if (_verbose) {
						System.out.println(
							"Unable to copy from mirrors mount " +
								mirrorsMountFile.getPath() + ".");
					}
				}
			}
		}

		if (_isValidFile(mirrorsCacheTempFile)) {
			return;
		}

		_deleteFile(mirrorsCacheTempFile);

		List<URL> urls = new ArrayList<>();

		if (_tryLocalNetwork) {
			String mirrorsHostname = _getMirrorsHostname();
			URL nexusTomcatURL = _getNexusTomcatURL();

			if (nexusTomcatURL != null) {
				urls.add(nexusTomcatURL);
			}
			else if (!mirrorsHostname.isEmpty()) {
				urls.add(_getMirrorsURL());
			}
		}

		urls.add(_getLocalURL());

		urls.removeAll(Collections.singleton(null));

		for (URL url : urls) {
			try {
				_downloadFile(url, mirrorsCacheTempFile, _retries);
			}
			catch (IOException ioException) {
				if (_verbose) {
					System.out.println("Unable to connect to " + url + ".");
				}
			}

			if (mirrorsCacheTempFile.exists()) {
				return;
			}
		}

		_downloadGCPFile(mirrorsCacheTempFile);

		if (_isValidFile(mirrorsCacheTempFile)) {
			return;
		}

		_deleteFile(mirrorsCacheTempFile);

		URL remoteURL = _getRemoteURL();

		try {
			_downloadFile(remoteURL, mirrorsCacheTempFile, _retries);
		}
		catch (IOException ioException) {
			_deleteFile(mirrorsCacheTempFile);

			throw ioException;
		}
	}

	private void _execute() throws IOException {
		if (_src.startsWith("file:")) {
			File srcFile = new File(_src.substring("file:".length()));

			File targetFile = _dest;

			if (_dest.exists() && _dest.isDirectory()) {
				targetFile = new File(_dest, srcFile.getName());
			}

			_copyFile(srcFile, targetFile);

			return;
		}

		Matcher matcher = _mirrorsHostNamePattern.matcher(_path);

		if (_tryLocalNetwork && matcher.find()) {
			String hostname = matcher.group();

			System.out.println(
				"The src attribute has an unnecessary reference to " +
					hostname + ".");

			_path = _path.substring(hostname.length());
		}

		File mirrorsCacheFile = _getMirrorsCacheFile();

		File mirrorsCacheLinkFile = _uniqueLinkFile(mirrorsCacheFile);
		File mirrorsCacheTempFile = _uniqueTempFile(mirrorsCacheFile);

		_deleteExpiredTempFiles(mirrorsCacheFile);

		try {
			File readFile = null;

			if (!_force) {
				readFile = _getReadFile(mirrorsCacheFile, mirrorsCacheLinkFile);

				if ((readFile != null) && !_isValidFile(readFile)) {
					_deleteFile(mirrorsCacheFile);
					_deleteFile(mirrorsCacheLinkFile);

					readFile = null;
				}
			}

			if (readFile == null) {
				_downloadMirrorsCacheTempFile(mirrorsCacheTempFile);

				if (_force) {
					_deleteFile(mirrorsCacheFile);
				}

				if (!_addToCache(mirrorsCacheFile, mirrorsCacheTempFile)) {
					System.out.println(
						mirrorsCacheFile.getPath() + " was already published.");
				}

				if (mirrorsCacheTempFile.exists()) {
					readFile = mirrorsCacheTempFile;
				}
				else {
					readFile = mirrorsCacheFile;
				}
			}

			if (_dest.exists() && _dest.isDirectory()) {
				_copyFile(readFile, new File(_dest, _fileName));
			}
			else {
				_copyFile(readFile, _dest);
			}
		}
		finally {
			_deleteFile(mirrorsCacheLinkFile);
			_deleteFile(mirrorsCacheTempFile);
		}
	}

	private Process _executeCommands(String[] commands)
		throws InterruptedException, IOException, RuntimeException {

		ProcessBuilder processBuilder = new ProcessBuilder(commands);

		Process process = processBuilder.start();

		process.waitFor();

		return process;
	}

	private String _getGCPBucketName() {
		if (_gcpBucketName != null) {
			return _gcpBucketName;
		}

		if (_hostName == null) {
			return null;
		}

		Map<String, Object> properties = project.getProperties();

		for (String propertyName : properties.keySet()) {
			Matcher bucketHostNamePropertyMatcher =
				_gcpBucketHostNamePropertyPattern.matcher(propertyName);

			if (!bucketHostNamePropertyMatcher.matches() ||
				!Objects.equals(_hostName, project.getProperty(propertyName))) {

				continue;
			}

			_gcpBucketName = bucketHostNamePropertyMatcher.group("bucketName");

			break;
		}

		return _gcpBucketName;
	}

	private File _getGCPCredentialsFile() {
		if (_gcpCredentialsFile != null) {
			return _gcpCredentialsFile;
		}

		String gcpBucketName = _getGCPBucketName();

		if (gcpBucketName == null) {
			return null;
		}

		Project project = getProject();

		String gcpCredentialsFileName = project.getProperty(
			"mirrors.gcp.credentials.file[" + gcpBucketName + "]");

		if (gcpCredentialsFileName == null) {
			return null;
		}

		File gcpCredentialsFile = new File(gcpCredentialsFileName);

		if (gcpCredentialsFile.exists()) {
			_gcpCredentialsFile = gcpCredentialsFile;
		}

		return _gcpCredentialsFile;
	}

	private String _getGSURL() {
		String gcpBucketName = _getGCPBucketName();

		if (gcpBucketName == null) {
			return null;
		}

		String path = _normalizePath(_path);

		if (!path.isEmpty()) {
			path = "/" + path;
		}

		return "gs://" + gcpBucketName + path + "/" + _fileName;
	}

	private URL _getLocalURL() {
		StringBuilder sb = new StringBuilder();

		Matcher releaseHostNameMatcher = _releaseHostNamePattern.matcher(
			_hostName);
		Matcher testHostNameMatcher = _testHostNamePattern.matcher(_hostName);

		if (releaseHostNameMatcher.find()) {
			sb.append("http://release-");
			sb.append(releaseHostNameMatcher.group("id"));
			sb.append("/");
			sb.append(releaseHostNameMatcher.group("id"));
		}
		else if (testHostNameMatcher.find()) {
			sb.append("http://");
			sb.append(testHostNameMatcher.group());
		}
		else {
			return _getRemoteURL();
		}

		sb.append("/");

		String path = _getPath();

		if (!path.isEmpty()) {
			sb.append(path);
			sb.append("/");
		}

		sb.append(_fileName);

		try {
			return new URL(sb.toString());
		}
		catch (MalformedURLException malformedURLException) {
			throw new RuntimeException(malformedURLException);
		}
	}

	private File _getMirrorsCacheFile() {
		StringBuilder sb = new StringBuilder();

		sb.append(System.getProperty("user.home"));
		sb.append(File.separator);
		sb.append(".liferay");
		sb.append(File.separator);
		sb.append("mirrors");
		sb.append(File.separator);
		sb.append(_hostName);
		sb.append(File.separator);
		sb.append(_getPlatformIndependentPath(_getPath()));

		return new File(sb.toString(), _fileName);
	}

	private String _getMirrorsHostname() {
		if (_mirrorsHostname != null) {
			return _mirrorsHostname;
		}

		Project project = getProject();

		_mirrorsHostname = project.getProperty("mirrors.hostname");

		if (_mirrorsHostname == null) {
			_mirrorsHostname = "";
		}

		return _mirrorsHostname;
	}

	private File _getMirrorsMountFile() {
		StringBuilder sb = new StringBuilder();

		sb.append("/mnt/shared/mirrors");
		sb.append(File.separator);
		sb.append(_hostName);
		sb.append(File.separator);
		sb.append(_getPlatformIndependentPath(_getPath()));

		return new File(sb.toString(), _fileName);
	}

	private URL _getMirrorsURL() {
		String mirrorsHostname = _getMirrorsHostname();

		if (mirrorsHostname.isEmpty()) {
			return _getRemoteURL();
		}

		StringBuilder sb = new StringBuilder();

		sb.append(_getURLScheme());
		sb.append(mirrorsHostname);
		sb.append("/");
		sb.append(_hostName);
		sb.append("/");

		String path = _getPath();

		if (!path.isEmpty()) {
			sb.append(path);
			sb.append("/");
		}

		sb.append(_fileName);

		try {
			return new URL(sb.toString());
		}
		catch (MalformedURLException malformedURLException) {
			throw new RuntimeException(malformedURLException);
		}
	}

	private URL _getNexusTomcatURL() {
		Matcher matcher = _nexusTomcatURLPattern.matcher(
			String.valueOf(_getRemoteURL()));

		if (!matcher.find()) {
			return null;
		}

		StringBuilder sb = new StringBuilder();

		sb.append(_getURLScheme());
		sb.append("repository.liferay.com/");
		sb.append("nexus/content/groups/public/org/apache/tomcat/tomcat/");
		sb.append(matcher.group("tomcatVersion"));
		sb.append("/");
		sb.append(matcher.group("tomcatFileName"));

		try {
			return new URL(sb.toString());
		}
		catch (MalformedURLException malformedURLException) {
			throw new RuntimeException(malformedURLException);
		}
	}

	private Pattern _getOrphanPattern(String fileName) {
		StringBuilder sb = new StringBuilder();

		sb.append("(?<timestamp>\\d{13,18})(-[0-9a-fA-F-]{36}(-");
		sb.append(_LINK_MARKER);
		sb.append(")?-)?");
		sb.append(Pattern.quote(fileName));

		return Pattern.compile(sb.toString());
	}

	private String _getPassword() {
		if (_password != null) {
			return _password;
		}

		Project project = getProject();

		_password = project.getProperty("mirrors.password");

		return _password;
	}

	private String _getPath() {
		String path = _normalizePath(_path);

		if (!Objects.equals(_hostName, "storage.googleapis.com") ||
			path.startsWith(_gcpBucketName + "/") ||
			Objects.equals(path, _gcpBucketName)) {

			return path;
		}

		if (path.isEmpty()) {
			return _gcpBucketName;
		}

		return _gcpBucketName + "/" + path;
	}

	private String _getPlatformIndependentPath(String path) {
		String[] separators = {"/", "\\"};

		for (String separator : separators) {
			if (!separator.equals(File.separator)) {
				path = path.replace(separator, File.separator);
			}
		}

		return path;
	}

	private String _getProcessOutput(Process process) {
		StringBuilder processOutput = new StringBuilder();

		try {
			BufferedReader bufferedReader = new BufferedReader(
				new InputStreamReader(process.getInputStream()));

			String line = bufferedReader.readLine();

			while (line != null) {
				processOutput.append(line);
				processOutput.append(System.lineSeparator());

				line = bufferedReader.readLine();
			}
		}
		catch (Exception exception) {
			System.out.println("Unable to get process output.");
		}

		return processOutput.toString();
	}

	private File _getReadFile(
		File mirrorsCacheFile, File mirrorsCacheLinkFile) {

		File readFile = _createReadLink(mirrorsCacheFile, mirrorsCacheLinkFile);

		if (mirrorsCacheFile.equals(readFile)) {
			StringBuilder sb = new StringBuilder();

			sb.append("Unable to link ");
			sb.append(mirrorsCacheFile.getPath());
			sb.append(". Reading it directly is not safe when the mirrors ");
			sb.append("cache is shared.");

			System.out.println(sb.toString());
		}

		return readFile;
	}

	private URL _getRemoteURL() {
		if (_hostName == null) {
			return null;
		}

		StringBuilder sb = new StringBuilder();

		if (_hostName.contains(".liferay.com") ||
			_hostName.contains("storage.googleapis.com") ||
			_src.startsWith("https://")) {

			sb.append("https://");
		}
		else {
			sb.append("http://");
		}

		sb.append(_hostName);
		sb.append("/");

		String path = _getPath();

		if (!path.isEmpty()) {
			sb.append(path);
			sb.append("/");
		}

		sb.append(_fileName);

		try {
			return new URL(sb.toString());
		}
		catch (MalformedURLException malformedURLException) {
			throw new RuntimeException(malformedURLException);
		}
	}

	private String _getURLScheme() {
		Project project = getProject();

		boolean ssl = _ssl;

		String mirrorsSSL = project.getProperty("mirrors.ssl");

		if ((mirrorsSSL != null) && !mirrorsSSL.isEmpty()) {
			ssl = Boolean.parseBoolean(mirrorsSSL);
		}

		if (ssl) {
			return "https://";
		}

		return "http://";
	}

	private String _getUserAgent() {
		if (_userAgent != null) {
			return _userAgent;
		}

		Project project = getProject();

		_userAgent = project.getProperty("mirrors.user.agent");

		return _userAgent;
	}

	private String _getUsername() {
		if (_username != null) {
			return _username;
		}

		Project project = getProject();

		_username = project.getProperty("mirrors.username");

		return _username;
	}

	private boolean _has7z() {
		String[] commands = {"/bin/bash", "-c", "type 7z"};

		try {
			Process process = _executeCommands(commands);

			if (process.exitValue() != 0) {
				System.out.println("Unable to validate 7z file.");

				return false;
			}
		}
		catch (Exception exception) {
			System.out.println("Unable to validate 7z file.");

			return false;
		}

		return true;
	}

	private boolean _is7zFile(File file) {
		if (!_has7z()) {
			return true;
		}

		String[] commands = {"/bin/bash", "-c", "7z t " + file.toString()};

		Process process = null;

		try {
			process = _executeCommands(commands);
		}
		catch (Exception exception) {
			System.out.println(file + " is invalid.");

			return false;
		}

		String processOutput = _getProcessOutput(process);

		int exitValue = process.exitValue();

		if ((exitValue == 0) && !processOutput.contains("Files: 0\n")) {
			return true;
		}

		System.out.println(processOutput);

		System.out.println(file + " is invalid.");

		return false;
	}

	private boolean _is7zFileName(String fileName) {
		return fileName.endsWith(".7z");
	}

	private boolean _isCINode() {
		if (_isNullOrEmpty(System.getenv("JENKINS_URL")) &&
			_isNullOrEmpty(System.getenv("MASTER_NETWORK_NAME"))) {

			return false;
		}

		return true;
	}

	private boolean _isNullOrEmpty(String string) {
		if (string == null) {
			return true;
		}

		String trimmedString = string.trim();

		return trimmedString.isEmpty();
	}

	private boolean _isTarGzFile(File file) throws IOException {
		if (!file.exists()) {
			return false;
		}

		try (GZIPInputStream gzipInputStream = new GZIPInputStream(
				new FileInputStream(file));

			InputStream bufferedInputStream = new BufferedInputStream(
				gzipInputStream);

			TarInputStream tarInputStream = new TarInputStream(
				bufferedInputStream)) {

			TarEntry tarEntry;

			while ((tarEntry = tarInputStream.getNextEntry()) != null) {
				if (tarEntry.isDirectory()) {
					continue;
				}

				byte[] buffer = new byte[1024];
				int bytesRead;

				while ((bytesRead = tarInputStream.read(buffer)) != -1) {
				}
			}

			return true;
		}
		catch (IOException ioException) {
			System.out.println(file.getPath() + " is an invalid TAR GZ file.");

			return false;
		}
	}

	private boolean _isTarGzFileName(String fileName) {
		if (fileName.endsWith(".tar.gz") || fileName.endsWith(".tgz")) {
			return true;
		}

		return false;
	}

	private boolean _isValidFile(File file) throws IOException {
		if (!file.exists()) {
			return false;
		}

		if (_is7zFileName(_fileName)) {
			return _is7zFile(file);
		}

		if (_isTarGzFileName(_fileName)) {
			return _isTarGzFile(file);
		}

		if (_isZipFileName(_fileName)) {
			return _isZipFile(file);
		}

		return true;
	}

	private boolean _isValidMD5(File file, URL url) throws IOException {
		if (_skipChecksum) {
			return true;
		}

		if ((file == null) || !file.exists()) {
			return false;
		}

		String remoteMD5 = null;

		try {
			remoteMD5 = _toString(url);
		}
		catch (Exception exception) {
			if (_verbose) {
				System.out.println("Unable to access MD5 file.");
			}

			return true;
		}

		Checksum checksum = new Checksum();

		checksum.setAlgorithm("MD5");
		checksum.setFile(file);
		checksum.setProject(new Project());
		checksum.setProperty("md5");

		checksum.execute();

		Project project = checksum.getProject();

		String localMD5 = project.getProperty("md5");

		return remoteMD5.contains(localMD5);
	}

	private boolean _isZipFile(File file) throws IOException {
		if (!file.exists()) {
			return false;
		}

		ZipFile zipFile = null;

		try {
			zipFile = new ZipFile(file, ZipFile.OPEN_READ);

			int count = 0;

			Enumeration<?> enumeration = zipFile.entries();

			while (enumeration.hasMoreElements()) {
				enumeration.nextElement();

				count++;
			}

			StringBuilder sb = new StringBuilder();

			sb.append(file.getPath());
			sb.append(" is a valid zip file with ");
			sb.append(count);
			sb.append(" entries.");

			System.out.println(sb.toString());

			return true;
		}
		catch (IOException ioException) {
			System.out.println(file.getPath() + " is an invalid zip file.");

			return false;
		}
		finally {
			if (zipFile != null) {
				zipFile.close();
			}
		}
	}

	private boolean _isZipFileName(String fileName) {
		if (fileName.endsWith(".ear") || fileName.endsWith(".jar") ||
			fileName.endsWith(".war") || fileName.endsWith(".zip")) {

			return true;
		}

		return false;
	}

	private String _normalizePath(String path) {
		if (path == null) {
			return "";
		}

		path = path.replaceAll("/+", "/");

		if (path.startsWith("/")) {
			path = path.substring(1);
		}

		if (path.endsWith("/")) {
			path = path.substring(0, path.length() - 1);
		}

		return path;
	}

	private URLConnection _openConnection(URL url) throws IOException {
		URLConnection urlConnection = null;

		while (true) {
			urlConnection = url.openConnection();

			if (!(urlConnection instanceof HttpURLConnection)) {
				break;
			}

			HttpURLConnection httpURLConnection =
				(HttpURLConnection)urlConnection;

			String password = _getPassword();
			String username = _getUsername();

			if ((password != null) && (username != null)) {
				String auth = username + ":" + password;
				Base64.Encoder encoder = Base64.getEncoder();

				httpURLConnection.setRequestProperty(
					"Authorization",
					"Basic " + encoder.encodeToString(auth.getBytes()));
			}

			if (_getUserAgent() != null) {
				httpURLConnection.setRequestProperty(
					"User-Agent", _getUserAgent());
			}

			int responseCode = httpURLConnection.getResponseCode();

			if ((responseCode != HttpURLConnection.HTTP_MOVED_PERM) &&
				(responseCode != HttpURLConnection.HTTP_MOVED_TEMP)) {

				break;
			}

			url = new URL(httpURLConnection.getHeaderField("Location"));
		}

		return urlConnection;
	}

	private boolean _renameFile(File cacheFile, File tempFile)
		throws IOException {

		if (tempFile.renameTo(cacheFile)) {
			return true;
		}

		if (cacheFile.exists()) {
			return false;
		}

		StringBuilder sb = new StringBuilder();

		sb.append("Unable to publish ");
		sb.append(tempFile.getPath());
		sb.append(" to ");
		sb.append(cacheFile.getPath());
		sb.append(".");

		throw new IOException(sb.toString());
	}

	private int _toFile(URL url, File file) throws IOException {
		if (file.exists()) {
			_deleteFile(file);
		}

		File dir = file.getParentFile();

		if ((dir != null) && !dir.exists()) {
			dir.mkdirs();
		}

		OutputStream outputStream = new FileOutputStream(file);

		try {
			return _toOutputStream(url, outputStream);
		}
		catch (IOException ioException) {
			if (file.exists()) {
				_deleteFile(file);
			}

			throw ioException;
		}
		finally {
			if (outputStream != null) {
				outputStream.close();
			}
		}
	}

	private int _toOutputStream(URL url, OutputStream outputStream)
		throws IOException {

		URLConnection urlConnection = _openConnection(url);

		InputStream inputStream = urlConnection.getInputStream();

		try {
			byte[] bytes = new byte[1024 * 16];
			int read = 0;
			int size = 0;
			long time = System.currentTimeMillis();

			while ((read = inputStream.read(bytes)) > 0) {
				outputStream.write(bytes, 0, read);
				size += read;

				if (_verbose && ((System.currentTimeMillis() - time) > 100)) {
					System.out.print(".");

					time = System.currentTimeMillis();
				}
			}

			if (_verbose) {
				System.out.println("\n");
			}

			return size;
		}
		finally {
			if (inputStream != null) {
				inputStream.close();
			}
		}
	}

	private String _toString(URL url) throws IOException {
		OutputStream outputStream = new ByteArrayOutputStream();

		try {
			_toOutputStream(url, outputStream);

			return outputStream.toString();
		}
		finally {
			if (outputStream != null) {
				outputStream.close();
			}
		}
	}

	private File _uniqueFile(File cacheFile, String marker) {
		StringBuilder sb = new StringBuilder();

		sb.append(System.currentTimeMillis());
		sb.append("-");
		sb.append(UUID.randomUUID());
		sb.append("-");

		if (!marker.isEmpty()) {
			sb.append(marker);
			sb.append("-");
		}

		sb.append(cacheFile.getName());

		return new File(cacheFile.getParentFile(), sb.toString());
	}

	private File _uniqueLinkFile(File cacheFile) {
		return _uniqueFile(cacheFile, _LINK_MARKER);
	}

	private File _uniqueTempFile(File cacheFile) {
		return _uniqueFile(cacheFile, "");
	}

	private static final String _LINK_MARKER = "link";

	private static final long _MAX_AGE_MILLIS = 24 * 60 * 60 * 1000;

	private static final Pattern _basicAuthenticationURLPattern =
		Pattern.compile("(https?://)([^:]+):([^@]+)@(.+)");
	private static final Pattern _gcpBucketHostNamePropertyPattern =
		Pattern.compile(
			"mirrors.gcp.bucket.hostname\\[(?<bucketName>[^\\]]+)\\]");
	private static final Pattern _gsURLPattern = Pattern.compile(
		"gs://(?<bucketName>[^/]+)/(?<path>.+/)(?<fileName>.+)");
	private static final Pattern _httpURLPattern = Pattern.compile(
		"https?://(?<mirrorsHostname>mirrors(\\.[^\\.]+\\.liferay.com)?/)?" +
			"(?<hostName>[^/]+(/\\d+)?)/(?<path>.+/)(?<fileName>.+)");
	private static final Pattern _mirrorsHostNamePattern = Pattern.compile(
		"^mirrors\\.[^\\.]+\\.liferay.com/");
	private static final Pattern _nexusTomcatURLPattern = Pattern.compile(
		"http://archive.apache.org/dist/tomcat/tomcat-\\d+/" +
			"v(?<tomcatVersion>[^/]+)/bin/" +
				"apache-(?<tomcatFileName>.+(\\.tar\\.gz|\\.zip))");
	private static final Pattern _releaseHostNamePattern = Pattern.compile(
		"(release-\\d+|release.liferay.com)/(?<id>\\d+)");
	private static final Pattern _testHostNamePattern = Pattern.compile(
		"test-\\d+-\\d+");

	private File _dest;
	private String _fileName;
	private boolean _force;
	private String _gcpBucketName;
	private File _gcpCredentialsFile;
	private String _hostName;
	private boolean _ignoreErrors;
	private String _mirrorsHostname;
	private String _password;
	private String _path;
	private int _retries = 1;
	private boolean _skipChecksum;
	private String _src;
	private boolean _ssl;
	private boolean _tryLocalNetwork = _isCINode();
	private String _userAgent;
	private String _username;
	private boolean _verbose;

}