/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.internal.resource.v1_0.layout.structure.item.importer.util;

import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.model.AssetVocabulary;
import com.liferay.asset.kernel.service.AssetCategoryLocalServiceUtil;
import com.liferay.asset.kernel.service.AssetVocabularyLocalServiceUtil;
import com.liferay.fragment.util.configuration.FragmentConfigurationField;
import com.liferay.fragment.util.configuration.FragmentEntryConfigurationParserUtil;
import com.liferay.headless.admin.site.dto.v1_0.CategoryFragmentConfigurationFieldValue;
import com.liferay.headless.admin.site.dto.v1_0.CheckboxFragmentConfigurationFieldValue;
import com.liferay.headless.admin.site.dto.v1_0.CollectionFragmentConfigurationFieldValue;
import com.liferay.headless.admin.site.dto.v1_0.ColorPaletteFragmentConfigurationFieldValue;
import com.liferay.headless.admin.site.dto.v1_0.ColorPaletteValue;
import com.liferay.headless.admin.site.dto.v1_0.ColorPickerFragmentConfigurationFieldValue;
import com.liferay.headless.admin.site.dto.v1_0.ContextualMenuNavigationMenuValue;
import com.liferay.headless.admin.site.dto.v1_0.FragmentConfigurationFieldValue;
import com.liferay.headless.admin.site.dto.v1_0.HrefURLValue;
import com.liferay.headless.admin.site.dto.v1_0.ItemExternalReference;
import com.liferay.headless.admin.site.dto.v1_0.ItemFragmentConfigurationFieldValue;
import com.liferay.headless.admin.site.dto.v1_0.ItemValue;
import com.liferay.headless.admin.site.dto.v1_0.LengthFragmentConfigurationFieldValue;
import com.liferay.headless.admin.site.dto.v1_0.NavigationMenuFragmentConfigurationFieldValue;
import com.liferay.headless.admin.site.dto.v1_0.NavigationMenuValue;
import com.liferay.headless.admin.site.dto.v1_0.SelectFragmentConfigurationFieldValue;
import com.liferay.headless.admin.site.dto.v1_0.SiteMenuNavigationMenuValue;
import com.liferay.headless.admin.site.dto.v1_0.SitePageURLValue;
import com.liferay.headless.admin.site.dto.v1_0.SitePagesNavigationMenuValue;
import com.liferay.headless.admin.site.dto.v1_0.TemplateReference;
import com.liferay.headless.admin.site.dto.v1_0.TextFragmentConfigurationFieldValue;
import com.liferay.headless.admin.site.dto.v1_0.URLFragmentConfigurationFieldValue;
import com.liferay.headless.admin.site.dto.v1_0.URLValue;
import com.liferay.headless.admin.site.dto.v1_0.VideoFragmentConfigurationFieldValue;
import com.liferay.headless.admin.site.dto.v1_0.VideoValue;
import com.liferay.headless.admin.site.internal.dto.v1_0.util.CollectionUtil;
import com.liferay.headless.admin.site.internal.dto.v1_0.util.ContextualMenuTypeUtil;
import com.liferay.headless.admin.site.internal.dto.v1_0.util.FragmentConfigurationFieldValueTypeUtil;
import com.liferay.headless.admin.site.internal.dto.v1_0.util.InfoItemUtil;
import com.liferay.headless.admin.site.internal.dto.v1_0.util.ItemScopeUtil;
import com.liferay.headless.admin.site.internal.dto.v1_0.util.LayoutUtil;
import com.liferay.headless.admin.site.internal.dto.v1_0.util.LocalizedValueUtil;
import com.liferay.headless.admin.site.internal.resource.v1_0.layout.structure.item.importer.context.LayoutStructureItemImporterContext;
import com.liferay.headless.admin.site.internal.util.LogUtil;
import com.liferay.item.selector.criteria.VideoEmbeddableHTMLItemSelectorReturnType;
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.service.LayoutLocalServiceUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.site.navigation.model.SiteNavigationMenu;
import com.liferay.site.navigation.model.SiteNavigationMenuItem;
import com.liferay.site.navigation.service.SiteNavigationMenuItemLocalServiceUtil;
import com.liferay.site.navigation.service.SiteNavigationMenuLocalServiceUtil;

