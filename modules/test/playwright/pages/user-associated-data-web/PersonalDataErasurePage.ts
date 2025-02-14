/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page} from '@playwright/test';

import {searchTableRowByValue} from '../account-admin-web/AccountsPage';
import {ApplicationsMenuPage} from '../product-navigation-applications-menu/ApplicationsMenuPage';

export class PersonalDataErasurePage {
	readonly actionsButton: Locator;
	readonly allSelectedButton: Locator;
	readonly anonymizeButton: Locator;
	readonly anonymizeMenuItem: Locator;
	readonly applicationsMenuPage: ApplicationsMenuPage;
	readonly blogCheckBox: (
		blogId: string,
		blogTitle: string,
		match: boolean
	) => Locator;
	readonly deleteMenuItem: Locator;
	readonly documentsAndMediaRadioButton: Locator;
	readonly journalArticleCheckBox: (
		articleId: string,
		articleUrlTitle: string,
		match: boolean
	) => Locator;
	readonly menuItemDelete: Locator;
	readonly objectCountLink: (objectCountNumber: string) => Locator;
	readonly page: Page;
	readonly pageTitle: Locator;
	readonly selectAllItemsOnPageCheckbox: Locator;
	readonly userAssociatedDataTable: Locator;
	readonly userAssociatedDataTableRow: (
		colPosition: number,
		value: string,
		strictEqual?: boolean
	) => Promise<{column: Locator; row: Locator}>;
	readonly userAssociatedDataTableRowCheckBox: (
		name: string
	) => Promise<Locator>;
	readonly objectLink: (objectName: string) => Locator;

	constructor(page: Page) {
		this.actionsButton = page.getByRole('button', {name: 'Actions'});
		this.allSelectedButton = page
			.locator('nav')
			.filter({hasText: 'All Selected'})
			.getByRole('button');
		this.applicationsMenuPage = new ApplicationsMenuPage(page);
		this.anonymizeButton = page.getByRole('button', {name: 'Anonymize'});
		this.anonymizeMenuItem = page.getByRole('menuitem', {
			name: 'Anonymize',
		});
		this.blogCheckBox = (
			blogId: string,
			blogTitle: string,
			match: boolean
		) => {
			const blogIdLocator = page.locator(`[value="${blogId}"]`);

			return match
				? this.objectLink(blogTitle)
						.locator('../..')
						.filter({has: blogIdLocator})
						.getByRole('checkbox')
				: this.objectLink(blogTitle)
						.locator('../..')
						.filter({hasNot: blogIdLocator})
						.getByRole('checkbox');
		};
		this.deleteMenuItem = page.getByRole('menuitem', {name: 'Delete'});
		this.documentsAndMediaRadioButton = page.locator(
			'input[type="radio"][value="com.liferay.document.library.uad"]'
		);
		this.journalArticleCheckBox = (
			articleId: string,
			articleUrlTitle: string,
			match: boolean
		) => {
			const articleIdLocator = page.locator(
				`[value="${parseInt(articleId, 10) + 1}"]`
			);

			return match
				? this.objectLink(articleUrlTitle)
						.locator('../..')
						.filter({has: articleIdLocator})
						.getByRole('checkbox')
				: this.objectLink(articleUrlTitle)
						.locator('../..')
						.filter({hasNot: articleIdLocator})
						.getByRole('checkbox');
		};
		this.menuItemDelete = page.getByRole('menuitem', {name: 'Delete'});
		this.objectCountLink = (objectCountNumber: string) => {
			return page.getByRole('link', {name: objectCountNumber});
		};
		this.page = page;
		this.selectAllItemsOnPageCheckbox = page.getByLabel(
			'Select All Items on the Page'
		);
		this.userAssociatedDataTable = page
			.locator(
				'#_com_liferay_user_associated_data_web_portlet_UserAssociatedData_uadEntities_com_liferay_blogs_uad'
			)
			.or(
				page.locator(
					'#_com_liferay_user_associated_data_web_portlet_UserAssociatedData_uadEntities_com_liferay_journal_uad'
				)
			)
			.or(
				page.locator(
					'#_com_liferay_user_associated_data_web_portlet_UserAssociatedData_uadEntities_com_liferay_document_library_uad'
				)
			);
		this.userAssociatedDataTableRow = async (
			colPosition: number,
			value: string,
			strictEqual: boolean = false
		) => {
			return await searchTableRowByValue(
				this.userAssociatedDataTable,
				colPosition,
				value,
				strictEqual
			);
		};
		this.userAssociatedDataTableRowCheckBox = async (name: string) => {
			const userAssociatedDataTableRow =
				await this.userAssociatedDataTableRow(1, name, true);

			if (userAssociatedDataTableRow && userAssociatedDataTableRow.row) {
				return userAssociatedDataTableRow.row.getByTitle('Select');
			}

			throw new Error(`Cannot locate account row with name ${name}`);
		};
		this.objectLink = (objectName: string) =>
			page.getByRole('link', {name: objectName});
	}
}
