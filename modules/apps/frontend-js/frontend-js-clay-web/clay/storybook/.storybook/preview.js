/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

(async () => {
	try {

		// The stylesheet is compiled from the Clay CSS sources next door, so
		// that a change to them shows up in the preview.

		await import('@clayui/css/src/scss/atlas.scss');
	}
	catch (error) {
		console.error(`${error.name}: ${error.message}`);
	}
})();

const spritemap = require('@clayui/css/src/images/icons/icons.svg');
import {Provider} from '@clayui/provider';
import React, {useEffect} from 'react';
import svg4everybody from 'svg4everybody';

export const decorators = [
	(Story) => {
		useEffect(() => {
			svg4everybody({
				polyfill: true,
			});
		}, []);

		return (
			<Provider spritemap={spritemap}>
				<div>
					<Story />
				</div>
			</Provider>
		);
	},
];

export const parameters = {
	options: {
		storySort: {
			order: ['Design System', ['Application', 'Components', 'Charts']],
		},
	},
};
