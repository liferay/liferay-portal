/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.importer;

import com.liferay.fragment.model.FragmentEntryLink;
import com.liferay.layout.util.structure.LayoutStructure;
import com.liferay.portal.kernel.model.Layout;

import java.io.File;

import java.util.List;

/**
 * @author Jürgen Kappler
 */
public interface LayoutsImporter {

	public void importFile(
			long userId, long groupId, File file,
			LayoutsImportStrategy layoutsImportStrategy,
			boolean preserveItemIds)
		throws Exception;

	public List<LayoutsImporterResultEntry> importFile(
			long userId, long groupId, long layoutPageTemplateCollectionId,
			File file, LayoutsImportStrategy layoutsImportStrategy,
			boolean preserveItemIds)
		throws Exception;

	public Layout importLayoutSettings(
			long userId, Layout layout, String settingsJSON)
		throws Exception;

	public List<FragmentEntryLink> importPageElement(
			Layout layout, LayoutStructure layoutStructure, String parentItemId,
			String pageElementJSON, int position, boolean preserveItemIds)
		throws Exception;

	public List<FragmentEntryLink> importPageElement(
			long userId, Layout layout, LayoutStructure layoutStructure,
			String parentItemId, String pageElementJSON, int position,
			boolean preserveItemIds, long segmentsExperienceId)
		throws Exception;

	public boolean validateFile(
			long groupId, long layoutPageTemplateCollectionId, File file)
		throws Exception;

}