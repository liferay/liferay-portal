import {
	getModalProps,
	isModalOpen,
	resetModalProps
} from 'test/mock-searchable-table-modal';

import mockStore from 'test/mock-store';
import React from 'react';
import SelectCategoryFromModal from '../SelectCategoryFromModal';
import {act, cleanup, fireEvent, render, waitFor} from '@testing-library/react';
import {OrderedMap} from 'immutable';
import {Provider} from 'react-redux';

jest.unmock('react-dom');

const defaultProps = {
	channelId: 'ch-1',
	groupId: 'gp-1',
	onCategoriesChange: jest.fn(),
	selectedCategories: [],
	vocabularyId: 'vocab-id-1'
};

const DefaultComponent = props => (
	<Provider store={mockStore()}>
		<SelectCategoryFromModal {...defaultProps} {...props} />
	</Provider>
);

describe('SelectCategoryFromModal', () => {
	beforeAll(() => {
	});

	afterEach(() => {
		cleanup();
		resetModalProps();
	});

	describe('render with zero categories', () => {
		it('should render an add-category button', () => {
			const {getByText} = render(
				<DefaultComponent selectedCategories={[]} />
			);

			expect(getByText(/add category/i)).toBeTruthy();
		});

		it('should not render labels or clear button when there are no categories', () => {
			const {container, queryByLabelText} = render(
				<DefaultComponent selectedCategories={[]} />
			);

			expect(container.querySelectorAll('.label')).toHaveLength(0);
			expect(queryByLabelText('clear')).toBeNull();
		});
	});

	describe('render with categories', () => {
		const selectedCategories = [
			{id: 'cat-1', name: 'Category One'},
			{id: 'cat-2', name: 'Category Two'}
		];

		it('should render a label for each selected category', () => {
			const {getByText} = render(
				<DefaultComponent selectedCategories={selectedCategories} />
			);

			expect(getByText('Category One')).toBeTruthy();
			expect(getByText('Category Two')).toBeTruthy();
		});

		it('should render a select button and a clear button', () => {
			const {getByLabelText, getByText} = render(
				<DefaultComponent selectedCategories={selectedCategories} />
			);

			expect(getByText(/select/i)).toBeTruthy();
			expect(getByLabelText(/clear/i)).toBeTruthy();
		});
	});

	describe('open modal', () => {
		it('should open the modal when clicking add-category from empty state', () => {
			const {getByText} = render(
				<DefaultComponent selectedCategories={[]} />
			);

			fireEvent.click(getByText(/add category/i));

			expect(isModalOpen()).toBe(true);
		});

		it('should open the modal when clicking select from non-empty state', () => {
			const {getByText} = render(
				<DefaultComponent
					selectedCategories={[{id: 'cat-1', name: 'Cat 1'}]}
				/>
			);

			fireEvent.click(getByText(/select/i));

			expect(isModalOpen()).toBe(true);
		});

		it('should pass current selectedCategories as initialSelectedItems', () => {
			const selectedCategories = [{id: 'cat-1', name: 'Category One'}];

			const {getByText} = render(
				<DefaultComponent selectedCategories={selectedCategories} />
			);

			fireEvent.click(getByText(/select/i));

			expect(getModalProps().initialSelectedItems).toEqual(
				selectedCategories
			);
		});
	});

	describe('remove individual category', () => {
		it('should call onCategoriesChange without the removed category', () => {
			const onCategoriesChange = jest.fn();
			const selectedCategories = [
				{id: 'cat-1', name: 'Category One'},
				{id: 'cat-2', name: 'Category Two'}
			];

			const {getAllByLabelText} = render(
				<DefaultComponent
					onCategoriesChange={onCategoriesChange}
					selectedCategories={selectedCategories}
				/>
			);

			fireEvent.click(getAllByLabelText(/close/i)[0]);

			expect(onCategoriesChange).toHaveBeenCalledWith([
				{id: 'cat-2', name: 'Category Two'}
			]);
		});

		it('should keep remaining categories after removal', () => {
			const onCategoriesChange = jest.fn();
			const selectedCategories = [
				{id: 'cat-1', name: 'Cat 1'},
				{id: 'cat-2', name: 'Cat 2'},
				{id: 'cat-3', name: 'Cat 3'}
			];

			const {getAllByLabelText} = render(
				<DefaultComponent
					onCategoriesChange={onCategoriesChange}
					selectedCategories={selectedCategories}
				/>
			);

			fireEvent.click(getAllByLabelText(/close/i)[1]);

			expect(onCategoriesChange).toHaveBeenCalledWith([
				{id: 'cat-1', name: 'Cat 1'},
				{id: 'cat-3', name: 'Cat 3'}
			]);
		});
	});

	describe('clear all categories', () => {
		it('should call onCategoriesChange with an empty array', () => {
			const onCategoriesChange = jest.fn();

			const {getByLabelText} = render(
				<DefaultComponent
					onCategoriesChange={onCategoriesChange}
					selectedCategories={[{id: 'cat-1', name: 'Cat 1'}]}
				/>
			);

			fireEvent.click(getByLabelText(/clear/i));

			expect(onCategoriesChange).toHaveBeenCalledWith([]);
		});
	});

	describe('modal submit', () => {
		it('should call onCategoriesChange with the submitted items', () => {
			const onCategoriesChange = jest.fn();

			const {getByText} = render(
				<DefaultComponent
					onCategoriesChange={onCategoriesChange}
					selectedCategories={[]}
				/>
			);

			fireEvent.click(getByText(/add category/i));

			const {onSubmit} = getModalProps();
			const submittedItems = new OrderedMap({
				'cat-1': {id: 'cat-1', name: 'Category One'},
				'cat-2': {id: 'cat-2', name: 'Category Two'}
			});

			onSubmit(submittedItems);

			expect(onCategoriesChange).toHaveBeenCalledWith([
				{id: 'cat-1', name: 'Category One'},
				{id: 'cat-2', name: 'Category Two'}
			]);
		});

		it('should close the modal after submit', async () => {
			const {getByText} = render(
				<DefaultComponent selectedCategories={[]} />
			);

			fireEvent.click(getByText(/add category/i));

			const {onSubmit} = getModalProps();

			act(() => {
				onSubmit(
					new OrderedMap({'cat-1': {id: 'cat-1', name: 'Cat 1'}})
				);
			});

			await waitFor(() => expect(isModalOpen()).toBe(false));
		});
	});
});
