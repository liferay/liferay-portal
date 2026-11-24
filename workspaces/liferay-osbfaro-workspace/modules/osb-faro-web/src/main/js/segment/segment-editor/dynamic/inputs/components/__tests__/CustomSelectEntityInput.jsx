import * as data from 'test/data';
import CustomSelectEntityInput from '../CustomSelectEntityInput';
import mockStore from 'test/mock-store';
import React from 'react';
import {cleanup, render} from '@testing-library/react';
import {createCustomValueMap} from '../../../utils/custom-inputs';
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
				name: 'Foo Organization'
			})
		}
	}
});

const mockValue = createCustomValueMap([
	{key: 'criterionGroup', value: [{operatorName: 'eq', value: ''}]}
]);

const defaultProps = {
	displayValue: 'Address',
	onChange: jest.fn(),
	operatorRenderer: () => <div>{'operator'}</div>,
	property: new Property(),
	touched: false,
	valid: true,
	value: mockValue
};

const DefaultComponent = props => (
	<Provider store={mockStore()}>
		<ReferencedObjectsProvider segment={mockSegment}>
			<CustomSelectEntityInput {...defaultProps} {...props} />
		</ReferencedObjectsProvider>
	</Provider>
);

describe('CustomSelectEntityInput', () => {
	afterEach(cleanup);

	it('should render', () => {
		const {container} = render(<DefaultComponent valid={false} />);

		expect(container).toMatchSnapshot();
	});

	it('should render with a preselected entity', () => {
		const {getByText} = render(
			<DefaultComponent
				entityType={EntityType.Organizations}
				value={createCustomValueMap([
					{
						key: 'criterionGroup',
						value: [{operatorName: 'eq', value: '123'}]
					}
				])}
			/>
		);

		expect(getByText('Foo Organization')).toBeTruthy();
	});
});
