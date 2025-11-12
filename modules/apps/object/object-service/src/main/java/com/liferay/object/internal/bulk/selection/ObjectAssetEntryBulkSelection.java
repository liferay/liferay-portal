/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.internal.bulk.selection;

import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.service.AssetEntryLocalService;
import com.liferay.bulk.selection.BulkSelection;
import com.liferay.bulk.selection.BulkSelectionFactory;
import com.liferay.object.model.ObjectEntry;
import com.liferay.petra.function.UnsafeConsumer;
import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.portal.kernel.exception.PortalException;

import java.io.Serializable;

import java.util.Map;

/**
 * @author Andrea Sbarra
 */
public class ObjectAssetEntryBulkSelection
	implements BulkSelection<AssetEntry> {

	public ObjectAssetEntryBulkSelection(
		AssetEntryLocalService assetEntryLocalService,
		BulkSelection<Object> objectEntryBulkSelection) {

		_assetEntryLocalService = assetEntryLocalService;

		_objectBulkSelection = objectEntryBulkSelection;
	}

	@Override
	public <E extends PortalException> void forEach(
			UnsafeConsumer<AssetEntry, E> unsafeConsumer)
		throws PortalException {

		_objectBulkSelection.forEach(
			object -> unsafeConsumer.accept(_toAssetEntry(object)));
	}

	@Override
	public Class<? extends BulkSelectionFactory>
		getBulkSelectionFactoryClass() {

		return _objectBulkSelection.getBulkSelectionFactoryClass();
	}

	@Override
	public Map<String, String[]> getParameterMap() {
		return _objectBulkSelection.getParameterMap();
	}

	@Override
	public long getSize() throws PortalException {
		return _objectBulkSelection.getSize();
	}

	@Override
	public Serializable serialize() {
		return _objectBulkSelection.serialize();
	}

	@Override
	public BulkSelection<AssetEntry> toAssetEntryBulkSelection() {
		return this;
	}

	private AssetEntry _toAssetEntry(Object object) {
		try {
			ObjectEntry objectObjectEntry = (ObjectEntry)object;

			return _assetEntryLocalService.getEntry(
				objectObjectEntry.getModelClassName(),
				objectObjectEntry.getObjectEntryId());
		}
		catch (PortalException portalException) {
			return ReflectionUtil.throwException(portalException);
		}
	}

	private final AssetEntryLocalService _assetEntryLocalService;
	private final BulkSelection<Object> _objectBulkSelection;

}