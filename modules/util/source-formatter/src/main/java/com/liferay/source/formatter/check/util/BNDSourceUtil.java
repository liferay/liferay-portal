/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.check.util;

import aQute.bnd.osgi.Constants;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.source.formatter.util.FileUtil;

import java.io.File;
import java.io.IOException;

import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Hugo Huijser
 */
public class BNDSourceUtil {

	public static Map<String, String> getBundleSymbolicNamesMap(
		String rootDirName) {

		Map<String, String> bundleSymbolicNamesMap = new HashMap<>();

		if (Validator.isNull(rootDirName)) {
			return bundleSymbolicNamesMap;
		}

		try {
			File modulesDir = new File(rootDirName + "/modules");

			final List<File> files = new ArrayList<>();

			Files.walkFileTree(
				modulesDir.toPath(),
				new SimpleFileVisitor<Path>() {

					@Override
					public FileVisitResult preVisitDirectory(
						Path dirPath, BasicFileAttributes basicFileAttributes) {

						for (PathMatcher pathMatcher : _PATH_MATCHERS) {
							if (pathMatcher.matches(dirPath)) {
								return FileVisitResult.SKIP_SUBTREE;
							}
						}

						return FileVisitResult.CONTINUE;
					}

					@Override
					public FileVisitResult visitFile(
						Path filePath,
						BasicFileAttributes basicFileAttributes) {

						if (_PATH_MATCHER.matches(filePath)) {
							files.add(filePath.toFile());
						}

						return FileVisitResult.CONTINUE;
					}

				});

			for (File file : files) {
				String content = FileUtil.read(file);

				String bundleSymbolicName = getDefinitionValue(
					content, "Bundle-SymbolicName");

				if ((bundleSymbolicName != null) &&
					bundleSymbolicName.startsWith("com.liferay")) {

					bundleSymbolicNamesMap.put(
						bundleSymbolicName,
						SourceUtil.getAbsolutePath(file.getParentFile()));
				}
			}
		}
		catch (IOException ioException) {
			if (_log.isDebugEnabled()) {
				_log.debug(ioException);
			}
		}

		return bundleSymbolicNamesMap;
	}

	public static String getDefinition(String content, String key) {
		Pattern pattern = Pattern.compile(
			"^" + key + ":[\\s\\S]*?([^\\\\]\n|\\Z)", Pattern.MULTILINE);

		Matcher matcher = pattern.matcher(content);

		if (!matcher.find()) {
			return null;
		}

		return StringUtil.trim(matcher.group());
	}

	public static Map<String, String> getDefinitionKeysMap() {
		return _populateDefinitionKeysMap(
			ArrayUtil.append(
				ArrayUtil.toStringArray(Constants.BUNDLE_SPECIFIC_HEADERS),
				ArrayUtil.toStringArray(Constants.headers),
				ArrayUtil.toStringArray(Constants.options),
				new String[] {"-bundleannotations"}));
	}

	public static String getDefinitionValue(String content, String key) {
		Pattern pattern = Pattern.compile(
			"^" + key + ": (.*)(\n|\\Z)", Pattern.MULTILINE);

		Matcher matcher = pattern.matcher(content);

		if (matcher.find()) {
			return matcher.group(1);
		}

		return null;
	}

	public static List<String> getDefinitionValues(String content, String key) {
		List<String> definitionValues = new ArrayList<>();

		if (!content.contains(key + ":")) {
			return definitionValues;
		}

		String definitionValue = getDefinitionValue(content, key);

		if (definitionValue != null) {
			definitionValues.add(definitionValue);

			return definitionValues;
		}

		int x = content.indexOf(key + ":\\\n");

		if (x == -1) {
			return definitionValues;
		}

		int lineNumber = SourceUtil.getLineNumber(content, x);

		for (int i = lineNumber + 1;; i++) {
			String line = StringUtil.trim(SourceUtil.getLine(content, i));

			if (line.endsWith(",\\")) {
				definitionValues.add(
					StringUtil.replaceLast(line, ",\\", StringPool.BLANK));
			}
			else if (!line.endsWith(StringPool.BACK_SLASH)) {
				definitionValues.add(line);

				return definitionValues;
			}
		}
	}

	public static Map<String, Map<String, String>>
		getFileSpecificDefinitionKeysMap() {

		return HashMapBuilder.<String, Map<String, String>>put(
			"app.bnd", _populateDefinitionKeysMap(_APP_BND_DEFINITION_KEYS)
		).put(
			"bnd.bnd", _populateDefinitionKeysMap(_BND_BND_DEFINITION_KEYS)
		).put(
			"common.bnd",
			_populateDefinitionKeysMap(_COMMON_BND_DEFINITION_KEYS)
		).put(
			"subsystem.bnd",
			_populateDefinitionKeysMap(_SUBSYSTEM_BND_DEFINITION_KEYS)
		).put(
			"suite.bnd", _populateDefinitionKeysMap(_SUITE_BND_DEFINITION_KEYS)
		).build();
	}

	public static String getModuleName(String absolutePath) {
		int x = absolutePath.lastIndexOf(StringPool.SLASH);

		int y = absolutePath.lastIndexOf(StringPool.SLASH, x - 1);

		return absolutePath.substring(y + 1, x);
	}

	public static String updateInstruction(
		String content, String header, String value) {

		String instruction = header + StringPool.COLON;

		if (Validator.isNotNull(value)) {
			instruction = instruction + StringPool.SPACE + value;
		}

		if (!content.contains(header)) {
			return content + StringPool.NEW_LINE + instruction;
		}

		String[] lines = StringUtil.splitLines(content);

		for (String line : lines) {
			if (line.contains(header)) {
				content = StringUtil.replaceFirst(content, line, instruction);
			}
		}

		return content;
	}

