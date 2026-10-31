/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.ai.hub.cell.rest.internal.resource.v1_0;

import com.liferay.ai.hub.cell.authorization.AIHubCellAuthorizationTokenProvider;
import com.liferay.ai.hub.cell.rest.dto.v1_0.AuthorizationToken;
import com.liferay.ai.hub.cell.rest.resource.v1_0.AuthorizationTokenResource;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.security.auth.PrincipalException;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Feliphe Marinho
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/authorization-token.properties",
	scope = ServiceScope.PROTOTYPE, service = AuthorizationTokenResource.class
)
public class AuthorizationTokenResourceImpl
	extends BaseAuthorizationTokenResourceImpl {

	@Override
	public AuthorizationToken postAuthorizationToken() throws Exception {
		if (contextUser.isGuestUser()) {
			throw new PrincipalException.MustBeAuthenticated(
				contextUser.getUserId());
		}

		JSONObject jsonObject =
			_aiHubCellAuthorizationTokenProvider.
				getAuthorizationTokenJSONObject(
					contextCompany.getCompanyId(), contextUser.getUserId());

		return new AuthorizationToken() {
			{
				setAccessToken(() -> jsonObject.getString("accessToken"));
				setScope(() -> jsonObject.getString("scope"));
				setServiceURL(() -> jsonObject.getString("serviceURL"));
				setUserToken(() -> jsonObject.getString("userToken", null));
			}
		};
	}

	@Reference
	private AIHubCellAuthorizationTokenProvider
		_aiHubCellAuthorizationTokenProvider;

}