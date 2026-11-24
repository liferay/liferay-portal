jest.mock(
	'shared/components/workspaces/SuccessDisplay',
	() => () => 'SuccessDisplay'
);

jest.mock(
	'shared/components/workspaces/ErrorDisplay',
	() =>
		({errorType}) =>
			`WorkspacesErrorDisplay ${errorType}`
);

jest.mock(
	'shared/components/workspaces/ActivatingDisplay',
	() => () => 'ActivatingDisplay'
);

jest.mock('shared/pages/WorkspaceNotFound', () => () => 'WorkspaceNotFound');

import * as API from 'shared/api';
import mockStore from 'test/mock-store';
import ProjectGate from '../ProjectGate';
import React from 'react';
import {Provider} from 'react-redux';
import {ProjectStates} from 'shared/util/constants';
import {render, screen} from '@testing-library/react';

const renderGate = (groupId) =>
	render(
		<Provider store={mockStore()}>
			<ProjectGate groupId={groupId}>{'workspace page'}</ProjectGate>
		</Provider>
	);

describe('ProjectGate', () => {
	it.each([
		['23', 'ready'],
		['27', 'scheduled'],
	])('renders the page for the %s workspace (%s)', (groupId) => {
		renderGate(groupId);

		expect(screen.getByText('workspace page')).toBeInTheDocument();
	});

	it('renders the success display while the workspace is being created', () => {
		renderGate('24');

		expect(screen.getByText('SuccessDisplay')).toBeInTheDocument();
	});

	it.each([
		['25', ProjectStates.Maintenance],
		['26', ProjectStates.Unavailable],
		['29', ProjectStates.Deactivated],
	])('renders the error display for the %s workspace (%s)', (groupId, state) => {
		renderGate(groupId);

		expect(
			screen.getByText(`WorkspacesErrorDisplay ${state}`)
		).toBeInTheDocument();
	});

	it('renders the activating display while the workspace is activating', () => {
		renderGate('28');

		expect(screen.getByText('ActivatingDisplay')).toBeInTheDocument();
	});

	it('renders WorkspaceNotFound when the workspace cannot be loaded', async () => {
		API.projects.fetch.mockReturnValueOnce(
			Promise.reject({message: 'foo rejection from server'})
		);

		renderGate('23');

		expect(
			await screen.findByText('WorkspaceNotFound')
		).toBeInTheDocument();
	});
});
