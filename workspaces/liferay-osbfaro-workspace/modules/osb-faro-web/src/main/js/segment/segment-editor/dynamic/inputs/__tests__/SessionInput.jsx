import client from 'shared/apollo/client';
import React from 'react';
import SessionInput from '../SessionInput';
import {ApolloProvider} from '@apollo/client';
import {cleanup, fireEvent, render} from '@testing-library/react';
import {fromJS} from 'immutable';
import {MockedProvider} from '@apollo/client/testing';
import {mockPreferenceReq} from 'test/graphql-data';
import {Property} from 'shared/util/records';
import {PropertyTypes, RelationalOperators} from '../../utils/constants';

jest.unmock('react-dom');

const {EQ} = RelationalOperators;

const WrapperComponent = ({children}) => (
	<ApolloProvider client={client}>
		<MockedProvider mocks={[mockPreferenceReq()]}>
			{children}
		</MockedProvider>
	</ApolloProvider>
);

describe('SessionInput', () => {
	afterEach(cleanup);

	it('should render', () => {
		const {getAllByText, getByText} = render(
			<WrapperComponent>
				<SessionInput
					operatorRenderer={() => <div>{'operator'}</div>}
					property={new Property()}
					touched={{customInput: true, dateFilter: true}}
					valid={{customInput: true, dateFilter: true}}
					value={fromJS({
						criterionGroup: {
							items: [
								{operatorName: EQ},
								{
									operatorName: EQ,
									propertyName: 'completeDate',
									value: '2021-01-01'
								}
							]
						}
					})}
				/>
			</WrapperComponent>
		);
		fireEvent.click(getByText('is'));
		fireEvent.click(getByText('on'));

		expect(getAllByText('is')[1]).toBeInTheDocument();
		expect(getByText('is not')).toBeInTheDocument();
		expect(getByText('contains')).toBeInTheDocument();
		expect(getByText('does not contain')).toBeInTheDocument();
		expect(getByText('is known')).toBeInTheDocument();
		expect(getByText('is unknown')).toBeInTheDocument();

		expect(getByText('since')).toBeInTheDocument();
		expect(getByText('after')).toBeInTheDocument();
		expect(getByText('before')).toBeInTheDocument();
		expect(getByText('between')).toBeInTheDocument();
		expect(getByText('ever')).toBeInTheDocument();
		expect(getAllByText('on')[1]).toBeInTheDocument();
	});

	it('should render with "ever"', () => {
		const {getAllByText, getByText} = render(
			<WrapperComponent>
				<SessionInput
					operatorRenderer={() => <div>{'operator'}</div>}
					property={new Property()}
					touched={{customInput: true, dateFilter: true}}
					valid={{customInput: true, dateFilter: true}}
					value={fromJS({
						criterionGroup: {
							items: [{operatorName: EQ}]
						}
					})}
				/>
			</WrapperComponent>
		);
		fireEvent.click(getByText('is'));
		fireEvent.click(getByText('ever'));

		expect(getAllByText('is')[1]).toBeInTheDocument();
		expect(getByText('is not')).toBeInTheDocument();
		expect(getByText('contains')).toBeInTheDocument();
		expect(getByText('does not contain')).toBeInTheDocument();
		expect(getByText('is known')).toBeInTheDocument();
		expect(getByText('is unknown')).toBeInTheDocument();

		expect(getByText('since')).toBeInTheDocument();
		expect(getByText('after')).toBeInTheDocument();
		expect(getByText('before')).toBeInTheDocument();
		expect(getByText('between')).toBeInTheDocument();
		expect(getAllByText('ever')[1]).toBeInTheDocument();
		expect(getByText('on')).toBeInTheDocument();
	});

	it('should render a CustomNumberInput', () => {
		const {getByTestId} = render(
			<WrapperComponent>
				<SessionInput
					operatorRenderer={() => <div>{'operator'}</div>}
					property={new Property({type: PropertyTypes.SessionNumber})}
					touched={{customInput: true, dateFilter: true}}
					valid={{customInput: true, dateFilter: true}}
					value={fromJS({
						criterionGroup: {items: [{operatorName: EQ}]}
					})}
				/>
			</WrapperComponent>
		);

		expect(getByTestId('number-input')).toBeTruthy();
	});

	it('should render without the date filter conjunction for PropertyTypes.SessionChannel', () => {
		const {getByText, queryByText} = render(
			<WrapperComponent>
				<SessionInput
					operatorRenderer={() => <div>{'operator'}</div>}
					property={
						new Property({
							options: [
								{label: 'Direct', value: 'Direct'},
								{label: 'Organic', value: 'Organic'}
							],
							type: PropertyTypes.SessionChannel
						})
					}
					touched={{customInput: true}}
					valid={{customInput: true}}
					value={fromJS({
						criterionGroup: {
							items: [{operatorName: EQ, value: 'Direct'}]
						}
					})}
				/>
			</WrapperComponent>
		);
		fireEvent.click(getByText('is'));

		expect(getByText('is not')).toBeInTheDocument();

		expect(queryByText('since')).toBeNull();
		expect(queryByText('after')).toBeNull();
		expect(queryByText('before')).toBeNull();
		expect(queryByText('between')).toBeNull();
		expect(queryByText('ever')).toBeNull();
	});
});
