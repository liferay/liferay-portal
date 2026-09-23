import CampaignsDataSet from '../CampaignsDataSet';
import React from 'react';
import {cleanup, render, screen} from '@testing-library/react';
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

const renderDataSet = () =>
	render(<CampaignsDataSet channelId="123" groupId="23" />);

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
			'/o/faro/contacts/23/campaigns?channelId=123'
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

	it('should link the campaign name at its detail screen', () => {
		renderDataSet();

		const {container} = render(
			lastFDSProps.customDataRenderers.campaignNameRenderer({
				itemData: {id: '7'},
				value: 'Multi-Cloud Solutions Guide',
			})
		);

		expect(container.querySelector('a')).toHaveAttribute(
			'href',
			'/workspace/23/123/campaigns/7'
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

	it('should render a missing count as zero', () => {
		renderDataSet();

		const {container} = render(
			lastFDSProps.customDataRenderers.countRenderer({})
		);

		expect(container).toHaveTextContent('0');
	});

	it('should tell the marketer that no campaigns were synced', () => {
		renderDataSet();

		expect(lastFDSProps.emptyState.title).toMatch(/no campaigns found/i);
		expect(lastFDSProps.emptyState.description).toMatch(
			/no campaigns were synced/i
		);
	});

	it('should illustrate the empty state with the satellite', () => {
		renderDataSet();

		expect(lastFDSProps.emptyState.image).toBe('/states/satellite.svg');
		expect(lastFDSProps.emptyState.imageReducedMotion).toBe(
			'/states/satellite.svg'
		);
	});

	it('should leave the search and filter empty states to the data set', () => {
		renderDataSet();

		expect(lastFDSProps.emptyState.filtered).toBeUndefined();
	});
});
