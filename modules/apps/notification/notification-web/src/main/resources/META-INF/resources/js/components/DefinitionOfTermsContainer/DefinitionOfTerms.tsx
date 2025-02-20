/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {FrontendDataSet} from '@liferay/frontend-data-set-web';
import {SingleSelect, stringUtils} from '@liferay/object-js-components-web';
import {createResourceURL, fetch} from 'frontend-js-web';
import React, {useMemo, useState} from 'react';

import copyTerm from '../../util/copyTerm';
import RelationshipSection from './RelationshipSection';

interface DefinitionOfTermsProps {
	baseResourceURL: string;
	objectDefinitions: ObjectDefinition[];
	selectedEntityId: number;
	setSelectedEntityId: Function;
}
export interface RelationshipSections {
	objectRelationshipId: number;
	sectionLabel: string;
	terms?: Item[];
}

interface TermsResponse {
	relationshipSections: RelationshipSections[];
	terms: Item[];
}

export interface Item {
	termLabel: string;
	termName: string;
}

export function DefinitionOfTerms({
	baseResourceURL,
	objectDefinitions,
	selectedEntityId,
	setSelectedEntityId,
}: DefinitionOfTermsProps) {
	const [entityFields, setObjectFieldTerms] = useState<Item[]>([]);
	const [relationshipSections, setRelationshipSections] = useState<
		RelationshipSections[]
	>([]);

	const objectDefinitionItems = useMemo(() => {
		return objectDefinitions.map(
			({defaultLanguageId, id, label, name}) => ({
				label: stringUtils.getLocalizableLabel(
					defaultLanguageId,
					label,
					name
				),
				value: id,
			})
		) as LabelValueObject<number>[];
	}, [objectDefinitions]);

	const getObjectFieldTerms = async (objectDefinitionId: number) => {
		const response = await fetch(
			createResourceURL(baseResourceURL, {
				objectDefinitionId,
				p_p_resource_id:
					'/notification_templates/get_object_field_notification_template_terms',
			}).toString()
		);

		const {relationshipSections, terms} =
			(await response.json()) as TermsResponse;

		setObjectFieldTerms(terms);
		setRelationshipSections(relationshipSections);
	};

	return (
		<>
			<>
				<SingleSelect
					id="definitionOfTermsEntity"
					items={objectDefinitionItems}
					label={Liferay.Language.get('entity')}
					onSelectionChange={(value) => {
						getObjectFieldTerms(value as number);
						setSelectedEntityId(value as number);
					}}
					selectedKey={selectedEntityId}
				/>

				<div id="lfr-notification-web__definition-of-terms-table">
					<FrontendDataSet
						id="DefinitionOfTermsTable"
						items={entityFields}
						itemsActions={[
							{
								href: 'copyTerm',
								icon: 'copy',
								id: 'copyTerm',
								label: Liferay.Language.get('copy'),
								onClick: copyTerm,
								target: 'event',
							},
						]}
						selectedItemsKey="termName"
						showManagementBar={false}
						showPagination={false}
						showSearch={false}
						views={[
							{
								contentRenderer: 'table',
								label: 'Table',
								name: 'table',
								schema: {
									fields: [
										{
											fieldName: 'termLabel',
											label: Liferay.Language.get(
												'label'
											),
										},
										{
											fieldName: 'termName',
											label: Liferay.Language.get('term'),
										},
									],
								},
								thumbnail: 'table',
							},
						]}
					/>
				</div>
			</>

			{relationshipSections?.map((relationshipSection, index) => (
				<RelationshipSection
					baseResourceURL={baseResourceURL}
					currentRelationshipSectionIndex={index}
					key={relationshipSection.objectRelationshipId}
					relationshipSection={relationshipSection}
					relationshipSections={relationshipSections}
					setRelationshipSections={setRelationshipSections}
				/>
			))}
		</>
	);
}
