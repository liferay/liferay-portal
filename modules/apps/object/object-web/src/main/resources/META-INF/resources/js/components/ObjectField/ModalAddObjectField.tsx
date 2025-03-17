/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayAlert from '@clayui/alert';
import ClayButton from '@clayui/button';
import ClayForm from '@clayui/form';
import ClayModal, {ClayModalProvider, useModal} from '@clayui/modal';
import {ClayTooltipProvider} from '@clayui/tooltip';
import {API, Input, Toggle} from '@liferay/object-js-components-web';
import React, {useEffect, useState} from 'react';

import {defaultLanguageId} from '../../utils/constants';
import {toCamelCase} from '../../utils/string';
import ListTypeDefaultValueSelect from './DefaultValueFields/ListTypeDefaultValueSelect';
import ObjectFieldFormBase from './ObjectFieldFormBase';
import {useObjectFieldForm} from './useObjectFieldForm';

import './ModalAddObjectField.scss';

import {createResourceURL, fetch} from 'frontend-js-web';

interface ModalAddObjectField {
	baseResourceURL: string;
	creationLanguageId: Liferay.Language.Locale;
	objectDefinitionExternalReferenceCode: string;
	objectDefinitionName?: string;
	onAfterSubmit: (value: ObjectField) => void;
	setVisible: (value: boolean) => void;
}

