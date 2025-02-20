/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import pkceChallenge from 'pkce-challenge';

interface IOAuth2ClientFromParametersOptions {
	authorizeURL?: string;
	clientId: string;
	debug?: boolean;
	homePageURL: string;
	redirectURIs?: Array<string>;
	tokenURL?: string;
}

interface IOAuth2ClientOptions {
	authorizeURL: string;
	clientId: string;
	debug?: boolean;
	homePageURL: string;
	redirectURIs: Array<string>;
	tokenURL: string;
}

interface IOAuth2ClientTokenResponse {
	access_token: string;
	expires_after_ms: number;
	expires_in: number;
	refresh_token: string;
	scope: string;
	token_type: string;
}

export class OAuth2Client {
	private authorizeURL: string;
	private clientId: string;
	private debug: boolean;
	private homePageURL: string;
	private redirectURIs: Array<string>;
	private tokenURL: string;

	constructor(options: IOAuth2ClientOptions) {
		this.authorizeURL = options.authorizeURL;
		this.clientId = options.clientId;
		this.debug = options.debug || false;
		this.homePageURL = options.homePageURL;
		this.redirectURIs = options.redirectURIs;
		this.tokenURL = options.tokenURL;
	}

	public async fetch(url: RequestInfo, options: any = {}): Promise<any> {
		const oauth2Client = this;

		return oauth2Client._fetch(url, options).then((response) => {
			if (response.ok) {
				const contentType = response.headers.get('content-type');
				if (
					contentType &&
					contentType.indexOf('application/json') !== -1
				) {
					return response.json();
				}
				else {
					return Promise.resolve(response);
				}
			}

			return Promise.reject(response);
		});
	}

	private _createIframe(
		challenge: ReturnType<typeof pkceChallenge>,
		sessionKey: string
	): Promise<any> {
		const oauth2Client = this;

		const origin = window.location.origin;
		const redirectURI = oauth2Client.redirectURIs.find((uri) =>
			uri.startsWith(origin)
		);

		if (!redirectURI) {
			return Promise.reject(
				`No redirectURI in ${oauth2Client.redirectURIs} matching origin ${origin}`
			);
		}

		const ifrm = document.createElement('iframe');

		ifrm.src = `${oauth2Client.authorizeURL}?client_id=${
			oauth2Client.clientId
		}&code_challenge=${
			challenge.code_challenge
		}&code_challenge_method=S256&redirect_uri=${encodeURIComponent(
			redirectURI
		)}&response_type=code&prompt=none&state=${sessionKey}`;
		ifrm.style.display = 'none';

		document.body.appendChild(ifrm);

		return new Promise((resolve, reject) => {
			const eventHandler = (event: any) => {
				if (oauth2Client.debug) {

					// eslint-disable-next-line no-console
					console.debug('OAuth2Client._createIframe.event', event);
				}

				if (event.data.error) {

					// Remove the iframe and reject the promise

					if (event.target && event.target.parentElement) {
						event.target.parentElement.removeChild(event.target);
					}

					reject(event.data.error);

					return;
				}
				else if (!event.data.code) {

					// Ignore messages that don't contain a code

					return;
				}

				if (event.data.state !== sessionKey) {

					// Remove the iframe and reject the promise

					if (event.target && event.target.parentElement) {
						event.target.parentElement.removeChild(event.target);
					}

					reject('state does not match');

					return;
				}

				const tokenResponse = oauth2Client._requestToken(
					challenge.code_verifier,
					event.data.code,
					redirectURI
				);

				resolve(tokenResponse);

				tokenResponse
					.then((response) =>
						Liferay.Util.SessionStorage.setItem(
							sessionKey,
							JSON.stringify({
								...response,
								expires_after_ms:
									new Date().getTime() +
									response.expires_in * 1000,
							}),
							Liferay.Util.SessionStorage.TYPES.NECESSARY
						)
					)
					.then(() => {

						// Remove the iframe

						if (event.target && event.target.parentElement) {
							event.target.parentElement.removeChild(
								event.target
							);
						}
					});
			};

			if (ifrm.contentWindow) {
				ifrm.contentWindow.addEventListener('message', eventHandler);
			}
		});
	}

	private async _fetch(
		resource: RequestInfo | URL,
		options: RequestInit = {}
	): Promise<any> {
		const oauth2Client = this;

		let resourceUrl: string =
			resource instanceof Request ? resource.url : resource.toString();

		if (
			resourceUrl.includes('//') &&
			!resourceUrl.startsWith(oauth2Client.homePageURL)
		) {
			throw new Error(
				`This client only supports calls to ${oauth2Client.homePageURL}`
			);
		}

		if (!resourceUrl.startsWith(oauth2Client.homePageURL)) {
			if (resourceUrl.startsWith('/')) {
				resourceUrl = resourceUrl.substring(1);
			}

			resourceUrl = `${oauth2Client.homePageURL}/${resourceUrl}`;
		}

		const tokenData = await oauth2Client._getOrRequestToken();

		resource =
			resource instanceof Request
				? {...resource, url: resourceUrl}
				: resourceUrl;

		// This client must avoid using @liferay/portal/no-global-fetch in order
		// to perform OAuth2 token authentication instead
		// eslint-disable-next-line @liferay/portal/no-global-fetch
		return await fetch(resource, {
			...options,
			headers: {
				...options?.headers,
				Authorization: `Bearer ${tokenData.access_token}`,
			},
		});
	}

