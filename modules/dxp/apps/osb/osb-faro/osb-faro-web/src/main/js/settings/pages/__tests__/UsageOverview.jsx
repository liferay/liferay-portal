import * as data from 'test/data';
import mockStore from 'test/mock-store';
import React from 'react';
import {cleanup, getByTestId, render} from '@testing-library/react';
import {fromJS} from 'immutable';
import {Project} from 'shared/util/records';
import {Provider} from 'react-redux';
import {StaticRouter} from 'react-router';
import {SubscriptionStatuses, UserRoleNames} from 'shared/util/constants';
import {UsageOverview} from '../UsageOverview';
import {useCurrentUser} from 'shared/hooks/useCurrentUser';
import {User} from 'shared/util/records';

jest.unmock('react-dom');

jest.mock('shared/hooks/useTimeZone', () => ({
	useTimeZone: () => ({timeZoneId: 'UTC'})
}));

jest.mock('shared/hooks/useCurrentUser', () => ({
	useCurrentUser: jest.fn()
}));

const defaultProps = {
	groupId: '23',
	project: new Project(
		data.mockProject(23, {
			faroSubscription: fromJS(data.mockSubscription())
		})
	)
};

const WrappedComponent = props => (
	<Provider store={mockStore()}>
		<StaticRouter>
			<UsageOverview {...props} />
		</StaticRouter>
	</Provider>
);

