/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {render, screen} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import React from 'react';

import FrontendDataSetContext from '../../../src/main/resources/META-INF/resources/FrontendDataSetContext';
import CreationMenu from '../../../src/main/resources/META-INF/resources/management_bar/controls/CreationMenu';

describe('CreationMenu', () => {
	const renderCreationMenu = (primaryItems) =>
		render(
			<FrontendDataSetContext.Provider value={{loadData: jest.fn()}}>
				<CreationMenu
					inEmptyState={false}
					primaryItems={primaryItems}
				/>
			</FrontendDataSetContext.Provider>
		);

	it('forwards the item className to the dropdown item', async () => {
		renderCreationMenu([
			{className: 'cms-generate-with-ai', label: 'Generate'},
			{label: 'Create Folder'},
		]);

		await userEvent.click(screen.getByTestId('fdsCreationActionButton'));

		expect(screen.getByText('Generate')).toHaveClass(
			'cms-generate-with-ai'
		);
	});

	it('forwards the item className to the single creation button', () => {
		renderCreationMenu([
			{className: 'cms-generate-with-ai', label: 'Generate'},
		]);

		expect(screen.getByText('Generate')).toHaveClass(
			'cms-generate-with-ai'
		);
	});
});
