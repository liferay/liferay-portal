/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.web.internal.display.context;

import com.liferay.fragment.constants.FragmentActionKeys;
import com.liferay.fragment.constants.FragmentPortletKeys;
import com.liferay.fragment.model.FragmentEntry;
import com.liferay.fragment.model.FragmentEntryLink;
import com.liferay.fragment.service.FragmentEntryLinkLocalServiceUtil;
import com.liferay.fragment.service.FragmentEntryLocalServiceUtil;
import com.liferay.fragment.util.comparator.FragmentEntryLinkLastPropagationDateComparator;
import com.liferay.fragment.web.internal.security.permission.resource.FragmentPermission;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.VerticalNavItemList;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.VerticalNavItemListBuilder;
import com.liferay.layout.page.template.constants.LayoutPageTemplateEntryTypeConstants;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryLocalServiceUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.search.EmptyOnClickRowChecker;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.portlet.SearchOrderByUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.service.LayoutLocalServiceUtil;
import com.liferay.portal.kernel.service.permission.LayoutPermissionUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.PortletURL;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.Objects;

/**
 * @author Pavel Savinov
 */
public class FragmentEntryLinkDisplayContext {

	public FragmentEntryLinkDisplayContext(
		HttpServletRequest httpServletRequest, RenderRequest renderRequest,
		RenderResponse renderResponse) {

		_httpServletRequest = httpServletRequest;
		_renderRequest = renderRequest;
		_renderResponse = renderResponse;
	}

	public int getAllUsageCount() throws PortalException {
		if (_allUsageCount != null) {
			return _allUsageCount;
		}

		if (isGroupUsages()) {
			_allUsageCount =
				FragmentEntryLinkLocalServiceUtil.
					getAllFragmentEntryLinksCountByFragmentEntry(
						_getGroupId(), getFragmentEntry());
		}
		else {
			_allUsageCount =
				FragmentEntryLinkLocalServiceUtil.
					getAllFragmentEntryLinksCountByFragmentEntry(
						getFragmentEntry());
		}

		return _allUsageCount;
	}

	public int getDisplayPagesUsageCount() throws PortalException {
		if (_displayPagesUsageCount != null) {
			return _displayPagesUsageCount;
		}

		if (isGroupUsages()) {
			_displayPagesUsageCount =
				FragmentEntryLinkLocalServiceUtil.
					getAllLayoutPageTemplateFragmentEntryLinksCountByFragmentEntry(
						_getGroupId(), getFragmentEntry(),
						LayoutPageTemplateEntryTypeConstants.DISPLAY_PAGE);
		}
		else {
			_displayPagesUsageCount =
				FragmentEntryLinkLocalServiceUtil.
					getAllLayoutPageTemplateFragmentEntryLinksCountByFragmentEntry(
						getFragmentEntry(),
						LayoutPageTemplateEntryTypeConstants.DISPLAY_PAGE);
		}

		return _displayPagesUsageCount;
	}

	public long getFragmentCollectionId() {
		if (Validator.isNotNull(_fragmentCollectionId)) {
			return _fragmentCollectionId;
		}

		_fragmentCollectionId = ParamUtil.getLong(
			_renderRequest, "fragmentCollectionId");

		return _fragmentCollectionId;
	}

	public FragmentEntry getFragmentEntry() throws PortalException {
		if (_fragmentEntry != null) {
			return _fragmentEntry;
		}

		_fragmentEntry = FragmentEntryLocalServiceUtil.getFragmentEntry(
			getFragmentEntryId());

		return _fragmentEntry;
	}

	public long getFragmentEntryId() {
		if (Validator.isNotNull(_fragmentEntryId)) {
			return _fragmentEntryId;
		}

		_fragmentEntryId = ParamUtil.getLong(_renderRequest, "fragmentEntryId");

		return _fragmentEntryId;
	}

