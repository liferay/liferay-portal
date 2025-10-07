/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayTabs from '@clayui/tabs';
import {API, SidebarCategory} from '@liferay/object-js-components-web';
import classNames from 'classnames';
import {createResourceURL, fetch} from 'frontend-js-web';
import React, {ElementType, useEffect, useState} from 'react';

import {EditObjectFieldProps} from './EditObjectField';
import {ObjectFieldErrors} from './ObjectFieldFormBase';
import {AdvancedTab} from './Tabs/Advanced/AdvancedTab';
import {BasicInfoTab} from './Tabs/BasicInfo/BasicInfoTab';

import './EditObjectFieldContent.scss';

interface EditObjectFieldContentProps
	extends Omit<
		EditObjectFieldProps,
		| 'forbiddenChars'
		| 'forbiddenLastChars'
		| 'forbiddenNames'
		| 'objectDefinitionExternalReferenceCode'
		| 'objectFieldId'
	> {
	containerWrapper: ElementType;
	errors: ObjectFieldErrors;
	handleChange: React.ChangeEventHandler<HTMLInputElement>;
	modelBuilder?: boolean;
	objectDefinition?: ObjectDefinition | ObjectDefinitionNodeData;
	objectFieldId: number;
	onSubmit?: (editedObjectField?: Partial<ObjectField>) => void;
	setValues: (values: Partial<ObjectField>) => void;
	values: Partial<ObjectField>;
}

const TABS = [Liferay.Language.get('basic-info')];

export function EditObjectFieldContent({
	baseResourceURL,
	containerWrapper,
	creationLanguageId,
	errors,
	filterOperators,
	handleChange,
	isDefaultStorageType,
	isRootDescendantNode,
	learnResources,
	modelBuilder = false,
	objectDefinition,
	objectFieldId,
	onSubmit,
	readOnly,
	setValues,
	values,
	workflowStatuses,
}: EditObjectFieldContentProps) {
	const [activeIndex, setActiveIndex] = useState(0);

	const [dbObjectFieldRequired, setDbObjectFieldRequired] =
		useState<boolean>();
	const [objectFieldBusinessTypes, setObjectFieldBusinessTypes] = useState<
		ObjectFieldBusinessType[]
	>([]);
	const [objectRelationshipId, setObjectRelationshipId] = useState(0);
	const [readOnlySidebarElements, setReadOnlySidebarElements] = useState<
		SidebarCategory[]
	>([]);
	const [sidebarElements, setSidebarElements] = useState<SidebarCategory[]>(
		[]
	);

	if (
		(isDefaultStorageType || values.businessType === 'Picklist') &&
		TABS.length < 2
	) {
		TABS.push(Liferay.Language.get('advanced'));
	}

	useEffect(() => {
		const makeFetch = async () => {
			const objectFieldResponse = await API.getObjectField(objectFieldId);

			setDbObjectFieldRequired(objectFieldResponse.required);
			setValues(objectFieldResponse);
		};

		makeFetch();

		// eslint-disable-next-line react-hooks/exhaustive-deps
	}, []);

	useEffect(() => {
		const makeFetch = async () => {
			if (values.id !== 0) {
				const url = createResourceURL(baseResourceURL, {
					objectFieldId: values?.id,
					p_p_resource_id:
						'/object_definitions/get_object_field_info',
				}).href;

				const objectFieldInfoResponse = await fetch(url, {
					method: 'GET',
				});

				const objectFieldInfoJSON =
					(await objectFieldInfoResponse.json()) as {
						objectFieldBusinessTypes: ObjectFieldBusinessType[];
						objectRelationshipId: number;
						readOnlySidebarElements: SidebarCategory[];
						sidebarElements: SidebarCategory[];
					};

				if (values.businessType === 'Relationship') {
					setObjectRelationshipId(
						objectFieldInfoJSON.objectRelationshipId
					);
				}

				setObjectFieldBusinessTypes(
					objectFieldInfoJSON.objectFieldBusinessTypes
				);
				setReadOnlySidebarElements(
					objectFieldInfoJSON.readOnlySidebarElements
				);
				setSidebarElements(objectFieldInfoJSON.sidebarElements);
			}
		};

		makeFetch();

		// eslint-disable-next-line react-hooks/exhaustive-deps
	}, [baseResourceURL, values.id]);

	return (
		<>
			{isDefaultStorageType || values.businessType === 'Picklist' ? (
				<>
					<ClayTabs className="side-panel-iframe__tabs">
						{TABS.map((label, index) => (
							<ClayTabs.Item
								active={activeIndex === index}
								key={index}
								onClick={() => setActiveIndex(index)}
							>
								{label}
							</ClayTabs.Item>
						))}
					</ClayTabs>

					<ClayTabs.Content activeIndex={activeIndex} fade>
						<ClayTabs.TabPane
							className={classNames({
								'lfr-objects__edit-object-field-content-panel':
									modelBuilder,
							})}
						>
							<BasicInfoTab
								baseResourceURL={baseResourceURL}
								containerWrapper={containerWrapper}
								dbObjectFieldRequired={dbObjectFieldRequired}
								errors={errors}
								filterOperators={filterOperators}
								handleChange={handleChange}
								learnResources={learnResources}
								modelBuilder={modelBuilder}
								objectDefinition={objectDefinition}
								objectFieldBusinessTypes={
									objectFieldBusinessTypes
								}
								objectRelationshipId={objectRelationshipId}
								onSubmit={onSubmit}
								readOnly={readOnly}
								setDbObjectFieldRequired={
									setDbObjectFieldRequired
								}
								setValues={setValues}
								sidebarElements={sidebarElements}
								values={values}
								workflowStatuses={workflowStatuses}
							/>
						</ClayTabs.TabPane>

						<ClayTabs.TabPane
							className={classNames({
								'lfr-objects__edit-object-field-content-panel':
									modelBuilder,
							})}
						>
							<AdvancedTab
								containerWrapper={containerWrapper}
								creationLanguageId={creationLanguageId}
								errors={errors}
								isDefaultStorageType={isDefaultStorageType}
								isRootDescendantNode={isRootDescendantNode}
								learnResources={learnResources}
								modelBuilder={modelBuilder}
								onSubmit={onSubmit}
								readOnlySidebarElements={
									readOnlySidebarElements
								}
								setValues={setValues}
								sidebarElements={sidebarElements}
								values={values}
							/>
						</ClayTabs.TabPane>
					</ClayTabs.Content>
				</>
			) : (
				<BasicInfoTab
					baseResourceURL={baseResourceURL}
					containerWrapper={containerWrapper}
					dbObjectFieldRequired={dbObjectFieldRequired}
					errors={errors}
					filterOperators={filterOperators}
					handleChange={handleChange}
					learnResources={learnResources}
					modelBuilder={modelBuilder}
					objectDefinition={objectDefinition}
					objectFieldBusinessTypes={objectFieldBusinessTypes}
					objectRelationshipId={objectRelationshipId}
					onSubmit={onSubmit}
					readOnly={readOnly}
					setDbObjectFieldRequired={setDbObjectFieldRequired}
					setValues={setValues}
					sidebarElements={sidebarElements}
					values={values}
					workflowStatuses={workflowStatuses}
				/>
			)}
		</>
	);
}
