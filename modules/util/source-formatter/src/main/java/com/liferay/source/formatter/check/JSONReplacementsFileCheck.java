/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.source.formatter.check;

import com.liferay.portal.json.JSONArrayImpl;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.util.NaturalOrderStringComparator;
import com.liferay.source.formatter.check.util.JavaSourceUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @author Alan Huang
 */
public class JSONReplacementsFileCheck extends BaseFileCheck {

	@Override
	public boolean isLiferaySourceCheck() {
		return true;
	}

	@Override
	protected String doProcess(
			String fileName, String absolutePath, String content)
		throws JSONException {

		if (!absolutePath.endsWith(
				"modules/util/source-formatter/src/main/resources" +
					"/dependencies/replacements.json")) {

			return content;
		}

		JSONArray jsonArray = new JSONArrayImpl(content);

		List<Object> objects = JSONUtil.toObjectList(jsonArray);

		Collections.sort(objects, new ReplacementComparator());

		jsonArray = new JSONArrayImpl();

		for (Object object : objects) {
			jsonArray.put(object);
		}

		return JSONUtil.toString(jsonArray);
	}

	private class ReplacementComparator implements Comparator<Object> {

		@Override
		public int compare(Object object1, Object object2) {
			NaturalOrderStringComparator comparator =
				new NaturalOrderStringComparator();

			JSONObject jsonObject1 = (JSONObject)object1;
			JSONObject jsonObject2 = (JSONObject)object2;

			String issueKey1 = jsonObject1.getString("issueKey");
			String issueKey2 = jsonObject2.getString("issueKey");

			if (!issueKey1.equals(issueKey2)) {
				return comparator.compare(issueKey1, issueKey2);
			}

			String from1 = jsonObject1.getString("from");
			String from2 = jsonObject2.getString("from");

			String methodName1 = _getMethodName(from1);
			String methodName2 = _getMethodName(from2);

			if (!methodName1.equals(methodName2)) {
				return comparator.compare(methodName1, methodName2);
			}

			List<String> parameterTypes1 = new ArrayList<>();
			List<String> parameterTypes2 = new ArrayList<>();

			if (!from1.startsWith("regex:") && !from2.startsWith("regex:")) {
				if (from1.contains("(")) {
					parameterTypes1 = JavaSourceUtil.getParameterTypes(from1);
				}

				if (from2.contains("(")) {
					parameterTypes2 = JavaSourceUtil.getParameterTypes(from2);
				}
			}

			if (parameterTypes1.isEmpty() && !parameterTypes2.isEmpty()) {
				return -1;
			}
			else if (!parameterTypes1.isEmpty() && parameterTypes2.isEmpty()) {
				return 1;
			}

			int min = Math.min(parameterTypes1.size(), parameterTypes2.size());

			for (int i = 0; i < min; i++) {
				String parameterType1 = parameterTypes1.get(i);
				String parameterType2 = parameterTypes2.get(i);

				if (parameterType1.compareToIgnoreCase(parameterType2) != 0) {
					return parameterType1.compareToIgnoreCase(parameterType2);
				}

				if (parameterType1.compareTo(parameterType2) != 0) {
					return -parameterType1.compareTo(parameterType2);
				}
			}

			if (parameterTypes1.size() != parameterTypes2.size()) {
				return parameterTypes1.size() - parameterTypes2.size();
			}

			String to1 = jsonObject1.getString("to");
			String to2 = jsonObject2.getString("to");

			methodName1 = _getMethodName(to1);
			methodName2 = _getMethodName(to2);

			if (!methodName1.equals(methodName2)) {
				return comparator.compare(methodName1, methodName2);
			}

			return to1.compareTo(to2);
		}

		private String _getMethodName(String s) {
			int x = s.indexOf("(");

			if (x != -1) {
				return s.substring(0, x);
			}

			return s;
		}

	}

}