/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cmp.site.initializer.internal.frontend.data.set.filter;

import com.liferay.asset.kernel.service.AssetTagLocalService;
import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.model.DepotEntryModel;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.frontend.data.set.constants.FDSEntityFieldTypes;
import com.liferay.frontend.data.set.filter.BaseSelectionFDSFilter;
import com.liferay.frontend.data.set.filter.SelectionFDSFilterItem;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.model.GroupedModel;

import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * @author Fábio Alves
 */
public class TagSelectionFDSFilter extends BaseSelectionFDSFilter {

	public TagSelectionFDSFilter(
		AssetTagLocalService assetTagLocalService,
		ObjectDefinition cmpProjectObjectDefinition,
		DepotEntryLocalService depotEntryLocalService,
		GroupedModel groupedModel) {

		_assetTagLocalService = assetTagLocalService;
		_cmpProjectObjectDefinition = cmpProjectObjectDefinition;
		_depotEntryLocalService = depotEntryLocalService;
		_groupedModel = groupedModel;
	}

	@Override
	public String getEntityFieldType() {
		return FDSEntityFieldTypes.STRING;
	}

	@Override
	public String getId() {
		return "keywords";
	}

	@Override
	public String getLabel() {
		return "tag";
	}

	@Override
	public List<SelectionFDSFilterItem> getSelectionFDSFilterItems(
		Locale locale) {

		long[] groupIds = null;

		if (_groupedModel != null) {
			groupIds = new long[] {_groupedModel.getGroupId()};
		}
		else {
			groupIds = TransformUtil.transformToLongArray(
				_depotEntryLocalService.getDepotEntries(
					_cmpProjectObjectDefinition.getCompanyId(),
					DepotConstants.TYPE_PROJECT),
				DepotEntryModel::getGroupId);
		}

		Set<String> assetTagNames = new HashSet<>();

		return TransformUtil.transform(
			_assetTagLocalService.getGroupsTags(groupIds),
			assetTag -> {
				if (!assetTagNames.add(assetTag.getName())) {
					return null;
				}

				return new SelectionFDSFilterItem(
					assetTag.getName(), assetTag.getName());
			});
	}

	@Override
	public boolean isAutocompleteEnabled() {
		return true;
	}

	private final AssetTagLocalService _assetTagLocalService;
	private final ObjectDefinition _cmpProjectObjectDefinition;
	private final DepotEntryLocalService _depotEntryLocalService;
	private final GroupedModel _groupedModel;

}