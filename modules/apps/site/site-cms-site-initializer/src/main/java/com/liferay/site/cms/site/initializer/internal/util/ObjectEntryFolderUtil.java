/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.util;

import com.liferay.depot.model.DepotEntry;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.util.DLAppHelperThreadLocal;
import com.liferay.object.constants.ObjectEntryFolderConstants;
import com.liferay.object.entry.folder.util.ObjectEntryFolderThreadLocal;
import com.liferay.object.field.attachment.AttachmentManager;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntryFolder;
import com.liferay.object.service.ObjectDefinitionLocalServiceUtil;
import com.liferay.object.service.ObjectEntryFolderLocalServiceUtil;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Repository;
import com.liferay.portal.kernel.service.RepositoryLocalServiceUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.TempFileEntryUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.repository.temporaryrepository.TemporaryFileEntryRepository;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * @author Jürgen Kappler
 * @author Roberto Díaz
 */
public class ObjectEntryFolderUtil {

	public static void addObjectEntryFolders(
			DepotEntry depotEntry, AttachmentManager attachmentManager)
		throws PortalException {

		_addObjectEntryFolder(
			ObjectEntryFolderConstants.EXTERNAL_REFERENCE_CODE_CONTENTS,
			depotEntry.getGroup(), "contents", "Contents");
		_addObjectEntryFolder(
			ObjectEntryFolderConstants.EXTERNAL_REFERENCE_CODE_FILES,
			depotEntry.getGroup(), "files", "Files");

		_addObjectEntryFolderFileRepository(
			depotEntry.getGroup(), attachmentManager);
	}

	public static void deleteObjectEntryFolders(DepotEntry depotEntry)
		throws PortalException {

		try (SafeCloseable safeCloseable =
				ObjectEntryFolderThreadLocal.
					setForceDeleteSystemObjectEntryFolderWithSafeCloseable(
						true)) {

			_deleteObjectEntryFolder(
				ObjectEntryFolderConstants.EXTERNAL_REFERENCE_CODE_CONTENTS,
				depotEntry);
			_deleteObjectEntryFolder(
				ObjectEntryFolderConstants.EXTERNAL_REFERENCE_CODE_FILES,
				depotEntry);
		}
	}

	private static void _addObjectEntryFolder(
			String externalReferenceCode, Group group, String labelKey,
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

		Map<Locale, String> labels = new HashMap<>();

		Set<Locale> locales = LanguageUtil.getAvailableLocales(
			group.getGroupId());

		for (Locale locale : locales) {
			labels.put(locale, LanguageUtil.get(locale, labelKey, name));
		}

		ObjectEntryFolderLocalServiceUtil.addObjectEntryFolder(
			externalReferenceCode, group.getGroupId(), group.getCreatorUserId(),
			ObjectEntryFolderConstants.PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
			"", labels, name, ServiceContextThreadLocal.getServiceContext());
	}

	private static void _addObjectEntryFolderFileRepository(
			Group group, AttachmentManager attachmentManager)
		throws PortalException {

		ObjectDefinition objectDefinition =
			ObjectDefinitionLocalServiceUtil.
				fetchObjectDefinitionByExternalReferenceCode(
					"L_CMS_BASIC_DOCUMENT", group.getCompanyId());

		if (objectDefinition == null) {
			return;
		}

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		if (serviceContext == null) {
			serviceContext = new ServiceContext();
		}

		attachmentManager.getDLFolder(
			objectDefinition.getCompanyId(), group.getGroupId(),
			objectDefinition.getPortletId(), serviceContext,
			group.getCreatorUserId());

		try (SafeCloseable safeCloseable =
				DLAppHelperThreadLocal.setEnabledWithSafeCloseable(false)) {

			Repository repository = RepositoryLocalServiceUtil.fetchRepository(
				group.getGroupId(), TempFileEntryUtil.class.getName(),
				TempFileEntryUtil.class.getName());

			if (repository != null) {
				return;
			}

			RepositoryLocalServiceUtil.addRepository(
				null, group.getCreatorUserId(), group.getGroupId(),
				PortalUtil.getClassNameId(
					TemporaryFileEntryRepository.class.getName()),
				DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
				TempFileEntryUtil.class.getName(), StringPool.BLANK,
				TempFileEntryUtil.class.getName(), new UnicodeProperties(),
				true, serviceContext);
		}
	}

	private static void _deleteObjectEntryFolder(
			String externalReferenceCode, DepotEntry depotEntry)
		throws PortalException {

		ObjectEntryFolder objectEntryFolder =
			ObjectEntryFolderLocalServiceUtil.
				fetchObjectEntryFolderByExternalReferenceCode(
					externalReferenceCode, depotEntry.getGroupId(),
					depotEntry.getCompanyId());

		if (objectEntryFolder != null) {
			ObjectEntryFolderLocalServiceUtil.deleteObjectEntryFolder(
				objectEntryFolder);
		}
	}

}