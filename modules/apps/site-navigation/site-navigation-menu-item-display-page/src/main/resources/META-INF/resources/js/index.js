/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayAlert from '@clayui/alert';
import {ClayButtonWithIcon} from '@clayui/button';
import ClayForm, {ClayCheckbox, ClayInput} from '@clayui/form';
import ClayIcon from '@clayui/icon';
import ClayLabel from '@clayui/label';
import classNames from 'classnames';
import {
	TranslationAdminSelector,
	openSelectionModal,
} from 'frontend-js-components-web';
import {fetch, objectToFormData} from 'frontend-js-web';
import PropTypes from 'prop-types';
import React, {useEffect, useState} from 'react';

import '../css/main.scss';

function DisplayPageItemContextualSidebar({
	chooseItemProps,
	defaultLanguageId,
	hasDisplayPage: initiallyHasDisplayPage,
	item,
	itemSubtype,
	itemType,
	locales,
	localizedNames,
	namespace,
	useCustomName,
}) {
	const [translations, setTranslations] = useState(localizedNames);
	const [customNameEnabled, setCustomNameEnabled] = useState(useCustomName);
	const [selectedLocaleId, setSelectedLocaleId] = useState(defaultLanguageId);
	const [selectedItem, setSelectedItem] = useState(item);
	const [hasDisplayPage, setItemHasDisplayPage] = useState(
		initiallyHasDisplayPage
	);

	const [type, setType] = useState(itemType);
	const [subtype, setSubtype] = useState(itemSubtype);
	const [itemData, setItemData] = useState(item.data);
	const [customName, setCustomName] = useState(
		translations[selectedLocaleId] || item.title
	);
	const [customNameInvalid, setCustomNameInvalid] = useState(false);

	const {eventName, getItemDetailsURL, itemSelectorURL, modalTitle} =
		chooseItemProps;

	useEffect(() => {
		const onFormSubmit = (event) => {
			if (!customName) {
				event.preventDefault();

				setCustomNameInvalid(true);
			}
		};

		const submitButton = document.querySelector('button[type=submit]');

		submitButton?.addEventListener('click', onFormSubmit);

		return () => {
			submitButton?.removeEventListener('click', onFormSubmit);
		};
	}, [customName]);

	const openChooseItemModal = () =>
		openSelectionModal({
			onSelect: (selectedItems) => {
				if (selectedItems) {
					let infoItem = Object.values(selectedItems)[0];

					let value;

					if (typeof selectedItems.value === 'string') {
						try {
							value = JSON.parse(selectedItems.value);
						}
						catch (error) {}
					}
					else if (
						selectedItems.value &&
						typeof selectedItems.value === 'object'
					) {
						value = selectedItems.value;
					}

					if (value) {
						delete infoItem.value;
						infoItem = {...value};
					}

					setSelectedItem(infoItem);

					const namespacedInfoItem = Liferay.Util.ns(
						namespace,
						infoItem
					);

					fetch(getItemDetailsURL, {
						body: objectToFormData(namespacedInfoItem),
						method: 'POST',
					})
						.then((response) => response.json())
						.then((jsonResponse) => {
							setType(jsonResponse.itemType);
							setSubtype(jsonResponse.itemSubtype);
							setItemData(jsonResponse.data);
							setItemHasDisplayPage(jsonResponse.hasDisplayPage);
						})
						.catch(() => {});
				}
			},
			selectEventName: eventName,
			title: modalTitle,
			url: itemSelectorURL,
		});

	return (
		<>
			<ClayForm.Group className="align-items-center d-flex mb-2">
				<ClayCheckbox
					checked={customNameEnabled}
					label={Liferay.Language.get('use-custom-name')}
					onChange={() => setCustomNameEnabled(!customNameEnabled)}
				/>

				<span
					className="mb-3 ml-1"
					data-tooltip-align="top"
					title={Liferay.Language.get('use-custom-name-help')}
				>
					<ClayIcon symbol="question-circle" />
				</span>
			</ClayForm.Group>

			<ClayForm.Group
				className={classNames({'has-error': customNameInvalid})}
			>
				<label
					className={classNames({disabled: !customNameEnabled})}
					htmlFor={`${namespace}_nameInput`}
				>
					{Liferay.Language.get('name')}
				</label>

				<ClayInput.Group>
					<ClayInput.GroupItem>
						<ClayInput
							disabled={!customNameEnabled}
							id={`${namespace}_nameInput`}
							onChange={(event) => {
								setTranslations({
									...translations,
									[selectedLocaleId]: event.target.value,
								});

								setCustomName(event.target.value);
							}}
							type="text"
							value={customName}
						/>
					</ClayInput.GroupItem>

					<ClayInput.GroupItem shrink>
						<TranslationAdminSelector
							activeLanguageIds={locales.map(
								(locale) => locale.id
							)}
							availableLocales={locales}
							defaultLanguageId={defaultLanguageId}
							onSelectedLanguageIdChange={setSelectedLocaleId}
							selectedLanguageId={selectedLocaleId}
							translations={translations}
						/>
					</ClayInput.GroupItem>
				</ClayInput.Group>

				{customNameInvalid && (
					<ClayForm.FeedbackItem>
						{Liferay.Language.get('this-field-is-required')}
					</ClayForm.FeedbackItem>
				)}
			</ClayForm.Group>

			<ClayForm.Group
				className={classNames({'has-warning': !hasDisplayPage})}
			>
				<label htmlFor={`${namespace}_itemInput`}>
					{Liferay.Language.get('item')}
				</label>

				<ClayInput.Group>
					<ClayInput.GroupItem>
						<ClayInput
							className="text-secondary"
							id={`${namespace}_itemInput`}
							onChange={() => {}}
							onClick={openChooseItemModal}
							type="text"
							value={selectedItem.title}
						/>
					</ClayInput.GroupItem>

					<ClayInput.GroupItem shrink>
						<ClayButtonWithIcon
							aria-label={Liferay.Language.get('change-item')}
							displayType="secondary"
							onClick={openChooseItemModal}
							symbol="change"
							title={Liferay.Language.get('change-item')}
						/>
					</ClayInput.GroupItem>
				</ClayInput.Group>

				{!hasDisplayPage && (
					<>
						<ClayAlert
							className="mt-1"
							displayType="warning"
							role={null}
							title={Liferay.Language.get('no-display-page')}
							variant="feedback"
						/>

						<p className="small text-secondary">
							{`${Liferay.Language.get(
								'this-item-does-not-have-a-display-page'
							)} 
								${Liferay.Language.get(
									'items-without-display-page-do-not-have-links-and-are-hidden-from-menus'
								)}`}
						</p>
					</>
				)}
			</ClayForm.Group>

			<ClayForm.Group className="pt-2">
				<div className="list-group">
					<p className="list-group-title">
						{Liferay.Language.get('type')}
					</p>

					<div>
						<ClayLabel displayType="secondary">{type}</ClayLabel>
					</div>
				</div>
			</ClayForm.Group>

			{subtype && (
				<ClayForm.Group>
					<div className="list-group">
						<p className="list-group-title">
							{Liferay.Language.get('subtype')}
						</p>

						<p className="list-group-text">{subtype}</p>
					</div>
				</ClayForm.Group>
			)}

			{Boolean(itemData?.length) &&
				itemData.map(({title, value}) => (
					<ClayForm.Group key={title}>
						<div className="list-group">
							<p className="list-group-title">{title}</p>

							<p className="list-group-text">{value}</p>
						</div>
					</ClayForm.Group>
				))}

			<FormValues
				localizedNames={translations}
				namespace={namespace}
				selectedItem={selectedItem}
				useCustomName={customNameEnabled}
			/>
		</>
	);
}

