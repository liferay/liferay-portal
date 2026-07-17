/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {featureFlagsTest} from '../../../../../fixtures/featureFlagsTest';
import {loginTest} from '../../../../../fixtures/loginTest';
import {balloonPageTest} from '../../../../frontend-editor-ckeditor5-sample-web/fixtures/balloonPageTest';

export const test = mergeTests(
	balloonPageTest,
	featureFlagsTest({
		'LPD-11235': {enabled: false},
		'LPS-178052': {enabled: true},
	}),
	loginTest()
);

test(
	'Toolbar contains all advanced preset controls',
	{tag: '@LPD-11235'},
	async ({balloonPage}) => {
		await expect(
			balloonPage.editable.getByText('Lorem ipsum dolor sit amet')
		).toBeVisible();

		await balloonPage.editable.selectText();

		await expect(balloonPage.toolbar).toBeVisible();

		const advancedPresetControls = [
			'Accessibility help',
			'Undo',
			'Redo',
			'Find and replace',
			'Styles',
			'Normal',
			'Bold',
			'Italic',
			'Underline',
			'Strikethrough',
			'Font Color',
			'Font Background Color',
			'Remove Format',
			'Numbered List',
			'Bulleted List',
			'Increase indent',
			'Decrease indent',
			'Block quote',
			'Link',
			'Insert table',
			'Image',
			'Video',
			'Horizontal line',
			'Text alignment',
		];

		const controls = await balloonPage.toolbar
			.getByRole('button')
			.locator('.ck-button__label')
			.allInnerTexts();

		expect(controls).toEqual(advancedPresetControls);
	}
);
