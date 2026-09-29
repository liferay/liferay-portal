/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

/* eslint-disable react/jsx-curly-brace-presence */

import ClayAlert from '@clayui/alert';
import ClayButton from '@clayui/button';
import ClayForm from '@clayui/form';
import {
	ExpressionBuilder,
	SidebarCategory,
	Toggle,
} from '@liferay/object-js-components-web';
import classNames from 'classnames';
import {
	ILearnResourceContext,
	LearnMessage,
	LearnResourcesContext,
} from 'frontend-js-components-web';
import React, {useEffect, useState} from 'react';

import {
	getDefaultValueFieldSettings,
	getUpdatedDefaultValueFieldSettings,
	getUpdatedDefaultValueType,
} from '../../../../utils/defaultValues';
import {removeFieldSettings} from '../../../../utils/fieldSettings';
import BooleanDefaultValueSelect from '../../DefaultValueFields/BooleanDefaultValueSelect';
import DateDefaultValueInput from '../../DefaultValueFields/DateDefaultValueInput';
import ListTypeDefaultValueSelect from '../../DefaultValueFields/ListTypeDefaultValueSelect';
import LocationDefaultValueInput from '../../DefaultValueFields/LocationDefaultValueInput';
import NumericDefaultValueInput from '../../DefaultValueFields/NumericDefaultValueInput';
import RichTextDefaultValue from '../../DefaultValueFields/RichTextDefaultValue';
import TextDefaultValueInput from '../../DefaultValueFields/TextDefaultValueInput';
import {ObjectFieldErrors} from '../../ObjectFieldFormBase';
interface DefaultValueContainerProps {
	ckEditor5Config?: object;
	creationLanguageId: Liferay.Language.Locale;
	decimalSeparator: string;
	defaultValueSidebarElements: SidebarCategory[];
	errors: ObjectFieldErrors;
	googleMapsAPIKey?: string;
	learnResources: ILearnResourceContext;
	mapProviderKey?: string;
	modelBuilder?: boolean;
	onSubmit?: (values?: Partial<ObjectField>) => void;
	setValues: (value: Partial<ObjectField>) => void;
	values: Partial<ObjectField>;
}

export interface InputAsValueFieldComponentProps {
	ckEditor5Config?: object;
	creationLanguageId: Liferay.Language.Locale;
	dataType?: string;
	decimalSeparator?: string;
	defaultValue?: ObjectFieldSettingValue;
	error?: string;
	googleMapsAPIKey?: string;
	id?: string;
	label: string;
	mapProviderKey?: string;
	onSubmit?: (values?: Partial<ObjectField>) => void;
	placeholder?: string;
	required?: boolean;
	setValues: (values: Partial<ObjectField>) => void;
	values: Partial<ObjectField>;
}

type InputAsValueFieldComponents = {
	[key in ObjectFieldBusinessTypeName]: React.FC<InputAsValueFieldComponentProps>;
};

const InputAsValueFieldComponents: Partial<InputAsValueFieldComponents> = {
	Boolean: BooleanDefaultValueSelect,
	Date: DateDefaultValueInput,
	DateTime: DateDefaultValueInput,
	Decimal: NumericDefaultValueInput,
	EmailAddress: TextDefaultValueInput,
	Integer: NumericDefaultValueInput,
	Location: LocationDefaultValueInput,
	LongInteger: NumericDefaultValueInput,
	LongText: TextDefaultValueInput,
	PhoneNumber: TextDefaultValueInput,
	Picklist: ListTypeDefaultValueSelect,
	PrecisionDecimal: NumericDefaultValueInput,
	RichText: RichTextDefaultValue,
	Text: TextDefaultValueInput,
};

