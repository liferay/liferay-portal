/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.admin.web.internal.display.context;

import com.liferay.asset.kernel.model.AssetVocabulary;
import com.liferay.asset.kernel.service.AssetCategoryServiceUtil;
import com.liferay.asset.kernel.service.AssetVocabularyServiceUtil;
import com.liferay.client.extension.constants.ClientExtensionEntryConstants;
import com.liferay.client.extension.model.ClientExtensionEntryRel;
import com.liferay.client.extension.service.ClientExtensionEntryRelLocalServiceUtil;
import com.liferay.client.extension.type.CET;
import com.liferay.client.extension.type.item.selector.CETItemSelectorCriterion;
import com.liferay.client.extension.type.item.selector.CETItemSelectorReturnType;
import com.liferay.client.extension.type.manager.CETManager;
import com.liferay.exportimport.kernel.staging.LayoutStagingUtil;
import com.liferay.frontend.taglib.clay.servlet.taglib.LinkTag;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItemListBuilder;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.NavigationItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.NavigationItemListBuilder;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.VerticalNavItemList;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.VerticalNavItemListBuilder;
import com.liferay.frontend.taglib.servlet.taglib.constants.ScreenNavigationWebKeys;
import com.liferay.item.selector.ItemSelector;
import com.liferay.item.selector.criteria.FileEntryItemSelectorReturnType;
import com.liferay.item.selector.criteria.UUIDItemSelectorReturnType;
import com.liferay.item.selector.criteria.file.criterion.FileItemSelectorCriterion;
import com.liferay.layout.admin.constants.LayoutAdminPortletKeys;
import com.liferay.layout.admin.constants.LayoutScreenNavigationEntryConstants;
import com.liferay.layout.admin.web.internal.helper.LayoutActionsHelper;
import com.liferay.layout.admin.web.internal.util.FaviconUtil;
import com.liferay.layout.constants.LayoutTypeSettingsConstants;
import com.liferay.layout.page.template.constants.LayoutPageTemplateEntryTypeConstants;
import com.liferay.layout.page.template.model.LayoutPageTemplateCollection;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.service.LayoutPageTemplateCollectionServiceUtil;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryLocalServiceUtil;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryServiceUtil;
import com.liferay.layout.set.prototype.helper.LayoutSetPrototypeHelper;
import com.liferay.layout.theme.item.selector.LayoutThemeItemSelectorCriterion;
import com.liferay.layout.util.comparator.LayoutCreateDateComparator;
import com.liferay.layout.util.comparator.LayoutRelevanceComparator;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.petra.string.StringUtil;
import com.liferay.portal.kernel.dao.search.EmptyOnClickRowChecker;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutConstants;
import com.liferay.portal.kernel.model.LayoutRevision;
import com.liferay.portal.kernel.model.LayoutSet;
import com.liferay.portal.kernel.model.LayoutSetBranch;
import com.liferay.portal.kernel.model.Theme;
import com.liferay.portal.kernel.model.impl.VirtualLayout;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.LiferayWindowState;
import com.liferay.portal.kernel.portlet.RequestBackedPortletURLFactoryUtil;
import com.liferay.portal.kernel.portlet.SearchDisplayStyleUtil;
import com.liferay.portal.kernel.portlet.SearchOrderByUtil;
import com.liferay.portal.kernel.portlet.constants.FriendlyURLResolverConstants;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.portlet.url.builder.ResourceURLBuilder;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.LayoutLocalServiceUtil;
import com.liferay.portal.kernel.service.LayoutServiceUtil;
import com.liferay.portal.kernel.service.LayoutSetBranchLocalServiceUtil;
import com.liferay.portal.kernel.service.LayoutSetLocalServiceUtil;
import com.liferay.portal.kernel.service.RoleLocalServiceUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.service.permission.GroupPermissionUtil;
import com.liferay.portal.kernel.service.permission.LayoutPermissionUtil;
import com.liferay.portal.kernel.servlet.taglib.ui.BreadcrumbEntry;
import com.liferay.portal.kernel.theme.PortletDisplay;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.LocalizationUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.PrefsPropsUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.util.RobotsUtil;
import com.liferay.site.display.context.GroupDisplayContextHelper;
import com.liferay.site.navigation.model.SiteNavigationMenu;
import com.liferay.site.navigation.service.SiteNavigationMenuLocalServiceUtil;
import com.liferay.site.navigation.taglib.servlet.taglib.util.BreadcrumbEntryBuilder;
import com.liferay.site.navigation.taglib.servlet.taglib.util.BreadcrumbEntryListBuilder;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletURL;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.jsp.JspException;

import java.io.IOException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Objects;

/**
 * @author Eudaldo Alonso
 */
public class LayoutsAdminDisplayContext {

