/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {OBJECT_ENTRY_FOLDER_CLASS_NAME} from '../../../../src/main/resources/META-INF/resources/js/common/utils/constants';
import {isSpaceMember} from '../../../../src/main/resources/META-INF/resources/js/common/utils/isSpaceMember';

describe('isSpaceMember', () => {
	const assetLibraries = [
		{externalReferenceCode: 'SPACE_A', groupId: 1, name: 'Space A'},
	];

	it('allows a folder from a Space in the asset libraries list', () => {
		expect(
			isSpaceMember(assetLibraries, {
				embedded: {scope: {externalReferenceCode: 'SPACE_A'}},
				entryClassName: OBJECT_ENTRY_FOLDER_CLASS_NAME,
			})
		).toBe(true);
	});

	it('allows an asset from a Space in the asset libraries list', () => {
		expect(
			isSpaceMember(assetLibraries, {
				embedded: {
					systemProperties: {
						scope: {externalReferenceCode: 'SPACE_A'},
					},
				},
			})
		).toBe(true);
	});

	it('allows an asset when the asset libraries list is missing', () => {
		expect(
			isSpaceMember(undefined, {
				embedded: {
					systemProperties: {
						scope: {externalReferenceCode: 'SPACE_B'},
					},
				},
			})
		).toBe(true);
	});

	it('allows an asset without Space data', () => {
		expect(isSpaceMember(assetLibraries, {embedded: {}})).toBe(true);
	});

	it('denies an asset from a Space outside the asset libraries list', () => {
		expect(
			isSpaceMember(assetLibraries, {
				embedded: {
					systemProperties: {
						scope: {externalReferenceCode: 'SPACE_B'},
					},
				},
			})
		).toBe(false);
	});
});
