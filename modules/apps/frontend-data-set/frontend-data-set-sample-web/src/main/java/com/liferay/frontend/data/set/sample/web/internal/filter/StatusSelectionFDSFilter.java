/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.data.set.sample.web.internal.filter;

import com.liferay.frontend.data.set.constants.FDSEntityFieldTypes;
import com.liferay.frontend.data.set.filter.BaseSelectionFDSFilter;
import com.liferay.frontend.data.set.filter.FDSFilter;
import com.liferay.frontend.data.set.filter.SelectionFDSFilterItem;
import com.liferay.frontend.data.set.sample.web.internal.constants.FDSSampleFDSNames;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import java.util.List;
import java.util.Locale;

import org.osgi.service.component.annotations.Component;

/**
 * @author Javier de Arcos
 */
@Component(
	property = {
		"frontend.data.set.name=" + FDSSampleFDSNames.ADVANCED,
		"frontend.data.set.name=" + FDSSampleFDSNames.DELEGATED_FILTERS
	},
	service = FDSFilter.class
)
public class StatusSelectionFDSFilter extends BaseSelectionFDSFilter {

	@Override
	public String getEntityFieldType() {
		return FDSEntityFieldTypes.COLLECTION_INTEGER;
	}

	@Override
	public String getId() {
		return "status";
	}

	@Override
	public String getLabel() {
		return "status";
	}

	@Override
	public List<SelectionFDSFilterItem> getSelectionFDSFilterItems(
		Locale locale) {

		return ListUtil.fromArray(
			new SelectionFDSFilterItem(
				WorkflowConstants.getStatusLabel(
					WorkflowConstants.STATUS_APPROVED),
				WorkflowConstants.STATUS_APPROVED),
			new SelectionFDSFilterItem(
				WorkflowConstants.getStatusLabel(
					WorkflowConstants.STATUS_DRAFT),
				WorkflowConstants.STATUS_DRAFT),
			new SelectionFDSFilterItem(
				WorkflowConstants.getStatusLabel(
					WorkflowConstants.STATUS_PENDING),
				WorkflowConstants.STATUS_PENDING));
	}

}