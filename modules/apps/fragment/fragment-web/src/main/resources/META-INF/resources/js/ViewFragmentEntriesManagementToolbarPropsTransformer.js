/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {AIAssistant} from '@liferay/ai-hub-cell-js-components-web';
import {render} from '@liferay/frontend-js-react-web';
import {openSelectionModal} from 'frontend-js-components-web';
import {getCheckedCheckboxes} from 'frontend-js-web';

import AddFragmentModal from './AddFragmentModal';
import openDeleteFragmentModal from './openDeleteFragmentModal';

export default function propsTransformer({
	additionalProps: {
		addFragmentEntryURL,
		copyFragmentEntryURL,
		deleteFragmentCompositionsAndFragmentEntriesURL,
		exportFragmentCompositionsAndFragmentEntriesURL,
		fieldTypes,
		fragmentCollectionId,
		fragmentTypes,
		moveFragmentCompositionsAndFragmentEntriesURL,
		selectFragmentCollectionURL,
	},
	portletNamespace,
	...otherProps
}) {
	const copySelectedFragmentEntries = () => {
		const form = document.getElementById(`${portletNamespace}fm`);

		if (!form) {
			return;
		}

		const fragmentEntryIds = getCheckedCheckboxes(
			form,
			`${portletNamespace}allRowIds`
		);

		const fragmentCollectionIdElement = document.getElementById(
			`${portletNamespace}fragmentCollectionId`
		);

		if (fragmentCollectionIdElement) {
			fragmentCollectionIdElement.setAttribute(
				'value',
				fragmentCollectionId
			);
		}

		const fragmentEntryIdsElement = document.getElementById(
			`${portletNamespace}fragmentEntryIds`
		);

		if (fragmentEntryIdsElement) {
			fragmentEntryIdsElement.setAttribute('value', fragmentEntryIds);
		}

		const fragmentEntryForm = document.getElementById(
			`${portletNamespace}fragmentEntryFm`
		);

		if (fragmentEntryForm) {
			submitForm(fragmentEntryForm, copyFragmentEntryURL);
		}
	};

	const deleteFragmentCompositionsAndFragmentEntries = () => {
		openDeleteFragmentModal({
			multiple: true,
			onDelete: () => {
				const form = document.getElementById(`${portletNamespace}fm`);

				if (form) {
					submitForm(
						form,
						deleteFragmentCompositionsAndFragmentEntriesURL
					);
				}
			},
		});
	};

	const exportFragmentCompositionsAndFragmentEntries = () => {
		const form = document.getElementById(`${portletNamespace}fm`);

		if (form) {
			submitForm(form, exportFragmentCompositionsAndFragmentEntriesURL);
		}
	};

	const moveFragmentCompositionsAndFragmentEntries = () => {
		const form = document.getElementById(`${portletNamespace}fm`);

		if (!form) {
			return;
		}

		const fragmentCompositionIds = getCheckedCheckboxes(
			form,
			`${portletNamespace}allRowIds`,
			`${portletNamespace}rowIdsFragmentComposition`
		);

		const fragmentEntryIds = getCheckedCheckboxes(
			form,
			`${portletNamespace}allRowIds`,
			`${portletNamespace}rowIdsFragmentEntry`
		);

		openSelectionModal({
			id: `${portletNamespace}selectFragmentCollection`,
			onSelect(selectedItem) {
				if (selectedItem) {
					const itemValue = JSON.parse(selectedItem.value);

					const form = document.getElementById(
						`${portletNamespace}fragmentEntryFm`
					);

					if (!form) {
						return;
					}

					const fragmentCollectionIdElement = document.getElementById(
						`${portletNamespace}fragmentCollectionId`
					);

					if (fragmentCollectionIdElement) {
						fragmentCollectionIdElement.setAttribute(
							'value',
							itemValue.fragmentCollectionId
						);
					}

					const fragmentCompositionIdsElement =
						document.getElementById(
							`${portletNamespace}fragmentCompositionIds`
						);

					if (fragmentCompositionIdsElement) {
						fragmentCompositionIdsElement.setAttribute(
							'value',
							fragmentCompositionIds
						);
					}

					const fragmentEntryIdsElement = document.getElementById(
						`${portletNamespace}fragmentEntryIds`
					);

					if (fragmentEntryIdsElement) {
						fragmentEntryIdsElement.setAttribute(
							'value',
							fragmentEntryIds
						);
					}

					submitForm(
						form,
						moveFragmentCompositionsAndFragmentEntriesURL
					);
				}
			},
			selectEventName: `${portletNamespace}selectFragmentCollection`,
			title: Liferay.Language.get('select-fragment-set'),
			url: selectFragmentCollectionURL,
		});
	};

	const handleCreation = (item) => {
		const action = item.data?.action;

		if (action === 'addFragmentEntry') {
			render(
				AddFragmentModal,
				{
					addFragmentEntryURL,
					fieldTypes,
					fragmentTypes,
					namespace: portletNamespace,
				},
				document.createElement('div')
			);
		}
		else if (action === 'generateFragmentWithAI') {
			AIAssistant.open({
				context: {
					fragmentSetExternalReferenceCode:
						item.data.fragmentSetExternalReferenceCode,
					siteExternalReferenceCode:
						item.data.siteExternalReferenceCode,
				},
				initialMessage: Liferay.Language.get('generate-fragment'),
				instructionDefinitionScope: '',
				presentation: 'sidebar',
				pushContainer: '#main-content',
				triggerId: 'generateFragmentWithAI',
			});
		}
	};

	const {creationMenu} = otherProps;

	return {
		...otherProps,
		creationMenu: creationMenu && {...creationMenu, closeOnClick: true},
		onActionButtonClick(event, {item}) {
			const action = item.data?.action;

			if (action === 'copySelectedFragmentEntries') {
				copySelectedFragmentEntries();
			}
			else if (
				action === 'deleteFragmentCompositionsAndFragmentEntries'
			) {
				deleteFragmentCompositionsAndFragmentEntries();
			}
			else if (
				action === 'exportFragmentCompositionsAndFragmentEntries'
			) {
				exportFragmentCompositionsAndFragmentEntries();
			}
			else if (
				action === 'moveFragmentCompositionsAndFragmentEntries'
			) {
				moveFragmentCompositionsAndFragmentEntries();
			}
		},
		onCreateButtonClick(event, {item}) {
			handleCreation(item);
		},
		onCreationMenuItemClick(event, {item}) {
			handleCreation(item);
		},
	};
}
