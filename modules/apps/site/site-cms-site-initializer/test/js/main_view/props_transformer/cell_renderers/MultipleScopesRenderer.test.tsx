/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {render, screen, waitFor} from '@testing-library/react';
import React from 'react';

import SpaceService from '../../../../../src/main/resources/META-INF/resources/js/common/services/SpaceService';
import {Space} from '../../../../../src/main/resources/META-INF/resources/js/common/types/Space';
import MultipleScopesRenderer, {
	MultipleScopesRendererProps,
} from '../../../../../src/main/resources/META-INF/resources/js/main_view/props_transformer/cell_renderers/MultipleScopesRenderer';

jest.mock(
	'../../../../../src/main/resources/META-INF/resources/js/common/services/SpaceService'
);

jest.mock('frontend-js-web', () => ({
	sub: (str: string, arg: string) => str.replace('{0}', arg),
}));

const mockLiferayLanguageGet = jest.fn((key: string) => {
	if (key === 'available-in-spaces-x') {
		return 'Available in spaces: {0}';
	}

	if (key === 'available-in-projects-x') {
		return 'Available in projects: {0}';
	}

	return key;
});

(global as any).Liferay = {
	Language: {
		get: mockLiferayLanguageGet,
	},
};

const mockSpaces = [
	{
		externalReferenceCode: 'ERC_1',
		id: 1,
		name: 'Space 1',
		settings: {logoColor: 'outline-5'},
	},
	{
		externalReferenceCode: 'ERC_2',
		id: 2,
		name: 'Space 2',
		settings: {logoColor: 'outline-7'},
	},
	{
		externalReferenceCode: 'ERC_3',
		id: 3,
		name: 'Space 3',
		settings: {logoColor: 'outline-9'},
	},
	{
		externalReferenceCode: 'ERC_P1',
		id: 11,
		name: 'Project 1',
		settings: {logoColor: 'outline-2'},
	},
	{
		externalReferenceCode: 'ERC_P2',
		id: 12,
		name: 'Project 2',
		settings: {logoColor: 'outline-3'},
	},
] as Space[];

const itemData = {
	assetLibraries: [
		{externalReferenceCode: 'ERC_1', id: 1, name: ''},
		{externalReferenceCode: 'ERC_2', id: 2, name: ''},
		{externalReferenceCode: 'ERC_3', id: 3, name: ''},
	],
} as MultipleScopesRendererProps['itemData'];

const mockGetSpaceWithCache = () =>
	jest
		.spyOn(SpaceService, 'getSpaceWithCache')
		.mockImplementation((spaceExternalReferenceCode) => {
			return Promise.resolve(
				mockSpaces.find(
					(space) =>
						space.externalReferenceCode ===
						spaceExternalReferenceCode
				)!
			);
		});

