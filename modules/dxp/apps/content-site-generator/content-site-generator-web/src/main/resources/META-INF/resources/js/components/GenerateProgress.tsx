/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayIcon from '@clayui/icon';
import ClayLabel from '@clayui/label';
import React, {useEffect, useMemo, useState} from 'react';

import {buildTemplates, getItemLanguages} from '../util/contentModel';
import SummaryStats from './SummaryStats';

import type {SummaryStat} from '../types/ContentModel';
import type {Generation} from '../types/Generation';
import type {GenerationItem} from '../types/GenerationItem';

const PHASE_ADVANCE_INTERVAL = 1800;

interface IProps {
	generation: Generation;
	items: GenerationItem[];
}

export default function GenerateProgress({generation, items}: IProps) {
	const ready =
		generation.generationStatus.key === 'ready' ||
		generation.generationStatus.key === 'committed';

	const languages = useMemo(() => {
		const languagesSet = new Set<string>();

		for (const item of items) {
			for (const language of getItemLanguages(item)) {
				languagesSet.add(language);
			}
		}

		return languagesSet;
	}, [items]);

	const phases = useMemo(() => {
		const basePhases = [
			Liferay.Language.get('analyzing-your-prompt'),
			Liferay.Language.get('extracting-key-topics-and-features'),
			Liferay.Language.get('generating-content'),
			Liferay.Language.get('generating-content-pages'),
		];

		if (languages.size > 1) {
			return [
				...basePhases,
				Liferay.Language.get('localizing-to-target-languages'),
			];
		}

		return basePhases;
	}, [languages]);

	const [step, setStep] = useState(0);

	useEffect(() => {
		if (ready) {
			return;
		}

		const intervalId = setInterval(() => {
			setStep((current) => Math.min(current + 1, phases.length - 1));
		}, PHASE_ADVANCE_INTERVAL);

		return () => clearInterval(intervalId);
	}, [phases.length, ready]);

	const phaseIndex = ready ? phases.length : step;

	const stats: SummaryStat[] = [
		{
			icon: 'document',
			label: Liferay.Language.get('content-entries'),
			value: items.reduce((sum, item) => sum + (item.itemCount ?? 0), 0),
		},
		{
			icon: 'page',
			label: Liferay.Language.get('content-pages'),
			value: buildTemplates(items).reduce(
				(sum, template) => sum + template.pageCount,
				0
			),
		},
		{
			icon: 'automatic-translate',
			label: Liferay.Language.get('languages'),
			value: languages.size,
		},
	];

	return (
		<div className="content-site-generator__generate">
			<h3 className="content-site-generator__section-title">
				{Liferay.Language.get('generate')}
			</h3>

			<SummaryStats stats={stats} />

			<ul className="content-site-generator__stages list-unstyled">
				{phases.map((phaseLabel, index) => {
					const completed = index < phaseIndex;
					const active = index === phaseIndex && !ready;

					const status = completed
						? 'completed'
						: active
							? 'in-progress'
							: 'pending';

					return (
						<li
							className={`content-site-generator__stage content-site-generator__stage--${status}`}
							key={phaseLabel}
						>
							<div className="content-site-generator__stage-header">
								<span className="content-site-generator__stage-bullet">
									{completed && (
										<ClayIcon
											className="text-success"
											spritemap={Liferay.Icons.spritemap}
											symbol="check-circle-full"
										/>
									)}

									{active && (
										<span
											aria-hidden="true"
											className="content-site-generator__stage-bullet--in-progress"
										/>
									)}

									{!completed && !active && (
										<span
											aria-hidden="true"
											className="content-site-generator__stage-bullet--pending"
										/>
									)}
								</span>

								<span className="content-site-generator__stage-label">
									{phaseLabel}
								</span>

								{completed && (
									<ClayLabel displayType="success">
										{Liferay.Language.get('done')}
									</ClayLabel>
								)}

								{active && (
									<ClayLabel displayType="info">
										{Liferay.Language.get('in-progress')}
									</ClayLabel>
								)}
							</div>

							{(completed || active) && (
								<div className="content-site-generator__stage-progress">
									<div className="progress">
										<div
											className={
												completed
													? 'progress-bar bg-success'
													: 'progress-bar progress-bar-animated progress-bar-striped'
											}
											style={{width: '100%'}}
										/>
									</div>

									{completed && (
										<ClayIcon
											className="content-site-generator__stage-progress-check text-success"
											spritemap={Liferay.Icons.spritemap}
											symbol="check-circle"
										/>
									)}
								</div>
							)}
						</li>
					);
				})}
			</ul>
		</div>
	);
}
