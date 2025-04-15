/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.kernel.lar;

import com.liferay.portal.kernel.backgroundtask.BackgroundTask;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.model.StagedModel;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.xml.Document;
import com.liferay.portal.kernel.xml.Element;
import com.liferay.portal.kernel.zip.ZipWriter;

import jakarta.portlet.PortletRequest;

import java.io.File;

import java.util.List;
import java.util.Map;

/**
 * @author Zsolt Berentey
 */
public class ExportImportHelperUtil {

	public static long[] getAllLayoutIds(long groupId, boolean privateLayout) {
		ExportImportHelper exportImportHelper =
			_exportImportHelperSnapshot.get();

		return exportImportHelper.getAllLayoutIds(groupId, privateLayout);
	}

	public static Map<Long, Boolean> getAllLayoutIdsMap(
		long groupId, boolean privateLayout) {

		ExportImportHelper exportImportHelper =
			_exportImportHelperSnapshot.get();

		return exportImportHelper.getAllLayoutIdsMap(groupId, privateLayout);
	}

	public static List<Portlet> getDataSiteAndInstanceLevelPortlets(
			long companyId)
		throws Exception {

		ExportImportHelper exportImportHelper =
			_exportImportHelperSnapshot.get();

		return exportImportHelper.getDataSiteAndInstanceLevelPortlets(
			companyId);
	}

	public static List<Portlet> getDataSiteAndInstanceLevelPortlets(
			long companyId, boolean excludeDataAlwaysStaged)
		throws Exception {

		ExportImportHelper exportImportHelper =
			_exportImportHelperSnapshot.get();

		return exportImportHelper.getDataSiteAndInstanceLevelPortlets(
			companyId, excludeDataAlwaysStaged);
	}

	public static List<Portlet> getDataSiteLevelPortlets(long companyId)
		throws Exception {

		ExportImportHelper exportImportHelper =
			_exportImportHelperSnapshot.get();

		return exportImportHelper.getDataSiteLevelPortlets(companyId);
	}

	public static List<Portlet> getDataSiteLevelPortlets(
			long companyId, boolean excludeDataAlwaysStaged)
		throws Exception {

		ExportImportHelper exportImportHelper =
			_exportImportHelperSnapshot.get();

		return exportImportHelper.getDataSiteLevelPortlets(
			companyId, excludeDataAlwaysStaged);
	}

	public static String getExportableRootPortletId(
			long companyId, String portletId)
		throws Exception {

		ExportImportHelper exportImportHelper =
			_exportImportHelperSnapshot.get();

		return exportImportHelper.getExportableRootPortletId(
			companyId, portletId);
	}

	public static Map<String, Boolean> getExportPortletControlsMap(
			long companyId, String portletId,
			Map<String, String[]> parameterMap)
		throws Exception {

		ExportImportHelper exportImportHelper =
			_exportImportHelperSnapshot.get();

		return exportImportHelper.getExportPortletControlsMap(
			companyId, portletId, parameterMap);
	}

	public static Map<String, Boolean> getExportPortletControlsMap(
			long companyId, String portletId,
			Map<String, String[]> parameterMap, String type)
		throws Exception {

		ExportImportHelper exportImportHelper =
			_exportImportHelperSnapshot.get();

		return exportImportHelper.getExportPortletControlsMap(
			companyId, portletId, parameterMap, type);
	}

	public static Map<String, Boolean> getImportPortletControlsMap(
			long companyId, String portletId,
			Map<String, String[]> parameterMap, Element portletDataElement,
			ManifestSummary manifestSummary)
		throws Exception {

		ExportImportHelper exportImportHelper =
			_exportImportHelperSnapshot.get();

		return exportImportHelper.getImportPortletControlsMap(
			companyId, portletId, parameterMap, portletDataElement,
			manifestSummary);
	}

	public static Map<Long, Boolean> getLayoutIdMap(
			PortletRequest portletRequest)
		throws PortalException {

		ExportImportHelper exportImportHelper =
			_exportImportHelperSnapshot.get();

		return exportImportHelper.getLayoutIdMap(portletRequest);
	}

