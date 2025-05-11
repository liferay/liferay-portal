/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom/extend-expect';
import {fireEvent, render, screen} from '@testing-library/react';
import React from 'react';

import {
	IRangeSelectorsDropdown,
	RangeSelectors,
	RangeSelectorsDropdown,
} from '../../../../src/main/resources/META-INF/resources/js/main/dashboard/components/RangeSelectorsDropdown';

describe('[CMS Dashboard] Components: RangeSelectorsDropdown', () => {
	const mockedOnChange = jest.fn();
	const mockedProps: IRangeSelectorsDropdown = {
		activeRangeSelector: RangeSelectors.Last7Days,
		onChange: mockedOnChange,
	};

	it('renders correctly with different date range options', () => {
		render(<RangeSelectorsDropdown {...mockedProps} />);

		const RangeSelectorDropdown = screen.getByRole('button');
		expect(RangeSelectorDropdown).toBeInTheDocument();
		expect(RangeSelectorDropdown).toHaveTextContent('last-7-days');

		fireEvent.click(RangeSelectorDropdown);

		const YesterdayOption = screen.getByRole('menuitem', {
			name: /(yesterday) (\w{3} \d{2} - \w{3} \d{2})/,
		});
		expect(YesterdayOption).toBeInTheDocument();

		const Last7DaysOption = screen.getByRole('menuitem', {
			name: /(last-7-days) (\w{3} \d{2} - \w{3} \d{2})/,
		});
		expect(Last7DaysOption).toBeInTheDocument();

		const Last28DaysOption = screen.getByRole('menuitem', {
			name: /(last-28-days) (\w{3} \d{2} - \w{3} \d{2})/,
		});
		expect(Last28DaysOption).toBeInTheDocument();

		const Last30DaysOption = screen.getByRole('menuitem', {
			name: /(last-30-days) (\w{3} \d{2} - \w{3} \d{2})/,
		});
		expect(Last30DaysOption).toBeInTheDocument();

		const Last90DaysOption = screen.getByRole('menuitem', {
			name: /(last-90-days) (\w{3} \d{2} - \w{3} \d{2})/,
		});
		expect(Last90DaysOption).toBeInTheDocument();
	});

	it('calls "onChange" function correctly after click on date range option', () => {
		render(<RangeSelectorsDropdown {...mockedProps} />);

		const RangeSelectorDropdown = screen.getByRole('button');
		expect(RangeSelectorDropdown).toBeInTheDocument();
		expect(RangeSelectorDropdown).toHaveTextContent('last-7-days');

		fireEvent.click(RangeSelectorDropdown);

		expect(mockedOnChange).not.toHaveBeenCalled();

		const Last28DaysOption = screen.getByRole('menuitem', {
			name: /(last-28-days)/,
		});
		expect(Last28DaysOption).toBeInTheDocument();

		fireEvent.click(Last28DaysOption);

		expect(mockedOnChange).toHaveBeenCalledTimes(1);
	});

	it('renders correctly with given classname', () => {
		const customClass = 'custom-class';

		const {container} = render(
			<RangeSelectorsDropdown {...mockedProps} className={customClass} />
		);

		const Element = container.querySelector('.custom-class');
		expect(Element).toBeInTheDocument();
	});
});
