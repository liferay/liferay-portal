/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {trackVisibleElements} from '../../src/utils/trackVisibleElements';
import {wait} from '../helpers';

// jsdom implements MutationObserver but not IntersectionObserver, so the tests
// install a controllable IntersectionObserver and drive intersection manually.

class MockIntersectionObserver {
	static instances: MockIntersectionObserver[] = [];

	callback: IntersectionObserverCallback;
	elements = new Set<Element>();

	constructor(callback: IntersectionObserverCallback) {
		this.callback = callback;

		MockIntersectionObserver.instances.push(this);
	}

	disconnect() {
		this.elements.clear();
	}

	observe(element: Element) {
		this.elements.add(element);
	}

	unobserve(element: Element) {
		this.elements.delete(element);
	}
}

// Drives every observer currently watching the element with the given state.

function intersect(element: Element, isIntersecting = true) {
	MockIntersectionObserver.instances.forEach((observer) => {
		if (observer.elements.has(element)) {
			observer.callback(
				[
					{isIntersecting, target: element},
				] as unknown as IntersectionObserverEntry[],
				observer as unknown as IntersectionObserver
			);
		}
	});
}

// isVisible (scroll.ts) delegates to element.checkVisibility, so each element
// carries a controllable mock of it to stand in for CSS visibility.

function createElement(visible = true) {
	const element = document.createElement('div');

	element.className = 'trackable';

	const checkVisibility = jest.fn(() => visible);

	(element as unknown as {checkVisibility: jest.Mock}).checkVisibility =
		checkVisibility;

	document.body.appendChild(element);

	return {checkVisibility, element};
}

