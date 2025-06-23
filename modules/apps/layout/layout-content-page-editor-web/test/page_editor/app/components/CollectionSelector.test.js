/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom/extend-expect';
import {State} from '@liferay/frontend-js-state-web';
import {render, screen} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import React from 'react';

import {CollectionItemContextProvider} from '../../../../src/main/resources/META-INF/resources/page_editor/app/contexts/CollectionItemContext';
import {StoreAPIContextProvider} from '../../../../src/main/resources/META-INF/resources/page_editor/app/contexts/StoreContext';
import {pageContentsAtom} from '../../../../src/main/resources/META-INF/resources/page_editor/app/utils/usePageContents';
import CollectionSelector from '../../../../src/main/resources/META-INF/resources/page_editor/common/components/CollectionSelector';
import {openItemSelector} from '../../../../src/main/resources/META-INF/resources/page_editor/common/openItemSelector';

jest.mock(
	'../../../../src/main/resources/META-INF/resources/page_editor/common/openItemSelector',
	() => ({
		openItemSelector: jest.fn(() => {}),
	})
);

describe('CollectionSelector', () => {
	beforeAll(() => {
		State.writeAtom(pageContentsAtom, {
			data: [],
			status: 'saved',
		});
	});

	afterEach(() => {
		openItemSelector.mockClear();
	});

	it('uses custom item selector URL when present in the collection item context', async () => {
		const CUSTOM_COLLECTION_SELECTOR_URL = 'CUSTOM_COLLECTION_SELECTOR_URL';
		const DEFAULT_ITEM_SELECTOR_URL = 'DEFAULT_ITEM_SELECTOR_URL';

		render(
			<StoreAPIContextProvider dispatch={() => {}} getState={() => ({})}>
				<CollectionItemContextProvider
					value={{
						customCollectionSelectorURL:
							CUSTOM_COLLECTION_SELECTOR_URL,
					}}
				>
					<CollectionSelector
						itemSelectorURL={DEFAULT_ITEM_SELECTOR_URL}
						label="something"
						onCollectionSelect={() => {}}
					/>
				</CollectionItemContextProvider>
			</StoreAPIContextProvider>
		);

		const button = screen.getByLabelText('select-something');

		await userEvent.click(button);

		expect(openItemSelector).toBeCalledWith(
			expect.objectContaining({
				itemSelectorURL: CUSTOM_COLLECTION_SELECTOR_URL,
			})
		);
	});

	it('uses passed item selector URL when not inside a collection item context', async () => {
		const DEFAULT_ITEM_SELECTOR_URL = 'DEFAULT_ITEM_SELECTOR_URL';

		render(
			<StoreAPIContextProvider dispatch={() => {}} getState={() => ({})}>
				<CollectionSelector
					itemSelectorURL={DEFAULT_ITEM_SELECTOR_URL}
					label="something"
					onCollectionSelect={() => {}}
				/>
			</StoreAPIContextProvider>
		);

		const button = screen.getByLabelText('select-something');

		await userEvent.click(button);

		expect(openItemSelector).toBeCalledWith(
			expect.objectContaining({
				itemSelectorURL: DEFAULT_ITEM_SELECTOR_URL,
			})
		);
	});

	it('does not show collection prefilter label when the filter is not configured', () => {
		render(
			<StoreAPIContextProvider dispatch={() => {}} getState={() => ({})}>
				<CollectionSelector label="" onCollectionSelect={() => {}} />
			</StoreAPIContextProvider>
		);

		expect(
			screen.queryByText('collection-filtered')
		).not.toBeInTheDocument();
	});

	it('shows collection prefilter label when the filter is not configured', () => {
		render(
			<StoreAPIContextProvider dispatch={() => {}} getState={() => ({})}>
				<CollectionSelector
					collectionItem={{
						config: {
							title: 'test',
						},
					}}
					label=""
					onCollectionSelect={() => {}}
				/>
			</StoreAPIContextProvider>
		);

		expect(screen.queryByText('collection-filtered')).toBeInTheDocument();
	});
});
