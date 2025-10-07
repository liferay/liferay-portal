/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.util.structure;

import com.liferay.document.library.kernel.service.DLAppLocalServiceUtil;
import com.liferay.document.library.util.DLURLHelperUtil;
import com.liferay.layout.responsive.ViewportSize;
import com.liferay.petra.lang.HashUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.StringUtil;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * @author Pavel Savinov
 */
public abstract class StyledLayoutStructureItem extends LayoutStructureItem {

	public StyledLayoutStructureItem(String parentItemId) {
		super(parentItemId);
	}

	public StyledLayoutStructureItem(String itemId, String parentItemId) {
		super(itemId, parentItemId);
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof StyledLayoutStructureItem)) {
			return false;
		}

		StyledLayoutStructureItem styledLayoutStructureItem =
			(StyledLayoutStructureItem)object;

		JSONObject stylesJSONObject =
			styledLayoutStructureItem.stylesJSONObject;

		for (String key : this.stylesJSONObject.keySet()) {
			if (!Objects.deepEquals(
					GetterUtil.getString(this.stylesJSONObject.get(key)),
					GetterUtil.getString(stylesJSONObject.get(key)))) {

				return false;
			}
		}

		return super.equals(object);
	}

	public JSONObject getBackgroundImageJSONObject() {
		JSONObject jsonObject = stylesJSONObject.getJSONObject(
			"backgroundImage");

		if (jsonObject == null) {
			return JSONFactoryUtil.createJSONObject();
		}

		return jsonObject;
	}

	public String getCssClass() {
		return LAYOUT_STRUCTURE_ITEM_CSS_CLASS_PREFIX + getItemType();
	}

	public Set<String> getCssClasses() {
		return _cssClasses;
	}

	public String getCustomCSS() {
		return _customCSS;
	}

	public Map<String, String> getCustomCSSViewports() {
		return _customCSSViewports;
	}

	@Override
	public JSONObject getItemConfigJSONObject() {
		JSONObject jsonObject = JSONUtil.put(
			"cssClasses", JSONFactoryUtil.createJSONArray(_cssClasses)
		).put(
			"customCSS", _customCSS
		).put(
			"name", _name
		).put(
			"styles", stylesJSONObject
		);

		for (ViewportSize viewportSize : _viewportSizes) {
			if (viewportSize.equals(ViewportSize.DESKTOP)) {
				continue;
			}

			jsonObject.put(
				viewportSize.getViewportSizeId(),
				JSONUtil.put(
					"customCSS",
					_customCSSViewports.get(viewportSize.getViewportSizeId())
				).put(
					"styles",
					viewportStyleJSONObjects.getOrDefault(
						viewportSize.getViewportSizeId(),
						JSONFactoryUtil.createJSONObject())
				));
		}

		return jsonObject;
	}

	public String getName() {
		return _name;
	}

	public String getStyledCssClasses() {
		return StringUtil.merge(getCssClasses(), StringPool.SPACE);
	}

	public JSONObject getStylesJSONObject() {
		return stylesJSONObject;
	}

	public String getUniqueCssClass() {
		return LAYOUT_STRUCTURE_ITEM_CSS_CLASS_PREFIX + getItemId();
	}

	@Override
	public int hashCode() {
		return HashUtil.hash(0, getItemId());
	}

	public void setCssClasses(Set<String> cssClasses) {
		_cssClasses = cssClasses;
	}

	public void setCustomCSS(String customCSS) {
		_customCSS = customCSS;
	}

	public void setCustomCSSViewport(String viewportSizeId, String customCSS) {
		_customCSSViewports.put(viewportSizeId, customCSS);
	}

	public void setName(String name) {
		_name = name;
	}

	@Override
	public void updateItemConfig(JSONObject itemConfigJSONObject) {
		if (itemConfigJSONObject.has("cssClasses")) {
			LinkedHashSet<String> cssClasses = new LinkedHashSet<>();

			JSONUtil.addToStringCollection(
				cssClasses, itemConfigJSONObject.getJSONArray("cssClasses"));

			setCssClasses(cssClasses);
		}

		if (itemConfigJSONObject.has("customCSS")) {
			setCustomCSS(itemConfigJSONObject.getString("customCSS"));
		}

		if (itemConfigJSONObject.has("name")) {
			setName(itemConfigJSONObject.getString("name"));
		}

		try {
			_updateItemConfigValues(stylesJSONObject, itemConfigJSONObject);

			if (itemConfigJSONObject.has("styles")) {
				JSONObject newStylesJSONObject =
					itemConfigJSONObject.getJSONObject("styles");

				_updateItemConfigValues(stylesJSONObject, newStylesJSONObject);
			}

			for (ViewportSize viewportSize : _viewportSizes) {
				if (viewportSize.equals(ViewportSize.DESKTOP)) {
					continue;
				}

				_updateCustomCSSViewports(itemConfigJSONObject, viewportSize);

				_updateViewportStyleJSONObjects(
					itemConfigJSONObject, viewportSize);
			}
		}
		catch (Exception exception) {
			_log.error("Unable to get available style names", exception);
		}
	}

	protected static final String LAYOUT_STRUCTURE_ITEM_CSS_CLASS_PREFIX =
		"lfr-layout-structure-item-";

	protected JSONObject stylesJSONObject = JSONFactoryUtil.createJSONObject();
	protected Map<String, JSONObject> viewportStyleJSONObjects =
		new HashMap<>();

	private JSONObject _getBackgroundImageStyleValueJSONObject(
		Object styleValue) {

		if (styleValue == null) {
			return null;
		}

		JSONObject styleValueJSONObject = (JSONObject)styleValue;

		long fileEntryId = styleValueJSONObject.getLong("fileEntryId");

		if (fileEntryId <= 0) {
			return styleValueJSONObject;
		}

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		if (serviceContext == null) {
			return styleValueJSONObject;
		}

		ThemeDisplay themeDisplay = serviceContext.getThemeDisplay();

		if (themeDisplay == null) {
			return styleValueJSONObject;
		}

		try {
			FileEntry fileEntry = DLAppLocalServiceUtil.getFileEntry(
				fileEntryId);

			styleValueJSONObject.put(
				"url",
				DLURLHelperUtil.getPreviewURL(
					fileEntry, fileEntry.getFileVersion(), themeDisplay,
					StringPool.BLANK, false, false));
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(
					"Unable to get file entry  " + fileEntryId, exception);
			}
		}

		return styleValueJSONObject;
	}

	private void _updateCustomCSSViewports(
		JSONObject itemConfigJSONObject, ViewportSize viewportSize) {

		JSONObject viewportItemConfigJSONObject =
			itemConfigJSONObject.getJSONObject(
				viewportSize.getViewportSizeId());

		if ((viewportItemConfigJSONObject != null) &&
			viewportItemConfigJSONObject.has("customCSS")) {

			_customCSSViewports.put(
				viewportSize.getViewportSizeId(),
				viewportItemConfigJSONObject.getString("customCSS"));
		}
	}

	private void _updateItemConfigValues(
			JSONObject currentJSONObject, JSONObject newJSONObject)
		throws Exception {

		List<String> availableStyleNames =
			CommonStylesUtil.getAvailableStyleNames();

		for (String styleName : availableStyleNames) {
			if (newJSONObject.has(styleName)) {
				Object styleValue = newJSONObject.get(styleName);

				if (Objects.equals(
						styleValue,
						CommonStylesUtil.getDefaultStyleValue(styleName))) {

					currentJSONObject.remove(styleName);
				}
				else {
					if (Objects.equals(styleName, "backgroundImage")) {
						styleValue = _getBackgroundImageStyleValueJSONObject(
							styleValue);
					}

					currentJSONObject.put(styleName, styleValue);
				}
			}
		}
	}

	private void _updateViewportStyleJSONObjects(
		JSONObject itemConfigJSONObject, ViewportSize viewportSize) {

		List<String> availableStyleNames =
			CommonStylesUtil.getAvailableStyleNames();

		JSONObject viewportItemConfigJSONObject =
			itemConfigJSONObject.getJSONObject(
				viewportSize.getViewportSizeId());

		if (ListUtil.isEmpty(availableStyleNames) ||
			(viewportItemConfigJSONObject == null)) {

			return;
		}

		JSONObject newStylesJSONObject =
			viewportItemConfigJSONObject.getJSONObject("styles");

		if ((newStylesJSONObject == null) ||
			(newStylesJSONObject.length() == 0)) {

			return;
		}

		JSONObject currentViewportStyleJSONObject =
			viewportStyleJSONObjects.getOrDefault(
				viewportSize.getViewportSizeId(),
				JSONFactoryUtil.createJSONObject());

		for (String styleName : availableStyleNames) {
			if (newStylesJSONObject.has(styleName)) {
				currentViewportStyleJSONObject.put(
					styleName, newStylesJSONObject.get(styleName));
			}
		}

		viewportStyleJSONObjects.put(
			viewportSize.getViewportSizeId(), currentViewportStyleJSONObject);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		StyledLayoutStructureItem.class);

	private static final ViewportSize[] _viewportSizes = ViewportSize.values();

	private Set<String> _cssClasses;
	private String _customCSS;
	private final Map<String, String> _customCSSViewports = new HashMap<>();
	private String _name;

}