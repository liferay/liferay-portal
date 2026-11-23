/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

// eslint-disable-next-line
import {checkAccessibility} from '@liferay/layout-js-components-web/test/__lib__/index';

import '@testing-library/jest-dom';
import {act, render, screen} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import React from 'react';

import {StoreAPIContextProvider} from '../../../../../../src/main/resources/META-INF/resources/page_editor/app/contexts/StoreContext';
import LayoutService from '../../../../../../src/main/resources/META-INF/resources/page_editor/app/services/LayoutService';
import changeMasterLayout from '../../../../../../src/main/resources/META-INF/resources/page_editor/app/thunks/changeMasterLayout';
import PageDesignOptionsSidebar from '../../../../../../src/main/resources/META-INF/resources/page_editor/plugins/page_design_options/components/PageDesignOptionsSidebar';

jest.mock(
	'../../../../../../src/main/resources/META-INF/resources/page_editor/app/services/LayoutService',
	() => ({
		changeStyleBookEntry: jest.fn(() => Promise.resolve({tokenValues: {}})),
	})
);

jest.mock(
	'../../../../../../src/main/resources/META-INF/resources/page_editor/app/thunks/changeMasterLayout',
	() => jest.fn()
);

const DEFAULT_CONFIG = {
	layoutType: '0',
	masterLayouts: [
		{masterLayoutPageTemplateEntryERC: '', name: 'Blank'},
		{
			masterLayoutPageTemplateEntryERC: '15',
			name: 'Pablo Master Layout',
		},
	],
	portletNamespace: 'ContentPageEditorPortlet',
	styleBookEnabled: true,
	styleBooks: [
		{
			name: 'Pablo Style',
			styleBookEntryERC: '3',
		},
	],
	themeName: 'Test Theme',
};

const mockConfigGetter = jest.fn(() => DEFAULT_CONFIG);

jest.mock(
	'../../../../../../src/main/resources/META-INF/resources/page_editor/app/config/index',
	() => ({
		get config() {
			return mockConfigGetter();
		},
	})
);

const renderComponent = ({masterLayoutPageTemplateEntryERC = ''} = {}) => {
	return render(
		<StoreAPIContextProvider
			dispatch={() => Promise.resolve({styleBook: {}})}
			getState={() => ({
				masterLayout: {
					masterLayoutPageTemplateEntryERC,
				},
				permissions: {
					LOCKED_SEGMENTS_EXPERIMENT: true,
					UPDATE: false,
				},
			})}
		>
			<PageDesignOptionsSidebar />
		</StoreAPIContextProvider>
	);
};

describe('PageDesignOptionsSidebar', () => {
	it('has a sidebar panel title', () => {
		renderComponent();

		expect(screen.getByText('page-design-options')).toBeInTheDocument();
	});

	it('assert style books info message', () => {
		renderComponent();

		expect(
			screen.getByText(
				'only-style-books-based-on-the-frontend-token-definition-provided-by-Test Theme-are-visible'
			)
		).toBeInTheDocument();
	});

	it('checks panel accessibility', async () => {
		const {container} = renderComponent();

		await checkAccessibility({context: container});
	});

	it('calls changeMasterLayout when a master layout is selected', async () => {
		renderComponent();
		const button = screen.getByLabelText('Pablo Master Layout');

		await act(async () => {
			await userEvent.click(button);
		});

		expect(changeMasterLayout).toHaveBeenCalledWith(
			expect.objectContaining({masterLayoutPageTemplateEntryERC: '15'})
		);
	});

	it('calls changeStyleBookEntry when a style is selected', async () => {
		renderComponent();
		const button = screen.getByLabelText('Pablo Style');

		await userEvent.click(button);

		expect(LayoutService.changeStyleBookEntry).toHaveBeenCalledTimes(1);
		expect(LayoutService.changeStyleBookEntry).toHaveBeenCalledWith(
			expect.objectContaining({
				styleBookEntryERC: '3',
			})
		);
	});

	it('renders the more page design options link', () => {
		mockConfigGetter.mockImplementation(() => ({
			...DEFAULT_CONFIG,
			lookAndFeelURL: 'lookAndFeelURL',
		}));

		renderComponent();

		expect(
			screen.getByTitle('more-page-design-options')
		).toBeInTheDocument();

		mockConfigGetter.mockImplementation(() => DEFAULT_CONFIG);
	});

	it('does not render the more page design options link when editing a launch draft', () => {
		mockConfigGetter.mockImplementation(() => ({
			...DEFAULT_CONFIG,
			launchSetId: 1,
			lookAndFeelURL: 'lookAndFeelURL',
		}));

		renderComponent();

		expect(
			screen.queryByTitle('more-page-design-options')
		).not.toBeInTheDocument();

		mockConfigGetter.mockImplementation(() => DEFAULT_CONFIG);
	});
});
