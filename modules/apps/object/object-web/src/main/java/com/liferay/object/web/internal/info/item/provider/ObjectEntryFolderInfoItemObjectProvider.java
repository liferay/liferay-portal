/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.web.internal.info.item.provider;

import com.liferay.info.exception.NoSuchInfoItemException;
import com.liferay.info.item.ClassPKInfoItemIdentifier;
import com.liferay.info.item.ERCInfoItemIdentifier;
import com.liferay.info.item.InfoItemIdentifier;
import com.liferay.info.item.provider.InfoItemObjectProvider;
import com.liferay.object.model.ObjectEntryFolder;
import com.liferay.object.service.ObjectEntryFolderLocalService;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.util.Validator;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Marco Leo
 */
@Component(
	property = {
		"info.item.identifier=com.liferay.info.item.ClassPKInfoItemIdentifier",
		"info.item.identifier=com.liferay.info.item.ERCInfoItemIdentifier",
		"item.class.name=com.liferay.object.model.ObjectEntryFolder"
	},
	service = InfoItemObjectProvider.class
)
public class ObjectEntryFolderInfoItemObjectProvider
	implements InfoItemObjectProvider<ObjectEntryFolder> {

	@Override
	public ObjectEntryFolder getInfoItem(InfoItemIdentifier infoItemIdentifier)
		throws NoSuchInfoItemException {

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		return getInfoItem(
			serviceContext.getScopeGroupId(), infoItemIdentifier);
	}

	@Override
	public ObjectEntryFolder getInfoItem(
			long groupId, InfoItemIdentifier infoItemIdentifier)
		throws NoSuchInfoItemException {

		if (!(infoItemIdentifier instanceof ClassPKInfoItemIdentifier) &&
			!(infoItemIdentifier instanceof ERCInfoItemIdentifier)) {

			throw new NoSuchInfoItemException(
				"Unsupported info item identifier " + infoItemIdentifier);
		}

		if (infoItemIdentifier instanceof ClassPKInfoItemIdentifier) {
			ClassPKInfoItemIdentifier classPKInfoItemIdentifier =
				(ClassPKInfoItemIdentifier)infoItemIdentifier;

			ObjectEntryFolder objectEntryFolder =
				_objectEntryFolderLocalService.fetchObjectEntryFolder(
					classPKInfoItemIdentifier.getClassPK());

			if (objectEntryFolder == null) {
				throw new NoSuchInfoItemException(
					"Unable to get object entry folder " +
						classPKInfoItemIdentifier.getClassPK());
			}

			return objectEntryFolder;
		}

		ERCInfoItemIdentifier ercInfoItemIdentifier =
			(ERCInfoItemIdentifier)infoItemIdentifier;
		long scopeGroupId = groupId;
		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		if (Validator.isNotNull(
				ercInfoItemIdentifier.getScopeExternalReferenceCode())) {

			try {
				Group group =
					_groupLocalService.getGroupByExternalReferenceCode(
						ercInfoItemIdentifier.getScopeExternalReferenceCode(),
						serviceContext.getCompanyId());

				scopeGroupId = group.getGroupId();
			}
			catch (PortalException portalException) {
				throw new NoSuchInfoItemException(
					StringBundler.concat(
						"No group found with company ID ",
						serviceContext.getCompanyId(),
						" and external reference code ",
						ercInfoItemIdentifier.getScopeExternalReferenceCode()),
					portalException);
			}
		}

		try {
			return _objectEntryFolderLocalService.
				getObjectEntryFolderByExternalReferenceCode(
					ercInfoItemIdentifier.getExternalReferenceCode(),
					scopeGroupId, serviceContext.getCompanyId());
		}
		catch (PortalException portalException) {
			throw new NoSuchInfoItemException(
				StringBundler.concat(
					"No object entry folder found with external reference ",
					"code ", ercInfoItemIdentifier.getExternalReferenceCode(),
					" and group ID ", scopeGroupId),
				portalException);
		}
	}

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private ObjectEntryFolderLocalService _objectEntryFolderLocalService;

}