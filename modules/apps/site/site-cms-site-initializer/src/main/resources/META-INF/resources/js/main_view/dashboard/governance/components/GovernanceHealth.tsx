/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Text} from '@clayui/core';
import ClayLoadingIndicator from '@clayui/loading-indicator';
import React, {useContext} from 'react';

import HelpTooltipIcon from '../../../../common/components/forms/HelpTooltipIcon';
import {GovernanceContext} from '../GovernanceContext';
import getGovernanceHealth from '../getGovernanceHealth';

import './GovernanceHealth.scss';

const MAX_SCORE = 100;

function SubScore({label, value}: {label: string; value: number}) {
	return (
		<div className="cms-governance-health__sub-score">
			<span className="d-block font-weight-bold text-7">{value}</span>

			<span className="d-block font-weight-semi-bold text-2 text-secondary">
				{label}
			</span>
		</div>
	);
}

export function GovernanceHealth() {
	const {loadingStatistics, statistics} = useContext(GovernanceContext);

	const title = Liferay.Language.get('governance-health');

	const health = statistics ? getGovernanceHealth(statistics) : undefined;

	return (
		<section
			aria-label={title}
			className="align-items-center cms-governance-health d-flex justify-content-between my-4 pl-4 pr-5 py-3 rounded-lg"
		>
			<div className="mb-2">
				<div className="align-items-center d-flex my-1">
					<Text size={6} weight="semi-bold">
						{title}
					</Text>

					<HelpTooltipIcon
						className="ml-2 mt-1 text-4"
						message={Liferay.Language.get(
							'the-governance-health-score-summarizes-how-well-the-content-in-the-selected-spaces-is-maintained'
						)}
					/>
				</div>

				{loadingStatistics ? (
					<ClayLoadingIndicator size="sm" />
				) : health ? (
					<div className="align-items-baseline d-flex">
						<Text size={9} weight="bold">
							{health.score}
						</Text>

						<Text color="secondary" size={7}>
							/{MAX_SCORE}
						</Text>
					</div>
				) : null}
			</div>

			{health ? (
				<div className="c-gap-5 d-flex mr-3 mt-4">
					<SubScore
						label={Liferay.Language.get('reliability')}
						value={health.reliability}
					/>

					<SubScore
						label={Liferay.Language.get('freshness')}
						value={health.freshness}
					/>
				</div>
			) : null}
		</section>
	);
}
