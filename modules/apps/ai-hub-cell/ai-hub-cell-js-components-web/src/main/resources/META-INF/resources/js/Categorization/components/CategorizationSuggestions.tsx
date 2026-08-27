/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import ClayIcon from '@clayui/icon';
import {sub} from 'frontend-js-web';
import React from 'react';

import {CategorizationStatus, Suggestion} from '../types';
import SuggestionLabel from './SuggestionLabel';

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
			<span className="categorization-suggestions categorization-suggestions__loading-text font-weight-semi-bold">
				{kind === 'categories'
					? Liferay.Language.get('searching-for-categories')
					: Liferay.Language.get('generating-tags')}
			</span>
		);
	}

	if (status === 'error') {
		return (
			<span className="categorization-suggestions text-danger">
				{Liferay.Language.get('an-unexpected-error-occurred')}
			</span>
		);
	}

	if (status === 'stopped') {
		return (
			<span className="categorization-suggestions">
				{Liferay.Language.get(
					'this-request-was-replaced-by-a-newer-one'
				)}
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

			<div className="categorization-suggestions__labels mb-3">
				{suggestions.map((suggestion) => (
					<SuggestionLabel
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
					size="sm"
				>
					<ClayIcon
						className="mr-2"
						spritemap={Liferay.Icons.spritemap}
						symbol="reload"
					/>

					{Liferay.Language.get('try-again')}
				</ClayButton>

				<ClayButton
					disabled={committed || !suggestions.length}
					displayType="primary"
					onClick={() => onCommit(suggestions)}
					size="sm"
				>
					{kind === 'categories'
						? Liferay.Language.get('add-categories')
						: Liferay.Language.get('add-tags')}
				</ClayButton>
			</div>
		</div>
	);
}
