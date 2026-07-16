/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayIcon from '@clayui/icon';
import ClaySticker from '@clayui/sticker';
import React from 'react';

import {FragmentSetScope} from '../../../app/actions/updateFragments';

const INDICATORS = {
	'design-library': {
		className: 'page-editor__scope-indicator--design-library',
		symbol: 'books-brush',
	},
	'global': {
		className: 'page-editor__scope-indicator--global',
		symbol: 'globe-lines',
	},
} as const;

export function ScopeIndicator({scope}: {scope?: FragmentSetScope}) {
	if (!scope) {
		return null;
	}

	const indicator = INDICATORS[scope.type as keyof typeof INDICATORS];

	if (!indicator) {
		return null;
	}

	const title =
		scope.type === 'global'
			? Liferay.Language.get('global-site')
			: scope.label;

	return (
		<ClaySticker
			className={`c-ml-1 flex-shrink-0 lfr-portal-tooltip page-editor__scope-indicator rounded ${indicator.className}`}
			data-tooltip-align="top"
			displayType="unstyled"
			size="sm"
			title={title}
		>
			<ClayIcon symbol={indicator.symbol} />
		</ClaySticker>
	);
}
