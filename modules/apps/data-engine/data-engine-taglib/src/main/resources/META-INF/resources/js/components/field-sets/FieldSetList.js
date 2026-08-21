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
	useConfig,
	useForm,
	useFormState,
} from 'data-engine-js-components-web';
import React, {useEffect, useState} from 'react';

import {getItems} from '../../utils/client.es';
import {getLocalizedValue, getPluralMessage, sub} from '../../utils/lang.es';
import FieldType from '../field-types/FieldType.es';
import FieldSetModal from './FieldSetModal';
import useDeleteFieldSet from './actions/useDeleteFieldSet.es';
import usePropagateFieldSet from './actions/usePropagateFieldSet.es';

function getSortedFieldsets(fieldsets) {
	return [...fieldsets].sort((a, b) => {
		const localizedValueA = getLocalizedValue(a.defaultLanguageId, a.name);
		const localizedValueB = getLocalizedValue(b.defaultLanguageId, b.name);

		return localizedValueA.localeCompare(localizedValueB);
	});
}

export default function FieldSetList({searchTerm}) {
	const [modalState, setModalState] = useState({isVisible: false});
	const {fieldSets} = useFormState();
	const {dataDefinition} = useFormState({schema: ['dataDefinition']});
	const dispatch = useForm();
	const deleteFieldSet = useDeleteFieldSet();
	const propagateFieldSet = usePropagateFieldSet();

	const {contentType, dataDefinitionId, groupId} = useConfig();
	const [loading, setLoading] = useState(false);
	const [searchResults, setSearchResults] = useState(null);

	useEffect(() => {
		if (!searchTerm) {
			setLoading(false);
			setSearchResults(null);

			return;
		}

		const abortController = new AbortController();

		const timeoutId = setTimeout(async () => {
			setLoading(true);

			try {
				const {signal} = abortController;

				const siteFieldSetsPromise = groupId
					? getItems(
							`/o/data-engine/v2.0/sites/${groupId}/data-definitions/by-content-type/${contentType}`,
							searchTerm,
							{signal}
						)
					: Promise.resolve([]);

				const globalFieldSetsPromise =
					Number(groupId) === Number(themeDisplay.getCompanyGroupId())
						? Promise.resolve([])
						: getItems(
								`/o/data-engine/v2.0/data-definitions/by-content-type/${contentType}`,
								searchTerm,
								{signal}
							);

				const [siteFieldSets, globalFieldSets] = await Promise.all([
					siteFieldSetsPromise,
					globalFieldSetsPromise,
				]);

				if (!signal.aborted) {
					setSearchResults(
						[...siteFieldSets, ...globalFieldSets].filter(
							({id}) => id !== parseInt(dataDefinitionId, 10)
						)
					);
				}
			}
			catch (error) {
				if (!abortController.signal.aborted) {
					if (process.env.NODE_ENV === 'development') {
						console.warn('[FieldSetList] search failed:', error);
					}

					setSearchResults([]);
				}
			}
			finally {

				// Skip when aborted: the next effect run (new search term) or
				// the empty-term early return will reset loading correctly.

				if (!abortController.signal.aborted) {
					setLoading(false);
				}
			}
		}, 300);

		return () => {
			abortController.abort();
			clearTimeout(timeoutId);
		};
	}, [contentType, dataDefinitionId, groupId, searchTerm]);

	const displayedFieldsets = getSortedFieldsets(
		searchTerm ? searchResults ?? [] : fieldSets
	);

	const isPendingSearch = !!searchTerm && searchResults === null && !loading;

	const fieldSetsInUse = new Set();
	dataDefinition.dataDefinitionFields.forEach(
		({customProperties: {ddmStructureId}, fieldType}) => {
			if (fieldType === 'fieldset') {
				fieldSetsInUse.add(parseInt(ddmStructureId, 10));
			}
		}
	);

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
			{!!displayedFieldsets.length || loading || isPendingSearch ? (
				<>
					<CreateNewFieldsetButton />

					<div className="mt-3">
						{loading || isPendingSearch ? (
							<ClayLoadingIndicator />
						) : (
							displayedFieldsets.map((fieldSet) => {
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
												onPropagate: deleteFieldSet,
											}),
										name: Liferay.Language.get('delete'),
									},
								];
								const description = getPluralMessage(
									Liferay.Language.get('x-field'),
									Liferay.Language.get('x-fields'),
									fieldSet.dataDefinitionFields.length
								);
								const disabled = fieldSetsInUse.has(
									fieldSet.id
								);
								const label = getLocalizedValue(
									fieldSet.defaultLanguageId,
									fieldSet.name
								);
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
							})
						)}
					</div>
				</>
			) : (
				<div className="mt-2">
					{searchTerm ? (
						<ClayEmptyState
							description={sub(
								Liferay.Language.get(
									'there-are-no-results-for-x'
								),
								[searchTerm]
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