	public static long[] getLayoutIds(List<Layout> layouts) {
		ExportImportHelper exportImportHelper =
			_exportImportHelperSnapshot.get();

		return exportImportHelper.getLayoutIds(layouts);
	}

	public static long[] getLayoutIds(Map<Long, Boolean> layoutIdMap)
		throws PortalException {

		ExportImportHelper exportImportHelper =
			_exportImportHelperSnapshot.get();

		return exportImportHelper.getLayoutIds(layoutIdMap);
	}

	public static long[] getLayoutIds(
			Map<Long, Boolean> layoutIdMap, long targetGroupId)
		throws PortalException {

		ExportImportHelper exportImportHelper =
			_exportImportHelperSnapshot.get();

		return exportImportHelper.getLayoutIds(layoutIdMap, targetGroupId);
	}

	public static long[] getLayoutIds(PortletRequest portletRequest)
		throws PortalException {

		ExportImportHelper exportImportHelper =
			_exportImportHelperSnapshot.get();

		return exportImportHelper.getLayoutIds(portletRequest);
	}

	public static long[] getLayoutIds(
			PortletRequest portletRequest, long targetGroupId)
		throws PortalException {

		ExportImportHelper exportImportHelper =
			_exportImportHelperSnapshot.get();

		return exportImportHelper.getLayoutIds(portletRequest, targetGroupId);
	}

	public static long getLayoutModelDeletionCount(
			PortletDataContext portletDataContext, boolean privateLayout)
		throws PortalException {

		ExportImportHelper exportImportHelper =
			_exportImportHelperSnapshot.get();

		return exportImportHelper.getLayoutModelDeletionCount(
			portletDataContext, privateLayout);
	}

	public static Layout getLayoutOrCreateDummyRootLayout(long plid)
		throws PortalException {

		ExportImportHelper exportImportHelper =
			_exportImportHelperSnapshot.get();

		return exportImportHelper.getLayoutOrCreateDummyRootLayout(plid);
	}

	public static ZipWriter getLayoutSetZipWriter(long groupId) {
		ExportImportHelper exportImportHelper =
			_exportImportHelperSnapshot.get();

		return exportImportHelper.getLayoutSetZipWriter(groupId);
	}

	public static ManifestSummary getManifestSummary(
			long userId, long groupId, Map<String, String[]> parameterMap,
			FileEntry fileEntry)
		throws Exception {

		ExportImportHelper exportImportHelper =
			_exportImportHelperSnapshot.get();

		return exportImportHelper.getManifestSummary(
			userId, groupId, parameterMap, fileEntry);
	}

	public static ManifestSummary getManifestSummary(
			PortletDataContext portletDataContext)
		throws Exception {

		ExportImportHelper exportImportHelper =
			_exportImportHelperSnapshot.get();

		return exportImportHelper.getManifestSummary(portletDataContext);
	}

	public static List<Layout> getMissingParentLayouts(
			Layout layout, long liveGroupId)
		throws PortalException {

		ExportImportHelper exportImportHelper =
			_exportImportHelperSnapshot.get();

		return exportImportHelper.getMissingParentLayouts(layout, liveGroupId);
	}

	public static long getModelDeletionCount(
			PortletDataContext portletDataContext,
			StagedModelType stagedModelType)
		throws PortalException {

		ExportImportHelper exportImportHelper =
			_exportImportHelperSnapshot.get();

		return exportImportHelper.getModelDeletionCount(
			portletDataContext, stagedModelType);
	}

	public static String getPortletExportFileName(Portlet portlet) {
		ExportImportHelper exportImportHelper =
			_exportImportHelperSnapshot.get();

		return exportImportHelper.getPortletExportFileName(portlet);
	}

	public static ZipWriter getPortletZipWriter(String portletId) {
		ExportImportHelper exportImportHelper =
			_exportImportHelperSnapshot.get();

		return exportImportHelper.getPortletZipWriter(portletId);
	}

