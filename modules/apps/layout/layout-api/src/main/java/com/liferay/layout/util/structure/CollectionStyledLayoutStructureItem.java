/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.util.structure;

import com.liferay.layout.responsive.ViewportSize;
import com.liferay.layout.util.CollectionPaginationUtil;
import com.liferay.layout.util.constants.LayoutDataItemTypeConstants;
import com.liferay.layout.util.constants.StyledLayoutStructureConstants;
import com.liferay.layout.util.structure.collection.EmptyCollectionOptions;
import com.liferay.petra.lang.HashUtil;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.util.Validator;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author Eudaldo Alonso
 */
public class CollectionStyledLayoutStructureItem
	extends StyledLayoutStructureItem {

	public CollectionStyledLayoutStructureItem(String parentItemId) {
		super(parentItemId);
	}

	public CollectionStyledLayoutStructureItem(
		String itemId, String parentItemId) {

		super(itemId, parentItemId);
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof CollectionStyledLayoutStructureItem)) {
			return false;
		}

		CollectionStyledLayoutStructureItem
			collectionStyledLayoutStructureItem =
				(CollectionStyledLayoutStructureItem)object;

		if (!Objects.equals(
				_collectionJSONObject,
				collectionStyledLayoutStructureItem._collectionJSONObject) ||
			!Objects.equals(
				_displayAllItems,
				collectionStyledLayoutStructureItem._displayAllItems) ||
			!Objects.equals(
				_displayAllPages,
				collectionStyledLayoutStructureItem._displayAllPages) ||
			!Objects.equals(
				_emptyCollectionOptions,
				collectionStyledLayoutStructureItem._emptyCollectionOptions) ||
			!Objects.equals(
				_gutters, collectionStyledLayoutStructureItem._gutters) ||
			!Objects.equals(
				_listStyle, collectionStyledLayoutStructureItem._listStyle) ||
			!Objects.equals(
				_numberOfColumns,
				collectionStyledLayoutStructureItem._numberOfColumns) ||
			!Objects.equals(
				_numberOfItems,
				collectionStyledLayoutStructureItem._numberOfItems) ||
			!Objects.equals(
				_numberOfItemsPerPage,
				collectionStyledLayoutStructureItem._numberOfItemsPerPage) ||
			!Objects.equals(
				_numberOfPages,
				collectionStyledLayoutStructureItem._numberOfPages) ||
			!Objects.equals(
				_paginationType,
				collectionStyledLayoutStructureItem._paginationType) ||
			!Objects.equals(
				_showAllItems,
				collectionStyledLayoutStructureItem._showAllItems) ||
			!Objects.equals(
				_verticalAlignment,
				collectionStyledLayoutStructureItem._verticalAlignment)) {

			return false;
		}

		return super.equals(object);
	}

	public String getAlign() {
		return _align;
	}

	public JSONObject getCollectionJSONObject() {
		return _collectionJSONObject;
	}

	public EmptyCollectionOptions getEmptyCollectionOptions() {
		return _emptyCollectionOptions;
	}

	public String getFlexWrap() {
		return _flexWrap;
	}

	@Override
	public JSONObject getItemConfigJSONObject() {
		JSONObject jsonObject = super.getItemConfigJSONObject();

		jsonObject = jsonObject.put(
			"align",
			() -> {
				if (Validator.isBlank(_align)) {
					return null;
				}

				return _align;
			}
		).put(
			"collection", _collectionJSONObject
		).put(
			"displayAllItems", _displayAllItems
		).put(
			"displayAllPages", _displayAllPages
		).put(
			"emptyCollectionOptions",
			() -> {
				if (_emptyCollectionOptions == null) {
					return null;
				}

				return _emptyCollectionOptions.toJSONObject();
			}
		).put(
			"flexWrap",
			() -> {
				if (Validator.isBlank(_flexWrap)) {
					return null;
				}

				return _flexWrap;
			}
		).put(
			"gutters", _gutters
		).put(
			"justify",
			() -> {
				if (Validator.isBlank(_justify)) {
					return null;
				}

				return _justify;
			}
		).put(
			"listItemStyle", _listItemStyle
		).put(
			"listStyle", _listStyle
		).put(
			"numberOfColumns", _numberOfColumns
		).put(
			"numberOfItems", _numberOfItems
		).put(
			"numberOfItemsPerPage", _numberOfItemsPerPage
		).put(
			"numberOfPages", _numberOfPages
		).put(
			"paginationType", _paginationType
		).put(
			"showAllItems", _showAllItems
		).put(
			"templateKey", _templateKey
		).put(
			"verticalAlignment",
			() -> {
				if (Objects.equals(_verticalAlignment, "start")) {
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
				"numberOfColumns",
				viewportConfigurationJSONObject.get("numberOfColumns"));

			jsonObject.put(
				viewportSize.getViewportSizeId(),
				currentViewportConfigurationJSONObject);
		}

		return jsonObject;
	}

	@Override
	public String getItemType() {
		return LayoutDataItemTypeConstants.TYPE_COLLECTION;
	}

	public String getJustify() {
		return _justify;
	}

	public String getListItemStyle() {
		return _listItemStyle;
	}

	public String getListStyle() {
		return _listStyle;
	}

	public int getNumberOfColumns() {
		return _numberOfColumns;
	}

	public int getNumberOfItems() {
		return _numberOfItems;
	}

	public int getNumberOfItemsPerPage() {
		return _numberOfItemsPerPage;
	}

	public int getNumberOfPages() {
		return _numberOfPages;
	}

	public String getPaginationType() {
		return _paginationType;
	}

	public String getTemplateKey() {
		return _templateKey;
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

	public boolean isDisplayAllItems() {
		return _displayAllItems;
	}

	public boolean isDisplayAllPages() {
		return _displayAllPages;
	}

	public boolean isGutters() {
		return _gutters;
	}

	/**
	 * @deprecated As of Cavanaugh (7.4.x), replaced by {@link
	 *             #isDisplayAllItems()}
	 */
	@Deprecated
	public boolean isShowAllItems() {
		return _showAllItems;
	}

	public void setAlign(String align) {
		_align = align;
	}

	public void setCollectionJSONObject(JSONObject collectionJSONObject) {
		_collectionJSONObject = collectionJSONObject;
	}

	public void setDisplayAllItems(Boolean displayAllItems) {
		if (displayAllItems == null) {
			_displayAllItems = false;
		}
		else {
			_displayAllItems = displayAllItems;
		}
	}

	public void setDisplayAllPages(Boolean displayAllPages) {
		if (displayAllPages == null) {
			_displayAllPages = true;
		}
		else {
			_displayAllPages = displayAllPages;
		}
	}

	public void setEmptyCollectionOptions(
		EmptyCollectionOptions emptyCollectionOptions) {

		_emptyCollectionOptions = emptyCollectionOptions;
	}

	public void setFlexWrap(String flexWrap) {
		_flexWrap = flexWrap;
	}

	public void setGutters(boolean gutters) {
		_gutters = gutters;
	}

	public void setJustify(String justify) {
		_justify = justify;
	}

	public void setListItemStyle(String listItemStyle) {
		_listItemStyle = listItemStyle;
	}

	public void setListStyle(String listStyle) {
		_listStyle = listStyle;
	}

	public void setNumberOfColumns(int numberOfColumns) {
		_numberOfColumns = numberOfColumns;
	}

	public void setNumberOfItems(int numberOfItems) {
		_numberOfItems = numberOfItems;
	}

	public void setNumberOfItemsPerPage(int numberOfItemsPerPage) {
		_numberOfItemsPerPage = numberOfItemsPerPage;
	}

	public void setNumberOfPages(int numberOfPages) {
		_numberOfPages = numberOfPages;
	}

	public void setPaginationType(String paginationType) {
		_paginationType = paginationType;
	}

	/**
	 * @deprecated As of Cavanaugh (7.4.x), replaced by {@link
	 *             #setDisplayAllItems(Boolean)}
	 */
	@Deprecated
	public void setShowAllItems(Boolean showAllItems) {
		if (showAllItems == null) {
			_showAllItems = false;
		}
		else {
			_showAllItems = showAllItems;
		}
	}

	public void setTemplateKey(String templateKey) {
		_templateKey = templateKey;
	}

	public void setVerticalAlignment(String verticalAlignment) {
		_verticalAlignment = verticalAlignment;
	}

	public void setViewportConfiguration(
		String viewportSizeId, JSONObject configurationJSONObject) {

		JSONObject viewportConfigurationJSONObject =
			_viewportConfigurationJSONObjects.getOrDefault(
				viewportSizeId, JSONFactoryUtil.createJSONObject());

		_viewportConfigurationJSONObjects.put(
			viewportSizeId,
			viewportConfigurationJSONObject.put(
				"align",
				() -> {
					if (!configurationJSONObject.has("align")) {
						return null;
					}

					return configurationJSONObject.getString("align");
				}
			).put(
				"flexWrap",
				() -> {
					if (!configurationJSONObject.has("flexWrap")) {
						return null;
					}

					return configurationJSONObject.getString("flexWrap");
				}
			).put(
				"justify",
				() -> {
					if (!configurationJSONObject.has("justify")) {
						return null;
					}

					return configurationJSONObject.getString("justify");
				}
			).put(
				"numberOfColumns",
				() -> {
					if (!configurationJSONObject.has("numberOfColumns")) {
						return null;
					}

					return configurationJSONObject.getInt("numberOfColumns");
				}
			).put(
				"styles",
				() -> {
					if (!configurationJSONObject.has("styles")) {
						return null;
					}

					return configurationJSONObject.getJSONObject("styles");
				}
			));
	}

	@Override
	public void updateItemConfig(JSONObject itemConfigJSONObject) {
		super.updateItemConfig(itemConfigJSONObject);

		if (itemConfigJSONObject.has("align")) {
			setAlign(itemConfigJSONObject.getString("align"));
		}

		if (itemConfigJSONObject.has("collection")) {
			setCollectionJSONObject(
				itemConfigJSONObject.getJSONObject("collection"));
		}

		if (itemConfigJSONObject.has("displayAllItems")) {
			setDisplayAllItems(
				itemConfigJSONObject.getBoolean("displayAllItems"));
		}

		if (itemConfigJSONObject.has("displayAllPages")) {
			setDisplayAllPages(
				itemConfigJSONObject.getBoolean("displayAllPages"));
		}

		if (itemConfigJSONObject.has("emptyCollectionOptions")) {
			setEmptyCollectionOptions(
				EmptyCollectionOptions.of(
					itemConfigJSONObject.getJSONObject(
						"emptyCollectionOptions")));
		}

		if (itemConfigJSONObject.has("flexWrap")) {
			setFlexWrap(itemConfigJSONObject.getString("flexWrap"));
		}

		if (itemConfigJSONObject.has("gutters")) {
			setGutters(itemConfigJSONObject.getBoolean("gutters"));
		}

		if (itemConfigJSONObject.has("justify")) {
			setJustify(itemConfigJSONObject.getString("justify"));
		}

		if (itemConfigJSONObject.has("listItemStyle")) {
			setListItemStyle(itemConfigJSONObject.getString("listItemStyle"));
		}

		if (itemConfigJSONObject.has("listStyle")) {
			setListStyle(itemConfigJSONObject.getString("listStyle"));
		}

		if (itemConfigJSONObject.has("numberOfColumns")) {
			setNumberOfColumns(itemConfigJSONObject.getInt("numberOfColumns"));
		}

		if (itemConfigJSONObject.has("numberOfItems")) {
			setNumberOfItems(itemConfigJSONObject.getInt("numberOfItems"));
		}

		if (itemConfigJSONObject.has("numberOfItemsPerPage")) {
			setNumberOfItemsPerPage(
				itemConfigJSONObject.getInt("numberOfItemsPerPage"));
		}

		if (itemConfigJSONObject.has("numberOfPages")) {
			setNumberOfPages(itemConfigJSONObject.getInt("numberOfPages"));
		}

		if (itemConfigJSONObject.has("paginationType")) {
			setPaginationType(itemConfigJSONObject.getString("paginationType"));
		}

		if (itemConfigJSONObject.has("showAllItems")) {
			setShowAllItems(itemConfigJSONObject.getBoolean("showAllItems"));
		}

		if (itemConfigJSONObject.has("templateKey")) {
			setTemplateKey(itemConfigJSONObject.getString("templateKey"));
		}

		if (itemConfigJSONObject.has("verticalAlignment")) {
			setVerticalAlignment(
				itemConfigJSONObject.getString("verticalAlignment"));
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

	private String _align = "";
	private JSONObject _collectionJSONObject;
	private boolean _displayAllItems;
	private boolean _displayAllPages = true;
	private EmptyCollectionOptions _emptyCollectionOptions;
	private String _flexWrap = "";
	private boolean _gutters = true;
	private String _justify = "";
	private String _listItemStyle;
	private String _listStyle;
	private int _numberOfColumns = 1;
	private int _numberOfItems = 5;
	private int _numberOfItemsPerPage = 20;
	private int _numberOfPages = 5;
	private String _paginationType =
		CollectionPaginationUtil.PAGINATION_TYPE_NUMERIC;
	private boolean _showAllItems;
	private String _templateKey;
	private String _verticalAlignment =
		StyledLayoutStructureConstants.VERTICAL_ALIGNMENT_START;
	private final Map<String, JSONObject> _viewportConfigurationJSONObjects =
		new HashMap<>();

}