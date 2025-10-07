/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayLoadingIndicator from '@clayui/loading-indicator';
import {
	API,
	SingleSelect,
	stringUtils,
} from '@liferay/object-js-components-web';
import React, {useEffect, useMemo, useState} from 'react';

import {normalizeFieldSettings} from '../../utils/fieldSettings';
import {ObjectFieldErrors} from './ObjectFieldFormBase';

interface AggregationFormBaseProps {
	creationLanguageId2: Liferay.Language.Locale;
	disabled?: boolean;
	editingObjectField?: boolean;
	errors: ObjectFieldErrors;
	objectDefinitionExternalReferenceCode: string;
	objectFieldSettings: ObjectFieldSetting[];
	onAggregationFilterChange?: (aggregationFilterArray: []) => void;
	onObjectRelationshipChange?: (
		objectDefinitionExternalReferenceCode2: string
	) => void;
	onSubmit?: (values?: Partial<ObjectField>) => void;
	setValues: (values: Partial<ObjectField>) => void;
	values: Partial<ObjectField>;
}

type TObjectRelationship = {
	label: LocalizedValue<string>;
	name: string;
	objectDefinitionExternalReferenceCode2: string;
};

const aggregationFunctions = [
	{
		label: Liferay.Language.get('count'),
		value: 'COUNT',
	},
	{
		label: Liferay.Language.get('sum'),
		value: 'SUM',
	},
	{
		label: Liferay.Language.get('average'),
		value: 'AVERAGE',
	},
	{
		label: Liferay.Language.get('min'),
		value: 'MIN',
	},
	{
		label: Liferay.Language.get('max'),
		value: 'MAX',
	},
];

