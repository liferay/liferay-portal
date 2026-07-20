/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Group} from '../../../src/main/resources/META-INF/resources/js/types';
import {getMoveTargets} from '../../../src/main/resources/META-INF/resources/js/util/getMoveTargets';

describe('getMoveTargets', () => {
	it('lists insertion targets across nested groups in visual order', () => {
		const root: Group = {
			conjunction: 'AND',
			id: 'root',
			items: [
				{attribute: 'age', id: 'age', operator: 'gt', value: '18'},
				{
					conjunction: 'OR',
					id: 'group',
					items: [
						{
							attribute: 'city',
							id: 'city',
							operator: 'eq',
							value: 'A',
						},
						{
							attribute: 'country',
							id: 'country',
							operator: 'eq',
							value: 'B',
						},
					],
				},
			],
		};

		expect(
			getMoveTargets(root).map(
				(target) =>
					`${target.nodeId}:${target.position}:${target.groupId}:${target.index}`
			)
		).toEqual([
			'age:top:root:0',
			'age:middle:root:0',
			'group:top:root:1',
			'city:top:group:0',
			'city:middle:group:0',
			'country:top:group:1',
			'country:middle:group:1',
			'country:bottom:group:2',
			'group:bottom:root:2',
		]);
	});

	it('includes a group target for each rule within the depth limit', () => {
		const root: Group = {
			conjunction: 'AND',
			id: 'root',
			items: [
				{attribute: 'age', id: 'age', operator: 'gt', value: '18'},
				{attribute: 'city', id: 'city', operator: 'eq', value: 'A'},
			],
		};

		expect(
			getMoveTargets(root).map(
				(target) => `${target.nodeId}:${target.position}`
			)
		).toEqual([
			'age:top',
			'age:middle',
			'city:top',
			'city:middle',
			'city:bottom',
		]);
	});

	it('omits the group target for a rule at the max depth', () => {
		const root: Group = {
			conjunction: 'AND',
			id: 'root',
			items: [
				{
					conjunction: 'AND',
					id: 'group-1',
					items: [
						{
							conjunction: 'AND',
							id: 'group-2',
							items: [
								{
									attribute: 'age',
									id: 'age',
									operator: 'gt',
									value: '18',
								},
							],
						},
					],
				},
			],
		};

		expect(
			getMoveTargets(root)
				.filter((target) => target.nodeId === 'age')
				.map((target) => target.position)
		).toEqual(['top', 'bottom']);
	});
});
