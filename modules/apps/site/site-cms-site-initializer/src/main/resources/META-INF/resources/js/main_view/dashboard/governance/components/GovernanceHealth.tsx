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

const METRICS = [
	Liferay.Language.get('quality-help'),
	Liferay.Language.get('freshness-help'),
	Liferay.Language.get('operations-help'),
];

function ScoreHelp({title}: {title: string}) {
	const [show, setShow] = useState(false);

	return (
		<ClayPopover
			alignPosition="right-top"
			closeOnClickOutside
			disableScroll
			header={title}
			onShowChange={setShow}
			show={show}
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
				{METRICS.map((metric) => (
					<li key={metric}>{metric}</li>
				))}
			</ul>
		</ClayPopover>
	);
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
				{sub(
					issues === 1
						? Liferay.Language.get('x-active-issue')
						: Liferay.Language.get('x-active-issues'),
					issues
				)}

				{warnings
					? ` · ${sub(
							warnings === 1
								? Liferay.Language.get('x-warning')
								: Liferay.Language.get('x-warnings'),
							warnings
						)}`
					: null}
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
			className="align-items-lg-center cms-governance-health d-flex flex-column flex-lg-row justify-content-between my-4 pl-4 pr-4 pr-lg-5 py-4 rounded-lg"
			style={{
				backgroundImage: `url(${getImage('governance_health_banner.svg')})`,
			}}
		>
			<div className="flex-shrink-0 mb-2">
				<div className="align-items-center d-flex my-1">
					<Text size={6} weight="semi-bold">
						{title}
					</Text>

					<ScoreHelp title={title} />
				</div>

				{loadingStatistics ? (
					<ClayLoadingIndicator size="sm" />
				) : health ? (
					<div className="align-items-baseline d-flex mb-1">
						<Text size={11} weight="bold">
							{health.score}
						</Text>

						<Text size={9}>/{MAX_SCORE}</Text>
					</div>
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
