/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.style.book.web.internal.display.context;

import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.style.book.zip.processor.StyleBookEntryZipProcessorImportResultEntry;

import jakarta.portlet.RenderRequest;

import java.util.List;

/**
 * @author Eudaldo Alonso
 */
public class ImportStyleBookDisplayContext {

	public ImportStyleBookDisplayContext(RenderRequest renderRequest) {
		_renderRequest = renderRequest;
	}

	public List<String> getStyleBookEntryZipProcessorImportResultEntryNames(
		StyleBookEntryZipProcessorImportResultEntry.Status status) {

		List<StyleBookEntryZipProcessorImportResultEntry>
			styleBookEntryZipProcessorImportResultEntries =
				_getStyleBookEntryZipProcessorImportResultEntryNames();

		if (ListUtil.isEmpty(styleBookEntryZipProcessorImportResultEntries)) {
			return null;
		}

		return TransformUtil.transform(
			styleBookEntryZipProcessorImportResultEntries,
			styleBookEntryZipProcessorImportResultEntry -> {
				if (styleBookEntryZipProcessorImportResultEntry.getStatus() !=
						status) {

					return null;
				}

				return styleBookEntryZipProcessorImportResultEntry.getName();
			});
	}

	private List<StyleBookEntryZipProcessorImportResultEntry>
		_getStyleBookEntryZipProcessorImportResultEntryNames() {

		if (_styleBookEntryZipProcessorImportResultEntries != null) {
			return _styleBookEntryZipProcessorImportResultEntries;
		}

		_styleBookEntryZipProcessorImportResultEntries =
			(List<StyleBookEntryZipProcessorImportResultEntry>)
				SessionMessages.get(
					_renderRequest,
					"styleBookEntryZipProcessorImportResultEntries");

		return _styleBookEntryZipProcessorImportResultEntries;
	}

	private final RenderRequest _renderRequest;
	private List<StyleBookEntryZipProcessorImportResultEntry>
		_styleBookEntryZipProcessorImportResultEntries;

}