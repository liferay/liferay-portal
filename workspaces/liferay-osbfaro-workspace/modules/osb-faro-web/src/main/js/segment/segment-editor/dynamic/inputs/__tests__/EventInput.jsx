import * as data from 'test/data';
import * as pedantic from 'test/pedantic';
import EventInput from '../EventInput';
import React from 'react';
import {createCustomValueMap} from '../../utils/custom-inputs';
import {fromJS} from 'immutable';
import {InMemoryCache} from '@apollo/client';
import {MemoryRouter, Route, Routes as RouterRoutes} from 'react-router-dom';
import {MockedProvider} from '@apollo/client/testing';
import {mockEventPropertiesReq} from 'test/graphql-data';
import {Property} from 'shared/util/records';
import {range} from 'lodash';
import {fireEvent, render, screen} from '@testing-library/react';
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

const renderEventInputWithoutAttributes = (segmentType) => {
	const noAttributeValue = createCustomValueMap([
		{
			key: 'criterionGroup',
			value: [
				{
					operatorName: 'eq',
					propertyName: 'eventId',
					value: 'whitePaperDownloaded'
				}
			]
		},
		{key: 'operator', value: 'ge'},
		{key: 'value', value: 1}
	]);

	render(
		<MockedProvider addTypename={false} mocks={[]}>
			<MemoryRouter
				initialEntries={[
					`/workspace/23/123123/contacts/segments/create?type=${segmentType}`
				]}
			>
				<RouterRoutes>
					<Route
						element={
							<EventInput
								displayValue='White Paper Downloaded'
								onChange={jest.fn()}
								operatorRenderer={() => (
									<div>{'has / has not'}</div>
								)}
								property={
									new Property({
										entityName: 'Event',
										id: 'whitePaperDownloaded',
										label: 'White Paper Downloaded',
										name: 'whitePaperDownloaded',
										options: [],
										propertyKey: 'event',
										type: 'event'
									})
								}
								segmentType={segmentType}
								touched={{
									attribute: false,
									attributeValue: false,
									dateFilter: false,
									occurenceCount: false
								}}
								valid={{
									attribute: true,
									attributeValue: true,
									dateFilter: true,
									occurenceCount: true
								}}
								value={fromJS(noAttributeValue)}
							/>
						}
						path={`${Routes.CONTACTS_SEGMENT_CREATE}/*`}
					/>
				</RouterRoutes>
			</MemoryRouter>
		</MockedProvider>
	);
};

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
					<RouterRoutes>
						<Route
							element={
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
							}
							path={`${Routes.CONTACTS_SEGMENT_CREATE}/*`}
						/>
					</RouterRoutes>
				</MemoryRouter>
			</MockedProvider>
		);

		expect(
			await screen.findByText('AttributeConjunctionInput')
		).toBeInTheDocument();

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
					<RouterRoutes>
						<Route
							element={
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
							}
							path={`${Routes.CONTACTS_SEGMENT_CREATE}/*`}
						/>
					</RouterRoutes>
				</MemoryRouter>
			</MockedProvider>
		);

		expect(screen.getByText('has / has not')).toBeInTheDocument();
		expect(screen.getByText('Asset Clicked')).toBeInTheDocument();
		expect(screen.getByText('triggered')).toBeInTheDocument();
	});

	it('should render the occurrence count, the date filter and the attribute filter in a batch segment', () => {
		renderEventInputWithoutAttributes('BATCH');

		expect(screen.getByText('Add Event Attribute')).toBeInTheDocument();
		expect(screen.getByText('time(s)')).toBeInTheDocument();
	});

	it('should not render the occurrence count, the date filter nor the attribute filter in a real time segment', () => {
		renderEventInputWithoutAttributes('REAL_TIME');

		expect(screen.getByText('White Paper Downloaded')).toBeInTheDocument();
		expect(screen.queryByText('Add Event Attribute')).toBeNull();
		expect(screen.queryByText('time(s)')).toBeNull();
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
					<RouterRoutes>
						<Route
							element={
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
							}
							path={`${Routes.CONTACTS_SEGMENT_CREATE}/*`}
						/>
					</RouterRoutes>
				</MemoryRouter>
			</MockedProvider>
		);

		expect(screen.getByText(/^add event attribute$/i)).toBeInTheDocument();
		expect(screen.queryByText('where event attribute')).toBeNull();

		fireEvent.click(screen.getByText(/^add event attribute$/i));

		expect(
			await screen.findByText('where event attribute')
		).toBeInTheDocument();
	});
});
