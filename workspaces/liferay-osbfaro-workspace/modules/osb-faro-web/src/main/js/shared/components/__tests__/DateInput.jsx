import client from 'shared/apollo/client';
import DateInput from '../DateInput';
import React from 'react';
import {ApolloProvider} from '@apollo/client';
import {getCustomDateFormat, getCustomDateTimeFormat} from 'shared/util/date';
import {MockedProvider} from '@apollo/client/testing';
import {mockPreferenceReq} from 'test/graphql-data';
import {render} from '@testing-library/react';

jest.unmock('react-dom');

const WrapperComponent = ({children}) => (
	<ApolloProvider client={client}>
		<MockedProvider mocks={[mockPreferenceReq()]}>
			{children}
		</MockedProvider>
	</ApolloProvider>
);

describe('DateInput', () => {
	it('should render', () => {
		const {getByTestId} = render(
			<WrapperComponent>
				<DateInput />
			</WrapperComponent>
		);

		expect(getByTestId('date-input')).toBeInTheDocument();
	});

	it('should use the displayFormat prop for displaying the date', () => {
		const {getByDisplayValue} = render(
			<WrapperComponent>
				<DateInput
					displayFormat='YYYY MM DD HH:mm'
					onDateInputChange={jest.fn()}
					value='1970-01-01'
				/>
			</WrapperComponent>
		);

		expect(getByDisplayValue('1970 01 01 00:00')).toBeTruthy();
	});

	// An empty value must read as empty so the mask placeholder shows, rather
	// than as moment's "Invalid date" string.

	it('should render an empty value as empty when a displayFormat is set', () => {
		const {getByTestId} = render(
			<WrapperComponent>
				<DateInput
					displayFormat='MMM D, YYYY'
					onDateInputChange={jest.fn()}
					value=''
				/>
			</WrapperComponent>
		);

		expect(getByTestId('date-input').value).toBe('');
	});
	it('should display the date in the custom date format', () => {
		const {getByTestId} = render(
			<WrapperComponent>
				<DateInput
					displayFormat={getCustomDateFormat()}
					onDateInputChange={jest.fn()}
					value='2026-06-10'
				/>
			</WrapperComponent>
		);

		expect(getByTestId('date-input').value).toBe('Jun 10, 2026');
	});

	it('should display the date and time in the custom date time format', () => {
		const {getByTestId} = render(
			<WrapperComponent>
				<DateInput
					displayFormat={getCustomDateTimeFormat()}
					onDateInputChange={jest.fn()}
					showTimeSelector
					value='2026-06-10T14:30:00Z'
				/>
			</WrapperComponent>
		);

		expect(getByTestId('date-input').value).toBe('Jun 10, 2026, 2:30 PM');
	});

	it('should display the date and time converted to the given time zone', () => {
		const {getByTestId} = render(
			<WrapperComponent>
				<DateInput
					displayFormat={getCustomDateTimeFormat()}
					onDateInputChange={jest.fn()}
					showTimeSelector
					timeZoneId='America/Recife'
					value='2026-06-10T14:30:00Z'
				/>
			</WrapperComponent>
		);

		expect(getByTestId('date-input').value).toBe('Jun 10, 2026, 11:30 AM');
	});
});
