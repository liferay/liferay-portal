/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {State} from '@liferay/frontend-js-state-web';
import {render, screen} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import React from 'react';

import {ItemSelectorField} from '../../../../../src/main/resources/META-INF/resources/page_editor/app/components/fragment_configuration_fields/ItemSelectorField';
import {pageContentsAtom} from '../../../../../src/main/resources/META-INF/resources/page_editor/app/utils/usePageContents';
import {openItemSelector} from '../../../../../src/main/resources/META-INF/resources/page_editor/common/openItemSelector';

const DOCUMENT_CLASS_NAME =
	'com.liferay.portal.kernel.repository.model.FileEntry';

const FIELD_NAME = 'itemSelectorFieldName';

const WEB_CONTENT_CLASS_NAME = 'com.liferay.journal.model.JournalArticle';

const PAGE_CONTENTS = [
	{
		className: WEB_CONTENT_CLASS_NAME,
		classPK: '001',
		classTypeId: 123,
		title: 'Web Content Title',
	},
	{
		className: DOCUMENT_CLASS_NAME,
		classPK: '002',
		classTypeId: 0,
		title: 'Document Title',
	},
];

jest.mock(
	'../../../../../src/main/resources/META-INF/resources/page_editor/app/config/index',
	() => ({
		config: {
			portletNamespace: 'portletNamespace',
		},
	})
);

jest.mock(
	'../../../../../src/main/resources/META-INF/resources/page_editor/common/openItemSelector',
	() => ({
		openItemSelector: jest.fn(() => {}),
	})
);

function renderItemSelectorField(typeOptions = {}) {
	State.writeAtom(pageContentsAtom, {
		data: PAGE_CONTENTS,
		status: 'saved',
	});

	return render(
		<ItemSelectorField
			field={{label: FIELD_NAME, name: FIELD_NAME, typeOptions}}
			onValueSelect={() => {}}
		/>
	);
}

describe('ItemSelectorField', () => {
	afterEach(() => {
		openItemSelector.mockClear();
	});

	it('shows every recent page content when the field declares no item type', async () => {
		renderItemSelectorField();

		await userEvent.click(screen.getByLabelText(`select-${FIELD_NAME}`));

		expect(screen.getByText('Web Content Title')).toBeInTheDocument();
		expect(screen.getByText('Document Title')).toBeInTheDocument();

		expect(openItemSelector).not.toBeCalled();
	});

	it('shows only the recent page contents matching the declared item type', async () => {
		renderItemSelectorField({itemType: DOCUMENT_CLASS_NAME});

		await userEvent.click(screen.getByLabelText(`select-${FIELD_NAME}`));

		expect(screen.getByText('Document Title')).toBeInTheDocument();
		expect(screen.queryByText('Web Content Title')).not.toBeInTheDocument();

		expect(openItemSelector).not.toBeCalled();
	});

	it('opens the item selector when no recent page content matches the declared item type', async () => {
		renderItemSelectorField({
			itemType: 'com.liferay.blogs.model.BlogsEntry',
		});

		await userEvent.click(screen.getByLabelText(`select-${FIELD_NAME}`));

		expect(openItemSelector).toBeCalled();
	});

	it('opens the item selector when the field declares an item subtype', async () => {
		renderItemSelectorField({
			itemSubtype: '123',
			itemType: WEB_CONTENT_CLASS_NAME,
		});

		await userEvent.click(screen.getByLabelText(`select-${FIELD_NAME}`));

		expect(screen.queryByText('Web Content Title')).not.toBeInTheDocument();

		expect(openItemSelector).toBeCalled();
	});

	it('opens the item selector when the field declares mime types', async () => {
		renderItemSelectorField({
			itemType: DOCUMENT_CLASS_NAME,
			mimeTypes: ['image/png'],
		});

		await userEvent.click(screen.getByLabelText(`select-${FIELD_NAME}`));

		expect(screen.queryByText('Document Title')).not.toBeInTheDocument();

		expect(openItemSelector).toBeCalled();
	});
});
