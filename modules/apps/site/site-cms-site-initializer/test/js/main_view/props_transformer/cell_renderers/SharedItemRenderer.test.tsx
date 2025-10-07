/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom/extend-expect';
import {render, screen, within} from '@testing-library/react';
import React from 'react';

import SharedItemRenderer from '../../../../../src/main/resources/META-INF/resources/js/main_view/props_transformer/cell_renderers/SharedItemRenderer';

const testActionBase = {
	data: {id: 'view'},
	href: 'http://www.test.com',
};

const testItemDataContent = {
	assetType: 'Web Content',
	siteName: 'Site Name',
};

const testItemDataDocument = {
	assetType: 'Basic Document',
	fileTypeIcon: 'document-image',
	fileTypeIconColor: 'file-icon-color-5',
	siteName: 'Site Name',
};

const testProps = {
	actions: [testActionBase],
	itemData: testItemDataDocument,
	options: {
		actionId: 'view',
	},
	value: 'File Test',
};

describe('SharedItemRenderer', () => {
	it('renders the link', () => {
		render(<SharedItemRenderer {...testProps} />);

		expect(screen.queryByRole('link')).toBeInTheDocument();

		expect(screen.getByText(testProps.value)).toBeInTheDocument();
	});

	it('shows the file type icon', () => {
		const {container} = render(<SharedItemRenderer {...testProps} />);

		expect(container.querySelectorAll('.sticker')[0]).toHaveClass(
			'file-icon-color-5'
		);

		expect(
			container.querySelector('.lexicon-icon-document-image')
		).toBeInTheDocument();
	});

	it('shows the default icon for basic web content', () => {
		const {container} = render(
			<SharedItemRenderer {...testProps} itemData={testItemDataContent} />
		);

		expect(container.querySelectorAll('.sticker')[0]).toHaveClass(
			'content-icon-basic-content'
		);

		expect(
			container.querySelector('.lexicon-icon-forms')
		).toBeInTheDocument();
	});

	it('hides the Shared from Space icon', () => {
		render(
			<SharedItemRenderer
				{...testProps}
				itemData={{
					fileTypeIcon: 'document-image',
					fileTypeIconColor: 'file-icon-color-5',
				}}
			/>
		);

		expect(screen.queryByTitle('shared-from-x')).toBeNull();
	});

	it('shows the Shared from Space icon', () => {
		render(<SharedItemRenderer {...testProps} />);

		const sharedIcon = screen.getByTitle('shared-from-x');

		expect(sharedIcon).toBeInTheDocument();

		expect(
			within(sharedIcon).getByRole('presentation', {name: ''})
		).toHaveClass('lexicon-icon-users');
	});
});
