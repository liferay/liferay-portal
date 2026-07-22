import BreakdownTable from '../index';
import React from 'react';
import {
	AttributesContext,
	withAttributesProvider
} from '../../context/attributes';
import {cleanup, render} from '@testing-library/react';
import {MockedProvider} from '@apollo/client/testing';
import {mockEventAnalysisResultReq} from 'test/graphql-data';
import {MemoryRouter} from 'react-router';
import {waitForLoadingToBeRemoved} from 'test/helpers';

const initialAttributes = {
	attributes: {
		1: {
			defaultDataType: 'boolean',
			displayName: 'Boolean Name',
			id: '1',
			name: 'booleanName'
		}
	},
	breakdownOrder: ['111'],
	breakdowns: {
		111: {
			attributeId: '1',
			attributeType: 'EVENT',
			dataType: 'BOOLEAN'
		}
	},
	filterOrder: ['123'],
	filters: {
		123: {
			attributeId: '1',
			attributeType: 'EVENT',
			dataType: 'BOOLEAN',
			operator: 'eq',
			values: ['true']
		}
	}
};

const breakdownItems = [
	{
		__typename: 'BreakdownItem',
		breakdownItems: [
			{
				__typename: 'BreakdownItem',
				breakdownItems: [
					{
						__typename: 'BreakdownItem',
						breakdownItems: [],
						leafNode: false,
						name: 'All Individuals',
						previousValue: 2633,
						value: 1717
					}
				],
				leafNode: true,
				name: 'View Article',
				previousValue: 5033,
				value: 3367
			}
		],
		leafNode: false,
		name: 'articleTitle [0]',
		previousValue: 5033,
		value: 3367
	}
];

const eventAnalysisResult = {
	__typename: 'EventAnalysis',
	breakdownItems,
	count: 1,
	page: 0,
	previousValue: 1234,
	value: 5033
};

jest.unmock('react-dom');

const event = {id: '1', name: 'View Article'};

describe('BreakdownTable', () => {
	afterEach(cleanup);

	it('render', async () => {
		const {container} = render(
			<MemoryRouter>
				<AttributesContext.Provider value={initialAttributes}>
					<MockedProvider
						mocks={[
							mockEventAnalysisResultReq(eventAnalysisResult, {
								eventAnalysisBreakdowns: Object.values(
									initialAttributes.breakdowns
								),
								eventAnalysisFilters: Object.values(
									initialAttributes.filters
								)
							})
						]}
					>
						<BreakdownTable
							channelId='123'
							compareToPrevious
							event={event}
							rangeSelectors={{
								rangeKey: '30'
							}}
							type='TOTAL'
						/>
					</MockedProvider>
				</AttributesContext.Provider>
			</MemoryRouter>
		);

		await waitForLoadingToBeRemoved(container);

		expect(
			container.querySelector('.breakdown-table-root')
		).toBeInTheDocument();
		expect(container.querySelector('.table-root')).toBeInTheDocument();
		expect(container.querySelector('table')).toBeInTheDocument();
	});

	it('render with single event', async () => {
		const BreakdownWithProvider = withAttributesProvider(BreakdownTable);

		const {container} = render(
			<MemoryRouter>
				<MockedProvider
					mocks={[
						mockEventAnalysisResultReq(eventAnalysisResult, {
							eventAnalysisBreakdowns: [],
							eventAnalysisFilters: []
						})
					]}
				>
					<BreakdownWithProvider
						channelId='123'
						compareToPrevious
						event={event}
						rangeSelectors={{
							rangeKey: '30'
						}}
						type='TOTAL'
					/>
				</MockedProvider>
			</MemoryRouter>
		);

		await waitForLoadingToBeRemoved(container);

		expect(
			container.querySelector('.breakdown-table-root')
		).toBeInTheDocument();
		expect(container.querySelector('table')).toBeInTheDocument();
	});

	it('render with empty state', () => {
		const {queryByText} = render(
			<BreakdownTable
				compareToPrevious={false}
				event={null}
				rangeSelectors={{
					rangeKey: '30'
				}}
			/>
		);

		expect(queryByText('Add an event to analyze.')).toBeTruthy();
	});

	it('render breakdown with decoded URL', async () => {
		const eventAnalysisResult = {
			__typename: 'EventAnalysis',
			breakdownItems: [
				{
					__typename: 'BreakdownItem',
					breakdownItems: [],
					leafNode: false,
					name: 'http://localhost:7400/%e6%96%b0%e3%81%97%e3%81%84%e3%82%b5%e3%82%a4%e3%83%88]',
					previousValue: 5033,
					value: 3367
				}
			],
			count: 1,
			page: 0,
			previousValue: 1234,
			value: 5033
		};

		const {container, getByText} = render(
			<MemoryRouter>
				<AttributesContext.Provider value={initialAttributes}>
					<MockedProvider
						mocks={[
							mockEventAnalysisResultReq(eventAnalysisResult, {
								eventAnalysisBreakdowns: Object.values(
									initialAttributes.breakdowns
								),
								eventAnalysisFilters: Object.values(
									initialAttributes.filters
								)
							})
						]}
					>
						<BreakdownTable
							channelId='123'
							compareToPrevious
							event={event}
							rangeSelectors={{
								rangeKey: '30'
							}}
							type='TOTAL'
						/>
					</MockedProvider>
				</AttributesContext.Provider>
			</MemoryRouter>
		);

		await waitForLoadingToBeRemoved(container);

		const spanElement = getByText('http://localhost:7400/新しいサイト]');

		expect(spanElement).toBeInTheDocument();
	});
});
