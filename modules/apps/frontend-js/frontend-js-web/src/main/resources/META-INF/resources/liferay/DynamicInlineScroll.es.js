/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import PortletBase from './PortletBase.es';
import delegate from './delegate/delegate.es';

/**
 * Appends list item elements to dropdown menus with inline-scrollers on scroll
 * events to improve page loading performance.
 *
 * @extends {Component}
 */
class DynamicInlineScroll extends PortletBase {

	/**
	 * @inheritDoc
	 */
	attached() {
		let {rootNode} = this;

		rootNode = rootNode || document.body;

		this.inlineScrollEventHandler_ = delegate(
			rootNode,
			'scroll',
			'ul.pagination ul.inline-scroller',
			this.onScroll_.bind(this)
		);
	}

	/**
	 * @inheritDoc
	 */
	created(props) {
		this.applyNamespaceToCurParam = props.applyNamespaceToCurParam;
		this.cur = Number(props.cur);
		this.curParam = props.curParam;
		this.forcePost = props.forcePost;
		this.formName = props.formName;
		this.initialPages = Number(props.initialPages);
		this.jsCall = props.jsCall;
		this.namespace = props.namespace;
		this.pages = Number(props.pages);
		this.randomNamespace = props.randomNamespace;
		this.url = props.url;
		this.urlAnchor = props.urlAnchor;

		this.handleListItemClick_ = this.handleListItemClick_.bind(this);
	}

	/**
	 * @inheritDoc
	 */
	detached() {
		super.detached();

		this.inlineScrollEventHandler_.dispose();

		const listItem = document.createElement('li');

		listItem.removeEventListener('click', this.handleListItemClick_);
	}

	/**
	 * Dynamically adds list item elements to the dropdown menu.
	 *
	 * @param {element} listElement The list element's DOM node.
	 * @param {number} pageIndex The Index of the page with an inline-scroller.
	 * @protected
	 */
	addListItem_(listElement, pageIndex) {
		const listItem = document.createElement('li');

		listItem.innerHTML = `<a aria-label="${Liferay.Util.sub(
			Liferay.Language.get('page-x'),
			[pageIndex]
		)}" class="dropdown-item" href="${this.getHREF_(
			pageIndex
		)}">${pageIndex}</a>`;

		pageIndex++;

		listElement.appendChild(listItem);
		listElement.setAttribute('data-page-index', pageIndex);

		listItem.addEventListener('click', this.handleListItemClick_);
	}

	/**
	 * Returns the <code>href</code> attribute value for each page.
	 *
	 * @param {number} pageIndex The Index of the page.
	 * @protected
	 * @return {string} The <code>href</code> value as a string.
	 */
	getHREF_(pageIndex) {
		const {
			applyNamespaceToCurParam,
			curParam,
			formName,
			jsCall,
			namespace,
			url,
			urlAnchor,
		} = this;

		const paramName = applyNamespaceToCurParam
			? `${namespace}${curParam}`
			: curParam;

		if (url !== null) {
			return `${url}&${paramName}=${pageIndex}${urlAnchor}`;
		}

		return `javascript:document.${formName}.${paramName}.value = "${pageIndex}; ${jsCall}`;
	}

	/**
	 * Returns the numerical value of the parameter passed in.
	 *
	 * @param {string|!Object} val The string or object to be converted to a number.
	 * @protected
	 * @return {number} The parameter's numberical value.
	 */
	getNumber_(val) {
		return Number(val ?? 0);
	}

	/**
	 * Handles the click event of the dynmaically added list item, preventing
	 * the default behavior and submitting the search container form.
	 *
	 * @param {Event} event The click event of the dynamically added list item.
	 * @protected
	 */
	handleListItemClick_(event) {
		if (this.forcePost) {
			event.preventDefault();

			const {curParam, id, namespace, randomNamespace} = this;

			const form = document.getElementById(
				`${randomNamespace}${namespace || id}pageIteratorFm`
			);

			form.elements[namespace + curParam].value =
				event.currentTarget.textContent;

			form.submit();
		}
	}

	/**
	 * An event triggered when a dropdown menu with an inline-scroller is
	 * scrolled. This dynamically adds list item elements to the dropdown menu
	 * as it is scrolled down.
	 *
	 * @param {Event} event The scroll event triggered by scrolling a dropdown
	 *        menu with an inline-scroller.
	 * @protected
	 */
	onScroll_(event) {
		const {cur, initialPages, pages} = this;
		const {target} = event;

		if (target.nodeName !== 'UL') {
			return;
		}

		let pageIndex = this.getNumber_(target.dataset.pageIndex);
		let pageIndexMax = this.getNumber_(target.dataset.maxIndex);

		if (pageIndex === 0) {
			const pageIndexCurrent = this.getNumber_(
				target.dataset.currentIndex
			);

			if (pageIndexCurrent === 0) {
				pageIndex = initialPages;
			}
			else {
				pageIndex = pageIndexCurrent + initialPages;
			}
		}

		if (pageIndexMax === 0) {
			pageIndexMax = pages;
		}

		if (
			cur <= pages &&
			pageIndex < pageIndexMax &&
			target.scrollTop >= target.scrollHeight - 300
		) {
			this.addListItem_(target, pageIndex);
		}
	}
}

export default DynamicInlineScroll;
