/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.frontend.data.set.filter;

import com.liferay.frontend.data.set.constants.FDSEntityFieldTypes;
import com.liferay.frontend.data.set.filter.BaseSelectionFDSFilter;
import com.liferay.frontend.data.set.filter.SelectionFDSFilterItem;
import com.liferay.object.constants.ObjectEntryFolderConstants;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.service.ObjectDefinitionService;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.util.ArrayUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.osgi.service.component.annotations.Reference;

/**
 * @author Roberto Díaz
 */
public abstract class BaseObjectDefinitionSelectionFDSFilter
	extends BaseSelectionFDSFilter {

	@Override
	public String getEntityFieldType() {
		return FDSEntityFieldTypes.STRING;
	}

	@Override
	public String getId() {
		return "objectDefinitionExternalReferenceCode";
	}

	@Override
	public String getLabel() {
		return "type";
	}

	@Override
	public List<SelectionFDSFilterItem> getSelectionFDSFilterItems(
		Locale locale) {

		List<SelectionFDSFilterItem> selectionFDSFilterItems =
			new ArrayList<>();

		List<ObjectDefinition> objectDefinitions =
			objectDefinitionService.getCMSObjectDefinitions(
				CompanyThreadLocal.getCompanyId(),
				getObjectFolderExternalReferenceCodes());

		String[] excludedObjectDefinitionExternalReferenceCodes =
			getExcludedObjectDefinitionExternalReferenceCodes();

		for (ObjectDefinition objectDefinition : objectDefinitions) {
			if (ArrayUtil.contains(
					excludedObjectDefinitionExternalReferenceCodes,
					objectDefinition.getExternalReferenceCode())) {

				continue;
			}

			selectionFDSFilterItems.add(
				new SelectionFDSFilterItem(
					objectDefinition.getLabel(locale),
					objectDefinition.getExternalReferenceCode()));
		}

		if (!ArrayUtil.contains(
				excludedObjectDefinitionExternalReferenceCodes,
				ObjectEntryFolderConstants.
					EXTERNAL_REFERENCE_CODE_OBJECT_ENTRY_FOLDER)) {

			selectionFDSFilterItems.add(
				new SelectionFDSFilterItem(
					language.get(locale, "folder"),
					ObjectEntryFolderConstants.
						EXTERNAL_REFERENCE_CODE_OBJECT_ENTRY_FOLDER));
		}

		return selectionFDSFilterItems;
	}

	protected String[] getExcludedObjectDefinitionExternalReferenceCodes() {
		return new String[0];
	}

	protected abstract String[] getObjectFolderExternalReferenceCodes();

	@Reference
	protected Language language;

	@Reference
	protected ObjectDefinitionService objectDefinitionService;

}