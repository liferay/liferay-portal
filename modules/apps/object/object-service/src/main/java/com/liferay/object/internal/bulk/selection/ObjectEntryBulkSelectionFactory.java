/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.internal.bulk.selection;

import com.liferay.asset.kernel.service.AssetEntryLocalService;
import com.liferay.bulk.selection.BulkSelection;
import com.liferay.bulk.selection.BulkSelectionFactory;
import com.liferay.bulk.selection.EmptyBulkSelection;
import com.liferay.document.library.internal.bulk.selection.util.BulkSelectionFactoryUtil;
import com.liferay.document.library.kernel.service.DLAppService;
import com.liferay.document.library.util.DLAssetHelper;
import com.liferay.object.model.ObjectEntry;
import com.liferay.portal.kernel.repository.RepositoryProvider;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.StringUtil;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import java.util.Map;

/**
 * @author Andrea Sbarra
 */
@Component(
	property = "model.class.name=com.liferay.object.model.ObjectEntry",
	service = BulkSelectionFactory.class
)
public class ObjectEntryBulkSelectionFactory
	implements BulkSelectionFactory<ObjectEntry> {

	@Override
	public BulkSelection<ObjectEntry> create(Map<String, String[]> parameterMap) {
		boolean selectAll = MapUtil.getBoolean(parameterMap, "selectAll");

		if (selectAll) {
			return null;
			/*
			return new SearchObjectEntryBulkSelection(
				BulkSelectionFactoryUtil.getRepositoryId(parameterMap),
				BulkSelectionFactoryUtil.getFolderId(parameterMap),
				parameterMap, _repositoryProvider, _dlAppService,
				_assetEntryLocalService, _dlAssetHelper);
			 */
		}

		if (!parameterMap.containsKey("rowIds")) {
			return new EmptyBulkSelection<>();
		}

		String[] values = parameterMap.get("rowIds");

		return _getObjectEntrySelection(values, parameterMap);
	}

	private BulkSelection<ObjectEntry> _getObjectEntrySelection(
		String[] values, Map<String, String[]> parameterMap) {

		if (values.length == 1) {
			values = StringUtil.split(values[0]);
		}

		long[] fileEntryIds = GetterUtil.getLongValues(values);

		if (fileEntryIds.length == 1) {
			return new SingleObjectEntryBulkSelection(
				fileEntryIds[0], parameterMap, _dlAppService,
				_assetEntryLocalService, _dlAssetHelper);
		}

		return new MultipleObjectEntryBulkSelection(
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