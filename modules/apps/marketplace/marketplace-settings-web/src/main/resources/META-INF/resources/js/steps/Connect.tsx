/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayAlert from '@clayui/alert';
import ClayButton from '@clayui/button';
import {LearnMessage, LearnResourcesContext} from 'frontend-js-components-web';
import {createResourceURL, fetch, objectToFormData} from 'frontend-js-web';
import pkceChallenge from 'pkce-challenge';
import React from 'react';

import Container from '../components/Container';
import {Authorization, MarketplaceSettingsProps} from '../types';

type ConnectProps = {
	authorization: Authorization;
	marketplaceSettingsProps: MarketplaceSettingsProps;
	onDisconnect: () => void;
	onNext: () => void;
	setAuthorization: React.Dispatch<React.SetStateAction<Authorization>>;
};

export default function Connect({
	authorization,
	marketplaceSettingsProps: {
		baseResourceURL,
		learnResources,
		portletNamespace,
		...oAuth2
	},
	onDisconnect,
	onNext,
	setAuthorization,
}: ConnectProps) {
	const onConnect = async () => {
		const {code_challenge, code_verifier} = pkceChallenge();

		const urlSearchParams = new URLSearchParams({
			client_id: oAuth2.clientId,
			code_challenge,
			code_challenge_method: 'S256',
			redirect_uri: oAuth2.url + oAuth2.redirect,
			response_type: 'code',
			state: JSON.stringify({origin: window.location.origin}),
		});

		const authorizeUrl = `${oAuth2.url}/o/oauth2/authorize?${urlSearchParams.toString()}`;

		const popup = window.open(
			authorizeUrl,
			'OAuth2 Popup',
			'addressbar=no,height=800,location=no,menubar=no,toolbar=no,width=500'
		) as Window;

		const handleMessage = async (event: MessageEvent) => {
			const {data, origin} = event;
			const {code, serviceURL, settings} = data ?? {};

			if (oAuth2.url !== origin || !code) {
				return;
			}

			const body = {
				[`${portletNamespace}code`]: code,
				[`${portletNamespace}codeVerifier`]: code_verifier,
				[`${portletNamespace}serviceURL`]: serviceURL,
				[`${portletNamespace}settings`]: JSON.stringify(settings),
			};

			await fetch(
				createResourceURL(baseResourceURL, {
					p_p_resource_id: '/marketplace_settings/connect',
				}).toString(),
				{
					body: objectToFormData(body),
					method: 'POST',
				}
			);

			setAuthorization((prevAuthorization) => ({
				...prevAuthorization,
				data,
			}));

			popup.close();

			window.removeEventListener('message', handleMessage);

			onNext();
		};

		window.addEventListener('message', handleMessage);
	};

	if (authorization.authorized) {
		return (
			<Container
				description={
					<>
						<ClayAlert displayType="success">
							{Liferay.Language.get('connected')}
						</ClayAlert>

						<p>
							{Liferay.Language.get(
								'your-liferay-instance-is-connected-to-the-marketplace'
							)}
						</p>
					</>
				}
				footer={
					<ClayButton
						displayType="secondary"
						onClick={onDisconnect}
						outline
					>
						{Liferay.Language.get('disconnect')}
					</ClayButton>
				}
				title={Liferay.Language.get('marketplace-connection')}
			/>
		);
	}

	return (
		<Container
			description={Liferay.Language.get(
				'clicking-connect-will-open-a-window-where-you-will-need-to-authenticate-yourself-in-the-marketplace-to-continue'
			)}
			footer={
				<ClayButton displayType="primary" onClick={onConnect}>
					{Liferay.Language.get('connect')}
				</ClayButton>
			}
			title={Liferay.Language.get('connect-to-the-marketplace')}
		>
			<h5 className="my-2">{Liferay.Language.get('do-you-need-help')}</h5>

			<LearnResourcesContext.Provider value={learnResources}>
				<LearnMessage
					resource="marketplace-settings-web"
					resourceKey="connect"
				/>
			</LearnResourcesContext.Provider>
		</Container>
	);
}
