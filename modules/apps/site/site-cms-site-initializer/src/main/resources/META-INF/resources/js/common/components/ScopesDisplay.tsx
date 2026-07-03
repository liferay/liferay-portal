/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import Badge from '@clayui/badge';
import {ClayTooltipProvider} from '@clayui/tooltip';
import {sub} from 'frontend-js-web';
import React from 'react';

import {Space as Scope} from '../types/Space';
import SpaceSticker from './SpaceSticker';

export interface ScopeDisplayProps {
	allScopesLabel: string;
	availableInScopeLabel: string;
	scopes: Scope[];
}

export default function ScopesDisplay({
	allScopesLabel,
	availableInScopeLabel,
	scopes,
}: ScopeDisplayProps) {
	const shouldRenderAllScopes =
		!scopes.length || scopes.some(({id}) => id === -1);

	if (shouldRenderAllScopes) {
		return (
			<Badge
				className="badge-pill"
				displayType="secondary"
				label={allScopesLabel}
			/>
		);
	}

	const [firstScope, ...otherScopes] = scopes;

	return (
		<span className="align-items-center c-gap-2 d-flex flex-wrap">
			<span className="align-items-center d-flex space-renderer-sticker">
				<SpaceSticker
					displayType={firstScope.settings?.logoColor}
					name={firstScope.name}
					size="xs"
				/>
			</span>

			{otherScopes.length ? (
				<ClayTooltipProvider>
					<span>
						<Badge
							className="badge-pill"
							data-tooltip-align="bottom"
							displayType="secondary"
							label={`+${otherScopes.length}`}
							title={sub(
								availableInScopeLabel,
								scopes.map((scope) => scope.name).join(', ')
							)}
						/>
					</span>
				</ClayTooltipProvider>
			) : null}
		</span>
	);
}
