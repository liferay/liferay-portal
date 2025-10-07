import * as API from 'shared/api';
import client from 'shared/apollo/client';
import FilterBySegment from '../FilterBySegment';
import React from 'react';
import {ApolloProvider} from '@apollo/react-hooks';
import {cleanup, fireEvent, render} from '@testing-library/react';
import {MockedProvider} from '@apollo/react-testing';
import {mockSegmentPageViewsReq} from 'test/graphql-data';
import {RangeKeyTimeRanges} from 'shared/util/constants';
import {StaticRouter} from 'react-router';
import {waitForLoadingToBeRemoved} from 'test/helpers';

jest.unmock('react-dom');

jest.mock('react-router-dom', () => ({
	...jest.requireActual('react-router-dom'),
	useParams: () => ({
		channelId: '456',
		groupId: '123',
		title: 'Liferay DXP - Home',
		touchpoint: 'http://liferay.com'
	})
}));

interface IWrapperComponentProps {
	onFilterChange: (item: any) => void;
	segmentPageViews: {segmentId: string; views: number}[];
}

const WrapperComponent: React.FC<IWrapperComponentProps> = ({
	onFilterChange,
	segmentPageViews
}) => (
	<ApolloProvider client={client}>
		<StaticRouter>
			<MockedProvider
				mocks={[mockSegmentPageViewsReq({segmentPageViews})]}
			>
				<FilterBySegment
					onFilterChange={onFilterChange}
					rangeSelectors={{
						rangeEnd: '',
						rangeKey: RangeKeyTimeRanges.Last24Hours,
						rangeStart: ''
					}}
				/>
			</MockedProvider>
		</StaticRouter>
	</ApolloProvider>
);

const MOCK_SEGMENT = (id: string, name: string) => ({
	activeIndividualCount: 0,
	activitiesCount: 0,
	anonymousIndividualCount: 0,
	channelId: '654287566937865570',
	dateCreated: 1700512862880,
	dateModified: 1700512862880,
	filter:
		"(activities.filterByCount(filter='(activityKey eq ''Page#pageViewed#1fa4f8a263c3ba307844bb9d37ac12e238e9f128aa894c2151e4b7cf5aa5e814'' and day gt ''last24Hours'')',operator='ge',value=1))",
	id,
	includeAnonymousUsers: false,
	individualAddedDate: null,
	individualCount: 1,
	knownIndividualCount: 1,
	lastActivityDate: null,
	name,
	properties: {},
	referencedObjects: {},
	segmentType: 'DYNAMIC',
	state: 'READY',
	status: 'ACTIVE',
	type: 4,
	userName: 'Test Test'
});

