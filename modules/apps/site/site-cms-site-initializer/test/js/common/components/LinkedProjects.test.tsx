/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {render, screen, waitFor} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import {openToast} from 'frontend-js-components-web';
import React from 'react';

import '@testing-library/jest-dom';

import LinkedProjects from '../../../../src/main/resources/META-INF/resources/js/common/components/LinkedProjects';
import ProjectLinkService, {
	CMPProject,
} from '../../../../src/main/resources/META-INF/resources/js/common/services/ProjectLinkService';

jest.mock('frontend-js-components-web', () => ({
	openToast: jest.fn(),
}));

const IN_PROGRESS = {key: 'inProgress', name: 'In Progress'};

const GOV_DIGITAL: CMPProject = {
	dueDate: '2999-12-21',
	id: 1,
	scopeKey: 'PROJECT-1',
	state: IN_PROGRESS,
	title: 'GOV Digital',
};

const TECH_LEADERS: CMPProject = {
	dueDate: '2999-06-12',
	id: 2,
	linkId: 12,
	scopeKey: 'PROJECT-2',
	state: IN_PROGRESS,
	title: 'TechLeaders Summit',
};

const IDENTITY = {
	entryClassName: 'com.example.Content',
	entryExternalReferenceCode: 'ASSET-1',
	entryGroupExternalReferenceCode: 'SPACE-1',
};

function mockService({
	linked = [],
	projects = [],
}: {
	linked?: CMPProject[];
	projects?: CMPProject[];
}) {
	jest.spyOn(ProjectLinkService, 'getLinkedTasks').mockResolvedValue({
		data: {1: [{id: 101, title: 'Review Blog Post'}]},
		error: null,
	});
	jest.spyOn(ProjectLinkService, 'getProjectAssetLinks').mockResolvedValue({
		data: linked.map((project) => ({
			id: project.linkId,
			projectId: project.id,
		})),
		error: null,
	});
	jest.spyOn(ProjectLinkService, 'getProjects').mockResolvedValue({
		data: [...projects, ...linked],
		error: null,
	});
	jest.spyOn(ProjectLinkService, 'linkProject').mockResolvedValue({
		data: {id: 99},
		error: null,
	});
	jest.spyOn(ProjectLinkService, 'unlinkProject').mockResolvedValue({
		data: null,
		error: null,
	});
}

describe('LinkedProjects', () => {
	afterEach(() => {
		jest.clearAllMocks();
		jest.restoreAllMocks();
	});

	it('does not offer an already linked project in the picker', async () => {
		mockService({linked: [TECH_LEADERS], projects: [GOV_DIGITAL]});

		render(<LinkedProjects {...IDENTITY} />);

		await userEvent.click(
			await screen.findByRole('combobox', {name: 'projects'})
		);

		expect(
			await screen.findByRole('option', {name: 'GOV Digital'})
		).toBeInTheDocument();
		expect(
			screen.queryByRole('option', {name: 'TechLeaders Summit'})
		).not.toBeInTheDocument();
	});

	it('links a project when it is selected from the picker', async () => {
		mockService({linked: [], projects: [GOV_DIGITAL]});

		render(<LinkedProjects {...IDENTITY} />);

		await userEvent.click(
			await screen.findByRole('combobox', {name: 'projects'})
		);

		await userEvent.click(await screen.findByText('GOV Digital'));

		expect(ProjectLinkService.linkProject).toHaveBeenCalledWith(
			expect.objectContaining({
				entryExternalReferenceCode: 'ASSET-1',
				project: expect.objectContaining({id: 1}),
			})
		);

		expect(await screen.findByText('GOV Digital')).toBeInTheDocument();
	});

	it('removes a linked project when its remove button is clicked', async () => {
		mockService({linked: [GOV_DIGITAL, TECH_LEADERS]});

		render(<LinkedProjects {...IDENTITY} />);

		await userEvent.click((await screen.findAllByLabelText('remove'))[1]);

		expect(ProjectLinkService.unlinkProject).toHaveBeenCalledWith(
			expect.objectContaining({linkId: 12})
		);

		await waitFor(() =>
			expect(
				screen.queryByText('TechLeaders Summit')
			).not.toBeInTheDocument()
		);
	});

	it('renders a card for each linked project', async () => {
		mockService({linked: [GOV_DIGITAL, TECH_LEADERS]});

		render(<LinkedProjects {...IDENTITY} />);

		expect(await screen.findByText('GOV Digital')).toBeInTheDocument();
		expect(screen.getByText('TechLeaders Summit')).toBeInTheDocument();
	});

	it('restores the card when unlinking fails', async () => {
		mockService({linked: [TECH_LEADERS]});

		jest.spyOn(ProjectLinkService, 'unlinkProject').mockResolvedValue({
			data: null,
			error: 'error-message',
		});

		render(<LinkedProjects {...IDENTITY} />);

		await userEvent.click(await screen.findByLabelText('remove'));

		await waitFor(() =>
			expect(openToast).toHaveBeenCalledWith({
				message: 'error-message',
				type: 'danger',
			})
		);

		expect(screen.getByText('TechLeaders Summit')).toBeInTheDocument();
	});

	it('shows an error and removes the card when linking fails', async () => {
		mockService({linked: [], projects: [GOV_DIGITAL]});

		jest.spyOn(ProjectLinkService, 'linkProject').mockResolvedValue({
			data: null,
			error: 'error-message',
		});

		render(<LinkedProjects {...IDENTITY} />);

		await userEvent.click(
			await screen.findByRole('combobox', {name: 'projects'})
		);

		await userEvent.click(await screen.findByText('GOV Digital'));

		await waitFor(() =>
			expect(openToast).toHaveBeenCalledWith({
				message: 'error-message',
				type: 'danger',
			})
		);

		expect(screen.queryByText('GOV Digital')).not.toBeInTheDocument();
	});

	it('shows an overdue badge for a past-due project that is not done', async () => {
		mockService({
			linked: [
				{
					dueDate: '2000-01-01',
					id: 3,
					linkId: 13,
					state: IN_PROGRESS,
					title: 'Devcon',
				},
			],
		});

		render(<LinkedProjects {...IDENTITY} />);

		expect(await screen.findByText('overdue')).toBeInTheDocument();
	});

	it('shows the project tasks when the card is expanded', async () => {
		mockService({linked: [GOV_DIGITAL]});

		render(<LinkedProjects {...IDENTITY} />);

		await userEvent.click(await screen.findByLabelText('expand'));

		expect(await screen.findByText('Review Blog Post')).toBeInTheDocument();
	});
});
