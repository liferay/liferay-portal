/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom/extend-expect';
import {render, screen} from '@testing-library/react';
import React from 'react';

import SimpleActionLinkRenderer from '../../../../../src/main/resources/META-INF/resources/js/main_view/props_transformer/cell_renderers/SimpleActionLinkRenderer';

const testActionBase = {
	data: {id: 'view'},
	href: 'http://www.test.com',
};

const testActionFolder = {
	data: {id: 'viewFolder'},
	href: 'http://www.test.com/folder',
};

const testAdditionalProps = {
	fileMimeTypeCssClasses: {
		default: 'file-icon-color-0',
		image: 'file-icon-color-3',
	},
	fileMimeTypeIcons: {
		default: 'document-default',
		image: 'document-image',
	},
	objectDefinitionCssClasses: {
		L_BASIC_WEB_CONTENT: 'content-icon-basic-content',
		default: 'content-icon-custom-structure',
	},
	objectDefinitionIcons: {
		L_BASIC_WEB_CONTENT: 'web-content',
		default: 'forms',
	},
};

const testBaseProps = {
	actions: [testActionBase, testActionFolder],
	additionalProps: testAdditionalProps,
	itemData: {
		embedded: {
			systemProperties: {
				objectDefinitionBrief: {
					externalReferenceCode: 'L_BASIC_WEB_CONTENT',
				},
			},
		},
		entryClassName: 'com.liferay.object.model.ObjectDefinition#H4T4',
	},
	options: {
		actionId: 'view',
	},
	value: 'Web Content Test',
};

const testFolderProps = {
	actions: [testActionBase, testActionFolder],
	additionalProps: testAdditionalProps,
	itemData: {
		entryClassName: 'com.liferay.object.model.ObjectEntryFolder',
	},
	options: {
		actionId: 'view',
	},
	value: 'Folder Test',
};

describe('SimpleActionLinkRenderer. Render the value only.', () => {
	it('there are no actions', () => {
		render(<SimpleActionLinkRenderer {...testBaseProps} actions={[]} />);

		expect(screen.queryByRole('link')).not.toBeInTheDocument();

		expect(screen.getByText(testBaseProps.value)).toBeInTheDocument();
	});

	it('empty actionId string', () => {
		render(
			<SimpleActionLinkRenderer
				{...testBaseProps}
				options={{actionId: ''}}
			/>
		);

		expect(screen.queryByRole('link')).not.toBeInTheDocument();

		expect(screen.getByText(testBaseProps.value)).toBeInTheDocument();
	});

	it('actionId not in available actions', () => {
		render(
			<SimpleActionLinkRenderer
				{...testBaseProps}
				options={{actionId: 'edit'}}
			/>
		);

		expect(screen.queryByRole('link')).not.toBeInTheDocument();

		expect(screen.getByText(testBaseProps.value)).toBeInTheDocument();
	});
});

describe('SimpleActionLinkRenderer. Render the link.', () => {
	it('non-folder action', () => {
		render(<SimpleActionLinkRenderer {...testBaseProps} />);

		expect(
			screen.getByRole('link', {name: testBaseProps.value})
		).toHaveAttribute('href', testActionBase.href);
	});

	it('folder action', () => {
		render(<SimpleActionLinkRenderer {...testFolderProps} />);

		expect(
			screen.getByRole('link', {name: testFolderProps.value})
		).toHaveAttribute('href', testActionFolder.href);
	});
});

describe('SimpleActionLinkRenderer. Show type icon.', () => {
	it('non-folder item with web content icon', () => {
		const {container} = render(
			<SimpleActionLinkRenderer {...testBaseProps} />
		);

		expect(container.querySelectorAll('.sticker')[0]).toHaveClass(
			'content-icon-basic-content'
		);
		expect(screen.getByRole('presentation', {name: ''})).toHaveClass(
			'lexicon-icon-web-content'
		);
	});

	it('folder item with folder icon', () => {
		const {container} = render(
			<SimpleActionLinkRenderer {...testFolderProps} />
		);

		expect(container.querySelectorAll('.sticker')[0]).toHaveClass(
			'file-icon-color-0'
		);
		expect(screen.getByRole('presentation', {name: ''})).toHaveClass(
			'lexicon-icon-folder'
		);
	});
});
