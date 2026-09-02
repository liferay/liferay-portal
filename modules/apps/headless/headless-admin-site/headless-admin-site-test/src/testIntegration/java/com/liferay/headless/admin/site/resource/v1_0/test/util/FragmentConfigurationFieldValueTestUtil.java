/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.resource.v1_0.test.util;

import com.liferay.fragment.util.configuration.FragmentConfigurationField;
import com.liferay.fragment.util.configuration.FragmentEntryConfigurationParserUtil;
import com.liferay.headless.admin.site.client.dto.v1_0.CategoryFragmentConfigurationFieldValue;
import com.liferay.headless.admin.site.client.dto.v1_0.CheckboxFragmentConfigurationFieldValue;
import com.liferay.headless.admin.site.client.dto.v1_0.CollectionFragmentConfigurationFieldValue;
import com.liferay.headless.admin.site.client.dto.v1_0.ColorPaletteFragmentConfigurationFieldValue;
import com.liferay.headless.admin.site.client.dto.v1_0.ColorPaletteValue;
import com.liferay.headless.admin.site.client.dto.v1_0.ColorPickerFragmentConfigurationFieldValue;
import com.liferay.headless.admin.site.client.dto.v1_0.ContextualMenuNavigationMenuValue;
import com.liferay.headless.admin.site.client.dto.v1_0.FragmentConfigurationFieldValue;
import com.liferay.headless.admin.site.client.dto.v1_0.HrefURLValue;
import com.liferay.headless.admin.site.client.dto.v1_0.ItemExternalReference;
import com.liferay.headless.admin.site.client.dto.v1_0.ItemFragmentConfigurationFieldValue;
import com.liferay.headless.admin.site.client.dto.v1_0.ItemValue;
import com.liferay.headless.admin.site.client.dto.v1_0.LengthFragmentConfigurationFieldValue;
import com.liferay.headless.admin.site.client.dto.v1_0.NavigationMenuFragmentConfigurationFieldValue;
import com.liferay.headless.admin.site.client.dto.v1_0.NavigationMenuValue;
import com.liferay.headless.admin.site.client.dto.v1_0.SelectFragmentConfigurationFieldValue;
import com.liferay.headless.admin.site.client.dto.v1_0.SiteMenuNavigationMenuValue;
import com.liferay.headless.admin.site.client.dto.v1_0.SitePageURLValue;
import com.liferay.headless.admin.site.client.dto.v1_0.SitePagesNavigationMenuValue;
import com.liferay.headless.admin.site.client.dto.v1_0.TargetCollectionDisplayFragmentConfigurationFieldValue;
import com.liferay.headless.admin.site.client.dto.v1_0.TemplateReference;
import com.liferay.headless.admin.site.client.dto.v1_0.TextFragmentConfigurationFieldValue;
import com.liferay.headless.admin.site.client.dto.v1_0.URLFragmentConfigurationFieldValue;
import com.liferay.headless.admin.site.client.dto.v1_0.URLValue;
import com.liferay.headless.admin.site.client.dto.v1_0.VideoFragmentConfigurationFieldValue;
import com.liferay.headless.admin.site.client.dto.v1_0.VideoValue;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.MapUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author Lourdes Fernández Besada
 */
public class FragmentConfigurationFieldValueTestUtil {

	public static Map<String, FragmentConfigurationFieldValue>
		getFragmentConfigurationFieldValuesMap(
			JSONObject configurationJSONObject, Map<String, Object> objectsMap,
			long scopeGroupId) {

		Map<String, FragmentConfigurationFieldValue> map = new HashMap<>();

		if (configurationJSONObject == null) {
			return map;
		}

		for (FragmentConfigurationField fragmentConfigurationField :
				FragmentEntryConfigurationParserUtil.
					getFragmentConfigurationFields(configurationJSONObject)) {

			Object object = objectsMap.get(
				fragmentConfigurationField.getName());

			if (object == null) {
				continue;
			}

			map.put(
				fragmentConfigurationField.getName(),
				_getFragmentConfigurationFieldValue(
					fragmentConfigurationField, scopeGroupId, object));
		}

		return map;
	}

