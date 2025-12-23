/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';

// eslint-disable-next-line
import {checkAccessibility} from '@liferay/layout-js-components-web/test/__lib__/index';
import {fireEvent, render, screen} from '@testing-library/react';
import React from 'react';

import AssetNavigationModalContent from '../../../../src/main/resources/META-INF/resources/js/main_view/modal/asset_navigation_view/AssetNavigationModalContent';
import {addParams} from '../../__mocks__/frontend-js-web';

const ACTIONS = {
	get: {href: '/link-to-get-action'},
	update: {href: '/link-to-update-action'},
	versions: {href: '/link-to-versions'},
};

const DATES = {
	dateCreated: '2025-09-23T12:05:16Z',
	dateModified: '2025-09-23T12:05:16Z',
};

const CREATOR = {
	contentType: 'UserAccount',
	id: 20132,
	name: 'Test Test',
};

const item1 = {
	...DATES,
	actions: ACTIONS,
	embedded: {
		...DATES,
		content: 'asd',
		creator: {
			...CREATOR,
			externalReferenceCode: '38e08113-4166-162b-5d2c-9ec3a68aabb2',
		},
		externalReferenceCode: 'cd9c3564-80ad-8d3f-300f-76659955e96d',
		file: {
			externalReferenceCode: '7468ba44-4db5-760b-cac4-1c56b9041a86',
			id: 36530,
			link: {
				href: '/documents/36523/36525/liferay+icon.png/7468ba44-4db5-760b-cac4-1c56b9041a86?version=1.0&t=1757930508059&download=true&groupExternalReferenceCode=5e059871-285a-5a19-46a7-27127417ad9f&objectDefinitionExternalReferenceCode=L_CMS_BASIC_DOCUMENT&objectEntryExternalReferenceCode=cd9c3564-80ad-8d3f-300f-76659955e96d',
				label: 'liferay icon.png',
			},
			mimeType: 'image/png',
			name: 'liferay icon.png',
			thumbnailURL:
				'/documents/36523/36525/liferay+icon.png/7468ba44-4db5-760b-cac4-1c56b9041a86?version=1.0&t=1757930508059&imageThumbnail=1',
		},
		id: 36535,
		objectEntryFolderExternalReferenceCode: 'L_FILES',
		objectEntryFolderId: 35400,
		scopeId: 35393,
		scopeKey: 'Default',
		title: 'liferay icon.png',
	},
	entryClassName: 'com.liferay.object.model.ObjectDefinition#Z7P5',
	id: 36535,
	score: 0,
	title: 'liferay icon.png',
};

const item2 = {
	...DATES,
	actions: ACTIONS,
	embedded: {
		...DATES,
		content: '<p>asd</p>',
		creator: CREATOR,
		externalReferenceCode: 'aa35cc56-67ec-44f4-23bf-2f2ea3799b21',
		id: 36599,
		objectEntryFolderExternalReferenceCode:
			'2fefc7c4-f2e1-ffd2-f255-7a6e8fa496f1',
		objectEntryFolderId: 36592,
		scopeId: 35393,
		scopeKey: 'Default',
		title: 'KB Article',
	},
	entryClassName: 'com.liferay.object.model.ObjectDefinition#H6H2',
	id: 36599,
	score: 0,
	title: 'KB Article',
};

const item3 = {
	...DATES,
	actions: ACTIONS,
	embedded: {
		...DATES,
		content: '<p>Third content</p>',
		creator: CREATOR,
		externalReferenceCode: 'aa35cc56-67ec-44f4-23bf-2f2ea3799b21',
		id: 36590,
		objectEntryFolderExternalReferenceCode:
			'2fefc7c4-f2e1-ffd2-f255-7a6e8fa496f1',
		objectEntryFolderId: 36592,
		scopeId: 35393,
		scopeKey: 'Default',
		title: 'First Blog',
	},
	entryClassName: 'com.liferay.object.model.ObjectDefinition#H6H2',
	id: 36590,
	score: 0,
	title: 'First Blog',
};