import java.util.Map;
import java.util.Objects;

/**
 * @author Lourdes Fernández Besada
 */
public class FragmentConfigurationFieldValuesUtil {

	public static JSONObject getFreeMarkerFragmentEntryProcessorJSONObject(
			String configuration,
			Map<String, FragmentConfigurationFieldValue>
				fragmentConfigurationFieldValuesMap,
			LayoutStructureItemImporterContext
				layoutStructureItemImporterContext)
		throws Exception {

		JSONObject jsonObject = JSONFactoryUtil.createJSONObject();

		if (fragmentConfigurationFieldValuesMap == null) {
			return jsonObject;
		}

		JSONObject configurationJSONObject = JSONFactoryUtil.createJSONObject(
			configuration);

		for (FragmentConfigurationField fragmentConfigurationField :
				FragmentEntryConfigurationParserUtil.
					getFragmentConfigurationFields(configurationJSONObject)) {

			FragmentConfigurationFieldValue fragmentConfigurationFieldValue =
				fragmentConfigurationFieldValuesMap.get(
					fragmentConfigurationField.getName());

			if (fragmentConfigurationFieldValue == null) {
				continue;
			}

			if (!Objects.equals(
					fragmentConfigurationFieldValue.getType(),
					FragmentConfigurationFieldValueTypeUtil.toExternalType(
						fragmentConfigurationField.getType()))) {

				throw new UnsupportedOperationException();
			}

			jsonObject.put(
				fragmentConfigurationField.getName(),
				_fromFragmentConfigurationFieldValue(
					fragmentConfigurationFieldValue, fragmentConfigurationField,
					layoutStructureItemImporterContext));
		}

		return jsonObject;
	}