	public String getFragmentEntryLinkName(
		FragmentEntryLink fragmentEntryLink) {

		Layout layout = _fetchLayout(fragmentEntryLink);

		if (layout == null) {
			return StringPool.BLANK;
		}

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			_fetchLayoutPageTemplateEntry(layout);

		ThemeDisplay themeDisplay = _getThemeDisplay();

		if (!_hasPermission(layout, themeDisplay.getPermissionChecker())) {
			return StringBundler.concat(
				StringPool.OPEN_BRACKET,
				LanguageUtil.get(themeDisplay.getLocale(), "restricted-page"),
				StringPool.CLOSE_BRACKET);
		}

		String name = layout.getName(themeDisplay.getLocale());

		if (layoutPageTemplateEntry != null) {
			name = layoutPageTemplateEntry.getName();
		}

		if (Validator.isNull(layout.getClassName()) &&
			(layout.getClassPK() <= 0)) {

			return name;
		}

		return StringBundler.concat(
			name, StringPool.SPACE, StringPool.OPEN_PARENTHESIS,
			LanguageUtil.get(themeDisplay.getLocale(), "draft"),
			StringPool.CLOSE_PARENTHESIS);
	}

	public String getFragmentEntryLinkTypeLabel(
		FragmentEntryLink fragmentEntryLink) {

		Layout layout = _fetchLayout(fragmentEntryLink);

		if (layout == null) {
			return StringPool.BLANK;
		}

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			_fetchLayoutPageTemplateEntry(layout);

		if (layoutPageTemplateEntry != null) {
			if (layoutPageTemplateEntry.getType() ==
					LayoutPageTemplateEntryTypeConstants.BASIC) {

				return "page-template";
			}
			else if (layoutPageTemplateEntry.getType() ==
						LayoutPageTemplateEntryTypeConstants.DISPLAY_PAGE) {

				return "display-page-template";
			}
			else if (layoutPageTemplateEntry.getType() ==
						LayoutPageTemplateEntryTypeConstants.MASTER_LAYOUT) {

				return "master-page";
			}
		}

		return "page";
	}

	public String getGroupName() {
		Group group = GroupLocalServiceUtil.fetchGroup(_getGroupId());

		if (group == null) {
			return StringPool.BLANK;
		}

		ThemeDisplay themeDisplay = _getThemeDisplay();

		try {
			return group.getDescriptiveName(themeDisplay.getLocale());
		}
		catch (PortalException portalException) {
			if (_log.isDebugEnabled()) {
				_log.debug(portalException);
			}

			return group.getName(themeDisplay.getLocale());
		}
	}

	public int getMasterPagesUsageCount() throws PortalException {
		if (_masterPagesUsageCount != null) {
			return _masterPagesUsageCount;
		}

		if (isGroupUsages()) {
			_masterPagesUsageCount =
				FragmentEntryLinkLocalServiceUtil.
					getAllLayoutPageTemplateFragmentEntryLinksCountByFragmentEntry(
						_getGroupId(), getFragmentEntry(),
						LayoutPageTemplateEntryTypeConstants.MASTER_LAYOUT);
		}
		else {
			_masterPagesUsageCount =
				FragmentEntryLinkLocalServiceUtil.
					getAllLayoutPageTemplateFragmentEntryLinksCountByFragmentEntry(
						getFragmentEntry(),
						LayoutPageTemplateEntryTypeConstants.MASTER_LAYOUT);
		}

		return _masterPagesUsageCount;
	}

	public String getNavigation() {
		if (Validator.isNotNull(_navigation)) {
			return _navigation;
		}

		_navigation = ParamUtil.getString(_renderRequest, "navigation", "all");

		return _navigation;
	}

	public String getOrderByCol() {
		if (Validator.isNotNull(_orderByCol)) {
			return _orderByCol;
		}

		_orderByCol = SearchOrderByUtil.getOrderByCol(
			_renderRequest, FragmentPortletKeys.FRAGMENT,
			"fragment-entry-link-order-by-col", "name");

		return _orderByCol;
	}

	public String getOrderByType() {
		if (Validator.isNotNull(_orderByType)) {
			return _orderByType;
		}

		_orderByType = SearchOrderByUtil.getOrderByType(
			_renderRequest, FragmentPortletKeys.FRAGMENT,
			"fragment-entry-link-order-by-type", "asc");

		return _orderByType;
	}

