/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.depot.web.internal.info.item.provider;

import com.liferay.depot.model.DepotEntry;
import com.liferay.info.exception.InfoItemPermissionException;
import com.liferay.info.item.ClassPKInfoItemIdentifier;
import com.liferay.info.item.InfoItemIdentifier;
import com.liferay.info.item.InfoItemReference;
import com.liferay.info.item.provider.InfoItemPermissionProvider;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Roberto Díaz
 */
@Component(service = InfoItemPermissionProvider.class)
public class DepotEntryInfoItemPermissionProvider
	implements InfoItemPermissionProvider<DepotEntry> {

	@Override
	public boolean hasPermission(
			PermissionChecker permissionChecker, DepotEntry depotEntry,
			String actionId)
		throws InfoItemPermissionException {

		try {
			return _depotEntryModelResourcePermission.contains(
				permissionChecker, depotEntry, actionId);
		}
		catch (PortalException portalException) {
			throw new InfoItemPermissionException(
				depotEntry.getDepotEntryId(), portalException);
		}
	}

	@Override
	public boolean hasPermission(
			PermissionChecker permissionChecker,
			InfoItemReference infoItemReference, String actionId)
		throws InfoItemPermissionException {

		InfoItemIdentifier infoItemIdentifier =
			infoItemReference.getInfoItemIdentifier();

		if (!(infoItemIdentifier instanceof ClassPKInfoItemIdentifier)) {
			return false;
		}

		ClassPKInfoItemIdentifier classPKInfoItemIdentifier =
			(ClassPKInfoItemIdentifier)
				infoItemReference.getInfoItemIdentifier();

		try {
			return _depotEntryModelResourcePermission.contains(
				permissionChecker, classPKInfoItemIdentifier.getClassPK(),
				actionId);
		}
		catch (PortalException portalException) {
			throw new InfoItemPermissionException(
				classPKInfoItemIdentifier.getClassPK(), portalException);
		}
	}

	@Reference(target = "(model.class.name=com.liferay.depot.model.DepotEntry)")
	private volatile ModelResourcePermission<DepotEntry>
		_depotEntryModelResourcePermission;

}