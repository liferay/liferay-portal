import CheckValidChannel from '../CheckValidChannel';
import React from 'react';
import {cleanup, render} from '@testing-library/react';
import {MemoryRouter} from 'react-router';

jest.unmock('react-dom');

const wrappedComponentText = () => 'wrapped component text';

describe('CheckValidChannel', () => {
	afterEach(cleanup);

	it('should render a wrapped component', () => {
		const WrappedComponent = CheckValidChannel(wrappedComponentText);

		const {container} = render(
			<WrappedComponent
				channelId='123'
				channels={[{id: '123'}]}
				location={{pathname: 'test'}}
			/>
		);

		expect(container.textContent).toBe('wrapped component text');
	});

	it('should render an error page', () => {
		const WrappedComponent = CheckValidChannel(wrappedComponentText);

		const {container} = render(
			<MemoryRouter>
				<WrappedComponent
					channelId='123'
					channels={[{id: '456'}]}
					location={{pathname: 'test'}}
				/>
			</MemoryRouter>
		);

		expect(container.textContent).toBe(
			'404Page Not FoundThe page you are looking for does not exist.Go to Home'
		);
	});
});
