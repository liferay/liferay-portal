/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {configure, waitFor} from '@testing-library/dom';
import {act, fireEvent, render, screen} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import {fetch} from 'frontend-js-web';
import React from 'react';

import GlobalMenu from '../src/main/resources/META-INF/resources/js/GlobalMenu';

configure({
	testIdAttribute: 'data-qa-id',
});

const panelAppsURL = '/fetchUrl';

const mockedData = {
	cms: {},
	items: [
		{
			homeURL: '/category1',
			key: 'category1',
			label: 'Category 1',
		},
	],
	portletNamespace: 'portletNamespace',
	sites: {
		recentSites: [
			{
				key: 'Recent1',
				label: 'Recent Site 1',
				logoURL: 'recentSite1Logo',
				url: 'recentSite1Url',
			},
		],
		viewAllURL: '/viewAllURL',
	},
};

const renderComponent = () => {
	return render(
		<GlobalMenu
			panelAppsURL={panelAppsURL}
			selectedPortletId="selectedPortletId"
		/>
	);
};

jest.mock('frontend-js-web', () => ({
	...(jest.requireActual('frontend-js-web') as any),
	fetch: jest.fn(() =>
		Promise.resolve({
			json: () => Promise.resolve(mockedData),
		})
	),
}));

describe('GlobalMenu', () => {
	afterEach(() => {
		Liferay.FeatureFlags['LPD-36105'] = false;
	});

	beforeEach(() => {
		Liferay.FeatureFlags['LPD-36105'] = true;

		global.Liferay.Browser = {
			...(global as any).Liferay,
			isMac: () => false,
		};

		jest.restoreAllMocks();
		jest.clearAllMocks();

		(fetch as jest.Mock).mockClear();
	});

	it('renders Global Menu', async () => {
		renderComponent();

		const trigger = screen.getByTestId('globalMenu');

		expect(trigger).toBeInTheDocument();

		userEvent.click(trigger);

		await waitFor(() => {
			expect(
				screen.getByRole('menuitem', {name: 'Recent Site 1'})
			).toBeInTheDocument();

			expect(
				screen.getByRole('menuitem', {name: 'Category 1'})
			).toBeInTheDocument();

			expect(
				screen.getByRole('menuitem', {name: 'view-all-sites'})
			).toBeInTheDocument();
		});
	});

	it('fetches Global Menu data when trigger button is focused', async () => {
		renderComponent();
		const trigger = screen.getByTestId('globalMenu');

		fireEvent.focus(trigger);

		expect(fetch).toHaveBeenCalledWith(panelAppsURL);
	});

	it('fetches Applications Menu data when trigger button is hovered', async () => {
		renderComponent();
		const trigger = screen.getByTestId('globalMenu');

		fireEvent.mouseOver(trigger);

		expect(fetch).toHaveBeenCalledWith(panelAppsURL);
	});

	it('fetches Applications Menu data when trigger button is clicked', async () => {
		renderComponent();

		const trigger = screen.getByTestId('globalMenu');

		fireEvent.click(trigger);

		expect(fetch).toHaveBeenCalledWith(panelAppsURL);
	});

	it('opens Global Menu modal when clicking ctrl+alt+A', async () => {
		renderComponent();

		act(() => {
			document.body.dispatchEvent(
				new KeyboardEvent('keydown', {
					altKey: true,
					ctrlKey: true,
					key: 'a',
				})
			);
		});

		await waitFor(() => {
			expect(
				screen.getByRole('menuitem', {name: 'view-all-sites'})
			).toBeInTheDocument();
		});
	});
});
