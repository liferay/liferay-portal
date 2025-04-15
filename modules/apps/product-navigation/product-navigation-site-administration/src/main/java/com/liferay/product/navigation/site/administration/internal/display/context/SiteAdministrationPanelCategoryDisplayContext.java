/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.product.navigation.site.administration.internal.display.context;

import com.liferay.application.list.GroupProvider;
import com.liferay.application.list.PanelCategory;
import com.liferay.application.list.constants.ApplicationListWebKeys;
import com.liferay.application.list.display.context.logic.PanelCategoryHelper;
import com.liferay.exportimport.kernel.exception.RemoteExportException;
import com.liferay.exportimport.kernel.staging.StagingUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutConstants;
import com.liferay.portal.kernel.model.Organization;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.impl.VirtualLayout;
import com.liferay.portal.kernel.portlet.LiferayWindowState;
import com.liferay.portal.kernel.portlet.PortletURLFactoryUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.LayoutLocalServiceUtil;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;
import com.liferay.portal.kernel.service.permission.GroupPermissionUtil;
import com.liferay.portal.kernel.theme.PortletDisplay;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.util.PropsValues;
import com.liferay.product.navigation.product.menu.constants.ProductNavigationProductMenuPortletKeys;
import com.liferay.product.navigation.product.menu.display.context.ProductMenuDisplayContext;
import com.liferay.product.navigation.site.administration.internal.application.list.SiteAdministrationPanelCategory;
import com.liferay.product.navigation.site.administration.internal.constants.SiteAdministrationWebKeys;
import com.liferay.site.manager.RecentGroupManager;
import com.liferay.site.provider.GroupURLProvider;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.RenderRequest;

import jakarta.servlet.http.HttpServletRequest;

import java.net.ConnectException;

import java.util.List;
import java.util.Objects;

/**
 * @author Julio Camarero
 */
public class SiteAdministrationPanelCategoryDisplayContext {

