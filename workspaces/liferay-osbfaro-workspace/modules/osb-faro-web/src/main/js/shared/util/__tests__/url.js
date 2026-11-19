import {
	removePageParam,
	removeUriQueryParam,
	resetPaginationParams,
	setUriFilterValues,
	setUriQueryValue,
	setUriQueryValues,
} from '../url';
import {Map, Set} from 'immutable';

describe('setUriFilterValues', () => {
	it('should add filter queries to url and return as a string', () => {
		const mockFilterBy = new Map({
			biz: new Set(['buz']),
			foo: new Set(['bar', 'baz']),
		});

		const url = 'http://www.liferay.com';

		expect(setUriFilterValues(mockFilterBy, url)).toBe(
			'/?biz=buz&foo=bar%2Cbaz'
		);
	});
});

describe('setUriQueryValue', () => {
	it('should add query to url and return as a string', () => {
		const url = 'http://www.liferay.com';

		expect(setUriQueryValue(url, 'foo', 'bar')).toBe('/?foo=bar');

		expect(setUriQueryValue(url, 'foo')).toBe('/');
	});
});

describe('setUriQueryValues', () => {
	it('should add multiple queries to url and return as a string', () => {
		const url = 'http://www.liferay.com';

		expect(setUriQueryValues({baz: 'qux', foo: 'bar'}, url)).toBe(
			'/?baz=qux&foo=bar'
		);
	});
});

describe('removePageParam', () => {
	it('should remove page query string', () => {
		const url = 'http://www.liferay.com/';

		expect(removePageParam(null, `${url}?page=3`)).toBe('/');
	});

	it('should remove page query string and set new path', () => {
		const url = 'http://www.liferay.com/';

		expect(removePageParam('/bar', `${url}foo?page=3`)).toBe('/bar');
	});
});

describe('removeUriQueryParam', () => {
	it('should remove uri query param', () => {
		const href =
			'http://localhost:3000/project/33551/touchpoints/?sortField=views';
		const name = 'sortField';
		const removeURI = removeUriQueryParam(href, name);

		expect(removeURI).toEqual('/project/33551/touchpoints/');
	});
});

describe('resetPaginationParams', () => {
	it('should reset the pagination parameters to the default value', () => {
		const url = 'http://www.liferay.com/';

		expect(
			resetPaginationParams(null, `${url}?page=3&orderBy=desc&query=test`)
		).toBe('/?page=1&orderBy=asc&query=');
	});
});

describe('setUriFilterValues', () => {
	it('should set the uri filter params from the filterBy Map', () => {
		const url = 'http://www.liferay.com';

		const mockFilterBy = new Map({
			devices: new Set(['desktop', 'mobile']),
			foo: new Set(['bar']),
		});

		expect(setUriFilterValues(mockFilterBy, url)).toBe(
			'/?devices=desktop%2Cmobile&foo=bar'
		);
	});
});
