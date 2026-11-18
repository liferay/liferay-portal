import client from 'shared/apollo/client';
import mockStore from 'test/mock-store';
import React from 'react';
import ValueInput from '../ValueInput';
import {ApolloProvider} from '@apollo/client';
import {DataTypes} from 'shared/types/DataTypes';
import {fireEvent, render} from '@testing-library/react';
import {
	FunctionalOperators,
	RelationalOperators
} from '../../../../utils/constants';
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

describe('ValueInput', () => {
	it('should render', () => {
		const {container, getAllByText, getByText} = render(
			<WrapperComponent>
				<ValueInput
					dataType={DataTypes.Boolean}
					onChange={jest.fn()}
					operatorName={RelationalOperators.EQ}
					touched={false}
					valid
					value='true'
				/>
			</WrapperComponent>
		);
		fireEvent.click(getByText(/^true$/i));

		expect(getAllByText(/^true$/i)[1]).toBeTruthy();
		expect(getByText(/^false$/i)).toBeTruthy();

		expect(container).toMatchSnapshot();
	});

	it.each`
		dataType             | operatorName                    | value                                       | testId
		${DataTypes.Boolean} | ${RelationalOperators.EQ}       | ${'true'}                                   | ${'attribute-value-boolean-input'}
		${DataTypes.Date}    | ${RelationalOperators.EQ}       | ${'2020-12-12'}                             | ${'date-input'}
		${DataTypes.Date}    | ${FunctionalOperators.Between}  | ${{end: '2020-12-12', start: '2020-12-01'}} | ${'date-range-input'}
		${DataTypes.Number}  | ${RelationalOperators.GT}       | ${1000}                                     | ${'number-input'}
		${DataTypes.Number}  | ${FunctionalOperators.Between}  | ${{end: 1000, start: 1}}                    | ${'between-number-end-input'}
		${DataTypes.String}  | ${FunctionalOperators.Contains} | ${'Stuff'}                                  | ${'attribute-value-string-input'}
	`(
		'should find $testId if dataType is $dataType and operatorName is $operatorName',
		({dataType, operatorName, testId, value}) => {
			const {queryByTestId} = render(
				<WrapperComponent>
					<ValueInput
						dataType={dataType}
						onChange={jest.fn()}
						operatorName={operatorName}
						touched={false}
						valid
						value={value}
					/>
				</WrapperComponent>
			);

			expect(queryByTestId(testId)).toBeTruthy();
		}
	);
});
