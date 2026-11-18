import React from 'react';
import TimePeriodInput from '../TimePeriodInput';
import {cleanup, fireEvent, render} from '@testing-library/react';
import {TimeSpans} from '../../../utils/constants';

jest.unmock('react-dom');

describe('TimePeriodInput', () => {
	afterEach(cleanup);

	it('should render', () => {
		const {container, getAllByText, getByText} = render(
			<TimePeriodInput onChange={jest.fn()} value={TimeSpans.Last7Days} />
		);
		fireEvent.click(getByText(/^last 7 days$/i));

		expect(getByText(/^last 24 hours$/i)).toBeTruthy();
		expect(getByText(/^yesterday$/i)).toBeTruthy();
		expect(getAllByText(/^last 7 days$/i)[1]).toBeTruthy();
		expect(getByText(/^last 28 days$/i)).toBeTruthy();
		expect(getByText(/^last 30 days$/i)).toBeTruthy();
		expect(getByText(/^last 90 days$/i)).toBeTruthy();

		expect(container).toMatchSnapshot();
	});
});
