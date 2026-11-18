import * as data from 'test/data';
import * as Router from 'shared/util/router';
import mockStore from 'test/mock-store';
import React from 'react';
import {AddWorkspace, routingFn} from '../AddWorkspace';
import {BrowserRouter} from 'react-router-dom';
import {cleanup, render} from '@testing-library/react';
import {fromJS} from 'immutable';
import {Project} from 'shared/util/records';
import {Provider} from 'react-redux';
import {Routes, toRoute} from 'shared/util/router';

Router.navigate = jest.fn();

const mockConfiguredProject = new Project(fromJS(data.mockProject(23)));

const mockUnconfiguredProject = new Project(
	fromJS(data.mockProject(0, {name: ''}))
);

jest.unmock('react-dom');

jest.mock('shared/components/workspaces/AddWorkspaceForm', () => () => null);

describe('AddWorkspace', () => {
	afterEach(cleanup);
	it('should render', () => {
		const {getByText} = render(
			<Provider store={mockStore()}>
				<BrowserRouter>
					<AddWorkspace
						history={{}}
						project={mockUnconfiguredProject}
					/>
				</BrowserRouter>
			</Provider>
		);

		expect(getByText(/^create workspace$/i)).toBeInTheDocument();
	});
});

describe('routingFn', () => {
	it('should route the user to the workspace home page if the project is already configred', () => {
		const expectedRoute = toRoute(Routes.WORKSPACE_WITH_ID, {
			groupId: '23'
		});

		expect(routingFn({project: mockConfiguredProject})).toBe(expectedRoute);
	});
});
