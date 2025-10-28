/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {render, screen} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import React from 'react';

import ItemConfigurationSidebar from '../../../../src/main/resources/META-INF/resources/page_editor/app/components/ItemConfigurationSidebar';
import {ControlsProvider} from '../../../../src/main/resources/META-INF/resources/page_editor/app/contexts/ControlsContext';
import {StoreAPIContextProvider} from '../../../../src/main/resources/META-INF/resources/page_editor/app/contexts/StoreContext';
import switchSidebarPanel from '../../../../src/main/resources/META-INF/resources/page_editor/app/thunks/switchSidebarPanel';

const INITIAL_STATE = {
	sidebar: {itemConfigurationOpen: true},
};

jest.mock(
	'../../../../src/main/resources/META-INF/resources/page_editor/app/thunks/switchSidebarPanel',
	() => jest.fn(() => () => Promise.resolve())
);

const renderComponent = ({activeItemIds = []} = {}) => {
	render(
		<StoreAPIContextProvider getState={() => INITIAL_STATE}>
			<ControlsProvider
				activeInitialState={{
					activeItemIds,
				}}
			>
				<ItemConfigurationSidebar />
			</ControlsProvider>
		</StoreAPIContextProvider>
	);
};

describe('ItemConfiguration', () => {
	it('renders ItemConfigurationSidebar and makes sure that the panel has label', () => {
		renderComponent();

		expect(
			screen.getByLabelText('configuration-panel')
		).toBeInTheDocument();
	});

	it('closes the configuration sidebar when close button is pressed and make sure that this button has title', async () => {
		renderComponent();

		const closeButton = screen.getByTitle('close');

		await userEvent.click(closeButton);

		expect(switchSidebarPanel).toBeCalledWith({
			itemConfigurationOpen: false,
		});
	});

	it('renders multiselect state when multiple items are selected', () => {
		renderComponent({activeItemIds: ['item-1', 'item-2']});

		expect(
			screen.getByText('multiple-page-elements-selected')
		).toBeInTheDocument();
	});
});
