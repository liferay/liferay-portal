/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ExportProcessesFDSPropsTransformer from '../../../../src/main/resources/META-INF/resources/revamp/js/fds/ExportProcessesFDSPropsTransformer';

describe('ExportProcessesFDSPropsTransformer', () => {
	it('hides the relaunch action for a static site export', () => {
		const {itemsActions} = ExportProcessesFDSPropsTransformer({
			additionalProps: {staticSiteExportBackgroundTaskIds: [2]},
			itemsActions: [{data: {id: 'relaunch'}}],
		});

		const [relaunchAction] = itemsActions as {
			isVisible: (itemData: {id: number}) => boolean;
		}[];

		expect(relaunchAction.isVisible({id: 1})).toBe(true);
		expect(relaunchAction.isVisible({id: 2})).toBe(false);
	});
});
