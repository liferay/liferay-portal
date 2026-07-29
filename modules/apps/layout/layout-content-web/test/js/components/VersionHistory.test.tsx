/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {
	hideProductMenuIfPresent,
	useMediaQuery,
} from '@liferay/layout-js-components-web';
import {render, screen, waitFor} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import {fetch} from 'frontend-js-web';
import React from 'react';

import '@testing-library/jest-dom';

import VersionHistory from '../../../src/main/resources/META-INF/resources/js/components/VersionHistory';
import {PageVersion} from '../../../src/main/resources/META-INF/resources/js/types/PageVersion';

jest.mock('@liferay/layout-js-components-web', () => {
	const react = require('react');

	return {
		SearchForm: ({onChange}: {onChange: (search: string) => void}) =>
			react.createElement('input', {
				'aria-label': 'search-form',
				'onChange': (event: {target: {value: string}}) =>
					onChange(event.target.value),
			}),
		hideProductMenuIfPresent: jest.fn(),
		useMediaQuery: jest.fn(),
	};
});

jest.mock('frontend-js-components-web', () => ({
	openToast: jest.fn(),
}));

jest.mock('frontend-js-web', () => ({
	...(jest.requireActual('frontend-js-web') as any),
	fetch: jest.fn(),
}));

const VERSIONS: PageVersion[] = [
	{
		creator: {
			externalReferenceCode: 'MARIA_ARCE',
			image: '/image/user_portrait?img_id=1',
			name: 'María Arce',
		},
		dateCreated: '2020-04-12T12:40:00Z',
		dateModified: '2020-04-12T12:40:00Z',
		externalReferenceCode: 'HOME_V_2',
		name: 'Home Halloween',
		status: 'Draft',
		statusDate: '2020-04-12T12:40:00Z',
		version: 2,
	},
	{
		creator: {
			externalReferenceCode: 'CAROLINA_RODRIGUEZ',
			image: '/image/user_portrait?img_id=0',
			name: 'Carolina Rodriguez',
		},
		dateCreated: '2020-03-01T15:40:00Z',
		dateModified: '2020-03-01T15:40:00Z',
		externalReferenceCode: 'HOME_V_1',
		name: 'Home',
		status: 'Approved',
		statusDate: '2020-03-01T15:40:00Z',
		version: 1,
	},
];

const mockFetch = fetch as jest.Mock;
const mockHideProductMenu = hideProductMenuIfPresent as jest.Mock;
const mockUseMediaQuery = useMediaQuery as jest.Mock;

function mockLargeScreen() {
	mockUseMediaQuery.mockReturnValue(true);
}

function mockSmallScreen() {
	mockUseMediaQuery.mockReturnValue(false);
}

function mockVersions(versions: PageVersion[]) {
	mockFetch.mockReturnValue(
		Promise.resolve({
			json: () => Promise.resolve({items: versions}),
			ok: true,
		})
	);
}

function renderComponent({hasDraft = false} = {}) {
	return render(
		<VersionHistory
			config={{
				availableLanguages: {},
				availableSegmentsExperiences: [],
				defaultLanguageId: 'en_US',
				draftName: 'Home',
				hasDraft,
				pageSpecificationVersionsURL: 'url',
			}}
		/>
	);
}

describe('VersionHistory', () => {
	beforeEach(() => {
		mockVersions([]);

		mockHideProductMenu.mockImplementation(
			({onHide}: {onHide: () => void}) => onHide()
		);

		(Liferay.Language.get as jest.Mock).mockImplementation((key: string) =>
			key === 'modified-by-x-on-x' ? 'Modified by {0}, {1}' : key
		);
	});

	afterEach(() => {
		jest.clearAllMocks();
	});

	it('does not show the open button on large screens', async () => {
		mockLargeScreen();

		renderComponent();

		expect(screen.getByText('version-history')).toBeInTheDocument();

		expect(
			screen.queryByRole('button', {name: 'open-version-history-panel'})
		).not.toBeInTheDocument();
	});

	it('reveals the open button after the panel is closed on small screens', async () => {
		mockSmallScreen();

		renderComponent();

		expect(
			screen.queryByRole('button', {name: 'open-version-history-panel'})
		).not.toBeInTheDocument();

		await userEvent.click(screen.getByRole('button', {name: 'close'}));

		expect(
			screen.getByRole('button', {name: 'open-version-history-panel'})
		).toBeInTheDocument();
	});

	it('hides the product menu on mount', async () => {
		mockLargeScreen();

		renderComponent();

		expect(mockHideProductMenu).toHaveBeenCalledTimes(1);
	});

	it('leaves the panel closed until the product menu is hidden', async () => {
		mockSmallScreen();
		mockHideProductMenu.mockImplementation(() => {});

		renderComponent();

		expect(
			screen.getByRole('button', {name: 'open-version-history-panel'})
		).toBeInTheDocument();
	});

	it('shows an empty state when there are no versions', async () => {
		mockLargeScreen();

		renderComponent();

		expect(
			await screen.findByText('there-are-no-results')
		).toBeInTheDocument();

		expect(screen.queryByText('no-results-found')).not.toBeInTheDocument();
	});

	it('renders one item per version', async () => {
		mockLargeScreen();
		mockVersions(VERSIONS);

		renderComponent();

		await waitFor(() =>
			expect(screen.getAllByRole('option')).toHaveLength(2)
		);

		expect(screen.getByText('Home Halloween')).toBeInTheDocument();
		expect(screen.getByText('Home')).toBeInTheDocument();
	});

	it('renders the portrait of the user who modified every version', async () => {
		mockLargeScreen();
		mockVersions(VERSIONS);

		renderComponent();

		expect(
			await screen.findByRole('img', {name: 'María Arce'})
		).toHaveAttribute('src', '/image/user_portrait?img_id=1');

		expect(
			screen.getByRole('img', {name: 'Carolina Rodriguez'})
		).toHaveAttribute('src', '/image/user_portrait?img_id=0');
	});

	it('renders the modifier, the date and the status of every version', async () => {
		mockLargeScreen();
		mockVersions(VERSIONS);

		renderComponent();

		expect(
			await screen.findByText(
				'Modified by María Arce, 04/12/2020 12:40 PM'
			)
		).toBeInTheDocument();

		expect(
			screen.getByText(
				'Modified by Carolina Rodriguez, 03/01/2020 3:40 PM'
			)
		).toBeInTheDocument();

		expect(screen.getByText('draft')).toBeInTheDocument();
		expect(screen.getByText('published')).toBeInTheDocument();
	});

	it('filters the versions by name and by modifier', async () => {
		mockLargeScreen();
		mockVersions(VERSIONS);

		renderComponent();

		await waitFor(() =>
			expect(screen.getAllByRole('option')).toHaveLength(2)
		);

		const search = screen.getByLabelText('search-form');

		await userEvent.type(search, 'Halloween');

		expect(screen.getAllByRole('option')).toHaveLength(1);
		expect(screen.getByText('Home Halloween')).toBeInTheDocument();

		await userEvent.clear(search);
		await userEvent.type(search, 'Carolina');

		expect(screen.getAllByRole('option')).toHaveLength(1);
		expect(screen.getByText('Home')).toBeInTheDocument();

		await userEvent.clear(search);
		await userEvent.type(search, 'zzz');

		expect(screen.queryAllByRole('option')).toHaveLength(0);
		expect(screen.getByText('no-results-found')).toBeInTheDocument();
	});
});