	private static Object _fromFragmentConfigurationFieldValue(
			FragmentConfigurationFieldValue fragmentConfigurationFieldValue,
			FragmentConfigurationField fragmentConfigurationField,
			LayoutStructureItemImporterContext
				layoutStructureItemImporterContext)
		throws Exception {

		if (Objects.equals(
				fragmentConfigurationFieldValue.getType(),
				FragmentConfigurationFieldValue.Type.CATEGORY)) {

			CategoryFragmentConfigurationFieldValue
				categoryFragmentConfigurationFieldValue =
					(CategoryFragmentConfigurationFieldValue)
						fragmentConfigurationFieldValue;

			return _getConfigurationJSONObject(
				fragmentConfigurationField.isLocalizable(),
				itemExternalReference -> _getCategoryTreeNodeJSONObject(
					itemExternalReference, layoutStructureItemImporterContext),
				categoryFragmentConfigurationFieldValue.getValue(),
				categoryFragmentConfigurationFieldValue.getValue_i18n());
		}

		if (Objects.equals(
				fragmentConfigurationFieldValue.getType(),
				FragmentConfigurationFieldValue.Type.CHECKBOX)) {

			CheckboxFragmentConfigurationFieldValue
				checkboxFragmentConfigurationFieldValue =
					(CheckboxFragmentConfigurationFieldValue)
						fragmentConfigurationFieldValue;

			return _getConfigurationObject(
				fragmentConfigurationField.isLocalizable(),
				checkboxFragmentConfigurationFieldValue.getValue(),
				checkboxFragmentConfigurationFieldValue.getValue_i18n());
		}

		if (Objects.equals(
				fragmentConfigurationFieldValue.getType(),
				FragmentConfigurationFieldValue.Type.COLLECTION)) {

			CollectionFragmentConfigurationFieldValue
				collectionFragmentConfigurationFieldValue =
					(CollectionFragmentConfigurationFieldValue)
						fragmentConfigurationFieldValue;

			return _getConfigurationJSONObject(
				fragmentConfigurationField.isLocalizable(),
				collectionReference -> CollectionUtil.getCollectionJSONObject(
					collectionReference,
					layoutStructureItemImporterContext.getCompanyId(),
					layoutStructureItemImporterContext.
						getInfoItemServiceRegistry(),
					layoutStructureItemImporterContext.getGroupId()),
				collectionFragmentConfigurationFieldValue.getValue(),
				collectionFragmentConfigurationFieldValue.getValue_i18n());
		}

		if (Objects.equals(
				fragmentConfigurationFieldValue.getType(),
				FragmentConfigurationFieldValue.Type.COLOR_PALETTE)) {

			ColorPaletteFragmentConfigurationFieldValue
				colorPaletteFragmentConfigurationFieldValue =
					(ColorPaletteFragmentConfigurationFieldValue)
						fragmentConfigurationFieldValue;

			return _getConfigurationJSONObject(
				fragmentConfigurationField.isLocalizable(),
				colorPaletteValue -> _getColorPaletteJSONObject(
					colorPaletteValue),
				colorPaletteFragmentConfigurationFieldValue.getValue(),
				colorPaletteFragmentConfigurationFieldValue.getValue_i18n());
		}

		if (Objects.equals(
				fragmentConfigurationFieldValue.getType(),
				FragmentConfigurationFieldValue.Type.COLOR_PICKER)) {

			ColorPickerFragmentConfigurationFieldValue
				colorPickerFragmentConfigurationFieldValue =
					(ColorPickerFragmentConfigurationFieldValue)
						fragmentConfigurationFieldValue;

			return _getConfigurationObject(
				fragmentConfigurationField.isLocalizable(),
				colorPickerFragmentConfigurationFieldValue.getValue(),
				colorPickerFragmentConfigurationFieldValue.getValue_i18n());
		}

		if (Objects.equals(
				fragmentConfigurationFieldValue.getType(),
				FragmentConfigurationFieldValue.Type.ITEM)) {

			ItemFragmentConfigurationFieldValue
				itemFragmentConfigurationFieldValue =
					(ItemFragmentConfigurationFieldValue)
						fragmentConfigurationFieldValue;

			return _getConfigurationJSONObject(
				fragmentConfigurationField.isLocalizable(),
				itemValue -> _getItemJSONObject(
					itemValue, layoutStructureItemImporterContext),
				itemFragmentConfigurationFieldValue.getValue(),
				itemFragmentConfigurationFieldValue.getValue_i18n());
		}

		if (Objects.equals(
				fragmentConfigurationFieldValue.getType(),
				FragmentConfigurationFieldValue.Type.LENGTH)) {

			LengthFragmentConfigurationFieldValue
				lengthFragmentConfigurationFieldValue =
					(LengthFragmentConfigurationFieldValue)
						fragmentConfigurationFieldValue;

			return _getConfigurationObject(
				fragmentConfigurationField.isLocalizable(),
				lengthFragmentConfigurationFieldValue.getValue(),
				lengthFragmentConfigurationFieldValue.getValue_i18n());
		}

		if (Objects.equals(
				fragmentConfigurationFieldValue.getType(),
				FragmentConfigurationFieldValue.Type.NAVIGATION_MENU)) {

			NavigationMenuFragmentConfigurationFieldValue
				navigationMenuFragmentConfigurationFieldValue =
					(NavigationMenuFragmentConfigurationFieldValue)
						fragmentConfigurationFieldValue;

			return _getConfigurationJSONObject(
				fragmentConfigurationField.isLocalizable(),
				navigationMenuValue -> _getNavigationMenuJSONObject(
					layoutStructureItemImporterContext, navigationMenuValue),
				navigationMenuFragmentConfigurationFieldValue.getValue(),
				navigationMenuFragmentConfigurationFieldValue.getValue_i18n());
		}

		if (Objects.equals(
				fragmentConfigurationFieldValue.getType(),
				FragmentConfigurationFieldValue.Type.SELECT)) {

			SelectFragmentConfigurationFieldValue
				selectFragmentConfigurationFieldValue =
					(SelectFragmentConfigurationFieldValue)
						fragmentConfigurationFieldValue;

			JSONObject typeOptionsJSONObject =
				fragmentConfigurationField.getTypeOptionsJSONObject();

			JSONArray validValuesJSONArray = typeOptionsJSONObject.getJSONArray(
				"validValues");

			return _getConfigurationObject(
				fragmentConfigurationField.isLocalizable(),
				value -> {
					if (_isValidValue(validValuesJSONArray, value)) {
						return value;
					}

					throw new UnsupportedOperationException();
				},
				selectFragmentConfigurationFieldValue.getValue(),
				selectFragmentConfigurationFieldValue.getValue_i18n());
		}

		if (Objects.equals(
				fragmentConfigurationFieldValue.getType(),
				FragmentConfigurationFieldValue.Type.TEXT)) {

			TextFragmentConfigurationFieldValue
				textFragmentConfigurationFieldValue =
					(TextFragmentConfigurationFieldValue)
						fragmentConfigurationFieldValue;

			return _getConfigurationObject(
				fragmentConfigurationField.isLocalizable(),
				textFragmentConfigurationFieldValue.getValue(),
				textFragmentConfigurationFieldValue.getValue_i18n());
		}

		if (Objects.equals(
				fragmentConfigurationFieldValue.getType(),
				FragmentConfigurationFieldValue.Type.URL)) {

			URLFragmentConfigurationFieldValue
				urlFragmentConfigurationFieldValue =
					(URLFragmentConfigurationFieldValue)
						fragmentConfigurationFieldValue;

			return _getConfigurationJSONObject(
				fragmentConfigurationField.isLocalizable(),
				urlValue -> _getURLJSONObject(
					layoutStructureItemImporterContext, urlValue),
				urlFragmentConfigurationFieldValue.getValue(),
				urlFragmentConfigurationFieldValue.getValue_i18n());
		}

		if (Objects.equals(
				fragmentConfigurationFieldValue.getType(),
				FragmentConfigurationFieldValue.Type.VIDEO)) {

			VideoFragmentConfigurationFieldValue
				videoFragmentConfigurationFieldValue =
					(VideoFragmentConfigurationFieldValue)
						fragmentConfigurationFieldValue;

			return _getConfigurationJSONObject(
				fragmentConfigurationField.isLocalizable(),
				videoValue -> _getVideoJSONObject(videoValue),
				videoFragmentConfigurationFieldValue.getValue(),
				videoFragmentConfigurationFieldValue.getValue_i18n());
		}

		return null;
	}

