/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import {Toolbar, VerticalNavLayout} from '@liferay/site-cms-site-initializer';
import {fetch, navigate, sub} from 'frontend-js-web';
import React, {useEffect, useState} from 'react';

import FieldMappingsSection from './edit_field_mappings/FieldMappingsSection';
import MappingRulesSection from './edit_field_mappings/MappingRulesSection';
import getObjectFields from './edit_field_mappings/getObjectFields';
import newFieldMapping from './edit_field_mappings/newFieldMapping';
import {
	IFieldMapping,
	IMappingRule,
	IObjectDefinition,
	TYPE_FIXED_VALUE,
} from './edit_field_mappings/types';

const EMPTY_MAPPING_RULE = {
	id: 0,
	stripHTMLTags: false,
	textCase: '',
	trimWhitespace: false,
	truncateLength: 0,
	urlEncode: false,
	useFirstNonemptySource: false,
};

interface IProps {
	apiURL: string;
	backURL: string;
	channelField: string;
	channelFieldLabel: string;
	channelFieldMultiple: boolean;
	fieldMappings: IFieldMapping[];
	mappingRule: IMappingRule | null;
	mappingRuleAPIURL: string;
	mappingRuleObjectRelationshipObjectFieldName: string;
	objectDefinitions: IObjectDefinition[];
	objectEntryId: number;
	objectRelationshipObjectFieldName: string;
	spritemap: string;
	title: string;
}

function isComplete({sourceFieldName, type, value}: IFieldMapping) {
	if (type === TYPE_FIXED_VALUE) {
		return Boolean(value);
	}

	return Boolean(sourceFieldName);
}

function isEmpty({
	stripHTMLTags,
	textCase,
	trimWhitespace,
	truncateLength,
	urlEncode,
	useFirstNonemptySource,
}: IMappingRule) {
	return (
		!stripHTMLTags &&
		!textCase &&
		!trimWhitespace &&
		!truncateLength &&
		!urlEncode &&
		!useFirstNonemptySource
	);
}

