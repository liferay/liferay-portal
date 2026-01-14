/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {render, screen} from '@testing-library/react';
import React from 'react';

import {OBJECT_ENTRY_FOLDER_CLASS_NAME} from '../../../../../src/main/resources/META-INF/resources/js/common/utils/constants';
import AdditionalItemInfoRenderer from '../../../../../src/main/resources/META-INF/resources/js/main_view/props_transformer/cell_renderers/AdditionalItemInfoRenderer';

jest.mock('frontend-js-web', () => ({
	dateUtils: {
		fromNow: jest.fn(() => '5 minutes ago'),
	},
	sub: jest.fn((key, ...args) => {
		let result = key;
		args.forEach((arg, i) => {
			result = result.replace(`{${i}}`, arg);
		});

		return result;
	}),
}));

const mockLiferayLanguageGet = jest.fn((key) => {
	const languageMap: {[key: string]: string} = {
		'modified-x-by-x': 'Modified {0} by {1}',
		'x-article': '{0} Article',
		'x-articles': '{0} Articles',
		'x-folder': '{0} Folder',
		'x-folders': '{0} Folders',
	};

	return languageMap[key] || key;
});

(global as any).Liferay = {
	Language: {
		get: mockLiferayLanguageGet,
	},
};

describe('AdditionalItemInfoRenderer', () => {
	it('renders the number of articles and folders for a folder with one article and one folder', () => {
		const itemData = {
			embedded: {
				numberOfObjectEntries: 1,
				numberOfObjectEntryFolders: 1,
			},
			entryClassName: OBJECT_ENTRY_FOLDER_CLASS_NAME,
		};

		render(<AdditionalItemInfoRenderer itemData={itemData} />);

		expect(screen.getByText('1 Article · 1 Folder')).toBeInTheDocument();
	});

	it('renders the number of articles and folders for a folder with multiple articles and folders', () => {
		const itemData = {
			embedded: {
				numberOfObjectEntries: 5,
				numberOfObjectEntryFolders: 2,
			},
			entryClassName: OBJECT_ENTRY_FOLDER_CLASS_NAME,
		};

		render(<AdditionalItemInfoRenderer itemData={itemData} />);

		expect(screen.getByText('5 Articles · 2 Folders')).toBeInTheDocument();
	});

	it('renders the number of articles and folders for a folder with zero articles and folders', () => {
		const itemData = {
			embedded: {
				numberOfObjectEntries: 0,
				numberOfObjectEntryFolders: 0,
			},
			entryClassName: OBJECT_ENTRY_FOLDER_CLASS_NAME,
		};

		render(<AdditionalItemInfoRenderer itemData={itemData} />);

		expect(screen.getByText('0 Articles · 0 Folders')).toBeInTheDocument();
	});

	it('renders the modification info for a non-folder item', () => {
		const itemData = {
			dateModified: '2023-10-27T10:00:00Z',
			embedded: {
				creator: {name: 'Test User'},
			},
			entryClassName:
				'com.liferay.document.library.kernel.model.DLFileEntry',
		};

		render(<AdditionalItemInfoRenderer itemData={itemData} />);

		expect(
			screen.getByText('Modified 5 minutes ago by Test User')
		).toBeInTheDocument();
	});
});
