/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Provider} from '@clayui/core';
const spritemap = require('@clayui/css/src/images/icons/icons.svg');
const emptyStatesSpritemap = require('@clayui/css/src/images/images/empty_states.svg');
import ClayIcon from '@clayui/icon';
import React from 'react';

export default {
	component: ClayIcon,
	title: 'Design System/Components/Icon',
};
export function Default(args: any) {
	return <ClayIcon spritemap={spritemap} symbol={args.symbol} />;
}

export function Illustration(args: any) {
	return (
		<div style={{height: '288px', width: '288px'}}>
			<ClayIcon
				illustration
				spritemap={emptyStatesSpritemap}
				symbol={args.symbol}
			/>
		</div>
	);
}

Illustration.args = {
	symbol: 'success-state',
};

Illustration.argTypes = {
	symbol: {
		control: {type: 'select'},
		options: [
			'action-toolbar',
			'ai-burst',
			'ai-chat',
			'ai-prompt',
			'ai-workflow',
			'audience',
			'connectors',
			'contenttypes',
			'discovery',
			'document',
			'knowledge-graph',
			'pagebuilder',
			'products',
			'success-state',
			'empty-state',
			'search-state',
			'toolbar-canvas',
			'toolbar-list',
			'workflow',
		],
	},
};

Default.args = {
	symbol: 'add-cell',
};
export function ContextSpritemap(args: any) {
	return (
		<Provider spritemap={spritemap}>
			<ClayIcon symbol={args.symbol} />
		</Provider>
	);
}

ContextSpritemap.args = {
	symbol: 'add-cell',
};
