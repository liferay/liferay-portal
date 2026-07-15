/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import parseContentDraftsMessage from '../../../../src/main/resources/META-INF/resources/js/AIAssistantChat/utils/parseContentDraftsMessage';

describe('parseContentDraftsMessage', () => {
	it('extracts content edit links and keeps the intro prose as text', () => {
		const {drafts, text} = parseContentDraftsMessage(
			'I created these contents for you:\n\n' +
				'- [Travelling around Japan](/cms/edit_content_item/12345)\n' +
				'- [North Japan](/cms/edit_content_item/67890)'
		);

		expect(drafts).toEqual([
			{
				editURL: '/cms/edit_content_item/12345',
				title: 'Travelling around Japan',
			},
			{editURL: '/cms/edit_content_item/67890', title: 'North Japan'},
		]);
		expect(text).toBe('I created these contents for you:');
	});

	it('ignores links that do not point to a content edit page', () => {
		const {drafts, text} = parseContentDraftsMessage(
			'See the [documentation](https://liferay.com/docs) for details.'
		);

		expect(drafts).toEqual([]);
		expect(text).toBe(
			'See the [documentation](https://liferay.com/docs) for details.'
		);
	});

	it('keeps only content edit links when links are mixed', () => {
		const {drafts} = parseContentDraftsMessage(
			'- [Draft A](/cms/edit_content_item/1)\n- [External](https://example.com)'
		);

		expect(drafts).toEqual([
			{editURL: '/cms/edit_content_item/1', title: 'Draft A'},
		]);
	});
});
