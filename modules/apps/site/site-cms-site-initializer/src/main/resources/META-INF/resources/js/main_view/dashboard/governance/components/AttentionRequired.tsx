/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayLayout from '@clayui/layout';
import {TrendClassification} from '@liferay/analytics-reports-js-components-web';
import React, {useContext, useEffect, useState} from 'react';

import {SectionHeader} from '../../common/SectionHeader';
import InteractiveCard, {
	MetricColor,
} from '../../performance/components/InteractiveCard';
import {GovernanceContext} from '../GovernanceContext';
import GovernanceService, {AssetStatistics} from '../GovernanceService';

const placeholderTrend = {
	classification: TrendClassification.Positive,
	percentage: 22.5,
};

type AttentionCard = {
	color: MetricColor;
	description: string;
	hoverContent: React.ReactNode;
	icon: string;
	statKey?: keyof AssetStatistics;
	title: string;
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
	},
	{
		color: 'orange',
		description: Liferay.Language.get(
			'number-of-expired-assets-across-the-selected-spaces'
		),
		hoverContent: reviewText(Liferay.Language.get('review-expired-assets')),
		icon: 'warning-full',
		statKey: 'expiredCount',
		title: Liferay.Language.get('expired-assets'),
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
		statKey: 'reviewDateOverdueCount',
		title: Liferay.Language.get('overdue-reviews'),
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
		statKey: 'pendingCount',
		title: Liferay.Language.get('pending-workflows'),
	},
];

export function AttentionRequired() {
	const [loading, setLoading] = useState(true);
	const {space} = useContext(GovernanceContext);
	const [statistics, setStatistics] = useState<AssetStatistics>();

	const title = Liferay.Language.get('attention-required');

	useEffect(() => {
		async function fetchStatistics() {
			setLoading(true);

			const {data} = await GovernanceService.getAssetStatistics(
				space.value === 'all' ? undefined : space.value
			);

			setStatistics(data ?? undefined);
			setLoading(false);
		}

		fetchStatistics();
	}, [space]);

	return (
		<>
			<SectionHeader icon="semantic-search" title={title} />

			<ClayLayout.Row aria-label={title} className="mt-3" role="group">
				{ATTENTION_CARDS.map(
					({
						color,
						description,
						hoverContent,
						icon,
						statKey,
						title,
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
								loading={loading}
								title={title}
								trend={placeholderTrend}
								value={(statKey && statistics?.[statKey]) || 0}
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
