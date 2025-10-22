/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.internal.bulk.selection;

import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.service.AssetEntryLocalService;
import com.liferay.bulk.selection.BaseMultipleEntryBulkSelection;
import com.liferay.bulk.selection.BulkSelection;
import com.liferay.bulk.selection.BulkSelectionFactory;
import com.liferay.document.library.kernel.exception.NoSuchFileEntryException;
import com.liferay.document.library.kernel.service.DLAppService;
import com.liferay.document.library.util.DLAssetHelper;
import com.liferay.object.model.ObjectEntry;
import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.repository.model.FileEntry;

import java.util.Map;

/**
 * @author Adolfo Pérez
 */
public class MultipleObjectEntryBulkSelection
	extends BaseMultipleEntryBulkSelection<ObjectEntry> {

	public MultipleObjectEntryBulkSelection(
		long[] fileEntryIds, Map<String, String[]> parameterMap,

		AssetEntryLocalService assetEntryLocalService,
		) {

		super(fileEntryIds, parameterMap);

		_assetEntryLocalService = assetEntryLocalService;
	}

	@Override
	public Class<? extends BulkSelectionFactory>
		getBulkSelectionFactoryClass() {

		return ObjectEntryBulkSelectionFactory.class;
	}

	@Override
	public BulkSelection<AssetEntry> toAssetEntryBulkSelection() {
		return new ObjectEntryBulkSelection(
			this, _assetEntryLocalService, _dlAssetHelper);
	}

	@Override
	protected FileEntry fetchEntry(long fileEntryId) {
		try {
			return _dlAppService.getFileEntry(fileEntryId);
		}
		catch (NoSuchFileEntryException noSuchFileEntryException) {
			if (_log.isWarnEnabled()) {
				_log.warn(noSuchFileEntryException);
			}

			return null;
		}
		catch (PortalException portalException) {
			return ReflectionUtil.throwException(portalException);
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		MultipleObjectEntryBulkSelection.class);

	private final AssetEntryLocalService _assetEntryLocalService;
	private final DLAppService _dlAppService;
	private final DLAssetHelper _dlAssetHelper;

}