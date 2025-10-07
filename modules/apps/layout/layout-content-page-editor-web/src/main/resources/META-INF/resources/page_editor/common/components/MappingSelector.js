/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayForm, {ClaySelectWithOption} from '@clayui/form';
import classNames from 'classnames';
import {useId} from 'frontend-js-components-web';
import {sub} from 'frontend-js-web';
import PropTypes from 'prop-types';
import React, {useEffect, useMemo, useState} from 'react';

import {addMappingFields} from '../../app/actions/index';
import {EDITABLE_TYPES} from '../../app/config/constants/editableTypes';
import {LAYOUT_TYPES} from '../../app/config/constants/layoutTypes';
import {config} from '../../app/config/index';
import {useCollectionConfig} from '../../app/contexts/CollectionItemContext';
import {useDispatch, useSelector} from '../../app/contexts/StoreContext';
import InfoItemService from '../../app/services/InfoItemService';
import {CACHE_KEYS} from '../../app/utils/cache';
import getMappedRelationship from '../../app/utils/editable_value/getMappedRelationship';
import isMapped from '../../app/utils/editable_value/isMapped';
import isMappedToInfoItem from '../../app/utils/editable_value/isMappedToInfoItem';
import isMappedToStructure from '../../app/utils/editable_value/isMappedToStructure';
import findPageContent from '../../app/utils/findPageContent';
import getMappingFieldsKey from '../../app/utils/getMappingFieldsKey';
import itemSelectorValueToInfoItem from '../../app/utils/item_selector_value/itemSelectorValueToInfoItem';
import loadCollectionFields from '../../app/utils/loadCollectionFields';
import useCache from '../../app/utils/useCache';
import usePageContents from '../../app/utils/usePageContents';
import ItemSelector from './ItemSelector';
import MappingFieldSelector from './MappingFieldSelector';
import RepeatableOptionsSelector from './RepeatableOptionsSelector';

const COLLECTION_TYPE_DIVIDER = ' - ';

const MAPPING_SOURCE_TYPES = {
	content: 'content',
	relationship: 'relationship',
	structure: 'structure',
};

const NOT_SELECTED_OPTION = {
	label: `-- ${Liferay.Language.get('not-selected')} --`,
	value: '',
};

const UNMAPPED_OPTION = {
	label: `-- ${Liferay.Language.get('unmapped')} --`,
	value: 'unmapped',
};

function filterFields(
	initialFields,
	fieldType,
	filterLinkTypes,
	selectedRelationship,
	relationships
) {
	let fields = initialFields;

	if (selectedRelationship) {
		fields = initialFields.filter(
			(fieldSet) => fieldSet.name === selectedRelationship
		);
	}

	if (relationships && !selectedRelationship) {
		fields = fields.filter(
			(fieldSet) =>
				!relationships
					.map((relationship) => relationship.name)
					.includes(fieldSet.name)
		);
	}

	return fields.reduce((acc, fieldSet) => {
		const newFields = fieldSet.fields.filter((field) => {
			if (fieldType === EDITABLE_TYPES['date-time']) {
				return field.type === 'date';
			}
			else if (fieldType === EDITABLE_TYPES.link && filterLinkTypes) {
				return (
					field.type !== EDITABLE_TYPES.action &&
					field.type !== EDITABLE_TYPES.image &&
					field.type !== 'boolean' &&
					field.type !== 'categories' &&
					field.type !== 'date' &&
					field.type !== 'tags'
				);
			}
			else if (
				fieldType === EDITABLE_TYPES.image ||
				fieldType === EDITABLE_TYPES.backgroundImage
			) {
				return field.type === EDITABLE_TYPES.image;
			}
			else if (fieldType === EDITABLE_TYPES.action) {
				return field.type === EDITABLE_TYPES.action;
			}
			else {
				return field.type !== EDITABLE_TYPES.image;
			}
		});

		if (newFields.length) {
			return [
				...acc,
				{
					...fieldSet,
					fields: newFields,
				},
			];
		}

		return acc;
	}, []);
}

function loadMappingFields({item, sourceType}) {
	let classNameId;
	let classTypeId;

	if (
		sourceType === MAPPING_SOURCE_TYPES.structure ||
		sourceType === MAPPING_SOURCE_TYPES.relationship
	) {
		const {selectedMappingTypes} = config;

		classNameId = selectedMappingTypes.type.id;
		classTypeId = selectedMappingTypes.subtype.id;
	}
	else if (
		sourceType === MAPPING_SOURCE_TYPES.content &&
		item.classNameId
	) {
		classNameId = item.classNameId;
		classTypeId = item.classTypeId;
	}

	const promise = InfoItemService.getAvailableStructureMappingFields({
		classNameId,
		classTypeId,
	});

	if (promise) {
		return promise.then((response) => {
			if (Array.isArray(response)) {
				return response;
			}

			return [];
		});
	}

	return Promise.resolve(null);
}

