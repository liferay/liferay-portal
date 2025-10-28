/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import register from '../../../src/main/resources/META-INF/resources/liferay/portlet/register.es';

describe('PortletHub', () => {
	describe('createResourceUrl', () => {
		const ids = global.portlet.getIds();
		const portletA = ids[0];
		const portletB = ids[1];

		const onStateChange = jest.fn();
		let hubA;
		let hubB;
		let listenerA;

		beforeEach(() => {
			return Promise.all([register(portletA), register(portletB)]).then(
				(values) => {
					hubA = values[0];

					listenerA = hubA.addEventListener(
						'portlet.onStateChange',
						onStateChange
					);

					hubB = values[1];
				}
			);
		});

		afterEach(() => {
			hubA.removeEventListener(listenerA);
			hubA = null;
			hubB = null;
		});

		it('is present in the register return object and is a function', () => {
			expect(typeof hubA.createResourceUrl).toEqual('function');
		});

		it('throws a TypeError if too many (>3) arguments are provided', () => {
			const testFn = () => {
				hubA.createResourceUrl(null, 'param1', 'param2', 'param3');
			};

			expect(testFn).toThrow(
				'Too many arguments. 3 arguments are allowed.'
			);
		});

		it('throws a TypeError if resource parameters is invalid', () => {
			const parameters = {
				param1: 'paramValue1',
			};

			const testFn = () => {
				hubA.createResourceUrl(parameters, 'cacheLevelPortlet');
			};

			expect(testFn).toThrow(TypeError);
		});

		it('throws a TypeError if the cacheability argument is invalid', () => {
			const parameters = {
				param1: ['paramValue1'],
			};

			const testFn = () => {
				hubA.createResourceUrl(parameters, 'Invalid');
			};

			expect(testFn).toThrow(TypeError);
		});

		it('throws a TypeError if there are 2 cacheability arguments', () => {
			const testFn = () => {
				hubA.createResourceUrl('cacheLevelPage', 'cacheLevelFull');
			};

			expect(testFn).toThrow(TypeError);
		});

		it('throws a TypeError if there are 2 res parameters arguments', () => {
			const parameters = {
				param1: ['paramValue1'],
			};

			const testFn = () => {
				return hubA.createResourceUrl(parameters, parameters);
			};

			expect(testFn).toThrow(TypeError);
		});

		it('does not throw if both arguments are valid', (done) => {
			const parameters = {
				param1: ['paramValue1'],
			};

			hubA.createResourceUrl(parameters, 'cacheLevelPage').then(() => {
				done();
			});
		});

		it('returns a string if both arguments are valid', () => {
			const parameters = {
				param1: ['paramValue1'],
			};

			return hubA
				.createResourceUrl(parameters, 'cacheLevelFull')
				.then((url) => {
					expect(typeof url).toEqual('string');
				});
		});

		it('Throws an exception if cacheability is specified first', () => {
			const parameters = {
				param1: ['paramValue1'],
			};

			const testFn = () => {
				return hubA.createResourceUrl('cacheLevelPage', parameters);
			};

			expect(testFn).toThrow(TypeError);
		});

		it('returns a string if only cacheability present', () => {
			return hubA
				.createResourceUrl(null, 'cacheLevelPortlet')
				.then((url) => {
					expect(typeof url).toEqual('string');
				});
		});

		it('returns a string if only resource parameters present', () => {
			const parameters = {
				param1: ['paramValue1'],
				param2: ['paramValue2'],
			};

			return hubA.createResourceUrl(parameters).then((url) => {
				expect(typeof url).toEqual('string');
			});
		});

		it('returns a string if no parameters present', () => {
			return hubA.createResourceUrl().then((url) => {
				expect(typeof url).toEqual('string');
			});
		});

		it('returns a URL indicating the initiating portlet A', () => {
			const parameters = {
				param1: ['paramValue1'],
				param2: ['paramValue2'],
			};

			return hubA
				.createResourceUrl(parameters, 'cacheLevelPage')
				.then((url) => {
					expect(typeof url).toEqual('string');
				});
		});

		it('returns a resource URL', () => {
			const cache = 'cacheLevelPage';
			const parameters = {
				param1: ['paramValue1'],
				param2: ['paramValue2'],
			};

			return hubB
				.createResourceUrl(parameters, cache, 'myResourceId')
				.then((url) => {
					expect(
						global.portlet.resource.isResourceUrl(url)
					).toBeTruthy();
				});
		});

		it('returns a URL with cacheability set to "cacheLevelPage"', () => {
			const cache = 'cacheLevelPage';
			const parameters = {
				param1: ['paramValue1'],
				param2: ['paramValue2'],
			};

			return hubB.createResourceUrl(parameters, cache).then((url) => {
				const str = global.portlet.resource.getCacheability(url);

				expect(str).toEqual(cache);
			});
		});
	});
});
