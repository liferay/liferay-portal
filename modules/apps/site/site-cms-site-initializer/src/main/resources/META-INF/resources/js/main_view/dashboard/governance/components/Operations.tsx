/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayLayout from '@clayui/layout';
import React, {useState} from 'react';

import {SectionHeader} from '../../common/SectionHeader';
import {GovernanceAdditionalProps} from '../types';
import {ContentProgress} from './ContentProgress';
import {ContributorConcentration} from './ContributorConcentration';
import {
	LongStandingDraftsCard,
	LongStandingDraftsList,
} from './LongStandingDrafts';
import {OverdueTasksCard, OverdueTasksList} from './OverdueTasks';
import {SLABreachesCard} from './SLABreaches';

type ExpandedList = 'longStandingDrafts' | 'overdueTasks' | null;

export function Operations({
	additionalProps,
}: {
	additionalProps: GovernanceAdditionalProps;
}) {
	const [expandedList, setExpandedList] = useState<ExpandedList>(null);

	const toggleList = (list: ExpandedList) =>
		setExpandedList((expanded) => (expanded === list ? null : list));

	const title = Liferay.Language.get('operations');

	return (
		<div aria-label={title} className="mb-3 py-4" role="group">
			<SectionHeader icon="organizations" title={title} />

			<ClayLayout.Row className="mt-4">
				<ClayLayout.Col className="mb-4" md={4}>
					<OverdueTasksCard
						expanded={expandedList === 'overdueTasks'}
						onClick={() => toggleList('overdueTasks')}
					/>
				</ClayLayout.Col>

				<ClayLayout.Col className="mb-4" md={4}>
					<LongStandingDraftsCard
						expanded={expandedList === 'longStandingDrafts'}
						onClick={() => toggleList('longStandingDrafts')}
					/>
				</ClayLayout.Col>

				<ClayLayout.Col className="mb-4" md={4}>
					<SLABreachesCard />
				</ClayLayout.Col>

				{expandedList === 'overdueTasks' ? (
					<ClayLayout.Col className="mb-4" size={12}>
						<OverdueTasksList additionalProps={additionalProps} />
					</ClayLayout.Col>
				) : null}

				{expandedList === 'longStandingDrafts' ? (
					<ClayLayout.Col className="mb-4" size={12}>
						<LongStandingDraftsList
							additionalProps={additionalProps}
						/>
					</ClayLayout.Col>
				) : null}
			</ClayLayout.Row>

			<ClayLayout.Row>
				<ClayLayout.Col md={6}>
					<ContentProgress additionalProps={additionalProps} />
				</ClayLayout.Col>

				<ClayLayout.Col md={6}>
					<ContributorConcentration
						additionalProps={additionalProps}
					/>
				</ClayLayout.Col>
			</ClayLayout.Row>
		</div>
	);
}
