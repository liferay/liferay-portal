/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.pim.site.initializer.internal.util;

import com.liferay.object.constants.ObjectEntryFolderConstants;
import com.liferay.object.model.ObjectEntryFolder;
import com.liferay.object.service.ObjectEntryFolderLocalService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.site.pim.site.initializer.internal.constants.PIMObjectEntryFolderConstants;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * @author Andrea Sbarra
 */
public class PIMObjectEntryFolderUtil {

	public static ObjectEntryFolder getOrAddProductsObjectEntryFolder(
			Group group,
			ObjectEntryFolderLocalService objectEntryFolderLocalService)
		throws PortalException {

		ObjectEntryFolder objectEntryFolder =
			objectEntryFolderLocalService.
				fetchObjectEntryFolderByExternalReferenceCode(
					PIMObjectEntryFolderConstants.
						EXTERNAL_REFERENCE_CODE_PRODUCTS,
					group.getGroupId(), group.getCompanyId());

		if (objectEntryFolder != null) {
			return objectEntryFolder;
		}

		Map<Locale, String> labelMap = new HashMap<>();

		for (Locale locale :
				LanguageUtil.getAvailableLocales(group.getGroupId())) {

			labelMap.put(
				locale, LanguageUtil.get(locale, "products", "Products"));
		}

		ServiceContext serviceContext = new ServiceContext();

		serviceContext.setAddGroupPermissions(false);
		serviceContext.setAddGuestPermissions(false);
		serviceContext.setCompanyId(group.getCompanyId());
		serviceContext.setScopeGroupId(group.getGroupId());
		serviceContext.setUserId(group.getCreatorUserId());

		try {
			return objectEntryFolderLocalService.addObjectEntryFolder(
				PIMObjectEntryFolderConstants.EXTERNAL_REFERENCE_CODE_PRODUCTS,
				group.getGroupId(), group.getCreatorUserId(),
				ObjectEntryFolderConstants.
					PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
				"", labelMap, "Products", serviceContext);
		}
		catch (PortalException portalException) {
			objectEntryFolder =
				objectEntryFolderLocalService.
					fetchObjectEntryFolderByExternalReferenceCode(
						PIMObjectEntryFolderConstants.
							EXTERNAL_REFERENCE_CODE_PRODUCTS,
						group.getGroupId(), group.getCompanyId());

			if (objectEntryFolder != null) {
				return objectEntryFolder;
			}

			throw portalException;
		}
	}

}