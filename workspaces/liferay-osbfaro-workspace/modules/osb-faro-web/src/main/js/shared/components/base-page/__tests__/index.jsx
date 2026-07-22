import mockStore from 'test/mock-store';
import React from 'react';
import {BasePage} from '../index';
import {cleanup, render} from '@testing-library/react';
import {Provider} from 'react-redux';
import {StaticRouter} from 'react-router';
import {useLDPEnabled} from 'shared/hooks/useLDPEnabled';

jest.unmock('react-dom');
jest.unmock('shared/components/DocumentTitle');

jest.mock('shared/hooks/useLDPEnabled', () => ({
	useLDPEnabled: jest.fn()
}));

describe('BasePage', () => {
	afterEach(cleanup);

	beforeEach(() => {
		useLDPEnabled.mockReturnValue(false);
	});

	it('renders BasePage', () => {
		const {container} = render(
			<Provider store={mockStore()}>
				<StaticRouter>
					<BasePage documentTitle='Test title'>
						{'Test test'}
					</BasePage>
				</StaticRouter>
			</Provider>
		);

		expect(container).toMatchSnapshot();
	});

	it('sets the document title with Analytics Cloud for a non-LDP workspace', () => {
		render(
			<Provider store={mockStore()}>
				<StaticRouter>
					<BasePage documentTitle='Test title' />
				</StaticRouter>
			</Provider>
		);

		expect(document.title).toEqual('Test title - Analytics Cloud');
	});

	it('sets the document title with Liferay Data Platform for an LDP workspace', () => {
		useLDPEnabled.mockReturnValue(true);

		render(
			<Provider store={mockStore()}>
				<StaticRouter>
					<BasePage documentTitle='Test title' />
				</StaticRouter>
			</Provider>
		);

		expect(document.title).toEqual('Test title - Liferay Data Platform');
	});
});
