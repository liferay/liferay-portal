/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.pim.site.initializer.internal.frontend.data.set.filter;

import com.liferay.frontend.data.set.constants.FDSEntityFieldTypes;
import com.liferay.frontend.data.set.filter.BaseSelectionFDSFilter;
import com.liferay.frontend.data.set.filter.FDSFilter;
import com.liferay.frontend.data.set.filter.SelectionFDSFilterItem;
import com.liferay.object.service.ObjectDefinitionService;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.site.pim.site.initializer.constants.PIMObjectFolderConstants;
import com.liferay.site.pim.site.initializer.internal.constants.PIMFDSNames;

import java.util.List;
import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Balazs Breier
 */
@Component(
	property = {
		"frontend.data.set.name=" + PIMFDSNames.PRODUCTS,
		"service.ranking:Integer=100"
	},
	service = FDSFilter.class
)
public class ObjectDefinitionSelectionFDSFilter extends BaseSelectionFDSFilter {

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

		return TransformUtil.transform(
			_objectDefinitionService.getCMSObjectDefinitions(
				CompanyThreadLocal.getCompanyId(),
				new String[] {
					PIMObjectFolderConstants.
						EXTERNAL_REFERENCE_CODE_PRODUCT_TYPES
				}),
			objectDefinition -> new SelectionFDSFilterItem(
				objectDefinition.getLabel(locale),
				objectDefinition.getExternalReferenceCode()));
	}

	@Reference
	private ObjectDefinitionService _objectDefinitionService;

}