describe('UsageOverview', () => {
	it('should render', () => {
		useCurrentUser.mockImplementation(() => ({isAdmin: () => true}));

		const {container} = render(<WrappedComponent {...defaultProps} />);

		expect(container).toMatchSnapshot();
	});

	it('should render with a warning type and danger type warning if one metric is approaching limit and the other is over', () => {
		useCurrentUser.mockImplementation(() => ({isAdmin: () => true}));

		const mockProject = new Project(
			data.mockProject(23, {
				faroSubscription: fromJS(
					data.mockSubscription({
						individualsStatus: SubscriptionStatuses.Approaching,
						pageViewsStatus: SubscriptionStatuses.Over
					})
				)
			})
		);

		const {container} = render(
			<WrappedComponent {...defaultProps} project={mockProject} />
		);

		expect(container.querySelector('.alert-warning')).toBeInTheDocument();
	});

	it('should render with an approaching limit warning if a metric is approaching plan limit', () => {
		useCurrentUser.mockImplementation(() => ({isAdmin: () => true}));

		const mockProject = new Project(
			data.mockProject(23, {
				faroSubscription: fromJS(
					data.mockSubscription({
						pageViewsStatus: SubscriptionStatuses.Approaching
					})
				)
			})
		);

		const {container} = render(
			<WrappedComponent {...defaultProps} project={mockProject} />
		);

		expect(container.querySelector('.alert-warning')).toBeInTheDocument();
	});

	it('should render the total number of INDIVIDUALS since last anniversary and the percentage used in the plan', () => {
		useCurrentUser.mockImplementation(() => ({isAdmin: () => true}));

		const mockProject = new Project(
			data.mockProject(23, {
				faroSubscription: fromJS(
					data.mockSubscription({
						individualsCountSinceLastAnniversary: 1000,
						individualsStatus: SubscriptionStatuses.Ok
					})
				)
			})
		);

		const {getByText} = render(
			<WrappedComponent {...defaultProps} project={mockProject} />
		);

		expect(getByText('KNOWN INDIVIDUALS')).toBeInTheDocument();

		expect(
			getByText(
				'Active users logged on your DXP instance have been tracked by Analytics Cloud since Jul 08, 2018.'
			)
		).toBeInTheDocument();

		expect(
			getByText('104,000 known individuals are available.')
		).toBeInTheDocument();

		expect(
			getByText('1,000 of 105,000 - 1% known individual was used.')
		).toBeInTheDocument();
	});

	it('should display the plan count percentage as 100% for INDIVIDUALS when count is higher than limit', () => {
		useCurrentUser.mockImplementation(() => ({isAdmin: () => true}));

		const mockProject = new Project(
			data.mockProject(23, {
				faroSubscription: fromJS(
					data.mockSubscription({
						individualsCountSinceLastAnniversary: 115000
					})
				)
			})
		);

		const {getByText} = render(
			<WrappedComponent {...defaultProps} project={mockProject} />
		);

		expect(
			getByText('115,000 of 105,000 - 100% known individuals were used.')
		).toBeInTheDocument();
	});

	it('should display the plan count percentage as 100% for PAGE VIEWS when count is higher than limit', () => {
		useCurrentUser.mockImplementation(() => ({isAdmin: () => true}));

		const mockProject = new Project(
			data.mockProject(23, {
				faroSubscription: fromJS(
					data.mockSubscription({
						pageViewsCountSinceLastAnniversary: 8000000
					})
				)
			})
		);

		const {getByText} = render(
			<WrappedComponent {...defaultProps} project={mockProject} />
		);

		expect(
			getByText('8,000,000 of 7,000,000 - 100% page views were used.')
		).toBeInTheDocument();
	});

	it('should display the limit of INDIVIDUALS and PAGE VIEWS. Also, it should render a warning if INDIVIDUALS is over the limit. Also, it should render the current plan name.', () => {
		useCurrentUser.mockImplementation(() => ({isAdmin: () => true}));

		const mockProject = new Project(
			data.mockProject(23, {
				faroSubscription: fromJS(
					data.mockSubscription({
						individualsStatus: SubscriptionStatuses.Over
					})
				)
			})
		);

		const {container, getByText} = render(
			<WrappedComponent {...defaultProps} project={mockProject} />
		);

		expect(container.querySelector('.alert-warning')).toBeInTheDocument();

		expect(getByText('Enterprise')).toBeInTheDocument();
	});

	it('should render the total of PAGE VIEWS since the last anniversary and the percentage used in the plan', () => {
		useCurrentUser.mockImplementation(() => ({isAdmin: () => true}));

		const mockProject = new Project(
			data.mockProject(23, {
				faroSubscription: fromJS(
					data.mockSubscription({
						individualsStatus: SubscriptionStatuses.Ok,
						pageViewsCountSinceLastAnniversary: 111123
					})
				)
			})
		);

		const {getByText} = render(
			<WrappedComponent {...defaultProps} project={mockProject} />
		);

		expect(getByText('PAGE VIEWS')).toBeInTheDocument();

		expect(
			getByText(
				'Active users logged on your DXP instance have been tracked by Analytics Cloud since Jul 08, 2018.'
			)
		).toBeInTheDocument();

		expect(
			getByText('111,123 of 7,000,000 - 1.6% page views were used.')
		).toBeInTheDocument();

		expect(
			getByText('6,888,877 page views are available.')
		).toBeInTheDocument();
	});

	it('should render with an overage warning if the PAGE VIEWS metric has exceeded the plan limit', () => {
		useCurrentUser.mockImplementation(() => ({isAdmin: () => true}));

		const mockProject = new Project(
			data.mockProject(23, {
				faroSubscription: fromJS(
					data.mockSubscription({
						pageViewsStatus: SubscriptionStatuses.Over
					})
				)
			})
		);

		const {container} = render(
			<WrappedComponent {...defaultProps} project={mockProject} />
		);

		expect(container.querySelector('.alert-warning')).toBeInTheDocument();
	});

	it('should render with a member-specific message overage warning if a metric is approaching plan limit and the user is a member role', () => {
		useCurrentUser.mockImplementation(() => ({
			isAdmin: () => false
		}));

		const mockProject = new Project(
			data.mockProject(23, {
				faroSubscription: fromJS(
					data.mockSubscription({
						pageViewsStatus: SubscriptionStatuses.Approaching
					})
				)
			})
		);

		const {container, getByText} = render(
			<WrappedComponent {...defaultProps} project={mockProject} />
		);

		expect(container.querySelector('.alert-warning')).toBeInTheDocument();
		expect(
			getByText(
				'Usage limit is approaching. Please contact your workspace administrator at the earliest convenience.'
			)
		).toBeInTheDocument();
	});

	it('not renders next anniversary date for BASIC PLAN', () => {
		useCurrentUser.mockImplementation(() => ({isAdmin: () => true}));

		const mockProject = new Project(
			data.mockProject(23, {
				faroSubscription: fromJS(
					data.mockSubscription({
						name: 'Liferay Analytics Cloud Basic'
					})
				)
			})
		);

		const {queryByTestId} = render(
			<WrappedComponent {...defaultProps} project={mockProject} />
		);

		expect(queryByTestId('next-anniversary-date')).toBeNull();
	});

	it('renders next anniversary date for ENTERPRISE PLAN', () => {
		useCurrentUser.mockImplementation(() => ({isAdmin: () => true}));

		const mockProject = new Project(
			data.mockProject(23, {
				faroSubscription: fromJS(
					data.mockSubscription({
						name: 'Liferay Analytics Cloud Enterprise'
					})
				)
			})
		);

		const {queryByTestId} = render(
			<WrappedComponent {...defaultProps} project={mockProject} />
		);

		expect(queryByTestId('next-anniversary-date').textContent).toEqual(
			'Plan usage resets on Jul 08, 2019.'
		);
	});

	it('renders next anniversary date for BUSINESS PLAN', () => {
		useCurrentUser.mockImplementation(() => ({isAdmin: () => true}));

		const mockProject = new Project(
			data.mockProject(23, {
				faroSubscription: fromJS(
					data.mockSubscription({
						name: 'Liferay Analytics Cloud Business'
					})
				)
			})
		);

		const {queryByTestId} = render(
			<WrappedComponent {...defaultProps} project={mockProject} />
		);

		expect(queryByTestId('next-anniversary-date').textContent).toEqual(
			'Plan usage resets on Jul 08, 2019.'
		);
	});

	it('not renders management button for Member view', () => {
		useCurrentUser.mockImplementation(() => ({isAdmin: () => true}));

		const mockProject = new Project(
			data.mockProject(23, {
				faroSubscription: fromJS(data.mockSubscription())
			})
		);

		const {queryByText} = render(
			<WrappedComponent {...defaultProps} project={mockProject} />
		);

		expect(
			queryByText('Manage Subscriptions(Opens a new window)')
		).toBeNull();
	});

	it('not renders management button for Admin view', () => {
		useCurrentUser.mockImplementation(() => ({isAdmin: () => true}));

		const mockProject = new Project(
			data.mockProject(23, {
				faroSubscription: fromJS(data.mockSubscription())
			})
		);

		const {queryByText} = render(
			<WrappedComponent {...defaultProps} project={mockProject} />
		);

		expect(queryByText('Go to Customer Portal')).toBeInTheDocument();
	});

	it('renders current usage message for BASIC PLAN', () => {
		const mockProject = new Project(
			data.mockProject(23, {
				faroSubscription: fromJS(
					data.mockSubscription({
						name: 'Liferay Analytics Cloud Basic'
					})
				)
			})
		);

		const {queryByText} = render(
			<WrappedComponent
				{...defaultProps}
				currentUser={
					new User(data.mockUser(0, {roleName: UserRoleNames.Member}))
				}
				project={mockProject}
			/>
		);

		expect(
			queryByText(
				'When either limit is exceeded, the current plan will have to be upgraded to Business or Enterprise.'
			)
		).toBeInTheDocument();
	});

	it('renders current usage message for BUSINESS PLAN', () => {
		const mockProject = new Project(
			data.mockProject(23, {
				faroSubscription: fromJS(
					data.mockSubscription({
						name: 'Liferay Analytics Cloud Business'
					})
				)
			})
		);

		const {queryByText} = render(
			<WrappedComponent
				{...defaultProps}
				currentUser={
					new User(data.mockUser(0, {roleName: UserRoleNames.Member}))
				}
				project={mockProject}
			/>
		);

		expect(
			queryByText(
				'When either limit is exceeded, the current plan will either have to be upgraded or add-ons will have to be purchased to accommodate the overage.'
			)
		).toBeInTheDocument();
	});

	it('renders current usage message for ENTERPRISE PLAN', () => {
		const mockProject = new Project(
			data.mockProject(23, {
				faroSubscription: fromJS(
					data.mockSubscription({
						name: 'Liferay Analytics Cloud Enterprise'
					})
				)
			})
		);

		const {queryByText} = render(
			<WrappedComponent
				{...defaultProps}
				currentUser={
					new User(data.mockUser(0, {roleName: UserRoleNames.Member}))
				}
				project={mockProject}
			/>
		);

		expect(
			queryByText(
				'When either limit is exceeded, the current plan will either have to be upgraded or add-ons will have to be purchased to accommodate the overage.'
			)
		).toBeInTheDocument();
	});
});

