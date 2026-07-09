import * as data from 'test/data';
import * as pedantic from 'test/pedantic';
import EventInput from '../EventInput';
import React from 'react';
import {createCustomValueMap} from '../../utils/custom-inputs';
import {fromJS} from 'immutable';
import {InMemoryCache} from '@apollo/client';
import {MemoryRouter, Route} from 'react-router-dom';
import {MockedProvider} from '@apollo/client/testing';
import {mockEventPropertiesReq} from 'test/graphql-data';
import {Property} from 'shared/util/records';
import {range} from 'lodash';
import {fireEvent, render, screen, waitFor} from '@testing-library/react';
import {Routes} from 'shared/util/router';

jest.unmock('react-dom');

jest.mock('../components/attribute-conjunction-input', () => () => (
	<div>{'AttributeConjunctionInput'}</div>
));

const mockValue = createCustomValueMap([
	{
		key: 'criterionGroup',
		value: [
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
		]
	},
	{key: 'operator', value: 'gt'},
	{key: 'value', value: 1}
]);

describe('EventInput', () => {
	let handleWindowError;

	beforeEach(() => {
		pedantic.disable();

		handleWindowError = event => {
			if (
				event.message &&
				event.message.includes('Cannot add property _key')
			) {
				event.preventDefault();
			}
		};

		window.addEventListener('error', handleWindowError);
	});

	afterEach(() => {
		pedantic.enable();

		window.removeEventListener('error', handleWindowError);
	});

	it('should render', async () => {
		const mocks = [
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
		];

		render(
			<MockedProvider
				addTypename={false}
				cache={
					new InMemoryCache({
						addTypename: false,
						freezeResults: false
					})
				}
				mocks={mocks}
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
							property={
								new Property({
									entityName: 'Event',
									id: '3',
									label: 'assetDepthReached',
									name: '3',
									options: [],
									propertyKey: 'event',
									type: 'event'
								})
							}
							segmentType='BATCH'
							touched={{
								attribute: true,
								attributeValue: 'true',
								dateFilter: true,
								occurenceCount: true
							}}
							valid={{
								attribute: true,
								attributeValue: 'true',
								dateFilter: true,
								occurenceCount: true
							}}
							value={fromJS(mockValue)}
						/>
					</Route>
				</MemoryRouter>
			</MockedProvider>
		);

		await waitFor(() =>
			expect(document.body.querySelector('.loading-root')).toBeNull()
		);

		expect(screen.getByText('has / has not')).toBeInTheDocument();
		expect(screen.getByText('Asset Clicked')).toBeInTheDocument();
		expect(screen.getByText('triggered')).toBeInTheDocument();
	}, 10000);

	it('should render the main criterion row before the attributes query resolves', () => {
		const mocks = [
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
		];

		render(
			<MockedProvider
				addTypename={false}
				cache={
					new InMemoryCache({
						addTypename: false,
						freezeResults: false
					})
				}
				mocks={mocks}
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
							property={
								new Property({
									entityName: 'Event',
									id: '3',
									label: 'assetDepthReached',
									name: '3',
									options: [],
									propertyKey: 'event',
									type: 'event'
								})
							}
							segmentType='BATCH'
							touched={{
								attribute: true,
								attributeValue: 'true',
								dateFilter: true,
								occurenceCount: true
							}}
							valid={{
								attribute: true,
								attributeValue: 'true',
								dateFilter: true,
								occurenceCount: true
							}}
							value={fromJS(mockValue)}
						/>
					</Route>
				</MemoryRouter>
			</MockedProvider>
		);

		expect(screen.getByText('has / has not')).toBeInTheDocument();
		expect(screen.getByText('Asset Clicked')).toBeInTheDocument();
		expect(screen.getByText('triggered')).toBeInTheDocument();
	});

	it('should not fetch the event attributes until Add Event Attribute is clicked', async () => {
		const noAttributeValue = createCustomValueMap([
			{
				key: 'criterionGroup',
				value: [
					{
						operatorName: 'eq',
						propertyName: 'eventDefinitionId',
						value: '1'
					},
					{
						operatorName: 'gt',
						propertyName: 'day',
						value: 'last24Hours'
					}
				]
			},
			{key: 'operator', value: 'gt'},
			{key: 'value', value: 1}
		]);

		const mocks = [
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
		];

		render(
			<MockedProvider
				addTypename={false}
				cache={
					new InMemoryCache({
						addTypename: false,
						freezeResults: false
					})
				}
				mocks={mocks}
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
							property={
								new Property({
									entityName: 'Event',
									id: '3',
									label: 'assetDepthReached',
									name: '3',
									options: [],
									propertyKey: 'event',
									type: 'event'
								})
							}
							segmentType='BATCH'
							touched={{
								attribute: false,
								attributeValue: false,
								dateFilter: true,
								occurenceCount: true
							}}
							valid={{
								attribute: true,
								attributeValue: true,
								dateFilter: true,
								occurenceCount: true
							}}
							value={fromJS(noAttributeValue)}
						/>
					</Route>
				</MemoryRouter>
			</MockedProvider>
		);

		expect(screen.getByText('Add Event Attribute')).toBeInTheDocument();
		expect(screen.queryByText('where event attribute')).toBeNull();

		fireEvent.click(screen.getByText('Add Event Attribute'));

		await waitFor(() =>
			expect(screen.getByText('where event attribute')).toBeInTheDocument()
		);
	});
});
