/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {State} from '@liferay/frontend-js-state-web';
import {openSelectionModal} from 'frontend-js-components-web';
import {
	addParams,
	delegate,
	fetch,
	sub,
	toggleDisabled,
	toggleSelectBox,
} from 'frontend-js-web';

import {propertiesAtom} from './atoms/propertiesAtom';

export default function ({
	classTypes,
	initialProperties,
	namespace,
	propertiesURL,
}) {
	const mapDDMStructures = {};

	const assetMultipleSelector = document.getElementById(
		`${namespace}currentClassNameIds`
	);
	const assetSelector = document.getElementById(`${namespace}anyAssetType`);
	const ddmStructureDisplayFieldValueInput = document.getElementById(
		`${namespace}ddmStructureDisplayFieldValue`
	);
	const ddmStructureFieldNameInput = document.getElementById(
		`${namespace}ddmStructureFieldName`
	);
	const ddmStructureFieldValueInput = document.getElementById(
		`${namespace}ddmStructureFieldValue`
	);
	const nonexistentClassNameIdsContainer = document.getElementById(
		`${namespace}nonexistentClassNameIds`
	);
	const orderByColumn1 = document.getElementById(
		`${namespace}orderByColumn1`
	);
	const orderByColumn2 = document.getElementById(
		`${namespace}orderByColumn2`
	);
	const orderingPanel = document.getElementById(`${namespace}ordering`);
	const saveButton = document.getElementById(`${namespace}saveButton`);
	const sourcePanel = document.querySelector('.source-container');

	const options = classTypes.reduce(
		(acc, {className}) => ({
			...acc,
			[className]: document.getElementById(
				`${namespace}${className}Options`
			),
		}),
		{}
	);

	const subtypeSelector = classTypes.reduce(
		(acc, {className}) => ({
			...acc,
			[className]: document.getElementById(
				`${namespace}anyClassType${className}`
			),
		}),
		{}
	);

	const eventDelegates = [];

	/**
	 * When more than one asset type or subtype is selected, the per-type field
	 * groups can collide, so expose only the first "Common Fields" group
	 * shared across every type.
	 */
	const collapseToCommonFields = (groups, {classNameIds, classTypeIds}) =>
		classNameIds.length > 1 || classTypeIds.length > 1
			? groups.slice(0, 1)
			: groups;

	/**
	 * Reads the currently selected asset type(s) and subtype(s) from the
	 * source panel selectors.
	 */
	const getSelectedIds = () => {
		const assetTypeValue = assetSelector?.value || '';

		let classNameIds = [];

		if (assetTypeValue === 'false') {

			// Multi-selection: collect every option out of the hidden
			// <select> the JSP populates with the user's picks.

			classNameIds = Array.from(assetMultipleSelector?.options || []).map(
				(option) => option.value
			);
		}
		else if (assetTypeValue && assetTypeValue !== 'true') {
			classNameIds = [assetTypeValue];
		}

		let classTypeIds = [];

		if (classNameIds.length === 1) {

			// Subtype selection: Subtypes only make sense when exactly one
			// asset type is selected — the subtype UI is hidden otherwise.

			const classType = classTypes.find(
				(ct) => `${ct.classNameId}` === classNameIds[0]
			);

			if (classType) {
				const subtypeValue =
					subtypeSelector[classType.className]?.value;

				if (subtypeValue === 'false') {

					// Multi-subtype selection: same pattern as
					// classNameIds above, but the element id is
					// namespaced with the class name.

					const multiSubtypeSelect = document.getElementById(
						`${namespace}${classType.className}currentClassTypeIds`
					);

					classTypeIds = Array.from(
						multiSubtypeSelect?.options || []
					).map((option) => option.value);
				}
				else if (subtypeValue && subtypeValue !== 'true') {
					classTypeIds = [subtypeValue];
				}
			}
		}

		return {classNameIds, classTypeIds};
	};

	/**
	 * Refetches the filterable properties using `propertiesURL` upon
	 * changes to the asset source (type / subtype selectors) and writes
	 * the result to `propertiesAtom`.
	 *
	 * CollectionFilterBuilder and CollectionOrdering React components
	 * subscribe to that atom via `useTypeProperties`.
	 */
	const refreshProperties = () => {
		if (!propertiesURL) {
			return;
		}

		const {classNameIds, classTypeIds} = getSelectedIds();

		fetch(
			addParams(
				{
					[`${namespace}classNameIds`]: classNameIds.join(','),
					[`${namespace}classTypeIds`]: classTypeIds.join(','),
				},
				propertiesURL
			)
		)
			.then((response) => response.json())
			.then((data) =>
				State.write(
					propertiesAtom,
					collapseToCommonFields(data || [], {
						classNameIds,
						classTypeIds,
					})
				)
			)
			.catch((error) => {
				if (process.env.NODE_ENV === 'development') {
					console.error('Failed to fetch type properties: ', error);
				}
			});
	};

	const updateNonexistentClassNameIds = () => {
		if (!nonexistentClassNameIdsContainer) {
			return;
		}

		const {classNameIds} = getSelectedIds();

		let visibleCount = 0;

		nonexistentClassNameIdsContainer
			.querySelectorAll('[data-class-name-id]')
			.forEach((item) => {
				item.hidden = !classNameIds.includes(item.dataset.classNameId);

				if (!item.hidden) {
					visibleCount++;
				}
			});

		nonexistentClassNameIdsContainer.hidden = !visibleCount;
	};

	const fireSourceChange = () => {
		Liferay.fire(`${namespace}sourceChange`);

		refreshProperties();

		updateNonexistentClassNameIds();
	};

	const createElement = (label, classNames, attributes, content) => {
		const element = document.createElement(label);

		if (classNames) {
			element.classList.add(classNames);
		}

		Object.keys(attributes).forEach((key) => {
			element.setAttribute(key, attributes[key]);
		});

		if (content) {
			element.innerHTML = content;
		}

		return element;
	};

	const createOptgroup = (attributes) =>
		createElement('optgroup', 'order-by-subtype', attributes);

	const createOption = (attributes, content) =>
		createElement('option', null, attributes, content);

	const togglePopupButtons = (enabledInput, enabledInputChecked) => {
		const popupButtons = document.querySelectorAll(
			'.asset-subtypefields-popup .btn'
		);

		if (enabledInput) {
			popupButtons.forEach((popupButton) => {
				toggleDisabled(popupButton, !enabledInputChecked);
			});
		}
	};

	const toggleSaveButton = (valueIsEmpty) => {
		if (valueIsEmpty) {
			saveButton.classList.add('disabled');
		}
		else {
			saveButton.classList.remove('disabled');
		}
		saveButton.disabled = valueIsEmpty;
	};

	const removeOptionsOrderByFilter = () => {
		const options = orderingPanel.querySelectorAll('.order-by-subtype');
		options.forEach((option) => option.remove());
	};

	const toggleSubclassesFields = (hideSubtypeFieldsWrapper, className) => {
		const selectedSubtype = subtypeSelector[className].value;
		const structureOptions = document.getElementById(
			`${namespace}${selectedSubtype}_${className}Options`
		);

		if (structureOptions) {
			structureOptions.classList.remove('hide');
		}

		const subtypeFieldsWrappers = document.querySelectorAll(
			`#${namespace}${className}subtypeFieldsWrapper, #${namespace}${className}subtypeFieldsFilterEnableWrapper`
		);

		subtypeFieldsWrappers.forEach((subtypeFieldsWrapper) => {
			if (selectedSubtype !== 'false' && selectedSubtype !== 'true') {
				if (orderingPanel) {
					removeOptionsOrderByFilter();

					const optOrderByColumn1 =
						mapDDMStructures[
							`${className}_${selectedSubtype}_column1`
						];
					const optOrderByColumn2 =
						mapDDMStructures[
							`${className}_${selectedSubtype}_column2`
						];

					if (optOrderByColumn1) {
						orderByColumn1.append(optOrderByColumn1);
					}
					if (optOrderByColumn2) {
						orderByColumn2.append(optOrderByColumn2);
					}
				}

				if (structureOptions) {
					subtypeFieldsWrapper.classList.remove('hide');
				}
				else if (hideSubtypeFieldsWrapper) {
					subtypeFieldsWrapper.classList.add('hide');
				}
			}
			else if (hideSubtypeFieldsWrapper) {
				subtypeFieldsWrapper.classList.add('hide');
			}
		});
	};

	const toggle = (assetSelectorValue, {className, classNameId}) => {
		const assetOptions = assetMultipleSelector.options;
		const showOptions =
			assetSelector.value === `${classNameId}` ||
			(assetSelector.value === 'false' &&
				assetOptions.length === 1 &&
				assetOptions[0].value === `${classNameId}`);

		if (showOptions) {
			options[className].classList.remove('hide');
		}
		else {
			options[className].classList.add('hide');
		}

		let selectedSubtype = null;

		const classTypeSelected = classTypes.find(
			(classType) => classType.classNameId === assetSelector.value
		);

		if (classTypeSelected) {
			selectedSubtype = subtypeSelector[classTypeSelected.className];
		}
		toggleSaveButton(
			assetSelector.value === '' || selectedSubtype?.value === ''
		);

		if (assetSelectorValue && classNameId === assetSelectorValue) {
			toggleSubclassesFields(true, className);
		}
	};

	const toggleSubclasses = (assetSelectorValue) => {
		classTypes.forEach((classType) => {
			toggle(assetSelectorValue, classType);
		});
	};

	const setDDMFields = ({
		className,
		displayValue = '',
		message = '',
		name = '',
		value = '',
	}) => {
		ddmStructureFieldNameInput.value = name;
		ddmStructureFieldValueInput.value = value;
		ddmStructureDisplayFieldValueInput.value = displayValue;

		const ddmStructureFieldMessageContainer = document.getElementById(
			`${namespace}${className}ddmStructureFieldMessage`
		);

		if (ddmStructureFieldMessageContainer) {
			ddmStructureFieldMessageContainer.innerHTML =
				Liferay.Util.escape(message);
		}
	};

	classTypes.forEach(({className, classSubtypes}) => {
		toggleSelectBox(
			`${namespace}anyClassType${className}`,
			'false',
			`${namespace}${className}Boxes`
		);

		classSubtypes.forEach(({classTypeFields, classTypeId, name}) => {
			const columnBuffer1 = createOptgroup({label: name});
			const columnBuffer2 = createOptgroup({label: name});

			classTypeFields.forEach(
				({
					label,
					selectedOrderByColumn1,
					selectedOrderByColumn2,
					value,
				}) => {
					const option1 = createOption(
						{
							value,
							...(selectedOrderByColumn1 && {
								selected: '',
							}),
						},
						label
					);

					const option2 = createOption(
						{
							value,
							...(selectedOrderByColumn2 && {
								selected: '',
							}),
						},
						label
					);

					columnBuffer1.append(option1);
					columnBuffer2.append(option2);
				}
			);

			mapDDMStructures[`${className}_${classTypeId}_column1`] =
				columnBuffer1;

			mapDDMStructures[`${className}_${classTypeId}_column2`] =
				columnBuffer2;
		});

		const onChangeSubtypeSelector = () => {
			const subtypeSelectorIsEmpty =
				subtypeSelector[className].value === '';

			toggleSaveButton(subtypeSelectorIsEmpty);

			setDDMFields({className});

			const enableCheckbox = document.getElementById(
				`${namespace}subtypeFieldsFilterEnabled${className}`
			);

			if (enableCheckbox) {
				enableCheckbox.checked = false;

				togglePopupButtons(enableCheckbox, false);
			}

			sourcePanel
				.querySelectorAll('.asset-subtypefields')
				.forEach((subtypeField) => {
					subtypeField.classList.add('hide');
				});

			toggleSubclassesFields(true, className);

			fireSourceChange();
		};

		const changeSubtypeSelector = delegate(
			sourcePanel,
			'change',
			`select#${namespace}anyClassType${className}`,
			onChangeSubtypeSelector
		);

		eventDelegates.push(changeSubtypeSelector);
	});

	const onChangeAssetSelector = () => {
		ddmStructureFieldNameInput.value = '';
		ddmStructureFieldValueInput.value = '';

		if (orderingPanel) {
			removeOptionsOrderByFilter();
		}

		toggleSubclasses(assetSelector.value);

		fireSourceChange();
	};

	const changeAssetSelector = delegate(
		sourcePanel,
		'change',
		`select#${namespace}anyAssetType`,
		onChangeAssetSelector
	);

	eventDelegates.push(changeAssetSelector);

	const enablePopupButtons = ({delegateTarget}) => {
		const enabledInput = delegateTarget.querySelector('input');

		togglePopupButtons(enabledInput, enabledInput.checked);
	};

	const clickEnablePopupButtons = delegate(
		sourcePanel,
		'click',
		'.asset-subtypefields-wrapper-enable label',
		enablePopupButtons
	);

	eventDelegates.push(clickEnablePopupButtons);

	Liferay.after('inputmoveboxes:moveItem', ({fromBox, toBox}) => {
		const classNameIdsId = `${namespace}currentClassNameIds`;
		const fromBoxId = fromBox.getAttribute('id');
		const toBoxId = toBox.getAttribute('id');

		const isClassNameMove =
			fromBoxId === classNameIdsId || toBoxId === classNameIdsId;

		const isClassTypeMove = classTypes.some(({className}) => {
			const classTypeIdsId = `${namespace}${className}currentClassTypeIds`;

			return fromBoxId === classTypeIdsId || toBoxId === classTypeIdsId;
		});

		if (!isClassNameMove && !isClassTypeMove) {
			return;
		}

		// The select boxes are rendered by ClayDualListBox; setTimeout defers
		// our reads until after React commits the new <option> children.

		setTimeout(() => {
			if (isClassNameMove) {
				toggleSubclasses();

				toggleSaveButton(
					!document.getElementById(classNameIdsId).options.length
				);
			}

			fireSourceChange();
		}, 0);
	});

	const openModal = ({delegateTarget}) => {
		let url = delegateTarget.dataset.href;

		url = addParams(
			`${namespace}ddmStructureDisplayFieldValue=${encodeURIComponent(
				ddmStructureDisplayFieldValueInput.value
			)}`,
			url
		);
		url = addParams(
			`${namespace}ddmStructureFieldName=${encodeURIComponent(
				ddmStructureFieldNameInput.value
			)}`,
			url
		);
		url = addParams(
			`${namespace}ddmStructureFieldValue=${encodeURIComponent(
				ddmStructureFieldValueInput.value
			)}`,
			url
		);

		openSelectionModal({
			customSelectEvent: true,
			id: `${namespace}selectDDMStructure${delegateTarget.id}`,
			iframeBodyCssClass: '',
			onSelect: (selectedItem) => {
				setDDMFields({
					className: selectedItem.className,
					displayValue: selectedItem.displayValue,
					message: `${selectedItem.label}: ${selectedItem.displayValue}`,
					name: selectedItem.name,
					value: selectedItem.value,
				});
			},
			selectEventName: `${namespace}selectDDMStructureField`,
			title: sub(
				Liferay.Language.get('select-x'),
				Liferay.Language.get('structure-field')
			),
			url,
		});
	};

	const clickOpenModal = delegate(
		sourcePanel,
		'click',
		'.asset-subtypefields-popup .btn',
		openModal
	);

	eventDelegates.push(clickOpenModal);

	toggleSubclasses(assetSelector.value);
	toggleSelectBox(
		`${namespace}anyAssetType`,
		'false',
		`${namespace}classNamesBoxes`
	);

	State.write(
		propertiesAtom,
		collapseToCommonFields(initialProperties || [], getSelectedIds())
	);

	return {
		dispose() {
			eventDelegates.forEach((eventDelegate) => eventDelegate.dispose());
		},
	};
}
