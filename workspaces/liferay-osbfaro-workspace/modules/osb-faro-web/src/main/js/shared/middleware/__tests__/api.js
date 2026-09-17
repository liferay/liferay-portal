jest.mock('shared/util/request');

import api, {CALL_API, toAction} from '../api';
import sendRequest from 'shared/util/request';

describe('API Middleware', () => {
	it('should call next middleware if not an API call', () => {
		sendRequest.setResponseData({foo: 'bar'});

		const next = jest.fn();

		const action = {
			type: 'TEST_REQUEST',
		};

		api()(next)(action);

		expect(next).toBeCalledWith(action);
	});

	it('should return a promise', () => {
		const action = {
			meta: {
				[CALL_API]: {
					data: {foo: 'bar'},
					method: 'GET',
					path: 'test/path',
					types: ['TEST_REQUEST', 'TEST_SUCCESS', 'TEST_FAILURE'],
				},
				schema: 'schema',
			},
			type: 'NO_OP',
		};

		const next = jest.fn();

		const result = api()(next)(action);

		expect(result instanceof Promise).toBe(true);
	});

	it('should call the next middleware with an action', () => {
		const action = {
			meta: {
				[CALL_API]: {
					data: {foo: 'bar'},
					method: 'GET',
					path: 'test/path',
					types: ['TEST_REQUEST', 'TEST_SUCCESS', 'TEST_FAILURE'],
				},
				schema: 'schema',
			},
			type: 'NO_OP',
		};

		const next = jest.fn();

		api()(next)(action);

		expect(next.mock.calls[0][0].type).toBeTruthy();
	});

	it('should call the next middleware with the successful action', () => {
		const action = {
			meta: {
				[CALL_API]: {
					data: {foo: 'bar'},
					method: 'GET',
					path: 'test/path',
					types: ['TEST_REQUEST', 'TEST_SUCCESS', 'TEST_FAILURE'],
				},
				schema: 'schema',
			},
			type: 'NO_OP',
		};

		const next = jest.fn();

		return api()(next)(action).then((val) =>
			expect(val.payload.foo).toBe('bar')
		);
	});

	it('should call the next middleware with the failure action', () => {
		const action = {
			meta: {
				[CALL_API]: {
					data: {foo: 'bar'},
					method: 'GET',
					path: 'test/path',
					types: ['TEST_REQUEST', 'TEST_SUCCESS', 'TEST_FAILURE'],
				},
				schema: 'schema',
			},
			type: 'NO_OP',
		};

		const next = jest.fn();

		return api()(next)(action).catch((val) =>
			expect(val.reason).toBe('bar')
		);
	});

	it('should put the error status on the failure action', () => {
		const error = new Error('Unauthorized Access');

		error.status = 403;

		const action = {
			meta: {
				[CALL_API]: {
					data: {foo: 'bar'},
					requestFn: () => Promise.reject(error),
					types: ['TEST_REQUEST', 'TEST_SUCCESS', 'TEST_FAILURE'],
				},
			},
			payload: {id: 'KOR-1'},
			type: 'NO_OP',
		};

		const next = jest.fn();

		return api()(next)(action).catch(() => {
			expect(next).toBeCalledTimes(2);
			expect(next.mock.calls[1][0].errorStatus).toBe(403);
		});
	});

	it('should call requestFn if requestFn exists', () => {
		const requestFn = jest.fn().mockReturnValue(Promise.resolve(''));

		const action = {
			meta: {
				[CALL_API]: {
					data: {foo: 'bar'},
					requestFn,
					types: ['TEST_REQUEST', 'TEST_SUCCESS', 'TEST_FAILURE'],
				},
				schema: 'schema',
			},
			type: 'NO_OP',
		};

		const next = jest.fn();

		api()(next)(action).then(() => expect(requestFn).toBeCalled());
	});
});

describe('toAction', () => {
	it('should return an action object', () => {
		const actionType = 'TEST';

		const action = toAction(actionType, {meta: {}});

		expect(action.type).toBe(actionType);
	});

	it('should not contain api call data', () => {
		const action = toAction('TEST', {
			meta: {
				[CALL_API]: 1,
				id: 15,
			},
		});

		expect(action.meta[CALL_API]).toBeUndefined();
	});
});
