/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayLayout from '@clayui/layout';
import {TrendClassification} from '@liferay/analytics-reports-js-components-web';
import React, {useState} from 'react';

import {SectionHeader} from '../common/SectionHeader';
import {SpaceOption, SpacePicker, initialSpace} from '../common/SpacePicker';
import InteractiveCard, {
	MetricColor,
} from '../performance/components/InteractiveCard';

const placeholderTrend = {
	classification: TrendClassification.Positive,
	percentage: 22.5,
};

type AttentionCard = {
	color: MetricColor;
	description: string;
	hoverContent: React.ReactNode;
	icon: string;
	title: string;
	trend: {
		classification: TrendClassification;
		percentage: number;
	};
	value: number;
};

const ATTENTION_CARDS: AttentionCard[] = [
	{
		color: 'red',
		description: Liferay.Language.get(
			'references-to-broken-links-across-the-selected-spaces'
		),
		hoverContent: reviewText(Liferay.Language.get('review-broken-links')),
		icon: 'link',
		title: Liferay.Language.get('broken-links'),
		trend: placeholderTrend,
		value: 8,
	},
	{
		color: 'orange',
		description: Liferay.Language.get(
			'number-of-expired-assets-across-the-selected-spaces'
		),
		hoverContent: reviewText(Liferay.Language.get('review-expired-assets')),
		icon: 'warning-full',
		title: Liferay.Language.get('expired-assets'),
		trend: placeholderTrend,
		value: 6,
	},
	{
		color: 'dark',
		description: Liferay.Language.get(
			'assets-with-overdue-reviews-across-the-selected-spaces'
		),
		hoverContent: reviewText(
			Liferay.Language.get('review-overdue-reviews')
		),
		icon: 'date-time',
		title: Liferay.Language.get('overdue-reviews'),
		trend: placeholderTrend,
		value: 3,
	},
	{
		color: 'purple',
		description: Liferay.Language.get(
			'assets-with-pending-workflows-across-the-selected-spaces'
		),
		hoverContent: reviewText(
			Liferay.Language.get('review-pending-workflows')
		),
		icon: 'flag-empty',
		title: Liferay.Language.get('pending-workflows'),
		trend: placeholderTrend,
		value: 4,
	},
];

export default function GovernanceDashboard() {
	const [selectedSpace, setSelectedSpace] =
		useState<SpaceOption>(initialSpace);

	const title = Liferay.Language.get('attention-required');

	return (
		<>
			<div className="mb-4">
				<SpacePicker
					onSelectSpace={setSelectedSpace}
					selectedSpace={selectedSpace}
				/>
			</div>

			<SectionHeader icon="semantic-search" title={title} />

			<ClayLayout.Row aria-label={title} className="mt-3" role="group">
				{ATTENTION_CARDS.map(
					({
						color,
						description,
						hoverContent,
						icon,
						title,
						trend,
						value,
					}) => (
						<ClayLayout.Col
							className="mb-3"
							key={title}
							md={6}
							xl={3}
						>
							<InteractiveCard
								color={color}
								description={description}
								hoverContent={hoverContent}
								icon={icon}
								title={title}
								trend={trend}
								value={value}
							/>
						</ClayLayout.Col>
					)
				)}
			</ClayLayout.Row>
		</>
	);
}

function reviewText(key: string) {
	return <span className="text-2 text-primary text-underline">{key}</span>;
}
