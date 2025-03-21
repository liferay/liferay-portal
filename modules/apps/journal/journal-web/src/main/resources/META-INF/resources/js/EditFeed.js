/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {getFormElement, setFormValues, toggleDisabled} from 'frontend-js-web';

export default function ({isNewJournalFeed, namespace, renderedWebContent}) {
	const form = document.getElementById(`${namespace}fm`);

	const contentFieldSelector = getFormElement(form, 'contentFieldSelector');

	const handleFieldSelector = () => {
		let contentFieldValue = '';
		let ddmRendererTemplateKeyValue = '';

		const selectedFeedItemOption =
			contentFieldSelector.options[contentFieldSelector.selectedIndex];

		if (selectedFeedItemOption) {
			contentFieldValue = selectedFeedItemOption.value || '';

			if (
				selectedFeedItemOption.dataset.contentfield ===
				renderedWebContent
			) {
				ddmRendererTemplateKeyValue = contentFieldValue;

				contentFieldValue = renderedWebContent;
			}
		}

		setFormValues(form, {
			contentField: contentFieldValue,
			ddmRendererTemplateKey: ddmRendererTemplateKeyValue,
		});
	};

	const saveFeed = () => {
		const actionInput = document.getElementById(
			`${namespace}jakarta-portlet-action`
		);

		let feed = '/journal/update_feed';

		if (isNewJournalFeed) {
			feed = '/journal/add_feed';

			document.getElementById(`${namespace}feedId`).value =
				document.getElementById(`${namespace}newFeedId`).value;
		}
		actionInput.value = feed;
		submitForm(form);
	};

	if (form) {
		contentFieldSelector.addEventListener('change', handleFieldSelector);

		form.addEventListener('submit', saveFeed);

		const autoFeedInput = document.getElementById(`${namespace}autoFeedId`);
		const newFeedCheckbox = document.getElementById(
			`${namespace}newFeedId`
		);

		if (autoFeedInput && newFeedCheckbox) {
			newFeedCheckbox.disabled = autoFeedInput.checked;

			autoFeedInput.addEventListener('click', () => {
				toggleDisabled(newFeedCheckbox, !newFeedCheckbox.disabled);
			});
		}
	}

	return {
		dispose() {
			contentFieldSelector.removeEventListener(
				'change',
				handleFieldSelector()
			);

			form.removeEventListener('submit', saveFeed);
		},
	};
}
