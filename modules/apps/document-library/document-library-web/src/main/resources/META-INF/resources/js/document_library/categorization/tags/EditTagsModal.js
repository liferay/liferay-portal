/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayAlert from '@clayui/alert';
import ClayButton from '@clayui/button';
import {ClayRadio, ClayRadioGroup} from '@clayui/form';
import ClayLoadingIndicator from '@clayui/loading-indicator';
import ClayModal from '@clayui/modal';
import {useIsMounted} from '@liferay/frontend-js-react-web';
import {AssetTagsSelector} from 'asset-taglib';
import {fetch, sub} from 'frontend-js-web';
import PropTypes from 'prop-types';
import React, {useCallback, useContext, useEffect, useState} from 'react';

import EditTagsContext from './EditTagsContext';

const URL_SELECTION = '/bulk/v1.0/bulk-selection';
const URL_TAGS = '/bulk/v1.0/keywords/common';
const URL_UPDATE_TAGS = '/bulk/v1.0/keywords/batch';

const noop = () => {};

const EditTagsModal = ({
	fileEntries,
	folderId,
	groupIds,
	pathModule,
	repositoryId,
	selectAll = false,
	observer,
	onModalClose = noop,
}) => {
	const {namespace} = useContext(EditTagsContext);

	const isMounted = useIsMounted();

	// Flag that indicates whether new selected items must be added to old ones
	// or replace them.

	const [append, setAppend] = useState(true);
	const [description, setDescription] = useState('');
	const [loading, setLoading] = useState(true);
	const [multiple, setMultiple] = useState(false);

	// Selected items received from the server and saved to compare with new
	// ones.

	const [initialSelectedItems, setInitialSelectedItems] = useState([]);
	const [inputValue, setInputValue] = useState('');

	// Current selected items.

	const [selectedItems, setSelectedItems] = useState([]);
	const [selectedRadioGroupValue, setSelectedRadioGroupValue] =
		useState('add');

	const fileEntriesLength = fileEntries && fileEntries.length;

	const fetchTags = useCallback(
		(url, method, bodyData) => {
			const init = {
				body: JSON.stringify(bodyData),
				headers: {'Content-Type': 'application/json'},
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

	const getDescription = (size) => {
		if (size === 1) {
			return Liferay.Language.get(
				'you-are-editing-the-tags-for-the-selected-item'
			);
		}

		return sub(
			Liferay.Language.get(
				'you-are-editing-the-common-tags-for-x-items.-select-edit-or-replace-current-tags'
			),
			size
		);
	};

	// This makes the component fetch selected items only after mounting it
	// (a.k.a. first render).

	useEffect(() => {
		const selection = {
			documentIds: fileEntries,
			selectionScope: {
				folderId,
				repositoryId,
				selectAll,
			},
		};

		Promise.all([
			fetchTags(URL_TAGS, 'POST', selection),
			fetchTags(URL_SELECTION, 'POST', selection),
		]).then(([responseTags, responseSelection]) => {
			if (responseTags && responseSelection) {
				const selectedItems = (responseTags.items || []).map(
					({name}) => ({
						label: name,
						value: name,
					})
				);

				if (isMounted()) {
					setLoading(false);
					setInitialSelectedItems(selectedItems);
					setSelectedItems(selectedItems);
					setDescription(getDescription(responseSelection.size));
					setMultiple(fileEntries.length > 1 || selectAll);
				}
			}
		});
	}, [
		fetchTags,
		fileEntries,
		fileEntriesLength,
		folderId,
		isMounted,
		repositoryId,
		selectAll,
	]);

	const handleMultipleSelectedOptionChange = (value) => {
		setAppend(value === 'add');
		setSelectedRadioGroupValue(value);
	};

	const handleSubmit = (event) => {
		event.preventDefault();

		const addedLabels = !append
			? selectedItems
			: selectedItems.filter(
					(selectedItem) =>
						!initialSelectedItems.find(
							(initialSelectedItem) =>
								initialSelectedItem.value === selectedItem.value
						)
				);

		const removedLabels = initialSelectedItems.filter(
			(initialSelectedItem) =>
				!selectedItems.find(
					(selectedItem) =>
						selectedItem.value === initialSelectedItem.value
				)
		);

		fetchTags(URL_UPDATE_TAGS, append ? 'PATCH' : 'PUT', {
			documentBulkSelection: {
				documentIds: fileEntries,
				selectionScope: {
					folderId,
					repositoryId,
					selectAll,
				},
			},
			keywordsToAdd: addedLabels.map((addedLabel) => addedLabel.value),
			keywordsToRemove: removedLabels.map(
				(removedLabel) => removedLabel.value
			),
		}).then(() => {
			const bulkStatusComponent = Liferay.component(
				`${namespace}BulkStatus`
			);
			if (bulkStatusComponent) {
				bulkStatusComponent.startWatch();
			}

			onModalClose();
		});
	};

	return (
		<ClayModal observer={observer} size="md">
			<ClayModal.Header
				closeButtonAriaLabel={Liferay.Language.get('close')}
			>
				{Liferay.Language.get('edit-tags')}
			</ClayModal.Header>

			<form onSubmit={handleSubmit}>
				<ClayModal.Body>
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
							onChange={handleMultipleSelectedOptionChange}
							value={selectedRadioGroupValue}
						>
							<ClayRadio
								label={Liferay.Language.get('edit')}
								value="add"
							>
								<div className="form-text">
									{Liferay.Language.get(
										'add-new-tags-or-remove-common-tags'
									)}
								</div>
							</ClayRadio>

							<ClayRadio
								label={Liferay.Language.get('replace')}
								value="replace"
							>
								<div className="form-text">
									{Liferay.Language.get(
										'these-tags-replace-all-existing-tags'
									)}
								</div>
							</ClayRadio>
						</ClayRadioGroup>
					)}

					<AssetTagsSelector
						groupIds={groupIds}
						inputName={`${namespace}_hiddenInput`}
						inputValue={inputValue}
						onInputValueChange={setInputValue}
						onSelectedItemsChange={setSelectedItems}
						selectedItems={selectedItems}
					/>
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

							<ClayButton displayType="primary" type="submit">
								{Liferay.Language.get('save')}
							</ClayButton>
						</ClayButton.Group>
					}
				/>
			</form>
		</ClayModal>
	);
};

EditTagsModal.propTypes = {
	fileEntries: PropTypes.array,
	folderId: PropTypes.string,
	groupIds: PropTypes.array,
	id: PropTypes.string,
	pathModule: PropTypes.string,
	repositoryId: PropTypes.string,
	selectAll: PropTypes.bool,
};

export default EditTagsModal;
