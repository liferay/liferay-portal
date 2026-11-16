/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {render, screen} from '@testing-library/react';
import React from 'react';

import '@testing-library/jest-dom';

import ProfilePathRenderer from '../../../src/main/resources/META-INF/resources/js/props_transformer/cell_renderers/ProfilePathRenderer';

import type {Profile} from '../../../src/main/resources/META-INF/resources/js/types/Profile';

describe('ProfilePathRenderer', () => {
	it('renders the profile path', () => {
		render(
			<ProfilePathRenderer
				itemData={{
					description: 'Description',
					name: 'Tool Search',
					profilePath: 'tool-search',
				}}
			/>
		);

		expect(screen.getByText('/tool-search')).toBeInTheDocument();
	});

	it('renders a missing profile path as the base URL', () => {
		render(
			<ProfilePathRenderer
				itemData={
					{
						description: 'Description',
						name: 'Profile',
					} as Profile
				}
			/>
		);

		expect(screen.getByText('/')).toBeInTheDocument();
	});
});
