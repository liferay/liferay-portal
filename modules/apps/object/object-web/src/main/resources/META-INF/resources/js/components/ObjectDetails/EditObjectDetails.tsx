/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayPanel from '@clayui/panel';
import {API, openToast, stringUtils} from '@liferay/object-js-components-web';
import React, {useEffect, useState} from 'react';

import ObjectManagementToolbar from '../ObjectManagementToolbar';
import {AccountRestrictionContainer} from './AccountRestrictionContainer';
import {ConfigurationContainer} from './ConfigurationContainer';
import {EntryDisplayContainer} from './EntryDisplayContainer';
import {ExternalDataSourceContainer} from './ExternalDataSourceContainer';
import {ObjectDataContainer} from './ObjectDataContainer';
import {ScopeContainer} from './ScopeContainer';
import Sheet from './Sheet';
import {TranslationsContainer} from './TranslationsContainer';
import {useObjectDetailsForm} from './useObjectDetailsForm';

import './ObjectDetails.scss';
import {getObjectDefinitionInfo} from '../ViewObjectDefinitions/objectDefinitionUtil';
import WorkflowContainer from '../WorkflowContainer';
import {SeoContainer} from './SeoContainer';

export type Scope = {
	items: LabelValueObject[];
	label: string;
};
interface EditObjectDetailsProps {
	backURL: string;
	baseResourceURL: string;
	companies: Scope[];
	dbTableName: string;
	hasPublishObjectPermission: boolean;
	hasUpdateObjectDefinitionPermission: boolean;
	isApproved: boolean;
	isRootDescendantNode: boolean;
	isRootNode: boolean;
	label: LocalizedValue<string>;
	nonRelationshipObjectFieldsInfo: {
		label: LocalizedValue<string>;
		name: string;
	}[];
	objectDefinitionExternalReferenceCode: string;
	objectDefinitionId: number;
	pluralLabel: LocalizedValue<string>;
	portletNamespace: string;
	shortName: string;
	sites: Scope[];
	storageTypes: LabelValueObject[];
}

function setAccountRelationshipFieldMandatory(
	values: Partial<ObjectDefinition>
) {
	const {objectFields} = values;

	const newObjectFields = objectFields?.map((field) => {
		if (field.name === values.accountEntryRestrictedObjectFieldName) {
			return {
				...field,
				required: true,
			};
		}

		return field;
	});

	return {
		...values,
		objectFields: newObjectFields,
	};
}