	private static Map<String, String> _populateDefinitionKeysMap(
		String[] keys) {

		Map<String, String> definitionKeysMap = new HashMap<>();

		for (String key : keys) {
			definitionKeysMap.put(StringUtil.toLowerCase(key), key);
		}

		return definitionKeysMap;
	}

	private static final String[] _APP_BND_DEFINITION_KEYS = {
		"Liferay-Releng-App-Description", "Liferay-Releng-App-Title",
		"Liferay-Releng-Bundle", "Liferay-Releng-Category",
		"Liferay-Releng-Demo-Url", "Liferay-Releng-Deprecated",
		"Liferay-Releng-Fix-Delivery-Method", "Liferay-Releng-Labs",
		"Liferay-Releng-Marketplace", "Liferay-Releng-Portal-Required",
		"Liferay-Releng-Public", "Liferay-Releng-Restart-Required",
		"Liferay-Releng-Suite", "Liferay-Releng-Support-Url",
		"Liferay-Releng-Supported"
	};

	private static final String[] _BND_BND_DEFINITION_KEYS = {
		"-jsp", "-liferay-aggregate-resource-bundles", "-metatype",
		"-metatype-inherit", "-sass", "Bundle-ActivationPolicy",
		"Can-Redefine-Classes", "Can-Retransform-Classes", "CPE-Name",
		"Eclipse-PlatformFilter", "Implementation-Version", "JPM-Command",
		"Liferay-Client-Extension-Batch", "Liferay-Configuration-Path",
		"Liferay-Enterprise-App", "Liferay-Icons-Pack-Name",
		"Liferay-Icons-Path", "Liferay-JS-Config",
		"Liferay-JS-Resources-Top-Head-Authenticated",
		"Liferay-JS-Resources-Top-Head", "Liferay-JS-Submodules-Bridge",
		"Liferay-JS-Submodules-Export", "Liferay-Modules-Compat-Adapters",
		"Liferay-Releng-App-Description",
		"Liferay-Releng-Module-Group-Description",
		"Liferay-Releng-Module-Group-Title", "Liferay-Require-SchemaVersion",
		"Liferay-RTL-Support-Required", "Liferay-Service",
		"Liferay-Site-Initializer-Description",
		"Liferay-Site-Initializer-Feature-Flag",
		"Liferay-Site-Initializer-Name", "Liferay-Theme-Contributor-Type",
		"Liferay-Theme-Contributor-Weight", "Liferay-Versions", "Main-Class",
		"Premain-Class", "Web-ContextPath", "Web-ServiceTracking"
	};

	private static final String[] _COMMON_BND_DEFINITION_KEYS = {
		"Git-Descriptor", "Git-SHA", "Javac-Compiler", "Javac-Debug",
		"Javac-Deprecation", "Javac-Encoding", "Liferay-Portal-Build-Date",
		"Liferay-Portal-Build-Number", "Liferay-Portal-Build-Time",
		"Liferay-Portal-Code-Name", "Liferay-Portal-Parent-Build-Number",
		"Liferay-Portal-Release-Info", "Liferay-Portal-Server-Info",
		"Liferay-Portal-Version", "Liferay-Portal-Version-Display-Name"
	};

	private static final FileSystem _FILE_SYSTEM = FileSystems.getDefault();

	private static final PathMatcher _PATH_MATCHER =
		_FILE_SYSTEM.getPathMatcher("glob:**/bnd.bnd");

	private static final PathMatcher[] _PATH_MATCHERS = {
		_FILE_SYSTEM.getPathMatcher("glob:**/.git/**"),
		_FILE_SYSTEM.getPathMatcher("glob:**/.gradle/**"),
		_FILE_SYSTEM.getPathMatcher("glob:**/.idea/**"),
		_FILE_SYSTEM.getPathMatcher("glob:**/.m2/**"),
		_FILE_SYSTEM.getPathMatcher("glob:**/.settings/**"),
		_FILE_SYSTEM.getPathMatcher("glob:**/bin/**"),
		_FILE_SYSTEM.getPathMatcher("glob:**/build/**"),
		_FILE_SYSTEM.getPathMatcher("glob:**/classes/**"),
		_FILE_SYSTEM.getPathMatcher("glob:**/node_modules/**"),
		_FILE_SYSTEM.getPathMatcher("glob:**/node_modules_cache/**"),
		_FILE_SYSTEM.getPathMatcher("glob:**/sql/**"),
		_FILE_SYSTEM.getPathMatcher("glob:**/src/**"),
		_FILE_SYSTEM.getPathMatcher("glob:**/test-classes/**"),
		_FILE_SYSTEM.getPathMatcher("glob:**/test-coverage/**"),
		_FILE_SYSTEM.getPathMatcher("glob:**/test-results/**"),
		_FILE_SYSTEM.getPathMatcher("glob:**/tmp/**")
	};

	private static final String[] _SUBSYSTEM_BND_DEFINITION_KEYS = {
		"Liferay-Releng-Marketplace", "Liferay-Releng-Subsystem-Title"
	};

	private static final String[] _SUITE_BND_DEFINITION_KEYS = {
		"Liferay-Releng-Suite-Description", "Liferay-Releng-Suite-Title"
	};

	private static final Log _log = LogFactoryUtil.getLog(BNDSourceUtil.class);

}