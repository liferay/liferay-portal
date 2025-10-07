/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton, {ClayButtonWithIcon} from '@clayui/button';
import {Option, Text} from '@clayui/core';
import ClayForm from '@clayui/form';
import ClayPopover from '@clayui/popover';
import {
	API,
	FormError,
	Input,
	SingleSelect,
	Toggle,
} from '@liferay/object-js-components-web';
import {
	ILearnResourceContext,
	LearnMessage,
	LearnResourcesContext,
} from 'frontend-js-components-web';
import {createResourceURL} from 'frontend-js-web';
import React, {
	ChangeEventHandler,
	ReactNode,
	useEffect,
	useMemo,
	useState,
} from 'react';

import {defaultLanguageId} from '../../utils/constants';
import {
	getDefaultValueFieldSettings,
	getUpdatedDefaultValueType,
} from '../../utils/defaultValues';
import {removeFieldSettings} from '../../utils/fieldSettings';
import {toCamelCase} from '../../utils/string';
import {AggregationFormBase} from './AggregationFormBase';
import {AttachmentFormBase} from './AttachmentFormBase';
import {AutoIncrementFormBase} from './AutoIncrementFormBase';
import {TimeStorage} from './TimeStorage';
import {UniqueValues} from './UniqueValues';
import {FORMULA_OUTPUT_OPTIONS, FormulaOutput} from './formulaFieldUtil';

import './ObjectFieldFormBase.scss';

import ClayIcon from '@clayui/icon';
import ClayLoadingIndicator from '@clayui/loading-indicator';
import classNames from 'classnames';

interface ObjectFieldFormBaseProps {
	baseResourceURL: string;
	children?: ReactNode;
	className?: string;
	creationLanguageId2?: Liferay.Language.Locale;
	dbObjectFieldRequired?: boolean;
	disabled?: boolean;
	editingObjectField?: boolean;
	errors: ObjectFieldErrors;
	handleChange: ChangeEventHandler<HTMLInputElement>;
	learnResources?: ILearnResourceContext;
	modelBuilder?: boolean;
	objectDefinition?: ObjectDefinition | ObjectDefinitionNodeData;
	objectField: Partial<ObjectField>;
	objectFieldBusinessTypesInfo: ObjectFieldBusinessType[];
	objectRelationshipId?: number;
	onAggregationFilterChange?: (aggregationFilterArray: []) => void;
	onObjectRelationshipChange?: (
		objectDefinitionExternalReferenceCode2: string
	) => void;
	onSubmit?: (values?: Partial<ObjectField>) => void;
	setDbObjectFieldRequired?: (value: boolean) => void;
	setValues: (values: Partial<ObjectField>) => void;
}

type TObjectRelationship = {
	deletionType: string;
	edge: boolean;
	id: number;
	label: LocalizedValue<string>;
	name: string;
	objectDefinitionExternalReferenceCode2: number;
};

export type ObjectFieldErrors = FormError<
	ObjectField & {[key in ObjectFieldSettingName]: unknown}
>;

const fieldSettingsMap = new Map<string, ObjectFieldSetting[]>([
	[
		'Aggregation',
		[
			{
				name: 'filters',
				objectFieldId: 0,
				value: Array(0),
			},
		],
	],
	[
		'Attachment',
		[
			{
				name: 'acceptedFileExtensions',
				value: 'jpeg, jpg, pdf, png',
			},
			{
				name: 'maximumFileSize',
				value: 0,
			},
		],
	],
	[
		'DateTime',
		[
			{
				name: 'timeStorage',
				value: 'convertToUTC',
			},
		],
	],
	[
		'LongText',
		[
			{
				name: 'showCounter',
				value: false,
			},
		],
	],
	[
		'Text',
		[
			{
				name: 'showCounter',
				value: false,
			},
		],
	],
]);

async function updateListTypeDefinitions(
	setListTypeDefinitions: (value: ListTypeDefinition[]) => void
) {
	const listTypeDefinitions = await API.getListTypeDefinitions();

	setListTypeDefinitions(listTypeDefinitions);
}

