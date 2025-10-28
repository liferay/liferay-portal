/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {render, screen} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import React from 'react';

import ToggleConfigurationSidebarButton from '../../../../src/main/resources/META-INF/resources/page_editor/app/components/ToggleConfigurationSidebarButton';
import switchSidebarPanel from '../../../../src/main/resources/META-INF/resources/page_editor/app/thunks/switchSidebarPanel';
import StoreMother from '../../../../src/main/resources/META-INF/resources/page_editor/test_utils/StoreMother';

const INITIAL_STATE = {
	sidebar: {itemConfigurationOpen: true},
};

jest.mock(
	'../../../../src/main/resources/META-INF/resources/page_editor/app/thunks/switchSidebarPanel',
	() => jest.fn(() => () => Promise.resolve())
);

const renderComponent = (state = INITIAL_STATE) => {
	return render(
		<StoreMother.Component getState={() => state}>
			<ToggleConfigurationSidebarButton />
		</StoreMother.Component>
	);
};

describe('ToggleConfigurationSidebarButton', () => {
	afterEach(() => {
		switchSidebarPanel.mockClear();
	});

	it('renders the toogle configuration sidebar button and makes sure it has tooltip', () => {
		renderComponent();

		expect(
			screen.getByTitle('open-configuration-panel')
		).toBeInTheDocument();
	});

	it('opens the sidebar when the button is pressed', async () => {
		renderComponent({sidebar: {itemConfigurationOpen: false}});

		const button = screen.getByLabelText('open-configuration-panel');

		await userEvent.click(button);

		expect(switchSidebarPanel).toBeCalledWith({
			itemConfigurationOpen: true,
		});
	});
});
