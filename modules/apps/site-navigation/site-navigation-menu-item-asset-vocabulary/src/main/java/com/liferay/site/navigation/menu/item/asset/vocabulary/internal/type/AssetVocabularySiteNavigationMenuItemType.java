/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.navigation.menu.item.asset.vocabulary.internal.type;

import com.liferay.asset.display.page.portlet.AssetDisplayPageFriendlyURLProvider;
import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.model.AssetVocabulary;
import com.liferay.asset.kernel.service.AssetCategoryLocalService;
import com.liferay.asset.kernel.service.AssetVocabularyLocalService;
import com.liferay.asset.vocabulary.item.selector.AssetVocabularyItemSelectorCriterion;
import com.liferay.asset.vocabulary.item.selector.AssetVocabularyItemSelectorReturnType;
import com.liferay.exportimport.kernel.lar.PortletDataContext;
import com.liferay.frontend.taglib.servlet.taglib.util.JSPRenderer;
import com.liferay.info.item.ClassPKInfoItemIdentifier;
import com.liferay.info.item.InfoItemReference;
import com.liferay.item.selector.ItemSelector;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.RequestBackedPortletURLFactoryUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.UnicodePropertiesBuilder;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.xml.Element;
import com.liferay.portlet.asset.service.permission.AssetCategoryPermission;
import com.liferay.portlet.asset.service.permission.AssetVocabularyPermission;
import com.liferay.site.navigation.menu.item.asset.vocabulary.internal.constants.AssetVocabularySiteNavigationMenuTypeConstants;
import com.liferay.site.navigation.menu.item.asset.vocabulary.internal.display.context.AssetVocabularySiteNavigationMenuTypeDisplayContext;
import com.liferay.site.navigation.menu.item.layout.constants.SiteNavigationMenuItemTypeConstants;
import com.liferay.site.navigation.model.SiteNavigationMenuItem;
import com.liferay.site.navigation.service.SiteNavigationMenuItemLocalService;
import com.liferay.site.navigation.type.SiteNavigationMenuItemType;

import jakarta.portlet.PortletURL;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Lourdes Fernández Besada
 */
