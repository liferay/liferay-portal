/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.web.internal.info.item.provider;

import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.info.exception.NoSuchInfoItemException;
import com.liferay.info.item.ClassPKInfoItemIdentifier;
import com.liferay.info.item.ERCInfoItemIdentifier;
import com.liferay.info.item.GroupUrlTitleInfoItemIdentifier;
import com.liferay.info.item.InfoItemIdentifier;
import com.liferay.info.item.provider.BaseInfoItemObjectProvider;
import com.liferay.info.item.provider.InfoItemObjectProvider;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.util.GetterUtil;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alejandro Tardín
 * @author Jorge Ferrer
 */
@Component(
	property = {
		"info.item.identifier=com.liferay.info.item.ClassPKInfoItemIdentifier",
		"info.item.identifier=com.liferay.info.item.ERCInfoItemIdentifier",
		"info.item.identifier=com.liferay.info.item.GroupUrlTitleInfoItemIdentifier",
		"item.class.name=com.liferay.portal.kernel.repository.model.FileEntry",
		"service.ranking:Integer=100"
	},
	service = InfoItemObjectProvider.class
)
public class FileEntryInfoItemObjectProvider
	extends BaseInfoItemObjectProvider<FileEntry> {

	@Override
	protected FileEntry doGetInfoItem(
			long groupId, InfoItemIdentifier infoItemIdentifier)
		throws NoSuchInfoItemException {

		if (!(infoItemIdentifier instanceof ClassPKInfoItemIdentifier) &&
			!(infoItemIdentifier instanceof ERCInfoItemIdentifier) &&
			!(infoItemIdentifier instanceof GroupUrlTitleInfoItemIdentifier)) {

			throw new NoSuchInfoItemException(
				"Unsupported info item identifier " + infoItemIdentifier);
		}

		try {
			if (infoItemIdentifier instanceof ClassPKInfoItemIdentifier) {
				ClassPKInfoItemIdentifier classPKInfoItemIdentifier =
					(ClassPKInfoItemIdentifier)infoItemIdentifier;

				return _dlAppLocalService.fetchFileEntry(
					classPKInfoItemIdentifier.getClassPK());
			}

			if (infoItemIdentifier instanceof ERCInfoItemIdentifier) {
				ERCInfoItemIdentifier ercInfoItemIdentifier =
					(ERCInfoItemIdentifier)infoItemIdentifier;

				return _dlAppLocalService.fetchFileEntryByExternalReferenceCode(
					groupId, ercInfoItemIdentifier.getExternalReferenceCode());
			}

			GroupUrlTitleInfoItemIdentifier groupURLTitleInfoItemIdentifier =
				(GroupUrlTitleInfoItemIdentifier)infoItemIdentifier;

			return _dlAppLocalService.fetchFileEntry(
				GetterUtil.getLong(
					groupURLTitleInfoItemIdentifier.getUrlTitle()));
		}
		catch (PortalException portalException) {
			throw new NoSuchInfoItemException(
				"Unable to get file entry with identifier " +
					infoItemIdentifier,
				portalException);
		}
	}

	@Reference
	private DLAppLocalService _dlAppLocalService;

}