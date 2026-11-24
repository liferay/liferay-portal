import withDataSource from '../WithDataSource';
import {renderWithStore} from 'test/mock-store';

jest.mock(
	'shared/hoc/WithAction',
	() => () => wrappedComponent => wrappedComponent
);

describe('WithDataSource', () => {
	it('should pass dataSource to the WrappedComponent', () => {
		let result = null;

		const MockComponent = props => {
			result = props;

			return null;
		};

		const WrappedComponent = withDataSource(MockComponent);

		renderWithStore(WrappedComponent, {
			dataSource: 'fooDataSource',
			id: 'test'
		});

		expect(result.dataSource).toBe('fooDataSource');
	});
});
