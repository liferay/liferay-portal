import React from 'react';
import redirectIf from '../RedirectIf';
import {render} from '@testing-library/react';
import {Routes, toRoute} from 'shared/util/router';
import {MemoryRouter} from 'react-router';

jest.unmock('react-dom');

const WrapperComponent = ({route = null}) => {
	const Component = redirectIf(() => route)(() => (
		<div className='my-component'>{'component body'}</div>
	));

	return (
		<MemoryRouter>
			<Component />
		</MemoryRouter>
	);
};

describe('redirectIf', () => {
	it('should render the component', () => {
		const {container} = render(<WrapperComponent />);

		expect(container).toMatchSnapshot();
	});

	it('should redirect if pass a route', () => {
		const expectedRoute = toRoute(Routes.WORKSPACES);
		const {container} = render(<WrapperComponent route={expectedRoute} />);

		expect(
			container.querySelector('.my-component')
		).not.toBeInTheDocument();
	});

	it('should render the passed component if dont pass a route', () => {
		const {container} = render(<WrapperComponent />);

		expect(container.querySelector('.my-component')).toBeInTheDocument();
	});
});
