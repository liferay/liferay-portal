/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {
	fireEvent,
	render,
	screen,
	waitFor,
	within,
} from '@testing-library/react';
import React, {useState} from 'react';

import ProjectLinkService from '../../../../src/main/resources/META-INF/resources/js/common/services/ProjectLinkService';
import {
	ProjectOption,
	ProjectPicker,
	initialProject,
} from '../../../../src/main/resources/META-INF/resources/js/main_view/dashboard/common/ProjectPicker';

jest.mock(
	'../../../../src/main/resources/META-INF/resources/js/common/services/ProjectLinkService'
);

const mockedProjectLinkService = ProjectLinkService as jest.Mocked<
	typeof ProjectLinkService
>;

const WrappedComponent = () => {
	const [project, setProject] = useState<ProjectOption>(initialProject);

	return (
		<ProjectPicker
			cmpProjectObjectDefinitionId={42}
			onSelectProject={setProject}
			selectedProject={project}
		/>
	);
};

describe('[CMS Dashboard] Components: ProjectPicker', () => {
	const mockProjects = [
		{id: 1, title: 'project 01'},
		{id: 2, title: 'project 02'},
	];

	beforeEach(() => {
		jest.clearAllMocks();

		mockedProjectLinkService.getProjects.mockResolvedValue({
			data: mockProjects,
			error: null,
		});
	});

	it('fetches the projects of the CMP project object definition', async () => {
		render(<WrappedComponent />);

		await waitFor(() =>
			expect(mockedProjectLinkService.getProjects).toHaveBeenCalledWith(
				expect.objectContaining({cmpProjectObjectDefinitionId: 42})
			)
		);
	});

	it('renders the selected project in the trigger', () => {
		render(<WrappedComponent />);

		expect(
			screen.getByRole('combobox', {name: 'filter-by-projects'})
		).toHaveTextContent('all-projects');
	});

	it('renders the project list', async () => {
		render(<WrappedComponent />);

		fireEvent.click(
			screen.getByRole('combobox', {name: 'filter-by-projects'})
		);

		const listbox = await screen.findByRole('listbox');

		expect(
			await within(listbox).findByRole('option', {name: 'project 01'})
		).toBeInTheDocument();

		expect(
			within(listbox).getByRole('option', {name: 'project 02'})
		).toBeInTheDocument();

		expect(
			within(listbox).getByRole('option', {name: 'all-projects'})
		).toBeInTheDocument();

		expect(within(listbox).getAllByRole('option')).toHaveLength(3);
	});

	it('filters the list when searching', async () => {
		render(<WrappedComponent />);

		fireEvent.click(
			screen.getByRole('combobox', {name: 'filter-by-projects'})
		);

		const listbox = await screen.findByRole('listbox');

		await within(listbox).findByRole('option', {name: 'project 02'});

		fireEvent.change(screen.getByPlaceholderText('search'), {
			target: {value: 'project 02'},
		});

		await waitFor(() =>
			expect(within(listbox).getAllByRole('option')).toHaveLength(1)
		);

		expect(
			within(listbox).getByRole('option', {name: 'project 02'})
		).toBeInTheDocument();
	});

	it('selects a new project', async () => {
		render(<WrappedComponent />);

		const trigger = screen.getByRole('combobox', {
			name: 'filter-by-projects',
		});

		fireEvent.click(trigger);

		const listbox = await screen.findByRole('listbox');

		fireEvent.click(
			await within(listbox).findByRole('option', {name: 'project 02'})
		);

		await waitFor(() => expect(trigger).toHaveTextContent('project 02'));
	});

	it('keeps only the all projects option when the request fails', async () => {
		mockedProjectLinkService.getProjects.mockResolvedValue({
			data: null,
			error: 'an-unexpected-error-occurred',
		});

		render(<WrappedComponent />);

		fireEvent.click(
			screen.getByRole('combobox', {name: 'filter-by-projects'})
		);

		const listbox = await screen.findByRole('listbox');

		expect(
			await within(listbox).findByRole('option', {name: 'all-projects'})
		).toBeInTheDocument();

		expect(within(listbox).getAllByRole('option')).toHaveLength(1);
	});
});
