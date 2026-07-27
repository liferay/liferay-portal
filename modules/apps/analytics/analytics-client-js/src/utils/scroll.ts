/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Analytics} from '../types';

/**
 * Returns the current scroll position of the page
 */
function getCurrentScrollPosition() {
	return window.pageYOffset || document.documentElement.scrollTop;
}

/**
 * Returns the entire height of the document
 */
function getDocumentHeight() {
	const heights = [
		document.body.clientHeight,
		document.documentElement.clientHeight,
		document.documentElement.scrollHeight,
	];

	return Math.max.apply(null, heights);
}

function getDimensions(element?: Analytics.HTMLElement) {
	const height = getDocumentHeight();
	const top = getCurrentScrollPosition();

	let positions: {
		bottom: number;
		height: number;
		top: number;
	} = {
		bottom: 0,
		height,
		top,
	};

	if (element) {
		const boundingClientRect = element.getBoundingClientRect();
		const {bottom, height, top} = boundingClientRect;

		positions = {
			bottom,
			height,
			top,
		};
	}

	return positions;
}

/**
 * Returns whether the element is actually visible on the page: rendered (not
 * `display: none`), not `visibility: hidden`, and not made transparent by an
 * `opacity: 0` ancestor. Unlike a bounding-box check, this excludes elements
 * that occupy space in the viewport but are hidden by CSS (e.g. an asset inside
 * a closed dropdown menu).
 */
function isVisible(element: Element) {
	if (typeof element.checkVisibility === 'function') {
		return element.checkVisibility({
			checkOpacity: true,
			checkVisibilityCSS: true,
			opacityProperty: true,
			visibilityProperty: true,
		});
	}

	const {height, width} = element.getBoundingClientRect();

	if (!height || !width) {
		return false;
	}

	if (getComputedStyle(element).visibility === 'hidden') {
		return false;
	}

	let node: Element | null = element;

	while (node) {
		if (parseFloat(getComputedStyle(node).opacity) === 0) {
			return false;
		}

		node = node.parentElement;
	}

	return true;
}

function isPartiallyInViewport(element: Element) {
	const {bottom, left, right, top} = element.getBoundingClientRect();

	const innerHeight =
		window.innerHeight || document.documentElement.clientHeight;
	const innerWidth =
		window.innerWidth || document.documentElement.clientWidth;

	const verticallyInViewport = top <= innerHeight && bottom >= 0;
	const horizontallyInViewport = left <= innerWidth && right >= 0;

	return verticallyInViewport && horizontallyInViewport;
}

class ScrollTracker {
	steps: number | null;
	stepsReached: number[] | null;

	constructor(steps = 4) {
		this.steps = steps;
		this.stepsReached = [];
	}

	dispose() {
		this.steps = null;
		this.stepsReached = null;
	}

	getDepthValue(element?: Analytics.HTMLElement) {
		const {bottom, height, top} = getDimensions(element);
		const visibleArea = window.innerHeight;

		let depthValue = (visibleArea - top) / height;

		if (top <= 0 && bottom >= 0) {
			depthValue = visibleArea / (height + top);
		}
		else if (!element) {
			depthValue = (top + visibleArea) / height;
		}

		return depthValue;
	}

	/**
	 * Calculates the depth of the element on the page. If the
	 * element is not passed as a parameter the calculation must be
	 * performed to get the page depth
	 */
	getDepth(element?: Analytics.HTMLElement) {
		const value = this.getDepthValue(element);

		const depth = Math.round(value * 100);

		return Math.min(Math.max(depth, 0), 100);
	}

	/**
	 * Processes the current scroll location and calculates the scroll depth level
	 * If the client reaches one of the pre-defined levels that is yet unreached
	 * it sends an analytics event
	 */
	onDepthReached(
		fn: (depth: number) => void,
		element?: Analytics.HTMLElement
	) {
		const depth = this.getDepth(element);
		const step = Math.floor(100 / (this.steps as number));

		const depthLevel = Math.floor(depth / step);

		if (this.stepsReached?.every((val) => val < depthLevel)) {
			this.stepsReached.push(depthLevel);

			if (depthLevel > 0) {
				fn(depthLevel * step);
			}
		}
	}
}

export {isPartiallyInViewport, isVisible, ScrollTracker};
