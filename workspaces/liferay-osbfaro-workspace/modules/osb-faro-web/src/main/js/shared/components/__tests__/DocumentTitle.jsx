jest.unmock('../DocumentTitle');

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

const getFaviconLink = () => document.querySelector("link[rel~='icon']");

describe('DocumentTitle', () => {
	afterEach(() => {
		cleanup();

		document.head.innerHTML = '';
	});

	beforeEach(() => {
		useLDPEnabled.mockReturnValue(false);
	});

	it('should change the document title with analytics cloud appended', () => {
		renderWithRouter(<DocumentTitle title='test' />);

		expect(document.title).toEqual('test - Analytics Cloud');
	});

	it('should fall back to the product name alone for an empty title', () => {
		renderWithRouter(<DocumentTitle title='' />);

		expect(document.title).toEqual('Analytics Cloud');
	});

	it('should change the document title with liferay data platform appended for an LDP workspace', () => {
		useLDPEnabled.mockReturnValue(true);

		renderWithRouter(<DocumentTitle title='test' />);

		expect(document.title).toEqual('test - Liferay Data Platform');
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

	it('should not name a product while the plan check is loading', () => {
		renderWithRouter(<DocumentTitle loadingLDPEnabled title='test' />);

		expect(document.title).toEqual('test');
	});

	it('should name the product once the plan check resolves', () => {
		useLDPEnabled.mockReturnValue(true);

		const {rerender} = renderWithRouter(
			<DocumentTitle loadingLDPEnabled title='test' />
		);

		rerender(
			<StaticRouter>
				<DocumentTitle loadingLDPEnabled={false} title='test' />
			</StaticRouter>
		);

		expect(document.title).toEqual('test - Liferay Data Platform');
	});

	it('should point the favicon at the LDP branding for an LDP workspace', () => {
		useLDPEnabled.mockReturnValue(true);

		renderWithRouter(<DocumentTitle title='test' />);

		expect(getFaviconLink().getAttribute('href')).toEqual(
			'ldp_favicon.svg'
		);

		expect(getFaviconLink().getAttribute('type')).toEqual('image/svg+xml');
	});

	it('should point the favicon at the AC branding for a non LDP workspace', () => {
		renderWithRouter(<DocumentTitle title='test' />);

		expect(getFaviconLink().getAttribute('href')).toEqual('ac_favicon.svg');

		expect(getFaviconLink().getAttribute('type')).toEqual('image/svg+xml');
	});

	it('should swap the favicon when the plan changes without leaving a duplicate link', () => {
		const {rerender} = renderWithRouter(<DocumentTitle ldpEnabled title='test' />);

		rerender(
			<StaticRouter>
				<DocumentTitle ldpEnabled={false} title='test' />
			</StaticRouter>
		);

		expect(document.querySelectorAll("link[rel~='icon']")).toHaveLength(1);

		expect(getFaviconLink().getAttribute('href')).toEqual('ac_favicon.svg');
	});

	it('should reuse the shortcut icon link the theme renders instead of appending another', () => {
		document.head.innerHTML =
			'<link href="/favicon.ico" rel="shortcut icon" type="image/x-icon" />';

		renderWithRouter(<DocumentTitle ldpEnabled title='test' />);

		expect(document.querySelectorAll("link[rel~='icon']")).toHaveLength(1);

		expect(getFaviconLink().getAttribute('rel')).toEqual('shortcut icon');

		expect(getFaviconLink().getAttribute('type')).toEqual('image/svg+xml');
	});

	it('should create the favicon link when the document has none', () => {
		expect(getFaviconLink()).toBeNull();

		renderWithRouter(<DocumentTitle ldpEnabled title='test' />);

		expect(getFaviconLink()).toBeTruthy();

		expect(getFaviconLink().getAttribute('rel')).toEqual('icon');
	});

	it('should leave the theme favicon in place while the plan check is loading', () => {
		document.head.innerHTML =
			'<link href="/favicon.ico" rel="shortcut icon" type="image/x-icon" />';

		renderWithRouter(<DocumentTitle loadingLDPEnabled title='test' />);

		expect(getFaviconLink().getAttribute('href')).toEqual('/favicon.ico');

		expect(getFaviconLink().getAttribute('type')).toEqual('image/x-icon');
	});

	it('should not create a favicon link while the plan check is loading', () => {
		renderWithRouter(<DocumentTitle loadingLDPEnabled title='test' />);

		expect(getFaviconLink()).toBeNull();
	});

	it('should point the favicon at the branding once the plan check resolves', () => {
		const {rerender} = renderWithRouter(
			<DocumentTitle ldpEnabled loadingLDPEnabled title='test' />
		);

		rerender(
			<StaticRouter>
				<DocumentTitle ldpEnabled loadingLDPEnabled={false} title='test' />
			</StaticRouter>
		);

		expect(getFaviconLink().getAttribute('href')).toEqual(
			'ldp_favicon.svg'
		);
	});
});
