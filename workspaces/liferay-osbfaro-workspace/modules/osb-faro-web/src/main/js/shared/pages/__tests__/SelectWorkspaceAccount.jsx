import * as data from 'test/data';
import mockStore from 'test/mock-store';
import React from 'react';
import SelectWorkspaceAccount, {routingFn} from '../SelectWorkspaceAccount';
import {BrowserRouter} from 'react-router-dom';
import {DataSourceStates} from 'shared/util/constants';
import {fromJS} from 'immutable';
import {Project} from 'shared/util/records';
import {Provider} from 'react-redux';
import {render} from '@testing-library/react';
import {Routes, setUriQueryValue, toRoute} from 'shared/util/router';
import {SubscriptionNames} from 'shared/util/subscriptions';
import {useFetchProjects} from 'shared/hooks/useProjects';

const mockBusinessProject = new Project(
	data.mockProject(123, {
		faroSubscription: fromJS(
			data.mockSubscription({
				name: SubscriptionNames.LiferayAnalyticsCloudBusiness
			})
		),
		name: 'Project A'
	})
);

const mockBasicSubscription = data.mockSubscription({
	name: SubscriptionNames.LiferayAnalyticsCloudBasic
});

jest.mock('shared/hooks/useCurrentUser', () => ({
	useCurrentUser: () => ({
		emailAddress: 'test@liferay.com'
	})
}));

jest.mock('shared/hooks/useProjects', () => ({
	useFetchProjects: jest.fn()
}));

jest.unmock('react-dom');

describe('SelectWorkspaceAccount', () => {
	it('should render', () => {
		const mockProjects = [
			new Project(
				data.mockProject(126, {
					faroSubscription: fromJS(mockBasicSubscription),
					groupId: null,
					name: 'Project C'
				})
			),
			new Project(
				data.mockProject(123, {
					faroSubscription: fromJS(mockBasicSubscription),
					groupId: null,
					name: '',
					state: DataSourceStates.Unconfigured
				})
			)
		];

		useFetchProjects.mockImplementation(() => ({
			data: mockProjects,
			loading: false
		}));

		const {getByText} = render(
			<Provider store={mockStore()}>
				<BrowserRouter>
					<SelectWorkspaceAccount />
				</BrowserRouter>
			</Provider>
		);

		jest.runAllTimers();

		expect(getByText(/^select account$/i)).toBeInTheDocument();
	});
});

describe('routingFn', () => {
	it('should route the user to the workspaces list if there are multiple basic workspaces and all are configured', () => {
		const expectedRoute = setUriQueryValue(
			toRoute(Routes.WORKSPACES),
			'allBasicConfigured',
			true
		);

		const mockProjects = [
			new Project(
				data.mockProject(124, {
					faroSubscription: fromJS(mockBasicSubscription),
					name: 'Project B'
				})
			),
			new Project(
				data.mockProject(125, {
					faroSubscription: fromJS(mockBasicSubscription),
					name: 'Project C'
				})
			)
		];

		expect(routingFn({projects: mockProjects})).toBe(expectedRoute);
	});

	it('should route the user to the workspace homepage if there is only one basic tier workspace and it is configured', () => {
		const expectedRoute = toRoute(Routes.WORKSPACE_WITH_ID, {
			groupId: '127'
		});

		const mockProjects = [
			mockBusinessProject,
			new Project(
				data.mockProject(127, {
					faroSubscription: fromJS(mockBasicSubscription),
					name: 'Project B'
				})
			)
		];

		expect(routingFn({projects: mockProjects})).toBe(expectedRoute);
	});

	it('should route the user to the create workspace form if there is only one basic tier workspace and it is NOT configured', () => {
		const corpProjectUuid = 'corpProjectId-124';
		const expectedRoute = toRoute(
			Routes.WORKSPACE_ADD_WITH_CORP_PROJECT_UUID,
			{
				corpProjectUuid
			}
		);

		const mockProjects = [
			mockBusinessProject,
			new Project(
				data.mockProject(0, {
					corpProjectUuid,
					faroSubscription: fromJS(mockBasicSubscription),
					name: ''
				})
			)
		];

		expect(routingFn({projects: mockProjects})).toBe(expectedRoute);
	});
});
