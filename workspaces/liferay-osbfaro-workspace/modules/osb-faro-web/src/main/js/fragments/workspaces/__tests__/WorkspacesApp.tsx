jest.mock('shared/pages/AddWorkspace', () => () => 'AddWorkspace');

jest.mock('settings/pages/OAuthReceive', () => () => 'OAuthReceive');

jest.mock(
	'shared/pages/SelectWorkspaceAccount',
	() => () => 'SelectWorkspaceAccount'
);

jest.mock('shared/pages/Workspaces', () => () => 'Workspaces');

import React from 'react';
import {
	AddWorkspace,
	OAuthReceive,
	SelectWorkspaceAccount,
	Workspaces,
} from '../WorkspacesApp';
import {render, screen} from '@testing-library/react';

jest.unmock('react-dom');

describe('WorkspacesApp', () => {
	it('renders the workspace creation of a project', async () => {
		render(<AddWorkspace params={{corpProjectUuid: '1'}} />);

		expect(await screen.findByText('AddWorkspace')).toBeInTheDocument();
	});

	it('renders the OAuth callback', async () => {
		render(<OAuthReceive />);

		expect(await screen.findByText('OAuthReceive')).toBeInTheDocument();
	});

	it('renders the account selection', async () => {
		render(<SelectWorkspaceAccount />);

		expect(
			await screen.findByText('SelectWorkspaceAccount')
		).toBeInTheDocument();
	});

	it('renders the workspaces list', async () => {
		render(<Workspaces />);

		expect(await screen.findByText('Workspaces')).toBeInTheDocument();
	});
});
