/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.staging.internal;

import com.liferay.exportimport.kernel.lar.ExportImportHelper;
import com.liferay.exportimport.kernel.lar.PortletDataHandler;
import com.liferay.exportimport.kernel.staging.StagingURLHelper;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.lang.ThreadContextClassLoaderUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.auth.HttpPrincipal;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.PortalClassLoaderUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.service.http.GroupServiceHttp;
import com.liferay.staging.StagingGroupHelper;
import com.liferay.staging.internal.constants.CompanyGroupConstants;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Akos Thurzo
 */
@Component(service = StagingGroupHelper.class)
public class StagingGroupHelperImpl implements StagingGroupHelper {

	@Override
	public Group fetchCompanyGroup(long companyId) {
		if (!FeatureFlagManagerUtil.isEnabled(companyId, "LPD-35914")) {
			return null;
		}

		return _groupLocalService.fetchFriendlyURLGroup(
			companyId, CompanyGroupConstants.FRIENDLY_URL);
	}

	@Override
	public Group fetchLiveGroup(Group group) {
		if (isLocalStagingGroup(group)) {
			return fetchLocalLiveGroup(group);
		}

		if (isRemoteStagingGroup(group)) {
			return fetchRemoteLiveGroup(group);
		}

		return null;
	}

	@Override
	public Group fetchLiveGroup(long groupId) {
		return fetchLiveGroup(_groupLocalService.fetchGroup(groupId));
	}

	@Override
	public Group fetchLocalLiveGroup(Group group) {
		if (!isLocalStagingGroup(group)) {
			return null;
		}

		group = _getParentGroupForScopeGroup(group);

		return _groupLocalService.fetchGroup(group.getLiveGroupId());
	}

	@Override
	public Group fetchLocalLiveGroup(long groupId) {
		return fetchLocalLiveGroup(_groupLocalService.fetchGroup(groupId));
	}

	@Override
	public Group fetchLocalStagingGroup(Group group) {
		if (!isLocalLiveGroup(group)) {
			return null;
		}

		group = _getParentGroupForScopeGroup(group);

		return _groupLocalService.fetchStagingGroup(group.getGroupId());
	}

	@Override
	public Group fetchLocalStagingGroup(long groupId) {
		return fetchLocalStagingGroup(_groupLocalService.fetchGroup(groupId));
	}

