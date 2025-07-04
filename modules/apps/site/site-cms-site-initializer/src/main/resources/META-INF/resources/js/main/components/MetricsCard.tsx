/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Text} from '@clayui/core';
import Icon from '@clayui/icon';

import '../../../css/InfoPanel/Metrics.scss';

import React from 'react';

import {toThousands} from '../dashboard/utils/number';

interface IMetricsCard {
	active: boolean;
	comparison: number;
	setSelectedCard: (title: string) => void;
	title: string;
	total: number;
}

function getComparisonClass(comparison: number): string {
	if (comparison > 0) {
		return 'text-success';
	}

	if (comparison === 0) {
		return 'text-secondary';
	}

	return 'text-danger';
}

function formatComparisonNumber(total: number): string {
	return Math.abs(total).toString();
}

const MetricsCard: React.FC<IMetricsCard> = ({
	active,
	comparison,
	setSelectedCard,
	title,
	total,
}) => {
	const comparisonClassName = getComparisonClass(comparison);

	const handleKeyDown = (event: React.KeyboardEvent<HTMLDivElement>) => {
		if (event.key === 'Enter' || event.key === ' ') {
			event.preventDefault();
			setSelectedCard(title);
		}
	};

	return (
		<div
			aria-pressed={active}
			className={`metrics-card rounded-lg fluid ${active ? 'active' : ''}`}
			onClick={() => setSelectedCard(title)}
			onKeyDown={handleKeyDown}
			role="button"
			tabIndex={0}
		>
			<Text size={3} weight="semi-bold">
				{title.toUpperCase()}
			</Text>

			<div className="body">
				<Text size={7} weight="bold">
					{toThousands(total)}
				</Text>

				<div className={comparisonClassName}>
					{comparison > 0 && <Icon symbol="caret-top" />}

					{/* eslint-disable-next-line @liferay/empty-line-between-elements */}
					{comparison < 0 && <Icon symbol="caret-bottom" />}

					{/* eslint-disable-next-line @liferay/empty-line-between-elements */}
					{formatComparisonNumber(comparison)}%
				</div>
			</div>
		</div>
	);
};

export {MetricsCard};
