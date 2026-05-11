/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {render, screen} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import React from 'react';

import {StoreAPIContextProvider} from '../../../../../src/main/resources/META-INF/resources/page_editor/app/contexts/StoreContext';
import LayoutService from '../../../../../src/main/resources/META-INF/resources/page_editor/app/services/LayoutService';
import PageDesignOptionsSidebar from '../../../../../src/main/resources/META-INF/resources/page_editor/plugins/page_design_options/components/PageDesignOptionsSidebar';
import {StyleBookContextProvider} from '../../../../../src/main/resources/META-INF/resources/page_editor/plugins/page_design_options/hooks/useStyleBook';

jest.mock(
	'../../../../../src/main/resources/META-INF/resources/page_editor/app/config/index',
	() => ({
		config: {
			frontendTokens: {},
			layoutType: 'content',
			masterLayouts: [],
			styleBookEntryERC: '',
			styleBookEntryScopeERC: '',
			styleBooks: [
				{
					imagePreviewURL: '',
					name: 'Site Book',
					styleBookEntryERC: 'site-book-erc',
				},
				{
					imagePreviewURL: '',
					name: 'Depot Book',
					styleBookEntryERC: 'depot-book-erc',
					styleBookEntryScopeERC: 'depot-group-erc',
					subtitle: 'Gerardo Depot',
				},
			],
			themeName: 'Classic',
		},
	})
);

jest.mock(
	'../../../../../src/main/resources/META-INF/resources/page_editor/app/services/LayoutService',
	() => ({
		__esModule: true,
		default: {
			changeStyleBookEntry: jest.fn(() =>
				Promise.resolve({tokenValues: {}})
			),
		},
	})
);

const renderSidebar = () =>
	render(
		<StoreAPIContextProvider dispatch={() => {}} getState={() => ({})}>
			<StyleBookContextProvider>
				<PageDesignOptionsSidebar />
			</StyleBookContextProvider>
		</StoreAPIContextProvider>
	);

describe('PageDesignOptionsSidebar', () => {
	afterEach(() => {
		LayoutService.changeStyleBookEntry.mockClear();
	});

	it('renders subtitle on cards whose styleBookEntryScopeERC is set', () => {
		renderSidebar();

		expect(screen.getByText('Gerardo Depot')).toBeInTheDocument();
	});

	it('omits subtitle on cards whose styleBookEntryScopeERC is null', () => {
		renderSidebar();

		const siteCard = screen.getByLabelText('Site Book');

		expect(siteCard.textContent).not.toContain('Gerardo Depot');
	});

	it('exposes scope context via aria-describedby on cross-scope cards', () => {
		renderSidebar();

		const depotCard = screen.getByLabelText('Depot Book');

		const subtitleId = depotCard.getAttribute('aria-describedby');

		expect(subtitleId).toBeTruthy();

		const subtitle = document.getElementById(subtitleId);

		expect(subtitle).toHaveAttribute(
			'aria-label',
			expect.stringContaining('Gerardo Depot')
		);
	});

	it('omits aria-describedby on cards whose styleBookEntryScopeERC is null', () => {
		renderSidebar();

		const siteCard = screen.getByLabelText('Site Book');

		expect(siteCard).not.toHaveAttribute('aria-describedby');
	});

	it('sends styleBookEntryScopeERC on selection of a cross-scope entry', async () => {
		renderSidebar();

		await userEvent.click(screen.getByLabelText(/Depot Book/));

		expect(LayoutService.changeStyleBookEntry).toHaveBeenCalledWith(
			expect.objectContaining({
				styleBookEntryERC: 'depot-book-erc',
				styleBookEntryScopeERC: 'depot-group-erc',
			})
		);
	});

	it('omits styleBookEntryScopeERC on selection of a site-scope entry', async () => {
		renderSidebar();

		await userEvent.click(screen.getByLabelText('Site Book'));

		expect(LayoutService.changeStyleBookEntry).toHaveBeenCalledWith(
			expect.objectContaining({
				styleBookEntryERC: 'site-book-erc',
				styleBookEntryScopeERC: undefined,
			})
		);
	});
});
