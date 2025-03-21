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
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.security.auth.HttpPrincipal;
import com.liferay.portal.kernel.util.ProxyFactory;
import com.liferay.portal.kernel.workflow.WorkflowTask;
import com.liferay.portal.kernel.xml.Element;

import jakarta.portlet.PortletRequest;

import jakarta.servlet.http.HttpServletRequest;

import java.io.File;

import java.util.Locale;
import java.util.Map;

/**
 * @author Raymond Augé
 */
public class StagingUtil {

	public static <T extends BaseModel> void addModelToChangesetCollection(
			T model)
		throws PortalException {

		Staging staging = _getStaging();

		staging.addModelToChangesetCollection(model);
	}

	public static long copyFromLive(PortletRequest portletRequest)
		throws PortalException {

		Staging staging = _getStaging();

		return staging.copyFromLive(portletRequest);
	}

	public static long copyFromLive(
			PortletRequest portletRequest, Portlet portlet)
		throws PortalException {

		Staging staging = _getStaging();

		return staging.copyFromLive(portletRequest, portlet);
	}

	public static long copyRemoteLayouts(
			ExportImportConfiguration exportImportConfiguration)
		throws PortalException {

		Staging staging = _getStaging();

		return staging.copyRemoteLayouts(exportImportConfiguration);
	}

	public static long copyRemoteLayouts(long exportImportConfigurationId)
		throws PortalException {

		Staging staging = _getStaging();

		return staging.copyRemoteLayouts(exportImportConfigurationId);
	}

	public static long copyRemoteLayouts(
			long sourceGroupId, boolean privateLayout,
			Map<Long, Boolean> layoutIdMap, Map<String, String[]> parameterMap,
			String remoteAddress, int remotePort, String remotePathContext,
			boolean secureConnection, long remoteGroupId,
			boolean remotePrivateLayout)
		throws PortalException {

		Staging staging = _getStaging();

		return staging.copyRemoteLayouts(
			sourceGroupId, privateLayout, layoutIdMap, parameterMap,
			remoteAddress, remotePort, remotePathContext, secureConnection,
			remoteGroupId, remotePrivateLayout);
	}

	public static long copyRemoteLayouts(
			long sourceGroupId, boolean privateLayout,
			Map<Long, Boolean> layoutIdMap, String name,
			Map<String, String[]> parameterMap, String remoteAddress,
			int remotePort, String remotePathContext, boolean secureConnection,
			long remoteGroupId, boolean remotePrivateLayout)
		throws PortalException {

		Staging staging = _getStaging();

		return staging.copyRemoteLayouts(
			sourceGroupId, privateLayout, layoutIdMap, name, parameterMap,
			remoteAddress, remotePort, remotePathContext, secureConnection,
			remoteGroupId, remotePrivateLayout);
	}

	public static void deleteLastImportSettings(
			Group liveGroup, boolean privateLayout)
		throws PortalException {

		Staging staging = _getStaging();

		staging.deleteLastImportSettings(liveGroup, privateLayout);
	}

	public static void deleteRecentLayoutRevisionId(
		HttpServletRequest httpServletRequest, long layoutSetBranchId,
		long plid) {

		Staging staging = _getStaging();

		staging.deleteRecentLayoutRevisionId(
			httpServletRequest, layoutSetBranchId, plid);
	}

	public static void deleteRecentLayoutRevisionId(
		long userId, long layoutSetBranchId, long plid) {

		Staging staging = _getStaging();

		staging.deleteRecentLayoutRevisionId(userId, layoutSetBranchId, plid);
	}

	public static JSONArray getErrorMessagesJSONArray(
		Locale locale, Map<String, MissingReference> missingReferences) {

		Staging staging = _getStaging();

		return staging.getErrorMessagesJSONArray(locale, missingReferences);
	}

	public static JSONObject getExceptionMessagesJSONObject(
		Locale locale, Exception exception,
		ExportImportConfiguration exportImportConfiguration) {

		Staging staging = _getStaging();

		return staging.getExceptionMessagesJSONObject(
			locale, exception, exportImportConfiguration);
	}

	public static Group getLiveGroup(Group group) {
		Staging staging = _getStaging();

		return staging.getLiveGroup(group);
	}

	public static Group getLiveGroup(long groupId) {
		Staging staging = _getStaging();

		return staging.getLiveGroup(groupId);
	}

	public static long getLiveGroupId(long groupId) {
		Staging staging = _getStaging();

		return staging.getLiveGroupId(groupId);
	}

