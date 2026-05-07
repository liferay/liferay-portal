/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth.client.persistence.service.impl;

import com.liferay.oauth.client.persistence.constants.OAuthClientPersistenceActionKeys;
import com.liferay.oauth.client.persistence.model.OAuthClientPRLocalMetadata;
import com.liferay.oauth.client.persistence.service.base.OAuthClientPRLocalMetadataServiceBaseImpl;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermissionUtil;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Jorge García Jiménez
 */
@Component(
	property = {
		"json.web.service.context.name=oauthclient",
		"json.web.service.context.path=OAuthClientPRLocalMetadata"
	},
	service = AopService.class
)
public class OAuthClientPRLocalMetadataServiceImpl
	extends OAuthClientPRLocalMetadataServiceBaseImpl {

	@Override
	public OAuthClientPRLocalMetadata addOAuthClientPRLocalMetadata(
			String metadataJSON)
		throws PortalException {

		ModelResourcePermissionUtil.check(
			_oAuthClientPRLocalMetadataModelResourcePermission,
			getPermissionChecker(), GroupConstants.DEFAULT_LIVE_GROUP_ID, 0,
			OAuthClientPersistenceActionKeys.
				ACTION_ADD_OAUTH_CLIENT_PR_LOCAL_METADATA);

		return oAuthClientPRLocalMetadataLocalService.
			addOAuthClientPRLocalMetadata(getUserId(), metadataJSON);
	}

	public OAuthClientPRLocalMetadata addOAuthClientPRLocalMetadata(
			String externalReferenceCode, String[] authorizationServers,
			String[] bearerMethodsSupported, boolean localWellKnownEnabled,
			String resource, String resourceName, String[] scopesSupported)
		throws PortalException {

		ModelResourcePermissionUtil.check(
			_oAuthClientPRLocalMetadataModelResourcePermission,
			getPermissionChecker(), GroupConstants.DEFAULT_LIVE_GROUP_ID, 0,
			OAuthClientPersistenceActionKeys.
				ACTION_ADD_OAUTH_CLIENT_PR_LOCAL_METADATA);

		return oAuthClientPRLocalMetadataLocalService.
			addOAuthClientPRLocalMetadata(
				externalReferenceCode, getUserId(), authorizationServers,
				bearerMethodsSupported, localWellKnownEnabled, resource,
				resourceName, scopesSupported);
	}

	@Override
	public OAuthClientPRLocalMetadata deleteOAuthClientPRLocalMetadata(
			long oAuthClientPRLocalMetadataId)
		throws PortalException {

		OAuthClientPRLocalMetadata oAuthClientPRLocalMetadata =
			oAuthClientPRLocalMetadataLocalService.
				getOAuthClientPRLocalMetadata(oAuthClientPRLocalMetadataId);

		_oAuthClientPRLocalMetadataModelResourcePermission.check(
			getPermissionChecker(), oAuthClientPRLocalMetadata,
			ActionKeys.DELETE);

		return oAuthClientPRLocalMetadataLocalService.
			deleteOAuthClientPRLocalMetadata(oAuthClientPRLocalMetadata);
	}

	@Override
	public OAuthClientPRLocalMetadata deleteOAuthClientPRLocalMetadata(
			long companyId, String localWellKnownURI)
		throws PortalException {

		OAuthClientPRLocalMetadata oAuthClientPRLocalMetadata =
			oAuthClientPRLocalMetadataLocalService.
				getOAuthClientPRLocalMetadata(companyId, localWellKnownURI);

		_oAuthClientPRLocalMetadataModelResourcePermission.check(
			getPermissionChecker(), oAuthClientPRLocalMetadata,
			ActionKeys.DELETE);

		return oAuthClientPRLocalMetadataLocalService.
			deleteOAuthClientPRLocalMetadata(oAuthClientPRLocalMetadata);
	}

	@Override
	public OAuthClientPRLocalMetadata fetchOAuthClientPRLocalMetadata(
			long oAuthClientPRLocalMetadataId)
		throws PortalException {

		OAuthClientPRLocalMetadata oAuthClientPRLocalMetadata =
			oAuthClientPRLocalMetadataPersistence.fetchByPrimaryKey(
				oAuthClientPRLocalMetadataId);

		if (oAuthClientPRLocalMetadata != null) {
			_oAuthClientPRLocalMetadataModelResourcePermission.check(
				getPermissionChecker(), oAuthClientPRLocalMetadata,
				ActionKeys.VIEW);
		}

		return oAuthClientPRLocalMetadata;
	}

	@Override
	public OAuthClientPRLocalMetadata fetchOAuthClientPRLocalMetadata(
			long companyId, String resource)
		throws PortalException {

		OAuthClientPRLocalMetadata oAuthClientPRLocalMetadata =
			oAuthClientPRLocalMetadataPersistence.fetchByC_R(
				companyId, resource);

		if (oAuthClientPRLocalMetadata != null) {
			_oAuthClientPRLocalMetadataModelResourcePermission.check(
				getPermissionChecker(), oAuthClientPRLocalMetadata,
				ActionKeys.VIEW);
		}

		return oAuthClientPRLocalMetadata;
	}

	@Override
	public OAuthClientPRLocalMetadata
			fetchOAuthClientPRLocalMetadataByExternalReferenceCode(
				String externalReferenceCode, long companyId)
		throws PortalException {

		OAuthClientPRLocalMetadata oAuthClientPRLocalMetadata =
			oAuthClientPRLocalMetadataLocalService.
				fetchOAuthClientPRLocalMetadataByExternalReferenceCode(
					externalReferenceCode, companyId);

		if (oAuthClientPRLocalMetadata != null) {
			_oAuthClientPRLocalMetadataModelResourcePermission.check(
				getPermissionChecker(), oAuthClientPRLocalMetadata,
				ActionKeys.VIEW);
		}

		return oAuthClientPRLocalMetadata;
	}

	@Override
	public List<OAuthClientPRLocalMetadata>
		getCompanyOAuthClientPRLocalMetadata(long companyId) {

		return oAuthClientPRLocalMetadataPersistence.filterFindByCompanyId(
			companyId);
	}

	@Override
	public List<OAuthClientPRLocalMetadata>
		getCompanyOAuthClientPRLocalMetadata(
			long companyId, int start, int end) {

		return oAuthClientPRLocalMetadataPersistence.filterFindByCompanyId(
			companyId, start, end);
	}

	@Override
	public OAuthClientPRLocalMetadata getOAuthClientPRLocalMetadata(
			long companyId, boolean localWellKnownEnabled,
			OrderByComparator<OAuthClientPRLocalMetadata> orderByComparator)
		throws PortalException {

		OAuthClientPRLocalMetadata oAuthClientPRLocalMetadata =
			oAuthClientPRLocalMetadataPersistence.findByC_L_First(
				companyId, localWellKnownEnabled, orderByComparator);

		_oAuthClientPRLocalMetadataModelResourcePermission.check(
			getPermissionChecker(), oAuthClientPRLocalMetadata,
			ActionKeys.VIEW);

		return oAuthClientPRLocalMetadata;
	}

	@Override
	public OAuthClientPRLocalMetadata getOAuthClientPRLocalMetadata(
			long companyId, String resource)
		throws PortalException {

		OAuthClientPRLocalMetadata oAuthClientPRLocalMetadata =
			oAuthClientPRLocalMetadataPersistence.findByC_R(
				companyId, resource);

		_oAuthClientPRLocalMetadataModelResourcePermission.check(
			getPermissionChecker(), oAuthClientPRLocalMetadata,
			ActionKeys.VIEW);

		return oAuthClientPRLocalMetadata;
	}

	@Override
	public OAuthClientPRLocalMetadata
			getOAuthClientPRLocalMetadataByExternalReferenceCode(
				String externalReferenceCode, long companyId)
		throws PortalException {

		OAuthClientPRLocalMetadata oAuthClientPRLocalMetadata =
			oAuthClientPRLocalMetadataLocalService.
				getOAuthClientPRLocalMetadataByExternalReferenceCode(
					externalReferenceCode, companyId);

		_oAuthClientPRLocalMetadataModelResourcePermission.check(
			getPermissionChecker(), oAuthClientPRLocalMetadata,
			ActionKeys.VIEW);

		return oAuthClientPRLocalMetadata;
	}

	@Override
	public OAuthClientPRLocalMetadata
			getOAuthClientPRLocalMetadataByLocalWellKnownURI(
				long companyId, String localWellKnownURI)
		throws PortalException {

		OAuthClientPRLocalMetadata oAuthClientPRLocalMetadata =
			oAuthClientPRLocalMetadataPersistence.findByC_LWKURI(
				companyId, localWellKnownURI);

		_oAuthClientPRLocalMetadataModelResourcePermission.check(
			getPermissionChecker(), oAuthClientPRLocalMetadata,
			ActionKeys.VIEW);

		return oAuthClientPRLocalMetadata;
	}

	@Override
	public List<OAuthClientPRLocalMetadata> getUserOAuthClientPRLocalMetadata(
		long userId) {

		return oAuthClientPRLocalMetadataPersistence.filterFindByUserId(userId);
	}

	@Override
	public List<OAuthClientPRLocalMetadata> getUserOAuthClientPRLocalMetadata(
		long userId, int start, int end) {

		return oAuthClientPRLocalMetadataPersistence.filterFindByUserId(
			userId, start, end);
	}

	@Override
	public OAuthClientPRLocalMetadata updateOAuthClientPRLocalMetadata(
			long oAuthClientPRLocalMetadataId, String metadataJSON)
		throws PortalException {

		_oAuthClientPRLocalMetadataModelResourcePermission.check(
			getPermissionChecker(),
			oAuthClientPRLocalMetadataPersistence.findByPrimaryKey(
				oAuthClientPRLocalMetadataId),
			ActionKeys.UPDATE);

		return oAuthClientPRLocalMetadataLocalService.
			updateOAuthClientPRLocalMetadata(
				oAuthClientPRLocalMetadataId, metadataJSON);
	}

	public OAuthClientPRLocalMetadata updateOAuthClientPRLocalMetadata(
			long oAuthClientPRLocalMetadataId, String[] authorizationServers,
			String[] bearerMethodsSupported, boolean localWellKnownEnabled,
			String resource, String resourceName, String[] scopesSupported)
		throws PortalException {

		_oAuthClientPRLocalMetadataModelResourcePermission.check(
			getPermissionChecker(),
			oAuthClientPRLocalMetadataPersistence.findByPrimaryKey(
				oAuthClientPRLocalMetadataId),
			ActionKeys.UPDATE);

		return oAuthClientPRLocalMetadataLocalService.
			updateOAuthClientPRLocalMetadata(
				oAuthClientPRLocalMetadataId, authorizationServers,
				bearerMethodsSupported, localWellKnownEnabled, resource,
				resourceName, scopesSupported);
	}

	@Reference(
		target = "(model.class.name=com.liferay.oauth.client.persistence.model.OAuthClientPRLocalMetadata)"
	)
	private ModelResourcePermission<OAuthClientPRLocalMetadata>
		_oAuthClientPRLocalMetadataModelResourcePermission;

}