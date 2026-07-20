import * as data from 'test/data';
import mockStore, {mockStoreDataLDP} from 'test/mock-store';
import React from 'react';
import UsageOverviewExternal from '../UsageOverviewExternal';
import {fromJS} from 'immutable';
import {Project} from 'shared/util/records';
import {Provider} from 'react-redux';
import {render} from '@testing-library/react';
import {StaticRouter} from 'react-router';
import {SubscriptionNames} from 'shared/util/subscriptions';
import {useCurrentUser} from 'shared/hooks/useCurrentUser';

jest.unmock('react-dom');

jest.mock('shared/hooks/useTimeZone', () => ({
	useTimeZone: () => ({timeZoneId: 'UTC'})
}));

jest.mock('react-router-dom', () => ({
	...jest.requireActual('react-router-dom'),
	useParams: () => ({
		groupId: '23'
	})
}));

jest.mock('shared/hooks/useCurrentUser', () => ({
	useCurrentUser: jest.fn()
}));

const defaultProps = {
	groupId: '23',
	project: new Project(
		data.mockProject(23, {
			faroSubscription: fromJS(
				data.mockSubscription({
					name: SubscriptionNames.LiferaySaasEnterprisePlan
				})
			)
		})
	)
};

const WrappedComponent = ({store = mockStore(), ...props}) => (
	<Provider store={store}>
		<StaticRouter>
			<UsageOverviewExternal {...props} />
		</StaticRouter>
	</Provider>
);

describe('UsageOverviewExternal', () => {
	it('should render', () => {
		useCurrentUser.mockImplementation(() => ({isAdmin: () => true}));

		const {container} = render(<WrappedComponent {...defaultProps} />);

		expect(container).toMatchSnapshot();
	});

	it('should render the Hero Banner', () => {
		const {container} = render(<WrappedComponent {...defaultProps} />);

		expect(container.querySelector('.saas-banner')).toBeTruthy();
	});

	it('should render the "Usage Overview" page for SaaS title', () => {
		const {getByText} = render(<WrappedComponent {...defaultProps} />);

		expect(getByText('View Your SaaS Project Metrics')).toBeInTheDocument();
	});

	it('should render the "Go to Customer Portal" button for admin user', () => {
		useCurrentUser.mockImplementation(() => ({isAdmin: () => true}));

		const {queryByText} = render(<WrappedComponent {...defaultProps} />);

		expect(queryByText('Go to Customer Portal')).toBeInTheDocument();
	});

	it('should not render the "Go to Customer Portal" button for member user', () => {
		useCurrentUser.mockImplementation(() => ({isAdmin: () => false}));

		const {queryByText} = render(<WrappedComponent {...defaultProps} />);

		expect(queryByText('Go to Customer Portal')).toBeNull();
	});

	it('should render the "Sites and Users" and "Resource Usage" sections', () => {
		const {getByText} = render(<WrappedComponent {...defaultProps} />);

		expect(getByText('Sites and Users')).toBeInTheDocument();
		expect(getByText('Resource Usage')).toBeInTheDocument();
	});
});

describe('UsageOverviewExternal when the subscription is LDP', () => {
	it('should render the "View Your Workspace Metrics" title', () => {
		const {getByText} = render(
			<WrappedComponent {...defaultProps} store={mockStore(mockStoreDataLDP)} />
		);

		expect(getByText('View Your Workspace Metrics')).toBeInTheDocument();
	});

	it('should render the "Go to Liferay One" button for admin user', () => {
		useCurrentUser.mockImplementation(() => ({isAdmin: () => true}));

		const {getByText} = render(
			<WrappedComponent {...defaultProps} store={mockStore(mockStoreDataLDP)} />
		);

		expect(getByText('Go to Liferay One')).toBeInTheDocument();
	});

	it('should not render the "SaaS plan usage" description', () => {
		const {queryByText} = render(
			<WrappedComponent {...defaultProps} store={mockStore(mockStoreDataLDP)} />
		);

		expect(
			queryByText('SaaS plan usage is determined by MALUs and APVs.')
		).toBeNull();
	});

	it('should redact the metric titles in the "Sites and Users" section', () => {
		const {container, queryByRole} = render(
			<WrappedComponent {...defaultProps} store={mockStore(mockStoreDataLDP)} />
		);

		expect(
			queryByRole('heading', {level: 3, name: 'Number of Sites'})
		).toBeNull();
		expect(
			container.querySelectorAll('.card-title-rectangle')
		).toHaveLength(3);
	});
});
