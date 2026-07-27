/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import classNames from 'classnames';
import React from 'react';

import ContentGapCellActions from './ContentGapCellActions';
import {TaxonomyTerm} from './types';
import {getCellTier, isSentinel} from './utils';

interface ContentGapCellProps {
	funnelStage: TaxonomyTerm;
	maxRealCount: number;
	onFilter?: (persona: TaxonomyTerm, funnelStage: TaxonomyTerm) => void;
	onGenerate?: (persona: TaxonomyTerm, funnelStage: TaxonomyTerm) => void;
	persona: TaxonomyTerm;
	selected?: boolean;
	totalCount: number;
}

export default function ContentGapCell({
	funnelStage,
	maxRealCount,
	onFilter,
	onGenerate,
	persona,
	selected,
	totalCount,
}: ContentGapCellProps) {
	const gap = totalCount === 0;

	const tier = gap ? 0 : getCellTier(totalCount, maxRealCount);

	const clickable = Boolean(onFilter);

	const generatable =
		Boolean(onGenerate) && !isSentinel(persona) && !isSentinel(funnelStage);

	const className = classNames('lfr-cmp__content-gap-cell', {
		'lfr-cmp__content-gap-cell--clickable': clickable,
		'lfr-cmp__content-gap-cell--gap': gap,
		'lfr-cmp__content-gap-cell--selected': selected,
		[`lfr-cmp__content-gap-cell--tier-${tier}`]: tier > 0,
	});

	const ariaLabel = `${persona.name}, ${funnelStage.name}: ${totalCount}`;

	const cellCount = (
		<span className="lfr-cmp__content-gap-cell-count">{totalCount}</span>
	);

	function handleFilter() {
		onFilter?.(persona, funnelStage);
	}

	function handleKeyDown(event: React.KeyboardEvent) {
		if (event.key === 'Enter' || event.key === ' ') {
			event.preventDefault();

			handleFilter();
		}
	}

	if (!clickable) {
		return (
			<div aria-label={ariaLabel} className={className} role="gridcell">
				{cellCount}
			</div>
		);
	}

	return (
		<div
			aria-label={ariaLabel}
			className={className}
			onClick={handleFilter}
			onKeyDown={handleKeyDown}
			role="gridcell"
			tabIndex={0}
		>
			{cellCount}

			<ContentGapCellActions
				onFilter={handleFilter}
				onGenerate={
					generatable
						? () => onGenerate?.(persona, funnelStage)
						: undefined
				}
			/>
		</div>
	);
}
