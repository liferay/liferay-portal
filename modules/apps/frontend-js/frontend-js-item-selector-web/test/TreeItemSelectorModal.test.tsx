/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {useModal} from '@clayui/modal';
import {render, waitFor, within} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import {fetch} from 'frontend-js-web';
import React from 'react';

import TreeItemSelectorModal from '../src/main/resources/META-INF/resources/item_selector/TreeItemSelectorModal';
import {ITaxonomyCategory} from '../src/main/resources/META-INF/resources/item_selector/useTaxonomyCategoryTreeNodes';

const VOCABULARY_ID = '1001';

const ROOT_PARENT: ITaxonomyCategory = {
	id: 1,
	name: 'Parent',
	numberOfTaxonomyCategories: 2,
};

const CHILD_ALPHA: ITaxonomyCategory = {
	id: 2,
	name: 'Alpha',
	numberOfTaxonomyCategories: 0,
	parentTaxonomyCategory: {id: 1, name: 'Parent'},
};

const CHILD_BETA: ITaxonomyCategory = {
	id: 3,
	name: 'Beta',
	numberOfTaxonomyCategories: 0,
	parentTaxonomyCategory: {id: 1, name: 'Parent'},
};

const ROOT_LEAF: ITaxonomyCategory = {
	id: 4,
	name: 'Solo',
	numberOfTaxonomyCategories: 0,
};

function pageOf<T>(items: T[]) {
	const headers = new Headers();
	headers.set('Content-Type', 'application/json');

	return Promise.resolve({
		headers,
		json: () =>
			Promise.resolve({
				items,
				lastPage: 1,
				page: 1,
				totalCount: items.length,
			}),
		ok: true,
		status: 200,
	});
}

jest.mock('frontend-js-web', () => {
	const actualPackage = jest.requireActual('frontend-js-web') as any;

	return {
		...actualPackage,
		fetch: jest.fn(),
		sub: jest.fn((...args) => actualPackage.sub(...args)),
	};
});

const mockedFetch = fetch as jest.Mock;

function configureFetch({
	emptyVocabulary = false,
}: {emptyVocabulary?: boolean} = {}) {
	mockedFetch.mockImplementation((url: string) => {
		if (url.includes(`/taxonomy-vocabularies/${VOCABULARY_ID}/`)) {
			return pageOf(
				emptyVocabulary
					? []
					: [ROOT_PARENT, CHILD_ALPHA, CHILD_BETA, ROOT_LEAF]
			);
		}

		return pageOf([]);
	});
}

const Wrapper = ({
	multiSelect = false,
	onItemsChange,
	selectedItems = [],
	title,
}: {
	multiSelect?: boolean;
	onItemsChange: (items: ITaxonomyCategory[]) => void;
	selectedItems?: ITaxonomyCategory[];
	title?: string;
}) => {
	const {observer, onOpenChange, open} = useModal({defaultOpen: true});

	return (
		<TreeItemSelectorModal
			multiSelect={multiSelect}
			observer={observer}
			onItemsChange={onItemsChange}
			onOpenChange={onOpenChange}
			open={open}
			selectedItems={selectedItems}
			title={title}
			vocabularyIds={[VOCABULARY_ID]}
		/>
	);
};

