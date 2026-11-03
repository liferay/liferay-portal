/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {getProcessFormErrors} from '../../../../src/main/resources/META-INF/resources/revamp/js/utils/getProcessFormErrors';

const EMPTY_SELECTION = {
	contentSelection: undefined,
	name: 'name',
	siteExternalReferenceCodes: [],
	spaceExternalReferenceCodes: [],
};

describe('getProcessFormErrors', () => {
	it('requires a name', () => {
		expect(getProcessFormErrors({name: ''})).toMatchObject({
			name: 'this-field-is-required',
		});
	});

	it('asks for an entity type where neither sites nor spaces are on offer', () => {
		expect(getProcessFormErrors(EMPTY_SELECTION)).toEqual({
			contentSelection:
				'please-select-at-least-one-entity-type-to-continue',
		});
	});

	it('asks for an entity type or a site where sites are on offer', () => {
		expect(
			getProcessFormErrors(EMPTY_SELECTION, {siteSelectionEnabled: true})
		).toEqual({
			selection:
				'please-select-at-least-one-entity-type-or-site-to-continue',
		});
	});

	it('asks for an entity type or a space where spaces are on offer', () => {
		expect(
			getProcessFormErrors(EMPTY_SELECTION, {spaceSelectionEnabled: true})
		).toEqual({
			selection:
				'please-select-at-least-one-entity-type-or-space-to-continue',
		});
	});

	it('asks for an entity type, a site or a space where both are on offer', () => {
		expect(
			getProcessFormErrors(EMPTY_SELECTION, {
				siteSelectionEnabled: true,
				spaceSelectionEnabled: true,
			})
		).toEqual({
			selection:
				'please-select-at-least-one-entity-type,-site,-or-space-to-continue',
		});
	});

	it('accepts a selection of spaces alone', () => {
		expect(
			getProcessFormErrors(
				{...EMPTY_SELECTION, spaceExternalReferenceCodes: ['erc-book']},
				{siteSelectionEnabled: true, spaceSelectionEnabled: true}
			)
		).toEqual({});
	});
});