	private static JSONObject _getCategoryTreeNodeJSONObject(
			ItemExternalReference itemExternalReference,
			LayoutStructureItemImporterContext
				layoutStructureItemImporterContext)
		throws PortalException {

		if ((itemExternalReference == null) ||
			Validator.isNull(
				itemExternalReference.getExternalReferenceCode())) {

			return null;
		}

		Long groupId = ItemScopeUtil.getGroupId(
			layoutStructureItemImporterContext.getCompanyId(),
			itemExternalReference.getScope(),
			layoutStructureItemImporterContext.getGroupId());

		if (groupId == null) {
			if (Objects.equals(
					itemExternalReference.getClassName(),
					AssetCategory.class.getName())) {

				return _getCategoryTreeNodeMissingReferenceJSONObject(
					"Category", layoutStructureItemImporterContext.getGroupId(),
					itemExternalReference);
			}

			return _getCategoryTreeNodeMissingReferenceJSONObject(
				"Vocabulary", layoutStructureItemImporterContext.getGroupId(),
				itemExternalReference);
		}

		if (Objects.equals(
				itemExternalReference.getClassName(),
				AssetCategory.class.getName())) {

			AssetCategory assetCategory =
				AssetCategoryLocalServiceUtil.
					fetchAssetCategoryByExternalReferenceCode(
						itemExternalReference.getExternalReferenceCode(),
						groupId);

			if (assetCategory != null) {
				return JSONUtil.put(
					"categoryTreeNodeId",
					String.valueOf(assetCategory.getCategoryId())
				).put(
					"categoryTreeNodeType", "Category"
				).put(
					"title", assetCategory.getName()
				);
			}

			return _getCategoryTreeNodeMissingReferenceJSONObject(
				"Category", layoutStructureItemImporterContext.getGroupId(),
				itemExternalReference);
		}

		if (Objects.equals(
				itemExternalReference.getClassName(),
				AssetVocabulary.class.getName())) {

			AssetVocabulary assetVocabulary =
				AssetVocabularyLocalServiceUtil.
					fetchAssetVocabularyByExternalReferenceCode(
						itemExternalReference.getExternalReferenceCode(),
						groupId);

			if (assetVocabulary != null) {
				return JSONUtil.put(
					"categoryTreeNodeId",
					String.valueOf(assetVocabulary.getVocabularyId())
				).put(
					"categoryTreeNodeType", "Vocabulary"
				).put(
					"title", assetVocabulary.getName()
				);
			}

			return _getCategoryTreeNodeMissingReferenceJSONObject(
				"Vocabulary", layoutStructureItemImporterContext.getGroupId(),
				itemExternalReference);
		}

		throw new UnsupportedOperationException();
	}

