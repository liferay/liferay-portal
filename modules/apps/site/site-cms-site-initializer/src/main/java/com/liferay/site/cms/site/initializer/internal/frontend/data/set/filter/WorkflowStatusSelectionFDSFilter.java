/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.frontend.data.set.filter;

import com.liferay.frontend.data.set.filter.BaseSelectionFDSFilter;
import com.liferay.frontend.data.set.filter.FDSFilter;
import com.liferay.frontend.data.set.filter.SelectionFDSFilterItem;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.site.cms.site.initializer.internal.constants.CMSSiteInitializerFDSNames;

import java.util.List;
import java.util.Locale;

import org.osgi.service.component.annotations.Component;

/**
 * @author Marco Leo
 */
@Component(
	property = {
		"frontend.data.set.name=" + CMSSiteInitializerFDSNames.ALL_SECTION,
		"frontend.data.set.name=" + CMSSiteInitializerFDSNames.CONTENTS_SECTION,
		"frontend.data.set.name=" + CMSSiteInitializerFDSNames.FILES_SECTION,
		"frontend.data.set.name=" + CMSSiteInitializerFDSNames.STRUCTURE_USAGES,
		"frontend.data.set.name=" + CMSSiteInitializerFDSNames.VIEW_FOLDER
	},
	service = FDSFilter.class
)
public class WorkflowStatusSelectionFDSFilter extends BaseSelectionFDSFilter {

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
				WorkflowConstants.LABEL_APPROVED,
				WorkflowConstants.STATUS_APPROVED),
			new SelectionFDSFilterItem(
				WorkflowConstants.LABEL_DENIED,
				WorkflowConstants.STATUS_DENIED),
			new SelectionFDSFilterItem(
				WorkflowConstants.LABEL_DRAFT, WorkflowConstants.STATUS_DRAFT),
			new SelectionFDSFilterItem(
				WorkflowConstants.LABEL_EXPIRED,
				WorkflowConstants.STATUS_EXPIRED),
			new SelectionFDSFilterItem(
				WorkflowConstants.LABEL_IN_TRASH,
				WorkflowConstants.STATUS_IN_TRASH),
			new SelectionFDSFilterItem(
				WorkflowConstants.LABEL_INACTIVE,
				WorkflowConstants.STATUS_INACTIVE),
			new SelectionFDSFilterItem(
				WorkflowConstants.LABEL_INCOMPLETE,
				WorkflowConstants.STATUS_INCOMPLETE),
			new SelectionFDSFilterItem(
				WorkflowConstants.LABEL_PENDING,
				WorkflowConstants.STATUS_PENDING),
			new SelectionFDSFilterItem(
				WorkflowConstants.LABEL_SCHEDULED,
				WorkflowConstants.STATUS_SCHEDULED));
	}

}