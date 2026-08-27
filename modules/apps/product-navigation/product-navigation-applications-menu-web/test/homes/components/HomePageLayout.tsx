/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';

// eslint-disable-next-line @liferay/portal/no-cross-module-deep-import
import {checkAccessibility} from '@liferay/layout-js-components-web/test/__lib__/index';
import {render, screen} from '@testing-library/react';
import React from 'react';

import HomePageLayout from '../../../src/main/resources/META-INF/resources/js/homes/components/HomePageLayout';

const UNLABELED_GROUP = {
	id: 'applications_menu.applications',
	items: [
		{
			href: '/announcements',
			id: 'announcements',
			label: 'Announcements and Alerts',
			leadingIcon: 'megaphone',
		},
	],
};

const WORKFLOW_GROUP = {
	id: 'control_panel.workflow',
	items: [
		{
			href: '/process-builder',
			id: 'process-builder',
			label: 'Process Builder',
			leadingIcon: 'flow',
		},
	],
	label: 'Workflow',
};

const renderComponent = (displayType?: 'horizontal' | 'vertical') =>
	render(
		<HomePageLayout
			displayType={displayType}
			icon="/applications.svg"
			items={[UNLABELED_GROUP, WORKFLOW_GROUP]}
			title="Applications"
		/>
	);

describe('HomePageLayout', () => {
	it('renders the title', () => {
		renderComponent();

		expect(
			screen.getByRole('heading', {name: 'Applications'})
		).toBeInTheDocument();
	});

	describe('vertical', () => {
		it('renders a card for every item, grouped or not', () => {
			renderComponent('vertical');

			expect(
				screen.getByText('Announcements and Alerts')
			).toBeInTheDocument();
			expect(screen.getByText('Process Builder')).toBeInTheDocument();
		});

		it('does not render group labels', () => {
			renderComponent('vertical');

			expect(screen.queryByText('Workflow')).not.toBeInTheDocument();
		});

		it('has no accessibility violations', async () => {
			const {container} = renderComponent('vertical');

			await checkAccessibility({bestPractices: true, context: container});
		});
	});

	describe('horizontal', () => {
		it('renders a labeled section for a group that has a label', () => {
			renderComponent('horizontal');

			expect(
				screen.getByRole('group', {name: 'Workflow'})
			).toBeInTheDocument();
			expect(screen.getByText('Workflow')).toBeInTheDocument();
			expect(screen.getByText('Process Builder')).toBeInTheDocument();
		});

		it('renders items of an unlabeled group without a section header', () => {
			renderComponent('horizontal');

			expect(
				screen.getByText('Announcements and Alerts')
			).toBeInTheDocument();
			expect(screen.getAllByRole('group')).toHaveLength(1);
		});

		it('has no accessibility violations', async () => {
			const {container} = renderComponent('horizontal');

			await checkAccessibility({bestPractices: true, context: container});
		});
	});
});