	public static Group getPermissionStagingGroup(Group group) {
		Staging staging = _getStaging();

		return staging.getPermissionStagingGroup(group);
	}

	public static long getRecentLayoutRevisionId(
			HttpServletRequest httpServletRequest, long layoutSetBranchId,
			long plid)
		throws PortalException {

		Staging staging = _getStaging();

		return staging.getRecentLayoutRevisionId(
			httpServletRequest, layoutSetBranchId, plid);
	}

	public static long getRecentLayoutRevisionId(
			User user, long layoutSetBranchId, long plid)
		throws PortalException {

		Staging staging = _getStaging();

		return staging.getRecentLayoutRevisionId(user, layoutSetBranchId, plid);
	}

	public static long getRecentLayoutSetBranchId(
		HttpServletRequest httpServletRequest, long layoutSetId) {

		Staging staging = _getStaging();

		return staging.getRecentLayoutSetBranchId(
			httpServletRequest, layoutSetId);
	}

	public static long getRecentLayoutSetBranchId(User user, long layoutSetId) {
		Staging staging = _getStaging();

		return staging.getRecentLayoutSetBranchId(user, layoutSetId);
	}

	public static Layout getRemoteLayout(
			long userId, long stagingGroupId, long plid)
		throws PortalException {

		Staging staging = _getStaging();

		return staging.getRemoteLayout(userId, stagingGroupId, plid);
	}

	public static long getRemoteLayoutPlid(
			long userId, long stagingGroupId, long plid)
		throws PortalException {

		Staging staging = _getStaging();

		return staging.getRemoteLayoutPlid(userId, stagingGroupId, plid);
	}

	public static String getRemoteSiteURL(
			Group stagingGroup, boolean privateLayout)
		throws PortalException {

		Staging staging = _getStaging();

		return staging.getRemoteSiteURL(stagingGroup, privateLayout);
	}

	public static String getSchedulerGroupName(
		String destinationName, long groupId) {

		Staging staging = _getStaging();

		return staging.getSchedulerGroupName(destinationName, groupId);
	}

	public static String getStagedPortletId(String portletId) {
		Staging staging = _getStaging();

		return staging.getStagedPortletId(portletId);
	}

	public static long[] getStagingAndLiveGroupIds(long groupId)
		throws PortalException {

		Staging staging = _getStaging();

		return staging.getStagingAndLiveGroupIds(groupId);
	}

	public static Group getStagingGroup(long groupId) {
		Staging staging = _getStaging();

		return staging.getStagingGroup(groupId);
	}

	public static JSONArray getWarningMessagesJSONArray(
		Locale locale, Map<String, MissingReference> missingReferences) {

		Staging staging = _getStaging();

		return staging.getWarningMessagesJSONArray(locale, missingReferences);
	}

	public static WorkflowTask getWorkflowTask(
			long userId, LayoutRevision layoutRevision)
		throws PortalException {

		Staging staging = _getStaging();

		return staging.getWorkflowTask(userId, layoutRevision);
	}

	public static boolean hasRemoteLayout(
			long userId, long stagingGroupId, long plid)
		throws PortalException {

		Staging staging = _getStaging();

		return staging.hasRemoteLayout(userId, stagingGroupId, plid);
	}

	public static boolean hasWorkflowTask(
			long userId, LayoutRevision layoutRevision)
		throws PortalException {

		Staging staging = _getStaging();

		return staging.hasWorkflowTask(userId, layoutRevision);
	}

	public static boolean isGroupAccessible(Group group, Group fromGroup) {
		Staging staging = _getStaging();

		return staging.isGroupAccessible(group, fromGroup);
	}

	public static boolean isGroupAccessible(long groupId, long fromGroupId)
		throws PortalException {

		Staging staging = _getStaging();

		return staging.isGroupAccessible(groupId, fromGroupId);
	}

	public static boolean isIncomplete(Layout layout) {
		Staging staging = _getStaging();

		return staging.isIncomplete(layout);
	}

	public static boolean isIncomplete(Layout layout, long layoutSetBranchId) {
		Staging staging = _getStaging();

		return staging.isIncomplete(layout, layoutSetBranchId);
	}

	public static boolean isRemoteLayoutHasPortletId(
		long userId, long stagingGroupId, long plid, String portletId) {

		Staging staging = _getStaging();

		return staging.isRemoteLayoutHasPortletId(
			userId, stagingGroupId, plid, portletId);
	}

