/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.announcements.web.internal.display.context;

import com.liferay.announcements.constants.AnnouncementsPortletKeys;
import com.liferay.announcements.kernel.model.AnnouncementsEntry;
import com.liferay.announcements.kernel.model.AnnouncementsFlagConstants;
import com.liferay.announcements.kernel.service.AnnouncementsEntryLocalServiceUtil;
import com.liferay.announcements.web.internal.display.context.helper.AnnouncementsRequestHelper;
import com.liferay.announcements.web.internal.util.AnnouncementsUtil;
import com.liferay.petra.function.UnsafeBiFunction;
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.petra.function.UnsafeSupplier;
import com.liferay.petra.function.UnsafeTriFunction;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.Organization;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.UserGroup;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.service.OrganizationLocalServiceUtil;
import com.liferay.portal.kernel.service.RoleLocalServiceUtil;
import com.liferay.portal.kernel.service.UserGroupLocalServiceUtil;
import com.liferay.portal.kernel.service.permission.GroupPermissionUtil;
import com.liferay.portal.kernel.service.permission.OrganizationPermissionUtil;
import com.liferay.portal.kernel.service.permission.PortletPermissionUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.FastDateFormatFactoryUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.PrefsParamUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.service.permission.UserGroupPermissionUtil;
import com.liferay.segments.SegmentsEntryRetriever;
import com.liferay.segments.configuration.provider.SegmentsConfigurationProvider;
import com.liferay.segments.context.RequestContextMapper;
import com.liferay.segments.model.SegmentsEntryRole;
import com.liferay.segments.service.SegmentsEntryRoleLocalServiceUtil;

import jakarta.portlet.PortletPreferences;
import jakarta.portlet.PortletURL;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import jakarta.servlet.http.HttpServletRequest;

import java.text.DateFormat;
import java.text.Format;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * @author Adolfo Pérez
 * @author Roberto Díaz
 */
public class AnnouncementsDisplayContext {

