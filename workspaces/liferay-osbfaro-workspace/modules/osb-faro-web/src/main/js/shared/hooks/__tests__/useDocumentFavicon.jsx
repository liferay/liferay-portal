jest.unmock('../useDocumentFavicon');

import React from 'react';
import {cleanup, render} from '@testing-library/react';
import {useDocumentFavicon} from '../useDocumentFavicon';

jest.unmock('react-dom');

const Component = ({ldpEnabled}) => {
	useDocumentFavicon(ldpEnabled);

	return null;
};

const getFaviconLink = () => document.querySelector("link[rel~='icon']");

describe('useDocumentFavicon', () => {
	afterEach(() => {
		cleanup();

		document.head.innerHTML = '';
	});

	it('should point the favicon at the LDP branding when ldpEnabled is true', () => {
		render(<Component ldpEnabled />);

		expect(getFaviconLink().getAttribute('href')).toEqual(
			'ldp_favicon.svg'
		);

		expect(getFaviconLink().getAttribute('type')).toEqual('image/svg+xml');
	});

	it('should point the favicon at the AC branding when ldpEnabled is false', () => {
		render(<Component ldpEnabled={false} />);

		expect(getFaviconLink().getAttribute('href')).toEqual('ac_favicon.svg');

		expect(getFaviconLink().getAttribute('type')).toEqual('image/svg+xml');
	});

	it('should swap the favicon when the plan changes without leaving a duplicate link', () => {
		const {rerender} = render(<Component ldpEnabled />);

		rerender(<Component ldpEnabled={false} />);

		expect(document.querySelectorAll("link[rel~='icon']")).toHaveLength(1);

		expect(getFaviconLink().getAttribute('href')).toEqual('ac_favicon.svg');
	});

	it('should reuse the shortcut icon link the theme renders instead of appending another', () => {
		document.head.innerHTML =
			'<link href="/favicon.ico" rel="shortcut icon" type="image/x-icon" />';

		render(<Component ldpEnabled />);

		expect(document.querySelectorAll("link[rel~='icon']")).toHaveLength(1);

		expect(getFaviconLink().getAttribute('rel')).toEqual('shortcut icon');

		expect(getFaviconLink().getAttribute('type')).toEqual('image/svg+xml');
	});

	it('should create the favicon link when the document has none', () => {
		expect(getFaviconLink()).toBeNull();

		render(<Component ldpEnabled />);

		expect(getFaviconLink()).toBeTruthy();

		expect(getFaviconLink().getAttribute('rel')).toEqual('icon');
	});
});
