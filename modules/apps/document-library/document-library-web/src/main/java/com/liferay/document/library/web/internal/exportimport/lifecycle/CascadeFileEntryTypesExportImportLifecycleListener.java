/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.web.internal.exportimport.lifecycle;

import com.liferay.document.library.kernel.model.DLFolder;
import com.liferay.document.library.kernel.service.DLFileEntryTypeLocalService;
import com.liferay.document.library.kernel.service.DLFolderLocalService;
import com.liferay.exportimport.kernel.lar.PortletDataContext;
import com.liferay.exportimport.kernel.lifecycle.EventAwareExportImportLifecycleListener;
import com.liferay.exportimport.kernel.lifecycle.ExportImportLifecycleListener;
import com.liferay.exportimport.kernel.model.ExportImportConfiguration;
import com.liferay.portal.kernel.model.StagedModel;
import com.liferay.portal.kernel.util.MapUtil;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Máté Thurzó
 */
@Component(service = ExportImportLifecycleListener.class)
public class CascadeFileEntryTypesExportImportLifecycleListener
	implements EventAwareExportImportLifecycleListener {

	@Override
	public boolean isParallel() {
		return true;
	}

	@Override
	public void onLayoutExportFailed(
		PortletDataContext portletDataContext, Throwable throwable) {
	}

	@Override
	public void onLayoutExportStarted(PortletDataContext portletDataContext) {
	}

	@Override
	public void onLayoutExportSucceeded(PortletDataContext portletDataContext) {
	}

	@Override
	public void onLayoutImportFailed(
		PortletDataContext portletDataContext, Throwable throwable) {
	}

	@Override
	public void onLayoutImportProcessFinished(
			PortletDataContext portletDataContext)
		throws Exception {

		_importedDLFolderIds =
			(Map<Long, Long>)portletDataContext.getNewPrimaryKeysMap(
				DLFolder.class);

		if (MapUtil.isEmpty(_importedDLFolderIds)) {
			return;
		}

		_processedDLFolderIds = new HashSet<>();

		_processDLFolderIds(_importedDLFolderIds.values());
	}

	@Override
	public void onLayoutImportStarted(PortletDataContext portletDataContext) {
	}

	@Override
	public void onLayoutImportSucceeded(PortletDataContext portletDataContext) {
	}

	@Override
	public void onLayoutLocalPublicationFailed(
		ExportImportConfiguration exportImportConfiguration,
		Throwable throwable) {
	}

	@Override
	public void onLayoutLocalPublicationStarted(
		ExportImportConfiguration exportImportConfiguration) {
	}

	@Override
	public void onLayoutLocalPublicationSucceeded(
		ExportImportConfiguration exportImportConfiguration) {
	}

	@Override
	public void onLayoutRemotePublicationFailed(
		ExportImportConfiguration exportImportConfiguration,
		Throwable throwable) {
	}

	@Override
	public void onLayoutRemotePublicationStarted(
		ExportImportConfiguration exportImportConfiguration) {
	}

	@Override
	public void onLayoutRemotePublicationSucceeded(
		ExportImportConfiguration exportImportConfiguration) {
	}

	@Override
	public void onPortletExportFailed(
		PortletDataContext portletDataContext, Throwable throwable) {
	}

	@Override
	public void onPortletExportStarted(PortletDataContext portletDataContext) {
	}

	@Override
	public void onPortletExportSucceeded(
		PortletDataContext portletDataContext) {
	}

	@Override
	public void onPortletImportFailed(
		PortletDataContext portletDataContext, Throwable throwable) {
	}

	@Override
	public void onPortletImportProcessFinished(
			PortletDataContext portletDataContext)
		throws Exception {

		_importedDLFolderIds =
			(Map<Long, Long>)portletDataContext.getNewPrimaryKeysMap(
				DLFolder.class);

		if (MapUtil.isEmpty(_importedDLFolderIds)) {
			return;
		}

		_processedDLFolderIds = new HashSet<>();

		_processDLFolderIds(_importedDLFolderIds.values());
	}

	@Override
	public void onPortletImportStarted(PortletDataContext portletDataContext) {
	}

	@Override
	public void onPortletImportSucceeded(
		PortletDataContext portletDataContext) {
	}

	@Override
	public void onPortletPublicationFailed(
		ExportImportConfiguration exportImportConfiguration,
		Throwable throwable) {
	}

	@Override
	public void onPortletPublicationStarted(
		ExportImportConfiguration exportImportConfiguration) {
	}

	@Override
	public void onPortletPublicationSucceeded(
		ExportImportConfiguration exportImportConfiguration) {
	}

	@Override
	public void onStagedModelExportFailed(
		PortletDataContext portletDataContext, StagedModel stagedModel,
		Throwable throwable) {
	}

	@Override
	public void onStagedModelExportStarted(
		PortletDataContext portletDataContext, StagedModel stagedModel) {
	}

	@Override
	public void onStagedModelExportSucceeded(
		PortletDataContext portletDataContext, StagedModel stagedModel) {
	}

	@Override
	public void onStagedModelImportFailed(
		PortletDataContext portletDataContext, StagedModel stagedModel,
		Throwable throwable) {
	}

	@Override
	public void onStagedModelImportStarted(
		PortletDataContext portletDataContext, StagedModel stagedModel) {
	}

	@Override
	public void onStagedModelImportSucceeded(
		PortletDataContext portletDataContext, StagedModel stagedModel) {
	}

	private DLFolder _getProcessableRootDLFolder(DLFolder dlFolder)
		throws Exception {

		long dlFolderId = dlFolder.getFolderId();

		if (_processedDLFolderIds.contains(dlFolderId)) {
			return null;
		}

		_processedDLFolderIds.add(dlFolderId);

		DLFolder parentDLFolder = dlFolder.getParentFolder();

		if ((parentDLFolder == null) ||
			!_importedDLFolderIds.containsValue(parentDLFolder.getFolderId())) {

			return dlFolder;
		}

		return _getProcessableRootDLFolder(parentDLFolder);
	}

	private void _processDLFolderIds(Collection<Long> dlFolderIds)
		throws Exception {

		for (Long dlFolderId : dlFolderIds) {
			DLFolder rootDLFolder = _getProcessableRootDLFolder(
				_dlFolderLocalService.fetchDLFolder(dlFolderId));

			if (rootDLFolder == null) {
				continue;
			}

			_dlFileEntryTypeLocalService.cascadeFileEntryTypes(
				rootDLFolder.getUserId(), rootDLFolder);
		}
	}

	@Reference
	private DLFileEntryTypeLocalService _dlFileEntryTypeLocalService;

	@Reference
	private DLFolderLocalService _dlFolderLocalService;

	private Map<Long, Long> _importedDLFolderIds;
	private Set<Long> _processedDLFolderIds;

}