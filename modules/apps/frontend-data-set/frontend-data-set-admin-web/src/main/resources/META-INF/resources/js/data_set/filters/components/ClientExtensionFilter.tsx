/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import ClayDropDown from '@clayui/drop-down';
import ClayForm from '@clayui/form';
import ClayLayout from '@clayui/layout';
import {IClientExtensionRenderer} from '@liferay/frontend-data-set-web';
import classNames from 'classnames';
import React, {useState} from 'react';

import RequiredMark from '../../../components/RequiredMark';
import ValidationFeedback from '../../../components/ValidationFeedback';
import {IClientExtensionFilter, IField, IFilter} from '../../../utils/types';
import Configuration from './Configuration';
import Footer from './Footer';

function Header() {
	return <>{Liferay.Language.get('new-client-extension-filter')}</>;
}

interface IBodyProps {
	fieldNames?: string[];
	fields: IField[];
	filter?: IFilter;
	filterClientExtensionRenderers: IClientExtensionRenderer[];
	namespace: string;
	onCancel: Function;
	onSave: Function;
}

function Body({
	fieldNames: usedFieldNames,
	fields,
	filter,
	filterClientExtensionRenderers,
	namespace,
	onCancel,
	onSave,
}: IBodyProps) {
	const [clientExtensionValidationError, setClientExtensionValidationError] =
		useState<boolean>(false);
	const [fieldInUseValidationError, setFieldInUseValidationError] =
		useState<boolean>(false);
	const [fieldValidationError, setFieldValidationError] =
		useState<boolean>(false);
	const [labelValidationError, setLabelValidationError] =
		useState<boolean>(false);

	const [saveButtonDisabled, setSaveButtonDisabled] =
		useState<boolean>(false);
	const [selectedClientExtension, setSelectedClientExtension] = useState<
		IClientExtensionRenderer | undefined
	>(
		filter
			? filterClientExtensionRenderers.find(
					(clientExtensionRenderer: IClientExtensionRenderer) =>
						clientExtensionRenderer.externalReferenceCode ===
						(filter as IClientExtensionFilter)
							.clientExtensionEntryERC
				)
			: undefined
	);
	const [i18nFilterLabels, setI18nFilterLabels] = useState(
		filter?.label_i18n ?? {}
	);
	const [selectedField, setSelectedField] = useState<IField | undefined>(
		filter
			? {
					entityFieldType: filter.entityFieldType,
					entityFieldTypeCollection: filter.entityFieldTypeCollection,
					label: filter.fieldName,
					name: filter.fieldName,
				}
			: undefined
	);
	const filterClientExtensionFormElementId = `${namespace}clientExtensionEntryERC`;

	const isi18nFilterLabelsValid = (
		i18nFilterLabels: Partial<Liferay.Language.FullyLocalizedValue<string>>
	) => {
		let isValid = true;

		if (!i18nFilterLabels || !Object.values(i18nFilterLabels).length) {
			isValid = false;
		}

		Object.values(i18nFilterLabels).forEach((value) => {
			if (!value) {
				isValid = false;
			}
		});

		return isValid;
	};

	const validate = () => {
		let isValid = true;

		const isLabelValid = isi18nFilterLabelsValid(i18nFilterLabels);
		setLabelValidationError(!isLabelValid);

		isValid = isLabelValid;

		if (!selectedField) {
			setFieldValidationError(true);

			isValid = false;
		}

		if (selectedField && !filter) {
			if (usedFieldNames?.includes(selectedField?.name)) {
				setFieldInUseValidationError(true);

				isValid = false;
			}
		}

		if (!selectedClientExtension) {
			setClientExtensionValidationError(true);

			isValid = false;
		}

		return isValid;
	};

	const saveClientExtensionFilter = () => {
		setSaveButtonDisabled(true);

		const success = validate();

		if (success) {
			const formData = {
				clientExtensionEntryERC:
					selectedClientExtension?.externalReferenceCode,
				entityFieldType: selectedField?.entityFieldType,
				entityFieldTypeCollection:
					selectedField?.entityFieldTypeCollection,
				fieldName: selectedField?.name,
				label_i18n: i18nFilterLabels,
				type: selectedField?.type,
			};

			onSave(formData);
		}
		else {
			setSaveButtonDisabled(false);
		}
	};

	return (
		<>
			<ClayLayout.SheetSection>
				<Configuration
					fieldInUseValidationError={fieldInUseValidationError}
					fieldValidationError={fieldValidationError}
					fields={fields}
					filter={filter}
					labelValidationError={labelValidationError}
					namespace={namespace}
					onBlur={() => {
						setLabelValidationError(
							!isi18nFilterLabelsValid(i18nFilterLabels)
						);
					}}
					onChangeField={(newValue) => {
						setSelectedField(newValue);

						setFieldValidationError(!newValue);
						setFieldInUseValidationError(
							newValue
								? !!usedFieldNames?.includes(newValue.name)
								: false
						);
					}}
					onChangeLabel={(newValue) => {
						setI18nFilterLabels(newValue);
					}}
					selectedField={selectedField}
				/>

				{!fieldInUseValidationError && (
					<ClayForm.Group
						className={classNames('form-group-autofit', {
							'has-error': clientExtensionValidationError,
						})}
					>
						<div className={classNames('form-group-item')}>
							<label htmlFor={filterClientExtensionFormElementId}>
								{Liferay.Language.get('client-extension')}

								<RequiredMark />
							</label>

							<ClayDropDown
								closeOnClick
								menuElementAttrs={{
									className:
										'fds-cell-renderers-dropdown-menu',
								}}
								trigger={
									<ClayButton
										aria-labelledby={`${namespace}cellRenderersLabel`}
										className="form-control form-control-select form-control-select-secondary"
										displayType="secondary"
										id={filterClientExtensionFormElementId}
										name={
											filterClientExtensionFormElementId
										}
									>
										{selectedClientExtension
											? selectedClientExtension.name
											: Liferay.Language.get('select')}
									</ClayButton>
								}
							>
								<ClayDropDown.ItemList
									items={filterClientExtensionRenderers}
									role="listbox"
								>
									{filterClientExtensionRenderers.map(
										(
											filterClientExtension: IClientExtensionRenderer
										) => (
											<ClayDropDown.Item
												className="align-items-center d-flex justify-content-between"
												key={filterClientExtension.name}
												onClick={() => {
													setSelectedClientExtension(
														filterClientExtension
													);
													setClientExtensionValidationError(
														!filterClientExtension
													);
												}}
												roleItem="option"
											>
												{filterClientExtension.name}
											</ClayDropDown.Item>
										)
									)}
								</ClayDropDown.ItemList>
							</ClayDropDown>

							{clientExtensionValidationError && (
								<ValidationFeedback />
							)}
						</div>
					</ClayForm.Group>
				)}
			</ClayLayout.SheetSection>

			<Footer
				onCancel={onCancel}
				onSave={saveClientExtensionFilter}
				saveButtonDisabled={saveButtonDisabled}
			/>
		</>
	);
}

export default {
	Body,
	Header,
};
