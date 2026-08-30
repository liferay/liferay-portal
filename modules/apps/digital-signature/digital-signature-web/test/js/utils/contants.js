/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {DOCUSIGN_STATUS} from '../../../src/main/resources/META-INF/resources/js/utils/contants';

describe('DOCUSIGN_STATUS', () => {
	it('has an entry for the correcting status', () => {
		expect(DOCUSIGN_STATUS.correct).toBeDefined();
	});

	it('gives the correcting status a label of its own', () => {
		expect(DOCUSIGN_STATUS.correct.label).not.toBe('correct');
	});

	it('does not render the correcting status as a secondary pill', () => {
		const status = 'correct';

		const resolved = DOCUSIGN_STATUS[status] || {
			color: 'secondary',
			label: status,
		};

		expect(resolved.color).not.toBe('secondary');
		expect(resolved.label).not.toBe(status);
	});
});
