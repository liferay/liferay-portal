import AccountsDataSet from '../AccountsDataSet';
import React from 'react';
import {cleanup, render, screen, waitFor} from '@testing-library/react';
import {CatalogFieldDataCategory, ICatalogField} from 'shared/api/catalog';
import {IViewField} from '../utils';
import {LifecycleStages} from 'contacts/pages/account/utils/constants';
import {RangeKeyTimeRanges} from 'shared/util/constants';
import {useRequest} from 'shared/hooks/useRequest';
import {warmFrontendDataSet} from 'test/warm-frontend-data-set';

interface IStageItem {
	externalReferenceCode: string;
	id: string;
	stageName?: string;
	stageType?: LifecycleStages;
}

const DEFAULT_STAGE_ITEMS: IStageItem[] = [
	{
		externalReferenceCode: 'erc-stage-9990',
		id: '9990',
		stageType: LifecycleStages.AWARE,
	},
	{
		externalReferenceCode: 'erc-stage-9991',
		id: '9991',
		stageType: LifecycleStages.ENGAGED,
	},
	{
		externalReferenceCode: 'erc-stage-9992',
		id: '9992',
		stageType: LifecycleStages.PIPELINE,
	},
	{
		externalReferenceCode: 'erc-stage-9993',
		id: '9993',
		stageType: LifecycleStages.AT_RISK,
	},
];

const buildCatalogField = (
	name: string,
	dataCategory: CatalogFieldDataCategory,
	displayName: string | null = null
): ICatalogField => ({
	dataCategory,
	dataType: dataCategory,
	description: null,
	displayName,
	id: name,
	name,
	parentField: null,
	tableName: 'account',
});

const DEFAULT_VIEW_FIELD_NAMES = [
	'accountName',
	'industry',
	'lifecycleStage',
	'annualRevenue',
	'country',
	'firstActive',
	'lastActive',
	'activitiesCount',
];

const ALL_ATTRIBUTES_FIXED_FIELD_NAMES = [
	...DEFAULT_VIEW_FIELD_NAMES,
	'lastEnriched',
];

const DEFAULT_VIEW_FIELD_CATALOG: ICatalogField[] = [
	buildCatalogField('accountName', 'Text'),
	buildCatalogField('industry', 'Text'),
	buildCatalogField('lifecycleStage', 'Text'),
	buildCatalogField('annualRevenue', 'Number'),
	buildCatalogField('country', 'Text'),
	buildCatalogField('lastActive', 'Date'),
	buildCatalogField('lastEnriched', 'Date'),
	buildCatalogField('firstActive', 'Date'),
];

const CALCULATED_CATALOG_FIELD: ICatalogField = {
	dataCategory: 'Number',
	dataType: 'INT64',
	description: null,
	displayName: 'Closed-Won Opportunities',
	id: '15',
	name: 'salesforce/closedWonOpportunityCount',
	parentField: 'calculatedFields',
	tableName: 'account',
};

