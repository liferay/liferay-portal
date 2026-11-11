/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.internal;

import com.liferay.asset.display.page.constants.AssetDisplayPageConstants;
import com.liferay.design.library.util.DesignLibraryUtil;
import com.liferay.document.library.kernel.model.DLFileEntryTypeConstants;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.util.LayoutPageTemplateEntryUtil;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;

/**
 * @author Roberto Díaz
 */
public class DLAssetDisplayPageUtil {

	public static boolean hasAssetDisplayPage(ServiceContext serviceContext) {
		int displayPageType = ParamUtil.getInteger(
			serviceContext, "displayPageType",
			AssetDisplayPageConstants.TYPE_DEFAULT);

		if (displayPageType == AssetDisplayPageConstants.TYPE_DEFAULT) {
			long fileEntryTypeId = ParamUtil.getLong(
				serviceContext, "fileEntryTypeId",
				DLFileEntryTypeConstants.FILE_ENTRY_TYPE_ID_BASIC_DOCUMENT);

			LayoutPageTemplateEntry layoutPageTemplateEntry =
				LayoutPageTemplateEntryUtil.fetchDefaultLayoutPageTemplateEntry(
					PortalUtil.getClassNameId(FileEntry.class), fileEntryTypeId,
					DesignLibraryUtil.fetchConnectedDesignLibraryGroupIds(
						serviceContext.getScopeGroupId()),
					serviceContext.getScopeGroupId());

			if (layoutPageTemplateEntry == null) {
				return false;
			}
		}
		else if (displayPageType == AssetDisplayPageConstants.TYPE_NONE) {
			return false;
		}
		else if ((displayPageType == AssetDisplayPageConstants.TYPE_SPECIFIC) &&
				 (ParamUtil.getLong(serviceContext, "assetDisplayPageId") ==
					 0)) {

			return false;
		}

		return true;
	}

}