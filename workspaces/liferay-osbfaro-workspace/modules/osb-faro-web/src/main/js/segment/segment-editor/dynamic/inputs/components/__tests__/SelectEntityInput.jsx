import * as data from 'test/data';
import mockStore from 'test/mock-store';
import React from 'react';
import SelectEntityInput from '../SelectEntityInput';
import {cleanup, render} from '@testing-library/react';
import {
	EntityType,
	ReferencedObjectsProvider
} from 'segment/segment-editor/dynamic/context/referencedObjects';
import {Property, Segment} from 'shared/util/records';
import {Provider} from 'react-redux';

const mockSegment = data.getImmutableMock(Segment, data.mockSegment, 0, {
	referencedObjects: {
		organizations: {
			123: data.mockGraphqlOrganization('123', {
				dataSourceName: 'Foo DataSource',
				name: 'Foo Organization'
			})
		}
	}
});

const defaultProps = {
	displayValue: 'Address',
	onValidChange: jest.fn(),
	operatorRenderer: () => <div>{'operator'}</div>,
	property: new Property(),
	touched: false,
	valid: true,
	value: ''
};

const DefaultComponent = props => (
	<Provider store={mockStore()}>
		<ReferencedObjectsProvider segment={mockSegment}>
			<SelectEntityInput {...defaultProps} {...props} />
		</ReferencedObjectsProvider>
	</Provider>
);

describe('SelectEntityInput', () => {
	afterEach(cleanup);

	it('should render', () => {
		const {container} = render(<DefaultComponent valid={false} />);

		expect(container).toMatchSnapshot();
	});

	it('should render with a preselected entity', () => {
		const {getByText} = render(
			<DefaultComponent
				entityType={EntityType.Organizations}
				onChange={jest.fn()}
				value='123'
			/>
		);

		expect(getByText('Foo Organization')).toBeTruthy();
		expect(getByText('Foo DataSource')).toBeTruthy();
	});

	it('should render with an error in the select input if the input is touched and valid is false', () => {
		const {container} = render(<DefaultComponent touched valid={false} />);

		expect(
			container.querySelector('.input-group-item.has-error')
		).toBeTruthy();
	});
});
