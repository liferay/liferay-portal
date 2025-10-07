/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayAlert from '@clayui/alert';
import {Option, Text} from '@clayui/core';
import {
	Card,
	SidebarCategory,
	SingleSelect,
	invalidateRequired,
} from '@liferay/object-js-components-web';
import {InputLocalized} from 'frontend-js-components-web';
import React, {useEffect, useMemo, useState} from 'react';

import {defaultLanguageId} from '../../../utils/constants';
import {DisabledGroovyScriptAlert} from '../../DisabledGroovyScriptAlert';
import {ActionError} from '../ObjectActionContainer';
import {ActionContainer} from './ActionContainer/ActionContainer';
import {ConditionContainer} from './ConditionContainer';

import './ActionBuilder.scss';

interface ActionBuilderProps {
	disableGroovyAction: boolean;
	errors: ActionError;
	isApproved: boolean;
	objectActionCodeEditorElements: SidebarCategory[];
	objectActionExecutors: ObjectActionTriggerExecutorItem[];
	objectActionTriggers: ObjectActionTriggerExecutorItem[];
	objectDefinitionExternalReferenceCode: string;
	objectDefinitionId: number;
	objectDefinitionsRelationshipsURL: string;
	objectFields: ObjectField[];
	scriptManagementConfigurationPortletURL: string;
	setValues: (values: Partial<ObjectAction>) => void;
	systemObject: boolean;
	validateExpressionURL: string;
	values: Partial<ObjectAction>;
}
export interface WarningStates {
	mandatoryRelationships: boolean;
	requiredFields: boolean;
}

const triggerKeys = [
	'liferay/commerce_order_status',
	'liferay/commerce_payment_status',
	'liferay/commerce_shipment_status',
	'onAfterAdd',
	'onAfterAttachmentDownload',
	'onAfterDelete',
	'onAfterLogin',
	'onAfterRootUpdate',
	'onAfterUpdate',
];

