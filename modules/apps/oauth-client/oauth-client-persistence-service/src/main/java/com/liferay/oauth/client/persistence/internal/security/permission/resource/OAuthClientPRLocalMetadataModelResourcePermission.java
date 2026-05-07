/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth.client.persistence.internal.security.permission.resource;

import com.liferay.oauth.client.persistence.model.OAuthClientPRLocalMetadata;
import com.liferay.oauth.client.persistence.service.OAuthClientPRLocalMetadataLocalService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.security.permission.resource.PortletResourcePermission;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Jorge García Jiménez
 */
@Component(
	property = "model.class.name=com.liferay.oauth.client.persistence.model.OAuthClientPRLocalMetadata",
	service = ModelResourcePermission.class
)
public class OAuthClientPRLocalMetadataModelResourcePermission
	implements ModelResourcePermission<OAuthClientPRLocalMetadata> {

	@Override
	public void check(
			PermissionChecker permissionChecker,
			long oAuthClientPRLocalMetadataId, String actionId)
		throws PortalException {

		if (!contains(
				permissionChecker, oAuthClientPRLocalMetadataId, actionId)) {

			throw new PrincipalException.MustHavePermission(
				permissionChecker, OAuthClientPRLocalMetadata.class.getName(),
				oAuthClientPRLocalMetadataId, actionId);
		}
	}

	@Override
	public void check(
			PermissionChecker permissionChecker,
			OAuthClientPRLocalMetadata oAuthClientPRLocalMetadata,
			String actionId)
		throws PortalException {

		if (!contains(
				permissionChecker, oAuthClientPRLocalMetadata, actionId)) {

			throw new PrincipalException.MustHavePermission(
				permissionChecker, OAuthClientPRLocalMetadata.class.getName(),
				oAuthClientPRLocalMetadata.getOAuthClientPRLocalMetadataId(),
				actionId);
		}
	}

	@Override
	public boolean contains(
			PermissionChecker permissionChecker,
			long oAuthClientPRLocalMetadataId, String actionId)
		throws PortalException {

		return contains(
			permissionChecker,
			_oAuthClientPRLocalMetadataLocalService.
				getOAuthClientPRLocalMetadata(oAuthClientPRLocalMetadataId),
			actionId);
	}

	@Override
	public boolean contains(
			PermissionChecker permissionChecker,
			OAuthClientPRLocalMetadata oAuthClientPRLocalMetadata,
			String actionId)
		throws PortalException {

		if (permissionChecker.hasOwnerPermission(
				oAuthClientPRLocalMetadata.getCompanyId(),
				OAuthClientPRLocalMetadata.class.getName(),
				oAuthClientPRLocalMetadata.getOAuthClientPRLocalMetadataId(),
				oAuthClientPRLocalMetadata.getUserId(), actionId)) {

			return true;
		}

		return permissionChecker.hasPermission(
			null, OAuthClientPRLocalMetadata.class.getName(),
			oAuthClientPRLocalMetadata.getOAuthClientPRLocalMetadataId(),
			actionId);
	}

	@Override
	public String getModelName() {
		return OAuthClientPRLocalMetadata.class.getName();
	}

	@Override
	public PortletResourcePermission getPortletResourcePermission() {
		return _portletResourcePermission;
	}

	@Reference
	private OAuthClientPRLocalMetadataLocalService
		_oAuthClientPRLocalMetadataLocalService;

	@Reference(target = "(resource.name=com.liferay.oauth.client.persistence)")
	private PortletResourcePermission _portletResourcePermission;

}