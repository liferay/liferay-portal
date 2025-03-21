/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.page.template.admin.web.internal.portlet.action;

import com.liferay.layout.exporter.LayoutsExporter;
import com.liferay.layout.page.template.admin.constants.LayoutPageTemplateAdminPortletKeys;
import com.liferay.layout.page.template.constants.LayoutPageTemplateEntryTypeConstants;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryLocalService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.portlet.PortletResponseUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Time;

import jakarta.portlet.PortletException;
import jakarta.portlet.ResourceRequest;
import jakarta.portlet.ResourceResponse;

import java.io.File;
import java.io.FileInputStream;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Rubén Pulido
 */
@Component(
	property = {
		"jakarta.portlet.name=" + LayoutPageTemplateAdminPortletKeys.LAYOUT_PAGE_TEMPLATES,
		"mvc.command.name=/layout_page_template_admin/export_display_pages"
	},
	service = MVCResourceCommand.class
)
public class ExportDisplayPagesMVCResourceCommand
	implements MVCResourceCommand {

	public File getFile(long[] layoutPageTemplateEntryIds)
		throws PortletException {

		try {
			return _layoutsExporter.exportLayoutPageTemplateEntries(
				layoutPageTemplateEntryIds,
				LayoutPageTemplateEntryTypeConstants.DISPLAY_PAGE);
		}
		catch (Exception exception) {
			throw new PortletException(exception);
		}
	}

	public String getFileName(long[] layoutPageTemplateEntryIds)
		throws PortalException {

		String fileNamePrefix = "display-page-templates-";

		if (layoutPageTemplateEntryIds.length == 1) {
			LayoutPageTemplateEntry layoutPageTemplateEntry =
				_layoutPageTemplateEntryLocalService.getLayoutPageTemplateEntry(
					layoutPageTemplateEntryIds[0]);

			fileNamePrefix =
				"display-page-template-" +
					layoutPageTemplateEntry.getLayoutPageTemplateEntryKey() +
						"-";
		}

		return fileNamePrefix + Time.getShortTimestamp() + ".zip";
	}

	public long[] getLayoutPageTemplateEntryIds(
		ResourceRequest resourceRequest) {

		long[] layoutPageTemplateEntryIds = null;

		long layoutPageTemplateEntryEntryId = ParamUtil.getLong(
			resourceRequest, "layoutPageTemplateEntryId");

		if (layoutPageTemplateEntryEntryId > 0) {
			layoutPageTemplateEntryIds = new long[] {
				layoutPageTemplateEntryEntryId
			};
		}
		else {
			layoutPageTemplateEntryIds = ParamUtil.getLongValues(
				resourceRequest, "rowIds");
		}

		return layoutPageTemplateEntryIds;
	}

	@Override
	public boolean serveResource(
			ResourceRequest resourceRequest, ResourceResponse resourceResponse)
		throws PortletException {

		long[] layoutPageTemplateEntryIds = getLayoutPageTemplateEntryIds(
			resourceRequest);

		if (layoutPageTemplateEntryIds.length == 0) {
			return false;
		}

		try {
			PortletResponseUtil.sendFile(
				resourceRequest, resourceResponse,
				getFileName(layoutPageTemplateEntryIds),
				new FileInputStream(getFile(layoutPageTemplateEntryIds)),
				ContentTypes.APPLICATION_ZIP);
		}
		catch (Exception exception) {
			throw new PortletException(exception);
		}

		return false;
	}

	@Reference
	private LayoutPageTemplateEntryLocalService
		_layoutPageTemplateEntryLocalService;

	@Reference
	private LayoutsExporter _layoutsExporter;

}