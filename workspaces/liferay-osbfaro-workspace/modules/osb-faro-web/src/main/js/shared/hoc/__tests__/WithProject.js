import withProject from '../WithProject';
import {renderWithStore} from 'test/mock-store';

jest.mock(
	'shared/hoc/WithAction',
	() => () => (wrappedComponent) => wrappedComponent
);

describe('WithProject', () => {
	it('should pass the project to the WrappedComponent', () => {
		let result = null;

		const MockComponent = (props) => {
			result = props;

			return null;
		};

		const WrappedComponent = withProject(MockComponent);

		renderWithStore(WrappedComponent, {
			id: 'test',
			project: 'fooProject',
		});

		expect(result.project).toBe('fooProject');
	});
});
