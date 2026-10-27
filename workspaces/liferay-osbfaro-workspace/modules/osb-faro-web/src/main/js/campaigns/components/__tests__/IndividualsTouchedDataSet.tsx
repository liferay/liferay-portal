import IndividualsTouchedDataSet from '../IndividualsTouchedDataSet';
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

const renderDataSet = (initialEntry = '/') =>
	render(
		<MemoryRouter initialEntries={[initialEntry]}>
			<IndividualsTouchedDataSet
				campaignId="7"
				channelId="123"
				groupId="23"
			/>
		</MemoryRouter>
	);

beforeAll(warmFrontendDataSet);

describe('IndividualsTouchedDataSet', () => {
	beforeEach(() => {
		lastFDSProps = undefined;
	});

	afterEach(cleanup);

	it('should render the FrontendDataSet with id "campaign-individuals-dataset"', () => {
		renderDataSet();

		expect(screen.getByTestId('fds-component')).toHaveAttribute(
			'id',
			'campaign-individuals-dataset'
		);
	});

	it('should point the data set at the campaign individuals endpoint', () => {
		renderDataSet();

		expect(lastFDSProps.apiURL).toBe(
			'/o/faro/contacts/23/campaigns/7/individuals?channelId=123&rangeKey=30'
		);
	});

	it('should ask the endpoint for a custom range by its bounds alone', () => {
		renderDataSet(
			'/?rangeEnd=2026-10-20&rangeKey=CUSTOM&rangeStart=2026-10-01'
		);

		expect(lastFDSProps.apiURL).toBe(
			'/o/faro/contacts/23/campaigns/7/individuals?channelId=123&rangeEnd=2026-10-20&rangeStart=2026-10-01'
		);
	});

	// The endpoint serves `IndividualDisplay`, which has no visitor type
	// field, so the Visitor Type column is derived from `sessionsCount`.

	it('should render the table columns', () => {
		renderDataSet();

		expect(
			getFields().map(({fieldName, label, sortable}: any) => ({
				fieldName,
				label,
				sortable,
			}))
		).toEqual([
			{fieldName: 'name', label: 'Individual Name', sortable: true},
			{fieldName: 'accountName', label: 'Account Name', sortable: true},
			{fieldName: 'sessionsCount', label: 'Visitor Type', sortable: true},
			{
				fieldName: 'lastActivityDate',
				label: 'Last Active',
				sortable: true,
			},
		]);
	});

	it('should sort by last activity descending by default', () => {
		renderDataSet();

		expect(lastFDSProps.sorts).toEqual([
			{
				active: true,
				default: true,
				direction: 'desc',
				key: 'lastActivityDate',
				label: 'Last Active',
			},
		]);
	});

	it('should paginate', () => {
		renderDataSet();

		expect(lastFDSProps.showPagination).toBe(true);
		expect(lastFDSProps.pagination).toBeDefined();
	});

	it('should offer a search box', () => {
		renderDataSet();

		expect(lastFDSProps.showSearch).not.toBe(false);
	});

	it('should link the individual name at their profile', () => {
		renderDataSet();

		const {container} = render(
			lastFDSProps.customDataRenderers.individualNameRenderer({
				itemData: {id: '101'},
				value: 'Jane Doe',
			})
		);

		expect(container).toHaveTextContent('Jane Doe');
		expect(container.querySelector('a')).toHaveAttribute(
			'href',
			'/workspace/23/123/contacts/individuals/known-individuals/101?rangeKey=30'
		);
	});

	it('should label a single session First Time and more Returning', () => {
		renderDataSet();

		const visitorType = (value?: number) =>
			render(
				<>
					{lastFDSProps.customDataRenderers.visitorTypeRenderer({
						value,
					})}
				</>
			).container.textContent;

		expect(visitorType(1)).toBe('First Time');
		expect(visitorType(4)).toBe('Returning');
		expect(visitorType(undefined)).toBe('');
	});

	it('should tell the marketer that no individuals were touched', () => {
		renderDataSet();

		expect(lastFDSProps.emptyState).toEqual(
			expect.objectContaining({
				description:
					'Check back later to verify if data has been received from your data sources.',
				title: 'No individuals were found.',
			})
		);
	});
});