	private static JSONObject _getCategoryTreeNodeMissingReferenceJSONObject(
			String categoryTreeNodeType, long groupId,
			ItemExternalReference itemExternalReference)
		throws PortalException {

		LogUtil.logOptionalReference(itemExternalReference, groupId);

		return JSONUtil.put(
			"categoryTreeNodeType", categoryTreeNodeType
		).put(
			"externalReferenceCode",
			itemExternalReference.getExternalReferenceCode()
		).put(
			"scopeExternalReferenceCode",
			ItemScopeUtil.getItemScopeExternalReferenceCode(
				itemExternalReference.getScope(), groupId)
		);
	}

	private static JSONObject _getColorPaletteJSONObject(
		ColorPaletteValue colorPaletteValue) {

		if (colorPaletteValue == null) {
			return null;
		}

		return JSONUtil.put(
			"color", colorPaletteValue.getColor()
		).put(
			"cssClass", colorPaletteValue.getCssClass()
		).put(
			"rgbValue", colorPaletteValue.getRgbValue()
		);
	}

	private static <T> JSONObject _getConfigurationJSONObject(
			boolean localizable,
			UnsafeFunction<T, JSONObject, Exception> unsafeFunction, T value,
			Map<String, T> valuesMap)
		throws Exception {

		if (!localizable) {
			return unsafeFunction.apply(value);
		}

		return LocalizedValueUtil.toJSONObject(
			valuesMap, curValue -> unsafeFunction.apply(curValue));
	}

	private static <T> Object _getConfigurationObject(
			boolean localizable, T value, Map<String, T> valuesMap)
		throws Exception {

		return _getConfigurationObject(
			localizable, curValue -> curValue, value, valuesMap);
	}

	private static <T, R> Object _getConfigurationObject(
			boolean localizable, UnsafeFunction<T, R, Exception> unsafeFunction,
			T value, Map<String, T> valuesMap)
		throws Exception {

		if (!localizable) {
			return unsafeFunction.apply(value);
		}

		return LocalizedValueUtil.toJSONObject(valuesMap, unsafeFunction);
	}

	private static JSONObject _getItemJSONObject(
		ItemValue itemValue,
		LayoutStructureItemImporterContext layoutStructureItemImporterContext) {

		if ((itemValue == null) || (itemValue.getItem() == null)) {
			return null;
		}

		ItemExternalReference itemExternalReference = itemValue.getItem();

		JSONObject jsonObject = InfoItemUtil.getMappedItemJSONObject(
			itemExternalReference.getClassName(),
			itemExternalReference.getExternalReferenceCode(), null,
			layoutStructureItemImporterContext.getInfoItemServiceRegistry(),
			itemExternalReference.getScope(),
			layoutStructureItemImporterContext.getGroupId());

		return jsonObject.put(
			"template",
			() -> {
				TemplateReference templateReference = itemValue.getTemplate();

				if (templateReference == null) {
					return null;
				}

				return JSONUtil.put(
					"infoItemRendererKey", templateReference.getRendererKey()
				).put(
					"templateKey", templateReference.getTemplateKey()
				);
			});
	}

