import AccountsTouchedDataSet from '../AccountsTouchedDataSet';
import React from 'react';
import {cleanup, render, screen} from '@testing-library/react';
import {MemoryRouter} from 'react-router-dom';
import {warmFrontendDataSet} from 'test/warm-frontend-data-set';

jest.unmock('react-dom');

let lastFDSProps: any;

jest.mock('@liferay/frontend-data-set-web', () => ({
	...jest.requireActual('@liferay/frontend-data-set-web'),
	FrontendDataSet: (props: any) => {
		lastFDSProps = props;

		return <div data-testid="fds-component" id={props.id} />;
	},
}));

const getFields = () => lastFDSProps.views[0].schema.fields;

const renderDataSet = (initialEntry = '/', props = {}) =>
	render(
		<MemoryRouter initialEntries={[initialEntry]}>
			<AccountsTouchedDataSet
				campaignId="7"
				channelId="123"
				groupId="23"
				lifecycles={[{externalReferenceCode: 'lifecycle-55', id: '55'}]}
				loading={false}
				{...props}
			/>
		</MemoryRouter>
	);

beforeAll(warmFrontendDataSet);

describe('AccountsTouchedDataSet', () => {
	beforeEach(() => {
		lastFDSProps = undefined;
	});

	afterEach(cleanup);

	it('should render the FrontendDataSet with id "campaign-accounts-dataset"', () => {
		renderDataSet();

		expect(screen.getByTestId('fds-component')).toHaveAttribute(
			'id',
			'campaign-accounts-dataset'
		);
	});

	it('should point the data set at the campaign accounts endpoint', () => {
		renderDataSet();

		expect(lastFDSProps.apiURL).toBe(
			'/o/faro/contacts/23/campaigns/7/accounts?accountLifecycleId=55&channelId=123&rangeKey=30'
		);
	});

	it('should leave out the lifecycle when there is none', () => {
		renderDataSet('/', {lifecycles: []});

		expect(lastFDSProps.apiURL).toBe(
			'/o/faro/contacts/23/campaigns/7/accounts?channelId=123&rangeKey=30'
		);
	});

	it('should hold the table back while the lifecycle loads', () => {
		renderDataSet('/', {lifecycles: undefined, loading: true});

		expect(screen.queryByTestId('fds-component')).not.toBeInTheDocument();
		expect(lastFDSProps).toBeUndefined();
	});

	it('should ask the endpoint for a custom range by its bounds alone', () => {
		renderDataSet(
			'/?rangeEnd=2026-10-20&rangeKey=CUSTOM&rangeStart=2026-10-01'
		);

		expect(lastFDSProps.apiURL).toBe(
			'/o/faro/contacts/23/campaigns/7/accounts?accountLifecycleId=55&channelId=123&rangeEnd=2026-10-20&rangeStart=2026-10-01'
		);
	});

	it('should leave the fetching to the data set rather than pass items', () => {
		renderDataSet();

		expect(lastFDSProps.items).toBeUndefined();
	});

	// The endpoint serves `AccountDisplay`, which flattens the amounts out
	// of `calculatedFields` into top level keys of the form
	// `<namespace>/<name>`.

	it('should render the table columns', () => {
		renderDataSet();

		expect(
			getFields().map(({fieldName, label, sortable}: any) => ({
				fieldName,
				label,
				sortable,
			}))
		).toEqual([
			{fieldName: 'accountName', label: 'Account Name', sortable: true},
			{
				fieldName: 'lifecycleStage',
				label: 'Lifecycle Stage',
				sortable: true,
			},
			{
				fieldName: 'salesforce/openPipelineAmount',
				label: 'Pipeline Value',
				sortable: true,
			},
			{
				fieldName: 'salesforce/closedWonAmount',
				label: 'Closed Won',
				sortable: true,
			},
		]);
	});

	it('should sort by pipeline value descending by default', () => {
		renderDataSet();

		expect(lastFDSProps.sorts).toEqual([
			{
				active: true,
				default: true,
				direction: 'desc',
				key: 'salesforce/openPipelineAmount',
				label: 'Pipeline Value',
			},
		]);
	});

	it('should paginate', () => {
		renderDataSet();

		expect(lastFDSProps.showPagination).toBe(true);
	});

	it('should offer a search box and the bar holding it', () => {
		renderDataSet();

		expect(lastFDSProps.showSearch).not.toBe(false);
		expect(lastFDSProps.showManagementBar).not.toBe(false);
	});

	it('should link the account name at its Overview tab', () => {
		renderDataSet();

		const {container} = render(
			lastFDSProps.customDataRenderers.accountNameRenderer({
				itemData: {id: '101'},
				value: 'Hydrofield Industries',
			})
		);

		expect(container.querySelector('a')).toHaveAttribute(
			'href',
			'/workspace/23/123/contacts/accounts/101/overview'
		);
	});

	it('should render the lifecycle stage as a colored label', () => {
		renderDataSet();

		const {container} = render(
			lastFDSProps.customDataRenderers.lifecycleStageRenderer({
				value: 'AT_RISK',
			})
		);

		expect(container).toHaveTextContent('At Risk');
		expect(container.querySelector('.label-inverse-danger')).toBeTruthy();
	});

	it.each([
		['missing', undefined],
		['empty', ''],
	])(
		'should render the no data value when the lifecycle stage is %s',
		(_, value) => {
			renderDataSet();

			render(
				lastFDSProps.customDataRenderers.lifecycleStageRenderer({value})
			);

			expect(screen.getByText('–')).toHaveClass('text-secondary');
		}
	);

	it('should abbreviate the amounts', () => {
		renderDataSet();

		const amount = (value: number | string) =>
			render(lastFDSProps.customDataRenderers.amountRenderer({value}))
				.container.textContent;

		// The endpoint sends the amounts as strings, and `toThousands`
		// returns '' for a string, so the renderer has to convert them.

		expect(amount('18500000')).toBe('18.5M');
		expect(amount('950000')).toBe('950K');
		expect(amount('0')).toBe('0');
		expect(amount(18500000)).toBe('18.5M');
	});

	it('should leave a missing amount blank', () => {
		renderDataSet();

		const amount = (value?: string | null) =>
			render(lastFDSProps.customDataRenderers.amountRenderer({value}))
				.container.textContent;

		expect(amount(undefined)).toBe('');
		expect(amount(null)).toBe('');
		expect(amount('')).toBe('');
	});

	it('should use the default empty state of the data set', () => {
		renderDataSet();

		expect(lastFDSProps.emptyState).toBeUndefined();
	});
});