export function DefaultValueContainer({
	ckEditor5Config,
	creationLanguageId,
	decimalSeparator,
	defaultValueSidebarElements,
	errors,
	googleMapsAPIKey,
	learnResources,
	mapProviderKey,
	modelBuilder = false,
	onSubmit,
	setValues,
	values,
}: DefaultValueContainerProps) {
	const {defaultValue, defaultValueType} =
		getDefaultValueFieldSettings(values);

	const [defaultValueToggleEnabled, setDefaultValueToggleEnabled] = useState(
		!!defaultValueType && !!defaultValue
	);

	const [defaultValueTypeSelection, setDefaultValueTypeSelection] = useState(
		defaultValueType || 'inputAsValue'
	);

	const dataType =
		values.businessType === 'Decimal' ||
		values.businessType === 'PrecisionDecimal'
			? 'double'
			: '';

	useEffect(() => {
		if (values.state) {
			setDefaultValueToggleEnabled(true);
			setDefaultValueTypeSelection('inputAsValue');
		}
	}, [values]);

	const handleToggle = (toggled: boolean) => {
		if (!toggled) {
			setValues({
				objectFieldSettings: removeFieldSettings(
					['defaultValueType', 'defaultValue'],
					values
				),
			});

			if (onSubmit) {
				onSubmit({
					...values,
					objectFieldSettings: removeFieldSettings(
						['defaultValueType', 'defaultValue'],
						values
					),
				});
			}
		}
		else {
			setValues({
				objectFieldSettings: getUpdatedDefaultValueType(
					values,
					'inputAsValue'
				),
			});
		}
		setDefaultValueToggleEnabled(toggled);
	};

	const InputAsValueFieldComponent =
		InputAsValueFieldComponents[
			values.businessType as keyof InputAsValueFieldComponents
		];

	return (
		<div
			className={classNames({
				'lfr-objects__edit-object-field-card-content': !modelBuilder,
				'lfr-objects__edit-object-field-model-builder-panel':
					modelBuilder,
			})}
		>
			{!values.state && values.businessType !== 'Location' && (
				<ClayAlert displayType="info" title="Info">
					{Liferay.Language.get(
						'enter-a-value-or-use-expressions-to-set-default-values'
					)}
					&nbsp;
					<LearnResourcesContext.Provider value={learnResources}>
						<LearnMessage
							className="alert-link"
							resource="object-web"
							resourceKey="expression-builder-validations-reference"
						/>
					</LearnResourcesContext.Provider>
				</ClayAlert>
			)}

			{!values.state && (
				<ClayForm.Group
					className={classNames({
						'lfr-objects__object-field-default-value-disabled':
							!defaultValueToggleEnabled,
						'lfr-objects__object-field-default-value-enabled':
							defaultValueToggleEnabled,
					})}
				>
					<Toggle
						label={Liferay.Language.get('use-default-value')}
						onToggle={(toggled) => {
							handleToggle(toggled);
						}}
						toggled={defaultValueToggleEnabled}
					/>
				</ClayForm.Group>
			)}

			{defaultValueSidebarElements &&
				defaultValueToggleEnabled &&
				!values.state &&
				values.businessType !== 'Location' && (
					<ClayButton.Group>
						<ClayButton
							className={classNames({
								active:
									defaultValueTypeSelection ===
									'inputAsValue',
							})}
							displayType="secondary"
							onClick={() => {
								setDefaultValueTypeSelection('inputAsValue');
								setValues({
									objectFieldSettings:
										getUpdatedDefaultValueType(
											values,
											'inputAsValue'
										),
								});
							}}
							size="sm"
						>
							{Liferay.Language.get('input-as-value')}
						</ClayButton>

						<ClayButton
							className={classNames({
								active:
									defaultValueTypeSelection ===
									'expressionBuilder',
							})}
							displayType="secondary"
							onClick={() => {
								setDefaultValueTypeSelection(
									'expressionBuilder'
								);
								setValues({
									objectFieldSettings:
										getUpdatedDefaultValueType(
											values,
											'expressionBuilder'
										),
								});
							}}
							size="sm"
						>
							{Liferay.Language.get('expression-builder')}
						</ClayButton>
					</ClayButton.Group>
				)}

			{defaultValueToggleEnabled &&
				defaultValueTypeSelection === 'inputAsValue' &&
				InputAsValueFieldComponent && (
					<InputAsValueFieldComponent
						ckEditor5Config={ckEditor5Config}
						creationLanguageId={creationLanguageId}
						dataType={dataType}
						decimalSeparator={decimalSeparator}
						defaultValue={
							defaultValueType === 'inputAsValue' && defaultValue
						}
						error={errors.defaultValue}
						googleMapsAPIKey={googleMapsAPIKey}
						id="default_value_container_input"
						label={
							!values.state
								? Liferay.Language.get('default-value')
								: Liferay.Language.get('input-as-value')
						}
						mapProviderKey={mapProviderKey}
						onSubmit={onSubmit}
						required
						setValues={setValues}
						values={values}
					/>
				)}

			{defaultValueToggleEnabled &&
				defaultValueTypeSelection === 'expressionBuilder' && (
					<ExpressionBuilder
						error={errors.defaultValue}
						feedbackMessage={Liferay.Language.get(
							'click-on-the-button-to-expand-the-expression-input-area'
						)}
						label={Liferay.Language.get('default-value')}
						onBlur={(event) => {
							event.stopPropagation();

							if (onSubmit) {
								onSubmit();
							}
						}}
						onChange={({target: {value}}) => {
							setValues({
								objectFieldSettings:
									getUpdatedDefaultValueFieldSettings(
										values,
										value,
										'expressionBuilder'
									),
							});
						}}
						onOpenModal={() => {
							const parentWindow = Liferay.Util.getOpener();

							parentWindow.Liferay.fire(
								'openExpressionBuilderModal',
								{
									eventSidebarElements:
										defaultValueSidebarElements,
									onSave: (script: string) => {
										setValues({
											objectFieldSettings:
												getUpdatedDefaultValueFieldSettings(
													values,
													script,
													'expressionBuilder'
												),
										});

										if (onSubmit) {
											onSubmit({
												...values,
												objectFieldSettings:
													getUpdatedDefaultValueFieldSettings(
														values,
														script,
														'expressionBuilder'
													),
											});
										}
									},
									placeholder: `<#-- ${Liferay.Language.get(
										'create-a-condition-to-set-the-default-value'
									)} -->`,
									required: false,
									source:
										defaultValueType ===
											'expressionBuilder' && defaultValue
											? defaultValue
											: '',
									validateExpressionURL: '',
								}
							);
						}}
						placeholder={Liferay.Language.get(
							'create-an-expression'
						)}
						required
						value={
							defaultValueType === 'expressionBuilder'
								? (defaultValue as string)
								: ''
						}
					/>
				)}
		</div>
	);
}
