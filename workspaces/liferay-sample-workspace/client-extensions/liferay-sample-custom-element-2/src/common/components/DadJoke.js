/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import * as OAuth2 from '@liferay/oauth2-provider-web/client';
import React from 'react';

function DadJoke() {
	const [joke, setJoke] = React.useState(null);

	React.useEffect(() => {
		OAuth2.FromUserAgentApplication(
			'liferay-sample-etc-spring-boot-oauth-application-user-agent'
		)
			.then((oAuth2Client) => {
				oAuth2Client
					?.fetch('/dad/joke')
					.then((response) => response.text())
					.then((joke) => {
						setJoke(joke);
					})

					// eslint-disable-next-line no-console
					.catch((error) => console.log(error));
			})
			.catch();
	}, []);

	if (!joke) {
		return <div>Loading...</div>;
	}

	return <div>{joke}</div>;
}

export default DadJoke;
