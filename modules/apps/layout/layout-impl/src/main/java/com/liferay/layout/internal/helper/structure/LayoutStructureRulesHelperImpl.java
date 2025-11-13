/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.internal.helper.structure;

import com.liferay.dynamic.data.mapping.expression.CreateExpressionRequest;
import com.liferay.dynamic.data.mapping.expression.DDMExpression;
import com.liferay.dynamic.data.mapping.expression.DDMExpressionException;
import com.liferay.dynamic.data.mapping.expression.DDMExpressionFactory;
import com.liferay.dynamic.data.mapping.expression.DDMExpressionFieldAccessor;
import com.liferay.dynamic.data.mapping.expression.DDMExpressionParameterAccessor;
import com.liferay.dynamic.data.mapping.expression.GetFieldPropertyRequest;
import com.liferay.dynamic.data.mapping.expression.GetFieldPropertyResponse;
import com.liferay.layout.helper.structure.LayoutStructureRulesHelper;
import com.liferay.layout.util.structure.LayoutStructure;
import com.liferay.layout.util.structure.LayoutStructureItem;
import com.liferay.layout.util.structure.LayoutStructureRule;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Víctor Galán
 */
@Component(service = LayoutStructureRulesHelper.class)
public class LayoutStructureRulesHelperImpl
	implements LayoutStructureRulesHelper {

	@Override
	public LayoutStructureRulesResult processLayoutStructureRules(
		long groupId, LayoutStructure layoutStructure,
		PermissionChecker permissionChecker, long[] segmentsEntryIds) {

		Map<String, List<String>> itemIdsMap = new HashMap<>();
		JSONArray jsonArray = _jsonFactory.createJSONArray();
		Map<String, List<String>> layoutStructureRuleIdsMap = new HashMap<>();
		LayoutStructureRulesContext layoutStructureRulesContext =
			new LayoutStructureRulesContext(
				groupId, permissionChecker, segmentsEntryIds);

		for (LayoutStructureRule layoutStructureRule :
				layoutStructure.getLayoutStructureRules()) {

			List<String> itemIds = _getItemIds(
				layoutStructure, layoutStructureRule);

			if (itemIds.isEmpty()) {
				_processActions(
					layoutStructureRule.getActionsJSONArray(), jsonArray,
					!_evaluateLayoutStructureRule(
						Collections.emptyMap(), layoutStructureRule,
						layoutStructureRulesContext));

				continue;
			}

			itemIds = ListUtil.filter(
				ListUtil.unique(itemIds),
				itemId ->
					layoutStructure.getLayoutStructureItem(itemId) != null);

			if (itemIds.isEmpty()) {
				continue;
			}

			layoutStructureRuleIdsMap.put(layoutStructureRule.getId(), itemIds);

			for (String itemId : itemIds) {
				List<String> layoutStructureRuleIds =
					itemIdsMap.computeIfAbsent(
						itemId, key -> new ArrayList<>());

				layoutStructureRuleIds.add(layoutStructureRule.getId());
			}
		}

		Set<String> disabledItemIds = new HashSet<>();
		Set<String> displayedItemIds = new HashSet<>();
		Set<String> enabledItemIds = new HashSet<>();
		Set<String> hiddenItemIds = new HashSet<>();

		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject jsonObject = jsonArray.getJSONObject(i);

			String action = jsonObject.getString("action");
			String itemId = jsonObject.getString("itemId");

			if (Objects.equals(action, Action.DISABLE.getValue())) {
				if (enabledItemIds.contains(itemId)) {
					enabledItemIds.remove(itemId);
				}

				disabledItemIds.add(itemId);
			}
			else if (Objects.equals(
						jsonObject.getString("action"),
						Action.SHOW.getValue())) {

				if (hiddenItemIds.contains(itemId)) {
					hiddenItemIds.remove(itemId);
				}

				displayedItemIds.add(itemId);
			}
			else if (Objects.equals(
						jsonObject.getString("action"),
						Action.ENABLE.getValue())) {

				if (disabledItemIds.contains(itemId)) {
					disabledItemIds.remove(itemId);
				}

				enabledItemIds.add(itemId);
			}
			else {
				if (displayedItemIds.contains(itemId)) {
					displayedItemIds.remove(itemId);
				}

				hiddenItemIds.add(itemId);
			}
		}

		return new LayoutStructureRulesResult(
			disabledItemIds, displayedItemIds, enabledItemIds, hiddenItemIds,
			itemIdsMap, layoutStructureRuleIdsMap);
	}

	@Override
	public JSONArray processLayoutStructureRules(
		long groupId, Map<String, Object> fieldValuesMap,
		List<LayoutStructureRule> layoutStructureRules,
		PermissionChecker permissionChecker, long[] segmentsEntryIds) {

		JSONArray jsonArray = _jsonFactory.createJSONArray();

		LayoutStructureRulesContext layoutStructureRulesContext =
			new LayoutStructureRulesContext(
				groupId, permissionChecker, segmentsEntryIds);

		for (LayoutStructureRule layoutStructureRule : layoutStructureRules) {
			_processActions(
				layoutStructureRule.getActionsJSONArray(), jsonArray,
				!_evaluateLayoutStructureRule(
					fieldValuesMap, layoutStructureRule,
					layoutStructureRulesContext));
		}

		return jsonArray;
	}

	public class LayoutStructureRuleDDMExpressionFieldAccessor
		implements DDMExpressionFieldAccessor {

		public LayoutStructureRuleDDMExpressionFieldAccessor(
			long[] roleIds, long[] segmentsEntryIds, User user,
			Map<String, Object> fieldValues) {

			_values = HashMapBuilder.<String, Object>put(
				"createDate", user.getCreateDate()
			).put(
				"emailAddresses", user.getEmailAddresses()
			).put(
				"lastLoginDate", user.getLastLoginDate()
			).put(
				"modifiedDate", user.getModifiedDate()
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

	public class LayoutStructureRuleDDMExpressionParameterAccessor
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

	private boolean _evaluateDDMExpression(
		String script, LayoutStructureRulesContext layoutStructureRulesContext,
		Map<String, Object> scriptFieldValues) {

		try {
			DDMExpression<Boolean> ddmExpression =
				_ddmExpressionFactory.createExpression(
					CreateExpressionRequest.Builder.newBuilder(
						script
					).withDDMExpressionFieldAccessor(
						layoutStructureRulesContext.
							getDDMExpressionFieldAccessor(scriptFieldValues)
					).withDDMExpressionParameterAccessor(
						layoutStructureRulesContext.
							getDDMExpressionParameterAccessor()
					).build());

			return ddmExpression.evaluate();
		}
		catch (DDMExpressionException ddmExpressionException) {
			_log.error(ddmExpressionException);
		}
		catch (Exception exception) {
			_log.error(exception);
		}

		return false;
	}

	private boolean _evaluateLayoutStructureRule(
		Map<String, Object> fieldValuesMap,
		LayoutStructureRule layoutStructureRule,
		LayoutStructureRulesContext layoutStructureRulesContext) {

		if (layoutStructureRule.isAdvancedRule()) {
			return _evaluateDDMExpression(
				layoutStructureRule.getScript(), layoutStructureRulesContext,
				fieldValuesMap);
		}

		JSONArray conditionsJSONArray =
			layoutStructureRule.getConditionsJSONArray();

		for (int i = 0; i < conditionsJSONArray.length(); i++) {
			JSONObject conditionJSONObject = conditionsJSONArray.getJSONObject(
				i);

			if (_isConditionActive(
					conditionJSONObject, fieldValuesMap,
					layoutStructureRulesContext)) {

				if (Objects.equals(
						layoutStructureRule.getConditionType(), "any")) {

					return true;
				}
			}
			else if (Objects.equals(
						layoutStructureRule.getConditionType(), "all")) {

				return false;
			}
		}

		return !Objects.equals(layoutStructureRule.getConditionType(), "any");
	}

	private boolean _evaluateUserTypeCondition(
		String field, LayoutStructureRulesContext layoutStructureRulesContext,
		boolean negated, long value) {

		if (Objects.equals(field, "role")) {
			if (negated) {
				return !ArrayUtil.contains(
					layoutStructureRulesContext.getRoleIds(), value);
			}

			return ArrayUtil.contains(
				layoutStructureRulesContext.getRoleIds(), value);
		}

		if (Objects.equals(field, "segment")) {
			if (negated) {
				return !ArrayUtil.contains(
					layoutStructureRulesContext.getSegmentsEntryIds(), value);
			}

			return ArrayUtil.contains(
				layoutStructureRulesContext.getSegmentsEntryIds(), value);
		}

		if (Objects.equals(field, "user")) {
			if (negated) {
				return !Objects.equals(
					layoutStructureRulesContext.getUserId(), value);
			}

			return Objects.equals(
				layoutStructureRulesContext.getUserId(), value);
		}

		return false;
	}

	private Action _getAction(boolean negated, String type) {
		if (Objects.equals(type, "disable")) {
			if (negated) {
				return Action.ENABLE;
			}

			return Action.DISABLE;
		}
		else if (Objects.equals(type, "enable")) {
			if (negated) {
				return Action.DISABLE;
			}

			return Action.ENABLE;
		}
		else if (Objects.equals(type, "show")) {
			if (negated) {
				return Action.HIDE;
			}

			return Action.SHOW;
		}
		else if (Objects.equals(type, "hide")) {
			if (negated) {
				return Action.SHOW;
			}

			return Action.HIDE;
		}

		throw new IllegalArgumentException("Unknown action type: " + type);
	}

	private List<String> _getItemIds(
		LayoutStructure layoutStructure,
		LayoutStructureRule layoutStructureRule) {

		List<String> itemIds = new ArrayList<>();

		JSONArray conditionsJSONArray =
			layoutStructureRule.getConditionsJSONArray();

		for (int i = 0; i < conditionsJSONArray.length(); i++) {
			JSONObject conditionJSONObject = conditionsJSONArray.getJSONObject(
				i);

			if (Objects.equals(conditionJSONObject.getString("type"), "user") ||
				layoutStructureRule.isAdvancedRule()) {

				continue;
			}

			itemIds.add(conditionJSONObject.getString("field"));
		}

		if (layoutStructureRule.isAdvancedRule()) {
			Pattern pattern = Pattern.compile("input__[A-Za-z0-9_]*");

			Matcher matcher = pattern.matcher(layoutStructureRule.getScript());

			while (matcher.find()) {
				String fullMatch = matcher.group();

				String itemId =
					fullMatch.substring(7).replaceAll("_", "-");

				itemIds.add(itemId);
			}
		}

		return itemIds;
	}

	private boolean _isConditionActive(
		JSONObject conditionJSONObject, Map<String, Object> fieldValuesMap,
		LayoutStructureRulesContext layoutStructureRulesContext) {

		boolean negated = false;
		Object value = 0L;

		JSONObject optionsJSONObject = conditionJSONObject.getJSONObject(
			"options");

		if (optionsJSONObject != null) {
			if (Objects.equals(
					optionsJSONObject.getString("type"), "not-equal")) {

				negated = true;
			}

			value = optionsJSONObject.get("value");
		}

		if (Objects.equals(conditionJSONObject.getString("type"), "form")) {
			if (negated) {
				return !Objects.equals(
					fieldValuesMap.get(conditionJSONObject.getString("field")),
					value);
			}

			return Objects.equals(
				fieldValuesMap.get(conditionJSONObject.getString("field")),
				value);
		}

		if (Objects.equals(conditionJSONObject.getString("type"), "user")) {
			return _evaluateUserTypeCondition(
				conditionJSONObject.getString("field"),
				layoutStructureRulesContext, negated,
				GetterUtil.getLong(value));
		}

		return false;
	}

	private void _processActions(
		JSONArray actionsJSONArray, JSONArray jsonArray, boolean negated) {

		for (int i = 0; i < actionsJSONArray.length(); i++) {
			JSONObject actionsJSONObject = actionsJSONArray.getJSONObject(i);

			jsonArray.put(
				JSONUtil.put(
					"action",
					() -> {
						Action action = _getAction(
							negated, actionsJSONObject.getString("type"));

						return action.getValue();
					}
				).put(
					"itemId", actionsJSONObject.getString("itemId")
				));
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		LayoutStructureRulesHelperImpl.class);

	@Reference
	private DDMExpressionFactory _ddmExpressionFactory;

	@Reference
	private JSONFactory _jsonFactory;

	private enum Action {

		DISABLE("disable"), ENABLE("enable"), HIDE("hide"), SHOW("show");

		public String getValue() {
			return _value;
		}

		private Action(String value) {
			_value = value;
		}

		private final String _value;

	}

	private class LayoutStructureRulesContext {

		public DDMExpressionFieldAccessor getDDMExpressionFieldAccessor(
			Map<String, Object> fieldValuesMap) {

			if (_ddmExpressionFieldAccessor != null) {
				return _ddmExpressionFieldAccessor;
			}

			_ddmExpressionFieldAccessor =
				new LayoutStructureRuleDDMExpressionFieldAccessor(
					getRoleIds(), getSegmentsEntryIds(),
					_permissionChecker.getUser(), fieldValuesMap);

			return _ddmExpressionFieldAccessor;
		}

		public DDMExpressionParameterAccessor
			getDDMExpressionParameterAccessor() {

			if (_ddmExpressionParameterAccessor != null) {
				return _ddmExpressionParameterAccessor;
			}

			_ddmExpressionParameterAccessor =
				new LayoutStructureRuleDDMExpressionParameterAccessor(
					_groupId, _permissionChecker.getUser());

			return _ddmExpressionParameterAccessor;
		}

		public long getGroupId() {
			return _groupId;
		}

		public PermissionChecker getPermissionChecker() {
			return _permissionChecker;
		}

		public long[] getRoleIds() {
			if (_roleIds != null) {
				return _roleIds;
			}

			_roleIds = _permissionChecker.getRoleIds(
				_permissionChecker.getUserId(), _groupId);

			return _roleIds;
		}

		public long[] getSegmentsEntryIds() {
			return _segmentsEntryIds;
		}

		public long getUserId() {
			return _permissionChecker.getUserId();
		}

		private LayoutStructureRulesContext(
			long groupId, PermissionChecker permissionChecker,
			long[] segmentsEntryIds) {

			_groupId = groupId;
			_permissionChecker = permissionChecker;
			_segmentsEntryIds = segmentsEntryIds;
		}

		private DDMExpressionFieldAccessor _ddmExpressionFieldAccessor;
		private DDMExpressionParameterAccessor _ddmExpressionParameterAccessor;
		private final long _groupId;
		private final PermissionChecker _permissionChecker;
		private long[] _roleIds;
		private final long[] _segmentsEntryIds;

	}

}