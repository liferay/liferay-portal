/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.internal.search.spi.model.index.contributor;

import com.liferay.object.constants.ObjectEntryFolderConstants;
import com.liferay.object.model.ObjectEntryFolder;
import com.liferay.object.service.ObjectEntryFolderLocalService;
import com.liferay.petra.string.CharPool;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.search.spi.model.index.contributor.ModelDocumentContributor;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Marco Leo
 */
@Component(
	property = "indexer.class.name=com.liferay.object.model.ObjectEntryFolder",
	service = ModelDocumentContributor.class
)
public class ObjectEntryFolderModelDocumentContributor
	implements ModelDocumentContributor<ObjectEntryFolder> {

	@Override
	public void contribute(
		Document document, ObjectEntryFolder objectEntryFolder) {

		document.addText(Field.DESCRIPTION, objectEntryFolder.getDescription());
		document.addKeyword(
			Field.FOLDER_ID, objectEntryFolder.getParentObjectEntryFolderId());
		document.addText(Field.NAME, objectEntryFolder.getName());
		document.addKeyword(Field.STATUS, objectEntryFolder.getStatus());
		document.addText(Field.TITLE, objectEntryFolder.getName());

		String[] parts = StringUtil.split(
			objectEntryFolder.getTreePath(), CharPool.SLASH);

		document.addKeyword(Field.TREE_PATH, parts);

		String cmsSection = _getCMSSection(parts);

		if (cmsSection != null) {
			document.addKeyword("cms_kind", "folder");
			document.addKeyword("cms_root", parts.length == 3);
			document.addKeyword("cms_section", cmsSection);
		}

		document.addLocalizedKeyword(
			"localized_label", objectEntryFolder.getLabelMap(), true, true);
	}

	private String _getCMSSection(String[] parts) {
		if (parts.length <= 2) {
			return null;
		}

		ObjectEntryFolder objectEntryFolder =
			_objectEntryFolderLocalService.fetchObjectEntryFolder(
				GetterUtil.getLong(parts[1]));

		if (objectEntryFolder == null) {
			return null;
		}

		String externalReferenceCode =
			objectEntryFolder.getExternalReferenceCode();

		if (externalReferenceCode.equals(
				ObjectEntryFolderConstants.EXTERNAL_REFERENCE_CODE_CONTENTS)) {

			return "contents";
		}

		if (externalReferenceCode.equals(
				ObjectEntryFolderConstants.EXTERNAL_REFERENCE_CODE_FILES)) {

			return "files";
		}

		return null;
	}

	@Reference
	private ObjectEntryFolderLocalService _objectEntryFolderLocalService;

}