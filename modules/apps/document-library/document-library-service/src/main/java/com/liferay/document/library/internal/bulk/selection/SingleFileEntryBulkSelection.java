/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.internal.bulk.selection;

import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.service.AssetEntryLocalService;
import com.liferay.bulk.selection.BaseSingleEntryBulkSelection;
import com.liferay.bulk.selection.BulkSelection;
import com.liferay.bulk.selection.BulkSelectionFactory;
import com.liferay.document.library.helper.DLAssetHelper;
import com.liferay.document.library.kernel.service.DLAppService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.repository.model.FileEntry;

import java.util.Map;

/**
 * @author Adolfo Pérez
 */
public class SingleFileEntryBulkSelection
	extends BaseSingleEntryBulkSelection<FileEntry> {

	public SingleFileEntryBulkSelection(
		long fileEntryId, Map<String, String[]> parameterMap,
		DLAppService dlAppService,
		AssetEntryLocalService assetEntryLocalService,
		DLAssetHelper dlAssetHelper) {

		super(fileEntryId, parameterMap);

		_fileEntryId = fileEntryId;
		_dlAppService = dlAppService;
		_assetEntryLocalService = assetEntryLocalService;
		_dlAssetHelper = dlAssetHelper;
	}

	@Override
	public Class<? extends BulkSelectionFactory>
		getBulkSelectionFactoryClass() {

		return FileEntryBulkSelectionFactory.class;
	}

	@Override
	public BulkSelection<AssetEntry> toAssetEntryBulkSelection() {
		return new FileEntryAssetEntryBulkSelection(
			this, _assetEntryLocalService, _dlAssetHelper);
	}

	@Override
	protected FileEntry getEntry() throws PortalException {
		return _dlAppService.getFileEntry(_fileEntryId);
	}

	@Override
	protected String getEntryName() throws PortalException {
		FileEntry fileEntry = getEntry();

		return fileEntry.getTitle();
	}

	private final AssetEntryLocalService _assetEntryLocalService;
	private final DLAppService _dlAppService;
	private final DLAssetHelper _dlAssetHelper;
	private final long _fileEntryId;

}