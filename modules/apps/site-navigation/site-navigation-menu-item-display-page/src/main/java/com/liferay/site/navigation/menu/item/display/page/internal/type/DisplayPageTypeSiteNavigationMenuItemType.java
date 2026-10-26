/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.navigation.menu.item.display.page.internal.type;

import com.liferay.asset.display.page.portlet.AssetDisplayPageFriendlyURLProvider;
import com.liferay.asset.display.page.util.AssetDisplayPageUtil;
import com.liferay.exportimport.kernel.lar.PortletDataContext;
import com.liferay.frontend.taglib.servlet.taglib.util.JSPRenderer;
import com.liferay.info.item.ClassPKInfoItemIdentifier;
import com.liferay.info.item.ERCInfoItemIdentifier;
import com.liferay.info.item.InfoItemDetails;
import com.liferay.info.item.InfoItemIdentifier;
import com.liferay.info.item.InfoItemReference;
import com.liferay.info.item.InfoItemServiceRegistry;
import com.liferay.info.item.provider.InfoItemDetailsProvider;
import com.liferay.info.item.provider.InfoItemObjectProvider;
import com.liferay.info.item.provider.InfoItemPermissionProvider;
import com.liferay.item.selector.ItemSelector;
import com.liferay.item.selector.criteria.InfoItemItemSelectorReturnType;
import com.liferay.item.selector.criteria.info.item.criterion.InfoItemItemSelectorCriterion;
import com.liferay.layout.display.page.LayoutDisplayPageMultiSelectionProvider;
import com.liferay.layout.display.page.LayoutDisplayPageObjectProvider;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.ClassedModel;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.portlet.RequestBackedPortletURLFactoryUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.UnicodePropertiesBuilder;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.xml.Element;
import com.liferay.site.navigation.constants.SiteNavigationMenuPortletKeys;
import com.liferay.site.navigation.menu.item.display.page.internal.display.context.DisplayPageTypeSiteNavigationMenuTypeDisplayContext;
import com.liferay.site.navigation.model.SiteNavigationMenuItem;
import com.liferay.site.navigation.type.SiteNavigationMenuItemType;
import com.liferay.site.navigation.type.SiteNavigationMenuItemTypeContext;
import com.liferay.staging.StagingGroupHelper;
import com.liferay.staging.StagingGroupHelperUtil;

import jakarta.portlet.PortletURL;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import java.util.Locale;

/**
 * @author Lourdes Fernández Besada
 */
