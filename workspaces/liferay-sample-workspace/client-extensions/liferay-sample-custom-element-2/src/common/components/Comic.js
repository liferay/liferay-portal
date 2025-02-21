/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import * as OAuth2 from '@liferay/oauth2-provider-web/client';
import React from 'react';

function Comic() {
	const [comicData, setComicData] = React.useState(null);

	React.useEffect(() => {
		OAuth2.FromUserAgentApplication(
			'liferay-sample-etc-node-oauth-application-user-agent'
		)
			.then((oAuth2Client) => {
				oAuth2Client?.fetch('/comic').then((comic) => {
					setComicData({
						alt: comic.alt,
						img: comic.img,
						title: comic.safe_title,
					});
				});
			})
			.catch();
	}, []);

	return !comicData ? (
		<div>Loading...</div>
	) : (
		<div>
			<h2>{comicData.title}</h2>

			<p>
				<img alt={comicData.alt} src={comicData.img} />
			</p>
		</div>
	);
}

export default Comic;
