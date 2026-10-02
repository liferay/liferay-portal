/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.util;

import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.service.DLFileEntryLocalServiceUtil;
import com.liferay.friendly.url.model.FriendlyURLEntry;
import com.liferay.friendly.url.model.FriendlyURLEntryLocalization;
import com.liferay.friendly.url.service.FriendlyURLEntryLocalServiceUtil;
import com.liferay.object.constants.ObjectFieldConstants;
import com.liferay.object.constants.ObjectFolderConstants;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.ObjectField;
import com.liferay.object.service.ObjectFieldLocalServiceUtil;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.ModelHintsUtil;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.FriendlyURLNormalizerUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

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

	public static boolean isUrlTitleAvailable(
		long groupId, ObjectDefinition objectDefinition, long objectEntryId,
		String urlTitle) {

		FriendlyURLEntry objectEntryFriendlyURLEntry =
			FriendlyURLEntryLocalServiceUtil.fetchFriendlyURLEntry(
				groupId,
				PortalUtil.getClassNameId(objectDefinition.getClassName()),
				urlTitle);

		if ((objectEntryFriendlyURLEntry != null) &&
			(objectEntryFriendlyURLEntry.getClassPK() != objectEntryId)) {

			return false;
		}

		FriendlyURLEntry fileEntryFriendlyURLEntry =
			FriendlyURLEntryLocalServiceUtil.fetchFriendlyURLEntry(
				groupId, PortalUtil.getClassNameId(FileEntry.class), urlTitle);

		if ((fileEntryFriendlyURLEntry == null) ||
			isObjectEntryAttachment(
				DLFileEntryLocalServiceUtil.fetchDLFileEntry(
					fileEntryFriendlyURLEntry.getClassPK()),
				objectDefinition, objectEntryId)) {

			return true;
		}

		return false;
	}

	public static void updateFileEntryFriendlyURL(
			ObjectDefinition objectDefinition, ObjectEntry objectEntry)
		throws PortalException {

		if (objectEntry.getStatus() != WorkflowConstants.STATUS_APPROVED) {
			return;
		}

		Map<String, Serializable> values = objectEntry.getValues();

		DLFileEntry dlFileEntry = DLFileEntryLocalServiceUtil.fetchDLFileEntry(
			GetterUtil.getLong(values.get("file")));

		if (!isObjectEntryAttachment(
				dlFileEntry, objectDefinition,
				objectEntry.getObjectEntryId())) {

			return;
		}

		long objectEntryClassNameId = PortalUtil.getClassNameId(
			objectDefinition.getClassName());

		FriendlyURLEntry objectEntryFriendlyURLEntry =
			FriendlyURLEntryLocalServiceUtil.fetchMainFriendlyURLEntry(
				objectEntryClassNameId, objectEntry.getObjectEntryId());

		if (objectEntryFriendlyURLEntry == null) {
			return;
		}

		String urlTitle = objectEntryFriendlyURLEntry.getUrlTitle(
			objectEntry.getDefaultLanguageId());

		if (Validator.isNull(urlTitle)) {
			return;
		}

		long fileEntryClassNameId = PortalUtil.getClassNameId(FileEntry.class);

		FriendlyURLEntry fileEntryFriendlyURLEntry =
			FriendlyURLEntryLocalServiceUtil.fetchMainFriendlyURLEntry(
				fileEntryClassNameId, dlFileEntry.getFileEntryId());

		if ((fileEntryFriendlyURLEntry != null) &&
			urlTitle.equals(fileEntryFriendlyURLEntry.getUrlTitle())) {

			return;
		}

		long groupId = objectEntry.getNonzeroGroupId();

		String uniqueUrlTitle = _getUniqueUrlTitle(
			groupId, objectDefinition, objectEntry.getObjectEntryId(),
			urlTitle);

		if (!uniqueUrlTitle.equals(urlTitle)) {
			FriendlyURLEntryLocalServiceUtil.addFriendlyURLEntry(
				groupId, objectEntryClassNameId, objectEntry.getObjectEntryId(),
				objectEntry.getDefaultLanguageId(),
				HashMapBuilder.putAll(
					objectEntryFriendlyURLEntry.getLanguageIdToUrlTitleMap()
				).put(
					objectEntry.getDefaultLanguageId(), uniqueUrlTitle
				).build(),
				new ServiceContext());

			if (_log.isWarnEnabled()) {
				_log.warn(
					StringBundler.concat(
						"Changed the friendly URL of object entry ",
						objectEntry.getObjectEntryId(), " from \"", urlTitle,
						"\" to \"", uniqueUrlTitle, "\""));
			}
		}

		for (long fileEntryId :
				_getPreviousFileEntryIds(
					dlFileEntry, fileEntryClassNameId, objectDefinition,
					objectEntry, objectEntryClassNameId)) {

			_moveFriendlyURLEntries(
				fileEntryClassNameId, fileEntryId, dlFileEntry.getGroupId(),
				dlFileEntry.getFileEntryId());
		}

		FriendlyURLEntryLocalServiceUtil.addFriendlyURLEntry(
			dlFileEntry.getGroupId(), fileEntryClassNameId,
			dlFileEntry.getFileEntryId(), objectEntry.getDefaultLanguageId(),
			HashMapBuilder.put(
				objectEntry.getDefaultLanguageId(), uniqueUrlTitle
			).build(),
			new ServiceContext());
	}

	private static Set<Long> _getPreviousFileEntryIds(
			DLFileEntry dlFileEntry, long fileEntryClassNameId,
			ObjectDefinition objectDefinition, ObjectEntry objectEntry,
			long objectEntryClassNameId)
		throws PortalException {

		Set<Long> fileEntryIds = new LinkedHashSet<>();

		for (FriendlyURLEntry objectEntryFriendlyURLEntry :
				FriendlyURLEntryLocalServiceUtil.getFriendlyURLEntries(
					objectEntry.getNonzeroGroupId(), objectEntryClassNameId,
					objectEntry.getObjectEntryId())) {

			Map<String, String> languageIdToUrlTitleMap =
				objectEntryFriendlyURLEntry.getLanguageIdToUrlTitleMap();

			for (String urlTitle : languageIdToUrlTitleMap.values()) {
				FriendlyURLEntry fileEntryFriendlyURLEntry =
					FriendlyURLEntryLocalServiceUtil.fetchFriendlyURLEntry(
						dlFileEntry.getGroupId(), fileEntryClassNameId,
						urlTitle);

				if ((fileEntryFriendlyURLEntry != null) &&
					(fileEntryFriendlyURLEntry.getClassPK() !=
						dlFileEntry.getFileEntryId()) &&
					isObjectEntryAttachment(
						DLFileEntryLocalServiceUtil.fetchDLFileEntry(
							fileEntryFriendlyURLEntry.getClassPK()),
						objectDefinition, objectEntry.getObjectEntryId())) {

					fileEntryIds.add(fileEntryFriendlyURLEntry.getClassPK());
				}
			}
		}

		return fileEntryIds;
	}

	private static String _getUniqueUrlTitle(
		long groupId, ObjectDefinition objectDefinition, long objectEntryId,
		String urlTitle) {

		int maxLength = ModelHintsUtil.getMaxLength(
			FriendlyURLEntryLocalization.class.getName(), "urlTitle");

		String curUrlTitle = urlTitle;

		for (int i = 1;
			 !isUrlTitleAvailable(
				 groupId, objectDefinition, objectEntryId, curUrlTitle);
			 i++) {

			String suffix = StringPool.DASH + i;

			String prefix = urlTitle;

			if ((prefix.length() + suffix.length()) > maxLength) {
				prefix = prefix.substring(0, maxLength - suffix.length());
			}

			curUrlTitle = FriendlyURLNormalizerUtil.normalizeWithEncoding(
				prefix + suffix);
		}

		return curUrlTitle;
	}

	private static void _moveFriendlyURLEntries(
			long classNameId, long fromClassPK, long groupId, long toClassPK)
		throws PortalException {

		List<FriendlyURLEntry> friendlyURLEntries = new ArrayList<>(
			FriendlyURLEntryLocalServiceUtil.getFriendlyURLEntries(
				groupId, classNameId, fromClassPK));

		friendlyURLEntries.sort(
			Comparator.comparingLong(FriendlyURLEntry::getFriendlyURLEntryId));

		List<Map<String, String>> languageIdToUrlTitleMaps =
			TransformUtil.transform(
				friendlyURLEntries,
				FriendlyURLEntry::getLanguageIdToUrlTitleMap);

		FriendlyURLEntryLocalServiceUtil.deleteFriendlyURLEntry(
			groupId, classNameId, fromClassPK);

		for (int i = 0; i < friendlyURLEntries.size(); i++) {
			FriendlyURLEntry friendlyURLEntry = friendlyURLEntries.get(i);

			FriendlyURLEntryLocalServiceUtil.addFriendlyURLEntry(
				groupId, classNameId, toClassPK,
				friendlyURLEntry.getDefaultLanguageId(),
				languageIdToUrlTitleMaps.get(i), new ServiceContext());
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CMSFileTypeUtil.class);

}