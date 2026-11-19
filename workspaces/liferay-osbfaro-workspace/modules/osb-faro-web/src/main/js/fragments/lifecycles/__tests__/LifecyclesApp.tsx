jest.mock(
	'../../SectionApp',
	() =>
		({path, state}: {path: string; state?: {lifecycleId?: string}}) =>
			`${path} ${state?.lifecycleId ?? ''}`
);

import React from 'react';
import {render, screen} from '@testing-library/react';
import {EditLifecycle, Lifecycle, Lifecycles} from '../LifecyclesApp';

jest.unmock('react-dom');

describe('LifecyclesApp', () => {
	it.each([
		[{lifecycleId: '1'}, 'lifecycle/1/edit'],
		[{sourceLifecycleId: '1'}, 'lifecycle/1/duplicate'],
		[{}, 'lifecycle/new'],
		[{backLifecycleId: '1'}, 'lifecycle/new 1'],
	])('renders the route of EditLifecycle with %j', (params, path) => {
		render(<EditLifecycle channelId="321" groupId="23" params={params} />);

		expect(screen.getByText(path)).toBeInTheDocument();
	});

	it('renders the route of Lifecycle', () => {
		render(
			<Lifecycle
				channelId="321"
				groupId="23"
				params={{lifecycleId: '1'}}
			/>
		);

		expect(screen.getByText('lifecycle/1')).toBeInTheDocument();
	});

	it.each([
		[{}, 'lifecycle'],
		[{lifecycleId: '1'}, 'lifecycle/1'],
	])('renders the route of Lifecycles with %j', (params, path) => {
		render(<Lifecycles channelId="321" groupId="23" params={params} />);

		expect(screen.getByText(path)).toBeInTheDocument();
	});
});
