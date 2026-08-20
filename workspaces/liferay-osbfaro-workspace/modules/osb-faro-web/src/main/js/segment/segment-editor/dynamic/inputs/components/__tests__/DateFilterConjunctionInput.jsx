import client from 'shared/apollo/client';
import DateFilterConjunctionInput from '../DateFilterConjunctionInput';
import mockStore from 'test/mock-store';
import React from 'react';
import {ApolloProvider} from '@apollo/client';
import {clickFirstSelectableDay} from 'test/helpers';
import {cleanup, fireEvent, render} from '@testing-library/react';
import {
	FunctionalOperators,
	RelationalOperators
} from '../../../utils/constants';
import {MockedProvider} from '@apollo/client/testing';
import {mockPreferenceReq} from 'test/graphql-data';
import {Provider} from 'react-redux';

jest.unmock('react-dom');

jest.mock('react-router-dom', () => ({
	...jest.requireActual('react-router-dom'),
	useParams: () => ({
		channelId: '456',
		groupId: '2000',
		query: {
			rangeKey: '30'
		}
	})
}));

jest.mock('shared/hooks/useTimeZone', () => ({
	useTimeZone: () => ({
		timeZoneId: 'UTC'
	})
}));

const WrapperComponent = ({children}) => (
	<ApolloProvider client={client}>
		<Provider store={mockStore()}>
			<MockedProvider mocks={[mockPreferenceReq()]}>
				{children}
			</MockedProvider>
		</Provider>
	</ApolloProvider>
);

describe('DateFilterConjunctionInput', () => {
	afterEach(cleanup);

	it('should render', () => {
		const {container, getAllByText, getByText} = render(
			<WrapperComponent>
				<DateFilterConjunctionInput
					conjunctionCriterion={{propertyName: 'date'}}
					onChange={jest.fn()}
				/>
			</WrapperComponent>
		);
		fireEvent.click(getByText('ever'));

		expect(getByText('since')).toBeTruthy();
		expect(getByText('after')).toBeTruthy();
		expect(getByText('before')).toBeTruthy();
		expect(getByText('between')).toBeTruthy();
		expect(getAllByText('ever')[1]).toBeTruthy();
		expect(getByText('on')).toBeTruthy();

		expect(container).toMatchSnapshot();
	});

	it('should render w/ TimePeriodInput', () => {
		const {getByTestId} = render(
			<WrapperComponent>
				<DateFilterConjunctionInput
					conjunctionCriterion={{
						operatorName: RelationalOperators.GT,
						propertyName: 'date',
						touched: false,
						valid: false,
						value: 'last90Days'
					}}
					onChange={jest.fn()}
				/>
			</WrapperComponent>
		);

		expect(getByTestId('clay-select')).toBeTruthy();
	});

	it('should render w/ DateInput', () => {
		const {getByTestId} = render(
			<WrapperComponent>
				<DateFilterConjunctionInput
					conjunctionCriterion={{
						operatorName: RelationalOperators.EQ,
						propertyName: 'date',
						touched: false,
						valid: false,
						value: '2020-12-12'
					}}
					onChange={jest.fn()}
				/>
			</WrapperComponent>
		);

		expect(getByTestId('date-input')).toBeTruthy();
	});

	it('should render w/ DateRangeInput', () => {
		const {getByTestId} = render(
			<WrapperComponent>
				<DateFilterConjunctionInput
					conjunctionCriterion={{
						operatorName: FunctionalOperators.Between,
						propertyName: 'date',
						touched: false,
						valid: false,
						value: {end: '2020-12-12', start: '2020-12-20'}
					}}
					onChange={jest.fn()}
				/>
			</WrapperComponent>
		);

		expect(getByTestId('date-range-input')).toBeTruthy();
	});

	it('should clear the date when switching from between to a single date operator', () => {
		const ControlledInput = () => {
			const [conjunctionCriterion, setConjunctionCriterion] =
				React.useState({
					operatorName: FunctionalOperators.Between,
					propertyName: 'day',
					touched: false,
					valid: true,
					value: {end: '2020-12-20', start: '2020-12-12'}
				});

			return (
				<DateFilterConjunctionInput
					conjunctionCriterion={conjunctionCriterion}
					onChange={setConjunctionCriterion}
				/>
			);
		};

		const {getByTestId, getByText} = render(
			<WrapperComponent>
				<ControlledInput />
			</WrapperComponent>
		);

		fireEvent.click(getByText('between'));
		fireEvent.click(getByText('after'));

		expect(getByTestId('date-input').value).toBe('');
	});

	it('should emit an ISO date when picking a day for the between operator', () => {
		const onChange = jest.fn();

		const {getByTestId} = render(
			<WrapperComponent>
				<DateFilterConjunctionInput
					conjunctionCriterion={{
						operatorName: FunctionalOperators.Between,
						propertyName: 'day',
						touched: false,
						valid: false,
						value: {end: '', start: ''}
					}}
					onChange={onChange}
				/>
			</WrapperComponent>
		);

		fireEvent.click(getByTestId('date-range-input'));

		clickFirstSelectableDay();

		expect(onChange.mock.calls[0][0].value.start).toMatch(
			/^\d{4}-\d{2}-\d{2}$/
		);
	});
});
