/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {sub} from 'frontend-js-web';
import React, {useEffect, useState} from 'react';

import CategorizationSuggestions from '../../Categorization/components/CategorizationSuggestions';
import {
	COMMIT_EVENT,
	CategorizeEventPayload,
} from '../../Categorization/events';
import {getCandidateCategories} from '../../Categorization/services/getCandidateCategories';
import {getExistingTags} from '../../Categorization/services/getExistingTags';
import {ECategorizationAgent, Suggestion} from '../../Categorization/types';
import useCategorizationAgent from '../../Categorization/useCategorizationAgent';
import AIAssistantMessageBalloonIcon from './AIAssistantMessageBalloonIcon';

interface CategorizationMessageBalloonProps extends CategorizeEventPayload {
	setIsGenerating: (isGenerating: boolean) => void;
}

function getKey(suggestion: Suggestion): string {
	return `${suggestion.id ?? suggestion.name}`;
}

export default function CategorizationMessageBalloon({
	agent,
	classNameId,
	cmsGroupId,
	content,
	count,
	currentCategoryIds,
	currentTagNames,
	scopeId,
	setIsGenerating,
	targets,
}: CategorizationMessageBalloonProps) {
	const [committed, setCommitted] = useState(false);
	const [dismissed, setDismissed] = useState<string[]>([]);

	const {regenerate, resolveTargets, run, status, suggestions} =
		useCategorizationAgent(agent);

	useEffect(() => {
		let active = true;

		const fetchCandidateCategories = async () => {
			try {
				return {
					candidateCategories: await getCandidateCategories({
						classNameId,
						cmsGroupId,
						scopeId,
					}),
				};
			}
			catch (error) {
				console.warn((error as Error).message);

				return {candidateCategories: []};
			}
		};

		const fetchExistingTags = async () => {
			try {
				return {
					existingTags: await getExistingTags({cmsGroupId, scopeId}),
				};
			}
			catch (error) {
				console.warn((error as Error).message);

				return {existingTags: []};
			}
		};

		(async () => {
			const data =
				agent === ECategorizationAgent.AUTO_CATEGORIZE
					? await fetchCandidateCategories()
					: await fetchExistingTags();

			if (!active) {
				return;
			}

			if (targets?.length) {
				resolveTargets({content, count, ...data}, targets);
			}
			else {
				run({content, count, ...data});
			}
		})();

		return () => {
			active = false;
		};
	}, [
		agent,
		classNameId,
		cmsGroupId,
		content,
		count,
		resolveTargets,
		run,
		scopeId,
		targets,
	]);

	const visibleSuggestions = suggestions.filter(
		(suggestion) => !dismissed.includes(getKey(suggestion))
	);

	const isCategories = agent === ECategorizationAgent.AUTO_CATEGORIZE;

	const newCategoryCount = visibleSuggestions.filter(
		(suggestion) =>
			typeof suggestion.id === 'number' &&
			!(currentCategoryIds ?? []).includes(suggestion.id)
	).length;

	const lowerCaseCurrentTagNames = (currentTagNames ?? []).map((name) =>
		name.toLowerCase()
	);

	const newTagCount = visibleSuggestions.filter(
		(suggestion) =>
			!lowerCaseCurrentTagNames.includes(suggestion.name.toLowerCase())
	).length;

	const committedCount = isCategories ? newCategoryCount : newTagCount;

	const confirmationMessage = sub(
		isCategories
			? Liferay.Language.get(
					'great-i-have-added-x-categories-to-your-content'
				)
			: Liferay.Language.get('great-i-have-added-x-tags-to-your-content'),
		`${committedCount}`
	);

	const isLoading = status === 'idle' || status === 'loading';

	useEffect(() => {
		setIsGenerating(isLoading);
	}, [isLoading, setIsGenerating]);

	if (isLoading) {
		return null;
	}

	return (
		<>
			<div className="ai-assistant-chat__ai-assistant-message-balloon d-flex flex-column mb-2 rounded">
				<div className="d-flex flex-row">
					<AIAssistantMessageBalloonIcon />

					<div className="flex-grow-1 m-2">
						<CategorizationSuggestions
							committed={committed}
							kind={isCategories ? 'categories' : 'tags'}
							onCommit={(committedSuggestions) => {
								Liferay.fire(COMMIT_EVENT, {
									agent,
									scopeId,
									suggestions: committedSuggestions,
								});

								setCommitted(true);
							}}
							onDismiss={(suggestion) =>
								setDismissed((previousDismissed) => [
									...previousDismissed,
									getKey(suggestion),
								])
							}
							onRegenerate={() => {
								setCommitted(false);
								setDismissed([]);

								regenerate();
							}}
							status={status}
							suggestions={visibleSuggestions}
						/>
					</div>
				</div>
			</div>

			{committed && committedCount > 0 ? (
				<div className="ai-assistant-chat__ai-assistant-message-balloon d-flex flex-column mb-2 rounded">
					<div className="d-flex flex-row">
						<AIAssistantMessageBalloonIcon />

						<div className="flex-grow-1 m-2">
							{confirmationMessage}
						</div>
					</div>
				</div>
			) : null}
		</>
	);
}
