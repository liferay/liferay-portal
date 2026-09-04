/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import ClayEmptyState from '@clayui/empty-state';
import ClayLoadingIndicator from '@clayui/loading-indicator';
import {
	DRAG_TYPES,
	EVENT_TYPES,
	useForm,
	useFormState,
} from 'data-engine-js-components-web';
import React, {useState} from 'react';

import useSearchFieldSets from '../../hooks/useSearchFieldSets.es';
import {getLocalizedValue, getPluralMessage, sub} from '../../utils/lang.es';
import FieldType from '../field-types/FieldType.es';
import FieldSetModal from './FieldSetModal';
import useDeleteFieldSet from './actions/useDeleteFieldSet.es';
import usePropagateFieldSet from './actions/usePropagateFieldSet.es';

function getFieldSetLabel(fieldSet) {
	return (
		getLocalizedValue(fieldSet.defaultLanguageId, fieldSet.name ?? {}) ?? ''
	);
}

function getSortedFieldsets(fieldsets) {
	return fieldsets.sort((a, b) => {
		const localizedValueA = getFieldSetLabel(a);
		const localizedValueB = getFieldSetLabel(b);

		return localizedValueA.localeCompare(localizedValueB);
	});
}

function matchesSearchTerm(fieldSet, lowerCaseSearchTerm) {
	const label = getFieldSetLabel(fieldSet);

	return label.toLowerCase().includes(lowerCaseSearchTerm);
}

function getMergedFieldsets(fieldSets, searchResults, searchTerm) {
	const lowerCaseSearchTerm = searchTerm.toLowerCase();

	const storeFieldSetsById = new Map(
		fieldSets.map((fieldSet) => [fieldSet.id, fieldSet])
	);

	const mergedFieldSetsById = new Map();

	(searchResults ?? []).forEach((fieldSet) => {
		mergedFieldSetsById.set(
			fieldSet.id,
			storeFieldSetsById.get(fieldSet.id) ?? fieldSet
		);
	});

	fieldSets.forEach((fieldSet) => {
		if (matchesSearchTerm(fieldSet, lowerCaseSearchTerm)) {
			mergedFieldSetsById.set(fieldSet.id, fieldSet);
		}
	});

	return [...mergedFieldSetsById.values()];
}

export default function FieldSetList({searchTerm}) {
	const [modalState, setModalState] = useState({isVisible: false});
	const {fieldSets} = useFormState();
	const {dataDefinition} = useFormState({schema: ['dataDefinition']});
	const dispatch = useForm();
	const deleteFieldSet = useDeleteFieldSet();
	const propagateFieldSet = usePropagateFieldSet();

	const trimmedSearchTerm = searchTerm ? searchTerm.trim() : '';

	const {
		hasError,
		isLoading,
		isTruncated,
		removeSearchResult,
		searchResults,
	} = useSearchFieldSets(trimmedSearchTerm);

	const filteredFieldsets = getSortedFieldsets(
		trimmedSearchTerm
			? getMergedFieldsets(fieldSets, searchResults, trimmedSearchTerm)
			: [...fieldSets]
	);

	const fieldSetsInUse = new Set();
	dataDefinition.dataDefinitionFields.forEach(
		({customProperties: {ddmStructureId}, fieldType}) => {
			if (fieldType === 'fieldset') {
				fieldSetsInUse.add(parseInt(ddmStructureId, 10));
			}
		}
	);

	const deleteFieldSetAndSearchResult = async (fieldSet) => {
		const deleted = await deleteFieldSet(fieldSet);

		if (deleted) {
			removeSearchResult(fieldSet.id);
		}

		return deleted;
	};

	const toggleFieldSet = (fieldSet) => {
		setModalState(({isVisible}) => ({
			fieldSet,
			isVisible: !isVisible,
		}));
	};

	const CreateNewFieldsetButton = () => (
		<ClayButton
			block
			className="add-fieldset"
			displayType="secondary"
			onClick={() => toggleFieldSet()}
		>
			{Liferay.Language.get('create-new-fieldset')}
		</ClayButton>
	);

	return (
		<>
			{!!filteredFieldsets.length || isLoading ? (
				<>
					<CreateNewFieldsetButton />

					<div className="mt-3">
						{isLoading && <ClayLoadingIndicator small />}

						{filteredFieldsets.map((fieldSet) => {
							const actions = [
								{
									action: () => toggleFieldSet(fieldSet),
									name: Liferay.Language.get('edit'),
								},
								{
									action: () =>
										propagateFieldSet({
											fieldSet,
											isDeleteAction: true,
											modal: {
												actionMessage:
													Liferay.Language.get(
														'delete'
													),
												fieldSetMessage:
													Liferay.Language.get(
														'the-fieldset-will-be-deleted-permanently-from'
													),
												headerMessage:
													Liferay.Language.get(
														'delete'
													),
												status: 'danger',
												warningMessage:
													Liferay.Language.get(
														'this-action-may-erase-data-permanently'
													),
											},
											onPropagate:
												deleteFieldSetAndSearchResult,
										}),
									name: Liferay.Language.get('delete'),
								},
							];
							const description = getPluralMessage(
								Liferay.Language.get('x-field'),
								Liferay.Language.get('x-fields'),
								fieldSet.dataDefinitionFields.length
							);
							const disabled = fieldSetsInUse.has(fieldSet.id);
							const label = getFieldSetLabel(fieldSet);
							const onDoubleClick = () => {
								dispatch({
									payload: {fieldSet},
									type: EVENT_TYPES.FIELD_SET.ADD,
								});
							};

							return (
								<FieldType
									actions={actions}
									description={description}
									disabled={disabled}
									dragType={DRAG_TYPES.DRAG_FIELDSET_ADD}
									fieldSet={fieldSet}
									icon="forms"
									key={fieldSet.dataDefinitionKey}
									label={label}
									onDoubleClick={onDoubleClick}
								/>
							);
						})}

						{!isLoading && isTruncated && (
							<p className="text-3 text-secondary">
								{Liferay.Language.get(
									'refine-the-search-criteria-to-reduce-results'
								)}
							</p>
						)}
					</div>
				</>
			) : (
				<div className="mt-2">
					{hasError ? (
						<ClayEmptyState
							description={Liferay.Language.get(
								'an-unexpected-error-occurred'
							)}
							imgSrc={`${themeDisplay.getPathThemeImages()}/states/search_state.svg`}
							small
							title={Liferay.Language.get(
								'unable-to-load-content'
							)}
						/>
					) : trimmedSearchTerm ? (
						<ClayEmptyState
							description={sub(
								Liferay.Language.get(
									'there-are-no-results-for-x'
								),
								[trimmedSearchTerm]
							)}
							imgSrc={`${themeDisplay.getPathThemeImages()}/states/search_state.svg`}
							small
							title={Liferay.Language.get('no-results-found')}
						/>
					) : (
						<ClayEmptyState
							description={Liferay.Language.get(
								'there-are-no-fieldsets-description'
							)}
							imgSrc={`${themeDisplay.getPathThemeImages()}/states/empty_state.svg`}
							small
							title={Liferay.Language.get(
								'there-are-no-fieldsets'
							)}
						>
							<CreateNewFieldsetButton />
						</ClayEmptyState>
					)}
				</div>
			)}
			{modalState.isVisible && (
				<FieldSetModal
					fieldSet={modalState.fieldSet}
					onClose={toggleFieldSet}
				/>
			)}
		</>
	);
}
