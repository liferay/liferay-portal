/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayForm, {ClayInput, ClaySelect, ClayToggle} from '@clayui/form';
import ClayList from '@clayui/list';
import classNames from 'classnames';
import React from 'react';

import RequiredMark from '../../components/RequiredMark';
import {
	MAX_CRAWL_DEPTH_OPTIONS,
	MAX_DURATION_OPTIONS,
	MAX_PAGES_OPTIONS,
	RANKING_METHOD_OPTIONS,
	SCOPE_OPTIONS,
} from '../constants';
import {EngineConfig, EngineKey, RankingMethod, ScanScope} from '../types';
import {getPathError} from '../validation';

interface Props {
	config: EngineConfig;
	engineKey: EngineKey;
	idPrefix: string;
	label: string;
	onChange: (config: EngineConfig) => void;
}

export default function EngineSection({
	config,
	engineKey,
	idPrefix,
	label,
	onChange,
}: Props) {
	function update(partial: Partial<EngineConfig>) {
		onChange({...config, ...partial});
	}

	const excludedPaths = config.scope === 'excludedPathsOnly';
	const showPaths = excludedPaths || config.scope === 'includedPathsOnly';

	const paths = excludedPaths ? config.excludedPaths : config.includedPaths;

	const pathError = showPaths ? getPathError(paths) : undefined;

	return (
		<ClayList.Item className="px-0">
			<ClayToggle
				containerProps={{className: 'd-flex'}}
				label={label}
				onToggle={(enabled) => update({enabled})}
				toggled={config.enabled}
			/>

			{config.enabled ? (
				<div className="mt-3">
					<ClayForm.Group>
						<label htmlFor={`${idPrefix}-scope`}>
							{Liferay.Language.get('scope')}
						</label>

						<ClaySelect
							id={`${idPrefix}-scope`}
							onChange={(event) =>
								update({scope: event.target.value as ScanScope})
							}
							value={config.scope}
						>
							{SCOPE_OPTIONS.map((option) => (
								<ClaySelect.Option
									key={option.value}
									label={option.label}
									value={option.value}
								/>
							))}
						</ClaySelect>
					</ClayForm.Group>

					{showPaths ? (
						<ClayForm.Group
							className={classNames({'has-error': pathError})}
						>
							<label htmlFor={`${idPrefix}-paths`}>
								{excludedPaths
									? Liferay.Language.get('excluded-path')
									: Liferay.Language.get('included-path')}

								<RequiredMark />
							</label>

							<ClayInput
								id={`${idPrefix}-paths`}
								onChange={(event) =>
									update(
										excludedPaths
											? {
													excludedPaths:
														event.target.value,
												}
											: {
													includedPaths:
														event.target.value,
												}
									)
								}
								placeholder={Liferay.Language.get('enter-path')}
								type="text"
								value={paths}
							/>

							{pathError ? (
								<ClayForm.FeedbackGroup>
									<ClayForm.FeedbackItem>
										{pathError}
									</ClayForm.FeedbackItem>
								</ClayForm.FeedbackGroup>
							) : null}
						</ClayForm.Group>
					) : null}

					<div className="row">
						<div className="col-12 col-md-6">
							<ClayForm.Group>
								<label htmlFor={`${idPrefix}-max-pages`}>
									{Liferay.Language.get('max-page-per-scan')}
								</label>

								<ClaySelect
									id={`${idPrefix}-max-pages`}
									onChange={(event) =>
										update({
											maxPagesPerScan: Number(
												event.target.value
											),
										})
									}
									value={String(config.maxPagesPerScan)}
								>
									{MAX_PAGES_OPTIONS.map((max) => (
										<ClaySelect.Option
											key={max}
											label={String(max)}
											value={String(max)}
										/>
									))}
								</ClaySelect>
							</ClayForm.Group>
						</div>

						<div className="col-12 col-md-6">
							<ClayForm.Group>
								<label htmlFor={`${idPrefix}-ranking-method`}>
									{Liferay.Language.get('ranking-method')}
								</label>

								<ClaySelect
									id={`${idPrefix}-ranking-method`}
									onChange={(event) =>
										update({
											rankingMethod: event.target
												.value as RankingMethod,
										})
									}
									value={config.rankingMethod}
								>
									{RANKING_METHOD_OPTIONS.map((option) => (
										<ClaySelect.Option
											key={option.value}
											label={option.label}
											value={option.value}
										/>
									))}
								</ClaySelect>
							</ClayForm.Group>
						</div>
					</div>

					{engineKey === 'crawler' ? (
						<div className="row">
							<div className="col-12 col-md-6">
								<ClayForm.Group>
									<label
										htmlFor={`${idPrefix}-max-crawl-depth`}
									>
										{Liferay.Language.get(
											'max-crawl-depth'
										)}
									</label>

									<ClaySelect
										id={`${idPrefix}-max-crawl-depth`}
										onChange={(event) =>
											update({
												maxCrawlDepth: Number(
													event.target.value
												),
											})
										}
										value={String(config.maxCrawlDepth)}
									>
										{MAX_CRAWL_DEPTH_OPTIONS.map((max) => (
											<ClaySelect.Option
												key={max}
												label={String(max)}
												value={String(max)}
											/>
										))}
									</ClaySelect>
								</ClayForm.Group>
							</div>

							<div className="col-12 col-md-6">
								<ClayForm.Group>
									<label htmlFor={`${idPrefix}-max-duration`}>
										{Liferay.Language.get('max-duration')}
									</label>

									<ClaySelect
										id={`${idPrefix}-max-duration`}
										onChange={(event) =>
											update({
												maxDuration: Number(
													event.target.value
												),
											})
										}
										value={String(config.maxDuration)}
									>
										{MAX_DURATION_OPTIONS.map((option) => (
											<ClaySelect.Option
												key={option.value}
												label={option.label}
												value={option.value}
											/>
										))}
									</ClaySelect>
								</ClayForm.Group>
							</div>
						</div>
					) : null}
				</div>
			) : null}
		</ClayList.Item>
	);
}
