/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayAlert from '@clayui/alert';
import ClayButton from '@clayui/button';
import {TreeView} from '@clayui/core';
import {ClayCheckbox, ClayRadio, ClayRadioGroup} from '@clayui/form';
import {LearnMessage, LearnResourcesContext} from 'frontend-js-components-web';
import React, {useMemo, useState} from 'react';

const SELECT_OPTIONS = {
	ALL: '', //  When 'vocabularyIds' is equal to this, 'All Vocabularies' has been selected.
	SELECT: 'SELECT',
};

/**
 * Converts a stringified list of IDs into an array of IDs. This separates
 * values by commas and filters out empty values.
 * @param {string} idsString The list of IDs as a string
 * @return {Array} Array of IDs as strings
 */
const transformCommaStringToArray = (commaString) =>
	commaString.split(',').filter((item) => item !== '');

function VocabularyTree({selectedKeys, setSelectedKeys, vocabularyTree}) {
	const _handleSelect = (list, add = true) => {
		const newList = new Set(selectedKeys);

		list.forEach(({externalReferenceCode}) => {
			if (add) {
				newList.add(externalReferenceCode);
			}
			else {
				newList.delete(externalReferenceCode);
			}
		});

		setSelectedKeys(newList);
	};

	const _handleToggle = (item) => {
		_handleSelect([item], !selectedKeys.has(item.externalReferenceCode));
	};

	return vocabularyTree.length ? (
		<TreeView
			defaultItems={vocabularyTree}
			nestedKey="children"
			onSelectionChange={setSelectedKeys}
			selectedKeys={selectedKeys}
			selectionHydrationMode="render-first"
			selectionMode="multiple"
			showExpanderOnHover={false}
		>
			{(item) => (
				<TreeView.Item
					actions={
						<div className="d-flex">
							<ClayButton
								aria-label={Liferay.Language.get('select-all')}
								className="c-mr-1 quick-action-item"
								displayType="secondary"
								onClick={(event) => {
									event.preventDefault();

									_handleSelect(item.children, true);
								}}
								onKeyDown={(event) => {
									if (
										event.key === 'Enter' ||
										event.key === ' '
									) {
										event.preventDefault();

										_handleSelect(item.children, true);
									}
								}}
								size="xs"
								type="submit"
							>
								{Liferay.Language.get('select-all')}
							</ClayButton>

							<ClayButton
								aria-label={Liferay.Language.get(
									'deselect-all'
								)}
								displayType="secondary quick-action-item"
								onClick={(event) => {
									event.preventDefault();

									_handleSelect(item.children, false);
								}}
								onKeyDown={(event) => {
									if (
										event.key === 'Enter' ||
										event.key === ' '
									) {
										event.preventDefault();

										_handleSelect(item.children, false);
									}
								}}
								size="xs"
							>
								{Liferay.Language.get('deselect-all')}
							</ClayButton>
						</div>
					}
					key={item.externalReferenceCode}
				>
					<TreeView.ItemStack>{item.name}</TreeView.ItemStack>

					{item.children?.length ? (
						<TreeView.Group items={item.children}>
							{({externalReferenceCode, name}) => (
								<TreeView.Item
									key={externalReferenceCode}
									style={{cursor: 'unset'}}
								>
									<ClayCheckbox
										checked={selectedKeys.has(
											externalReferenceCode
										)}
										onChange={() => _handleToggle(item)}
									/>

									{name}
								</TreeView.Item>
							)}
						</TreeView.Group>
					) : (
						<TreeView.Group>
							<TreeView.Item>
								{Liferay.Language.get('no-vocabularies')}
							</TreeView.Item>
						</TreeView.Group>
					)}
				</TreeView.Item>
			)}
		</TreeView>
	) : (
		<span className="c-mb-0 sheet-text text-3">
			{Liferay.Language.get(
				'an-error-has-occurred-and-we-were-unable-to-load-the-results'
			)}
		</span>
	);
}

