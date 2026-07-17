/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dispatch.talend.archive;

import com.liferay.dispatch.talend.archive.exception.TalendArchiveException;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.Validator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;

import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * @author Igor Beslic
 */
public class TalendArchiveParserUtil {

	public static String getJVMOptions(
		String newJVMOptions, String oldJVMOptions) {

		String[] jvmOptions = newJVMOptions.split("\\s");

		StringBundler sb = new StringBundler((jvmOptions.length * 2) + 1);

		for (String newJVMOption : jvmOptions) {
			if (oldJVMOptions.contains(newJVMOption)) {
				continue;
			}

			sb.append(newJVMOption);

			sb.append(StringPool.SPACE);
		}

		sb.append(oldJVMOptions);

		return sb.toString();
	}

	public static TalendArchive parse(InputStream jobArchiveInputStream)
		throws PortalException {

		try {
			return _parse(jobArchiveInputStream);
		}
		catch (Exception exception) {
			throw new TalendArchiveException(
				"Unable to parse Talend archive", exception);
		}
	}

	public static void updateUnicodeProperties(
			InputStream jobArchiveInputStream,
			UnicodeProperties unicodeProperties)
		throws PortalException {

		TalendArchive talendArchive = parse(jobArchiveInputStream);

		try {
			if (talendArchive.hasJVMOptions()) {
				String newJVMOptions = talendArchive.getJVMOptions();

				String jvmOptions = unicodeProperties.get("JAVA_OPTS");

				if (jvmOptions != null) {
					newJVMOptions = getJVMOptions(newJVMOptions, jvmOptions);
				}

				unicodeProperties.put("JAVA_OPTS", newJVMOptions);
			}
			else {
				unicodeProperties.put("JAVA_OPTS", "-Xms256M -Xmx1024M");
			}

			Properties contextProperties = talendArchive.getContextProperties();

			for (String propertyName :
					contextProperties.stringPropertyNames()) {

				if (unicodeProperties.containsKey(propertyName)) {
					continue;
				}

				unicodeProperties.put(
					propertyName,
					contextProperties.getProperty(propertyName) +
						" (Automatic Copy)");
			}
		}
		finally {
			FileUtil.deltree(talendArchive.getJobDirectory());
		}
	}

	private static void _addJVMOptionsList(
		List<String> jvmOptionsList, String commandLine) {

		if (Validator.isNull(commandLine) || !commandLine.startsWith("java")) {
			return;
		}

		String[] tokens = commandLine.split("\\s");

		for (String token : tokens) {
			if (token.startsWith(StringPool.QUOTE) ||
				token.startsWith(StringPool.APOSTROPHE)) {

				token = token.substring(1);
			}

			if (token.endsWith(StringPool.QUOTE) ||
				token.endsWith(StringPool.APOSTROPHE)) {

				token = token.substring(0, token.length() - 1);
			}

			if (token.startsWith("-X")) {
				jvmOptionsList.add(token);
			}
		}
	}

	private static Properties _getContextProperties(
			String contextName, String jobExecutableJarPath)
		throws IOException {

		Properties properties = new Properties();

		String contextPropertiesSuffix = contextName + ".properties";

		try (ZipFile zipFile = new ZipFile(new File(jobExecutableJarPath))) {
			Enumeration<? extends ZipEntry> enumeration = zipFile.entries();

			while (enumeration.hasMoreElements()) {
				ZipEntry zipEntry = enumeration.nextElement();

				String name = zipEntry.getName();

				if (name.endsWith(contextPropertiesSuffix)) {
					properties.load(zipFile.getInputStream(zipEntry));
				}
			}
		}

		return properties;
	}

	private static File _getJobDirectory(InputStream jobArchiveInputStream)
		throws IOException {

		File tempFile = FileUtil.createTempFile(jobArchiveInputStream);

		try {
			File tempFolder = FileUtil.createTempFolder();

			FileUtil.unzip(tempFile, tempFolder);

			return tempFolder;
		}
		finally {
			if (tempFile != null) {
				FileUtil.delete(tempFile);
			}
		}
	}

	private static Path _getJobJarPath(
		String jobName, Path jobDirectoryPath, String jobVersion) {

		String jarName = StringBundler.concat(
			jobName, StringPool.SLASH, jobName, StringPool.UNDERLINE,
			StringUtil.replace(jobVersion, CharPool.PERIOD, CharPool.UNDERLINE),
			".jar");

		Path jobJarPath = jobDirectoryPath.resolve(
			StringUtil.toLowerCase(jarName));

		if (jobJarPath != null) {
			return jobJarPath;
		}

		throw new IllegalArgumentException(
			"Unable to determine job JAR directory for " + jobName);
	}

	private static List<String> _getJobLibEntries(Path jobDirectoryPath)
		throws IOException {

		List<String> pathStrings = new ArrayList<>();

		Files.walkFileTree(
			jobDirectoryPath.resolve("lib"),
			new SimpleFileVisitor<Path>() {

				@Override
				public FileVisitResult visitFile(
						Path filePath, BasicFileAttributes basicFileAttributes)
					throws IOException {

					String pathString = filePath.toString();

					if (pathString.endsWith(".jar")) {
						pathStrings.add(pathString);
					}

					return FileVisitResult.CONTINUE;
				}

			});

		pathStrings.sort(null);

		return pathStrings;
	}

