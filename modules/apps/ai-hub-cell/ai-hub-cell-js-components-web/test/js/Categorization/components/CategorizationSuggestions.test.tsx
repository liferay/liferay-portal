/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {fireEvent, render, screen} from '@testing-library/react';
import React from 'react';

import '@testing-library/jest-dom';

import CategorizationSuggestions from '../../../../src/main/resources/META-INF/resources/js/Categorization/components/CategorizationSuggestions';

const noop = () => {};

const baseProps = {
	onCommit: noop,
	onDismiss: noop,
	onRegenerate: noop,
	suggestions: [],
};

describe('CategorizationSuggestions', () => {
	it('disables the actions once committed', () => {
		render(
			<CategorizationSuggestions
				{...baseProps}
				committed
				kind="categories"
				status="ready"
				suggestions={[{id: 1, name: 'International'}]}
			/>
		);

		expect(
			screen.getByRole('button', {name: 'add-categories'})
		).toBeDisabled();
		expect(screen.getByRole('button', {name: 'try-again'})).toBeDisabled();
	});

	it('fires dismiss when a label close button is clicked', () => {
		const onDismiss = jest.fn();
		const suggestion = {isNew: false, name: 'Japan'};

		render(
			<CategorizationSuggestions
				{...baseProps}
				kind="tags"
				onDismiss={onDismiss}
				status="ready"
				suggestions={[suggestion]}
			/>
		);

		fireEvent.click(screen.getByRole('button', {name: 'remove'}));

		expect(onDismiss).toHaveBeenCalledWith(suggestion);
	});

	it('fires try again', () => {
		const onRegenerate = jest.fn();

		render(
			<CategorizationSuggestions
				{...baseProps}
				kind="tags"
				onRegenerate={onRegenerate}
				status="ready"
				suggestions={[{isNew: false, name: 'Japan'}]}
			/>
		);

		fireEvent.click(screen.getByRole('button', {name: 'try-again'}));

		expect(onRegenerate).toHaveBeenCalled();
	});

	it('flags new tags with the new class', () => {
		const {container} = render(
			<CategorizationSuggestions
				{...baseProps}
				kind="tags"
				status="ready"
				suggestions={[{isNew: true, name: 'Culture'}]}
			/>
		);

		expect(
			container.querySelector('.categorization-suggestion-label--new')
		).toBeInTheDocument();
	});

	it('renders labels and commits the suggestions', () => {
		const onCommit = jest.fn();
		const suggestions = [
			{id: 1, name: 'International'},
			{id: 2, name: 'Roadtrip'},
		];

		render(
			<CategorizationSuggestions
				{...baseProps}
				kind="categories"
				onCommit={onCommit}
				status="ready"
				suggestions={suggestions}
			/>
		);

		expect(screen.getByText('International')).toBeInTheDocument();
		expect(screen.getByText('Roadtrip')).toBeInTheDocument();

		fireEvent.click(screen.getByRole('button', {name: 'add-categories'}));

		expect(onCommit).toHaveBeenCalledWith(suggestions);
	});

	it('renders nothing when idle', () => {
		const {container} = render(
			<CategorizationSuggestions
				{...baseProps}
				kind="categories"
				status="idle"
			/>
		);

		expect(container).toBeEmptyDOMElement();
	});

	it('shows the categories loading text', () => {
		render(
			<CategorizationSuggestions
				{...baseProps}
				kind="categories"
				status="loading"
			/>
		);

		expect(
			screen.getByText('searching-for-categories')
		).toBeInTheDocument();
	});

	it('shows the no-match text when empty', () => {
		render(
			<CategorizationSuggestions
				{...baseProps}
				kind="categories"
				status="empty"
			/>
		);

		expect(
			screen.getByText(
				'i-have-not-found-any-matching-categories-what-would-you-like-to-do'
			)
		).toBeInTheDocument();
	});

	it('shows the tags loading text', () => {
		render(
			<CategorizationSuggestions
				{...baseProps}
				kind="tags"
				status="loading"
			/>
		);

		expect(screen.getByText('generating-tags')).toBeInTheDocument();
	});
});
