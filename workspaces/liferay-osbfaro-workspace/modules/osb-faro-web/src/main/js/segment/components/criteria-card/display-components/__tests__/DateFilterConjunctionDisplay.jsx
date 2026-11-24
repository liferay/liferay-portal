import DateFilterConjunctionDisplay from '../DateFilterConjunctionDisplay';
import React from 'react';
import {cleanup, render} from '@testing-library/react';
import {
	FunctionalOperators,
	RelationalOperators,
	TimeSpans
} from 'segment/segment-editor/dynamic/utils/constants';

describe('DateFilterConjunctionDisplay', () => {
	afterEach(cleanup);

	it('should render', () => {
		const {container} = render(
			<DateFilterConjunctionDisplay
				conjunctionCriterion={{
					operatorName: RelationalOperators.EQ,
					propertyName: 'date',
					touched: false,
					valid: false,
					value: '2020-12-12'
				}}
			/>
		);

		expect(container).toMatchSnapshot();
	});

	it('should render w/ between', () => {
		const {getByText} = render(
			<DateFilterConjunctionDisplay
				conjunctionCriterion={{
					operatorName: FunctionalOperators.Between,
					propertyName: 'date',
					touched: false,
					valid: false,
					value: {end: '2020-12-12', start: '2020-12-01'}
				}}
			/>
		);

		expect(getByText('between')).toBeTruthy();
	});

	it('should render w/ ever', () => {
		const {getByText} = render(
			<DateFilterConjunctionDisplay
				conjunctionCriterion={{
					propertyName: 'date'
				}}
			/>
		);

		expect(getByText('ever')).toBeTruthy();
	});

	it('should render w/ since', () => {
		const {getByText} = render(
			<DateFilterConjunctionDisplay
				conjunctionCriterion={{
					operatorName: RelationalOperators.GT,
					propertyName: 'date',
					touched: false,
					valid: false,
					value: TimeSpans.Last90Days
				}}
			/>
		);

		expect(getByText('since')).toBeTruthy();
	});
});
