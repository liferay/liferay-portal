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
				symbol: 'blogs',
				title: 'Travelling around Japan',
			},
			{
				editURL: '/cms/edit_content_item/67890',
				symbol: 'blogs',
				title: 'North Japan',
			},
		]);
		expect(text).toBe('I created these contents for you:');
	});

	it('extracts edit links broken across lines', () => {
		const {drafts} = parseContentDraftsMessage(
			'[Hero Banner]\n(/group/guest/~/control_panel/manage?_mvcRenderCommandName=%2Ffragment%2Fedit_fragment_entry\n&_externalReferenceCode=123)'
		);

		expect(drafts).toEqual([
			{
				editURL:
					'/group/guest/~/control_panel/manage?_mvcRenderCommandName=%2Ffragment%2Fedit_fragment_entry&_externalReferenceCode=123',
				symbol: 'code',
				title: 'Hero Banner',
			},
		]);
	});

	it('extracts edit links with markdown escapes', () => {
		const {drafts} = parseContentDraftsMessage(
			'[Hero Banner](/manage?\\_mvcRenderCommandName=%2Ffragment%2Fedit\\_fragment\\_entry)'
		);

		expect(drafts).toEqual([
			{
				editURL:
					'/manage?_mvcRenderCommandName=%2Ffragment%2Fedit_fragment_entry',
				symbol: 'code',
				title: 'Hero Banner',
			},
		]);
	});

	it('extracts fragment edit links', () => {
		const {drafts, text} = parseContentDraftsMessage(
			'I created the fragment as a draft:\n\n' +
				'- [Hero Banner](/group/guest/~/control_panel/manage?_com_liferay_fragment_web_portlet_FragmentPortlet_mvcRenderCommandName=%2Ffragment%2Fedit_fragment_entry)'
		);

		expect(drafts).toEqual([
			{
				editURL:
					'/group/guest/~/control_panel/manage?_com_liferay_fragment_web_portlet_FragmentPortlet_mvcRenderCommandName=%2Ffragment%2Fedit_fragment_entry',
				symbol: 'code',
				title: 'Hero Banner',
			},
		]);
		expect(text).toBe('I created the fragment as a draft:');
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
			{
				editURL: '/cms/edit_content_item/1',
				symbol: 'blogs',
				title: 'Draft A',
			},
		]);
	});
});
