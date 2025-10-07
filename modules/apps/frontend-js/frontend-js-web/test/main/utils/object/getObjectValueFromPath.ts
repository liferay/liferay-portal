/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {getObjectValueFromPath} from '../../../../src/main/resources/META-INF/resources/main/utils/object/getObjectValueFromPath';

const object = {
	embedded: {
		author: {
			givenName: 'User',
			name: 'User',
		},
		id: 456,
	},
	id: 123,
	name: 'Test',
};

describe('getObjectValueFromPath', () => {
	it('is defined', () => {
		expect(getObjectValueFromPath).toBeDefined();
	});

	it('retrieves an item value when the selectedItemsKey is simple', () => {
		expect(getObjectValueFromPath({object, path: 'id'})).toEqual(123);
	});

	it('retrieves an item value using "id" if selectedItemsKey is not provided', () => {
		expect(getObjectValueFromPath({object})).toEqual(123);
	});

	it('retrieves an item value using "id" if selectedItemsKey is undefined', () => {
		expect(getObjectValueFromPath({object, path: undefined})).toEqual(123);
	});

	it('retrieves an item value using "id" if selectedItemsKey is null', () => {
		expect(getObjectValueFromPath({object, path: null})).toEqual(123);
	});

	it('retrieves an item value when the selectedItemsKey is a path to a nested object property (2 levels)', () => {
		expect(getObjectValueFromPath({object, path: 'embedded.id'})).toEqual(
			456
		);
	});

	it('retrieves an item value when the selectedItemsKey is a path to a nested object property (3 levels)', () => {
		expect(
			getObjectValueFromPath({object, path: 'embedded.author.name'})
		).toEqual('User');
	});

	it('returns null if the path does not match any property', () => {
		expect(
			getObjectValueFromPath({object, path: 'embedded.author.id'})
		).toBeNull();
	});
});
