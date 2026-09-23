import client from 'shared/apollo/client';
import DateTimeInput from '../DateTimeInput';
import React from 'react';
import {ApolloProvider} from '@apollo/client';
import {cleanup, fireEvent, render, screen, within} from '@testing-library/react';
import {MockedProvider} from '@apollo/client/testing';
import {mockPreferenceReq} from 'test/graphql-data';
import {Property} from 'shared/util/records';

jest.unmock('react-dom');

describe('DateTimeInput', () => {
	afterEach(cleanup);

	it('should render', () => {
		const {getByText} = render(
			<ApolloProvider client={client}>
				<MockedProvider mocks={[mockPreferenceReq()]}>
					<DateTimeInput
						displayValue='Start Date Time'
						operatorRenderer={() => <div>{'operator'}</div>}
						property={new Property()}
						value='2012-12-12T00:00:00.000Z'
					/>
				</MockedProvider>
			</ApolloProvider>
		);

		expect(getByText('Start Date Time')).toBeInTheDocument();
		expect(getByText('operator')).toBeInTheDocument();
	});

	describe('dates', () => {
		beforeEach(() => {
			jest.useFakeTimers().setSystemTime(
				new Date('2026-06-20T12:00:00.000Z')
			);
		});

		afterEach(() => {
			jest.useRealTimers();
		});

		const renderDateTimeInput = (props = {}) =>
			render(
				<ApolloProvider client={client}>
					<MockedProvider mocks={[mockPreferenceReq()]}>
						<DateTimeInput
							displayValue='Start Date Time'
							onChange={jest.fn()}
							operatorRenderer={() => <div>{'operator'}</div>}
							property={new Property()}
							timeZoneId='America/Recife'
							value='2026-06-10T14:30:00.000Z'
							{...props}
						/>
					</MockedProvider>
				</ApolloProvider>
			);

		it('renders the value in the time zone with the custom date time format', () => {
			renderDateTimeInput();

			expect(screen.getByTestId('date-input')).toHaveValue(
				'Jun 10, 2026, 11:30 AM'
			);
		});

		it('sends the selected day as a long date in UTC', () => {
			const onChange = jest.fn();

			renderDateTimeInput({onChange});

			fireEvent.click(screen.getByTestId('date-input'));

			fireEvent.click(
				within(document.querySelector('.calendar-root')).getByText('15')
			);

			expect(onChange).toHaveBeenCalledWith({
				type: 'date',
				value: 'June 15, 2026'
			});
		});
	});
});