	public int getPageTemplatesUsageCount() throws PortalException {
		if (_pageTemplatesUsageCount != null) {
			return _pageTemplatesUsageCount;
		}

		if (isGroupUsages()) {
			_pageTemplatesUsageCount =
				FragmentEntryLinkLocalServiceUtil.
					getAllLayoutPageTemplateFragmentEntryLinksCountByFragmentEntry(
						_getGroupId(), getFragmentEntry(),
						LayoutPageTemplateEntryTypeConstants.BASIC);
		}
		else {
			_pageTemplatesUsageCount =
				FragmentEntryLinkLocalServiceUtil.
					getAllLayoutPageTemplateFragmentEntryLinksCountByFragmentEntry(
						getFragmentEntry(),
						LayoutPageTemplateEntryTypeConstants.BASIC);
		}

		return _pageTemplatesUsageCount;
	}

	public int getPagesUsageCount() throws PortalException {
		if (_pagesUsageCount != null) {
			return _pagesUsageCount;
		}

		if (isGroupUsages()) {
			_pagesUsageCount =
				FragmentEntryLinkLocalServiceUtil.
					getAllLayoutFragmentEntryLinksCountByFragmentEntry(
						_getGroupId(), getFragmentEntry());
		}
		else {
			_pagesUsageCount =
				FragmentEntryLinkLocalServiceUtil.
					getAllLayoutFragmentEntryLinksCountByFragmentEntry(
						getFragmentEntry());
		}

		return _pagesUsageCount;
	}

	public PortletURL getPortletURL() {
		return PortletURLBuilder.createRenderURL(
			_renderResponse
		).setMVCRenderCommandName(
			"/fragment/view_fragment_entry_usages"
		).setRedirect(
			getRedirect()
		).setNavigation(
			getNavigation()
		).setParameter(
			"fragmentCollectionId", getFragmentCollectionId()
		).setParameter(
			"fragmentEntryId", getFragmentEntryId()
		).setParameter(
			"groupId",
			() -> {
				if (isGroupUsages()) {
					return _getGroupId();
				}

				return null;
			}
		).buildPortletURL();
	}

	public String getRedirect() {
		if (_redirect != null) {
			return _redirect;
		}

		_redirect = ParamUtil.getString(_renderRequest, "redirect");

		return _redirect;
	}

	public SearchContainer<FragmentEntryLink> getSearchContainer()
		throws PortalException {

		if (_searchContainer != null) {
			return _searchContainer;
		}

		ThemeDisplay themeDisplay = _getThemeDisplay();

		SearchContainer<FragmentEntryLink> fragmentEntryLinksSearchContainer =
			new SearchContainer<>(
				_renderRequest, getPortletURL(), null,
				"there-are-no-fragment-usages");

		fragmentEntryLinksSearchContainer.setId(
			"fragmentEntryLinks" + getNavigation());

		if (!isGroupUsages() &&
			FragmentPermission.contains(
				themeDisplay.getPermissionChecker(),
				themeDisplay.getScopeGroupId(),
				FragmentActionKeys.MANAGE_FRAGMENT_ENTRIES)) {

			fragmentEntryLinksSearchContainer.setRowChecker(
				new EmptyOnClickRowChecker(_renderResponse));
		}

		boolean orderByAsc = false;

		if (Objects.equals(getOrderByType(), "asc")) {
			orderByAsc = true;
		}

		fragmentEntryLinksSearchContainer.setOrderByCol(getOrderByCol());
		fragmentEntryLinksSearchContainer.setOrderByComparator(
			FragmentEntryLinkLastPropagationDateComparator.getInstance(
				orderByAsc));
		fragmentEntryLinksSearchContainer.setOrderByType(getOrderByType());

		fragmentEntryLinksSearchContainer.setResultsAndTotal(
			() -> _getFragmentEntryLinks(
				fragmentEntryLinksSearchContainer.getStart(),
				fragmentEntryLinksSearchContainer.getEnd(),
				fragmentEntryLinksSearchContainer.getOrderByComparator()),
			_getFragmentEntryLinksCount());

		_searchContainer = fragmentEntryLinksSearchContainer;

		return _searchContainer;
	}

