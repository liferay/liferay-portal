/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {fireEvent, render, screen} from '@testing-library/react';
import React from 'react';

import '@testing-library/jest-dom/extend-expect';

import {useSelectItem} from '../../../../../../src/main/resources/META-INF/resources/page_editor/app/contexts/ControlsContext';
import {
	useEditableProcessorUniqueId,
	useSetEditableProcessorUniqueId,
} from '../../../../../../src/main/resources/META-INF/resources/page_editor/app/contexts/EditableProcessorContext';
import {StoreContextProvider} from '../../../../../../src/main/resources/META-INF/resources/page_editor/app/contexts/StoreContext';
import PageContent from '../../../../../../src/main/resources/META-INF/resources/page_editor/plugins/page_content/components/PageContent';

jest.mock(
	'../../../../../../src/main/resources/META-INF/resources/page_editor/app/contexts/ControlsContext',
	() => {
		const selectItem = jest.fn();
		const hoverItem = jest.fn();
		const hoveredItemId = null;

		return {
			useHoverItem: () => hoverItem,
			useHoveredItemId: () => hoveredItemId,
			useSelectItem: () => selectItem,
		};
	}
);

jest.mock(
	'../../../../../../src/main/resources/META-INF/resources/page_editor/app/contexts/EditableProcessorContext'
);

const contents = [
	{
		actions: {
			editURL: 'editURL',
			permissionsURL: 'permissionsURL',
			viewUsagesURL: 'viewUsagesURL',
		},
		classNameId: '00000',
		classPK: '11111',
		subtype: 'Web Content Article',
		title: 'Test Web Content',
	},
	{
		actions: {
			addItems: [
				{
					href: 'URL',
					label: 'Basic Web Content to be added',
				},
			],
			editURL: 'editURL',
			otherAction: 'other-action',
			permissionsURL: 'permissionsURL',
			viewItemsURL: 'viewItemsURL',
			viewUsagesURL: 'viewUsagesURL',
		},
		classNameId: '00001',
		classPK: '11112',
		subtype: 'Collection',
		title: 'Test Collection',
	},
	{
		actions: {
			editImage: {
				editImageURL: '/editImageURL',
				fileEntryId: '40571',
				previewURL: '/previewURL',
			},
		},
		classNameId: '00002',
		classPK: '40571',
		subtype: 'Basic Document',
		title: 'image.png',
		type: 'Document',
	},
];

const inlineText = {
	editableId: '11113-element-text',
	title: 'Heading Example',
};

const renderPageContent = (
	{content = contents[0], hasUpdatePermissions = true} = {
		content: contents[0],
		hasUpdatePermissions: true,
	}
) =>
	render(
		<StoreContextProvider
			initialState={{
				layoutData: {items: {}},
				permissions: {
					UPDATE: hasUpdatePermissions,
					UPDATE_LAYOUT_CONTENT: true,
				},
			}}
		>
			<PageContent {...content} />
		</StoreContextProvider>
	);

describe('PageContent', () => {
	useSetEditableProcessorUniqueId.mockImplementation(() => jest.fn);

	it('shows properly the title of the content', () => {
		renderPageContent();

		expect(screen.getByText('Test Web Content')).toBeInTheDocument();
	});

	it('shows properly the content subtype', () => {
		renderPageContent();

		expect(screen.getByText('Test Web Content')).toBeInTheDocument();
	});

	it('shows all expected editing actions in dropdown menu', () => {
		const shownActions = [
			'edit',
			'permissions',
			'add-items',
			'view-items',
			'view-usages',
		];
		renderPageContent({content: contents[1]});

		fireEvent.click(screen.getByTitle('open-actions-menu'));

		shownActions.forEach((action) => {
			expect(screen.queryByText(action)).toBeInTheDocument();
		});
		expect(screen.queryByText('other-action')).not.toBeInTheDocument();
	});

	it('shows all items to be added when the Add Item action is clicked', () => {
		renderPageContent({content: contents[1]});

		fireEvent.click(screen.getByTitle('open-actions-menu'));
		fireEvent.click(screen.queryByText('add-items'));

		expect(
			screen.queryByText('Basic Web Content to be added')
		).toBeInTheDocument();
	});

	it('open image editor modal when the Edit Image action is clicked', () => {
		const {baseElement} = renderPageContent({content: contents[2]});

		fireEvent.click(screen.getByTitle('open-actions-menu'));
		fireEvent.click(screen.getByText('edit-image'));

		expect(
			baseElement.querySelector('.image-editor-modal')
		).toBeInTheDocument();
	});

	it('shows the edit button if the content is inline text', () => {
		renderPageContent({content: inlineText});

		expect(
			screen.getByLabelText('edit-inline-text-Heading Example')
		).toBeInTheDocument();
	});

	it('selects the corresponding element on the page when inline text item is clicked', () => {
		const selectItem = useSelectItem();
		renderPageContent({content: inlineText});

		fireEvent.click(screen.getByLabelText(`select ${inlineText.title}`));

		expect(selectItem).toHaveBeenCalledWith('11113-element-text', {
			itemType: 'editable',
			origin: 'sidebar',
		});
	});

	it('disables edit button when an inline text is being edited', () => {
		useEditableProcessorUniqueId.mockImplementation(
			() => '11113-element-text'
		);

		renderPageContent({content: inlineText});

		expect(
			screen.getByLabelText('edit-inline-text-Heading Example')
		).toBeDisabled();
	});

	it('disables edit button when user has no update permission', () => {
		renderPageContent({content: inlineText, hasUpdatePermissions: false});

		expect(
			screen.getByLabelText('edit-inline-text-Heading Example')
		).toBeDisabled();
	});
});