DisplayPageItemContextualSidebar.propTypes = {
	chooseItemProps: PropTypes.object.isRequired,
	defaultLanguageId: PropTypes.string.isRequired,
	hasDisplayPage: PropTypes.bool.isRequired,
	item: PropTypes.shape({
		classNameId: PropTypes.string,
		classPK: PropTypes.string,
		classTypeId: PropTypes.string,
		data: PropTypes.array,
		title: PropTypes.string,
		type: PropTypes.string,
	}).isRequired,
	itemSubtype: PropTypes.string,
	itemType: PropTypes.string.isRequired,
	locales: PropTypes.array.isRequired,
	localizedNames: PropTypes.object.isRequired,
	namespace: PropTypes.string.isRequired,
	useCustomName: PropTypes.bool.isRequired,
};

function FormValues({localizedNames, namespace, selectedItem, useCustomName}) {
	return (
		<>
			<input
				name={getFieldName(namespace, 'classNameId')}
				readOnly
				type="hidden"
				value={selectedItem.classNameId || ''}
			/>
			<input
				name={getFieldName(namespace, 'classPK')}
				readOnly
				type="hidden"
				value={selectedItem.classPK || ''}
			/>
			<input
				name={getFieldName(namespace, 'classTypeId')}
				readOnly
				type="hidden"
				value={selectedItem.classTypeId || ''}
			/>
			<input
				name={getFieldName(namespace, 'externalReferenceCode')}
				readOnly
				type="hidden"
				value={selectedItem.externalReferenceCode || ''}
			/>
			<input
				name={getFieldName(namespace, 'title')}
				readOnly
				type="hidden"
				value={selectedItem.title || ''}
			/>
			<input
				name={getFieldName(namespace, 'type')}
				readOnly
				type="hidden"
				value={selectedItem.type || ''}
			/>
			<input
				name={getFieldName(namespace, 'localizedNames')}
				readOnly
				type="hidden"
				value={useCustomName ? JSON.stringify(localizedNames) : '{}'}
			/>
			<input
				name={getFieldName(namespace, 'useCustomName')}
				readOnly
				type="hidden"
				value={useCustomName}
			/>
		</>
	);
}

FormValues.propTypes = {
	localizedNames: PropTypes.object.isRequired,
	namespace: PropTypes.string.isRequired,
	selectedItem: PropTypes.shape({
		classNameId: PropTypes.string,
		classPK: PropTypes.string,
		classTypeId: PropTypes.string,
		data: PropTypes.array,
		title: PropTypes.string,
		type: PropTypes.string,
	}).isRequired,
};

function getFieldName(namespace, fieldName) {
	return `${namespace}TypeSettingsProperties--${fieldName}--`;
}

export {DisplayPageItemContextualSidebar};
