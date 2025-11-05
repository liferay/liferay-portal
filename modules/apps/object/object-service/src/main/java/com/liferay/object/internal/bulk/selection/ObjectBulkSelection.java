/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.internal.bulk.selection;

import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.bulk.selection.BulkSelection;
import com.liferay.bulk.selection.BulkSelectionFactory;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryLocalService;
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
		String[] rowIds, Map<String, String[]> parameterMap,
		DepotEntryLocalService depotEntryLocalService,
		ObjectEntryLocalService objectEntryLocalService,
		ObjectEntryFolderLocalService objectEntryFolderLocalService) {

		_rowIds = rowIds;
		_parameterMap = parameterMap;
		_depotEntryLocalService = depotEntryLocalService;
		_objectEntryLocalService = objectEntryLocalService;
		_objectEntryFolderLocalService = objectEntryFolderLocalService;
	}

	@Override
	public <E extends PortalException> void forEach(
			UnsafeConsumer<Object, E> unsafeConsumer)
		throws PortalException {

		for (String rowId : _rowIds) {
			String[] split = rowId.split(StringPool.SPACE);

			if (split[0].equals("com.liferay.depot.model.DepotEntry")) {
				DepotEntry depotEntry =
					_depotEntryLocalService.fetchGroupDepotEntry(
						GetterUtil.getLong(split[1]));

				if (depotEntry != null) {
					unsafeConsumer.accept(depotEntry);
				}

				unsafeConsumer.accept(
					_depotEntryLocalService.getDepotEntry(
						GetterUtil.getLong(split[1])));
			}
			else if (split[0].equals(
						"com.liferay.object.model.ObjectEntryFolder")) {

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
		return _rowIds.length;
	}

	@Override
	public Serializable serialize() {
		return StringUtil.merge(_rowIds, StringPool.COMMA);
	}

	@Override
	public BulkSelection<AssetEntry> toAssetEntryBulkSelection() {
		throw new UnsupportedOperationException();
	}

	private final DepotEntryLocalService _depotEntryLocalService;
	private final ObjectEntryFolderLocalService _objectEntryFolderLocalService;
	private final ObjectEntryLocalService _objectEntryLocalService;
	private final Map<String, String[]> _parameterMap;
	private final String[] _rowIds;

}