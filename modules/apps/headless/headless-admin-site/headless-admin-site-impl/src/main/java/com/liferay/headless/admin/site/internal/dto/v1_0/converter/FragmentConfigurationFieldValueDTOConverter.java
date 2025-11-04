/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.internal.dto.v1_0.converter;

import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.model.AssetVocabulary;
import com.liferay.asset.kernel.service.AssetCategoryLocalService;
import com.liferay.asset.kernel.service.AssetVocabularyLocalService;
import com.liferay.fragment.entry.processor.helper.LayoutReferenceResolver;
import com.liferay.fragment.util.configuration.FragmentConfigurationField;
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
import com.liferay.headless.admin.site.dto.v1_0.Scope;
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
import com.liferay.headless.admin.site.internal.dto.v1_0.util.LocalizedValueUtil;
import com.liferay.info.item.ERCInfoItemIdentifier;
import com.liferay.info.item.InfoItemServiceRegistry;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterContext;
import com.liferay.site.navigation.model.SiteNavigationMenu;
import com.liferay.site.navigation.model.SiteNavigationMenuItem;
import com.liferay.site.navigation.service.SiteNavigationMenuItemLocalService;
import com.liferay.site.navigation.service.SiteNavigationMenuLocalService;

import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Lourdes Fernández Besada
 */
