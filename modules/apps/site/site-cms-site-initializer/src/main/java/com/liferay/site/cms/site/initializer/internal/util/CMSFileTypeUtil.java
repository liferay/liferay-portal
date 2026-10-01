/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.util;

import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.object.constants.ObjectFieldConstants;
import com.liferay.object.constants.ObjectFolderConstants;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectField;
import com.liferay.object.service.ObjectFieldLocalServiceUtil;
import com.liferay.portal.kernel.util.PortalUtil;

import java.util.Objects;

/**
 * @author Mikel Lorza
 */
public class CMSFileTypeUtil {

	public static boolean hasFileObjectField(
		ObjectDefinition objectDefinition) {

		if (!Objects.equals(
				objectDefinition.getObjectFolderExternalReferenceCode(),
				ObjectFolderConstants.EXTERNAL_REFERENCE_CODE_FILE_TYPES)) {

			return false;
		}

		ObjectField objectField = ObjectFieldLocalServiceUtil.fetchObjectField(
			objectDefinition.getObjectDefinitionId(), "file");

		if ((objectField != null) && objectField.isSystem() &&
			Objects.equals(
				objectField.getBusinessType(),
				ObjectFieldConstants.BUSINESS_TYPE_ATTACHMENT)) {

			return true;
		}

		return false;
	}

	public static boolean isObjectEntryAttachment(
		DLFileEntry dlFileEntry, ObjectDefinition objectDefinition,
		long objectEntryId) {

		if ((dlFileEntry != null) && (objectEntryId > 0) &&
			(dlFileEntry.getClassNameId() == PortalUtil.getClassNameId(
				objectDefinition.getClassName())) &&
			(dlFileEntry.getClassPK() == objectEntryId)) {

			return true;
		}

		return false;
	}

}