export default function EditPIMConnectorFieldMappings({
	apiURL,
	backURL,
	channelField,
	channelFieldLabel,
	channelFieldMultiple,
	fieldMappings: initialFieldMappings = [],
	mappingRule: initialMappingRule,
	mappingRuleAPIURL,
	mappingRuleObjectRelationshipObjectFieldName,
	objectDefinitions = [],
	objectEntryId,
	objectRelationshipObjectFieldName,
	spritemap,
	title,
}: IProps) {
	const [fieldMappings, setFieldMappings] = useState<IFieldMapping[]>(() => {
		const currentFieldMappings = initialFieldMappings
			.filter(
				(fieldMapping) =>
					fieldMapping.type === TYPE_FIXED_VALUE ||
					getObjectFields(
						objectDefinitions,
						fieldMapping.sourceClassName
					).some(({name}) => name === fieldMapping.sourceFieldName)
			)
			.map((fieldMapping) => ({
				...fieldMapping,
				key: `entry-${fieldMapping.id}`,
			}));

		if (currentFieldMappings.length) {
			return currentFieldMappings;
		}

		return [newFieldMapping()];
	});

	const [mappingRule, setMappingRule] = useState<IMappingRule>(
		initialMappingRule ?? EMPTY_MAPPING_RULE
	);

	useEffect(() => {
		if (!Number(objectEntryId) || !channelField) {
			navigate(backURL);
		}
	}, [backURL, channelField, objectEntryId]);

	const saveMappingRule = () => {
		const {id, ...values} = mappingRule;

		if (isEmpty(mappingRule)) {
			if (!id) {
				return null;
			}

			return fetch(`${mappingRuleAPIURL}/${id}`, {method: 'DELETE'});
		}

		const body: {[key: string]: unknown} = {
			...values,
			channelFieldName: channelField,
		};

		if (!id) {
			body[mappingRuleObjectRelationshipObjectFieldName] = objectEntryId;
		}

		return fetch(id ? `${mappingRuleAPIURL}/${id}` : mappingRuleAPIURL, {
			body: JSON.stringify(body),
			headers: {
				'Content-Type': 'application/json',
			},
			method: id ? 'PATCH' : 'POST',
		});
	};

	const handleSave = async () => {
		const nextFieldMappings = fieldMappings.filter(isComplete);

		if (!nextFieldMappings.length && !isEmpty(mappingRule)) {
			Liferay.Util.openToast({
				message: Liferay.Language.get(
					'add-at-least-one-source-attribute-to-save-the-mapping-rules-or-turn-the-mapping-rules-off'
				),
				type: 'danger',
			});

			return;
		}

		const nextIds = new Set(
			nextFieldMappings.map(({id}) => id).filter(Boolean)
		);

		try {
			const deleteResponses = await Promise.all(
				initialFieldMappings
					.filter(({id}) => !nextIds.has(id))
					.map(({id}) => fetch(`${apiURL}/${id}`, {method: 'DELETE'}))
			);

			if (deleteResponses.some(({ok}) => !ok)) {
				throw new Error();
			}

			const saveResponses = await Promise.all(
				nextFieldMappings.map((fieldMapping, index) => {
					const fixedValue = fieldMapping.type === TYPE_FIXED_VALUE;

					const body: {[key: string]: unknown} = {
						channelFieldName: channelField,
						priority: index,
						sourceClassName: fieldMapping.sourceClassName,
						sourceFieldName: fixedValue
							? ''
							: fieldMapping.sourceFieldName,
						type: fieldMapping.type,
						value: fixedValue ? fieldMapping.value : '',
					};

					if (!fieldMapping.id) {
						body[objectRelationshipObjectFieldName] = objectEntryId;
					}

					return fetch(
						fieldMapping.id
							? `${apiURL}/${fieldMapping.id}`
							: apiURL,
						{
							body: JSON.stringify(body),
							headers: {
								'Content-Type': 'application/json',
							},
							method: fieldMapping.id ? 'PATCH' : 'POST',
						}
					);
				})
			);

			if (saveResponses.some(({ok}) => !ok)) {
				throw new Error();
			}

			const mappingRuleResponse = await saveMappingRule();

			if (mappingRuleResponse && !mappingRuleResponse.ok) {
				throw new Error();
			}

			Liferay.Util.openToast({
				message: sub(
					Liferay.Language.get('x-was-updated-successfully'),
					channelFieldLabel
				),
				type: 'success',
			});

			navigate(backURL);
		}
		catch (error) {
			Liferay.Util.openToast({
				message: Liferay.Language.get('an-unexpected-error-occurred'),
				type: 'danger',
			});
		}
	};

	return (
		<>
			<Toolbar backURL={backURL} title={title}>
				<Toolbar.Item>
					<ClayButton
						displayType="secondary"
						onClick={() => navigate(backURL)}
						size="sm"
					>
						{Liferay.Language.get('cancel')}
					</ClayButton>

					<ClayButton
						className="inline-item-after"
						displayType="primary"
						onClick={handleSave}
						size="sm"
					>
						{Liferay.Language.get('save')}
					</ClayButton>
				</Toolbar.Item>
			</Toolbar>

			<VerticalNavLayout
				items={[
					{
						component: (
							<FieldMappingsSection
								fieldMappings={fieldMappings}
								objectDefinitions={objectDefinitions}
								setFieldMappings={setFieldMappings}
								spritemap={spritemap}
							/>
						),
						id: 'destination',
						label: Liferay.Language.get('destination'),
					},
					{
						component: (
							<MappingRulesSection
								channelFieldMultiple={channelFieldMultiple}
								disabled={!initialMappingRule}
								mappingRule={mappingRule}
								onChange={setMappingRule}
							/>
						),
						id: 'mappingRules',
						label: Liferay.Language.get('mapping-rules'),
					},
				]}
			/>
		</>
	);
}