	private _getOrRequestToken(): Promise<any> {
		const oauth2Client = this;
		const sessionKey = `${oauth2Client.clientId}-${Liferay.authToken}-token`;

		return new Promise((resolve) => {
			const cachedTokenData = Liferay.Util.SessionStorage.getItem(
				sessionKey,
				Liferay.Util.SessionStorage.TYPES.NECESSARY
			);

			if (oauth2Client.debug && cachedTokenData) {

				// eslint-disable-next-line no-console
				console.debug(
					'OAuth2Client._getOrRequestToken.cachedTokenData',
					cachedTokenData
				);
			}

			if (cachedTokenData !== null && cachedTokenData !== undefined) {
				const cachedToken = JSON.parse(
					cachedTokenData
				) as IOAuth2ClientTokenResponse;

				if (new Date().getTime() < cachedToken.expires_after_ms) {
					resolve(cachedToken);

					return;
				}
			}

			resolve(oauth2Client._requestTokenSilently(sessionKey));
		});
	}

	private _requestTokenSilently(sessionKey: string): Promise<any> {
		const oauth2Client = this;
		const challenge = pkceChallenge(128);

		return oauth2Client._createIframe(challenge, sessionKey);
	}

	private async _requestToken(
		codeVerifier: string,
		code: string,
		redirectURI: string
	): Promise<IOAuth2ClientTokenResponse> {
		const oauth2Client = this;

		// This client must avoid using @liferay/portal/no-global-fetch in order
		// to perform OAuth2 token authentication instead
		// eslint-disable-next-line @liferay/portal/no-global-fetch
		const response = await fetch(oauth2Client.tokenURL, {
			body: new URLSearchParams({
				client_id: oauth2Client.clientId,
				code,
				code_verifier: codeVerifier,
				grant_type: 'authorization_code',
				redirect_uri: redirectURI,
			}),
			cache: 'no-cache',
			headers: {
				'Content-Type': 'application/x-www-form-urlencoded',
			},
			method: 'POST',
			mode: 'cors',
		});

		if (response.ok) {
			return response.json();
		}

		return await Promise.reject(response);
	}
}

export function FromParameters(options: IOAuth2ClientFromParametersOptions) {
	return new OAuth2Client({
		authorizeURL: options.authorizeURL || getAuthorizeURL(),
		clientId: options.clientId,
		debug: options.debug,
		homePageURL: options.homePageURL,
		redirectURIs: options.redirectURIs || [getBuiltInRedirectURL()],
		tokenURL: options.tokenURL || getTokenURL(),
	});
}

export async function FromUserAgentApplication(
	userAgentApplicationName: string,
	debug?: boolean
) {
	const userAgentApplication = await getUserAgentApplication(
		userAgentApplicationName
	);

	if (!userAgentApplication) {
		throw new Error(
			`No Application User Agent profile found for ${userAgentApplicationName}`
		);
	}

	return new OAuth2Client({
		authorizeURL: getAuthorizeURL(),
		clientId: userAgentApplication.clientId,
		debug,
		homePageURL: userAgentApplication.homePageURL,
		redirectURIs: userAgentApplication.redirectURIs,
		tokenURL: getTokenURL(),
	});
}

export function getAuthorizeURL() {
	return Liferay.ThemeDisplay.getPathContext() + '/o/oauth2/authorize';
}

export function getBuiltInRedirectURL() {
	return Liferay.ThemeDisplay.getPathContext() + '/o/oauth2/redirect';
}

export function getIntrospectURL() {
	return Liferay.ThemeDisplay.getPathContext() + '/o/oauth2/introspect';
}

export function getTokenURL() {
	return Liferay.ThemeDisplay.getPathContext() + '/o/oauth2/token';
}

export async function getUserAgentApplication(externalReferenceCode: string) {

	// eslint-disable-next-line @liferay/portal/no-global-fetch
	const res = await fetch(
		`${Liferay.ThemeDisplay.getPathContext()}/o/oauth2/application?externalReferenceCode=${externalReferenceCode}`
	);

	const data = await res.json();

	return {
		clientId: data.client_id as string,
		homePageURL: data.homePageURL as string,
		redirectURIs: data.redirectURIs as Array<string>,
	};
}
