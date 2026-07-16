/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {AudiencesCriteria} from '../types';

export const DRAG_TYPES = {
	ATTRIBUTE: 'AUDIENCE_ATTRIBUTE',
	RULE: 'AUDIENCE_RULE',
};

export interface AttributeDragItem {
	audiencesCriteria: AudiencesCriteria;
	type: string;
}

export interface RuleDragItem {
	id: string;
	type: string;
}

export type RowDragItem = AttributeDragItem | RuleDragItem;
