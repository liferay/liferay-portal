/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.internal.bulk.selection;

import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.service.AssetEntryLocalService;
import com.liferay.bulk.selection.BulkSelection;
import com.liferay.bulk.selection.BulkSelectionFactory;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.object.model.ObjectEntryFolder;
import com.liferay.object.service.ObjectEntryFolderLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.petra.function.UnsafeConsumer;
import com.liferay.petra.string.StringPool;
import com.liferay.petra.string.StringUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.util.GetterUtil;

import java.io.Serializable;

import java.util.Map;

/**
 * @author Andrea Sbarra
 */
public class ObjectBulkSelection implements BulkSelection<Object> {

	public ObjectBulkSelection(
		AssetEntryLocalService assetEntryLocalService,
		DepotEntryLocalService depotEntryLocalService,
		ObjectEntryFolderLocalService objectEntryFolderLocalService,
		ObjectEntryLocalService objectEntryLocalService,
		Map<String, String[]> parameterMap) {

		_assetEntryLocalService = assetEntryLocalService;
		_depotEntryLocalService = depotEntryLocalService;
		_objectEntryFolderLocalService = objectEntryFolderLocalService;
		_objectEntryLocalService = objectEntryLocalService;
		_parameterMap = parameterMap;
	}

	@Override
	public <E extends PortalException> void forEach(
			UnsafeConsumer<Object, E> unsafeConsumer)
		throws PortalException {

		for (String rowId : _parameterMap.get("rowIds")) {
			String[] split = rowId.split(StringPool.SPACE);

			if (split[0].equals(DepotEntry.class.getName())) {
				DepotEntry depotEntry =
					_depotEntryLocalService.fetchGroupDepotEntry(
						GetterUtil.getLong(split[1]));

				if (depotEntry == null) {
					depotEntry = _depotEntryLocalService.getDepotEntry(
						GetterUtil.getLong(split[1]));
				}

				unsafeConsumer.accept(depotEntry);
			}
			else if (split[0].equals(ObjectEntryFolder.class.getName())) {
				unsafeConsumer.accept(
					_objectEntryFolderLocalService.getObjectEntryFolder(
						GetterUtil.getLong(split[1])));
			}
			else {
				unsafeConsumer.accept(
					_objectEntryLocalService.getObjectEntry(
						GetterUtil.getLong(split[1])));
			}
		}
	}

	@Override
	public Class<? extends BulkSelectionFactory>
		getBulkSelectionFactoryClass() {

		return ObjectBulkSelectionFactory.class;
	}

	@Override
	public Map<String, String[]> getParameterMap() {
		return _parameterMap;
	}

	@Override
	public long getSize() {
		String[] rowIds = _parameterMap.get("rowIds");

		return rowIds.length;
	}

	@Override
	public Serializable serialize() {
		return StringUtil.merge(_parameterMap.get("rowIds"), StringPool.COMMA);
	}

	@Override
	public BulkSelection<AssetEntry> toAssetEntryBulkSelection() {
		return new ObjectAssetEntryBulkSelection(_assetEntryLocalService, this);
	}

	private final AssetEntryLocalService _assetEntryLocalService;
	private final DepotEntryLocalService _depotEntryLocalService;
	private final ObjectEntryFolderLocalService _objectEntryFolderLocalService;
	private final ObjectEntryLocalService _objectEntryLocalService;
	private final Map<String, String[]> _parameterMap;

}