	public VerticalNavItemList getVerticalNavItemList() {
		return VerticalNavItemListBuilder.add(
			verticalNavItem -> {
				String name = LanguageUtil.format(
					_httpServletRequest, "all-x", getAllUsageCount());

				verticalNavItem.setActive(
					Objects.equals(getNavigation(), "all"));
				verticalNavItem.setHref(
					PortletURLBuilder.create(
						getPortletURL()
					).setNavigation(
						"all"
					).buildString());
				verticalNavItem.setId(name);
				verticalNavItem.setLabel(name);
			}
		).add(
			verticalNavItem -> {
				String name = LanguageUtil.format(
					_httpServletRequest, "pages-x", getPagesUsageCount());

				verticalNavItem.setActive(
					Objects.equals(getNavigation(), "pages"));
				verticalNavItem.setHref(
					PortletURLBuilder.create(
						getPortletURL()
					).setNavigation(
						"pages"
					).buildString());
				verticalNavItem.setId(name);
				verticalNavItem.setLabel(name);
			}
		).add(
			verticalNavItem -> {
				String name = LanguageUtil.format(
					_httpServletRequest, "master-pages-x",
					getMasterPagesUsageCount());

				verticalNavItem.setActive(
					Objects.equals(getNavigation(), "master-pages"));
				verticalNavItem.setHref(
					PortletURLBuilder.create(
						getPortletURL()
					).setNavigation(
						"master-pages"
					).buildString());
				verticalNavItem.setId(name);
				verticalNavItem.setLabel(name);
			}
		).add(
			verticalNavItem -> {
				String name = LanguageUtil.format(
					_httpServletRequest, "page-templates-x",
					getPageTemplatesUsageCount());

				verticalNavItem.setActive(
					Objects.equals(getNavigation(), "page-templates"));
				verticalNavItem.setHref(
					PortletURLBuilder.create(
						getPortletURL()
					).setNavigation(
						"page-templates"
					).buildString());
				verticalNavItem.setId(name);
				verticalNavItem.setLabel(name);
			}
		).add(
			verticalNavItem -> {
				String name = LanguageUtil.format(
					_httpServletRequest, "display-page-templates-x",
					getDisplayPagesUsageCount());

				verticalNavItem.setActive(
					Objects.equals(getNavigation(), "display-page-templates"));
				verticalNavItem.setHref(
					PortletURLBuilder.create(
						getPortletURL()
					).setNavigation(
						"display-page-templates"
					).buildString());
				verticalNavItem.setId(name);
				verticalNavItem.setLabel(name);
			}
		).build();
	}

	public boolean isGroupUsages() {
		if (_getGroupId() > 0) {
			return true;
		}

		return false;
	}

	private Layout _fetchLayout(FragmentEntryLink fragmentEntryLink) {
		Layout layout = LayoutLocalServiceUtil.fetchLayout(
			fragmentEntryLink.getPlid());

		if ((layout == null) && _log.isWarnEnabled()) {
			_log.warn(
				StringBundler.concat(
					"Unable to find layout with PLID ",
					fragmentEntryLink.getPlid(), " for fragment entry link ",
					fragmentEntryLink.getFragmentEntryLinkId()));
		}

		return layout;
	}

	private LayoutPageTemplateEntry _fetchLayoutPageTemplateEntry(
		Layout layout) {

		long layoutPageTemplateEntryPlid = layout.getPlid();

		if (layout.isDraftLayout()) {
			layoutPageTemplateEntryPlid = layout.getClassPK();
		}

		return LayoutPageTemplateEntryLocalServiceUtil.
			fetchLayoutPageTemplateEntryByPlid(layoutPageTemplateEntryPlid);
	}

