/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.style.book.zip.processor;

import com.liferay.style.book.model.StyleBookEntry;

import jakarta.portlet.PortletException;

import java.io.File;

import java.util.List;

/**
 * @author Jürgen Kappler
 */
public interface StyleBookEntryZipProcessor {

	public File exportStyleBookEntries(List<StyleBookEntry> styleBookEntries)
		throws PortletException;

	public List<StyleBookEntryZipProcessorImportResultEntry>
			importStyleBookEntries(
				long userId, long groupId, File file, boolean overwrite)
		throws Exception;

}