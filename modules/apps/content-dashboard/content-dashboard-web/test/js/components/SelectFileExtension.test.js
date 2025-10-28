/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {render} from '@testing-library/react';
import React from 'react';

import '@testing-library/jest-dom';

import SelectFileExtension from '../../../src/main/resources/META-INF/resources/js/components/SelectFileExtension';

const mockProps = {
	fileExtensionGroups: [
		{
			fileExtensions: [
				{
					fileExtension: 'mpga',
					selected: false,
				},
				{
					fileExtension: 'mp3',
					selected: true,
				},
				{
					fileExtension: 'mp2',
					selected: false,
				},
				{
					fileExtension: 'ogg',
					selected: false,
				},
				{
					fileExtension: 'wav',
					selected: false,
				},
			],
			icon: 'document-multimedia',
			label: 'Audio',
		},
		{
			fileExtensions: [
				{
					fileExtension: 'gif',
					selected: false,
				},
				{
					fileExtension: 'jpg',
					selected: false,
				},
				{
					fileExtension: 'jpeg',
					selected: false,
				},
				{
					fileExtension: 'png',
					selected: false,
				},
			],
			icon: 'document-image',
			label: 'Image',
		},
	],
	itemSelectorSaveEvent:
		'_com_liferay_content_dashboard_web_portlet_ContentDashboardAdminPortlet_selectedFileExtension',
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

describe('SelectFileExtension', () => {
	it('renders a TreeFilter with parent nodes indicating the number of children', () => {
		const {getByRole, getByText, queryByText} = render(
			<SelectFileExtension {...mockProps} />
		);

		const {className} = getByRole('tree');
		expect(className).toContain(
			'treeview show-quick-actions-on-hover treeview-light'
		);

		expect(
			getByText('Audio (5 items)', {exact: false})
		).toBeInTheDocument();
		expect(
			getByText('Image (4 items)', {exact: false})
		).toBeInTheDocument();
		expect(getByText('mp3')).toBeInTheDocument();
		expect(queryByText('gif')).not.toBeInTheDocument();
	});
});
