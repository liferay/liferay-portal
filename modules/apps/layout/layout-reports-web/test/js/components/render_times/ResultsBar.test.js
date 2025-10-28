/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {render, screen} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import React from 'react';

import '@testing-library/jest-dom';

import ResultsBar from '../../../../src/main/resources/META-INF/resources/js/components/render_times/ResultsBar';

const FRAGMENTS = [
	{
		name: 'Container',
	},
	{
		name: 'Heading',
	},
	{
		name: 'Image',
	},
];

const FILTERS = {
	origin: 'fromMaster',
	status: 'cached',
	type: 'fragment',
};

jest.mock('frontend-js-web', () => ({
	...jest.requireActual('frontend-js-web'),
	sub: jest.fn((langKey, args) => langKey.replace('x', args)),
}));

const onSetFilters = jest.fn();

const renderComponent = ({className} = {}) =>
	render(
		<ResultsBar
			className={className}
			filters={FILTERS}
			fragments={FRAGMENTS}
			onSetFilters={onSetFilters}
		/>
	);

describe('ResultsBar', () => {
	it('renders filter labels', () => {
		renderComponent();

		['cached', 'fragment', 'from-master'].forEach((value) => {
			expect(
				screen.getByLabelText(`remove-${value}-filter`)
			).toBeInTheDocument();
		});
	});

	it('shows "x resuls for" label', () => {
		renderComponent();

		expect(screen.getByText('3-results-for')).toBeInTheDocument();
	});

	it('accepts custom classes', () => {
		renderComponent({className: 'my-class-name'});

		expect(document.querySelector('.my-class-name')).toBeInTheDocument();
	});

	it('calls onSetFilters when the filter is removed', () => {
		renderComponent();

		userEvent.click(screen.getByLabelText('remove-fragment-filter'));

		expect(onSetFilters).toHaveBeenCalled();
	});

	it('calls onSetFilters when all filters are removed', () => {
		renderComponent();

		userEvent.click(screen.getByLabelText('clear-filters'));

		expect(onSetFilters).toHaveBeenCalledWith(
			expect.objectContaining({origin: null, status: null, type: null})
		);
	});

	it('sets feedback when the Clear button is pressed', () => {
		renderComponent();

		userEvent.click(screen.getByLabelText('clear-filters'));

		expect(screen.getByRole('alert')).toHaveTextContent('filters-cleared');
	});

	it('sets feedback when a filter is removed', () => {
		renderComponent();

		userEvent.click(screen.getByLabelText('remove-from-master-filter'));

		expect(screen.getByRole('alert')).toHaveTextContent('filter-removed');
	});
});
