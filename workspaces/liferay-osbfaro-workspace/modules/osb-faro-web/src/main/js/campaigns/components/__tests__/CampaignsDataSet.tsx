import CampaignsDataSet from '../CampaignsDataSet';
import React from 'react';
import {cleanup, render, screen} from '@testing-library/react';
import {mockCampaigns} from '../../utils/mock-campaigns';
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

beforeAll(warmFrontendDataSet);

describe('CampaignsDataSet', () => {
	beforeEach(() => {
		lastFDSProps = undefined;
	});

	afterEach(cleanup);

	it('should render the FrontendDataSet with id "campaigns-list-dataset"', () => {
		render(<CampaignsDataSet items={mockCampaigns} />);

		expect(screen.getByTestId('fds-component')).toHaveAttribute(
			'id',
			'campaigns-list-dataset'
		);
	});

	it('should feed the mocked campaigns to the data set', () => {
		render(<CampaignsDataSet items={mockCampaigns} />);

		expect(lastFDSProps.items).toBe(mockCampaigns);
	});

	it('should render the three columns the design specifies', () => {
		render(<CampaignsDataSet items={mockCampaigns} />);

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

	it('should leave the columns unsortable while the data set runs on items', () => {
		render(<CampaignsDataSet items={mockCampaigns} />);

		expect(getFields().every(({sortable}: any) => !sortable)).toBe(true);
	});

	it('should paginate', () => {
		render(<CampaignsDataSet items={mockCampaigns} />);

		expect(lastFDSProps.showPagination).toBe(true);
	});

	it('should not offer a search box, nor the bar holding it', () => {
		render(<CampaignsDataSet items={mockCampaigns} />);

		expect(lastFDSProps.showSearch).toBe(false);
		expect(lastFDSProps.showManagementBar).toBe(false);
	});

	it('should render the campaign name in a heavier weight', () => {
		render(<CampaignsDataSet items={mockCampaigns} />);

		const {container} = render(
			lastFDSProps.customDataRenderers.campaignNameRenderer({
				value: 'Q3 Manufacturing Webinar',
			})
		);

		expect(container.firstChild).toHaveClass('font-weight-semi-bold');
		expect(container).toHaveTextContent('Q3 Manufacturing Webinar');
	});

	it('should abbreviate the counts', () => {
		render(<CampaignsDataSet items={mockCampaigns} />);

		const {container} = render(
			lastFDSProps.customDataRenderers.countRenderer({value: 15200})
		);

		expect(container).toHaveTextContent('15.2K');
	});

	it('should render a missing count as zero', () => {
		render(<CampaignsDataSet items={mockCampaigns} />);

		const {container} = render(
			lastFDSProps.customDataRenderers.countRenderer({})
		);

		expect(container).toHaveTextContent('0');
	});
});
