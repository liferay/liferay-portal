/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {render, screen} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import React from 'react';

import SidebarPanel from '../../../../../../../src/main/resources/META-INF/resources/designer/js/definition-builder/diagram-builder/components/sidebar/SidebarPanel';

const getPanelBody = (toggle) =>
	document.getElementById(toggle.getAttribute('aria-controls'));

describe('The SidebarPanel component should', () => {
	it('Collapse and expand the panel with the keyboard', async () => {
		const user = userEvent.setup();

		render(
			<SidebarPanel panelTitle="information">
				<p>panel body</p>
			</SidebarPanel>
		);

		const toggle = screen.getByRole('button', {name: 'information'});

		await user.tab();

		expect(toggle).toHaveFocus();

		await user.keyboard('{Enter}');

		expect(toggle).toHaveAttribute('aria-expanded', 'false');
		expect(getPanelBody(toggle)).toHaveClass('collapse');

		await user.keyboard(' ');

		expect(toggle).toHaveAttribute('aria-expanded', 'true');
		expect(getPanelBody(toggle)).not.toHaveClass('collapse');
	});

	it('Collapse and expand the panel with the mouse', async () => {
		const user = userEvent.setup();

		render(
			<SidebarPanel panelTitle="information">
				<p>panel body</p>
			</SidebarPanel>
		);

		const toggle = screen.getByRole('button', {name: 'information'});

		await user.click(toggle);

		expect(toggle).toHaveAttribute('aria-expanded', 'false');
		expect(getPanelBody(toggle)).toHaveClass('collapse');

		await user.click(toggle);

		expect(toggle).toHaveAttribute('aria-expanded', 'true');
		expect(getPanelBody(toggle)).not.toHaveClass('collapse');
	});

	it('Render the title as an expanded toggle button that controls the panel body', () => {
		render(
			<SidebarPanel panelTitle="information">
				<input aria-label="label" />
			</SidebarPanel>
		);

		const toggle = screen.getByRole('button', {name: 'information'});

		expect(toggle).toHaveAttribute('aria-expanded', 'true');

		const panelBody = getPanelBody(toggle);

		expect(panelBody).toContainElement(
			screen.getByRole('textbox', {name: 'label'})
		);
		expect(panelBody).not.toHaveClass('collapse');
	});
});
