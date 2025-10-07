/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.processor;

import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.json.JSONFactoryImpl;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.source.formatter.SourceFormatterArgs;
import com.liferay.source.formatter.check.UpgradeCatchAllCheck;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

/**
 * @author Kyle Miho
 */
@RunWith(Parameterized.class)
public class UpgradeCatchAllCheckTest extends BaseSourceProcessorTestCase {

	@Parameterized.Parameters(name = "Testcase-{index}: Testing {0}.{1}")
	public static Iterable<Object[]> data() throws Exception {
		List<Object[]> objectsArray = new ArrayList<>();

		Map<String, JSONObject> issueKeyFileTypesMap = new HashMap<>();

		String[] issueKeys = StringUtil.split(
			System.getProperty("issue.key", null), StringPool.COMMA);

		JSONFactoryUtil jsonFactoryUtil = new JSONFactoryUtil();

		jsonFactoryUtil.setJSONFactory(new JSONFactoryImpl());

		ClassLoader classLoader = UpgradeCatchAllCheck.class.getClassLoader();

		JSONArray jsonArray = JSONFactoryUtil.createJSONArray(
			StringUtil.read(
				classLoader.getResourceAsStream(
					"dependencies/replacements.json")));

		for (Object object : jsonArray) {
			JSONObject jsonObject = (JSONObject)object;

			String issueKey = jsonObject.getString("issueKey");

			if (ArrayUtil.isNotEmpty(issueKeys) &&
				!ArrayUtil.contains(issueKeys, issueKey)) {

				continue;
			}

			String[] validExtensions = _getValidExtensions(jsonObject);

			for (String fileType : validExtensions) {
				issueKeyFileTypesMap.compute(
					issueKey,
					(key, value) -> {
						if (value == null) {
							value = JSONUtil.put("fileTypes", new HashSet<>());
						}

						Set<String> fileTypes = (Set<String>)value.get(
							"fileTypes");

						fileTypes.add(fileType);

						value.put("fileTypes", fileTypes);

						value.put(
							"hasMessage", jsonObject.getBoolean("hasMessage"));

						return value;
					});
			}
		}

		for (Map.Entry<String, JSONObject> entry :
				issueKeyFileTypesMap.entrySet()) {

			JSONObject jsonObject = entry.getValue();

			for (String fileType : (Set<String>)jsonObject.get("fileTypes")) {
				objectsArray.add(
					new Object[] {
						jsonObject.getBoolean("hasMessage"), entry.getKey(),
						fileType
					});
			}
		}

		return objectsArray;
	}

	public UpgradeCatchAllCheckTest(
		boolean hasMessage, String issueKey, String fileType) {

		_hasMessage = hasMessage;
		_issueKey = issueKey;
		_fileType = fileType;
	}

	@Before
	public void setUp() {
		UpgradeCatchAllCheck.setIssueKey(_issueKey);
		UpgradeCatchAllCheck.setTestMode(true);
	}

	@After
	public void tearDown() {
		UpgradeCatchAllCheck.setIssueKey(null);
		UpgradeCatchAllCheck.setTestMode(false);
	}

	@Test
	public void testUpgradeCatchAllCheck() throws Exception {
		String fileName =
			StringUtil.replace(_issueKey, CharPool.DASH, CharPool.UNDERLINE) +
				".test" + _fileType;

		Path filePath = Paths.get(
			"src/test/resources/com/liferay/source/formatter/dependencies" +
				"/upgrade/upgrade-catch-all-check/" + fileName);

		Assert.assertTrue(
			"Missing unit test in " + filePath, Files.exists(filePath));

		Path expectedFilePath = Paths.get(
			"src/test/resources/com/liferay/source/formatter/dependencies" +
				"/expected/upgrade/upgrade-catch-all-check/" + fileName);

		if (_hasMessage) {
			Assert.assertFalse(
				"Unexpected unit test in " + expectedFilePath,
				Files.exists(expectedFilePath));
		}
		else {
			Assert.assertTrue(
				"Missing unit test in " + expectedFilePath,
				Files.exists(expectedFilePath));
		}

		_testUpgradeCatchAllCheck(
			"upgrade/upgrade-catch-all-check/" + fileName);
	}

	@Override
	protected SourceFormatterArgs getSourceFormatterArgs() {
		SourceFormatterArgs sourceFormatterArgs =
			super.getSourceFormatterArgs();

		sourceFormatterArgs.setCheckCategoryNames(Arrays.asList("Upgrade"));
		sourceFormatterArgs.setJavaParserEnabled(false);
		sourceFormatterArgs.setSourceFormatterProperties(
			Arrays.asList(
				"upgrade.to.liferay.version=" + _UPGRADE_TO_LIFERAY_VERSION,
				"upgrade.to.release.version=" + _UPGRADE_TO_RELEASE_VERSION));

		return sourceFormatterArgs;
	}

	private static String[] _getValidExtensions(JSONObject jsonObject) {
		String[] validExtensions = JSONUtil.toStringArray(
			jsonObject.getJSONArray("validExtensions"));

		if (ArrayUtil.isEmpty(validExtensions)) {
			return new String[] {"java"};
		}

		return validExtensions;
	}

	private void _testUpgradeCatchAllCheck(String fileName) throws Exception {
		test(fileName, UpgradeCatchAllCheck.getExpectedMessages(fileName));
	}

	private static final String _UPGRADE_TO_LIFERAY_VERSION = "7.4.13.u27";

	private static final String _UPGRADE_TO_RELEASE_VERSION = "2024.q1.1";

	private final String _fileType;
	private final boolean _hasMessage;
	private final String _issueKey;

}