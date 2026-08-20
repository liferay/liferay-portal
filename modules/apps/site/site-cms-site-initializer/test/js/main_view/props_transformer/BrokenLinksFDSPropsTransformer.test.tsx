/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {render, screen} from '@testing-library/react';
import React from 'react';

import BrokenLinksFDSPropsTransformer from '../../../../src/main/resources/META-INF/resources/js/main_view/props_transformer/BrokenLinksFDSPropsTransformer';

jest.mock('@liferay/frontend-data-set-web', () => ({
	findAction: jest.fn(() => ({data: {id: 'edit'}, href: '/edit/{id}'})),
	replaceTokens: jest.fn(() => '/edit/1'),
}));

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
		jest.spyOn(Liferay.Language, 'get');
	});

	afterEach(() => {
		jest.restoreAllMocks();
	});

	it('sorts the broken link assets by title', () => {
		expect(transform().sorts).toEqual([
			{active: true, direction: 'asc', key: 'title', label: 'title'},
		]);
	});

	it('links the title of a content the caller cannot update', () => {
		renderTitle({brokenLinksCount: 1});

		expect(screen.getByRole('link', {name: TITLE})).toBeInTheDocument();
	});

	it('names the broken link when a content has a single one', () => {
		renderTitle({brokenLinkTitle: 'Expired Banner', brokenLinksCount: 1});

		expect(screen.getByText('x-expired-asset')).toBeInTheDocument();
		expect(Liferay.Language.get).not.toHaveBeenCalledWith('untitled-asset');
	});

	it('counts the expired assets when a content has more than one', () => {
		renderTitle({brokenLinkTitle: 'Expired Banner', brokenLinksCount: 3});

		expect(screen.getByText('x-expired-assets')).toBeInTheDocument();
		expect(screen.queryByText('x-expired-asset')).not.toBeInTheDocument();
	});

	it('calls the single broken link untitled when it has no title', () => {
		renderTitle({brokenLinksCount: 1});

		expect(screen.getByText('x-expired-asset')).toBeInTheDocument();
		expect(Liferay.Language.get).toHaveBeenCalledWith('untitled-asset');
	});

	it('counts zero when the content carries no expired asset', () => {
		renderTitle({});

		expect(screen.getByText('x-expired-assets')).toBeInTheDocument();
	});
});
