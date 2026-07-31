import AccountsDataSet from '../AccountsDataSet';
import React from 'react';
import {cleanup, render, screen} from '@testing-library/react';
import {LifecycleStages} from 'contacts/pages/account/utils/constants';
import {RangeKeyTimeRanges} from 'shared/util/constants';
import {useRequest} from 'shared/hooks/useRequest';

const DEFAULT_STAGE_ITEMS = [
	{id: '9990', stageType: LifecycleStages.AWARE},
	{id: '9991', stageType: LifecycleStages.ENGAGED},
	{id: '9992', stageType: LifecycleStages.PIPELINE},
	{id: '9993', stageType: LifecycleStages.AT_RISK},
];

jest.unmock('react-dom');

jest.mock('shared/api', () => ({
	accounts: {
		fetchLifecycleStageFieldValues: jest.fn(),
	},
}));

jest.mock('shared/hooks/useRequest', () => ({
	useRequest: jest.fn(),
}));

const mockedUseRequest = useRequest as jest.Mock;

const mockStages = (items: typeof DEFAULT_STAGE_ITEMS | undefined) => {
	mockedUseRequest.mockReturnValue({
		data: items ? {items} : undefined,
		loading: false,
	});
};

type FakeFilter = {
	apiURL?: string;
	id: string;
	items?: Array<{label: string; value: string}>;
	preloadedData?: {
		exclude: boolean;
		selectedItems: Array<{label?: string; value: string}>;
	};
};

type FakeCustomDataRenderers = {
	accountNameRenderer: (props: {
		itemData: {id: string | number};
		value: string;
	}) => React.ReactElement;
	activitiesCountRenderer: (props: {value?: number}) => React.ReactElement;
};

let lastApiURL: string | undefined;
let lastCustomDataRenderers: FakeCustomDataRenderers | undefined;
let lastFilters: FakeFilter[] | undefined;
let mountCount = 0;

jest.mock('@liferay/frontend-data-set-web', () => ({
	...jest.requireActual('@liferay/frontend-data-set-web'),
	FrontendDataSet: ({
		apiURL,
		customDataRenderers,
		filters,
		id,
	}: {
		apiURL: string;
		customDataRenderers: FakeCustomDataRenderers;
		filters: FakeFilter[];
		id: string;
	}) => {
		lastApiURL = apiURL;
		lastCustomDataRenderers = customDataRenderers;
		lastFilters = filters;

		React.useEffect(() => {
			mountCount += 1;
		}, []);

		return <div data-testid="fds-component" id={id} />;
	},
}));

