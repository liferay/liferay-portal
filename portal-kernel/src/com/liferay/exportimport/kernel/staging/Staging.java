/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.kernel.staging;

import com.liferay.exportimport.kernel.lar.MissingReference;
import com.liferay.exportimport.kernel.lar.PortletDataContext;
import com.liferay.exportimport.kernel.lar.StagedModelType;
import com.liferay.exportimport.kernel.model.ExportImportConfiguration;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.model.BaseModel;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutRevision;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.auth.HttpPrincipal;
import com.liferay.portal.kernel.workflow.WorkflowTask;
import com.liferay.portal.kernel.xml.Element;

import jakarta.portlet.PortletRequest;

import jakarta.servlet.http.HttpServletRequest;

import java.io.File;

import java.util.Locale;
import java.util.Map;

import org.osgi.annotation.versioning.ProviderType;

/**
 * @author Raymond Augé
 */
@ProviderType
public interface Staging {

	public <T extends BaseModel> void addModelToChangesetCollection(T model)
		throws PortalException;

	public long copyFromLive(PortletRequest portletRequest)
		throws PortalException;

	public long copyFromLive(PortletRequest portletRequest, Portlet portlet)
		throws PortalException;

	public long copyRemoteLayouts(
			ExportImportConfiguration exportImportConfiguration)
		throws PortalException;

	public long copyRemoteLayouts(long exportImportConfigurationId)
		throws PortalException;

	public long copyRemoteLayouts(
			long sourceGroupId, boolean privateLayout,
			Map<Long, Boolean> layoutIdMap, Map<String, String[]> parameterMap,
			String remoteAddress, int remotePort, String remotePathContext,
			boolean secureConnection, long remoteGroupId,
			boolean remotePrivateLayout)
		throws PortalException;

	public long copyRemoteLayouts(
			long sourceGroupId, boolean privateLayout,
			Map<Long, Boolean> layoutIdMap, String name,
			Map<String, String[]> parameterMap, String remoteAddress,
			int remotePort, String remotePathContext, boolean secureConnection,
			long remoteGroupId, boolean remotePrivateLayout)
		throws PortalException;

	public void deleteLastImportSettings(Group liveGroup, boolean privateLayout)
		throws PortalException;

	public void deleteRecentLayoutRevisionId(
		HttpServletRequest httpServletRequest, long layoutSetBranchId,
		long plid);

	public void deleteRecentLayoutRevisionId(
		long userId, long layoutSetBranchId, long plid);

	public JSONArray getErrorMessagesJSONArray(
		Locale locale, Map<String, MissingReference> missingReferences);

	public JSONObject getExceptionMessagesJSONObject(
		Locale locale, Exception exception,
		ExportImportConfiguration exportImportConfiguration);

	public Group getLiveGroup(Group group);

	public Group getLiveGroup(long groupId);

	public long getLiveGroupId(long groupId);

	public Group getPermissionStagingGroup(Group group);

	public long getRecentLayoutRevisionId(
			HttpServletRequest httpServletRequest, long layoutSetBranchId,
			long plid)
		throws PortalException;

	public long getRecentLayoutRevisionId(
			User user, long layoutSetBranchId, long plid)
		throws PortalException;

	public long getRecentLayoutSetBranchId(
		HttpServletRequest httpServletRequest, long layoutSetId);

	public long getRecentLayoutSetBranchId(User user, long layoutSetId);

	public Layout getRemoteLayout(long userId, long stagingGroupId, long plid)
		throws PortalException;

	public long getRemoteLayoutPlid(long userId, long stagingGroupId, long plid)
		throws PortalException;

	public String getRemoteSiteURL(Group stagingGroup, boolean privateLayout)
		throws PortalException;

	public String getSchedulerGroupName(String destinationName, long groupId);

	public String getStagedPortletId(String portletId);

	public long[] getStagingAndLiveGroupIds(long groupId)
		throws PortalException;

	public Group getStagingGroup(long groupId);

	public JSONArray getWarningMessagesJSONArray(
		Locale locale, Map<String, MissingReference> missingReferences);

	public WorkflowTask getWorkflowTask(
			long userId, LayoutRevision layoutRevision)
		throws PortalException;

	public boolean hasRemoteLayout(long userId, long stagingGroupId, long plid)
		throws PortalException;

	public boolean hasWorkflowTask(long userId, LayoutRevision layoutRevision)
		throws PortalException;

	public boolean isGroupAccessible(Group group, Group fromGroup);

