/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.content.web.internal.info.item.provider;

import com.liferay.commerce.context.CommerceContext;
import com.liferay.commerce.product.model.CPAttachmentFileEntry;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.permission.CommerceProductViewPermission;
import com.liferay.commerce.product.service.CPAttachmentFileEntryLocalService;
import com.liferay.commerce.util.CommerceContextThreadLocal;
import com.liferay.commerce.util.CommerceUtil;
import com.liferay.info.exception.InfoItemPermissionException;
import com.liferay.info.item.ClassPKInfoItemIdentifier;
import com.liferay.info.item.InfoItemIdentifier;
import com.liferay.info.item.InfoItemReference;
import com.liferay.info.item.provider.InfoItemPermissionProvider;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.util.Portal;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alessio Antonio Rendina
 */
@Component(service = InfoItemPermissionProvider.class)
public class CPAttachmentFileEntryInfoItemPermissionProvider
	implements InfoItemPermissionProvider<CPAttachmentFileEntry> {

	@Override
	public boolean hasPermission(
			PermissionChecker permissionChecker,
			CPAttachmentFileEntry cpAttachmentFileEntry, String actionId)
		throws InfoItemPermissionException {

		if (_portal.getClassNameId(CPDefinition.class.getName()) !=
				cpAttachmentFileEntry.getClassNameId()) {

			return false;
		}

		CommerceContext commerceContext = CommerceContextThreadLocal.get();

		if (commerceContext == null) {
			return false;
		}

		try {
			return _commerceProductViewPermission.contains(
				permissionChecker,
				CommerceUtil.getCommerceAccountId(commerceContext),
				commerceContext.getCommerceChannelGroupId(),
				cpAttachmentFileEntry.getClassPK());
		}
		catch (PortalException portalException) {
			throw new InfoItemPermissionException(
				cpAttachmentFileEntry.getCPAttachmentFileEntryId(),
				portalException);
		}
	}

	@Override
	public boolean hasPermission(
			PermissionChecker permissionChecker,
			InfoItemReference infoItemReference, String actionId)
		throws InfoItemPermissionException {

		CommerceContext commerceContext = CommerceContextThreadLocal.get();

		if (commerceContext == null) {
			return false;
		}

		InfoItemIdentifier infoItemIdentifier =
			infoItemReference.getInfoItemIdentifier();

		if (!(infoItemIdentifier instanceof ClassPKInfoItemIdentifier)) {
			return false;
		}

		ClassPKInfoItemIdentifier classPKInfoItemIdentifier =
			(ClassPKInfoItemIdentifier)infoItemIdentifier;

		try {
			CPAttachmentFileEntry cpAttachmentFileEntry =
				_cpAttachmentFileEntryLocalService.getCPAttachmentFileEntry(
					classPKInfoItemIdentifier.getClassPK());

			if (_portal.getClassNameId(CPDefinition.class.getName()) !=
					cpAttachmentFileEntry.getClassNameId()) {

				return false;
			}

			return _commerceProductViewPermission.contains(
				permissionChecker,
				CommerceUtil.getCommerceAccountId(commerceContext),
				commerceContext.getCommerceChannelGroupId(),
				cpAttachmentFileEntry.getClassPK());
		}
		catch (PortalException portalException) {
			throw new InfoItemPermissionException(
				classPKInfoItemIdentifier.getClassPK(), portalException);
		}
	}

	@Reference
	private CommerceProductViewPermission _commerceProductViewPermission;

	@Reference
	private CPAttachmentFileEntryLocalService
		_cpAttachmentFileEntryLocalService;

	@Reference
	private Portal _portal;

}