	private static JSONObject _getNavigationMenuJSONObject(
			LayoutStructureItemImporterContext
				layoutStructureItemImporterContext,
			NavigationMenuValue navigationMenuValue)
		throws Exception {

		if (navigationMenuValue == null) {
			return null;
		}

		if (Objects.equals(
				navigationMenuValue.getNavigationMenuType(),
				NavigationMenuValue.NavigationMenuType.CONTEXTUAL_MENU)) {

			ContextualMenuNavigationMenuValue
				contextualMenuNavigationMenuValue =
					(ContextualMenuNavigationMenuValue)navigationMenuValue;

			return JSONUtil.put(
				"contextualMenu",
				ContextualMenuTypeUtil.toInternalType(
					contextualMenuNavigationMenuValue.getContextualMenuType()));
		}

		if (Objects.equals(
				navigationMenuValue.getNavigationMenuType(),
				NavigationMenuValue.NavigationMenuType.SITE_MENU)) {

			return _getSiteMenuJSONObject(
				layoutStructureItemImporterContext,
				(SiteMenuNavigationMenuValue)navigationMenuValue);
		}

		return _getSitePagesJSONObject(
			layoutStructureItemImporterContext,
			(SitePagesNavigationMenuValue)navigationMenuValue);
	}

	private static JSONObject
			_getSiteMenuItemExternalReferenceMissingReferenceJSONObject(
				ItemExternalReference itemExternalReference, long groupId,
				String parentSiteNavigationMenuItemExternalReferenceCode)
		throws Exception {

		LogUtil.logOptionalReference(itemExternalReference, groupId);

		return JSONUtil.put(
			"parentSiteNavigationMenuItemExternalReferenceCode",
			parentSiteNavigationMenuItemExternalReferenceCode
		).put(
			"siteNavigationMenuExternalReferenceCode",
			itemExternalReference.getExternalReferenceCode()
		).put(
			"siteNavigationMenuScopeExternalReferenceCode",
			ItemScopeUtil.getItemScopeExternalReferenceCode(
				itemExternalReference.getScope(), groupId)
		);
	}

	private static JSONObject _getSiteMenuJSONObject(
			LayoutStructureItemImporterContext
				layoutStructureItemImporterContext,
			SiteMenuNavigationMenuValue navigationMenuValue)
		throws Exception {

		SiteMenuNavigationMenuValue siteMenuNavigationMenuValue =
			navigationMenuValue;

		ItemExternalReference itemExternalReference =
			siteMenuNavigationMenuValue.
				getNavigationMenuItemExternalReference();

		Long itemGroupId = ItemScopeUtil.getItemGroupId(
			layoutStructureItemImporterContext.getCompanyId(),
			itemExternalReference.getScope(),
			layoutStructureItemImporterContext.getGroupId());

		if (itemGroupId == null) {
			return _getSiteMenuItemExternalReferenceMissingReferenceJSONObject(
				itemExternalReference,
				layoutStructureItemImporterContext.getGroupId(),
				siteMenuNavigationMenuValue.
					getParentMenuItemExternalReferenceCode());
		}

		SiteNavigationMenu siteNavigationMenu =
			SiteNavigationMenuLocalServiceUtil.
				fetchSiteNavigationMenuByExternalReferenceCode(
					itemExternalReference.getExternalReferenceCode(),
					itemGroupId);

		if (siteNavigationMenu == null) {
			return _getSiteMenuItemExternalReferenceMissingReferenceJSONObject(
				itemExternalReference,
				layoutStructureItemImporterContext.getGroupId(),
				siteMenuNavigationMenuValue.
					getParentMenuItemExternalReferenceCode());
		}

		return JSONUtil.put(
			"parentSiteNavigationMenuItemExternalReferenceCode",
			siteMenuNavigationMenuValue.getParentMenuItemExternalReferenceCode()
		).put(
			"parentSiteNavigationMenuItemId",
			() -> {
				if (Validator.isNull(
						siteMenuNavigationMenuValue.
							getParentMenuItemExternalReferenceCode())) {

					return null;
				}

				SiteNavigationMenuItem siteNavigationMenuItem =
					SiteNavigationMenuItemLocalServiceUtil.
						fetchSiteNavigationMenuItemByExternalReferenceCode(
							siteMenuNavigationMenuValue.
								getParentMenuItemExternalReferenceCode(),
							itemGroupId);

				if (siteNavigationMenuItem != null) {
					return siteNavigationMenuItem.getSiteNavigationMenuItemId();
				}

				LogUtil.logOptionalReference(
					SiteNavigationMenuItem.class.getName(),
					siteMenuNavigationMenuValue.
						getParentMenuItemExternalReferenceCode(),
					itemExternalReference.getScope(),
					layoutStructureItemImporterContext.getGroupId());

				return null;
			}
		).put(
			"siteNavigationMenuExternalReferenceCode",
			itemExternalReference.getExternalReferenceCode()
		).put(
			"siteNavigationMenuId", siteNavigationMenu.getSiteNavigationMenuId()
		).put(
			"siteNavigationMenuScopeExternalReferenceCode",
			ItemScopeUtil.getItemScopeExternalReferenceCode(
				itemExternalReference.getScope(),
				layoutStructureItemImporterContext.getGroupId())
		);
	}

