/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.frontend.data.set.filter;

import com.liferay.frontend.data.set.constants.FDSEntityFieldTypes;
import com.liferay.frontend.data.set.filter.BaseSelectionFDSFilter;
import com.liferay.frontend.data.set.filter.FDSFilter;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.site.cms.site.initializer.internal.constants.CMSSiteInitializerFDSNames;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Pedro Leite
 */
@Component(
	property = {
		"frontend.data.set.name=" + CMSSiteInitializerFDSNames.ALL_SECTION,
		"frontend.data.set.name=" + CMSSiteInitializerFDSNames.CONTENTS_SECTION,
		"frontend.data.set.name=" + CMSSiteInitializerFDSNames.FILES_SECTION,
		"service.ranking:Integer=88"
	},
	service = FDSFilter.class
)
public class CMPProjectSelectionFDSFilter extends BaseSelectionFDSFilter {

	@Override
	public String getAPIURL() {
		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.
				fetchObjectDefinitionByExternalReferenceCode(
					"L_CMP_PROJECT", CompanyThreadLocal.getCompanyId());

		if (objectDefinition == null) {
			return null;
		}

		StringBundler sb = new StringBundler(4);

		sb.append("/o/search/v1.0/search?emptySearch=true&");
		sb.append("filter=objectDefinitionId eq ");
		sb.append(objectDefinition.getObjectDefinitionId());
		sb.append("&nestedFields=embedded");

		return sb.toString();
	}

	@Override
	public String getEntityFieldType() {
		return FDSEntityFieldTypes.COLLECTION_INTEGER;
	}

	@Override
	public String getId() {
		return "cmpProjectObjectEntryIds";
	}

	@Override
	public String getItemKey() {
		return "embedded.id";
	}

	@Override
	public String getItemLabel() {
		return "embedded.title";
	}

	@Override
	public String getLabel() {
		return "project";
	}

	@Override
	public boolean isAutocompleteEnabled() {
		return true;
	}

	@Reference
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

}