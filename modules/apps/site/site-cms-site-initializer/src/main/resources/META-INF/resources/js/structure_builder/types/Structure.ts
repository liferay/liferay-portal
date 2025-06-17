/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Field} from '../utils/field';
import {ValidationError} from '../utils/validation';
import {Uuid} from './Uuid';

type History = {
	deletedFields: boolean;
};

type Status = 'new' | 'draft' | 'published';

type Spaces = 'all' | string[];

export type ReferencedStructure = {
	erc: string;
	name: string;
	type: 'referenced-structure';
	uuid: Uuid;
};

export type Structure = {
	erc: string;
	error: string | null;
	fields: Map<Uuid, Field | ReferencedStructure>;
	history: History;
	id: number | null;
	invalids: Map<Uuid, Set<ValidationError>>;
	label: Liferay.Language.LocalizedValue<string>;
	name: string;
	publishedFields: Set<Uuid>;
	selection: Uuid[];
	spaces: Spaces;
	status: Status;
	type?: 'L_CMS_CONTENT_STRUCTURES' | 'L_CMS_FILE_TYPES';
	unsavedChanges: boolean;
	uuid: Uuid;
};

export type Structures = Map<Structure['erc'], Structure>;
