import React from 'react';
import {
	ACTION_TYPES,
	SelectionProvider,
	selectionReducer,
	useSelectionContext,
	withSelectionProvider
} from '../selection';
import {cleanup, render} from '@testing-library/react';
import {OrderedMap} from 'immutable';

const initialState = {selectedItems: new OrderedMap([['1', {}]])};

describe('selectionReducer', () => {
	it('should return selectedItems state with added items', () => {
		const {selectedItems} = selectionReducer(initialState, {
			payload: {items: [{id: '123'}]},
			type: ACTION_TYPES.add
		});

		expect(selectedItems.has('123')).toBe(true);
	});

	it('should return an empty selectedItems state', () => {
		const {selectedItems} = selectionReducer(initialState, {
			type: ACTION_TYPES.clearAll
		});

		expect(selectedItems.isEmpty()).toBe(true);
	});

	it('should return selectedItems state with items removed', () => {
		const {selectedItems} = selectionReducer(initialState, {
			payload: {items: [{id: '1'}]},
			type: ACTION_TYPES.remove
		});

		expect(selectedItems.has('1')).toBe(false);
	});

	it('should return selectedItems state with the given item toggled into selectedItems', () => {
		const {selectedItems} = selectionReducer(initialState, {
			payload: {item: {id: '2'}},
			type: ACTION_TYPES.toggle
		});

		expect(selectedItems.has('2')).toBe(true);
	});

	it('should return selectedItems state with the given item toggled out of selectedItems', () => {
		const {selectedItems} = selectionReducer(initialState, {
			payload: {item: {id: '1'}},
			type: ACTION_TYPES.toggle
		});

		expect(selectedItems.has('1')).toBe(false);
	});
});

describe('SelectionProvider', () => {
	afterEach(cleanup);

	it('should allow an initial context value to be set through the selectedItems prop', () => {
		const successMsg = 'has selected items intialized in context!';

		const ChildComponent = () => {
			const {selectedItems} = useSelectionContext();

			return selectedItems.size && successMsg;
		};

		const {container} = render(
			<SelectionProvider selectedItems={[{id: '1'}]}>
				<ChildComponent />
			</SelectionProvider>
		);

		expect(container).toHaveTextContent(successMsg);
	});
});

describe('useSelectionContext', () => {
	afterEach(cleanup);

	it('should return context', () => {
		const successMsg = 'has selected items context!';

		const ChildComponent = () => {
			const {selectedItems, selectionDispatch} = useSelectionContext();

			return selectedItems && selectionDispatch && successMsg;
		};

		const {container} = render(
			<SelectionProvider>
				<ChildComponent />
			</SelectionProvider>
		);

		expect(container).toHaveTextContent(successMsg);
	});
});

describe('withSelectionProvider', () => {
	afterEach(cleanup);

	it('should pass the WrappedComponent', () => {
		const WrappedComponent = withSelectionProvider(() => (
			<div>{'foo'}</div>
		));
		const {container} = render(<WrappedComponent />);
		expect(container).toHaveTextContent('foo');
	});
});
