/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {setDefaultLanguageLabels} from '../../../../../src/main/resources/META-INF/resources/js/common/utils/defaultLanguageLabels';
import {
	RepeatableGroup,
	Structure,
} from '../../../../../src/main/resources/META-INF/resources/js/structure_builder/types/Structure';
import getUuid from '../../../../../src/main/resources/META-INF/resources/js/structure_builder/utils/getUuid';
import addRepeatableGroup from '../../../../../src/main/resources/META-INF/resources/js/structure_builder/utils/state/addRepeatableGroup';

const ROOT_UUID = getUuid();
const GROUP_UUID = getUuid();

const ROOT: Structure = {
	children: new Map(),
	erc: 'root-erc',
	label: {},
	name: 'Root',
	path: '',
	slug: '',
	spaces: 'all',
	status: 'new',
	system: false,
	type: 'L_CMS_CONTENT_STRUCTURES',
	uuid: ROOT_UUID,
	workflows: {},
};

describe('addRepeatableGroup', () => {
	let getDefaultLanguageIdSpy: jest.SpyInstance;
	let getLanguageIdSpy: jest.SpyInstance;

	beforeEach(() => {
		getDefaultLanguageIdSpy = jest.spyOn(
			Liferay.ThemeDisplay,
			'getDefaultLanguageId'
		);
		getLanguageIdSpy = jest.spyOn(Liferay.ThemeDisplay, 'getLanguageId');
	});

	afterEach(() => {
		jest.restoreAllMocks();
		setDefaultLanguageLabels({labels: {}, locale: 'en_US'});
	});

	it('populates the new group label under both default and current language IDs', () => {
		getDefaultLanguageIdSpy.mockReturnValue('en_US');
		getLanguageIdSpy.mockReturnValue('es_ES');

		const children = addRepeatableGroup({
			groupChildren: [],
			groupParent: ROOT_UUID,
			groupUuid: GROUP_UUID,
			root: ROOT,
		});

		const group = children.get(GROUP_UUID) as RepeatableGroup;

		expect(group).toBeDefined();
		expect(group.type).toBe('repeatable-group');
		expect(group.label).toEqual({
			en_US: 'repeatable-group',
			es_ES: 'repeatable-group',
		});
	});

	it('produces a single label key when current and default language match', () => {
		getDefaultLanguageIdSpy.mockReturnValue('en_US');
		getLanguageIdSpy.mockReturnValue('en_US');

		const children = addRepeatableGroup({
			groupChildren: [],
			groupParent: ROOT_UUID,
			groupUuid: GROUP_UUID,
			root: ROOT,
		});

		const group = children.get(GROUP_UUID) as RepeatableGroup;

		expect(Object.keys(group.label)).toEqual(['en_US']);
		expect(group.label.en_US).toBe('repeatable-group');
	});

	it('seeds the default-language label from the singleton when current locale differs', () => {
		getDefaultLanguageIdSpy.mockReturnValue('en_US');
		getLanguageIdSpy.mockReturnValue('es_ES');

		const getLanguageSpy = jest
			.spyOn(Liferay.Language, 'get')
			.mockImplementation((key: string) =>
				key === 'repeatable-group' ? 'Grupo repetible' : key
			);

		setDefaultLanguageLabels({
			labels: {
				'repeatable-group': 'Repeatable group',
			},
			locale: 'en_US',
		});

		try {
			const children = addRepeatableGroup({
				groupChildren: [],
				groupParent: ROOT_UUID,
				groupUuid: GROUP_UUID,
				root: ROOT,
			});

			const group = children.get(GROUP_UUID) as RepeatableGroup;

			expect(group.label).toEqual({
				en_US: 'Repeatable group',
				es_ES: 'Grupo repetible',
			});
		}
		finally {
			getLanguageSpy.mockRestore();
		}
	});
});
