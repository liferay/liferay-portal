/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {render, screen} from '@testing-library/react';
import React from 'react';

import '@testing-library/jest-dom';
import userEvent from '@testing-library/user-event';

import Filter from '../../../../src/main/resources/META-INF/resources/js/components/render_times/Filter';

const FILTERS = {
	origin: 'fromMaster',
	status: 'cached',
	type: 'fragment',
};

const onFilterValue = jest.fn();

const renderComponent = () =>
	render(
		<Filter
			filters={FILTERS}
			onFilterValue={onFilterValue}
			onSearchValue={jest.fn}
			onSort={jest.fn}
		/>
	);

describe('Filter', () => {
	it('renders filters as active', () => {
		renderComponent();

		['cached', 'fragment', 'from-master'].forEach((value) => {
			expect(screen.getByText(value).parentElement).toHaveAttribute(
				'aria-selected',
				'true'
			);
		});
	});

	it('calls onFilterValue when the option is selected', () => {
		renderComponent();

		userEvent.click(screen.getByText('widget'));

		expect(onFilterValue).toHaveBeenCalled();
	});
});