	public boolean isGroupAccessible(long groupId, long fromGroupId)
		throws PortalException;

	public boolean isIncomplete(Layout layout);

	public boolean isIncomplete(Layout layout, long layoutSetBranchId);

	public boolean isRemoteLayoutHasPortletId(
		long userId, long stagingGroupId, long plid, String portletId);

	public void populateLastPublishDateCounts(
			PortletDataContext portletDataContext,
			StagedModelType[] stagedModelTypes)
		throws PortalException;

	public void populateLastPublishDateCounts(
			PortletDataContext portletDataContext, String[] classNames)
		throws PortalException;

	public long publishLayout(
			long userId, long plid, long liveGroupId, boolean includeChildren)
		throws PortalException;

	public long publishLayouts(
			long userId, ExportImportConfiguration exportImportConfiguration)
		throws PortalException;

	public long publishLayouts(long userId, long exportImportConfigurationId)
		throws PortalException;

	public long publishLayouts(
			long userId, long sourceGroupId, long targetGroupId,
			boolean privateLayout, long[] layoutIds,
			Map<String, String[]> parameterMap)
		throws PortalException;

	public long publishLayouts(
			long userId, long sourceGroupId, long targetGroupId,
			boolean privateLayout, long[] layoutIds, String name,
			Map<String, String[]> parameterMap)
		throws PortalException;

	public long publishLayouts(
			long userId, long sourceGroupId, long targetGroupId,
			boolean privateLayout, Map<String, String[]> parameterMap)
		throws PortalException;

	public long publishPortlet(
			long userId, ExportImportConfiguration exportImportConfiguration)
		throws PortalException;

	public long publishPortlet(long userId, long exportImportConfigurationId)
		throws PortalException;

	public long publishPortlet(
			long userId, long sourceGroupId, long targetGroupId,
			long sourcePlid, long targetPlid, String portletId,
			Map<String, String[]> parameterMap)
		throws PortalException;

	public long publishToLive(PortletRequest portletRequest)
		throws PortalException;

	public long publishToLive(PortletRequest portletRequest, Portlet portlet)
		throws PortalException;

	public long publishToRemote(PortletRequest portletRequest)
		throws PortalException;

	public <T extends BaseModel> void removeModelFromChangesetCollection(
			T model)
		throws PortalException;

	public void scheduleCopyFromLive(PortletRequest portletRequest)
		throws PortalException;

	public void schedulePublishToLive(PortletRequest portletRequest)
		throws PortalException;

	public void schedulePublishToRemote(PortletRequest portletRequest)
		throws PortalException;

	public void setRecentLayoutBranchId(
			HttpServletRequest httpServletRequest, long layoutSetBranchId,
			long plid, long layoutBranchId)
		throws PortalException;

	public void setRecentLayoutBranchId(
			User user, long layoutSetBranchId, long plid, long layoutBranchId)
		throws PortalException;

	public void setRecentLayoutRevisionId(
			HttpServletRequest httpServletRequest, long layoutSetBranchId,
			long plid, long layoutRevisionId)
		throws PortalException;

	public void setRecentLayoutRevisionId(
			User user, long layoutSetBranchId, long plid, long layoutRevisionId)
		throws PortalException;

	public void setRecentLayoutSetBranchId(
			HttpServletRequest httpServletRequest, long layoutSetId,
			long layoutSetBranchId)
		throws PortalException;

	public void setRecentLayoutSetBranchId(
			User user, long layoutSetId, long layoutSetBranchId)
		throws PortalException;

	public void setRemoteSiteURL(
			Group stagingGroup, boolean overrideRemoteSiteURL,
			String remoteSiteURL)
		throws PortalException;

	public String stripProtocolFromRemoteAddress(String remoteAddress);

	public void transferFileToRemoteLive(
			File file, long stagingRequestId, HttpPrincipal httpPrincipal)
		throws Exception;

	public void unscheduleCopyFromLive(PortletRequest portletRequest)
		throws PortalException;

	public void unschedulePublishToLive(PortletRequest portletRequest)
		throws PortalException;

	public void unschedulePublishToRemote(PortletRequest portletRequest)
		throws PortalException;

	public void updateLastImportSettings(
			Element layoutElement, Layout layout,
			PortletDataContext portletDataContext)
		throws PortalException;

	public void validateRemoteGroupIsSame(
			long groupId, long remoteGroupId, String remoteAddress,
			int remotePort, String remotePathContext, boolean secureConnection)
		throws PortalException;

}