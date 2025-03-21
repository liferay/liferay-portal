/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.page.template.admin.web.internal.struts;

import com.liferay.fragment.model.FragmentCollection;
import com.liferay.fragment.service.FragmentCollectionService;
import com.liferay.layout.exporter.LayoutsExporter;
import com.liferay.portal.kernel.servlet.ServletResponseUtil;
import com.liferay.portal.kernel.struts.StrutsAction;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.kernel.zip.ZipWriter;
import com.liferay.portal.kernel.zip.ZipWriterFactory;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Jürgen Kappler
 */
@Component(
	property = "path=/portal/layout_page_template/export_layout_page_template_entries",
	service = StrutsAction.class
)
public class ExportLayoutPageTemplateEntriesStrutsAction
	implements StrutsAction {

	@Override
	public String execute(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws Exception {

		long groupId = ParamUtil.getLong(httpServletRequest, "groupId");

		File file = _layoutsExporter.exportLayoutPageTemplateEntries(groupId);

		ZipWriter zipWriter = _zipWriterFactory.getZipWriter(file);

		List<FragmentCollection> fragmentCollections =
			_fragmentCollectionService.getFragmentCollections(groupId);

		for (FragmentCollection fragmentCollection : fragmentCollections) {
			fragmentCollection.populateZipWriter(zipWriter, "fragments");
		}

		try {
			file = zipWriter.getFile();

			try (InputStream inputStream = new FileInputStream(file)) {
				ServletResponseUtil.sendFile(
					httpServletRequest, httpServletResponse,
					"page-templates-" + Time.getTimestamp() + ".zip",
					inputStream, file.length(), ContentTypes.APPLICATION_ZIP);
			}
		}
		catch (Exception exception) {
			_portal.sendError(
				exception, httpServletRequest, httpServletResponse);
		}

		return null;
	}

	@Reference
	private FragmentCollectionService _fragmentCollectionService;

	@Reference
	private LayoutsExporter _layoutsExporter;

	@Reference
	private Portal _portal;

	@Reference
	private ZipWriterFactory _zipWriterFactory;

}