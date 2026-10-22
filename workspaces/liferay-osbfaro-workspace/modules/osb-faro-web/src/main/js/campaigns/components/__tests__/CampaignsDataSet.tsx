import CampaignsDataSet from '../CampaignsDataSet';
import React from 'react';
import {cleanup, render, screen} from '@testing-library/react';
import {
	EMPTY_STATE_IMAGE,
	EMPTY_STATE_REDUCED_MOTION_IMAGE,
} from 'shared/components/FrontendDataSet';
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
			<CampaignsDataSet channelId="123" groupId="23" />
		</MemoryRouter>
	);

beforeAll(warmFrontendDataSet);

describe('CampaignsDataSet', () => {
	beforeEach(() => {
		lastFDSProps = undefined;
	});

	afterEach(cleanup);

	it('should render the FrontendDataSet with id "campaigns-list-dataset"', () => {
		renderDataSet();

		expect(screen.getByTestId('fds-component')).toHaveAttribute(
			'id',
			'campaigns-list-dataset'
		);
	});

	it('should point the data set at the campaigns endpoint', () => {
		renderDataSet();

		expect(lastFDSProps.apiURL).toBe(
			'/o/faro/contacts/23/campaigns?channelId=123&rangeKey=30'
		);
	});

	it('should ask the endpoint for the range in the URL', () => {
		renderDataSet('/?rangeKey=7');

		expect(lastFDSProps.apiURL).toBe(
			'/o/faro/contacts/23/campaigns?channelId=123&rangeKey=7'
		);
	});

	it('should ask the endpoint for a custom range by its bounds alone', () => {
		renderDataSet(
			'/?rangeEnd=2026-10-20&rangeKey=CUSTOM&rangeStart=2026-10-01'
		);

		expect(lastFDSProps.apiURL).toBe(
			'/o/faro/contacts/23/campaigns?channelId=123&rangeEnd=2026-10-20&rangeStart=2026-10-01'
		);
	});

	it('should keep the custom range key on the campaign link', () => {
		renderDataSet(
			'/?rangeEnd=2026-10-20&rangeKey=CUSTOM&rangeStart=2026-10-01'
		);

		const {container} = render(
			lastFDSProps.customDataRenderers.campaignNameRenderer({
				itemData: {id: '7'},
				value: 'Multi-Cloud Solutions Guide',
			})
		);

		expect(container.querySelector('a')).toHaveAttribute(
			'href',
			'/workspace/23/123/campaigns/7?rangeEnd=2026-10-20&rangeKey=CUSTOM&rangeStart=2026-10-01'
		);
	});

	it('should leave the fetching to the data set rather than pass items', () => {
		renderDataSet();

		expect(lastFDSProps.items).toBeUndefined();
	});

	it('should render the three columns the design specifies', () => {
		renderDataSet();

		expect(getFields().map(({fieldName}: any) => fieldName)).toEqual([
			'campaignName',
			'accountsTouched',
			'individualsTouched',
		]);

		expect(getFields().map(({label}: any) => label)).toEqual([
			'Campaign Name',
			'Accounts Touched',
			'Individuals Touched',
		]);
	});

	it('should make every column sortable', () => {
		renderDataSet();

		expect(getFields().every(({sortable}: any) => sortable)).toBe(true);
	});

	it('should sort by accounts touched descending by default', () => {
		renderDataSet();

		expect(lastFDSProps.sorts).toEqual([
			{
				active: true,
				default: true,
				direction: 'desc',
				key: 'accountsTouched',
				label: 'Accounts Touched',
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

	it('should link the campaign name at its detail screen, keeping the range', () => {
		renderDataSet();

		const {container} = render(
			lastFDSProps.customDataRenderers.campaignNameRenderer({
				itemData: {id: '7'},
				value: 'Multi-Cloud Solutions Guide',
			})
		);

		expect(container.querySelector('a')).toHaveAttribute(
			'href',
			'/workspace/23/123/campaigns/7?rangeKey=30'
		);
	});

	it('should render the campaign name in a heavier weight', () => {
		renderDataSet();

		const {container} = render(
			lastFDSProps.customDataRenderers.campaignNameRenderer({
				itemData: {id: '1'},
				value: 'Q3 Manufacturing Webinar',
			})
		);

		expect(container.firstChild).toHaveClass('font-weight-semi-bold');
		expect(container).toHaveTextContent('Q3 Manufacturing Webinar');
	});

	it('should abbreviate the counts', () => {
		renderDataSet();

		const {container} = render(
			lastFDSProps.customDataRenderers.countRenderer({value: 15200})
		);

		expect(container).toHaveTextContent('15.2K');
	});

	it('should render a missing count as the no data value', () => {
		renderDataSet();

		const {container} = render(
			lastFDSProps.customDataRenderers.countRenderer({})
		);

		expect(container).toHaveTextContent('–');
	});

	it('should tell the marketer that no campaigns were synced', () => {
		renderDataSet();

		expect(lastFDSProps.emptyState.title).toMatch(/no campaigns found/i);
		expect(lastFDSProps.emptyState.description).toMatch(
			/no campaigns were synced/i
		);
	});

	it('should illustrate the empty state with the theme empty state image', () => {
		renderDataSet();

		expect(lastFDSProps.emptyState.image).toBe(EMPTY_STATE_IMAGE);
		expect(lastFDSProps.emptyState.imageReducedMotion).toBe(
			EMPTY_STATE_REDUCED_MOTION_IMAGE
		);
	});

	it('should leave the search and filter empty states to the data set', () => {
		renderDataSet();

		expect(lastFDSProps.emptyState.filtered).toBeUndefined();
	});
});