	public SiteAdministrationPanelCategoryDisplayContext(
		PortletRequest portletRequest, Group group) {

		_portletRequest = portletRequest;

		if (group != null) {
			_group = group;
		}

		_groupProvider = (GroupProvider)portletRequest.getAttribute(
			ApplicationListWebKeys.GROUP_PROVIDER);
		_groupURLProvider = (GroupURLProvider)portletRequest.getAttribute(
			SiteAdministrationWebKeys.GROUP_URL_PROVIDER);
		_panelCategory = (PanelCategory)portletRequest.getAttribute(
			ApplicationListWebKeys.PANEL_CATEGORY);
		_panelCategoryHelper = (PanelCategoryHelper)portletRequest.getAttribute(
			ApplicationListWebKeys.PANEL_CATEGORY_HELPER);
		_recentGroupManager = (RecentGroupManager)portletRequest.getAttribute(
			SiteAdministrationWebKeys.RECENT_GROUP_MANAGER);
		_themeDisplay = (ThemeDisplay)portletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);
	}

	public Group getGroup() {
		if (_group != null) {
			return _group;
		}

		_group = _groupProvider.getGroup(
			PortalUtil.getHttpServletRequest(_portletRequest));

		if (_group != null) {
			_updateLatentGroup(_group.getGroupId());
		}

		return _group;
	}

	public String getGroupName() throws PortalException {
		if (_groupName != null) {
			return _groupName;
		}

		Group group = getGroup();

		if (group == null) {
			_groupName = StringPool.BLANK;
		}
		else {
			if (group.isUser()) {
				if (group.getClassPK() == _themeDisplay.getUserId()) {
					_groupName = LanguageUtil.get(
						_themeDisplay.getRequest(), "my-site");
				}
				else {
					User user = UserLocalServiceUtil.getUser(
						group.getClassPK());

					_groupName = LanguageUtil.format(
						_themeDisplay.getLocale(), "x-site",
						user.getFullName());
				}
			}
			else {
				_groupName = group.getDescriptiveName(
					_themeDisplay.getLocale());
			}
		}

		return _groupName;
	}

	public String getGroupURL() {
		if (_groupURL != null) {
			return _groupURL;
		}

		_groupURL = StringPool.BLANK;

		return _groupURLProvider.getGroupURL(getGroup(), _portletRequest);
	}

	public String getLiveGroupLabel() {
		Group group = getGroup();

		if (group.isStagedRemotely()) {
			return "remote-live";
		}

		return "live";
	}

	public String getLiveGroupURL() throws RemoteExportException {
		if (_liveGroupURL != null) {
			return _liveGroupURL;
		}

		_liveGroupURL = StringPool.BLANK;

		Group group = getGroup();

		if (group.isStagedRemotely()) {
			Layout layout = _themeDisplay.getLayout();

			boolean privateLayout = layout.isPrivateLayout();

			if (layout instanceof VirtualLayout) {
				VirtualLayout virtualLayout = (VirtualLayout)layout;

				Group targetGroup = virtualLayout.getGroup();

				if (!targetGroup.hasPrivateLayouts()) {
					privateLayout = false;
				}
			}

			try {
				_liveGroupURL = StagingUtil.getRemoteSiteURL(
					group, privateLayout);
			}
			catch (PortalException portalException) {
				if (_log.isDebugEnabled()) {
					_log.debug("Unable to get live group URL", portalException);
				}

				_log.error(
					"Unable to get live group URL: " +
						portalException.getMessage());
			}
			catch (SystemException systemException) {
				Throwable throwable = systemException.getCause();

				if (!(throwable instanceof ConnectException)) {
					throw systemException;
				}

				_log.error(
					"Unable to connect to remote live: " +
						systemException.getMessage());

				if (_log.isDebugEnabled()) {
					_log.debug(systemException);
				}

				throw new RemoteExportException(
					RemoteExportException.BAD_CONNECTION);
			}
		}
		else if (group.isStagingGroup()) {
			Group liveGroup = StagingUtil.getLiveGroup(group.getGroupId());

			if (liveGroup != null) {
				_liveGroupURL = _groupURLProvider.getLiveGroupURL(
					liveGroup, _portletRequest);
			}
		}

		return _liveGroupURL;
	}

	public String getLogoURL() {
		if (Validator.isNotNull(_logoURL)) {
			return _logoURL;
		}

		_logoURL = StringPool.BLANK;

		Company company = _themeDisplay.getCompany();

		if (company.isSiteLogo()) {
			Group group = getGroup();

			if (group == null) {
				return _logoURL;
			}

			_logoURL = group.getLogoURL(_themeDisplay, false);
		}
		else {
			_logoURL = _themeDisplay.getCompanyLogo();
		}

		return _logoURL;
	}

	public List<Group> getMySites() throws PortalException {
		if (_mySites != null) {
			return _mySites;
		}

		User user = _themeDisplay.getUser();

		_mySites = user.getMySiteGroups(
			new String[] {
				Company.class.getName(), Group.class.getName(),
				Organization.class.getName()
			},
			PropsValues.MY_SITES_MAX_ELEMENTS);

		return _mySites;
	}

	public int getNotificationsCount() {
		if (_notificationsCount != null) {
			return _notificationsCount;
		}

		_notificationsCount = 0;

		Group group = getGroup();

		if (group == null) {
			return _notificationsCount;
		}

		SiteAdministrationPanelCategory siteAdministrationPanelCategory =
			(SiteAdministrationPanelCategory)_portletRequest.getAttribute(
				ApplicationListWebKeys.PANEL_CATEGORY);

		_notificationsCount = _panelCategoryHelper.getNotificationsCount(
			siteAdministrationPanelCategory.getKey(),
			_themeDisplay.getPermissionChecker(), getGroup(),
			_themeDisplay.getUser());

		return _notificationsCount;
	}

	public String getPageTreeURL() {
		return PortletURLBuilder.create(
			PortletURLFactoryUtil.create(
				_portletRequest,
				ProductNavigationProductMenuPortletKeys.
					PRODUCT_NAVIGATION_PRODUCT_MENU,
				RenderRequest.RENDER_PHASE)
		).setMVCPath(
			"/portlet/pages_tree.jsp"
		).setRedirect(
			ParamUtil.getString(
				_portletRequest, "redirect", _themeDisplay.getURLCurrent())
		).setBackURL(
			ParamUtil.getString(
				_portletRequest, "backURL", _themeDisplay.getURLCurrent())
		).setParameter(
			"selPpid",
			() -> {
				PortletDisplay portletDisplay =
					_themeDisplay.getPortletDisplay();

				return portletDisplay.getId();
			}
		).setWindowState(
			LiferayWindowState.EXCLUSIVE
		).buildString();
	}

	public PanelCategory getPanelCategory() {
		return _panelCategory;
	}

	public String getStagingGroupURL() {
		if (_stagingGroupURL != null) {
			return _stagingGroupURL;
		}

		_stagingGroupURL = StringPool.BLANK;

		Group group = getGroup();

		if (!group.isStagedRemotely() && group.hasStagingGroup()) {
			Group stagingGroup = StagingUtil.getStagingGroup(
				group.getGroupId());

			if (stagingGroup != null) {
				_stagingGroupURL = _groupURLProvider.getGroupURL(
					stagingGroup, _portletRequest);
			}
		}

		return _stagingGroupURL;
	}

	public String getStagingLabel() throws PortalException {
		if (_stagingLabel != null) {
			return _stagingLabel;
		}

		_stagingLabel = StringPool.BLANK;

		if (isShowStagingInfo()) {
			Group group = getGroup();

			if (group.isStagingGroup()) {
				_stagingLabel = "staging";
			}
			else if (group.hasStagingGroup()) {
				_stagingLabel = "live";
			}
		}

		return _stagingLabel;
	}

	public boolean isCollapsedPanel() throws PortalException {
		if (_collapsedPanel != null) {
			return _collapsedPanel;
		}

		ProductMenuDisplayContext productMenuDisplayContext =
			new ProductMenuDisplayContext(_portletRequest);

		_collapsedPanel = Objects.equals(
			_panelCategory.getKey(),
			productMenuDisplayContext.getRootPanelCategoryKey());

		return _collapsedPanel;
	}

	public boolean isDisplaySiteLink() {
		Group group = getGroup();

		Layout layout = _getFirstLayout(group);

		if ((layout == null) && group.isStaged()) {
			layout = _getFirstLayout(StagingUtil.getLiveGroup(group));
		}

		if (layout != null) {
			return true;
		}

		return false;
	}

	public boolean isFirstLayout() {
		Layout layout = _getFirstLayout(getGroup());

		if ((layout == null) || (layout.getPlid() != _themeDisplay.getPlid())) {
			return false;
		}

		return true;
	}

	public boolean isLayoutsTreeDisabled() throws PortalException {
		ProductMenuDisplayContext productMenuDisplayContext =
			new ProductMenuDisplayContext(_portletRequest);

		return productMenuDisplayContext.isLayoutsTreeDisabled();
	}

	public boolean isShowLayoutsTree() throws Exception {
		ProductMenuDisplayContext productMenuDisplayContext =
			new ProductMenuDisplayContext(_portletRequest);

		return productMenuDisplayContext.isShowLayoutsTree();
	}

	public boolean isShowSiteAdministration() throws PortalException {
		Group group = getGroup();

		if (group == null) {
			return false;
		}

		return GroupPermissionUtil.contains(
			_themeDisplay.getPermissionChecker(), group,
			ActionKeys.VIEW_SITE_ADMINISTRATION);
	}

	public boolean isShowSiteSelector() throws PortalException {
		List<Group> mySites = getMySites();

		if (!mySites.isEmpty()) {
			return true;
		}

		List<Group> recentSites = _recentGroupManager.getRecentGroups(
			PortalUtil.getHttpServletRequest(_portletRequest));

		return !recentSites.isEmpty();
	}

	public boolean isShowStagingInfo() throws PortalException {
		if (_showStagingInfo != null) {
			return _showStagingInfo;
		}

		_showStagingInfo = false;

		Group group = getGroup();

		if ((group == null) || (!group.isStaged() && !group.isStagingGroup())) {
			return _showStagingInfo;
		}

		if (!_hasStagingPermission()) {
			return _showStagingInfo;
		}

		_showStagingInfo = true;

		return _showStagingInfo;
	}

	private Layout _getFirstLayout(Group group) {
		Layout layout = LayoutLocalServiceUtil.fetchFirstLayout(
			group.getGroupId(), false, LayoutConstants.DEFAULT_PARENT_LAYOUT_ID,
			false);

		if ((layout != null) && !layout.isHidden()) {
			return layout;
		}

		layout = LayoutLocalServiceUtil.fetchFirstLayout(
			group.getGroupId(), true, LayoutConstants.DEFAULT_PARENT_LAYOUT_ID,
			false);

		if ((layout != null) && !layout.isHidden()) {
			return layout;
		}

		return null;
	}

	private boolean _hasStagingPermission() throws PortalException {
		if (GroupPermissionUtil.contains(
				_themeDisplay.getPermissionChecker(), getGroup(),
				ActionKeys.MANAGE_STAGING) ||
			GroupPermissionUtil.contains(
				_themeDisplay.getPermissionChecker(), getGroup(),
				ActionKeys.PUBLISH_STAGING) ||
			GroupPermissionUtil.contains(
				_themeDisplay.getPermissionChecker(), getGroup(),
				ActionKeys.VIEW_STAGING)) {

			return true;
		}

		return false;
	}

	private void _updateLatentGroup(long groupId) {
		if (groupId <= 0) {
			return;
		}

		HttpServletRequest httpServletRequest =
			PortalUtil.getHttpServletRequest(_portletRequest);

		_recentGroupManager.addRecentGroup(httpServletRequest, groupId);

		_groupProvider.setGroup(httpServletRequest, _group);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		SiteAdministrationPanelCategoryDisplayContext.class);

	private Boolean _collapsedPanel;
	private Group _group;
	private String _groupName;
	private final GroupProvider _groupProvider;
	private String _groupURL;
	private final GroupURLProvider _groupURLProvider;
	private String _liveGroupURL;
	private String _logoURL;
	private List<Group> _mySites;
	private Integer _notificationsCount;
	private final PanelCategory _panelCategory;
	private final PanelCategoryHelper _panelCategoryHelper;
	private final PortletRequest _portletRequest;
	private final RecentGroupManager _recentGroupManager;
	private Boolean _showStagingInfo;
	private String _stagingGroupURL;
	private String _stagingLabel;
	private final ThemeDisplay _themeDisplay;

}