describe('FilterBySegment', () => {
	afterEach(cleanup);

	beforeAll(() => {
		jest.useFakeTimers();
	});

	it('should render', async () => {
		const {container, getByText} = render(
			<WrapperComponent
				onFilterChange={jest.fn()}
				segmentPageViews={[
					{
						segmentId: '123',
						views: 100
					}
				]}
			/>
		);

		await waitForLoadingToBeRemoved(container);

		expect(getByText('Create Segment')).toHaveAttribute(
			'class',
			'btn btn-block btn-sm btn-secondary'
		);
		expect(container).toMatchSnapshot();
	});

	it('should open dropdown w/ no segments empty state', async () => {
		// @ts-ignore
		API.individualSegment.search.mockReturnValueOnce(
			Promise.resolve({
				disableSearch: false,
				items: [],
				total: 0
			})
		);

		const {container, getByText} = render(
			<WrapperComponent
				onFilterChange={jest.fn()}
				segmentPageViews={[
					{
						segmentId: '123',
						views: 100
					}
				]}
			/>
		);

		await waitForLoadingToBeRemoved(container);

		expect(getByText('Create Segment')).toHaveAttribute(
			'class',
			'btn btn-block btn-sm btn-primary'
		);

		fireEvent.click(getByText('Filter'));

		expect(getByText('Filter By Segment')).toBeInTheDocument();
		expect(getByText('Create Segment')).toHaveAttribute(
			'href',
			'/workspace/123/456/contacts/segments'
		);
		expect(getByText('There are no segments.')).toBeInTheDocument();
		expect(getByText('Start by creating a segment.')).toBeInTheDocument();
		expect(getByText('Learn more about segments.')).toHaveAttribute(
			'href',
			'https://learn.liferay.com/w/dxp/personalization/analytics-cloud/people/segments-analytics'
		);
	});

	it('should open dropdown w/ a list of segments', async () => {
		// @ts-ignore
		API.individualSegment.search.mockReturnValueOnce(
			Promise.resolve({
				disableSearch: false,
				items: [
					MOCK_SEGMENT('123', 'Viewed Page'),
					MOCK_SEGMENT('456', 'Viewed Form'),
					MOCK_SEGMENT('789', 'Viewed Web Content')
				],
				total: 3
			})
		);

		const {container, getByText} = render(
			<WrapperComponent
				onFilterChange={jest.fn()}
				segmentPageViews={[
					{
						segmentId: '123',
						views: 100
					}
				]}
			/>
		);

		await waitForLoadingToBeRemoved(container);

		expect(getByText('Create Segment')).toHaveAttribute(
			'class',
			'btn btn-block btn-sm btn-secondary'
		);

		fireEvent.click(getByText('Filter'));

		expect(getByText('Viewed Page')).toBeInTheDocument();
		expect(getByText('Viewed Form')).toBeInTheDocument();
		expect(getByText('Viewed Web Content')).toBeInTheDocument();
	});

	it('should open dropdown w/ no segments found empty state', async () => {
		// @ts-ignore
		API.individualSegment.search.mockReturnValueOnce(
			Promise.resolve({
				disableSearch: false,
				items: [MOCK_SEGMENT('123', 'Viewed Page')],
				total: 1
			})
		);

		const {container, getByRole, getByText} = render(
			<WrapperComponent
				onFilterChange={jest.fn()}
				segmentPageViews={[
					{
						segmentId: '123',
						views: 100
					}
				]}
			/>
		);

		await waitForLoadingToBeRemoved(container);

		expect(getByText('Create Segment')).toHaveAttribute(
			'class',
			'btn btn-block btn-sm btn-secondary'
		);

		fireEvent.click(getByText('Filter'));

		expect(getByText('Viewed Page')).toBeInTheDocument();

		fireEvent.change(getByRole('textbox'), {
			target: {value: 'Viewed Form'}
		});

		expect(getByText('Create Segment')).toHaveAttribute(
			'class',
			'btn btn-block btn-sm btn-primary'
		);

		expect(getByText('There are no results found.')).toBeInTheDocument();
		expect(
			getByText('Please try a different search term.')
		).toBeInTheDocument();
	});

	it('should open dropdown w/ segments and select one of them', async () => {
		const onFilterChange = jest.fn();

		// @ts-ignore
		API.individualSegment.search.mockReturnValueOnce(
			Promise.resolve({
				disableSearch: false,
				items: [
					MOCK_SEGMENT('123', 'Viewed Page'),
					MOCK_SEGMENT('456', 'Viewed Form'),
					MOCK_SEGMENT('789', 'Viewed Web Content')
				],
				total: 3
			})
		);

		const {container, getByRole, getByText} = render(
			<WrapperComponent
				onFilterChange={onFilterChange}
				segmentPageViews={[
					{
						segmentId: '123',
						views: 100
					},
					{
						segmentId: '456',
						views: 100
					},
					{
						segmentId: '789',
						views: 100
					}
				]}
			/>
		);

		await waitForLoadingToBeRemoved(container);

		expect(getByText('Create Segment')).toHaveAttribute(
			'class',
			'btn btn-block btn-sm btn-secondary'
		);

		fireEvent.click(getByText('Filter'));

		fireEvent.click(getByText('Viewed Page'));

		expect(onFilterChange).toHaveBeenCalledWith(
			expect.objectContaining(MOCK_SEGMENT('123', 'Viewed Page'))
		);

		expect(container.querySelector('.label')).toBeInTheDocument();
		expect(
			getByRole('button', {
				name: /remove filter/i
			})
		).toBeInTheDocument();
	});

	it('should open dropdown w/ segments, select one of them, and then, remove filter', async () => {
		const onFilterChange = jest.fn();

		// @ts-ignore
		API.individualSegment.search.mockReturnValueOnce(
			Promise.resolve({
				disableSearch: false,
				items: [
					MOCK_SEGMENT('123', 'Viewed Page'),
					MOCK_SEGMENT('456', 'Viewed Form'),
					MOCK_SEGMENT('789', 'Viewed Web Content')
				],
				total: 3
			})
		);

		const {container, getByRole, getByText} = render(
			<WrapperComponent
				onFilterChange={onFilterChange}
				segmentPageViews={[
					{
						segmentId: '123',
						views: 100
					},
					{
						segmentId: '456',
						views: 100
					},
					{
						segmentId: '789',
						views: 100
					}
				]}
			/>
		);

		await waitForLoadingToBeRemoved(container);

		expect(getByText('Create Segment')).toHaveAttribute(
			'class',
			'btn btn-block btn-sm btn-secondary'
		);

		fireEvent.click(getByText('Filter'));

		fireEvent.click(getByText('Viewed Page'));

		expect(onFilterChange).toHaveBeenCalledWith(
			expect.objectContaining(MOCK_SEGMENT('123', 'Viewed Page'))
		);

		expect(container.querySelector('.label')).toBeInTheDocument();

		fireEvent.click(
			getByRole('button', {
				name: /remove filter/i
			})
		);

		expect(container.querySelector('.label')).not.toBeInTheDocument();

		expect(onFilterChange).toHaveBeenCalledWith(
			expect.objectContaining([])
		);
	});

	it('should open dropdown w/ segment disabled if there are no views', async () => {
		const onFilterChange = jest.fn();

		// @ts-ignore
		API.individualSegment.search.mockReturnValueOnce(
			Promise.resolve({
				disableSearch: false,
				items: [MOCK_SEGMENT('123', 'Viewed Page')],
				total: 3
			})
		);

		const {container, getByText} = render(
			<WrapperComponent
				onFilterChange={onFilterChange}
				segmentPageViews={[
					{
						segmentId: '123',
						views: 0
					}
				]}
			/>
		);

		await waitForLoadingToBeRemoved(container);

		fireEvent.click(getByText('Filter'));

		const viewedPageSegment = getByText('Viewed Page');

		expect(viewedPageSegment).toHaveAttribute('disabled');

		fireEvent.click(viewedPageSegment);

		expect(onFilterChange).not.toHaveBeenCalledWith();
	});
});
