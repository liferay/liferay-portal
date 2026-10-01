/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.internal.bulk.selection;

import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.service.AssetEntryLocalService;
import com.liferay.bulk.selection.BulkSelection;
import com.liferay.bulk.selection.BulkSelectionFactory;
import com.liferay.document.library.helper.DLAssetHelper;
import com.liferay.document.library.kernel.model.DLFileEntryConstants;
import com.liferay.petra.function.UnsafeConsumer;
import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.repository.model.FileEntry;

import java.io.Serializable;

import java.util.Map;

/**
 * @author Adolfo Pérez
 */
public class FileEntryAssetEntryBulkSelection
	implements BulkSelection<AssetEntry> {

	public FileEntryAssetEntryBulkSelection(
		BulkSelection<FileEntry> fileEntryBulkSelection,
		AssetEntryLocalService assetEntryLocalService,
		DLAssetHelper dlAssetHelper) {

		_fileEntryBulkSelection = fileEntryBulkSelection;
		_assetEntryLocalService = assetEntryLocalService;
		_dlAssetHelper = dlAssetHelper;
	}

	@Override
	public <E extends PortalException> void forEach(
			UnsafeConsumer<AssetEntry, E> unsafeConsumer)
		throws PortalException {

		_fileEntryBulkSelection.forEach(
			fileEntry -> unsafeConsumer.accept(_toAssetEntry(fileEntry)));
	}

	@Override
	public Class<? extends BulkSelectionFactory>
		getBulkSelectionFactoryClass() {

		return _fileEntryBulkSelection.getBulkSelectionFactoryClass();
	}

	@Override
	public Map<String, String[]> getParameterMap() {
		return _fileEntryBulkSelection.getParameterMap();
	}

	@Override
	public long getSize() throws PortalException {
		return _fileEntryBulkSelection.getSize();
	}

	@Override
	public Serializable serialize() {
		return _fileEntryBulkSelection.serialize();
	}

	@Override
	public BulkSelection<AssetEntry> toAssetEntryBulkSelection() {
		return this;
	}

	private AssetEntry _toAssetEntry(FileEntry fileEntry) {
		try {
			return _assetEntryLocalService.getEntry(
				DLFileEntryConstants.getClassName(),
				_dlAssetHelper.getAssetClassPK(
					fileEntry, fileEntry.getLatestFileVersion()));
		}
		catch (PortalException portalException) {
			return ReflectionUtil.throwException(portalException);
		}
	}

	private final AssetEntryLocalService _assetEntryLocalService;
	private final DLAssetHelper _dlAssetHelper;
	private final BulkSelection<FileEntry> _fileEntryBulkSelection;

}