/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.internal.bulk.selection;

import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.service.AssetEntryLocalService;
import com.liferay.bulk.selection.BulkSelection;
import com.liferay.bulk.selection.BulkSelectionFactory;
import com.liferay.document.library.kernel.model.DLFileEntryConstants;
import com.liferay.document.library.util.DLAssetHelper;
import com.liferay.object.model.ObjectEntry;
import com.liferay.petra.function.UnsafeConsumer;
import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.repository.model.FileEntry;

import java.io.Serializable;
import java.util.Map;

/**
 * @author Andrea Sbarra
 */
public class ObjectEntryAssetEntryBulkSelection
	implements BulkSelection<AssetEntry> {

	public ObjectEntryAssetEntryBulkSelection(
		BulkSelection<ObjectEntry> objectEntryBulkSelection,
		AssetEntryLocalService assetEntryLocalService) {

		_objectEntryBulkSelection = objectEntryBulkSelection;
		_assetEntryLocalService = assetEntryLocalService;
	}

	@Override
	public <E extends PortalException> void forEach(
			UnsafeConsumer<AssetEntry, E> unsafeConsumer)
		throws PortalException {

		_objectEntryBulkSelection.forEach(
			objectEntry -> unsafeConsumer.accept(_toAssetEntry(objectEntry)));
	}

	@Override
	public Class<? extends BulkSelectionFactory>
		getBulkSelectionFactoryClass() {

		return _objectEntryBulkSelection.getBulkSelectionFactoryClass();
	}

	@Override
	public Map<String, String[]> getParameterMap() {
		return _objectEntryBulkSelection.getParameterMap();
	}

	@Override
	public long getSize() throws PortalException {
		return _objectEntryBulkSelection.getSize();
	}

	@Override
	public Serializable serialize() {
		return _objectEntryBulkSelection.serialize();
	}

	@Override
	public BulkSelection<AssetEntry> toAssetEntryBulkSelection() {
		return this;
	}

	private AssetEntry _toAssetEntry(ObjectEntry objectEntry) {
		try {
			return _assetEntryLocalService.getEntry(
				DLFileEntryConstants.getClassName(),
				objectEntry.getc));
		}
		catch (PortalException portalException) {
			return ReflectionUtil.throwException(portalException);
		}
	}

	private final AssetEntryLocalService _assetEntryLocalService;
	private final BulkSelection<ObjectEntry> _objectEntryBulkSelection;

}