/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.fragment.internal.resource.v1_0.util;

import com.liferay.document.library.kernel.model.DLFolder;
import com.liferay.document.library.kernel.service.DLFolderLocalServiceUtil;
import com.liferay.fragment.model.FragmentCollection;
import com.liferay.fragment.service.FragmentCollectionLocalServiceUtil;
import com.liferay.fragment.service.FragmentCollectionServiceUtil;
import com.liferay.headless.admin.fragment.dto.v1_0.FragmentSet;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.lazy.referencing.LazyReferencingThreadLocal;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Validator;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Locale;
import java.util.Objects;

/**
 * @author Rubén Pulido
 */
public class FragmentSetUtil {

	public static FragmentCollection addFragmentCollection(
			FragmentSet fragmentSet, ServiceContext serviceContext)
		throws Exception {

		return FragmentCollectionServiceUtil.addFragmentCollection(
			fragmentSet.getExternalReferenceCode(),
			serviceContext.getScopeGroupId(), fragmentSet.getKey(),
			fragmentSet.getName(), fragmentSet.getDescription(),
			GetterUtil.getBoolean(fragmentSet.getMarketplace()),
			serviceContext);
	}

	public static FragmentCollection getFragmentCollection(DLFolder dlFolder) {
		if (dlFolder == null) {
			return null;
		}

		DLFolder parentDLFolder = DLFolderLocalServiceUtil.fetchDLFolder(
			dlFolder.getParentFolderId());

		while ((parentDLFolder != null) && !parentDLFolder.isMountPoint()) {
			dlFolder = parentDLFolder;

			parentDLFolder = DLFolderLocalServiceUtil.fetchDLFolder(
				dlFolder.getParentFolderId());
		}

		return FragmentCollectionLocalServiceUtil.fetchFragmentCollection(
			dlFolder.getGroupId(), dlFolder.getName());
	}

	public static FragmentCollection getOrAddFragmentCollection(
			long companyId, FragmentSet fragmentSet,
			String fragmentSetExternalReferenceCode, long groupId,
			HttpServletRequest httpServletRequest, String key, Locale locale,
			long userId)
		throws Exception {

		if (Validator.isNull(fragmentSetExternalReferenceCode)) {
			if (!LazyReferencingThreadLocal.isEnabled() ||
				(fragmentSet == null) ||
				Validator.isNull(fragmentSet.getExternalReferenceCode())) {

				throw new IllegalArgumentException(
					LanguageUtil.get(locale, key));
			}

			fragmentSetExternalReferenceCode =
				fragmentSet.getExternalReferenceCode();
		}

		FragmentCollection fragmentCollection =
			FragmentCollectionLocalServiceUtil.
				fetchFragmentCollectionByExternalReferenceCode(
					fragmentSetExternalReferenceCode, groupId);

		if (fragmentCollection != null) {
			return fragmentCollection;
		}

		if ((fragmentSet != null) && LazyReferencingThreadLocal.isEnabled()) {
			if (!Objects.equals(
					fragmentSet.getExternalReferenceCode(),
					fragmentSetExternalReferenceCode)) {

				throw new IllegalArgumentException(
					LanguageUtil.get(
						locale,
						"the-fragment-set-external-reference-codes-do-not-" +
							"match"));
			}

			return addFragmentCollection(
				fragmentSet,
				ServiceContextUtil.getServiceContext(
					companyId, fragmentSet.getDateCreated(), groupId,
					httpServletRequest, fragmentSet.getDateModified(), userId));
		}

		throw new IllegalArgumentException(
			LanguageUtil.format(
				locale,
				"no-fragment-set-was-found-with-external-reference-code-x",
				fragmentSetExternalReferenceCode));
	}

}