async function getObjectFieldSettingsByBusinessType(
	objectRelationshipId: number,
	setListTypeDefinitions: (value: ListTypeDefinition[]) => void,
	setObjectRelationship: (value: TObjectRelationship) => void,
	setReloadPicklistSingleSelect: (value: boolean) => void,
	setSelectedOutputValue: (value: string) => void,
	values: Partial<ObjectField>
) {
	const {businessType, objectFieldSettings} = values;

	if (businessType === 'Picklist' || businessType === 'MultiselectPicklist') {
		setReloadPicklistSingleSelect(true);
		updateListTypeDefinitions(setListTypeDefinitions);
	}

	if (businessType === 'Formula') {
		const output = objectFieldSettings?.find(
			(fieldSetting) => fieldSetting.name === 'output'
		);

		if (output) {
			setSelectedOutputValue(
				FORMULA_OUTPUT_OPTIONS.find(
					(formulaOption) => formulaOption.value === output?.value
				)?.value as string
			);
		}
	}

	if (businessType === 'Relationship' && objectRelationshipId !== 0) {
		const relationshipData =
			await API.getObjectRelationship<TObjectRelationship>(
				objectRelationshipId!
			);

		if (relationshipData.id) {
			setObjectRelationship(relationshipData);
		}
	}
}

