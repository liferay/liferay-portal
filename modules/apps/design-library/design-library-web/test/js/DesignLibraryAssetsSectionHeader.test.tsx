/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';

// eslint-disable-next-line
import {checkAccessibility} from '@liferay/layout-js-components-web/test/__lib__/index';
import {act, render, screen} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import React from 'react';

import DesignLibraryAssetsSectionHeader from '../../src/main/resources/META-INF/resources/js/DesignLibraryAssetsSectionHeader';

const PAGE_TEMPLATE_SET = {
	color: 'blue',
	creationItems: [
		{
			id: 'add-content-page-template',
			label: 'new-content-page-template',
			module: 'http://localhost/layout-page-template-admin-web',
			moduleProps: {},
		},
		{
			id: 'add-page-template-set',
			label: 'new-page-template-set',
			module: 'http://localhost/layout-page-template-admin-web',
			moduleProps: {},
		},
	],
	creationItemsGroupLabel: 'Page Templates',
	defaultActionId: 'view',
	entryClassName:
		'com.liferay.layout.page.template.model.LayoutPageTemplateCollection',
	key: 'page-template-set',
	label: 'Page Template Set',
	symbol: 'page-template',
};

const STYLE_BOOK = {
	color: 'purple',
	creationItems: [
		{
			id: 'add-style-book',
			label: 'new-style-book',
			module: 'http://localhost/style-book-web',
			moduleProps: {},
		},
	],
	defaultActionId: 'edit',
	entryClassName: 'com.liferay.style.book.model.StyleBookEntry',
	key: 'style-book',
	label: 'Style Book',
	symbol: 'book',
};

describe('DesignLibraryAssetsSectionHeader', () => {
	it('hides the add asset action without creation items and groups them per type', async () => {
		Object.defineProperty(document.body, 'clientWidth', {
			configurable: true,
			value: 1440,
		});

		const {container, rerender} = render(
			<DesignLibraryAssetsSectionHeader
				resourceTypes={[{...STYLE_BOOK, creationItems: undefined}]}
			/>
		);

		expect(
			screen.queryByRole('button', {name: 'add-asset'})
		).not.toBeInTheDocument();

		rerender(
			<DesignLibraryAssetsSectionHeader
				resourceTypes={[PAGE_TEMPLATE_SET, STYLE_BOOK]}
			/>
		);

		await userEvent.click(screen.getByRole('button', {name: 'add-asset'}));

		expect(
			screen.getByRole('menuitem', {name: 'new-style-book'})
		).toBeVisible();

		await userEvent.click(
			screen.getByRole('menuitem', {name: 'Page Templates'})
		);

		expect(
			screen.getByRole('menuitem', {name: 'new-content-page-template'})
		).toBeVisible();
		expect(
			screen.getByRole('menuitem', {name: 'new-page-template-set'})
		).toBeVisible();

		await act(async () => {
			await checkAccessibility({
				bestPractices: true,
				context: container,
			});
		});
	});
});
