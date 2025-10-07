/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom/extend-expect';
import {render, screen} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import React from 'react';

import ObjectEntryFooter from '../../../object_entries/object_entry/ObjectEntryFooter';

function renderObjectEntryFooter() {
	return render(
		<ObjectEntryFooter backURL="" portletNamespace="" submitRef="" />
	);
}

describe('ObjectEntryFooter component', () => {
	beforeAll(() => {
		global.Liferay = {
			...global.Liferay,
			FeatureFlags: {
				...global.Liferay?.FeatureFlags,
				'LPD-17564': true,
			},
		};
	});

	it('renders publish and cancel buttons', async () => {
		renderObjectEntryFooter();

		expect(
			screen.getByRole('button', {name: 'publish'})
		).toBeInTheDocument();

		expect(
			screen.getByRole('button', {name: 'cancel'})
		).toBeInTheDocument();
	});

	it('renders publish button with caret-bottom symbol', async () => {
		renderObjectEntryFooter();

		const publishButton = screen.getByRole('button', {name: 'publish'});

		const caretBottomIcon = publishButton.querySelector(
			'.lfr-object__entries-schedule-panel-publish-icon'
		);

		expect(caretBottomIcon).toBeInTheDocument();
	});

	it('renders publishing options dropdown after clicking on publish button', async () => {
		renderObjectEntryFooter();

		const publishButton = screen.getByRole('button', {name: 'publish'});

		expect(publishButton).toHaveAttribute('aria-expanded', 'false');

		await userEvent.click(publishButton);

		expect(publishButton).toHaveAttribute('aria-expanded', 'true');

		expect(
			screen.getByRole('menuitem', {name: 'publish'})
		).toBeInTheDocument();

		expect(
			screen.getByRole('menuitem', {name: 'schedule-publication'})
		).toBeInTheDocument();
	});
});
