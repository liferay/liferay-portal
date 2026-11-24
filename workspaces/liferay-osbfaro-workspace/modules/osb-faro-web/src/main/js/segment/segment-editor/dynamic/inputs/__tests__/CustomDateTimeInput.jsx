import client from 'shared/apollo/client';
import CustomDateTimeInput from '../CustomDateTimeInput';
import React from 'react';
import {ApolloProvider} from '@apollo/client';
import {cleanup, fireEvent, render} from '@testing-library/react';
import {createCustomValueMap} from '../../utils/custom-inputs';
import {MockedProvider} from '@apollo/client/testing';
import {mockPreferenceReq} from 'test/graphql-data';
import {Property} from 'shared/util/records';
import {RelationalOperators} from '../../utils/constants';

const mockValue = createCustomValueMap([
	{
		key: 'criterionGroup',
		value: [
			{
				operatorName: RelationalOperators.GT,
				propertyName: 'completeDate',
				value: '2020-01-17T00:00:00.000Z'
			}
		]
	}
]);

describe('CustomDateTimeInput', () => {
	afterEach(cleanup);

	it('should render', () => {
		const {getAllByText, getByText} = render(
			<ApolloProvider client={client}>
				<MockedProvider mocks={[mockPreferenceReq()]}>
					<CustomDateTimeInput
						property={new Property()}
						timeZoneId='UTC'
						value={mockValue}
					/>
				</MockedProvider>
			</ApolloProvider>
		);

		expect(getByText('is after')).toBeInTheDocument();

		fireEvent.click(getByText('is after'));

		expect(getByText('is before')).toBeInTheDocument();
		expect(getByText('is')).toBeInTheDocument();
		expect(getAllByText('is after')[1]).toBeInTheDocument();
	});
});
