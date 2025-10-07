/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {IInternalRenderer} from '../utils/types';

// @ts-ignore

import ActionsLinkRenderer from './ActionLinkRenderer';

// @ts-ignore

import BooleanRenderer from './BooleanRenderer';
import DateRenderer from './DateRenderer';
import DateTimeRenderer from './DateTimeRenderer';
import DefaultRenderer from './DefaultRenderer';

// @ts-ignore

import ImageRenderer from './ImageRenderer';

// @ts-ignore

import LabelRenderer from './LabelRenderer';

// @ts-ignore

import LinkRenderer from './LinkRenderer';

// @ts-ignore

import QuantitySelectorRenderer from './QuantitySelectorRenderer';

// @ts-ignore

import StatusRenderer from './StatusRenderer';

export const INTERNAL_CELL_RENDERERS: Array<IInternalRenderer> = [
	{
		component: ActionsLinkRenderer,
		label: Liferay.Language.get('action-link'),
		name: 'actionLink',
		type: 'internal',
	},
	{
		component: BooleanRenderer,
		label: Liferay.Language.get('boolean'),
		name: 'boolean',
		type: 'internal',
	},
	{
		component: DateRenderer,
		label: Liferay.Language.get('date'),
		name: 'date',
		type: 'internal',
	},
	{
		component: DateTimeRenderer,
		label: Liferay.Language.get('date-and-time'),
		name: 'dateTime',
		type: 'internal',
	},
	{
		component: DefaultRenderer,
		label: Liferay.Language.get('default'),
		name: 'default',
		type: 'internal',
	},
	{
		component: ImageRenderer,
		label: Liferay.Language.get('image'),
		name: 'image',
		type: 'internal',
	},
	{
		component: LabelRenderer,
		label: Liferay.Language.get('label'),
		name: 'label',
		type: 'internal',
	},
	{
		component: LinkRenderer,
		label: Liferay.Language.get('link'),
		name: 'link',
		type: 'internal',
	},
	{
		component: QuantitySelectorRenderer,
		label: Liferay.Language.get('quantity-selector'),
		name: 'quantitySelector',
		type: 'internal',
	},
	{
		component: StatusRenderer,
		label: Liferay.Language.get('status'),
		name: 'status',
		type: 'internal',
	},
];
