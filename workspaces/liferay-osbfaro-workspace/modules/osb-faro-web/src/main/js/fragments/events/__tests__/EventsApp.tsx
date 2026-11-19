jest.mock(
	'../../SectionApp',
	() =>
		({path}: {path: string}) =>
			path
);

import React from 'react';
import {render, screen} from '@testing-library/react';
import {EditEventAnalysis} from '../EventsApp';

jest.unmock('react-dom');

describe('EventsApp', () => {
	it.each([
		[{id: '1'}, 'event-analysis/1'],
		[{}, 'event-analysis/create'],
	])('renders the route of EditEventAnalysis with %j', (params, path) => {
		render(
			<EditEventAnalysis channelId="321" groupId="23" params={params} />
		);

		expect(screen.getByText(path)).toBeInTheDocument();
	});
});
