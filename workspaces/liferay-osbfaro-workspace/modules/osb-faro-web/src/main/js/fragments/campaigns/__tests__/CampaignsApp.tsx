jest.mock(
	'../../SectionApp',
	() =>
		({path}: {path: string}) =>
			path
);

import React from 'react';
import {render, screen} from '@testing-library/react';
import {Campaign} from '../CampaignsApp';

jest.unmock('react-dom');

describe('CampaignsApp', () => {
	it('renders the route of Campaign', () => {
		render(<Campaign channelId="321" groupId="23" params={{id: '1'}} />);

		expect(screen.getByText('campaigns/1')).toBeInTheDocument();
	});
});
