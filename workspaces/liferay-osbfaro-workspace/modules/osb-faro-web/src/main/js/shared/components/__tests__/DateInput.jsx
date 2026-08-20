import client from 'shared/apollo/client';
import DateInput from '../DateInput';
import React from 'react';
import {ApolloProvider} from '@apollo/client';
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
});
