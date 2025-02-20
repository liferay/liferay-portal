/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {render} from '@liferay/frontend-js-react-web';
import {openToast} from 'frontend-js-components-web';
import {fetch} from 'frontend-js-web';

import TemplateSelect from './TemplateSelect';
import {
	HEADLESS_BATCH_PLANNER_URL,
	SCHEMA_SELECTED_EVENT,
	TEMPLATE_SELECTED_EVENT,
	TEMPLATE_SOILED_EVENT,
} from './constants';

const HEADERS = new Headers({
	'content-type': 'application/json',
	'x-csrf-token': window.Liferay.authToken,
});

function handleOverrideExistingRecordsCheckbox(namespace) {
	const createStrategySelect = document.querySelector(
		`#${namespace}createStrategy`
	);

	if (createStrategySelect) {
		const updateStrategySelect = document.querySelector(
			`#${namespace}updateStrategy`
		);

		createStrategySelect.addEventListener('change', ({target}) => {
			updateStrategySelect.disabled = target.value === 'INSERT';
		});
	}
}

function trimPackage(name) {
	if (!name || name.lastIndexOf('.') < 0) {
		return name;
	}

	return name.substr(name.lastIndexOf('.') + 1);
}

export default function ({
	initialExternalType,
	initialTemplateClassName,
	initialTemplateMapping,
	isExport,
	namespace,
	templatesOptions,
}) {
	const internalClassNameKeySelect = document.querySelector(
		`#${namespace}internalClassNameKey`
	);
	const externalTypeInput = document.querySelector(
		`#${namespace}externalType`
	);

	if (!isExport) {
		handleOverrideExistingRecordsCheckbox(namespace);
	}

	async function handleTemplateSelectedEvent({template}) {
		if (template) {
			if (template.externalType) {
				externalTypeInput.value = template.externalType;
			}

			const selectedClassNameValue = template.internalClassNameKey;

			const internalClassTemplateOption =
				internalClassNameKeySelect.querySelector(
					`option[value='${selectedClassNameValue}']`
				);
			internalClassTemplateOption.selected = true;

			await handleClassNameSelectChange();
		}
	}

	async function handleClassNameSelectChange(event) {
		if (event) {
			Liferay.fire(TEMPLATE_SOILED_EVENT);
		}

		const selectedOption =
			internalClassNameKeySelect.options[
				internalClassNameKeySelect.selectedIndex
			];

		const internalClassNameKeyValue = trimPackage(selectedOption.value);

		if (!internalClassNameKeyValue) {
			Liferay.fire(SCHEMA_SELECTED_EVENT, {
				schema: null,
			});

			return;
		}

		try {
			const response = await fetch(
				`${HEADLESS_BATCH_PLANNER_URL}/plans/${selectedOption.value.replace(
					'#',
					encodeURIComponent('#')
				)}/fields?export=${isExport}`,
				{
					credentials: 'include',
					headers: HEADERS,
					method: 'GET',
				}
			);

			const data = await response.json();
			Liferay.fire(SCHEMA_SELECTED_EVENT, {
				isExport,
				schema: data.items,
				schemaName: selectedOption.value,
			});
		}
		catch (error) {
			openToast({
				message: Liferay.Language.get('your-request-has-failed'),
				type: 'danger',
			});

			console.error(`Failed to fetch ${error}`);
		}
	}

	Liferay.on(TEMPLATE_SELECTED_EVENT, handleTemplateSelectedEvent);

	internalClassNameKeySelect.addEventListener(
		'change',
		handleClassNameSelectChange
	);

	let initialTemplate;

	if (initialTemplateClassName && initialTemplateMapping) {
		initialTemplate = {
			externalType: initialExternalType,
			internalClassNameKey: initialTemplateClassName,
			mapping: initialTemplateMapping,
		};
	}

	render(
		TemplateSelect,
		{
			initialTemplate,
			initialTemplateOptions: templatesOptions,
			portletNamespace: namespace,
		},
		document.getElementById(`${namespace}templateSelect`)
	);

	return {
		dispose: () => {
			Liferay.detach(
				TEMPLATE_SELECTED_EVENT,
				handleTemplateSelectedEvent
			);
		},
	};
}
