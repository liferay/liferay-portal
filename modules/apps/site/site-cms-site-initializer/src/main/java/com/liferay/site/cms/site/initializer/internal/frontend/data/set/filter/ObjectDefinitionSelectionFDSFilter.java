/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.frontend.data.set.filter;

import com.liferay.frontend.data.set.constants.FDSEntityFieldTypes;
import com.liferay.frontend.data.set.filter.BaseSelectionFDSFilter;
import com.liferay.frontend.data.set.filter.FDSFilter;
import com.liferay.frontend.data.set.filter.SelectionFDSFilterItem;
import com.liferay.object.constants.ObjectFolderConstants;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.service.ObjectDefinitionService;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.site.cms.site.initializer.internal.constants.CMSSiteInitializerFDSNames;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Roberto Díaz
 */
@Component(
	property = {
		"frontend.data.set.name=" + CMSSiteInitializerFDSNames.ALL_SECTION,
		"frontend.data.set.name=" + CMSSiteInitializerFDSNames.CONTENTS_SECTION,
		"frontend.data.set.name=" + CMSSiteInitializerFDSNames.FILES_SECTION,
		"frontend.data.set.name=" + CMSSiteInitializerFDSNames.RECYCLE_BIN_SECTION,
		"frontend.data.set.name=" + CMSSiteInitializerFDSNames.VIEW_CONTENTS_FOLDER,
		"frontend.data.set.name=" + CMSSiteInitializerFDSNames.VIEW_FILES_FOLDER,
		"service.ranking:Integer=100"
	},
	service = FDSFilter.class
)
public class ObjectDefinitionSelectionFDSFilter extends BaseSelectionFDSFilter {

	@Override
	public String getEntityFieldType() {
		return FDSEntityFieldTypes.INTEGER;
	}

	@Override
	public String getId() {
		return "objectDefinitionId";
	}

	@Override
	public String getLabel() {
		return "asset-type";
	}

	@Override
	public List<SelectionFDSFilterItem> getSelectionFDSFilterItems(
		Locale locale) {

		List<SelectionFDSFilterItem> selectionFDSFilterItems =
			new ArrayList<>();

		List<ObjectDefinition> objectDefinitions =
			_objectDefinitionService.getCMSObjectDefinitions(
				CompanyThreadLocal.getCompanyId(),
				new String[] {
					ObjectFolderConstants.
						EXTERNAL_REFERENCE_CODE_CONTENT_STRUCTURES,
					ObjectFolderConstants.EXTERNAL_REFERENCE_CODE_FILE_TYPES
				});

		if (objectDefinitions.isEmpty()) {
			return selectionFDSFilterItems;
		}

		for (ObjectDefinition objectDefinition : objectDefinitions) {
			objectDefinition.getLabel(locale);

			selectionFDSFilterItems.add(
				new SelectionFDSFilterItem(
					objectDefinition.getLabel(locale),
					objectDefinition.getObjectDefinitionId()));
		}

		return selectionFDSFilterItems;
	}

	@Reference
	private ObjectDefinitionService _objectDefinitionService;

}