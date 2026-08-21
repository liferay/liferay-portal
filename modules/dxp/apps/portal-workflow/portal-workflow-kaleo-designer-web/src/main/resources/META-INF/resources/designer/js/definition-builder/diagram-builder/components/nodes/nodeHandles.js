/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

const handleStyle = {
	background: 'transparent',
	border: '2px solid var(--primary-l1)',
	borderRadius: '50%',
	padding: '4px',
};

const sourceHandles = [
	{
		id: 'leftSource',
		position: 'left',
		style: {...handleStyle, left: '4px', top: '50%'},
		type: 'source',
	},
	{
		id: 'topSource',
		position: 'top',
		style: {...handleStyle, left: '50%', top: '4px'},
		type: 'source',
	},
	{
		id: 'rightSource',
		position: 'right',
		style: {...handleStyle, right: '4px', top: '50%'},
		type: 'source',
	},
	{
		id: 'bottomSource',
		position: 'bottom',
		style: {...handleStyle, bottom: '4px', left: '50%'},
		type: 'source',
	},
];

const targetHandles = [
	{
		id: 'leftTarget',
		position: 'left',
		style: {...handleStyle, left: '4px', top: '50%'},
		type: 'target',
	},
	{
		id: 'topTarget',
		position: 'top',
		style: {...handleStyle, left: '50%', top: '4px'},
		type: 'target',
	},
	{
		id: 'rightTarget',
		position: 'right',
		style: {...handleStyle, right: '4px', top: '50%'},
		type: 'target',
	},
	{
		id: 'bottomTarget',
		position: 'bottom',
		style: {...handleStyle, bottom: '4px', left: '50%'},
		type: 'target',
	},
];

export {sourceHandles, targetHandles};
