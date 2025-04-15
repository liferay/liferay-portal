/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.web.internal.context;

import com.github.scribejava.core.exceptions.OAuthException;

import com.liferay.expando.kernel.model.ExpandoBridge;
import com.liferay.oauth2.provider.model.OAuth2Authorization;
import com.liferay.oauth2.provider.service.OAuth2AuthorizationLocalService;

import jakarta.servlet.http.HttpServletRequest;

import jakarta.ws.rs.ext.Provider;

import java.util.Date;

import org.apache.cxf.jaxrs.ext.ContextProvider;
import org.apache.cxf.message.Message;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Marcellus Tavares
 */
@Component(service = GroupInfoContextProvider.class)
@Provider
public class GroupInfoContextProvider implements ContextProvider<GroupInfo> {

	@Override
	public GroupInfo createContext(Message message) {
		HttpServletRequest httpServletRequest =
			(HttpServletRequest)message.getContextualProperty("HTTP.REQUEST");

		String authorization = httpServletRequest.getHeader("Authorization");

		if (authorization == null) {
			throw new IllegalStateException(
				"Authorization Header is not available");
		}

		try {
			OAuth2Authorization oAuth2Authorization =
				_oAuth2AuthorizationLocalService.
					getOAuth2AuthorizationByAccessTokenContent(
						authorization.substring(7));

			Date currentDate = new Date(System.currentTimeMillis());

			if (currentDate.after(
					oAuth2Authorization.getAccessTokenExpirationDate())) {

				throw new OAuthException(
					"Your access token is invalid. Please check your token " +
						"and try again.");
			}

			ExpandoBridge expandoBridge =
				oAuth2Authorization.getExpandoBridge();

			return new GroupInfo(
				(long)expandoBridge.getAttribute("groupId", false));
		}
		catch (Exception exception) {
			throw new IllegalStateException(
				"Unable to fetch the OAuth2Authorization with access token " +
					authorization.substring(7),
				exception);
		}
	}

	@Reference
	private OAuth2AuthorizationLocalService _oAuth2AuthorizationLocalService;

}