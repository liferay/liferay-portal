/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {render, screen} from '@testing-library/react';
import React from 'react';

import SpaceRenderer from '../../../../../src/main/resources/META-INF/resources/js/main_view/props_transformer/cell_renderers/SpaceRenderer';

describe('SpaceRenderer', () => {
	it('renders the default space', () => {
		const {container} = render(
			<SpaceRenderer logoColor="outline-0" value="Test Space" />
		);

		expect(screen.getByText('Test Space')).toBeInTheDocument();
		expect(screen.queryByRole('link')).toBeNull();
		expect(container.querySelectorAll('.sticker')[0]).toHaveClass(
			...['sticker-outline-0', 'sticker-xs']
		);
	});

	it('renders the correct size prop', () => {
		const {container} = render(
			<SpaceRenderer logoColor="outline-0" size="sm" value="Test Space" />
		);

		expect(container.querySelectorAll('.sticker')[0]).toHaveClass(
			'sticker-sm'
		);
	});

	it('renders the space link correctly', () => {
		render(
			<SpaceRenderer
				href="http://www.test.com/space"
				logoColor="outline-0"
				size="sm"
				value="Test Space"
			/>
		);

		expect(screen.getByRole('link')).toHaveAttribute(
			'href',
			'http://www.test.com/space'
		);
	});
});
