/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.util.structure;

import com.liferay.layout.responsive.ViewportSize;
import com.liferay.layout.util.constants.LayoutDataItemTypeConstants;
import com.liferay.petra.lang.HashUtil;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author Eudaldo Alonso
 */
public class ColumnLayoutStructureItem extends LayoutStructureItem {

	public ColumnLayoutStructureItem(String parentItemId) {
		super(parentItemId);
	}

	public ColumnLayoutStructureItem(String itemId, String parentItemId) {
		super(itemId, parentItemId);
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof ColumnLayoutStructureItem)) {
			return false;
		}

		ColumnLayoutStructureItem columnLayoutStructureItem =
			(ColumnLayoutStructureItem)object;

		if (!Objects.equals(_size, columnLayoutStructureItem._size)) {
			return false;
		}

		return super.equals(object);
	}

	@Override
	public JSONObject getItemConfigJSONObject() {
		JSONObject jsonObject = JSONUtil.put("size", _size);

		for (ViewportSize viewportSize : _viewportSizes) {
			if (viewportSize.equals(ViewportSize.DESKTOP)) {
				continue;
			}

			jsonObject.put(
				viewportSize.getViewportSizeId(),
				JSONUtil.put(
					"size",
					() -> {
						JSONObject viewportConfigurationJSONObject =
							_viewportConfigurationJSONObjects.getOrDefault(
								viewportSize.getViewportSizeId(),
								JSONFactoryUtil.createJSONObject());

						return viewportConfigurationJSONObject.get("size");
					}));
		}

		return jsonObject;
	}

	@Override
	public String getItemType() {
		return LayoutDataItemTypeConstants.TYPE_COLUMN;
	}

	public int getSize() {
		return _size;
	}

	public Map<String, JSONObject> getViewportConfigurationJSONObjects() {
		return _viewportConfigurationJSONObjects;
	}

	@Override
	public int hashCode() {
		return HashUtil.hash(0, getItemId());
	}

	public void setSize(int size) {
		_size = size;
	}

	public void setViewportConfiguration(
		String viewportSizeId, JSONObject configurationJSONObject) {

		JSONObject viewportConfigurationJSONObject =
			_viewportConfigurationJSONObjects.getOrDefault(
				viewportSizeId, JSONFactoryUtil.createJSONObject());

		_viewportConfigurationJSONObjects.put(
			viewportSizeId,
			viewportConfigurationJSONObject.put(
				"size",
				() -> {
					if (configurationJSONObject.has("size")) {
						return configurationJSONObject.getInt("size");
					}

					return null;
				}));
	}

	@Override
	public void updateItemConfig(JSONObject itemConfigJSONObject) {
		if (itemConfigJSONObject.has("size")) {
			setSize(itemConfigJSONObject.getInt("size"));
		}

		for (ViewportSize viewportSize : _viewportSizes) {
			if (viewportSize.equals(ViewportSize.DESKTOP) ||
				!itemConfigJSONObject.has(viewportSize.getViewportSizeId())) {

				continue;
			}

			setViewportConfiguration(
				viewportSize.getViewportSizeId(),
				itemConfigJSONObject.getJSONObject(
					viewportSize.getViewportSizeId()));
		}
	}

	private static final ViewportSize[] _viewportSizes = ViewportSize.values();

	private int _size;
	private final Map<String, JSONObject> _viewportConfigurationJSONObjects =
		new HashMap<>();

}