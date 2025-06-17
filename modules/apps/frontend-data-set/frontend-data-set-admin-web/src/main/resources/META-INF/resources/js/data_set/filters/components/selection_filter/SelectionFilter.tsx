/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayForm, {
	ClayRadio,
	ClayRadioGroup,
	ClaySelectWithOption,
} from '@clayui/form';
import {TItem} from '@clayui/form/lib/SelectBox';
import ClayIcon from '@clayui/icon';
import ClayLayout from '@clayui/layout';
import classNames from 'classnames';
import React, {useState} from 'react';

import CheckboxMultiSelect from '../../../../components/CheckboxMultiSelect';
import RequiredMark from '../../../../components/RequiredMark';
import {
	ESelectionFilterSourceType,
	IField,
	IFilter,
	IPickList,
	ISelectionFilter,
} from '../../../../utils/types';
import Configuration from '../Configuration';
import Footer from '../Footer';
import ApiRestApplication from './source_type/ApiRestApplication';
import ObjectPicklist from './source_type/ObjectPicklist';

function Header() {
	return <>{Liferay.Language.get('new-selection-filter')}</>;
}

function Body({
	fieldNames: usedFieldNames,
	fields,
	filter,
	namespace,
	onCancel,
	onSave,
	resolvedRESTSchemas,
	restApplications,
}: {
	fieldNames?: string[];
	fields: IField[];
	filter?: IFilter;
	namespace: string;
	onCancel: Function;
	onSave: Function;
	resolvedRESTSchemas: string[];
	restApplications: string[];
}) {
	const [fieldInUseValidationError, setFieldInUseValidationError] =
		useState<boolean>(false);
	const [fieldValidationError, setFieldValidationError] =
		useState<boolean>(false);
	const [itemKeyValidationError, setItemKeyValidationError] =
		useState<boolean>(false);
	const [itemLabelValidationError, setItemLabelValidationError] =
		useState<boolean>(false);
	const [labelValidationError, setLabelValidationError] =
		useState<boolean>(false);
	const [
		requiredRESTApplicationValidationError,
		setRequiredRESTApplicationValidationError,
	] = useState(false);
	const [restSchemaValidationError, setRESTSchemaValidationError] =
		useState(false);
	const [restEndpointValidationError, setRESTEndpointValidationError] =
		useState(false);
	const [sourceValidationError, setSourceValidationError] =
		useState<boolean>(false);
	const [sourceTypeValidationError, setSourceTypeValidationError] =
		useState<boolean>(false);

	const [filteredSourceItems, setFilteredSourceItems] = useState<TItem[]>([]);
	const [saveButtonDisabled, setSaveButtonDisabled] =
		useState<boolean>(false);
	const [includeMode, setIncludeMode] = useState<string>('include');

	const [multiple, setMultiple] = useState(filter?.multiple ?? true);
	const [picklists, setPicklists] = useState<IPickList[]>();
	const [preselectedValueInput, setPreselectedValueInput] = useState('');
	const [preselectedValues, setPreselectedValues] = useState<TItem[]>(
		JSON.parse(filter?.preselectedValues || '[]')
	);
	const [selectedField, setSelectedField] = useState<IField | undefined>(
		filter ? {label: filter.fieldName, name: filter.fieldName, filterable: filter.filterable, entityFieldType: filter.entityFieldType} : undefined
	);
	const [source, setSource] = useState<string | undefined>(filter?.source);
	const [sourceType, setSourceType] = useState(filter?.sourceType);
	const fdsFilterLabelTranslations = filter?.label_i18n ?? {};
	const [i18nFilterLabels, setI18nFilterLabels] = useState(
		fdsFilterLabelTranslations
	);
	const [selectedPicklist, setSelectedPicklist] = useState<
		IPickList | undefined
	>();
	const [selectedRESTApplication, setSelectedRESTApplication] = useState(
		filter?.restApplication || null
	);
	const [selectedRESTSchema, setSelectedRESTSchema] = useState(
		filter?.restSchema || null
	);
	const [selectedRESTEndpoint, setSelectedRESTEndpoint] = useState(
		filter?.restEndpoint || null
	);
	const [selectedItemKey, setSelectedItemKey] = useState(filter?.itemKey);
	const [selectedItemLabel, setSelectedItemLabel] = useState(
		filter?.itemLabel
	);

	const includeModeFormElementId = `${namespace}IncludeMode`;
	const multipleFormElementId = `${namespace}Multiple`;
	const sourceOptionFormElementId = `${namespace}SourceOption`;
	const preselectedValuesFormElementId = `${namespace}PreselectedValues`;

	const isValidSingleMode =
		multiple || (!multiple && !(preselectedValues.length > 1));

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

		if (!sourceType) {
			setSourceTypeValidationError(true);

			isValid = false;
		}

		if (!source) {
			setSourceValidationError(true);

			isValid = false;
		}

		if (sourceType === ESelectionFilterSourceType.API_REST_APPLICATION) {
			if (!selectedItemKey) {
				setItemKeyValidationError(true);

				isValid = false;
			}

			if (!selectedItemLabel) {
				setItemLabelValidationError(true);

				isValid = false;
			}

			if (!selectedRESTApplication) {
				setRequiredRESTApplicationValidationError(true);

				isValid = false;
			}

			if (!selectedRESTSchema) {
				setRESTSchemaValidationError(true);

				isValid = false;
			}

			if (!selectedRESTEndpoint) {
				setRESTEndpointValidationError(true);

				isValid = false;
			}
		}

		return isValid;
	};

	const saveSelectionFilter = () => {
		setSaveButtonDisabled(true);

		const success = validate();

		if (success) {
			let formData: any = {
				entityFieldType: selectedField?.entityFieldType,
				fieldName: selectedField?.name,
				include: includeMode === 'include',
				label_i18n: i18nFilterLabels,
				multiple,
				preselectedValues: JSON.stringify(
					preselectedValues.map((item: any) => ({
						label: item.label,
						value: item.value,
					}))
				),
				source,
				sourceType,
			};

			if (
				sourceType === ESelectionFilterSourceType.API_REST_APPLICATION
			) {
				formData = {
					...formData,
					itemKey: selectedItemKey,
					itemLabel: selectedItemLabel,
					restApplication: selectedRESTApplication,
					restEndpoint: selectedRESTEndpoint,
					restSchema: selectedRESTSchema,
				};
			}

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
					<>
						<ClayLayout.SheetSection className="mb-4">
							<h3 className="sheet-subtitle">
								{Liferay.Language.get('filter-source')}
							</h3>

							<ClayForm.Text>
								{Liferay.Language.get(
									'the-filter-source-determines-the-values-to-be-offered-in-this-filter-to-the-user'
								)}
							</ClayForm.Text>
						</ClayLayout.SheetSection>

						<ClayForm.Group
							className={classNames({
								'has-error': sourceTypeValidationError,
							})}
						>
							<label htmlFor={sourceOptionFormElementId}>
								{Liferay.Language.get('source')}

								<RequiredMark />
							</label>

							<ClaySelectWithOption
								aria-label={Liferay.Language.get(
									'choose-an-option'
								)}
								id={sourceOptionFormElementId}
								name={sourceOptionFormElementId}
								onChange={(event) => {
									setSourceType(
										event.target
											.value as ESelectionFilterSourceType
									);

									setSourceTypeValidationError(false);

									setSelectedItemKey(undefined);
									setSelectedItemLabel(undefined);
									setSelectedRESTApplication(null);
									setSelectedRESTEndpoint(null);
									setSelectedRESTEndpoint(null);
									setSource(undefined);
									setPreselectedValueInput('');
									setPreselectedValues([]);

									setSelectedPicklist(undefined);
								}}
								options={[
									{
										disabled: true,
										label: Liferay.Language.get(
											'choose-an-option'
										),
										value: '',
									},
									{
										disabled: false,
										label: Liferay.Language.get(
											'api-rest-application'
										),
										value: ESelectionFilterSourceType.API_REST_APPLICATION,
									},
									{
										disabled: false,
										label: Liferay.Language.get(
											'object-picklist'
										),
										value: ESelectionFilterSourceType.OBJECT_PICKLIST,
									},
								]}
								required
								title={Liferay.Language.get('source')}
								value={sourceType || ''}
							/>

							{sourceTypeValidationError && (
								<ClayForm.FeedbackGroup>
									<ClayForm.FeedbackItem>
										<ClayForm.FeedbackIndicator symbol="exclamation-full" />

										{Liferay.Language.get(
											'this-field-is-required'
										)}
									</ClayForm.FeedbackItem>
								</ClayForm.FeedbackGroup>
							)}
						</ClayForm.Group>

						{sourceType ===
							ESelectionFilterSourceType.OBJECT_PICKLIST && (
							<ObjectPicklist
								namespace={namespace}
								onChange={(picklist: IPickList) => {
									setSource(picklist.externalReferenceCode);

									setSelectedPicklist(picklist);

									setSourceValidationError(false);

									setFilteredSourceItems(
										picklist.listTypeEntries.map(
											(item) => ({
												label: item.name,
												value: String(
													item.externalReferenceCode
												),
											})
										)
									);
								}}
								onPicklistsLoad={(
									picklists: Array<IPickList>
								) => {
									setPicklists(picklists);

									const savedPicklistERC = filter?.source;

									if (!savedPicklistERC) {
										return;
									}

									const picklist = picklists.find(
										(item) =>
											item.externalReferenceCode ===
											savedPicklistERC
									);

									if (!picklist) {
										return;
									}

									setSelectedPicklist(picklist);

									setFilteredSourceItems(
										picklist.listTypeEntries.map(
											(item) => ({
												label: item.name,
												value: item.externalReferenceCode,
											})
										)
									);

									const validPreselectedValues: Array<TItem> =
										[];

									picklist.listTypeEntries.forEach((item) => {
										const value = preselectedValues.find(
											(preselectedValue) =>
												preselectedValue.value ===
												item.externalReferenceCode
										);

										if (value) {
											validPreselectedValues.push({
												label: item.name,
												value: item.externalReferenceCode,
											});
										}
									});

									setPreselectedValues(
										validPreselectedValues
									);
								}}
								picklists={picklists}
								selectedPicklist={selectedPicklist}
								sourceValidationError={sourceValidationError}
							/>
						)}

						{sourceType ===
							ESelectionFilterSourceType.API_REST_APPLICATION && (
							<ApiRestApplication
								filter={filter as ISelectionFilter}
								itemKeyValidationError={itemKeyValidationError}
								itemLabelValidationError={
									itemLabelValidationError
								}
								namespace={namespace}
								onChange={({
									selectedItemKey,
									selectedItemLabel,
									selectedRESTApplication,
									selectedRESTEndpoint,
									selectedRESTSchema,
									sourceItems,
								}) => {
									setSelectedRESTApplication(
										selectedRESTApplication
									);
									if (selectedRESTApplication) {
										setRequiredRESTApplicationValidationError(
											false
										);
									}

									setSelectedRESTEndpoint(
										selectedRESTEndpoint
									);
									if (selectedRESTEndpoint) {
										setRESTEndpointValidationError(false);
									}

									if (
										selectedRESTApplication &&
										selectedRESTEndpoint
									) {
										setSource(
											`/o${selectedRESTApplication.replace('v1.0', '')}${selectedRESTEndpoint}`
										);
									}

									setSourceValidationError(false);

									setSelectedRESTSchema(selectedRESTSchema);
									if (selectedRESTSchema) {
										setRESTSchemaValidationError(false);
									}

									setSelectedItemKey(selectedItemKey);
									setItemKeyValidationError(false);

									setSelectedItemLabel(selectedItemLabel);
									setItemLabelValidationError(false);

									setFilteredSourceItems(sourceItems);
								}}
								preselectedValueInput={preselectedValueInput}
								requiredRESTApplicationValidationError={
									requiredRESTApplicationValidationError
								}
								resolvedRESTSchemas={resolvedRESTSchemas}
								restApplications={restApplications}
								restEndpointValidationError={
									restEndpointValidationError
								}
								restSchemaValidationError={
									restSchemaValidationError
								}
								source={source as string}
							/>
						)}

						{source &&
							(sourceType ===
								ESelectionFilterSourceType.API_REST_APPLICATION ||
								picklists) && (
								<>
									<ClayLayout.SheetSection className="mb-4">
										<h3 className="sheet-subtitle">
											{Liferay.Language.get(
												'filter-options'
											)}
										</h3>
									</ClayLayout.SheetSection>
									<ClayForm.Group
										className={classNames({
											'has-error': !isValidSingleMode,
										})}
									>
										<label
											htmlFor={
												preselectedValuesFormElementId
											}
										>
											{Liferay.Language.get(
												'preselected-values'
											)}

											<span
												className="label-icon lfr-portal-tooltip ml-2"
												title={Liferay.Language.get(
													'choose-values-to-preselect-for-your-filters-source-option'
												)}
											>
												<ClayIcon symbol="question-circle-full" />
											</span>
										</label>

										<CheckboxMultiSelect
											allowsCustomLabel={false}
											aria-label={Liferay.Language.get(
												'preselected-values'
											)}
											inputName={
												preselectedValuesFormElementId
											}
											items={preselectedValues}
											loadingState={4}
											onChange={setPreselectedValueInput}
											onItemsChange={(
												selectedItems: any
											) => {
												setPreselectedValues(
													selectedItems
												);
											}}
											placeholder={Liferay.Language.get(
												'select-a-default-value-for-your-filter'
											)}
											sourceItems={filteredSourceItems}
											value={preselectedValueInput}
										/>

										{!isValidSingleMode && (
											<ClayForm.FeedbackGroup>
												<ClayForm.FeedbackItem>
													<ClayForm.FeedbackIndicator symbol="exclamation-full" />

													{Liferay.Language.get(
														'only-one-value-is-allowed-in-single-selection-mode'
													)}
												</ClayForm.FeedbackItem>
											</ClayForm.FeedbackGroup>
										)}
									</ClayForm.Group>

									<ClayLayout.Row justify="start">
										<ClayLayout.Col size={6}>
											<ClayForm.Group>
												<label
													htmlFor={
														multipleFormElementId
													}
												>
													{Liferay.Language.get(
														'selection'
													)}

													<span
														className="label-icon lfr-portal-tooltip ml-2"
														title={Liferay.Language.get(
															'determines-how-many-preselected-values-for-the-filter-can-be-added'
														)}
													>
														<ClayIcon symbol="question-circle-full" />
													</span>
												</label>

												<ClayRadioGroup
													name={multipleFormElementId}
													onChange={(newVal: any) => {
														const newMultiple =
															newVal === 'true';
														setMultiple(
															newMultiple
														);
													}}
													value={
														multiple
															? 'true'
															: 'false'
													}
												>
													<ClayRadio
														label={Liferay.Language.get(
															'multiple'
														)}
														value="true"
													/>

													<ClayRadio
														label={Liferay.Language.get(
															'single'
														)}
														value="false"
													/>
												</ClayRadioGroup>
											</ClayForm.Group>
										</ClayLayout.Col>

										{preselectedValues?.length > 0 && (
											<ClayLayout.Col size={6}>
												<ClayForm.Group>
													<label
														htmlFor={
															includeModeFormElementId
														}
													>
														{Liferay.Language.get(
															'filter-mode'
														)}

														<span
															className="label-icon lfr-portal-tooltip ml-2"
															title={Liferay.Language.get(
																'include-returns-only-the-selected-values.-exclude-returns-all-except-the-selected-ones'
															)}
														>
															<ClayIcon symbol="question-circle-full" />
														</span>
													</label>

													<ClayRadioGroup
														name={
															includeModeFormElementId
														}
														onChange={(
															val: any
														) => {
															setIncludeMode(val);
														}}
														value={includeMode}
													>
														<ClayRadio
															label={Liferay.Language.get(
																'include'
															)}
															value="include"
														/>

														<ClayRadio
															label={Liferay.Language.get(
																'exclude'
															)}
															value="exclude"
														/>
													</ClayRadioGroup>
												</ClayForm.Group>
											</ClayLayout.Col>
										)}
									</ClayLayout.Row>
								</>
							)}
					</>
				)}
			</ClayLayout.SheetSection>

			<Footer
				onCancel={onCancel}
				onSave={saveSelectionFilter}
				saveButtonDisabled={saveButtonDisabled}
			/>
		</>
	);
}

export default {
	Body,
	Header,
};