const sharingItem = {
	...item3,
	actionIds: ['VIEW'],
};

const DEFAULT_PROPS = {
	additionalProps: {
		assetLibraries: [{groupId: 35393, name: 'Default'}],
		cmsGroupId: 123,
		commentsProps: {
			addCommentURL: '/my-random-add-url',
			comments: [],
			deleteCommentURL: '',
			editCommentURL: '',
			editorConfig: {},
			getCommentsURL: '',
		},
	},
	contentViewURL: '/my-random-content-view',
	currentIndex: 0,
	items: [item1, item2, item3],
};

const renderComponent = (props = DEFAULT_PROPS) => {
	return render(<AssetNavigationModalContent {...(props as any)} />);
};

const renderComponentInSharing = (props = DEFAULT_PROPS) => {
	return render(
		<AssetNavigationModalContent
			{...(props as any)}
			showInfoPanel={false}
		/>
	);
};

describe('AssetNavigationModalContent', () => {
	it('checks the accessibility of the item navigation component', async () => {
		const {container} = renderComponent();

		await checkAccessibility({bestPractices: true, context: container});
	});

	it('renders with the first item with the download link', () => {
		renderComponent();

		expect(
			document.querySelector(
				'.modal-title [data-testid="modal-header-name"]'
			)?.textContent
		).toEqual('liferay icon.png');
		expect(
			screen.getByRole('link', {name: 'download'})
		).toBeInTheDocument();
		expect(
			document.querySelector(
				'.asset-navigation-container > div:not(.c-slideout) img.preview-file-image'
			)
		).toBeInTheDocument();
	});

	it('navigates to next item when right arrow key is pressed', () => {
		const {getByLabelText} = renderComponent();

		fireEvent.click(getByLabelText('next'));

		expect(screen.getByText('KB Article')).toBeInTheDocument();
	});

	it('wraps around to last item when pressing previous on first item', () => {
		const {getByLabelText} = renderComponent();

		fireEvent.click(getByLabelText('previous'));

		expect(screen.getByText('First Blog')).toBeInTheDocument();
	});

	it('arrows are hidden if there is only one element', () => {
		renderComponent({
			...DEFAULT_PROPS,
			items: [item1],
		});

		expect(
			screen.queryByRole('button', {name: /previous/i})
		).not.toBeInTheDocument();
		expect(
			screen.queryByRole('button', {name: /next/i})
		).not.toBeInTheDocument();
	});

	it('can see info panel', () => {
		const {getByLabelText} = renderComponent();

		fireEvent.click(getByLabelText('show-details'));

		expect(
			screen.queryByRole('tab', {name: /details/i})
		).toBeInTheDocument();

		expect(screen.getByText('Test Test')).toBeInTheDocument();
	});

	it('can see comments panel', () => {
		addParams.mockReturnValue('/my-random-add-url?someParams');

		const {getByLabelText} = renderComponent();

		fireEvent.click(getByLabelText('show-comments'));

		expect(screen.getByText('add-comment')).toBeInTheDocument();
	});

	describe('Sharing items', () => {
		it('info panel is hidden', () => {
			const {getByLabelText, queryByText} = renderComponentInSharing();

			expect(queryByText('show-details')).not.toBeInTheDocument();
			expect(getByLabelText('show-comments')).toBeInTheDocument();
		});

		it('comment panel is hidden if sharing item has no permission', () => {
			const {getByLabelText, queryByText} = renderComponentInSharing({
				...DEFAULT_PROPS,
				items: [item1, item2, sharingItem],
			});

			expect(getByLabelText('show-comments')).toBeInTheDocument();

			fireEvent.click(getByLabelText('previous'));

			expect(queryByText('show-comments')).not.toBeInTheDocument();

			fireEvent.click(getByLabelText('previous'));

			expect(getByLabelText('show-comments')).toBeInTheDocument();
		});
	});
});
