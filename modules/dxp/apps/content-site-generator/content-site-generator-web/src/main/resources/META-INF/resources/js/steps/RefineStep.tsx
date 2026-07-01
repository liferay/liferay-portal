/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayAlert from '@clayui/alert';
import ClayIcon from '@clayui/icon';
import ClayLabel from '@clayui/label';
import React, {useState} from 'react';

import ContentSample from '../components/ContentSample';
import StepActions from '../components/StepActions';
import SummaryStats from '../components/SummaryStats';
import {
	buildDetectedConfig,
	buildSummary,
	buildTemplates,
	parseContentSample,
} from '../util/contentModel';

import type {Generation} from '../types/Generation';
import type {GenerationItem} from '../types/GenerationItem';

interface IProps {
	generation: Generation;
	items: GenerationItem[];
	onBack: () => void;
	onCancel: () => void;
	onContinue: () => void;
}

export default function RefineStep({
	generation,
	items,
	onBack,
	onCancel,
	onContinue,
}: IProps) {
	const [tipVisible, setTipVisible] = useState(true);

	const detectedConfig = buildDetectedConfig(generation, items);
	const summary = buildSummary(generation, items);
	const templates = buildTemplates(items);

	const languageCount = Math.max(1, detectedConfig.languageLabels.length);

	const samples = items
		.map((item) => parseContentSample(item))
		.filter(
			(sample): sample is NonNullable<typeof sample> => sample !== null
		);

	const totalEntries = items.reduce(
		(sum, item) => sum + (item.itemCount ?? 0),
		0
	);

	return (
		<div className="content-site-generator__refine">
			<div className="content-site-generator__refine-header">
				<h3>
					{Liferay.Language.get('preview-content-to-be-generated')}
				</h3>

				<p className="text-secondary">
					{Liferay.Language.get(
						'review-the-configuration-and-content-samples-before-generating'
					)}
				</p>
			</div>

			<SummaryStats stats={summary} />

			<div className="card content-site-generator__prompt-card">
				<div className="card-body">
					<h4>{Liferay.Language.get('your-prompt')}</h4>

					<p className="text-secondary">
						<em>{`"${generation.prompt}"`}</em>
					</p>
				</div>
			</div>

			<div className="card content-site-generator__config-card">
				<div className="card-body">
					<h4>{Liferay.Language.get('detected-configuration')}</h4>

					<dl className="content-site-generator__config-list">
						<dt>{Liferay.Language.get('languages')}</dt>

						<dd>
							{detectedConfig.languageLabels.length
								? detectedConfig.languageLabels.join(', ')
								: Liferay.Language.get('default-language')}
						</dd>
					</dl>
				</div>
			</div>

			{!templates.length && (
				<>
					<h4 className="content-site-generator__section-title">
						{Liferay.Language.get('content-by-template-type')}
					</h4>

					<p className="text-secondary">
						{Liferay.Language.get('no-results-found')}
					</p>
				</>
			)}

			{!!templates.length && (
				<>
					<h4 className="content-site-generator__section-title">
						{Liferay.Language.get('content-by-template-type')}
					</h4>

					<ul className="content-site-generator__template-list list-unstyled">
						{templates.map((template) => (
							<li
								className="card content-site-generator__template-item"
								key={template.label}
							>
								<div className="card-body">
									<div className="content-site-generator__template-heading">
										<ClayIcon
											className="mr-2 text-secondary"
											spritemap={Liferay.Icons.spritemap}
											symbol={template.icon}
										/>

										<h5>{template.label}</h5>
									</div>

									<span className="text-secondary">
										{Liferay.Util.sub(
											Liferay.Language.get('x-entries'),
											String(template.itemCount)
										)}
									</span>

									<div className="content-site-generator__template-badges">
										{template.languageCount > 0 && (
											<ClayLabel displayType="success">
												{Liferay.Util.sub(
													Liferay.Language.get(
														'x-languages'
													),
													String(
														template.languageCount
													)
												)}
											</ClayLabel>
										)}

										{template.pageCount > 0 && (
											<ClayLabel displayType="info">
												{Liferay.Util.sub(
													Liferay.Language.get(
														'x-pages'
													),
													String(template.pageCount)
												)}
											</ClayLabel>
										)}
									</div>
								</div>
							</li>
						))}
					</ul>

					{!!samples.length && (
						<>
							<h4 className="content-site-generator__section-title">
								{Liferay.Language.get('content-samples')}
							</h4>

							<p className="text-secondary">
								{Liferay.Language.get(
									'preview-of-how-your-generated-content-will-be-structured'
								)}
							</p>

							<div className="content-site-generator__samples">
								{samples.map((sample, index) => (
									<ContentSample
										defaultExpanded={index === 0}
										key={sample.title}
										sample={sample}
									/>
								))}
							</div>
						</>
					)}

					<div className="content-site-generator__will-generate">
						<h4 className="content-site-generator__section-title">
							{Liferay.Language.get('what-will-be-generated')}
						</h4>

						<ul className="content-site-generator__checklist list-unstyled">
							<li>
								<ClayIcon
									className="mr-2 text-success"
									spritemap={Liferay.Icons.spritemap}
									symbol="check-circle"
								/>

								{Liferay.Util.sub(
									Liferay.Language.get(
										'x-complete-content-entries-across-x-content-types-and-x-languages'
									),
									String(totalEntries),
									String(templates.length),
									String(languageCount)
								)}
							</li>

							<li>
								<ClayIcon
									className="mr-2 text-success"
									spritemap={Liferay.Icons.spritemap}
									symbol="check-circle"
								/>

								{Liferay.Language.get(
									'multi-language-support-for-each-content-type'
								)}
							</li>

							<li>
								<ClayIcon
									className="mr-2 text-success"
									spritemap={Liferay.Icons.spritemap}
									symbol="check-circle"
								/>

								{Liferay.Language.get(
									'layout-specific-structures-optimized-for-each-content-type'
								)}
							</li>
						</ul>
					</div>
				</>
			)}

			{tipVisible && (
				<ClayAlert
					displayType="info"
					onClose={() => setTipVisible(false)}
					title={Liferay.Language.get('tip')}
				>
					{Liferay.Language.get(
						'use-the-chat-on-the-left-to-refine-your-requirements-before-generating'
					)}
				</ClayAlert>
			)}

			<StepActions
				backLabel={Liferay.Language.get('back-to-prompt')}
				continueDisabled={!templates.length}
				continueLabel={Liferay.Language.get('continue-to-review')}
				onBack={onBack}
				onCancel={onCancel}
				onContinue={onContinue}
			/>
		</div>
	);
}
