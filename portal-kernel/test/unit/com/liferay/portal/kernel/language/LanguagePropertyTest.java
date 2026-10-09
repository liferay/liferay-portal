/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.language;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.language.constants.LanguageConstants;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.StringUtil;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Drew Brokke
 */
public class LanguagePropertyTest {

	@BeforeClass
	public static void setUpClass() throws Exception {
		FileSystem fileSystem = FileSystems.getDefault();

		PathMatcher modulePathMatcher = fileSystem.getPathMatcher(
			"glob:./modules/**/src/**/Language*.properties");
		PathMatcher portalLanguageLangPathMatcher = fileSystem.getPathMatcher(
			"glob:./modules/apps/portal-language/portal-language-lang/src" +
				"/main/resources/content/Language*.properties");

		FileVisitor<Path> simpleFileVisitor = new SimpleFileVisitor<Path>() {

			@Override
			public FileVisitResult visitFile(
					Path path, BasicFileAttributes basicFileAttributes)
				throws IOException {

				Map<String, Properties> propertiesMap = null;

				if (portalLanguageLangPathMatcher.matches(path)) {
					propertiesMap = _portalLanguageLangPropertiesMap;
				}
				else if (modulePathMatcher.matches(path)) {
					propertiesMap = _modulesPropertiesMap;
				}

				if (propertiesMap != null) {
					String fileName = path.toString();

					Properties properties = new Properties();

					try (InputStream inputStream = new FileInputStream(
							fileName)) {

						properties.load(inputStream);
					}

					propertiesMap.put(fileName, properties);
				}

				return FileVisitResult.CONTINUE;
			}

			@Override
			public FileVisitResult visitFileFailed(
				Path path, IOException ioException) {

				return FileVisitResult.CONTINUE;
			}

		};

		Files.walkFileTree(Paths.get("./"), simpleFileVisitor);
	}

	@Test
	public void testSpecialKeyDir() {
		_testSpecialKey(LanguageConstants.KEY_DIR);
	}

	@Test
	public void testSpecialKeyLineBegin() {
		_testSpecialKey(LanguageConstants.KEY_LINE_BEGIN);
	}

	@Test
	public void testSpecialKeyLineEnd() {
		_testSpecialKey(LanguageConstants.KEY_LINE_END);
	}

	@Test
	public void testSpecialKeyUserDefaultPortrait() {
		_testSpecialKey(LanguageConstants.KEY_USER_DEFAULT_PORTRAIT);
	}

	@Test
	public void testSpecialKeyUserInitialsFieldNames() {
		_testSpecialKey(LanguageConstants.KEY_USER_INITIALS_FIELD_NAMES);
	}

	@Test
	public void testSpecialKeyUserNameFieldNames() {
		_testSpecialKey(LanguageConstants.KEY_USER_NAME_FIELD_NAMES);
	}

	@Test
	public void testSpecialKeyUserNamePrefixValues() {
		_testSpecialKey(LanguageConstants.KEY_USER_NAME_PREFIX_VALUES);
	}

	@Test
	public void testSpecialKeyUserNameRequiredFieldNames() {
		_testSpecialKey(LanguageConstants.KEY_USER_NAME_REQUIRED_FIELD_NAMES);
	}

	@Test
	public void testSpecialKeyUserNameSuffixValues() {
		_testSpecialKey(LanguageConstants.KEY_USER_NAME_SUFFIX_VALUES);
	}

	@Test
	public void testUserNameRequiredFieldNamesSubsetOfUserNameFieldNames() {
		Set<String> paths = _portalLanguageLangPropertiesMap.keySet();

		List<String> failureMessages = new ArrayList<>();

		for (String path : paths) {
			Properties properties = _portalLanguageLangPropertiesMap.get(path);

			String userNameFieldNamesString = properties.getProperty(
				LanguageConstants.KEY_USER_NAME_FIELD_NAMES);
			String userNameRequiredFieldNamesString = properties.getProperty(
				LanguageConstants.KEY_USER_NAME_REQUIRED_FIELD_NAMES);

			if ((userNameFieldNamesString == null) ||
				(userNameRequiredFieldNamesString == null)) {

				continue;
			}

			String[] userNameFieldNames = StringUtil.split(
				userNameFieldNamesString);
			String[] userNameRequiredFieldNames = StringUtil.split(
				userNameRequiredFieldNamesString);

			for (String userNameRequiredFieldName :
					userNameRequiredFieldNames) {

				if (ArrayUtil.contains(
						userNameFieldNames, userNameRequiredFieldName)) {

					continue;
				}

				failureMessages.add(path);
			}
		}

		Assert.assertTrue(
			"Required field names are not a subset of user name field names " +
				"in " + failureMessages,
			failureMessages.isEmpty());
	}

	@Test
	public void testValidKeyDir() {
		_testValidKey(LanguageConstants.KEY_DIR);
	}

	@Test
	public void testValidKeyLineBegin() {
		_testValidKey(LanguageConstants.KEY_LINE_BEGIN);
	}

	@Test
	public void testValidKeyLineEnd() {
		_testValidKey(LanguageConstants.KEY_LINE_END);
	}

	@Test
	public void testValidKeyUserDefaultPortrait() {
		_testValidKey(LanguageConstants.KEY_USER_DEFAULT_PORTRAIT);
	}

	@Test
	public void testValidKeyUserInitialsFieldNames() {
		_testValidKey(LanguageConstants.KEY_USER_INITIALS_FIELD_NAMES);
	}

	@Test
	public void testValidKeyUserNameFieldNames() {
		_testValidKey(LanguageConstants.KEY_USER_NAME_FIELD_NAMES);
	}

	@Test
	public void testValidKeyUserNamePrefixValues() {
		_testValidKey(LanguageConstants.KEY_USER_NAME_PREFIX_VALUES);
	}

	@Test
	public void testValidKeyUserNameRequiredFieldNames() {
		_testValidKey(LanguageConstants.KEY_USER_NAME_REQUIRED_FIELD_NAMES);
	}

	@Test
	public void testValidKeyUserNameSuffixValues() {
		_testValidKey(LanguageConstants.KEY_USER_NAME_SUFFIX_VALUES);
	}

	private void _testSpecialKey(String key) {
		List<String> invalidFileNames = new ArrayList<>();

		Set<String> fileNames = _modulesPropertiesMap.keySet();

		for (String fileName : fileNames) {
			Properties properties = _modulesPropertiesMap.get(fileName);

			Set<String> propertyNames = properties.stringPropertyNames();

			if (!propertyNames.contains(key)) {
				continue;
			}

			invalidFileNames.add(fileName);
		}

		Assert.assertTrue(
			StringBundler.concat(
				"Special key \"", key, "\" is found in: ", invalidFileNames),
			invalidFileNames.isEmpty());
	}

	private void _testValidKey(String key) {
		List<String> invalidFileNames = new ArrayList<>();

		Set<String> fileNames = _portalLanguageLangPropertiesMap.keySet();

		for (String path : fileNames) {
			Properties properties = _portalLanguageLangPropertiesMap.get(path);

			String value = properties.getProperty(key);

			if ((value == null) || LanguageValidator.isValid(key, value)) {
				continue;
			}

			invalidFileNames.add(path);
		}

		Assert.assertTrue(
			StringBundler.concat(
				"Invalid values for key \"", key, "\" are found in: ",
				invalidFileNames),
			invalidFileNames.isEmpty());
	}

	private static final Map<String, Properties> _modulesPropertiesMap =
		new HashMap<>();
	private static final Map<String, Properties>
		_portalLanguageLangPropertiesMap = new HashMap<>();

}