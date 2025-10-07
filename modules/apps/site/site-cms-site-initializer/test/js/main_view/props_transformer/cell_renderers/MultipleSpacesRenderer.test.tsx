/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom/extend-expect';
import {render, screen} from '@testing-library/react';
import React from 'react';

import MultipleSpacesRenderer, {
	MultipleSpacesRendererProps,
} from '../../../../../src/main/resources/META-INF/resources/js/main_view/props_transformer/cell_renderers/MultipleSpacesRenderer';

jest.mock('frontend-js-web', () => ({
	sub: (str: string, arg: string) => str.replace('{0}', arg),
}));

const mockLiferayLanguageGet = jest.fn((key: string) => {
	if (key === 'available-in-spaces-x') {
		return 'Available in spaces: {0}';
	}

	return key;
});

(global as any).Liferay = {
	Language: {
		get: mockLiferayLanguageGet,
	},
};

describe('MultipleSpacesRenderer', () => {
	afterEach(() => {
		mockLiferayLanguageGet.mockClear();
		jest.clearAllMocks();
	});

	it('renders "All Spaces" badge when assetLibraryIds includes -1', () => {
		const itemData = {
			assetLibraries: [{id: -1, name: ''}],
		} as MultipleSpacesRendererProps['itemData'];

		render(<MultipleSpacesRenderer itemData={itemData} />);

		expect(screen.getByText('all-spaces')).toBeInTheDocument();
		expect(screen.queryByText('+')).not.toBeInTheDocument();
	});

	it('renders correctly when assetLibraryIds does not include -1', () => {
		const itemData = {
			assetLibraries: [
				{id: 1, name: 'Space 1'},
				{id: 2, name: 'Space 2'},
				{id: 3, name: 'Space 3'},
			],
		} as MultipleSpacesRendererProps['itemData'];

		const additionalSpacesCount = itemData.assetLibraries.length - 1;
		const spaceNames = `${itemData.assetLibraries[0].name}, ${itemData.assetLibraries[1].name}, ${itemData.assetLibraries[2].name}`;

		render(<MultipleSpacesRenderer itemData={itemData} />);

		expect(
			screen.getByText(itemData.assetLibraries[0].name)
		).toBeInTheDocument();
		expect(
			screen.getByText(itemData.assetLibraries[0].name.charAt(0))
		).toBeInTheDocument();

		const spacesDisplay = screen.getByText(`+${additionalSpacesCount}`);
		expect(spacesDisplay).toBeInTheDocument();
		expect(screen.queryByText('all-spaces')).not.toBeInTheDocument();

		expect(spacesDisplay.parentElement).toHaveAttribute(
			'title',
			`Available in spaces: ${spaceNames}`
		);
	});
});
