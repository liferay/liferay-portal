import * as data from 'test/data';
import EventInput from '../EventInput';
import React from 'react';
import {createNewGroup} from '../../utils/utils';
import {CustomValue, Property} from 'shared/util/records';
import {fireEvent, render} from '@testing-library/react';
import {fromJS} from 'immutable';
import {MemoryRouter, Route} from 'react-router-dom';
import {MockedProvider} from '@apollo/react-testing';
import {mockEventPropertiesReq} from 'test/graphql-data';
import {range} from 'lodash';
import {RelationalOperators} from '../../utils/constants';
import {Routes} from 'shared/util/router';
import {waitForLoading} from 'test/helpers';

jest.unmock('react-dom');

describe('EventInput', () => {
	it('should render', async () => {
		const {container, getAllByRole, getAllByText, getByText} = render(
			<MockedProvider
				mocks={[
					mockEventPropertiesReq(
						range(10).map(i =>
							data.mockEventAttributeDefinition(i, {
								__typename: 'EventProperty'
							})
						),
						{
							eventId: '3',
							page: 0,
							size: 25,
							sort: {
								column: 'name',
								type: 'ASC'
							}
						}
					)
				]}
			>
				<MemoryRouter
					initialEntries={[
						'/workspace/23/123123/contacts/segments/create?type=BATCH'
					]}
				>
					<Route path={Routes.CONTACTS_SEGMENT_CREATE}>
						<EventInput
							displayValue='Asset Clicked'
							onChange={jest.fn()}
							operatorRenderer={() => (
								<div>{'has / has not'}</div>
							)}
							property={Property({
								entityName: 'Event',
								id: '3',
								label: 'assetDepthReached',
								name: '3',
								propertyKey: 'event',
								type: 'event'
							})}
							touched={{attribute: true, attributeValue: true}}
							valid={{attribute: true, attributeValue: true}}
							value={CustomValue(
								fromJS({
									criterionGroup: createNewGroup([
										{
											operatorName: 'eq',
											propertyName: 'eventDefinitionId',
											value: '1'
										},
										{
											operatorName: 'contains',
											propertyName: 'attribute/2',
											value: ''
										},
										{
											operatorName: 'gt',
											propertyName: 'day',
											value: 'last24Hours'
										}
									]),
									operator: RelationalOperators.GT,
									value: 1
								})
							)}
						/>
					</Route>
				</MemoryRouter>
			</MockedProvider>
		);

		await waitForLoading(container);

		jest.runAllTimers();

		fireEvent.click(getAllByRole('combobox')[0]);
		fireEvent.click(getByText('since'));
		fireEvent.click(getByText('Last 24 hours'));
		fireEvent.click(getAllByText('displayName-2')[0]);

		expect(getByText('at least')).toBeTruthy();
		expect(getByText('at most')).toBeTruthy();

		expect(getAllByText('since')[1]).toBeTruthy();
		expect(getByText('after')).toBeTruthy();
		expect(getByText('before')).toBeTruthy();
		expect(getByText('between')).toBeTruthy();
		expect(getByText('ever')).toBeTruthy();
		expect(getByText('on')).toBeTruthy();

		expect(getAllByText('Last 24 hours')[1]).toBeTruthy();
		expect(getByText('Yesterday')).toBeTruthy();
		expect(getByText('Last 7 days')).toBeTruthy();
		expect(getByText('Last 28 days')).toBeTruthy();
		expect(getByText('Last 30 days')).toBeTruthy();
		expect(getByText('Last 90 days')).toBeTruthy();

		expect(getByText('displayName-0')).toBeTruthy();
		expect(getByText('displayName-1')).toBeTruthy();
		expect(getAllByText('displayName-2')[1]).toBeTruthy();
		expect(getByText('displayName-3')).toBeTruthy();
		expect(getByText('displayName-4')).toBeTruthy();
		expect(getByText('displayName-5')).toBeTruthy();
		expect(getByText('displayName-6')).toBeTruthy();
		expect(getByText('displayName-7')).toBeTruthy();
		expect(getByText('displayName-8')).toBeTruthy();
		expect(getByText('displayName-9')).toBeTruthy();

		expect(container).toMatchSnapshot();
	});
});