@Component(
	property = {
		"service.ranking:Integer=600",
		"site.navigation.menu.item.type=" + SiteNavigationMenuItemTypeConstants.ASSET_VOCABULARY
	},
	service = SiteNavigationMenuItemType.class
)
public class AssetVocabularySiteNavigationMenuItemType
	implements SiteNavigationMenuItemType {

	@Override
	public boolean exportData(
		PortletDataContext portletDataContext,
		Element siteNavigationMenuItemElement,
		SiteNavigationMenuItem siteNavigationMenuItem) {

		UnicodeProperties typeSettingsUnicodeProperties =
			UnicodePropertiesBuilder.fastLoad(
				siteNavigationMenuItem.getTypeSettings()
			).build();

		long assetVocabularyId = GetterUtil.getLong(
			typeSettingsUnicodeProperties.get("classPK"));

		AssetVocabulary assetVocabulary =
			_assetVocabularyLocalService.fetchAssetVocabulary(
				assetVocabularyId);

		if (assetVocabulary == null) {
			return false;
		}

		siteNavigationMenuItemElement.addAttribute(
			"asset-vocabulary-id", String.valueOf(assetVocabularyId));

		portletDataContext.addReferenceElement(
			siteNavigationMenuItem, siteNavigationMenuItemElement,
			assetVocabulary, PortletDataContext.REFERENCE_TYPE_DEPENDENCY,
			false);

		return true;
	}

	@Override
	public String getAddTitle(Locale locale) {
		return _language.format(locale, "select-x", "vocabularies");
	}

	@Override
	public PortletURL getAddURL(
		RenderRequest renderRequest, RenderResponse renderResponse) {

		return PortletURLBuilder.createActionURL(
			renderResponse
		).setActionName(
			"/navigation_menu" +
				"/add_asset_vocabulary_type_site_navigation_menu_items"
		).setParameter(
			"siteNavigationMenuItemType",
			SiteNavigationMenuItemTypeConstants.ASSET_VOCABULARY
		).buildPortletURL();
	}

	@Override
	public List<SiteNavigationMenuItem> getChildrenSiteNavigationMenuItems(
			HttpServletRequest httpServletRequest,
			SiteNavigationMenuItem siteNavigationMenuItem)
		throws Exception {

		UnicodeProperties typeSettingsUnicodeProperties =
			UnicodePropertiesBuilder.fastLoad(
				siteNavigationMenuItem.getTypeSettings()
			).build();

		if (Objects.equals(
				typeSettingsUnicodeProperties.get("type"),
				"asset-vocabulary")) {

			return _getChildrenSiteNavigationMenuItems(
				0,
				GetterUtil.getLong(
					typeSettingsUnicodeProperties.get("classPK")),
				httpServletRequest,
				siteNavigationMenuItem.getSiteNavigationMenuItemId());
		}

		return _getChildrenSiteNavigationMenuItems(
			GetterUtil.getLong(typeSettingsUnicodeProperties.get("classPK")),
			GetterUtil.getLong(
				typeSettingsUnicodeProperties.get("assetVocabularyId")),
			httpServletRequest,
			siteNavigationMenuItem.getSiteNavigationMenuItemId());
	}

	@Override
	public String getIcon() {
		return "vocabulary";
	}

	@Override
	public String getItemSelectorURL(HttpServletRequest httpServletRequest) {
		RenderResponse renderResponse =
			(RenderResponse)httpServletRequest.getAttribute(
				JavaConstants.JAKARTA_PORTLET_RESPONSE);

		AssetVocabularyItemSelectorCriterion
			assetVocabularyItemSelectorCriterion =
				new AssetVocabularyItemSelectorCriterion();

		assetVocabularyItemSelectorCriterion.setDesiredItemSelectorReturnTypes(
			new AssetVocabularyItemSelectorReturnType());
		assetVocabularyItemSelectorCriterion.
			setIncludeAncestorSiteAndDepotGroupIds(true);
		assetVocabularyItemSelectorCriterion.setIncludeInternalVocabularies(
			false);
		assetVocabularyItemSelectorCriterion.setMultiSelection(
			isMultiSelection());

		return PortletURLBuilder.create(
			_itemSelector.getItemSelectorURL(
				RequestBackedPortletURLFactoryUtil.create(httpServletRequest),
				renderResponse.getNamespace() + "selectItem",
				assetVocabularyItemSelectorCriterion)
		).buildString();
	}

	@Override
	public String getLabel(Locale locale) {
		return _language.get(locale, "vocabulary");
	}

	@Override
	public String getName(String typeSettings) {
		UnicodeProperties typeSettingsUnicodeProperties =
			UnicodePropertiesBuilder.fastLoad(
				typeSettings
			).build();

		return typeSettingsUnicodeProperties.get("title");
	}

	@Override
	public String getRegularURL(
		HttpServletRequest httpServletRequest,
		SiteNavigationMenuItem siteNavigationMenuItem) {

		UnicodeProperties typeSettingsUnicodeProperties =
			UnicodePropertiesBuilder.fastLoad(
				siteNavigationMenuItem.getTypeSettings()
			).build();

		if (Objects.equals(
				typeSettingsUnicodeProperties.get("type"),
				"asset-vocabulary")) {

			return StringPool.BLANK;
		}

		String regularURL = typeSettingsUnicodeProperties.get("regularURL");

		if (Validator.isNull(regularURL)) {
			return StringPool.BLANK;
		}

		return regularURL;
	}

	@Override
	public List<SiteNavigationMenuItem> getSiteNavigationMenuItems(
			HttpServletRequest httpServletRequest,
			SiteNavigationMenuItem siteNavigationMenuItem)
		throws Exception {

		UnicodeProperties typeSettingsUnicodeProperties =
			UnicodePropertiesBuilder.fastLoad(
				siteNavigationMenuItem.getTypeSettings()
			).build();

		if (GetterUtil.getBoolean(
				typeSettingsUnicodeProperties.get(
					"showAssetVocabularyLevel")) ||
			Objects.equals(
				typeSettingsUnicodeProperties.get("type"), "asset-category")) {

			return Collections.singletonList(siteNavigationMenuItem);
		}

		return _getChildrenSiteNavigationMenuItems(
			0, GetterUtil.getLong(typeSettingsUnicodeProperties.get("classPK")),
			httpServletRequest,
			siteNavigationMenuItem.getSiteNavigationMenuItemId());
	}

	@Override
	public String getStatusIcon(SiteNavigationMenuItem siteNavigationMenuItem) {
		UnicodeProperties typeSettingsUnicodeProperties =
			UnicodePropertiesBuilder.fastLoad(
				siteNavigationMenuItem.getTypeSettings()
			).build();

		int numCategories =
			_assetCategoryLocalService.getVocabularyCategoriesCount(
				GetterUtil.getLong(
					typeSettingsUnicodeProperties.get("classPK")));

		if (numCategories > 0) {
			return SiteNavigationMenuItemType.super.getStatusIcon(
				siteNavigationMenuItem);
		}

		return "warning-full";
	}

	@Override
	public String getTitle(
		SiteNavigationMenuItem siteNavigationMenuItem, Locale locale) {

		UnicodeProperties typeSettingsUnicodeProperties =
			UnicodePropertiesBuilder.fastLoad(
				siteNavigationMenuItem.getTypeSettings()
			).build();

		if (Objects.equals(
				typeSettingsUnicodeProperties.get("type"), "asset-category")) {

			return typeSettingsUnicodeProperties.get("title");
		}

		String defaultTitle = typeSettingsUnicodeProperties.getProperty(
			"title");

		AssetVocabulary assetVocabulary =
			_assetVocabularyLocalService.fetchAssetVocabulary(
				GetterUtil.getLong(
					typeSettingsUnicodeProperties.get("classPK")));

		String defaultLanguageId = typeSettingsUnicodeProperties.getProperty(
			Field.DEFAULT_LANGUAGE_ID,
			LocaleUtil.toLanguageId(LocaleUtil.getMostRelevantLocale()));

		if (assetVocabulary != null) {
			defaultTitle = assetVocabulary.getTitle(defaultLanguageId);
		}

		if (!GetterUtil.getBoolean(
				typeSettingsUnicodeProperties.get("useCustomName"))) {

			return defaultTitle;
		}

		String localizedNames = typeSettingsUnicodeProperties.getProperty(
			"localizedNames", "{}");

		try {
			JSONObject localizedNamesJSONObject = _jsonFactory.createJSONObject(
				localizedNames);

			return localizedNamesJSONObject.getString(
				LocaleUtil.toLanguageId(locale),
				localizedNamesJSONObject.getString(
					defaultLanguageId, defaultTitle));
		}
		catch (JSONException jsonException) {
			_log.error(
				"Unable to get parse from localized names: " + localizedNames,
				jsonException);
		}

		return defaultTitle;
	}

	@Override
	public String getType() {
		return SiteNavigationMenuItemTypeConstants.ASSET_VOCABULARY;
	}

	@Override
	public boolean hasPermission(
			PermissionChecker permissionChecker,
			SiteNavigationMenuItem siteNavigationMenuItem)
		throws PortalException {

		UnicodeProperties typeSettingsUnicodeProperties =
			UnicodePropertiesBuilder.fastLoad(
				siteNavigationMenuItem.getTypeSettings()
			).build();

		return AssetVocabularyPermission.contains(
			permissionChecker,
			GetterUtil.getLong(typeSettingsUnicodeProperties.get("classPK")),
			ActionKeys.VIEW);
	}

	@Override
	public boolean importData(
		PortletDataContext portletDataContext,
		SiteNavigationMenuItem siteNavigationMenuItem,
		SiteNavigationMenuItem importedSiteNavigationMenuItem) {

		Element element = portletDataContext.getImportDataElement(
			siteNavigationMenuItem);

		long classPK = GetterUtil.getLong(
			element.attributeValue("asset-vocabulary-id"));

		if (classPK <= 0) {
			return false;
		}

		long newClassPK = MapUtil.getLong(
			(Map<Long, Long>)portletDataContext.getNewPrimaryKeysMap(
				AssetVocabulary.class.getName()),
			classPK, classPK);

		AssetVocabulary assetVocabulary =
			_assetVocabularyLocalService.fetchAssetVocabulary(newClassPK);

		if (assetVocabulary == null) {
			return false;
		}

		importedSiteNavigationMenuItem.setTypeSettings(
			UnicodePropertiesBuilder.fastLoad(
				siteNavigationMenuItem.getTypeSettings()
			).put(
				"classPK", String.valueOf(newClassPK)
			).put(
				"groupId", String.valueOf(assetVocabulary.getGroupId())
			).put(
				"title", assetVocabulary.getTitle(LocaleUtil.getSiteDefault())
			).put(
				"type", "asset-vocabulary"
			).buildString());

		return true;
	}

	@Override
	public boolean isBrowsable(SiteNavigationMenuItem siteNavigationMenuItem) {
		UnicodeProperties typeSettingsUnicodeProperties =
			UnicodePropertiesBuilder.fastLoad(
				siteNavigationMenuItem.getTypeSettings()
			).build();

		if (Objects.equals(
				typeSettingsUnicodeProperties.get("type"), "asset-category") &&
			Validator.isNotNull(
				typeSettingsUnicodeProperties.get("regularURL"))) {

			return true;
		}

		return false;
	}

	@Override
	public boolean isDynamic() {
		return true;
	}

	@Override
	public boolean isItemSelector() {
		return true;
	}

	@Override
	public boolean isMultiSelection() {
		return true;
	}

	@Override
	public void renderEditPage(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse,
			SiteNavigationMenuItem siteNavigationMenuItem)
		throws IOException {

		httpServletRequest.setAttribute(
			AssetVocabularySiteNavigationMenuTypeConstants.
				ASSET_VOCABULARY_SITE_NAVIGATION_MENU_TYPE_DISPLAY_CONTEXT,
			new AssetVocabularySiteNavigationMenuTypeDisplayContext(
				httpServletRequest, _itemSelector, siteNavigationMenuItem));

		_jspRenderer.renderJSP(
			_servletContext, httpServletRequest, httpServletResponse,
			"/edit_asset_vocabulary_type.jsp");
	}

	private List<SiteNavigationMenuItem> _getChildrenSiteNavigationMenuItems(
			long parentCategoryId, long vocabularyId,
			HttpServletRequest httpServletRequest,
			long vocabularySiteNavigationMenuItemId)
		throws Exception {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		if (themeDisplay == null) {
			return Collections.emptyList();
		}

		List<SiteNavigationMenuItem> siteNavigationMenuItems =
			new ArrayList<>();

		SiteNavigationMenuItem vocabularySiteNavigationMenuItem =
			_siteNavigationMenuItemLocalService.getSiteNavigationMenuItem(
				vocabularySiteNavigationMenuItemId);

		for (AssetCategory assetCategory :
				_assetCategoryLocalService.getVocabularyCategories(
					parentCategoryId, vocabularyId, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, null)) {

			if (!AssetCategoryPermission.contains(
					themeDisplay.getPermissionChecker(), assetCategory,
					ActionKeys.VIEW)) {

				continue;
			}

			SiteNavigationMenuItem assetCategorySiteNavigationMenuItem =
				vocabularySiteNavigationMenuItem.cloneWithOriginalValues();

			assetCategorySiteNavigationMenuItem.setTypeSettings(
				UnicodePropertiesBuilder.create(
					true
				).put(
					"assetVocabularyId",
					String.valueOf(assetCategory.getVocabularyId())
				).put(
					"classPK", String.valueOf(assetCategory.getCategoryId())
				).put(
					"groupId", String.valueOf(assetCategory.getGroupId())
				).put(
					"regularURL",
					_assetDisplayPageFriendlyURLProvider.getFriendlyURL(
						new InfoItemReference(
							AssetCategory.class.getName(),
							new ClassPKInfoItemIdentifier(
								assetCategory.getCategoryId())),
						themeDisplay)
				).put(
					"title", assetCategory.getTitle(themeDisplay.getLocale())
				).put(
					"type", "asset-category"
				).buildString());

			siteNavigationMenuItems.add(assetCategorySiteNavigationMenuItem);
		}

		return siteNavigationMenuItems;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		AssetVocabularySiteNavigationMenuItemType.class);

	@Reference
	private AssetCategoryLocalService _assetCategoryLocalService;

	@Reference
	private AssetDisplayPageFriendlyURLProvider
		_assetDisplayPageFriendlyURLProvider;

	@Reference
	private AssetVocabularyLocalService _assetVocabularyLocalService;

	@Reference
	private ItemSelector _itemSelector;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private JSPRenderer _jspRenderer;

	@Reference
	private Language _language;

	@Reference(
		target = "(osgi.web.symbolicname=com.liferay.site.navigation.menu.item.asset.vocabulary)",
		unbind = "-"
	)
	private ServletContext _servletContext;

	@Reference
	private SiteNavigationMenuItemLocalService
		_siteNavigationMenuItemLocalService;

}