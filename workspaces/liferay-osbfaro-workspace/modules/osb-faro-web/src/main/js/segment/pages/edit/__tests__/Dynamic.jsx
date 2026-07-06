import DynamicSegmentEdit from '../Dynamic';
import mockStore from 'test/mock-store';
import React from 'react';
import {cleanup, render} from '@testing-library/react';
import {List} from 'immutable';
import {Provider} from 'react-redux';
import {StaticRouter} from 'react-router';

jest.unmock('react-dom');

jest.mock('contacts/hoc/segment/WithBaseEdit', () => Component => Component);
jest.mock(
	'segment/segment-editor/dynamic/hoc/WithPropertyGroups',
	() => Component => Component
);

// Capture the segment type the editor is rendered with.

jest.mock('segment/segment-editor/dynamic', () => ({
	__esModule: true,
	default: ({type}) => <div data-testid='segment-type'>{type}</div>
}));

const renderEdit = ({location = '/', segment} = {}) =>
	render(
		<StaticRouter location={location}>
			<Provider store={mockStore()}>
				<DynamicSegmentEdit
					groupId='23'
					id='123'
					propertyGroupsIList={new List()}
					segment={segment}
				/>
			</Provider>
		</StaticRouter>
	);

describe('DynamicSegmentEdit', () => {
	afterEach(cleanup);

	it("should use an existing segment's type", () => {
		const {getByTestId} = renderEdit({
			segment: {segmentType: 'REAL_TIME'}
		});

		expect(getByTestId('segment-type').textContent).toBe('REAL_TIME');
	});

	it('should use the type query param when there is no existing segment', () => {
		const {getByTestId} = renderEdit({location: '/?type=REAL_TIME'});

		expect(getByTestId('segment-type').textContent).toBe('REAL_TIME');
	});

	it('should default to BATCH when neither a segment nor a type param is present', () => {
		const {getByTestId} = renderEdit({});

		expect(getByTestId('segment-type').textContent).toBe('BATCH');
	});
});
