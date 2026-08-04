/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {OBJECT_ENTRY_FOLDER_CLASS_NAME} from '../../../../src/main/resources/META-INF/resources/js/common/utils/constants';
import {getScopeExternalReferenceCode} from '../../../../src/main/resources/META-INF/resources/js/common/utils/getScopeExternalReferenceCode';

describe('getScopeExternalReferenceCode', () => {
	it('reads the system properties scope for an asset', () => {
		const item = {
			embedded: {
				systemProperties: {scope: {externalReferenceCode: 'ASSET_ERC'}},
			},
			entryClassName: 'com.liferay.object.model.ObjectEntry',
		};

		expect(getScopeExternalReferenceCode(item)).toBe('ASSET_ERC');
	});

	it('reads the scope for a folder', () => {
		const item = {
			embedded: {scope: {externalReferenceCode: 'FOLDER_ERC'}},
			entryClassName: OBJECT_ENTRY_FOLDER_CLASS_NAME,
		};

		expect(getScopeExternalReferenceCode(item)).toBe('FOLDER_ERC');
	});

	it('returns undefined for an asset without embedded data', () => {
		const item = {entryClassName: 'com.liferay.object.model.ObjectEntry'};

		expect(getScopeExternalReferenceCode(item)).toBeUndefined();
	});

	it('returns undefined for a folder without embedded data', () => {
		const item = {entryClassName: OBJECT_ENTRY_FOLDER_CLASS_NAME};

		expect(getScopeExternalReferenceCode(item)).toBeUndefined();
	});

	it('returns undefined when there is no item', () => {
		expect(getScopeExternalReferenceCode(null)).toBeUndefined();
	});
});
