/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {cleanup, fireEvent, render} from '@testing-library/react';
import React from 'react';

import EmptyState from '../../../../src/main/resources/META-INF/resources/js/shared/components/empty-state/EmptyState.es';

describe('The EmptyState component should', () => {
	afterEach(cleanup);

	test('Be render with message only', () => {
		const {getByText} = render(<EmptyState filtered hideAnimation />);

		const message = getByText('no-results-were-found');

		expect(message).toBeTruthy();
	});

	test('Be render with message and animation', () => {
		const {container, getByText} = render(<EmptyState filtered />);

		const animation = container.querySelector('.c-empty-state-image');
		const message = getByText('no-results-were-found');

		expect(animation).toBeTruthy();
		expect(message).toBeTruthy();
	});

	test('Be render with message, title and animation', () => {
		const {container, getByText} = render(<EmptyState title="No data" />);

		const animation = container.querySelector('.c-empty-state-image');
		const message = getByText('there-is-no-data-at-the-moment');
		const title = getByText('No data');

		expect(animation).toBeTruthy();
		expect(message).toBeTruthy();
		expect(title).toBeTruthy();
	});

	test('Be render with message and action button', () => {
		const mockClick = jest.fn();

		const {getByText} = render(
			<EmptyState
				actionButton={<button onClick={mockClick}>Reload</button>}
				hideAnimation
				message="Failed to retrieve data, click on 'Reload' to retrying."
			/>
		);

		const button = getByText('Reload');
		const message = getByText(
			"Failed to retrieve data, click on 'Reload' to retrying."
		);

		expect(message).toBeTruthy();

		fireEvent.click(button);

		expect(mockClick).toHaveBeenCalled();
	});
});
