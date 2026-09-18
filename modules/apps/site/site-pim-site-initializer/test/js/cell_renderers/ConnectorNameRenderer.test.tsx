/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {render, screen} from '@testing-library/react';
import React from 'react';

import ConnectorNameRenderer from '../../../src/main/resources/META-INF/resources/js/cell_renderers/ConnectorNameRenderer';

jest.mock('@liferay/frontend-data-set-web', () => ({
	getItemActionURL: (actions: any[], actionId: string, itemData: any) => {
		const action = actions.find((action) => action?.data?.id === actionId);

		if (!action) {
			return '';
		}

		return action.href.replace('{id}', itemData.id);
	},
}));

const ACTIONS = [
	{
		data: {id: 'edit'},
		href: '/edit-connector?objectEntryId={id}',
	},
	{
		data: {id: 'fieldMapping'},
		href: '/field-mapping?objectEntryId={id}',
	},
];

describe('ConnectorNameRenderer', () => {
	it('links the connector name to the field mapping page when the user can update', () => {
		render(
			<ConnectorNameRenderer
				actions={ACTIONS}
				itemData={{actions: {update: {}}, id: '42'}}
				value="Ushio Commerce"
			/>
		);

		expect(
			screen.getByRole('link', {name: 'Ushio Commerce'})
		).toHaveAttribute('href', '/field-mapping?objectEntryId=42');
	});

	it('renders the connector name as plain text when the user cannot update', () => {
		render(
			<ConnectorNameRenderer
				actions={ACTIONS}
				itemData={{actions: {}, id: '42'}}
				value="Ushio Commerce"
			/>
		);

		expect(screen.queryByRole('link')).not.toBeInTheDocument();
		expect(screen.getByText('Ushio Commerce')).toBeInTheDocument();
	});
});
