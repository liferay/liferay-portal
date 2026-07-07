/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import classNames from 'classnames';
import React, {useEffect, useRef, useState} from 'react';

import ContentGapCellActions from './ContentGapCellActions';
import {TaxonomyTerm} from './types';
import {getCellTier, isSentinel} from './utils';

interface ContentGapCellProps {
	funnelStage: TaxonomyTerm;
	maxRealCount: number;
	onFilter?: (persona: TaxonomyTerm, funnelStage: TaxonomyTerm) => void;
	persona: TaxonomyTerm;
	selected?: boolean;
	totalCount: number;
}

export default function ContentGapCell({
	funnelStage,
	maxRealCount,
	onFilter,
	persona,
	selected,
	totalCount,
}: ContentGapCellProps) {
	const [active, setActive] = useState(false);

	const cellRef = useRef<HTMLDivElement>(null);

	const gap = totalCount === 0;

	const tier = gap ? 0 : getCellTier(totalCount, maxRealCount);

	// Only real persona/funnel-stage cells filter the asset table. The
	// uncategorized "No Persona" row and "No Funnel" column have no category to
	// filter by, so they stay static.

	const clickable =
		Boolean(onFilter) && !isSentinel(persona) && !isSentinel(funnelStage);

	const className = classNames('lfr-cmp__content-gap-cell', {
		'lfr-cmp__content-gap-cell--active': active,
		'lfr-cmp__content-gap-cell--clickable': clickable,
		'lfr-cmp__content-gap-cell--gap': gap,
		'lfr-cmp__content-gap-cell--selected': selected,
		[`lfr-cmp__content-gap-cell--tier-${tier}`]: tier > 0,
	});

	const ariaLabel = `${persona.name}, ${funnelStage.name}: ${totalCount}`;

	const cellCount = (
		<span className="lfr-cmp__content-gap-cell-count">{totalCount}</span>
	);

	// Close the action bar when clicking outside the cell.

	useEffect(() => {
		if (!active) {
			return;
		}

		function handleDocumentClick(event: MouseEvent) {
			if (!cellRef.current?.contains(event.target as Node)) {
				setActive(false);
			}
		}

		document.addEventListener('click', handleDocumentClick);

		return () => document.removeEventListener('click', handleDocumentClick);
	}, [active]);

	function handleKeyDown(event: React.KeyboardEvent) {
		if (event.key === 'Enter' || event.key === ' ') {
			event.preventDefault();

			toggleActive();
		}
		else if (event.key === 'Escape') {
			setActive(false);
		}
	}

	function toggleActive() {
		setActive((wasActive) => !wasActive);
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
			aria-expanded={active}
			aria-haspopup="true"
			aria-label={ariaLabel}
			className={className}
			onClick={toggleActive}
			onKeyDown={handleKeyDown}
			ref={cellRef}
			role="gridcell"
			tabIndex={0}
		>
			{cellCount}

			{active ? (
				<ContentGapCellActions
					onFilter={() => {
						onFilter?.(persona, funnelStage);

						setActive(false);
					}}
				/>
			) : null}
		</div>
	);
}
