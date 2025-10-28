/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {render} from '@testing-library/react';
import React from 'react';

import SynonymSetsForm from '../../../src/main/resources/META-INF/resources/js/components/SynonymSetsForm.es';

import '@testing-library/jest-dom';

const FORM_NAME = 'testForm';

const INPUT_NAME = 'testInput';

const ORIGINAL_INPUT_NAME = 'testOriginalInput';

describe('SynonymSetsForm', () => {
	it('renders with no existing synonyms', () => {
		const {container} = render(
			<SynonymSetsForm
				formName={FORM_NAME}
				inputName={INPUT_NAME}
				originalInputName={ORIGINAL_INPUT_NAME}
				synonymSets=""
			/>
		);

		expect(container).not.toBeNull();
	});

	it('renders with existing synonyms', () => {
		const {container} = render(
			<SynonymSetsForm
				formName={FORM_NAME}
				inputName={INPUT_NAME}
				originalInputName={ORIGINAL_INPUT_NAME}
				synonymSets="one,two,three"
			/>
		);

		expect(container).not.toBeNull();
	});

	it('has title', () => {
		const {getByText} = render(
			<SynonymSetsForm
				formName={FORM_NAME}
				inputName={INPUT_NAME}
				originalInputName={ORIGINAL_INPUT_NAME}
				synonymSets="one,two,three"
			/>
		);

		expect(getByText('new-synonym-set')).toBeInTheDocument();
	});

	it('has description text', () => {
		const {getByText} = render(
			<SynonymSetsForm
				formName={FORM_NAME}
				inputName={INPUT_NAME}
				originalInputName={ORIGINAL_INPUT_NAME}
				synonymSets="one,two,three"
			/>
		);

		expect(
			getByText(
				'broaden-the-scope-of-search-by-treating-terms-equally-using-synonyms'
			)
		).toBeInTheDocument();
	});

	it('has help text', () => {
		const {getByText} = render(
			<SynonymSetsForm
				formName={FORM_NAME}
				inputName={INPUT_NAME}
				originalInputName={ORIGINAL_INPUT_NAME}
				synonymSets="one,two,three"
			/>
		);

		expect(
			getByText('type-a-comma-or-press-enter-to-input-a-synonym')
		).toBeInTheDocument();
	});

	it('has a form with label and input', () => {
		const {getByLabelText} = render(
			<SynonymSetsForm
				formName={FORM_NAME}
				inputName={INPUT_NAME}
				originalInputName={ORIGINAL_INPUT_NAME}
				synonymSets="one,two,three"
			/>
		);

		expect(getByLabelText('synonyms')).toBeInTheDocument();
	});

	it('enables the publish button when it has synonyms', () => {
		const {getByText} = render(
			<SynonymSetsForm
				formName={FORM_NAME}
				inputName={INPUT_NAME}
				originalInputName={ORIGINAL_INPUT_NAME}
				synonymSets="one,two,three"
			/>
		);

		expect(getByText('publish')).toBeEnabled();
	});

	it('disables the publish button when it has no synonyms', () => {
		const {getByText} = render(
			<SynonymSetsForm
				formName={FORM_NAME}
				inputName={INPUT_NAME}
				originalInputName={ORIGINAL_INPUT_NAME}
				synonymSets=""
			/>
		);

		expect(getByText('publish')).toBeDisabled();
	});

	it('shows listed synonym', () => {
		const {container} = render(
			<SynonymSetsForm
				formName={FORM_NAME}
				inputName={INPUT_NAME}
				originalInputName={ORIGINAL_INPUT_NAME}
				synonymSets="one"
			/>
		);

		const tagsElement = container.querySelectorAll('.label-item-expand');

		expect(tagsElement.length).toBe(1);
		expect(tagsElement[0]).toHaveTextContent('one');
	});

	it('shows listed synonyms', () => {
		const {container} = render(
			<SynonymSetsForm
				formName={FORM_NAME}
				inputName={INPUT_NAME}
				originalInputName={ORIGINAL_INPUT_NAME}
				synonymSets="one,two,three,four,five"
			/>
		);

		const tagsElement = container.querySelectorAll('.label-item-expand');

		expect(tagsElement.length).toBe(5);

		expect(tagsElement[0]).toHaveTextContent('one');
		expect(tagsElement[1]).toHaveTextContent('two');
		expect(tagsElement[2]).toHaveTextContent('three');
		expect(tagsElement[3]).toHaveTextContent('four');
		expect(tagsElement[4]).toHaveTextContent('five');
	});
});
