import * as data from 'test/data';
import AssociatedSegmentsList from '../AssociatedSegmentsList';
import mockStore from 'test/mock-store';
import React from 'react';
import {fromJS} from 'immutable';
import {Project} from 'shared/util/records';
import {Provider} from 'react-redux';
import {render} from '@testing-library/react';
import {MemoryRouter} from 'react-router';

jest.unmock('react-dom');

const defaultProps = {
	project: new Project(
		data.mockProject(23, {
			faroSubscription: fromJS(data.mockSubscription())
		})
	)
};

const WrappedComponent = () => (
	<Provider store={mockStore()}>
		<MemoryRouter>
			<AssociatedSegmentsList
				channelId='123123'
				dataSourceFn={() => Promise.resolve({})}
				groupId='23'
				id='test'
				total={2}
			/>
		</MemoryRouter>
	</Provider>
);

describe('AssociatedSegmentsList', () => {
	it('should render card header with segment total', () => {
		const {container} = render(<WrappedComponent {...defaultProps} />);

		expect(container.querySelector('.card-title')).toHaveTextContent(
			'Associated Segments'
		);
		expect(container.querySelector('.secondary-info b')).toHaveTextContent(
			'2'
		);
	});

	it('should render loading state without ghost table', () => {
		const {container} = render(<WrappedComponent {...defaultProps} />);

		expect(container.querySelector('.loading-root')).toBeTruthy();
		expect(container.querySelector('table')).toBeFalsy();
	});
});