function getInitialSourceType(mappedItem, relationship) {
	if (relationship) {
		return MAPPING_SOURCE_TYPES.relationship;
	}
	else if (
		!isMappedToInfoItem(mappedItem) &&
		(isMappedToStructure(mappedItem) ||
			config.layoutType === LAYOUT_TYPES.display)
	) {
		return MAPPING_SOURCE_TYPES.structure;
	}

	return MAPPING_SOURCE_TYPES.content;
}

export default function MappingSelectorWrapper({
	fieldSelectorLabel,
	fieldType,
	filterLinkTypes = false,
	itemSelectorURL,
	mappedItem,
	onMappingSelect,
}) {
	const collectionConfig = useCollectionConfig();
	const [collectionFields, setCollectionFields] = useState([]);
	const [collectionTypeLabels, setCollectionItemTypeLabels] = useState({
		itemSubtype: '',
		itemType: '',
	});
	const mappingFields = useSelector((state) => state.mappingFields);
	const pageContents = usePageContents();
	const dispatch = useDispatch();

	useEffect(() => {
		if (!collectionConfig) {
			return;
		}

		const {
			classNameId,
			fieldName,
			itemSubtype,
			itemType,
			key: collectionKey,
		} = collectionConfig.collection;

		const key = classNameId
			? getMappingFieldsKey(collectionConfig.collection)
			: fieldName
				? `${collectionKey}-${fieldName}`
				: collectionKey;

		if (!mappingFields[key]) {
			loadCollectionFields(
				dispatch,
				fieldName,
				itemType,
				itemSubtype,
				key
			);
		}
	}, [collectionConfig, dispatch, mappingFields]);

	useEffect(() => {
		if (!collectionConfig) {
			setCollectionFields([]);

			return;
		}

		const key = collectionConfig.collection.classNameId
			? getMappingFieldsKey(collectionConfig.collection)
			: collectionConfig.collection.fieldName
				? `${collectionConfig.collection.key}-${collectionConfig.collection.fieldName}`
				: collectionConfig.collection.key;

		const fields = mappingFields[key];

		if (fields) {
			setCollectionFields(
				filterFields(fields, fieldType, filterLinkTypes)
			);
		}
	}, [collectionConfig, mappingFields, fieldType, filterLinkTypes]);

	useEffect(() => {
		if (!collectionConfig?.collection?.itemType) {
			return;
		}

		const {
			classNameId,
			classPK,
			key: collectionKey,
		} = collectionConfig.collection;

		const collection = pageContents.find((content) =>
			collectionKey
				? content.classPK === collectionKey
				: content.classNameId === classNameId &&
					content.classPK === classPK
		);

		if (collection) {
			const [typeLabel, subtypeLabel] =
				collection?.subtype?.split(COLLECTION_TYPE_DIVIDER) || [];

			setCollectionItemTypeLabels({
				itemSubtype: subtypeLabel,
				itemType: typeLabel,
			});
		}
	}, [collectionConfig, pageContents]);

	return collectionConfig ? (
		<>
			{collectionTypeLabels.itemType && (
				<p
					className={classNames(
						'page-editor__mapping-panel__type-label',
						{
							'mb-0': collectionTypeLabels.itemSubtype,
							'mb-2': !collectionTypeLabels.itemSubtype,
						}
					)}
				>
					<span className="mr-1">
						{Liferay.Language.get('content-type')}:
					</span>

					{collectionTypeLabels.itemType}
				</p>
			)}

			{collectionTypeLabels.itemSubtype && (
				<p className="mb-2 page-editor__mapping-panel__type-label">
					<span className="mr-1">
						{Liferay.Language.get('subtype')}:
					</span>

					{collectionTypeLabels.itemSubtype}
				</p>
			)}

			<MappingFieldSelector
				fieldType={fieldType}
				fields={collectionFields}
				label={fieldSelectorLabel}
				onValueSelect={(event) => {
					if (event.target.value === UNMAPPED_OPTION.value) {
						onMappingSelect({});
					}
					else {
						onMappingSelect({
							collectionFieldId: event.target.value,
						});
					}
				}}
				value={mappedItem.collectionFieldId}
			/>
		</>
	) : (
		<MappingSelector
			fieldSelectorLabel={fieldSelectorLabel}
			fieldType={fieldType}
			filterLinkTypes={filterLinkTypes}
			itemSelectorURL={itemSelectorURL}
			mappedItem={mappedItem}
			onMappingSelect={onMappingSelect}
		/>
	);
}

