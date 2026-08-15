/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayLayout from '@clayui/layout';
import {navigate} from 'frontend-js-web';
import React, {useContext, useEffect, useState} from 'react';

import {SectionHeader} from '../../common/SectionHeader';
import InteractiveCard, {
	MetricColor,
} from '../../performance/components/InteractiveCard';
import {GovernanceContext} from '../GovernanceContext';
import GovernanceService, {AssetStatistics} from '../GovernanceService';

type AttentionCard = {
	color: MetricColor;
	description: string;
	icon: string;
	statKey: keyof AssetStatistics;
	title: string;
};

const ATTENTION_CARDS: AttentionCard[] = [
	{
		color: 'red',
		description: Liferay.Language.get(
			'these-are-references-to-broken-links-across-the-selected-spaces'
		),
		icon: 'link',
		statKey: 'brokenLinksCount',
		title: Liferay.Language.get('broken-links'),
	},
	{
		color: 'orange',
		description: Liferay.Language.get(
			'these-are-expired-assets-across-the-selected-spaces'
		),
		icon: 'warning-full',
		statKey: 'expiredCount',
		title: Liferay.Language.get('expired-assets'),
	},
	{
		color: 'dark',
		description: Liferay.Language.get(
			'these-are-assets-with-overdue-reviews-across-the-selected-spaces'
		),
		icon: 'date-time',
		statKey: 'reviewDateOverdueCount',
		title: Liferay.Language.get('overdue-reviews'),
	},
	{
		color: 'purple',
		description: Liferay.Language.get(
			'these-are-assets-with-pending-workflows-across-the-selected-spaces'
		),
		icon: 'flag-empty',
		statKey: 'pendingCount',
		title: Liferay.Language.get('pending-workflows'),
	},
];

const SECTION_PATHS: Partial<Record<keyof AssetStatistics, string>> = {
	expiredCount: 'expired-assets',
	pendingCount: 'pending-workflows',
	reviewDateOverdueCount: 'overdue-reviews',
};

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

	const openSection = (path: string) => {
		const url = `${Liferay.ThemeDisplay.getPathFriendlyURLPublic()}/cms/${path}`;

		navigate(space.siteId ? `${url}?groupId=${space.siteId}` : url);
	};

	return (
		<>
			<SectionHeader icon="semantic-search" title={title} />

			<ClayLayout.Row aria-label={title} className="mt-3" role="group">
				{ATTENTION_CARDS.map(
					({color, description, icon, statKey, title}) => {
						const sectionPath = SECTION_PATHS[statKey];

						return (
							<ClayLayout.Col
								className="mb-3"
								key={title}
								md={6}
								xl={3}
							>
								<InteractiveCard
									color={color}
									description={description}
									icon={icon}
									loading={loading}
									onClick={
										sectionPath
											? () => openSection(sectionPath)
											: undefined
									}
									title={title}
									value={statistics?.[statKey] || 0}
								/>
							</ClayLayout.Col>
						);
					}
				)}
			</ClayLayout.Row>
		</>
	);
}
