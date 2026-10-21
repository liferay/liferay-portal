/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ClayButtonWithIcon} from '@clayui/button';
import {Text} from '@clayui/core';
import ClayLoadingIndicator from '@clayui/loading-indicator';
import ClayPopover from '@clayui/popover';
import {sub} from 'frontend-js-web';
import React, {useContext, useState} from 'react';

import {getImage} from '../../../../common/utils/getImage';
import {GovernanceContext} from '../GovernanceContext';
import getGovernanceHealth, {GovernanceDimension} from '../getGovernanceHealth';

import './GovernanceHealth.scss';

const MAX_SCORE = 100;

const SCORE_RULES = [
	Liferay.Language.get('governance-health-issues-help'),
	Liferay.Language.get('governance-health-warnings-help'),
	Liferay.Language.get('governance-health-informational-help'),
];

function ScoreHelp() {
	const [show, setShow] = useState(false);

	return (
		<ClayPopover
			alignPosition="right-top"
			closeOnClickOutside
			disableScroll
			header={Liferay.Language.get('about-this-dashboard')}
			onShowChange={setShow}
			show={show}
			size="lg"
			trigger={
				<ClayButtonWithIcon
					aria-expanded={show}
					aria-haspopup="dialog"
					aria-label={Liferay.Language.get('about-governance-health')}
					className="text-secondary"
					displayType="unstyled"
					size="sm"
					symbol="question-circle"
				/>
			}
		>
			<p>{Liferay.Language.get('governance-health-help')}</p>

			<ul className="mb-0 pl-4">
				{SCORE_RULES.map((rule) => (
					<li key={rule}>{rule}</li>
				))}
			</ul>
		</ClayPopover>
	);
}

function getIssuesLabel(issues: number, warnings = 0) {
	const issuesLabel = sub(
		issues === 1
			? Liferay.Language.get('x-issue')
			: Liferay.Language.get('x-issues'),
		issues
	);

	if (!warnings) {
		return issuesLabel;
	}

	return `${issuesLabel} · ${sub(
		warnings === 1
			? Liferay.Language.get('x-warning')
			: Liferay.Language.get('x-warnings'),
		warnings
	)}`;
}

function SubScore({
	dimension: {issues, score, warnings},
	label,
}: {
	dimension: GovernanceDimension;
	label: string;
}) {
	return (
		<div className="cms-governance-health__sub-score">
			<span className="d-block font-weight-semi-bold text-9">
				{score}
			</span>

			<span className="d-block font-weight-semi-bold text-3 text-secondary">
				{label}
			</span>

			<span className="d-block text-2 text-secondary">
				{getIssuesLabel(issues, warnings)}
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
			aria-busy={loadingStatistics}
			aria-label={title}
			className="align-items-lg-center cms-governance-health d-flex flex-column flex-lg-row justify-content-between my-4 pl-4 pr-4 pr-lg-5 py-4 rounded-lg"
			style={{
				backgroundImage: `url(${getImage('governance_health_banner.svg')})`,
			}}
		>
			<div className="flex-shrink-0 my-2">
				<div className="align-items-center d-flex mb-2">
					<Text size={6} weight="semi-bold">
						{title}
					</Text>

					<ScoreHelp />
				</div>

				{health ? (
					<>
						<div className="align-items-baseline d-flex">
							<Text size={11} weight="bold">
								{health.score}
							</Text>

							<Text size={9}>/{MAX_SCORE}</Text>
						</div>

						<Text color="secondary" size={3}>
							{getIssuesLabel(health.issues)}
						</Text>
					</>
				) : loadingStatistics ? (
					<ClayLoadingIndicator size="sm" />
				) : null}
			</div>

			{health ? (
				<div className="c-gap-4 d-flex flex-wrap mt-3 mt-lg-0">
					<SubScore
						dimension={health.quality}
						label={Liferay.Language.get('quality')}
					/>

					<SubScore
						dimension={health.freshness}
						label={Liferay.Language.get('freshness')}
					/>

					<SubScore
						dimension={health.operations}
						label={Liferay.Language.get('operations')}
					/>
				</div>
			) : null}
		</section>
	);
}
