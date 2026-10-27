import MembershipsCard from '../MembershipsCard';
import React from 'react';
import {
	cleanup,
	fireEvent,
	render,
	screen,
	waitFor,
} from '@testing-library/react';
import {MemoryRouter} from 'react-router-dom';
import {useRequest} from 'shared/hooks/useRequest';
import {warmFrontendDataSet} from 'test/warm-frontend-data-set';

jest.unmock('react-dom');

jest.mock('@liferay/frontend-data-set-web', () => ({
	...jest.requireActual('@liferay/frontend-data-set-web'),
	FrontendDataSet: (props: any) => (
		<div
			data-apiurl={props.apiURL}
			data-testid="fds-component"
			id={props.id}
		/>
	),
}));

jest.mock('shared/hooks/useRequest', () => ({
	useRequest: jest.fn(),
}));

const mockLifecycles = (data?: {id: string}[], loading = false) =>
	(useRequest as jest.Mock).mockReturnValue({data, loading});

const renderCard = () =>
	render(
		<MemoryRouter>
			<MembershipsCard campaignId="7" channelId="123" groupId="23" />
		</MemoryRouter>
	);

const getDataSet = (id: string) => document.getElementById(id) as HTMLElement;

const expectActivePane = (id: string) =>
	waitFor(() =>
		expect(getDataSet(id).closest('.tab-pane')).toHaveClass('active')
	);

beforeAll(warmFrontendDataSet);

describe('MembershipsCard', () => {
	beforeEach(() => {
		mockLifecycles([{id: '55'}]);
	});

	afterEach(cleanup);

	it('should title the card Membership', () => {
		renderCard();

		expect(screen.getByText('Membership')).toBeInTheDocument();
	});

	it('should render both tabs with Accounts Touched active', () => {
		renderCard();

		expect(screen.getByText('Accounts Touched')).toHaveClass('active');
		expect(screen.getByText('Individuals Touched')).not.toHaveClass(
			'active'
		);
	});

	// The lifecycle stage sort only applies when the endpoint knows which
	// account lifecycle the stages belong to, so the card sends the default
	// lifecycle of the channel.

	it('should ask for the default account lifecycle', () => {
		renderCard();

		expect(useRequest).toHaveBeenCalledWith(
			expect.objectContaining({
				variables: {
					channelId: '123',
					defaultAccountLifecycle: true,
					groupId: '23',
				},
			})
		);
	});

	it('should pass the default account lifecycle to the accounts table', () => {
		renderCard();

		expect(getDataSet('campaign-accounts-dataset')).toHaveAttribute(
			'data-apiurl',
			expect.stringContaining('accountLifecycleId=55')
		);
	});

	it('should hold the accounts table back while the lifecycle loads', () => {
		mockLifecycles(undefined, true);

		renderCard();

		expect(screen.getAllByTestId('fds-component')).toHaveLength(1);
		expect(getDataSet('campaign-individuals-dataset')).toBeInTheDocument();
	});

	it('should show the accounts table under Accounts Touched', async () => {
		renderCard();

		await expectActivePane('campaign-accounts-dataset');

		expect(
			getDataSet('campaign-individuals-dataset').closest('.tab-pane')
		).not.toHaveClass('active');
	});

	it('should select the Individuals Touched tab when it is clicked', () => {
		renderCard();

		fireEvent.click(screen.getByText('Individuals Touched'));

		expect(screen.getByText('Individuals Touched')).toHaveClass('active');
		expect(screen.getByText('Accounts Touched')).not.toHaveClass('active');
	});

	it('should show the individuals table under Individuals Touched', async () => {
		renderCard();

		fireEvent.click(screen.getByText('Individuals Touched'));

		await expectActivePane('campaign-individuals-dataset');
	});

	// Remounting a data set sends it back to its first page and default sort.

	it('should keep both tables mounted when switching tabs', async () => {
		renderCard();

		const accountsDataSet = getDataSet('campaign-accounts-dataset');
		const individualsDataSet = getDataSet('campaign-individuals-dataset');

		fireEvent.click(screen.getByText('Individuals Touched'));

		await expectActivePane('campaign-individuals-dataset');

		fireEvent.click(screen.getByText('Accounts Touched'));

		await expectActivePane('campaign-accounts-dataset');

		expect(getDataSet('campaign-accounts-dataset')).toBe(accountsDataSet);
		expect(getDataSet('campaign-individuals-dataset')).toBe(
			individualsDataSet
		);
	});
});
