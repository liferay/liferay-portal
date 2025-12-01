/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth.client.persistence.service.impl;

import com.liferay.oauth.client.persistence.constants.OAuthClientPersistenceActionKeys;
import com.liferay.oauth.client.persistence.model.OAuthClientASLocalMetadata;
import com.liferay.oauth.client.persistence.service.base.OAuthClientASLocalMetadataServiceBaseImpl;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermissionUtil;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Arthur Chan
 */
@Component(
	property = {
		"json.web.service.context.name=oauthclient",
		"json.web.service.context.path=OAuthClientASLocalMetadata"
	},
	service = AopService.class
)
public class OAuthClientASLocalMetadataServiceImpl
	extends OAuthClientASLocalMetadataServiceBaseImpl {

	@Override
	public OAuthClientASLocalMetadata addOAuthClientASLocalMetadata(
			long userId, Boolean enabled, String issuerString, String jwksUri,
			String[] supportedGrantTypes, String[] supportedScopes,
			String[] supportedSubjectTypes, String tokenEndpointString,
			String userinfoEndpoint)
		throws PortalException {

		ModelResourcePermissionUtil.check(
			_oAuthClientASLocalMetadataModelResourcePermission,
			getPermissionChecker(), GroupConstants.DEFAULT_LIVE_GROUP_ID, 0,
			OAuthClientPersistenceActionKeys.
				ACTION_ADD_OAUTH_CLIENT_AS_LOCAL_METADATA);

		return oAuthClientASLocalMetadataLocalService.
			addOAuthClientASLocalMetadata(
				userId, enabled, issuerString, jwksUri, supportedGrantTypes,
				supportedScopes, supportedSubjectTypes, tokenEndpointString,
				userinfoEndpoint);
	}

	@Override
	public OAuthClientASLocalMetadata deleteOAuthClientASLocalMetadata(
			long oAuthClientASLocalMetadataId)
		throws PortalException {

		OAuthClientASLocalMetadata oAuthClientASLocalMetadata =
			oAuthClientASLocalMetadataLocalService.
				getOAuthClientASLocalMetadata(oAuthClientASLocalMetadataId);

		_oAuthClientASLocalMetadataModelResourcePermission.check(
			getPermissionChecker(), oAuthClientASLocalMetadata,
			ActionKeys.DELETE);

		return oAuthClientASLocalMetadataLocalService.
			deleteOAuthClientASLocalMetadata(oAuthClientASLocalMetadata);
	}

	@Override
	public OAuthClientASLocalMetadata deleteOAuthClientASLocalMetadata(
			String localWellKnownURI)
		throws PortalException {

		OAuthClientASLocalMetadata oAuthClientASLocalMetadata =
			oAuthClientASLocalMetadataLocalService.
				getOAuthClientASLocalMetadata(localWellKnownURI);

		_oAuthClientASLocalMetadataModelResourcePermission.check(
			getPermissionChecker(), oAuthClientASLocalMetadata,
			ActionKeys.DELETE);

		return oAuthClientASLocalMetadataLocalService.
			deleteOAuthClientASLocalMetadata(oAuthClientASLocalMetadata);
	}

	@Override
	public List<OAuthClientASLocalMetadata>
		getCompanyOAuthClientASLocalMetadata(long companyId) {

		return oAuthClientASLocalMetadataPersistence.filterFindByCompanyId(
			companyId);
	}

	@Override
	public List<OAuthClientASLocalMetadata>
		getCompanyOAuthClientASLocalMetadata(
			long companyId, int start, int end) {

		return oAuthClientASLocalMetadataPersistence.filterFindByCompanyId(
			companyId, start, end);
	}

	@Override
	public OAuthClientASLocalMetadata getOAuthClientASLocalMetadata(
			String localWellKnownURI)
		throws PortalException {

		OAuthClientASLocalMetadata oAuthClientASLocalMetadata =
			oAuthClientASLocalMetadataLocalService.
				getOAuthClientASLocalMetadata(localWellKnownURI);

		_oAuthClientASLocalMetadataModelResourcePermission.check(
			getPermissionChecker(), oAuthClientASLocalMetadata,
			ActionKeys.VIEW);

		return oAuthClientASLocalMetadata;
	}

	@Override
	public List<OAuthClientASLocalMetadata> getUserOAuthClientASLocalMetadata(
		long userId) {

		return oAuthClientASLocalMetadataPersistence.filterFindByUserId(userId);
	}

	@Override
	public List<OAuthClientASLocalMetadata> getUserOAuthClientASLocalMetadata(
		long userId, int start, int end) {

		return oAuthClientASLocalMetadataPersistence.filterFindByUserId(
			userId, start, end);
	}

	@Override
	public OAuthClientASLocalMetadata updateOAuthClientASLocalMetadata(
			long oAuthClientASLocalMetadataId, Boolean enabled,
			String issuerString, String jwksUri, String[] supportedGrantTypes,
			String[] supportedScopes, String[] supportedSubjectTypes,
			String tokenEndpointString, String userinfoEndpoint)
		throws PortalException {

		_oAuthClientASLocalMetadataModelResourcePermission.check(
			getPermissionChecker(),
			oAuthClientASLocalMetadataLocalService.
				getOAuthClientASLocalMetadata(oAuthClientASLocalMetadataId),
			ActionKeys.UPDATE);

		return oAuthClientASLocalMetadataLocalService.
			updateOAuthClientASLocalMetadata(
				oAuthClientASLocalMetadataId, enabled, issuerString, jwksUri,
				supportedGrantTypes, supportedScopes, supportedSubjectTypes,
				tokenEndpointString, userinfoEndpoint);
	}

	@Reference(
		target = "(model.class.name=com.liferay.oauth.client.persistence.model.OAuthClientASLocalMetadata)"
	)
	private ModelResourcePermission<OAuthClientASLocalMetadata>
		_oAuthClientASLocalMetadataModelResourcePermission;

}