	public static void populateLastPublishDateCounts(
			PortletDataContext portletDataContext,
			StagedModelType[] stagedModelTypes)
		throws PortalException {

		Staging staging = _getStaging();

		staging.populateLastPublishDateCounts(
			portletDataContext, stagedModelTypes);
	}

	public static void populateLastPublishDateCounts(
			PortletDataContext portletDataContext, String[] classNames)
		throws PortalException {

		Staging staging = _getStaging();

		staging.populateLastPublishDateCounts(portletDataContext, classNames);
	}

	public static long publishLayout(
			long userId, long plid, long liveGroupId, boolean includeChildren)
		throws PortalException {

		Staging staging = _getStaging();

		return staging.publishLayout(
			userId, plid, liveGroupId, includeChildren);
	}

	public static long publishLayouts(
			long userId, ExportImportConfiguration exportImportConfiguration)
		throws PortalException {

		Staging staging = _getStaging();

		return staging.publishLayouts(userId, exportImportConfiguration);
	}

	public static long publishLayouts(
			long userId, long exportImportConfigurationId)
		throws PortalException {

		Staging staging = _getStaging();

		return staging.publishLayouts(userId, exportImportConfigurationId);
	}

	public static long publishLayouts(
			long userId, long sourceGroupId, long targetGroupId,
			boolean privateLayout, long[] layoutIds,
			Map<String, String[]> parameterMap)
		throws PortalException {

		Staging staging = _getStaging();

		return staging.publishLayouts(
			userId, sourceGroupId, targetGroupId, privateLayout, layoutIds,
			parameterMap);
	}

	public static long publishLayouts(
			long userId, long sourceGroupId, long targetGroupId,
			boolean privateLayout, long[] layoutIds, String name,
			Map<String, String[]> parameterMap)
		throws PortalException {

		Staging staging = _getStaging();

		return staging.publishLayouts(
			userId, sourceGroupId, targetGroupId, privateLayout, layoutIds,
			name, parameterMap);
	}

	public static long publishLayouts(
			long userId, long sourceGroupId, long targetGroupId,
			boolean privateLayout, Map<String, String[]> parameterMap)
		throws PortalException {

		Staging staging = _getStaging();

		return staging.publishLayouts(
			userId, sourceGroupId, targetGroupId, privateLayout, parameterMap);
	}

	public static long publishPortlet(
			long userId, ExportImportConfiguration exportImportConfiguration)
		throws PortalException {

		Staging staging = _getStaging();

		return staging.publishPortlet(userId, exportImportConfiguration);
	}

	public static long publishPortlet(
			long userId, long exportImportConfigurationId)
		throws PortalException {

		Staging staging = _getStaging();

		return staging.publishPortlet(userId, exportImportConfigurationId);
	}

	public static long publishPortlet(
			long userId, long sourceGroupId, long targetGroupId,
			long sourcePlid, long targetPlid, String portletId,
			Map<String, String[]> parameterMap)
		throws PortalException {

		Staging staging = _getStaging();

		return staging.publishPortlet(
			userId, sourceGroupId, targetGroupId, sourcePlid, targetPlid,
			portletId, parameterMap);
	}

	public static long publishToLive(PortletRequest portletRequest)
		throws PortalException {

		Staging staging = _getStaging();

		return staging.publishToLive(portletRequest);
	}

	public static long publishToLive(
			PortletRequest portletRequest, Portlet portlet)
		throws PortalException {

		Staging staging = _getStaging();

		return staging.publishToLive(portletRequest, portlet);
	}

	public static long publishToRemote(PortletRequest portletRequest)
		throws PortalException {

		Staging staging = _getStaging();

		return staging.publishToRemote(portletRequest);
	}

	public static <T extends BaseModel> void removeModelFromChangesetCollection(
			T model)
		throws PortalException {

		Staging staging = _getStaging();

		staging.removeModelFromChangesetCollection(model);
	}

	public static void scheduleCopyFromLive(PortletRequest portletRequest)
		throws PortalException {

		Staging staging = _getStaging();

		staging.scheduleCopyFromLive(portletRequest);
	}

	public static void schedulePublishToLive(PortletRequest portletRequest)
		throws PortalException {

		Staging staging = _getStaging();

		staging.schedulePublishToLive(portletRequest);
	}

	public static void schedulePublishToRemote(PortletRequest portletRequest)
		throws PortalException {

		Staging staging = _getStaging();

		staging.schedulePublishToRemote(portletRequest);
	}

