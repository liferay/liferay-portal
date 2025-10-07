/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Input, SidebarCategory} from '@liferay/object-js-components-web';
import classNames from 'classnames';
import {ILearnResourceContext} from 'frontend-js-components-web';
import React, {ElementType, useState} from 'react';

import {AutoIncrementFormBase} from '../../AutoIncrementFormBase';
import {ObjectFieldErrors} from '../../ObjectFieldFormBase';
import {AggregationFilterContainer} from './AggregationFilterContainer';
import {BasicInfoContainer} from './BasicInfoContainer';
import {FormulaContainer} from './FormulaContainer';
import {SearchableContainer} from './SearchableContainer';
import {TranslationOptionsContainer} from './TranslationOptionsContainer';

export interface AggregationFilters {
	defaultSort?: boolean;
	fieldLabel?: string;
	filterBy?: string;
	filterType?: string;
	label?: LocalizedValue<string>;
	objectFieldBusinessType?: string;
	objectFieldName: string;
	priority?: number;
	sortOrder?: string;
	type?: string;
	value?: string;
	valueList?: LabelValueObject[];
}

interface BasicInfoTabProps {
	baseResourceURL: string;
	containerWrapper: ElementType;
	dbObjectFieldRequired?: boolean;
	errors: ObjectFieldErrors;
	filterOperators: TFilterOperators;
	handleChange: React.ChangeEventHandler<HTMLInputElement>;
	learnResources: ILearnResourceContext;
	modelBuilder?: boolean;
	objectDefinition?: ObjectDefinition | ObjectDefinitionNodeData;
	objectFieldBusinessTypes: ObjectFieldBusinessType[];
	objectRelationshipId: number;
	onSubmit?: (editedObjectField?: Partial<ObjectField>) => void;
	readOnly: boolean;
	setDbObjectFieldRequired?: (value: boolean) => void;
	setValues: (values: Partial<ObjectField>) => void;
	sidebarElements: SidebarCategory[];
	values: Partial<ObjectField>;
	workflowStatuses: LabelValueObject[];
}

export function BasicInfoTab({
	baseResourceURL,
	containerWrapper: ContainerWrapper,
	dbObjectFieldRequired,
	errors,
	filterOperators,
	handleChange,
	learnResources,
	modelBuilder = false,
	objectDefinition,
	objectFieldBusinessTypes,
	objectRelationshipId,
	onSubmit,
	readOnly,
	setDbObjectFieldRequired,
	setValues,
	sidebarElements,
	values,
	workflowStatuses,
}: BasicInfoTabProps) {
	const [aggregationFilters, setAggregationFilters] = useState<
		AggregationFilters[]
	>([]);

	const [creationLanguageId2, setCreationLanguageId2] =
		useState<Liferay.Language.Locale>();

	const [
		objectDefinitionExternalReferenceCode2,
		setObjectDefinitionExternalReferenceCode2,
	] = useState<string>();

	const isApproved = objectDefinition?.status!.label === 'approved';

	return (
		<>
			<ContainerWrapper
				collapsable
				defaultExpanded
				displayTitle={Liferay.Language.get('basic-info')}
				displayType="unstyled"
				title={Liferay.Language.get('basic-info')}
			>
				<BasicInfoContainer
					baseResourceURL={baseResourceURL}
					creationLanguageId2={creationLanguageId2}
					dbObjectFieldRequired={dbObjectFieldRequired}
					errors={errors}
					handleChange={handleChange}
					learnResources={learnResources}
					modelBuilder={modelBuilder}
					objectDefinition={objectDefinition}
					objectFieldBusinessTypes={objectFieldBusinessTypes}
					objectRelationshipId={objectRelationshipId}
					onSubmit={onSubmit}
					readOnly={readOnly}
					setAggregationFilters={setAggregationFilters}
					setDbObjectFieldRequired={setDbObjectFieldRequired}
					setObjectDefinitionExternalReferenceCode2={
						setObjectDefinitionExternalReferenceCode2
					}
					setValues={setValues}
					values={values}
				/>
			</ContainerWrapper>

			{values.businessType === 'AutoIncrement' && (
				<ContainerWrapper
					collapsable
					defaultExpanded
					displayTitle={Liferay.Language.get(
						'increment-configuration'
					)}
					displayType="unstyled"
					title={Liferay.Language.get('increment-configuration')}
				>
					<AutoIncrementFormBase
						disabled={isApproved}
						errors={errors}
						modelBuilder={modelBuilder}
						onSubmit={onSubmit}
						setValues={setValues}
						values={values}
					/>
				</ContainerWrapper>
			)}

			{values.businessType === 'Aggregation' &&
				objectDefinition?.externalReferenceCode !==
					objectDefinitionExternalReferenceCode2 && (
					<AggregationFilterContainer
						aggregationFilters={aggregationFilters}
						containerWrapper={ContainerWrapper}
						creationLanguageId2={creationLanguageId2}
						filterOperators={filterOperators}
						modelBuilder={modelBuilder}
						objectDefinitionExternalReferenceCode2={
							objectDefinitionExternalReferenceCode2
						}
						onSubmit={onSubmit}
						setAggregationFilters={setAggregationFilters}
						setCreationLanguageId2={setCreationLanguageId2}
						setValues={setValues}
						values={values}
						workflowStatuses={workflowStatuses}
					/>
				)}

			{values.businessType === 'Formula' && (
				<ContainerWrapper
					collapsable
					defaultExpanded
					displayTitle={Liferay.Language.get('formula')}
					displayType="unstyled"
					title={Liferay.Language.get('formula')}
				>
					<FormulaContainer
						errors={errors}
						modelBuilder={modelBuilder}
						objectFieldSettings={
							values.objectFieldSettings as ObjectFieldSetting[]
						}
						onSubmit={onSubmit}
						setValues={setValues}
						sidebarElements={sidebarElements}
						values={values}
					/>
				</ContainerWrapper>
			)}

			{values.DBType !== 'Blob' && values.businessType !== 'Formula' && (
				<ContainerWrapper
					collapsable
					defaultExpanded
					displayTitle={Liferay.Language.get('searchable')}
					displayType="unstyled"
					title={Liferay.Language.get('searchable')}
				>
					<SearchableContainer
						isApproved={isApproved}
						modelBuilder={modelBuilder}
						onSubmit={onSubmit}
						readOnly={readOnly}
						setValues={setValues}
						values={values}
					/>
				</ContainerWrapper>
			)}

			<ContainerWrapper
				collapsable
				defaultExpanded
				displayTitle={Liferay.Language.get('translation-options')}
				displayType="unstyled"
				title={Liferay.Language.get('translation-options')}
			>
				<TranslationOptionsContainer
					learnResources={learnResources}
					modelBuilder={modelBuilder}
					objectDefinition={objectDefinition}
					onSubmit={onSubmit}
					published={isApproved}
					setValues={setValues}
					values={values}
				/>
			</ContainerWrapper>

			<ContainerWrapper
				collapsable
				defaultExpanded
				displayTitle={Liferay.Language.get('external-data-source')}
				displayType="unstyled"
				title={Liferay.Language.get('external-data-source')}
			>
				<Input
					className={classNames({
						'lfr-objects__edit-object-field-model-builder-panel':
							modelBuilder,
					})}
					disabled={values.system}
					label={Liferay.Language.get('external-reference-code')}
					name="externalReferenceCode"
					onBlur={(event) => {
						event.stopPropagation();

						if (onSubmit) {
							onSubmit();
						}
					}}
					onChange={handleChange}
					value={values.externalReferenceCode}
				/>
			</ContainerWrapper>
		</>
	);
}
