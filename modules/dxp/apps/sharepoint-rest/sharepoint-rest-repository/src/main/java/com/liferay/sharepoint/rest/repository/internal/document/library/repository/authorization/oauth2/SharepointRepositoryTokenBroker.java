/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.sharepoint.rest.repository.internal.document.library.repository.authorization.oauth2;

import com.liferay.document.library.repository.authorization.oauth2.Token;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.security.key.secret.SecretResolverUtil;
import com.liferay.sharepoint.rest.repository.internal.configuration.SharepointRepositoryConfiguration;

import com.microsoft.aad.msal4j.AuthorizationCodeParameters;
import com.microsoft.aad.msal4j.AuthorizationRequestUrlParameters;
import com.microsoft.aad.msal4j.ClientCredentialFactory;
import com.microsoft.aad.msal4j.ConfidentialClientApplication;
import com.microsoft.aad.msal4j.IAccount;
import com.microsoft.aad.msal4j.IAuthenticationResult;
import com.microsoft.aad.msal4j.Prompt;
import com.microsoft.aad.msal4j.ResponseMode;
import com.microsoft.aad.msal4j.SilentParameters;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import java.util.Collections;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * @author Adolfo Pérez
 */
public class SharepointRepositoryTokenBroker {

	public SharepointRepositoryTokenBroker(
		SharepointRepositoryConfiguration sharepointRepositoryConfiguration) {

		_sharepointRepositoryConfiguration = sharepointRepositoryConfiguration;
	}

	public String getAuthorizationRequestUrl(
			String nonce, String redirectUri, String state)
		throws MalformedURLException {

		ConfidentialClientApplication confidentialClientApplication =
			_getConfidentialClientApplication();

		URL url = confidentialClientApplication.getAuthorizationRequestUrl(
			AuthorizationRequestUrlParameters.builder(
				redirectUri,
				Collections.singleton(
					_sharepointRepositoryConfiguration.scope())
			).responseMode(
				ResponseMode.QUERY
			).prompt(
				Prompt.SELECT_ACCOUNT
			).state(
				state
			).nonce(
				nonce
			).build());

		return url.toString();
	}

	public SharepointRepositoryAuthenticationResult requestAccessToken(
			String code, String redirectURL)
		throws ExecutionException, InterruptedException, MalformedURLException,
			   URISyntaxException {

		ConfidentialClientApplication confidentialClientApplication =
			_getConfidentialClientApplication();

		IAuthenticationResult iAuthenticationResult =
			confidentialClientApplication.acquireToken(
				AuthorizationCodeParameters.builder(
					code, new URI(redirectURL)
				).scopes(
					Collections.singleton(
						_sharepointRepositoryConfiguration.scope())
				).build()
			).get();

		return new SharepointRepositoryAuthenticationResult(
			iAuthenticationResult,
			confidentialClientApplication.tokenCache(
			).serialize());
	}

	public SharepointRepositoryAuthenticationResult requestAccessTokenSilently(
			Token token)
		throws ExecutionException, InterruptedException, MalformedURLException {

		ConfidentialClientApplication confidentialClientApplication =
			_getConfidentialClientApplication();

		confidentialClientApplication.tokenCache(
		).deserialize(
			token.getAccessToken()
		);

		IAuthenticationResult iAuthenticationResult =
			confidentialClientApplication.acquireTokenSilently(
				SilentParameters.builder(
					Collections.singleton(
						_sharepointRepositoryConfiguration.scope()),
					_getIAccount(confidentialClientApplication)
				).build()
			).get();

		return new SharepointRepositoryAuthenticationResult(
			iAuthenticationResult,
			confidentialClientApplication.tokenCache(
			).serialize());
	}

	private ConfidentialClientApplication _getConfidentialClientApplication()
		throws MalformedURLException {

		return ConfidentialClientApplication.builder(
			_sharepointRepositoryConfiguration.clientId(),
			ClientCredentialFactory.createFromSecret(
				SecretResolverUtil.resolve(
					CompanyThreadLocal.getCompanyId(),
					_sharepointRepositoryConfiguration.clientSecret()))
		).authority(
			"https://login.microsoftonline.com/" +
				_sharepointRepositoryConfiguration.tenantId()
		).build();
	}

	private IAccount _getIAccount(
			ConfidentialClientApplication confidentialClientApplication)
		throws ExecutionException, InterruptedException {

		CompletableFuture<Set<IAccount>> accounts =
			confidentialClientApplication.getAccounts();

		Set<IAccount> iAccounts = accounts.get();

		Iterator<IAccount> iterator = iAccounts.iterator();

		return iterator.next();
	}

	private final SharepointRepositoryConfiguration
		_sharepointRepositoryConfiguration;

}