/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.web.internal.info.item.provider;

import com.liferay.info.item.ClassPKInfoItemIdentifier;
import com.liferay.info.item.ERCInfoItemIdentifier;
import com.liferay.info.item.InfoItemClassDetails;
import com.liferay.info.item.InfoItemDetails;
import com.liferay.info.item.InfoItemIdentifier;
import com.liferay.info.item.InfoItemReference;
import com.liferay.info.item.provider.InfoItemDetailsProvider;
import com.liferay.object.model.ObjectEntryFolder;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.GroupLocalService;

import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Marco Leo
 */
@Component(
	property = "item.class.name=com.liferay.object.model.ObjectEntryFolder",
	service = InfoItemDetailsProvider.class
)
public class ObjectEntryFolderInfoItemDetailsProvider
	implements InfoItemDetailsProvider<ObjectEntryFolder> {

	@Override
	public InfoItemClassDetails getInfoItemClassDetails() {
		return new InfoItemClassDetails(ObjectEntryFolder.class.getName());
	}

	@Override
	public InfoItemDetails getInfoItemDetails(
		long groupId,
		Class<? extends InfoItemIdentifier> infoItemIdentifierClass,
		ObjectEntryFolder objectEntryFolder) {

		if (!Objects.equals(
				infoItemIdentifierClass, ClassPKInfoItemIdentifier.class) &&
			!Objects.equals(
				infoItemIdentifierClass, ERCInfoItemIdentifier.class)) {

			return null;
		}

		if (Objects.equals(
				infoItemIdentifierClass, ClassPKInfoItemIdentifier.class)) {

			return new InfoItemDetails(
				getInfoItemClassDetails(),
				new InfoItemReference(
					ObjectEntryFolder.class.getName(),
					objectEntryFolder.getObjectEntryFolderId()));
		}

		String scopeExternalReferenceCode = null;

		if (groupId != objectEntryFolder.getGroupId()) {
			Group group = _groupLocalService.fetchGroup(
				objectEntryFolder.getGroupId());

			scopeExternalReferenceCode = group.getExternalReferenceCode();
		}

		return new InfoItemDetails(
			getInfoItemClassDetails(),
			new InfoItemReference(
				ObjectEntryFolder.class.getName(),
				new ERCInfoItemIdentifier(
					objectEntryFolder.getExternalReferenceCode(),
					scopeExternalReferenceCode)));
	}

	@Override
	public InfoItemDetails getInfoItemDetails(
		ObjectEntryFolder objectEntryFolder) {

		return new InfoItemDetails(
			getInfoItemClassDetails(),
			new InfoItemReference(
				ObjectEntryFolder.class.getName(),
				objectEntryFolder.getObjectEntryFolderId()));
	}

	@Reference
	private GroupLocalService _groupLocalService;

}