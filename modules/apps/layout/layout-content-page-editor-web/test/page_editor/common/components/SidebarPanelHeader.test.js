/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import ClayLink from '@clayui/link';
import {render, screen} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import React from 'react';

import {switchSidebarPanel} from '../../../../src/main/resources/META-INF/resources/page_editor/app/actions/index';
import SidebarPanelHeader from '../../../../src/main/resources/META-INF/resources/page_editor/common/components/SidebarPanelHeader';
import StoreMother from '../../../../src/main/resources/META-INF/resources/page_editor/test_utils/StoreMother';

const renderComponent = ({
	dispatch = jest.fn(),
	showCloseButton,
	iconLeft = null,
	iconRight = null,
} = {}) =>
	render(
		<StoreMother.Component dispatch={dispatch}>
			<SidebarPanelHeader
				iconLeft={iconLeft}
				iconRight={iconRight}
				showCloseButton={showCloseButton}
			>
				My Heading
			</SidebarPanelHeader>
		</StoreMother.Component>
	);

describe('SidebarPanelHeader', () => {
	it('renders SidebarPanelHeader', () => {
		renderComponent();

		expect(screen.getByText('My Heading')).toBeInTheDocument();
	});

	it('closes the sidebar when the close button is pressed', async () => {
		const dispatch = jest.fn();

		renderComponent({dispatch});

		await userEvent.click(screen.getByTitle('close'));

		expect(dispatch).toBeCalledWith(
			switchSidebarPanel({
				itemConfigurationOpen: true,
				sidebarOpen: false,
			})
		);
	});

	it('does not show close button when the showCloseButton props is set to false', () => {
		renderComponent({showCloseButton: false});

		expect(screen.queryByTitle('close')).not.toBeInTheDocument();
	});

	it('renders something to the left of the heading when the prop iconLeft is set', () => {
		renderComponent({
			iconLeft: <ClayLink>IconLeft</ClayLink>,
		});

		expect(
			screen.getByText('My Heading').previousSibling
		).toHaveTextContent('IconLeft');
	});

	it('renders something to the right of the heading when the prop iconRight is set', () => {
		renderComponent({
			iconRight: <ClayLink>iconRight</ClayLink>,
		});

		expect(screen.getByText('My Heading').nextSibling).toHaveTextContent(
			'iconRight'
		);
	});
});
