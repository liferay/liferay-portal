/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {State} from '../../../../../src/main/resources/META-INF/resources/js/structure_builder/contexts/StateContext';
import {
	RelatedContent,
	Structure,
} from '../../../../../src/main/resources/META-INF/resources/js/structure_builder/types/Structure';
import getUuid from '../../../../../src/main/resources/META-INF/resources/js/structure_builder/utils/getUuid';
import updateHistory from '../../../../../src/main/resources/META-INF/resources/js/structure_builder/utils/state/updateHistory';

const ROOT_UUID = getUuid();
const RELATED_CONTENT_UUID = getUuid();

const INITIAL_HISTORY: State['history'] = {
	deletedChildren: [],
	deletedGroupERCs: [],
	deletedRelationships: [],
	modifiedNames: new Set(),
	modifiedSlugs: new Set(),
};

const RELATED_CONTENT: RelatedContent = {
	erc: 'related-content-erc',
	label: {},
	multiselection: false,
	name: 'relatedContent',
	parent: ROOT_UUID,
	relatedStructureERC: 'target-structure-erc',
	type: 'related-content',
	uuid: RELATED_CONTENT_UUID,
};

const STRUCTURE: Structure = {
	children: new Map([[RELATED_CONTENT_UUID, RELATED_CONTENT]]),
	erc: 'root-erc',
	label: {},
	name: 'Root',
	path: '',
	settings: {},
	slug: '',
	spaces: 'all',
	status: 'draft',
	system: false,
	type: 'L_CMS_CONTENT_STRUCTURES',
	uuid: ROOT_UUID,
	workflows: {},
};

describe('updateHistory', () => {
	it('Records the deleted relationship of a saved but unpublished child', () => {
		const history = updateHistory({
			deletedChildrenUuids: new Set([RELATED_CONTENT_UUID]),
			initialHistory: INITIAL_HISTORY,
			savedChildren: new Set([RELATED_CONTENT_UUID]),
			structure: STRUCTURE,
		});

		expect(history.deletedRelationships).toEqual([
			{
				relationshipERC: 'related-content-erc',
				structureERC: 'target-structure-erc',
			},
		]);
		expect(history.deletedChildren).toEqual([RELATED_CONTENT]);
	});

	it('Records nothing for a child that was never saved', () => {
		const history = updateHistory({
			deletedChildrenUuids: new Set([RELATED_CONTENT_UUID]),
			initialHistory: INITIAL_HISTORY,
			savedChildren: new Set(),
			structure: STRUCTURE,
		});

		expect(history.deletedChildren).toEqual([]);
		expect(history.deletedRelationships).toEqual([]);
	});
});