	public LayoutsAdminDisplayContext(
		ItemSelector itemSelector, LayoutActionsHelper layoutActionsHelper,
		LayoutLocalService layoutLocalService,
		LayoutSetPrototypeHelper layoutSetPrototypeHelper,
		LiferayPortletRequest liferayPortletRequest,
		LiferayPortletResponse liferayPortletResponse) {

		_itemSelector = itemSelector;
		_layoutActionsHelper = layoutActionsHelper;
		_layoutLocalService = layoutLocalService;
		_layoutSetPrototypeHelper = layoutSetPrototypeHelper;
		_liferayPortletRequest = liferayPortletRequest;
		_liferayPortletResponse = liferayPortletResponse;

		httpServletRequest = PortalUtil.getHttpServletRequest(
			liferayPortletRequest);

		_cetManager = (CETManager)httpServletRequest.getAttribute(
			CETManager.class.getName());
		_groupDisplayContextHelper = new GroupDisplayContextHelper(
			httpServletRequest);

		themeDisplay = (ThemeDisplay)liferayPortletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);
	}

	public long getActiveLayoutSetBranchId() throws PortalException {
		if (_activeLayoutSetBranchId != null) {
			return _activeLayoutSetBranchId;
		}

		_activeLayoutSetBranchId = ParamUtil.getLong(
			httpServletRequest, "layoutSetBranchId");

		Layout layout = getSelLayout();

		if (layout != null) {
			LayoutRevision layoutRevision = LayoutStagingUtil.getLayoutRevision(
				layout);

			if (layoutRevision != null) {
				_activeLayoutSetBranchId =
					layoutRevision.getLayoutSetBranchId();
			}
		}

		List<LayoutSetBranch> layoutSetBranches =
			LayoutSetBranchLocalServiceUtil.getLayoutSetBranches(
				themeDisplay.getScopeGroupId(), isPrivateLayout());

		if ((_activeLayoutSetBranchId == 0) && !layoutSetBranches.isEmpty()) {
			LayoutSetBranch currentUserLayoutSetBranch =
				LayoutSetBranchLocalServiceUtil.getUserLayoutSetBranch(
					themeDisplay.getUserId(), themeDisplay.getScopeGroupId(),
					isPrivateLayout(), 0, 0);

			_activeLayoutSetBranchId =
				currentUserLayoutSetBranch.getLayoutSetBranchId();
		}

		if ((_activeLayoutSetBranchId == 0) && !layoutSetBranches.isEmpty()) {
			LayoutSetBranch layoutSetBranch =
				LayoutSetBranchLocalServiceUtil.getMasterLayoutSetBranch(
					themeDisplay.getScopeGroupId(), isPrivateLayout());

			_activeLayoutSetBranchId = layoutSetBranch.getLayoutSetBranchId();
		}

		return _activeLayoutSetBranchId;
	}

	public List<DropdownItem> getAddLayoutDropdownItems() {
		Group group = getSelGroup();

		if (!group.isPrivateLayoutsEnabled()) {
			return DropdownItemListBuilder.add(
				this::isShowPublicLayouts,
				dropdownItem -> {
					dropdownItem.setHref(
						getSelectLayoutPageTemplateEntryURL(false));
					dropdownItem.setLabel(
						LanguageUtil.get(httpServletRequest, "page"));
				}
			).build();
		}

		return DropdownItemListBuilder.add(
			this::isShowPublicLayouts,
			dropdownItem -> {
				dropdownItem.setHref(
					getSelectLayoutPageTemplateEntryURL(false));
				dropdownItem.setLabel(
					LanguageUtil.get(httpServletRequest, "public-page"));
			}
		).add(
			dropdownItem -> {
				dropdownItem.setHref(getSelectLayoutPageTemplateEntryURL(true));
				dropdownItem.setLabel(
					LanguageUtil.get(httpServletRequest, "private-page"));
			}
		).build();
	}

	public String getAddLayoutURL() {
		return PortletURLBuilder.createActionURL(
			_liferayPortletResponse
		).setActionName(
			() -> {
				long layoutPageTemplateEntryId = ParamUtil.getLong(
					httpServletRequest, "layoutPageTemplateEntryId");

				if (layoutPageTemplateEntryId > 0) {
					return "/layout_admin/add_content_layout";
				}

				return "/layout_admin/add_simple_layout";
			}
		).setMVCRenderCommandName(
			"/layout_admin/select_layout_page_template_entry"
		).setBackURL(
			getBackURL()
		).setPortletResource(
			getPortletResource()
		).setParameter(
			"explicitCreation", true
		).setParameter(
			"groupId", getGroupId()
		).setParameter(
			"layoutPageTemplateEntryId",
			ParamUtil.getLong(httpServletRequest, "layoutPageTemplateEntryId")
		).setParameter(
			"liveGroupId", getLiveGroupId()
		).setParameter(
			"masterLayoutPlid",
			ParamUtil.getLong(httpServletRequest, "masterLayoutPlid")
		).setParameter(
			"parentLayoutId", getParentLayoutId()
		).setParameter(
			"privateLayout", isPrivateLayout()
		).setParameter(
			"stagingGroupId", getStagingGroupId()
		).setParameter(
			"type",
			() -> {
				String type = ParamUtil.getString(httpServletRequest, "type");

				if (Validator.isNotNull(type)) {
					return type;
				}

				return null;
			}
		).buildString();
	}

	public String getAddModalTitle() {
		String title = "add-page";

		if (ParamUtil.getBoolean(httpServletRequest, "emptyLayout")) {
			title = "page-name";
		}

		return LanguageUtil.get(httpServletRequest, title);
	}

	public List<SiteNavigationMenu> getAutoSiteNavigationMenus() {
		return SiteNavigationMenuLocalServiceUtil.getAutoSiteNavigationMenus(
			themeDisplay.getScopeGroupId());
	}

	public String getBackURL() {
		if (_backURL != null) {
			return _backURL;
		}

		String backURL = ParamUtil.getString(_liferayPortletRequest, "backURL");

		if (Validator.isNull(backURL)) {
			backURL = getRedirect();
		}

		_backURL = backURL;

		return _backURL;
	}

	public PortletURL getCETItemSelectorURL(
		boolean multipleSelection, String selectEventName, String type) {

		CETItemSelectorCriterion cetItemSelectorCriterion =
			new CETItemSelectorCriterion();

		cetItemSelectorCriterion.setDesiredItemSelectorReturnTypes(
			new CETItemSelectorReturnType());
		cetItemSelectorCriterion.setMultipleSelection(multipleSelection);
		cetItemSelectorCriterion.setType(type);

		return _itemSelector.getItemSelectorURL(
			RequestBackedPortletURLFactoryUtil.create(httpServletRequest),
			selectEventName, cetItemSelectorCriterion);
	}

	public String getConfigurationTitle(Layout layout, Locale locale) {
		LayoutPageTemplateEntry layoutPageTemplateEntry =
			LayoutPageTemplateEntryLocalServiceUtil.
				fetchLayoutPageTemplateEntryByPlid(layout.getPlid());

		if (layoutPageTemplateEntry != null) {
			return layoutPageTemplateEntry.getName();
		}

		return layout.getName(locale);
	}

	public String getConfigureLayoutURL(Layout layout) {
		return PortletURLBuilder.createRenderURL(
			_liferayPortletResponse
		).setMVCRenderCommandName(
			"/layout_admin/edit_layout"
		).setRedirect(
			getRedirect()
		).setBackURL(
			_getBackURL()
		).setPortletResource(
			() -> {
				PortletDisplay portletDisplay =
					themeDisplay.getPortletDisplay();

				return portletDisplay.getId();
			}
		).setParameter(
			"backURLTitle", LanguageUtil.get(httpServletRequest, "pages")
		).setParameter(
			"groupId", layout.getGroupId()
		).setParameter(
			"privateLayout", layout.isPrivateLayout()
		).setParameter(
			"selPlid", layout.getPlid()
		).buildString();
	}

	public String getConvertEmptyLayoutURL() {
		return PortletURLBuilder.createActionURL(
			_liferayPortletResponse
		).setActionName(
			"/layout_admin/convert_empty_layout"
		).setParameter(
			"layoutPageTemplateEntryId",
			ParamUtil.getLong(httpServletRequest, "layoutPageTemplateEntryId")
		).setParameter(
			"masterLayoutPlid",
			ParamUtil.getLong(httpServletRequest, "masterLayoutPlid")
		).setParameter(
			"type",
			() -> {
				String type = ParamUtil.getString(httpServletRequest, "type");

				if (Validator.isNotNull(type)) {
					return type;
				}

				return null;
			}
		).buildString();
	}

	public String getCopyLayoutActionURL(
		boolean copyPermissions, long sourcePlid) {

		return PortletURLBuilder.createActionURL(
			_liferayPortletResponse
		).setActionName(
			"/layout_admin/copy_layout"
		).setRedirect(
			getRedirect()
		).setParameter(
			"copyPermissions", copyPermissions
		).setParameter(
			"explicitCreation", Boolean.TRUE
		).setParameter(
			"groupId", getGroupId()
		).setParameter(
			"liveGroupId", getLiveGroupId()
		).setParameter(
			"privateLayout", isPrivateLayout()
		).setParameter(
			"sourcePlid", sourcePlid
		).setParameter(
			"stagingGroupId", getStagingGroupId()
		).buildString();
	}

	public String getDisplayStyle() {
		if (Validator.isNotNull(_displayStyle)) {
			return _displayStyle;
		}

		_displayStyle = SearchDisplayStyleUtil.getDisplayStyle(
			_liferayPortletRequest, LayoutAdminPortletKeys.GROUP_PAGES,
			"miller-columns");

		return _displayStyle;
	}

	public Layout getDraftLayout(Layout layout) throws Exception {
		Layout draftLayout = layout.fetchDraftLayout();

		if (draftLayout != null) {
			return draftLayout;
		}

		if (!layout.isTypeContent()) {
			return null;
		}

		UnicodeProperties unicodeProperties =
			layout.getTypeSettingsProperties();

		ServiceContext serviceContext = ServiceContextFactory.getInstance(
			httpServletRequest);

		draftLayout = LayoutLocalServiceUtil.addLayout(
			null, layout.getUserId(), layout.getGroupId(),
			layout.isPrivateLayout(), layout.getParentLayoutId(),
			PortalUtil.getClassNameId(Layout.class), layout.getPlid(),
			layout.getNameMap(), layout.getTitleMap(),
			layout.getDescriptionMap(), layout.getKeywordsMap(),
			layout.getRobotsMap(), layout.getType(),
			unicodeProperties.toString(), true, true, Collections.emptyMap(),
			layout.getMasterLayoutPlid(), serviceContext);

		draftLayout = _layoutLocalService.copyLayoutContent(
			layout, draftLayout);

		serviceContext.setAttribute(
			LayoutTypeSettingsConstants.KEY_PUBLISHED, Boolean.TRUE);

		return LayoutLocalServiceUtil.updateStatus(
			draftLayout.getUserId(), draftLayout.getPlid(),
			WorkflowConstants.STATUS_APPROVED, serviceContext);
	}

	public String getEditLayoutURL(Layout layout) throws Exception {
		if (layout.isTypeContent()) {
			return _getDraftLayoutURL(layout);
		}

		if (!Objects.equals(layout.getType(), LayoutConstants.TYPE_PORTLET) ||
			(layout.fetchDraftLayout() == null)) {

			return StringPool.BLANK;
		}

		return _getDraftLayoutURL(layout);
	}

	public String getEditOrViewLayoutURL(Layout layout) throws Exception {
		if ((isConversionDraft(layout) || layout.isTypeContent()) &&
			layout.isLayoutUpdateable() &&
			_layoutActionsHelper.isShowConfigureAction(layout)) {

			return getEditLayoutURL(layout);
		}

		if (_layoutActionsHelper.isShowViewLayoutAction(layout)) {
			return getViewLayoutURL(layout);
		}

		return StringPool.BLANK;
	}

	public Map<String, Object> getFaviconButtonProps() {
		return HashMapBuilder.<String, Object>put(
			"clearButtonEnabled", isClearFaviconButtonEnabled()
		).put(
			"defaultImgURL", _getDefaultFaviconURL()
		).put(
			"defaultTitle", _getDefaultFaviconTitle()
		).put(
			"faviconFileEntryId",
			() -> {
				Layout selLayout = getSelLayout();

				if (selLayout != null) {
					return selLayout.getFaviconFileEntryId();
				}

				LayoutSet selLayoutSet = getSelLayoutSet();

				return selLayoutSet.getFaviconFileEntryId();
			}
		).put(
			"imgURL", getFaviconURL()
		).put(
			"isReadOnly", isReadOnly()
		).put(
			"themeFaviconCETExternalReferenceCode",
			getThemeFaviconCETExternalReferenceCode()
		).put(
			"title", HtmlUtil.escape(getFaviconTitle())
		).put(
			"url", getFileEntryItemSelectorURL()
		).build();
	}

	public String getFaviconTitle() {
		if (getSelLayout() != null) {
			return FaviconUtil.getFaviconTitle(
				_cetManager, getSelLayout(), themeDisplay.getLocale());
		}

		return FaviconUtil.getFaviconTitle(
			getSelLayoutSet(), themeDisplay.getLocale());
	}

	public String getFaviconURL() {
		String faviconURL = StringPool.BLANK;

		if (getSelLayout() != null) {
			faviconURL = FaviconUtil.getFaviconURL(_cetManager, getSelLayout());
		}

		if (Validator.isNotNull(faviconURL)) {
			return faviconURL;
		}

		if (getSelLayoutSet() != null) {
			faviconURL = FaviconUtil.getFaviconURL(
				_cetManager, getSelLayoutSet());
		}

		if (Validator.isNotNull(faviconURL)) {
			return faviconURL;
		}

		return themeDisplay.getPathThemeImages() + "/" +
			PropsUtil.get(PropsKeys.THEME_SHORTCUT_ICON);
	}

	public String getFileEntryItemSelectorURL() {
		FileItemSelectorCriterion itemSelectorCriterion =
			new FileItemSelectorCriterion();

		itemSelectorCriterion.setDesiredItemSelectorReturnTypes(
			new FileEntryItemSelectorReturnType());

		CETItemSelectorCriterion cetItemSelectorCriterion =
			new CETItemSelectorCriterion();

		cetItemSelectorCriterion.setDesiredItemSelectorReturnTypes(
			new CETItemSelectorReturnType());
		cetItemSelectorCriterion.setType(
			ClientExtensionEntryConstants.TYPE_THEME_FAVICON);

		return String.valueOf(
			_itemSelector.getItemSelectorURL(
				RequestBackedPortletURLFactoryUtil.create(httpServletRequest),
				getSelectFaviconEventName(), itemSelectorCriterion,
				cetItemSelectorCriterion));
	}

	public String getFirstColumnConfigureLayoutURL(boolean privatePages) {
		return PortletURLBuilder.createRenderURL(
			_liferayPortletResponse
		).setMVCRenderCommandName(
			"/layout_admin/edit_layout_set"
		).setRedirect(
			themeDisplay.getURLCurrent()
		).setBackURL(
			_getBackURL()
		).setParameter(
			"groupId", themeDisplay.getScopeGroupId()
		).setParameter(
			"privateLayout", privatePages
		).setParameter(
			"selPlid", LayoutConstants.DEFAULT_PLID
		).buildString();
	}

	public String getFriendlyURLBase() {
		StringBuilder friendlyURLBase = new StringBuilder();

		friendlyURLBase.append(themeDisplay.getPortalURL());

		Layout selLayout = getSelLayout();

		if (selLayout.isTypeAssetDisplay()) {
			friendlyURLBase.append(
				FriendlyURLResolverConstants.URL_SEPARATOR_X_CUSTOM_ASSET);

			return friendlyURLBase.toString();
		}

		LayoutSet layoutSet = selLayout.getLayoutSet();

		NavigableMap<String, String> virtualHostnames =
			layoutSet.getVirtualHostnames();

		if (virtualHostnames.isEmpty() ||
			!_matchesHostname(friendlyURLBase, virtualHostnames)) {

			Group group = getGroup();

			friendlyURLBase.append(
				group.getPathFriendlyURL(isPrivateLayout(), themeDisplay));
			friendlyURLBase.append(
				HttpComponentsUtil.decodeURL(group.getFriendlyURL()));
		}

		return friendlyURLBase.toString();
	}

	public String getFriendlyURLWarningMessage() throws PortalException {
		if (_warningMessage != null) {
			return _warningMessage;
		}

		Layout layout = getSelLayout();

		Group group = layout.getGroup();
		LayoutSet layoutSet = layout.getLayoutSet();

		if (!FeatureFlagManagerUtil.isEnabled("LPS-174417") ||
			(!group.isLayoutSetPrototype() &&
			 !layoutSet.isLayoutSetPrototypeLinkActive())) {

			_warningMessage = StringPool.BLANK;

			return _warningMessage;
		}

		List<Layout> layouts =
			_layoutSetPrototypeHelper.getDuplicatedFriendlyURLLayouts(layout);

		if (layouts.isEmpty()) {
			_warningMessage = StringPool.BLANK;

			return _warningMessage;
		}

		String heading;

		if (group.isLayoutSetPrototype()) {
			heading = LanguageUtil.get(
				themeDisplay.getLocale(),
				"the-site-template-page-friendly-url-is-conflicting-with-the-" +
					"page-friendly-url-in-some-of-the-sites-created-from-" +
						"this-template");
		}
		else {
			heading = LanguageUtil.get(
				themeDisplay.getLocale(),
				"the-friendly-url-of-this-page-is-conflicting-with-a-" +
					"friendly-url-of-a-page-in-the-site-template,-from-which-" +
						"this-site-was-created");
		}

		List<String> layoutMessages = new ArrayList<>();

		for (Layout duplicatedFriendlyURLLayout : layouts) {
			layoutMessages.add(_getLayoutMessage(duplicatedFriendlyURLLayout));
		}

		_warningMessage = _getWarningMessageHTML(heading, layoutMessages);

		return _warningMessage;
	}

	public Group getGroup() {
		return _groupDisplayContextHelper.getGroup();
	}

	public Long getGroupId() {
		return _groupDisplayContextHelper.getGroupId();
	}

	public UnicodeProperties getGroupTypeSettingsUnicodeProperties() {
		return _groupDisplayContextHelper.getGroupTypeSettings();
	}

	public String getKeywords() {
		if (_keywords != null) {
			return _keywords;
		}

		_keywords = ParamUtil.getString(httpServletRequest, "keywords");

		return _keywords;
	}

	public String getLayoutNameLabel() {
		Layout layout = getSelLayout();

		if (layout.isTypeAssetDisplay()) {
			return "display-page-name";
		}

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			LayoutPageTemplateEntryLocalServiceUtil.
				fetchLayoutPageTemplateEntryByPlid(layout.getPlid());

		if (layoutPageTemplateEntry == null) {
			layoutPageTemplateEntry =
				LayoutPageTemplateEntryLocalServiceUtil.
					fetchLayoutPageTemplateEntryByPlid(layout.getClassPK());
		}

		if (layoutPageTemplateEntry == null) {
			return "page-name";
		}

		if (Objects.equals(
				layoutPageTemplateEntry.getType(),
				LayoutPageTemplateEntryTypeConstants.MASTER_LAYOUT)) {

			return "master-name";
		}

		return "page-template-name";
	}

	public PortletURL getLayoutScreenNavigationPortletURL(long plid) {
		return PortletURLBuilder.createRenderURL(
			_liferayPortletResponse
		).setMVCRenderCommandName(
			"/layout_admin/edit_layout"
		).setBackURL(
			getBackURL()
		).setPortletResource(
			ParamUtil.getString(httpServletRequest, "portletResource")
		).setParameter(
			"backURLTitle",
			() -> {
				PortletDisplay portletDisplay =
					themeDisplay.getPortletDisplay();

				return portletDisplay.getURLBackTitle();
			}
		).setParameter(
			"privateLayout", isPrivateLayout()
		).setParameter(
			"selPlid", plid
		).buildPortletURL();
	}

	public PortletURL getLayoutSetScreenNavigationPortletURL() {
		return PortletURLBuilder.createRenderURL(
			_liferayPortletResponse
		).setMVCRenderCommandName(
			"/layout_admin/edit_layout_set"
		).setBackURL(
			getBackURL()
		).setPortletResource(
			ParamUtil.getString(httpServletRequest, "portletResource")
		).setParameter(
			"privateLayout", isPrivateLayout()
		).buildPortletURL();
	}

	public SearchContainer<Layout> getLayoutsSearchContainer()
		throws PortalException {

		if (_layoutsSearchContainer != null) {
			return _layoutsSearchContainer;
		}

		String emptyResultMessage = "there-are-no-pages";

		Group group = themeDisplay.getScopeGroup();

		if (group.isPrivateLayoutsEnabled()) {
			emptyResultMessage = "there-are-no-public-pages";

			if (isPrivateLayout()) {
				emptyResultMessage = "there-are-no-private-pages";
			}
		}

		SearchContainer<Layout> layoutsSearchContainer = new SearchContainer(
			_liferayPortletRequest, getPortletURL(), null, emptyResultMessage);

		layoutsSearchContainer.setOrderByCol(_getOrderByCol());

		boolean orderByAsc = false;

		if (Objects.equals(_getOrderByType(), "asc")) {
			orderByAsc = true;
		}

		OrderByComparator<Layout> orderByComparator = null;

		String keywords = getKeywords();

		if (Objects.equals(_getOrderByCol(), "create-date")) {
			orderByComparator = LayoutCreateDateComparator.getInstance(
				orderByAsc);
		}
		else if (Objects.equals(_getOrderByCol(), "relevance") &&
				 Validator.isNotNull(keywords)) {

			orderByComparator = LayoutRelevanceComparator.getInstance(
				orderByAsc);
		}

		layoutsSearchContainer.setOrderByComparator(orderByComparator);
		layoutsSearchContainer.setOrderByType(_getOrderByType());

		int[] statuses = null;

		if (Validator.isNotNull(keywords)) {
			statuses = new int[] {WorkflowConstants.STATUS_ANY};
		}

		int[] layoutStatuses = statuses;

		layoutsSearchContainer.setResultsAndTotal(
			() -> LayoutServiceUtil.getLayouts(
				getSelGroupId(), isPrivateLayout(), keywords, _getTypes(),
				layoutStatuses, layoutsSearchContainer.getStart(),
				layoutsSearchContainer.getEnd(),
				layoutsSearchContainer.getOrderByComparator()),
			LayoutServiceUtil.getLayoutsCount(
				getSelGroupId(), isPrivateLayout(), keywords, _getTypes(),
				layoutStatuses));

		layoutsSearchContainer.setRowChecker(
			new EmptyOnClickRowChecker(_liferayPortletResponse));

		_layoutsSearchContainer = layoutsSearchContainer;

		return _layoutsSearchContainer;
	}

	public Group getLiveGroup() {
		return _groupDisplayContextHelper.getLiveGroup();
	}

	public Long getLiveGroupId() {
		return _groupDisplayContextHelper.getLiveGroupId();
	}

	public String getNameMapAsXML() {
		Layout layout = getSelLayout();

		Map<Locale, String> nameMap = layout.getNameMap();

		Locale siteDefaultLocale = LocaleUtil.getSiteDefault();

		if (MapUtil.isNotEmpty(nameMap) &&
			!nameMap.containsKey(siteDefaultLocale)) {

			String name = nameMap.get(layout.getDefaultLanguageId());

			if (name == null) {
				Collection<String> values = nameMap.values();

				Iterator<String> iterator = values.iterator();

				name = iterator.next();
			}

			nameMap.put(siteDefaultLocale, name);
		}

		return LocalizationUtil.updateLocalization(
			nameMap, StringPool.BLANK, "Name",
			LocaleUtil.toLanguageId(siteDefaultLocale));
	}

	public List<NavigationItem> getNavigationItems() {
		return NavigationItemListBuilder.add(
			navigationItem -> {
				navigationItem.setActive(
					!Objects.equals(getTabs1(), "utility-pages"));
				navigationItem.setHref(getPortletURL(), "tabs1", "");
				navigationItem.setLabel(
					LanguageUtil.get(httpServletRequest, "static-pages"));
			}
		).add(
			navigationItem -> {
				navigationItem.setActive(
					Objects.equals(getTabs1(), "utility-pages"));
				navigationItem.setHref(
					getPortletURL(), "tabs1", "utility-pages");
				navigationItem.setLabel(
					LanguageUtil.get(httpServletRequest, "utility-pages"));
			}
		).build();
	}

	public long getParentLayoutId() {
		if (_parentLayoutId != null) {
			return _parentLayoutId;
		}

		_parentLayoutId = 0L;

		Layout layout = getSelLayout();

		if (layout != null) {
			_parentLayoutId = layout.getLayoutId();
		}

		return _parentLayoutId;
	}

	public List<BreadcrumbEntry> getPortletBreadcrumbEntries() {
		Layout selLayout = getSelLayout();

		return BreadcrumbEntryListBuilder.add(
			breadcrumbEntry -> {
				breadcrumbEntry.setTitle(
					LanguageUtil.get(httpServletRequest, "pages"));
				breadcrumbEntry.setURL(
					PortletURLBuilder.createRenderURL(
						_liferayPortletResponse
					).setTabs1(
						getTabs1()
					).setParameter(
						"displayStyle",
						() -> {
							String displayStyle = getDisplayStyle();

							if (Validator.isNotNull(displayStyle)) {
								return displayStyle;
							}

							return null;
						}
					).setParameter(
						"firstColumn", true
					).setParameter(
						"selPlid", LayoutConstants.DEFAULT_PLID
					).buildString());
			}
		).add(
			() -> !isFirstColumn() && isPrivateLayoutsEnabled(),
			breadcrumbEntry -> {
				boolean privatePages = isPrivateLayout();

				if (selLayout != null) {
					privatePages = selLayout.isPrivateLayout();
				}

				breadcrumbEntry.setTitle(getTitle(privatePages));
				breadcrumbEntry.setURL(
					PortletURLBuilder.create(
						getPortletURL()
					).setParameter(
						"privateLayout", privatePages
					).setParameter(
						"selPlid", LayoutConstants.DEFAULT_PLID
					).buildString());
			}
		).addAll(
			() ->
				!isFirstColumn() &&
				(getSelPlid() != LayoutConstants.DEFAULT_PLID) &&
				(selLayout != null),
			() -> {
				List<Layout> layouts = selLayout.getAncestors();

				Collections.reverse(layouts);

				return TransformUtil.transform(
					layouts,
					layout -> BreadcrumbEntryBuilder.setTitle(
						layout.getName(themeDisplay.getLocale())
					).setURL(
						PortletURLBuilder.create(
							getPortletURL()
						).setParameter(
							"privateLayout", layout.isPrivateLayout()
						).setParameter(
							"selPlid", layout.getPlid()
						).buildString()
					).build());
			}
		).add(
			() ->
				!isFirstColumn() &&
				(getSelPlid() != LayoutConstants.DEFAULT_PLID) &&
				(selLayout != null),
			breadcrumbEntry -> breadcrumbEntry.setTitle(
				selLayout.getName(themeDisplay.getLocale()))
		).build();
	}

	public String getPortletResource() {
		String portletResource = ParamUtil.getString(
			httpServletRequest, "portletResource");

		if (Validator.isNull(portletResource)) {
			PortletDisplay portletDisplay = themeDisplay.getPortletDisplay();

			portletResource = portletDisplay.getPortletName();
		}

		return portletResource;
	}

	public PortletURL getPortletURL() {
		return PortletURLBuilder.createRenderURL(
			_liferayPortletResponse
		).setTabs1(
			getTabs1()
		).setParameter(
			"displayStyle",
			() -> {
				String displayStyle = getDisplayStyle();

				if (Validator.isNotNull(displayStyle)) {
					return displayStyle;
				}

				return null;
			}
		).setParameter(
			"privateLayout", isPrivateLayout()
		).buildPortletURL();
	}

	public String getPreviewCurrentDesignURL() {
		return PortletURLBuilder.createRenderURL(
			_liferayPortletResponse
		).setMVCPath(
			"/preview_current_design.jsp"
		).setRedirect(
			themeDisplay.getURLCurrent()
		).setParameter(
			"readOnly", true
		).setParameter(
			"selPlid",
			() -> {
				Layout selLayout = getSelLayout();

				if (selLayout.isDraftLayout()) {
					return selLayout.getClassPK();
				}

				return selLayout.getPlid();
			}
		).buildString();
	}

	public Map<String, Object> getProps() {
		return HashMapBuilder.<String, Object>put(
			"getFriendlyURLWarningURL", () -> _getFriendlyURLWarningURL()
		).put(
			"shouldCheckFriendlyURL", () -> _isShouldCheckFriendlyURL()
		).build();
	}

	public String getRedirect() {
		if (_redirect != null) {
			return _redirect;
		}

		_redirect = ParamUtil.getString(
			_liferayPortletRequest, "redirect", _getBackURL());

		return _redirect;
	}

	public List<BreadcrumbEntry> getRelativeBreadcrumbEntries(Layout layout) {
		return BreadcrumbEntryListBuilder.addAll(
			() -> {
				List<Layout> ancestorLayouts = layout.getAncestors();

				Collections.reverse(ancestorLayouts);

				return TransformUtil.transform(
					ancestorLayouts,
					ancestorLayout -> {
						if (LayoutPermissionUtil.contains(
								themeDisplay.getPermissionChecker(),
								ancestorLayout, ActionKeys.VIEW)) {

							return BreadcrumbEntryBuilder.setTitle(
								ancestorLayout.getName(themeDisplay.getLocale())
							).setURL(
								PortletURLBuilder.create(
									getPortletURL()
								).setParameter(
									"privateLayout",
									ancestorLayout.isPrivateLayout()
								).setParameter(
									"selPlid", ancestorLayout.getPlid()
								).buildString()
							).build();
						}

						return BreadcrumbEntryBuilder.setTitle(
							StringPool.TRIPLE_PERIOD
						).build();
					});
			}
		).add(
			breadcrumbEntry -> breadcrumbEntry.setTitle(
				layout.getName(themeDisplay.getLocale()))
		).build();
	}

	public String getRobots() {
		return ParamUtil.getString(
			httpServletRequest, "robots", _getStrictRobots());
	}

	public String getSelectFaviconEventName() {
		return _liferayPortletResponse.getNamespace() + "selectImage";
	}

	public String getSelectLayoutPageTemplateEntryURL(boolean privateLayout) {
		return getSelectLayoutPageTemplateEntryURL(0, privateLayout);
	}

	public String getSelectLayoutPageTemplateEntryURL(
		long layoutPageTemplateCollectionId, boolean privateLayout) {

		return getSelectLayoutPageTemplateEntryURL(
			layoutPageTemplateCollectionId, LayoutConstants.DEFAULT_PLID,
			privateLayout);
	}

	public String getSelectLayoutPageTemplateEntryURL(
		long layoutPageTemplateCollectionId, long selPlid,
		boolean privateLayout) {

		return getSelectLayoutPageTemplateEntryURL(
			layoutPageTemplateCollectionId, selPlid, "basic-templates",
			privateLayout);
	}

	public String getSelectLayoutPageTemplateEntryURL(
		long layoutPageTemplateCollectionId, long selPlid, String selectedTab,
		boolean privateLayout) {

		PortletURL selectLayoutPageTemplateEntryURL =
			PortletURLBuilder.createRenderURL(
				_liferayPortletResponse
			).setMVCRenderCommandName(
				"/layout_admin/select_layout_page_template_entry"
			).setRedirect(
				getRedirect()
			).setBackURL(
				_getBackURL()
			).setParameter(
				"groupId", getSelGroupId()
			).setParameter(
				"privateLayout", privateLayout
			).setParameter(
				"selPlid", selPlid
			).buildPortletURL();

		if (selPlid != LayoutConstants.DEFAULT_PLID) {
			Layout layout = LayoutLocalServiceUtil.fetchLayout(selPlid);

			if ((layout != null) && layout.isTypeEmpty()) {
				selectLayoutPageTemplateEntryURL.setParameter(
					"emptyLayout",
					String.valueOf(
						ParamUtil.getBoolean(
							httpServletRequest, "emptyLayout")));
				selectLayoutPageTemplateEntryURL.setParameter(
					"externalReferenceCode", layout.getExternalReferenceCode());
			}
		}

		if (layoutPageTemplateCollectionId > 0) {
			selectLayoutPageTemplateEntryURL.setParameter(
				"layoutPageTemplateCollectionId",
				String.valueOf(layoutPageTemplateCollectionId));
		}
		else if (Validator.isNotNull(selectedTab)) {
			selectLayoutPageTemplateEntryURL.setParameter(
				"selectedTab", selectedTab);
		}

		return selectLayoutPageTemplateEntryURL.toString();
	}

	public String getSelectThemeURL() {
		LayoutThemeItemSelectorCriterion layoutThemeItemSelectorCriterion =
			new LayoutThemeItemSelectorCriterion();

		layoutThemeItemSelectorCriterion.setDesiredItemSelectorReturnTypes(
			Collections.singletonList(new UUIDItemSelectorReturnType()));

		return String.valueOf(
			_itemSelector.getItemSelectorURL(
				RequestBackedPortletURLFactoryUtil.create(
					_liferayPortletRequest),
				_liferayPortletResponse.getNamespace() + "selectTheme",
				layoutThemeItemSelectorCriterion));
	}

	public Group getSelGroup() {
		return _groupDisplayContextHelper.getSelGroup();
	}

	public long getSelGroupId() {
		Group selGroup = getSelGroup();

		if (selGroup != null) {
			return selGroup.getGroupId();
		}

		return 0;
	}

	public Layout getSelLayout() {
		if (_selLayout != null) {
			return _selLayout;
		}

		if (getSelPlid() != LayoutConstants.DEFAULT_PLID) {
			_selLayout = LayoutLocalServiceUtil.fetchLayout(getSelPlid());
		}

		return _selLayout;
	}

	public LayoutSet getSelLayoutSet() {
		if (_selLayoutSet != null) {
			return _selLayoutSet;
		}

		Group group = getStagingGroup();

		if (group == null) {
			group = getLiveGroup();
		}

		_selLayoutSet = LayoutSetLocalServiceUtil.fetchLayoutSet(
			group.getGroupId(), isPrivateLayout());

		return _selLayoutSet;
	}

	public Long getSelPlid() {
		if (_selPlid != null) {
			return _selPlid;
		}

		_selPlid = ParamUtil.getLong(
			_liferayPortletRequest, "selPlid", LayoutConstants.DEFAULT_PLID);

		if ((_selPlid == 0) ||
			(!Objects.equals(
				ParamUtil.getString(
					httpServletRequest, "screenNavigationEntryKey"),
				LayoutScreenNavigationEntryConstants.ENTRY_KEY_DESIGN) &&
			 !Objects.equals(
				 httpServletRequest.getAttribute(
					 ScreenNavigationWebKeys.SELECTED_ENTRY_KEY),
				 LayoutScreenNavigationEntryConstants.ENTRY_KEY_DESIGN))) {

			return _selPlid;
		}

		Layout layout = LayoutLocalServiceUtil.fetchLayout(_selPlid);

		if (layout == null) {
			return _selPlid;
		}

		Layout draftLayout = layout.fetchDraftLayout();

		if (draftLayout != null) {
			_selPlid = draftLayout.getPlid();
		}

		return _selPlid;
	}

	public Group getStagingGroup() {
		return _groupDisplayContextHelper.getStagingGroup();
	}

	public Long getStagingGroupId() {
		return _groupDisplayContextHelper.getStagingGroupId();
	}

	public String getStyleBookWarningMessage() throws PortalException {
		LayoutSet publicLayoutSet = LayoutSetLocalServiceUtil.fetchLayoutSet(
			getSelGroupId(), false);

		String themeId = _getThemeId();

		if (Validator.isNull(themeId) ||
			Objects.equals(publicLayoutSet.getThemeId(), themeId) ||
			((_selLayout == null) && !_selLayoutSet.isPrivateLayout())) {

			return StringPool.BLANK;
		}

		if (_selLayout != null) {
			Group group = getGroup();

			if (group.isPrivateLayoutsEnabled()) {
				return LanguageUtil.get(
					httpServletRequest,
					"this-page-is-using-a-different-theme-than-the-one-set-" +
						"for-public-pages");
			}

			return LanguageUtil.get(
				httpServletRequest,
				"this-page-is-using-a-different-theme-than-the-one-set-for-" +
					"all-pages");
		}

		String url = PortletURLBuilder.createRenderURL(
			_liferayPortletResponse
		).setMVCRenderCommandName(
			"/layout_admin/edit_layout_set"
		).setRedirect(
			PortalUtil.getCurrentURL(httpServletRequest)
		).setBackURL(
			_backURL
		).setParameter(
			"groupId", themeDisplay.getScopeGroupId()
		).setParameter(
			"privateLayout", false
		).setWindowState(
			LiferayWindowState.MAXIMIZED
		).buildString();

		return LanguageUtil.format(
			httpServletRequest,
			"private-pages-is-using-a-different-theme-than-the-one-set-for-x-" +
				"public-pages-x",
			new String[] {"<a href =\"" + url + "\">", "</a>"});
	}

	public String getTabs1() {
		if (_tabs1 != null) {
			return _tabs1;
		}

		_tabs1 = ParamUtil.getString(_liferayPortletRequest, "tabs1", "pages");

		return _tabs1;
	}

	public String getTarget(Layout layout) {
		return HtmlUtil.escape(layout.getTypeSettingsProperty("target"));
	}

	public Map<String, Object> getThemeCSSReplacementSelectorProps()
		throws PortalException {

		String selectThemeCSSClientExtensionEventName =
			"selectThemeCSSClientExtension";

		LayoutSet setLayoutSet = getSelLayoutSet();

		String className = LayoutSet.class.getName();
		long classPK = setLayoutSet.getLayoutSetId();

		Layout selLayout = getSelLayout();

		if (selLayout != null) {
			className = Layout.class.getName();
			classPK = selLayout.getPlid();
		}

		ClientExtensionEntryRel clientExtensionEntryRel =
			ClientExtensionEntryRelLocalServiceUtil.
				fetchClientExtensionEntryRel(
					PortalUtil.getClassNameId(className), classPK,
					ClientExtensionEntryConstants.TYPE_THEME_CSS);

		return HashMapBuilder.<String, Object>put(
			"helpText", _getHelpText()
		).put(
			"isReadOnly", isReadOnly()
		).put(
			"placeholder", _getPlaceholder()
		).put(
			"selectThemeCSSClientExtensionEventName",
			selectThemeCSSClientExtensionEventName
		).put(
			"selectThemeCSSClientExtensionURL",
			() -> String.valueOf(
				getCETItemSelectorURL(
					false, selectThemeCSSClientExtensionEventName,
					ClientExtensionEntryConstants.TYPE_THEME_CSS))
		).put(
			"themeCSSCETExternalReferenceCode",
			() -> {
				if (clientExtensionEntryRel != null) {
					return clientExtensionEntryRel.
						getCETExternalReferenceCode();
				}

				return StringPool.BLANK;
			}
		).put(
			"themeCSSExtensionName",
			() -> {
				if (clientExtensionEntryRel != null) {
					CET cet = _cetManager.getCET(
						themeDisplay.getCompanyId(),
						clientExtensionEntryRel.getCETExternalReferenceCode());

					if (cet != null) {
						return cet.getName(themeDisplay.getLocale());
					}
				}

				return StringPool.BLANK;
			}
		).build();
	}

	public String getThemeFaviconCETExternalReferenceCode() {
		Layout selLayout = getSelLayout();

		if (selLayout != null) {
			ClientExtensionEntryRel clientExtensionEntryRel =
				ClientExtensionEntryRelLocalServiceUtil.
					fetchClientExtensionEntryRel(
						PortalUtil.getClassNameId(Layout.class),
						selLayout.getPlid(),
						ClientExtensionEntryConstants.TYPE_THEME_FAVICON);

			if (clientExtensionEntryRel != null) {
				return clientExtensionEntryRel.getCETExternalReferenceCode();
			}
		}

		LayoutSet setLayoutSet = getSelLayoutSet();

		ClientExtensionEntryRel clientExtensionEntryRel =
			ClientExtensionEntryRelLocalServiceUtil.
				fetchClientExtensionEntryRel(
					PortalUtil.getClassNameId(LayoutSet.class),
					setLayoutSet.getLayoutSetId(),
					ClientExtensionEntryConstants.TYPE_THEME_FAVICON);

		if (clientExtensionEntryRel != null) {
			return clientExtensionEntryRel.getCETExternalReferenceCode();
		}

		return StringPool.BLANK;
	}

	public String getTitle(boolean privatePages) {
		String title = "pages";

		if (isShowPublicLayouts() && isPrivateLayoutsEnabled()) {
			if (privatePages) {
				title = "private-pages";
			}
			else {
				title = "public-pages";
			}
		}

		return LanguageUtil.get(httpServletRequest, title);
	}

	public VerticalNavItemList getVerticalNavItemList(
		SelectLayoutPageTemplateEntryDisplayContext
			selectLayoutPageTemplateEntryDisplayContext) {

		VerticalNavItemList verticalNavItemList =
			VerticalNavItemListBuilder.add(
				verticalNavItem -> {
					verticalNavItem.setActive(
						selectLayoutPageTemplateEntryDisplayContext.
							isBasicTemplates());
					verticalNavItem.setHref(
						getSelectLayoutPageTemplateEntryURL(
							0, getSelPlid(), "basic-templates",
							isPrivateLayout()));

					String name = LanguageUtil.get(
						httpServletRequest, "basic-templates");

					verticalNavItem.setId(name);
					verticalNavItem.setLabel(name);
				}
			).add(
				verticalNavItem -> {
					verticalNavItem.setActive(
						selectLayoutPageTemplateEntryDisplayContext.
							isGlobalTemplates());
					verticalNavItem.setHref(
						getSelectLayoutPageTemplateEntryURL(
							0, getSelPlid(), "global-templates",
							isPrivateLayout()));

					String name = LanguageUtil.get(
						httpServletRequest, "global-templates");

					verticalNavItem.setId(name);
					verticalNavItem.setLabel(name);
				}
			).build();

		for (LayoutPageTemplateCollection layoutPageTemplateCollection :
				LayoutPageTemplateCollectionServiceUtil.
					getLayoutPageTemplateCollections(
						themeDisplay.getScopeGroupId(),
						LayoutPageTemplateEntryTypeConstants.BASIC)) {

			int layoutPageTemplateEntriesCount =
				LayoutPageTemplateEntryServiceUtil.
					getLayoutPageTemplateEntriesCount(
						themeDisplay.getScopeGroupId(),
						layoutPageTemplateCollection.
							getLayoutPageTemplateCollectionId(),
						WorkflowConstants.STATUS_APPROVED);

			if (layoutPageTemplateEntriesCount <= 0) {
				continue;
			}

			String name = layoutPageTemplateCollection.getName();

			verticalNavItemList.add(
				verticalNavItem -> {
					long layoutPageTemplateCollectionId =
						selectLayoutPageTemplateEntryDisplayContext.
							getLayoutPageTemplateCollectionId();

					if (layoutPageTemplateCollectionId ==
							layoutPageTemplateCollection.
								getLayoutPageTemplateCollectionId()) {

						verticalNavItem.setActive(true);
					}

					verticalNavItem.setHref(
						getSelectLayoutPageTemplateEntryURL(
							layoutPageTemplateCollection.
								getLayoutPageTemplateCollectionId(),
							getSelPlid(), isPrivateLayout()));
					verticalNavItem.setId(name);
					verticalNavItem.setLabel(name);
				});
		}

		return verticalNavItemList;
	}

	public String getViewLayoutURL(Layout layout) throws PortalException {
		String layoutFullURL = null;

		if (layout.isDenied() || layout.isPending()) {
			layoutFullURL = PortalUtil.getLayoutFullURL(
				layout.fetchDraftLayout(), themeDisplay);
		}
		else {
			layoutFullURL = PortalUtil.getLayoutFullURL(layout, themeDisplay);
		}

		if (layout.isTypeURL()) {
			return layoutFullURL;
		}

		try {
			layoutFullURL = HttpComponentsUtil.addParameters(
				layoutFullURL, "p_l_back_url", _getBackURL(layout),
				"p_l_back_url_title",
				LanguageUtil.get(httpServletRequest, "pages"));
		}
		catch (Exception exception) {
			_log.error(
				"Unable to generate view layout URL for " + layoutFullURL,
				exception);
		}

		return layoutFullURL;
	}

	public String getVirtualHostname() {
		LayoutSet layoutSet = getSelLayoutSet();

		if (layoutSet == null) {
			return StringPool.BLANK;
		}

		String virtualHostname = null;

		NavigableMap<String, String> virtualHostnames =
			PortalUtil.getVirtualHostnames(layoutSet);

		if (!virtualHostnames.isEmpty()) {
			virtualHostname = virtualHostnames.firstKey();
		}

		Group scopeGroup = themeDisplay.getScopeGroup();

		if (Validator.isNull(virtualHostname) && scopeGroup.isStagingGroup()) {
			Group liveGroup = scopeGroup.getLiveGroup();

			LayoutSet liveGroupLayoutSet = liveGroup.getPublicLayoutSet();

			if (layoutSet.isPrivateLayout()) {
				liveGroupLayoutSet = liveGroup.getPrivateLayoutSet();
			}

			virtualHostname = null;

			virtualHostnames = PortalUtil.getVirtualHostnames(
				liveGroupLayoutSet);

			if (!virtualHostnames.isEmpty()) {
				virtualHostname = virtualHostnames.firstKey();
			}
		}

		return virtualHostname;
	}

	public boolean hasEditableMasterLayout() {
		if (_hasEditableMasterLayout != null) {
			return _hasEditableMasterLayout;
		}

		boolean hasEditableMasterLayout = false;

		Layout selLayout = getSelLayout();

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			LayoutPageTemplateEntryLocalServiceUtil.
				fetchLayoutPageTemplateEntryByPlid(selLayout.getPlid());

		if (layoutPageTemplateEntry == null) {
			layoutPageTemplateEntry =
				LayoutPageTemplateEntryLocalServiceUtil.
					fetchLayoutPageTemplateEntryByPlid(selLayout.getClassPK());
		}

		if ((layoutPageTemplateEntry == null) ||
			!Objects.equals(
				layoutPageTemplateEntry.getType(),
				LayoutPageTemplateEntryTypeConstants.MASTER_LAYOUT)) {

			hasEditableMasterLayout = true;
		}

		_hasEditableMasterLayout = hasEditableMasterLayout;

		return _hasEditableMasterLayout;
	}

	public boolean hasLayouts() {
		if (_hasLayouts != null) {
			return _hasLayouts;
		}

		boolean hasLayouts = false;

		if ((_getLayoutsCount(true) > 0) || (_getLayoutsCount(false) > 0)) {
			hasLayouts = true;
		}

		_hasLayouts = hasLayouts;

		return _hasLayouts;
	}

	public boolean hasRequiredVocabularies() {
		long classNameId = PortalUtil.getClassNameId(Layout.class);

		List<AssetVocabulary> assetVocabularies =
			AssetVocabularyServiceUtil.getGroupVocabularies(_getGroupIds());

		for (AssetVocabulary assetVocabulary : assetVocabularies) {
			if (assetVocabulary.isAssociatedToClassNameId(classNameId) &&
				assetVocabulary.isRequired(
					classNameId, 0, themeDisplay.getScopeGroupId())) {

				return true;
			}
		}

		return false;
	}

	public boolean isClearFaviconButtonEnabled() {
		Layout selLayout = getSelLayout();

		if (selLayout != null) {
			if (selLayout.getFaviconFileEntryId() > 0) {
				return true;
			}

			ClientExtensionEntryRel clientExtensionEntryRel =
				ClientExtensionEntryRelLocalServiceUtil.
					fetchClientExtensionEntryRel(
						PortalUtil.getClassNameId(Layout.class),
						selLayout.getPlid(),
						ClientExtensionEntryConstants.TYPE_THEME_FAVICON);

			if (clientExtensionEntryRel != null) {
				return true;
			}

			return false;
		}

		LayoutSet selLayoutSet = getSelLayoutSet();

		if (selLayoutSet.getFaviconFileEntryId() > 0) {
			return true;
		}

		ClientExtensionEntryRel clientExtensionEntryRel =
			ClientExtensionEntryRelLocalServiceUtil.
				fetchClientExtensionEntryRel(
					PortalUtil.getClassNameId(LayoutSet.class),
					selLayoutSet.getLayoutSetId(),
					ClientExtensionEntryConstants.TYPE_THEME_FAVICON);

		if (clientExtensionEntryRel != null) {
			return true;
		}

		return false;
	}

	public boolean isConversionDraft(Layout layout) {
		if (Objects.equals(layout.getType(), LayoutConstants.TYPE_PORTLET) &&
			(layout.fetchDraftLayout() != null)) {

			return true;
		}

		return false;
	}

	public boolean isDraft() {
		Layout layout = getSelLayout();

		if (layout.isDraftLayout() && layout.isSystem()) {
			return true;
		}

		return false;
	}

	public boolean isFirstColumn() {
		if (_firstColumn != null) {
			return _firstColumn;
		}

		_firstColumn = ParamUtil.getBoolean(httpServletRequest, "firstColumn");

		return _firstColumn;
	}

	public boolean isLayoutPageTemplateEntry() {
		Layout layout = getSelLayout();

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			LayoutPageTemplateEntryLocalServiceUtil.
				fetchLayoutPageTemplateEntryByPlid(layout.getPlid());

		if (layout.isTypeAssetDisplay() ||
			((layoutPageTemplateEntry != null) && layout.isSystem())) {

			return true;
		}

		return false;
	}

	public boolean isPrivateLayout() {
		if (_privateLayout != null) {
			return _privateLayout;
		}

		Group selGroup = getSelGroup();

		if (selGroup.isLayoutSetPrototype()) {
			_privateLayout = true;

			return _privateLayout;
		}

		if (getSelLayout() != null) {
			Layout selLayout = getSelLayout();

			_privateLayout = selLayout.isPrivateLayout();

			return _privateLayout;
		}

		Layout layout = themeDisplay.getLayout();

		if (!layout.isTypeControlPanel()) {
			_privateLayout = layout.isPrivateLayout();

			return _privateLayout;
		}

		String privateLayoutString = _liferayPortletRequest.getParameter(
			"privateLayout");

		if (Validator.isNotNull(privateLayoutString)) {
			_privateLayout = GetterUtil.getBoolean(privateLayoutString);

			return _privateLayout;
		}

		Boolean privateLayout = false;

		if ((_getLayoutsCount(true) > 0) && (_getLayoutsCount(false) <= 0)) {
			privateLayout = true;
		}

		_privateLayout = privateLayout;

		return _privateLayout;
	}

	public boolean isPrivateLayoutsEnabled() {
		if (_privateLayoutsEnabled != null) {
			return _privateLayoutsEnabled;
		}

		Group group = getSelGroup();

		if (group.isPrivateLayoutsEnabled()) {
			_privateLayoutsEnabled = true;
		}
		else {
			_privateLayoutsEnabled = false;
		}

		return _privateLayoutsEnabled;
	}

	public boolean isReadOnly() {
		if (_readOnly != null) {
			return _readOnly;
		}

		_readOnly = ParamUtil.getBoolean(_liferayPortletRequest, "readOnly");

		return _readOnly;
	}

	public boolean isSearch() {
		return Validator.isNotNull(getKeywords());
	}

	public boolean isShowAddChildPageAction(Layout layout)
		throws PortalException {

		return _layoutActionsHelper.isShowAddChildPageAction(layout);
	}

	public boolean isShowAddRootLayoutButton() throws PortalException {
		return _layoutActionsHelper.isShowAddRootLayoutButton(getSelGroup());
	}

	public boolean isShowButtons() throws PortalException {
		Layout selLayout = getSelLayout();

		if ((selLayout.getGroupId() == getGroupId()) &&
			!(selLayout instanceof VirtualLayout) &&
			selLayout.isLayoutUpdateable() &&
			LayoutPermissionUtil.containsLayoutUpdatePermission(
				themeDisplay.getPermissionChecker(), selLayout)) {

			return true;
		}

		return false;
	}

	public boolean isShowCategorization() {
		long classNameId = PortalUtil.getClassNameId(Layout.class);

		List<AssetVocabulary> assetVocabularies =
			AssetVocabularyServiceUtil.getGroupVocabularies(_getGroupIds());

		for (AssetVocabulary assetVocabulary : assetVocabularies) {
			if (assetVocabulary.isAssociatedToClassNameId(classNameId) &&
				assetVocabulary.isRequired(
					classNameId, 0, themeDisplay.getScopeGroupId())) {

				int assetVocabularyCategoriesCount =
					AssetCategoryServiceUtil.getVocabularyCategoriesCount(
						assetVocabulary.getGroupId(),
						assetVocabulary.getVocabularyId());

				if (assetVocabularyCategoriesCount > 0) {
					return true;
				}
			}
		}

		return false;
	}

	public boolean isShowFirstColumnConfigureAction() throws PortalException {
		return GroupPermissionUtil.contains(
			themeDisplay.getPermissionChecker(), getSelGroupId(),
			ActionKeys.MANAGE_LAYOUTS);
	}

	public boolean isShowFriendlyURLWarningMessage() throws PortalException {
		return Validator.isNotNull(getFriendlyURLWarningMessage());
	}

	public boolean isShowPublicLayouts() {
		Group selGroup = getSelGroup();

		if (selGroup.isLayoutSetPrototype() || selGroup.isLayoutPrototype()) {
			return false;
		}

		return true;
	}

	public boolean isShowPublishedConfigurationMessage() {
		Layout selLayout = getSelLayout();

		UnicodeProperties typeSettingsUnicodeProperties =
			selLayout.getTypeSettingsProperties();

		return GetterUtil.getBoolean(
			typeSettingsUnicodeProperties.getProperty(
				LayoutTypeSettingsConstants.KEY_DESIGN_CONFIGURATION_MODIFIED));
	}

	public boolean isShowUserPrivateLayouts() throws PortalException {
		Group selGroup = getSelGroup();

		if (selGroup.isUser()) {
			if (!PrefsPropsUtil.getBoolean(
					themeDisplay.getCompanyId(),
					PropsKeys.LAYOUT_USER_PRIVATE_LAYOUTS_ENABLED)) {

				return false;
			}
			else if (PropsValues.
						LAYOUT_USER_PRIVATE_LAYOUTS_POWER_USER_REQUIRED) {

				boolean hasPowerUserRole = RoleLocalServiceUtil.hasUserRole(
					selGroup.getClassPK(), selGroup.getCompanyId(),
					RoleConstants.POWER_USER, true);

				if (!hasPowerUserRole) {
					return false;
				}
			}
		}

		if (!selGroup.isLayoutSetPrototype() &&
			!selGroup.isPrivateLayoutsEnabled()) {

			return false;
		}

		return true;
	}

	public boolean isURLAdvancedSettingsVisible() {
		Layout layout = getSelLayout();

		if (layout.isTypeAssetDisplay()) {
			return false;
		}

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			LayoutPageTemplateEntryLocalServiceUtil.
				fetchLayoutPageTemplateEntryByPlid(layout.getPlid());

		if (layoutPageTemplateEntry == null) {
			layoutPageTemplateEntry =
				LayoutPageTemplateEntryLocalServiceUtil.
					fetchLayoutPageTemplateEntryByPlid(layout.getClassPK());
		}

		if ((layoutPageTemplateEntry != null) &&
			Objects.equals(
				layoutPageTemplateEntry.getType(),
				LayoutPageTemplateEntryTypeConstants.MASTER_LAYOUT)) {

			return false;
		}

		return true;
	}

	protected List<String> getAvailableActions(Layout layout)
		throws PortalException {

		List<String> availableActions = new ArrayList<>();

		if (_layoutActionsHelper.isShowConvertLayoutAction(layout)) {
			availableActions.add("convertSelectedPages");
		}

		if (LayoutPermissionUtil.contains(
				themeDisplay.getPermissionChecker(), layout,
				ActionKeys.DELETE) &&
			_layoutActionsHelper.isShowDeleteAction(layout)) {

			availableActions.add("deleteSelectedPages");
		}

		if (_layoutActionsHelper.isShowExportTranslationAction(layout)) {
			availableActions.add("exportTranslation");
		}

		if (!layout.isTypeEmpty() &&
			LayoutPermissionUtil.contains(
				themeDisplay.getPermissionChecker(), layout,
				ActionKeys.PERMISSIONS)) {

			availableActions.add("changePermissions");
		}

		return availableActions;
	}

	protected boolean isActive(long plid) throws PortalException {
		if (plid == getSelPlid()) {
			return true;
		}

		Layout selLayout = getSelLayout();

		if (selLayout == null) {
			return false;
		}

		for (Layout layout : selLayout.getAncestors()) {
			if (plid == layout.getPlid()) {
				return true;
			}
		}

		return false;
	}

	protected final HttpServletRequest httpServletRequest;
	protected final ThemeDisplay themeDisplay;

	private String _getBackURL() {
		return _getBackURL(getSelLayout());
	}

	private String _getBackURL(Layout layout) {
		PortletURL portletURL = PortalUtil.getControlPanelPortletURL(
			_liferayPortletRequest, getGroup(),
			LayoutAdminPortletKeys.GROUP_PAGES, 0, 0,
			PortletRequest.RENDER_PHASE);

		if (layout != null) {
			portletURL.setParameter(
				"selPlid", String.valueOf(layout.getPlid()));
		}

		return portletURL.toString();
	}

	private String _getDefaultFaviconTitle() {
		Layout selLayout = getSelLayout();

		if (selLayout != null) {
			if (hasEditableMasterLayout() &&
				(selLayout.getMasterLayoutPlid() > 0)) {

				Layout masterLayout = LayoutLocalServiceUtil.fetchLayout(
					selLayout.getMasterLayoutPlid());

				if (masterLayout != null) {
					ClientExtensionEntryRel clientExtensionEntryRel =
						ClientExtensionEntryRelLocalServiceUtil.
							fetchClientExtensionEntryRel(
								PortalUtil.getClassNameId(Layout.class),
								selLayout.getPlid(),
								ClientExtensionEntryConstants.
									TYPE_THEME_FAVICON);

					if ((masterLayout.getFaviconFileEntryId() > 0) ||
						(clientExtensionEntryRel != null)) {

						return LanguageUtil.get(
							httpServletRequest, "favicon-from-master");
					}
				}
			}
			else {
				return FaviconUtil.getFaviconTitle(
					getSelLayoutSet(), themeDisplay.getLocale());
			}
		}

		return LanguageUtil.get(httpServletRequest, "favicon-from-theme");
	}

	private String _getDefaultFaviconURL() {
		Layout selLayout = getSelLayout();

		if (selLayout != null) {
			if (hasEditableMasterLayout() &&
				(selLayout.getMasterLayoutPlid() > 0)) {

				Layout masterLayout = LayoutLocalServiceUtil.fetchLayout(
					selLayout.getMasterLayoutPlid());

				if (masterLayout != null) {
					String faviconURL = FaviconUtil.getFaviconURL(
						_cetManager, masterLayout);

					if (Validator.isNotNull(faviconURL)) {
						return faviconURL;
					}
				}
			}
			else {
				String faviconURL = FaviconUtil.getFaviconURL(
					_cetManager, getSelLayoutSet());

				if (Validator.isNotNull(faviconURL)) {
					return faviconURL;
				}
			}
		}

		return themeDisplay.getPathThemeImages() + "/" +
			PropsUtil.get(PropsKeys.THEME_SHORTCUT_ICON);
	}

	private String _getDraftLayoutURL(Layout layout) throws Exception {
		return HttpComponentsUtil.addParameters(
			PortalUtil.getLayoutFullURL(getDraftLayout(layout), themeDisplay),
			"p_l_back_url", _getBackURL(layout), "p_l_back_url_title",
			LanguageUtil.get(httpServletRequest, "pages"), "p_l_mode",
			Constants.EDIT);
	}

	private String _getFriendlyURLWarningURL() {
		return ResourceURLBuilder.createResourceURL(
			_liferayPortletResponse
		).setParameter(
			"groupId", getGroupId()
		).setParameter(
			"plid", getSelPlid()
		).setParameter(
			"privateLayout", isPrivateLayout()
		).setResourceID(
			"/layout_admin/get_friendly_url_warning"
		).buildString();
	}

	private long[] _getGroupIds() {
		return PortalUtil.getCurrentAndAncestorSiteGroupIds(
			themeDisplay.getScopeGroupId());
	}

	private String _getHelpText() {
		Layout selLayout = getSelLayout();

		if (selLayout == null) {
			return null;
		}

		ClientExtensionEntryRel clientExtensionEntryRel =
			ClientExtensionEntryRelLocalServiceUtil.
				fetchClientExtensionEntryRel(
					PortalUtil.getClassNameId(Layout.class),
					selLayout.getMasterLayoutPlid(),
					ClientExtensionEntryConstants.TYPE_THEME_CSS);

		if (clientExtensionEntryRel == null) {
			LayoutSet selLayoutSet = getSelLayoutSet();

			clientExtensionEntryRel =
				ClientExtensionEntryRelLocalServiceUtil.
					fetchClientExtensionEntryRel(
						PortalUtil.getClassNameId(LayoutSet.class),
						selLayoutSet.getLayoutSetId(),
						ClientExtensionEntryConstants.TYPE_THEME_CSS);
		}

		if (clientExtensionEntryRel != null) {
			CET cet = _cetManager.getCET(
				themeDisplay.getCompanyId(),
				clientExtensionEntryRel.getCETExternalReferenceCode());

			return LanguageUtil.format(
				themeDisplay.getLocale(), "x-is-applied",
				cet.getName(themeDisplay.getLocale()));
		}

		return null;
	}

	private String _getLayoutMessage(Layout layout) throws PortalException {
		if (LayoutPermissionUtil.containsLayoutUpdatePermission(
				themeDisplay.getPermissionChecker(), layout)) {

			LinkTag linkTag = new LinkTag();

			linkTag.setCssClass("alert-link");
			linkTag.setHref(getConfigureLayoutURL(layout));
			linkTag.setLabel(
				HtmlUtil.escape(layout.getName(themeDisplay.getLocale())));

			try {
				String link = linkTag.doTagAsString(
					httpServletRequest,
					PortalUtil.getHttpServletResponse(_liferayPortletResponse));

				Group group = layout.getGroup();

				return LanguageUtil.format(
					themeDisplay.getLocale(), "page-x-of-x",
					new String[] {
						link.trim(), group.getName(themeDisplay.getLocale())
					},
					false);
			}
			catch (JspException jspException) {
				_log.error(jspException);
			}
		}

		Group group = layout.getGroup();

		return com.liferay.portal.kernel.util.StringUtil.
			appendParentheticalSuffix(
				LanguageUtil.format(
					themeDisplay.getLocale(), "page-x-of-x",
					new String[] {
						layout.getName(themeDisplay.getLocale()),
						group.getName(themeDisplay.getLocale())
					},
					false),
				LanguageUtil.get(
					themeDisplay.getLocale(),
					"please-contact-the-administrator-to-resolve-this-" +
						"friendly-url-conflict"));
	}

	private int _getLayoutsCount(boolean privateLayouts) {
		try {
			if (GroupPermissionUtil.contains(
					themeDisplay.getPermissionChecker(), getSelGroupId(),
					ActionKeys.MANAGE_LAYOUTS)) {

				return LayoutLocalServiceUtil.getLayoutsCount(
					getSelGroup(), privateLayouts, 0);
			}

			return LayoutServiceUtil.getLayoutsCount(
				getSelGroupId(), privateLayouts, 0);
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}
		}

		return 0;
	}

	private String _getOrderByCol() {
		if (Validator.isNotNull(_orderByCol)) {
			return _orderByCol;
		}

		String defaultOrderByCol = "create-date";

		if (isSearch()) {
			defaultOrderByCol = "relevance";
		}

		_orderByCol = SearchOrderByUtil.getOrderByCol(
			httpServletRequest, LayoutAdminPortletKeys.GROUP_PAGES,
			defaultOrderByCol);

		return _orderByCol;
	}

	private String _getOrderByType() {
		if (Objects.equals(_getOrderByCol(), "relevance")) {
			return "desc";
		}

		if (Validator.isNotNull(_orderByType)) {
			return _orderByType;
		}

		_orderByType = SearchOrderByUtil.getOrderByType(
			httpServletRequest, LayoutAdminPortletKeys.GROUP_PAGES, "asc");

		return _orderByType;
	}

	private String _getPlaceholder() throws PortalException {
		Layout selLayout = getSelLayout();

		if (selLayout != null) {
			ClientExtensionEntryRel clientExtensionEntryRel =
				ClientExtensionEntryRelLocalServiceUtil.
					fetchClientExtensionEntryRel(
						PortalUtil.getClassNameId(Layout.class),
						selLayout.getMasterLayoutPlid(),
						ClientExtensionEntryConstants.TYPE_THEME_CSS);

			if (clientExtensionEntryRel != null) {
				return LanguageUtil.get(
					themeDisplay.getLocale(),
					"theme-css-is-inherited-from-master");
			}
		}

		LayoutSet selLayoutSet = getSelLayoutSet();

		ClientExtensionEntryRel clientExtensionEntryRel =
			ClientExtensionEntryRelLocalServiceUtil.
				fetchClientExtensionEntryRel(
					PortalUtil.getClassNameId(LayoutSet.class),
					selLayoutSet.getLayoutSetId(),
					ClientExtensionEntryConstants.TYPE_THEME_CSS);

		if (clientExtensionEntryRel != null) {
			Group group = selLayoutSet.getGroup();

			return LanguageUtil.format(
				themeDisplay.getLocale(), "theme-css-is-inherited-from-x",
				group.getLayoutRootNodeName(
					selLayoutSet.isPrivateLayout(), themeDisplay.getLocale()),
				false);
		}

		return LanguageUtil.get(
			themeDisplay.getLocale(),
			"no-theme-css-client-extension-was-loaded");
	}

	private String _getStrictRobots() {
		LayoutSet layoutSet = getSelLayoutSet();

		if (layoutSet != null) {
			try {
				return GetterUtil.getString(
					layoutSet.getSettingsProperty(
						layoutSet.isPrivateLayout() + "-robots.txt"),
					StringUtil.read(
						RobotsUtil.class.getClassLoader(),
						PropsValues.ROBOTS_TXT_WITH_SITEMAP));
			}
			catch (IOException ioException) {
				_log.error(
					"Unable to read the content for " +
						PropsValues.ROBOTS_TXT_WITH_SITEMAP,
					ioException);
			}
		}

		try {
			return StringUtil.read(
				RobotsUtil.class.getClassLoader(),
				PropsValues.ROBOTS_TXT_WITHOUT_SITEMAP);
		}
		catch (IOException ioException) {
			_log.error(
				"Unable to read the content for " +
					PropsValues.ROBOTS_TXT_WITHOUT_SITEMAP,
				ioException);

			return null;
		}
	}

	private String _getThemeId() throws PortalException {
		if (_themeId != null) {
			return _themeId;
		}

		String themeId = ParamUtil.getString(httpServletRequest, "themeId");

		if (Validator.isNull(themeId)) {
			if (_selLayout == null) {
				themeId = _selLayoutSet.getThemeId();
			}
			else {
				Theme theme = _selLayout.getTheme();

				themeId = theme.getThemeId();
			}
		}

		_themeId = themeId;

		return _themeId;
	}

	private String[] _getTypes() {
		if (_types != null) {
			return _types;
		}

		_types = new String[] {
			LayoutConstants.TYPE_CONTENT, LayoutConstants.TYPE_EMBEDDED,
			LayoutConstants.TYPE_EMPTY, LayoutConstants.TYPE_LINK_TO_LAYOUT,
			LayoutConstants.TYPE_FULL_PAGE_APPLICATION,
			LayoutConstants.TYPE_NODE, LayoutConstants.TYPE_PANEL,
			LayoutConstants.TYPE_PORTLET, LayoutConstants.TYPE_URL
		};

		return _types;
	}

	private String _getWarningMessageHTML(
		String heading, List<String> layoutMessages) {

		StringBuilder sb = new StringBuilder();

		sb.append(heading);
		sb.append("<ul>");

		for (String layoutMessage : layoutMessages) {
			sb.append("<li>");
			sb.append(layoutMessage);
			sb.append("</li>");
		}

		sb.append("</ul>");

		return sb.toString();
	}

	private boolean _isShouldCheckFriendlyURL() {
		if (!FeatureFlagManagerUtil.isEnabled("LPS-174417")) {
			return false;
		}

		Group group = getGroup();

		if (group.isLayoutSetPrototype()) {
			return true;
		}

		LayoutSet layoutSet = getSelLayoutSet();

		return layoutSet.isLayoutSetPrototypeLinkEnabled();
	}

	private boolean _matchesHostname(
		StringBuilder friendlyURLBase,
		NavigableMap<String, String> virtualHostnames) {

		for (String virtualHostname : virtualHostnames.keySet()) {
			if (friendlyURLBase.indexOf(virtualHostname) != -1) {
				return true;
			}
		}

		return false;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		LayoutsAdminDisplayContext.class);

	private Long _activeLayoutSetBranchId;
	private String _backURL;
	private final CETManager _cetManager;
	private String _displayStyle;
	private Boolean _firstColumn;
	private final GroupDisplayContextHelper _groupDisplayContextHelper;
	private Boolean _hasEditableMasterLayout;
	private Boolean _hasLayouts;
	private final ItemSelector _itemSelector;
	private String _keywords;
	private final LayoutActionsHelper _layoutActionsHelper;
	private final LayoutLocalService _layoutLocalService;
	private final LayoutSetPrototypeHelper _layoutSetPrototypeHelper;
	private SearchContainer<Layout> _layoutsSearchContainer;
	private final LiferayPortletRequest _liferayPortletRequest;
	private final LiferayPortletResponse _liferayPortletResponse;
	private String _orderByCol;
	private String _orderByType;
	private Long _parentLayoutId;
	private Boolean _privateLayout;
	private Boolean _privateLayoutsEnabled;
	private Boolean _readOnly;
	private String _redirect;
	private Layout _selLayout;
	private LayoutSet _selLayoutSet;
	private Long _selPlid;
	private String _tabs1;
	private String _themeId;
	private String[] _types;
	private String _warningMessage;

}