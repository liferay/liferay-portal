import React from 'react';
import {
	ActionType,
	UnassignedSegmentsProvider,
	unassignedSegmentsReducer,
	useUnassignedSegmentsContext,
	withUnassignedSegmentsProvider
} from '../unassignedSegments';
import {cleanup, render} from '@testing-library/react';
import {mockSegment} from 'test/data';

const initialState = {
	unassignedSegments: []
};

describe('unassignedSegmentsReducer', () => {
	it('should return unassignedSegments state with added items', () => {
		const segment = mockSegment();

		const {unassignedSegments} = unassignedSegmentsReducer(initialState, {
			payload: [segment],
			type: ActionType.setSegments
		});

		expect(unassignedSegments).toContain(segment);
	});
});

describe('UnassignedSegmentsProvider', () => {
	afterEach(cleanup);

	it('should allow an initial context value to be set through the unassignedSegments prop', () => {
		const successMsg = 'has unassignedSegments intialized in context!';

		const ChildComponent = () => {
			const {unassignedSegments} = useUnassignedSegmentsContext();

			return unassignedSegments.length && successMsg;
		};

		const {container} = render(
			<UnassignedSegmentsProvider unassignedSegments={[mockSegment()]}>
				<ChildComponent />
			</UnassignedSegmentsProvider>
		);

		expect(container).toHaveTextContent(successMsg);
	});
});

describe('useUnassignedSegmentsContext', () => {
	afterEach(cleanup);

	it('should return context', () => {
		const successMsg = 'has context!';

		const ChildComponent = () => {
			const {unassignedSegments, unassignedSegmentsDispatch} =
				useUnassignedSegmentsContext();

			return (
				unassignedSegments && unassignedSegmentsDispatch && successMsg
			);
		};

		const {container} = render(
			<UnassignedSegmentsProvider>
				<ChildComponent />
			</UnassignedSegmentsProvider>
		);

		expect(container).toHaveTextContent(successMsg);
	});
});

describe('withUnassignedSegmentsProvider', () => {
	afterEach(cleanup);

	it('should pass the WrappedComponent', () => {
		const WrappedComponent = withUnassignedSegmentsProvider(() => (
			<div>{'foo'}</div>
		));
		const {container} = render(<WrappedComponent />);
		expect(container).toHaveTextContent('foo');
	});
});