	private static JSONObject _getSitePagesJSONObject(
		LayoutStructureItemImporterContext layoutStructureItemImporterContext,
		SitePagesNavigationMenuValue navigationMenuValue) {

		SitePagesNavigationMenuValue sitePagesNavigationMenuValue =
			navigationMenuValue;

		return JSONUtil.put(
			"parentSiteNavigationMenuItemExternalReferenceCode",
			sitePagesNavigationMenuValue.
				getParentSitePageExternalReferenceCode()
		).put(
			"parentSiteNavigationMenuItemId",
			() -> {
				if (Validator.isNull(
						sitePagesNavigationMenuValue.
							getParentSitePageExternalReferenceCode())) {

					return null;
				}

				Layout layout =
					LayoutLocalServiceUtil.fetchLayoutByExternalReferenceCode(
						sitePagesNavigationMenuValue.
							getParentSitePageExternalReferenceCode(),
						layoutStructureItemImporterContext.getGroupId());

				if (layout != null) {
					return layout.getPlid();
				}

				LogUtil.logOptionalReference(
					Layout.class.getName(),
					sitePagesNavigationMenuValue.
						getParentSitePageExternalReferenceCode(),
					null, layoutStructureItemImporterContext.getGroupId());

				return null;
			}
		).put(
			"privateLayout",
			Objects.equals(
				sitePagesNavigationMenuValue.getPageSetType(),
				SitePagesNavigationMenuValue.PageSetType.PRIVATE_PAGES)
		);
	}

	private static JSONObject _getURLJSONObject(
			LayoutStructureItemImporterContext
				layoutStructureItemImporterContext,
			URLValue urlValue)
		throws Exception {

		if (urlValue == null) {
			return null;
		}

		if (Objects.equals(urlValue.getUrlType(), URLValue.UrlType.HREF)) {
			HrefURLValue hrefURLValue = (HrefURLValue)urlValue;

			return JSONUtil.put("href", hrefURLValue.getHref());
		}

		SitePageURLValue sitePageURLValue = (SitePageURLValue)urlValue;

		ItemExternalReference itemExternalReference =
			sitePageURLValue.getSitePage();

		if (itemExternalReference == null) {
			return null;
		}

		return JSONUtil.put(
			"layout",
			LayoutUtil.getMappedLayoutJSONObject(
				layoutStructureItemImporterContext.getCompanyId(),
				itemExternalReference.getExternalReferenceCode(),
				itemExternalReference.getScope(),
				layoutStructureItemImporterContext.getGroupId()));
	}

	private static JSONObject _getVideoJSONObject(VideoValue videoValue) {
		if ((videoValue == null) || Validator.isNull(videoValue.getHtml())) {
			return null;
		}

		return JSONUtil.put(
			"html", videoValue.getHtml()
		).put(
			"title", videoValue.getTitle()
		).put(
			"type", VideoEmbeddableHTMLItemSelectorReturnType.class.getName()
		);
	}

	private static boolean _isValidValue(
		JSONArray validValuesJSONArray, String value) {

		for (int i = 0; i < validValuesJSONArray.length(); i++) {
			JSONObject jsonObject = validValuesJSONArray.getJSONObject(i);

			if (Objects.equals(jsonObject.getString("value"), value)) {
				return true;
			}
		}

		return false;
	}

}