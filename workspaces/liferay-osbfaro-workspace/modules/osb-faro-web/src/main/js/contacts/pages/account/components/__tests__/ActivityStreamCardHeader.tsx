import ActivityStreamCardHeader from '../ActivityStreamCardHeader';
import React from 'react';
import {fireEvent, render} from '@testing-library/react';
import {RangeKeyTimeRanges} from 'shared/util/constants';

jest.unmock('react-dom');

const baseProps = {
	description: 'The account activities',
	interval: 'D' as const,
	label: 'ACTIVITY STREAM',
	legacy: false,
	onChangeInterval: jest.fn(),
	onRangeSelectorsChange: jest.fn(),
	rangeSelectors: {
		rangeEnd: null,
		rangeKey: RangeKeyTimeRanges.Last30Days,
		rangeStart: null,
	},
	showInterval: true,

	// The range-key dropdown issues its own GraphQL query, which is covered by
	// its own tests. Leaving it out keeps this suite focused on the header.

	showRangeKey: false,
};

const renderHeader = (props = {}) =>
	render(<ActivityStreamCardHeader {...baseProps} {...props} />);

describe('ActivityStreamCardHeader', () => {
	beforeEach(() => {
		jest.clearAllMocks();
	});

	it('renders the label and description', () => {
		const {getByText} = renderHeader();

		expect(getByText('ACTIVITY STREAM')).toBeInTheDocument();
		expect(getByText('The account activities')).toBeInTheDocument();
	});

	it('omits the chart view selector when no change handler is provided', () => {
		const {queryByLabelText} = renderHeader();

		expect(queryByLabelText('Chart View')).toBeNull();
	});

	it('shows the active chart view on the selector trigger', () => {
		const {getByLabelText} = renderHeader({
			chartView: 'line',
			onChartViewChange: jest.fn(),
		});

		expect(getByLabelText('Chart View')).toHaveTextContent('Line Chart');
	});

	it('reports the newly selected chart view', () => {
		const onChartViewChange = jest.fn();

		const {getByLabelText, getByText} = renderHeader({
			chartView: 'bar',
			onChartViewChange,
		});

		fireEvent.click(getByLabelText('Chart View'));
		fireEvent.click(getByText('Line Chart'));

		expect(onChartViewChange).toHaveBeenCalledWith('line');
	});
});
