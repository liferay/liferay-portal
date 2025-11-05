/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.internal.bulk.selection;

import com.liferay.bulk.selection.BulkSelection;
import com.liferay.bulk.selection.BulkSelectionFactory;
import com.liferay.bulk.selection.EmptyBulkSelection;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.object.service.ObjectEntryFolderLocalService;
import com.liferay.object.service.ObjectEntryLocalService;

import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Andrea Sbarra
 */
@Component(
	property = "model.class.name=java.lang.Object",
	service = BulkSelectionFactory.class
)
public class ObjectBulkSelectionFactory
	implements BulkSelectionFactory<Object> {

	@Override
	public BulkSelection<Object> create(Map<String, String[]> parameterMap) {
		if (!parameterMap.containsKey("rowIds")) {
			return new EmptyBulkSelection<>();
		}

		String[] rowIds = parameterMap.get("rowIds");

		return _getObjectBulkSelection(rowIds, parameterMap);
	}

	private BulkSelection<Object> _getObjectBulkSelection(
		String[] values, Map<String, String[]> parameterMap) {

		return new ObjectBulkSelection(
			values, parameterMap, _depotEntryLocalService,
			_objectEntryLocalService, _objectEntryFolderLocalService);
	}

	@Reference
	private DepotEntryLocalService _depotEntryLocalService;

	@Reference
	private ObjectEntryFolderLocalService _objectEntryFolderLocalService;

	@Reference
	private ObjectEntryLocalService _objectEntryLocalService;

}