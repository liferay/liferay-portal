/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.ai.hub.web.internal.frontend.data.set.filter;

import com.liferay.ai.hub.web.internal.constants.AIHubFDSNames;
import com.liferay.frontend.data.set.filter.BaseSelectionFDSFilter;
import com.liferay.frontend.data.set.filter.FDSFilter;
import com.liferay.frontend.data.set.filter.SelectionFDSFilterItem;
import com.liferay.list.type.model.ListTypeDefinition;
import com.liferay.list.type.service.ListTypeDefinitionLocalService;
import com.liferay.list.type.service.ListTypeEntryLocalService;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Davyson Melo
 */
@Component(
	property = "frontend.data.set.name=" + AIHubFDSNames.ISSUE_REPORTS,
	service = FDSFilter.class
)
public class IssueReportLevelSelectionFDSFilter extends BaseSelectionFDSFilter {

	@Override
	public String getId() {
		return "level";
	}

	@Override
	public String getLabel() {
		return "level";
	}

	@Override
	public List<SelectionFDSFilterItem> getSelectionFDSFilterItems(
		Locale locale) {

		ListTypeDefinition listTypeDefinition =
			_listTypeDefinitionLocalService.
				fetchListTypeDefinitionByExternalReferenceCode(
					"L_AI_HUB_REPORT_LEVELS",
					CompanyThreadLocal.getCompanyId());

		if (listTypeDefinition == null) {
			return Collections.emptyList();
		}

		return TransformUtil.transform(
			_listTypeEntryLocalService.getListTypeEntries(
				listTypeDefinition.getListTypeDefinitionId(), QueryUtil.ALL_POS,
				QueryUtil.ALL_POS),
			listTypeEntry -> new SelectionFDSFilterItem(
				listTypeEntry.getName(locale), listTypeEntry.getKey()));
	}

	@Reference
	private ListTypeDefinitionLocalService _listTypeDefinitionLocalService;

	@Reference
	private ListTypeEntryLocalService _listTypeEntryLocalService;

}