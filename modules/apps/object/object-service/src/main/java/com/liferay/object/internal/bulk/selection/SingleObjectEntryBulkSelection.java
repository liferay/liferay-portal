/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.internal.bulk.selection;

import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.service.AssetEntryLocalService;
import com.liferay.bulk.selection.BaseSingleEntryBulkSelection;
import com.liferay.bulk.selection.BulkSelection;
import com.liferay.bulk.selection.BulkSelectionFactory;
import com.liferay.document.library.kernel.service.DLAppService;
import com.liferay.document.library.util.DLAssetHelper;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.repository.model.FileEntry;

import java.util.Map;

/**
 * @author Andrea Sbarra
 */
public class SingleObjectEntryBulkSelection
	extends BaseSingleEntryBulkSelection<ObjectEntry> {

	public SingleObjectEntryBulkSelection(
		String className, long classPK,
		Map<String, String[]> parameterMap,
		AssetEntryLocalService assetEntryLocalService) {

		super(classPK, parameterMap);

		_className= className;
		_classPK = classPK;
		_assetEntryLocalService = assetEntryLocalService;
	}

	@Override
	public Class<? extends BulkSelectionFactory>
		getBulkSelectionFactoryClass() {

		return ObjectEntryBulkSelectionFactory.class;
	}

	@Override
	public BulkSelection<AssetEntry> toAssetEntryBulkSelection() {
		return new ObjectEntryAssetEntryBulkSelection(
			this, _assetEntryLocalService);
	}

	@Override
	protected ObjectEntry getEntry() throws PortalException {
		return _assetEntryLocalService.fetchEntry(_className, _classPK);
	}

	@Override
	protected String getEntryName() throws PortalException {
		ObjectEntry objectEntry = getEntry();

		return objectEntry.getTitleValue();
	}

	private final AssetEntryLocalService _assetEntryLocalService;
	private final long _classPK;
	private final String _className;

}