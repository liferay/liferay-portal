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
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.xml.Document;
import com.liferay.portal.kernel.xml.Element;
import com.liferay.portal.kernel.zip.ZipWriter;

import jakarta.portlet.PortletRequest;

import java.io.File;

import java.util.List;
import java.util.Map;

import org.osgi.annotation.versioning.ProviderType;

/**
 * @author Zsolt Berentey
 */
@ProviderType
public interface ExportImportHelper {

	public static final String TEMP_FOLDER_NAME =
		ExportImportHelper.class.getName();

	public long[] getAllLayoutIds(long groupId, boolean privateLayout);

	public Map<Long, Boolean> getAllLayoutIdsMap(
		long groupId, boolean privateLayout);

	public List<Portlet> getDataSiteAndInstanceLevelPortlets(long companyId)
		throws Exception;

	public List<Portlet> getDataSiteAndInstanceLevelPortlets(
			long companyId, boolean excludeDataAlwaysStaged)
		throws Exception;

	public List<Portlet> getDataSiteLevelPortlets(long companyId)
		throws Exception;

	public List<Portlet> getDataSiteLevelPortlets(
			long companyId, boolean excludeDataAlwaysStaged)
		throws Exception;

	public String getExportableRootPortletId(long companyId, String portletId)
		throws Exception;

	public Map<String, Boolean> getExportPortletControlsMap(
			long companyId, String portletId,
			Map<String, String[]> parameterMap)
		throws Exception;

	public Map<String, Boolean> getExportPortletControlsMap(
			long companyId, String portletId,
			Map<String, String[]> parameterMap, String type)
		throws Exception;

	public Map<String, Boolean> getImportPortletControlsMap(
			long companyId, String portletId,
			Map<String, String[]> parameterMap, Element portletDataElement,
			ManifestSummary manifestSummary)
		throws Exception;

	public Map<Long, Boolean> getLayoutIdMap(PortletRequest portletRequest)
		throws PortalException;

	public long[] getLayoutIds(List<Layout> layouts);

	public long[] getLayoutIds(Map<Long, Boolean> layoutIdMap)
		throws PortalException;

	public long[] getLayoutIds(
			Map<Long, Boolean> layoutIdMap, long targetGroupId)
		throws PortalException;

	public long[] getLayoutIds(PortletRequest portletRequest)
		throws PortalException;

	public long[] getLayoutIds(
			PortletRequest portletRequest, long targetGroupId)
		throws PortalException;

	public long getLayoutModelDeletionCount(
			PortletDataContext portletDataContext, boolean privateLayout)
		throws PortalException;

	/**
	 * Returns the layout with the primary key or a dummy root layout.
	 *
	 * @param  plid the primary key of the layout
	 * @return the layout
	 * @throws PortalException if the layout is not a dummy and no layout with
	 *         the primary key could be found
	 */
	public Layout getLayoutOrCreateDummyRootLayout(long plid)
		throws PortalException;

	public ZipWriter getLayoutSetZipWriter(long groupId);

	public ManifestSummary getManifestSummary(
			long userId, long groupId, Map<String, String[]> parameterMap,
			FileEntry fileEntry)
		throws Exception;

	public ManifestSummary getManifestSummary(
			PortletDataContext portletDataContext)
		throws Exception;

	public List<Layout> getMissingParentLayouts(Layout layout, long liveGroupId)
		throws PortalException;

	public long getModelDeletionCount(
			PortletDataContext portletDataContext,
			StagedModelType stagedModelType)
		throws PortalException;

	public String getPortletExportFileName(Portlet portlet);

	public ZipWriter getPortletZipWriter(String portletId);

	public String getSelectedLayoutsJSON(
		long groupId, boolean privateLayout, String selectedNodes);

	public FileEntry getTempFileEntry(
			long groupId, long userId, String folderName)
		throws PortalException;

	public UserIdStrategy getUserIdStrategy(long userId, String userIdStrategy)
		throws PortalException;

	public boolean isAlwaysIncludeReference(
		PortletDataContext portletDataContext,
		StagedModel referenceStagedModel);

	public boolean isAlwaysIncludeReference(
		PortletDataContext portletDataContext, StagedModel referenceStagedModel,
		String rootPortletId);

	public boolean isExportPortletData(PortletDataContext portletDataContext);

	public boolean isLayoutRevisionInReview(Layout layout);

	public boolean isPublishDisplayedContent(
		PortletDataContext portletDataContext, Portlet portlet);

	public boolean isReferenceWithinExportScope(
		PortletDataContext portletDataContext, StagedModel stagedModel);

	public void processBackgroundTaskManifestSummary(
			long userId, long sourceGroupId, BackgroundTask backgroundTask,
			File file)
		throws PortalException;

	public void setPortletScope(
		PortletDataContext portletDataContext, Element portletElement);

	public MissingReferences validateMissingReferences(
			PortletDataContext portletDataContext)
		throws Exception;

	public void writeManifestSummary(
		Document document, ManifestSummary manifestSummary);

}