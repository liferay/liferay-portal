/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {configure} from '@testing-library/dom';
import {render, screen} from '@testing-library/react';
import React from 'react';

import SideNavigationScopeItem from '../src/main/resources/META-INF/resources/js/SideNavigationScopeItem';
import {SideNavigationScope} from '../src/main/resources/META-INF/resources/js/types/SideNavigation';

configure({
	testIdAttribute: 'data-qa-id',
});

const renderComponent = ({
	id = 'systemLabel',
	label = 'System',
	scope = 'system',
}: {id?: string; label?: string; scope?: SideNavigationScope} = {}) =>
	render(
		<ul>
			<SideNavigationScopeItem id={id} label={label} scope={scope} />
		</ul>
	);

describe('SideNavigationScopeItem', () => {
	it('names the scope it marks the start of', () => {
		renderComponent();

		expect(screen.getByText('System')).toBeInTheDocument();
	});

	it('carries the id its zone is labelled by', () => {
		renderComponent();

		expect(screen.getByText('System').closest('.label')).toHaveAttribute(
			'id',
			'systemLabel'
		);
	});

	it('marks the start of the instance scope', () => {
		renderComponent({label: 'Instance: Liferay', scope: 'instance'});

		expect(screen.getByTestId('sideNavigationScopeItem')).toHaveClass(
			'side-navigation-scope-item-instance'
		);
	});

	it('marks the start of the system scope', () => {
		renderComponent();

		expect(screen.getByTestId('sideNavigationScopeItem')).toHaveClass(
			'side-navigation-scope-item-system'
		);
	});

	it('offers nothing to interact with', () => {
		renderComponent();

		expect(screen.queryByRole('button')).not.toBeInTheDocument();
		expect(screen.queryByRole('link')).not.toBeInTheDocument();
		expect(screen.queryByRole('menuitem')).not.toBeInTheDocument();
	});
});