	private static FragmentConfigurationFieldValue
		_getCategoryFragmentConfigurationFieldValue(
			boolean localizable, Object object, long scopeGroupId) {

		CategoryFragmentConfigurationFieldValue
			categoryFragmentConfigurationFieldValue =
				new CategoryFragmentConfigurationFieldValue() {
					{
						setType(() -> Type.CATEGORY);
					}
				};

		if (localizable) {
			categoryFragmentConfigurationFieldValue.setValue_i18n(
				HashMapBuilder.put(
					LocaleUtil.toBCP47LanguageId(LocaleUtil.getDefault()),
					ReferencesTestUtil.getItemExternalReference(
						object, scopeGroupId)
				).build());
		}
		else {
			categoryFragmentConfigurationFieldValue.setValue(
				ReferencesTestUtil.getItemExternalReference(
					object, scopeGroupId));
		}

		return categoryFragmentConfigurationFieldValue;
	}

	private static FragmentConfigurationFieldValue
		_getCheckboxFragmentConfigurationFieldValue(
			boolean localizable, Object object) {

		CheckboxFragmentConfigurationFieldValue
			checkboxFragmentConfigurationFieldValue =
				new CheckboxFragmentConfigurationFieldValue() {
					{
						setType(() -> Type.CHECKBOX);
					}
				};

		if (localizable) {
			checkboxFragmentConfigurationFieldValue.setValue_i18n(
				HashMapBuilder.put(
					LocaleUtil.toBCP47LanguageId(LocaleUtil.getDefault()),
					GetterUtil.getBoolean(object)
				).build());
		}
		else {
			checkboxFragmentConfigurationFieldValue.setValue(
				GetterUtil.getBoolean(object));
		}

		return checkboxFragmentConfigurationFieldValue;
	}

	private static FragmentConfigurationFieldValue
		_getCollectionFragmentConfigurationFieldValue(
			boolean localizable, Object object, long scopeGroupId) {

		CollectionFragmentConfigurationFieldValue
			collectionFragmentConfigurationFieldValue =
				new CollectionFragmentConfigurationFieldValue() {
					{
						setType(() -> Type.COLLECTION);
					}
				};

		if (localizable) {
			collectionFragmentConfigurationFieldValue.setValue_i18n(
				HashMapBuilder.put(
					LocaleUtil.toBCP47LanguageId(LocaleUtil.getDefault()),
					ReferencesTestUtil.getCollectionReference(
						object, scopeGroupId)
				).build());
		}
		else {
			collectionFragmentConfigurationFieldValue.setValue(
				ReferencesTestUtil.getCollectionReference(
					object, scopeGroupId));
		}

		return collectionFragmentConfigurationFieldValue;
	}

	private static FragmentConfigurationFieldValue
		_getColorPaletteConfigurationFieldValue(
			boolean localizable, Object object) {

		ColorPaletteFragmentConfigurationFieldValue
			colorPaletteFragmentConfigurationFieldValue =
				new ColorPaletteFragmentConfigurationFieldValue() {
					{
						setType(() -> Type.COLOR_PALETTE);
					}
				};

		if (localizable) {
			colorPaletteFragmentConfigurationFieldValue.setValue_i18n(
				HashMapBuilder.put(
					LocaleUtil.toBCP47LanguageId(LocaleUtil.getDefault()),
					_getColorPaletteValue((Map<String, String>)object)
				).build());
		}
		else {
			colorPaletteFragmentConfigurationFieldValue.setValue(
				_getColorPaletteValue((Map<String, String>)object));
		}

		return colorPaletteFragmentConfigurationFieldValue;
	}

	private static ColorPaletteValue _getColorPaletteValue(
		Map<String, String> map) {

		ColorPaletteValue colorPaletteValue = new ColorPaletteValue();

		colorPaletteValue.setColor(map.get("color"));
		colorPaletteValue.setCssClass(map.get("cssClass"));
		colorPaletteValue.setRgbValue(map.get("rgbValue"));

		return colorPaletteValue;
	}

	private static FragmentConfigurationFieldValue
		_getColorPickerFragmentConfigurationFieldValue(
			boolean localizable, Object object) {

		ColorPickerFragmentConfigurationFieldValue
			colorPickerFragmentConfigurationFieldValue =
				new ColorPickerFragmentConfigurationFieldValue() {
					{
						setType(() -> Type.COLOR_PICKER);
					}
				};

		if (localizable) {
			colorPickerFragmentConfigurationFieldValue.setValue_i18n(
				HashMapBuilder.put(
					LocaleUtil.toBCP47LanguageId(LocaleUtil.getDefault()),
					GetterUtil.getString(object)
				).build());
		}
		else {
			colorPickerFragmentConfigurationFieldValue.setValue(
				GetterUtil.getString(object));
		}

		return colorPickerFragmentConfigurationFieldValue;
	}

