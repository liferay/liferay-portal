/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.fragment.internal.resource.v1_0.util;

import com.liferay.document.library.kernel.exception.NoSuchFolderException;
import com.liferay.document.library.kernel.model.DLFolder;
import com.liferay.document.library.kernel.service.DLAppServiceUtil;
import com.liferay.document.library.kernel.service.DLFolderLocalServiceUtil;
import com.liferay.fragment.model.FragmentCollection;
import com.liferay.fragment.service.FragmentCollectionServiceUtil;
import com.liferay.headless.admin.fragment.dto.v1_0.ResourceFolder;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.lazy.referencing.LazyReferencingThreadLocal;
import com.liferay.portal.kernel.repository.model.Folder;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.Validator;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Locale;
import java.util.Objects;

/**
 * @author Rubén Pulido
 */
public class ResourceFolderUtil {

	public static DLFolder addDLFolder(
			long companyId, FragmentCollection fragmentCollection, long groupId,
			HttpServletRequest httpServletRequest, Locale locale,
			ResourceFolder resourceFolder, long userId)
		throws Exception {

		DLFolder parentDLFolder = getOrAddParentDLFolder(
			companyId, fragmentCollection, groupId, httpServletRequest, locale,
			resourceFolder.getParentResourceFolder(),
			resourceFolder.getParentResourceFolderExternalReferenceCode(),
			userId);

		ServiceContext serviceContext = ServiceContextUtil.getServiceContext(
			companyId, resourceFolder.getDateCreated(), groupId,
			httpServletRequest, resourceFolder.getDateModified(), userId);

		serviceContext.setAddGroupPermissions(true);
		serviceContext.setAddGuestPermissions(true);

		Folder folder = DLAppServiceUtil.addFolder(
			resourceFolder.getExternalReferenceCode(),
			parentDLFolder.getRepositoryId(), parentDLFolder.getFolderId(),
			resourceFolder.getName(), StringPool.BLANK, serviceContext);

		return DLFolderLocalServiceUtil.getDLFolder(folder.getFolderId());
	}

	public static void checkResourceFolder(DLFolder dlFolder) throws Exception {
		FragmentCollection fragmentCollection =
			FragmentSetUtil.getFragmentCollection(dlFolder);

		if (fragmentCollection == null) {
			throw new NoSuchFolderException(
				"No resource folder exists with external reference code " +
					dlFolder.getExternalReferenceCode());
		}

		FragmentCollectionServiceUtil.getFragmentCollection(
			fragmentCollection.getFragmentCollectionId());
	}

	public static DLFolder getOrAddParentDLFolder(
			long companyId, FragmentCollection fragmentCollection, long groupId,
			HttpServletRequest httpServletRequest, Locale locale,
			ResourceFolder parentResourceFolder,
			String parentResourceFolderExternalReferenceCode, long userId)
		throws Exception {

		if (Validator.isNull(parentResourceFolderExternalReferenceCode)) {
			if (!LazyReferencingThreadLocal.isEnabled() ||
				(parentResourceFolder == null) ||
				Validator.isNull(
					parentResourceFolder.getExternalReferenceCode())) {

				return DLFolderLocalServiceUtil.getDLFolder(
					fragmentCollection.getResourcesFolderId(true));
			}

			parentResourceFolderExternalReferenceCode =
				parentResourceFolder.getExternalReferenceCode();
		}

		DLFolder parentDLFolder =
			DLFolderLocalServiceUtil.fetchDLFolderByExternalReferenceCode(
				parentResourceFolderExternalReferenceCode, groupId);

		if ((parentDLFolder == null) && (parentResourceFolder != null) &&
			LazyReferencingThreadLocal.isEnabled()) {

			if (!Objects.equals(
					parentResourceFolder.getExternalReferenceCode(),
					parentResourceFolderExternalReferenceCode)) {

				throw new IllegalArgumentException(
					LanguageUtil.get(
						locale,
						"the-parent-resource-folder-external-reference-codes-" +
							"do-not-match"));
			}

			parentDLFolder = addDLFolder(
				companyId,
				FragmentSetUtil.getOrAddFragmentCollection(
					companyId, parentResourceFolder.getFragmentSet(),
					parentResourceFolder.getFragmentSetExternalReferenceCode(),
					groupId, httpServletRequest,
					"a-fragment-set-external-reference-code-is-required-to-" +
						"create-a-new-resource-folder",
					locale, userId),
				groupId, httpServletRequest, locale, parentResourceFolder,
				userId);
		}

		if (FragmentSetUtil.getFragmentCollection(parentDLFolder) == null) {
			throw new IllegalArgumentException(
				LanguageUtil.format(
					locale,
					"no-resource-folder-was-found-with-external-reference-" +
						"code-x",
					parentResourceFolderExternalReferenceCode));
		}

		return parentDLFolder;
	}

	public static DLFolder getResourceDLFolder(DLFolder dlFolder) {
		if (dlFolder == null) {
			return null;
		}

		DLFolder parentDLFolder = DLFolderLocalServiceUtil.fetchDLFolder(
			dlFolder.getParentFolderId());

		if ((parentDLFolder == null) || parentDLFolder.isMountPoint()) {
			return null;
		}

		return dlFolder;
	}

}