function MappingSelector({
	fieldSelectorLabel,
	fieldType,
	filterLinkTypes,
	itemSelectorURL,
	mappedItem,
	onMappingSelect,
}) {
	const dispatch = useDispatch();
	const mappingFields = useSelector((state) => state.mappingFields);
	const pageContents = usePageContents();
	const mappingSelectorSourceSelectId = useId();
	const relationshipSelectId = useId();

	const {selectedMappingTypes} = config;

	const [itemFields, setItemFields] = useState(null);
	const [selectedItem, setSelectedItem] = useState(
		getHydratedItem(mappedItem, pageContents)
	);

	const [typeLabel, setTypeLabel] = useState(null);
	const [subtypeLabel, setSubtypeLabel] = useState(null);

	const [selectedRelationship, setSelectedRelationship] = useState(
		getMappedRelationship(mappedItem.mappedField)
	);

	const [selectedSourceType, setSelectedSourceType] = useState(
		getInitialSourceType(mappedItem, selectedRelationship)
	);

	const relationships = useCache({
		fetcher: () =>
			InfoItemService.getInfoItemRelationships({
				classNameId: selectedMappingTypes?.type?.id,
				classTypeId: selectedMappingTypes?.subtype?.id,
			}),
		key: [
			CACHE_KEYS.relationships,
			selectedMappingTypes?.type?.id,
			selectedMappingTypes?.subtype?.id || '0',
		],
	});

	const sourceTypes = useMemo(() => {
		const types = [];

		if (config.layoutType === LAYOUT_TYPES.display) {
			types.push({
				label: sub(
					Liferay.Language.get('x-default'),
					selectedMappingTypes.subtype
						? selectedMappingTypes.subtype.label
						: selectedMappingTypes.type.label
				),
				value: MAPPING_SOURCE_TYPES.structure,
			});

			types.push({
				label: Liferay.Language.get('specific-content'),
				value: MAPPING_SOURCE_TYPES.content,
			});
		}

		if (
			Liferay.FeatureFlags['LPD-60546'] &&
			relationships?.length &&
			fieldType !== EDITABLE_TYPES.action
		) {
			types.push({
				label: Liferay.Language.get('relationship'),
				value: MAPPING_SOURCE_TYPES.relationship,
			});
		}

		return types;
	}, [fieldType, relationships, selectedMappingTypes]);

	const onInfoItemSelect = (selectedInfoItem) => {
		const item = getHydratedItem(selectedInfoItem, pageContents);

		setSelectedItem(item);

		if (isMapped(mappedItem)) {
			onMappingSelect({});
		}
	};

	const onFieldSelect = (event) => {
		const fieldValue = event.target.value;

		const data =
			fieldValue === UNMAPPED_OPTION.value
				? {}
				: selectedSourceType === MAPPING_SOURCE_TYPES.content
					? {...selectedItem, fieldId: fieldValue}
					: {mappedField: fieldValue};

		if (selectedSourceType === MAPPING_SOURCE_TYPES.content) {
			setSelectedItem((selectedItem) => ({
				...selectedItem,
				fieldId: fieldValue,
			}));
		}
		else {
			setSelectedItem((selectedItem) => ({
				...selectedItem,
				mappedField: fieldValue,
			}));
		}

		onMappingSelect(data);
	};

	useEffect(() => {
		const mappedContent = findPageContent(pageContents, selectedItem);

		const type = selectedItem?.itemType || mappedContent?.type;
		const subtype = selectedItem?.itemSubtype || mappedContent?.subtype;

		setTypeLabel(type);
		setSubtypeLabel(subtype);
	}, [selectedItem, pageContents]);

	useEffect(() => {
		if (
			(selectedSourceType === MAPPING_SOURCE_TYPES.content &&
				!selectedItem.classNameId) ||
			(selectedSourceType === MAPPING_SOURCE_TYPES.relationship &&
				!selectedRelationship)
		) {
			setItemFields(null);

			return;
		}

		const infoItem =
			findPageContent(pageContents, selectedItem) || selectedItem;

		const key =
			selectedSourceType === MAPPING_SOURCE_TYPES.content
				? getMappingFieldsKey(infoItem)
				: selectedSourceType === MAPPING_SOURCE_TYPES.relationship
					? getMappingFieldsKey({
							classNameId: selectedRelationship,
							classTypeId: '0',
						})
					: getMappingFieldsKey(selectedMappingTypes);

		const fields = mappingFields[key];

		if (fields) {
			setItemFields(
				filterFields(
					fields,
					fieldType,
					filterLinkTypes,
					selectedRelationship,
					relationships
				)
			);
		}
		else {
			loadMappingFields({
				item: selectedItem,
				sourceType: selectedSourceType,
			}).then((newFields) => {
				dispatch(addMappingFields({fields: newFields, key}));
			});
		}
	}, [
		dispatch,
		fieldType,
		filterLinkTypes,
		pageContents,
		mappingFields,
		relationships,
		selectedItem,
		selectedMappingTypes,
		selectedRelationship,
		selectedSourceType,
	]);

	return (
		<>
			{config.layoutType === LAYOUT_TYPES.display && (
				<>
					<ClayForm.Group small>
						<label htmlFor={mappingSelectorSourceSelectId}>
							{Liferay.Language.get('source')}
						</label>

						<ClaySelectWithOption
							className="pr-4 text-truncate"
							id={mappingSelectorSourceSelectId}
							onChange={(event) => {
								setSelectedSourceType(event.target.value);

								setSelectedItem({});

								setSelectedRelationship(null);

								if (isMapped(mappedItem)) {
									onMappingSelect({});
								}
							}}
							options={sourceTypes}
							value={selectedSourceType}
						/>
					</ClayForm.Group>

					{selectedSourceType ===
					MAPPING_SOURCE_TYPES.relationship ? (
						<ClayForm.Group small>
							<label htmlFor={relationshipSelectId}>
								{Liferay.Language.get('relationship')}
							</label>

							<ClaySelectWithOption
								className="pr-4 text-truncate"
								id={relationshipSelectId}
								onChange={(event) => {
									setSelectedRelationship(event.target.value);
								}}
								options={[
									NOT_SELECTED_OPTION,
									...(relationships || []).map(
										({label, name}) => ({
											label,
											value: name,
										})
									),
								]}
								value={selectedRelationship}
							/>
						</ClayForm.Group>
					) : null}
				</>
			)}

			{selectedSourceType === MAPPING_SOURCE_TYPES.content && (
				<ItemSelector
					className="mb-2"
					itemSelectorURL={itemSelectorURL}
					label={Liferay.Language.get('item')}
					onItemSelect={onInfoItemSelect}
					selectedItem={selectedItem}
					transformValueCallback={itemSelectorValueToInfoItem}
				/>
			)}

			{typeLabel && (
				<p
					className={classNames(
						'page-editor__mapping-panel__type-label',
						{'mb-0': subtypeLabel, 'mb-2': !subtypeLabel}
					)}
				>
					<span className="mr-1">
						{Liferay.Language.get('content-type')}:
					</span>

					{typeLabel}
				</p>
			)}

			{subtypeLabel && (
				<p className="mb-2 page-editor__mapping-panel__type-label">
					<span className="mr-1">
						{Liferay.Language.get('subtype')}:
					</span>

					{subtypeLabel}
				</p>
			)}

			<ClayForm.Group small>
				<MappingFieldSelector
					fieldType={fieldType}
					fields={itemFields}
					label={fieldSelectorLabel}
					onValueSelect={onFieldSelect}
					value={selectedItem.mappedField || selectedItem.fieldId}
				/>
			</ClayForm.Group>

			<RepeatableOptionsSelector
				fieldName={selectedItem.mappedField || selectedItem.fieldId}
				fields={itemFields}
				onOptionsSelect={(options) => {
					setSelectedItem((selectedItem) => ({
						...selectedItem,
						config: {
							...selectedItem.config,
							...options,
						},
					}));

					onMappingSelect({
						...selectedItem,
						config: {
							...selectedItem.config,
							...options,
						},
					});
				}}
				options={selectedItem.config}
			/>
		</>
	);
}

function getHydratedItem(item, pageContents) {
	const pageContent = findPageContent(pageContents, item);

	if (pageContent) {
		return {...pageContent, ...item};
	}

	return item;
}

MappingSelector.propTypes = {
	fieldType: PropTypes.string,
	mappedItem: PropTypes.oneOfType([
		PropTypes.shape({
			classNameId: PropTypes.string,
			classPK: PropTypes.string,
			externalReferenceCode: PropTypes.string,
			fieldId: PropTypes.string,
			fileEntryId: PropTypes.string,
		}),
		PropTypes.shape({
			collectionFieldId: PropTypes.string,
			fileEntryId: PropTypes.string,
		}),
		PropTypes.shape({mappedField: PropTypes.string}),
	]),
	onMappingSelect: PropTypes.func.isRequired,
};