export default function EditObjectDetails({
	backURL,
	baseResourceURL,
	companies,
	dbTableName,
	hasPublishObjectPermission,
	hasUpdateObjectDefinitionPermission,
	isApproved,
	isRootDescendantNode,
	isRootNode,
	label,
	nonRelationshipObjectFieldsInfo,
	objectDefinitionExternalReferenceCode,
	objectDefinitionId,
	pluralLabel,
	portletNamespace,
	shortName,
	sites,
	storageTypes,
}: EditObjectDetailsProps) {
	const [objectFields, setObjectFields] = useState<ObjectField[]>([]);
	const [workflowLabel, setWorkflowLabel] = useState('');

	const {errors, handleChange, handleValidate, setValues, values} =
		useObjectDetailsForm({
			initialValues: {
				defaultLanguageId: 'en_US',
				externalReferenceCode: objectDefinitionExternalReferenceCode,
				id: objectDefinitionId,
				label,
				name: shortName,
				pluralLabel,
			},
			onSubmit: () => {},
		});

	const onSubmit = async (draft: boolean) => {
		const validationErrors = handleValidate();

		if (!Object.keys(validationErrors).length) {
			let objectDefinition = values;

			if (values.accountEntryRestricted) {
				objectDefinition = setAccountRelationshipFieldMandatory(values);
			}

			const saveResponse =
				await API.putObjectDefinitionByExternalReferenceCode(
					objectDefinition
				);

			if (!saveResponse.ok) {
				const {title} = (await saveResponse.json()) as {
					status: string;
					title: string;
				};

				openToast({
					message: title,
					type: 'danger',
				});

				return;
			}

			if (!draft) {
				const publishResponse: any =
					await API.postObjectDefinitionPublish(values.id as number);

				if (!publishResponse.ok) {
					const {title} = (await publishResponse.json()) as {
						status: string;
						title: string;
					};

					openToast({
						message: title,
						type: 'danger',
					});

					return;
				}

				openToast({
					message: Liferay.Language.get(
						'the-object-was-published-successfully'
					),
					type: 'success',
				});

				setTimeout(() => window.location.reload(), 1000);

				return;
			}

			openToast({
				message: Liferay.Language.get(
					'the-object-was-saved-successfully'
				),
				type: 'success',
			});

			setTimeout(() => window.location.reload(), 1000);
		}
	};

	useEffect(() => {
		const makeFetch = async () => {
			const objectFieldsResponse =
				await API.getObjectDefinitionByExternalReferenceCodeObjectFields(
					objectDefinitionExternalReferenceCode
				);
			const objectDefinitionResponse =
				await API.getObjectDefinitionByExternalReferenceCode(
					objectDefinitionExternalReferenceCode
				);

			const objectDefinitionInfo = await getObjectDefinitionInfo({
				baseResourceURL,
				objectDefinitionId,
			});

			setValues(objectDefinitionResponse);
			setObjectFields(objectFieldsResponse);
			setWorkflowLabel(objectDefinitionInfo.workflowDefinitionTitle);
		};

		makeFetch();

		// eslint-disable-next-line react-hooks/exhaustive-deps
	}, [objectDefinitionId]);

	return (
		<>
			<div className="lfr-objects__object-definition-details-management-toolbar">
				<ObjectManagementToolbar
					backURL={backURL}
					hasPublishObjectPermission={hasPublishObjectPermission}
					hasUpdateObjectDefinitionPermission={
						hasUpdateObjectDefinitionPermission
					}
					isApproved={isApproved}
					isRootDescendantNode={isRootDescendantNode}
					isRootNode={isRootNode}
					label={stringUtils.getLocalizableLabel({
						fallbackLabel: values.name,
						fallbackLanguageId:
							values.defaultLanguageId as Liferay.Language.Locale,
						labels: values.label,
					})}
					objectDefinitionExternalReferenceCode={
						objectDefinitionExternalReferenceCode
					}
					objectDefinitionId={objectDefinitionId}
					onSubmit={onSubmit}
					portletNamespace={portletNamespace}
					screenNavigationCategoryKey="details"
					system={values.system as boolean}
				/>
			</div>

			<div className="lfr-objects__object-definition-details">
				<Sheet title={Liferay.Language.get('basic-information')}>
					<ClayPanel
						displayTitle={Liferay.Language.get(
							'object-definition-data'
						)}
						displayType="unstyled"
					>
						<ClayPanel.Body>
							<ObjectDataContainer
								dbTableName={dbTableName}
								errors={errors}
								handleChange={handleChange}
								hasUpdateObjectDefinitionPermission={
									hasUpdateObjectDefinitionPermission
								}
								isApproved={isApproved}
								setValues={setValues}
								values={values}
							/>
						</ClayPanel.Body>
					</ClayPanel>

					<ClayPanel
						collapsable
						defaultExpanded
						displayTitle={Liferay.Language.get('entry-display')}
						displayType="unstyled"
					>
						<ClayPanel.Body>
							<EntryDisplayContainer
								errors={errors}
								nonRelationshipObjectFieldsInfo={
									nonRelationshipObjectFieldsInfo
								}
								objectFields={objectFields}
								setValues={setValues}
								values={values}
							/>
						</ClayPanel.Body>
					</ClayPanel>

					{Liferay.FeatureFlags['LPS-135430'] && (
						<ClayPanel
							collapsable
							defaultExpanded
							displayTitle={
								<div className="lfr__object-web-edit-object-details-external-data-source-panel">
									<span className="panel-title">
										{Liferay.Language.get(
											'external-data-source'
										)}
									</span>
								</div>
							}
							displayType="unstyled"
						>
							<ClayPanel.Body>
								<div className="lfr__object-web-edit-object-details-external-data-source-container">
									<ExternalDataSourceContainer
										errors={errors}
										storageTypes={storageTypes}
										values={values}
									/>
								</div>
							</ClayPanel.Body>
						</ClayPanel>
					)}

					<ClayPanel
						collapsable
						defaultExpanded
						displayTitle={Liferay.Language.get('scope')}
						displayType="unstyled"
					>
						<ClayPanel.Body>
							<ScopeContainer
								companies={companies}
								errors={errors}
								hasUpdateObjectDefinitionPermission={
									hasUpdateObjectDefinitionPermission
								}
								isApproved={isApproved}
								isRootDescendantNode={isRootDescendantNode}
								setValues={setValues}
								sites={sites}
								values={values}
							/>
						</ClayPanel.Body>
					</ClayPanel>

					{values.modifiable && (
						<ClayPanel
							collapsable
							defaultExpanded
							displayTitle={Liferay.Language.get(
								'account-restriction'
							)}
							displayType="unstyled"
						>
							<ClayPanel.Body>
								<AccountRestrictionContainer
									errors={errors}
									isApproved={isApproved}
									isRootDescendantNode={isRootDescendantNode}
									objectFields={objectFields}
									setValues={setValues}
									values={values}
								/>
							</ClayPanel.Body>
						</ClayPanel>
					)}

					{Liferay.FeatureFlags['LPD-34594'] &&
						values.scope === 'company' &&
						isApproved && (
							<ClayPanel
								collapsable
								defaultExpanded
								displayTitle={Liferay.Language.get('workflow')}
								displayType="unstyled"
							>
								<ClayPanel.Body>
									<WorkflowContainer
										baseResourceURL={baseResourceURL}
										className="lfr-objects__object-definition-details-section"
										objectDefinitionId={objectDefinitionId}
										workflowLabel={workflowLabel}
									/>
								</ClayPanel.Body>
							</ClayPanel>
						)}

					<ClayPanel
						collapsable
						defaultExpanded
						displayTitle={Liferay.Language.get('configuration')}
						displayType="unstyled"
					>
						<ClayPanel.Body>
							<ConfigurationContainer
								hasUpdateObjectDefinitionPermission={
									hasUpdateObjectDefinitionPermission
								}
								isRootDescendantNode={isRootDescendantNode}
								setValues={setValues}
								values={values}
							/>
						</ClayPanel.Body>
					</ClayPanel>

					<ClayPanel
						collapsable
						defaultExpanded
						displayTitle={Liferay.Language.get('translations')}
						displayType="unstyled"
					>
						<ClayPanel.Body>
							<TranslationsContainer
								setValues={setValues}
								values={values}
							/>
						</ClayPanel.Body>
					</ClayPanel>

					{Liferay.FeatureFlags['LPD-21926'] && (
						<ClayPanel
							collapsable
							defaultExpanded
							displayTitle={Liferay.Language.get('seo')}
							displayType="unstyled"
						>
							<ClayPanel.Body>
								<SeoContainer
									setValues={setValues}
									values={values}
								/>
							</ClayPanel.Body>
						</ClayPanel>
					)}
				</Sheet>
			</div>
		</>
	);
}