	private static FragmentConfigurationFieldValue
		_getFragmentConfigurationFieldValue(
			FragmentConfigurationField fragmentConfigurationField,
			long scopeGroupId, Object value) {

		String type = fragmentConfigurationField.getType();

		if (Objects.equals(type, "categoryTreeNodeSelector")) {
			return _getCategoryFragmentConfigurationFieldValue(
				fragmentConfigurationField.isLocalizable(), value,
				scopeGroupId);
		}

		if (Objects.equals(type, "checkbox")) {
			return _getCheckboxFragmentConfigurationFieldValue(
				fragmentConfigurationField.isLocalizable(), value);
		}

		if (Objects.equals(type, "collectionSelector")) {
			return _getCollectionFragmentConfigurationFieldValue(
				fragmentConfigurationField.isLocalizable(), value,
				scopeGroupId);
		}

		if (Objects.equals(type, "colorPalette")) {
			return _getColorPaletteConfigurationFieldValue(
				fragmentConfigurationField.isLocalizable(), value);
		}

		if (Objects.equals(type, "colorPicker")) {
			return _getColorPickerFragmentConfigurationFieldValue(
				fragmentConfigurationField.isLocalizable(), value);
		}

		if (Objects.equals(type, "itemSelector")) {
			return _getItemFragmentConfigurationFieldValue(
				fragmentConfigurationField.isLocalizable(), value,
				scopeGroupId);
		}

		if (Objects.equals(type, "length")) {
			return _getLengthFragmentConfigurationFieldValue(
				fragmentConfigurationField.isLocalizable(), value);
		}

		if (Objects.equals(type, "navigationMenuSelector")) {
			return _getNavigationMenuConfigurationFieldValue(
				fragmentConfigurationField.isLocalizable(), value,
				scopeGroupId);
		}

		if (Objects.equals(type, "select")) {
			return _getSelectFragmentConfigurationFieldValue(
				fragmentConfigurationField.isLocalizable(), value);
		}

		if (Objects.equals(type, "targetCollectionDisplay")) {
			return _getTargetCollectionDisplayFragmentConfigurationFieldValue(
				value);
		}

		if (Objects.equals(type, "text")) {
			return _getTextFragmentConfigurationFieldValue(
				fragmentConfigurationField.isLocalizable(), value);
		}

		if (Objects.equals(type, "url")) {
			return _getURLFragmentConfigurationFieldValue(
				fragmentConfigurationField.isLocalizable(), value,
				scopeGroupId);
		}

		if (Objects.equals(type, "videoSelector")) {
			return _getVideoFragmentConfigurationFieldValue(
				fragmentConfigurationField.isLocalizable(), value);
		}

		return null;
	}

	private static FragmentConfigurationFieldValue
		_getItemFragmentConfigurationFieldValue(
			boolean localizable, Object object, long scopeGroupId) {

		ItemFragmentConfigurationFieldValue
			itemFragmentConfigurationFieldValue =
				new ItemFragmentConfigurationFieldValue() {
					{
						setType(() -> Type.ITEM);
					}
				};

		if (localizable) {
			itemFragmentConfigurationFieldValue.setValue_i18n(
				HashMapBuilder.put(
					LocaleUtil.toBCP47LanguageId(LocaleUtil.getDefault()),
					_getItemValue((Map<String, Object>)object, scopeGroupId)
				).build());
		}
		else {
			itemFragmentConfigurationFieldValue.setValue(
				_getItemValue((Map<String, Object>)object, scopeGroupId));
		}

		return itemFragmentConfigurationFieldValue;
	}

	private static ItemValue _getItemValue(
		Map<String, Object> map, long scopeGroupId) {

		ItemValue itemValue = new ItemValue();

		itemValue.setItemExternalReference(
			() -> ReferencesTestUtil.getItemExternalReference(
				map.get("item"), scopeGroupId));
		itemValue.setTemplateReference(
			() -> {
				if (!(map.get("template") instanceof Map<?, ?> template)) {
					return null;
				}

				Map<String, String> templateMap = (Map<String, String>)template;

				return new TemplateReference() {
					{
						setRendererKey(
							() -> templateMap.get("infoItemRendererKey"));
						setTemplateKey(() -> templateMap.get("templateKey"));
					}
				};
			});

		return itemValue;
	}

