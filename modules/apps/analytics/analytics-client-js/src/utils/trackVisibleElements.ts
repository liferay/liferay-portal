/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {onReady} from './events';
import {isVisible} from './scroll';

const VIEWED_ATTRIBUTE = 'data-analytics-asset-viewed';

// Document events after which an in-viewport, CSS-hidden element may have become
// visible: an interaction (`click`) that opened a menu/tab/accordion, or the
// reveal transition/animation completing. These cover reveals that change
// neither the element's geometry nor its own attributes (e.g. an ancestor
// toggling `visibility`/`opacity` through a `:has(:checked)` or `:hover`
// selector), which the IntersectionObserver and the per-element MutationObserver
// cannot detect on their own.

const REVEAL_EVENTS: (keyof DocumentEventMap)[] = [
	'animationend',
	'click',
	'transitionend',
];

/**
 * Emits an event for each matching element the first time it is actually
 * visible on the page: inside the viewport (via IntersectionObserver) and not
 * hidden by CSS (via isVisible/checkVisibility). Elements that occupy space in
 * the viewport but are hidden by CSS (e.g. an asset inside a closed dropdown,
 * or one with `opacity: 0`) are skipped until they become visible.
 *
 * Each element is reported at most once: on report it is flagged in the DOM
 * with the `data-analytics-asset-viewed` attribute, which is read back to skip
 * the element on every later scan. A flag already present when tracking starts
 * (set server-side, or carried over from a previous session) therefore prevents
 * the event too, and once an element is flagged no instance reports it again.
 */
function trackVisibleElements<T extends Element>({
	isTrackable,
	onVisible,
	selector,
}: {
	isTrackable: (element: T) => boolean;
	onVisible: (element: T) => void;
	selector: string;
}) {

	// Bail out where the observers are unavailable (SSR, very old browsers).

	if (
		typeof IntersectionObserver !== 'function' ||
		typeof MutationObserver !== 'function'
	) {
		return () => {};
	}

	// The keys of `attributeObservers` double as the pending set: every element
	// that is in the viewport yet hidden by CSS has a reveal MutationObserver, so
	// iterating the keys yields exactly the elements to re-check on a reveal
	// event, and the map emptying is the signal to detach the document listeners.

	const attributeObservers = new Map<T, MutationObserver>();
	const intersecting = new Set<T>();

	const isReported = (element: T) =>
		element.getAttribute(VIEWED_ATTRIBUTE) === 'true';

	// The IntersectionObserver reports geometric visibility (scrolling into
	// view, or `display: none` becoming rendered). isVisible then confirms the
	// element is not hidden by CSS before it is reported.

	const intersectionObserver = new IntersectionObserver((entries) => {
		entries.forEach((entry) => {
			const element = entry.target as T;

			if (entry.isIntersecting) {
				intersecting.add(element);

				report(element);
			}
			else {
				intersecting.delete(element);
			}
		});
	});

	// A reveal through `opacity` or `visibility` changes CSS but not geometry,
	// so the IntersectionObserver never fires again. Such elements (in the
	// viewport yet hidden by CSS) are watched for reveal two complementary ways,
	// both set up lazily and only while at least one element is pending: a
	// per-element MutationObserver catches a reveal that toggles the element's
	// own class/style, and the document-level reveal events below catch a reveal
	// driven by an ancestor (e.g. a menu opening through a `:has(:checked)` or
	// `:hover` selector), which changes neither the element's geometry nor its
	// own attributes. A `display: none` ancestor needs neither: revealing it
	// changes geometry, so the IntersectionObserver fires on its own.

	// Re-checks pending (in-viewport, CSS-hidden) elements after a reveal event.
	// A transition/animation originates on the element that transitioned, so
	// only its descendants can have been revealed; a click can open a container
	// elsewhere in the tree, so every pending element is re-checked (the set is
	// small: only elements in the viewport yet hidden by CSS).

	const onDocumentReveal = (event: Event) => {
		const {target} = event;

		const scoped = event.type !== 'click' && target instanceof Node;

		attributeObservers.forEach((_observer, element) => {
			if (!scoped || (target as Node).contains(element)) {
				report(element);
			}
		});
	};

	// The reveal listeners exist only while at least one element is pending (the
	// callers attach on the first pending element and detach once the map
	// empties). Re-adding or removing the same capture listener is a no-op, so
	// no attached/detached bookkeeping is needed.

	const attachDocumentReveal = () =>
		REVEAL_EVENTS.forEach((type) =>
			document.addEventListener(type, onDocumentReveal, true)
		);

	const detachDocumentReveal = () =>
		REVEAL_EVENTS.forEach((type) =>
			document.removeEventListener(type, onDocumentReveal, true)
		);

	const stopObserving = (element: T) => {
		intersectionObserver.unobserve(element);
		intersecting.delete(element);

		const attributeObserver = attributeObservers.get(element);

		if (attributeObserver) {
			attributeObserver.disconnect();
			attributeObservers.delete(element);
		}

		if (attributeObservers.size === 0) {
			detachDocumentReveal();
		}
	};

	const watchForReveal = (element: T) => {
		if (attributeObservers.has(element)) {
			return;
		}

		const attributeObserver = new MutationObserver(() => report(element));

		attributeObserver.observe(element, {
			attributeFilter: ['class', 'style'],
			attributes: true,
		});

		attributeObservers.set(element, attributeObserver);

		attachDocumentReveal();
	};

	function report(element: T) {
		if (
			!intersecting.has(element) ||
			isReported(element) ||
			!isTrackable(element)
		) {
			return;
		}

		if (!isVisible(element)) {
			watchForReveal(element);

			return;
		}

		// The `data-analytics-asset-viewed` flag deduplicates: an element
		// carrying it is skipped above, so setting it here reports the element
		// exactly once — across every instance, and honoring a flag that was
		// already present before tracking started.

		element.setAttribute(VIEWED_ATTRIBUTE, 'true');

		stopObserving(element);

		onVisible(element);
	}

	const observe = (element: T) => {
		if (!isReported(element)) {
			intersectionObserver.observe(element);
		}
	};

	const forEachMatching = (
		root: ParentNode,
		callback: (element: T) => void
	) => {
		root.querySelectorAll(selector).forEach((element) =>
			callback(element as T)
		);
	};

	// Elements added to or removed from the DOM after the initial pass (e.g.
	// asynchronously rendered content) are observed or released as they come
	// and go, so the observers track the live DOM instead of accumulating
	// references to detached nodes.

	const documentObserver = new MutationObserver((mutations) => {
		mutations.forEach((mutation) => {
			mutation.addedNodes.forEach((node) => {
				if (node instanceof Element) {
					if (node.matches(selector)) {
						observe(node as T);
					}

					forEachMatching(node, observe);
				}
			});

			mutation.removedNodes.forEach((node) => {
				if (node instanceof Element) {
					if (node.matches(selector)) {
						stopObserving(node as T);
					}

					forEachMatching(node, stopObserving);
				}
			});
		});
	});

	const stopTrackingOnReady = onReady(() => {
		forEachMatching(document, observe);

		documentObserver.observe(document.documentElement, {
			childList: true,
			subtree: true,
		});
	});

	return () => {
		stopTrackingOnReady();

		documentObserver.disconnect();
		intersectionObserver.disconnect();

		attributeObservers.forEach((attributeObserver) =>
			attributeObserver.disconnect()
		);
		attributeObservers.clear();
		intersecting.clear();

		detachDocumentReveal();
	};
}

export {trackVisibleElements};
