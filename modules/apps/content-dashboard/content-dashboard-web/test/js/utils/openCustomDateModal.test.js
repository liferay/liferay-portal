/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {act, render, screen} from '@testing-library/react';
import React from 'react';

import '@testing-library/jest-dom';

import {CustomDateModal} from '../../../src/main/resources/META-INF/resources/js/utils/openCustomDateModal';

const DEFAULT_PROPS = {
	dateTypes: [
		{label: 'Create Date', value: 'create-date'},
		{label: 'Display Date', value: 'display-date'},
	],
	filterUrl: 'example-url',
	namespace: 'namespace',
	selectedDateType: '',
	selectedEndDate: '',
	selectedStartDate: '',
};

function renderComponent(props) {
	const componentProps = {...DEFAULT_PROPS, ...props};

	render(<CustomDateModal {...componentProps} />);

	act(() => {
		jest.runAllTimers();
	});
}

describe('CustomDateModal', () => {
	afterAll(() => {
		jest.useRealTimers();
	});

	beforeAll(() => {
		jest.useFakeTimers();
	});

	it('renders Done button disabled when there is no date selected', () => {
		renderComponent();

		expect(screen.getByText('done')).toBeDisabled();
	});

	it('renders Done button enabled when there is a date selected', () => {
		renderComponent({
			selectedDateType: 'create-date',
			selectedEndDate: '2024-02-17',
			selectedStartDate: '2024-01-11',
		});

		expect(screen.getByText('done')).not.toBeDisabled();
	});
});