describe('Subscription Details', () => {
	afterEach(cleanup);

	it('renders subscription details for BASIC PLAN', () => {
		useCurrentUser.mockImplementation(() => ({isAdmin: () => true}));

		const mockProject = new Project(
			data.mockProject(23, {
				faroSubscription: fromJS(
					data.mockSubscription({
						name: 'Liferay Analytics Cloud Basic'
					})
				)
			})
		);

		const {container, queryByText} = render(
			<WrappedComponent {...defaultProps} project={mockProject} />
		);

		expect(queryByText('PURCHASED ADD-ONS')).toBeNull();

		expect(
			getByTestId(container, 'subscription-details')
		).toMatchSnapshot();
	});

	it('renders subscription details for BUSINESS PLAN', () => {
		useCurrentUser.mockImplementation(() => ({isAdmin: () => true}));

		const mockProject = new Project(
			data.mockProject(23, {
				faroSubscription: fromJS(
					data.mockSubscription({
						name: 'Liferay Analytics Cloud Business'
					})
				)
			})
		);

		const {container, queryByText} = render(
			<WrappedComponent {...defaultProps} project={mockProject} />
		);

		expect(queryByText('PURCHASED ADD-ONS')).toBeInTheDocument();

		expect(
			getByTestId(container, 'subscription-details')
		).toMatchSnapshot();
	});

	it('renders subscription details for ENTERPRISE PLAN', () => {
		useCurrentUser.mockImplementation(() => ({isAdmin: () => true}));

		const mockProject = new Project(
			data.mockProject(23, {
				faroSubscription: fromJS(
					data.mockSubscription({
						name: 'Liferay Analytics Cloud Enterprise'
					})
				)
			})
		);

		const {container, queryByText} = render(
			<WrappedComponent {...defaultProps} project={mockProject} />
		);

		expect(queryByText('PURCHASED ADD-ONS')).toBeInTheDocument();

		expect(
			getByTestId(container, 'subscription-details')
		).toMatchSnapshot();
	});
});