describe('TreeItemSelectorModal', () => {
	beforeEach(() => {
		configureFetch();
	});

	afterEach(() => {
		jest.clearAllMocks();
	});

	it('renders the vocabulary tree once the fetch resolves', async () => {
		const {findByRole} = render(<Wrapper onItemsChange={jest.fn()} />);

		const modal = await findByRole('dialog');

		expect(await within(modal).findByText('Parent')).toBeInTheDocument();
		expect(await within(modal).findByText('Solo')).toBeInTheDocument();
	});

	it('shows an error state when the API call fails', async () => {
		mockedFetch.mockResolvedValueOnce({
			headers: new Headers(),
			ok: false,
			status: 500,
		});

		const {findByRole} = render(<Wrapper onItemsChange={jest.fn()} />);

		const modal = await findByRole('dialog');

		expect(
			await within(modal).findByText('an-unexpected-error-occurred')
		).toBeInTheDocument();

		expect(
			await within(modal).findByText('unable-to-load-content')
		).toBeInTheDocument();
	});

	it('renders an empty state when the vocabulary has no categories', async () => {
		configureFetch({emptyVocabulary: true});

		const {findByRole} = render(<Wrapper onItemsChange={jest.fn()} />);

		const modal = await findByRole('dialog');

		expect(
			await within(modal).findByText('no-categories-were-found')
		).toBeInTheDocument();
	});

	it('confirms a single-select choice immediately and closes the modal', async () => {
		const user = userEvent.setup();
		const onItemsChange = jest.fn();

		const {findByRole} = render(<Wrapper onItemsChange={onItemsChange} />);

		const modal = await findByRole('dialog');

		const solo = await within(modal).findByText('Solo');

		await user.click(solo);

		await waitFor(() => {
			expect(onItemsChange).toHaveBeenCalledTimes(1);
		});

		expect(onItemsChange).toHaveBeenLastCalledWith([ROOT_LEAF]);
	});

	it('multi-select waits for confirmation before emitting items', async () => {
		const user = userEvent.setup();
		const onItemsChange = jest.fn();

		const {findByRole} = render(
			<Wrapper multiSelect={true} onItemsChange={onItemsChange} />
		);

		const modal = await findByRole('dialog');

		await within(modal).findByText('Solo');

		const checkboxes = await within(modal).findAllByRole('checkbox');

		await user.click(checkboxes[checkboxes.length - 1]);

		expect(onItemsChange).not.toHaveBeenCalled();

		const done = await within(modal).findByRole('button', {
			name: 'done',
		});

		await user.click(done);

		expect(onItemsChange).toHaveBeenCalledTimes(1);

		const [emittedItems] = onItemsChange.mock.calls[0];

		expect(emittedItems).toHaveLength(1);
		expect(emittedItems[0].id).toEqual(ROOT_LEAF.id);
	});

	it('keeps ancestor nodes visible when search matches a descendant only', async () => {
		const user = userEvent.setup();

		const {findByRole} = render(<Wrapper onItemsChange={jest.fn()} />);

		const modal = await findByRole('dialog');

		await within(modal).findByText('Parent');

		const search = await within(modal).findByRole('searchbox');

		await user.type(search, 'Alpha');

		await waitFor(() => {
			expect(within(modal).getByText('Parent')).toBeInTheDocument();
		});

		expect(within(modal).getByText('Alpha')).toBeInTheDocument();
		expect(within(modal).queryByText('Solo')).not.toBeInTheDocument();
	});

	it('shows a no-results state when the search query matches nothing', async () => {
		const user = userEvent.setup();

		const {findByRole} = render(<Wrapper onItemsChange={jest.fn()} />);

		const modal = await findByRole('dialog');

		await within(modal).findByText('Parent');

		const search = await within(modal).findByRole('searchbox');

		await user.type(search, 'no-such-category-zzz');

		await waitFor(() => {
			expect(
				within(modal).getByText('no-results-found')
			).toBeInTheDocument();
		});
	});

	it('reflects pre-selected items as checked checkboxes in multi-select', async () => {
		const {findByRole} = render(
			<Wrapper
				multiSelect={true}
				onItemsChange={jest.fn()}
				selectedItems={[CHILD_ALPHA]}
			/>
		);

		const modal = await findByRole('dialog');

		await within(modal).findByText('Alpha');

		const alphaCheckbox = within(modal)
			.getByText('Alpha')
			.closest('.treeview-item')
			?.querySelector(
				'input[type="checkbox"]'
			) as HTMLInputElement | null;

		expect(alphaCheckbox).not.toBeNull();
		expect(alphaCheckbox).toBeChecked();
	});
});
