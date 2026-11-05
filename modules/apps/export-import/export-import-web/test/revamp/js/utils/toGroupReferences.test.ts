/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {toGroupReferences} from '../../../../src/main/resources/META-INF/resources/revamp/js/utils/toGroupReferences';

describe('toGroupReferences', () => {
	it('wraps each external reference code as a group reference', () => {
		expect(toGroupReferences(['erc-book', 'erc-pictures'])).toEqual([
			{externalReferenceCode: 'erc-book'},
			{externalReferenceCode: 'erc-pictures'},
		]);
	});

	it('returns no references when nothing is selected', () => {
		expect(toGroupReferences([])).toEqual([]);
	});
});
