/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

export default function ({currentLanguageId, namespace}) {
	let localeChangedHandler = null;
	let defaultLocaleChangedHandler = null;

	Liferay.componentReady(`${namespace}dataEngineLayoutRenderer`).then(
		(dataEngineLayoutRenderer) => {
			const dataEngineReactComponentRef =
				dataEngineLayoutRenderer?.reactComponentRef;

			localeChangedHandler = Liferay.after(
				'inputLocalized:localeChanged',
				(event) => {
					const selectedLanguageId =
						event.item.getAttribute('data-value') ||
						event.item.getAttribute('data-languageid');

					switchLanguage({
						dataEngineReactComponentRef,
						languageId: selectedLanguageId,
						namespace,
					});
				}
			);

			defaultLocaleChangedHandler = Liferay.after(
				'inputLocalized:defaultLocaleChanged',
				(event) => {
					const selectedLanguageId =
						event.item.getAttribute('data-value');

					const defaultLanguageIdInput = document.getElementById(
						`${namespace}defaultLanguageId`
					);

					defaultLanguageIdInput.value = selectedLanguageId;

					Liferay.fire('journal:defaultLocaleChanged', {
						item: event.item,
					});
				}
			);

			switchLanguage({
				dataEngineReactComponentRef,
				languageId: currentLanguageId,
				namespace,
				preserveValue: true,
			});
		}
	);

	return {
		dispose() {
			if (localeChangedHandler) {
				localeChangedHandler.detach();
			}

			if (defaultLocaleChangedHandler) {
				defaultLocaleChangedHandler.detach();
			}
		},
	};
}

function switchLanguage({
	dataEngineReactComponentRef,
	languageId,
	namespace,
	preserveValue = false,
}) {
	if (dataEngineReactComponentRef?.current) {
		const defaultLanguageIdInput = document.getElementById(
			`${namespace}defaultLanguageId`
		);

		dataEngineReactComponentRef.current.updateEditingLanguageId({
			defaultLanguageId: defaultLanguageIdInput.value,
			editingLanguageId: languageId,
			preserveValue,
		});
	}
}
