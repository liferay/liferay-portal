/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {render, screen, waitFor} from '@testing-library/react';
import React from 'react';

import SpaceService from '../../../../../src/main/resources/META-INF/resources/js/common/services/SpaceService';
import {Space} from '../../../../../src/main/resources/META-INF/resources/js/common/types/Space';
import SpaceRendererWithCache from '../../../../../src/main/resources/META-INF/resources/js/main_view/props_transformer/cell_renderers/SpaceRendererWithCache';

jest.mock(
	'../../../../../src/main/resources/META-INF/resources/js/common/services/SpaceService'
);

describe('SpaceRendererWithCache', () => {
	const mockSpace = {
		externalReferenceCode: 'ERC',
		id: 123,
		name: 'Test Space',
		settings: {
			logoColor: 'outline-0',
		},
	} as Space;

	afterEach(() => {
		jest.clearAllMocks();
	});

	it('shows a loading indicator while fetching data', () => {
		jest.spyOn(SpaceService, 'getSpaceWithCache').mockReturnValue(
			new Promise(() => {})
		);

		render(<SpaceRendererWithCache spaceExternalReferenceCode="ERC" />);

		expect(
			screen.getByTestId('space-renderer-loading')
		).toBeInTheDocument();
	});

	it('renders the space name and logo color after data is fetched', async () => {
		jest.spyOn(SpaceService, 'getSpaceWithCache').mockResolvedValue(
			mockSpace
		);

		const {container} = render(
			<SpaceRendererWithCache spaceExternalReferenceCode="ERC" />
		);

		expect(await screen.findByText('Test Space')).toBeInTheDocument();

		expect(SpaceService.getSpaceWithCache).toHaveBeenCalledWith('ERC');

		expect(container.querySelector('.sticker')).toHaveClass(
			'sticker-outline-0'
		);
	});

	it('renders nothing when no space data is returned', async () => {
		jest.spyOn(SpaceService, 'getSpaceWithCache').mockResolvedValue(
			null as any
		);

		const {container} = render(
			<SpaceRendererWithCache spaceExternalReferenceCode="ERC" />
		);

		await waitFor(() => {
			expect(container.firstChild).toBeNull();
		});
	});
});
