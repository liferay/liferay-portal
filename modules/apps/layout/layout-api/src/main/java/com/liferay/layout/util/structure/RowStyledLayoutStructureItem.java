/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.util.structure;

import com.liferay.layout.responsive.ViewportSize;
import com.liferay.layout.util.constants.LayoutDataItemTypeConstants;
import com.liferay.layout.util.constants.StyledLayoutStructureConstants;
import com.liferay.petra.lang.HashUtil;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author Eudaldo Alonso
 */
public class RowStyledLayoutStructureItem extends StyledLayoutStructureItem {

	public RowStyledLayoutStructureItem(String parentItemId) {
		super(parentItemId);
	}

	public RowStyledLayoutStructureItem(String itemId, String parentItemId) {
		super(itemId, parentItemId);
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof RowStyledLayoutStructureItem)) {
			return false;
		}

		RowStyledLayoutStructureItem rowStyledLayoutStructureItem =
			(RowStyledLayoutStructureItem)object;

		if (!Objects.equals(_gutters, rowStyledLayoutStructureItem._gutters) ||
			!Objects.equals(
				_modulesPerRow, rowStyledLayoutStructureItem._modulesPerRow) ||
			!Objects.equals(
				_numberOfColumns,
				rowStyledLayoutStructureItem._numberOfColumns) ||
			!Objects.equals(
				_reverseOrder, rowStyledLayoutStructureItem._reverseOrder) ||
			!Objects.equals(
				_verticalAlignment,
				rowStyledLayoutStructureItem._verticalAlignment)) {

			return false;
		}

		return super.equals(object);
	}

	@Override
	public JSONObject getItemConfigJSONObject() {
		JSONObject jsonObject = super.getItemConfigJSONObject();

		jsonObject.put(
			"gutters", _gutters
		).put(
			"indexed",
			() -> {
				if (_indexed) {
					return null;
				}

				return false;
			}
		).put(
			"modulesPerRow", getModulesPerRow()
		).put(
			"numberOfColumns", _numberOfColumns
		).put(
			"reverseOrder", _reverseOrder
		).put(
			"verticalAlignment",
			() -> {
				if (Objects.equals(_verticalAlignment, "top")) {
					return null;
				}

				return _verticalAlignment;
			}
		);

		for (ViewportSize viewportSize : _viewportSizes) {
			if (viewportSize.equals(ViewportSize.DESKTOP)) {
				continue;
			}

			JSONObject currentViewportConfigurationJSONObject =
				JSONFactoryUtil.createJSONObject();

			if (jsonObject.has(viewportSize.getViewportSizeId())) {
				currentViewportConfigurationJSONObject =
					jsonObject.getJSONObject(viewportSize.getViewportSizeId());
			}

			JSONObject viewportConfigurationJSONObject =
				_viewportConfigurationJSONObjects.getOrDefault(
					viewportSize.getViewportSizeId(),
					JSONFactoryUtil.createJSONObject());

			currentViewportConfigurationJSONObject.put(
				"modulesPerRow",
				viewportConfigurationJSONObject.get("modulesPerRow")
			).put(
				"reverseOrder",
				viewportConfigurationJSONObject.get("reverseOrder")
			).put(
				"verticalAlignment",
				viewportConfigurationJSONObject.get("verticalAlignment")
			);

			jsonObject.put(
				viewportSize.getViewportSizeId(),
				currentViewportConfigurationJSONObject);
		}

		return jsonObject;
	}

	@Override
	public String getItemType() {
		return LayoutDataItemTypeConstants.TYPE_ROW;
	}

	public int getModulesPerRow() {
		if (_modulesPerRow == null) {
			return _numberOfColumns;
		}

		return _modulesPerRow;
	}

	public int getNumberOfColumns() {
		return _numberOfColumns;
	}

	public String getVerticalAlignment() {
		return _verticalAlignment;
	}

	public Map<String, JSONObject> getViewportConfigurationJSONObjects() {
		return _viewportConfigurationJSONObjects;
	}

	@Override
	public int hashCode() {
		return HashUtil.hash(0, getItemId());
	}

	public boolean isGutters() {
		return _gutters;
	}

	public boolean isIndexed() {
		return _indexed;
	}

	public boolean isReverseOrder() {
		return _reverseOrder;
	}

	public void setGutters(boolean gutters) {
		_gutters = gutters;
	}

	public void setIndexed(boolean indexed) {
		_indexed = indexed;
	}

	public void setModulesPerRow(int modulesPerRow) {
		_modulesPerRow = modulesPerRow;
	}

	public void setNumberOfColumns(int numberOfColumns) {
		_numberOfColumns = numberOfColumns;
	}

	public void setReverseOrder(boolean reverseOrder) {
		_reverseOrder = reverseOrder;
	}

	public void setVerticalAlignment(String verticalAlignment) {
		_verticalAlignment = verticalAlignment;
	}

	public void setViewportConfiguration(
		String viewportSizeId, JSONObject configurationJSONObject) {

		_viewportConfigurationJSONObjects.put(
			viewportSizeId,
			_viewportConfigurationJSONObjects.getOrDefault(
				viewportSizeId, JSONFactoryUtil.createJSONObject()
			).put(
				"customCSS",
				() -> {
					if (configurationJSONObject.has("customCSS")) {
						return configurationJSONObject.getString("customCSS");
					}

					return null;
				}
			).put(
				"modulesPerRow",
				() -> {
					if (configurationJSONObject.has("modulesPerRow")) {
						return configurationJSONObject.getInt("modulesPerRow");
					}

					return null;
				}
			).put(
				"reverseOrder",
				() -> {
					if (!configurationJSONObject.has("reverseOrder")) {
						return null;
					}

					return configurationJSONObject.getBoolean("reverseOrder");
				}
			).put(
				"verticalAlignment",
				() -> {
					if (!configurationJSONObject.has("verticalAlignment")) {
						return null;
					}

					return configurationJSONObject.getString(
						"verticalAlignment");
				}
			));
	}

	@Override
	public void updateItemConfig(JSONObject itemConfigJSONObject) {
		super.updateItemConfig(itemConfigJSONObject);

		if (itemConfigJSONObject.has("gutters")) {
			setGutters(itemConfigJSONObject.getBoolean("gutters"));
		}

		if (itemConfigJSONObject.has("modulesPerRow")) {
			setModulesPerRow(itemConfigJSONObject.getInt("modulesPerRow"));
		}

		if (itemConfigJSONObject.has("indexed")) {
			setIndexed(itemConfigJSONObject.getBoolean("indexed"));
		}

		if (itemConfigJSONObject.has("numberOfColumns")) {
			setNumberOfColumns(itemConfigJSONObject.getInt("numberOfColumns"));
		}

		if (itemConfigJSONObject.has("reverseOrder")) {
			setReverseOrder(itemConfigJSONObject.getBoolean("reverseOrder"));
		}

		if (itemConfigJSONObject.has("verticalAlignment")) {
			setVerticalAlignment(
				itemConfigJSONObject.getString("verticalAlignment"));
		}

		for (ViewportSize viewportSize : _viewportSizes) {
			if (viewportSize.equals(ViewportSize.DESKTOP)) {
				continue;
			}

			if (itemConfigJSONObject.has(viewportSize.getViewportSizeId())) {
				setViewportConfiguration(
					viewportSize.getViewportSizeId(),
					itemConfigJSONObject.getJSONObject(
						viewportSize.getViewportSizeId()));
			}
		}
	}

	private static final ViewportSize[] _viewportSizes = ViewportSize.values();

	private boolean _gutters = true;
	private boolean _indexed = true;
	private Integer _modulesPerRow;
	private int _numberOfColumns;
	private boolean _reverseOrder;
	private String _verticalAlignment =
		StyledLayoutStructureConstants.VERTICAL_ALIGNMENT_TOP;
	private final Map<String, JSONObject> _viewportConfigurationJSONObjects =
		new HashMap<>();

}