export default function ObjectFieldFormBase({
	baseResourceURL,
	children,
	className,
	creationLanguageId2,
	dbObjectFieldRequired,
	disabled,
	editingObjectField = false,
	errors,
	handleChange,
	learnResources,
	modelBuilder = false,
	objectDefinition,
	objectField: values,
	objectFieldBusinessTypesInfo,
	objectRelationshipId,
	onAggregationFilterChange,
	onObjectRelationshipChange,
	onSubmit,
	setDbObjectFieldRequired,
	setValues,
}: ObjectFieldFormBaseProps) {
	const [listTypeDefinitions, setListTypeDefinitions] = useState<
		Partial<ListTypeDefinition>[]
	>([]);

	const [listTypeDefinitionsURL, setListTypeDefinitionsURL] =
		useState<string>('');

	const [objectRelationship, setObjectRelationship] =
		useState<TObjectRelationship>();
	const [reloadPicklistSingleSelect, setReloadPicklistSingleSelect] =
		useState(false);
	const [selectedOutputValue, setSelectedOutputValue] = useState<string>();
	const [showPopover, setShowPopover] = useState(false);
	const validListTypeDefinitionId =
		values.listTypeDefinitionId !== undefined &&
		values.listTypeDefinitionId !== 0;

	const listTypeDefinitionsItems = useMemo(() => {
		return listTypeDefinitions.map(({externalReferenceCode, name}) => ({
			label: name,
			value: externalReferenceCode,
		})) as LabelValueObject[];
	}, [listTypeDefinitions]);

	const selectedListTypeDefinitionExternalReferenceCode = useMemo(() => {
		return listTypeDefinitions.find(
			({externalReferenceCode}) =>
				values.listTypeDefinitionExternalReferenceCode ===
				externalReferenceCode
		)?.externalReferenceCode;
	}, [listTypeDefinitions, values.listTypeDefinitionExternalReferenceCode]);

	const handleTypeChange = async (selectedBusinessType: string) => {
		const selectedObjectFieldBusinessTypeInfo =
			objectFieldBusinessTypesInfo.find(
				(objectFieldBusinessTypeInfo) =>
					objectFieldBusinessTypeInfo.businessType ===
					selectedBusinessType
			);

		const objectFieldSettings: ObjectFieldSetting[] =
			fieldSettingsMap.get(selectedBusinessType) || [];

		const indexed =
			selectedBusinessType !== 'Aggregation' &&
			selectedBusinessType !== 'Formula' &&
			selectedBusinessType !== 'Encrypted';

		const isSearchableByText =
			selectedBusinessType === 'Attachment' ||
			selectedObjectFieldBusinessTypeInfo?.dbType === 'Clob' ||
			selectedObjectFieldBusinessTypeInfo?.dbType === 'String';

		const indexedAsKeyword = isSearchableByText && values.indexedAsKeyword;

		const indexedLanguageId =
			isSearchableByText && !values.indexedAsKeyword
				? values.indexedLanguageId ?? defaultLanguageId
				: '';

		errors.businessType =
			selectedBusinessType === values.businessType
				? errors.businessType
				: undefined;

		setSelectedOutputValue(undefined);

		setValues({
			DBType: selectedObjectFieldBusinessTypeInfo?.dbType,
			businessType: selectedObjectFieldBusinessTypeInfo?.businessType,
			indexed,
			indexedAsKeyword,
			indexedLanguageId,
			listTypeDefinitionExternalReferenceCode: '',
			listTypeDefinitionId: 0,
			objectFieldSettings,
			state: false,
		});

		if (onSubmit) {
			onSubmit({
				...values,
				DBType: selectedObjectFieldBusinessTypeInfo?.dbType,
				businessType: selectedObjectFieldBusinessTypeInfo?.businessType,
				indexed,
				indexedAsKeyword,
				indexedLanguageId,
				listTypeDefinitionExternalReferenceCode: '',
				listTypeDefinitionId: 0,
				objectFieldSettings,
				state: false,
			});
		}
	};

	const getMandatoryToggleDisabledState = () => {
		if (
			objectDefinition?.accountEntryRestricted &&
			objectDefinition?.accountEntryRestrictedObjectFieldName ===
				values.name
		) {
			return true;
		}

		if (values.readOnly === 'true' || values.readOnly === 'conditional') {
			return true;
		}

		if (
			objectRelationship &&
			objectRelationship.deletionType !== 'disassociate'
		) {
			return Liferay.FeatureFlags['LPD-34594']
				? objectRelationship.edge
				: false;
		}

		if (
			!dbObjectFieldRequired &&
			editingObjectField &&
			objectDefinition?.status?.label === 'approved'
		) {
			return true;
		}

		return (
			values.businessType === 'Relationship' ||
			(!Liferay.FeatureFlags['LPD-32050'] && values.localized) ||
			values.state
		);
	};

	const handleStateToggleChange = (toggled: boolean) => {
		let defaultValue;
		let defaultValueType;

		if (values.id) {
			const currentDefaultValueSettings =
				getDefaultValueFieldSettings(values);
			defaultValue = currentDefaultValueSettings.defaultValue;
			defaultValueType = currentDefaultValueSettings.defaultValueType;
		}

		if (toggled) {
			if (defaultValueType && defaultValue) {
				setValues({required: toggled, state: toggled});

				if (onSubmit) {
					onSubmit({
						...values,
						required: toggled,
						state: toggled,
					});
				}
			}
			else if (!defaultValueType || !defaultValue) {
				setValues({
					objectFieldSettings: getUpdatedDefaultValueType(
						values,
						'inputAsValue'
					),
					required: toggled,
					state: toggled,
				});

				if (onSubmit) {
					onSubmit({
						...values,
						objectFieldSettings: getUpdatedDefaultValueType(
							values,
							'inputAsValue'
						),
						required: toggled,
						state: toggled,
					});
				}
			}
		}
		else {
			setValues({
				required: toggled,
				state: toggled,
			});

			if (onSubmit) {
				onSubmit({
					...values,
					required: toggled,
					state: toggled,
				});
			}
		}
	};

	useEffect(() => {
		const makeFetch = async () => {
			await getObjectFieldSettingsByBusinessType(
				objectRelationshipId as number,
				setListTypeDefinitions,
				setObjectRelationship,
				setReloadPicklistSingleSelect,
				setSelectedOutputValue,
				values
			);

			const listTypeDefinitionsURL = createResourceURL(baseResourceURL, {
				p_p_resource_id:
					'/object_definitions/get_view_list_type_definitions_url',
			}).href;

			const {url} = await API.fetchJSON<{
				url: string;
			}>(listTypeDefinitionsURL);

			setListTypeDefinitionsURL(url);
		};

		makeFetch();

		// eslint-disable-next-line react-hooks/exhaustive-deps
	}, [objectRelationshipId, values.businessType]);

	useEffect(() => {
		if (reloadPicklistSingleSelect) {
			setTimeout(() => setReloadPicklistSingleSelect(false), 200);
		}
	}, [reloadPicklistSingleSelect]);

	return (
		<>
			<Input
				disabled={disabled}
				error={errors.name}
				label={Liferay.Language.get('field-name')}
				name="name"
				onBlur={(event) => {
					event.stopPropagation();

					if (onSubmit) {
						onSubmit();
					}
				}}
				onChange={handleChange}
				required
				value={
					values.name ??
					toCamelCase(values.label?.[defaultLanguageId] ?? '', true)
				}
			/>

			<SingleSelect<ObjectFieldBusinessType>
				className={className}
				disabled={disabled}
				error={errors.businessType}
				id="object-field-form-base__type-input"
				items={objectFieldBusinessTypesInfo}
				label={Liferay.Language.get('type')}
				onSelectionChange={(value) => {
					handleTypeChange(value as string);
				}}
				required
				selectedKey={values.businessType}
			>
				{(item) => (
					<Option key={item.businessType} textValue={item.label}>
						<div className="lfr-objects__object-field-form-base-object-field-type-option">
							<Text size={3} weight="semi-bold">
								{item.label}
							</Text>

							<Text aria-hidden color="secondary" size={2}>
								{item.description}
							</Text>
						</div>
					</Option>
				)}
			</SingleSelect>

			{values.businessType === 'Attachment' && objectDefinition && (
				<AttachmentFormBase
					disabled={disabled}
					error={errors.fileSource}
					objectDefinitionName={objectDefinition.name}
					objectFieldSettings={
						values.objectFieldSettings as ObjectFieldSetting[]
					}
					onSubmit={onSubmit}
					setValues={setValues}
					values={values}
				/>
			)}

			{values.businessType === 'AutoIncrement' && !editingObjectField && (
				<AutoIncrementFormBase
					disabled={disabled as boolean}
					errors={errors}
					onSubmit={onSubmit}
					setValues={setValues}
					values={values}
				/>
			)}

			{values.businessType === 'Aggregation' &&
				objectDefinition?.externalReferenceCode && (
					<AggregationFormBase
						creationLanguageId2={
							creationLanguageId2 as Liferay.Language.Locale
						}
						editingObjectField={editingObjectField}
						errors={errors}
						objectDefinitionExternalReferenceCode={
							objectDefinition.externalReferenceCode
						}
						objectFieldSettings={
							values.objectFieldSettings as ObjectFieldSetting[]
						}
						onAggregationFilterChange={onAggregationFilterChange}
						onObjectRelationshipChange={onObjectRelationshipChange}
						onSubmit={onSubmit}
						setValues={setValues}
						values={values}
					/>
				)}

			{values.businessType === 'Formula' && (
				<SingleSelect<FormulaOutput>
					error={errors.output}
					items={FORMULA_OUTPUT_OPTIONS}
					label={Liferay.Language.get('output')}
					onSelectionChange={(value) => {
						let newObjectFieldSettings: ObjectFieldSetting[] = [];

						if (values.objectFieldSettings) {
							newObjectFieldSettings =
								values.objectFieldSettings?.filter(
									(objectFieldSetting) =>
										objectFieldSetting.name !== 'output'
								) as ObjectFieldSetting[];
						}

						setValues({
							objectFieldSettings: [
								...newObjectFieldSettings,
								{
									name: 'output',
									value,
								},
							],
						});

						if (onSubmit) {
							onSubmit({
								...values,
								objectFieldSettings: [
									...newObjectFieldSettings,
									{
										name: 'output',
										value,
									},
								],
							});
						}

						setSelectedOutputValue(
							FORMULA_OUTPUT_OPTIONS.find(
								(formulaFieldOption) =>
									formulaFieldOption.value === value
							)?.value as string
						);
					}}
					required
					selectedKey={selectedOutputValue}
				/>
			)}

			{(values.businessType === 'Picklist' ||
				values.businessType === 'MultiselectPicklist') && (
				<div className="form-group lfr-objects__object-field-form-base-picklist-container">
					{reloadPicklistSingleSelect ? (
						<ClayLoadingIndicator
							displayType="secondary"
							size="sm"
						/>
					) : (
						<div
							className={classNames(
								'lfr-objects__object-field-form-base-picklist-single-select',
								{
									'lfr-objects__object-field-form-base-picklist-single-select-error':
										errors.listTypeDefinitionId,
								}
							)}
						>
							<SingleSelect
								className="lfr-objects__object-field-form-base-picklist-select-field"
								disabled={disabled}
								error={errors.listTypeDefinitionId}
								id="objectFieldFormBase"
								items={listTypeDefinitionsItems}
								label={Liferay.Language.get('picklist')}
								onSelectionChange={(value) => {
									const selectedListTypeDefinition =
										listTypeDefinitions.find(
											({externalReferenceCode}) =>
												externalReferenceCode === value
										);
									if (selectedListTypeDefinition) {
										setValues({
											listTypeDefinitionExternalReferenceCode:
												selectedListTypeDefinition.externalReferenceCode,
											listTypeDefinitionId:
												selectedListTypeDefinition.id,
											objectFieldSettings:
												removeFieldSettings(
													[
														'defaultValue',
														'stateFlow',
													],
													values
												),
										});

										if (onSubmit) {
											onSubmit({
												...values,
												listTypeDefinitionExternalReferenceCode:
													selectedListTypeDefinition.externalReferenceCode,
												listTypeDefinitionId:
													selectedListTypeDefinition.id,
												objectFieldSettings:
													removeFieldSettings(
														[
															'defaultValue',
															'stateFlow',
														],
														values
													),
											});
										}
									}
								}}
								selectedKey={
									selectedListTypeDefinitionExternalReferenceCode
								}
							/>

							<ClayButtonWithIcon
								aria-label={Liferay.Language.get(
									'refresh-list'
								)}
								className="lfr-objects__object-field-form-base-picklist-reload-button"
								data-tooltip-align="top"
								displayType="secondary"
								onClick={() =>
									updateListTypeDefinitions(
										setListTypeDefinitions
									)
								}
								symbol="reload"
								title={Liferay.Language.get('refresh-list')}
							/>
						</div>
					)}

					<ClayButton
						aria-labelledby={Liferay.Language.get(
							'manage-picklists'
						)}
						className="lfr-objects__object-field-form-base-picklist-manage-button"
						displayType="secondary"
						onClick={() => {
							window.open(listTypeDefinitionsURL, '_blank');
						}}
					>
						<span className="icon">
							{Liferay.Language.get('manage-picklists')}
						</span>

						<ClayIcon symbol="shortcut" />
					</ClayButton>
				</div>
			)}

			{values.businessType === 'DateTime' && (
				<TimeStorage
					disabled={disabled}
					objectFieldSettings={
						values.objectFieldSettings as ObjectFieldSetting[]
					}
					onSubmit={onSubmit}
					setValues={setValues}
					values={values}
				/>
			)}

			{children}

			<ClayForm.Group
				className="lfr-objects__object-field-form-base-mandatory-toggle"
				onMouseLeave={() => {
					setShowPopover(false);
				}}
			>
				{values.businessType !== 'Aggregation' &&
					values.businessType !== 'AutoIncrement' &&
					values.businessType !== 'Formula' && (
						<Toggle
							disabled={getMandatoryToggleDisabledState()}
							label={Liferay.Language.get('mandatory')}
							name="required"
							onToggle={(required) => {
								setValues({required});

								if (
									dbObjectFieldRequired &&
									editingObjectField &&
									modelBuilder &&
									objectDefinition?.status?.label ===
										'approved' &&
									setDbObjectFieldRequired
								) {
									setDbObjectFieldRequired(required);
								}

								if (onSubmit) {
									onSubmit({
										...values,
										required,
									});
								}
							}}
							toggled={values.required || values.state}
						/>
					)}

				{Liferay.FeatureFlags['LPD-34594'] &&
					objectRelationship?.edge && (
						<ClayPopover
							alignPosition="top"
							closeOnClickOutside={true}
							disableScroll
							header={Liferay.Language.get(
								'inheritance-relationships-fields'
							)}
							onMouseLeave={() => setShowPopover(false)}
							onMouseOver={() => setShowPopover(true)}
							onShowChange={setShowPopover}
							show={showPopover}
							trigger={
								<ClayIcon
									aria-label={Liferay.Language.get(
										'help-text'
									)}
									className="mandatory-tooltip-icon"
									onFocus={() => setShowPopover(true)}
									onMouseOver={() => setShowPopover(true)}
									symbol="question-circle-full"
								/>
							}
						>
							{Liferay.Language.get(
								'the-relationship-field-cannot-be-mandatory-when-inheritance-is-enabled'
							)}
							&nbsp;
							{learnResources && (
								<LearnResourcesContext.Provider
									value={learnResources}
								>
									<LearnMessage
										className="alert-link"
										resource="object-web"
										resourceKey="inheritance-relationships"
									/>
								</LearnResourcesContext.Provider>
							)}
						</ClayPopover>
					)}
			</ClayForm.Group>

			{values.businessType === 'Picklist' &&
				validListTypeDefinitionId && (
					<ClayForm.Group>
						<Toggle
							disabled={disabled || !objectDefinition?.modifiable}
							label={Liferay.Language.get('mark-as-state')}
							name="state"
							onToggle={(state) => {
								handleStateToggleChange(state);
							}}
							toggled={values.state}
						/>
					</ClayForm.Group>
				)}

			{(values.businessType === 'Text' ||
				values.businessType === 'Integer') && (
				<UniqueValues
					disabled={disabled}
					objectField={values}
					onSubmit={onSubmit}
					setValues={setValues}
				/>
			)}
		</>
	);
}