function SelectVocabularies({
	groups = [],
	initialSelectedVocabularyExternalReferenceCodes = SELECT_OPTIONS.ALL,
	namespace = '',
	vocabularyExternalReferenceCodesInputName = '',
}) {
	const initialSelectedVocabularyExternalReferenceCodesSet = useMemo(() => {
		return new Set(
			initialSelectedVocabularyExternalReferenceCodes ===
			SELECT_OPTIONS.ALL
				? []
				: transformCommaStringToArray(
						initialSelectedVocabularyExternalReferenceCodes
					)
		);
	}, [initialSelectedVocabularyExternalReferenceCodes]);

	const [selection, setSelection] = useState(
		initialSelectedVocabularyExternalReferenceCodes === SELECT_OPTIONS.ALL
			? SELECT_OPTIONS.ALL
			: SELECT_OPTIONS.SELECT
	);
	const [selectedKeys, setSelectedKeys] = useState(
		initialSelectedVocabularyExternalReferenceCodesSet
	);

	const unavailableVocabularyExternalReferenceCodes = useMemo(() => {
		const fetchedExternalReferenceCodes = groups.flatMap(
			(group) =>
				group.children?.map(
					({externalReferenceCode}) => externalReferenceCode
				) || []
		);

		return Array.from(
			initialSelectedVocabularyExternalReferenceCodesSet
		).filter(
			(externalReferenceCode) =>
				!fetchedExternalReferenceCodes.includes(externalReferenceCode)
		);
	}, [groups, initialSelectedVocabularyExternalReferenceCodesSet]);

	const _handleDeselectUnavailableVocabularyExternalReferenceCodes = () => {
		const newList = new Set(selectedKeys);

		unavailableVocabularyExternalReferenceCodes.forEach(
			(externalReferenceCode) => {
				newList.delete(externalReferenceCode);
			}
		);

		setSelectedKeys(newList);
	};

	const _isUnavailableVocabularyExternalReferenceCodesSelected = () =>
		unavailableVocabularyExternalReferenceCodes.some(
			(externalReferenceCode) => selectedKeys.has(externalReferenceCode)
		);

	return (
		<div className="select-vocabularies">
			<label>{Liferay.Language.get('select-vocabularies')}</label>

			<input
				hidden
				id={`${namespace}${vocabularyExternalReferenceCodesInputName}`}
				name={`${namespace}${vocabularyExternalReferenceCodesInputName}`}
				readOnly
				value={
					selection === SELECT_OPTIONS.ALL
						? SELECT_OPTIONS.ALL
						: Array.from(selectedKeys)
								.filter((item) => item.includes('&&')) // Filter out site headers ClayTreeView may select
								.toString()
				}
			/>

			<div className="c-mb-3 c-mt-2 sheet-text text-3">
				<span>
					{Liferay.Language.get(
						'select-vocabularies-configuration-description'
					)}
				</span>

				<LearnMessage
					className="c-ml-1"
					resource="portal-search-web"
					resourceKey="tag-and-category-facet"
				/>
			</div>

			<ClayRadioGroup onChange={setSelection} value={selection}>
				<ClayRadio
					label={Liferay.Language.get('all-vocabularies')}
					value={SELECT_OPTIONS.ALL}
				/>

				<ClayRadio
					label={Liferay.Language.get('select-vocabularies')}
					value={SELECT_OPTIONS.SELECT}
				/>
			</ClayRadioGroup>

			{selection === SELECT_OPTIONS.SELECT &&
				!!unavailableVocabularyExternalReferenceCodes.length && (
					<>
						{_isUnavailableVocabularyExternalReferenceCodesSelected() ? (
							<ClayAlert
								displayType="info"
								style={{opacity: 1}}
								title={`${Liferay.Language.get('info')}:`}
							>
								{Liferay.Language.get(
									'select-vocabularies-configuration-alert'
								)}

								<LearnMessage
									className="c-ml-1"
									resource="portal-search-web"
									resourceKey="tag-and-category-facet"
								/>

								<ClayAlert.Footer>
									<ClayButton
										alert
										onClick={
											_handleDeselectUnavailableVocabularyExternalReferenceCodes
										}
									>
										{Liferay.Language.get(
											'remove-unavailable-vocabularies'
										)}
									</ClayButton>
								</ClayAlert.Footer>
							</ClayAlert>
						) : (
							<ClayAlert
								displayType="success"
								style={{opacity: 1}}
								title={`${Liferay.Language.get('success')}:`}
							>
								{Liferay.Language.get(
									'unavailable-vocabularies-removed-from-selection'
								)}
							</ClayAlert>
						)}
					</>
				)}

			{selection === SELECT_OPTIONS.SELECT && (
				<VocabularyTree
					selectedKeys={selectedKeys}
					setSelectedKeys={setSelectedKeys}
					vocabularyTree={groups}
				/>
			)}
		</div>
	);
}

export default function ({
	groups,
	initialSelectedVocabularyExternalReferenceCodes,
	learnResources,
	namespace,
	vocabularyExternalReferenceCodesInputName,
}) {
	return (
		<LearnResourcesContext.Provider value={learnResources}>
			<SelectVocabularies
				groups={groups}
				initialSelectedVocabularyExternalReferenceCodes={
					initialSelectedVocabularyExternalReferenceCodes
				}
				namespace={namespace}
				vocabularyExternalReferenceCodesInputName={
					vocabularyExternalReferenceCodesInputName
				}
			/>
		</LearnResourcesContext.Provider>
	);
}
