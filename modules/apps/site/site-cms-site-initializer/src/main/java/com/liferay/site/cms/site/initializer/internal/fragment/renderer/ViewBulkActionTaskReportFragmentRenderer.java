/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.fragment.renderer;

import com.liferay.fragment.renderer.FragmentRenderer;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.site.cms.site.initializer.internal.display.context.ViewBulkActionTaskReportDisplayContext;

import jakarta.servlet.http.HttpServletRequest;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Luca Pellizzon
 */
@Component(service = FragmentRenderer.class)
public class ViewBulkActionTaskReportFragmentRenderer
	extends BaseJSPSectionFragmentRenderer
		<ViewBulkActionTaskReportDisplayContext> {

	@Override
	public String getCollectionKey() {
		return "bulk-actions";
	}

	@Override
	protected ViewBulkActionTaskReportDisplayContext getDisplayContext(
			HttpServletRequest httpServletRequest)
		throws PortalException {

		return new ViewBulkActionTaskReportDisplayContext(
			httpServletRequest, _language, _objectDefinitionLocalService);
	}

	@Override
	protected String getLabelKey() {
		return "view-bulk-action-task-report";
	}

	@Reference
	private Language _language;

	@Reference
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

}