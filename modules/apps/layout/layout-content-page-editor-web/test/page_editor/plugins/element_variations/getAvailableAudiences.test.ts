/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ElementVariation} from '../../../../src/main/resources/META-INF/resources/page_editor/plugins/element_variations/elementVariationsReducer';
import getAvailableAudiences from '../../../../src/main/resources/META-INF/resources/page_editor/plugins/element_variations/getAvailableAudiences';

const AUDIENCES = [
	{label: 'Audience A', value: 'audience-a'},
	{label: 'Audience B', value: 'audience-b'},
	{label: 'Audience C', value: 'audience-c'},
];

function createVariation(
	overrides: Partial<ElementVariation> = {}
): ElementVariation {
	return {
		active: true,
		audienceEntryERCs: [],
		externalReferenceCode: 'erc',
		hide: false,
		html: {},
		js: {},
		key: 'variation',
		name: 'Variation',
		segmentsExperienceERC: 'experience',
		targetElement: '.title',
		...overrides,
	};
}

describe('getAvailableAudiences', () => {
	it('removes audiences used by another variation on the same page element', () => {
		const availableAudiences = getAvailableAudiences(
			AUDIENCES,
			[
				createVariation({
					audienceEntryERCs: ['audience-a'],
					key: 'other',
					targetElement: '.title',
				}),
			],
			{key: 'draft', targetElement: '.title'}
		);

		expect(availableAudiences).toEqual([
			{label: 'Audience B', value: 'audience-b'},
			{label: 'Audience C', value: 'audience-c'},
		]);
	});

	it('keeps audiences used by a variation on a different page element', () => {
		const availableAudiences = getAvailableAudiences(
			AUDIENCES,
			[
				createVariation({
					audienceEntryERCs: ['audience-a'],
					key: 'other',
					targetElement: '.body',
				}),
			],
			{key: 'draft', targetElement: '.title'}
		);

		expect(availableAudiences).toEqual(AUDIENCES);
	});

	it('keeps audiences used by the variation being edited', () => {
		const availableAudiences = getAvailableAudiences(
			AUDIENCES,
			[
				createVariation({
					audienceEntryERCs: ['audience-a'],
					key: 'draft',
					targetElement: '.title',
				}),
			],
			{key: 'draft', targetElement: '.title'}
		);

		expect(availableAudiences).toEqual(AUDIENCES);
	});

	it('removes audiences taken across several sibling variations', () => {
		const availableAudiences = getAvailableAudiences(
			AUDIENCES,
			[
				createVariation({
					audienceEntryERCs: ['audience-a'],
					key: 'other-1',
					targetElement: '.title',
				}),
				createVariation({
					audienceEntryERCs: ['audience-b'],
					key: 'other-2',
					targetElement: '.title',
				}),
			],
			{key: 'draft', targetElement: '.title'}
		);

		expect(availableAudiences).toEqual([
			{label: 'Audience C', value: 'audience-c'},
		]);
	});

	it('returns every audience when there are no sibling variations', () => {
		const availableAudiences = getAvailableAudiences(AUDIENCES, [], {
			key: 'draft',
			targetElement: '.title',
		});

		expect(availableAudiences).toEqual(AUDIENCES);
	});
});
