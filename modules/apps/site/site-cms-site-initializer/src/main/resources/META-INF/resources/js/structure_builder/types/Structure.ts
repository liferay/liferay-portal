/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Space} from '../../common/types/Space';
import {Workflow} from '../../common/types/Workflow';
import {Field} from '../utils/field';
import {Uuid} from './Uuid';

type Status = 'new' | 'draft' | 'published';

type Spaces = 'all' | string[];

type Workflows = Record<'' | Space['externalReferenceCode'], Workflow['name']>;

export type ReferencedStructure = {
	children: Map<Uuid, StructureChild>;
	editURL: string;
	erc: string;
	label: Liferay.Language.LocalizedValue<string>;
	name: string;
	parent: Uuid;
	relationshipName: string;
	spaces: Spaces;
	type: 'referenced-structure';
	uuid: Uuid;
	workflows: Workflows;
};

export type RepeatableGroup = {
	children: Map<Uuid, StructureChild>;
	erc: string;
	label: Liferay.Language.LocalizedValue<string>;
	name: string;
	parent: Uuid;
	relationshipName: string;
	type: 'repeatable-group';
	uuid: Uuid;
};

export type StructureChild = Field | ReferencedStructure | RepeatableGroup;

export type Structure = {
	children: Map<Uuid, StructureChild>;
	erc: string;
	label: Liferay.Language.LocalizedValue<string>;
	name: string;
	spaces: Spaces;
	status: Status;
	type?: 'L_CMS_CONTENT_STRUCTURES' | 'L_CMS_FILE_TYPES';
	uuid: Uuid;
	workflows: Workflows;
};

export type Structures = Map<Structure['erc'], Structure>;
