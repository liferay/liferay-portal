/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {cleanup, fireEvent, render} from '@testing-library/react';
import React from 'react';

import '@testing-library/jest-dom';

import CategoriesPopover from '../../../src/main/resources/META-INF/resources/js/components/CategoriesPopover';

const mockProps = {
	categories: ['Teenagers', 'Young adults', 'Adults', 'Children'],
	vocabulary: 'Public',
};

describe('CategoriesPopover', () => {
	beforeEach(() => {
		cleanup();
	});

	it('renders only the button with a "+" and the numbers of categories it will show in a Popover, but not Popover is shown yet', () => {
		const {getByText, queryByText} = render(
			<CategoriesPopover {...mockProps} />
		);

		expect(getByText('+ 4')).toBeInTheDocument();
		expect(queryByText('4 Public categories')).not.toBeInTheDocument();
		expect(queryByText('Teenagers')).not.toBeInTheDocument();
	});

	it('renders the Popover with a title and all the categories when clicking the button', () => {
		const {container, getByText} = render(
			<CategoriesPopover {...mockProps} />
		);

		fireEvent.click(container.querySelector('.category-label-see-more'));
		expect(getByText('+ 4')).toBeInTheDocument();
		expect(getByText('4 Public categories')).toBeInTheDocument();
		expect(getByText('Teenagers')).toBeInTheDocument();
		expect(getByText('Young adults')).toBeInTheDocument();
		expect(getByText('Adults')).toBeInTheDocument();
		expect(getByText('Children')).toBeInTheDocument();
	});

	it('hides the Popover when clicking in the button again', () => {
		const {container, getByText, queryByText} = render(
			<CategoriesPopover {...mockProps} />
		);

		fireEvent.click(container.querySelector('.category-label-see-more'));
		expect(getByText('4 Public categories')).toBeInTheDocument();
		expect(getByText('Teenagers')).toBeInTheDocument();

		fireEvent.click(container.querySelector('.category-label-see-more'));
		expect(queryByText('4 Public categories')).not.toBeInTheDocument();
		expect(queryByText('Teenagers')).not.toBeInTheDocument();
	});

	it('hides the Popover when clicking outside', () => {
		const {container, getByText, queryByText} = render(
			<>
				<CategoriesPopover {...mockProps} />
				<button>Other place to click</button>
			</>
		);

		fireEvent.click(container.querySelector('.category-label-see-more'));
		expect(getByText('4 Public categories')).toBeInTheDocument();
		expect(getByText('Teenagers')).toBeInTheDocument();

		fireEvent.mouseDown(
			container.querySelector(
				':not(.categories-popover):not(.category-label-see-more)'
			)
		);

		expect(queryByText('4 Public categories')).not.toBeInTheDocument();

		expect(queryByText('Teenagers')).not.toBeInTheDocument();
	});
});
