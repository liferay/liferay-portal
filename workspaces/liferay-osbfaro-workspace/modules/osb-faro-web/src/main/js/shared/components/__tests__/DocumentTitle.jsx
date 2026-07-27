jest.unmock('../DocumentTitle');
jest.unmock('shared/hooks/useDocumentFavicon');

import DocumentTitle from '../DocumentTitle';
import React from 'react';
import {cleanup, render} from '@testing-library/react';
import {StaticRouter} from 'react-router-dom';
import {useLDPEnabled} from 'shared/hooks/useLDPEnabled';

jest.unmock('react-dom');

jest.mock('shared/hooks/useLDPEnabled', () => ({
	useLDPEnabled: jest.fn()
}));

const renderWithRouter = ui => render(<StaticRouter>{ui}</StaticRouter>);

describe('DocumentTitle', () => {
	afterEach(cleanup);

	beforeEach(() => {
		useLDPEnabled.mockReturnValue(false);
	});

	it('should change the document title with analytics cloud appended', () => {
		renderWithRouter(<DocumentTitle title='test' />);

		expect(document.title).toEqual('test - Analytics Cloud');
	});

	it('should change the document title with the provided product name appended', () => {
		renderWithRouter(
			<DocumentTitle
				productName='Liferay Data Platform'
				title='test'
			/>
		);

		expect(document.title).toEqual('test - Liferay Data Platform');
	});

	it('should change the document title with liferay data platform appended for an LDP workspace', () => {
		useLDPEnabled.mockReturnValue(true);

		renderWithRouter(<DocumentTitle title='test' />);

		expect(document.title).toEqual('test - Liferay Data Platform');
	});

	it('should point the favicon at the branding matching the resolved plan', () => {
		renderWithRouter(<DocumentTitle title='test' />);

		expect(
			document.querySelector("link[rel~='icon']").getAttribute('href')
		).toEqual('ac_favicon.svg');

		useLDPEnabled.mockReturnValue(true);

		cleanup();

		renderWithRouter(<DocumentTitle title='test' />);

		expect(
			document.querySelector("link[rel~='icon']").getAttribute('href')
		).toEqual('ldp_favicon.svg');
	});

	it('should prefer an explicit ldpEnabled over the route-derived value', () => {
		renderWithRouter(<DocumentTitle ldpEnabled title='test' />);

		expect(document.title).toEqual('test - Liferay Data Platform');
	});

	it('should prefer an explicit ldpEnabled of false over the route-derived value', () => {
		useLDPEnabled.mockReturnValue(true);

		renderWithRouter(<DocumentTitle ldpEnabled={false} title='test' />);

		expect(document.title).toEqual('test - Analytics Cloud');
	});

	it('should prefer an explicit product name over the LDP-computed default', () => {
		useLDPEnabled.mockReturnValue(true);

		renderWithRouter(
			<DocumentTitle productName='Custom Name' title='test' />
		);

		expect(document.title).toEqual('test - Custom Name');
	});
});
