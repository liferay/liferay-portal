import '@testing-library/jest-dom';
import 'jest-extended/all';
import lang from './lang';
import {TextDecoder, TextEncoder} from 'util';

// Temporarily silence console.error/warn in tests until the underlying
// deprecations (Apollo Client v3, react-dom/test-utils, graphql HOC/<Query />,
// cache merge policies) are addressed. See MIGRATION_PLAN.md, Phase 12.
// The `./pedantic` module remains available to re-enable the strict behavior
// (throw on any console.error/warn) once tests are clean.

console.error = jest.fn(); // eslint-disable-line no-console
console.warn = jest.fn(); // eslint-disable-line no-console

jest.mock('shared/api');
jest.mock('shared/components/DocumentTitle');
jest.mock('react-dom');

document.body.className = 'dxp';

global.ResizeObserver = jest.fn().mockImplementation(() => ({
	disconnect: jest.fn(),
	observe: jest.fn(),
	unobserve: jest.fn(),
}));

global.analytics = {
	group: () => {},
	identify: () => {},
	track: () => {},
};

global.AUI = () => ({
	use: (module, callback) => callback(),
});

global.Liferay = {
	FeatureFlags: {},
	Language: {
		get: lang,
	},
};

global.localStorage = (() => {
	let store = {};

	return {

		/**
		 * Clear
		 */
		clear() {
			store = {};
		},

		/**
		 * Get Item
		 * @param {string} key
		 */
		getItem(key) {
			return store[key];
		},

		/**
		 * Remove Item
		 * @param {string} key
		 */
		removeItem(key) {
			delete store[key];
		},

		/**
		 * Set Item
		 * @param {string} key
		 * @param {string} value
		 */
		setItem(key, value) {
			store[key] = value.toString();
		},
	};
})();

global.pendo = {
	initialize: () => {},
};

global.TextDecoder = TextDecoder;
global.TextEncoder = TextEncoder;

// if (typeof queueMicrotask === 'undefined') {
// 	global.queueMicrotask = cb => Promise.resolve().then(cb);
// }

require('jest-extended/all');
require('jest-canvas-mock');