	private List<FragmentEntryLink> _getFragmentEntryLinks(
			int start, int end,
			OrderByComparator<FragmentEntryLink> orderByComparator)
		throws PortalException {

		FragmentEntry fragmentEntry = getFragmentEntry();
		String navigation = getNavigation();

		if (Objects.equals(navigation, "pages")) {
			if (isGroupUsages()) {
				return FragmentEntryLinkLocalServiceUtil.
					getAllLayoutFragmentEntryLinksByFragmentEntry(
						_getGroupId(), fragmentEntry, start, end,
						orderByComparator);
			}

			return FragmentEntryLinkLocalServiceUtil.
				getAllLayoutFragmentEntryLinksByFragmentEntry(
					fragmentEntry, start, end, orderByComparator);
		}

		int layoutPageTemplateType = _getLayoutPageTemplateType(navigation);

		if (layoutPageTemplateType >= 0) {
			if (isGroupUsages()) {
				return FragmentEntryLinkLocalServiceUtil.
					getAllLayoutPageTemplateFragmentEntryLinksByFragmentEntry(
						_getGroupId(), fragmentEntry, layoutPageTemplateType,
						start, end, orderByComparator);
			}

			return FragmentEntryLinkLocalServiceUtil.
				getAllLayoutPageTemplateFragmentEntryLinksByFragmentEntry(
					fragmentEntry, layoutPageTemplateType, start, end,
					orderByComparator);
		}

		if (isGroupUsages()) {
			return FragmentEntryLinkLocalServiceUtil.
				getAllFragmentEntryLinksByFragmentEntry(
					_getGroupId(), fragmentEntry, start, end,
					orderByComparator);
		}

		return FragmentEntryLinkLocalServiceUtil.
			getAllFragmentEntryLinksByFragmentEntry(
				fragmentEntry, start, end, orderByComparator);
	}

	private int _getFragmentEntryLinksCount() throws PortalException {
		String navigation = getNavigation();

		if (Objects.equals(navigation, "display-page-templates")) {
			return getDisplayPagesUsageCount();
		}
		else if (Objects.equals(navigation, "master-pages")) {
			return getMasterPagesUsageCount();
		}
		else if (Objects.equals(navigation, "page-templates")) {
			return getPageTemplatesUsageCount();
		}
		else if (Objects.equals(navigation, "pages")) {
			return getPagesUsageCount();
		}

		return getAllUsageCount();
	}

	private long _getGroupId() {
		if (_groupId != null) {
			return _groupId;
		}

		_groupId = ParamUtil.getLong(_renderRequest, "groupId");

		return _groupId;
	}

	private int _getLayoutPageTemplateType(String navigation) {
		if (Objects.equals(navigation, "display-page-templates")) {
			return LayoutPageTemplateEntryTypeConstants.DISPLAY_PAGE;
		}
		else if (Objects.equals(navigation, "master-pages")) {
			return LayoutPageTemplateEntryTypeConstants.MASTER_LAYOUT;
		}
		else if (Objects.equals(navigation, "page-templates")) {
			return LayoutPageTemplateEntryTypeConstants.BASIC;
		}

		return -1;
	}

	private ThemeDisplay _getThemeDisplay() {
		if (_themeDisplay != null) {
			return _themeDisplay;
		}

		_themeDisplay = (ThemeDisplay)_renderRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		return _themeDisplay;
	}

	private boolean _hasPermission(
		Layout layout, PermissionChecker permissionChecker) {

		long plid = layout.getPlid();

		if (layout.isDraftLayout()) {
			plid = layout.getClassPK();
		}

		try {
			return LayoutPermissionUtil.contains(
				permissionChecker, plid, ActionKeys.VIEW);
		}
		catch (PortalException portalException) {
			if (_log.isDebugEnabled()) {
				_log.debug(portalException);
			}

			return false;
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		FragmentEntryLinkDisplayContext.class);

	private Integer _allUsageCount;
	private Integer _displayPagesUsageCount;
	private Long _fragmentCollectionId;
	private FragmentEntry _fragmentEntry;
	private Long _fragmentEntryId;
	private Long _groupId;
	private final HttpServletRequest _httpServletRequest;
	private Integer _masterPagesUsageCount;
	private String _navigation;
	private String _orderByCol;
	private String _orderByType;
	private Integer _pageTemplatesUsageCount;
	private Integer _pagesUsageCount;
	private String _redirect;
	private final RenderRequest _renderRequest;
	private final RenderResponse _renderResponse;
	private SearchContainer<FragmentEntryLink> _searchContainer;
	private ThemeDisplay _themeDisplay;

}