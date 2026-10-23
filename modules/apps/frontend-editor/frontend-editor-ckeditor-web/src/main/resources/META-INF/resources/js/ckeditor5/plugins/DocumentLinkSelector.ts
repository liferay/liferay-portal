/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Plugin} from '@ckeditor/ckeditor5-core/dist/index.js';
import {Link, LinkUI} from '@ckeditor/ckeditor5-link/dist/index.js';
import {ButtonView} from '@ckeditor/ckeditor5-ui/dist/index.js';
import {openCMSFileSelectorModal} from '@liferay/frontend-js-item-selector-web';

class DocumentLinkSelector extends Plugin {
	static get requires() {
		return [Link];
	}

	afterInit() {
		const editor = this.editor;
		const linkUI = editor.plugins.get(LinkUI) as any;

		if (linkUI._documentLinkSelectorPatched) {
			return;
		}

		linkUI._documentLinkSelectorPatched = true;

		const getGroupId = () =>
			Number(editor.config.get('groupId')) ||
			Liferay.ThemeDisplay.getScopeGroupId();

		const originalCreateFormView = linkUI._createFormView.bind(linkUI);

		linkUI._createFormView = (...args: unknown[]) => {
			const formView = originalCreateFormView(...args);

			const selectDocumentButton = new ButtonView(editor.locale);

			selectDocumentButton.set({
				class: 'ck-button-action ck-button-bold',
				label: Liferay.Language.get('select-document'),
				withText: true,
			});

			selectDocumentButton.on('execute', () => {
				openCMSFileSelectorModal({
					groupId: getGroupId(),
					itemTypeLabel: Liferay.Language.get('document'),
					onSelect: (items) => {
						const href = items[0]?.embedded?.file?.link?.href;

						if (!href) {
							return;
						}

						formView.urlInputView.fieldView.value = href;

						if (formView.urlInputView.fieldView.element) {
							formView.urlInputView.fieldView.element.value =
								href;
						}

						editor.execute('link', href);

						linkUI._closeFormView();
					},
				});
			});

			for (let i = 0; i < formView.children.length; i++) {
				const rowView = formView.children.get(i) as any;

				if (!rowView?.children) {
					continue;
				}

				for (let j = 0; j < rowView.children.length; j++) {
					if (rowView.children.get(j) === formView.saveButtonView) {
						rowView.children.add(selectDocumentButton, j + 1);

						return formView;
					}
				}
			}

			return formView;
		};
	}
}

export default DocumentLinkSelector;