	public AnnouncementsDisplayContext(
		AnnouncementsRequestHelper announcementsRequestHelper,
		HttpServletRequest httpServletRequest, String portletName,
		RenderRequest renderRequest, RenderResponse renderResponse,
		RequestContextMapper requestContextMapper,
		SegmentsEntryRetriever segmentsEntryRetriever,
		SegmentsConfigurationProvider segmentsConfigurationProvider) {

		_announcementsRequestHelper = announcementsRequestHelper;
		_httpServletRequest = httpServletRequest;
		_portletName = portletName;
		_renderRequest = renderRequest;
		_renderResponse = renderResponse;
		_requestContextMapper = requestContextMapper;
		_segmentsEntryRetriever = segmentsEntryRetriever;
		_segmentsConfigurationProvider = segmentsConfigurationProvider;

		_themeDisplay = (ThemeDisplay)httpServletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);
	}

	public LinkedHashMap<Long, long[]> getAnnouncementScopes()
		throws PortalException {

		if (_announcementScopes != null) {
			return _announcementScopes;
		}

		_announcementScopes = new LinkedHashMap<>();

		if (isCustomizeAnnouncementsDisplayed()) {
			long[] selectedScopeGroupIdsArray = ListUtil.toLongArray(
				_getSelectedScopeGroups(), Group.GROUP_ID_ACCESSOR);
			long[] selectedScopeOrganizationIdsArray = ListUtil.toLongArray(
				_getSelectedScopeOrganizations(),
				Organization.ORGANIZATION_ID_ACCESSOR);
			long[] selectedScopeRoleIdsArray = ListUtil.toLongArray(
				_getSelectedScopeRoles(), Role.ROLE_ID_ACCESSOR);
			long[] selectedScopeUserGroupIdsArray = ListUtil.toLongArray(
				_getSelectedScopeUserGroups(),
				UserGroup.USER_GROUP_ID_ACCESSOR);

			if (selectedScopeGroupIdsArray.length != 0) {
				_announcementScopes.put(
					PortalUtil.getClassNameId(Group.class.getName()),
					selectedScopeGroupIdsArray);
			}

			if (selectedScopeOrganizationIdsArray.length != 0) {
				_announcementScopes.put(
					PortalUtil.getClassNameId(Organization.class.getName()),
					selectedScopeOrganizationIdsArray);
			}

			if (selectedScopeRoleIdsArray.length != 0) {
				_announcementScopes.put(
					PortalUtil.getClassNameId(Role.class.getName()),
					selectedScopeRoleIdsArray);
			}

			if (selectedScopeUserGroupIdsArray.length != 0) {
				_announcementScopes.put(
					PortalUtil.getClassNameId(UserGroup.class.getName()),
					selectedScopeUserGroupIdsArray);
			}
		}
		else {
			LinkedHashMap<Long, long[]> announcementScopes =
				AnnouncementsUtil.getAnnouncementScopes(
					_announcementsRequestHelper.getUser());

			if (_segmentsConfigurationProvider.isRoleSegmentationEnabled(
					_announcementsRequestHelper.getCompanyId())) {

				long roleClassNameId = PortalUtil.getClassNameId(
					Role.class.getName());

				Set<Long> roleIds = SetUtil.fromArray(
					announcementScopes.get(roleClassNameId));

				long[] segmentsEntryIds =
					_segmentsEntryRetriever.getSegmentsEntryIds(
						_announcementsRequestHelper.getScopeGroupId(),
						_themeDisplay.getUserId(),
						_requestContextMapper.map(_httpServletRequest),
						new long[0]);

				for (long segmentsEntryId : segmentsEntryIds) {
					List<SegmentsEntryRole> segmentsEntryRoles =
						SegmentsEntryRoleLocalServiceUtil.getSegmentsEntryRoles(
							segmentsEntryId);

					for (SegmentsEntryRole segmentsEntryRole :
							segmentsEntryRoles) {

						roleIds.add(segmentsEntryRole.getRoleId());
					}
				}

				announcementScopes.put(
					roleClassNameId, ArrayUtil.toLongArray(roleIds));
			}

			_announcementScopes = announcementScopes;
		}

		_announcementScopes.put(0L, new long[] {0});

		return _announcementScopes;
	}

	public Format getDateFormat() {
		ThemeDisplay themeDisplay =
			_announcementsRequestHelper.getThemeDisplay();

		return FastDateFormatFactoryUtil.getDate(
			DateFormat.FULL, themeDisplay.getLocale(),
			themeDisplay.getTimeZone());
	}

	public List<Group> getGroups() throws PortalException {
		return _getModels(
			AnnouncementsUtil::getGroups, this::_getSelectedScopeGroups,
			GroupPermissionUtil::contains);
	}

	public List<Organization> getOrganizations() throws PortalException {
		return _getModels(
			AnnouncementsUtil::getOrganizations,
			this::_getSelectedScopeOrganizations,
			OrganizationPermissionUtil::contains);
	}

	public int getPageDelta() {
		PortletPreferences portletPreferences =
			_announcementsRequestHelper.getPortletPreferences();

		return GetterUtil.getInteger(
			portletPreferences.getValue(
				"pageDelta", String.valueOf(SearchContainer.DEFAULT_DELTA)));
	}

	public List<Role> getRoles() throws PortalException {
		return _getModels(
			AnnouncementsUtil::getRoles, this::_getSelectedScopeRoles,
			(permissionChecker, role, actionKey) ->
				AnnouncementsUtil.hasManageAnnouncementsPermission(
					role, permissionChecker));
	}

	public SearchContainer<AnnouncementsEntry> getSearchContainer()
		throws PortalException {

		if (_searchContainer != null) {
			return _searchContainer;
		}

		_searchContainer = new SearchContainer(
			_renderRequest, null, null, "cur1", getPageDelta(),
			_getPortletURL(), null, "no-entries-were-found");

		_searchContainer.setResultsAndTotal(
			() -> AnnouncementsEntryLocalServiceUtil.getEntries(
				_themeDisplay.getUserId(), getAnnouncementScopes(),
				_portletName.equals(AnnouncementsPortletKeys.ALERTS),
				_getFlag(), _searchContainer.getStart(),
				_searchContainer.getEnd()),
			AnnouncementsEntryLocalServiceUtil.getEntriesCount(
				_themeDisplay.getUserId(), getAnnouncementScopes(),
				_portletName.equals(AnnouncementsPortletKeys.ALERTS),
				_getFlag()));

		return _searchContainer;
	}

	public String getTabs1Names() {
		return "unread,read";
	}

	public String getTabs1PortletURL() {
		return PortletURLBuilder.createRenderURL(
			_announcementsRequestHelper.getLiferayPortletResponse()
		).setMVCRenderCommandName(
			"/announcements/view"
		).setTabs1(
			_announcementsRequestHelper.getTabs1()
		).buildString();
	}

	public List<UserGroup> getUserGroups() throws PortalException {
		return _getModels(
			AnnouncementsUtil::getUserGroups, this::_getSelectedScopeUserGroups,
			(permissionChecker, userGroup, actionKey) ->
				UserGroupPermissionUtil.contains(
					permissionChecker, userGroup.getUserGroupId(), actionKey));
	}

	public UUID getUuid() {
		return _UUID;
	}

	public boolean hasAddAnnouncementsEntryPermission() {
		try {
			if (GroupPermissionUtil.contains(
					_themeDisplay.getPermissionChecker(),
					_themeDisplay.getScopeGroupId(),
					ActionKeys.MANAGE_ANNOUNCEMENTS) ||
				PortletPermissionUtil.hasControlPanelAccessPermission(
					_themeDisplay.getPermissionChecker(),
					_themeDisplay.getScopeGroupId(),
					AnnouncementsPortletKeys.ANNOUNCEMENTS_ADMIN)) {

				return true;
			}
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}
		}

		return false;
	}

	public boolean isCustomizeAnnouncementsDisplayed() {
		String portletName = _announcementsRequestHelper.getPortletName();

		if (portletName.equals(AnnouncementsPortletKeys.ALERTS)) {
			return false;
		}

		Group scopeGroup = _announcementsRequestHelper.getScopeGroup();

		return PrefsParamUtil.getBoolean(
			_announcementsRequestHelper.getPortletPreferences(),
			_announcementsRequestHelper.getRequest(),
			"customizeAnnouncementsDisplayed", !scopeGroup.isUser());
	}

	public boolean isScopeGroupSelected(Group scopeGroup) throws JSONException {
		List<String> selectedScopeGroupExternalReferenceCodes =
			_getSelectedScopeGroupExternalReferenceCodes();

		return selectedScopeGroupExternalReferenceCodes.contains(
			scopeGroup.getExternalReferenceCode());
	}

	public boolean isScopeOrganizationSelected(Organization organization)
		throws JSONException {

		List<String> selectedScopeOrganizationExternalReferenceCodes =
			_getSelectedScopeOrganizationExternalReferenceCodes();

		return selectedScopeOrganizationExternalReferenceCodes.contains(
			organization.getExternalReferenceCode());
	}

	public boolean isScopeRoleSelected(Role role) throws JSONException {
		List<String> selectedScopeRoleExternalReferenceCodes =
			_getSelectedScopeRoleExternalReferenceCodes();

		return selectedScopeRoleExternalReferenceCodes.contains(
			role.getExternalReferenceCode());
	}

	public boolean isScopeUserGroupSelected(UserGroup userGroup)
		throws JSONException {

		List<String> selectedScopeUserGroupExternalReferenceCodes =
			_getSelectedScopeUserGroupExternalReferenceCodes();

		return selectedScopeUserGroupExternalReferenceCodes.contains(
			userGroup.getExternalReferenceCode());
	}

	public boolean isShowReadEntries() {
		String tabs1 = _announcementsRequestHelper.getTabs1();

		return tabs1.equals("read");
	}

	public boolean isShowScopeName() {
		String mvcRenderCommandName = ParamUtil.getString(
			_announcementsRequestHelper.getRequest(), "mvcRenderCommandName");

		return mvcRenderCommandName.equals("/announcements/edit_entry");
	}

	public boolean isTabs1Visible() {
		String portletName = _announcementsRequestHelper.getPortletName();

		ThemeDisplay themeDisplay =
			_announcementsRequestHelper.getThemeDisplay();

		try {
			if (!portletName.equals(AnnouncementsPortletKeys.ALERTS) ||
				(portletName.equals(AnnouncementsPortletKeys.ALERTS) &&
				 PortletPermissionUtil.hasControlPanelAccessPermission(
					 _announcementsRequestHelper.getPermissionChecker(),
					 themeDisplay.getScopeGroupId(),
					 AnnouncementsPortletKeys.ANNOUNCEMENTS_ADMIN))) {

				return true;
			}
		}
		catch (PortalException portalException) {
			_log.error(portalException);
		}

		return false;
	}

	private int _getFlag() {
		if (_flag != null) {
			return _flag;
		}

		_flag = isShowReadEntries() ? AnnouncementsFlagConstants.HIDDEN :
			AnnouncementsFlagConstants.NOT_HIDDEN;

		return _flag;
	}

	private <T> List<T> _getModels(
			UnsafeFunction<ThemeDisplay, List<T>, PortalException>
				unsafeFunction,
			UnsafeSupplier<List<T>, PortalException> unsafeSupplier,
			UnsafeTriFunction
				<PermissionChecker, T, String, Boolean, PortalException>
					unsafeTriFunction)
		throws PortalException {

		if (!isCustomizeAnnouncementsDisplayed() ||
			StringUtil.equals(
				_announcementsRequestHelper.getPortletId(),
				AnnouncementsPortletKeys.ANNOUNCEMENTS_ADMIN)) {

			return unsafeFunction.apply(
				_announcementsRequestHelper.getThemeDisplay());
		}

		List<T> selectedEntries = new ArrayList<>();

		for (T entry : unsafeSupplier.get()) {
			if (unsafeTriFunction.apply(
					_announcementsRequestHelper.getPermissionChecker(), entry,
					ActionKeys.MANAGE_ANNOUNCEMENTS)) {

				selectedEntries.add(entry);
			}
		}

		return selectedEntries;
	}

	private PortletURL _getPortletURL() {
		if (_portletURL != null) {
			return _portletURL;
		}

		_portletURL = PortletURLBuilder.createRenderURL(
			_renderResponse
		).setMVCRenderCommandName(
			"/announcements/view"
		).setTabs1(
			_announcementsRequestHelper.getTabs1()
		).buildPortletURL();

		return _portletURL;
	}

	private <T, E extends Exception> List<T> _getSelectedScopeEntries(
			List<String> externalReferenceCodes,
			UnsafeBiFunction<String, Long, T, E> unsafeBiFunction)
		throws E {

		return TransformUtil.transform(
			externalReferenceCodes,
			externalReferenceCode -> unsafeBiFunction.apply(
				externalReferenceCode,
				_announcementsRequestHelper.getCompanyId()));
	}

	private List<String> _getSelectedScopeExternalReferenceCodes(
			String param, String defaultValue)
		throws JSONException {

		return AnnouncementsUtil.toStringList(
			PrefsParamUtil.getString(
				_announcementsRequestHelper.getPortletPreferences(),
				_announcementsRequestHelper.getRequest(), param, defaultValue));
	}

	private List<String> _getSelectedScopeGroupExternalReferenceCodes()
		throws JSONException {

		Layout layout = _announcementsRequestHelper.getLayout();

		Group group = layout.getGroup();

		return _getSelectedScopeExternalReferenceCodes(
			"selectedScopeGroupExternalReferenceCodes",
			AnnouncementsUtil.toJSON(
				Collections.singletonList(group.getExternalReferenceCode())));
	}

	private List<Group> _getSelectedScopeGroups() throws PortalException {
		return _getSelectedScopeEntries(
			_getSelectedScopeGroupExternalReferenceCodes(),
			GroupLocalServiceUtil::getGroupByExternalReferenceCode);
	}

	private List<String> _getSelectedScopeOrganizationExternalReferenceCodes()
		throws JSONException {

		return _getSelectedScopeExternalReferenceCodes(
			"selectedScopeOrganizationExternalReferenceCodes",
			StringPool.BLANK);
	}

	private List<Organization> _getSelectedScopeOrganizations()
		throws PortalException {

		return _getSelectedScopeEntries(
			_getSelectedScopeOrganizationExternalReferenceCodes(),
			OrganizationLocalServiceUtil::
				getOrganizationByExternalReferenceCode);
	}

	private List<String> _getSelectedScopeRoleExternalReferenceCodes()
		throws JSONException {

		return _getSelectedScopeExternalReferenceCodes(
			"selectedScopeRoleExternalReferenceCodes", StringPool.BLANK);
	}

	private List<Role> _getSelectedScopeRoles() throws PortalException {
		return _getSelectedScopeEntries(
			_getSelectedScopeRoleExternalReferenceCodes(),
			RoleLocalServiceUtil::getRoleByExternalReferenceCode);
	}

	private List<String> _getSelectedScopeUserGroupExternalReferenceCodes()
		throws JSONException {

		return _getSelectedScopeExternalReferenceCodes(
			"selectedScopeUserGroupExternalReferenceCodes", StringPool.BLANK);
	}

	private List<UserGroup> _getSelectedScopeUserGroups()
		throws PortalException {

		return _getSelectedScopeEntries(
			_getSelectedScopeUserGroupExternalReferenceCodes(),
			UserGroupLocalServiceUtil::getUserGroupByExternalReferenceCode);
	}

	private static final UUID _UUID = UUID.fromString(
		"CD705D0E-7DB4-430C-9492-F1FA25ACE02E");

	private static final Log _log = LogFactoryUtil.getLog(
		AnnouncementsDisplayContext.class);

	private LinkedHashMap<Long, long[]> _announcementScopes;
	private final AnnouncementsRequestHelper _announcementsRequestHelper;
	private Integer _flag;
	private final HttpServletRequest _httpServletRequest;
	private final String _portletName;
	private PortletURL _portletURL;
	private final RenderRequest _renderRequest;
	private final RenderResponse _renderResponse;
	private final RequestContextMapper _requestContextMapper;
	private SearchContainer<AnnouncementsEntry> _searchContainer;
	private final SegmentsConfigurationProvider _segmentsConfigurationProvider;
	private final SegmentsEntryRetriever _segmentsEntryRetriever;
	private final ThemeDisplay _themeDisplay;

}