	public static void setRecentLayoutBranchId(
			HttpServletRequest httpServletRequest, long layoutSetBranchId,
			long plid, long layoutBranchId)
		throws PortalException {

		Staging staging = _getStaging();

		staging.setRecentLayoutBranchId(
			httpServletRequest, layoutSetBranchId, plid, layoutBranchId);
	}

	public static void setRecentLayoutBranchId(
			User user, long layoutSetBranchId, long plid, long layoutBranchId)
		throws PortalException {

		Staging staging = _getStaging();

		staging.setRecentLayoutBranchId(
			user, layoutSetBranchId, plid, layoutBranchId);
	}

	public static void setRecentLayoutRevisionId(
			HttpServletRequest httpServletRequest, long layoutSetBranchId,
			long plid, long layoutRevisionId)
		throws PortalException {

		Staging staging = _getStaging();

		staging.setRecentLayoutRevisionId(
			httpServletRequest, layoutSetBranchId, plid, layoutRevisionId);
	}

	public static void setRecentLayoutRevisionId(
			User user, long layoutSetBranchId, long plid, long layoutRevisionId)
		throws PortalException {

		Staging staging = _getStaging();

		staging.setRecentLayoutRevisionId(
			user, layoutSetBranchId, plid, layoutRevisionId);
	}

	public static void setRecentLayoutSetBranchId(
			HttpServletRequest httpServletRequest, long layoutSetId,
			long layoutSetBranchId)
		throws PortalException {

		Staging staging = _getStaging();

		staging.setRecentLayoutSetBranchId(
			httpServletRequest, layoutSetId, layoutSetBranchId);
	}

	public static void setRecentLayoutSetBranchId(
			User user, long layoutSetId, long layoutSetBranchId)
		throws PortalException {

		Staging staging = _getStaging();

		staging.setRecentLayoutSetBranchId(
			user, layoutSetId, layoutSetBranchId);
	}

	public static void setRemoteSiteURL(
			Group stagingGroup, boolean overrideRemoteSiteURL,
			String remoteSiteURL)
		throws PortalException {

		Staging staging = _getStaging();

		staging.setRemoteSiteURL(
			stagingGroup, overrideRemoteSiteURL, remoteSiteURL);
	}

	public static String stripProtocolFromRemoteAddress(String remoteAddress) {
		Staging staging = _getStaging();

		return staging.stripProtocolFromRemoteAddress(remoteAddress);
	}

	public static void transferFileToRemoteLive(
			File file, long stagingRequestId, HttpPrincipal httpPrincipal)
		throws Exception {

		Staging staging = _getStaging();

		staging.transferFileToRemoteLive(file, stagingRequestId, httpPrincipal);
	}

	public static void unscheduleCopyFromLive(PortletRequest portletRequest)
		throws PortalException {

		Staging staging = _getStaging();

		staging.unscheduleCopyFromLive(portletRequest);
	}

	public static void unschedulePublishToLive(PortletRequest portletRequest)
		throws PortalException {

		Staging staging = _getStaging();

		staging.unschedulePublishToLive(portletRequest);
	}

	public static void unschedulePublishToRemote(PortletRequest portletRequest)
		throws PortalException {

		Staging staging = _getStaging();

		staging.unschedulePublishToRemote(portletRequest);
	}

	public static void updateLastImportSettings(
			Element layoutElement, Layout layout,
			PortletDataContext portletDataContext)
		throws PortalException {

		Staging staging = _getStaging();

		staging.updateLastImportSettings(
			layoutElement, layout, portletDataContext);
	}

	public static void validateRemoteGroupIsSame(
			long groupId, long remoteGroupId, String remoteAddress,
			int remotePort, String remotePathContext, boolean secureConnection)
		throws PortalException {

		Staging staging = _getStaging();

		staging.validateRemoteGroupIsSame(
			groupId, remoteGroupId, remoteAddress, remotePort,
			remotePathContext, secureConnection);
	}

	private static Staging _getStaging() {
		Staging staging = _stagingSnapshot.get();

		if (staging == null) {
			return DummyStagingHolder._dummyStaging;
		}

		return staging;
	}

	private static final Snapshot<Staging> _stagingSnapshot = new Snapshot<>(
		StagingUtil.class, Staging.class);

	private static class DummyStagingHolder {

		private static final Staging _dummyStaging =
			ProxyFactory.newDummyInstance(Staging.class);

	}

}