	private static FragmentConfigurationFieldValue
		_getLengthFragmentConfigurationFieldValue(
			boolean localizable, Object object) {

		LengthFragmentConfigurationFieldValue
			lengthFragmentConfigurationFieldValue =
				new LengthFragmentConfigurationFieldValue() {
					{
						setType(() -> Type.LENGTH);
					}
				};

		if (localizable) {
			lengthFragmentConfigurationFieldValue.setValue_i18n(
				HashMapBuilder.put(
					LocaleUtil.toBCP47LanguageId(LocaleUtil.getDefault()),
					GetterUtil.getString(object)
				).build());
		}
		else {
			lengthFragmentConfigurationFieldValue.setValue(
				GetterUtil.getString(object));
		}

		return lengthFragmentConfigurationFieldValue;
	}

	private static FragmentConfigurationFieldValue
		_getNavigationMenuConfigurationFieldValue(
			boolean localizable, Object object, long scopeGroupId) {

		NavigationMenuFragmentConfigurationFieldValue
			navigationMenuFragmentConfigurationFieldValue =
				new NavigationMenuFragmentConfigurationFieldValue() {
					{
						setType(() -> Type.NAVIGATION_MENU);
					}
				};

		if (localizable) {
			navigationMenuFragmentConfigurationFieldValue.setValue_i18n(
				HashMapBuilder.put(
					LocaleUtil.toBCP47LanguageId(LocaleUtil.getDefault()),
					_getNavigationMenuValue(
						(Map<String, Object>)object, scopeGroupId)
				).build());
		}
		else {
			navigationMenuFragmentConfigurationFieldValue.setValue(
				_getNavigationMenuValue(
					(Map<String, Object>)object, scopeGroupId));
		}

		return navigationMenuFragmentConfigurationFieldValue;
	}

	private static NavigationMenuValue _getNavigationMenuValue(
		Map<String, Object> map, long scopeGroupId) {

		if (MapUtil.isEmpty(map)) {
			return null;
		}

		if (map.get("contextualMenu") instanceof
				ContextualMenuNavigationMenuValue.ContextualMenuType
					contextualMenuType) {

			ContextualMenuNavigationMenuValue
				contextualMenuNavigationMenuValue =
					new ContextualMenuNavigationMenuValue() {
						{
							setNavigationMenuType(
								() -> NavigationMenuType.CONTEXTUAL_MENU);
						}
					};

			contextualMenuNavigationMenuValue.setContextualMenuType(
				() -> contextualMenuType);

			return contextualMenuNavigationMenuValue;
		}

		Object siteNavigationMenu = map.get("siteNavigationMenu");

		if (siteNavigationMenu != null) {
			SiteMenuNavigationMenuValue siteMenuNavigationMenuValue =
				new SiteMenuNavigationMenuValue() {
					{
						setNavigationMenuType(
							() -> NavigationMenuType.SITE_MENU);
					}
				};

			siteMenuNavigationMenuValue.setNavigationMenuItemExternalReference(
				() -> ReferencesTestUtil.getItemExternalReference(
					siteNavigationMenu, scopeGroupId));

			siteMenuNavigationMenuValue.setParentMenuItemExternalReferenceCode(
				() -> GetterUtil.getString(
					map.get("siteNavigationMenuItemExternalReferenceCode"),
					null));

			return siteMenuNavigationMenuValue;
		}

		SitePagesNavigationMenuValue sitePagesNavigationMenuValue =
			new SitePagesNavigationMenuValue() {
				{
					setNavigationMenuType(() -> NavigationMenuType.SITE_PAGES);
				}
			};

		sitePagesNavigationMenuValue.setPageSetType(
			() -> {
				if (GetterUtil.getBoolean(map.get("privateLayout"))) {
					return SitePagesNavigationMenuValue.PageSetType.
						PRIVATE_PAGES;
				}

				return SitePagesNavigationMenuValue.PageSetType.PUBLIC_PAGES;
			});

		sitePagesNavigationMenuValue.setParentSitePageExternalReferenceCode(
			() -> GetterUtil.getString(
				map.get("parentLayoutExternalReferenceCode"), null));

		return sitePagesNavigationMenuValue;
	}

