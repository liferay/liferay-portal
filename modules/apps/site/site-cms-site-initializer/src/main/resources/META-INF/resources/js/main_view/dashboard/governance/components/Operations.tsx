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
import {OverdueTasksCard} from './OverdueTasks';

export function Operations({
	additionalProps,
}: {
	additionalProps: GovernanceAdditionalProps;
}) {
	const [longStandingDraftsExpanded, setLongStandingDraftsExpanded] =
		useState(false);

	const title = Liferay.Language.get('operations');

	return (
		<div aria-label={title} className="mb-3 py-4" role="group">
			<SectionHeader icon="organizations" title={title} />

			<ClayLayout.Row className="mt-4">
				<ClayLayout.Col className="mb-4" md={4}>
					<OverdueTasksCard />
				</ClayLayout.Col>

				<ClayLayout.Col className="mb-4" md={4}>
					<LongStandingDraftsCard
						expanded={longStandingDraftsExpanded}
						onClick={() =>
							setLongStandingDraftsExpanded(
								(expanded) => !expanded
							)
						}
					/>
				</ClayLayout.Col>

				{longStandingDraftsExpanded ? (
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
