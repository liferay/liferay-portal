import withSegment from '../WithSegment';
import {renderWithStore} from 'test/mock-store';
import {Segment} from 'shared/util/records';

jest.mock(
	'shared/hoc/WithAction',
	() => () => wrappedComponent => wrappedComponent
);

describe('WithSegment', () => {
	it('should pass the segment to the WrappedComponent', () => {
		let result = null;

		const MockComponent = props => {
			result = props;

			return null;
		};

		const WrappedComponent = withSegment(MockComponent);

		renderWithStore(WrappedComponent, {
			id: 'test',
			segment: new Segment()
		});

		expect(result.segment).toBeInstanceOf(Segment);
	});
});
