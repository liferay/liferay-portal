/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

/**
 * The analytics-client-js implementation relies on the non-standard
 * `innerText` property, which jsdom does not implement, so we need this
 * special helper in tests that sets `innerText` whenever `innerHTML` is
 * set.
 *
 * @see https://github.com/jsdom/jsdom/issues/1245
 */

if (!global.performance.timing) {
	Object.defineProperty(global.performance, 'timing', {
		get() {
			return {
				loadEventStart: 1,
				navigationStart: 0,
			};
		},
	});
}

/**
 * jsdom does not implement IntersectionObserver, which the visibility tracking
 * relies on. This mock reports an element as intersecting when its (mocked)
 * bounding rect overlaps the viewport — mirroring the isPartiallyInViewport
 * check the tracking used before — and delivers the callback asynchronously
 * like the real observer. Tests that place an element with `mockVisibleRect`
 * therefore keep working unchanged.
 */
class MockIntersectionObserver {
	_callback: IntersectionObserverCallback;

	constructor(callback: IntersectionObserverCallback) {
		this._callback = callback;
	}

	disconnect() {}

	observe(element: Element) {
		const {bottom, left, right, top} = element.getBoundingClientRect();

		const isIntersecting =
			top <= window.innerHeight &&
			bottom >= 0 &&
			left <= window.innerWidth &&
			right >= 0;

		Promise.resolve().then(() =>
			this._callback(
				[
					{isIntersecting, target: element},
				] as unknown as IntersectionObserverEntry[],
				this as unknown as IntersectionObserver
			)
		);
	}

	takeRecords() {
		return [] as IntersectionObserverEntry[];
	}

	unobserve() {}
}

(
	global as unknown as {
		IntersectionObserver: unknown;
	}
).IntersectionObserver = MockIntersectionObserver;

// Liferay.Util.Cookie = {
// 	TYPES: {
// 		FUNCTIONAL: 'CONSENT_TYPE_FUNCTIONAL',
// 		NECESSARY: 'CONSENT_TYPE_NECESSARY',
// 		PERFORMANCE: 'CONSENT_TYPE_PERFORMANCE',
// 		PERSONALIZATION: 'CONSENT_TYPE_PERSONALIZATION',
// 	},
// 	set: jest.fn(),
// };