	private static String _getJobMainClassFQN(
			String jobName, String jobExecutableJarPath)
		throws IOException {

		String mainClassSuffix = jobName + ".class";

		try (ZipFile zipFile = new ZipFile(new File(jobExecutableJarPath))) {
			Enumeration<? extends ZipEntry> enumeration = zipFile.entries();

			while (enumeration.hasMoreElements()) {
				ZipEntry zipEntry = enumeration.nextElement();

				String name = zipEntry.getName();

				if (name.endsWith(mainClassSuffix)) {
					name = name.substring(0, name.length() - 6);

					return StringUtil.replace(
						name, CharPool.SLASH, CharPool.PERIOD);
				}
			}
		}

		throw new IllegalArgumentException(
			"Unable to determine job main class");
	}

	private static Properties _getJobProperties(File jobDirectory)
		throws IOException {

		Properties properties = new Properties();

		try (InputStream inputStream = new FileInputStream(
				new File(jobDirectory, "jobInfo.properties"))) {

			properties.load(inputStream);
		}

		return properties;
	}

	private static List<String> _getJobScriptPathStrings(Path jobDirectoryPath)
		throws IOException {

		List<String> pathStrings = new ArrayList<>();

		Files.walkFileTree(
			jobDirectoryPath,
			new SimpleFileVisitor<Path>() {

				@Override
				public FileVisitResult visitFile(
					Path filePath, BasicFileAttributes basicFileAttributes) {

					String pathString = filePath.toString();

					if (pathString.endsWith(".bat") ||
						pathString.endsWith(".ps1") ||
						pathString.endsWith(".sh")) {

						pathStrings.add(pathString);
					}

					return FileVisitResult.CONTINUE;
				}

			});

		pathStrings.sort(null);

		return pathStrings;
	}

	private static List<String> _getJVMOptionsList(
			File jobDirectory, String jobName)
		throws IOException {

		List<String> jvmOptionsList = new ArrayList<>();

		Path path = jobDirectory.toPath();

		List<String> jobScriptPathStrings = _getJobScriptPathStrings(
			path.resolve(jobName));

		for (String jobScriptPathString : jobScriptPathStrings) {
			try (BufferedReader bufferedReader = new BufferedReader(
					new FileReader(jobScriptPathString))) {

				String line = bufferedReader.readLine();

				while (line != null) {
					_addJVMOptionsList(jvmOptionsList, line);

					line = bufferedReader.readLine();
				}

				if (!jvmOptionsList.isEmpty()) {
					break;
				}
			}
		}

		return jvmOptionsList;
	}

	private static List<String> _getSubjobEntries(
			Path jobDirectoryPath, String jobName)
		throws IOException {

		List<String> pathStrings = new ArrayList<>();

		Files.walkFileTree(
			jobDirectoryPath.resolve(jobName),
			new SimpleFileVisitor<Path>() {

				@Override
				public FileVisitResult visitFile(
						Path filePath, BasicFileAttributes basicFileAttributes)
					throws IOException {

					String pathString = filePath.toString();

					if (pathString.endsWith(".jar")) {
						pathStrings.add(pathString);
					}

					return FileVisitResult.CONTINUE;
				}

			});

		return pathStrings;
	}

	private static TalendArchive _parse(InputStream jobZIPInputStream)
		throws IOException {

		TalendArchive.Builder talendArchiveBuilder =
			new TalendArchive.Builder();

		File jobDirectory = _getJobDirectory(jobZIPInputStream);

		Path jobDirectoryPath = jobDirectory.toPath();

		List<String> classPathEntries = _getJobLibEntries(jobDirectoryPath);

		Properties jobProperties = _getJobProperties(jobDirectory);

		String jobName = (String)jobProperties.get("job");

		classPathEntries.addAll(_getSubjobEntries(jobDirectoryPath, jobName));

		talendArchiveBuilder.classPathEntries(classPathEntries);

		String contextName = (String)jobProperties.get("contextName");

		talendArchiveBuilder.contextName(contextName);

		Path jobJarPath = _getJobJarPath(
			jobName, jobDirectoryPath, (String)jobProperties.get("jobVersion"));

		talendArchiveBuilder.contextProperties(
			_getContextProperties(contextName, jobJarPath.toString()));

		talendArchiveBuilder.jobDirectory(jobDirectory.getAbsolutePath());

		Path jobJarParentDirectoryPath = jobJarPath.getParent();

		talendArchiveBuilder.jobJarParentDirectory(
			jobJarParentDirectoryPath.toString());

		talendArchiveBuilder.jobJarPath(jobJarPath.toString());
		talendArchiveBuilder.jobMainClassFQN(
			_getJobMainClassFQN(jobName, jobJarPath.toString()));

		talendArchiveBuilder.jvmOptionsList(
			_getJVMOptionsList(jobDirectory, jobName));

		return talendArchiveBuilder.build();
	}

}