describe('trackVisibleElements', () => {
	beforeEach(() => {
		MockIntersectionObserver.instances = [];

		(
			global as unknown as {
				IntersectionObserver: unknown;
			}
		).IntersectionObserver = MockIntersectionObserver;
	});

	afterEach(() => {
		document.body.innerHTML = '';

		jest.restoreAllMocks();
	});

	it('calls onVisible once for a visible, trackable element that intersects', () => {
		const {element} = createElement();

		const onVisible = jest.fn();

		const stopTracking = trackVisibleElements<HTMLElement>({
			isTrackable: () => true,
			onVisible,
			selector: '.trackable',
		});

		intersect(element);

		expect(onVisible).toHaveBeenCalledTimes(1);
		expect(onVisible).toHaveBeenCalledWith(element);
		expect(element.dataset.analyticsAssetViewed).toBe('true');

		stopTracking();
	});

	it('does not call onVisible before the element intersects', () => {
		createElement();

		const onVisible = jest.fn();

		const stopTracking = trackVisibleElements<HTMLElement>({
			isTrackable: () => true,
			onVisible,
			selector: '.trackable',
		});

		expect(onVisible).not.toHaveBeenCalled();

		stopTracking();
	});

	it('does not call onVisible for a CSS-hidden element', () => {
		const {element} = createElement(false);

		const onVisible = jest.fn();

		const stopTracking = trackVisibleElements<HTMLElement>({
			isTrackable: () => true,
			onVisible,
			selector: '.trackable',
		});

		intersect(element);

		expect(onVisible).not.toHaveBeenCalled();
		expect(element.dataset.analyticsAssetViewed).toBeUndefined();

		stopTracking();
	});

	it('does not call onVisible for a non-trackable element', () => {
		const {element} = createElement();

		const onVisible = jest.fn();

		const stopTracking = trackVisibleElements<HTMLElement>({
			isTrackable: () => false,
			onVisible,
			selector: '.trackable',
		});

		intersect(element);

		expect(onVisible).not.toHaveBeenCalled();

		stopTracking();
	});

	it('calls onVisible when a hidden element later becomes visible', async () => {
		const {checkVisibility, element} = createElement(false);

		const onVisible = jest.fn();

		const stopTracking = trackVisibleElements<HTMLElement>({
			isTrackable: () => true,
			onVisible,
			selector: '.trackable',
		});

		intersect(element);

		expect(onVisible).not.toHaveBeenCalled();

		checkVisibility.mockReturnValue(true);

		element.classList.add('shown');

		await wait(0);

		expect(onVisible).toHaveBeenCalledTimes(1);

		stopTracking();
	});

	it('calls onVisible when an ancestor reveal fires a transitionend without changing the element attributes', async () => {
		const {checkVisibility, element} = createElement(false);

		// The element is revealed by an ANCESTOR (e.g. a menu opening via a
		// CSS `:has(:checked)` / opacity transition), so neither the element's
		// geometry nor its own class/style change.

		const container = document.createElement('div');

		document.body.appendChild(container);
		container.appendChild(element);

		const onVisible = jest.fn();

		const stopTracking = trackVisibleElements<HTMLElement>({
			isTrackable: () => true,
			onVisible,
			selector: '.trackable',
		});

		intersect(element);

		expect(onVisible).not.toHaveBeenCalled();

		checkVisibility.mockReturnValue(true);

		container.dispatchEvent(new Event('transitionend', {bubbles: true}));

		await wait(0);

		expect(onVisible).toHaveBeenCalledTimes(1);
		expect(element.dataset.analyticsAssetViewed).toBe('true');

		stopTracking();
	});

	it('calls onVisible when a click reveals a previously hidden element', async () => {
		const {checkVisibility, element} = createElement(false);

		const onVisible = jest.fn();

		const stopTracking = trackVisibleElements<HTMLElement>({
			isTrackable: () => true,
			onVisible,
			selector: '.trackable',
		});

		intersect(element);

		expect(onVisible).not.toHaveBeenCalled();

		checkVisibility.mockReturnValue(true);

		document.body.dispatchEvent(new Event('click', {bubbles: true}));

		await wait(0);

		expect(onVisible).toHaveBeenCalledTimes(1);

		stopTracking();
	});

	it('calls onVisible when an ancestor reveal fires an animationend', async () => {
		const {checkVisibility, element} = createElement(false);

		const container = document.createElement('div');

		document.body.appendChild(container);
		container.appendChild(element);

		const onVisible = jest.fn();

		const stopTracking = trackVisibleElements<HTMLElement>({
			isTrackable: () => true,
			onVisible,
			selector: '.trackable',
		});

		intersect(element);

		expect(onVisible).not.toHaveBeenCalled();

		checkVisibility.mockReturnValue(true);

		container.dispatchEvent(new Event('animationend', {bubbles: true}));

		await wait(0);

		expect(onVisible).toHaveBeenCalledTimes(1);

		stopTracking();
	});

	it('ignores a transitionend that did not originate from an ancestor', async () => {
		const {checkVisibility, element} = createElement(false);

		const container = document.createElement('div');

		document.body.appendChild(container);
		container.appendChild(element);

		const unrelated = document.createElement('div');

		document.body.appendChild(unrelated);

		const onVisible = jest.fn();

		const stopTracking = trackVisibleElements<HTMLElement>({
			isTrackable: () => true,
			onVisible,
			selector: '.trackable',
		});

		intersect(element);

		checkVisibility.mockReturnValue(true);

		// A transition on an unrelated element must not re-check the pending
		// element, even though it is now visible.

		unrelated.dispatchEvent(new Event('transitionend', {bubbles: true}));

		await wait(0);

		expect(onVisible).not.toHaveBeenCalled();

		// The reveal transition on an ancestor does.

		container.dispatchEvent(new Event('transitionend', {bubbles: true}));

		await wait(0);

		expect(onVisible).toHaveBeenCalledTimes(1);

		stopTracking();
	});

	it('attaches the reveal listeners only while an element is pending', async () => {
		const revealEvents = ['animationend', 'click', 'transitionend'];

		const addEventListener = jest.spyOn(document, 'addEventListener');
		const removeEventListener = jest.spyOn(document, 'removeEventListener');

		const {checkVisibility, element} = createElement(false);

		const onVisible = jest.fn();

		const stopTracking = trackVisibleElements<HTMLElement>({
			isTrackable: () => true,
			onVisible,
			selector: '.trackable',
		});

		intersect(element);

		const added = addEventListener.mock.calls.filter((call) =>
			revealEvents.includes(call[0])
		);

		expect(added).toHaveLength(revealEvents.length);

		checkVisibility.mockReturnValue(true);

		element.classList.add('shown');

		await wait(0);

		expect(onVisible).toHaveBeenCalledTimes(1);

		const removed = removeEventListener.mock.calls.filter((call) =>
			revealEvents.includes(call[0])
		);

		expect(removed).toHaveLength(revealEvents.length);

		stopTracking();
	});

	it('does not call onVisible more than once for the same element', () => {
		const {element} = createElement();

		const onVisible = jest.fn();

		const stopTracking = trackVisibleElements<HTMLElement>({
			isTrackable: () => true,
			onVisible,
			selector: '.trackable',
		});

		intersect(element);
		intersect(element);

		expect(onVisible).toHaveBeenCalledTimes(1);

		stopTracking();
	});

	it('does not report an element already flagged as viewed', () => {
		const {element} = createElement();

		element.setAttribute('data-analytics-asset-viewed', 'true');

		const onVisible = jest.fn();

		const stopTracking = trackVisibleElements<HTMLElement>({
			isTrackable: () => true,
			onVisible,
			selector: '.trackable',
		});

		intersect(element);

		expect(onVisible).not.toHaveBeenCalled();

		stopTracking();
	});

	it('does not report an element already flagged by another instance', () => {
		const {element} = createElement();

		const onVisibleFirst = jest.fn();
		const onVisibleSecond = jest.fn();

		const stopTrackingFirst = trackVisibleElements<HTMLElement>({
			isTrackable: () => true,
			onVisible: onVisibleFirst,
			selector: '.trackable',
		});
		const stopTrackingSecond = trackVisibleElements<HTMLElement>({
			isTrackable: () => true,
			onVisible: onVisibleSecond,
			selector: '.trackable',
		});

		intersect(element);

		expect(onVisibleFirst).toHaveBeenCalledTimes(1);
		expect(onVisibleSecond).not.toHaveBeenCalled();

		stopTrackingFirst();
		stopTrackingSecond();
	});

	it('observes elements added to the DOM after tracking starts', async () => {
		const onVisible = jest.fn();

		const stopTracking = trackVisibleElements<HTMLElement>({
			isTrackable: () => true,
			onVisible,
			selector: '.trackable',
		});

		const {element} = createElement();

		await wait(0);

		intersect(element);

		expect(onVisible).toHaveBeenCalledTimes(1);

		stopTracking();
	});

	it('stops reporting after the returned cleanup runs', () => {
		const {element} = createElement();

		const onVisible = jest.fn();

		const stopTracking = trackVisibleElements<HTMLElement>({
			isTrackable: () => true,
			onVisible,
			selector: '.trackable',
		});

		stopTracking();

		intersect(element);

		expect(onVisible).not.toHaveBeenCalled();
	});

	it('is a no-op when IntersectionObserver is unavailable', () => {
		delete (global as unknown as {IntersectionObserver?: unknown})
			.IntersectionObserver;

		const {element} = createElement();

		const onVisible = jest.fn();

		const stopTracking = trackVisibleElements<HTMLElement>({
			isTrackable: () => true,
			onVisible,
			selector: '.trackable',
		});

		intersect(element);

		expect(onVisible).not.toHaveBeenCalled();
		expect(typeof stopTracking).toBe('function');

		stopTracking();
	});
});
