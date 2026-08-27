import IndividualsDataSet, {getVisitorType} from '../IndividualsDataSet';
import React from 'react';
import {cleanup, render, screen} from '@testing-library/react';

jest.unmock('react-dom');

let lastFDSProps: any;
let mockSearch = '';

jest.mock('@liferay/frontend-data-set-web', () => ({
	...jest.requireActual('@liferay/frontend-data-set-web'),
	FrontendDataSet: (props: any) => {
		lastFDSProps = props;

		return <div data-testid="fds-component" id={props.id} />;
	},
}));

jest.mock('react-router-dom', () => ({
	...jest.requireActual('react-router-dom'),
	useLocation: () => ({search: mockSearch}),
	useParams: () => ({channelId: '456', groupId: '23', id: 'acc-1'}),
}));

describe('IndividualsDataSet', () => {
	beforeEach(() => {
		jest.clearAllMocks();
		lastFDSProps = undefined;
		mockSearch = '';
	});

	afterEach(cleanup);

	it('should render the FrontendDataSet with the dataset id', () => {
		render(<IndividualsDataSet />);

		expect(screen.getByTestId('fds-component')).toHaveAttribute(
			'id',
			'account-individuals-dataset'
		);
	});

	it('should call the account individuals api with the group and account ids', () => {
		render(<IndividualsDataSet />);

		expect(lastFDSProps.apiURL).toBe(
			'/o/faro/contacts/23/account/acc-1/individuals?channelId=456&rangeKey=30'
		);
	});

	it('should default the range to the last thirty days', () => {
		render(<IndividualsDataSet />);

		expect(lastFDSProps.apiURL).toContain('rangeKey=30');
	});

	it('should request the range that is on the query string', () => {
		mockSearch = '?rangeKey=7';

		render(<IndividualsDataSet />);

		expect(lastFDSProps.apiURL).toBe(
			'/o/faro/contacts/23/account/acc-1/individuals?channelId=456&rangeKey=7'
		);
	});

	it('should request the bounds of a custom range', () => {
		mockSearch =
			'?rangeEnd=2026-02-20&rangeKey=CUSTOM&rangeStart=2026-02-10';

		render(<IndividualsDataSet />);

		expect(lastFDSProps.apiURL).toContain('rangeEnd=2026-02-20');
		expect(lastFDSProps.apiURL).toContain('rangeKey=CUSTOM');
		expect(lastFDSProps.apiURL).toContain('rangeStart=2026-02-10');
	});

	it('should tell an empty result apart by the selected period', () => {
		render(<IndividualsDataSet />);

		expect(lastFDSProps.emptyState.description).toBe(
			'No activities were found on the selected period.'
		);
		expect(lastFDSProps.emptyState.title).toBe(
			'No individuals were found.'
		);
	});

	it('should configure the dataset with pagination shown', () => {
		render(<IndividualsDataSet />);

		expect(lastFDSProps.showPagination).toBe(true);
		expect(lastFDSProps.pagination).toBeDefined();
	});

	describe('preview', () => {
		it('should use its own dataset id', () => {
			render(<IndividualsDataSet preview />);

			expect(screen.getByTestId('fds-component')).toHaveAttribute(
				'id',
				'most-engaged-individuals-dataset'
			);
		});

		it('should request only three individuals', () => {
			render(<IndividualsDataSet preview />);

			expect(lastFDSProps.views[0].initialPaginationDelta).toBe(3);
		});

		it('should hide the management bar, the search and the pagination', () => {
			render(<IndividualsDataSet preview />);

			expect(lastFDSProps.showManagementBar).toBe(false);
			expect(lastFDSProps.showSearch).toBe(false);
			expect(lastFDSProps.showPagination).toBe(false);
			expect(lastFDSProps.pagination).toBeUndefined();
		});

		it('should declare the same columns as the full table, none sortable', () => {
			render(<IndividualsDataSet preview />);

			const fields = lastFDSProps.views[0].schema.fields;

			expect(fields.map((field: any) => field.fieldName)).toEqual([
				'name',
				'jobTitle',
				'sessionsCount',
				'activitiesCount',
				'averageSessionDuration',
				'lastActivityDate',
			]);
			expect(fields.some((field: any) => field.sortable)).toBe(false);
		});

		it('should keep the total events sort of the full table', () => {
			render(<IndividualsDataSet preview />);

			expect(lastFDSProps.sorts[0].key).toBe('activitiesCount');
			expect(lastFDSProps.sorts[0].direction).toBe('desc');
		});
	});

	it('should declare the six expected sortable columns', () => {
		render(<IndividualsDataSet />);

		const fields = lastFDSProps.views[0].schema.fields;

		expect(fields.map((field: any) => field.fieldName)).toEqual([
			'name',
			'jobTitle',
			'sessionsCount',
			'activitiesCount',
			'averageSessionDuration',
			'lastActivityDate',
		]);
		expect(fields.every((field: any) => field.sortable === true)).toBe(
			true
		);
	});

	it('should no longer declare the department column', () => {
		render(<IndividualsDataSet />);

		const fields = lastFDSProps.views[0].schema.fields;

		expect(
			fields.some((field: any) => field.fieldName === 'department')
		).toBe(false);
	});

	it('should label the columns from the language bundle', () => {
		render(<IndividualsDataSet />);

		const fields = lastFDSProps.views[0].schema.fields;

		expect(fields.map((field: any) => field.label)).toEqual([
			'Individual Name',
			'Job Title',
			'Visitor Type',
			'Total Events',
			'Avg. Session Duration',
			'Last Active',
		]);
	});

	it('should sort the individuals by total events descending', () => {
		render(<IndividualsDataSet />);

		expect(lastFDSProps.sorts).toEqual([
			{
				active: true,
				default: true,
				direction: 'desc',
				key: 'activitiesCount',
				label: 'Total Events',
			},
		]);
	});

	it('should abbreviate the total events', () => {
		render(<IndividualsDataSet />);

		const renderer = lastFDSProps.customDataRenderers.totalEventsRenderer;

		expect(renderer({value: 6700})).toBe('6.7K');
		expect(renderer({})).toBe('');
	});

	it('should format the average session duration', () => {
		render(<IndividualsDataSet />);

		const renderer =
			lastFDSProps.customDataRenderers.avgSessionDurationRenderer;

		expect(renderer({value: 675000})).toBe('00:11:15');
		expect(renderer({})).toBe('');
	});

	it('should read the job title from the individual properties', () => {
		render(<IndividualsDataSet />);

		const renderer = lastFDSProps.customDataRenderers.jobTitleRenderer;

		expect(
			renderer({itemData: {properties: {jobTitle: 'Estimator'}}})
		).toBe('Estimator');
		expect(renderer({itemData: {}})).toBe('');
	});

	it('should wire the individual name renderer to the contacts individual route', () => {
		render(<IndividualsDataSet />);

		const renderer =
			lastFDSProps.customDataRenderers.individualNameRenderer;

		const link = renderer({
			itemData: {id: 'individual-1'},
			value: 'Ada Lovelace',
		});

		expect(link.props.href).toContain(
			'/contacts/individuals/known-individuals/individual-1'
		);
		expect(link.props.children).toBe('Ada Lovelace');
	});

	it('should carry the range to the individual profile', () => {
		mockSearch =
			'?rangeEnd=2026-02-20&rangeKey=CUSTOM&rangeStart=2026-02-10';

		render(<IndividualsDataSet />);

		const renderer =
			lastFDSProps.customDataRenderers.individualNameRenderer;

		const link = renderer({
			itemData: {id: 'individual-1'},
			value: 'Ada Lovelace',
		});

		expect(link.props.href).toContain('rangeEnd=2026-02-20');
		expect(link.props.href).toContain('rangeKey=CUSTOM');
		expect(link.props.href).toContain('rangeStart=2026-02-10');
	});

	it('should not put the channel id on the individual profile query', () => {
		render(<IndividualsDataSet />);

		const renderer =
			lastFDSProps.customDataRenderers.individualNameRenderer;

		const link = renderer({
			itemData: {id: 'individual-1'},
			value: 'Ada Lovelace',
		});

		expect(link.props.href).not.toContain('channelId=');
	});

	it('should format the last active date', () => {
		render(<IndividualsDataSet />);

		const renderer = lastFDSProps.customDataRenderers.lastActiveRenderer;

		const cell = renderer({value: '2026-05-01T10:23:00Z'});

		expect(cell.props.children).toBeTruthy();
	});
});

describe('getVisitorType', () => {
	it('should read no sessions as no activities', () => {
		expect(getVisitorType(0).label).toBe('No Activities');
	});

	it('should read a single session as a first time visitor', () => {
		expect(getVisitorType(1).label).toBe('First Time');
	});

	it('should read more than one session as a returning visitor', () => {
		expect(getVisitorType(2).label).toBe('Returning');
	});
});
