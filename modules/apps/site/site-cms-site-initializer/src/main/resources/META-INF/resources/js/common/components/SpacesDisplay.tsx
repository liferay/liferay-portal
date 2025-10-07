/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import Badge from '@clayui/badge';
import {ClayTooltipProvider} from '@clayui/tooltip';
import {sub} from 'frontend-js-web';
import React from 'react';

import {Space} from '../types/Space';
import SpaceSticker from './SpaceSticker';

export interface SpaceDisplayProps {
	spaces: Space[];
}

export default function SpacesDisplay(props: SpaceDisplayProps) {
	const {spaces} = props;
	const shouldRenderAllSpaces =
		!spaces.length || spaces.some(({id}) => id === -1);

	if (shouldRenderAllSpaces) {
		return (
			<Badge
				className="badge-pill"
				displayType="secondary"
				label={Liferay.Language.get('all-spaces')}
			/>
		);
	}

	const [firstSpace, ...otherSpaces] = spaces;

	return (
		<span className="align-items-center c-gap-2 d-flex flex-wrap">
			<span className="align-items-center d-flex space-renderer-sticker">
				<SpaceSticker
					displayType={firstSpace.settings?.logoColor}
					name={firstSpace.name}
					size="sm"
				/>
			</span>

			{otherSpaces.length ? (
				<ClayTooltipProvider>
					<span>
						<Badge
							className="badge-pill cursor-pointer"
							data-tooltip-align="bottom"
							displayType="secondary"
							label={`+${otherSpaces.length}`}
							title={sub(
								Liferay.Language.get('available-in-spaces-x'),
								spaces.map((space) => space.name).join(', ')
							)}
						/>
					</span>
				</ClayTooltipProvider>
			) : null}
		</span>
	);
}
