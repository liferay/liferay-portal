/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Text} from '@clayui/core';
import ClayIcon from '@clayui/icon';
import {sub} from 'frontend-js-web';
import React from 'react';

import {toThousands} from '../utils/number';

export enum TrendClassification {
	Negative = 'NEGATIVE',
	Neutral = 'NEUTRAL',
	Positive = 'POSITIVE',
}

export interface IContentAndFilesCard {
	categories: number;
	tags: number;
	title: string;
	trend: {
		classification: TrendClassification;
		percentage: number;
	};
	vocabularies: number;
}

function getStatsColor(trendClassification: TrendClassification) {
	if (trendClassification === TrendClassification.Negative) {
		return 'danger';
	}
	else if (trendClassification === TrendClassification.Positive) {
		return 'success';
	}

	return 'secondary';
}

function getStatsIcon(trendPercentage: number) {
	if (trendPercentage > 0) {
		return 'caret-top';
	}
	else if (trendPercentage < 0) {
		return 'caret-bottom';
	}

	return null;
}

const ContentAndFilesCard: React.FC<IContentAndFilesCard> = ({
	categories,
	tags,
	title,
	trend,
	vocabularies,
}) => {
	const statsIcon = getStatsIcon(trend.percentage);

	const breakdown = [
		{
			icon: 'vocabulary',
			label: Liferay.Language.get('vocabularies'),
			value: vocabularies,
		},
		{
			icon: 'vocabulary',
			label: Liferay.Language.get('categories'),
			value: categories,
		},
		{
			icon: 'tag',
			label: Liferay.Language.get('tags'),
			value: tags,
		},
	];

	return (
		<div className="cms-dashboard__content-and-files-card">
			<Text size={7} weight="semi-bold">
				{title}
			</Text>

			<div>
				<Text color={getStatsColor(trend.classification)} size={3}>
					{statsIcon && (
						<span className="mr-1">
							<ClayIcon symbol={statsIcon} />
						</span>
					)}

					<span>{Math.abs(trend.percentage)}%</span>
				</Text>

				<Text color="secondary" size={3}>
					<span
						className="text-lowercase"
						dangerouslySetInnerHTML={{
							__html: sub(
								Liferay.Language.get('x-vs-previous-period'),
								`<span class='hide'>${Math.abs(trend.percentage)}</span>`
							),
						}}
					/>
				</Text>
			</div>

			<div className="d-flex flex-wrap mt-3">
				{breakdown.map(({icon, label, value}) => (
					<div
						className="cms-dashboard__content-and-files-card__breakdown mt-1"
						key={label}
					>
						<Text color="secondary" size={3}>
							<ClayIcon symbol={icon} />

							<span className="mx-1">{toThousands(value)}</span>

							<span>{label}</span>
						</Text>
					</div>
				))}
			</div>
		</div>
	);
};

export {ContentAndFilesCard};
