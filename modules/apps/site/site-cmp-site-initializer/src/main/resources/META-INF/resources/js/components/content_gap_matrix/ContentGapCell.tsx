/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import classNames from 'classnames';
import React from 'react';

import {TaxonomyTerm} from './types';
import {getCellTier} from './utils';

export default function ContentGapCell({
	funnelStage,
	maxRealCount,
	persona,
	totalCount,
}: {
	funnelStage: TaxonomyTerm;
	maxRealCount: number;
	persona: TaxonomyTerm;
	totalCount: number;
}) {
	const gap = totalCount === 0;

	const tier = gap ? 0 : getCellTier(totalCount, maxRealCount);

	return (
		<div
			aria-label={`${persona.name}, ${funnelStage.name}: ${totalCount}`}
			className={classNames('lfr-cmp__content-gap-cell', {
				'lfr-cmp__content-gap-cell--gap': gap,
				[`lfr-cmp__content-gap-cell--tier-${tier}`]: tier > 0,
			})}
			role="gridcell"
		>
			<span className="lfr-cmp__content-gap-cell-count">
				{totalCount}
			</span>
		</div>
	);
}
