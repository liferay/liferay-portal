import React from 'react';
import TouchedAccountsCard from '../TouchedAccountsCard';
import {cleanup, fireEvent, render, screen} from '@testing-library/react';
import {mockCampaignAccounts} from '../../utils/mock-campaigns';
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

beforeAll(warmFrontendDataSet);

const renderCard = () =>
	render(
		<TouchedAccountsCard
			channelId="123"
			groupId="23"
			items={mockCampaignAccounts}
		/>
	);

const getFields = () => lastFDSProps.views[0].schema.fields;

describe('TouchedAccountsCard', () => {
	afterEach(cleanup);

	beforeEach(() => {
		lastFDSProps = undefined;
	});

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

	it('should leave the Individuals Touched tab inert', () => {
		renderCard();

		const tab = screen.getByText('Individuals Touched');

		expect(tab).toBeDisabled();

		fireEvent.click(tab);

		// Clicking it must not move the selection off Accounts Touched.

		expect(screen.getByText('Accounts Touched')).toHaveClass('active');
		expect(tab).not.toHaveClass('active');
	});

	it('should render the four columns the design shows, unsorted', () => {
		renderCard();

		expect(getFields().map(({label}: any) => label)).toEqual([
			'Name',
			'Lifecycle Stage',
			'Pipeline Value',
			'Closed Won',
		]);

		expect(getFields().every(({sortable}: any) => !sortable)).toBe(true);
	});

	it('should feed the mocked accounts to the data set', () => {
		renderCard();

		expect(lastFDSProps.items).toBe(mockCampaignAccounts);
		expect(screen.getByTestId('fds-component')).toHaveAttribute(
			'id',
			'campaign-accounts-dataset'
		);
	});

	it('should link the account name at its Overview tab', () => {
		renderCard();

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

	it('should render the lifecycle stage as a coloured label', () => {
		renderCard();

		const {container} = render(
			lastFDSProps.customDataRenderers.lifecycleStageRenderer({
				value: 'AT_RISK',
			})
		);

		expect(container).toHaveTextContent('At Risk');
		expect(container.querySelector('.label-danger')).toBeTruthy();
	});

	it('should abbreviate an amount and leave a missing one blank', () => {
		renderCard();

		const amount = (value?: number) =>
			render(lastFDSProps.customDataRenderers.amountRenderer({value}))
				.container.textContent;

		expect(amount(18500000)).toBe('18.5M');
		expect(amount(950000)).toBe('950K');

		// The design carries a 0 in these cells, but at zero opacity, so an
		// account with no opportunity value shows nothing rather than a zero.

		expect(amount(undefined)).toBe('');
	});

	it('should leave the empty result to the data set default', () => {
		render(<TouchedAccountsCard channelId="123" groupId="23" items={[]} />);

		// The design asks for the FrontendDataSet default here, so the card
		// must not override `emptyState` with one of its own.

		expect(lastFDSProps.items).toEqual([]);
		expect(lastFDSProps.emptyState).toBeUndefined();
	});

	it('should not offer a search box, nor the bar holding it', () => {
		renderCard();

		expect(lastFDSProps.showSearch).toBe(false);
		expect(lastFDSProps.showManagementBar).toBe(false);
	});
});
