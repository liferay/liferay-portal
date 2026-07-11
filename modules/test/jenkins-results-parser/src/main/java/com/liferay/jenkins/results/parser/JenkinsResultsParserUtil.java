/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser;

import com.google.common.collect.Lists;
import com.google.common.io.CountingInputStream;

import com.liferay.poshi.core.pql.PQLEntityFactory;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;

import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.net.UnknownHostException;

import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.BasicFileAttributes;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.Random;
import java.util.Set;
import java.util.Stack;
import java.util.StringTokenizer;
import java.util.TimeZone;
import java.util.TreeSet;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.SystemUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Node;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author Peter Yoo
 */
public class JenkinsResultsParserUtil {

	public static final String JENKINS_REPOSITORY_NAME = "liferay-jenkins-ee";

	public static final int PAGES_GITHUB_API_PAGES_SIZE_MAX = 10;

	public static final int PER_PAGE_GITHUB_API_PAGES_SIZE_MAX = 100;

	public static boolean debug;

	public static void addRedactToken(String token) {
		if (isNullOrEmpty(token) || _forbiddenRedactTokens.contains(token)) {
			return;
		}

		_redactTokens.addAll(_getRedactTokenVariants(token));
	}

	public static void append(File file, String content) throws IOException {
		if (debug) {
			System.out.println(
				combine(
					"Append to file ", file.getPath(), " with length ",
					String.valueOf(content.length())));
		}

		File parentDir = file.getParentFile();

		if ((parentDir != null) && !parentDir.exists()) {
			if (debug) {
				System.out.println("Make parent directories for " + file);
			}

			parentDir.mkdirs();
		}

		try (OutputStream outputStream = Files.newOutputStream(
				Paths.get(file.toURI()), StandardOpenOption.CREATE,
				StandardOpenOption.APPEND)) {

			outputStream.write(content.getBytes());
		}
	}

	public static void appendToCacheFile(String key, String content) {
		File cacheFile = getCacheFile(key);

		boolean cacheFileCreated = false;

		if (!cacheFile.exists()) {
			cacheFileCreated = true;
		}

		try {
			append(cacheFile, content);
		}
		catch (IOException ioException) {
			throw new RuntimeException(
				"Unable to append to cache file", ioException);
		}

		if (cacheFileCreated) {
			System.out.println("Created cache file in " + cacheFile.getPath());

			cacheFile.deleteOnExit();
		}
	}

	public static void cancelQueuedItem(
		long itemId, JenkinsMaster jenkinsMaster) {

		StringBuilder sb = new StringBuilder();

		sb.append("def queue = Jenkins.instance.queue;\n");

		sb.append("def items = queue.items.findAll{it.getId() == ");
		sb.append(itemId);
		sb.append("};\n");

		sb.append("queue.cancel(items[0]);");

		executeJenkinsScript(jenkinsMaster.getName(), sb.toString());
	}

	public static void clearCache() {
		_cacheURL = null;
		_ciNode = null;

		File cacheDirectory = new File(
			System.getProperty("java.io.tmpdir"), "jenkins-cached-files");

		System.out.println(
			"Clearing cache " + getCanonicalPath(cacheDirectory));

		if (!cacheDirectory.exists()) {
			return;
		}

		delete(cacheDirectory);
	}

	public static String combine(String... strings) {
		if ((strings == null) || (strings.length == 0)) {
			return "";
		}

		StringBuilder sb = new StringBuilder();

		for (String string : strings) {
			sb.append(string);
		}

		return sb.toString();
	}

	public static <T> List<T> concatenate(
		List<List<T>> lists, boolean allowDuplicates) {

		Collection<T> concatenatedCollection = new ArrayList<>();

		if (!allowDuplicates) {
			concatenatedCollection = new HashSet<>();
		}

		for (List<T> list : lists) {
			concatenatedCollection.addAll(list);
		}

		return new ArrayList<>(concatenatedCollection);
	}

	public static void copy(File sourceFile, File targetFile)
		throws IOException {

		try {
			if (!sourceFile.exists()) {
				throw new FileNotFoundException(
					sourceFile.getPath() + " does not exist");
			}

			if (sourceFile.isDirectory()) {
				targetFile.mkdir();

				for (File file : sourceFile.listFiles()) {
					copy(file, new File(targetFile, file.getName()));
				}

				return;
			}

			File parentFile = targetFile.getParentFile();

			if ((parentFile != null) && !parentFile.exists()) {
				parentFile.mkdirs();
			}

			try (FileInputStream fileInputStream = new FileInputStream(
					sourceFile)) {

				try (FileOutputStream fileOutputStream = new FileOutputStream(
						targetFile)) {

					Files.copy(Paths.get(sourceFile.toURI()), fileOutputStream);

					fileOutputStream.flush();
				}
			}
		}
		catch (IOException ioException) {
			if (targetFile.exists()) {
				delete(targetFile);
			}

			throw ioException;
		}
	}

	public static void copy(
			final File sourceBaseDir, final File targetBaseDir,
			final List<PathMatcher> includePathMatchers,
			final List<PathMatcher> excludePathMatchers)
		throws IOException {

		if ((sourceBaseDir == null) || !sourceBaseDir.exists() ||
			(targetBaseDir == null)) {

			return;
		}

		if (!targetBaseDir.exists()) {
			targetBaseDir.mkdirs();
		}

		Files.walkFileTree(
			sourceBaseDir.toPath(),
			new SimpleFileVisitor<Path>() {

				@Override
				public FileVisitResult preVisitDirectory(
						Path filePath, BasicFileAttributes basicFileAttributes)
					throws IOException {

					if (isFileExcluded(
							excludePathMatchers, filePath.toFile())) {

						return FileVisitResult.SKIP_SUBTREE;
					}

					return FileVisitResult.CONTINUE;
				}

				@Override
				public FileVisitResult visitFile(
						Path filePath, BasicFileAttributes basicFileAttributes)
					throws IOException {

					if (!isFileIncluded(
							excludePathMatchers, includePathMatchers,
							filePath.toFile())) {

						return FileVisitResult.CONTINUE;
					}

					File file = filePath.toFile();

					String relativePath = getPathRelativeTo(
						file, sourceBaseDir);

					copy(file, new File(targetBaseDir, relativePath));

					return FileVisitResult.CONTINUE;
				}

			});
	}

	public static JSONArray createJSONArray(String jsonString) {
		jsonString = jsonString.trim();

		while (jsonString.startsWith("\uFEFF")) {
			jsonString = jsonString.substring(1);
		}

		return new JSONArray(jsonString);
	}

	public static JSONObject createJSONObject(String jsonString)
		throws IOException {

		jsonString = jsonString.trim();

		while (jsonString.startsWith("\uFEFF")) {
			jsonString = jsonString.substring(1);
		}

		JSONObject jsonObject = new JSONObject(jsonString);

		if (jsonObject.isNull("duration") || jsonObject.isNull("result") ||
			jsonObject.isNull("url")) {

			return jsonObject;
		}

		String url = jsonObject.getString("url");

		if (!url.contains("AXIS_VARIABLE")) {
			return jsonObject;
		}

		Object result = jsonObject.get("result");

		if (result instanceof JSONObject) {
			return jsonObject;
		}

		if ((jsonObject.getInt("duration") == 0) && result.equals("FAILURE")) {
			jsonObject.putOpt("result", getActualResult(url));
		}

		return jsonObject;
	}

	public static URL createURL(String url) throws Exception {
		return encode(new URL(url));
	}

	public static String decode(String url)
		throws UnsupportedEncodingException {

		return URLDecoder.decode(url, "UTF-8");
	}

	public static boolean delete(File file) {
		if (!file.exists()) {
			System.out.println(
				"Unable to delete because file does not exist " +
					file.getPath());

			return false;
		}

		boolean successful = true;

		if (file.isDirectory()) {
			for (File subfile : file.listFiles()) {
				if (successful) {
					successful = delete(subfile);
				}
				else {
					delete(subfile);
				}
			}
		}

		if (successful) {
			return file.delete();
		}

		return successful;
	}

	public static void delete(
			File baseDir, final List<PathMatcher> includePathMatchers,
			final List<PathMatcher> excludePathMatchers)
		throws IOException {

		if ((baseDir == null) || !baseDir.exists()) {
			return;
		}

		Files.walkFileTree(
			baseDir.toPath(),
			new SimpleFileVisitor<Path>() {

				@Override
				public FileVisitResult postVisitDirectory(
						Path dirPath, IOException ioException)
					throws IOException {

					if (Files.isDirectory(dirPath)) {
						File dir = dirPath.toFile();

						File[] files = dir.listFiles();

						if ((files == null) || (files.length == 0)) {
							delete(dir);
						}
					}

					return FileVisitResult.CONTINUE;
				}

				@Override
				public FileVisitResult preVisitDirectory(
						Path filePath, BasicFileAttributes basicFileAttributes)
					throws IOException {

					if (isFileExcluded(
							excludePathMatchers, filePath.toFile())) {

						return FileVisitResult.SKIP_SUBTREE;
					}

					return FileVisitResult.CONTINUE;
				}

				@Override
				public FileVisitResult visitFile(
						Path filePath, BasicFileAttributes basicFileAttributes)
					throws IOException {

					if (!isFileIncluded(
							excludePathMatchers, includePathMatchers,
							filePath.toFile())) {

						return FileVisitResult.CONTINUE;
					}

					delete(filePath.toFile());

					return FileVisitResult.CONTINUE;
				}

			});
	}

	public static String encode(String url)
		throws MalformedURLException, URISyntaxException {

		URL encodedURL = encode(new URL(url));

		return encodedURL.toExternalForm();
	}

	public static URL encode(URL url)
		throws MalformedURLException, URISyntaxException {

		URI uri = new URI(
			url.getProtocol(), url.getUserInfo(), url.getHost(), url.getPort(),
			url.getPath(), url.getQuery(), url.getRef());

		String uriASCIIString = uri.toASCIIString();

		return new URL(uriASCIIString.replace("#", "%23"));
	}

	public static String escapeForBash(String string) {
		string = string.replaceAll(" ", "\\\\ ");
		string = string.replaceAll("'", "\\\\\\\'");
		string = string.replaceAll(";", "\\\\;");
		string = string.replaceAll("<", "\\\\\\<");
		string = string.replaceAll(">", "\\\\\\>");
		string = string.replaceAll("\"", "\\\\\\\"");
		string = string.replaceAll("\\$", "\\\\\\$");
		string = string.replaceAll("\\(", "\\\\\\(");
		string = string.replaceAll("\\)", "\\\\\\)");
		string = string.replaceAll("\\n", "\\\\n");

		return string;
	}

	public static Process executeBashCommands(
			boolean exitOnFirstFail, File baseDir, long timeout,
			String... commands)
		throws IOException, TimeoutException {

		return executeBashCommands(
			baseDir, exitOnFirstFail, true, timeout, commands);
	}

	public static Process executeBashCommands(
			boolean exitOnFirstFail, String... commands)
		throws IOException, TimeoutException {

		return executeBashCommands(
			exitOnFirstFail, new File("."),
			_MILLIS_BASH_COMMAND_TIMEOUT_DEFAULT, commands);
	}

	public static Process executeBashCommands(
			File baseDir, boolean exitOnFirstFail, boolean printCommands,
			long timeout, String... commands)
		throws IOException, TimeoutException {

		Shell.ExecutionResult executionResult = Shell.execute(
			new Shell.ExecutionRequest(
				baseDir, commands, exitOnFirstFail, printCommands, timeout));

		return new ExecutionResultProcess(executionResult);
	}

	public static Process executeBashCommands(File baseDir, String... commands)
		throws IOException, TimeoutException {

		return executeBashCommands(
			true, baseDir, _MILLIS_BASH_COMMAND_TIMEOUT_DEFAULT, commands);
	}

	public static Process executeBashCommands(long timeout, String... commands)
		throws IOException, TimeoutException {

		return executeBashCommands(true, new File("."), timeout, commands);
	}

	public static Process executeBashCommands(String... commands)
		throws IOException, TimeoutException {

		return executeBashCommands(
			true, new File("."), _MILLIS_BASH_COMMAND_TIMEOUT_DEFAULT,
			commands);
	}

	public static void executeBashCommandService(
		String command, File baseDir, Map<String, String> environments,
		long maxLogSize) {

		_executeCommandService(
			command, baseDir, environments, maxLogSize, false);
	}

	public static void executeBatchCommandService(
		String command, File baseDir, Map<String, String> environments,
		long maxLogSize) {

		if (!isWindows()) {
			throw new RuntimeException("Invalid OS: " + SystemUtils.OS_NAME);
		}

		_executeCommandService(
			command, baseDir, environments, maxLogSize, true);
	}

	public static String executeJenkinsScript(
		String jenkinsMasterName, String script) {

		return executeJenkinsScript(jenkinsMasterName, script, false);
	}

	public static String executeJenkinsScript(
		String jenkinsMasterName, String script, boolean rawResponse) {

		return executeJenkinsScript(
			jenkinsMasterName, script, rawResponse, _MILLIS_TIMEOUT_DEFAULT);
	}

	public static String executeJenkinsScript(
		String jenkinsMasterName, String script, boolean rawResponse,
		int timeout) {

		Matcher matcher = _test1MasterNamePattern.matcher(jenkinsMasterName);

		try {
			String url = null;

			if (!isCINode() && matcher.matches()) {
				int masterNumber = Integer.valueOf(matcher.group(1));

				if (masterNumber > 40) {
					url = combine(
						"https://", jenkinsMasterName, ".liferay.com/script");
				}
			}

			if (url == null) {
				url = getLocalURL(
					combine("http://", jenkinsMasterName, "/script"));
			}

			URL urlObject = new URL(fixURL(url));

			HttpURLConnection httpURLConnection =
				(HttpURLConnection)urlObject.openConnection();

			if (timeout != 0) {
				httpURLConnection.setConnectTimeout(timeout);
				httpURLConnection.setReadTimeout(timeout);
			}

			httpURLConnection.setDoOutput(true);
			httpURLConnection.setRequestMethod("POST");

			HTTPAuthorization httpAuthorization = getJenkinsHTTPAuthorization();

			httpURLConnection.setRequestProperty(
				"Authorization", httpAuthorization.toString());

			try (OutputStream outputStream =
					httpURLConnection.getOutputStream()) {

				String post = "script=" + URLEncoder.encode(script, "UTF-8");

				outputStream.write(post.getBytes("UTF-8"));

				outputStream.flush();
			}

			httpURLConnection.connect();

			int responseCode = httpURLConnection.getResponseCode();

			System.out.println(
				combine(
					"Response from ", urlObject.toString(), ": ",
					String.valueOf(responseCode), " ",
					httpURLConnection.getResponseMessage()));

			if (responseCode >= 400) {
				System.out.println(script);

				return null;
			}

			String responseText = readInputStream(
				httpURLConnection.getInputStream());

			String rawResponseText = responseText.substring(
				responseText.lastIndexOf("<pre>") + 5,
				responseText.lastIndexOf("</pre>"));

			if (rawResponse) {
				return rawResponseText.trim();
			}

			return combine(jenkinsMasterName, ":\n", rawResponseText);
		}
		catch (IOException ioException) {
			System.out.println(
				"WARNING: Unable to execute Jenkins script: " +
					ioException.getMessage());
		}

		return null;
	}

	public static boolean exists(URL url) {
		HttpURLConnection httpURLConnection = null;

		try {
			httpURLConnection = (HttpURLConnection)url.openConnection();

			httpURLConnection.setRequestMethod("HEAD");

			HTTPAuthorization httpAuthorization = getJenkinsHTTPAuthorization();

			httpURLConnection.setRequestProperty(
				"Authorization", httpAuthorization.toString());

			httpURLConnection.connect();

			int responseCode = httpURLConnection.getResponseCode();

			if (responseCode == HttpURLConnection.HTTP_OK) {
				return true;
			}
		}
		catch (IOException ioException) {
			if (_log.isDebugEnabled()) {
				_log.debug(ioException);
			}
		}
		finally {
			if (httpURLConnection != null) {
				httpURLConnection.disconnect();
			}
		}

		return false;
	}

	public static String expandSlaveRange(String value) {
		StringBuilder sb = new StringBuilder();

		for (String hostName : value.split(",")) {
			hostName = hostName.trim();

			int x = hostName.indexOf("..");

			if (x == -1) {
				if (sb.length() > 0) {
					sb.append(",");
				}

				sb.append(hostName);

				continue;
			}

			int y = hostName.lastIndexOf("-") + 1;

			String prefix = hostName.substring(0, y);

			int first = Integer.parseInt(hostName.substring(y, x));

			int last = Integer.parseInt(hostName.substring(x + 2));

			for (int current = first; current <= last; current++) {
				if (sb.length() > 0) {
					sb.append(",");
				}

				sb.append(prefix);
				sb.append(current);
			}
		}

		return sb.toString();
	}

	public static List<File> findDirs(File baseDir, String regex) {
		List<File> dirs = new ArrayList<>();

		for (File dir : baseDir.listFiles()) {
			if (!dir.isDirectory()) {
				continue;
			}

			String dirName = dir.getName();

			if (dirName.matches(regex)) {
				dirs.add(dir);
			}

			dirs.addAll(findDirs(dir, regex));
		}

		return dirs;
	}

	public static List<File> findFiles(File baseDir, String regex) {
		List<File> files = new ArrayList<>();

		File[] filesArray = baseDir.listFiles();

		if (filesArray == null) {
			return files;
		}

		for (File file : filesArray) {
			String fileName = file.getName();

			if (file.isDirectory()) {
				files.addAll(findFiles(file, regex));
			}
			else if (fileName.matches(regex)) {
				files.add(file);
			}
		}

		return files;
	}

	public static List<File> findSiblingFiles(File file) {
		return findSiblingFiles(file, false);
	}

	public static List<File> findSiblingFiles(File file, boolean includeFile) {
		if ((file == null) || !file.exists()) {
			if (includeFile) {
				return Arrays.asList(file);
			}

			return Collections.emptyList();
		}

		File parentFile = file.getParentFile();

		if ((parentFile == null) || !parentFile.exists()) {
			if (includeFile) {
				return Arrays.asList(file);
			}

			return Collections.emptyList();
		}

		List<File> siblingFiles = new ArrayList<>(
			Arrays.asList(parentFile.listFiles()));

		if (!includeFile) {
			siblingFiles.remove(file);
		}

		return Collections.unmodifiableList(siblingFiles);
	}

	public static String fixFileName(String fileName) {
		String prefix = "";

		if (fileName.startsWith("file:")) {
			prefix = "file:";

			fileName = fileName.substring(prefix.length());
		}

		fileName = fileName.replace(">", "[gt]");
		fileName = fileName.replace("<", "[lt]");
		fileName = fileName.replace("|", "[pi]");
		fileName = fileName.replace("?", "[qt]");
		fileName = fileName.replace(":", "[sc]");

		return prefix + fileName;
	}

	public static String fixFileURL(String fileURL) {
		try {
			return URLDecoder.decode(fileURL, "UTF-8");
		}
		catch (UnsupportedEncodingException unsupportedEncodingException) {
			throw new RuntimeException(unsupportedEncodingException);
		}
	}

	public static String fixJSON(String json) {
		json = json.replaceAll("'", "&#39;");
		json = json.replaceAll("<", "&#60;");
		json = json.replaceAll(">", "&#62;");
		json = json.replaceAll("\\(", "&#40;");
		json = json.replaceAll("\\)", "&#41;");
		json = json.replaceAll("\\[", "&#91;");
		json = json.replaceAll("\\\"", "&#34;");
		json = json.replaceAll("\\\\", "&#92;");
		json = json.replaceAll("\\]", "&#93;");
		json = json.replaceAll("\\{", "&#123;");
		json = json.replaceAll("\\}", "&#125;");
		json = json.replaceAll("\n", "<br />");
		json = json.replaceAll("\t", "&#09;");
		json = json.replaceAll("\u00BB", "&raquo;");

		return json;
	}

	public static String fixURL(String urlString) {
		URL url = null;

		try {
			url = new URL(urlString);
		}
		catch (MalformedURLException malformedURLException) {
			try {
				urlString = URLEncoder.encode(
					urlString, StandardCharsets.UTF_8.name());

				urlString = urlString.replaceAll("\\+", "%20");

				urlString = urlString.replaceAll("%21", "!");
				urlString = urlString.replaceAll("%25", "%");
				urlString = urlString.replaceAll("%2B", "+");

				return urlString;
			}
			catch (UnsupportedEncodingException unsupportedEncodingException) {
				throw new RuntimeException(unsupportedEncodingException);
			}
		}

		if (!urlString.contains("?")) {
			return urlString;
		}

		StringBuilder sb = new StringBuilder(
			urlString.replaceAll("(.*\\?).*", "$1"));

		String queryString = url.getQuery();

		if ((queryString == null) || queryString.isEmpty()) {
			return sb.toString();
		}

		Matcher matcher = _urlQueryStringPattern.matcher(url.getQuery());

		while (matcher.find()) {
			sb.append(matcher.group(1));
			sb.append("=");

			try {
				String queryParameterValue = matcher.group(2);

				queryParameterValue = URLEncoder.encode(
					queryParameterValue, StandardCharsets.UTF_8.name());

				queryParameterValue = queryParameterValue.replaceAll(
					"\\+", "%20");

				queryParameterValue = queryParameterValue.replaceAll(
					"%21", "!");
				queryParameterValue = queryParameterValue.replaceAll(
					"%25", "%");
				queryParameterValue = queryParameterValue.replaceAll(
					"%2B", "+");

				sb.append(queryParameterValue);
			}
			catch (UnsupportedEncodingException unsupportedEncodingException) {
				throw new RuntimeException(unsupportedEncodingException);
			}

			if (!matcher.hitEnd()) {
				sb.append("&");
			}
		}

		return sb.toString();
	}

	public static List<Build> flatten(List<Build> builds) {
		List<Build> flattenedBuilds = new ArrayList<>();

		for (Build build : builds) {
			flattenedBuilds.add(build);

			if (!(build instanceof ParentBuild)) {
				continue;
			}

			ParentBuild parentBuild = (ParentBuild)build;

			List<Build> downstreamBuilds = parentBuild.getDownstreamBuilds(
				null);

			if (!downstreamBuilds.isEmpty()) {
				flattenedBuilds.addAll(flatten(downstreamBuilds));
			}
		}

		return flattenedBuilds;
	}

