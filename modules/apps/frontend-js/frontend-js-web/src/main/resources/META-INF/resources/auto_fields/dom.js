/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

let counter = 0;

/**
 * Returns every descendant of <code>element</code> matching
 * <code>selector</code> as a real array, so that it can be safely iterated
 * while the DOM is being mutated.
 */
export function all(element, selector) {
	return Array.from(element.querySelectorAll(selector));
}

/**
 * Builds a detached element out of an HTML string.
 */
export function create(html) {
	const template = document.createElement('template');

	template.innerHTML = html.trim();

	return template.content.firstElementChild;
}

/**
 * Replaces <code>oldCSSClass</code> with <code>newCSSClass</code> on every
 * descendant of <code>element</code> carrying it.
 */
export function replaceCSSClass(element, oldCSSClass, newCSSClass) {
	for (const item of all(element, '.' + oldCSSClass)) {
		item.classList.replace(oldCSSClass, newCSSClass);
	}
}

/**
 * Generates an identifier that is unique within the page.
 */
export function guid() {
	return 'autofields_' + ++counter;
}

/**
 * AlloyUI's <code>Node.hide()</code> both adds the <code>hide</code> CSS class
 * and sets an inline <code>display: none</code>. Both are reproduced here
 * because rows are located by CSS class but their visibility is asserted
 * through the computed style.
 */
export function hide(element) {
	element.classList.add('hide');

	element.style.display = 'none';
}

export function isHidden(element) {
	return element.classList.contains('hide');
}

export function show(element) {
	element.classList.remove('hide');

	element.style.display = '';
}

/**
 * Tells whether the browser is currently painting <code>element</code>,
 * regardless of how it was hidden.
 */
export function isVisible(element) {
	const computedStyle = window.getComputedStyle(element);

	return (
		computedStyle.display !== 'none' &&
		computedStyle.visibility !== 'collapse' &&
		computedStyle.visibility !== 'hidden'
	);
}

/**
 * Accepts a CSS selector, a DOM element or an AlloyUI node, since the
 * <code>saveAutoFields</code> event still carries an AlloyUI node as its form.
 */
export function toElement(elementOrSelector) {
	if (!elementOrSelector) {
		return null;
	}

	if (typeof elementOrSelector === 'string') {
		return document.querySelector(elementOrSelector);
	}

	if (elementOrSelector._node) {
		return elementOrSelector._node;
	}

	return elementOrSelector;
}
