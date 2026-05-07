/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth.client.persistence.service;

import com.liferay.portal.kernel.service.ServiceWrapper;

/**
 * Provides a wrapper for {@link OAuthClientPRLocalMetadataService}.
 *
 * @author Brian Wing Shun Chan
 * @see OAuthClientPRLocalMetadataService
 * @generated
 */
public class OAuthClientPRLocalMetadataServiceWrapper
	implements OAuthClientPRLocalMetadataService,
			   ServiceWrapper<OAuthClientPRLocalMetadataService> {

	public OAuthClientPRLocalMetadataServiceWrapper() {
		this(null);
	}

	public OAuthClientPRLocalMetadataServiceWrapper(
		OAuthClientPRLocalMetadataService oAuthClientPRLocalMetadataService) {

		_oAuthClientPRLocalMetadataService = oAuthClientPRLocalMetadataService;
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _oAuthClientPRLocalMetadataService.getOSGiServiceIdentifier();
	}

	@Override
	public OAuthClientPRLocalMetadataService getWrappedService() {
		return _oAuthClientPRLocalMetadataService;
	}

	@Override
	public void setWrappedService(
		OAuthClientPRLocalMetadataService oAuthClientPRLocalMetadataService) {

		_oAuthClientPRLocalMetadataService = oAuthClientPRLocalMetadataService;
	}

	private OAuthClientPRLocalMetadataService
		_oAuthClientPRLocalMetadataService;

}
// LIFERAY-SERVICE-BUILDER-HASH:-997487258