/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.internal.util;

import com.liferay.dynamic.data.mapping.expression.CreateExpressionRequest;
import com.liferay.dynamic.data.mapping.expression.DDMExpression;
import com.liferay.dynamic.data.mapping.expression.DDMExpressionFactory;
import com.liferay.dynamic.data.mapping.expression.DDMExpressionFieldAccessor;
import com.liferay.dynamic.data.mapping.expression.DDMExpressionParameterAccessor;
import com.liferay.dynamic.data.mapping.expression.GetFieldPropertyRequest;
import com.liferay.dynamic.data.mapping.expression.GetFieldPropertyResponse;
import com.liferay.layout.util.structure.LayoutStructureRule;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.StringUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Víctor Galán
 */
public class AdvancedLayoutStructureRuleUtil {

	public static boolean evaluateScript(
		DDMExpressionFactory ddmExpressionFactory, long groupId, long[] roleIds,
		String script, Map<String, Object> scriptFieldValues,
		long[] segmentsEntryIds, User user) {

		try {
			DDMExpression<Boolean> ddmExpression =
				ddmExpressionFactory.createExpression(
					CreateExpressionRequest.Builder.newBuilder(
						script
					).withDDMExpressionFieldAccessor(
						new LayoutStructureRuleDDMExpressionFieldAccessor(
							roleIds, segmentsEntryIds, user, scriptFieldValues)
					).withDDMExpressionParameterAccessor(
						new LayoutStructureRuleDDMExpressionParameterAccessor(
							groupId, user)
					).build());

			return ddmExpression.evaluate();
		}
		catch (Exception exception) {
			_log.error(exception);
		}

		return false;
	}

	public static List<String> getItemIds(
		LayoutStructureRule layoutStructureRule) {

		List<String> itemIds = new ArrayList<>();

		Matcher matcher = _pattern.matcher(layoutStructureRule.getScript());

		while (matcher.find()) {
			itemIds.add(
				StringUtil.replace(
					StringUtil.removeSubstring(matcher.group(), _INPUT_PREFIX),
					CharPool.UNDERLINE, CharPool.DASH));
		}

		return itemIds;
	}

	private static final String _INPUT_PREFIX = "input__";

	private static final Log _log = LogFactoryUtil.getLog(
		AdvancedLayoutStructureRuleUtil.class);

	private static final Pattern _pattern = Pattern.compile(
		_INPUT_PREFIX + "[A-Za-z0-9_]*");

	private static class LayoutStructureRuleDDMExpressionFieldAccessor
		implements DDMExpressionFieldAccessor {

		public LayoutStructureRuleDDMExpressionFieldAccessor(
			long[] roleIds, long[] segmentsEntryIds, User user,
			Map<String, Object> fieldValues) {

			_values = HashMapBuilder.<String, Object>put(
				"createDate", user.getCreateDate()
			).put(
				"emailAddresses", user.getEmailAddresses()
			).put(
				"roleIds",
				JSONFactoryUtil.createJSONArray(ArrayUtil.toLongArray(roleIds))
			).put(
				"screenName", user.getScreenName()
			).put(
				"segmentsEntryIds",
				JSONFactoryUtil.createJSONArray(
					ArrayUtil.toLongArray(segmentsEntryIds))
			).put(
				"userId", user.getUserId()
			).putAll(
				fieldValues
			).build();
		}

		@Override
		public GetFieldPropertyResponse getFieldProperty(
			GetFieldPropertyRequest getFieldPropertyRequest) {

			Object value = _values.get(getFieldPropertyRequest.getField());

			if ((value == null) &&
				isField(getFieldPropertyRequest.getField())) {

				value = StringPool.BLANK;
			}

			GetFieldPropertyResponse.Builder builder =
				GetFieldPropertyResponse.Builder.newBuilder(value);

			return builder.build();
		}

		@Override
		public boolean isField(String parameter) {
			return _values.containsKey(parameter);
		}

		private final Map<String, Object> _values;

	}

	private static class LayoutStructureRuleDDMExpressionParameterAccessor
		implements DDMExpressionParameterAccessor {

		public LayoutStructureRuleDDMExpressionParameterAccessor(
			long groupId, User user) {

			_groupId = groupId;

			_companyId = user.getCompanyId();

			_locale = user.getLocale();
			_timeZoneId = user.getTimeZoneId();
			_userId = user.getUserId();
		}

		@Override
		public long getCompanyId() {
			return _companyId;
		}

		@Override
		public String getGooglePlacesAPIKey() {
			return StringPool.BLANK;
		}

		@Override
		public long getGroupId() {
			return _groupId;
		}

		@Override
		public Locale getLocale() {
			return _locale;
		}

		@Override
		public JSONArray getObjectFieldsJSONArray() {
			return JSONFactoryUtil.createJSONArray();
		}

		@Override
		public String getTimeZoneId() {
			return _timeZoneId;
		}

		@Override
		public long getUserId() {
			return _userId;
		}

		private final long _companyId;
		private final long _groupId;
		private final Locale _locale;
		private final String _timeZoneId;
		private final long _userId;

	}

}