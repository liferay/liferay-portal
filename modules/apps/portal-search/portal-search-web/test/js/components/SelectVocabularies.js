/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {render, screen, within} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import React from 'react';

import SelectVocabularies from '../../../src/main/resources/META-INF/resources/js/components/SelectVocabularies';

const GLOBAL_VOCABULARY = {
	externalReferenceCode: 'GLOBAL_ERC&&GLOBAL_VOCABULARY_ERC',
	id: 3,
	name: 'Global Vocabulary',
};

const LIBRARY_VOCABULARY = {
	externalReferenceCode: 'LIBRARY_ERC&&LIBRARY_VOCABULARY_ERC',
	id: 2,
	name: 'Library Vocabulary',
};

const SITE_VOCABULARY = {
	externalReferenceCode: 'SITE_ERC&&SITE_VOCABULARY_ERC',
	id: 1,
	name: 'Site Vocabulary',
};

const ASSET_LIBRARY = {
	assetLibraryKey: 'LIBRARY_KEY',
	children: [LIBRARY_VOCABULARY],
	externalReferenceCode: 'LIBRARY_ERC',
	groupId: 30456,
	name: 'Library',
};

const GLOBAL = {
	children: [GLOBAL_VOCABULARY],
	externalReferenceCode: 'GLOBAL_ERC',
	groupId: 20099,
	name: 'Global',
};

const SITE = {
	children: [SITE_VOCABULARY],
	externalReferenceCode: 'SITE_ERC',
	groupId: 20123,
	name: 'Site',
};

const INPUT_NAME = 'groupVocabularyExternalReferenceCodes';

function renderSelectVocabularies({
	groups = [SITE, GLOBAL, ASSET_LIBRARY],
	initialSelectedVocabularyExternalReferenceCodes = 'SITE_ERC&&SITE_VOCABULARY_ERC',
} = {}) {
	return render(
		<SelectVocabularies
			groups={groups}
			initialSelectedVocabularyExternalReferenceCodes={
				initialSelectedVocabularyExternalReferenceCodes
			}
			learnResources={{}}
			vocabularyExternalReferenceCodesInputName={INPUT_NAME}
		/>
	);
}

describe('SelectVocabularies', () => {
	it('lists asset libraries as tree items after sites', () => {
		renderSelectVocabularies();

		const treeItems = screen.getAllByRole('treeitem');

		const siteTreeItem = screen.getByRole('treeitem', {name: /Site/});
		const globalTreeItem = screen.getByRole('treeitem', {name: /Global/});
		const libraryTreeItem = screen.getByRole('treeitem', {
			name: /Library/,
		});

		expect(treeItems.indexOf(siteTreeItem)).toBeLessThan(
			treeItems.indexOf(globalTreeItem)
		);
		expect(treeItems.indexOf(globalTreeItem)).toBeLessThan(
			treeItems.indexOf(libraryTreeItem)
		);
	});

	it("selects only the clicked group's own vocabulary when using select-all", async () => {
		renderSelectVocabularies();

		const libraryTreeItem = screen.getByRole('treeitem', {
			name: /Library/,
		});

		await userEvent.click(
			within(libraryTreeItem).getByRole('button', {name: 'select-all'})
		);

		const globalTreeItem = screen.getByRole('treeitem', {name: /Global/});

		await userEvent.click(
			within(globalTreeItem).getByRole('button', {name: 'select-all'})
		);

		expect(
			screen.getByDisplayValue(
				'SITE_ERC&&SITE_VOCABULARY_ERC,LIBRARY_ERC&&LIBRARY_VOCABULARY_ERC,GLOBAL_ERC&&GLOBAL_VOCABULARY_ERC'
			)
		).toBeInTheDocument();
	});

	it('flags a vocabulary stored with a group that does not own it', async () => {
		renderSelectVocabularies({
			initialSelectedVocabularyExternalReferenceCodes:
				'SITE_ERC&&LIBRARY_VOCABULARY_ERC',
		});

		expect(
			screen.getByText('select-vocabularies-configuration-alert')
		).toBeInTheDocument();

		await userEvent.click(
			screen.getByRole('button', {
				name: 'remove-unavailable-vocabularies',
			})
		);

		expect(
			screen.getByText('unavailable-vocabularies-removed-from-selection')
		).toBeInTheDocument();
	});

	it('shows an error message when there are no groups to select from', () => {
		renderSelectVocabularies({groups: []});

		expect(
			screen.getByText(
				'an-error-has-occurred-and-we-were-unable-to-load-the-results'
			)
		).toBeInTheDocument();
	});
});