	public static String getActualResult(String buildURL) throws IOException {
		String progressiveText = toString(
			getLocalURL(buildURL + "/logText/progressiveText"), false);

		if (progressiveText.contains("Finished:")) {
			if (progressiveText.contains("Finished: SUCCESS")) {
				return "SUCCESS";
			}

			if (progressiveText.contains("Finished: UNSTABLE") ||
				progressiveText.contains("Finished: FAILURE")) {

				return "FAILURE";
			}
		}

		return null;
	}

	public static Long getAverage(List<Long> numbers) {
		if ((numbers == null) || numbers.isEmpty()) {
			return 0L;
		}

		List<Long> numbersToAverage = new ArrayList<>(numbers);

		numbersToAverage.removeAll(Collections.singleton(null));

		if (numbersToAverage.isEmpty()) {
			return 0L;
		}

		long total = 0L;

		for (Long number : numbersToAverage) {
			total += number;
		}

		return total / numbersToAverage.size();
	}

	public static String getAxisVariable(JSONObject jsonObject) {
		JSONArray actionsJSONArray = (JSONArray)jsonObject.get("actions");

		for (int i = 0; i < actionsJSONArray.length(); i++) {
			Object object = actionsJSONArray.get(i);

			if (object.equals(JSONObject.NULL)) {
				continue;
			}

			JSONObject actionsJSONObject = actionsJSONArray.getJSONObject(i);

			JSONArray parametersJSONArray = actionsJSONObject.optJSONArray(
				"parameters");

			if (parametersJSONArray == null) {
				continue;
			}

			for (int j = 0; j < parametersJSONArray.length(); j++) {
				JSONObject parametersJSONObject =
					parametersJSONArray.getJSONObject(j);

				String name = parametersJSONObject.getString("name");

				if (name.contains("AXIS_VARIABLE")) {
					return parametersJSONObject.getString("value");
				}
			}
		}

		return "";
	}

	public static String getAxisVariable(String axisBuildURL) {
		String url = null;

		try {
			url = decode(axisBuildURL);
		}
		catch (UnsupportedEncodingException unsupportedEncodingException) {
			throw new RuntimeException("Unable to encode " + axisBuildURL);
		}

		Matcher matcher = _axisVariablePattern.matcher(url);

		if (matcher.find()) {
			return matcher.group("axisVariable");
		}

		return "";
	}

	public static File getBaseGitRepositoryDir() {
		Properties buildProperties = null;

		try {
			buildProperties = getBuildProperties();
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}

		return new File(buildProperties.getProperty("base.repository.dir"));
	}

	public static String getBuildArtifactURL(
		String buildURL, String artifactName) {

		Matcher matcher = _buildURLPattern.matcher(buildURL);

		matcher.find();

		StringBuilder sb = new StringBuilder();

		sb.append("http://test-");
		sb.append(matcher.group("cohortNumber"));
		sb.append("-");
		sb.append(matcher.group("masterNumber"));
		sb.append("/userContent/jobs/");
		sb.append(matcher.group("jobName"));
		sb.append("/builds/");
		sb.append(matcher.group("buildNumber"));
		sb.append("/");
		sb.append(artifactName);

		return sb.toString();
	}

	public static String getBuildDirPath() {
		String topLevelBuildURL = Environment.get("TOP_LEVEL_BUILD_URL");

		if (topLevelBuildURL != null) {
			String buildDirPath = getBuildDirPath(topLevelBuildURL);

			if (buildDirPath != null) {
				return buildDirPath;
			}
		}

		String buildNumber = Environment.get("BUILD_NUMBER");
		String jobName = Environment.get("JOB_NAME");
		String masterHostname = Environment.get("MASTER_HOSTNAME");

		return getBuildDirPath(buildNumber, jobName, masterHostname);
	}

	public static String getBuildDirPath(String buildURL) {
		Matcher matcher = _buildURLPattern.matcher(buildURL);

		if (matcher.find()) {
			return getBuildDirPath(
				matcher.group("buildNumber"), matcher.group("jobName"),
				matcher.group("masterHostname"));
		}

		return null;
	}

	public static String getBuildDirPath(
		String buildNumber, String jobName, String masterHostname) {

		Properties buildProperties = null;

		try {
			buildProperties = getBuildProperties();
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}

		StringBuilder sb = new StringBuilder();

		sb.append(buildProperties.getProperty("jenkins.tmp.dir"));

		if (!isCINode() || isNullOrEmpty(buildNumber) ||
			isNullOrEmpty(jobName) || isNullOrEmpty(masterHostname)) {

			LocalDate localDate = LocalDate.now();

			sb.append(
				localDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));

			return sb.toString();
		}

		sb.append(masterHostname);
		sb.append("/");
		sb.append(
			jobName.replaceAll("([^/]+)/AXIS_VARIABLE=([^,]+)(,.*)?", "$1/$2"));
		sb.append("/");
		sb.append(buildNumber);

