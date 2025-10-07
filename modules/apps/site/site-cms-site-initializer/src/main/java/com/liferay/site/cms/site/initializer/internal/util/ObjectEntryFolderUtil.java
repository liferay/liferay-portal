/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.util;

import com.liferay.object.constants.ObjectEntryFolderConstants;
import com.liferay.object.model.ObjectEntryFolder;
import com.liferay.object.service.ObjectEntryFolderLocalServiceUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;

/**
 * @author Jürgen Kappler
 */
public class ObjectEntryFolderUtil {

	public static void addObjectEntryFolders(long groupId)
		throws PortalException {

		Group group = GroupLocalServiceUtil.fetchGroup(groupId);

		if ((group == null) ||
			!FeatureFlagManagerUtil.isEnabled(
				group.getCompanyId(), "LPD-17564")) {

			return;
		}

		_addObjectEntryFolder(
			ObjectEntryFolderConstants.EXTERNAL_REFERENCE_CODE_CONTENTS, group,
			"Contents", "Contents");
		_addObjectEntryFolder(
			ObjectEntryFolderConstants.EXTERNAL_REFERENCE_CODE_FILES, group,
			"Files", "Files");
	}

	private static void _addObjectEntryFolder(
			String externalReferenceCode, Group group, String label,
			String name)
		throws PortalException {

		ObjectEntryFolder objectEntryFolder =
			ObjectEntryFolderLocalServiceUtil.
				fetchObjectEntryFolderByExternalReferenceCode(
					externalReferenceCode, group.getGroupId(),
					group.getCompanyId());

		if (objectEntryFolder != null) {
			return;
		}

		ObjectEntryFolderLocalServiceUtil.addObjectEntryFolder(
			externalReferenceCode, group.getGroupId(), group.getCreatorUserId(),
			ObjectEntryFolderConstants.PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
			"",
			HashMapBuilder.put(
				LocaleUtil.ENGLISH, label
			).build(),
			name, ServiceContextThreadLocal.getServiceContext());
	}

}