public class DisplayPageTypeSiteNavigationMenuItemType
	implements SiteNavigationMenuItemType {

	public DisplayPageTypeSiteNavigationMenuItemType(
		AssetDisplayPageFriendlyURLProvider assetDisplayPageFriendlyURLProvider,
		DisplayPageTypeContext displayPageTypeContext,
		GroupLocalService groupLocalService,
		InfoItemServiceRegistry infoItemServiceRegistry,
		ItemSelector itemSelector, JSPRenderer jspRenderer, Portal portal,
		ServletContext servletContext) {

		_assetDisplayPageFriendlyURLProvider =
			assetDisplayPageFriendlyURLProvider;
		_displayPageTypeContext = displayPageTypeContext;
		_groupLocalService = groupLocalService;
		_infoItemServiceRegistry = infoItemServiceRegistry;
		_itemSelector = itemSelector;
		_jspRenderer = jspRenderer;
		_portal = portal;
		_servletContext = servletContext;
	}

	@Override
	public boolean exportData(
		PortletDataContext portletDataContext,
		Element siteNavigationMenuItemElement,
		SiteNavigationMenuItem siteNavigationMenuItem) {

		UnicodeProperties typeSettingsUnicodeProperties =
			UnicodePropertiesBuilder.fastLoad(
				siteNavigationMenuItem.getTypeSettings()
			).build();

		String externalReferenceCode = GetterUtil.getString(
			typeSettingsUnicodeProperties.get("externalReferenceCode"));
		String scopeExternalReferenceCode = typeSettingsUnicodeProperties.get(
			"scopeExternalReferenceCode");

		try {
			LayoutDisplayPageObjectProvider<?> layoutDisplayPageObjectProvider =
				_displayPageTypeContext.getLayoutDisplayPageObjectProvider(
					externalReferenceCode, siteNavigationMenuItem.getGroupId(),
					scopeExternalReferenceCode);

			if (layoutDisplayPageObjectProvider == null) {
				return false;
			}

			siteNavigationMenuItemElement.addAttribute(
				"display-page-class-name",
				_displayPageTypeContext.getClassName());
			siteNavigationMenuItemElement.addAttribute(
				"display-page-external-reference-code",
				GetterUtil.getString(
					typeSettingsUnicodeProperties.get(
						"externalReferenceCode")));
			siteNavigationMenuItemElement.addAttribute(
				"display-page-scope-external-reference-code",
				scopeExternalReferenceCode);

			portletDataContext.addReferenceElement(
				siteNavigationMenuItem, siteNavigationMenuItemElement,
				(ClassedModel)
					layoutDisplayPageObjectProvider.getDisplayObject(),
				PortletDataContext.REFERENCE_TYPE_DEPENDENCY, false);

			return true;
		}
		catch (RuntimeException runtimeException) {
			_log.error(runtimeException);
		}

		return false;
	}

	@Override
	public String getAddTitle(Locale locale) {
		String label = _displayPageTypeContext.getLabel(locale);

		LayoutDisplayPageMultiSelectionProvider<?>
			layoutDisplayPageMultiSelectionProvider =
				_displayPageTypeContext.
					getLayoutDisplayPageMultiSelectionProvider();

		if (layoutDisplayPageMultiSelectionProvider != null) {
			label = layoutDisplayPageMultiSelectionProvider.getPluralLabel(
				locale);
		}

		return LanguageUtil.format(locale, "select-x", label);
	}

	@Override
	public PortletURL getAddURL(
		RenderRequest renderRequest, RenderResponse renderResponse) {

		return PortletURLBuilder.createActionURL(
			renderResponse
		).setActionName(
			() -> {
				if (isMultiSelection()) {
					return "/navigation_menu" +
						"/add_multiple_display_page_type_site_navigation_" +
							"menu_item";
				}

				return "/navigation_menu" +
					"/add_display_page_type_site_navigation_menu_item";
			}
		).setParameter(
			"siteNavigationMenuItemType", getType()
		).buildPortletURL();
	}

	@Override
	public String getIcon() {
		return "page";
	}

	@Override
	public String getItemSelectorURL(HttpServletRequest httpServletRequest) {
		RenderResponse renderResponse =
			(RenderResponse)httpServletRequest.getAttribute(
				JavaConstants.JAKARTA_PORTLET_RESPONSE);

		InfoItemItemSelectorCriterion itemSelectorCriterion =
			new InfoItemItemSelectorCriterion();

		itemSelectorCriterion.setDesiredItemSelectorReturnTypes(
			new InfoItemItemSelectorReturnType());
		itemSelectorCriterion.setItemType(
			_displayPageTypeContext.getClassName());
		itemSelectorCriterion.setMultiSelection(isMultiSelection());

		return PortletURLBuilder.create(
			_itemSelector.getItemSelectorURL(
				RequestBackedPortletURLFactoryUtil.create(httpServletRequest),
				renderResponse.getNamespace() + "selectItem",
				itemSelectorCriterion)
		).buildString();
	}

	@Override
	public String getLabel(Locale locale) {
		return _displayPageTypeContext.getLabel(locale);
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
			SiteNavigationMenuItem siteNavigationMenuItem)
		throws Exception {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		if (themeDisplay == null) {
			return StringPool.BLANK;
		}

		String friendlyURL =
			_assetDisplayPageFriendlyURLProvider.getFriendlyURL(
				_displayPageTypeContext.getInfoItemReference(
					siteNavigationMenuItem),
				themeDisplay);

		if (Validator.isNotNull(friendlyURL)) {
			return friendlyURL;
		}

		return StringPool.BLANK;
	}

	@Override
	public String getStatusIcon(SiteNavigationMenuItem siteNavigationMenuItem) {
		if (!_hasAssetDisplayPage(siteNavigationMenuItem) ||
			!hasModel(
				siteNavigationMenuItem.getCompanyId(),
				siteNavigationMenuItem.getGroupId(),
				UnicodePropertiesBuilder.fastLoad(
					siteNavigationMenuItem.getTypeSettings()
				).build())) {

			return "warning-full";
		}

		return SiteNavigationMenuItemType.super.getStatusIcon(
			siteNavigationMenuItem);
	}

	@Override
	public String getSubtitle(
		SiteNavigationMenuItem siteNavigationMenuItem, Locale locale) {

		return _displayPageTypeContext.getLabel(locale);
	}

	@Override
	public String getTarget(SiteNavigationMenuItem siteNavigationMenuItem) {
		UnicodeProperties typeSettingsUnicodeProperties =
			UnicodePropertiesBuilder.fastLoad(
				siteNavigationMenuItem.getTypeSettings()
			).build();

		boolean useNewTab = GetterUtil.getBoolean(
			typeSettingsUnicodeProperties.getProperty(
				"useNewTab", Boolean.FALSE.toString()));

		if (!useNewTab) {
			return StringPool.BLANK;
		}

		return "target=\"_blank\"";
	}

	@Override
	public String getTitle(
		SiteNavigationMenuItem siteNavigationMenuItem, Locale locale) {

		UnicodeProperties typeSettingsUnicodeProperties =
			UnicodePropertiesBuilder.fastLoad(
				siteNavigationMenuItem.getTypeSettings()
			).build();

		LayoutDisplayPageObjectProvider<?> layoutDisplayPageObjectProvider =
			null;

		String scopeExternalReferenceCode = typeSettingsUnicodeProperties.get(
			"scopeExternalReferenceCode");

		Group group = _groupLocalService.fetchGroupByExternalReferenceCode(
			scopeExternalReferenceCode, siteNavigationMenuItem.getCompanyId());

		if (group != null) {
			layoutDisplayPageObjectProvider =
				_displayPageTypeContext.getLayoutDisplayPageObjectProvider(
					typeSettingsUnicodeProperties.get("externalReferenceCode"),
					siteNavigationMenuItem.getGroupId(),
					scopeExternalReferenceCode);
		}

		String defaultTitle = typeSettingsUnicodeProperties.getProperty(
			"title");

		if (layoutDisplayPageObjectProvider != null) {
			defaultTitle = layoutDisplayPageObjectProvider.getTitle(locale);
		}

		if (!GetterUtil.getBoolean(
				typeSettingsUnicodeProperties.get("useCustomName"))) {

			return defaultTitle;
		}

		String defaultLanguageId = typeSettingsUnicodeProperties.getProperty(
			Field.DEFAULT_LANGUAGE_ID,
			LocaleUtil.toLanguageId(LocaleUtil.getMostRelevantLocale()));

		String localizedNames = typeSettingsUnicodeProperties.getProperty(
			"localizedNames", "{}");

		try {
			JSONObject localizedNamesJSONObject =
				JSONFactoryUtil.createJSONObject(localizedNames);

			return localizedNamesJSONObject.getString(
				LocaleUtil.toLanguageId(locale),
				localizedNamesJSONObject.getString(
					defaultLanguageId, defaultTitle));
		}
		catch (JSONException jsonException) {
			_log.error(
				"Unable to get localizedNamesJSONObject from localizedNames: " +
					localizedNames,
				jsonException);
		}

		return defaultTitle;
	}

	@Override
	public String getType() {
		return _displayPageTypeContext.getClassName();
	}

	@Override
	public boolean hasModel(
		long companyId, long groupId,
		UnicodeProperties typeSettingsUnicodeProperties) {

		LayoutDisplayPageObjectProvider<?> layoutDisplayPageObjectProvider =
			_displayPageTypeContext.getLayoutDisplayPageObjectProvider(
				typeSettingsUnicodeProperties.get("externalReferenceCode"),
				groupId,
				typeSettingsUnicodeProperties.get(
					"scopeExternalReferenceCode"));

		if ((layoutDisplayPageObjectProvider == null) ||
			(layoutDisplayPageObjectProvider.getDisplayObject() == null)) {

			return false;
		}

		return true;
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

		if (!hasModel(
				siteNavigationMenuItem.getCompanyId(),
				siteNavigationMenuItem.getGroupId(),
				typeSettingsUnicodeProperties)) {

			return false;
		}

		InfoItemPermissionProvider infoItemPermissionProvider =
			_displayPageTypeContext.getInfoItemPermissionProvider();

		if (infoItemPermissionProvider == null) {
			return true;
		}

		String className = typeSettingsUnicodeProperties.get("className");

		InfoItemObjectProvider<?> infoItemObjectProvider =
			_infoItemServiceRegistry.getFirstInfoItemService(
				InfoItemObjectProvider.class, className,
				ClassPKInfoItemIdentifier.INFO_ITEM_SERVICE_FILTER);

		Object infoItem = infoItemObjectProvider.getInfoItem(
			new ERCInfoItemIdentifier(
				typeSettingsUnicodeProperties.get("externalReferenceCode"),
				typeSettingsUnicodeProperties.get(
					"scopeExternalReferenceCode")));

		InfoItemDetailsProvider infoItemDetailsProvider =
			_infoItemServiceRegistry.getFirstInfoItemService(
				InfoItemDetailsProvider.class, className);

		InfoItemDetails infoItemDetails =
			infoItemDetailsProvider.getInfoItemDetails(
				siteNavigationMenuItem.getGroupId(),
				ClassPKInfoItemIdentifier.class, infoItem);

		return infoItemPermissionProvider.hasPermission(
			permissionChecker, infoItemDetails.getInfoItemReference(),
			ActionKeys.VIEW);
	}

	@Override
	public boolean importData(
		PortletDataContext portletDataContext,
		SiteNavigationMenuItem siteNavigationMenuItem,
		SiteNavigationMenuItem importedSiteNavigationMenuItem) {

		Element element = portletDataContext.getImportDataElement(
			siteNavigationMenuItem);

		String externalReferenceCode = GetterUtil.getString(
			element.attributeValue("display-page-external-reference-code"));

		if (externalReferenceCode == null) {
			return false;
		}

		String scopeExternalReferenceCode = GetterUtil.getString(
			element.attributeValue(
				"display-page-scope-external-reference-code"));

		if (Validator.isNull(scopeExternalReferenceCode)) {
			StagingGroupHelper stagingGroupHelper =
				StagingGroupHelperUtil.getStagingGroupHelper();

			if (stagingGroupHelper.isStagedPortlet(
					portletDataContext.getGroupId(),
					SiteNavigationMenuPortletKeys.SITE_NAVIGATION_MENU) &&
				!stagingGroupHelper.isStagedPortletData(
					portletDataContext.getGroupId(),
					_displayPageTypeContext.getClassName())) {

				Group group = _groupLocalService.fetchGroup(
					importedSiteNavigationMenuItem.getGroupId());

				if (group == null) {
					return false;
				}

				Group liveGroup = group.getLiveGroup();

				scopeExternalReferenceCode =
					liveGroup.getExternalReferenceCode();
			}
		}
		else {
			Group group = _groupLocalService.fetchGroupByExternalReferenceCode(
				scopeExternalReferenceCode,
				importedSiteNavigationMenuItem.getCompanyId());

			if ((group == null) ||
				(group.getGroupId() ==
					importedSiteNavigationMenuItem.getGroupId())) {

				scopeExternalReferenceCode = null;
			}
		}

		importedSiteNavigationMenuItem.setTypeSettings(
			UnicodePropertiesBuilder.fastLoad(
				siteNavigationMenuItem.getTypeSettings()
			).put(
				"externalReferenceCode", externalReferenceCode
			).put(
				"scopeExternalReferenceCode", scopeExternalReferenceCode
			).buildString());

		return true;
	}

	@Override
	public boolean isAvailable(
		SiteNavigationMenuItemTypeContext siteNavigationMenuItemTypeContext) {

		return _displayPageTypeContext.isAvailable();
	}

	@Override
	public boolean isBrowsable(SiteNavigationMenuItem siteNavigationMenuItem) {
		return _hasAssetDisplayPage(siteNavigationMenuItem);
	}

	@Override
	public boolean isItemSelector() {
		return true;
	}

	@Override
	public boolean isMultiSelection() {
		LayoutDisplayPageMultiSelectionProvider<?>
			layoutDisplayPageMultiSelectionProvider =
				_displayPageTypeContext.
					getLayoutDisplayPageMultiSelectionProvider();

		if (layoutDisplayPageMultiSelectionProvider != null) {
			return true;
		}

		return false;
	}

	@Override
	public void renderEditPage(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse,
			SiteNavigationMenuItem siteNavigationMenuItem)
		throws IOException {

		httpServletRequest.setAttribute(
			DisplayPageTypeSiteNavigationMenuTypeDisplayContext.class.getName(),
			new DisplayPageTypeSiteNavigationMenuTypeDisplayContext(
				_displayPageTypeContext, httpServletRequest,
				_infoItemServiceRegistry, _itemSelector,
				siteNavigationMenuItem));

		_jspRenderer.renderJSP(
			_servletContext, httpServletRequest, httpServletResponse,
			"/edit_display_page_type.jsp");
	}

	private boolean _hasAssetDisplayPage(
		SiteNavigationMenuItem siteNavigationMenuItem) {

		UnicodeProperties typeSettingsUnicodeProperties =
			UnicodePropertiesBuilder.fastLoad(
				siteNavigationMenuItem.getTypeSettings()
			).build();

		String scopeExternalReferenceCode = typeSettingsUnicodeProperties.get(
			"scopeExternalReferenceCode");

		Group group = _groupLocalService.fetchGroupByExternalReferenceCode(
			scopeExternalReferenceCode, siteNavigationMenuItem.getCompanyId());

		if ((scopeExternalReferenceCode != null) && (group == null)) {
			return false;
		}

		InfoItemIdentifier infoItemIdentifier = new ERCInfoItemIdentifier(
			GetterUtil.getString(
				typeSettingsUnicodeProperties.get("externalReferenceCode")),
			scopeExternalReferenceCode);

		return AssetDisplayPageUtil.hasAssetDisplayPage(
			siteNavigationMenuItem.getGroupId(),
			new InfoItemReference(
				GetterUtil.getString(
					typeSettingsUnicodeProperties.get("className")),
				infoItemIdentifier));
	}

	private static final Log _log = LogFactoryUtil.getLog(
		DisplayPageTypeSiteNavigationMenuItemType.class);

	private final AssetDisplayPageFriendlyURLProvider
		_assetDisplayPageFriendlyURLProvider;
	private final DisplayPageTypeContext _displayPageTypeContext;
	private final GroupLocalService _groupLocalService;
	private final InfoItemServiceRegistry _infoItemServiceRegistry;
	private final ItemSelector _itemSelector;
	private final JSPRenderer _jspRenderer;
	private final Portal _portal;
	private final ServletContext _servletContext;

}