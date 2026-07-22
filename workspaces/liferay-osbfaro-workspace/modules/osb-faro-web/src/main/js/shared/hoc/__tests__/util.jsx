import React from 'react';
import {cleanup, render} from '@testing-library/react';
import {SafeResults, withEmpty, withError, withLoading} from '../util';
import {MemoryRouter} from 'react-router';

jest.unmock('react-dom');

describe('HOC Util', () => {
	afterEach(cleanup);

	describe('SafeResults', () => {
		it('should pass the children when error and loading are false', () => {
			const {getByText} = render(
				<SafeResults error={false} loading={false}>
					{() => 'foo'}
				</SafeResults>
			);

			expect(getByText('foo')).toBeTruthy();
		});

		it('should return a loading screen when loading is true', () => {
			const {container} = render(
				<SafeResults error={false} loading>
					{() => 'foo'}
				</SafeResults>
			);

			expect(container.querySelector('.loading-root')).toBeTruthy();
		});

		it('should return an error screen when error is true', () => {
			const {container} = render(
				<MemoryRouter>
					<SafeResults error>{() => 'foo'}</SafeResults>
				</MemoryRouter>
			);

			expect(container.querySelector('.error-page-root')).toBeTruthy();
		});
	});

	describe('withEmpty', () => {
		it('should render with an empty state', () => {
			const WrappedComponent = withEmpty()(() => 'test');

			const {container} = render(<WrappedComponent data={{total: 0}} />);

			expect(container.querySelector('.no-results-root')).toBeTruthy();
		});

		it('should return the passed component', () => {
			const componentSpy = jest.fn(() => 'test');

			const WrappedComponent = withEmpty()(componentSpy);

			render(<WrappedComponent data={{total: 1}} />);

			expect(componentSpy).toHaveBeenCalled();
		});
	});

	describe('withError', () => {
		it('should render with an error page', () => {
			const WrappedComponent = withError()(jest.fn(() => 'test'));

			const {container} = render(
				<MemoryRouter>
					<WrappedComponent error />
				</MemoryRouter>
			);

			expect(container.querySelector('.error-page-root')).toBeTruthy();
		});

		it('should render with an error display', () => {
			const WrappedComponent = withError({page: false})(
				jest.fn(() => 'test')
			);

			const {container} = render(<WrappedComponent error />);

			expect(container.querySelector('.error-display-root')).toBeTruthy();
		});

		it('should return the passed component', () => {
			const componentSpy = jest.fn(() => 'test');

			const WrappedComponent = withError()(componentSpy);

			render(<WrappedComponent error={false} />);

			expect(componentSpy).toHaveBeenCalled();
		});

		it('should render a custom error message', () => {
			const customMessage = 'my fancy message, oh so fancy';

			const WrappedComponent = withError({message: customMessage})(
				jest.fn(() => 'test')
			);

			const {getByText} = render(
				<MemoryRouter>
					<WrappedComponent error />
				</MemoryRouter>
			);

			expect(getByText(customMessage)).toBeTruthy();
		});
	});

	describe('withLoading', () => {
		it('should render with a loading page', () => {
			const WrappedComponent = withLoading()(jest.fn(() => 'test'));

			const {container} = render(<WrappedComponent loading />);

			expect(container.querySelector('.loading-root')).toBeTruthy();
		});

		it('should render w/ loading', () => {
			const WrappedComponent = withLoading()(jest.fn(() => 'test'));

			const {container} = render(<WrappedComponent loading />);

			expect(container.querySelector('.loading-root')).toBeTruthy();
		});

		it('should return the passed component', () => {
			const componentSpy = jest.fn(() => 'test');

			const WrappedComponent = withLoading()(componentSpy);

			render(<WrappedComponent loading={false} />);

			expect(componentSpy).toHaveBeenCalled();
		});
	});
});
