/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import ClayIcon from '@clayui/icon';
import ClayLoadingIndicator from '@clayui/loading-indicator';
import {sub} from 'frontend-js-web';
import React from 'react';

import {CategorizationStatus, Suggestion} from '../types';
import SuggestionChip from './SuggestionChip';

import '../categorization.scss';

interface CategorizationSuggestionsProps {
	committed?: boolean;
	kind: 'categories' | 'tags';
	onCommit: (suggestions: Suggestion[]) => void;
	onDismiss: (suggestion: Suggestion) => void;
	onRegenerate: () => void;
	status: CategorizationStatus;
	suggestions: Suggestion[];
}

function getIntroText(
	kind: 'categories' | 'tags',
	suggestions: Suggestion[]
): string {
	if (kind === 'categories') {
		return sub(
			Liferay.Language.get('i-have-found-x-matching-categories'),
			`${suggestions.length}`
		);
	}

	const newCount = suggestions.filter(
		(suggestion) => suggestion.isNew
	).length;

	const existingCount = suggestions.length - newCount;

	if (newCount) {
		return sub(
			Liferay.Language.get(
				'i-found-x-existing-tags-and-suggest-x-new-tags'
			),
			`${existingCount}`,
			`${newCount}`
		);
	}

	return sub(
		Liferay.Language.get('i-found-x-existing-tags-for-this-content'),
		`${existingCount}`
	);
}

export default function CategorizationSuggestions({
	committed = false,
	kind,
	onCommit,
	onDismiss,
	onRegenerate,
	status,
	suggestions,
}: CategorizationSuggestionsProps) {
	if (status === 'idle') {
		return null;
	}

	if (status === 'loading') {
		return (
			<div className="align-items-center categorization-suggestions d-flex">
				<ClayLoadingIndicator className="mr-2" />

				<span className="font-weight-semi-bold text-secondary">
					{kind === 'categories'
						? Liferay.Language.get('searching-for-categories')
						: Liferay.Language.get('generating')}
				</span>
			</div>
		);
	}

	if (status === 'error') {
		return (
			<span className="categorization-suggestions text-danger">
				{Liferay.Language.get('an-unexpected-error-occurred')}
			</span>
		);
	}

	if (status === 'empty' || !suggestions.length) {
		return (
			<span className="categorization-suggestions">
				{kind === 'categories'
					? Liferay.Language.get(
							'i-have-not-found-any-matching-categories-what-would-you-like-to-do'
						)
					: Liferay.Language.get(
							'i-have-not-found-any-matching-tags-what-would-you-like-to-do'
						)}
			</span>
		);
	}

	return (
		<div className="categorization-suggestions">
			<p>{getIntroText(kind, suggestions)}</p>

			<div className="categorization-suggestions__chips mb-3">
				{suggestions.map((suggestion) => (
					<SuggestionChip
						disabled={committed}
						key={`${suggestion.id ?? suggestion.name}`}
						onDismiss={onDismiss}
						suggestion={suggestion}
					/>
				))}
			</div>

			<div className="d-flex justify-content-end">
				<ClayButton
					className="mr-2"
					disabled={committed}
					displayType="secondary"
					onClick={onRegenerate}
				>
					<ClayIcon
						className="mr-2"
						spritemap={Liferay.Icons.spritemap}
						symbol="reload"
					/>

					{kind === 'categories'
						? Liferay.Language.get('try-again')
						: Liferay.Language.get('regenerate')}
				</ClayButton>

				<ClayButton
					disabled={committed || !suggestions.length}
					displayType="primary"
					onClick={() => onCommit(suggestions)}
				>
					{kind === 'categories'
						? Liferay.Language.get('save-categories')
						: Liferay.Language.get('add-tags')}
				</ClayButton>
			</div>
		</div>
	);
}
