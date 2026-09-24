/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.internal.helper;

import com.liferay.document.library.util.DLFileEntryJSONHelper;
import com.liferay.document.library.util.DLURLHelper;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Saurasish Basak
 */
@Component(service = DLFileEntryJSONHelper.class)
public class DLFileEntryJSONHelperImpl implements DLFileEntryJSONHelper {

	@Override
	public String getFileEntryJSON(
			FileEntry fileEntry, JSONObject valueJSONObject)
		throws PortalException {

		Group group = _groupLocalService.fetchGroup(fileEntry.getGroupId());
		String previewURL = _dlURLHelper.getPreviewURL(
			fileEntry, fileEntry.getFileVersion(), null, StringPool.BLANK,
			false, true);

		JSONObject jsonObject = JSONUtil.put(
			"alt", valueJSONObject.getString("alt")
		).put(
			"classNameId",
			_classNameLocalService.getClassNameId(FileEntry.class)
		).put(
			"classPK", fileEntry.getFileEntryId()
		).put(
			"description", valueJSONObject.getString("description")
		).put(
			"extension", fileEntry.getExtension()
		).put(
			"externalReferenceCode", fileEntry.getExternalReferenceCode()
		).put(
			"fileEntryId", fileEntry.getFileEntryId()
		).put(
			"groupExternalReferenceCode",
			() -> {
				if (group == null) {
					return StringPool.BLANK;
				}

				return group.getExternalReferenceCode();
			}
		).put(
			"groupId", fileEntry.getGroupId()
		).put(
			"name", fileEntry.getFileName()
		).put(
			"resourcePrimKey", fileEntry.getPrimaryKey()
		).put(
			"size", fileEntry.getSize()
		).put(
			"title", fileEntry.getTitle()
		).put(
			"type", "document"
		).put(
			"url", previewURL
		).put(
			"uuid", fileEntry.getUuid()
		);

		JSONObject mergedJSONObject = JSONUtil.merge(
			valueJSONObject, jsonObject);

		return mergedJSONObject.toString();
	}

	@Reference
	private ClassNameLocalService _classNameLocalService;

	@Reference
	private DLURLHelper _dlURLHelper;

	@Reference
	private GroupLocalService _groupLocalService;

}