describe('AccountsDataSet', () => {
	beforeEach(() => {
		jest.clearAllMocks();
		mockStages(DEFAULT_STAGE_ITEMS);
		lastApiURL = undefined;
		lastCustomDataRenderers = undefined;
		lastFilters = undefined;
		mountCount = 0;
	});

	afterEach(cleanup);

	it('should render the FrontendDataSet with id "accounts-list-dataset"', () => {
		render(
			<AccountsDataSet apiURL="fake-url" channelId="123" groupId="23" />
		);

		expect(screen.getByTestId('fds-component')).toHaveAttribute(
			'id',
			'accounts-list-dataset'
		);
	});

	it('should pass the apiURL directly to FrontendDataSet without appending range params', () => {
		render(
			<AccountsDataSet apiURL="fake-url" channelId="123" groupId="23" />
		);

		expect(lastApiURL).toBe('fake-url');
	});

	it('should preload the rangeKey filter with Last 30 Days by default', () => {
		render(
			<AccountsDataSet apiURL="fake-url" channelId="123" groupId="23" />
		);

		const rangeKeyFilter = lastFilters?.find((f) => f.id === 'rangeKey');

		expect(rangeKeyFilter?.preloadedData).toEqual({
			exclude: false,
			selectedItems: [
				{
					label: 'Last 30 days',
					value: RangeKeyTimeRanges.Last30Days,
				},
			],
		});
	});

	it('should include all 8 time range options in the rangeKey filter', () => {
		render(
			<AccountsDataSet apiURL="fake-url" channelId="123" groupId="23" />
		);

		const rangeKeyFilter = lastFilters?.find((f) => f.id === 'rangeKey');

		expect(rangeKeyFilter?.items).toHaveLength(8);
		expect(rangeKeyFilter?.items?.map((i) => i.value)).toEqual([
			RangeKeyTimeRanges.Last24Hours,
			RangeKeyTimeRanges.Yesterday,
			RangeKeyTimeRanges.Last7Days,
			RangeKeyTimeRanges.Last28Days,
			RangeKeyTimeRanges.Last30Days,
			RangeKeyTimeRanges.Last90Days,
			RangeKeyTimeRanges.Last180Days,
			RangeKeyTimeRanges.LastYear,
		]);
	});

	it('should leave country and industry filters without preloadedData when no props are passed', () => {
		render(
			<AccountsDataSet apiURL="fake-url" channelId="123" groupId="23" />
		);

		const countryFilter = lastFilters?.find((f) => f.id === 'country');
		const industryFilter = lastFilters?.find((f) => f.id === 'industry');

		expect(countryFilter?.preloadedData).toBeUndefined();
		expect(industryFilter?.preloadedData).toBeUndefined();
	});

	it('should omit the lifecycleStatus filter when accountLifecycleId is not provided', () => {
		render(
			<AccountsDataSet apiURL="fake-url" channelId="123" groupId="23" />
		);

		const lifecycleStatusFilter = lastFilters?.find(
			(f) => f.id === 'lifecycleStatus'
		);

		expect(lifecycleStatusFilter).toBeUndefined();
	});

	it('should preload the country filter when countryFilter prop is provided', () => {
		render(
			<AccountsDataSet
				apiURL="fake-url"
				channelId="123"
				countryFilter="US"
				groupId="23"
			/>
		);

		const countryFilter = lastFilters?.find((f) => f.id === 'country');

		expect(countryFilter?.preloadedData).toEqual({
			exclude: false,
			selectedItems: [{label: 'US', value: 'US'}],
		});
	});

	it('should preload the industry filter when industryFilter prop is provided', () => {
		render(
			<AccountsDataSet
				apiURL="fake-url"
				channelId="123"
				groupId="23"
				industryFilter="Tech"
			/>
		);

		const industryFilter = lastFilters?.find((f) => f.id === 'industry');

		expect(industryFilter?.preloadedData).toEqual({
			exclude: false,
			selectedItems: [{label: 'Tech', value: 'Tech'}],
		});
	});

	it('should build the lifecycleStatus items from the fetched stages with localized labels', () => {
		render(
			<AccountsDataSet
				accountLifecycleId="al-1"
				apiURL="fake-url"
				channelId="123"
				groupId="23"
			/>
		);

		const lifecycleStatusFilter = lastFilters?.find(
			(f) => f.id === 'lifecycleStatus'
		);

		expect(lifecycleStatusFilter?.items).toEqual([
			{label: 'Aware', value: '9990'},
			{label: 'Engaged', value: '9991'},
			{label: 'Pipeline', value: '9992'},
			{label: 'At Risk', value: '9993'},
		]);
	});

	it('should preload the lifecycleStatus filter with the stage id and localized label when lifecycleStageFilter prop is provided', () => {
		render(
			<AccountsDataSet
				accountLifecycleId="al-1"
				apiURL="fake-url"
				channelId="123"
				groupId="23"
				lifecycleStageFilter={LifecycleStages.AT_RISK}
			/>
		);

		const lifecycleStatusFilter = lastFilters?.find(
			(f) => f.id === 'lifecycleStatus'
		);

		expect(lifecycleStatusFilter?.preloadedData).toEqual({
			exclude: false,
			selectedItems: [{label: 'At Risk', value: '9993'}],
		});
	});

	it('should leave the lifecycleStatus preloadedData undefined when stages have not loaded yet', () => {
		mockStages(undefined);

		render(
			<AccountsDataSet
				accountLifecycleId="al-1"
				apiURL="fake-url"
				channelId="123"
				groupId="23"
				lifecycleStageFilter={LifecycleStages.AT_RISK}
			/>
		);

		const lifecycleStatusFilter = lastFilters?.find(
			(f) => f.id === 'lifecycleStatus'
		);

		expect(lifecycleStatusFilter?.preloadedData).toBeUndefined();
	});

	it('should render the account name link with channelId in the href', () => {
		render(
			<AccountsDataSet apiURL="fake-url" channelId="123" groupId="23" />
		);

		const {container} = render(
			lastCustomDataRenderers!.accountNameRenderer({
				itemData: {id: 'abc'},
				value: 'Acme Corp',
			})
		);

		expect(container.querySelector('a')).toHaveAttribute(
			'href',
			'/workspace/23/123/contacts/accounts/abc'
		);
	});

	it('should render 0 recent activities when the account has no events', () => {
		render(
			<AccountsDataSet apiURL="fake-url" channelId="123" groupId="23" />
		);

		const {container} = render(
			lastCustomDataRenderers!.activitiesCountRenderer({
				value: undefined,
			})
		);

		expect(container).toHaveTextContent('0');
	});

	it('should render the recent activities count when the account has events', () => {
		render(
			<AccountsDataSet apiURL="fake-url" channelId="123" groupId="23" />
		);

		const {container} = render(
			lastCustomDataRenderers!.activitiesCountRenderer({value: 42})
		);

		expect(container).toHaveTextContent('42');
	});

	it('should not expose a filter for recent activities', () => {
		render(
			<AccountsDataSet apiURL="fake-url" channelId="123" groupId="23" />
		);

		const activitiesCountFilter = lastFilters?.find(
			(f) => f.id === 'activitiesCount'
		);

		expect(activitiesCountFilter).toBeUndefined();
	});

	it('should not append segmentFilter as a query param on the apiURL', () => {
		render(
			<AccountsDataSet
				apiURL="fake-url"
				channelId="123"
				groupId="23"
				segmentFilter="segment-100"
			/>
		);

		expect(lastApiURL).toBe('fake-url');
	});

	it('should leave the segment filter without preloadedData when no segmentFilter prop is passed', () => {
		render(
			<AccountsDataSet apiURL="fake-url" channelId="123" groupId="23" />
		);

		const segmentFilter = lastFilters?.find((f) => f.id === 'segmentId');

		expect(segmentFilter?.preloadedData).toBeUndefined();
	});

	it('should preload the segment filter with the segment name when segmentFilter and segmentName props are provided', () => {
		render(
			<AccountsDataSet
				apiURL="fake-url"
				channelId="123"
				groupId="23"
				segmentFilter="segment-100"
				segmentName="VIP Customers"
			/>
		);

		const segmentFilter = lastFilters?.find((f) => f.id === 'segmentId');

		expect(segmentFilter?.preloadedData).toEqual({
			exclude: false,
			selectedItems: [
				{label: 'VIP Customers', value: 'segment-100'},
			],
		});
	});

	it('should point the segment filter apiURL at the individual segment search endpoint', () => {
		render(
			<AccountsDataSet apiURL="fake-url" channelId="123" groupId="23" />
		);

		const segmentFilter = lastFilters?.find((f) => f.id === 'segmentId');

		expect(segmentFilter?.apiURL).toBe(
			'/o/faro/contacts/23/individual_segment?channelId=123'
		);
	});

	it('should remount the FrontendDataSet when segmentFilter changes', () => {
		const {rerender} = render(
			<AccountsDataSet apiURL="fake-url" channelId="123" groupId="23" />
		);

		expect(mountCount).toBe(1);

		rerender(
			<AccountsDataSet
				apiURL="fake-url"
				channelId="123"
				groupId="23"
				segmentFilter="segment-100"
			/>
		);

		expect(mountCount).toBe(2);
	});

	it('should remount the FrontendDataSet when stageSelectionNonce changes even if lifecycleStageFilter is unchanged', () => {
		const {rerender} = render(
			<AccountsDataSet
				accountLifecycleId="al-1"
				apiURL="fake-url"
				channelId="123"
				groupId="23"
				lifecycleStageFilter={LifecycleStages.AWARE}
				stageSelectionNonce={0}
			/>
		);

		expect(mountCount).toBe(1);

		rerender(
			<AccountsDataSet
				accountLifecycleId="al-1"
				apiURL="fake-url"
				channelId="123"
				groupId="23"
				lifecycleStageFilter={LifecycleStages.AWARE}
				stageSelectionNonce={1}
			/>
		);

		expect(mountCount).toBe(2);
	});
});
