/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

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

function getKey(suggestion: Suggestion): string {
	return `${suggestion.id ?? suggestion.name}`;
}

export default function CategorizationMessageBalloon({
	agent,
	classNameId,
	cmsGroupId,
	content,
	count,
	scopeId,
}: CategorizeEventPayload) {
	const [committed, setCommitted] = useState(false);
	const [dismissed, setDismissed] = useState<string[]>([]);

	const {regenerate, run, status, suggestions} =
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

			if (active) {
				run({content, count, ...data});
			}
		})();

		return () => {
			active = false;
		};
	}, [agent, classNameId, cmsGroupId, content, count, run, scopeId]);

	const visibleSuggestions = suggestions.filter(
		(suggestion) => !dismissed.includes(getKey(suggestion))
	);

	return (
		<div className="ai-assistant-chat__ai-assistant-message-balloon mb-2 p-2 rounded">
			<CategorizationSuggestions
				committed={committed}
				kind={
					agent === ECategorizationAgent.AUTO_CATEGORIZE
						? 'categories'
						: 'tags'
				}
				onCommit={(committedSuggestions) => {
					Liferay.fire(COMMIT_EVENT, {
						agent,
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
				status={status === 'idle' ? 'loading' : status}
				suggestions={visibleSuggestions}
			/>
		</div>
	);
}
