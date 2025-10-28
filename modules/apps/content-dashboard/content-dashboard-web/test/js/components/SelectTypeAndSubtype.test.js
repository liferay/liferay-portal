/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {render} from '@testing-library/react';
import React from 'react';

import '@testing-library/jest-dom';

import SelectTypeAndSubtype from '../../../src/main/resources/META-INF/resources/js/components/SelectTypeAndSubtype';

const mockProps = {
	contentDashboardItemTypes: [
		{
			icon: 'web-content',
			itemSubtypes: [
				{
					className:
						'com.liferay.dynamic.data.mapping.model.DDMStructure',
					classPK: '40873',
					label: 'Basic Web Content',
					selected: false,
				},
			],
			label: 'Web Content Article',
		},
		{
			icon: 'documents-and-media',
			itemSubtypes: [
				{
					className:
						'com.liferay.document.library.kernel.model.DLFileEntryType',
					classPK: '0',
					label: 'Basic Document',
					selected: false,
				},
				{
					className:
						'com.liferay.document.library.kernel.model.DLFileEntryType',
					classPK: '40709',
					label: 'External Video Shortcut',
					selected: false,
				},
				{
					className:
						'com.liferay.document.library.kernel.model.DLFileEntryType',
					classPK: '40761',
					label: 'Google Drive Shortcut',
					selected: false,
				},
			],

			label: 'Document',
		},
	],
	itemSelectorSaveEvent:
		'_com_liferay_content_dashboard_web_portlet_ContentDashboardAdminPortlet_selectedContentDashboardItemSubtype',
	portletNamespace:
		'_com_liferay_item_selector_web_portlet_ItemSelectorPortlet_',
};

jest.mock('frontend-js-web', () => ({
	...jest.requireActual('frontend-js-web'),
	getOpener: jest.fn(() => ({
		Liferay: {
			fire: jest.fn(),
		},
	})),
}));

describe('SelectTypeAndSubtype', () => {
	it('renders a TreeFilter with parent nodes indicating the number of children', () => {
		const {getByRole, getByText, queryByText} = render(
			<SelectTypeAndSubtype {...mockProps} />
		);

		const {className} = getByRole('tree');
		expect(className).toContain(
			'treeview show-quick-actions-on-hover treeview-light'
		);

		expect(
			getByText('Document (3 items)', {exact: false})
		).toBeInTheDocument();
		expect(
			getByText('Web Content Article (1 item)', {exact: false})
		).toBeInTheDocument();
		expect(getByText('Basic Web Content')).toBeInTheDocument();
		expect(queryByText('External Video Shortcut')).not.toBeInTheDocument();
	});
});
