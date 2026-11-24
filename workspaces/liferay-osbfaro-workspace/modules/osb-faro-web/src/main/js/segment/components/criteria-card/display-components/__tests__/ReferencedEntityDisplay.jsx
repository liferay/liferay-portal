import * as data from 'test/data';
import React from 'react';
import ReferencedEntityDisplay from '../ReferencedEntityDisplay';
import {cleanup, render} from '@testing-library/react';
import {
	EntityType,
	ReferencedObjectsProvider
} from 'segment/segment-editor/dynamic/context/referencedObjects';
import {Segment} from 'shared/util/records';

const defaultProps = {
	id: '123',
	label: 'Page',
	type: EntityType.Assets
};

const mockSegment = data.getImmutableMock(Segment, data.mockSegment, 0, {
	referencedObjects: {
		assets: {
			123: {id: '123', name: 'Foo Page'}
		}
	}
});

const DefaultComponent = props => (
	<ReferencedObjectsProvider segment={mockSegment}>
		<ReferencedEntityDisplay {...defaultProps} {...props} />
	</ReferencedObjectsProvider>
);

describe('ReferencedEntityDisplay', () => {
	afterEach(cleanup);

	it('renders', () => {
		const {container} = render(<DefaultComponent />);

		expect(container).toMatchSnapshot();
	});

	it('renders with an undefined entity display', () => {
		const {container} = render(<DefaultComponent id='456' />);

		expect(container).toMatchSnapshot();
	});
});
