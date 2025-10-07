/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.fragment.renderer;

import com.liferay.fragment.renderer.FragmentRenderer;
import com.liferay.site.cms.site.initializer.internal.display.context.ViewBulkActionTaskReportSuccessfulItemsDisplayContext;

import jakarta.servlet.http.HttpServletRequest;

import org.osgi.service.component.annotations.Component;

/**
 * @author Ivica Cardic
 */
@Component(service = FragmentRenderer.class)
public class ViewBulkActionTaskReportSuccessfulItemsJSPSectionFragmentRenderer
	extends BaseJSPSectionFragmentRenderer
		<ViewBulkActionTaskReportSuccessfulItemsDisplayContext> {

	@Override
	public String getCollectionKey() {
		return "bulk-actions";
	}

	@Override
	public ViewBulkActionTaskReportSuccessfulItemsDisplayContext
		getDisplayContext(HttpServletRequest httpServletRequest) {

		return new ViewBulkActionTaskReportSuccessfulItemsDisplayContext(
			httpServletRequest);
	}

	@Override
	protected String getLabelKey() {
		return "view-bulk-action-task-report-successful-items";
	}

}