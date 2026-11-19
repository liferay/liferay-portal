jest.mock(
	'../../SectionApp',
	() =>
		({path}: {path: string}) =>
			path
);

import React from 'react';
import {render, screen} from '@testing-library/react';
import {Test} from '../TestsApp';

jest.unmock('react-dom');

describe('TestsApp', () => {
	it('renders the route of Test', () => {
		render(<Test channelId="321" groupId="23" params={{id: '1'}} />);

		expect(screen.getByText('tests/overview/1')).toBeInTheDocument();
	});
});
