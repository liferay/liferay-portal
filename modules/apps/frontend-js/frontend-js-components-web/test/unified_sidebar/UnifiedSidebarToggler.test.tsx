/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {render, screen} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import React from 'react';

import '@testing-library/jest-dom';

import UnifiedSidebarToggler from '../../src/main/resources/META-INF/resources/unified_sidebar/UnifiedSidebarToggler';

function renderToggler(visible: boolean) {
	render(
		<UnifiedSidebarToggler
			navigationId="navigationId"
			stateChangedEventName="stateChanged"
			stateRequestedEventName="stateRequested"
			visible={visible}
		/>
	);

	return screen.getByRole('tab');
}

describe('UnifiedSidebarToggler', () => {
	it('controls the given navigation', () => {
		const toggler = renderToggler(false);

		expect(toggler).toHaveAttribute('aria-controls', 'navigationId');
		expect(toggler).toHaveAttribute('aria-expanded', 'false');
	});

	it('follows a navigation that rendered before it', () => {
		const navigation = document.createElement('div');

		navigation.id = 'navigationId';

		document.body.appendChild(navigation);

		try {
			expect(renderToggler(true)).toHaveAttribute(
				'aria-expanded',
				'false'
			);
		}
		finally {
			navigation.remove();
		}
	});

	it('requests the opposite state on click', async () => {
		await userEvent.click(renderToggler(true));

		expect(Liferay.fire).toHaveBeenCalledWith('stateRequested', {
			visible: false,
		});
	});
});
