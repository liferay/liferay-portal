/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.check;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.json.JSONObjectImpl;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.source.formatter.check.comparator.PropertyValueComparator;
import com.liferay.source.formatter.check.util.JSONSourceUtil;
import com.liferay.source.formatter.util.FileUtil;

import java.util.Comparator;
import java.util.Objects;

/**
 * @author Alan Huang
 * @author Hugo Huijser
 */
public class JSONPackageJSONCheck extends BaseFileCheck {

	@Override
	public boolean isLiferaySourceCheck() {
		return true;
	}

	@Override
	protected String doProcess(
			String fileName, String absolutePath, String content)
		throws JSONException {

		if (!absolutePath.endsWith("/package.json") ||
			(!absolutePath.contains("/modules/apps/") &&
			 !absolutePath.contains("/modules/dxp/apps/") &&
			 !absolutePath.contains("/modules/private/apps/"))) {

			return content;
		}

		String dirName = absolutePath.substring(0, absolutePath.length() - 12);

		if (!FileUtil.exists(dirName + "build.gradle") &&
			!FileUtil.exists(dirName + "bnd.bnd")) {

			return content;
		}

		JSONObject jsonObject = new JSONObjectImpl(content);

		if (jsonObject.isNull("scripts")) {
			return content;
		}

		JSONObject scriptsJSONObject = jsonObject.getJSONObject("scripts");

		if (!scriptsJSONObject.isNull("build") &&
			Objects.equals(
				scriptsJSONObject.get("build"), "liferay-npm-bundler")) {

			return content;
		}

		_checkIncorrectEntry(fileName, jsonObject, "devDependencies");

		if (absolutePath.endsWith("commerce-theme-minium/package.json") ||
			absolutePath.endsWith("commerce-theme-speedwell/package.json") ||
			absolutePath.endsWith("frontend-theme-admin/package.json") ||
			absolutePath.endsWith("frontend-theme-ai-hub/package.json") ||
			absolutePath.endsWith("frontend-theme-classic/package.json") ||
			absolutePath.endsWith("frontend-theme-cms/package.json") ||
			absolutePath.endsWith("frontend-theme-dialect/package.json") ||
			absolutePath.endsWith("frontend-theme-styled/package.json") ||
			absolutePath.endsWith("frontend-theme-unstyled/package.json")) {

			_checkScript(
				fileName, scriptsJSONObject, "build", false, "theme build",
				"build:theme");
		}
		else {
			_checkScript(
				fileName, scriptsJSONObject, "build", false, "build",
				"build:custom", "webpack");
		}

		_checkScript(
			fileName, scriptsJSONObject, "checkFormat", true, "--check",
			"check", "check:ci");
		_checkScript(
			fileName, scriptsJSONObject, "format", true, "fix", "format");

		return _checkJest(content);
	}

	private void _checkIncorrectEntry(
		String fileName, JSONObject jsonObject, String entryName) {

		if (!jsonObject.isNull(entryName)) {
			addMessage(fileName, "Entry \"" + entryName + "\" is not allowed");
		}
	}

	private String _checkJest(String content) throws JSONException {
		JSONObject jsonObject = new JSONObjectImpl(content);

		JSONObject jestJSONObject = jsonObject.getJSONObject("jest");

		if (jestJSONObject == null) {
			return content;
		}

		JSONArray testMatchJSONArray = jestJSONObject.getJSONArray("testMatch");

		if (testMatchJSONArray != null) {
			jestJSONObject.put(
				"testMatch",
				JSONSourceUtil.sortJSONArray(
					testMatchJSONArray, new TestMatchComparator()));
		}

		jsonObject.put("jest", jestJSONObject);

		return JSONUtil.toString(jsonObject) + "\n";
	}

	private void _checkScript(
		String fileName, JSONObject scriptsJSONObject, String key,
		boolean requiredScript, String... allowedValues) {

		if (scriptsJSONObject.isNull(key)) {
			if (requiredScript) {
				addMessage(
					fileName, "Missing entry \"" + key + "\" in \"scripts\"");
			}

			return;
		}

		String value = scriptsJSONObject.getString(key);

		for (String allowedValue : allowedValues) {
			if (value.equals(allowedValue) ||
				value.endsWith(StringPool.SPACE + allowedValue)) {

				return;
			}
		}

		if (allowedValues.length == 1) {
			addMessage(
				fileName,
				StringBundler.concat(
					"Value \"", value, "\" for entry \"", key,
					"\" should end with \"", allowedValues[0], "\""));

			return;
		}

		StringBundler sb = new StringBundler((allowedValues.length * 4) + 5);

		sb.append("Value \"");
		sb.append(value);
		sb.append("\" for entry \"");
		sb.append(key);
		sb.append(
			"\" should end with (or be exactly) one of the following values: ");

		for (int i = 0; i < allowedValues.length; i++) {
			if (i > 0) {
				sb.append(", ");
			}

			sb.append(StringPool.QUOTE);
			sb.append(allowedValues[i]);
			sb.append(StringPool.QUOTE);
		}

		addMessage(fileName, sb.toString());
	}

	private class TestMatchComparator implements Comparator<Object> {

		@Override
		public int compare(Object object1, Object object2) {
			PropertyValueComparator comparator = new PropertyValueComparator();

			return comparator.compare(object1.toString(), object2.toString());
		}

	}

}