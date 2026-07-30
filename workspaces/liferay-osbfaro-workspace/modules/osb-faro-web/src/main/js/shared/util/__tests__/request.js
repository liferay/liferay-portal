jest.mock('../router');

import 'whatwg-fetch';
import request, {
	addParams,
	getFormData,
	getRequestData,
	getServiceError,
	parseFromJSON,
	serializeQueryString,
	stringifyValues,
} from '../request';
import {reloadPage} from '../router';

describe('addParams', () => {
	it('should correctly append query string params', () => {
		const result = addParams('http://www.test.com', {
			name: 'joe',
			title: 'blogger',
		});

		expect(result).toContain('joe');
		expect(result).toContain('blogger');
	});

	it('should use correct param separator', () => {
		const result = addParams('http://www.test.com?parameter=1', {
			name: 'joe',
		});

		expect(result.split('?').length - 1).toBe(1);
	});
});

describe('getFormData', () => {
	it('should return a FormData object', () => {
		const formData = getFormData({
			name: 'test',
		});

		expect(formData instanceof FormData).toBe(true);
	});

	it('should encode form values', () => {
		const name = 'test~!@#$%^&*()-=+_;:?><,./';

		const formData = getFormData({name});

		expect(formData.get('name')).toBe(encodeURIComponent(name));
	});
});

describe('parseFromJSON', () => {
	it('should return a parsed JSON object', () => {
		const expected = {foo: 'bar'};

		expect(parseFromJSON(JSON.stringify(expected))).toEqual(expected);
	});
});

describe('getServiceError', () => {
	it('should return a parsed service error', () => {
		const nestedError = {status: 403};

		const err = {message: JSON.stringify(nestedError)};

		expect(getServiceError(err)).toEqual(nestedError);
	});

	it('should return null if not a service error', () => {
		const nestedError = {status: 500};

		const err = {message: JSON.stringify(nestedError)};

		expect(getServiceError(JSON.stringify(err))).toBeNull();
	});
});

describe('serializeQueryString', () => {
	it('should contain params', () => {
		const queryString = serializeQueryString(
			{
				name: 'test',
			},
			true
		);

		expect(queryString).toContain('name');
	});

	it('should contain params', () => {
		const queryString = serializeQueryString({
			name: 'joe',
		});

		expect(queryString).toContain('joe');
	});
});

describe('request', () => {
	beforeEach(() => {
		window.fetch = jest.fn();
	});

	afterEach(() => {
		fetch.mockClear();
	});

	it('should return a Promise', () => {
		fetch.mockReturnValue(Promise.resolve(new Response()));

		const result = request({}).catch(jest.fn());

		expect(result instanceof Promise).toBe(true);
	});

	it('should correctly create request URL', () => {
		fetch.mockReturnValue(Promise.resolve(new Response('')));

		return request({
			contentType: '',
			method: 'GET',
			path: 'contacts/field_mapping',
		}).then(() => {
			const requestURL = fetch.mock.calls[0][0];

			expect(requestURL).toContain('field_mapping');
			expect(requestURL).toContain('contacts');
		});
	});

	it('should update requestURL if method is GET', () => {
		fetch.mockReturnValue(Promise.resolve(new Response('', {status: 204})));

		return request({
			data: {
				a: 2,
			},
			method: 'GET',
			path: 'contacts/field_mapping',
		}).then(() => {
			const requestURL = fetch.mock.calls[0][0];

			expect(requestURL).toContain('field_mapping?a=2');
		});
	});

	it('should resolve with the parsed response', () => {
		const value = 25;

		fetch.mockReturnValue(Promise.resolve(new Response(`{"a": ${value}}`)));

		return request({}).then((data) => expect(data.a).toBe(value));
	});

	it('should throw an error if the response cannot be parsed', () => {
		fetch.mockReturnValue(Promise.resolve(new Response('{test:}')));

		return request({}).catch((error) =>
			expect(error instanceof SyntaxError).toBe(true)
		);
	});

	it('should reject on a xhr status greater than or equal to 300', () => {
		fetch.mockReturnValue(Promise.resolve(new Response('', {status: 301})));

		return request({}).catch((error) =>
			expect(error instanceof Error).toBe(true)
		);
	});

	it('should handle an xhr status of 204 where the response is empty', () => {
		fetch.mockReturnValue(Promise.resolve(new Response('', {status: 204})));

		return request({}).then((response) => expect(response).toEqual({}));
	});

	it('should reject on a xhr status equal to 403', () => {
		fetch.mockReturnValue(
			Promise.resolve(
				new Response('', {
					status: 403,
				})
			)
		);

		return request({}).catch((error) =>
			expect(error instanceof Error).toBe(true)
		);
	});

	it('should reject on a xhr status equal to 500 with a messageKey', () => {
		fetch.mockReturnValue(
			Promise.resolve(
				new Response('{ "messageKey": "test"}', {
					status: 500,
				})
			)
		);

		return request({}).catch((error) => {
			expect(error instanceof Error).toBe(true);
			expect(error.message).toBe('test');
		});
	});

	it('should reject with a generic error on a 500 with a non-JSON body', () => {
		fetch.mockReturnValue(
			Promise.resolve(
				new Response('<html><body>500</body></html>', {
					status: 500,
				})
			)
		);

		return request({}).catch((error) => {
			expect(error instanceof SyntaxError).toBe(false);
			expect(error.message).toBe('Request Error');
		});
	});

	it('should call reloadPage on an xhr status equal to 401', () => {
		fetch.mockReturnValue(
			Promise.resolve(
				new Response('', {
					status: 401,
				})
			)
		);

		return request({}).then(() => expect(reloadPage).toBeCalled());
	});

	it('should reject if the response is not valid JSON', () => {
		fetch.mockReturnValue(
			Promise.resolve(new Response('this is definitely not json'))
		);

		return expect(request({})).rejects.toBeInstanceOf(SyntaxError);
	});
});

describe('getRequestData', () => {
	it('should omit undefined values so they are not sent as the string "undefined"', () => {
		const data = getRequestData({
			channelsConfiguration: {enableAllChannels: true},
			name: undefined,
		});

		expect(data).not.toHaveProperty('name');
		expect(data.channelsConfiguration).toBe(
			JSON.stringify({enableAllChannels: true})
		);
	});

	it('should keep values that are defined but falsy', () => {
		const data = getRequestData({
			enabled: false,
			name: '',
			total: 0,
		});

		expect(data.enabled).toBe(false);
		expect(data.name).toBe('');
		expect(data.total).toBe(0);
	});
});

describe('stringifyValues', () => {
	it('should serialize instances of objects', () => {
		const arr = ['foo'];
		const obj = {foo: 'bar'};

		const val = stringifyValues({
			arr,
			obj,
		});

		expect(val.arr).toBe(JSON.stringify(arr));
		expect(val.obj).toBe(JSON.stringify(obj));
	});
});