	private static FragmentConfigurationFieldValue
		_getSelectFragmentConfigurationFieldValue(
			boolean localizable, Object object) {

		SelectFragmentConfigurationFieldValue
			selectFragmentConfigurationFieldValue =
				new SelectFragmentConfigurationFieldValue() {
					{
						setType(() -> Type.SELECT);
					}
				};

		if (localizable) {
			selectFragmentConfigurationFieldValue.setValue_i18n(
				HashMapBuilder.put(
					LocaleUtil.toBCP47LanguageId(LocaleUtil.getDefault()),
					GetterUtil.getString(object)
				).build());
		}
		else {
			selectFragmentConfigurationFieldValue.setValue(
				GetterUtil.getString(object));
		}

		return selectFragmentConfigurationFieldValue;
	}

	private static FragmentConfigurationFieldValue
		_getTargetCollectionDisplayFragmentConfigurationFieldValue(
			Object object) {

		return new TargetCollectionDisplayFragmentConfigurationFieldValue() {
			{
				setType(() -> Type.TARGET_COLLECTION_DISPLAY);
				setValue(() -> (String[])object);
			}
		};
	}

	private static FragmentConfigurationFieldValue
		_getTextFragmentConfigurationFieldValue(
			boolean localizable, Object object) {

		TextFragmentConfigurationFieldValue
			textFragmentConfigurationFieldValue =
				new TextFragmentConfigurationFieldValue() {
					{
						setType(() -> Type.TEXT);
					}
				};

		if (localizable) {
			textFragmentConfigurationFieldValue.setValue_i18n(
				HashMapBuilder.put(
					LocaleUtil.toBCP47LanguageId(LocaleUtil.getDefault()),
					GetterUtil.getString(object)
				).build());
		}
		else {
			textFragmentConfigurationFieldValue.setValue(
				GetterUtil.getString(object));
		}

		return textFragmentConfigurationFieldValue;
	}

	private static FragmentConfigurationFieldValue
		_getURLFragmentConfigurationFieldValue(
			boolean localizable, Object object, long scopeGroupId) {

		URLFragmentConfigurationFieldValue urlFragmentConfigurationFieldValue =
			new URLFragmentConfigurationFieldValue() {
				{
					setType(() -> Type.URL);
				}
			};

		if (localizable) {
			urlFragmentConfigurationFieldValue.setValue_i18n(
				HashMapBuilder.put(
					LocaleUtil.toBCP47LanguageId(LocaleUtil.getDefault()),
					_getURLValue((Map<String, Object>)object, scopeGroupId)
				).build());
		}
		else {
			urlFragmentConfigurationFieldValue.setValue(
				_getURLValue((Map<String, Object>)object, scopeGroupId));
		}

		return urlFragmentConfigurationFieldValue;
	}

	private static URLValue _getURLValue(
		Map<String, Object> map, long scopeGroupId) {

		String href = GetterUtil.getString(map.get("href"), null);

		if (href != null) {
			HrefURLValue hrefURLValue = new HrefURLValue();

			hrefURLValue.setHref(href);
			hrefURLValue.setUrlType(URLValue.UrlType.HREF);

			return hrefURLValue;
		}

		ItemExternalReference itemExternalReference =
			ReferencesTestUtil.getItemExternalReference(
				map.get("layout"), scopeGroupId);

		if (itemExternalReference == null) {
			return null;
		}

		return new SitePageURLValue() {
			{
				setSitePageItemExternalReference(() -> itemExternalReference);
				setUrlType(() -> UrlType.SITE_PAGE);
			}
		};
	}

	private static FragmentConfigurationFieldValue
		_getVideoFragmentConfigurationFieldValue(
			boolean localizable, Object object) {

		VideoFragmentConfigurationFieldValue
			videoFragmentConfigurationFieldValue =
				new VideoFragmentConfigurationFieldValue() {
					{
						setType(() -> Type.VIDEO);
					}
				};

		if (localizable) {
			videoFragmentConfigurationFieldValue.setValue_i18n(
				HashMapBuilder.put(
					LocaleUtil.toBCP47LanguageId(LocaleUtil.getDefault()),
					_getVideoValue((Map<String, String>)object)
				).build());
		}
		else {
			videoFragmentConfigurationFieldValue.setValue(
				_getVideoValue((Map<String, String>)object));
		}

		return videoFragmentConfigurationFieldValue;
	}

	private static VideoValue _getVideoValue(Map<String, String> map) {
		VideoValue videoValue = new VideoValue();

		videoValue.setHtml(map.get("html"));
		videoValue.setTitle(map.get("title"));

		return videoValue;
	}

}