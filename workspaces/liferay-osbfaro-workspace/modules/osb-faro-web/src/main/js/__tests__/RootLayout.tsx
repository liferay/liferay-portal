import React from 'react';
import RootLayout from '../RootLayout';
import {render, screen} from '@testing-library/react';

jest.unmock('react-dom');

jest.mock('react-router-dom', () => ({
	...jest.requireActual('react-router-dom'),
	Outlet: () => null,
	ScrollRestoration: () => <span data-testid="scroll-restoration" />,
	useMatch: () => null,
}));

jest.mock('react-redux', () => ({
	useSelector: () => undefined,
}));

jest.mock('shared/components/AlertFeed', () => () => null);

jest.mock('shared/components/ModalRenderer', () => () => null);

jest.mock('shared/hooks/useCurrentUser', () => ({
	useFetchCurrentUser: () => ({data: undefined, loading: false}),
}));

jest.mock('shared/util/ai-hub-chatbot', () => ({
	syncAIHubChatbot: jest.fn(),
}));

jest.mock('shared/util/pendo', () => ({
	...jest.requireActual('shared/util/pendo'),
	Pendo: jest.fn().mockImplementation(() => ({
		getUserConsent: jest.fn(),
		initialize: jest.fn(),
	})),
}));

describe('RootLayout', () => {
	it('lets the router manage the scroll on navigation', () => {
		render(<RootLayout />);

		expect(screen.getByTestId('scroll-restoration')).toBeInTheDocument();
	});
});
