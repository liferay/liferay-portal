/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

export function getLinkableEditableEditorWrapper(element: HTMLElement) {
	const anchor =
		element instanceof HTMLAnchorElement
			? element
			: element.querySelector('a');

	if (anchor) {
		const innerWrapper = document.createElement('div');
		innerWrapper.innerHTML = anchor.innerHTML;
		anchor.innerHTML = '';
		anchor.appendChild(innerWrapper);

		return innerWrapper;
	}
	else {
		const wrapper = document.createElement('div');
		wrapper.innerHTML = element.innerHTML;
		element.innerHTML = '';
		element.appendChild(wrapper);

		return wrapper;
	}
}
