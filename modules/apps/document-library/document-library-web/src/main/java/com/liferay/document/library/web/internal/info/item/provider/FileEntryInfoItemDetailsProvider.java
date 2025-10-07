/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.web.internal.info.item.provider;

import com.liferay.info.item.ClassPKInfoItemIdentifier;
import com.liferay.info.item.ERCInfoItemIdentifier;
import com.liferay.info.item.GroupUrlTitleInfoItemIdentifier;
import com.liferay.info.item.InfoItemClassDetails;
import com.liferay.info.item.provider.BaseInfoItemDetailsProvider;
import com.liferay.info.item.provider.InfoItemDetailsProvider;
import com.liferay.portal.kernel.repository.model.FileEntry;

import org.osgi.service.component.annotations.Component;

/**
 * @author Jorge Ferrer
 */
@Component(
	property = "item.class.name=com.liferay.portal.kernel.repository.model.FileEntry",
	service = InfoItemDetailsProvider.class
)
public class FileEntryInfoItemDetailsProvider
	extends BaseInfoItemDetailsProvider<FileEntry> {

	@Override
	public InfoItemClassDetails getInfoItemClassDetails() {
		return new InfoItemClassDetails(FileEntry.class.getName());
	}

	@Override
	protected InfoItemIdentifierFactory<FileEntry>
		getInfoItemIdentifierFactory() {

		return new InfoItemIdentifierFactory<>() {

			@Override
			public ClassPKInfoItemIdentifier createClassPKInfoItemIdentifier(
				FileEntry fileEntry) {

				return new ClassPKInfoItemIdentifier(
					fileEntry.getFileEntryId());
			}

			@Override
			public ERCInfoItemIdentifier createERCInfoItemIdentifier(
				String externalReferenceCode,
				String scopeExternalReferenceCode) {

				return new ERCInfoItemIdentifier(
					externalReferenceCode, scopeExternalReferenceCode);
			}

			@Override
			public GroupUrlTitleInfoItemIdentifier
				createGroupUrlTitleInfoItemIdentifier(
					long groupId, FileEntry fileEntry) {

				return new GroupUrlTitleInfoItemIdentifier(
					groupId, String.valueOf(fileEntry.getFileEntryId()));
			}

		};
	}

}