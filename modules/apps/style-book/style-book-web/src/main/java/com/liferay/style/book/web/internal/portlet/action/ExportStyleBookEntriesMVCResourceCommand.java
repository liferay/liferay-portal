/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.style.book.web.internal.portlet.action;

import com.liferay.portal.kernel.portlet.PortletResponseUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Time;
import com.liferay.style.book.constants.StyleBookPortletKeys;
import com.liferay.style.book.model.StyleBookEntry;
import com.liferay.style.book.service.StyleBookEntryLocalService;
import com.liferay.style.book.zip.processor.StyleBookEntryZipProcessor;

import jakarta.portlet.PortletException;
import jakarta.portlet.ResourceRequest;
import jakarta.portlet.ResourceResponse;

import java.io.File;
import java.io.FileInputStream;

import java.util.ArrayList;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eudaldo Alonso
 */
@Component(
	property = {
		"jakarta.portlet.name=" + StyleBookPortletKeys.STYLE_BOOK,
		"mvc.command.name=/style_book/export_style_book_entries"
	},
	service = MVCResourceCommand.class
)
public class ExportStyleBookEntriesMVCResourceCommand
	implements MVCResourceCommand {

	@Override
	public boolean serveResource(
			ResourceRequest resourceRequest, ResourceResponse resourceResponse)
		throws PortletException {

		long[] exportStyleBookEntryIds = null;

		long styleBookEntryId = ParamUtil.getLong(
			resourceRequest, "styleBookEntryId");

		if (styleBookEntryId > 0) {
			exportStyleBookEntryIds = new long[] {styleBookEntryId};
		}
		else {
			exportStyleBookEntryIds = ParamUtil.getLongValues(
				resourceRequest, "rowIds");
		}

		try {
			File file = _exportStyleBookEntries(exportStyleBookEntryIds);

			PortletResponseUtil.sendFile(
				resourceRequest, resourceResponse,
				"style-book-entries-" + Time.getTimestamp() + ".zip",
				new FileInputStream(file), ContentTypes.APPLICATION_ZIP);
		}
		catch (Exception exception) {
			throw new PortletException(exception);
		}

		return false;
	}

	private File _exportStyleBookEntries(long[] exportStyleBookEntryIds)
		throws Exception {

		List<StyleBookEntry> styleBookEntries = new ArrayList<>();

		if (ArrayUtil.isNotEmpty(exportStyleBookEntryIds)) {
			for (long exportStyleBookEntryId : exportStyleBookEntryIds) {
				styleBookEntries.add(
					_styleBookEntryLocalService.fetchStyleBookEntry(
						exportStyleBookEntryId));
			}
		}

		return _styleBookEntryZipProcessor.exportStyleBookEntries(
			styleBookEntries);
	}

	@Reference
	private StyleBookEntryLocalService _styleBookEntryLocalService;

	@Reference
	private StyleBookEntryZipProcessor _styleBookEntryZipProcessor;

}