/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {
	NonRepeatableGroup,
	RepeatableGroup,
	Structure,
	StructureChild,
} from '../../../../src/main/resources/META-INF/resources/js/structure_builder/types/Structure';
import {Uuid} from '../../../../src/main/resources/META-INF/resources/js/structure_builder/types/Uuid';
import getUuid from '../../../../src/main/resources/META-INF/resources/js/structure_builder/utils/getUuid';
import {copyRepeatableGroups} from '../../../../src/main/resources/META-INF/resources/js/structure_builder/utils/repeatableGroups';

const ROOT_UUID = getUuid();

function buildTab(children: StructureChild[]): NonRepeatableGroup {
	const uuid = getUuid();

	return {
		children: new Map(
			children.map((child) => [child.uuid, {...child, parent: uuid}])
		),
		isRepeatable: false,
		label: {},
		parent: ROOT_UUID,
		type: 'group',
		uuid,
	};
}

function buildRepeatableGroup(name: string, parent: Uuid): RepeatableGroup {
	return {
		children: new Map(),
		erc: `${name}-erc`,
		isRepeatable: true,
		label: {},
		name,
		parent,
		relationshipERC: `${name}-relationship-erc`,
		relationshipName: name,
		type: 'group',
		uuid: getUuid(),
	};
}

describe('copyRepeatableGroups', () => {
	it('gives repeatable groups their own object definitions and relationships and keeps the relationship names', () => {
		const images = buildRepeatableGroup('images', ROOT_UUID);

		const tab = buildTab([buildRepeatableGroup('attachments', ROOT_UUID)]);

		const children: Structure['children'] = new Map<Uuid, StructureChild>([
			[images.uuid, images],
			[tab.uuid, tab],
		]);

		copyRepeatableGroups(children);

		const copiedImages = children.get(images.uuid) as RepeatableGroup;

		expect(copiedImages.erc).not.toBe('images-erc');
		expect(copiedImages.name).not.toBe('images');
		expect(copiedImages.relationshipERC).not.toBe(
			'images-relationship-erc'
		);
		expect(copiedImages.relationshipName).toBe('images');

		const copiedTab = children.get(tab.uuid) as NonRepeatableGroup;

		expect(copiedTab.erc).toBeUndefined();

		const [copiedAttachments] = [
			...copiedTab.children.values(),
		] as RepeatableGroup[];

		expect(copiedAttachments.erc).not.toBe('attachments-erc');
		expect(copiedAttachments.name).not.toBe('attachments');
		expect(copiedAttachments.relationshipERC).not.toBe(
			'attachments-relationship-erc'
		);
		expect(copiedAttachments.relationshipName).toBe('attachments');
	});
});
