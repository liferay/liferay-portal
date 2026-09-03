/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Page, expect} from '@playwright/test';

import POM from '../../../utils/POM';
import {EEditorType, waitForEditor} from '../../../utils/waitFor';
import {BalloonPage} from '../pages/BalloonPage';
import {ClassicPage} from '../pages/ClassicPage';
import {InputLocalizedPage} from '../pages/InputLocalizedPage';

export enum TabName {
	ADVANCED_CLASSIC = 'Advanced Classic',
	BALLOON = 'Balloon',
	BASIC_CLASSIC = 'Basic Classic',
	INPUT_LOCALIZED = 'Input Localized',
	REACT = 'React',
	REACT_PLUS_CET = 'React + CET',
	REACT_PLUS_CET_PREMIUM = 'React + CET Premium',
}

export class CKEditor5SamplePage extends POM {
	constructor(page: Page, url: string) {
		super(page, url);
	}

	async gotoTab<T>(tabName: TabName): Promise<T | null> {
		const navLink = this.page
			.locator(
				'.portlet-ckeditor5-sample .lfr-tooltip-scope:nth-child(1) .navbar'
			)
			.getByRole('link', {exact: true, name: tabName});

		await navLink.click();

		await expect(navLink).toHaveClass(/active/);

		await waitForEditor({
			editorType: EEditorType.CKEDITOR5,
			page: this.page,
		});

		let visitedPage = null;

		switch (tabName) {
			case TabName.ADVANCED_CLASSIC:
			case TabName.BASIC_CLASSIC:
			case TabName.REACT:
			case TabName.REACT_PLUS_CET:
			case TabName.REACT_PLUS_CET_PREMIUM:
				visitedPage = new ClassicPage(this.page);
				break;

			case TabName.BALLOON:
				visitedPage = new BalloonPage(this.page);
				break;

			case TabName.INPUT_LOCALIZED:
				visitedPage = new InputLocalizedPage(this.page);
				break;

			default:
				break;
		}

		return visitedPage as unknown as T;
	}

	override async waitFor() {
		await waitForEditor({
			editorType: EEditorType.CKEDITOR5,
			page: this.page,
		});
	}
}