	@Override
	public Group fetchRemoteLiveGroup(Group group) {
		if (!isRemoteStagingGroup(group)) {
			return null;
		}

		group = _getParentGroupForScopeGroup(group);

		try (SafeCloseable safeCloseable = ThreadContextClassLoaderUtil.swap(
				PortalClassLoaderUtil.getClassLoader())) {

			PermissionChecker permissionChecker =
				PermissionThreadLocal.getPermissionChecker();

			User user = permissionChecker.getUser();

			String remoteURL = _stagingURLHelper.buildRemoteURL(
				group.getTypeSettingsProperties());

			HttpPrincipal httpPrincipal = new HttpPrincipal(
				remoteURL, user.getLogin(), user.getPassword(),
				user.isPasswordEncrypted());

			long remoteGroupId = GetterUtil.getLong(
				_getTypeSettingsProperty(group, "remoteGroupId"));

			return GroupServiceHttp.getGroup(httpPrincipal, remoteGroupId);
		}
		catch (PortalException portalException) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					"Unable to get remote live group: " +
						portalException.getMessage());
			}

			return null;
		}
	}

	@Override
	public Group fetchRemoteLiveGroup(long groupId) {
		return fetchRemoteLiveGroup(_groupLocalService.fetchGroup(groupId));
	}

	@Override
	public Group getStagedPortletGroup(Group group, String portletId) {
		if (isLocalStagingGroup(group.getGroupId()) &&
			!isStagedPortlet(group.getGroupId(), portletId)) {

			return group.getLiveGroup();
		}

		return group;
	}

	@Override
	public long getStagedPortletGroupId(long groupId, String portletId) {
		if (!isStagedPortlet(groupId, portletId) &&
			!isRemoteStagingGroup(groupId)) {

			Group liveGroup = fetchLiveGroup(groupId);

			if (liveGroup != null) {
				return liveGroup.getGroupId();
			}
		}

		return groupId;
	}

	@Override
	public boolean isCompanyGroup(Group group) {
		return isCompanyGroup(group.getCompanyId(), group.getGroupId());
	}

	@Override
	public boolean isCompanyGroup(long companyId, long groupId) {
		Group companyGroup = fetchCompanyGroup(companyId);

		if ((companyGroup != null) && (companyGroup.getGroupId() == groupId)) {
			return true;
		}

		return false;
	}

	@Override
	public boolean isCompanyGroupFriendlyURL(String friendlyURL) {
		return Objects.equals(friendlyURL, CompanyGroupConstants.FRIENDLY_URL);
	}

	@Override
	public boolean isDepotGroup(long groupId) {
		Group group = _groupLocalService.fetchGroup(groupId);

		return group.isDepot();
	}

	@Override
	public boolean isLiveGroup(Group group) {
		if (isLocalLiveGroup(group) || isRemoteLiveGroup(group)) {
			return true;
		}

		return false;
	}

	@Override
	public boolean isLiveGroup(long groupId) {
		return isLiveGroup(_groupLocalService.fetchGroup(groupId));
	}

	@Override
	public boolean isLocalLiveGroup(Group group) {
		if (group == null) {
			return false;
		}

		group = _getParentGroupForScopeGroup(group);

		Group stagingGroup = _groupLocalService.fetchStagingGroup(
			group.getGroupId());

		if (stagingGroup == null) {
			return false;
		}

		return true;
	}

	@Override
	public boolean isLocalLiveGroup(long groupId) {
		return isLocalLiveGroup(_groupLocalService.fetchGroup(groupId));
	}

	@Override
	public boolean isLocalStagingGroup(Group group) {
		if (group == null) {
			return false;
		}

		group = _getParentGroupForScopeGroup(group);

		if (group.getLiveGroupId() == GroupConstants.DEFAULT_LIVE_GROUP_ID) {
			return false;
		}

		return true;
	}

	@Override
	public boolean isLocalStagingGroup(long groupId) {
		return isLocalStagingGroup(_groupLocalService.fetchGroup(groupId));
	}

	@Override
	public boolean isLocalStagingOrLocalLiveGroup(Group group) {
		if (isLocalStagingGroup(group) || isLocalLiveGroup(group)) {
			return true;
		}

		return false;
	}

	@Override
	public boolean isLocalStagingOrLocalLiveGroup(long groupId) {
		return isLocalStagingOrLocalLiveGroup(
			_groupLocalService.fetchGroup(groupId));
	}

	@Override
	public boolean isRemoteLiveGroup(Group group) {
		if (group == null) {
			return false;
		}

		group = _getParentGroupForScopeGroup(group);

		if (group.getRemoteStagingGroupCount() > 0) {
			return true;
		}

		return false;
	}

	@Override
	public boolean isRemoteLiveGroup(long groupId) {
		return isRemoteLiveGroup(_groupLocalService.fetchGroup(groupId));
	}

	@Override
	public boolean isRemoteStagingGroup(Group group) {
		if (group == null) {
			return false;
		}

		group = _getParentGroupForScopeGroup(group);

		return GetterUtil.getBoolean(
			_getTypeSettingsProperty(group, "stagedRemotely"));
	}

	@Override
	public boolean isRemoteStagingGroup(long groupId) {
		return isRemoteStagingGroup(_groupLocalService.fetchGroup(groupId));
	}

	@Override
	public boolean isRemoteStagingOrRemoteLiveGroup(Group group) {
		if (isRemoteStagingGroup(group) || isRemoteLiveGroup(group)) {
			return true;
		}

		return false;
	}

	@Override
	public boolean isRemoteStagingOrRemoteLiveGroup(long groupId) {
		return isRemoteStagingOrRemoteLiveGroup(
			_groupLocalService.fetchGroup(groupId));
	}

	@Override
	public boolean isStagedPortlet(Group group, String portletId) {
		if (!isStagingGroup(group) && !isLiveGroup(group)) {
			return false;
		}

		return group.isStagedPortlet(portletId);
	}

	@Override
	public boolean isStagedPortlet(long groupId, String portletId) {
		return isStagedPortlet(
			_groupLocalService.fetchGroup(groupId), portletId);
	}

	@Override
	public boolean isStagedPortletData(long groupId, String className) {
		Group group = _groupLocalService.fetchGroup(groupId);

		if (group == null) {
			return true;
		}

		List<Portlet> dataSiteLevelPortlets = Collections.emptyList();

		try {
			dataSiteLevelPortlets =
				_exportImportHelper.getDataSiteLevelPortlets(
					group.getCompanyId(), true);
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}

			return true;
		}

		for (Portlet dataSiteLevelPortlet : dataSiteLevelPortlets) {
			PortletDataHandler portletDataHandler =
				dataSiteLevelPortlet.getPortletDataHandlerInstance();

			if (ArrayUtil.contains(
					portletDataHandler.getClassNames(), className)) {

				return isStagedPortlet(
					groupId, dataSiteLevelPortlet.getRootPortletId());
			}
		}

		return true;
	}

	@Override
	public boolean isStagingGroup(Group group) {
		if (isLocalStagingGroup(group) || isRemoteStagingGroup(group)) {
			return true;
		}

		return false;
	}

	@Override
	public boolean isStagingGroup(long groupId) {
		return isStagingGroup(_groupLocalService.fetchGroup(groupId));
	}

	@Override
	public boolean isStagingOrLiveGroup(Group group) {
		if (isStagingGroup(group) || isLiveGroup(group)) {
			return true;
		}

		return false;
	}

	@Override
	public boolean isStagingOrLiveGroup(long groupId) {
		return isStagingOrLiveGroup(_groupLocalService.fetchGroup(groupId));
	}

	private Group _getParentGroupForScopeGroup(Group group) {
		if (group.isLayout()) {
			return group.getParentGroup();
		}

		return group;
	}

	private String _getTypeSettingsProperty(Group group, String key) {
		UnicodeProperties typeSettingsUnicodeProperties =
			group.getTypeSettingsProperties();

		return typeSettingsUnicodeProperties.getProperty(key);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		StagingGroupHelperImpl.class);

	@Reference
	private ExportImportHelper _exportImportHelper;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private StagingURLHelper _stagingURLHelper;

}