@Component(
	property = "dto.class.name=com.liferay.fragment.util.configuration.FragmentConfigurationField",
	service = DTOConverter.class
)
public class FragmentConfigurationFieldValueDTOConverter
	implements DTOConverter
		<FragmentConfigurationField, FragmentConfigurationFieldValue> {

	@Override
	public String getContentType() {
		return FragmentConfigurationFieldValue.class.getSimpleName();
	}

	@Override
	public FragmentConfigurationFieldValue toDTO(
			DTOConverterContext dtoConverterContext,
			FragmentConfigurationField fragmentConfigurationField)
		throws Exception {

		if (dtoConverterContext == null) {
			return null;
		}

		Object fragmentFragmentConfigurationFieldValue =
			dtoConverterContext.getAttribute(
				"fragmentFragmentConfigurationFieldValue");

		if (fragmentFragmentConfigurationFieldValue == null) {
			return null;
		}

		FragmentConfigurationFieldValue.Type type =
			FragmentConfigurationFieldValueTypeUtil.toExternalType(
				fragmentConfigurationField.getType());

		if (Objects.equals(
				type, FragmentConfigurationFieldValue.Type.CATEGORY)) {

			if (!(fragmentFragmentConfigurationFieldValue instanceof
					JSONObject)) {

				return null;
			}

			return _getCategoryFragmentConfigurationFieldValue(
				dtoConverterContext, fragmentConfigurationField,
				(JSONObject)fragmentFragmentConfigurationFieldValue);
		}

		if (Objects.equals(
				type, FragmentConfigurationFieldValue.Type.CHECKBOX)) {

			return _getCheckboxFragmentConfigurationFieldValue(
				fragmentConfigurationField,
				fragmentFragmentConfigurationFieldValue);
		}

		if (Objects.equals(
				type, FragmentConfigurationFieldValue.Type.COLLECTION)) {

			if (!(fragmentFragmentConfigurationFieldValue instanceof
					JSONObject)) {

				return null;
			}

			return _getCollectionFragmentConfigurationFieldValue(
				dtoConverterContext, fragmentConfigurationField,
				(JSONObject)fragmentFragmentConfigurationFieldValue);
		}

		if (Objects.equals(
				type, FragmentConfigurationFieldValue.Type.COLOR_PALETTE)) {

			if (!(fragmentFragmentConfigurationFieldValue instanceof
					JSONObject)) {

				return null;
			}

			return _getColorPaletteFragmentConfigurationFieldValue(
				fragmentConfigurationField,
				(JSONObject)fragmentFragmentConfigurationFieldValue);
		}

		if (Objects.equals(
				type, FragmentConfigurationFieldValue.Type.COLOR_PICKER)) {

			return _getColorPickerFragmentConfigurationFieldValue(
				fragmentConfigurationField,
				fragmentFragmentConfigurationFieldValue);
		}

		if (Objects.equals(type, FragmentConfigurationFieldValue.Type.ITEM)) {
			if (!(fragmentFragmentConfigurationFieldValue instanceof
					JSONObject)) {

				return null;
			}

			return _getItemFragmentConfigurationFieldValue(
				dtoConverterContext, fragmentConfigurationField,
				(JSONObject)fragmentFragmentConfigurationFieldValue);
		}

		if (Objects.equals(type, FragmentConfigurationFieldValue.Type.LENGTH)) {
			return _getLengthFragmentConfigurationFieldValue(
				fragmentConfigurationField,
				fragmentFragmentConfigurationFieldValue);
		}

		if (Objects.equals(
				type, FragmentConfigurationFieldValue.Type.NAVIGATION_MENU)) {

			if (!(fragmentFragmentConfigurationFieldValue instanceof
					JSONObject)) {

				return null;
			}

			return _getNavigationMenuFragmentConfigurationFieldValue(
				dtoConverterContext, fragmentConfigurationField,
				(JSONObject)fragmentFragmentConfigurationFieldValue);
		}

		if (Objects.equals(type, FragmentConfigurationFieldValue.Type.SELECT)) {
			return _getSelectFragmentConfigurationFieldValue(
				fragmentConfigurationField,
				fragmentFragmentConfigurationFieldValue);
		}

		if (Objects.equals(type, FragmentConfigurationFieldValue.Type.TEXT)) {
			return _getTextFragmentConfigurationFieldValue(
				fragmentConfigurationField,
				fragmentFragmentConfigurationFieldValue);
		}

		if (Objects.equals(type, FragmentConfigurationFieldValue.Type.URL)) {
			if (!(fragmentFragmentConfigurationFieldValue instanceof
					JSONObject)) {

				return null;
			}

			return _getURLFragmentConfigurationFieldValue(
				dtoConverterContext, fragmentConfigurationField,
				(JSONObject)fragmentFragmentConfigurationFieldValue);
		}

		if (Objects.equals(type, FragmentConfigurationFieldValue.Type.VIDEO)) {
			if (!(fragmentFragmentConfigurationFieldValue instanceof
					JSONObject)) {

				return null;
			}

			return _getVideoFragmentConfigurationFieldValue(
				fragmentConfigurationField,
				(JSONObject)fragmentFragmentConfigurationFieldValue);
		}

		return null;
	}

	private FragmentConfigurationFieldValue
		_getCategoryFragmentConfigurationFieldValue(
			DTOConverterContext dtoConverterContext,
			FragmentConfigurationField fragmentConfigurationField,
			JSONObject jsonObject) {

		Long companyId = (Long)dtoConverterContext.getAttribute("companyId");
		Long scopeGroupId = (Long)dtoConverterContext.getAttribute(
			"scopeGroupId");

		if ((companyId == null) || (scopeGroupId == null)) {
			throw new UnsupportedOperationException();
		}

		CategoryFragmentConfigurationFieldValue
			categoryFragmentConfigurationFieldValue =
				new CategoryFragmentConfigurationFieldValue();

		if (fragmentConfigurationField.isLocalizable()) {
			categoryFragmentConfigurationFieldValue.setValue_i18n(
				() -> LocalizedValueUtil.toLocalizedValues(
					jsonObject,
					key -> _getCategoryTreeNodeItemExternalReference(
						companyId, jsonObject.getJSONObject(key),
						scopeGroupId)));
		}
		else {
			categoryFragmentConfigurationFieldValue.setValue(
				() -> _getCategoryTreeNodeItemExternalReference(
					companyId, jsonObject, scopeGroupId));
		}

		return categoryFragmentConfigurationFieldValue;
	}

	private ItemExternalReference _getCategoryTreeNodeItemExternalReference(
			long companyId, JSONObject jsonObject, long scopeGroupId)
		throws Exception {

		if (JSONUtil.isEmpty(jsonObject)) {
			return null;
		}

		long categoryTreeNodeId = jsonObject.getLong("categoryTreeNodeId");
		String externalReferenceCode = jsonObject.getString(
			"externalReferenceCode");
		String type = jsonObject.getString("categoryTreeNodeType");

		if (((categoryTreeNodeId == 0) &&
			 Validator.isNull(externalReferenceCode)) ||
			(!Objects.equals(type, "Category") &&
			 !Objects.equals(type, "Vocabulary"))) {

			return null;
		}

		if (categoryTreeNodeId == 0) {
			String className = AssetCategory.class.getName();

			if (Objects.equals(type, "Vocabulary")) {
				className = AssetVocabulary.class.getName();
			}

			return _getItemExternalReference(
				className, externalReferenceCode,
				ItemScopeUtil.getItemScope(
					companyId,
					jsonObject.getString("scopeExternalReferenceCode"),
					scopeGroupId));
		}

		if (Objects.equals(type, "Category")) {
			AssetCategory assetCategory =
				_assetCategoryLocalService.fetchAssetCategory(
					categoryTreeNodeId);

			if (assetCategory == null) {
				return _getItemExternalReference(
					AssetCategory.class.getName(), externalReferenceCode,
					ItemScopeUtil.getItemScope(
						companyId,
						jsonObject.getString("scopeExternalReferenceCode"),
						scopeGroupId));
			}

			return _getItemExternalReference(
				AssetCategory.class.getName(),
				assetCategory.getExternalReferenceCode(),
				ItemScopeUtil.getItemScope(
					assetCategory.getGroupId(), scopeGroupId));
		}

		AssetVocabulary assetVocabulary =
			_assetVocabularyLocalService.fetchAssetVocabulary(
				categoryTreeNodeId);

		if (assetVocabulary == null) {
			return _getItemExternalReference(
				AssetVocabulary.class.getName(), externalReferenceCode,
				ItemScopeUtil.getItemScope(
					companyId,
					jsonObject.getString("scopeExternalReferenceCode"),
					scopeGroupId));
		}

		return _getItemExternalReference(
			AssetVocabulary.class.getName(),
			assetVocabulary.getExternalReferenceCode(),
			ItemScopeUtil.getItemScope(
				assetVocabulary.getGroupId(), scopeGroupId));
	}

	private FragmentConfigurationFieldValue
		_getCheckboxFragmentConfigurationFieldValue(
			FragmentConfigurationField fragmentConfigurationField,
			Object fragmentFragmentConfigurationFieldValue) {

		CheckboxFragmentConfigurationFieldValue
			checkboxFragmentConfigurationFieldValue =
				new CheckboxFragmentConfigurationFieldValue();

		if (fragmentConfigurationField.isLocalizable()) {
			JSONObject jsonObject =
				(JSONObject)fragmentFragmentConfigurationFieldValue;

			checkboxFragmentConfigurationFieldValue.setValue_i18n(
				() -> LocalizedValueUtil.toLocalizedValues(
					jsonObject, key -> jsonObject.getBoolean(key)));
		}
		else {
			checkboxFragmentConfigurationFieldValue.setValue(
				() -> GetterUtil.getBoolean(
					fragmentFragmentConfigurationFieldValue));
		}

		return checkboxFragmentConfigurationFieldValue;
	}

	private FragmentConfigurationFieldValue
		_getCollectionFragmentConfigurationFieldValue(
			DTOConverterContext dtoConverterContext,
			FragmentConfigurationField fragmentConfigurationField,
			JSONObject jsonObject) {

		Long companyId = (Long)dtoConverterContext.getAttribute("companyId");
		Long scopeGroupId = (Long)dtoConverterContext.getAttribute(
			"scopeGroupId");

		if ((companyId == null) || (scopeGroupId == null)) {
			throw new UnsupportedOperationException();
		}

		CollectionFragmentConfigurationFieldValue
			collectionFragmentConfigurationFieldValue =
				new CollectionFragmentConfigurationFieldValue();

		if (fragmentConfigurationField.isLocalizable()) {
			collectionFragmentConfigurationFieldValue.setValue_i18n(
				() -> LocalizedValueUtil.toLocalizedValues(
					jsonObject,
					key -> CollectionUtil.getCollectionReference(
						companyId, jsonObject.getJSONObject(key),
						scopeGroupId)));
		}
		else {
			collectionFragmentConfigurationFieldValue.setValue(
				() -> CollectionUtil.getCollectionReference(
					companyId, jsonObject, scopeGroupId));
		}

		return collectionFragmentConfigurationFieldValue;
	}

	private FragmentConfigurationFieldValue
		_getColorPaletteFragmentConfigurationFieldValue(
			FragmentConfigurationField fragmentConfigurationField,
			JSONObject jsonObject) {

		ColorPaletteFragmentConfigurationFieldValue
			colorPaletteFragmentConfigurationFieldValue =
				new ColorPaletteFragmentConfigurationFieldValue();

		if (fragmentConfigurationField.isLocalizable()) {
			colorPaletteFragmentConfigurationFieldValue.setValue_i18n(
				() -> LocalizedValueUtil.toLocalizedValues(
					jsonObject,
					key -> _getColorPaletteValue(
						jsonObject.getJSONObject(key))));
		}
		else {
			colorPaletteFragmentConfigurationFieldValue.setValue(
				() -> _getColorPaletteValue(jsonObject));
		}

		return colorPaletteFragmentConfigurationFieldValue;
	}

	private ColorPaletteValue _getColorPaletteValue(JSONObject jsonObject) {
		if (JSONUtil.isEmpty(jsonObject)) {
			return null;
		}

		return new ColorPaletteValue() {
			{
				setColor(() -> jsonObject.getString("color"));
				setCssClass(() -> jsonObject.getString("cssClass"));
				setRgbValue(() -> jsonObject.getString("rgbValue"));
			}
		};
	}

	private FragmentConfigurationFieldValue
		_getColorPickerFragmentConfigurationFieldValue(
			FragmentConfigurationField fragmentConfigurationField,
			Object fragmentFragmentConfigurationFieldValue) {

		ColorPickerFragmentConfigurationFieldValue
			colorPickerFragmentConfigurationFieldValue =
				new ColorPickerFragmentConfigurationFieldValue();

		if (fragmentConfigurationField.isLocalizable()) {
			JSONObject jsonObject =
				(JSONObject)fragmentFragmentConfigurationFieldValue;

			colorPickerFragmentConfigurationFieldValue.setValue_i18n(
				() -> LocalizedValueUtil.toLocalizedValues(jsonObject));
		}
		else {
			colorPickerFragmentConfigurationFieldValue.setValue(
				() -> GetterUtil.getString(
					fragmentFragmentConfigurationFieldValue));
		}

		return colorPickerFragmentConfigurationFieldValue;
	}

	private ContextualMenuNavigationMenuValue
		_getContextualMenuNavigationMenuValue(String contextualMenu) {

		if (Validator.isNull(contextualMenu)) {
			return null;
		}

		ContextualMenuNavigationMenuValue contextualMenuNavigationMenuValue =
			new ContextualMenuNavigationMenuValue();

		contextualMenuNavigationMenuValue.setContextualMenuType(
			() -> ContextualMenuTypeUtil.toExternalType(contextualMenu));

		return contextualMenuNavigationMenuValue;
	}

	private ItemExternalReference _getInfoItemExternalReference(
		long companyId, JSONObject jsonObject, long scopeGroupId) {

		if (JSONUtil.isEmpty(jsonObject)) {
			return null;
		}

		String className = jsonObject.getString("className");
		long classPK = jsonObject.getLong("classPK");
		String externalReferenceCode = jsonObject.getString(
			"externalReferenceCode");

		if (Validator.isNull(className) ||
			((classPK == 0) && Validator.isNull(externalReferenceCode))) {

			return null;
		}

		ERCInfoItemIdentifier ercInfoItemIdentifier =
			InfoItemUtil.getERCInfoItemIdentifier(
				className, classPK, _infoItemServiceRegistry, scopeGroupId);

		if (ercInfoItemIdentifier != null) {
			return _getItemExternalReference(
				className, ercInfoItemIdentifier.getExternalReferenceCode(),
				ItemScopeUtil.getItemScope(
					companyId,
					ercInfoItemIdentifier.getScopeExternalReferenceCode(),
					scopeGroupId));
		}

		return _getItemExternalReference(
			className, externalReferenceCode,
			ItemScopeUtil.getItemScope(
				companyId, jsonObject.getString("scopeExternalReferenceCode"),
				scopeGroupId));
	}

	private ItemExternalReference _getItemExternalReference(
		String className, String externalReferenceCode, Scope scope) {

		ItemExternalReference itemExternalReference =
			new ItemExternalReference();

		itemExternalReference.setClassName(() -> className);
		itemExternalReference.setExternalReferenceCode(
			() -> externalReferenceCode);
		itemExternalReference.setScope(() -> scope);

		return itemExternalReference;
	}

	private FragmentConfigurationFieldValue
			_getItemFragmentConfigurationFieldValue(
				DTOConverterContext dtoConverterContext,
				FragmentConfigurationField fragmentConfigurationField,
				JSONObject jsonObject)
		throws Exception {

		Long companyId = (Long)dtoConverterContext.getAttribute("companyId");
		Long scopeGroupId = (Long)dtoConverterContext.getAttribute(
			"scopeGroupId");

		if ((companyId == null) || (scopeGroupId == null)) {
			throw new UnsupportedOperationException();
		}

		ItemFragmentConfigurationFieldValue
			itemFragmentConfigurationFieldValue =
				new ItemFragmentConfigurationFieldValue();

		if (fragmentConfigurationField.isLocalizable()) {
			itemFragmentConfigurationFieldValue.setValue_i18n(
				() -> LocalizedValueUtil.toLocalizedValues(
					jsonObject,
					key -> _getItemValue(
						companyId, jsonObject.getJSONObject(key),
						scopeGroupId)));
		}
		else {
			itemFragmentConfigurationFieldValue.setValue(
				() -> _getItemValue(companyId, jsonObject, scopeGroupId));
		}

		return itemFragmentConfigurationFieldValue;
	}

	private ItemValue _getItemValue(
		long companyId, JSONObject jsonObject, long scopeGroupId) {

		ItemExternalReference infoItemExternalReference =
			_getInfoItemExternalReference(companyId, jsonObject, scopeGroupId);

		if (infoItemExternalReference == null) {
			return null;
		}

		ItemValue itemValue = new ItemValue();

		itemValue.setItem(() -> infoItemExternalReference);
		itemValue.setTemplate(
			() -> {
				JSONObject templateJSONObject = jsonObject.getJSONObject(
					"template");

				if (JSONUtil.isEmpty(templateJSONObject)) {
					return null;
				}

				return new TemplateReference() {
					{
						setRendererKey(
							() -> templateJSONObject.getString(
								"infoItemRendererKey", null));
						setTemplateKey(
							() -> templateJSONObject.getString(
								"templateKey", null));
					}
				};
			});

		return itemValue;
	}

	private FragmentConfigurationFieldValue
		_getLengthFragmentConfigurationFieldValue(
			FragmentConfigurationField fragmentConfigurationField,
			Object fragmentFragmentConfigurationFieldValue) {

		LengthFragmentConfigurationFieldValue
			lengthFragmentConfigurationFieldValue =
				new LengthFragmentConfigurationFieldValue();

		if (fragmentConfigurationField.isLocalizable()) {
			JSONObject jsonObject =
				(JSONObject)fragmentFragmentConfigurationFieldValue;

			lengthFragmentConfigurationFieldValue.setValue_i18n(
				() -> LocalizedValueUtil.toLocalizedValues(jsonObject));
		}
		else {
			lengthFragmentConfigurationFieldValue.setValue(
				() -> GetterUtil.getString(
					fragmentFragmentConfigurationFieldValue));
		}

		return lengthFragmentConfigurationFieldValue;
	}

	private FragmentConfigurationFieldValue
			_getNavigationMenuFragmentConfigurationFieldValue(
				DTOConverterContext dtoConverterContext,
				FragmentConfigurationField fragmentConfigurationField,
				JSONObject jsonObject)
		throws Exception {

		Long companyId = (Long)dtoConverterContext.getAttribute("companyId");
		Long scopeGroupId = (Long)dtoConverterContext.getAttribute(
			"scopeGroupId");

		if ((companyId == null) || (scopeGroupId == null)) {
			throw new UnsupportedOperationException();
		}

		NavigationMenuFragmentConfigurationFieldValue
			navigationMenuFragmentConfigurationFieldValue =
				new NavigationMenuFragmentConfigurationFieldValue();

		if (fragmentConfigurationField.isLocalizable()) {
			navigationMenuFragmentConfigurationFieldValue.setValue_i18n(
				() -> LocalizedValueUtil.toLocalizedValues(
					jsonObject,
					key -> _getNavigationMenuValue(
						companyId, jsonObject.getJSONObject(key),
						scopeGroupId)));
		}
		else {
			navigationMenuFragmentConfigurationFieldValue.setValue(
				() -> _getNavigationMenuValue(
					companyId, jsonObject, scopeGroupId));
		}

		return navigationMenuFragmentConfigurationFieldValue;
	}

	private NavigationMenuValue _getNavigationMenuValue(
			long companyId, JSONObject jsonObject, long scopeGroupId)
		throws Exception {

		if (JSONUtil.isEmpty(jsonObject)) {
			return null;
		}

		long siteNavigationMenuId = jsonObject.getLong("siteNavigationMenuId");
		String siteNavigationMenuExternalReferenceCode = jsonObject.getString(
			"siteNavigationMenuExternalReferenceCode");
		long parentSiteNavigationMenuItemId = jsonObject.getLong(
			"parentSiteNavigationMenuItemId");
		String parentSiteNavigationMenuItemExternalReferenceCode =
			jsonObject.getString(
				"parentSiteNavigationMenuItemExternalReferenceCode", null);

		if (!jsonObject.has("privateLayout") &&
			Validator.isNull(siteNavigationMenuExternalReferenceCode) &&
			(siteNavigationMenuId == 0) &&
			Validator.isNull(
				parentSiteNavigationMenuItemExternalReferenceCode) &&
			(parentSiteNavigationMenuItemId == 0)) {

			return _getContextualMenuNavigationMenuValue(
				jsonObject.getString("contextualMenu"));
		}

		ItemExternalReference itemExternalReference =
			_getSiteNavigationMenuItemExternalReference(
				companyId, siteNavigationMenuExternalReferenceCode,
				jsonObject.getString(
					"siteNavigationMenuScopeExternalReferenceCode"),
				siteNavigationMenuId, scopeGroupId);

		if (itemExternalReference != null) {
			return _getSiteMenuNavigationMenuValue(
				itemExternalReference, parentSiteNavigationMenuItemId,
				parentSiteNavigationMenuItemExternalReferenceCode);
		}

		return _getSitePagesNavigationMenuValue(
			parentSiteNavigationMenuItemExternalReferenceCode,
			jsonObject.getBoolean("privateLayout"));
	}

	private FragmentConfigurationFieldValue
		_getSelectFragmentConfigurationFieldValue(
			FragmentConfigurationField fragmentConfigurationField,
			Object fragmentFragmentConfigurationFieldValue) {

		SelectFragmentConfigurationFieldValue
			selectFragmentConfigurationFieldValue =
				new SelectFragmentConfigurationFieldValue();

		if (fragmentConfigurationField.isLocalizable()) {
			JSONObject jsonObject =
				(JSONObject)fragmentFragmentConfigurationFieldValue;

			selectFragmentConfigurationFieldValue.setValue_i18n(
				() -> LocalizedValueUtil.toLocalizedValues(jsonObject));
		}
		else {
			selectFragmentConfigurationFieldValue.setValue(
				() -> GetterUtil.getString(
					fragmentFragmentConfigurationFieldValue));
		}

		return selectFragmentConfigurationFieldValue;
	}

	private SiteMenuNavigationMenuValue _getSiteMenuNavigationMenuValue(
		ItemExternalReference itemExternalReference,
		long parentSiteNavigationMenuItemId,
		String parentSiteNavigationMenuItemExternalReferenceCode) {

		SiteMenuNavigationMenuValue siteMenuNavigationMenuValue =
			new SiteMenuNavigationMenuValue();

		siteMenuNavigationMenuValue.setNavigationMenuItemExternalReference(
			() -> itemExternalReference);

		siteMenuNavigationMenuValue.setParentMenuItemExternalReferenceCode(
			() -> {
				SiteNavigationMenuItem siteNavigationMenuItem =
					_siteNavigationMenuItemLocalService.
						fetchSiteNavigationMenuItem(
							parentSiteNavigationMenuItemId);

				if (siteNavigationMenuItem != null) {
					return siteNavigationMenuItem.getExternalReferenceCode();
				}

				return parentSiteNavigationMenuItemExternalReferenceCode;
			});

		return siteMenuNavigationMenuValue;
	}

	private ItemExternalReference _getSiteNavigationMenuItemExternalReference(
			long companyId, String externalReferenceCode,
			String scopeExternalReferenceCode, long siteNavigationMenuId,
			long scopeGroupId)
		throws Exception {

		if (siteNavigationMenuId > 0) {
			SiteNavigationMenu siteNavigationMenu =
				_siteNavigationMenuLocalService.fetchSiteNavigationMenu(
					siteNavigationMenuId);

			if (siteNavigationMenu != null) {
				return _getItemExternalReference(
					SiteNavigationMenu.class.getName(),
					siteNavigationMenu.getExternalReferenceCode(),
					ItemScopeUtil.getItemScope(
						siteNavigationMenu.getGroupId(), scopeGroupId));
			}
		}

		if (Validator.isNotNull(externalReferenceCode)) {
			return _getItemExternalReference(
				SiteNavigationMenu.class.getName(), externalReferenceCode,
				ItemScopeUtil.getItemScope(
					companyId, scopeExternalReferenceCode, scopeGroupId));
		}

		return null;
	}

	private SitePagesNavigationMenuValue _getSitePagesNavigationMenuValue(
		String parentSitePageExternalReferenceCode, Boolean privateLayout) {

		SitePagesNavigationMenuValue sitePagesNavigationMenuValue =
			new SitePagesNavigationMenuValue();

		sitePagesNavigationMenuValue.setPageSetType(
			() -> {
				if (GetterUtil.getBoolean(privateLayout)) {
					return SitePagesNavigationMenuValue.PageSetType.
						PRIVATE_PAGES;
				}

				return SitePagesNavigationMenuValue.PageSetType.PUBLIC_PAGES;
			});

		sitePagesNavigationMenuValue.setParentSitePageExternalReferenceCode(
			() -> parentSitePageExternalReferenceCode);

		return sitePagesNavigationMenuValue;
	}

	private FragmentConfigurationFieldValue
		_getTextFragmentConfigurationFieldValue(
			FragmentConfigurationField fragmentConfigurationField,
			Object fragmentFragmentConfigurationFieldValue) {

		TextFragmentConfigurationFieldValue
			textFragmentConfigurationFieldValue =
				new TextFragmentConfigurationFieldValue();

		if (fragmentConfigurationField.isLocalizable()) {
			JSONObject jsonObject =
				(JSONObject)fragmentFragmentConfigurationFieldValue;

			textFragmentConfigurationFieldValue.setValue_i18n(
				() -> LocalizedValueUtil.toLocalizedValues(jsonObject));
		}
		else {
			textFragmentConfigurationFieldValue.setValue(
				() -> GetterUtil.getString(
					fragmentFragmentConfigurationFieldValue));
		}

		return textFragmentConfigurationFieldValue;
	}

	private FragmentConfigurationFieldValue
		_getURLFragmentConfigurationFieldValue(
			DTOConverterContext dtoConverterContext,
			FragmentConfigurationField fragmentConfigurationField,
			JSONObject jsonObject) {

		Long companyId = (Long)dtoConverterContext.getAttribute("companyId");
		Long scopeGroupId = (Long)dtoConverterContext.getAttribute(
			"scopeGroupId");

		if ((companyId == null) || (scopeGroupId == null)) {
			throw new UnsupportedOperationException();
		}

		URLFragmentConfigurationFieldValue urlFragmentConfigurationFieldValue =
			new URLFragmentConfigurationFieldValue();

		if (fragmentConfigurationField.isLocalizable()) {
			urlFragmentConfigurationFieldValue.setValue_i18n(
				() -> LocalizedValueUtil.toLocalizedValues(
					jsonObject,
					key -> _getURLValue(
						companyId, jsonObject.getJSONObject(key),
						scopeGroupId)));
		}
		else {
			urlFragmentConfigurationFieldValue.setValue(
				() -> _getURLValue(companyId, jsonObject, scopeGroupId));
		}

		return urlFragmentConfigurationFieldValue;
	}

	private URLValue _getURLValue(
		long companyId, JSONObject jsonObject, long scopeGroupId) {

		if (jsonObject.has("href")) {
			HrefURLValue hrefURLValue = new HrefURLValue();

			hrefURLValue.setHref(() -> jsonObject.getString("href"));

			return hrefURLValue;
		}

		JSONObject layoutJSONObject = jsonObject.getJSONObject("layout");

		if (layoutJSONObject == null) {
			return null;
		}

		Layout layout = _layoutReferenceResolver.resolve(
			companyId, layoutJSONObject, scopeGroupId);

		if (layout != null) {
			SitePageURLValue sitePageURLValue = new SitePageURLValue();

			sitePageURLValue.setSitePage(
				() -> _getItemExternalReference(
					Layout.class.getName(), layout.getExternalReferenceCode(),
					ItemScopeUtil.getItemScope(
						layout.getGroupId(), scopeGroupId)));

			return sitePageURLValue;
		}

		if (!layoutJSONObject.has("externalReferenceCode")) {
			return null;
		}

		SitePageURLValue sitePageURLValue = new SitePageURLValue();

		sitePageURLValue.setSitePage(
			() -> _getItemExternalReference(
				Layout.class.getName(),
				layoutJSONObject.getString("externalReferenceCode"),
				ItemScopeUtil.getItemScope(
					companyId,
					layoutJSONObject.getString("scopeExternalReferenceCode"),
					scopeGroupId)));

		return sitePageURLValue;
	}

	private FragmentConfigurationFieldValue
		_getVideoFragmentConfigurationFieldValue(
			FragmentConfigurationField fragmentConfigurationField,
			JSONObject jsonObject) {

		VideoFragmentConfigurationFieldValue
			videoFragmentConfigurationFieldValue =
				new VideoFragmentConfigurationFieldValue();

		if (fragmentConfigurationField.isLocalizable()) {
			videoFragmentConfigurationFieldValue.setValue_i18n(
				() -> LocalizedValueUtil.toLocalizedValues(
					jsonObject,
					key -> _getVideoValue(jsonObject.getJSONObject(key))));
		}
		else {
			videoFragmentConfigurationFieldValue.setValue(
				() -> _getVideoValue(jsonObject));
		}

		return videoFragmentConfigurationFieldValue;
	}

	private VideoValue _getVideoValue(JSONObject jsonObject) {
		if (!jsonObject.has("html") && !jsonObject.has("title")) {
			return null;
		}

		return new VideoValue() {
			{
				setHtml(() -> jsonObject.getString("html"));
				setTitle(() -> jsonObject.getString("title"));
			}
		};
	}

	@Reference
	private AssetCategoryLocalService _assetCategoryLocalService;

	@Reference
	private AssetVocabularyLocalService _assetVocabularyLocalService;

	@Reference
	private InfoItemServiceRegistry _infoItemServiceRegistry;

	@Reference
	private LayoutReferenceResolver _layoutReferenceResolver;

	@Reference
	private SiteNavigationMenuItemLocalService
		_siteNavigationMenuItemLocalService;

	@Reference
	private SiteNavigationMenuLocalService _siteNavigationMenuLocalService;

}