export function ModalAddObjectField({
	baseResourceURL,
	creationLanguageId,
	objectDefinitionExternalReferenceCode,
	onAfterSubmit,
	setVisible,
}: ModalAddObjectField) {
	const [error, setError] = useState<string>('');
	const [objectDefinition, setObjectDefinition] =
		useState<ObjectDefinition>();
	const [objectFieldBusinessTypes, setObjectFieldBusinessTypes] = useState<
		ObjectFieldBusinessType[]
	>([]);
	const {observer, onClose} = useModal({onClose: () => setVisible(false)});
	const formId = 'modalAddObjectField';
	const initialValues: Partial<ObjectField> = {
		indexed: true,
		indexedAsKeyword: false,
		indexedLanguageId: '',
		listTypeDefinitionExternalReferenceCode: '',
		listTypeDefinitionId: 0,
		localized: false,
		readOnly: 'false',
		readOnlyConditionExpression: '',
		required: false,
	};

	const onSubmit = async (field: Partial<ObjectField>) => {
		if (
			field.businessType === 'Aggregation' ||
			field.businessType === 'Formula'
		) {
			field.readOnly = 'true';
			delete field.readOnlyConditionExpression;
		}

		if (field.label) {
			field = {
				...field,
				name:
					field.name ||
					toCamelCase(field.label[defaultLanguageId] as string, true),
			};

			delete field.listTypeDefinitionId;

			try {
				const objectFieldResponse = await API.save<ObjectField>({
					item: field,
					method: 'POST',
					returnValue: true,
					url: `/o/object-admin/v1.0/object-definitions/by-external-reference-code/${objectDefinitionExternalReferenceCode}/object-fields`,
				});

				onAfterSubmit(objectFieldResponse as ObjectField);
			}
			catch (error) {
				setError((error as Error).message);
			}
		}
	};

	const {errors, handleChange, handleSubmit, setValues, values} =
		useObjectFieldForm({
			initialValues,
			onSubmit,
		});

	const showEnableTranslationToggle =
		values.businessType === 'LongText' ||
		values.businessType === 'RichText' ||
		values.businessType === 'Text' ||
		(Liferay.FeatureFlags['LPD-32050'] &&
			(values.businessType === 'Attachment' ||
				values.businessType === 'Boolean' ||
				values.businessType === 'Date' ||
				values.businessType === 'DateTime' ||
				values.businessType === 'Decimal' ||
				values.businessType === 'Integer' ||
				values.businessType === 'LongInteger' ||
				values.businessType === 'MultiselectPicklist' ||
				values.businessType === 'Picklist' ||
				values.businessType === 'PrecisionDecimal'));

	useEffect(() => {
		const makeFetch = async () => {
			const objectDefinitionResponse =
				await API.getObjectDefinitionByExternalReferenceCode(
					objectDefinitionExternalReferenceCode
				);

			setObjectDefinition(objectDefinitionResponse);

			const url = createResourceURL(baseResourceURL, {
				objectDefinitionId: objectDefinitionResponse.id,
				p_p_resource_id:
					'/object_definitions/get_object_field_business_types',
			}).href;

			const objectFieldBusinessTypesResponse = await fetch(url, {
				method: 'GET',
			});

			const {objectFieldBusinessTypes} =
				(await objectFieldBusinessTypesResponse.json()) as {
					objectFieldBusinessTypes: ObjectFieldBusinessType[];
				};

			setObjectFieldBusinessTypes(
				objectFieldBusinessTypes.filter((objectFieldBusinessType) => {
					if (
						objectFieldBusinessType.businessType !== 'Relationship'
					) {
						return objectFieldBusinessType;
					}
				})
			);
		};

		makeFetch();

		// eslint-disable-next-line react-hooks/exhaustive-deps
	}, [objectDefinitionExternalReferenceCode, values.businessType]);

	return (
		<ClayModalProvider>
			<ClayTooltipProvider>
				<ClayModal center observer={observer}>
					<ClayModal.Header>
						{Liferay.Language.get('new-field')}
					</ClayModal.Header>

					<ClayModal.Body>
						<ClayForm id={formId} onSubmit={handleSubmit}>
							{error && (
								<ClayAlert displayType="danger">
									{error}
								</ClayAlert>
							)}

							<Input
								error={errors.label}
								label={Liferay.Language.get('label')}
								name="label"
								onChange={({target: {value}}) => {
									setValues({
										label: {[defaultLanguageId]: value},
									});
								}}
								required
								value={values.label?.[defaultLanguageId]}
							/>

							<ObjectFieldFormBase
								baseResourceURL={baseResourceURL}
								className="lfr-objects__modal-add-object-field-form-base"
								errors={errors}
								handleChange={handleChange}
								objectDefinition={
									objectDefinition as ObjectDefinition
								}
								objectField={values}
								objectFieldBusinessTypesInfo={
									objectFieldBusinessTypes
								}
								setValues={setValues}
							>
								{showEnableTranslationToggle && (
									<div className="lfr-objects__modal-add-object-field-enable-translations-toggle">
										<Toggle
											disabled={
												!objectDefinition?.enableLocalization ||
												(!Liferay.FeatureFlags[
													'LPD-32050'
												] &&
													values.required)
											}
											label={Liferay.Language.get(
												'enable-entry-translations'
											)}
											name="enableEntryTranslations"
											onToggle={(localized) =>
												setValues({
													localized,
												})
											}
											toggled={values.localized}
											tooltip={Liferay.Language.get(
												'users-will-be-able-to-add-translations-for-the-entries-of-this-field'
											)}
										/>
									</div>
								)}
							</ObjectFieldFormBase>

							{values.state && (
								<ListTypeDefaultValueSelect
									creationLanguageId={creationLanguageId}
									defaultValue={
										values.objectFieldSettings?.find(
											(setting) =>
												setting.name === 'defaultValue'
										)?.value
									}
									error={errors.defaultValue}
									label={Liferay.Language.get(
										'default-value'
									)}
									required
									setValues={setValues}
									values={values}
								/>
							)}
						</ClayForm>
					</ClayModal.Body>

					<ClayModal.Footer
						last={
							<ClayButton.Group spaced>
								<ClayButton
									displayType="secondary"
									onClick={() => onClose()}
								>
									{Liferay.Language.get('cancel')}
								</ClayButton>

								<ClayButton form={formId} type="submit">
									{Liferay.Language.get('save')}
								</ClayButton>
							</ClayButton.Group>
						}
					/>
				</ClayModal>
			</ClayTooltipProvider>
		</ClayModalProvider>
	);
}
