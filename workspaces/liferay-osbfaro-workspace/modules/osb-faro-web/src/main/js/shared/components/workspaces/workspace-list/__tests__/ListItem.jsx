import * as API from 'shared/api';
import * as data from 'test/data';
import React from 'react';
import WorkspaceListItem from '../ListItem';
import {cleanup, fireEvent, render} from '@testing-library/react';
import {ProjectStates} from 'shared/util/constants';
import {StaticRouter} from 'react-router-dom';
import {waitForLoadingToBeRemoved} from 'test/helpers';

jest.unmock('react-dom');

describe('WorkspaceListItem', () => {
	afterEach(cleanup);

	it('should render', () => {
		const {container} = render(
			<StaticRouter>
				<WorkspaceListItem
					accountName=''
					projectState={ProjectStates.Ready}
				/>
			</StaticRouter>
		);

		expect(container).toMatchSnapshot();
	});

	it('should render a workspace item as enabled if is unavailable and the user clicks the item to reload and it then changes to available', async () => {
		API.projects.fetch.mockImplementationOnce(() =>
			Promise.resolve(data.mockProject('23'))
		);

		const {container, queryByText} = render(
			<StaticRouter>
				<WorkspaceListItem projectState={ProjectStates.Unavailable} />
			</StaticRouter>
		);

		const button = queryByText('Workspace unavailable; click to reload.');

		expect(button).toBeTruthy();

		fireEvent.click(button);

		jest.runAllTimers();

		await waitForLoadingToBeRemoved(container);

		expect(
			queryByText('Workspace unavailable; click to reload.')
		).toBeNull();
	});

	it('should render a workspace with projects you can join', () => {
		const {queryByText} = render(
			<StaticRouter>
				<WorkspaceListItem
					isJoinableProjects
					projectState={ProjectStates.Ready}
				/>
			</StaticRouter>
		);

		expect(queryByText('Request Access')).toBeTruthy();
	});

	it('should render a workspace with a deactivated project', () => {
		const {queryByText} = render(
			<StaticRouter>
				<WorkspaceListItem projectState={ProjectStates.Deactivated} />
			</StaticRouter>
		);
		expect(queryByText('Activate')).toBeTruthy();
	});

	it('should render contact sales and the Analytics Cloud limit message when hasLimitReached and ldpEnabled is false', () => {
		const {getByText} = render(
			<StaticRouter>
				<WorkspaceListItem
					hasLimitReached
					projectState={ProjectStates.Ready}
				/>
			</StaticRouter>
		);

		expect(
			getByText(
				'Access to Analytics Cloud has been restricted because your workspace has reached the known individuals or page view limit. Please contact sales to proceed.'
			)
		).toBeTruthy();

		expect(getByText('Contact Sales')).toBeTruthy();
	});

	it('should render the Liferay Data Platform limit message when hasLimitReached and ldpEnabled is true', () => {
		const {getByText} = render(
			<StaticRouter>
				<WorkspaceListItem
					hasLimitReached
					ldpEnabled
					projectState={ProjectStates.Ready}
				/>
			</StaticRouter>
		);

		expect(
			getByText(
				'Access to Liferay Data Platform has been restricted because your workspace has reached the known individuals or page view limit. Please contact sales to proceed.'
			)
		).toBeTruthy();
	});
});