	public static String getSelectedLayoutsJSON(
		long groupId, boolean privateLayout, String selectedNodes) {

		ExportImportHelper exportImportHelper =
			_exportImportHelperSnapshot.get();

		return exportImportHelper.getSelectedLayoutsJSON(
			groupId, privateLayout, selectedNodes);
	}

	public static FileEntry getTempFileEntry(
			long groupId, long userId, String folderName)
		throws PortalException {

		ExportImportHelper exportImportHelper =
			_exportImportHelperSnapshot.get();

		return exportImportHelper.getTempFileEntry(groupId, userId, folderName);
	}

	public static UserIdStrategy getUserIdStrategy(
			long userId, String userIdStrategy)
		throws PortalException {

		ExportImportHelper exportImportHelper =
			_exportImportHelperSnapshot.get();

		return exportImportHelper.getUserIdStrategy(userId, userIdStrategy);
	}

	public static boolean isAlwaysIncludeReference(
		PortletDataContext portletDataContext,
		StagedModel referenceStagedModel) {

		ExportImportHelper exportImportHelper =
			_exportImportHelperSnapshot.get();

		return exportImportHelper.isAlwaysIncludeReference(
			portletDataContext, referenceStagedModel);
	}

	public static boolean isAlwaysIncludeReference(
		PortletDataContext portletDataContext, StagedModel referenceStagedModel,
		String rootPortletId) {

		ExportImportHelper exportImportHelper =
			_exportImportHelperSnapshot.get();

		return exportImportHelper.isAlwaysIncludeReference(
			portletDataContext, referenceStagedModel, rootPortletId);
	}

	public static boolean isExportPortletData(
		PortletDataContext portletDataContext) {

		ExportImportHelper exportImportHelper =
			_exportImportHelperSnapshot.get();

		return exportImportHelper.isExportPortletData(portletDataContext);
	}

	public static boolean isLayoutRevisionInReview(Layout layout) {
		ExportImportHelper exportImportHelper =
			_exportImportHelperSnapshot.get();

		return exportImportHelper.isLayoutRevisionInReview(layout);
	}

	public static boolean isPublishDisplayedContent(
		PortletDataContext portletDataContext, Portlet portlet) {

		ExportImportHelper exportImportHelper =
			_exportImportHelperSnapshot.get();

		return exportImportHelper.isPublishDisplayedContent(
			portletDataContext, portlet);
	}

	public static boolean isReferenceWithinExportScope(
		PortletDataContext portletDataContext, StagedModel stagedModel) {

		ExportImportHelper exportImportHelper =
			_exportImportHelperSnapshot.get();

		return exportImportHelper.isReferenceWithinExportScope(
			portletDataContext, stagedModel);
	}

	public static void processBackgroundTaskManifestSummary(
			long userId, long sourceGroupId, BackgroundTask backgroundTask,
			File file)
		throws PortalException {

		ExportImportHelper exportImportHelper =
			_exportImportHelperSnapshot.get();

		exportImportHelper.processBackgroundTaskManifestSummary(
			userId, sourceGroupId, backgroundTask, file);
	}

	public static void setPortletScope(
		PortletDataContext portletDataContext, Element portletElement) {

		ExportImportHelper exportImportHelper =
			_exportImportHelperSnapshot.get();

		exportImportHelper.setPortletScope(portletDataContext, portletElement);
	}

	public static MissingReferences validateMissingReferences(
			PortletDataContext portletDataContext)
		throws Exception {

		ExportImportHelper exportImportHelper =
			_exportImportHelperSnapshot.get();

		return exportImportHelper.validateMissingReferences(portletDataContext);
	}

	public static void writeManifestSummary(
		Document document, ManifestSummary manifestSummary) {

		ExportImportHelper exportImportHelper =
			_exportImportHelperSnapshot.get();

		exportImportHelper.writeManifestSummary(document, manifestSummary);
	}

	private static final Snapshot<ExportImportHelper>
		_exportImportHelperSnapshot = new Snapshot<>(
			ExportImportHelperUtil.class, ExportImportHelper.class);

}