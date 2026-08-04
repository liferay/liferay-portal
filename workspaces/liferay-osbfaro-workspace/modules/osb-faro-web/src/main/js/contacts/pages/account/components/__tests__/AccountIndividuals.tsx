import AccountIndividuals, {getVisitorType} from '../AccountIndividuals';
import React from 'react';
import {cleanup, render, screen} from '@testing-library/react';

jest.unmock('react-dom');

let lastFDSProps: any;

jest.mock('@liferay/frontend-data-set-web', () => ({
	...jest.requireActual('@liferay/frontend-data-set-web'),
	FrontendDataSet: (props: any) => {
		lastFDSProps = props;

		return <div data-testid="fds-component" id={props.id} />;
	},
}));

jest.mock('react-router-dom', () => ({
	...jest.requireActual('react-router-dom'),
	useParams: () => ({channelId: '456', groupId: '23', id: 'acc-1'}),
}));

describe('AccountIndividuals', () => {
	beforeEach(() => {
		jest.clearAllMocks();
		lastFDSProps = undefined;
	});

	afterEach(cleanup);

	it('should render the card title', () => {
		render(<AccountIndividuals />);

		expect(screen.getByText('Account Individuals')).toBeInTheDocument();
	});

	it('should render the card description', () => {
		render(<AccountIndividuals />);

		expect(
			screen.getByText(
				'Lists all individuals associated with this account.'
			)
		).toBeInTheDocument();
	});

	it('should render the FrontendDataSet with the dataset id', () => {
		render(<AccountIndividuals />);

		expect(screen.getByTestId('fds-component')).toHaveAttribute(
			'id',
			'account-individuals-dataset'
		);
	});

	it('should call the account individuals api with the group and account ids', () => {
		render(<AccountIndividuals />);

		expect(lastFDSProps.apiURL).toBe(
			'/o/faro/contacts/23/account/acc-1/individuals?channelId=456'
		);
	});

	it('should configure the dataset with pagination shown', () => {
		render(<AccountIndividuals />);

		expect(lastFDSProps.showPagination).toBe(true);
		expect(lastFDSProps.pagination).toBeDefined();
	});

	it('should declare the six expected sortable columns', () => {
		render(<AccountIndividuals />);

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
		render(<AccountIndividuals />);

		const fields = lastFDSProps.views[0].schema.fields;

		expect(
			fields.some((field: any) => field.fieldName === 'department')
		).toBe(false);
	});

	it('should label the columns from the language bundle', () => {
		render(<AccountIndividuals />);

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
		render(<AccountIndividuals />);

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
		render(<AccountIndividuals />);

		const renderer = lastFDSProps.customDataRenderers.totalEventsRenderer;

		expect(renderer({value: 6700})).toBe('6.7K');
		expect(renderer({})).toBe('');
	});

	it('should format the average session duration', () => {
		render(<AccountIndividuals />);

		const renderer =
			lastFDSProps.customDataRenderers.avgSessionDurationRenderer;

		expect(renderer({value: 675000})).toBe('00:11:15');
		expect(renderer({})).toBe('');
	});

	it('should read the job title from the individual properties', () => {
		render(<AccountIndividuals />);

		const renderer = lastFDSProps.customDataRenderers.jobTitleRenderer;

		expect(
			renderer({itemData: {properties: {jobTitle: 'Estimator'}}})
		).toBe('Estimator');
		expect(renderer({itemData: {}})).toBe('');
	});

	it('should wire the individual name renderer to the contacts individual route', () => {
		render(<AccountIndividuals />);

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

	it('should format the last active date', () => {
		render(<AccountIndividuals />);

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
		expect(getVisitorType(1).label).toBe('First-Time');
	});

	it('should read more than one session as a returning visitor', () => {
		expect(getVisitorType(2).label).toBe('Returning');
	});
});
