/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayAlert from '@clayui/alert';
import ClayButton from '@clayui/button';
import ClayForm, {ClayRadio, ClayRadioGroup} from '@clayui/form';
import ClayLoadingIndicator from '@clayui/loading-indicator';
import ClayModal from '@clayui/modal';
import {useIsMounted} from '@liferay/frontend-js-react-web';
import {AssetCategoriesSelector} from 'asset-taglib';
import {fetch, sub} from 'frontend-js-web';
import PropTypes from 'prop-types';
import React, {useCallback, useContext, useEffect, useState} from 'react';

import EditCategoriesContext from './EditCategoriesContext';

const URL_SELECTION = '/bulk/v1.0/bulk-selection';
const URL_UPDATE_CATEGORIES = '/bulk/v1.0/taxonomy-categories/batch';

const EditCategoriesModal = ({
	fileEntries,
	folderId,
	groupIds,
	hiddenInput = 'assetCategoriesIds_',
	observer,
	onModalClose,
	pathModule,
	repositoryId,
	selectAll = false,
	selectCategoriesUrl,
}) => {
	const {namespace} = useContext(EditCategoriesContext);

	const [append, setAppend] = useState(true);

	const [description, setDescription] = useState('');

	const [initialCategories, setInitialCategories] = useState([]);

	const [isValid, setIsValid] = useState(true);

	const [loading, setLoading] = useState(false);

	const [multiple, setMultiple] = useState(false);

	const [vocabularies, setVocabularies] = useState([]);

	const [selectedRadioGroupValue, setSelectedRadioGroupValue] =
		useState('add');

	const bulkStatusComponent = Liferay.component(`${namespace}BulkStatus`);

	const getDescription = (size) => {
		if (size === 1) {
			return Liferay.Language.get(
				'you-are-editing-the-categories-for-the-selected-item'
			);
		}

		return sub(
			Liferay.Language.get(
				'you-are-editing-the-common-categories-for-x-items.-select-edit-or-replace-current-categories'
			),
			size
		);
	};

	const getFinalCategories = () => {
		const categories = new Set();

		vocabularies.forEach(({selectedItems}) => {
			selectedItems.map(({value}) => {
				categories.add(parseInt(value, 10));
			});
		});

		return Array.from(categories);
	};

	const handleMultiSelectOptionChange = (value) => {
		setAppend(value === 'add');
		setSelectedRadioGroupValue(value);
	};

	const handleVocabulariesChange = (newVocabularies) => {
		const requiredVocabularies = newVocabularies.filter(
			(vocabulary) =>
				vocabulary.required && !vocabulary.selectedItems.length
		);

		const isInvalid = requiredVocabularies.length
			? requiredVocabularies.some(
					(item) => item.required && !item.selectedItems.length
				)
			: false;

		setIsValid(!isInvalid);
		setVocabularies(newVocabularies);
	};

	const fetchCategories = useCallback(
		(url, method, bodyData) => {
			const init = {
				body: JSON.stringify(bodyData),
				headers: {
					'Content-Type': 'application/json',
				},
				method,
			};

			return fetch(`${pathModule}${url}`, init)
				.then((response) =>
					response.status === 204 ? '' : response.json()
				)
				.catch(() => {
					onModalClose();
				});
		},
		[onModalClose, pathModule]
	);

	const handleSubmit = (event) => {
		event.preventDefault();

		const finalCategories = getFinalCategories();

		let addedCategories = [];

		if (!append) {
			addedCategories = finalCategories;
		}
		else {
			addedCategories = finalCategories.filter(
				(categoryId) => initialCategories.indexOf(categoryId) === -1
			);
		}

		const removedCategories = initialCategories.filter(
			(category) => finalCategories.indexOf(category) === -1
		);

		fetchCategories(URL_UPDATE_CATEGORIES, append ? 'PATCH' : 'PUT', {
			documentBulkSelection: {
				documentIds: fileEntries,
				selectionScope: {
					folderId,
					repositoryId,
					selectAll,
				},
			},
			taxonomyCategoryIdsToAdd: addedCategories,
			taxonomyCategoryIdsToRemove: removedCategories,
		}).then(() => {
			onModalClose();

			if (bulkStatusComponent) {
				bulkStatusComponent.startWatch();
			}
		});
	};

	const isMounted = useIsMounted();

	const parseCategories = (categories) => {
		return categories.map((item) => {
			return {
				label: item.taxonomyCategoryName,
				value: item.taxonomyCategoryId,
			};
		});
	};

	const parseVocabularies = useCallback((vocabularies) => {
		let initialCategories = [];
		const requiredVocabularies = [];
		const vocabulariesList = [];

		vocabularies.forEach((vocabulary) => {
			const categories = parseCategories(
				vocabulary.taxonomyCategories || []
			);

			const categoryIds = categories.map((item) => item.value);

			const object = {
				id: vocabulary.taxonomyVocabularyId.toString(),
				required: vocabulary.required,
				selectedCategoryIds: categoryIds.join(','),
				selectedItems: categories,
				singleSelect: !vocabulary.multiValued,
				title: vocabulary.name,
			};

			vocabulariesList.push(object);

			if (vocabulary.required) {
				requiredVocabularies.push(object);
			}

			initialCategories = initialCategories.concat(categoryIds);
		});

		setInitialCategories(initialCategories);

		return vocabulariesList;
	}, []);

	useEffect(() => {
		const selection = {
			documentIds: fileEntries,
			selectionScope: {
				folderId,
				repositoryId,
				selectAll,
			},
		};

		const urlCategories = `/bulk/v1.0/sites/${groupIds[0]}/taxonomy-vocabularies/common`;

		Promise.all([
			fetchCategories(urlCategories, 'POST', selection),
			fetchCategories(URL_SELECTION, 'POST', selection),
		]).then(([responseCategories, responseSelection]) => {
			if (responseCategories && responseSelection) {
				if (isMounted()) {
					setLoading(false);
					setDescription(getDescription(responseSelection.size));
					setMultiple(fileEntries.length > 1 || selectAll);
					setVocabularies(
						parseVocabularies(responseCategories.items || [])
					);
				}
			}
		});
	}, [
		fetchCategories,
		fileEntries,
		fileEntries.length,
		folderId,
		groupIds,
		isMounted,
		parseVocabularies,
		repositoryId,
		selectAll,
	]);

	return (
		<ClayModal
			className="portlet-document-library-edit-categories"
			observer={observer}
			size="md"
		>
			<ClayModal.Header
				closeButtonAriaLabel={Liferay.Language.get('close')}
			>
				{Liferay.Language.get('edit-categories')}
			</ClayModal.Header>

			<ClayForm id={`${namespace}form`} onSubmit={handleSubmit}>
				<ClayModal.Body scrollable>
					{loading && <ClayLoadingIndicator />}

					{selectAll && (
						<ClayAlert title="">
							{Liferay.Language.get(
								'this-operation-will-not-be-applied-to-any-of-the-selected-folders'
							)}
						</ClayAlert>
					)}

					<p>{description}</p>

					{multiple && (
						<ClayRadioGroup
							name="add-replace"
							onChange={handleMultiSelectOptionChange}
							value={selectedRadioGroupValue}
						>
							<ClayRadio
								checked="true"
								label={Liferay.Language.get('edit')}
								value="add"
							>
								<div className="form-text">
									{Liferay.Language.get(
										'add-new-categories-or-remove-common-categories'
									)}
								</div>
							</ClayRadio>

							<ClayRadio
								label={Liferay.Language.get('replace')}
								value="replace"
							>
								<div className="form-text">
									{Liferay.Language.get(
										'these-categories-replace-all-existing-categories'
									)}
								</div>
							</ClayRadio>
						</ClayRadioGroup>
					)}

					{vocabularies && (
						<AssetCategoriesSelector
							eventName={`${namespace}selectCategories`}
							groupIds={groupIds}
							id={`${namespace}assetTagsSelector`}
							inputName={`${namespace}${hiddenInput}`}
							onVocabulariesChange={handleVocabulariesChange}
							portletURL={selectCategoriesUrl}
							useFallbackInput={true}
							vocabularies={vocabularies}
						/>
					)}
				</ClayModal.Body>

				<ClayModal.Footer
					last={
						<ClayButton.Group spaced>
							<ClayButton
								displayType="secondary"
								onClick={onModalClose}
							>
								{Liferay.Language.get('cancel')}
							</ClayButton>

							<ClayButton
								disabled={!isValid}
								displayType="primary"
								type="submit"
							>
								{Liferay.Language.get('save')}
							</ClayButton>
						</ClayButton.Group>
					}
				/>
			</ClayForm>
		</ClayModal>
	);
};

EditCategoriesModal.propTypes = {
	fileEntries: PropTypes.array.isRequired,
	folderId: PropTypes.string.isRequired,
	groupIds: PropTypes.array.isRequired,
	hiddenInput: PropTypes.string,
	observer: PropTypes.object.isRequired,
	onModalClose: PropTypes.func.isRequired,
	pathModule: PropTypes.string.isRequired,
	repositoryId: PropTypes.string.isRequired,
	selectAll: PropTypes.bool,
	selectCategoriesUrl: PropTypes.string.isRequired,
};

export default EditCategoriesModal;
