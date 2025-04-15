/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.page.template.admin.web.internal.portlet.action;

import com.liferay.layout.exporter.LayoutsExporter;
import com.liferay.layout.page.template.admin.constants.LayoutPageTemplateAdminPortletKeys;
import com.liferay.portal.kernel.portlet.PortletResponseUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Time;

import jakarta.portlet.PortletException;
import jakarta.portlet.ResourceRequest;
import jakarta.portlet.ResourceResponse;

import java.io.FileInputStream;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Bárbara Cabrera
 */
@Component(
	property = {
		"jakarta.portlet.name=" + LayoutPageTemplateAdminPortletKeys.LAYOUT_PAGE_TEMPLATES,
		"mvc.command.name=/layout_page_template_admin/export_layout_page_template_entries_and_layout_page_template_collections"
	},
	service = MVCResourceCommand.class
)
public class
	ExportLayoutPageTemplateEntriesAndLayoutPageTemplateCollectionsMVCResourceCommand
		implements MVCResourceCommand {

	@Override
	public boolean serveResource(
			ResourceRequest resourceRequest, ResourceResponse resourceResponse)
		throws PortletException {

		try {
			long[] layoutPageTemplateCollectionsIds = ParamUtil.getLongValues(
				resourceRequest, "layoutPageTemplateCollectionsIds");
			long[] layoutPageTemplateEntriesIds = ParamUtil.getLongValues(
				resourceRequest, "layoutPageTemplateEntriesIds");

			if ((layoutPageTemplateEntriesIds.length == 0) &&
				(layoutPageTemplateCollectionsIds.length == 0)) {

				return false;
			}

			PortletResponseUtil.sendFile(
				resourceRequest, resourceResponse,
				"display-page-entries-" + Time.getTimestamp() + ".zip",
				new FileInputStream(
					_layoutsExporter.
						exportLayoutPageTemplateEntriesAndLayoutPageTemplateCollections(
							layoutPageTemplateEntriesIds,
							layoutPageTemplateCollectionsIds)),
				ContentTypes.APPLICATION_ZIP);

			return false;
		}
		catch (Exception exception) {
			throw new PortletException(exception);
		}
	}

	@Reference
	private LayoutsExporter _layoutsExporter;

}