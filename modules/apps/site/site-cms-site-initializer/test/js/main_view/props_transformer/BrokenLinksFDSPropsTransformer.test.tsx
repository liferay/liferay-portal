/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {render, screen} from '@testing-library/react';
import React from 'react';

import BrokenLinksFDSPropsTransformer from '../../../../src/main/resources/META-INF/resources/js/main_view/props_transformer/BrokenLinksFDSPropsTransformer';

jest.mock('frontend-js-web', () => ({
	...(jest.requireActual('frontend-js-web') as object),
	sub: (template: string, ...values: string[]) =>
		values.reduce(
			(result, value, index) => result.replace(`{${index}}`, value),
			template
		),
}));

jest.mock('@liferay/frontend-data-set-web', () => ({
	findAction: jest.fn(() => ({data: {id: 'edit'}, href: '/edit/{id}'})),
	replaceTokens: jest.fn(() => '/edit/1'),
	useFDSRecordVisit: () => jest.fn(),
}));

const LANGUAGE_KEYS: Record<string, string> = {
	'x-asset-in-recycle-bin': '{0} Asset in Recycle Bin',
	'x-assets-in-recycle-bin': '{0} Assets in Recycle Bin',
	'x-deleted-asset': '{0} Deleted Asset',
	'x-deleted-assets': '{0} Deleted Assets',
	'x-draft-asset': '{0} Draft Asset',
	'x-draft-assets': '{0} Draft Assets',
	'x-expired-asset': '{0} Expired Asset',
	'x-expired-assets': '{0} Expired Assets',
};

const TITLE = 'Referring Content';

describe('[CMS Broken Links] BrokenLinksFDSPropsTransformer', () => {
	const transform = () =>
		BrokenLinksFDSPropsTransformer({
			additionalProps: {},
			id: 'com.liferay.site.cms.site.initializer-brokenLinksSection',
		}) as any;

	const renderTitle = (itemData: object) => {
		const [{component: TitleRenderer}] =
			transform().customRenderers.listSection;

		render(
			<TitleRenderer
				actions={[{data: {id: 'edit'}, href: '/edit/{id}'}]}
				itemData={{title: TITLE, ...itemData}}
				value={TITLE}
			/>
		);
	};

	beforeEach(() => {
		jest.spyOn(Liferay.Language, 'get').mockImplementation(
			(key: string) => LANGUAGE_KEYS[key] ?? key
		);
	});

	afterEach(() => {
		jest.restoreAllMocks();
	});

	it('sorts the broken link assets by title', () => {
		expect(transform().sorts).toEqual([
			{active: true, direction: 'asc', key: 'title', label: 'title'},
		]);
	});

	it('renders the title as plain text without the update permission', () => {
		renderTitle({expiredBrokenLinksCount: 1});

		expect(screen.queryByRole('link', {name: TITLE})).toBeNull();
		expect(screen.getByText(TITLE)).toBeInTheDocument();
	});

	it('links the title with the update permission', () => {
		renderTitle({actions: {update: {}}, expiredBrokenLinksCount: 1});

		expect(screen.getByRole('link', {name: TITLE})).toBeInTheDocument();
	});

	it('counts each type of broken link in the singular or the plural', () => {
		renderTitle({
			deletedBrokenLinksCount: 1,
			draftBrokenLinksCount: 4,
			expiredBrokenLinksCount: 2,
			inTrashBrokenLinksCount: 1,
		});

		expect(
			screen.getByText(
				'2 Expired Assets · 1 Deleted Asset · 1 Asset in Recycle Bin · 4 Draft Assets'
			)
		).toBeInTheDocument();
	});

	it('leaves out the types of broken link the content does not have', () => {
		renderTitle({expiredBrokenLinksCount: 1});

		expect(screen.getByText('1 Expired Asset')).toBeInTheDocument();
	});
});