export function AggregationFormBase({
	creationLanguageId2,
	disabled,
	errors,
	editingObjectField,
	onAggregationFilterChange,
	onObjectRelationshipChange,
	onSubmit,
	objectDefinitionExternalReferenceCode,
	objectFieldSettings = [],
	setValues,
	values,
}: AggregationFormBaseProps) {
	const [objectRelationships, setObjectRelationships] =
		useState<TObjectRelationship[]>();
	const [objectRelationshipFields, setObjectRelationshipFields] =
		useState<ObjectField[]>();
	const [reload, setReload] = useState(false);
	const [
		selectedRelatedObjectRelationship,
		setSelectRelatedObjectRelationship,
	] = useState<TObjectRelationship>();
	const [selectedSummarizeFieldName, setSelectedSummarizeFieldName] =
		useState<string>();
	const [
		selectedAggregationFunctionValue,
		setSelectedAggregationFunctionValue,
	] = useState<string>();

	const filteredObjectRelationships = useMemo(() => {
		return objectRelationships?.map(({label, name}) => ({
			label: stringUtils.getLocalizableLabel({
				fallbackLabel: name,
				fallbackLanguageId:
					creationLanguageId2 as Liferay.Language.Locale,
				labels: label,
			}),
			value: name,
		})) as LabelValueObject[];
	}, [creationLanguageId2, objectRelationships]);

	const filteredObjectRelationshipFields = useMemo(() => {
		return objectRelationshipFields?.map(({label, name}) => ({
			label: stringUtils.getLocalizableLabel({
				fallbackLabel: name,
				fallbackLanguageId:
					creationLanguageId2 as Liferay.Language.Locale,
				labels: label,
			}),
			value: name,
		}));
	}, [creationLanguageId2, objectRelationshipFields]);

	const handleChangeRelatedObjectRelationship = async (
		objectRelationshipName: string
	) => {
		const selectedObjectRelationship = objectRelationships?.find(
			({name}) => name === objectRelationshipName
		);

		setSelectRelatedObjectRelationship(selectedObjectRelationship);
		setSelectedSummarizeFieldName(undefined);

		const relatedFields =
			await API.getObjectDefinitionByExternalReferenceCodeObjectFields(
				selectedObjectRelationship?.objectDefinitionExternalReferenceCode2 as string
			);

		const numericFields = relatedFields.filter(
			(objectField) =>
				objectField.businessType === 'Integer' ||
				objectField.businessType === 'LongInteger' ||
				objectField.businessType === 'Decimal' ||
				objectField.businessType === 'PrecisionDecimal'
		);

		setObjectRelationshipFields(numericFields);

		const fieldSettingWithoutSummarizeField = objectFieldSettings.filter(
			(fieldSettings) =>
				fieldSettings.name !== 'objectFieldName' &&
				fieldSettings.name !== 'filters' &&
				fieldSettings.name !== 'objectRelationshipName'
		);

		const newObjectFieldSettings: ObjectFieldSetting[] | undefined = [
			...fieldSettingWithoutSummarizeField,
			{
				name: 'objectRelationshipName',
				value: selectedObjectRelationship?.name as string,
			},
			{
				name: 'filters',
				value: [],
			},
		];

		if (onAggregationFilterChange) {
			onAggregationFilterChange([]);
		}

		setValues({
			objectFieldSettings: newObjectFieldSettings,
		});

		if (onObjectRelationshipChange) {
			onObjectRelationshipChange(
				selectedObjectRelationship?.objectDefinitionExternalReferenceCode2 as string
			);
		}

		if (onSubmit) {
			onSubmit({
				...values,
				objectFieldSettings: newObjectFieldSettings,
			});
		}
	};

	const handleAggregationFunctionChange = (value: string) => {
		const aggregationFunction = aggregationFunctions.find(
			(aggregationFunction) => aggregationFunction.value === value
		);

		setSelectedAggregationFunctionValue(aggregationFunction?.value);

		let newObjectFieldSettings: ObjectFieldSetting[] | undefined;

		if (value === 'COUNT') {
			setSelectedSummarizeFieldName(undefined);

			const fieldSettingWithoutSummarizeField =
				objectFieldSettings.filter(
					(fieldSettings) => fieldSettings.name !== 'objectFieldName'
				);

			newObjectFieldSettings = [
				...fieldSettingWithoutSummarizeField.filter(
					(fieldSettings) => fieldSettings.name !== 'function'
				),
				{
					name: 'function',
					value,
				},
			];

			setValues({
				objectFieldSettings: newObjectFieldSettings,
			});

			if (onSubmit) {
				onSubmit({
					...values,
					objectFieldSettings: newObjectFieldSettings,
				});
			}

			return;
		}

		newObjectFieldSettings = [
			...objectFieldSettings.filter(
				(fieldSettings) => fieldSettings.name !== 'function'
			),
			{
				name: 'function',
				value,
			},
		];

		setValues({
			objectFieldSettings: newObjectFieldSettings,
		});

		if (onSubmit) {
			onSubmit({
				...values,
				objectFieldSettings: newObjectFieldSettings,
			});
		}
	};

	const handleSummarizeFieldChange = (objectFieldName: string) => {
		const selectedObjectField = objectRelationshipFields?.find(
			({name}) => name === objectFieldName
		);

		setSelectedSummarizeFieldName(selectedObjectField?.name);

		const newObjectFieldSettings: ObjectFieldSetting[] | undefined = [
			...objectFieldSettings.filter(
				(fieldSettings) => fieldSettings.name !== 'objectFieldName'
			),
			{
				name: 'objectFieldName',
				value: selectedObjectField?.name as string,
			},
		];

		setValues({
			objectFieldSettings: newObjectFieldSettings,
		});

		if (onSubmit) {
			onSubmit({
				objectFieldSettings: newObjectFieldSettings,
			});
		}
	};

	useEffect(() => {
		const makeFetch = async () => {
			const objectRelationshipsData =
				await API.getObjectDefinitionByExternalReferenceCodeObjectRelationships(
					objectDefinitionExternalReferenceCode
				);

			setObjectRelationships(
				objectRelationshipsData.filter(
					(objectRelationship) =>
						!(
							objectRelationship.type === 'manyToMany' &&
							objectRelationship.reverse &&
							objectRelationship.objectDefinitionExternalReferenceCode1 ===
								objectRelationship.objectDefinitionExternalReferenceCode2
						)
				)
			);
		};

		makeFetch();
	}, [objectDefinitionExternalReferenceCode]);

	useEffect(() => {
		if (editingObjectField && objectRelationships) {
			const makeFetch = async () => {
				const settings = normalizeFieldSettings(objectFieldSettings);

				const currentRelatedObjectRelationship =
					objectRelationships.find(
						(relationship) =>
							relationship.name ===
							settings.objectRelationshipName
					) as ObjectRelationship;

				const currentFunction = aggregationFunctions.find(
					(aggregationFunction) =>
						aggregationFunction.value === settings.function
				);

				if (currentRelatedObjectRelationship) {
					const relatedFields =
						await API.getObjectDefinitionByExternalReferenceCodeObjectFields(
							currentRelatedObjectRelationship.objectDefinitionExternalReferenceCode2
						);

					const currentSummarizeField = relatedFields.find(
						(relatedField) =>
							relatedField.name === settings.objectFieldName
					) as ObjectField;

					if (onObjectRelationshipChange) {
						onObjectRelationshipChange(
							currentRelatedObjectRelationship.objectDefinitionExternalReferenceCode2
						);
					}

					setObjectRelationshipFields(
						relatedFields.filter(
							(objectField) =>
								objectField.businessType === 'Integer' ||
								objectField.businessType === 'LongInteger' ||
								objectField.businessType === 'Decimal' ||
								objectField.businessType === 'PrecisionDecimal'
						)
					);

					setSelectRelatedObjectRelationship(
						currentRelatedObjectRelationship
					);

					setSelectedAggregationFunctionValue(currentFunction?.value);

					if (currentSummarizeField) {
						setSelectedSummarizeFieldName(
							currentSummarizeField.name
						);
					}
				}
			};

			makeFetch();
		}
	}, [
		creationLanguageId2,
		editingObjectField,
		objectRelationships,
		objectFieldSettings,
		onObjectRelationshipChange,
	]);

	useEffect(() => {
		if (selectedAggregationFunctionValue !== 'COUNT') {
			setReload(true);
			setTimeout(() => {
				setReload(false);
			}, 100);
		}
	}, [selectedAggregationFunctionValue]);

	return (
		<>
			{objectRelationships && (
				<SingleSelect
					error={errors.objectRelationshipName}
					id="objectFieldAggregationRelationship"
					items={filteredObjectRelationships ?? []}
					label={Liferay.Language.get('relationship')}
					onSelectionChange={(value) => {
						handleChangeRelatedObjectRelationship(value as string);
					}}
					required
					selectedKey={selectedRelatedObjectRelationship?.name}
				/>
			)}

			<SingleSelect
				disabled={disabled}
				error={errors.function}
				id="objectFieldAggregationFunction"
				items={aggregationFunctions}
				label={Liferay.Language.get('function')}
				onSelectionChange={(value) =>
					handleAggregationFunctionChange(value as string)
				}
				required
				selectedKey={selectedAggregationFunctionValue}
			/>

			{reload && selectedAggregationFunctionValue ? (
				<ClayLoadingIndicator displayType="secondary" size="sm" />
			) : (
				selectedAggregationFunctionValue !== 'COUNT' && (
					<SingleSelect
						error={errors.objectFieldName}
						id="objectFieldAggregationField"
						items={filteredObjectRelationshipFields ?? []}
						label={Liferay.Language.get('field')}
						onSelectionChange={(value) => {
							handleSummarizeFieldChange(value as string);
						}}
						required
						selectedKey={selectedSummarizeFieldName}
					/>
				)
			)}
		</>
	);
}
