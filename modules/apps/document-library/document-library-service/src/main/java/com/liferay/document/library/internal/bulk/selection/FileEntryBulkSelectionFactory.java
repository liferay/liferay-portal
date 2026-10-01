/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.internal.bulk.selection;

import com.liferay.asset.kernel.service.AssetEntryLocalService;
import com.liferay.bulk.selection.BulkSelection;
import com.liferay.bulk.selection.BulkSelectionFactory;
import com.liferay.bulk.selection.EmptyBulkSelection;
import com.liferay.document.library.helper.DLAssetHelper;
import com.liferay.document.library.internal.bulk.selection.util.BulkSelectionFactoryUtil;
import com.liferay.document.library.kernel.service.DLAppService;
import com.liferay.portal.kernel.repository.RepositoryProvider;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.StringUtil;

import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Adolfo Pérez
 */
@Component(
	property = "model.class.name=com.liferay.document.library.kernel.model.DLFileEntry",
	service = BulkSelectionFactory.class
)
public class FileEntryBulkSelectionFactory
	implements BulkSelectionFactory<FileEntry> {

	@Override
	public BulkSelection<FileEntry> create(Map<String, String[]> parameterMap) {
		if (BulkSelectionFactoryUtil.isSelectAll(parameterMap)) {
			return new FolderFileEntryBulkSelection(
				BulkSelectionFactoryUtil.getRepositoryId(parameterMap),
				BulkSelectionFactoryUtil.getFolderId(parameterMap),
				parameterMap, _repositoryProvider, _dlAppService,
				_assetEntryLocalService, _dlAssetHelper);
		}

		if (!parameterMap.containsKey("rowIdsFileEntry")) {
			return new EmptyBulkSelection<>();
		}

		String[] values = parameterMap.get("rowIdsFileEntry");

		return _getFileEntrySelection(values, parameterMap);
	}

	private BulkSelection<FileEntry> _getFileEntrySelection(
		String[] values, Map<String, String[]> parameterMap) {

		if (values.length == 1) {
			values = StringUtil.split(values[0]);
		}

		long[] fileEntryIds = GetterUtil.getLongValues(values);

		if (fileEntryIds.length == 1) {
			return new SingleFileEntryBulkSelection(
				fileEntryIds[0], parameterMap, _dlAppService,
				_assetEntryLocalService, _dlAssetHelper);
		}

		return new MultipleFileEntryBulkSelection(
			fileEntryIds, parameterMap, _dlAppService, _assetEntryLocalService,
			_dlAssetHelper);
	}

	@Reference
	private AssetEntryLocalService _assetEntryLocalService;

	@Reference
	private DLAppService _dlAppService;

	@Reference
	private DLAssetHelper _dlAssetHelper;

	@Reference
	private RepositoryProvider _repositoryProvider;

}