describe('MultipleScopesRenderer', () => {
	afterEach(() => {
		mockLiferayLanguageGet.mockClear();
		jest.clearAllMocks();
	});

	it('renders "All Spaces" badge when assetLibraryIds includes -1', () => {
		const itemData = {
			assetLibraries: [{id: -1, name: ''}],
		} as MultipleScopesRendererProps['itemData'];

		render(<MultipleScopesRenderer itemData={itemData} />);

		expect(screen.getByText('all-spaces')).toBeInTheDocument();
		expect(screen.queryByText('+')).not.toBeInTheDocument();
	});

	it('shows a loading indicator while fetching data', () => {
		jest.spyOn(SpaceService, 'getSpaceWithCache').mockReturnValue(
			new Promise(() => {})
		);

		render(<MultipleScopesRenderer itemData={itemData} />);

		expect(
			screen.getByTestId('space-renderer-loading')
		).toBeInTheDocument();
	});

	it('renders correctly after fetching space data', async () => {
		mockGetSpaceWithCache();

		render(<MultipleScopesRenderer itemData={itemData} />);

		await waitFor(() => {
			expect(screen.getByText('Space 1')).toBeInTheDocument();
		});

		const spacesDisplay = screen.getByText('+2');

		expect(screen.queryByText('all-spaces')).not.toBeInTheDocument();

		expect(spacesDisplay.parentElement).toHaveAttribute(
			'title',
			'Available in spaces: Space 1, Space 2, Space 3'
		);
	});

	it('renders fallback names when fetching space data fails', async () => {
		jest.spyOn(SpaceService, 'getSpaceWithCache').mockRejectedValue(
			new Error('API Error')
		);

		const itemData = {
			assetLibraries: [
				{externalReferenceCode: 'ERC_1', id: 1, name: 'Fallback 1'},
				{externalReferenceCode: 'ERC_2', id: 2, name: 'Fallback 2'},
			],
		} as MultipleScopesRendererProps['itemData'];

		render(<MultipleScopesRenderer itemData={itemData} />);

		await waitFor(() => {
			expect(screen.getByText('Fallback 1')).toBeInTheDocument();
		});

		expect(screen.getByText('+1').parentElement).toHaveAttribute(
			'title',
			'Available in spaces: Fallback 1, Fallback 2'
		);
	});

	it('does not render projects when itemData has no projects', () => {
		const itemData = {
			assetLibraries: [{id: -1, name: ''}],
		} as MultipleScopesRendererProps['itemData'];

		render(<MultipleScopesRenderer itemData={itemData} />);

		expect(screen.getByText('all-spaces')).toBeInTheDocument();
		expect(screen.queryByText('all-projects')).not.toBeInTheDocument();
	});

	describe('When itemData has projects', () => {
		beforeEach(() => {
			mockGetSpaceWithCache();
		});

		it('renders both "All Spaces" and "All Projects" badges', async () => {
			const itemData = {
				assetLibraries: [{id: -1, name: ''}],
				projects: [{id: -1, name: ''}],
			} as MultipleScopesRendererProps['itemData'];

			render(<MultipleScopesRenderer itemData={itemData} />);

			await waitFor(() => {
				expect(screen.getByText('all-spaces')).toBeInTheDocument();
			});

			expect(screen.getByText('all-projects')).toBeInTheDocument();
			expect(screen.queryByText('+')).not.toBeInTheDocument();
		});

		it('renders "All Spaces" with individual projects', async () => {
			const itemData = {
				assetLibraries: [{id: -1, name: ''}],
				projects: [
					{externalReferenceCode: 'ERC_P1', id: 11, name: ''},
					{externalReferenceCode: 'ERC_P2', id: 12, name: ''},
				],
			} as MultipleScopesRendererProps['itemData'];

			render(<MultipleScopesRenderer itemData={itemData} />);

			await waitFor(() => {
				expect(screen.getByText('Project 1')).toBeInTheDocument();
			});

			expect(screen.getByText('all-spaces')).toBeInTheDocument();
			expect(screen.queryByText('all-projects')).not.toBeInTheDocument();

			expect(screen.getByText('+1').parentElement).toHaveAttribute(
				'title',
				'Available in projects: Project 1, Project 2'
			);
		});

		it('renders individual spaces with "All Projects"', async () => {
			const itemData = {
				assetLibraries: [
					{externalReferenceCode: 'ERC_1', id: 1, name: ''},
					{externalReferenceCode: 'ERC_2', id: 2, name: ''},
				],
				projects: [{id: -1, name: ''}],
			} as MultipleScopesRendererProps['itemData'];

			render(<MultipleScopesRenderer itemData={itemData} />);

			await waitFor(() => {
				expect(screen.getByText('Space 1')).toBeInTheDocument();
			});

			expect(screen.getByText('all-projects')).toBeInTheDocument();
			expect(screen.queryByText('all-spaces')).not.toBeInTheDocument();

			expect(screen.getByText('+1').parentElement).toHaveAttribute(
				'title',
				'Available in spaces: Space 1, Space 2'
			);
		});

		it('renders individual spaces with individual projects', async () => {
			const itemData = {
				assetLibraries: [
					{externalReferenceCode: 'ERC_1', id: 1, name: ''},
					{externalReferenceCode: 'ERC_2', id: 2, name: ''},
					{externalReferenceCode: 'ERC_3', id: 3, name: ''},
				],
				projects: [
					{externalReferenceCode: 'ERC_P1', id: 11, name: ''},
					{externalReferenceCode: 'ERC_P2', id: 12, name: ''},
				],
			} as MultipleScopesRendererProps['itemData'];

			render(<MultipleScopesRenderer itemData={itemData} />);

			await waitFor(() => {
				expect(screen.getByText('Space 1')).toBeInTheDocument();
			});

			expect(screen.getByText('Project 1')).toBeInTheDocument();

			expect(screen.getByText('+2').parentElement).toHaveAttribute(
				'title',
				'Available in spaces: Space 1, Space 2, Space 3'
			);
			expect(screen.getByText('+1').parentElement).toHaveAttribute(
				'title',
				'Available in projects: Project 1, Project 2'
			);
		});
	});
});
