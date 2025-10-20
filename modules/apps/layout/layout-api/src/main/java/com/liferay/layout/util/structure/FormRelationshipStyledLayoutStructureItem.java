/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.util.structure;

import com.liferay.layout.util.constants.LayoutDataItemTypeConstants;
import com.liferay.petra.lang.HashUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.util.Validator;

import java.util.Objects;

/**
 * @author Víctor Galán
 */
public class FormRelationshipStyledLayoutStructureItem
	extends StyledLayoutStructureItem {

	public FormRelationshipStyledLayoutStructureItem(String parentItemId) {
		super(parentItemId);
	}

	public FormRelationshipStyledLayoutStructureItem(
		String itemId, String parentItemId) {

		super(itemId, parentItemId);
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof FormRelationshipStyledLayoutStructureItem)) {
			return false;
		}

		FormRelationshipStyledLayoutStructureItem
			formRelationshipStyledLayoutStructureItem =
				(FormRelationshipStyledLayoutStructureItem)object;

		if (!Objects.equals(
				_contentType,
				formRelationshipStyledLayoutStructureItem._contentType)) {

			return false;
		}

		return super.equals(object);
	}

	public JSONObject getButtonLabelJSONObject() {
		return _buttonLabelJSONObject;
	}

	public String getContentType() {
		return _contentType;
	}

	@Override
	public JSONObject getItemConfigJSONObject() {
		JSONObject jsonObject = super.getItemConfigJSONObject();

		return jsonObject.put(
			"buttonLabel", _buttonLabelJSONObject
		).put(
			"contentType",
			() -> {
				if (Validator.isBlank(_contentType)) {
					return null;
				}

				return _contentType;
			}
		).put(
			"repeatable", _repeatable
		);
	}

	@Override
	public String getItemType() {
		return LayoutDataItemTypeConstants.TYPE_FORM_RELATIONSHIP;
	}

	@Override
	public int hashCode() {
		return HashUtil.hash(0, getItemId());
	}

	public boolean isRepeatable() {
		return _repeatable;
	}

	public void setButtonLabelJSONObject(JSONObject buttonLabelJSONObject) {
		_buttonLabelJSONObject = buttonLabelJSONObject;
	}

	public void setContentType(String contentType) {
		_contentType = contentType;
	}

	public void setRepeatable(boolean repeatable) {
		_repeatable = repeatable;
	}

	@Override
	public void updateItemConfig(JSONObject itemConfigJSONObject) {
		super.updateItemConfig(itemConfigJSONObject);

		if (itemConfigJSONObject.has("buttonLabel")) {
			setButtonLabelJSONObject(
				itemConfigJSONObject.getJSONObject("buttonLabel"));
		}

		if (itemConfigJSONObject.has("contentType")) {
			setContentType(itemConfigJSONObject.getString("contentType"));
		}

		if (itemConfigJSONObject.has("repeatable")) {
			setRepeatable(itemConfigJSONObject.getBoolean("repeatable"));
		}
	}

	private JSONObject _buttonLabelJSONObject;
	private String _contentType = "";
	private boolean _repeatable = true;

}