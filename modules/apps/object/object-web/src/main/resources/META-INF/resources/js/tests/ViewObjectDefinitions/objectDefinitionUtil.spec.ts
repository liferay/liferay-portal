/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {
	canCreateInObjectFolder,
	getDefaultObjectFolder,
	getObjectDefinitionsFilter,
} from '../../components/ViewObjectDefinitions/objectDefinitionUtil';

describe('canCreateInObjectFolder', () => {
	it('returns false when no folder is selected', () => {
		expect(canCreateInObjectFolder(undefined)).toBe(false);
	});

	it('returns true for a regular object folder', () => {
		expect(canCreateInObjectFolder({externalReferenceCode: 'TST123'})).toBe(
			true
		);
	});

	it('returns false for the CMS Content Structures folder', () => {
		expect(
			canCreateInObjectFolder({
				externalReferenceCode: 'L_CMS_CONTENT_STRUCTURES',
			})
		).toBe(false);
	});
});

describe('getDefaultObjectFolder', () => {
	it('returns the Default folder when it is not the first folder', () => {
		const defaultObjectFolder = {
			externalReferenceCode: 'default',
			name: 'Default',
		};

		expect(
			getDefaultObjectFolder([
				{
					externalReferenceCode:
						'L_CMP_PROJECT_MANAGEMENT_DEFINITIONS',
					name: 'CMPProjectManagementDefinitions',
				},
				defaultObjectFolder,
			])
		).toBe(defaultObjectFolder);
	});

	it('returns the first folder when there is no Default folder', () => {
		const firstObjectFolder = {
			externalReferenceCode: 'folder1',
			name: 'folder1',
		};

		expect(
			getDefaultObjectFolder([
				firstObjectFolder,
				{externalReferenceCode: 'folder2', name: 'folder2'},
			])
		).toBe(firstObjectFolder);
	});
});

describe('getObjectDefinitionsFilter', () => {
	it('includes hidden eq false filter when showHiddenObjects is false', () => {
		expect(getObjectDefinitionsFilter('DEFAULT', false)).toBe(
			"hidden eq false and objectFolderExternalReferenceCode eq 'DEFAULT'"
		);
	});

	it('omits hidden eq false filter when showHiddenObjects is true', () => {
		expect(getObjectDefinitionsFilter('DEFAULT', true)).toBe(
			"objectFolderExternalReferenceCode eq 'DEFAULT'"
		);
	});
});