export default function ActionBuilder({
	disableGroovyAction,
	errors,
	isApproved,
	objectActionCodeEditorElements,
	objectActionExecutors,
	objectActionTriggers,
	objectDefinitionExternalReferenceCode,
	objectDefinitionId,
	objectDefinitionsRelationshipsURL,
	objectFields,
	scriptManagementConfigurationPortletURL,
	setValues,
	systemObject,
	validateExpressionURL,
	values,
}: ActionBuilderProps) {
	const [newObjectActionExecutors, setNewObjectActionExecutors] = useState<
		ObjectActionTriggerExecutorItem[]
	>(objectActionExecutors);

	const [infoAlert, setInfoAlert] = useState(true);

	const [warningAlerts, setWarningAlerts] = useState<WarningStates>({
		mandatoryRelationships: false,
		requiredFields: false,
	});

	const [currentObjectDefinitionFields, setCurrentObjectDefinitionFields] =
		useState<ObjectField[]>([]);

	const [errorAlert, setErrorAlert] = useState(false);

	const objectFieldsMap = useMemo(() => {
		const fields = new Map<string, ObjectField>();

		currentObjectDefinitionFields.forEach((field) => {
			fields.set(field.name, field);
		});

		return fields;
	}, [currentObjectDefinitionFields]);

	const showConditionContainer = values.objectActionTriggerKey
		? triggerKeys.includes(values.objectActionTriggerKey)
		: true;

	useEffect(() => {
		const predefinedValues = values.parameters?.predefinedValues;

		const requiredFields = predefinedValues
			? predefinedValues.filter(
					({name}) => objectFieldsMap.get(name)?.required
				)
			: [];

		const hasEmptyValues = requiredFields?.some((item) =>
			invalidateRequired(item.value)
		);

		setWarningAlerts((previousWarnings) => ({
			...previousWarnings,
			requiredFields: hasEmptyValues,
		}));
	}, [
		values.parameters?.predefinedValues,
		objectFieldsMap,
		setWarningAlerts,
	]);

	const closeWarningAlert = (warning: string) => {
		setWarningAlerts((previousWarnings) => ({
			...previousWarnings,
			[warning]: false,
		}));
	};

	const hasLocalizedField = useMemo(() => {
		return objectFields.some((field) => field.localized);
	}, [objectFields]);

	useEffect(() => {
		const predefinedValues = values.parameters?.predefinedValues;

		const requiredFields = predefinedValues
			? predefinedValues.filter(
					({name}) => objectFieldsMap.get(name)?.required
				)
			: [];

		const hasEmptyValues = requiredFields?.some((item) =>
			invalidateRequired(item.value)
		);

		setWarningAlerts((previousWarnings) => ({
			...previousWarnings,
			requiredFields: hasEmptyValues,
		}));
	}, [
		values.parameters?.predefinedValues,
		objectFieldsMap,
		setWarningAlerts,
	]);

	useEffect(() => {
		if (values.objectActionTriggerKey === 'onAfterDelete') {
			newObjectActionExecutors.map((action) => {
				if (action.value === 'update-object-entry') {
					action.disabled = true;
					action.popover = {
						body:
							Liferay.Language.get(
								'it-is-not-possible-to-create-an-update-action-with-an-on-after-delete-trigger'
							) +
							' ' +
							Liferay.Language.get(
								'please-change-the-action-trigger'
							),
						header: Liferay.Language.get('action-not-allowed'),
					};
				}
			});

			if (values.objectActionExecutorKey === 'update-object-entry') {
				setErrorAlert(true);
			}

			setNewObjectActionExecutors(newObjectActionExecutors);
		}
		else if (
			values.objectActionTriggerKey === 'onAfterAdd' ||
			values.objectActionTriggerKey === 'onAfterUpdate'
		) {
			newObjectActionExecutors.map((action) => {
				if (action.value === 'update-object-entry') {
					delete action.disabled;
					delete action.popover;
				}
			});

			if (values.objectActionExecutorKey === 'update-object-entry') {
				setErrorAlert(false);
			}

			setNewObjectActionExecutors(newObjectActionExecutors);
		}

		// eslint-disable-next-line react-hooks/exhaustive-deps
	}, [values.objectActionTriggerKey]);

	return (
		<>
			{disableGroovyAction && (
				<DisabledGroovyScriptAlert
					scriptManagementConfigurationPortletURL={
						scriptManagementConfigurationPortletURL
					}
				/>
			)}

			{infoAlert && (
				<ClayAlert
					className="lfr-objects__side-panel-content-container"
					displayType="info"
					onClose={() => setInfoAlert(false)}
					title={`${Liferay.Language.get('info')}:`}
				>
					{Liferay.Language.get(
						'create-conditions-and-predefined-values-using-expressions'
					) + ' '}

					<a
						className="alert-link"
						href="https://learn.liferay.com/dxp/latest/en/building-applications/objects/creating-and-managing-objects/expression-builder-validations-reference.html"
						target="_blank"
					>
						{Liferay.Language.get('click-here-for-documentation')}
					</a>
				</ClayAlert>
			)}

			{Liferay.FeatureFlags['LPD-32050'] && hasLocalizedField && (
				<ClayAlert
					className="lfr-objects__side-panel-content-container"
					displayType="info"
					onClose={() => setInfoAlert(false)}
					title={`${Liferay.Language.get('info')}:`}
				>
					{`${Liferay.Language.get(
						'this-object-includes-translatable-fields'
					)} ${Liferay.Language.get(
						'actions-always-use-the-object-entrys-default-language'
					)}`}
				</ClayAlert>
			)}

			{errorAlert && (
				<ClayAlert
					className="lfr-objects__side-panel-content-container"
					displayType="danger"
					onClose={() => setErrorAlert(false)}
					title={`${Liferay.Language.get('error')}:`}
				>
					{Liferay.Language.get(
						'it-is-not-possible-to-create-an-update-action-with-an-on-after-delete-trigger'
					)}
				</ClayAlert>
			)}

			<Card title={Liferay.Language.get('trigger')}>
				<Card
					title={Liferay.Language.get('when[object]')}
					viewMode="inline"
				>
					<SingleSelect
						disabled={
							isApproved || values.system || disableGroovyAction
						}
						error={errors.objectActionTriggerKey}
						items={objectActionTriggers}
						onSelectionChange={(value) =>
							setValues({
								conditionExpression: undefined,
								objectActionTriggerKey: value as string,
							})
						}
						placeholder={Liferay.Language.get('choose-a-trigger')}
						selectedKey={values.objectActionTriggerKey}
					>
						{(item) => (
							<Option key={item.value} textValue={item.label}>
								<div className="lfr-objects__object-action-builder-when-option">
									<Text size={3} weight="semi-bold">
										{item.label}
									</Text>

									<Text
										aria-hidden
										color="secondary"
										size={2}
									>
										{item.description}
									</Text>
								</div>
							</Option>
						)}
					</SingleSelect>
				</Card>
			</Card>

			{showConditionContainer && (
				<ConditionContainer
					disabled={disableGroovyAction}
					errors={errors}
					setValues={setValues}
					validateExpressionURL={validateExpressionURL}
					values={values}
				/>
			)}

			{warningAlerts.requiredFields && (
				<ClayAlert
					className="lfr-objects__side-panel-content-container"
					displayType="warning"
					onClose={() => closeWarningAlert('requiredFields')}
					title={`${Liferay.Language.get('warning')}:`}
				>
					{Liferay.Language.get(
						'required-fields-must-have-predefined-values'
					)}
				</ClayAlert>
			)}

			{warningAlerts.mandatoryRelationships && (
				<ClayAlert
					className="lfr-objects__side-panel-content-container"
					displayType="warning"
					onClose={() => closeWarningAlert('mandatoryRelationships')}
					title={`${Liferay.Language.get('warning')}:`}
				>
					{Liferay.Language.get(
						'the-selected-object-definition-has-mandatory-relationship-fields'
					)}
				</ClayAlert>
			)}
			<ActionContainer
				currentObjectDefinitionFields={currentObjectDefinitionFields}
				disableGroovyAction={disableGroovyAction}
				errors={errors}
				newObjectActionExecutors={newObjectActionExecutors}
				objectActionCodeEditorElements={objectActionCodeEditorElements}
				objectActionExecutors={objectActionExecutors}
				objectDefinitionExternalReferenceCode={
					objectDefinitionExternalReferenceCode
				}
				objectDefinitionId={objectDefinitionId}
				objectDefinitionsRelationshipsURL={
					objectDefinitionsRelationshipsURL
				}
				objectFieldsMap={objectFieldsMap}
				setCurrentObjectDefinitionFields={
					setCurrentObjectDefinitionFields
				}
				setValues={setValues}
				setWarningAlerts={setWarningAlerts}
				systemObject={systemObject}
				validateExpressionURL={validateExpressionURL}
				values={values}
			/>

			{values.objectActionTriggerKey === 'standalone' && (
				<Card title={Liferay.Language.get('error-message')}>
					<InputLocalized
						disabled={values.system}
						error={errors.errorMessage}
						label={Liferay.Language.get('message')}
						name="label"
						onChange={(value) =>
							setValues({
								errorMessage: value,
							})
						}
						required
						translations={
							values.errorMessage ?? {[defaultLanguageId]: ''}
						}
					/>
				</Card>
			)}
		</>
	);
}
