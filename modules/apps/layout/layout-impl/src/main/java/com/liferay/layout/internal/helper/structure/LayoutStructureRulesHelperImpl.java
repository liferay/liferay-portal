/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.internal.helper.structure;

import com.liferay.dynamic.data.mapping.form.validation.util.DateParameterUtil;
import com.liferay.info.field.InfoField;
import com.liferay.info.field.InfoFieldValue;
import com.liferay.info.field.type.DateInfoFieldType;
import com.liferay.info.field.type.DateTimeInfoFieldType;
import com.liferay.info.field.type.InfoFieldType;
import com.liferay.info.field.type.PicklistMultiselectInfoFieldType;
import com.liferay.info.field.type.PicklistSelectInfoFieldType;
import com.liferay.info.item.InfoItemFieldValues;
import com.liferay.info.type.KeyLocalizedLabelPair;
import com.liferay.layout.helper.structure.LayoutStructureRulesHelper;
import com.liferay.layout.internal.util.AdvancedLayoutStructureRuleEvaluator;
import com.liferay.layout.util.structure.LayoutStructure;
import com.liferay.layout.util.structure.LayoutStructureRule;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.DateFormatFactoryUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import java.math.BigDecimal;

import java.text.DateFormat;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

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
		long groupId, InfoItemFieldValues infoItemFieldValues,
		LayoutStructure layoutStructure, Locale locale,
		PermissionChecker permissionChecker, long[] segmentsEntryIds) {

		Map<String, String> infoItemFieldTypesMap = _getInfoItemFieldTypesMap(
			infoItemFieldValues);
		Map<String, Object> infoItemFieldValuesMap = _getInfoItemFieldValuesMap(
			infoItemFieldValues, locale);
		Map<String, List<String>> itemIdsMap = new HashMap<>();
		JSONArray jsonArray = _jsonFactory.createJSONArray();
		Map<String, List<String>> layoutStructureRuleIdsMap = new HashMap<>();
		LayoutStructureRulesContext layoutStructureRulesContext =
			new LayoutStructureRulesContext(
				groupId, permissionChecker, segmentsEntryIds);

		for (LayoutStructureRule layoutStructureRule :
				layoutStructure.getLayoutStructureRules()) {

			List<String> itemIds = _getItemIds(layoutStructureRule);

			if (itemIds.isEmpty()) {
				_processActions(
					layoutStructureRule.getActionsJSONArray(), jsonArray,
					!_evaluateLayoutStructureRule(
						infoItemFieldTypesMap, infoItemFieldValuesMap,
						layoutStructureRule, layoutStructureRulesContext));

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
				enabledItemIds.remove(itemId);

				disabledItemIds.add(itemId);
			}
			else if (Objects.equals(
						jsonObject.getString("action"),
						Action.SHOW.getValue())) {

				hiddenItemIds.remove(itemId);

				displayedItemIds.add(itemId);
			}
			else if (Objects.equals(
						jsonObject.getString("action"),
						Action.ENABLE.getValue())) {

				disabledItemIds.remove(itemId);

				enabledItemIds.add(itemId);
			}
			else {
				displayedItemIds.remove(itemId);

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
					Collections.emptyMap(), fieldValuesMap, layoutStructureRule,
					layoutStructureRulesContext));
		}

		return jsonArray;
	}

	private boolean _contains(Object fieldValue, Object value) {
		if ((fieldValue == null) || (value == null)) {
			return false;
		}

		if (fieldValue instanceof JSONArray) {
			if (!(value instanceof JSONArray)) {
				return false;
			}

			JSONArray fieldValueJSONArray = (JSONArray)fieldValue;
			JSONArray valueJSONArray = (JSONArray)value;

			for (int i = 0; i < valueJSONArray.length(); i++) {
				if (JSONUtil.hasValue(
						fieldValueJSONArray, valueJSONArray.get(i))) {

					return true;
				}
			}

			return false;
		}

		String fieldValueString = StringUtil.toLowerCase(fieldValue.toString());
		String valueString = StringUtil.toLowerCase(value.toString());

		if (Validator.isNull(fieldValueString) ||
			Validator.isNull(valueString)) {

			return false;
		}

		return fieldValueString.contains(valueString);
	}

	private boolean _evaluateLayoutStructureRule(
		Map<String, String> fieldTypesMap, Map<String, Object> fieldValuesMap,
		LayoutStructureRule layoutStructureRule,
		LayoutStructureRulesContext layoutStructureRulesContext) {

		if (layoutStructureRule.isAdvancedRule()) {
			return _advancedLayoutStructureRuleEvaluator.evaluateScript(
				layoutStructureRulesContext.getGroupId(),
				layoutStructureRulesContext.getRoleIds(),
				layoutStructureRule.getScript(), fieldValuesMap,
				layoutStructureRulesContext.getSegmentsEntryIds(),
				layoutStructureRulesContext.getUser());
		}

		JSONArray conditionsJSONArray =
			layoutStructureRule.getConditionsJSONArray();

		for (int i = 0; i < conditionsJSONArray.length(); i++) {
			JSONObject conditionJSONObject = conditionsJSONArray.getJSONObject(
				i);

			if (_isConditionActive(
					conditionJSONObject, fieldTypesMap, fieldValuesMap,
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

	private Map<String, String> _getInfoItemFieldTypesMap(
		InfoItemFieldValues infoItemFieldValues) {

		Map<String, String> map = new HashMap<>();

		if (infoItemFieldValues == null) {
			return map;
		}

		for (InfoFieldValue<Object> infoFieldValue :
				infoItemFieldValues.getInfoFieldValues()) {

			InfoField<?> infoField = infoFieldValue.getInfoField();

			InfoFieldType infoFieldType = infoField.getInfoFieldType();

			map.put(infoField.getUniqueId(), infoFieldType.getName());
		}

		return map;
	}

	private Map<String, Object> _getInfoItemFieldValuesMap(
		InfoItemFieldValues infoItemFieldValues, Locale locale) {

		Map<String, Object> map = new HashMap<>();

		if (infoItemFieldValues == null) {
			return map;
		}

		for (InfoFieldValue<Object> infoFieldValue :
				infoItemFieldValues.getInfoFieldValues()) {

			InfoField infoField = infoFieldValue.getInfoField();

			Object value = infoFieldValue.getValue(locale);

			if (infoField.getInfoFieldType() == DateInfoFieldType.INSTANCE) {
				try {
					DateFormat dateFormat =
						DateFormatFactoryUtil.getSimpleDateFormat(
							"yyyy-MM-dd", locale);

					value = dateFormat.format(value);
				}
				catch (Exception exception) {
					if (_log.isDebugEnabled()) {
						_log.debug(
							"Unable to parse date from " + value, exception);
					}
				}
			}
			else if (infoField.getInfoFieldType() ==
						DateTimeInfoFieldType.INSTANCE) {

				try {
					DateTimeFormatter dateTimeFormatter =
						DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

					value = dateTimeFormatter.format((TemporalAccessor)value);
				}
				catch (Exception exception) {
					if (_log.isDebugEnabled()) {
						_log.debug(
							"Unable to parse date from " + value, exception);
					}
				}
			}
			else if (infoField.getInfoFieldType() ==
						PicklistMultiselectInfoFieldType.INSTANCE) {

				if (value instanceof List) {
					List<KeyLocalizedLabelPair> keyLocalizedLabelPairs =
						(List<KeyLocalizedLabelPair>)value;

					try {
						value = JSONUtil.toJSONArray(
							keyLocalizedLabelPairs,
							KeyLocalizedLabelPair::getKey);
					}
					catch (Exception exception) {
						if (_log.isDebugEnabled()) {
							_log.debug(exception);
						}
					}
				}
			}
			else if (infoField.getInfoFieldType() ==
						PicklistSelectInfoFieldType.INSTANCE) {

				if (value instanceof List) {
					List<KeyLocalizedLabelPair> keyLocalizedLabelPairs =
						(List<KeyLocalizedLabelPair>)value;

					if (ListUtil.isNotEmpty(keyLocalizedLabelPairs)) {
						KeyLocalizedLabelPair keyLocalizedLabelPair =
							keyLocalizedLabelPairs.get(0);

						value = keyLocalizedLabelPair.getKey();
					}
				}
			}

			if (value instanceof JSONArray) {
				map.put(infoField.getUniqueId(), value);
			}
			else {
				map.put(infoField.getUniqueId(), String.valueOf(value));
			}
		}

		return map;
	}

	private List<String> _getItemIds(LayoutStructureRule layoutStructureRule) {
		List<String> itemIds = new ArrayList<>();

		if (layoutStructureRule.isAdvancedRule()) {
			itemIds.addAll(
				_advancedLayoutStructureRuleEvaluator.getItemIds(
					layoutStructureRule));
		}
		else {
			JSONArray conditionsJSONArray =
				layoutStructureRule.getConditionsJSONArray();

			for (int i = 0; i < conditionsJSONArray.length(); i++) {
				JSONObject conditionJSONObject =
					conditionsJSONArray.getJSONObject(i);

				if (Objects.equals(
						conditionJSONObject.getString("type"), "field") ||
					Objects.equals(
						conditionJSONObject.getString("type"), "user")) {

					continue;
				}

				itemIds.add(conditionJSONObject.getString("field"));
			}
		}

		return itemIds;
	}

	private boolean _isConditionActive(
		JSONObject conditionJSONObject, Map<String, String> fieldTypesMap,
		Map<String, Object> fieldValuesMap,
		LayoutStructureRulesContext layoutStructureRulesContext) {

		String optionsType = null;
		Object value = 0L;

		JSONObject optionsJSONObject = conditionJSONObject.getJSONObject(
			"options");

		if (optionsJSONObject != null) {
			optionsType = optionsJSONObject.getString("type");
			value = optionsJSONObject.get("value");
		}

		if (Objects.equals(conditionJSONObject.getString("type"), "field") ||
			Objects.equals(conditionJSONObject.getString("type"), "form")) {

			String fieldName = conditionJSONObject.getString("field");

			Object fieldValue = fieldValuesMap.get(fieldName);

			if (Objects.equals(optionsType, "contains")) {
				return _contains(fieldValue, value);
			}

			if (Objects.equals(optionsType, "does-not-contain")) {
				return !_contains(fieldValue, value);
			}

			String fieldType = fieldTypesMap.get(fieldName);

			if (Objects.equals(optionsType, "greater-than")) {
				return _isGreaterThan(fieldType, fieldValue, value);
			}

			if (Objects.equals(optionsType, "greater-than-or-equals")) {
				return _isGreaterThanOrEqual(fieldType, fieldValue, value);
			}

			if (Objects.equals(optionsType, "is-empty")) {
				return _isEmpty(fieldValue);
			}

			if (Objects.equals(optionsType, "is-not-empty")) {
				return !_isEmpty(fieldValue);
			}

			if (Objects.equals(optionsType, "less-than")) {
				return _isLessThan(fieldType, fieldValue, value);
			}

			if (Objects.equals(optionsType, "less-than-or-equals")) {
				return _isLessThanOrEqual(fieldType, fieldValue, value);
			}

			if (Objects.equals(optionsType, "not-equal")) {
				return !_isEqual(fieldValue, value);
			}

			return _isEqual(fieldValue, value);
		}

		if (Objects.equals(conditionJSONObject.getString("type"), "user")) {
			return _evaluateUserTypeCondition(
				conditionJSONObject.getString("field"),
				layoutStructureRulesContext,
				Objects.equals(optionsType, "not-equal"),
				GetterUtil.getLong(value));
		}

		return false;
	}

	private boolean _isEmpty(Object fieldValue) {
		if (fieldValue == null) {
			return true;
		}

		if (fieldValue instanceof JSONArray) {
			return JSONUtil.isEmpty((JSONArray)fieldValue);
		}

		return Validator.isNull(fieldValue.toString());
	}

	private boolean _isEqual(Object fieldValue, Object value) {
		if ((fieldValue instanceof JSONArray) && (value instanceof JSONArray)) {
			JSONArray fieldValueJSONArray = (JSONArray)fieldValue;
			JSONArray valueJSONArray = (JSONArray)value;

			if (fieldValueJSONArray.length() != valueJSONArray.length()) {
				return false;
			}

			for (int i = 0; i < valueJSONArray.length(); i++) {
				if (!JSONUtil.hasValue(
						fieldValueJSONArray, valueJSONArray.get(i))) {

					return false;
				}
			}

			return true;
		}

		return Objects.equals(fieldValue, value);
	}

	private boolean _isGreaterThan(
		String fieldType, Object fieldValue, Object value) {

		if (Objects.equals(fieldType, "date") ||
			Objects.equals(fieldType, "date-time")) {

			LocalDateTime fieldLocalDateTime = _toLocalDateTime(fieldValue);
			LocalDateTime valueLocalDateTime = _toLocalDateTime(value);

			if ((fieldLocalDateTime == null) || (valueLocalDateTime == null)) {
				return false;
			}

			return fieldLocalDateTime.isAfter(valueLocalDateTime);
		}

		if (Objects.equals(fieldType, "number")) {
			BigDecimal fieldBigDecimal = _toBigDecimal(fieldValue);
			BigDecimal valueBigDecimal = _toBigDecimal(value);

			if ((fieldBigDecimal == null) || (valueBigDecimal == null)) {
				return false;
			}

			if (fieldBigDecimal.compareTo(valueBigDecimal) > 0) {
				return true;
			}

			return false;
		}

		return false;
	}

	private boolean _isGreaterThanOrEqual(
		String fieldType, Object fieldValue, Object value) {

		if (Objects.equals(fieldType, "date") ||
			Objects.equals(fieldType, "date-time")) {

			LocalDateTime fieldLocalDateTime = _toLocalDateTime(fieldValue);
			LocalDateTime valueLocalDateTime = _toLocalDateTime(value);

			if ((fieldLocalDateTime == null) || (valueLocalDateTime == null)) {
				return false;
			}

			return !fieldLocalDateTime.isBefore(valueLocalDateTime);
		}

		if (Objects.equals(fieldType, "number")) {
			BigDecimal fieldBigDecimal = _toBigDecimal(fieldValue);
			BigDecimal valueBigDecimal = _toBigDecimal(value);

			if ((fieldBigDecimal == null) || (valueBigDecimal == null)) {
				return false;
			}

			if (fieldBigDecimal.compareTo(valueBigDecimal) >= 0) {
				return true;
			}

			return false;
		}

		return false;
	}

	private boolean _isLessThan(
		String fieldType, Object fieldValue, Object value) {

		if (Objects.equals(fieldType, "date") ||
			Objects.equals(fieldType, "date-time")) {

			LocalDateTime fieldLocalDateTime = _toLocalDateTime(fieldValue);
			LocalDateTime valueLocalDateTime = _toLocalDateTime(value);

			if ((fieldLocalDateTime == null) || (valueLocalDateTime == null)) {
				return false;
			}

			return fieldLocalDateTime.isBefore(valueLocalDateTime);
		}

		if (Objects.equals(fieldType, "number")) {
			BigDecimal fieldBigDecimal = _toBigDecimal(fieldValue);
			BigDecimal valueBigDecimal = _toBigDecimal(value);

			if ((fieldBigDecimal == null) || (valueBigDecimal == null)) {
				return false;
			}

			if (fieldBigDecimal.compareTo(valueBigDecimal) < 0) {
				return true;
			}

			return false;
		}

		return false;
	}

	private boolean _isLessThanOrEqual(
		String fieldType, Object fieldValue, Object value) {

		if (Objects.equals(fieldType, "date") ||
			Objects.equals(fieldType, "date-time")) {

			LocalDateTime fieldLocalDateTime = _toLocalDateTime(fieldValue);
			LocalDateTime valueLocalDateTime = _toLocalDateTime(value);

			if ((fieldLocalDateTime == null) || (valueLocalDateTime == null)) {
				return false;
			}

			return !fieldLocalDateTime.isAfter(valueLocalDateTime);
		}

		if (Objects.equals(fieldType, "number")) {
			BigDecimal fieldBigDecimal = _toBigDecimal(fieldValue);
			BigDecimal valueBigDecimal = _toBigDecimal(value);

			if ((fieldBigDecimal == null) || (valueBigDecimal == null)) {
				return false;
			}

			if (fieldBigDecimal.compareTo(valueBigDecimal) <= 0) {
				return true;
			}

			return false;
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

	private BigDecimal _toBigDecimal(Object value) {
		if (value == null) {
			return null;
		}

		try {
			return new BigDecimal(value.toString());
		}
		catch (NumberFormatException numberFormatException) {
			if (_log.isDebugEnabled()) {
				_log.debug(
					"Unable to parse number from " + value,
					numberFormatException);
			}

			return null;
		}
	}

	private LocalDateTime _toLocalDateTime(Object value) {
		if (value == null) {
			return null;
		}

		return DateParameterUtil.getLocalDateTime(value.toString());
	}

	private static final Log _log = LogFactoryUtil.getLog(
		LayoutStructureRulesHelperImpl.class);

	@Reference
	private AdvancedLayoutStructureRuleEvaluator
		_advancedLayoutStructureRuleEvaluator;

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

		public long getGroupId() {
			return _groupId;
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

		public User getUser() {
			return _permissionChecker.getUser();
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

		private final long _groupId;
		private final PermissionChecker _permissionChecker;
		private long[] _roleIds;
		private final long[] _segmentsEntryIds;

	}

}