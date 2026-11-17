/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.util.structure;

import com.liferay.layout.util.constants.LayoutDataItemTypeConstants;
import com.liferay.petra.lang.HashUtil;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;

import java.util.Objects;

/**
 * @author Eudaldo Alonso
 */
public class DropZoneLayoutStructureItem extends LayoutStructureItem {

	public DropZoneLayoutStructureItem(String parentItemId) {
		super(parentItemId);
	}

	public DropZoneLayoutStructureItem(String itemId, String parentItemId) {
		super(itemId, parentItemId);
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof DropZoneLayoutStructureItem)) {
			return false;
		}

		DropZoneLayoutStructureItem dropZoneLayoutStructureItem =
			(DropZoneLayoutStructureItem)object;

		if (!Objects.equals(
				_allowNewFragmentEntries,
				dropZoneLayoutStructureItem._allowNewFragmentEntries) ||
			!Objects.equals(
				_fragmentEntriesJSONArray,
				dropZoneLayoutStructureItem._fragmentEntriesJSONArray)) {

			return false;
		}

		return super.equals(object);
	}

	public JSONArray getFragmentEntriesJSONArray() {
		return _fragmentEntriesJSONArray;
	}

	@Override
	public JSONObject getItemConfigJSONObject() {
		return JSONUtil.put(
			"allowNewFragmentEntries", _allowNewFragmentEntries
		).put(
			"fragmentEntries", _fragmentEntriesJSONArray
		);
	}

	@Override
	public String getItemType() {
		return LayoutDataItemTypeConstants.TYPE_DROP_ZONE;
	}

	@Override
	public int hashCode() {
		return HashUtil.hash(0, getItemId());
	}

	public boolean isAllowNewFragmentEntries() {
		return _allowNewFragmentEntries;
	}

	public void setAllowNewFragmentEntries(boolean allowNewFragmentEntries) {
		_allowNewFragmentEntries = allowNewFragmentEntries;
	}

	public void setFragmentEntriesJSONArray(
		JSONArray fragmentEntriesJSONArray) {

		_fragmentEntriesJSONArray = fragmentEntriesJSONArray;
	}

	@Override
	public void updateItemConfig(JSONObject itemConfigJSONObject) {
		if (itemConfigJSONObject.has("allowNewFragmentEntries")) {
			setAllowNewFragmentEntries(
				itemConfigJSONObject.getBoolean("allowNewFragmentEntries"));
		}

		if (itemConfigJSONObject.has("fragmentEntries")) {
			setFragmentEntriesJSONArray(
				itemConfigJSONObject.getJSONArray("fragmentEntries"));
		}
	}

	private boolean _allowNewFragmentEntries = true;
	private JSONArray _fragmentEntriesJSONArray;

}