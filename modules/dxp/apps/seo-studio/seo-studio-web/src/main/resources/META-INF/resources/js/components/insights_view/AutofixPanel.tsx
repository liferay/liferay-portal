/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import ClayIcon from '@clayui/icon';
import {openToast} from 'frontend-js-components-web';
import {sub} from 'frontend-js-web';
import React, {useEffect, useState} from 'react';

import {AUTOFIX_DEFINITIONS} from './autofix_definitions/AutofixDefinitions';
import {
	generateCandidates,
	getPageContent,
	patchScanInsight,
	postAutofix,
} from './services/AutofixService';
import {AutofixCandidate, ScanInsightItem} from './types/Autofix';

import './AutofixPanel.scss';

function getPageSummary(item: ScanInsightItem): string {
	const page = item.r_seoStudioPageToSEOStudioScanInsights_seoStudioPage;

	return [
		page?.title ? `Title: ${page.title}` : '',
		page?.pageURL ? `URL: ${page.pageURL}` : '',
		page?.type ? `Type: ${page.type}` : '',
	]
		.filter(Boolean)
		.join('\n');
}

function getPageName(item: ScanInsightItem): string {
	const page = item.r_seoStudioPageToSEOStudioScanInsights_seoStudioPage;

	return page?.title || page?.pageURL || '';
}

export default function AutofixPanel({
	insightTypeName,
	item,
	onClose,
	onResolved,
}: {
	insightTypeName: string;
	item: ScanInsightItem;
	onClose: () => void;
	onResolved: () => void;
}) {
	const [applying, setApplying] = useState(false);
	const [candidates, setCandidates] = useState<AutofixCandidate[]>([]);
	const [generating, setGenerating] = useState(true);

	const definition = AUTOFIX_DEFINITIONS[insightTypeName];
	const pageName = getPageName(item);

	useEffect(() => {
		if (!definition) {
			return;
		}

		const controller = new AbortController();
		const pageURL =
			item.r_seoStudioPageToSEOStudioScanInsights_seoStudioPage?.pageURL;

		setGenerating(true);

		(pageURL ? getPageContent(pageURL) : Promise.resolve(''))
			.then((fetchedContent) =>
				generateCandidates(
					definition,
					[getPageSummary(item), fetchedContent]
						.filter(Boolean)
						.join('\n\n'),
					controller.signal
				)
			)
			.then((fixCandidates) => {
				setCandidates(fixCandidates);
				setGenerating(false);
			})
			.catch((error) => {
				if (controller.signal.aborted) {
					return;
				}

				setGenerating(false);

				openToast({
					message:
						error?.message || definition.getGenerateErrorMessage(),
					type: 'danger',
				});
			});

		return () => {
			controller.abort();
		};
	}, [definition, item]);

	if (!definition) {
		return null;
	}

	const handleApply = async (value: string) => {
		const pageURL =
			item.r_seoStudioPageToSEOStudioScanInsights_seoStudioPage?.pageURL;

		if (!pageURL) {
			openToast({
				message: definition.getApplyErrorMessage(),
				type: 'danger',
			});

			return;
		}

		setApplying(true);

		try {
			await postAutofix({
				insightType: definition.insightTypeName,
				pageURL,
				value,
			});
		}
		catch {
			setApplying(false);

			openToast({
				message: definition.getApplyErrorMessage(),
				type: 'danger',
			});

			return;
		}

		try {
			await patchScanInsight(item.id);
		}
		catch {
			setApplying(false);

			openToast({
				message: definition.getResolvedPartialMessage(),
				type: 'danger',
			});

			return;
		}

		openToast({
			message: definition.getResolvedSuccessMessage(),
			type: 'success',
		});

		onResolved();
		onClose();
	};

	return (
		<div className="seo-studio-autofix-panel">
			<div className="seo-studio-autofix-panel-header">
				<h4 className="mb-0">{Liferay.Language.get('ai-assistant')}</h4>

				<ClayButton
					aria-label={Liferay.Language.get('close')}
					borderless
					displayType="secondary"
					monospaced
					onClick={onClose}
				>
					<ClayIcon symbol="times" />
				</ClayButton>
			</div>

			<div className="seo-studio-autofix-panel-body">
				<div className="seo-studio-autofix-message seo-studio-autofix-message-user">
					{definition.getPromptMessage(pageName)}
				</div>

				{generating && (
					<div className="seo-studio-autofix-generating seo-studio-autofix-message seo-studio-autofix-message-assistant">
						<span
							aria-hidden="true"
							className="loading-animation loading-animation-secondary mr-2"
						/>

						{Liferay.Language.get('generating')}
					</div>
				)}

				{!generating && !!candidates.length && (
					<div className="seo-studio-autofix-message seo-studio-autofix-message-assistant">
						<p className="mb-3">
							{definition.getSuggestionsIntroMessage()}
						</p>

						{candidates.map((candidate, index) => (
							<div
								className="seo-studio-autofix-candidate"
								key={index}
							>
								<p className="font-weight-semi-bold mb-1">
									{definition.getCandidateLabel(candidate)}
								</p>

								{candidate.rationale && (
									<p className="text-2 text-secondary">
										{candidate.rationale}
									</p>
								)}

								<ClayButton
									disabled={applying}
									displayType="secondary"
									onClick={() => handleApply(candidate.value)}
									small
								>
									{sub(
										Liferay.Language.get('apply-option-x'),
										String(index + 1)
									)}
								</ClayButton>
							</div>
						))}
					</div>
				)}

				{!generating && !candidates.length && (
					<div className="seo-studio-autofix-message seo-studio-autofix-message-assistant">
						{Liferay.Language.get('no-suggestions-were-generated')}
					</div>
				)}
			</div>
		</div>
	);
}
