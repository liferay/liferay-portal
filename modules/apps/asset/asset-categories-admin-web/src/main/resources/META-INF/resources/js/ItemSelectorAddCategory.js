/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {delegate, getOpener, navigate} from 'frontend-js-web';

const createButton = ({action, buttonClasses, label, type = 'submit'}) => {
	const wrapper = document.createElement('div');

	wrapper.classList.add('btn-group-item');

	const button = document.createElement('button');

	button.classList.add('add-category-toolbar-button', 'btn');

	for (const buttonClass of buttonClasses) {
		button.classList.add(buttonClass);
	}

	button.dataset.action = action;
	button.textContent = label;
	button.type = type;

	wrapper.appendChild(button);

	return wrapper;
};

export default function ({currentURL, namespace, redirect}) {
	const formSheet = document.querySelector('.lfr-form-content div');

	formSheet.classList.add('border-0');

	const openerWindow = getOpener();

	const modalTitle = openerWindow.document.querySelector('.modal-title');

	const initialModalTitle = modalTitle.textContent;

	modalTitle.textContent = `${
		modalTitle.textContent
	} - ${Liferay.Language.get('add-new')}`;

	const initialModalFooterButtons = openerWindow.document.querySelectorAll(
		'.liferay-modal .modal-footer button'
	);

	initialModalFooterButtons.forEach((item) => {
		item.classList.add('hide');
	});

	const footer = openerWindow.document.querySelector(
		'.modal-footer .btn-group-spaced'
	);

	const addCategoryButtons = footer.querySelectorAll(
		'.add-category-toolbar-button'
	);

	if (addCategoryButtons.length) {
		addCategoryButtons.forEach((button) => {
			button.parentElement.classList.remove('hide');
			button.classList.remove('hide');
		});
	}
	else {
		const buttons = [
			createButton({
				action: 'cancel',
				buttonClasses: [
					'btn-outline-borderless',
					'btn-outline-secondary',
				],
				label: Liferay.Language.get('cancel'),
				type: 'button',
			}),

			createButton({
				action: 'saveAndAddNew',
				buttonClasses: ['btn-secondary'],
				label: Liferay.Language.get('save-and-add-a-new-one'),
			}),

			createButton({
				action: 'save',
				buttonClasses: ['btn-primary'],
				label: Liferay.Language.get('save'),
			}),
		];

		buttons.forEach((button) => footer.appendChild(button));
	}

	const hideAddCategoryButtons = () => {
		footer
			.querySelectorAll('.add-category-toolbar-button')
			.forEach((button) => button.parentElement.classList.add('hide'));
	};

	const delegateHandler = delegate(
		footer,
		'click',
		'.add-category-toolbar-button',
		(event) => {
			const delegateTarget = event.delegateTarget;

			const action = delegateTarget.dataset.action;

			if (action === 'cancel') {
				navigate(redirect);
			}
			else if (action === 'saveAndAddNew') {
				document.getElementById(`${namespace}redirect`).value =
					currentURL;

				submitForm(document.getElementById(`${namespace}fm`));
			}
			else if (action === 'save') {
				submitForm(document.getElementById(`${namespace}fm`));
			}
		}
	);

	function disposeHandler() {
		initialModalFooterButtons.forEach((item) => {
			item.classList.remove('hide');
		});

		hideAddCategoryButtons();

		modalTitle.textContent = initialModalTitle;

		delegateHandler.dispose();
	}

	window.onbeforeunload = disposeHandler;

	return {
		dispose: disposeHandler,
	};
}
