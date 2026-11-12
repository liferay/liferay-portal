/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import PropTypes from 'prop-types';
import React from 'react';

import AssetVocabularyCategoriesSelector from './AssetVocabularyCategoriesSelector';

function AssetCategoriesSelector({
	groupIds,
	id,
	inputName,
	onVocabulariesChange,
	showLabel,
	useDataCategoriesAttribute,
	useFallbackInput,
	vocabularies = [],
}) {
	return (
		<div id={id}>
			{vocabularies.map((vocabulary, index) => {
				const label = vocabulary.group
					? `${vocabulary.title} (${vocabulary.group})`
					: vocabulary.title;
				const vocabularyInputName = inputName + vocabulary.id;

				return (
					<AssetVocabularyCategoriesSelector
						groupIds={groupIds}
						id={`namespace_assetCategoriesSelector_${vocabulary.id}`}
						inputName={vocabularyInputName}
						key={vocabulary.id}
						label={label}
						onSelectedItemsChange={(selectedItems) => {
							const newVocabulary = {
								...vocabulary,
								selectedItems,
							};

							onVocabulariesChange([
								...vocabularies.slice(0, index),
								newVocabulary,
								...vocabularies.slice(index + 1),
							]);
						}}
						required={vocabulary.required}
						selectedItems={
							vocabulary.selectedItems
								? [...vocabulary.selectedItems]
								: []
						}
						showAddCategoryButton={vocabulary.showAddCategoryButton}
						showLabel={showLabel}
						singleSelect={vocabulary.singleSelect}
						sourceItemsVocabularyIds={[vocabulary.id]}
						useDataCategoriesAttribute={useDataCategoriesAttribute}
						useFallbackInput={useFallbackInput}
					/>
				);
			})}
		</div>
	);
}

AssetCategoriesSelector.propTypes = {
	groupIds: PropTypes.array.isRequired,
	id: PropTypes.string.isRequired,
	inputName: PropTypes.string.isRequired,
	label: PropTypes.string,
	onVocabulariesChange: PropTypes.func.isRequired,
	showLabel: PropTypes.bool,
	useDataCategoriesAttribute: PropTypes.bool,
	useFallbackInput: PropTypes.bool,
};

export default AssetCategoriesSelector;
