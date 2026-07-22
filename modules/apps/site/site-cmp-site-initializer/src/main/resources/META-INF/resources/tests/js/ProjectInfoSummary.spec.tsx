/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {render, screen} from '@testing-library/react';
import React from 'react';

import ProjectInfoSummary from '../../js/components/project/ProjectInfoSummary';

const mockStates = [
	{key: 'notStarted', name: 'Not Started', nextStates: ['inProgress']},
	{key: 'inProgress', name: 'In Progress', nextStates: ['notStarted']},
];

const mockManager = {image: '', name: 'Manager Name'};
const mockSponsor = {image: '', name: 'Sponsor Name'};

describe('ProjectInfoSummary', () => {
	it('renders with props', () => {
		const {getByText} = render(
			<ProjectInfoSummary
				dueDate="2023-12-31"
				funnelStages={[]}
				hasUpdatePermission
				initialState="notStarted"
				manager={mockManager}
				personas={[]}
				projectId="123"
				sponsor={mockSponsor}
				states={mockStates}
				tags={['tag1', 'tag2']}
			/>
		);

		expect(getByText('Dec 31, 2023')).toBeInTheDocument();
		expect(getByText('Manager Name')).toBeInTheDocument();
		expect(getByText('Sponsor Name')).toBeInTheDocument();
		expect(getByText('not-started')).toBeInTheDocument();
		expect(getByText('tag1')).toBeInTheDocument();
		expect(getByText('tag2')).toBeInTheDocument();
	});

	it('renders personas and funnel stages as chips', () => {
		const {getByText} = render(
			<ProjectInfoSummary
				dueDate="2023-12-31"
				funnelStages={['Awareness', 'Consideration']}
				hasUpdatePermission
				initialState="notStarted"
				manager={mockManager}
				personas={['Decision Maker', 'Champion']}
				projectId="123"
				sponsor={mockSponsor}
				states={mockStates}
				tags={[]}
			/>
		);

		expect(getByText('Decision Maker')).toBeInTheDocument();
		expect(getByText('Champion')).toBeInTheDocument();
		expect(getByText('Awareness')).toBeInTheDocument();
		expect(getByText('Consideration')).toBeInTheDocument();
	});

	it('renders empty personas and funnel stages without chips', () => {
		const {queryByText} = render(
			<ProjectInfoSummary
				dueDate="2023-12-31"
				funnelStages={[]}
				hasUpdatePermission
				initialState="notStarted"
				manager={mockManager}
				personas={[]}
				projectId="123"
				sponsor={mockSponsor}
				states={mockStates}
				tags={[]}
			/>
		);

		expect(queryByText('Decision Maker')).not.toBeInTheDocument();
		expect(queryByText('Awareness')).not.toBeInTheDocument();
	});

	it('disables the state selector when the user lacks update permission', () => {
		render(
			<ProjectInfoSummary
				dueDate="2023-12-31"
				funnelStages={[]}
				hasUpdatePermission={false}
				initialState="notStarted"
				manager={mockManager}
				personas={[]}
				projectId="123"
				sponsor={mockSponsor}
				states={mockStates}
				tags={[]}
			/>
		);

		expect(screen.getByRole('combobox')).toHaveClass('disabled');
	});
});
