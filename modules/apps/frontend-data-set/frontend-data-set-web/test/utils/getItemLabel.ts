/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {getItemLabel} from '../../src/main/resources/META-INF/resources/utils/getItemLabel';

describe('getItemLabel', () => {
	it('returns the field the view names its rows by', () => {
		expect(
			getItemLabel(
				{id: 1, reference: 'AF-1', title: 'Nike'},
				{accessibleNameField: 'reference'}
			)
		).toBe('AF-1');
	});

	it('returns the title of a row the view names by nothing in particular', () => {
		expect(
			getItemLabel({id: 1, title: 'Nike'}, {fallback: 'View Details'})
		).toBe('Nike');
	});

	it('returns the title when the field the view names its rows by is empty', () => {
		expect(
			getItemLabel(
				{id: 1, title: 'Nike'},
				{accessibleNameField: 'reference'}
			)
		).toBe('Nike');
	});

	it('returns the name of a row that carries no title', () => {
		expect(
			getItemLabel({id: 1, name: 'Nike'}, {fallback: 'View Details'})
		).toBe('Nike');
	});

	it('returns a title the server sent as a map of locales', () => {
		expect(getItemLabel({id: 1, title: {en_US: 'Nike'}})).toBe('Nike');
	});

	it('returns the translation the server sent alongside the title', () => {
		expect(
			getItemLabel({
				id: 1,
				title: 'Zapatilla',
				title_i18n: {en_US: 'Nike'},
			})
		).toBe('Nike');
	});

	it('falls back for a row that goes by no name of its own', () => {
		expect(getItemLabel({id: 1}, {fallback: 'View Details'})).toBe(
			'View Details'
		);
	});

	it('falls back when there is no row at all', () => {
		expect(getItemLabel(undefined, {fallback: 'View Details'})).toBe(
			'View Details'
		);
	});

	it('returns nothing when neither the row nor the fallback names anything', () => {
		expect(getItemLabel({id: 1})).toBe('');
	});
});
