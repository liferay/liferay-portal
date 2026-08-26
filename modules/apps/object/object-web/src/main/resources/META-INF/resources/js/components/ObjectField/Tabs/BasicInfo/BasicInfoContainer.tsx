/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {CountryInfo} from '@liferay/object-js-components-web';
import classNames from 'classnames';
import {
	ILearnResourceContext,
	InputLocalized,
} from 'frontend-js-components-web';
import React from 'react';

import {updateFieldSettings} from '../../../../utils/fieldSettings';
import ObjectFieldFormBase, {
	ObjectFieldErrors,
} from '../../ObjectFieldFormBase';
import {AttachmentProperties} from './AttachmentProperties';
import {AggregationFilters} from './BasicInfoTab';
import {MaxLengthProperties} from './MaxLengthProperties';
import {PhoneNumberProperties} from './PhoneNumberProperties';

import '../../EditObjectFieldContent.scss';

interface BasicInfoContainerProps {
	baseResourceURL: string;
	countries: CountryInfo[];
	creationLanguageId2?: Liferay.Language.Locale;
	dbObjectFieldRequired?: boolean;
	errors: ObjectFieldErrors;
	handleChange: React.ChangeEventHandler<HTMLInputElement>;
	hasDepotEntry?: boolean;
	learnResources: ILearnResourceContext;
	metadataObjectFieldNames?: string[];
	modelBuilder?: boolean;
	objectDefinition?: ObjectDefinition | ObjectDefinitionNodeData;
	objectFieldBusinessTypes: ObjectFieldBusinessType[];
	objectRelationshipId: number;
	onSubmit?: () => void;
	readOnly: boolean;
	setAggregationFilters: (values: AggregationFilters[]) => void;
	setDbObjectFieldRequired?: (value: boolean) => void;
	setObjectDefinitionExternalReferenceCode2: (value: string) => void;
	setValues: (values: Partial<ObjectField>) => void;
	values: Partial<ObjectField>;
}

export function BasicInfoContainer({
	baseResourceURL,
	countries,
	creationLanguageId2,
	dbObjectFieldRequired,
	errors,
	handleChange,
	hasDepotEntry,
	learnResources,
	metadataObjectFieldNames,
	modelBuilder = false,
	objectDefinition,
	objectFieldBusinessTypes,
	objectRelationshipId,
	onSubmit,
	readOnly,
	setAggregationFilters,
	setDbObjectFieldRequired,
	setObjectDefinitionExternalReferenceCode2,
	setValues,
	values,
}: BasicInfoContainerProps) {
	const disableFieldFormBase = !!(
		objectDefinition?.status?.label === 'approved' ||
		values.system ||
		values.relationshipType
	);

	const handleSettingsChange = ({name, value}: ObjectFieldSetting) =>
		setValues({
			objectFieldSettings: updateFieldSettings(
				values.objectFieldSettings,
				{name, value}
			),
		});

	return (
		<div
			className={classNames({
				'lfr-objects__edit-object-field-card-content': !modelBuilder,
				'lfr-objects__edit-object-field-model-builder-panel':
					modelBuilder,
			})}
		>
			<InputLocalized
				disabled={readOnly}
				error={errors.label}
				id="objectFieldLabelInput"
				label={Liferay.Language.get('label')}
				onBlur={(event) => {
					event.stopPropagation();

					if (onSubmit) {
						onSubmit();
					}
				}}
				onChange={(label) => setValues({label})}
				required
				translations={values.label as LocalizedValue<string>}
			/>

			<ObjectFieldFormBase
				baseResourceURL={baseResourceURL}
				creationLanguageId2={
					creationLanguageId2 as Liferay.Language.Locale
				}
				dbObjectFieldRequired={dbObjectFieldRequired}
				disabled={disableFieldFormBase}
				editingObjectField
				errors={errors}
				handleChange={handleChange}
				hasDepotEntry={hasDepotEntry}
				learnResources={learnResources}
				metadataObjectFieldNames={metadataObjectFieldNames}
				modelBuilder={modelBuilder}
				objectDefinition={objectDefinition}
				objectField={values}
				objectFieldBusinessTypesInfo={objectFieldBusinessTypes}
				objectRelationshipId={objectRelationshipId}
				onAggregationFilterChange={setAggregationFilters}
				onObjectRelationshipChange={
					setObjectDefinitionExternalReferenceCode2
				}
				onSubmit={onSubmit}
				readOnly={readOnly}
				setDbObjectFieldRequired={setDbObjectFieldRequired}
				setValues={setValues}
			>
				{values.businessType === 'Attachment' && (
					<AttachmentProperties
						errors={errors}
						modelBuilder={modelBuilder}
						objectFieldSettings={
							values.objectFieldSettings as ObjectFieldSetting[]
						}
						onSettingsChange={handleSettingsChange}
						onSubmit={onSubmit}
						values={values}
					/>
				)}

				{(values.businessType === 'Encrypted' ||
					values.businessType === 'LongText' ||
					values.businessType === 'Text') && (
					<MaxLengthProperties
						disabled={values.system}
						errors={errors}
						objectField={values}
						objectFieldSettings={
							values.objectFieldSettings as ObjectFieldSetting[]
						}
						onSettingsChange={handleSettingsChange}
						onSubmit={onSubmit}
						setValues={setValues}
					/>
				)}
			</ObjectFieldFormBase>

			{values.businessType === 'PhoneNumber' && (
				<PhoneNumberProperties
					countries={countries}
					objectFieldSettings={
						values.objectFieldSettings as ObjectFieldSetting[]
					}
					onSubmit={onSubmit}
					setValues={setValues}
					values={values}
				/>
			)}
		</div>
	);
}
