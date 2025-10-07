/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page} from '@playwright/test';

import POM from '../../../../utils/POM';
import {clickAndExpectToBeVisible} from '../../../../utils/clickAndExpectToBeVisible';

export enum TabName {
	ALERTS = 'Alerts',
	BADGES = 'Badges',
	BUTTONS = 'Buttons',
	CARDS = 'Cards',
	DROPDOWNS = 'Dropdowns',
	FORM_ELEMENTS = 'Form Elements',
	ICONS = 'Icons',
	LABELS = 'Labels',
	LINKS = 'Links',
	MANAGEMENT_TOOLBARS = 'Management Toolbars',
	NAVIGATION_BARS = 'Navigation Bars',
	PAGINATION_BARS = 'Pagination Bars',
	PANEL = 'Panel',
	PROGRESS_BARS = 'Progress Bars',
	STICKERS = 'Stickers',
	TABS = 'Tabs',
	TOGGLE = 'Toggle',
	VERTICAL_NAV = 'Vertical Nav',
}

export class ClaySamplePage extends POM {
	readonly managementToolbarActiveState: Locator;
	readonly managementToolbarDefaultState: Locator;
	readonly managementToolbarUsingDisplayContext: Locator;
	readonly managementToolbarWithResultsBar: Locator;
	readonly tablist: Locator;
	readonly tooltip: Locator;

	constructor(page: Page, url: string) {
		super(page, url);

		this.managementToolbarActiveState = page.locator(
			'#managementToolbarActiveState'
		);
		this.managementToolbarDefaultState = page.locator(
			'#managementToolbarDefaultState'
		);
		this.managementToolbarUsingDisplayContext = page.locator(
			'#managementToolbarUsingDisplayContext'
		);
		this.managementToolbarWithResultsBar = page.locator(
			'#managementToolbarWithResultsBar'
		);
		this.tablist = page.getByRole('tablist');
		this.tooltip = page.locator('.tooltip-inner');
	}

	async selectTab(tabName: TabName) {
		const tabHeading = this.tablist.getByText(tabName);

		const target: Locator | undefined = {
			[TabName.ALERTS]: this.page.getByRole('heading', {
				name: 'EMBEDDED',
			}),

			[TabName.BADGES]: this.page.getByText('Primary'),

			[TabName.BUTTONS]: this.page.getByRole('heading', {name: 'TYPES'}),

			[TabName.CARDS]: this.page.getByRole('heading', {
				name: 'Image Cards',
			}),

			[TabName.DROPDOWNS]: this.page.getByRole('heading', {
				name: 'DROPDOWN MENU',
			}),

			[TabName.FORM_ELEMENTS]: this.page.getByRole('heading', {
				name: 'CHECKBOX',
			}),

			[TabName.ICONS]: this.page.getByRole('heading', {
				name: 'Liferay Icon Library',
			}),

			[TabName.LABELS]: this.page.getByRole('heading', {
				name: 'LABEL REMOVABLE',
			}),

			[TabName.LINKS]: this.page.getByRole('heading', {
				name: 'SINGLE LINK',
			}),

			[TabName.MANAGEMENT_TOOLBARS]: this.page.getByRole('heading', {
				name: 'DEFAULT STATE',
			}),

			[TabName.NAVIGATION_BARS]: this.page.getByRole('heading', {
				name: 'NAVIGATION BARS USING DISPLAY CONTEXT',
			}),

			[TabName.PAGINATION_BARS]: this.page.getByRole('heading', {
				name: 'Default',
			}),

			[TabName.PANEL]: this.page.getByRole('heading', {
				name: 'DEFAULT PANEL',
			}),

			[TabName.PROGRESS_BARS]: this.page.getByText(
				'Progress bar is a progress indicator used to show the completion percentage of a task.'
			),

			[TabName.STICKERS]: this.page.getByRole('heading', {
				name: 'SQUARE',
			}),

			[TabName.TABS]: this.page.getByRole('heading', {
				name: 'DEFAULT TABS',
			}),

			[TabName.TOGGLE]: this.page.getByRole('heading', {
				name: 'DEFAULT TOGGLE',
			}),

			[TabName.VERTICAL_NAV]: this.page.getByRole('heading', {
				name: 'DEFAULT VERTICAL NAV',
			}),
		}[tabName];

		if (target === undefined) {
			throw new Error(`Unknown tab name ${tabName}`);
		}

		await clickAndExpectToBeVisible({
			autoClick: true,
			target,
			trigger: tabHeading,
		});
	}

	override async waitFor() {
		await this.page
			.getByRole('heading', {name: 'EMBEDDED'})
			.waitFor({state: 'visible'});
	}
}
