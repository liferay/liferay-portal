/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {render, screen} from '@testing-library/react';
import React from 'react';

import '@testing-library/jest-dom';

import CountBadge from '../../src/main/resources/META-INF/resources/js/components/CountBadge';

function renderBadge({count = 6, selected = false} = {}) {
	render(
		<>
			<button aria-describedby="group-count" type="button">
				Group
			</button>

			<CountBadge count={count} id="group-count" selected={selected} />
		</>
	);
}

function badge(count: number) {
	return screen.getByText(String(count)).closest('.badge');
}

describe('CountBadge', () => {
	it('shows the count and describes it out of sight in the plural', () => {
		renderBadge();

		expect(badge(6)).toHaveAttribute('aria-hidden', 'true');
		expect(screen.getByText('6-items')).toHaveClass('sr-only');
		expect(screen.getByRole('button')).toHaveAccessibleDescription(
			'6-items'
		);
	});

	it('describes a single item in the singular', () => {
		renderBadge({count: 1});

		expect(screen.getByRole('button')).toHaveAccessibleDescription(
			'1-item'
		);
	});

	it('stays secondary until selected', () => {
		renderBadge();

		expect(badge(6)).toHaveClass('badge-secondary');
	});

	it('turns primary when selected', () => {
		renderBadge({selected: true});

		expect(badge(6)).toHaveClass('badge-primary');
	});
});
