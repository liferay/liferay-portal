/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {render, screen, waitFor} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import React from 'react';

import '@testing-library/jest-dom';

import ProjectAutocomplete from '../../../../src/main/resources/META-INF/resources/js/common/components/ProjectAutocomplete';
import {CMPProject} from '../../../../src/main/resources/META-INF/resources/js/common/services/ProjectLinkService';

const IN_PROGRESS = {key: 'inProgress', name: 'In Progress'};

const GOV_DIGITAL: CMPProject = {
	id: 1,
	scopeKey: 'PROJECT-1',
	state: IN_PROGRESS,
	title: 'GOV Digital',
};

const TECH_LEADERS: CMPProject = {
	id: 2,
	scopeKey: 'PROJECT-2',
	state: IN_PROGRESS,
	title: 'TechLeaders Summit',
};

const PROJECTS = [GOV_DIGITAL, TECH_LEADERS];

function renderComponent(onSelect = jest.fn()) {
	render(
		<ProjectAutocomplete
			ariaLabel="projects"
			onSelect={onSelect}
			projects={PROJECTS}
		/>
	);

	return {
		input: screen.getByRole('combobox', {name: 'projects'}),
		onSelect,
	};
}

describe('ProjectAutocomplete', () => {
	const {ResizeObserver: ResizeObserverOriginal} = window;

	beforeAll(() => {
		window.ResizeObserver = jest.fn().mockImplementation(() => ({
			disconnect: jest.fn(),
			observe: jest.fn(),
			unobserve: jest.fn(),
		}));
	});

	afterAll(() => {
		window.ResizeObserver = ResizeObserverOriginal;
	});

	it('calls onSelect with the project that was clicked', async () => {
		const {input, onSelect} = renderComponent();

		await userEvent.click(input);

		await userEvent.click(
			await screen.findByRole('option', {name: /GOV Digital/})
		);

		expect(onSelect).toHaveBeenCalledWith(GOV_DIGITAL);
	});

	it('clears the input after a project is selected', async () => {
		const {input} = renderComponent();

		await userEvent.type(input, 'GOV', {delay: null});

		await userEvent.click(
			await screen.findByRole('option', {name: /GOV Digital/})
		);

		await waitFor(() => expect(input).toHaveValue(''));
	});

	it('offers every project when the input is focused', async () => {
		const {input} = renderComponent();

		await userEvent.click(input);

		await waitFor(() =>
			expect(screen.getAllByRole('option')).toHaveLength(PROJECTS.length)
		);
	});

	it('shows only the projects matching the typed text', async () => {
		const {input} = renderComponent();

		await userEvent.type(input, 'tech', {delay: null});

		expect(
			await screen.findByRole('option', {name: /TechLeaders Summit/})
		).toBeInTheDocument();

		expect(
			screen.queryByRole('option', {name: /GOV Digital/})
		).not.toBeInTheDocument();
	});
});