const ALL_ATTRIBUTES_FIELD_CATALOG: ICatalogField[] = [
	buildCatalogField('accountName', 'Boolean'),
	buildCatalogField('description', 'Text', 'Description'),
	buildCatalogField('employeeCount', 'Number'),
	buildCatalogField('signupDate', 'Date'),
	buildCatalogField('isPartner', 'Boolean'),
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

const mockStages = (items: IStageItem[] | undefined) => {
	mockedUseRequest.mockReturnValue({
		data: items ? {items} : undefined,
		loading: false,
	});
};

const mockStagesLoading = () => {
	mockedUseRequest.mockReturnValue({data: undefined, loading: true});
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
	accountLifecycleStageRenderer: (props: {
		value?: LifecycleStages | '';
	}) => React.ReactElement;
	activitiesCountRenderer: (props: {value?: number}) => React.ReactElement;
	annualRevenueRenderer: (props: {value?: number}) => React.ReactElement;
	textRenderer: (props: {
		value?: boolean | number | string;
	}) => React.ReactElement;
};

type FakeView = {
	default?: boolean;
	label: string;
	name: string;
	schema: {fields: IViewField[]};
};

let lastApiURL: string | undefined;
let lastCustomDataRenderers: FakeCustomDataRenderers | undefined;
let lastFilters: FakeFilter[] | undefined;
let lastViews: FakeView[] | undefined;
let mountCount = 0;

const allAttributesFields = () =>
	lastViews!.find((view) => !view.default)!.schema.fields;

const byFieldName = (name: string) =>
	allAttributesFields().find((field) => field.fieldName === name);

jest.mock('@liferay/frontend-data-set-web', () => ({
	...jest.requireActual('@liferay/frontend-data-set-web'),
	FrontendDataSet: ({
		apiURL,
		customDataRenderers,
		filters,
		id,
		views,
	}: {
		apiURL: string;
		customDataRenderers: FakeCustomDataRenderers;
		filters: FakeFilter[];
		id: string;
		views: FakeView[];
	}) => {
		lastApiURL = apiURL;
		lastCustomDataRenderers = customDataRenderers;
		lastFilters = filters;
		lastViews = views;

		React.useEffect(() => {
			mountCount += 1;
		}, []);

		return <div data-testid="fds-component" id={id} />;
	},
}));

const renderAccountsDataSet = async (ui: React.ReactElement) => {
	const result = render(ui);

	await screen.findByTestId('fds-component');

	return result;
};

beforeAll(warmFrontendDataSet);

describe('AccountsDataSet', () => {
	beforeEach(() => {
		jest.clearAllMocks();
		mockStages(DEFAULT_STAGE_ITEMS);
		lastApiURL = undefined;
		lastCustomDataRenderers = undefined;
		lastFilters = undefined;
		lastViews = undefined;
		mountCount = 0;
	});

	afterEach(cleanup);

	it('should render the FrontendDataSet with id "accounts-list-dataset"', async () => {
		await renderAccountsDataSet(
			<AccountsDataSet apiURL="fake-url" channelId="123" groupId="23" />
		);

		expect(screen.getByTestId('fds-component')).toHaveAttribute(
			'id',
			'accounts-list-dataset'
		);
	});

	it('should pass the apiURL directly to FrontendDataSet without appending range params', async () => {
		await renderAccountsDataSet(
			<AccountsDataSet apiURL="fake-url" channelId="123" groupId="23" />
		);

		expect(lastApiURL).toBe('fake-url');
	});

	it('should leave the rangeKey filter without preloadedData when rangeKeyFilter is not provided', async () => {
		await renderAccountsDataSet(
			<AccountsDataSet apiURL="fake-url" channelId="123" groupId="23" />
		);

		const rangeKeyFilter = lastFilters?.find((f) => f.id === 'rangeKey');

		expect(rangeKeyFilter?.preloadedData).toBeUndefined();
	});

	it('should preload the rangeKey filter when rangeKeyFilter prop is provided', async () => {
		await renderAccountsDataSet(
			<AccountsDataSet
				apiURL="fake-url"
				channelId="123"
				groupId="23"
				rangeKeyFilter={RangeKeyTimeRanges.Last30Days}
			/>
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

	it('should include all 8 time range options in the rangeKey filter', async () => {
		await renderAccountsDataSet(
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

	it('should leave country and industry filters without preloadedData when no props are passed', async () => {
		await renderAccountsDataSet(
			<AccountsDataSet apiURL="fake-url" channelId="123" groupId="23" />
		);

		const countryFilter = lastFilters?.find((f) => f.id === 'country');
		const industryFilter = lastFilters?.find((f) => f.id === 'industry');

		expect(countryFilter?.preloadedData).toBeUndefined();
		expect(industryFilter?.preloadedData).toBeUndefined();
	});

	it('should omit the lifecycleStatus filter when accountLifecycleId is not provided', async () => {
		await renderAccountsDataSet(
			<AccountsDataSet apiURL="fake-url" channelId="123" groupId="23" />
		);

		const lifecycleStatusFilter = lastFilters?.find(
			(f) => f.id === 'lifecycleStatus'
		);

		expect(lifecycleStatusFilter).toBeUndefined();
	});

	it('should preload the country filter when countryFilter prop is provided', async () => {
		await renderAccountsDataSet(
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

	it('should preload the industry filter when industryFilter prop is provided', async () => {
		await renderAccountsDataSet(
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

	it('should build the lifecycleStatus items from the fetched stages with localized labels', async () => {
		await renderAccountsDataSet(
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
			{label: 'Aware', value: 'erc-stage-9990'},
			{label: 'Engaged', value: 'erc-stage-9991'},
			{label: 'Pipeline', value: 'erc-stage-9992'},
			{label: 'At Risk', value: 'erc-stage-9993'},
		]);
	});

	it('should preload the lifecycleStatus filter with the external reference code and label of the stage whose id is in lifecycleStageFilter', async () => {
		await renderAccountsDataSet(
			<AccountsDataSet
				accountLifecycleId="al-1"
				apiURL="fake-url"
				channelId="123"
				groupId="23"
				lifecycleStageFilter="9993"
			/>
		);

		const lifecycleStatusFilter = lastFilters?.find(
			(f) => f.id === 'lifecycleStatus'
		);

		expect(lifecycleStatusFilter?.preloadedData).toEqual({
			exclude: false,
			selectedItems: [{label: 'At Risk', value: 'erc-stage-9993'}],
		});
	});

	it('should leave the lifecycleStatus preloadedData undefined when stages have not loaded yet', async () => {
		mockStages(undefined);

		await renderAccountsDataSet(
			<AccountsDataSet
				accountLifecycleId="al-1"
				apiURL="fake-url"
				channelId="123"
				groupId="23"
				lifecycleStageFilter="9993"
			/>
		);

		const lifecycleStatusFilter = lastFilters?.find(
			(f) => f.id === 'lifecycleStatus'
		);

		expect(lifecycleStatusFilter?.preloadedData).toBeUndefined();
	});

	it('should render the account name link with channelId in the href', async () => {
		await renderAccountsDataSet(
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

	it('should render the no data value when the account has no recent activities count', async () => {
		await renderAccountsDataSet(
			<AccountsDataSet apiURL="fake-url" channelId="123" groupId="23" />
		);

		const {container} = render(
			lastCustomDataRenderers!.activitiesCountRenderer({
				value: undefined,
			})
		);

		expect(container).toHaveTextContent('–');
	});

	it('should render the recent activities count when the account has events', async () => {
		await renderAccountsDataSet(
			<AccountsDataSet apiURL="fake-url" channelId="123" groupId="23" />
		);

		const {container} = render(
			lastCustomDataRenderers!.activitiesCountRenderer({value: 42})
		);

		expect(container).toHaveTextContent('42');
	});

	it('should render the lifecycle stage label when the account has a lifecycle stage', async () => {
		await renderAccountsDataSet(
			<AccountsDataSet apiURL="fake-url" channelId="123" groupId="23" />
		);

		const {container} = render(
			lastCustomDataRenderers!.accountLifecycleStageRenderer({
				value: LifecycleStages.ENGAGED,
			})
		);

		expect(container).toHaveTextContent('Engaged');
	});

	it.each([
		['missing', undefined],
		['empty', ''],
	] as const)(
		'should render the no data value when the account lifecycle stage is %s',
		async (_, value) => {
			await renderAccountsDataSet(
				<AccountsDataSet
					apiURL="fake-url"
					channelId="123"
					groupId="23"
				/>
			);

			render(
				lastCustomDataRenderers!.accountLifecycleStageRenderer({value})
			);

			expect(screen.getByText('–')).toHaveClass('text-secondary');
		}
	);

	it.each([
		['missing', undefined],
		['empty', ''],
	] as const)(
		'should render the no data value when an account attribute is %s',
		async (_, value) => {
			await renderAccountsDataSet(
				<AccountsDataSet
					apiURL="fake-url"
					channelId="123"
					groupId="23"
				/>
			);

			render(lastCustomDataRenderers!.textRenderer({value}));

			expect(screen.getByText('–')).toHaveClass('text-secondary');
		}
	);

	it('should render a zero account attribute as zero', async () => {
		await renderAccountsDataSet(
			<AccountsDataSet apiURL="fake-url" channelId="123" groupId="23" />
		);

		const {container} = render(
			lastCustomDataRenderers!.textRenderer({value: '0'})
		);

		expect(container.textContent).toBe('0');
	});

	it.each([
		['true', true],
		['false', false],
	] as const)(
		'should render a boolean account attribute as %s',
		async (expected, value) => {
			await renderAccountsDataSet(
				<AccountsDataSet
					apiURL="fake-url"
					channelId="123"
					groupId="23"
				/>
			);

			const {container} = render(
				lastCustomDataRenderers!.textRenderer({value})
			);

			expect(container.textContent).toBe(expected);
		}
	);

	it('should render the account attribute value when the account has one', async () => {
		await renderAccountsDataSet(
			<AccountsDataSet apiURL="fake-url" channelId="123" groupId="23" />
		);

		const {container} = render(
			lastCustomDataRenderers!.textRenderer({value: 'Software'})
		);

		expect(container.textContent).toBe('Software');
	});

	it('should render a zero annual revenue', async () => {
		await renderAccountsDataSet(
			<AccountsDataSet apiURL="fake-url" channelId="123" groupId="23" />
		);

		const {container} = render(
			lastCustomDataRenderers!.annualRevenueRenderer({value: 0})
		);

		expect(container.textContent).toBe('0');
	});

	it('should render the no data value when the account annual revenue is unknown', async () => {
		await renderAccountsDataSet(
			<AccountsDataSet apiURL="fake-url" channelId="123" groupId="23" />
		);

		const {getByText} = render(
			lastCustomDataRenderers!.annualRevenueRenderer({value: undefined})
		);

		expect(getByText('–')).toHaveClass('text-secondary');
	});

	it('should render the annual revenue in thousands when the account has annual revenue', async () => {
		await renderAccountsDataSet(
			<AccountsDataSet apiURL="fake-url" channelId="123" groupId="23" />
		);

		const {container} = render(
			lastCustomDataRenderers!.annualRevenueRenderer({value: 120000000})
		);

		expect(container).toHaveTextContent('120M');
	});

	it('should not expose a filter for recent activities', async () => {
		await renderAccountsDataSet(
			<AccountsDataSet apiURL="fake-url" channelId="123" groupId="23" />
		);

		const activitiesCountFilter = lastFilters?.find(
			(f) => f.id === 'activitiesCount'
		);

		expect(activitiesCountFilter).toBeUndefined();
	});

	it('should not append segmentFilter as a query param on the apiURL', async () => {
		await renderAccountsDataSet(
			<AccountsDataSet
				apiURL="fake-url"
				channelId="123"
				groupId="23"
				segmentFilter="segment-100"
			/>
		);

		expect(lastApiURL).toBe('fake-url');
	});

	it('should leave the segment filter without preloadedData when no segmentFilter prop is passed', async () => {
		await renderAccountsDataSet(
			<AccountsDataSet apiURL="fake-url" channelId="123" groupId="23" />
		);

		const segmentFilter = lastFilters?.find((f) => f.id === 'segmentId');

		expect(segmentFilter?.preloadedData).toBeUndefined();
	});

	it('should preload the segment filter with the segment name when segmentFilter and segmentName props are provided', async () => {
		await renderAccountsDataSet(
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
			selectedItems: [{label: 'VIP Customers', value: 'segment-100'}],
		});
	});

	it('should point the segment filter apiURL at the individual segment search endpoint', async () => {
		await renderAccountsDataSet(
			<AccountsDataSet apiURL="fake-url" channelId="123" groupId="23" />
		);

		const segmentFilter = lastFilters?.find((f) => f.id === 'segmentId');

		expect(segmentFilter?.apiURL).toBe(
			'/o/faro/contacts/23/individual_segment/search?channelId=123'
		);
	});

	it('should remount the FrontendDataSet when segmentFilter changes', async () => {
		const {rerender} = await renderAccountsDataSet(
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

		await waitFor(() => expect(mountCount).toBe(2));
	});

	it('should remount the FrontendDataSet when stageSelectionNonce changes even if lifecycleStageFilter is unchanged', async () => {
		const {rerender} = await renderAccountsDataSet(
			<AccountsDataSet
				accountLifecycleId="al-1"
				apiURL="fake-url"
				channelId="123"
				groupId="23"
				lifecycleStageFilter="9990"
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
				lifecycleStageFilter="9990"
				stageSelectionNonce={1}
			/>
		);

		await waitFor(() => expect(mountCount).toBe(2));
	});

	it('should mount the FrontendDataSet only once, once the lifecycle stages have arrived', async () => {
		mockStagesLoading();

		const {rerender} = render(
			<AccountsDataSet
				accountLifecycleId="al-1"
				apiURL="fake-url"
				channelId="123"
				groupId="23"
			/>
		);

		expect(screen.queryByTestId('fds-component')).toBeNull();
		expect(mountCount).toBe(0);

		mockStages(DEFAULT_STAGE_ITEMS);

		rerender(
			<AccountsDataSet
				accountLifecycleId="al-1"
				apiURL="fake-url"
				channelId="123"
				groupId="23"
			/>
		);

		expect(await screen.findByTestId('fds-component')).toBeInTheDocument();
		expect(mountCount).toBe(1);
	});

	it('should mount the FrontendDataSet right away when accountLifecycleId is not provided, since the stages request is skipped', async () => {
		mockStagesLoading();

		await renderAccountsDataSet(
			<AccountsDataSet apiURL="fake-url" channelId="123" groupId="23" />
		);

		expect(screen.getByTestId('fds-component')).toBeInTheDocument();
		expect(mountCount).toBe(1);
	});

	describe('when fieldCatalog is not provided', () => {
		it('should build only the Default View', async () => {
			await renderAccountsDataSet(
				<AccountsDataSet
					apiURL="fake-url"
					channelId="123"
					groupId="23"
				/>
			);

			expect(lastViews).toHaveLength(1);

			const [view] = lastViews!;

			expect(view.name).toBe('table');
			expect(view.default).toBe(true);
			expect(view.label).toBe('Default View');

			expect(view.schema.fields.map((f) => f.fieldName)).toEqual(
				DEFAULT_VIEW_FIELD_NAMES
			);
		});
	});

	describe('when fieldCatalog is provided', () => {
		it('should build two views', async () => {
			await renderAccountsDataSet(
				<AccountsDataSet
					apiURL="fake-url"
					channelId="123"
					fieldCatalog={ALL_ATTRIBUTES_FIELD_CATALOG}
					groupId="23"
				/>
			);

			expect(lastViews).toHaveLength(2);
		});

		it('should build only the Default View when every catalog field is already curated', async () => {
			await renderAccountsDataSet(
				<AccountsDataSet
					apiURL="fake-url"
					channelId="123"
					fieldCatalog={DEFAULT_VIEW_FIELD_CATALOG}
					groupId="23"
				/>
			);

			expect(lastViews).toHaveLength(1);
			expect(lastViews![0].name).toBe('table');
		});

		it('should resolve the date renderer whatever case the catalog reports', async () => {
			await renderAccountsDataSet(
				<AccountsDataSet
					apiURL="fake-url"
					channelId="123"
					fieldCatalog={[
						buildCatalogField(
							'shoutedDate',
							'DATE' as CatalogFieldDataCategory
						),
						buildCatalogField(
							'whisperedDate',
							'date' as CatalogFieldDataCategory
						),
					]}
					groupId="23"
				/>
			);

			expect(byFieldName('shoutedDate')?.contentRenderer).toBe(
				'dateRenderer'
			);
			expect(byFieldName('whisperedDate')?.contentRenderer).toBe(
				'dateRenderer'
			);
		});

		it('should carry a calculated catalog field through verbatim', async () => {
			await renderAccountsDataSet(
				<AccountsDataSet
					apiURL="fake-url"
					channelId="123"
					fieldCatalog={[CALCULATED_CATALOG_FIELD]}
					groupId="23"
				/>
			);

			const field = byFieldName('salesforce/closedWonOpportunityCount');

			expect(field?.label).toBe('Closed-Won Opportunities');
			expect(field?.sortable).toBe(false);
			expect(field?.contentRenderer).toBe('textRenderer');
		});

		it('should build the Default View with the 8 fixed fields in order, with the correct renderers', async () => {
			await renderAccountsDataSet(
				<AccountsDataSet
					apiURL="fake-url"
					channelId="123"
					fieldCatalog={DEFAULT_VIEW_FIELD_CATALOG}
					groupId="23"
				/>
			);

			const defaultView = lastViews!.find((v) => v.default);

			expect(defaultView).toBeDefined();
			expect(defaultView!.label).toBe('Default View');
			expect(defaultView!.schema.fields.map((f) => f.fieldName)).toEqual(
				DEFAULT_VIEW_FIELD_NAMES
			);

			expect(
				defaultView!.schema.fields.map((f) => f.contentRenderer)
			).toEqual([
				'accountNameRenderer',
				'textRenderer',
				'accountLifecycleStageRenderer',
				'annualRevenueRenderer',
				'textRenderer',
				'dateRenderer',
				'dateRenderer',
				'activitiesCountRenderer',
			]);

			const activitiesCountField = defaultView!.schema.fields.find(
				(f) => f.fieldName === 'activitiesCount'
			);
			const firstActiveField = defaultView!.schema.fields.find(
				(f) => f.fieldName === 'firstActive'
			);

			expect(activitiesCountField?.label).toBe('Recent Activities');
			expect(firstActiveField?.label).toBe('First Active');
		});

		it('should build the All Attributes View from the full catalog, resolving renderers by type and letting known overrides win', async () => {
			await renderAccountsDataSet(
				<AccountsDataSet
					apiURL="fake-url"
					channelId="123"
					fieldCatalog={ALL_ATTRIBUTES_FIELD_CATALOG}
					groupId="23"
				/>
			);

			const allAttributesView = lastViews!.find((v) => !v.default);

			expect(allAttributesView).toBeDefined();
			expect(allAttributesView!.label).toBe('All Attributes');
			expect(allAttributesView!.schema.fields).toHaveLength(
				ALL_ATTRIBUTES_FIXED_FIELD_NAMES.length +
					ALL_ATTRIBUTES_FIELD_CATALOG.filter(
						({name}) =>
							!ALL_ATTRIBUTES_FIXED_FIELD_NAMES.includes(name)
					).length
			);

			expect(byFieldName('accountName')?.contentRenderer).toBe(
				'accountNameRenderer'
			);
			expect(byFieldName('description')?.contentRenderer).toBe(
				'textRenderer'
			);
			expect(byFieldName('employeeCount')?.contentRenderer).toBe(
				'textRenderer'
			);
			expect(byFieldName('signupDate')?.contentRenderer).toBe(
				'dateRenderer'
			);
			expect(byFieldName('isPartner')?.contentRenderer).toBe(
				'textRenderer'
			);
		});

		it('should order the All Attributes View with the default columns first, then the remaining catalog fields in catalog order', async () => {
			await renderAccountsDataSet(
				<AccountsDataSet
					apiURL="fake-url"
					channelId="123"
					fieldCatalog={[
						buildCatalogField('website', 'Text'),
						buildCatalogField('lastActive', 'Date'),
						buildCatalogField('city', 'Text'),
						buildCatalogField('accountName', 'Text'),
						buildCatalogField('industry', 'Text'),
					]}
					groupId="23"
				/>
			);

			const allAttributesView = lastViews!.find((v) => !v.default);

			expect(
				allAttributesView!.schema.fields.map((f) => f.fieldName)
			).toEqual([...ALL_ATTRIBUTES_FIXED_FIELD_NAMES, 'website', 'city']);
		});

		it('should build only the Default View when the catalog comes back empty', async () => {
			await renderAccountsDataSet(
				<AccountsDataSet
					apiURL="fake-url"
					channelId="123"
					fieldCatalog={[]}
					groupId="23"
				/>
			);

			expect(lastViews).toHaveLength(1);
			expect(lastViews![0].name).toBe('table');
		});

		it('should not repeat a catalog field that appears twice', async () => {
			await renderAccountsDataSet(
				<AccountsDataSet
					apiURL="fake-url"
					channelId="123"
					fieldCatalog={[
						buildCatalogField('website', 'Text'),
						buildCatalogField('website', 'Text'),
					]}
					groupId="23"
				/>
			);

			expect(
				allAttributesFields().filter(
					(field) => field.fieldName === 'website'
				)
			).toHaveLength(1);
		});

		it('should leave catalog columns unsortable, since the engine does not know their names', async () => {
			await renderAccountsDataSet(
				<AccountsDataSet
					apiURL="fake-url"
					channelId="123"
					fieldCatalog={ALL_ATTRIBUTES_FIELD_CATALOG}
					groupId="23"
				/>
			);

			expect(byFieldName('description')?.sortable).toBe(false);
			expect(byFieldName('accountName')?.sortable).toBe(true);
		});

		it('should label catalog fields with displayName, falling back to the field name when the catalog omits it', async () => {
			await renderAccountsDataSet(
				<AccountsDataSet
					apiURL="fake-url"
					channelId="123"
					fieldCatalog={ALL_ATTRIBUTES_FIELD_CATALOG}
					groupId="23"
				/>
			);

			expect(byFieldName('description')?.label).toBe('Description');
			expect(byFieldName('signupDate')?.label).toBe('signupDate');
		});
	});

	describe('the Last Enriched column', () => {
		it('should be left out of the Default View', async () => {
			await renderAccountsDataSet(
				<AccountsDataSet
					apiURL="fake-url"
					channelId="123"
					fieldCatalog={ALL_ATTRIBUTES_FIELD_CATALOG}
					groupId="23"
				/>
			);

			const defaultView = lastViews!.find((v) => v.default);

			expect(
				defaultView!.schema.fields.map((f) => f.fieldName)
			).not.toContain('lastEnriched');
		});

		it('should sit at the end of the fixed columns in the All Attributes View, still curated', async () => {
			await renderAccountsDataSet(
				<AccountsDataSet
					apiURL="fake-url"
					channelId="123"
					fieldCatalog={ALL_ATTRIBUTES_FIELD_CATALOG}
					groupId="23"
				/>
			);

			expect(
				allAttributesFields().findIndex(
					(field) => field.fieldName === 'lastEnriched'
				)
			).toBe(DEFAULT_VIEW_FIELD_NAMES.length);

			const field = byFieldName('lastEnriched');

			expect(field?.contentRenderer).toBe('dateRenderer');
			expect(field?.sortable).toBe(true);
			expect(field?.label).toMatch(/last.enriched/i);
		});

		it('should not be duplicated when the catalog also reports it', async () => {
			await renderAccountsDataSet(
				<AccountsDataSet
					apiURL="fake-url"
					channelId="123"
					fieldCatalog={[
						buildCatalogField('lastEnriched', 'Date'),
						buildCatalogField('website', 'Text'),
					]}
					groupId="23"
				/>
			);

			expect(
				allAttributesFields().filter(
					(field) => field.fieldName === 'lastEnriched'
				)
			).toHaveLength(1);

			expect(byFieldName('lastEnriched')?.sortable).toBe(true);
		});
	});
});
