/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {render, screen} from '@testing-library/react';
import React from 'react';

import '@testing-library/jest-dom';

import ProjectCard from '../../../../src/main/resources/META-INF/resources/js/common/components/ProjectCard';
import {CMPProject} from '../../../../src/main/resources/META-INF/resources/js/common/services/ProjectLinkService';

const PROJECT: CMPProject = {
	id: 7,
	title: 'GOV Digital',
};

describe('ProjectCard', () => {
	it('links a task to its view URL and id', () => {
		render(
			<ProjectCard
				expanded
				onRemove={jest.fn()}
				onToggleTasks={jest.fn()}
				project={PROJECT}
				taskViewURL="/task"
				tasks={[{id: 42, title: 'Review Blog Post'}]}
			/>
		);

		expect(
			screen.getByRole('link', {name: 'Review Blog Post'})
		).toHaveAttribute('href', '/task/42');
	});

	it('links the project to its view URL and id', () => {
		render(
			<ProjectCard
				onRemove={jest.fn()}
				project={PROJECT}
				projectViewURL="/project"
			/>
		);

		expect(screen.getByRole('link', {name: 'GOV Digital'})).toHaveAttribute(
			'href',
			'/project/7'
		);
	});

	it('renders the project title as plain text without a view URL', () => {
		render(<ProjectCard onRemove={jest.fn()} project={PROJECT} />);

		expect(
			screen.queryByRole('link', {name: 'GOV Digital'})
		).not.toBeInTheDocument();
		expect(screen.getByText('GOV Digital')).toBeInTheDocument();
	});
});
