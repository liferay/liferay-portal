import withIndividual from '../WithIndividual';
import {renderWithStore} from 'test/mock-store';

jest.mock(
	'shared/hoc/WithAction',
	() => () => wrappedComponent => wrappedComponent
);

describe('WithIndividual', () => {
	it('should pass the individual to the WrappedComponent', () => {
		let result = null;

		const MockComponent = props => {
			result = props;

			return null;
		};

		const WrappedComponent = withIndividual(MockComponent);

		renderWithStore(WrappedComponent, {
			id: 'test',
			individual: 'fooIndividual'
		});

		expect(result.individual).toBe('fooIndividual');
	});
});
