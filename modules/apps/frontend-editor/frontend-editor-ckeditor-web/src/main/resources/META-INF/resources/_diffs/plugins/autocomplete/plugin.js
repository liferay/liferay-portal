/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

(function () {
	CKEDITOR.plugins.add('autocomplete', {
		init(editor) {
			const instance = this;

			AUI().use(
				'aui-event-base',
				'aui-event-key',
				'aui-debounce',
				'aui-base',
				'autocomplete',
				'autocomplete-filters',
				'autocomplete-highlighters',
				() => {
					const path = instance.path;

					const dependencies = [
						CKEDITOR.getUrl(path + 'autocomplete.js'),
					];

					CKEDITOR.scriptLoader.load(dependencies, () => {
						const liferayAutoCompleteCKEditor =
							new Liferay.AutoCompleteCKEditor({
								...editor.config.autocomplete,
								editor,
								width: 300,
							});

						liferayAutoCompleteCKEditor.render();

						liferayAutoCompleteCKEditor.detach('valueChange');
					});
				}
			);
		},
	});
})();
