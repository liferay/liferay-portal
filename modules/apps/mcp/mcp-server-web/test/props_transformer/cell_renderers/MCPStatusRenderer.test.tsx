/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {render, screen} from '@testing-library/react';
import React from 'react';

import '@testing-library/jest-dom';

import MCPStatusRenderer from '../../../src/main/resources/META-INF/resources/js/props_transformer/cell_renderers/MCPStatusRenderer';

describe('MCPStatusRenderer', () => {
	it('renders an active status as a success label', () => {
		render(<MCPStatusRenderer value={{key: 'active', name: 'Active'}} />);

		expect(screen.getByText('Active').closest('.label')).toHaveClass(
			'label-success'
		);
	});

	it('renders an inactive status as a danger label', () => {
		render(
			<MCPStatusRenderer value={{key: 'inactive', name: 'Inactive'}} />
		);

		expect(screen.getByText('Inactive').closest('.label')).toHaveClass(
			'label-danger'
		);
	});

	it('renders nothing without a value', () => {
		const {container} = render(<MCPStatusRenderer />);

		expect(container).toBeEmptyDOMElement();
	});
});
