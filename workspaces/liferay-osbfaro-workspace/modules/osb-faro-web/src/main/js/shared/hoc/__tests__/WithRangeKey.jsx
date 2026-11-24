import React from 'react';
import withRangeKey from '../WithRangeKey';
import {RangeKeyTimeRanges} from 'shared/util/constants';
import {render} from '@testing-library/react';

const WrappedComponent = withRangeKey(({rangeSelectors}) => {
	const {rangeEnd, rangeKey, rangeStart} = rangeSelectors;

	return (
		<div>
			<span>{`rangeEnd: ${rangeEnd}`}</span>
			<span>{`rangeKey: ${rangeKey}`}</span>
			<span>{`rangeStart: ${rangeStart}`}</span>
		</div>
	);
});

describe('WithRangeKey', () => {
	it('should render', () => {
		const {getByText} = render(<WrappedComponent />);

		expect(getByText('rangeEnd: null')).toBeTruthy();
		expect(
			getByText(`rangeKey: ${RangeKeyTimeRanges.Last30Days}`)
		).toBeTruthy();
		expect(getByText('rangeStart: null')).toBeTruthy();
	});

	it('should pass rangeSelectors as a prop to the wrapped component', () => {
		const {getByText} = render(
			<WrappedComponent
				rangeSelectors={{
					rangeKey: RangeKeyTimeRanges.Last180Days
				}}
			/>
		);

		expect(getByText('rangeEnd: null')).toBeTruthy();
		expect(
			getByText(`rangeKey: ${RangeKeyTimeRanges.Last180Days}`)
		).toBeTruthy();
		expect(getByText('rangeStart: null')).toBeTruthy();
	});

	it('should pass custom rangeSelectors as a prop to the wrapped component', () => {
		const {getByText} = render(
			<WrappedComponent
				rangeSelectors={{
					rangeEnd: '2022-15-01',
					rangeKey: RangeKeyTimeRanges.CustomRange,
					rangeStart: '2022-10-01'
				}}
			/>
		);

		expect(getByText('rangeEnd: 2022-15-01')).toBeTruthy();
		expect(
			getByText(`rangeKey: ${RangeKeyTimeRanges.CustomRange}`)
		).toBeTruthy();
		expect(getByText('rangeStart: 2022-10-01')).toBeTruthy();
	});
});