		return sb.toString();
	}

	public static String getBuildId(String topLevelBuildURL) {
		Matcher matcher = _buildURLPattern.matcher(topLevelBuildURL);

		matcher.find();

		StringBuilder sb = new StringBuilder();

		sb.append(matcher.group("cohortNumber"));

		String masterNumber = matcher.group("masterNumber");

		sb.append(String.format("%02d", Integer.parseInt(masterNumber)));

		Properties buildProperties = null;

		try {
			buildProperties = getBuildProperties(false);
		}
		catch (IOException ioException) {
			throw new RuntimeException(
				"Unable to get build.properties", ioException);
		}

		sb.append(
			buildProperties.getProperty(
				"job.id[" + matcher.group("jobName") + "]"));

		sb.append("_");
		sb.append(matcher.group("buildNumber"));

		return sb.toString();
	}

	public static String getBuildParameter(String buildURL, String key) {
		return getBuildParameter(buildURL, key, null);
	}

	public static String getBuildParameter(
		String buildURL, String key, Build parentBuild) {

		Map<String, String> buildParameters = getBuildParameters(
			buildURL, parentBuild);

		if (buildParameters.containsKey(key)) {
			return buildParameters.get(key);
		}

		throw new RuntimeException(
			"Unable to find build parameter '" + key + "' at " + buildURL);
	}

	public static Map<String, String> getBuildParameters(String buildURL) {
		return getBuildParameters(buildURL, null);
	}

	public static Map<String, String> getBuildParameters(
		String buildURL, Build parentBuild) {

		if (!buildURL.endsWith("/")) {
			buildURL += "/";
		}

		JSONObject jsonObject = null;

		if (parentBuild != null) {
			Matcher matcher = _buildURLPattern.matcher(buildURL);

			if (matcher.find()) {
				StringBuilder sb = new StringBuilder();

				sb.append(parentBuild.getArchiveRootDir());
				sb.append("/");
				sb.append(parentBuild.getArchiveName());
				sb.append("/");
				sb.append(matcher.group("masterHostname"));
				sb.append("/");
				sb.append(matcher.group("jobName"));
				sb.append("/");
				sb.append(matcher.group("buildNumber"));
				sb.append("/api/json");

				File file = new File(sb.toString());

				if (file.exists()) {
					try {
						jsonObject = new JSONObject(read(file));
					}
					catch (IOException | JSONException exception) {
					}
				}
			}
		}

		if (jsonObject == null) {
			String buildParametersURL = getLocalURL(
				buildURL + "api/json?tree=actions[parameters[name,value]]");

			try {
				jsonObject = toJSONObject(buildParametersURL);
			}
			catch (IOException ioException) {
				throw new RuntimeException(ioException);
			}
		}

		return JenkinsAPIUtil.getBuildParameters(jsonObject);
	}

	public static Properties getBuildProperties() throws IOException {
		return getBuildProperties(true);
	}

	public static Properties getBuildProperties(boolean checkCache)
		throws IOException {

		if (_getCacheURL() == null) {
			System.out.println("WARNING: Unable to get build properties");

			return new SecureProperties();
		}

		Properties properties = new SecureProperties();

		synchronized (_buildProperties) {
			if (checkCache && !_buildProperties.isEmpty()) {
				properties.putAll(_buildProperties);

				return properties;
			}

			if (_buildPropertiesURLs == null) {
				_buildPropertiesURLs = _getDefaultBuildPropertiesURLs();
			}

			Map<String, String> map = Environment.getAll();

			for (Map.Entry<String, String> entry : map.entrySet()) {
				properties.setProperty(entry.getKey(), entry.getValue());
				properties.setProperty(
					"env." + entry.getKey(), entry.getValue());
			}

			for (String url : _buildPropertiesURLs) {
				if (url.startsWith("file://")) {
					properties.putAll(new EnvironmentBuildProperties(url));

					continue;
				}

				properties.putAll(
					new EnvironmentBuildProperties(getLocalURL(url)));
			}

			String s3BucketName = Environment.get("S3_BUCKET_NAME");

			if (!isNullOrEmpty(s3BucketName)) {
				properties.setProperty(
					"env.S3_BUCKET_NAME", Environment.get("S3_BUCKET_NAME"));
			}

			if (!properties.containsKey("user.home")) {
				properties.setProperty(
					"user.home", getCanonicalPath(_userHomeDir));
			}

			_buildProperties.clear();

			_buildProperties.putAll(properties);
		}

		return new SecureProperties(properties);
	}

	public static String getBuildProperty(
			boolean checkCache, String propertyName)
		throws IOException {

		return getProperty(getBuildProperties(checkCache), propertyName);
	}

	public static String getBuildProperty(String propertyName)
		throws IOException {

		return getBuildProperty(true, propertyName);
	}

	public static String getBuildProperty(String propertyName, String... opts)
		throws IOException {

		return getProperty(getBuildProperties(true), propertyName, opts);
	}

	public static List<String> getBuildPropertyAsList(
			boolean checkCache, String key)
		throws IOException {

		String propertyContent = getProperty(
			getBuildProperties(checkCache), key);

		if (propertyContent == null) {
			return Collections.emptyList();
		}

		return Arrays.asList(propertyContent.split(","));
	}

	public static String getBuildURL(
		String jenkinsJobName, JenkinsMaster jenkinsMaster,
		long jenkinsQueueId) {

		if (isNullOrEmpty(jenkinsJobName) || (jenkinsMaster == null) ||
			(jenkinsQueueId < 0)) {

			return null;
		}

		Class<?> clazz = JenkinsResultsParserUtil.class;

		String script;

		try {
			script = readInputStream(
				clazz.getResourceAsStream("dependencies/get-build-url.groovy"));
		}
		catch (IOException ioException) {
			throw new RuntimeException(
				"Unable to load groovy script", ioException);
		}

		script = script.replace("${jenkinsJobName}", jenkinsJobName);
		script = script.replace(
			"${jenkinsQueueId}", String.valueOf(jenkinsQueueId));

		try {
			String response = executeJenkinsScript(
				jenkinsMaster.getName(), script, true);

			if (isURL(response)) {
				return response;
			}

			return null;
		}
		catch (Exception exception) {
			return null;
		}
	}

	public static String getBuildURLByBuildId(String buildId) {
		Matcher matcher = _buildIdPattern.matcher(buildId);

		matcher.find();

		StringBuilder sb = new StringBuilder();

		sb.append("https://test-");
		sb.append(matcher.group("cohortNumber"));
		sb.append("-");
		sb.append(Integer.parseInt(matcher.group("masterNumber")));
		sb.append(".liferay.com/job/");

		Properties buildProperties = null;

		try {
			buildProperties = getBuildProperties();
		}
		catch (IOException ioException) {
			throw new RuntimeException(
				"Unable to get build.properties", ioException);
		}

		String jobName = null;

		for (String propertyName : buildProperties.stringPropertyNames()) {
			if (propertyName.startsWith("job.id[")) {
				String propertyValue = buildProperties.getProperty(
					propertyName);

				if (propertyValue.equals(matcher.group("jobId"))) {
					jobName = propertyName.substring(
						7, propertyName.length() - 1);

					break;
				}
			}
		}

		sb.append(jobName);
		sb.append("/");
		sb.append(matcher.group("buildNumber"));

		return sb.toString();
	}

	public static String getBuildURLByJenkinsReportURL(
		String jenkinsReportURL) {

		Matcher matcher = _jenkinsReportURLPattern.matcher(jenkinsReportURL);

		if (!matcher.find()) {
			return jenkinsReportURL;
		}

		StringBuilder sb = new StringBuilder();

		sb.append(matcher.group("masterURL"));
		sb.append("job/");
		sb.append(matcher.group("jobName"));
		sb.append("/");
		sb.append(matcher.group("buildNumber"));

		return sb.toString();
	}

	public static BufferedReader getCachedFileBufferedReader(String key) {
		File cachedTextFile = getCacheFile(key);

		if (!cachedTextFile.exists()) {
			return null;
		}

		try {
			return Files.newBufferedReader(
				Paths.get(cachedTextFile.toURI()), StandardCharsets.UTF_8);
		}
		catch (IOException ioException) {
			throw new RuntimeException(
				"Unable to get buffered reader for " + cachedTextFile.getPath(),
				ioException);
		}
	}

	public static String getCachedText(String key) {
		File cachedTextFile = getCacheFile(key);

		if (!cachedTextFile.exists()) {
			return null;
		}

		try {
			return read(cachedTextFile);
		}
		catch (IOException ioException) {
			return null;
		}
	}

	public static File getCacheFile(String key) {
		String fileName = combine(
			System.getProperty("java.io.tmpdir"), "/jenkins-cached-files/",
			String.valueOf(key.hashCode()), ".txt");

		return new File(fileName);
	}

	public static String getCacheFileKey(String urlString, String postContent) {
		String key = fixURL(urlString);

		key.replace("//", "/");

		if (!isNullOrEmpty(postContent)) {
			key += postContent;
		}

		return key;
	}

	public static long getCacheFileSize(String key) {
		File cacheFile = getCacheFile(key);

		if ((cacheFile == null) || !cacheFile.exists()) {
			return 0;
		}

		return cacheFile.length();
	}

	public static File getCanonicalFile(File file) {
		return new File(getCanonicalPath(file));
	}

	public static String getCanonicalPath(File file) {
		File canonicalFile = null;

		try {
			canonicalFile = file.getCanonicalFile();
		}
		catch (IOException ioException) {
			throw new RuntimeException(
				"Unable to get canonical file", ioException);
		}

		return _getCanonicalPath(canonicalFile);
	}

	public static String getCIProperty(
		String branchName, String key, String repositoryName) {

		List<String> ciPropertyURLs = new ArrayList<>();

		if (repositoryName.startsWith("com-liferay-")) {
			StringBuilder sb = new StringBuilder();

			sb.append("https://raw.githubusercontent.com/liferay");
			sb.append("/liferay-portal");

			if (!branchName.equals("master")) {
				sb.append("-ee");
			}

			sb.append("/");
			sb.append(branchName);
			sb.append("/ci.properties");

			ciPropertyURLs.add(sb.toString());
		}

		StringBuilder sb = new StringBuilder();

		sb.append("https://raw.githubusercontent.com/liferay/");
		sb.append(repositoryName);
		sb.append("/");
		sb.append(branchName);
		sb.append("/ci.properties");

		ciPropertyURLs.add(sb.toString());

		Properties ciProperties = new SecureProperties();

		for (String ciPropertyURL : ciPropertyURLs) {
			try {
				String ciPropertiesString = toString(ciPropertyURL, true);

				ciProperties.load(new StringReader(ciPropertiesString));
			}
			catch (IOException ioException) {
				System.out.println(
					"Unable to load ci.properties from " + ciPropertyURL);
			}
		}

		return getProperty(ciProperties, key);
	}

	public static String getCohortName() {
		String jenkinsURL = Environment.get("JENKINS_URL");

		return getCohortName(jenkinsURL);
	}

	public static String getCohortName(String masterHostname) {
		if (masterHostname == null) {
			return null;
		}

		Matcher matcher = _jenkinsMasterPattern.matcher(masterHostname);

		if (!matcher.find()) {
			return null;
		}

		return matcher.group("cohortName");
	}

	public static long getCurrentTimeMillis() {
		if (!isCINode() || isJenkinsMaster()) {
			return System.currentTimeMillis();
		}

		if (_currentTimeMillisDelta == null) {
			Long remoteCurrentTimeSeconds = null;

			int retry = 0;

			while ((remoteCurrentTimeSeconds == null) && (retry < 3)) {
				retry++;

				remoteCurrentTimeSeconds = getRemoteCurrentTimeSeconds(
					getJenkinsMasterName(getHostName(null)));

				if ((remoteCurrentTimeSeconds == null) && (retry < 3)) {
					sleep(1000);
				}
			}

			if (remoteCurrentTimeSeconds != null) {
				long remoteCurrentTimeMillis = remoteCurrentTimeSeconds * 1000;

				_currentTimeMillisDelta =
					System.currentTimeMillis() - remoteCurrentTimeMillis;
			}
		}

		if (_currentTimeMillisDelta == null) {
			return System.currentTimeMillis();
		}

		return System.currentTimeMillis() - _currentTimeMillisDelta;
	}

	public static String[] getDateStrings(
		long durationDays, LocalDate localDate) {

		String[] dateStrings = new String[(int)durationDays];

		for (int i = 0; i < durationDays; i++) {
			String dateString = localDate.format(
				DateTimeFormatter.ofPattern("yyyyMMdd"));

			dateStrings[i] = dateString;

			localDate = localDate.plusDays(1);
		}

		return dateStrings;
	}

	public static String[] getDateStrings(long startTime, long duration) {
		long durationDays = TimeUnit.MILLISECONDS.toDays(duration);

		return getDateStrings(durationDays, getLocalDate(startTime));
	}

	public static List<File> getDirectoriesContainingFiles(
		List<File> directories, List<File> files) {

		List<File> directoriesContainingFiles = new ArrayList<>(
			directories.size());

		for (File directory : directories) {
			if (!directory.isDirectory()) {
				continue;
			}

			boolean containsFile = false;

			for (File file : files) {
				if (isFileInDirectory(directory, file)) {
					containsFile = true;

					break;
				}
			}

			if (containsFile) {
				directoriesContainingFiles.add(directory);
			}
		}

		return directoriesContainingFiles;
	}

	public static String getDistinctTimeStamp() {
		while (true) {
			String timeStamp = String.valueOf(getCurrentTimeMillis());

			synchronized (_timeStamps) {
				if (_timeStamps.contains(timeStamp)) {
					continue;
				}

				_timeStamps.add(timeStamp);
			}

			return timeStamp;
		}
	}

	public static String getDistPortalBundlesBuildURL(String portalBranchName) {
		int lastCompletedBuildNumber =
			JenkinsAPIUtil.getLastCompletedBuildNumber(
				_getDistPortalJobURL(portalBranchName));

		Pattern distPortalBundleFileNamesPattern =
			_getDistPortalBundleFileNamesPattern(portalBranchName);

		int buildNumber = lastCompletedBuildNumber + 1;

		while (buildNumber > Math.max(0, lastCompletedBuildNumber - 10)) {
			String distPortalBundlesBuildURL = combine(
				_getDistPortalBundlesURL(portalBranchName), "/",
				String.valueOf(buildNumber), "/");

			try {
				Matcher matcher = distPortalBundleFileNamesPattern.matcher(
					toString(distPortalBundlesBuildURL, false));

				if (matcher.find()) {
					return distPortalBundlesBuildURL;
				}
			}
			catch (IOException ioException) {
				System.out.println("WARNING: " + ioException.getMessage());
			}

			buildNumber--;
		}

		return null;
	}

	public static String getDockerImageName(File dockerFile) {
		if ((dockerFile == null) || !dockerFile.exists()) {
			return null;
		}

		String dockerFileContent;

		try {
			dockerFileContent = read(dockerFile);
		}
		catch (IOException ioException) {
			return null;
		}

		if (isNullOrEmpty(dockerFileContent)) {
			return null;
		}

		Matcher dockerFileMatcher = _dockerFilePattern.matcher(
			dockerFileContent);

		if (!dockerFileMatcher.find()) {
			return null;
		}

		return dockerFileMatcher.group("dockerImageName");
	}

	public static String getEnvironmentVariable(
		String environmentVariableName) {

		String environmentVariableValue = Environment.get(
			environmentVariableName);

		if ((environmentVariableValue == null) ||
			environmentVariableValue.isEmpty()) {

			throw new RuntimeException(
				combine(
					"Unable to find required environment variable \'",
					environmentVariableName, "\'"));
		}

		return environmentVariableValue;
	}

	public static List<File> getExcludedFiles(
		List<PathMatcher> excludesPathMatchers, List<File> files) {

		List<File> excludedFiles = new ArrayList<>(files.size());

		for (File file : files) {
			if (isFileExcluded(excludesPathMatchers, file)) {
				excludedFiles.add(file);
			}
		}

		return excludedFiles;
	}

	public static File getFileFromPathSnippet(
		File baseDir, final String pathSnippet) {

		baseDir = getCanonicalFile(baseDir);

		try {
			Process process = executeBashCommands(
				baseDir, combine("git ls-files | grep \"", pathSnippet, "\""));

			String output = readInputStream(process.getInputStream());

			if (!isNullOrEmpty(output)) {
				for (String line : output.split("\n")) {
					if (isNullOrEmpty(line)) {
						continue;
					}

					File file = new File(baseDir, line);

					if (!file.exists()) {
						continue;
					}

					return file;
				}
			}
		}
		catch (Exception exception) {
			System.out.println(exception.getMessage());
		}

		final List<File> matchingFiles = new ArrayList<>();

		try {
			Files.walkFileTree(
				baseDir.toPath(),
				new SimpleFileVisitor<Path>() {

					@Override
					public FileVisitResult visitFile(
							Path filePath,
							BasicFileAttributes basicFileAttributes)
						throws IOException {

						String filePathString = filePath.toString();

						if (filePathString.contains(pathSnippet) ||
							filePathString.contains(
								pathSnippet.replaceAll("\\.", "/"))) {

							matchingFiles.add(filePath.toFile());
						}

						return FileVisitResult.CONTINUE;
					}

				});
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}

		if (matchingFiles.isEmpty()) {
			return null;
		}

		return matchingFiles.get(0);
	}

	public static String getGitDirectoryName(
		String repositoryName, String upstreamBranchName) {

		String targetGitDirectoryName = _getGitDirectoryName(
			repositoryName, upstreamBranchName,
			_getGitWorkingDirectoriesJSONArray());

		if (targetGitDirectoryName == null) {
			targetGitDirectoryName = _getGitDirectoryName(
				repositoryName, upstreamBranchName,
				_getGitDirectoriesJSONArray());
		}

		if (targetGitDirectoryName == null) {
			return repositoryName;
		}

		return targetGitDirectoryName;
	}

	public static String getGitHubAPIRateLimitStatusMessage() {
		try {
			JSONObject jsonObject = toJSONObject(
				"https://api.github.com/rate_limit");

			jsonObject = jsonObject.getJSONObject("rate");

			return getGitHubAPIRateLimitStatusMessage(
				jsonObject.getInt("limit"), jsonObject.getInt("remaining"),
				jsonObject.getLong("reset"));
		}
		catch (Exception exception) {
			System.out.println("Unable to get GitHub API rate limit");
		}

		return "";
	}

	public static String getGitHubAPIRateLimitStatusMessage(
		int limit, int remaining, long reset) {

		StringBuilder sb = new StringBuilder();

		sb.append(remaining);
		sb.append(" GitHub API calls out of ");
		sb.append(limit);
		sb.append(" remain. GitHub API call limit will reset in ");
		sb.append(toDurationString((1000 * reset) - getCurrentTimeMillis()));
		sb.append(".");

		return sb.toString();
	}

	public static String getGitHubApiSearchUrl(List<String> filters) {
		return combine(
			"https://api.github.com/search/issues?q=", join("+", filters));
	}

	public static String getGitHubAPIURL(
		String gitRepositoryName, String username, String path) {

		return combine(
			"https://api.github.com/repos/", username, "/", gitRepositoryName,
			"/", path.replaceFirst("^/*", ""));
	}

	public static List<String> getGitHubCacheHostnames() {
		if (isCloudCINode()) {
			return Collections.emptyList();
		}

		try {
			Properties buildProperties = getBuildProperties();

			String gitHubCacheHostnames = buildProperties.getProperty(
				"github.cache.hostnames");

			String cohortName = getCohortName();

			if ((cohortName != null) &&
				buildProperties.containsKey(
					"github.cache.hostnames[" + cohortName + "]")) {

				gitHubCacheHostnames = buildProperties.getProperty(
					"github.cache.hostnames[" + cohortName + "]");
			}

			return Lists.newArrayList(gitHubCacheHostnames.split(","));
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}
	}

	public static DateFormat getGitHubDateFormat() {
		return _gitHubDateFormat;
	}

	public static String getGitRepositoryName(String gitDirectoryName) {
		JSONObject gitDirectoryJSONObject = _getGitDirectoryJSONObject(
			gitDirectoryName);

		if (gitDirectoryJSONObject == null) {
			return null;
		}

		return gitDirectoryJSONObject.getString("repository");
	}

	public static String getGitUpstreamBranchName(String gitDirectoryName) {
		JSONObject gitDirectoryJSONObject = _getGitDirectoryJSONObject(
			gitDirectoryName);

		if (gitDirectoryJSONObject == null) {
			return null;
		}

		return gitDirectoryJSONObject.getString("branch");
	}

	public static File getGitWorkingDir(File file) {
		if (file == null) {
			return null;
		}

		File canonicalFile = getCanonicalFile(file);

		File parentFile = canonicalFile.getParentFile();

		if ((parentFile == null) || !parentFile.exists()) {
			return file;
		}

		if (!canonicalFile.isDirectory()) {
			return getGitWorkingDir(parentFile);
		}

		File gitDir = new File(canonicalFile, ".git");

		if (!gitDir.exists()) {
			return getGitWorkingDir(parentFile);
		}

		return canonicalFile;
	}

	public static String[] getGlobsFromProperty(String globProperty) {
		List<String> curlyBraceExpansionList = new ArrayList<>();

		Matcher curlyBraceMatcher = _curlyBraceExpansionPattern.matcher(
			globProperty);

		while (curlyBraceMatcher.find()) {
			int index = curlyBraceExpansionList.size();

			String value = curlyBraceMatcher.group();

			curlyBraceExpansionList.add(value);

			globProperty = globProperty.replace(
				value, combine("${", String.valueOf(index), "}"));
		}

		List<String> globs = new ArrayList<>();

		for (String tempGlob : globProperty.split(",")) {
			Matcher matcher = _nestedPropertyPattern.matcher(tempGlob);

			String glob = tempGlob;

			while (matcher.find()) {
				Integer index = Integer.parseInt(matcher.group(1));

				glob = glob.replace(
					matcher.group(), curlyBraceExpansionList.get(index));
			}

			globs.add(glob);
		}

		return globs.toArray(new String[0]);
	}

	public static String getHeadersString(URLConnection urlConnection) {
		Map<String, List<String>> headersMap = urlConnection.getHeaderFields();

		StringBuilder sb = new StringBuilder();

		for (Map.Entry<String, List<String>> entry : headersMap.entrySet()) {
			sb.append(entry.getKey());
			sb.append(": ");

			sb.append(join(", ", entry.getValue()));
			sb.append("\n");
		}

		sb.append("\n");

		return sb.toString();
	}

	public static String getHostIPAddress() {
		try {
			InetAddress inetAddress = InetAddress.getLocalHost();

			return inetAddress.getHostAddress();
		}
		catch (UnknownHostException unknownHostException) {
			return "127.0.0.1";
		}
	}

	public static String getHostName(String defaultHostName) {
		try {
			InetAddress inetAddress = InetAddress.getLocalHost();

			String hostName = inetAddress.getHostName();

			if (hostName.matches("\\d+-\\d+-\\d+-\\d+")) {
				hostName = "cloud-" + hostName;
			}

			return hostName;
		}
		catch (UnknownHostException unknownHostException) {
			return defaultHostName;
		}
	}

	public static List<File> getIncludedFiles(
		File basedir, String[] excludes, String[] includes) {

		if (includes == null) {
			return new ArrayList<>();
		}

		final List<PathMatcher> excludesPathMatchers = new ArrayList<>();

		if ((excludes != null) && (excludes.length > 0)) {
			excludesPathMatchers.addAll(toPathMatchers(null, excludes));
		}

		final List<PathMatcher> includesPathMatchers = toPathMatchers(
			null, includes);

		final List<File> includedFiles = new ArrayList<>();

		try {
			Files.walkFileTree(
				basedir.toPath(),
				new SimpleFileVisitor<Path>() {

					@Override
					public FileVisitResult visitFile(
							Path filePath,
							BasicFileAttributes basicFileAttributes)
						throws IOException {

						for (PathMatcher pathMatcher : excludesPathMatchers) {
							if (pathMatcher.matches(filePath)) {
								return FileVisitResult.CONTINUE;
							}
						}

						for (PathMatcher pathMatcher : includesPathMatchers) {
							if (pathMatcher.matches(filePath)) {
								includedFiles.add(filePath.toFile());

								break;
							}
						}

						return FileVisitResult.CONTINUE;
					}

				});
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}

		return includedFiles;
	}

	public static List<File> getIncludedFiles(
		List<PathMatcher> excludesPathMatchers,
		List<PathMatcher> includesPathMatchers, List<File> files) {

		List<File> includedFiles = new ArrayList<>(files.size());

		for (File file : files) {
			if (isFileIncluded(
					excludesPathMatchers, includesPathMatchers, file)) {

				includedFiles.add(file);
			}
		}

		return includedFiles;
	}

	public static List<URL> getIncludedResourceURLs(
			String[] resourceIncludesRelativeGlobs, File rootDir)
		throws IOException {

		final List<PathMatcher> pathMatchers = toPathMatchers(
			getCanonicalPath(rootDir) + File.separator,
			resourceIncludesRelativeGlobs);

		Path rootDirPath = rootDir.toPath();

		if (!Files.exists(rootDirPath)) {
			System.out.println(
				combine(
					"Directory ", rootDirPath.toString(), " does not exist."));

			return Collections.emptyList();
		}

		final List<URL> includedResourceURLs = new ArrayList<>();

		Files.walkFileTree(
			rootDirPath,
			new SimpleFileVisitor<Path>() {

				@Override
				public FileVisitResult visitFile(
						Path filePath, BasicFileAttributes basicFileAttributes)
					throws IOException {

					for (PathMatcher pathMatcher : pathMatchers) {
						if (pathMatcher.matches(filePath)) {
							URI uri = filePath.toUri();

							includedResourceURLs.add(uri.toURL());

							break;
						}
					}

					return FileVisitResult.CONTINUE;
				}

			});

		return includedResourceURLs;
	}

	public static float getJavaVersionNumber() {
		Matcher matcher = _javaVersionPattern.matcher(
			System.getProperty("java.version"));

		if (!matcher.find()) {
			throw new RuntimeException(
				"Unable to determine Java version number");
		}

		return Float.parseFloat(matcher.group(1));
	}

	public static Properties getJenkinsBuildProperties() {
		try {
			return getBuildProperties();
		}
		catch (IOException ioException) {
			throw new RuntimeException(
				"Unable to get build properties", ioException);
		}
	}

	public static long getJenkinsBuildQueueId(String location) {
		if (isNullOrEmpty(location)) {
			return 0L;
		}

		Matcher jenkinsBuildQueueURLMatcher =
			_jenkinsBuildQueueURLPattern.matcher(location);

		if (!jenkinsBuildQueueURLMatcher.find()) {
			return 0L;
		}

		return Long.parseLong(jenkinsBuildQueueURLMatcher.group("queueId"));
	}

	public static String getJenkinsBuildResult(String buildURL) {
		try {
			JSONObject jsonObject = toJSONObject(
				buildURL + "/api/json?tree=result", false);

			if (!jsonObject.has("result") || jsonObject.isNull("result")) {
				return null;
			}

			return jsonObject.optString("result");
		}
		catch (IOException ioException) {
			ioException.printStackTrace();

			return "ABORTED";
		}
	}

	public static JenkinsCohort getJenkinsCohort() {
		return JenkinsCohort.getInstance(getCohortName());
	}

	public static String getJenkinsDistRootPath() {
		try {
			String filePath = getBuildProperty("jenkins.dist.root.path");

			if (!isNullOrEmpty(filePath)) {
				return filePath;
			}
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}

		return _JENKINS_DIST_ROOT_PATH_DEFAULT;
	}

	public static String getJenkinsGitHubURL() {
		try {
			String jenkinsGitHubURL = getBuildProperty("jenkins.github.url");

			if (isURL(jenkinsGitHubURL)) {
				return jenkinsGitHubURL;
			}
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}

		return _URL_JENKINS_GITHUB_DEFAULT;
	}

	public static HTTPAuthorization getJenkinsHTTPAuthorization()
		throws IOException {

		return new BasicHTTPAuthorization(
			getBuildProperty("jenkins.admin.user.token"),
			getBuildProperty("jenkins.admin.user.name"));
	}

	public static String getJenkinsLoadBalancerURL() {
		try {
			String jenkinsLoadBalancerURL = getBuildProperty(
				"jenkins.load.balancer.url");

			if (isURL(jenkinsLoadBalancerURL)) {
				return jenkinsLoadBalancerURL;
			}
		}
		catch (IOException ioException) {
			throw new RuntimeException(
				"Unable to get build properties", ioException);
		}

		return _URL_LOAD_BALANCER_DEFAULT;
	}

	public static JenkinsMaster getJenkinsMaster(URL buildURL) {
		Matcher matcher = _buildURLPattern.matcher(String.valueOf(buildURL));

		if (!matcher.find()) {
			throw new RuntimeException("Invalid Build URL");
		}

		return JenkinsMaster.getInstance(matcher.group("masterHostname"));
	}

	public static String getJenkinsMasterName(String jenkinsSlaveName) {
		jenkinsSlaveName = jenkinsSlaveName.replaceAll("([^\\.]+).*", "$1");

		if (jenkinsSlaveName.matches("test-\\d{1,2}-\\d{1,2}")) {
			return jenkinsSlaveName;
		}

		Map<String, List<String>> jenkinsNodeMap = getJenkinsNodeMap();

		if (jenkinsNodeMap != null) {
			for (Map.Entry<String, List<String>> entry :
					jenkinsNodeMap.entrySet()) {

				List<String> jenkinsSlaveNames = entry.getValue();

				if (jenkinsSlaveNames.contains(jenkinsSlaveName)) {
					return entry.getKey();
				}
			}
		}

		StringBuilder sb = new StringBuilder();

		sb.append("Unable to get Jenkins master name for slave ");
		sb.append(jenkinsSlaveName);
		sb.append(".\n");
		sb.append("Jenkins Node Map:\n");

		if (jenkinsNodeMap != null) {
			for (Map.Entry<String, List<String>> entry :
					jenkinsNodeMap.entrySet()) {

				sb.append(entry.getKey());
				sb.append("\n    ");

				for (String jenkinsNodeName : entry.getValue()) {
					sb.append(" ");
					sb.append(jenkinsNodeName);
				}

				sb.append("\n");
			}
		}

		NotificationUtil.sendEmail(
			sb.toString(), "jenkins", "Unable to get Jenkins master name",
			"peter.yoo@liferay.com");

		return null;
	}

	public static List<JenkinsMaster> getJenkinsMasters(
		Properties buildProperties, int minimumRAM, int maximumSlavesPerHost,
		String cohortName) {

		return getJenkinsMasters(
			buildProperties, minimumRAM, maximumSlavesPerHost, cohortName,
			null);
	}

	public static List<JenkinsMaster> getJenkinsMasters(
		Properties buildProperties, int minimumRAM, int maximumSlavesPerHost,
		String cohortName, String networkName) {

		List<JenkinsMaster> jenkinsMasters = new ArrayList<>();

		Pattern pattern = Pattern.compile(
			"master\\.property\\((?<jenkinsMasterName>" + cohortName +
				"-\\d+)/executors.size\\)");

		for (String buildPropertyName : buildProperties.stringPropertyNames()) {
			Matcher matcher = pattern.matcher(buildPropertyName);

			if (!matcher.matches()) {
				continue;
			}

			JenkinsMaster jenkinsMaster = JenkinsMaster.getInstance(
				matcher.group("jenkinsMasterName"));

			if (jenkinsMaster.isBlackListed() ||
				(jenkinsMaster.getSlaveRAM() < minimumRAM) ||
				(jenkinsMaster.getSlavesPerHost() > maximumSlavesPerHost)) {

				continue;
			}

			if (isNullOrEmpty(networkName)) {
				jenkinsMasters.add(jenkinsMaster);

				continue;
			}

			if (!Objects.equals(jenkinsMaster.getNetworkName(), networkName)) {
				continue;
			}

			jenkinsMasters.add(jenkinsMaster);
		}

		return jenkinsMasters;
	}

	public static Map<String, List<String>> getJenkinsNodeMap() {
		Retryable<Map<String, List<String>>> retryable =
			new Retryable<Map<String, List<String>>>(false, 2, 10, true) {

				@Override
				public Map<String, List<String>> execute() {
					Properties buildProperties = null;

					try {
						buildProperties = getBuildProperties(_checkCache);
					}
					catch (IOException ioException) {
						throw new RuntimeException(
							"Unable to get build properties", ioException);
					}

					Map<String, List<String>> jenkinsNodeMap = new HashMap<>();

					for (Object propertyName : buildProperties.keySet()) {
						Matcher jenkinsSlavesPropertyNameMatcher =
							_jenkinsSlavesPropertyNamePattern.matcher(
								propertyName.toString());

						if (jenkinsSlavesPropertyNameMatcher.matches()) {
							String jenkinsMasterName =
								jenkinsSlavesPropertyNameMatcher.group(1);

							List<String> jenkinsSlaveNames = getSlaves(
								buildProperties, jenkinsMasterName, null,
								false);

							if (jenkinsSlaveNames.isEmpty()) {
								continue;
							}

							jenkinsNodeMap.put(
								jenkinsMasterName, jenkinsSlaveNames);
						}
					}

					if (jenkinsNodeMap.isEmpty()) {
						System.out.println(
							combine(
								"Jenkins slave name mapping properties could ",
								"not be found. Build properties URLs will be ",
								"reverted to their default values."));

						setBuildProperties(_getDefaultBuildPropertiesURLs());

						_checkCache = false;

						throw new RuntimeException(
							"Unable to load Jenkins node map");
					}

					return jenkinsNodeMap;
				}

				private boolean _checkCache = true;

			};

		return retryable.executeWithRetries();
	}

	public static List<String> getJenkinsNodes() {
		Map<String, List<String>> jenkinsNodeMap = getJenkinsNodeMap();

		Set<String> jenkinsNodes = new TreeSet<>(jenkinsNodeMap.keySet());

		for (List<String> jenkinsSlaves : jenkinsNodeMap.values()) {
			jenkinsNodes.addAll(jenkinsSlaves);
		}

		return new ArrayList<>(jenkinsNodes);
	}

	public static Properties getJenkinsProperties() throws IOException {
		Properties properties = new Properties();

		if ((_jenkinsProperties != null) && !_jenkinsProperties.isEmpty()) {
			properties.putAll(_jenkinsProperties);

			return properties;
		}

		String url = _getJenkinsRepositoryURL() + "/jenkins.properties";

		properties.load(new StringReader(toString(getLocalURL(url), false)));

		LocalGitRepository localGitRepository =
			GitRepositoryFactory.getLocalGitRepository(
				"liferay-jenkins-ee", "master");

		File jenkinsPropertiesFile = new File(
			localGitRepository.getDirectory(), "jenkins.properties");

		if (jenkinsPropertiesFile.exists()) {
			properties.putAll(getProperties(jenkinsPropertiesFile));
		}

		_jenkinsProperties = new SecureProperties(properties);

		return (Properties)_jenkinsProperties;
	}

	public static File getJenkinsRepositoryDir() {
		String cacheDirPath = Environment.get("CACHE_DIR");

		if (cacheDirPath == null) {
			cacheDirPath = "/opt/dev/projects/github";
		}

		return new File(cacheDirPath, JENKINS_REPOSITORY_NAME);
	}

	public static String getJenkinsTempMapURL() {
		try {
			String jenkinsOSBJenkinsWebURL = getBuildProperty(
				"jenkins.osb.jenkins.web.url");

			if (isURL(jenkinsOSBJenkinsWebURL)) {
				return jenkinsOSBJenkinsWebURL + "/map";
			}
		}
		catch (IOException ioException) {
			throw new RuntimeException(
				"Unable to get build properties", ioException);
		}

		return _URL_TEMP_MAP_DEFAULT;
	}

	public static Document getJobConfigDocument(
			JenkinsMaster jenkinsMaster, String jobName)
		throws DocumentException, IOException {

		String url = combine(
			jenkinsMaster.getURL(), "/job/", jobName, "/config.xml");

		return Dom4JUtil.parse(toString(url));
	}

	public static int getJobTimeoutMinutes(
		JenkinsMaster jenkinsMaster, String jobName) {

		if (jenkinsMaster.isBlackListed()) {
			jenkinsMaster = JenkinsMaster.getInstance(
				Environment.get("MASTER_HOSTNAME"));
		}

		String key = jenkinsMaster.getName() + "_" + jobName;

		synchronized (_jobTimeoutMinutes) {
			Integer jobTimeoutMinute = _jobTimeoutMinutes.get(key);

			if (jobTimeoutMinute != null) {
				return jobTimeoutMinute;
			}

			Document configDocument;

			try {
				configDocument = getJobConfigDocument(jenkinsMaster, jobName);

				Node timeoutNode = Dom4JUtil.getNodeByXPath(
					configDocument,
					combine(
						"/project/buildWrappers/",
						"hudson.plugins.build__timeout.BuildTimeoutWrapper/",
						"strategy[@class=\'hudson.plugins.build_timeout.impl.",
						"AbsoluteTimeOutStrategy\']/timeoutMinutes"));

				jobTimeoutMinute = Integer.valueOf(timeoutNode.getText()) + 15;
			}
			catch (Exception exception) {
				System.out.println("Unable to get timeout of job " + jobName);

				try {
					jobTimeoutMinute = Integer.valueOf(
						getBuildProperty("build.default.timeout.minutes"));
				}
				catch (IOException ioException) {
					jobTimeoutMinute = 135;
				}
			}
			finally {
				_jobTimeoutMinutes.put(key, jobTimeoutMinute);

				return jobTimeoutMinute;
			}
		}
	}

	public static String getJobVariant(JSONObject jsonObject) {
		JSONArray actionsJSONArray = jsonObject.getJSONArray("actions");

		for (int i = 0; i < actionsJSONArray.length(); i++) {
			Object object = actionsJSONArray.get(i);

			if (object.equals(JSONObject.NULL)) {
				continue;
			}

			JSONObject actionsJSONObject = actionsJSONArray.getJSONObject(i);

			if (actionsJSONObject.has("parameters")) {
				JSONArray parametersJSONArray = actionsJSONObject.getJSONArray(
					"parameters");

				for (int j = 0; j < parametersJSONArray.length(); j++) {
					JSONObject parametersJSONObject =
						parametersJSONArray.getJSONObject(j);

					if ("JOB_VARIANT".contains(
							parametersJSONObject.getString("name"))) {

						return parametersJSONObject.getString("value");
					}
				}
			}
		}

		return "";
	}

	public static String getJobVariant(String json) {
		return getJobVariant(new JSONObject(json));
	}

	public static LocalDate getLocalDate(Date date) {
		Instant instant = date.toInstant();

		ZonedDateTime zonedDateTime = instant.atZone(ZoneId.systemDefault());

		return zonedDateTime.toLocalDate();
	}

	public static LocalDate getLocalDate(long milliseconds) {
		Instant instant = Instant.ofEpochMilli(milliseconds);

		ZonedDateTime zonedDateTime = instant.atZone(ZoneId.systemDefault());

		return zonedDateTime.toLocalDate();
	}

	public static LocalDateTime getLocalDateTime(Date date) {
		Instant instant = date.toInstant();

		ZonedDateTime zonedDateTime = instant.atZone(ZoneId.systemDefault());

		return zonedDateTime.toLocalDateTime();
	}

	public static Properties getLocalLiferayJenkinsEEBuildProperties() {
		Properties buildProperties = null;

		try {
			buildProperties = getBuildProperties();
		}
		catch (IOException ioException) {
			throw new RuntimeException(
				"Unable to get build properties", ioException);
		}

		File localLiferayJenkinsEEBuildPropertiesFile = new File(
			buildProperties.getProperty("base.repository.dir"),
			combine(
				"liferay-jenkins-ee", File.separator, "commands",
				File.separator, "build.properties"));

		return getProperties(localLiferayJenkinsEEBuildPropertiesFile);
	}

	public static String getLocalURL(String remoteURL) {
		if (remoteURL.contains(Build.DEPENDENCIES_URL_TOKEN)) {
			remoteURL = fixFileName(remoteURL);

			String fileURL = remoteURL.replace(
				Build.DEPENDENCIES_URL_TOKEN, urlDependenciesFile);

			File file = new File(fileURL.substring("file:".length()));

			if (file.exists()) {
				remoteURL = fileURL;
			}
		}

		if (remoteURL.startsWith("file")) {
			remoteURL = fixFileName(remoteURL);
		}

		String localURL = remoteURL;
		String localURLQueryString = "";

		int x = remoteURL.indexOf("?");

		if (x != -1) {
			localURL = remoteURL.substring(0, x);
			localURLQueryString = remoteURL.substring(x);
		}

		Matcher matcher = _remoteURLAuthorityPattern1.matcher(localURL);

		if (isCloudCINode() && matcher.find()) {
			String localURLAuthority = combine(
				"http://", matcher.group(1), "/");
			String remoteURLAuthority = matcher.group(0);

			localURL = localURL.replaceAll(
				remoteURLAuthority, localURLAuthority);

			return localURL + localURLQueryString;
		}

		matcher = _remoteURLAuthorityPattern2.matcher(localURL);

		if (matcher.find()) {
			String localURLAuthority = combine(
				"http://", matcher.group(1), "/");
			String remoteURLAuthority = matcher.group(0);

			localURL = localURL.replaceAll(
				remoteURLAuthority, localURLAuthority);

			return localURL + localURLQueryString;
		}

		matcher = _remoteURLAuthorityPattern3.matcher(localURL);

		if (matcher.find()) {
			String localURLAuthority = combine(
				"http://mirrors.lax.liferay.com/", matcher.group(2));
			String remoteURLAuthority = matcher.group(0);

			localURL = localURL.replaceAll(
				remoteURLAuthority, localURLAuthority);

			return localURL + localURLQueryString;
		}

		return localURL + localURLQueryString;
	}

	public static long getMillis(LocalDateTime localDateTime) {
		ZonedDateTime zonedDateTime = ZonedDateTime.of(
			localDateTime, ZoneId.systemDefault());

		Instant instant = zonedDateTime.toInstant();

		return instant.toEpochMilli();
	}

	public static JenkinsMaster getMostAvailableJenkinsMaster(
		String baseInvocationURL, int invokedBatchSize) {

		String mostAvailableMasterURL = getMostAvailableMasterURL(
			baseInvocationURL, null, invokedBatchSize);

		return JenkinsMaster.getInstance(
			mostAvailableMasterURL.replaceAll("http://(.+)", "$1"));
	}

	public static JenkinsMaster getMostAvailableJenkinsMaster(
		String baseInvocationURL, String blacklist, int invokedBatchSize) {

		String mostAvailableMasterURL = getMostAvailableMasterURL(
			baseInvocationURL, blacklist, invokedBatchSize);

		return JenkinsMaster.getInstance(
			mostAvailableMasterURL.replaceAll("http://(.+)", "$1"));
	}

	public static String getMostAvailableMasterURL(
		String baseInvocationURL, int invokedBatchSize) {

		return getMostAvailableMasterURL(
			baseInvocationURL, null, invokedBatchSize);
	}

	public static String getMostAvailableMasterURL(
		String baseInvocationURL, String blacklist, int invokedBatchSize) {

		return getMostAvailableMasterURL(
			baseInvocationURL, blacklist, invokedBatchSize, null);
	}

	public static String getMostAvailableMasterURL(
		String baseInvocationURL, String blacklist, int invokedBatchSize,
		String jobName) {

		StringBuilder sb = new StringBuilder();

		sb.append(getJenkinsLoadBalancerURL());
		sb.append("?baseInvocationURL=");
		sb.append(baseInvocationURL);

		if (blacklist != null) {
			sb.append("&blacklist=");
			sb.append(blacklist);
		}

		if (invokedBatchSize > 0) {
			sb.append("&invokedJobBatchSize=");
			sb.append(invokedBatchSize);
		}

		if (!isNullOrEmpty(jobName)) {
			sb.append("&jobName=");
			sb.append(fixURL(jobName));
		}

		try {
			JSONObject jsonObject = toJSONObject(sb.toString(), false);

			return jsonObject.getString("mostAvailableMasterURL");
		}
		catch (IOException | JSONException exception) {
			System.out.println("WARNING: " + exception.getMessage());

			Properties buildProperties = null;

			try {
				buildProperties = getBuildProperties(false);
			}
			catch (IOException ioException) {
				throw new RuntimeException(
					"Unable to get build properties", ioException);
			}

			List<JenkinsMaster> availableJenkinsMasters =
				LoadBalancerUtil.getAvailableJenkinsMasters(
					blacklist,
					LoadBalancerUtil.getMasterPrefix(baseInvocationURL),
					buildProperties, true);

			Random random = new Random(getCurrentTimeMillis());

			JenkinsMaster randomJenkinsMaster = availableJenkinsMasters.get(
				random.nextInt(availableJenkinsMasters.size()));

			return "http://" + randomJenkinsMaster.getName();
		}
	}

	public static ThreadPoolExecutor getNewThreadPoolExecutor(
		int maximumPoolSize, boolean autoShutDown) {

		ThreadPoolExecutor threadPoolExecutor =
			(ThreadPoolExecutor)Executors.newFixedThreadPool(maximumPoolSize);

		if (autoShutDown) {
			threadPoolExecutor.setKeepAliveTime(5, TimeUnit.SECONDS);

			threadPoolExecutor.allowCoreThreadTimeOut(true);
			threadPoolExecutor.setCorePoolSize(maximumPoolSize);
			threadPoolExecutor.setMaximumPoolSize(maximumPoolSize);
		}

		return threadPoolExecutor;
	}

	public static String getNounForm(
		int count, String plural, String singular) {

		if (count == 1) {
			return singular;
		}

		return plural;
	}

	public static String getPathRelativeTo(File file, File relativeToFile) {
		try {
			String filePath = getCanonicalPath(file);

			return filePath.replace(getCanonicalPath(relativeToFile) + "/", "");
		}
		catch (RuntimeException runtimeException) {
			throw new RuntimeException(
				"Unable to get relative path", runtimeException);
		}
	}

	public static Properties getProperties(File... propertiesFiles) {
		Properties properties = new Properties();

		for (File propertiesFile : propertiesFiles) {
			if ((propertiesFile != null) && propertiesFile.exists()) {
				properties.putAll(_getProperties(propertiesFile));
			}
		}

		return new SecureProperties(properties);
	}

	public static String getProperty(Properties properties, String name) {
		return _getProperty(properties, new ArrayList<String>(), name);
	}

	public static String getProperty(
		Properties properties, String basePropertyName,
		boolean useBasePropertyAsDefault, String... opts) {

		String propertyName = getPropertyName(
			properties, basePropertyName, opts);

		if (!useBasePropertyAsDefault &&
			basePropertyName.equals(propertyName)) {

			return null;
		}

		return _getProperty(properties, new ArrayList<String>(), propertyName);
	}

	public static String getProperty(
		Properties properties, String basePropertyName, String... opts) {

		return getProperty(properties, basePropertyName, true, opts);
	}

	public static String getPropertyName(
		Properties properties, boolean useBasePropertyName,
		String basePropertyName, String... opts) {

		if ((opts == null) || (opts.length == 0)) {
			return basePropertyName;
		}

		Set<String> optSet = new LinkedHashSet<>(Arrays.asList(opts));

		optSet.remove(null);
		optSet.remove("");

		opts = optSet.toArray(new String[0]);

		Properties matchingProperties = new Properties();

		for (Object key : properties.keySet()) {
			String keyString = key.toString();

			if (keyString.matches(
					Pattern.quote(basePropertyName) + "(\\[.*|$)")) {

				matchingProperties.setProperty(
					keyString, properties.getProperty(keyString));
			}
		}

		Set<Set<String>> targetOptSets = _getOrderedOptSets(opts);

		String propertyName = null;

		Map<String, Set<String>> propertyOptRegexSets =
			_getPropertyOptRegexSets(
				basePropertyName, matchingProperties.stringPropertyNames());

		for (Set<String> targetOptSet : targetOptSets) {
			for (Map.Entry<String, Set<String>> propertyOptRegexEntry :
					propertyOptRegexSets.entrySet()) {

				Set<String> propertyOptRegexes =
					propertyOptRegexEntry.getValue();

				if (targetOptSet.size() < propertyOptRegexes.size()) {
					continue;
				}

				boolean matchesAllPropertyOptRegexes = true;

				for (String targetOpt : targetOptSet) {
					boolean matchesPropertyOptRegex = false;

					for (String propertyOptRegex : propertyOptRegexes) {
						if (targetOpt.matches(propertyOptRegex)) {
							matchesPropertyOptRegex = true;
						}
					}

					if (!matchesPropertyOptRegex) {
						matchesAllPropertyOptRegexes = false;

						break;
					}
				}

				if (matchesAllPropertyOptRegexes) {
					propertyName = propertyOptRegexEntry.getKey();

					break;
				}
			}

			if (propertyName != null) {
				break;
			}
		}

		if (propertyName != null) {
			return propertyName;
		}

		if (useBasePropertyName) {
			return basePropertyName;
		}

		return null;
	}

	public static String getPropertyName(
		Properties properties, String basePropertyName, String... opts) {

		return getPropertyName(properties, true, basePropertyName, opts);
	}

	public static List<String> getPropertyOptions(String propertyName) {
		List<String> propertyOptions = new ArrayList<>();

		Stack<Integer> stack = new Stack<>();

		Integer start = null;

		for (int i = 0; i < propertyName.length(); i++) {
			char c = propertyName.charAt(i);

			if (c == '[') {
				stack.push(i);

				if (start == null) {
					start = i;
				}

				continue;
			}

			if ((c != ']') || (start == null)) {
				continue;
			}

			stack.pop();

			if (stack.isEmpty()) {
				propertyOptions.add(propertyName.substring(start + 1, i));

				start = null;
			}
		}

		return propertyOptions;
	}

	public static String getRandomGitHubDevNodeHostname() {
		return getRandomGitHubDevNodeHostname(null);
	}

	public static String getRandomGitHubDevNodeHostname(
		List<String> excludedHostnames) {

		if (isCloudCINode()) {
			return "";
		}

		List<String> gitHubDevNodeHostnames = getGitHubCacheHostnames();

		if (excludedHostnames != null) {
			for (String excludedHostname : excludedHostnames) {
				gitHubDevNodeHostnames.remove(excludedHostname);
			}
		}

		return getRandomString(gitHubDevNodeHostnames);
	}

	public static List<String> getRandomList(List<String> list, int size) {
		if (list.size() < size) {
			throw new IllegalStateException(
				"Size must not exceed the size of the list");
		}

		if (size == list.size()) {
			return list;
		}

		List<String> randomList = new ArrayList<>(size);

		for (int i = 0; i < size; i++) {
			String item = null;

			while (true) {
				item = getRandomString(list);

				if (randomList.contains(item)) {
					continue;
				}

				randomList.add(item);

				break;
			}
		}

		return randomList;
	}

	public static <T> T getRandomListItem(List<T> list) {
		return list.get(getRandomValue(0, list.size() - 1));
	}

	public static String getRandomString(Collection<String> collection) {
		if ((collection == null) || collection.isEmpty()) {
			throw new IllegalArgumentException("Collection is null or empty");
		}

		int randomIndex = getRandomValue(0, collection.size() - 1);

		Iterator<String> iterator = collection.iterator();

		for (int i = 0; i < (randomIndex - 1); i++) {
			iterator.next();
		}

		return iterator.next();
	}

	public static int getRandomValue(int start, int end) {
		int size = Math.abs(end - start) + 1;

		double randomDouble = Math.random();

		return Math.min(start, end) + (int)Math.floor(size * randomDouble);
	}

	public static long getRandomValue(long start, long end) {
		long size = Math.abs(end - start) + 1;

		double randomDouble = Math.random();

		return Math.min(start, end) + (int)Math.floor(size * randomDouble);
	}

	public static List<JenkinsSlave> getReachableJenkinsSlaves(
		List<JenkinsMaster> jenkinsMasters, Integer targetSlaveCount) {

		List<Callable<List<JenkinsSlave>>> callables = new ArrayList<>(
			jenkinsMasters.size());

		for (final JenkinsMaster jenkinsMaster : jenkinsMasters) {
			Callable<List<JenkinsSlave>> callable =
				new Callable<List<JenkinsSlave>>() {

					@Override
					public List<JenkinsSlave> call() throws Exception {
						jenkinsMaster.update();

						return jenkinsMaster.getOnlineJenkinsSlaves();
					}

				};

			callables.add(callable);
		}

		ThreadPoolExecutor threadPoolExecutor = getNewThreadPoolExecutor(
			jenkinsMasters.size(), true);

		ParallelExecutor<List<JenkinsSlave>> parallelExecutor =
			new ParallelExecutor<>(
				callables, threadPoolExecutor, "getReachableJenkinsSlaves");

		List<JenkinsSlave> onlineJenkinsSlaves;

		try {
			onlineJenkinsSlaves = concatenate(
				parallelExecutor.execute(), false);

			Collections.sort(onlineJenkinsSlaves);

			if (targetSlaveCount == null) {
				targetSlaveCount = onlineJenkinsSlaves.size();
			}
		}
		catch (TimeoutException timeoutException) {
			throw new RuntimeException(timeoutException);
		}

		List<JenkinsSlave> reachableJenkinsSlaves = new ArrayList<>(
			targetSlaveCount);

		while (reachableJenkinsSlaves.size() < targetSlaveCount) {
			JenkinsSlave randomJenkinsSlave = getRandomListItem(
				onlineJenkinsSlaves);

			onlineJenkinsSlaves.remove(randomJenkinsSlave);

			if (randomJenkinsSlave.isReachable()) {
				reachableJenkinsSlaves.add(randomJenkinsSlave);
			}

			if (onlineJenkinsSlaves.isEmpty() &&
				(reachableJenkinsSlaves.size() < targetSlaveCount)) {

				throw new RuntimeException(
					"Unable to find enough reachable Jenkins slaves");
			}
		}

		return reachableJenkinsSlaves;
	}

	public static String getRegexLiteral(String string) {
		if (string == null) {
			throw new NullPointerException("String is null");
		}

		String specialCharactersString = "\\^$.|?*+()[]{}";

		StringBuilder sb = new StringBuilder();

		for (char character : string.toCharArray()) {
			if (specialCharactersString.indexOf(character) != -1) {
				sb.append('\\');
			}

			sb.append(character);
		}

		return sb.toString();
	}

	public static Long getRemoteCurrentTimeSeconds(String hostname) {
		if (isNullOrEmpty(hostname)) {
			return null;
		}

		String command = "date +%s";

		if (!hostname.equals(getHostName(""))) {
			command = combine("ssh root@", hostname, " ", command);
		}

		try {
			Process process = executeBashCommands(
				false, new File("."), 3000, command);

			if (process.exitValue() != 0) {
				return null;
			}

			String output = readInputStream(process.getInputStream());

			try {
				return Long.parseLong(output.replaceAll("(?s)(\\d+).*", "$1"));
			}
			catch (NumberFormatException numberFormatException) {
				return null;
			}
		}
		catch (IOException | TimeoutException exception) {
			return null;
		}
	}

	public static String getRemoteURL(String localURL) {
		if (localURL.startsWith("file")) {
			localURL = fixFileName(localURL);
		}

		String remoteURL = localURL;
		String remoteURLQueryString = "";

		int x = localURL.indexOf("?");

		if (x != -1) {
			remoteURL = localURL.substring(0, x);
			remoteURLQueryString = localURL.substring(x);
		}

		Matcher matcher = _localURLAuthorityPattern1.matcher(remoteURL);

		if (isCloudCINode() && matcher.find()) {
			String localURLAuthority = matcher.group(0);
			String remoteURLAuthority = combine(
				"https://", matcher.group(1), "-aws", ".liferay.com/");

			remoteURL = remoteURL.replaceAll(
				localURLAuthority, remoteURLAuthority);

			return remoteURL + remoteURLQueryString;
		}

		matcher = _localURLAuthorityPattern2.matcher(remoteURL);

		if (matcher.find()) {
			String localURLAuthority = matcher.group(0);
			String remoteURLAuthority = combine(
				"https://", matcher.group(1), ".liferay.com/");

			remoteURL = remoteURL.replaceAll(
				localURLAuthority, remoteURLAuthority);

			return remoteURL + remoteURLQueryString;
		}

		matcher = _localURLAuthorityPattern3.matcher(remoteURL);

		if (matcher.find()) {
			String localURLAuthority = matcher.group(0);
			String remoteURLAuthority = combine("https://", matcher.group(2));

			remoteURL = remoteURL.replaceAll(
				localURLAuthority, remoteURLAuthority);

			return remoteURL + remoteURLQueryString;
		}

		return remoteURL + remoteURLQueryString;
	}

	public static String getResourceFileContent(String resourceName)
		throws IOException {

		try (InputStream resourceInputStream =
				JenkinsResultsParserUtil.class.getResourceAsStream(
					resourceName)) {

			if (resourceInputStream == null) {
				throw new RuntimeException(
					"Unable to find resource: " + resourceName);
			}

			return readInputStream(resourceInputStream);
		}
	}

	public static String getResponseHeaders(URLConnection urlConnection) {
		StringBuilder sb = new StringBuilder();

		Map<String, List<String>> map = urlConnection.getHeaderFields();

		for (Map.Entry<String, List<String>> entry : map.entrySet()) {
			sb.append(entry.getKey());
			sb.append(":");
			sb.append(join(",", entry.getValue()));
			sb.append("\n");
		}

		return sb.toString();
	}

	public static String getSHA(File file) {
		try {
			Process process = executeBashCommands(
				combine(
					"sha512sum '", getCanonicalPath(file),
					"' | awk '{print $1}'"));

			return readInputStream(process.getInputStream());
		}
		catch (IOException | TimeoutException exception) {
			throw new RuntimeException("Unable to read SHA file", exception);
		}
	}

	public static List<String> getSlaves(
		Properties buildProperties, String jenkinsMasterPatternString) {

		return getSlaves(
			buildProperties, jenkinsMasterPatternString, null, false);
	}

	public static List<String> getSlaves(
		Properties buildProperties, String jenkinsMasterPatternString,
		Integer targetSlaveCount, boolean validate) {

		Set<String> slaves = new LinkedHashSet<>();

		Pattern jenkinsSlavesPropertyNamePattern = Pattern.compile(
			"master.slaves\\(" + jenkinsMasterPatternString + "\\)");

		for (Object propertyName : buildProperties.keySet()) {
			Matcher jenkinsSlavesPropertyNameMatcher =
				jenkinsSlavesPropertyNamePattern.matcher(
					propertyName.toString());

			if (jenkinsSlavesPropertyNameMatcher.find()) {
				String slavesString = expandSlaveRange(
					buildProperties.getProperty(propertyName.toString()));

				for (String slave : slavesString.split(",")) {
					slave = slave.trim();

					if (isNullOrEmpty(slave)) {
						continue;
					}

					slaves.add(slave);
				}
			}
		}

		if (targetSlaveCount == null) {
			if (!validate) {
				return new ArrayList<>(slaves);
			}

			targetSlaveCount = slaves.size();
		}

		if (slaves.size() < targetSlaveCount) {
			throw new IllegalStateException(
				"Target size exceeds the number of available slaves");
		}

		List<String> randomSlaves = new ArrayList<>(targetSlaveCount);

		while (randomSlaves.size() < targetSlaveCount) {
			String randomSlave = getRandomString(slaves);

			slaves.remove(randomSlave);

			if (!validate || isReachable(randomSlave)) {
				randomSlaves.add(randomSlave);
			}

			if (slaves.isEmpty() && (randomSlaves.size() < targetSlaveCount)) {
				throw new RuntimeException(
					"Unable to find enough reachable slaves");
			}
		}

		return randomSlaves;
	}

	public static List<String> getSlaves(String jenkinsMasterPatternString)
		throws Exception {

		return getSlaves(getBuildProperties(), jenkinsMasterPatternString);
	}

	public static File getSshDir() {
		return _sshDir;
	}

	public static String getStackTrace(Exception exception, int lines) {
		if (exception == null) {
			return null;
		}

		StringBuilder sb = new StringBuilder();

		StackTraceElement[] stackTraceElements = exception.getStackTrace();

		Class<?> clazz = exception.getClass();

		sb.append(clazz.getName());

		String message = exception.getMessage();

		if (!isNullOrEmpty(message)) {
			sb.append(": ");
			sb.append(message);
		}

		sb.append("\n");

		if (stackTraceElements != null) {
			lines = Math.min(stackTraceElements.length, lines);

			for (int i = 0; i < lines; i++) {
				sb.append("\t");
				sb.append(stackTraceElements[i]);
				sb.append("\n");
			}

			if (stackTraceElements.length > lines) {
				sb.append("\t...\n");
			}
		}

		return sb.toString();
	}

	public static List<File> getSubdirectories(int depth, File rootDirectory) {
		if (!rootDirectory.isDirectory()) {
			return Collections.emptyList();
		}

		List<File> subdirectories = new ArrayList<>();

		if (depth == 0) {
			subdirectories.add(rootDirectory);
		}
		else {
			for (File file : rootDirectory.listFiles()) {
				if (!file.isDirectory()) {
					continue;
				}

				subdirectories.addAll(getSubdirectories(depth - 1, file));
			}
		}

		return subdirectories;
	}

	public static String getUpstreamUserName(
		String repositoryName, String upstreamBranchName) {

		if (!repositoryName.startsWith("liferay-portal")) {
			return _UPSTREAM_USER_NAME_DEFAULT;
		}

		try {
			String upstreamUserName = getBuildProperty(
				"portal.upstream.username", upstreamBranchName);

			if (!isNullOrEmpty(upstreamUserName)) {
				return upstreamUserName;
			}
		}
		catch (IOException ioException) {
			System.out.println(ioException.getMessage());
		}

		return _UPSTREAM_USER_NAME_DEFAULT;
	}

	public static File getUserHomeDir() {
		return _userHomeDir;
	}

	public static void gzip(File sourceFile, File targetGzipFile) {
		try (FileOutputStream fileOutputStream = new FileOutputStream(
				targetGzipFile);

			GZIPOutputStream gzipOutputStream = new GZIPOutputStream(
				fileOutputStream);

			FileInputStream fileInputStream = new FileInputStream(sourceFile)) {

			byte[] bytes = new byte[1024];
			int length = 0;

			while ((length = fileInputStream.read(bytes)) > 0) {
				gzipOutputStream.write(bytes, 0, length);
			}
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}
	}

	public static long invokeJenkinsBuild(
		JenkinsMaster jenkinsMaster, String jenkinsJobName,
		Map<String, String> buildParameters) {

		return invokeJenkinsBuild(
			jenkinsMaster, jenkinsJobName, buildParameters,
			_MILLIS_TIMEOUT_DEFAULT);
	}

	public static long invokeJenkinsBuild(
		JenkinsMaster jenkinsMaster, String jenkinsJobName,
		Map<String, String> buildParameters, int timeout) {

		StringBuilder sb = new StringBuilder();

		sb.append(jenkinsMaster.getRemoteURL());
		sb.append("job/");
		sb.append(jenkinsJobName);
		sb.append("/buildWithParameters?");

		for (Map.Entry<String, String> buildParameter :
				buildParameters.entrySet()) {

			String value = buildParameter.getValue();

			if (isNullOrEmpty(value)) {
				continue;
			}

			sb.append(buildParameter.getKey());
			sb.append("=");
			sb.append(value);
			sb.append("&");
		}

		try {
			sb.append("token=");
			sb.append(getBuildProperty("jenkins.authentication.token"));

			return getJenkinsBuildQueueId(
				UrlReader.getResponseHeader(
					"Location", getJenkinsHTTPAuthorization(),
					HttpRequestMethod.GET, null, timeout, sb.toString()));
		}
		catch (IOException ioException) {
			throw new RuntimeException(
				"Unable to invoke a Jenkins job", ioException);
		}
	}

	public static boolean isBuildCachingEnabled(
		String jobName, String testSuiteName) {

		if (!isCloudCINode()) {
			return false;
		}

		String buildCachingEnabled = Environment.get("BUILD_CACHING_ENABLED");

		if (!isNullOrEmpty(buildCachingEnabled)) {
			return Objects.equals(buildCachingEnabled, "true");
		}

		try {
			buildCachingEnabled = getBuildProperty(
				"build.caching.enabled", jobName, testSuiteName);

			if (Objects.equals(buildCachingEnabled, "true")) {
				return true;
			}
		}
		catch (IOException ioException) {
			return false;
		}

		return false;
	}

	public static boolean isCINode() {
		if (_ciNode == null) {
			if (isNullOrEmpty(Environment.get("JENKINS_URL")) &&
				isNullOrEmpty(Environment.get("MASTER_NETWORK_NAME"))) {

				_ciNode = false;
			}
			else {
				_ciNode = true;
			}
		}

		return _ciNode;
	}

	public static boolean isCloudCINode() {
		if (!isCINode()) {
			return false;
		}

		String masterNetworkName = Environment.get("MASTER_NETWORK_NAME");

		if (!isNullOrEmpty(masterNetworkName) &&
			(masterNetworkName.equals("aws-network") ||
			 masterNetworkName.equals("gcp-network"))) {

			return true;
		}

		return false;
	}

	public static boolean isDouble(String string) {
		try {
			Double.parseDouble(string);
		}
		catch (Exception exception) {
			return false;
		}

		return true;
	}

	public static boolean isFileExcluded(
		List<PathMatcher> excludesPathMatchers, File file) {

		return isFileExcluded(excludesPathMatchers, file.toPath());
	}

	public static boolean isFileExcluded(
		List<PathMatcher> excludesPathMatchers, Path path) {

		if (excludesPathMatchers != null) {
			for (PathMatcher excludesPathMatcher : excludesPathMatchers) {
				if (excludesPathMatcher.matches(path)) {
					return true;
				}
			}
		}

		return false;
	}

	public static boolean isFileIncluded(
		List<PathMatcher> excludesPathMatchers,
		List<PathMatcher> includesPathMatchers, File file) {

		return isFileIncluded(
			excludesPathMatchers, includesPathMatchers, file.toPath());
	}

	public static boolean isFileIncluded(
		List<PathMatcher> excludesPathMatchers,
		List<PathMatcher> includesPathMatchers, Path path) {

		if (isFileExcluded(excludesPathMatchers, path)) {
			return false;
		}

		if ((includesPathMatchers != null) && !includesPathMatchers.isEmpty()) {
			for (PathMatcher includesPathMatcher : includesPathMatchers) {
				if (includesPathMatcher.matches(path)) {
					return true;
				}
			}

			return false;
		}

		return true;
	}

	public static boolean isFileInDirectory(File directory, File file) {
		if (directory == null) {
			throw new IllegalArgumentException("Directory is null");
		}

		if (file == null) {
			throw new IllegalArgumentException("File is null");
		}

		if (!directory.isDirectory()) {
			throw new IllegalArgumentException(
				directory.getName() + " is not a directory");
		}

		String directoryCanonicalPath = getCanonicalPath(directory) + "/";
		String fileCanonicalPath = getCanonicalPath(file);

		return fileCanonicalPath.startsWith(directoryCanonicalPath);
	}

	public static boolean isInteger(String string) {
		if ((string != null) && string.matches("\\d+")) {
			return true;
		}

		return false;
	}

	public static boolean isJenkinsMaster() {
		try {
			JenkinsMaster.getInstance(getHostName(null));
		}
		catch (Exception exception) {
			return false;
		}

		return true;
	}

	public static boolean isJenkinsSlaveInNetwork(
		String jenkinsSlaveName, String networkName) {

		String jenkinsMasterName = getJenkinsMasterName(jenkinsSlaveName);

		try {
			String jenkinsMasterNetworkName = getBuildProperty(
				"master.property(" + jenkinsMasterName +
					"/master.network.name)");

			if (!isNullOrEmpty(jenkinsMasterNetworkName)) {
				return jenkinsMasterNetworkName.equals(networkName);
			}
		}
		catch (IOException ioException) {
			System.out.println("WARNING: Unable to get build properties");
		}

		JenkinsMaster jenkinsMaster = JenkinsMaster.getInstance(
			jenkinsMasterName);

		if ((jenkinsMaster == null) ||
			!Objects.equals(jenkinsMaster.getNetworkName(), networkName)) {

			return false;
		}

		return true;
	}

	public static boolean isJSONArray(String string) {
		if (isNullOrEmpty(string)) {
			return false;
		}

		string = string.trim();

		try {
			new JSONArray(string);
		}
		catch (Exception exception) {
			return false;
		}

		return true;
	}

	public static boolean isJSONArrayEqual(
		JSONArray expectedJSONArray, JSONArray actualJSONArray) {

		if (expectedJSONArray.length() != actualJSONArray.length()) {
			return false;
		}

		for (int i = 0; i < expectedJSONArray.length(); i++) {
			Object actual = actualJSONArray.get(i);
			Object expected = expectedJSONArray.get(i);

			if (!_isJSONExpectedAndActualEqual(expected, actual)) {
				return false;
			}
		}

		return true;
	}

	public static boolean isJSONObject(String string) {
		if (isNullOrEmpty(string)) {
			return false;
		}

		string = string.trim();

		try {
			new JSONObject(string);
		}
		catch (Exception exception) {
			return false;
		}

		return true;
	}

	public static boolean isJSONObjectEqual(
		JSONObject expectedJSONObject, JSONObject actualJSONObject) {

		JSONArray namesJSONArray = expectedJSONObject.names();

		for (int i = 0; i < namesJSONArray.length(); i++) {
			String name = namesJSONArray.getString(i);

			if (!actualJSONObject.has(name)) {
				return false;
			}

			Object expected = expectedJSONObject.get(name);
			Object actual = actualJSONObject.get(name);

			if (!_isJSONExpectedAndActualEqual(expected, actual)) {
				return false;
			}
		}

		return true;
	}

	public static boolean isMatchingSHAFile(File file, File shaFile) {
		if (!file.exists() || !shaFile.exists()) {
			return false;
		}

		try {
			if (Objects.equals(read(shaFile), getSHA(file))) {
				return true;
			}
		}
		catch (IOException ioException) {
			System.out.println("WARNING: Unable to check SHA");
		}

		return false;
	}

	public static boolean isNullOrEmpty(String string) {
		if (string == null) {
			return true;
		}

		String trimmedString = string.trim();

		return trimmedString.isEmpty();
	}

	public static boolean isOSX() {
		return SystemUtils.IS_OS_MAC_OSX;
	}

	public static boolean isPoshiFile(File file) {
		Matcher poshiFileNamePatternMatcher = _poshiFileNamePattern.matcher(
			file.getName());

		return poshiFileNamePatternMatcher.matches();
	}

	public static boolean isReachable(String hostname) {
		try {
			InetAddress inetAddress = InetAddress.getByName(hostname);

			return inetAddress.isReachable(5000);
		}
		catch (IOException ioException) {
			System.out.println("Unable to reach " + hostname);

			return false;
		}
	}

	public static boolean isServerPortReachable(String hostname, int port) {
		try (Socket socket = new Socket()) {
			socket.connect(new InetSocketAddress(hostname, port), 5000);

			return true;
		}
		catch (IOException ioException) {
			System.out.println(
				combine(
					"Unable to reach ", hostname, ":", String.valueOf(port)));

			return false;
		}
	}

	public static boolean isSHA(String sha) {
		if (sha == null) {
			return false;
		}

		Matcher matcher = _shaPattern.matcher(sha);

		return matcher.matches();
	}

	public static synchronized boolean isTopLevelJobName(String jobName) {
		if (isNullOrEmpty(jobName)) {
			return false;
		}

		if (_topLevelJobNames != null) {
			return _topLevelJobNames.contains(jobName);
		}

		String masterHostname = Environment.get("MASTER_HOSTNAME");

		if (isNullOrEmpty(masterHostname)) {
			return false;
		}

		final JenkinsMaster jenkinsMaster = JenkinsMaster.getInstance(
			masterHostname);

		Retryable<Set<String>> retryable = new Retryable<Set<String>>(
			true, 3, 5, false) {

			@Override
			public Set<String> execute() {
				JSONObject topLevelBuildsJSONObject;

				try {
					topLevelBuildsJSONObject = toJSONObject(
						jenkinsMaster.getRemoteURL() +
							"/view/Top%20Level/api/json?tree=jobs[name]");
				}
				catch (IOException ioException) {
					throw new RuntimeException(ioException);
				}

				JSONArray jobsJSONArray = topLevelBuildsJSONObject.optJSONArray(
					"jobs");

				Set<String> topLevelJobNames = new HashSet<>();

				if ((jobsJSONArray == null) || jobsJSONArray.isEmpty()) {
					return topLevelJobNames;
				}

				for (int i = 0; i < jobsJSONArray.length(); i++) {
					JSONObject jobJSONObject = jobsJSONArray.optJSONObject(i);

					if (jobJSONObject == null) {
						continue;
					}

					String topLevelJobName = jobJSONObject.getString("name");

					topLevelJobNames.add(topLevelJobName);
				}

				return topLevelJobNames;
			}

		};

		_topLevelJobNames = retryable.executeWithRetries();

		return _topLevelJobNames.contains(jobName);
	}

	public static boolean isURL(String urlString) {
		if (isNullOrEmpty(urlString) || !urlString.matches("https?://.+")) {
			return false;
		}

		return true;
	}

	public static boolean isValid7zFile(File file) {
		String fileName = file.getName();

		if (!fileName.endsWith(".7z")) {
			return false;
		}

		Process process = null;

		String processOutput = null;

		try {
			process = executeBashCommands("7z t " + file);

			processOutput = readInputStream(process.getInputStream());
		}
		catch (IOException | TimeoutException exception) {
			return false;
		}

		int exitValue = process.exitValue();

		if ((exitValue == 0) && !processOutput.contains("Files: 0\n")) {
			return true;
		}

		return false;
	}

	public static boolean isValidGitSHA(String sha) {
		if (sha == null) {
			return false;
		}

		Matcher matcher = _gitSHAPattern.matcher(sha);

		return matcher.matches();
	}

	public static boolean isValidTarFile(File file) {
		if (!file.exists()) {
			return false;
		}

		String fileName = file.getName();

		if (!fileName.endsWith(".tar")) {
			return false;
		}

		try (InputStream bufferedInputStream = new BufferedInputStream(
				new FileInputStream(file));

			TarArchiveInputStream tarArchiveInputStream =
				new TarArchiveInputStream(bufferedInputStream)) {

			TarArchiveEntry tarArchiveEntry;

			byte[] buffer = new byte[8192];

			while ((tarArchiveEntry =
						tarArchiveInputStream.getNextTarEntry()) != null) {

				if (tarArchiveEntry.isDirectory()) {
					continue;
				}

				while (tarArchiveInputStream.read(buffer) != -1) {
				}
			}

			return true;
		}
		catch (IOException ioException) {
			return false;
		}
	}

	public static boolean isValidTarGzipFile(File file) {
		if (!file.exists()) {
			return false;
		}

		String fileName = file.getName();

		if (!fileName.endsWith(".tar.gz")) {
			return false;
		}

		try (GZIPInputStream gzipInputStream = new GZIPInputStream(
				new FileInputStream(file));

			InputStream bufferedInputStream = new BufferedInputStream(
				gzipInputStream);

			TarArchiveInputStream tarArchiveInputStream =
				new TarArchiveInputStream(bufferedInputStream)) {

			TarArchiveEntry tarArchiveEntry;

			byte[] buffer = new byte[8192];

			while ((tarArchiveEntry =
						tarArchiveInputStream.getNextTarEntry()) != null) {

				if (tarArchiveEntry.isDirectory()) {
					continue;
				}

				while (tarArchiveInputStream.read(buffer) != -1) {
				}
			}

			return true;
		}
		catch (IOException ioException) {
			return false;
		}
	}

	public static boolean isValidZipFile(File file) throws IOException {
		if (!file.exists()) {
			return false;
		}

		String fileName = file.getName();

		if (!fileName.endsWith(".war") && !fileName.endsWith(".zip")) {
			return false;
		}

		try (ZipFile zipFile = new ZipFile(file, ZipFile.OPEN_READ)) {
			Enumeration<?> enumeration = zipFile.entries();

			while (enumeration.hasMoreElements()) {
				enumeration.nextElement();
			}

			return true;
		}
		catch (IOException ioException) {
			return false;
		}
	}

	public static boolean isWindows() {
		return SystemUtils.IS_OS_WINDOWS;
	}

	public static String join(String delimiter, List<String> list) {
		return join(delimiter, list.toArray(new String[0]));
	}

	public static String join(String delimiter, String... strings) {
		StringBuilder sb = new StringBuilder();

		for (String string : strings) {
			if (string == null) {
				continue;
			}

			if (sb.length() > 0) {
				sb.append(delimiter);
			}

			sb.append(string);
		}

		return sb.toString();
	}

	public static void keepJenkinsBuild(
		boolean keepBuildLogs, int buildNumber, String jobName,
		String masterHostname) {

		StringBuilder sb = new StringBuilder();

		sb.append("def job = Jenkins.instance.getItemByFullName(\"");
		sb.append(jobName);
		sb.append("\"); ");

		sb.append("def build = job.getBuildByNumber(");
		sb.append(buildNumber);
		sb.append("); ");

		sb.append("build.keepLog(");
		sb.append(keepBuildLogs);
		sb.append(");");

		executeJenkinsScript(masterHostname, sb.toString());
	}

	public static int lastIndexOfRegex(String string, String regex) {
		Pattern pattern = Pattern.compile(regex);

		Matcher matcher = pattern.matcher(string);

		int lastIndex = -1;

		while (matcher.find()) {
			lastIndex = matcher.start();
		}

		return lastIndex;
	}

	public static void loadBalanceQueuedBuilds(
			String jobName, JenkinsMaster jenkinsMaster)
		throws IOException {

		List<JenkinsMaster> availableJenkinsMasters = new ArrayList<>();

		JenkinsCohort jenkinsCohort = jenkinsMaster.getJenkinsCohort();

		for (JenkinsMaster availableJenkinsMaster :
				jenkinsCohort.getJenkinsMasters()) {

			if (!availableJenkinsMaster.isAvailable() ||
				availableJenkinsMaster.isBlackListed()) {

				continue;
			}

			availableJenkinsMasters.add(availableJenkinsMaster);
		}

		JSONObject jsonObject = toJSONObject(
			combine(
				"https://", jenkinsMaster.getName(),
				".liferay.com/queue/api/json"));

		JSONArray itemsJSONArray = jsonObject.getJSONArray("items");

		for (int i = 0; i < itemsJSONArray.length(); i++) {
			JSONObject itemJSONObject = itemsJSONArray.getJSONObject(i);

			JSONObject taskJSONObject = itemJSONObject.getJSONObject("task");

			String name = taskJSONObject.getString("name");

			if (!name.equals(jobName)) {
				continue;
			}

			JSONArray actionsJSONArray = itemJSONObject.optJSONArray("actions");

			StringBuilder sb = new StringBuilder();

			JenkinsMaster availableJenkinsMaster = getRandomListItem(
				availableJenkinsMasters);

			sb.append("http://");
			sb.append(availableJenkinsMaster.getName());
			sb.append("/job/");
			sb.append(jobName);
			sb.append("/buildWithParameters?");
			sb.append("token=raen3Aib");

			for (int j = 0; j < actionsJSONArray.length(); j++) {
				JSONObject actionJSONObject = actionsJSONArray.getJSONObject(j);

				if (!Objects.equals(
						actionJSONObject.optString("_class"),
						"hudson.model.ParametersAction")) {

					continue;
				}

				JSONArray parametersJSONArray = actionJSONObject.optJSONArray(
					"parameters");

				for (int k = 0; k < parametersJSONArray.length(); k++) {
					JSONObject parameterJSONObject =
						parametersJSONArray.getJSONObject(k);

					String paramName = parameterJSONObject.getString("name");
					String paramValue = parameterJSONObject.getString("value");

					if (isNullOrEmpty(paramName) || isNullOrEmpty(paramValue)) {
						continue;
					}

					sb.append("&");
					sb.append(paramName);
					sb.append("=");
					sb.append(paramValue);
				}
			}

			System.out.println(sb);

			toString(sb.toString());

			cancelQueuedItem(itemJSONObject.getLong("id"), jenkinsMaster);
		}
	}

	public static void move(File sourceFile, File targetFile)
		throws IOException {

		copy(sourceFile, targetFile);

		if (!delete(sourceFile)) {
			throw new IOException("Unable to delete " + sourceFile);
		}
	}

	public static FilenameFilter newFilenameFilter(String patternString) {
		final Pattern pattern = Pattern.compile(patternString);

		return new FilenameFilter() {

			@Override
			public boolean accept(File dir, String name) {
				Matcher matcher = pattern.matcher(name);

				return matcher.matches();
			}

		};
	}

	public static <T> List<List<T>> partitionByCount(List<T> list, int count) {
		int listSize = list.size();

		int partitionSize = 1;

		if (listSize > count) {
			partitionSize = listSize / count;

			if ((listSize % count) > 0) {
				partitionSize++;
			}
		}

		return Lists.partition(list, partitionSize);
	}

	public static void printTable(String[][] table) {
		if (table.length == 0) {
			return;
		}

		int[] maxColumnWidth = new int[table[0].length];

		for (String[] row : table) {
			for (int columnNumber = 0; columnNumber < row.length;
				 columnNumber++) {

				String item = row[columnNumber];

				if (maxColumnWidth[columnNumber] <= item.length()) {
					maxColumnWidth[columnNumber] = item.length();
				}
			}
		}

		StringBuilder rowsStringBuilder = new StringBuilder();

		for (String[] row : table) {
			for (int columnNumber = 0; columnNumber < row.length;
				 columnNumber++) {

				String cellText = row[columnNumber];

				rowsStringBuilder.append(
					String.format(
						combine(
							"| %-",
							String.valueOf(maxColumnWidth[columnNumber]), "s "),
						cellText));
			}

			rowsStringBuilder.append("|\n");
		}

		int rowTotalSize = rowsStringBuilder.indexOf("\n");

		StringBuilder tableStringBuilder = new StringBuilder();

		for (int columnNumber = 0; columnNumber < rowTotalSize;
			 columnNumber++) {

			tableStringBuilder.append("-");
		}

		tableStringBuilder.append("\n");
		tableStringBuilder.append(rowsStringBuilder);

		for (int columnNumber = 0; columnNumber < rowTotalSize;
			 columnNumber++) {

			tableStringBuilder.append("-");
		}

		System.out.println(tableStringBuilder.toString());
	}

	public static void pullDockerImageDependencies(
		File baseDir, String[] excludedDockerImageNames) {

		pullDockerImageDependencies(baseDir, excludedDockerImageNames, null);
	}

	public static void pullDockerImageDependencies(
		File baseDir, String[] excludedDockerImageNames,
		String ecrRegistryName) {

		String dockerEnabled = Environment.get("LIFERAY_DOCKER_ENABLED");

		if (isNullOrEmpty(dockerEnabled) || !dockerEnabled.equals("true")) {
			return;
		}

		List<String> pulledDockerImageNames = new ArrayList<>();

		for (File dockerFile : findFiles(baseDir, "Dockerfile")) {
			String dockerImageName = getDockerImageName(dockerFile);

			if (isNullOrEmpty(dockerImageName) ||
				pulledDockerImageNames.contains(dockerImageName)) {

				continue;
			}

			boolean excludeDockerImageName = false;

			if (excludedDockerImageNames != null) {
				for (String excludedImageName : excludedDockerImageNames) {
					if (isNullOrEmpty(excludedImageName)) {
						continue;
					}

					if (dockerImageName.matches(excludedImageName)) {
						excludeDockerImageName = true;

						break;
					}
				}
			}

			if (excludeDockerImageName) {
				continue;
			}

			try {
				Process process = null;

				if (!isNullOrEmpty(ecrRegistryName)) {
					Matcher dockerImageNameMatcher =
						_dockerImageNamePattern.matcher(dockerImageName);

					if (!dockerImageNameMatcher.find()) {
						continue;
					}

					StringBuilder sb = new StringBuilder();

					sb.append(ecrRegistryName);
					sb.append("/");

					String dockerImageRepository = dockerImageNameMatcher.group(
						"repository");

					if (isNullOrEmpty(dockerImageRepository)) {
						sb.append("library");
					}
					else {
						sb.append(dockerImageRepository);
					}

					sb.append("/");

					sb.append(dockerImageNameMatcher.group("name"));

					String dockerImageVersion = dockerImageNameMatcher.group(
						"version");

					if (!isNullOrEmpty(dockerImageVersion)) {
						sb.append(":");
						sb.append(dockerImageVersion);
					}

					String ecrDockerImageName = dockerImageNameMatcher.group(
						"ecrDockerImageName");

					if (!isNullOrEmpty(ecrDockerImageName)) {
						dockerImageName = ecrDockerImageName;
					}

					process = executeBashCommands(
						"docker pull " + sb,
						"docker tag " + sb + " " + dockerImageName);
				}
				else {
					process = executeBashCommands(
						"docker pull " + dockerImageName);
				}

				if (process.exitValue() != 0) {
					System.out.println(
						"Unable to pull Docker image " + dockerImageName);

					System.out.println(
						readInputStream(process.getErrorStream(), true));

					return;
				}

				System.out.println(
					readInputStream(process.getInputStream(), true));

				pulledDockerImageNames.add(dockerImageName);
			}
			catch (IOException | TimeoutException exception) {
				System.out.println(
					"Unable to pull Docker image " + dockerImageName);

				exception.printStackTrace();
			}
		}
	}

	public static String read(File file) throws IOException {
		String fileName = file.getName();

		if (!fileName.endsWith(".gz")) {
			return new String(Files.readAllBytes(Paths.get(file.toURI())));
		}

		String timeStamp = getDistinctTimeStamp();

		File tempFile = new File(Environment.get("WORKSPACE"), timeStamp);
		File tempGzipFile = new File(
			Environment.get("WORKSPACE"), timeStamp + ".gz");

		try {
			copy(file, tempGzipFile);

			unGzip(tempGzipFile, tempFile);

			return read(tempFile);
		}
		catch (Exception exception) {
			return null;
		}
		finally {
			if (tempFile.exists()) {
				delete(tempFile);
			}

			if (tempGzipFile.exists()) {
				delete(tempGzipFile);
			}
		}
	}

	public static String readInputStream(InputStream inputStream)
		throws IOException {

		return readInputStream(inputStream, false);
	}

	public static String readInputStream(
			InputStream inputStream, boolean resetAfterReading)
		throws IOException {

		if (resetAfterReading && !inputStream.markSupported()) {
			Class<?> inputStreamClass = inputStream.getClass();

			System.out.println(
				"Unable to reset after reading input stream " +
					inputStreamClass.getName());
		}

		if (resetAfterReading && inputStream.markSupported()) {
			inputStream.mark(Integer.MAX_VALUE);
		}

		StringBuffer sb = new StringBuffer();

		byte[] bytes = new byte[1024];

		int size = inputStream.read(bytes);

		while (size > 0) {
			sb.append(new String(Arrays.copyOf(bytes, size)));

			size = inputStream.read(bytes);
		}

		if (resetAfterReading && inputStream.markSupported()) {
			inputStream.reset();
		}

		return sb.toString();
	}

	public static String redact(String string) {
		if (isNullOrEmpty(string)) {
			return string;
		}

		synchronized (_redactTokens) {
			for (String redactToken : _redactTokens) {
				if (_forbiddenRedactTokens.contains(redactToken)) {
					continue;
				}

				string = string.replace(redactToken, "[REDACTED]");
			}
		}

		return string;
	}

	public static String removeDuplicates(
		String delimiter, String delimitedString) {

		if (isNullOrEmpty(delimitedString)) {
			return delimitedString;
		}

		List<String> strings = new ArrayList<>();

		for (String string : delimitedString.split(delimiter)) {
			if (!strings.contains(string)) {
				strings.add(string);
			}
		}

		return join(",", strings);
	}

	public static List<File> removeExcludedFiles(
		List<PathMatcher> excludesPathMatchers, List<File> files) {

		List<File> excludedFiles = getExcludedFiles(
			excludesPathMatchers, files);

		files.removeAll(excludedFiles);

		return files;
	}

	public static void rsync(
		String destinationHostName, String destinationDirPath,
		String sourceHostName, String sourceFilePath) {

		rsync(
			destinationHostName, destinationDirPath, sourceHostName,
			sourceFilePath, "-aqsz --chmod=go=rx");
	}

	public static void rsync(
		final String destinationHostName, final String destinationDirPath,
		final String sourceHostName, final String sourceFilePath,
		final String argumentString) {

		if ((destinationHostName != null) &&
			!destinationDirPath.startsWith("::")) {

			RemoteExecutor remoteExecutor = new RemoteExecutor();

			int returnCode = remoteExecutor.execute(
				1, new String[] {destinationHostName},
				new String[] {"mkdir -p " + escapeForBash(destinationDirPath)});

			if (returnCode != 0) {
				throw new RuntimeException("Unable to create target directory");
			}
		}
		else {
			File destinationDir = new File(destinationDirPath);

			destinationDir.mkdirs();
		}

		Retryable<Process> retryable = new Retryable<Process>(
			true, 3, 3, false) {

			@Override
			public Process execute() {
				String command = _combineCommandArgs(
					"time", "timeout", "1200", "rsync", argumentString,
					_getRyncPath(sourceHostName, escapeForBash(sourceFilePath)),
					_getRyncPath(
						destinationHostName,
						escapeForBash(destinationDirPath)));

				try {
					return executeBashCommands(command);
				}
				catch (IOException | TimeoutException exception) {
					throw new RuntimeException(exception);
				}
			}

		};

		try {
			Process process = retryable.executeWithRetries();

			if (process.exitValue() != 0) {
				throw new RuntimeException(
					readInputStream(process.getErrorStream()));
			}
		}
		catch (Exception exception) {
			throw new RuntimeException(
				"Unable to rsync " +
					_getRyncPath(sourceHostName, sourceFilePath),
				exception);
		}
	}

	public static void saveToCacheFile(String key, String text) {
		File cacheFile = getCacheFile(key);

		if (isNullOrEmpty(text)) {
			if (cacheFile.exists()) {
				cacheFile.delete();
			}

			return;
		}

		try {
			write(cacheFile, text);

			cacheFile.deleteOnExit();
		}
		catch (IOException ioException) {
			throw new RuntimeException(
				"Unable to save to cache file", ioException);
		}
	}

	public static void setBuildProperties(
		Hashtable<Object, Object> buildProperties) {

		_buildPropertiesURLs = null;

		synchronized (_buildProperties) {
			_buildProperties.clear();

			if (buildProperties != null) {
				_buildProperties.putAll(buildProperties);
			}
		}
	}

	public static void setBuildProperties(String... urls) {
		synchronized (_buildProperties) {
			_buildProperties.clear();
		}

		_buildPropertiesURLs = urls;
	}

	public static void sleep(long duration) {
		try {
			Thread.sleep(duration);
		}
		catch (InterruptedException interruptedException) {
			throw new RuntimeException(interruptedException);
		}
	}

	public static void tarGzip(File sourceDir, File targetTarGzipFile) {
		if (!sourceDir.isDirectory()) {
			throw new RuntimeException(sourceDir + " is not a directory");
		}

		try (FileOutputStream fileOutputStream = new FileOutputStream(
				targetTarGzipFile);

			BufferedOutputStream bufferedOutputStream =
				new BufferedOutputStream(fileOutputStream);

			GzipCompressorOutputStream gzipCompressorOutputStream =
				new GzipCompressorOutputStream(bufferedOutputStream);

			TarArchiveOutputStream tarArchiveOutputStream =
				new TarArchiveOutputStream(gzipCompressorOutputStream)) {

			tarArchiveOutputStream.setLongFileMode(
				TarArchiveOutputStream.LONGFILE_POSIX);

			for (File file : findFiles(sourceDir, ".*")) {
				TarArchiveEntry tarArchiveEntry = new TarArchiveEntry(
					file, getPathRelativeTo(file, sourceDir));

				tarArchiveOutputStream.putArchiveEntry(tarArchiveEntry);

				Files.copy(file.toPath(), tarArchiveOutputStream);

				tarArchiveOutputStream.closeArchiveEntry();
			}

			tarArchiveOutputStream.finish();
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}
	}

	public static BufferedReader toBufferedReader(
			String url, boolean checkCache)
		throws IOException {

		return toBufferedReader(
			url, checkCache, _RETRIES_SIZE_MAX_DEFAULT, null, null,
			_SECONDS_RETRY_PERIOD_DEFAULT, _MILLIS_TIMEOUT_DEFAULT, null);
	}

	public static BufferedReader toBufferedReader(
			String url, boolean checkCache, int maxRetries,
			HttpRequestMethod httpRequestMethod, String postContent,
			int retryPeriod, int timeout, HTTPAuthorization httpAuthorization)
		throws IOException {

		return new BufferedReader(
			new InputStreamReader(
				toInputStream(
					url, checkCache, maxRetries, httpRequestMethod, postContent,
					retryPeriod, timeout, httpAuthorization)));
	}

	public static String toDateString(Date date) {
		return toDateString(
			date, "MMM dd, yyyy h:mm:ss a z", "America/Los_Angeles");
	}

	public static String toDateString(Date date, String timeZoneName) {
		return toDateString(date, "MMM dd, yyyy h:mm:ss a z", timeZoneName);
	}

	public static String toDateString(
		Date date, String format, String timeZoneName) {

		SimpleDateFormat sdf = new SimpleDateFormat(format);

		if (timeZoneName != null) {
			sdf.setTimeZone(TimeZone.getTimeZone(timeZoneName));
		}

		return sdf.format(date);
	}

	public static String toDurationString(long duration) {
		long remainingDuration = duration;

		StringBuilder sb = new StringBuilder();

		remainingDuration = _appendStringForUnit(
			remainingDuration, _MILLIS_DAY, false, sb, "day", "days");

		remainingDuration = _appendStringForUnit(
			remainingDuration, _MILLIS_HOUR, false, sb, "hour", "hours");

		remainingDuration = _appendStringForUnit(
			remainingDuration, _MILLIS_MINUTE, true, sb, "minute", "minutes");

		if (duration < 60000) {
			remainingDuration = _appendStringForUnit(
				remainingDuration, _MILLIS_SECOND, true, sb, "second",
				"seconds");
		}

		if (duration < 1000) {
			_appendStringForUnit(remainingDuration, 1, true, sb, "ms", "ms");
		}

		String durationString = sb.toString();

		durationString = durationString.trim();

		if (durationString.equals("")) {
			durationString = "0 ms";
		}

		return durationString;
	}

	public static void toFile(URL url, File file) {
		try {
			System.out.println(
				combine(
					"Downloading ", url.toString(), " to ",
					getCanonicalPath(file)));

			long start = System.currentTimeMillis();

			try (InputStream inputStream = toInputStream(
					url.toString(), false)) {

				if (file.exists()) {
					file.delete();
				}

				File parentFile = file.getParentFile();

				if ((parentFile != null) && !parentFile.exists()) {
					parentFile.mkdirs();
				}

				byte[] bytes = new byte[10240];

				int bytesReadCount = inputStream.read(bytes);

				long totalBytesWrittenCount = 0;

				try (FileOutputStream fileOutputStream = new FileOutputStream(
						file)) {

					while (bytesReadCount != -1) {
						fileOutputStream.write(bytes, 0, bytesReadCount);

						totalBytesWrittenCount += bytesReadCount;

						bytesReadCount = inputStream.read(bytes);
					}

					fileOutputStream.flush();
				}

				System.out.println(
					combine(
						"Finished downloading ",
						toFileSizeString(totalBytesWrittenCount), " in ",
						toDurationString(System.currentTimeMillis() - start)));
			}
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}
	}

	public static String toFileSizeString(long byteCount) {
		long remainingByteCount = byteCount;

		StringBuilder sb = new StringBuilder();

		remainingByteCount = _appendStringForUnit(
			remainingByteCount, _BYTES_GIGA, false, sb, "GB", "GB");

		remainingByteCount = _appendStringForUnit(
			remainingByteCount, _BYTES_MEGA, false, sb, "MB", "MB");

		remainingByteCount = _appendStringForUnit(
			remainingByteCount, _BYTES_KILO, true, sb, "KB", "KB");

		if (byteCount < _BYTES_KILO) {
			_appendStringForUnit(remainingByteCount, 1, true, sb, "B", "B");
		}

		String fileSizeString = sb.toString();

		fileSizeString = fileSizeString.trim();

		if (fileSizeString.equals("")) {
			fileSizeString = "0 B";
		}

		return fileSizeString;
	}

	public static InputStream toInputStream(String url, boolean checkCache)
		throws IOException {

		return toInputStream(
			url, checkCache, _RETRIES_SIZE_MAX_DEFAULT, null, null,
			_SECONDS_RETRY_PERIOD_DEFAULT, _MILLIS_TIMEOUT_DEFAULT, null);
	}

	public static InputStream toInputStream(
			String url, boolean checkCache, int maxRetries,
			HttpRequestMethod httpRequestMethod, String postContent,
			int retryPeriod, int timeout, HTTPAuthorization httpAuthorization)
		throws IOException {

		return UrlReader.read(
			checkCache, httpAuthorization, httpRequestMethod, maxRetries,
			postContent, retryPeriod, timeout, url);
	}

	public static JSONArray toJSONArray(String url) throws IOException {
		return toJSONArray(
			url, true, _RETRIES_SIZE_MAX_DEFAULT, null,
			_SECONDS_RETRY_PERIOD_DEFAULT, _MILLIS_TIMEOUT_DEFAULT, null);
	}

	public static JSONArray toJSONArray(String url, boolean checkCache)
		throws IOException {

		return toJSONArray(
			url, checkCache, _RETRIES_SIZE_MAX_DEFAULT, null,
			_SECONDS_RETRY_PERIOD_DEFAULT, _MILLIS_TIMEOUT_DEFAULT, null);
	}

	public static JSONArray toJSONArray(
			String url, boolean checkCache, int maxRetries, String postContent,
			int retryPeriod, int timeout)
		throws IOException {

		return toJSONArray(
			url, checkCache, maxRetries, postContent, retryPeriod, timeout,
			null);
	}

	public static JSONArray toJSONArray(
			String url, boolean checkCache, int maxRetries, String postContent,
			int retryPeriod, int timeout, HTTPAuthorization httpAuthorization)
		throws IOException {

		String response = toString(
			url, checkCache, maxRetries, null, postContent, retryPeriod,
			timeout, httpAuthorization, true);

		if ((response == null) ||
			response.endsWith("was truncated due to its size.")) {

			return null;
		}

		return new JSONArray(response);
	}

	public static JSONArray toJSONArray(String url, String postContent)
		throws IOException {

		return toJSONArray(
			url, false, _RETRIES_SIZE_MAX_DEFAULT, postContent,
			_SECONDS_RETRY_PERIOD_DEFAULT, _MILLIS_TIMEOUT_DEFAULT, null);
	}

	public static JSONArray toJSONArray(
			String url, String postContent, HTTPAuthorization httpAuthorization)
		throws IOException {

		return toJSONArray(
			url, false, _RETRIES_SIZE_MAX_DEFAULT, postContent,
			_SECONDS_RETRY_PERIOD_DEFAULT, _MILLIS_TIMEOUT_DEFAULT,
			httpAuthorization);
	}

	public static JSONObject toJSONObject(String url) throws IOException {
		return toJSONObject(url, (HTTPAuthorization)null);
	}

	public static JSONObject toJSONObject(String url, boolean checkCache)
		throws IOException {

		return toJSONObject(
			url, checkCache, _RETRIES_SIZE_MAX_DEFAULT, null, null,
			_SECONDS_RETRY_PERIOD_DEFAULT, _MILLIS_TIMEOUT_DEFAULT, null);
	}

	public static JSONObject toJSONObject(
			String url, boolean checkCache, HTTPAuthorization httpAuthorization)
		throws IOException {

		return toJSONObject(
			url, checkCache, _RETRIES_SIZE_MAX_DEFAULT, null, null,
			_SECONDS_RETRY_PERIOD_DEFAULT, _MILLIS_TIMEOUT_DEFAULT,
			httpAuthorization);
	}

	public static JSONObject toJSONObject(
			String url, boolean checkCache, HttpRequestMethod httpRequestMethod)
		throws IOException {

		return toJSONObject(
			url, checkCache, _RETRIES_SIZE_MAX_DEFAULT, httpRequestMethod, null,
			_SECONDS_RETRY_PERIOD_DEFAULT, _MILLIS_TIMEOUT_DEFAULT, null);
	}

	public static JSONObject toJSONObject(
			String url, boolean checkCache, HttpRequestMethod httpRequestMethod,
			String postContent)
		throws IOException {

		return toJSONObject(
			url, checkCache, _RETRIES_SIZE_MAX_DEFAULT, httpRequestMethod,
			postContent, _SECONDS_RETRY_PERIOD_DEFAULT, _MILLIS_TIMEOUT_DEFAULT,
			null);
	}

	public static JSONObject toJSONObject(
			String url, boolean checkCache, int timeout)
		throws IOException {

		return toJSONObject(
			url, checkCache, _RETRIES_SIZE_MAX_DEFAULT, null, null,
			_SECONDS_RETRY_PERIOD_DEFAULT, timeout, null);
	}

	public static JSONObject toJSONObject(
			String url, boolean checkCache, int maxRetries,
			HttpRequestMethod httpRequestMethod, String postContent,
			int retryPeriod, int timeout, HTTPAuthorization httpAuthorization)
		throws IOException {

		Retryable<JSONObject> retryable = new Retryable<JSONObject>(
			true, maxRetries, retryPeriod, true) {

			@Override
			public JSONObject execute() {
				try {
					String response = JenkinsResultsParserUtil.toString(
						url, checkCache, 0, httpRequestMethod, postContent,
						retryPeriod, timeout, httpAuthorization, true);

					if ((response == null) ||
						response.endsWith("was truncated due to its size.")) {

						return null;
					}

					return createJSONObject(response);
				}
				catch (IOException ioException) {
					throw new RuntimeException(ioException);
				}
			}

			@Override
			protected String getRetryMessage(int retryCount) {
				return combine(
					"Unable to create JSONObject: ",
					super.getRetryMessage(retryCount));
			}

		};

		try {
			return retryable.executeWithRetries();
		}
		catch (Exception exception) {
			throw new RuntimeException("Unable to create JSON object");
		}
	}

	public static JSONObject toJSONObject(
			String url, boolean checkCache, int maxRetries, int retryPeriod,
			int timeout)
		throws IOException {

		return toJSONObject(
			url, checkCache, maxRetries, null, null, retryPeriod, timeout,
			null);
	}

	public static JSONObject toJSONObject(
			String url, boolean checkCache, int maxRetries, String postContent,
			int retryPeriod, int timeout)
		throws IOException {

		return toJSONObject(
			url, checkCache, maxRetries, null, postContent, retryPeriod,
			timeout, null);
	}

	public static JSONObject toJSONObject(
			String url, HTTPAuthorization httpAuthorization)
		throws IOException {

		if ((url != null) && url.startsWith("file:")) {
			try {
				return toJSONObject(
					url, false, 1, null, null, 0, 5, httpAuthorization);
			}
			catch (IOException ioException) {
				if (!url.contains("[qt]")) {
					throw ioException;
				}

				return toJSONObject(
					url.substring(0, url.indexOf("[qt]")), httpAuthorization);
			}
		}

		return toJSONObject(
			url, true, _RETRIES_SIZE_MAX_DEFAULT, null, null,
			_SECONDS_RETRY_PERIOD_DEFAULT, _MILLIS_TIMEOUT_DEFAULT,
			httpAuthorization);
	}

	public static JSONObject toJSONObject(
			String url, int maxRetries, int retryPeriod, String postContent)
		throws IOException {

		return toJSONObject(
			url, true, maxRetries, null, postContent, retryPeriod,
			_MILLIS_TIMEOUT_DEFAULT, null);
	}

	public static JSONObject toJSONObject(
			String url, int maxRetries, int retryPeriod, String postContent,
			HTTPAuthorization httpAuthorization)
		throws IOException {

		return toJSONObject(
			url, true, maxRetries, null, postContent, retryPeriod,
			_MILLIS_TIMEOUT_DEFAULT, httpAuthorization);
	}

	public static JSONObject toJSONObject(String url, String postContent)
		throws IOException {

		return toJSONObject(
			url, false, _RETRIES_SIZE_MAX_DEFAULT, null, postContent,
			_SECONDS_RETRY_PERIOD_DEFAULT, _MILLIS_TIMEOUT_DEFAULT, null);
	}

	public static JSONObject toJSONObject(
			String url, String postContent, HTTPAuthorization httpAuthorization)
		throws IOException {

		return toJSONObject(
			url, false, _RETRIES_SIZE_MAX_DEFAULT, null, postContent,
			_SECONDS_RETRY_PERIOD_DEFAULT, _MILLIS_TIMEOUT_DEFAULT,
			httpAuthorization);
	}

	public static PathMatcher toPathMatcher(String prefix, String glob) {
		FileSystem fileSystem = FileSystems.getDefault();

		return fileSystem.getPathMatcher(combine("glob:", prefix, glob));
	}

	public static List<PathMatcher> toPathMatchers(
		String prefix, List<String> globs) {

		return toPathMatchers(prefix, globs.toArray(new String[0]));
	}

	public static List<PathMatcher> toPathMatchers(
		String prefix, String... globs) {

		if (prefix == null) {
			prefix = "";
		}

		FileSystem fileSystem = FileSystems.getDefault();

		List<PathMatcher> pathMatchers = new ArrayList<>(globs.length);

		for (String glob : globs) {
			pathMatchers.add(
				fileSystem.getPathMatcher(combine("glob:", prefix, glob)));
		}

		return pathMatchers;
	}

	public static Properties toProperties(String url) throws IOException {
		Properties properties = new Properties();

		properties.load(new StringReader(toString(url)));

		return new SecureProperties(properties);
	}

	public static String toString(String url) throws IOException {
		return toString(
			url, true, _RETRIES_SIZE_MAX_DEFAULT, null, null,
			_SECONDS_RETRY_PERIOD_DEFAULT, _MILLIS_TIMEOUT_DEFAULT, null,
			false);
	}

	public static String toString(String url, boolean checkCache)
		throws IOException {

		return toString(
			url, checkCache, _RETRIES_SIZE_MAX_DEFAULT, null, null,
			_SECONDS_RETRY_PERIOD_DEFAULT, _MILLIS_TIMEOUT_DEFAULT, null,
			false);
	}

	public static String toString(
			String url, boolean checkCache, boolean requireResponse)
		throws IOException {

		return toString(
			url, checkCache, _RETRIES_SIZE_MAX_DEFAULT, null, null,
			_SECONDS_RETRY_PERIOD_DEFAULT, _MILLIS_TIMEOUT_DEFAULT, null,
			requireResponse);
	}

	public static String toString(
			String url, boolean checkCache, HttpRequestMethod httpRequestMethod)
		throws IOException {

		return toString(
			url, checkCache, _RETRIES_SIZE_MAX_DEFAULT, httpRequestMethod, null,
			_SECONDS_RETRY_PERIOD_DEFAULT, _MILLIS_TIMEOUT_DEFAULT, null,
			false);
	}

	public static String toString(
			String url, boolean checkCache, HttpRequestMethod httpRequestMethod,
			String postContent)
		throws IOException {

		return toString(
			url, checkCache, _RETRIES_SIZE_MAX_DEFAULT, httpRequestMethod,
			postContent, _SECONDS_RETRY_PERIOD_DEFAULT, _MILLIS_TIMEOUT_DEFAULT,
			null, false);
	}

	public static String toString(
			String url, boolean checkCache, HttpRequestMethod httpRequestMethod,
			String postContent, HTTPAuthorization httpAuthorization)
		throws IOException {

		return toString(
			url, checkCache, _RETRIES_SIZE_MAX_DEFAULT, httpRequestMethod,
			postContent, _SECONDS_RETRY_PERIOD_DEFAULT, _MILLIS_TIMEOUT_DEFAULT,
			httpAuthorization, false);
	}

	public static String toString(String url, boolean checkCache, int timeout)
		throws IOException {

		return toString(
			url, checkCache, _RETRIES_SIZE_MAX_DEFAULT, null, null,
			_SECONDS_RETRY_PERIOD_DEFAULT, timeout, null, false);
	}

	public static String toString(
			String url, boolean checkCache, int maxRetries,
			HttpRequestMethod httpRequestMethod, String postContent,
			int retryPeriod, int timeout, HTTPAuthorization httpAuthorization,
			boolean expectResponse)
		throws IOException {

		long start = System.currentTimeMillis();

		try {
			for (int i = 0; i < 2; i++) {
				try (BufferedReader bufferedReader = toBufferedReader(
						url, checkCache, maxRetries, httpRequestMethod,
						postContent, retryPeriod, timeout, httpAuthorization)) {

					StringBuilder sb = new StringBuilder();

					String line = bufferedReader.readLine();

					while (line != null) {
						sb.append(line);
						sb.append("\n");

						line = bufferedReader.readLine();
					}

					String content = sb.toString();

					if (expectResponse && isNullOrEmpty(content) && (i < 1)) {
						System.out.println(
							"Unable to get response, retrying request");

						continue;
					}

					if (checkCache && !url.startsWith("file:")) {
						saveToCacheFile(
							getCacheFileKey(url, postContent), content);
					}

					return content;
				}
			}

			return "";
		}
		finally {
			long duration = System.currentTimeMillis() - start;

			if (duration > (1000 * 60 * 5)) {
				System.out.println(
					combine(
						"HTTP call took ", toDurationString(duration),
						". URL: ", url));
			}
		}
	}

	public static String toString(
			String url, boolean checkCache, int maxRetries,
			int retryPeriodSeconds, int timeout)
		throws IOException {

		return toString(
			url, checkCache, maxRetries, null, null, retryPeriodSeconds,
			timeout, null, false);
	}

	public static String toString(
			String url, boolean checkCache, int maxRetries,
			int retryPeriodSeconds, int timeout, boolean expectResponse)
		throws IOException {

		return toString(
			url, checkCache, maxRetries, null, null, retryPeriodSeconds,
			timeout, null, expectResponse);
	}

	public static String toString(
			String url, boolean checkCache, int maxRetries, String postContent,
			int retryPeriod, int timeout)
		throws IOException {

		return toString(
			url, checkCache, maxRetries, null, postContent, retryPeriod,
			timeout, null, false);
	}

	public static String toString(
			String url, HttpRequestMethod httpRequestMethod)
		throws IOException {

		return toString(
			url, true, _RETRIES_SIZE_MAX_DEFAULT, httpRequestMethod, null,
			_SECONDS_RETRY_PERIOD_DEFAULT, _MILLIS_TIMEOUT_DEFAULT, null,
			false);
	}

	public static String toString(
			String url, HttpRequestMethod httpRequestMethod, String postContent)
		throws IOException {

		return toString(
			url, false, _RETRIES_SIZE_MAX_DEFAULT, httpRequestMethod,
			postContent, _SECONDS_RETRY_PERIOD_DEFAULT, _MILLIS_TIMEOUT_DEFAULT,
			null, false);
	}

	public static String toString(String url, String postContent)
		throws IOException {

		return toString(
			url, false, _RETRIES_SIZE_MAX_DEFAULT, null, postContent,
			_SECONDS_RETRY_PERIOD_DEFAULT, _MILLIS_TIMEOUT_DEFAULT, null,
			false);
	}

	public static String toString(
			String url, String postContent, HTTPAuthorization httpAuthorization)
		throws IOException {

		return toString(
			url, false, _RETRIES_SIZE_MAX_DEFAULT, null, postContent,
			_SECONDS_RETRY_PERIOD_DEFAULT, _MILLIS_TIMEOUT_DEFAULT,
			httpAuthorization, false);
	}

	public static void unGzip(File sourceGzipFile, File targetFile) {
		try (FileOutputStream fileOutputStream = new FileOutputStream(
				targetFile);

			FileInputStream fileInputStream = new FileInputStream(
				sourceGzipFile);

			GZIPInputStream gzipInputStream = new GZIPInputStream(
				fileInputStream)) {

			byte[] bytes = new byte[1024];
			int length = 0;

			while ((length = gzipInputStream.read(bytes)) > 0) {
				fileOutputStream.write(bytes, 0, length);
			}
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}
	}

	public static void unTarGzip(File sourceTarGzipFile, File targetDir) {
		targetDir.mkdirs();

		try {
			executeBashCommands(
				combine(
					"tar --directory=", getCanonicalPath(targetDir),
					" --extract --file=", getCanonicalPath(sourceTarGzipFile),
					" --gzip"));
		}
		catch (IOException | TimeoutException exception) {
			throw new RuntimeException(exception);
		}
	}

	public static void unzip(File zipFile, File destDir) {
		destDir.mkdirs();

		try {
			executeBashCommands(
				combine(
					"unzip -o -q ", getCanonicalPath(zipFile), " -d ",
					getCanonicalPath(destDir)));
		}
		catch (IOException | TimeoutException exception) {
			throw new RuntimeException(exception);
		}
	}

	public static void updateBuildDescription(
		String buildDescription, int buildNumber, String jobName,
		final String masterHostname) {

		buildDescription = buildDescription.replaceAll("\"", "\\\\\"");
		buildDescription = buildDescription.replaceAll("\'", "\\\\\'");

		jobName = jobName.replace("%28", "(");
		jobName = jobName.replace("%29", ")");

		final String jenkinsScript = combine(
			"def job = Jenkins.instance.getItemByFullName(\"", jobName,
			"\"); def build = job.getBuildByNumber(",
			String.valueOf(buildNumber), "); build.description = \"",
			buildDescription, "\";");

		Retryable<Object> retryable = new Retryable<Object>(true, 3, 3, true) {

			@Override
			public Object execute() {
				String responseText = executeJenkinsScript(
					masterHostname, jenkinsScript);

				if (responseText == null) {
					throw new RuntimeException(
						"Unable to update build description");
				}

				return responseText;
			}

			@Override
			protected String getRetryMessage(int retryCount) {
				return combine(
					"Unable to update build description: ",
					super.getRetryMessage(retryCount));
			}

		};

		try {
			retryable.executeWithRetries();
		}
		catch (Exception exception) {
			throw new RuntimeException(
				combine(
					"Unable to update build description to \"",
					buildDescription, "\""),
				exception);
		}
	}

	public static void updateBuildDescription(
		String buildDescription, URL buildURL) {

		Matcher matcher = _buildURLPattern.matcher(String.valueOf(buildURL));

		if (!matcher.find()) {
			throw new RuntimeException("Invalid Build URL");
		}

		updateBuildDescription(
			buildDescription, Integer.valueOf(matcher.group("buildNumber")),
			matcher.group("jobName"), matcher.group("masterHostname"));
	}

	public static void updateBuildResult(
		int buildNumber, String buildResult, String jobName,
		String masterHostname) {

		String jenkinsScript = combine(
			"def job = Jenkins.instance.getItemByFullName(\"", jobName, "\"); ",
			"def build = job.getBuildByNumber(", String.valueOf(buildNumber),
			"); build.@result = hudson.model.Result.", buildResult, ";");

		executeJenkinsScript(masterHostname, jenkinsScript);
	}

	public static void validatePQL(String pql, File file) {
		try {
			PQLEntityFactory.newPQLEntity(pql);
		}
		catch (Exception exception) {
			throw new RuntimeException(
				"Invalid PQL at " + file + ": " + pql, exception);
		}
	}

	public static void write(File file, String content) throws IOException {
		if (debug) {
			System.out.println(
				"Write file " + file + " with length " + content.length());
		}

		if (file.exists()) {
			file.delete();
		}

		append(file, content);
	}

	public static void write(String path, String content) throws IOException {
		if (path.startsWith(BaseBuild.DEPENDENCIES_URL_TOKEN)) {
			path = path.replace(
				BaseBuild.DEPENDENCIES_URL_TOKEN,
				urlDependenciesFile.replace("file:", ""));
		}

		write(new File(path), content);
	}

	public static void writePropertiesFile(
		File propertiesFile, Properties properties, boolean verbose) {

		if (propertiesFile.exists()) {
			propertiesFile.delete();
		}

		File parentFile = propertiesFile.getParentFile();

		if (!parentFile.exists()) {
			parentFile.mkdirs();
		}

		if (properties.isEmpty()) {
			return;
		}

		StringBuilder sb = new StringBuilder();

		sb.append("## Autogenerated\n\n");

		Set<String> propertyNames = new TreeSet<>(
			properties.stringPropertyNames());

		for (String propertyName : propertyNames) {
			sb.append(propertyName);
			sb.append("=");

			sb.append(
				StringEscapeUtils.escapeJava(
					getProperty(properties, propertyName)));

			sb.append("\n");
		}

		try {
			write(propertiesFile, sb.toString());
		}
		catch (IOException ioException) {
			System.out.println(
				"Unable to write properties file " + propertiesFile);

			ioException.printStackTrace();
		}

		if (verbose) {
			System.out.println("#");
			System.out.println("# " + propertiesFile);
			System.out.println("#\n");

			try {
				System.out.println(read(propertiesFile));
			}
			catch (IOException ioException) {
				throw new RuntimeException(
					"Unable to read properties file " + propertiesFile,
					ioException);
			}
		}
	}

	public static void writeSHAFile(File file, File shaFile) {
		try {
			if (shaFile.exists()) {
				delete(shaFile);
			}

			write(shaFile, getSHA(file));

			if (!isMatchingSHAFile(file, shaFile)) {
				throw new RuntimeException("Unable to create SHA file");
			}
		}
		catch (IOException ioException) {
			throw new RuntimeException(
				"Unable to create SHA file", ioException);
		}
	}

	public static void zip(final File sourceDir, File zipFile) {
		try (FileOutputStream fileOutputStream = new FileOutputStream(zipFile);

			final ZipOutputStream zipOutputStream = new ZipOutputStream(
				fileOutputStream)) {

			Files.walkFileTree(
				sourceDir.toPath(),
				new SimpleFileVisitor<Path>() {

					@Override
					public FileVisitResult visitFile(
						Path path, BasicFileAttributes basicFileAttributes) {

						if (basicFileAttributes.isSymbolicLink()) {
							return FileVisitResult.CONTINUE;
						}

						try (FileInputStream fileInputStream =
								new FileInputStream(path.toFile())) {

							zipOutputStream.putNextEntry(
								new ZipEntry(
									getPathRelativeTo(
										path.toFile(), sourceDir)));

							byte[] bytes = new byte[1024];
							int len;

							while ((len = fileInputStream.read(bytes)) > 0) {
								zipOutputStream.write(bytes, 0, len);
							}

							zipOutputStream.closeEntry();
						}
						catch (IOException ioException) {
							throw new RuntimeException(ioException);
						}

						return FileVisitResult.CONTINUE;
					}

				});
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}
	}

	public static class BasicHTTPAuthorization extends HTTPAuthorization {

		public BasicHTTPAuthorization(String password, String username) {
			super(Type.BASIC);

			this.password = password;
			this.username = username;
		}

		@Override
		public String toString() {
			String authorization = combine(username, ":", password);

			return combine(
				"Basic ", Base64.encodeBase64String(authorization.getBytes()));
		}

		protected String password;
		protected String username;

	}

	public static class BearerHTTPAuthorization extends HTTPAuthorization {

		public BearerHTTPAuthorization(String token) {
			super(Type.BEARER);

			this.token = token;
		}

		@Override
		public String toString() {
			return "Bearer " + token;
		}

		protected String token;

	}

	public static class ClientCredentialsHTTPAuthorization
		extends HTTPAuthorization {

		public ClientCredentialsHTTPAuthorization(
			String clientId, String clientSecret, URL tokenURL) {

			super(Type.CLIENT_CREDENTIALS);

			_clientId = clientId;
			_clientSecret = clientSecret;
			_tokenURL = tokenURL;

			if (isCINode() || isJenkinsMaster()) {
				return;
			}

			System.out.println(
				combine(
					"Configuring client credentials:\n* Client ID: ",
					_getMaskedString(clientId), "\n* Client secret: ",
					_getMaskedString(clientSecret), "\n* Token URL: ",
					String.valueOf(_tokenURL)));
		}

		@Override
		public String toString() {
			_refreshToken();

			return _tokenType + " " + _token;
		}

		private String _getMaskedString(String string) {
			if (string == null) {
				return null;
			}

			if (string.length() <= 10) {
				return "*****";
			}

			return string.substring(0, 10) + "...";
		}

		private void _refreshToken() {
			Date currentDate = new Date();

			if ((_tokenExpirationDate != null) &&
				currentDate.before(_tokenExpirationDate)) {

				return;
			}

			StringBuilder sb = new StringBuilder();

			sb.append("grant_type=client_credentials&client_id=");
			sb.append(_clientId);
			sb.append("&client_secret=");
			sb.append(_clientSecret);

			try {
				JSONObject jsonObject = toJSONObject(
					String.valueOf(_tokenURL), sb.toString());

				_token = jsonObject.getString("access_token");
				_tokenExpirationDate = new Date(
					currentDate.getTime() +
						((jsonObject.optInt("expires_in", 600) - 60) * 1000L));
				_tokenType = jsonObject.getString("token_type");
			}
			catch (IOException ioException) {
				throw new RuntimeException(ioException);
			}
		}

		private final String _clientId;
		private final String _clientSecret;
		private String _token;
		private Date _tokenExpirationDate;
		private String _tokenType;
		private final URL _tokenURL;

	}

	public abstract static class HTTPAuthorization {

		public Type getType() {
			return type;
		}

		public static enum Type {

			BASIC, BEARER, CLIENT_CREDENTIALS, TOKEN

		}

		protected HTTPAuthorization(Type type) {
			this.type = type;
		}

		protected Type type;

	}

	public static enum HttpRequestMethod {

		DELETE, GET, HEAD, OPTIONS, PATCH, POST, PUT, TRACE

	}

	public static class PropertyNameComparator implements Comparator<String> {

		@Override
		public int compare(String propertyName1, String propertyName2) {
			if ((propertyName1 == null) || (propertyName2 == null)) {
				return ObjectUtils.compare(propertyName1, propertyName2);
			}

			Set<String> propertyOptSet1 = _getPropertyOptSet(propertyName1);
			Set<String> propertyOptSet2 = _getPropertyOptSet(propertyName2);

			if (propertyOptSet1.size() != propertyOptSet2.size()) {
				return Integer.compare(
					propertyOptSet2.size(), propertyOptSet1.size());
			}

			int propertyOptRegexCount1 = _countPropertyOptRegexes(
				propertyOptSet1);
			int propertyOptRegexCount2 = _countPropertyOptRegexes(
				propertyOptSet2);

			if (propertyOptRegexCount1 != propertyOptRegexCount2) {
				return Integer.compare(
					propertyOptRegexCount1, propertyOptRegexCount2);
			}

			if (propertyName1.length() != propertyName2.length()) {
				return Integer.compare(
					propertyName2.length(), propertyName1.length());
			}

			return propertyName2.compareTo(propertyName1);
		}

		private int _countPropertyOptRegexes(Set<String> propertyOptSet) {
			int count = 0;

			for (String propertyOpt : propertyOptSet) {
				if (propertyOpt.contains(".*")) {
					count++;
				}
			}

			return count;
		}

	}

	public static class TokenHTTPAuthorization extends HTTPAuthorization {

		public TokenHTTPAuthorization(String token) {
			super(Type.TOKEN);

			this.token = token;
		}

		@Override
		public String toString() {
			return combine("token ", token);
		}

		protected String token;

	}

	protected static String getFilteredPropertyValue(String propertyValue) {
		if (propertyValue == null) {
			return null;
		}

		List<String> propertyValues = new ArrayList<>();

		for (String value : propertyValue.split("\\s*,\\s*")) {
			if (!value.startsWith("#")) {
				propertyValues.add(value);
			}
		}

		return join(",", propertyValues);
	}

	protected static String urlDependenciesFile;

	static {
		File dependenciesDir = new File("src/test/resources/dependencies/");

		try {
			URI uri = dependenciesDir.toURI();

			URL url = uri.toURL();

			urlDependenciesFile = url.toString();
		}
		catch (MalformedURLException malformedURLException) {
			throw new RuntimeException(malformedURLException);
		}
	}

	private static long _appendStringForUnit(
		long duration, long millisInUnit, boolean round, StringBuilder sb,
		String unitDescriptionSingular, String unitDescriptionPlural) {

		if (duration >= millisInUnit) {
			long units = duration / millisInUnit;

			long remainder = duration % millisInUnit;

			if (round && (remainder >= (millisInUnit / 2))) {
				units++;
			}

			sb.append(units);

			sb.append(" ");

			sb.append(
				getNounForm(
					(int)units, unitDescriptionPlural,
					unitDescriptionSingular));

			sb.append(" ");

			return remainder;
		}

		return duration;
	}

	private static String _combineCommandArgs(String... args) {
		return join(" ", args);
	}

	private static void _executeCommandService(
		final String command, final File baseDir,
		final Map<String, String> environments, final long maxLogSize,
		final boolean batchCommand) {

		if (batchCommand && !isWindows()) {
			throw new RuntimeException("Invalid OS: " + SystemUtils.OS_NAME);
		}

		Runnable runnable = new Runnable() {

			public void run() {
				StringBuilder sb = new StringBuilder();

				try {
					if (isWindows()) {
						if (batchCommand) {
							sb.append("cmd.exe /c ");
						}
						else {
							sb.append("C:\\Program Files\\Git\\bin\\sh.exe ");
						}
					}
					else {
						sb.append("/bin/sh ");
					}

					sb.append(command);

					String bashCommand = sb.toString();

					if (bashCommand.isEmpty()) {
						throw new IllegalArgumentException("Empty command");
					}

					StringTokenizer stringTokenizer = new StringTokenizer(
						bashCommand);

					String[] commands =
						new String[stringTokenizer.countTokens()];

					for (int i = 0; stringTokenizer.hasMoreTokens(); i++) {
						commands[i] = stringTokenizer.nextToken();
					}

					ProcessBuilder processBuilder = new ProcessBuilder(
						commands);

					Map<String, String> processEnvironment =
						processBuilder.environment();

					processEnvironment.clear();

					processEnvironment.putAll(environments);

					processBuilder.directory(baseDir);

					// Merge the error stream into the output stream so the
					// single reader below drains both. An unread error stream
					// blocks the process once its buffer fills.

					processBuilder.redirectErrorStream(true);

					Process process = processBuilder.start();

					try (CountingInputStream countingInputStream =
							new CountingInputStream(process.getInputStream());

						InputStreamReader inputStreamReader =
							new InputStreamReader(
								countingInputStream, StandardCharsets.UTF_8)) {

						int logCharInt = 0;

						while ((logCharInt = inputStreamReader.read()) != -1) {
							if (countingInputStream.getCount() > maxLogSize) {
								continue;
							}

							System.out.print((char)logCharInt);
						}
					}

					process.waitFor();
				}
				catch (InterruptedException | IOException exception) {
					throw new RuntimeException(exception);
				}
			}

		};

		Thread thread = new Thread(runnable);

		thread.start();
	}

	private static String _fixFilePathPropertyValue(
		File propertyFile, String propertyValue) {

		StringBuilder sb = new StringBuilder();

		String[] paths = propertyValue.split("\\s*,\\s*");

		for (String path : paths) {
			File file = new File(propertyFile.getParentFile(), path);

			if (file.exists()) {
				try {
					sb.append(file.getCanonicalPath());
				}
				catch (IOException ioException) {
					throw new RuntimeException(ioException);
				}
			}
			else {
				sb.append(path);
			}

			if (sb.length() > 0) {
				sb.append(",");
			}
		}

		return sb.toString();
	}

	private static synchronized String _getCacheURL() {
		if (_cacheURL != null) {
			return _cacheURL;
		}

		File jenkinsRepositoryDir = getJenkinsRepositoryDir();

		File cacheDir = jenkinsRepositoryDir.getParentFile();

		if (cacheDir.exists() && jenkinsRepositoryDir.exists()) {
			String cacheDirPath = cacheDir.getPath();

			System.out.println("Using " + cacheDirPath + " for cached files");

			_cacheURL = "file://" + cacheDirPath;

			return _cacheURL;
		}

		if (isCINode()) {
			_cacheURL = _URL_CACHE_MIRROR_DEFAULT;

			System.out.println("Using " + _cacheURL + " for cached files");

			return _cacheURL;
		}

		return null;
	}

	private static String _getCanonicalPath(File canonicalFile) {
		File parentCanonicalFile = canonicalFile.getParentFile();

		if (parentCanonicalFile == null) {
			String absolutePath = canonicalFile.getAbsolutePath();

			return absolutePath.substring(
				0, absolutePath.indexOf(File.separator));
		}

		String parentFileCanonicalPath = _getCanonicalPath(parentCanonicalFile);

		return combine(parentFileCanonicalPath, "/", canonicalFile.getName());
	}

	private static String[] _getDefaultBuildPropertiesURLs() {
		String jenkinsRepositoryURL = _getJenkinsRepositoryURL();

		return new String[] {
			jenkinsRepositoryURL + "/build.properties",
			jenkinsRepositoryURL + "/commands/build.properties"
		};
	}

	private static Pattern _getDistPortalBundleFileNamesPattern(
		String portalBranchName) {

		try {
			String distPortalBundleFileNames = getProperty(
				getBuildProperties(), "dist.portal.bundle.file.names",
				portalBranchName);

			if (distPortalBundleFileNames == null) {
				distPortalBundleFileNames =
					_DIST_PORTAL_BUNDLE_FILE_NAMES_DEFAULT;
			}

			StringBuilder sb = new StringBuilder();

			List<String> distPortalBundleFileNamesList = Lists.newArrayList(
				distPortalBundleFileNames.split("\\s*,\\s*"));

			Collections.sort(distPortalBundleFileNamesList);

			for (String distPortalBundleFileName :
					distPortalBundleFileNamesList) {

				String quotedDistPortalBundleFileName = Pattern.quote(
					distPortalBundleFileName);

				sb.append("\\<a href=\"");
				sb.append(quotedDistPortalBundleFileName);
				sb.append("\"\\>");
				sb.append(quotedDistPortalBundleFileName);
				sb.append("\\</a\\>.*");
			}

			sb.setLength(sb.length() - 2);

			return Pattern.compile(sb.toString(), Pattern.DOTALL);
		}
		catch (IOException ioException) {
			throw new RuntimeException(
				"Unable to load build properties", ioException);
		}
	}

	private static String _getDistPortalBundlesURL(String portalBranchName) {
		try {
			String distPortalBundlesURL = getProperty(
				getBuildProperties(), "dist.portal.bundles.url",
				portalBranchName);

			if (distPortalBundlesURL != null) {
				return distPortalBundlesURL;
			}
		}
		catch (IOException ioException) {
			System.out.println("WARNING: " + ioException.getMessage());
		}

		return combine(
			_DIST_PORTAL_BUNDLES_URL_DEFAULT, "(", portalBranchName, ")/");
	}

	private static String _getDistPortalJobURL(String portalBranchName) {
		try {
			String distPortalJobURL = getProperty(
				getBuildProperties(), "dist.portal.job.url", portalBranchName);

			if (distPortalJobURL != null) {
				return distPortalJobURL;
			}
		}
		catch (IOException ioException) {
			System.out.println("WARNING: " + ioException.getMessage());
		}

		try {
			return combine(
				"http://",
				getBuildProperty("upstream.acceptance.jenkins.master"),
				"/job/test-portal-acceptance-upstream", "(", portalBranchName,
				")");
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}
	}

	private static synchronized JSONArray _getGitDirectoriesJSONArray() {
		if (_gitDirectoriesJSONArray != null) {
			return _gitDirectoriesJSONArray;
		}

		_gitDirectoriesJSONArray = new JSONArray();

		String url = _getJenkinsRepositoryURL() + "/git-directories.json";

		JSONArray jsonArray = null;

		try {
			if (url.startsWith("file://")) {
				jsonArray = new JSONArray(
					read(new File(url.replace("file://", ""))));
			}
			else {
				jsonArray = toJSONArray(getLocalURL(url), false);
			}
		}
		catch (IOException ioException) {
		}

		if (jsonArray != null) {
			for (int i = 0; i < jsonArray.length(); i++) {
				_gitDirectoriesJSONArray.put(jsonArray.get(i));
			}
		}

		return _gitDirectoriesJSONArray;
	}

	private static JSONObject _getGitDirectoryJSONObject(
		String gitDirectoryName) {

		JSONObject jsonObject = _getGitDirectoryJSONObject(
			gitDirectoryName, _getGitDirectoriesJSONArray());

		if (jsonObject == null) {
			jsonObject = _getGitDirectoryJSONObject(
				gitDirectoryName, _getGitWorkingDirectoriesJSONArray());
		}

		return jsonObject;
	}

	private static JSONObject _getGitDirectoryJSONObject(
		String gitDirectoryName, JSONArray gitDirectoriesJSONArray) {

		for (int i = 0; i < gitDirectoriesJSONArray.length(); i++) {
			JSONObject gitDirectoryJSONObject =
				gitDirectoriesJSONArray.getJSONObject(i);

			if ((gitDirectoryJSONObject == JSONObject.NULL) ||
				!gitDirectoryName.equals(
					gitDirectoryJSONObject.getString("name"))) {

				continue;
			}

			return gitDirectoryJSONObject;
		}

		return null;
	}

	private static String _getGitDirectoryName(
		String repositoryName, String upstreamBranchName,
		JSONArray gitDirectoriesJSONArray) {

		for (int i = 0; i < gitDirectoriesJSONArray.length(); i++) {
			JSONObject gitDirectoryJSONObject =
				gitDirectoriesJSONArray.getJSONObject(i);

			if ((gitDirectoryJSONObject == JSONObject.NULL) ||
				!repositoryName.matches(
					gitDirectoryJSONObject.getString("repository")) ||
				!upstreamBranchName.matches(
					gitDirectoryJSONObject.getString("branch"))) {

				continue;
			}

			return gitDirectoryJSONObject.getString("name");
		}

		return null;
	}

	private static synchronized JSONArray _getGitWorkingDirectoriesJSONArray() {
		if (_gitWorkingDirectoriesJSONArray != null) {
			return _gitWorkingDirectoriesJSONArray;
		}

		_gitWorkingDirectoriesJSONArray = new JSONArray();

		String url =
			_getJenkinsRepositoryURL() + "/git-working-directories.json";

		JSONArray jsonArray = null;

		try {
			if (url.startsWith("file://")) {
				jsonArray = new JSONArray(
					read(new File(url.replace("file://", ""))));
			}
			else {
				jsonArray = toJSONArray(getLocalURL(url), false);
			}
		}
		catch (IOException ioException) {
		}

		if (jsonArray != null) {
			for (int i = 0; i < jsonArray.length(); i++) {
				_gitWorkingDirectoriesJSONArray.put(jsonArray.get(i));
			}
		}

		return _gitWorkingDirectoriesJSONArray;
	}

	private static String _getJenkinsRepositoryURL() {
		return _getCacheURL() + "/" + JENKINS_REPOSITORY_NAME;
	}

	private static Set<Set<String>> _getOrderedOptSets(String... opts) {
		Set<Set<String>> optSets = new LinkedHashSet<>();

		optSets.add(new LinkedHashSet<>(Arrays.asList(opts)));

		int optCount = opts.length;

		for (int i = optCount - 1; i >= 0; i--) {
			String[] childOpts = new String[optCount - 1];

			if (childOpts.length == 0) {
				continue;
			}

			for (int j = 0; j < optCount; j++) {
				if (j < i) {
					childOpts[j] = opts[j];
				}
				else if (j > i) {
					childOpts[j - 1] = opts[j];
				}
			}

			optSets.addAll(_getOrderedOptSets(childOpts));
		}

		Set<Set<String>> orderedOptSet = new LinkedHashSet<>();

		for (int i = optCount; i > 0; i--) {
			for (Set<String> optSet : optSets) {
				if (optSet.size() == i) {
					orderedOptSet.add(optSet);
				}
			}
		}

		return orderedOptSet;
	}

	private static Properties _getProperties(File basePropertiesFile) {
		if (!basePropertiesFile.exists()) {
			throw new RuntimeException(
				"Unable to find properties file " +
					basePropertiesFile.getPath());
		}

		List<File> propertiesFiles = new ArrayList<>();

		propertiesFiles.add(basePropertiesFile);

		String basePropertiesFileName = basePropertiesFile.getName();

		String[] environments = {
			Environment.get("HOSTNAME"), Environment.get("HOST"),
			Environment.get("COMPUTERNAME"), System.getProperty("user.name")
		};

		for (String environment : environments) {
			if (environment == null) {
				continue;
			}

			File environmentPropertyFile = new File(
				basePropertiesFile.getParentFile(),
				basePropertiesFileName.replace(
					".properties", "." + environment + ".properties"));

			if (environmentPropertyFile.exists()) {
				propertiesFiles.add(environmentPropertyFile);
			}
		}

		Properties properties = new Properties();

		String[] poshiDirPropertyNames = {"test.base.dir.name", "test.dirs"};

		for (File propertiesFile : propertiesFiles) {
			Properties temporaryProperties = new Properties();

			try {
				String urlString = EnvironmentBuildProperties.toURLString(
					propertiesFile);

				temporaryProperties.putAll(
					new EnvironmentBuildProperties(urlString));
			}
			catch (IOException ioException) {
				throw new RuntimeException(
					"Unable to load properties file " +
						basePropertiesFile.getPath(),
					ioException);
			}

			String propertiesFileName = propertiesFile.getName();

			if (propertiesFileName.equals("poshi-ext.properties") ||
				propertiesFileName.equals("poshi.properties")) {

				for (String poshiDirPropertyName : poshiDirPropertyNames) {
					if (temporaryProperties.containsKey(poshiDirPropertyName)) {
						temporaryProperties.setProperty(
							poshiDirPropertyName,
							_fixFilePathPropertyValue(
								propertiesFile,
								getProperty(
									temporaryProperties,
									poshiDirPropertyName)));
					}
				}
			}

			properties.putAll(temporaryProperties);
		}

		for (String propertyName : properties.stringPropertyNames()) {
			properties.setProperty(
				propertyName, getProperty(properties, propertyName));
		}

		return new SecureProperties(properties);
	}

	private static String _getProperty(
		Properties properties, List<String> previousNames, String name) {

		if (previousNames.contains(name)) {
			if (previousNames.size() > 1) {
				StringBuilder sb = new StringBuilder();

				sb.append("Circular property reference chain found\n");

				for (String previousName : previousNames) {
					sb.append(previousName);
					sb.append(" -> ");
				}

				sb.append(name);

				throw new IllegalStateException(sb.toString());
			}

			return combine("${", name, "}");
		}

		previousNames.add(name);

		if (!properties.containsKey(name)) {
			return null;
		}

		String value = getFilteredPropertyValue(properties.getProperty(name));

		Matcher matcher = _nestedPropertyPattern.matcher(value);

		String newValue = value;

		if (SecretsUtil.isSecretProperty(newValue)) {
			newValue = SecretsUtil.getSecret(newValue);
		}

		while (matcher.find()) {
			String propertyGroup = matcher.group(0);
			String propertyName = matcher.group(1);

			if (properties.containsKey(propertyName)) {
				newValue = newValue.replace(
					propertyGroup,
					_getProperty(
						properties, new ArrayList<>(previousNames),
						propertyName));
			}
		}

		return newValue;
	}

	private static Map<String, Set<String>> _getPropertyOptRegexSets(
		String basePropertyName, Set<String> propertyNames) {

		Map<String, Set<String>> propertyOptRegexSets = new LinkedHashMap<>();

		List<String> orderedPropertyNames = new ArrayList<>(propertyNames);

		Collections.sort(orderedPropertyNames, new PropertyNameComparator());

		Set<String> basePropertyOptSet = _getPropertyOptSet(basePropertyName);

		for (String propertyName : orderedPropertyNames) {
			Set<String> propertyOptSet = _getPropertyOptSet(propertyName);

			propertyOptSet.removeAll(basePropertyOptSet);

			propertyOptRegexSets.put(propertyName, propertyOptSet);
		}

		return propertyOptRegexSets;
	}

	private static Set<String> _getPropertyOptSet(String propertyName) {
		Set<String> propertyOptSet = new LinkedHashSet<>();

		List<Integer> indices = new ArrayList<>();
		Stack<Integer> stack = new Stack<>();

		Integer start = null;

		for (int i = 0; i < propertyName.length(); i++) {
			char c = propertyName.charAt(i);

			if (c == '[') {
				stack.push(i);

				if (start == null) {
					start = i;

					indices.add(start);
				}
			}

			if (c == ']') {
				if (start == null) {
					continue;
				}

				stack.pop();

				if (stack.isEmpty()) {
					indices.add(i);

					start = null;
				}
			}
		}

		for (int i = 0; i < indices.size(); i++) {
			int nextIndex = propertyName.length();

			if (indices.size() > (i + 1)) {
				nextIndex = indices.get(i + 1);
			}

			String opt = Pattern.quote(
				propertyName.substring(indices.get(i) + 1, nextIndex));

			propertyOptSet.add(opt.replaceAll("\\*", "\\\\E.*\\\\Q"));

			i++;
		}

		return propertyOptSet;
	}

	private static String _getRedactTokenKey(int index) {
		return "github.message.redact.token[" + index + "]";
	}

	private static Set<String> _getRedactTokenVariants(String token) {
		Set<String> redactTokenVariants = new HashSet<>();

		redactTokenVariants.add(token);

		String quotedToken = JSONObject.quote(token);

		redactTokenVariants.add(
			quotedToken.substring(1, quotedToken.length() - 1));

		try {
			String urlEncodedToken = URLEncoder.encode(
				token, StandardCharsets.UTF_8.name());

			redactTokenVariants.add(urlEncodedToken);
			redactTokenVariants.add(urlEncodedToken.replace("+", "%20"));
		}
		catch (UnsupportedEncodingException unsupportedEncodingException) {
			throw new RuntimeException(unsupportedEncodingException);
		}

		return redactTokenVariants;
	}

	private static String _getRyncPath(String hostName, String filePath) {
		if (hostName == null) {
			return filePath;
		}

		return hostName + ":" + filePath;
	}

	private static void _initializeRedactTokens() {
		Properties properties = null;

		try {
			properties = getBuildProperties();
		}
		catch (IOException ioException) {
			throw new RuntimeException(
				"Unable to get build properties", ioException);
		}

		for (int i = 1; properties.containsKey(_getRedactTokenKey(i)); i++) {
			String key = _getRedactTokenKey(i);

			String redactToken = getProperty(properties, key);

			if (isNullOrEmpty(redactToken) ||
				_forbiddenRedactTokens.contains(redactToken) ||
				redactToken.matches("^\\s*\\d{5}\\s*$")) {

				continue;
			}

			addRedactToken(redactToken);
		}
	}

	private static boolean _isJSONExpectedAndActualEqual(
		Object expected, Object actual) {

		if (actual instanceof JSONObject) {
			if (!(expected instanceof JSONObject) ||
				!isJSONObjectEqual((JSONObject)expected, (JSONObject)actual)) {

				return false;
			}
		}
		else if (actual instanceof JSONArray) {
			if (!(expected instanceof JSONArray) ||
				!isJSONArrayEqual((JSONArray)expected, (JSONArray)actual)) {

				return false;
			}
		}
		else {
			if (!actual.equals(expected)) {
				return false;
			}
		}

		return true;
	}

	private static final long _BYTES_GIGA = 1024 * 1024 * 1024;

	private static final long _BYTES_KILO = 1024;

	private static final long _BYTES_MEGA = 1024 * 1024;

	private static final String _DIST_PORTAL_BUNDLE_FILE_NAMES_DEFAULT =
		"git-hash,liferay-portal-bundle-tomcat.tar.gz," +
			"liferay-portal-source.tar.gz";

	private static final String _DIST_PORTAL_BUNDLES_URL_DEFAULT =
		"http://test-1-0/userContent/bundles/test-portal-acceptance-upstream";

	private static final String _JENKINS_DIST_ROOT_PATH_DEFAULT = "/tmp/dist";

	private static final long _MILLIS_BASH_COMMAND_TIMEOUT_DEFAULT =
		1000 * 60 * 60;

	private static final long _MILLIS_DAY = 24L * 60L * 60L * 1000L;

	private static final long _MILLIS_HOUR = 60L * 60L * 1000L;

	private static final long _MILLIS_MINUTE = 60L * 1000L;

	private static final long _MILLIS_SECOND = 1000L;

	private static final int _MILLIS_TIMEOUT_DEFAULT = 1000 * 60 * 5;

	private static final int _RETRIES_SIZE_MAX_DEFAULT = 3;

	private static final int _SECONDS_RETRY_PERIOD_DEFAULT = 5;

	private static final String _UPSTREAM_USER_NAME_DEFAULT = "liferay";

	private static final String _URL_CACHE_MIRROR_DEFAULT =
		"http://mirrors-no-cache.lax.liferay.com/github.com/liferay";

	private static final String _URL_JENKINS_GITHUB_DEFAULT =
		"https://github.com/liferay/liferay-jenkins-ee/tree/master";

	private static final String _URL_LOAD_BALANCER_DEFAULT =
		"http://cloud-10-0-0-31.lax.liferay.com/osb-jenkins-web/load_balancer";

	private static final String _URL_TEMP_MAP_DEFAULT =
		"http://cloud-10-0-0-31.lax.liferay.com/osb-jenkins-web/map";

	private static final Log _log = LogFactory.getLog(
		JenkinsResultsParserUtil.class);

	private static final Pattern _axisVariablePattern = Pattern.compile(
		".*AXIS_VARIABLE=(?<axisVariable>\\d+).*");
	private static final Pattern _buildIdPattern = Pattern.compile(
		"(?<cohortNumber>[\\d]{1})(?<masterNumber>[\\d]{2})" +
			"(?<jobId>[\\d]+)_(?<buildNumber>[\\d]+)");
	private static final Hashtable<Object, Object> _buildProperties =
		new Hashtable<>();
	private static String[] _buildPropertiesURLs;
	private static final Pattern _buildURLPattern = Pattern.compile(
		"http(?:|s):\\/\\/(?<masterHostname>test-(?<cohortNumber>[\\d]{1})-" +
			"(?<masterNumber>[\\d]{1,2})).*(?:|\\.liferay\\.com)\\/+job\\/+" +
				"(?<jobName>[\\w\\W]*?)\\/+(?<buildNumber>[0-9]*)");
	private static String _cacheURL;
	private static Boolean _ciNode;
	private static final Pattern _curlyBraceExpansionPattern = Pattern.compile(
		"\\{.*?\\}");
	private static Long _currentTimeMillisDelta;
	private static final Pattern _dockerFilePattern = Pattern.compile(
		".*FROM (?<dockerImageName>[^\\s]+)( AS builder)?\\n[\\s\\S]*");
	private static final Pattern _dockerImageNamePattern = Pattern.compile(
		"(?<ecrDockerImageName>((?<repository>[^/\\s]+)/)?" +
			"(?<name>[^/:\\s]+)(:(?<version>[^@:\\s]+))?)" +
				"(@sha256:[^\\s]+)?");
	private static final List<String> _forbiddenRedactTokens = Arrays.asList(
		"admin", "liferay", "test");
	private static JSONArray _gitDirectoriesJSONArray;
	private static final DateFormat _gitHubDateFormat;
	private static final Pattern _gitSHAPattern = Pattern.compile(
		"^([0-9a-fA-F]{6}|[0-9a-fA-F]{40}|[0-9a-fA-F]{64})$");
	private static JSONArray _gitWorkingDirectoriesJSONArray;
	private static final Pattern _javaVersionPattern = Pattern.compile(
		"(\\d+\\.\\d+)");
	private static final Pattern _jenkinsBuildQueueURLPattern = Pattern.compile(
		"https?://test-\\d+-\\d+(.liferay.com)?/queue/item/(?<queueId>\\d+)/?");
	private static final Pattern _jenkinsMasterPattern = Pattern.compile(
		"(?<cohortName>test-\\d+)-\\d+");
	private static Hashtable<?, ?> _jenkinsProperties;
	private static final Pattern _jenkinsReportURLPattern = Pattern.compile(
		"(?<masterURL>http(?:|s)://test-\\d+-\\d+.*(\\.liferay\\.com)?)" +
			"userContent/+jobs/+(?<jobName>[^/]+)/builds/" +
				"(?<buildNumber>\\d+)/+jenkins-report\\.html");
	private static final Pattern _jenkinsSlavesPropertyNamePattern =
		Pattern.compile("master.slaves\\((.+)\\)");
	private static final Map<String, Integer> _jobTimeoutMinutes =
		new HashMap<>();
	private static final Pattern _localURLAuthorityPattern1 = Pattern.compile(
		"http://(test-[0-9]+-[0])/");
	private static final Pattern _localURLAuthorityPattern2 = Pattern.compile(
		"http://(test-[0-9]+-[0-9]+)/");
	private static final Pattern _localURLAuthorityPattern3 = Pattern.compile(
		"https?://(mirrors/|mirrors.dlc.liferay.com/|mirrors.lax.liferay.com/" +
			")?((files|releases).liferay.com)");
	private static final Pattern _nestedPropertyPattern = Pattern.compile(
		"\\$\\{([^\\}]+)\\}");
	private static final Pattern _poshiFileNamePattern = Pattern.compile(
		".*\\.(function|macro|path|prose|testcase)");
	private static final Set<String> _redactTokens = new HashSet<>();
	private static final Pattern _remoteURLAuthorityPattern1 = Pattern.compile(
		"https://(test-[0-9]+-[0])-aws.liferay.com/");
	private static final Pattern _remoteURLAuthorityPattern2 = Pattern.compile(
		"https://(test-[0-9]+-[0-9]+).liferay.com/");
	private static final Pattern _remoteURLAuthorityPattern3 = Pattern.compile(
		"https?://(mirrors/|mirrors.dlc.liferay.com/|mirrors.lax.liferay.com/" +
			")?((files|releases).liferay.com)");
	private static final Pattern _shaPattern = Pattern.compile(
		"[0-9a-f]{7,40}");

	private static final File _sshDir = new File(
		JenkinsResultsParserUtil._userHomeDir, ".ssh") {

		{
			if (!exists()) {
				mkdirs();
			}
		}
	};

	private static final Pattern _test1MasterNamePattern = Pattern.compile(
		"test-1-(\\d+)");
	private static final Set<String> _timeStamps = new HashSet<>();
	private static Set<String> _topLevelJobNames;
	private static final Pattern _urlQueryStringPattern = Pattern.compile(
		"\\&??(\\w++)=([^\\&]*)");
	private static final File _userHomeDir = new File(
		System.getProperty("user.home"));

	static {
		_gitHubDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

		_gitHubDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

		try {
			_initializeRedactTokens();

			System.setErr(new SecurePrintStream(System.err));
			System.setOut(new SecurePrintStream(System.out));

			System.out.println("Securing standard error and out");
		}
		catch (Exception exception) {
		}
	}

	private static class ExecutionResultProcess extends Process {

		@Override
		public void destroy() {
		}

		@Override
		public int exitValue() {
			return _executionResult.getExitValue();
		}

		@Override
		public InputStream getErrorStream() {
			String standardError = _executionResult.getStandardError();

			return new ByteArrayInputStream(standardError.getBytes());
		}

		@Override
		public InputStream getInputStream() {
			String standardOut = _executionResult.getStandardOut();

			return new ByteArrayInputStream(standardOut.getBytes());
		}

		@Override
		public OutputStream getOutputStream() {
			return new ByteArrayOutputStream();
		}

		@Override
		public int waitFor() {
			return _executionResult.getExitValue();
		}

		private ExecutionResultProcess(Shell.ExecutionResult executionResult) {
			_executionResult = executionResult;
		}

		private final Shell.ExecutionResult _executionResult;

	}

}