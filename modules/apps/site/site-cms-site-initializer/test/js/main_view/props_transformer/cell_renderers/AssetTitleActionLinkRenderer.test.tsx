/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {render, screen} from '@testing-library/react';
import React from 'react';

import AssetTitleActionLinkRenderer from '../../../../../src/main/resources/META-INF/resources/js/main_view/props_transformer/cell_renderers/AssetTitleActionLinkRenderer';

const testActions = [
	{
		data: {id: 'actionLinkWorkflowTask'},
		href: '/web/cms/tasks/{taskId}',
	},
];

const testItemData = {
	taskId: '123',
};

describe('AssetTitleActionLinkRenderer', () => {
	it('renders the link with the asset title as its accessible name', () => {
		render(
			<AssetTitleActionLinkRenderer
				actions={testActions}
				itemData={testItemData}
				value="Web Content Test"
			/>
		);

		expect(
			screen.getByRole('link', {name: 'Web Content Test'})
		).toHaveAttribute('href', '/web/cms/tasks/123');
	});

	it('renders the value only when there is no matching action', () => {
		render(
			<AssetTitleActionLinkRenderer
				actions={[]}
				itemData={testItemData}
				value="Web Content Test"
			/>
		);

		expect(screen.queryByRole('link')).not.toBeInTheDocument();

		expect(screen.getByText('Web Content Test')).toBeInTheDocument();
	});

	it('falls back to the untitled-asset label when the asset has no title', () => {
		render(
			<AssetTitleActionLinkRenderer
				actions={testActions}
				itemData={testItemData}
				value=""
			/>
		);

		expect(
			screen.getByRole('link', {name: 'untitled-asset'})
		).toHaveAttribute('aria-label', 'untitled-asset');
	});
});
