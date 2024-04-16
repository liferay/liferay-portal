/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.util.structure;

import com.liferay.petra.lang.HashUtil;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.util.Validator;

import java.util.Objects;

/**
 * @author Lourdes Fernández Besada
 */
public class LayoutStructureRule {

	public static LayoutStructureRule of(JSONObject jsonObject) {
		return new LayoutStructureRule(
			jsonObject.getJSONArray("actions"),
			jsonObject.getJSONArray("conditions"),
			jsonObject.getString("conditionType"), jsonObject.getString("id"),
			jsonObject.getString("name"), jsonObject.getString("script"));
	}

	public LayoutStructureRule(
		JSONArray actionsJSONArray, JSONArray conditionsJSONArray,
		String conditionType, String id, String name, String script) {

		_actionsJSONArray = actionsJSONArray;
		_conditionsJSONArray = conditionsJSONArray;

		if (Validator.isNotNull(conditionType)) {
			_conditionType = conditionType;
		}

		_id = id;
		_name = name;
		_script = script;
	}

	public LayoutStructureRule(String id, String name) {
		this(
			JSONFactoryUtil.createJSONArray(),
			JSONFactoryUtil.createJSONArray(), null, id, name, null);
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof LayoutStructureRule)) {
			return false;
		}

		LayoutStructureRule layoutStructureRule = (LayoutStructureRule)object;

		if (Objects.equals(_id, layoutStructureRule._id) &&
			Objects.equals(_name, layoutStructureRule._name)) {

			return true;
		}

		return false;
	}

	public JSONArray getActionsJSONArray() {
		return _actionsJSONArray;
	}

	public JSONArray getConditionsJSONArray() {
		return _conditionsJSONArray;
	}

	public String getConditionType() {
		if (Validator.isNull(_conditionType)) {
			return "all";
		}

		return _conditionType;
	}

	public String getId() {
		return _id;
	}

	public String getName() {
		return _name;
	}

	public String getScript() {
		return _script;
	}

	@Override
	public int hashCode() {
		return HashUtil.hash(0, getId());
	}

	public boolean isAdvancedRule() {
		if (Validator.isNull(_script)) {
			return false;
		}

		return true;
	}

	public void setActionsJSONArray(JSONArray actionsJSONArray) {
		_actionsJSONArray = actionsJSONArray;
	}

	public void setConditionsJSONArray(JSONArray conditionsJSONArray) {
		_conditionsJSONArray = conditionsJSONArray;
	}

	public void setConditionType(String conditionType) {
		_conditionType = conditionType;
	}

	public void setId(String id) {
		_id = id;
	}

	public void setName(String name) {
		_name = name;
	}

	public void setScript(String script) {
		_script = script;
	}

	public JSONObject toJSONObject() {
		return JSONUtil.put(
			"actions", _actionsJSONArray
		).put(
			"conditions", _conditionsJSONArray
		).put(
			"conditionType", _conditionType
		).put(
			"id", getId()
		).put(
			"name", getName()
		).put(
			"script", getScript()
		);
	}

	@Override
	public String toString() {
		JSONObject jsonObject = toJSONObject();

		return jsonObject.toString();
	}

	private JSONArray _actionsJSONArray;
	private JSONArray _conditionsJSONArray;
	private String _conditionType = "all";
	private String _id;
	private String _name;
	private String _script;

}