/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayAlert from '@clayui/alert';
import ClayButton from '@clayui/button';
import {TreeView} from '@clayui/core';
import {ClayCheckbox, ClayRadio, ClayRadioGroup} from '@clayui/form';
import ClayLoadingIndicator from '@clayui/loading-indicator';
import {LearnMessage, LearnResourcesContext} from 'frontend-js-components-web';
import {fetch} from 'frontend-js-web';
import React, {useEffect, useMemo, useState} from 'react';

const CONFIGURATION = {
	headers: new Headers({
		'Accept': 'application/json',
		'Accept-Language': Liferay.ThemeDisplay.getBCP47LanguageId(),
		'Content-Type': 'application/json',
	}),
	method: 'GET',
};

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

function VocabularyTree({
	loading,
	selectedKeys,
	setSelectedKeys,
	vocabularyTree,
}) {
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

	if (loading || vocabularyTree === null) {
		return <ClayLoadingIndicator displayType="secondary" size="sm" />;
	}

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
					<TreeView.ItemStack>
						{item.descriptiveName_i18n?.[
							Liferay.ThemeDisplay.getLanguageId()
						] || item.descriptiveName}
					</TreeView.ItemStack>

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
	const [
		unavailableVocabularyExternalReferenceCodes,
		setUnavailableVocabularyExternalReferenceCodes,
	] = useState([]);
	const [vocabularyTree, setVocabularyTree] = useState(null);
	const [vocabularyTreeLoading, setVocabularyTreeLoading] = useState(false);

	useEffect(() => {
		if (selection === SELECT_OPTIONS.SELECT) {
			_handleFetchVocabularyTree();
		}
	}, []); //eslint-disable-line

	const _handleFetchVocabularyTree = () => {
		setVocabularyTreeLoading(true);

		fetch(`/api/jsonws/invoke`, {
			body: new URLSearchParams({
				cmd: JSON.stringify({
					'/group/get-user-sites-groups': {},
				}),
				p_auth: Liferay.authToken,
			}),
			headers: new Headers({
				'Accept-Language': Liferay.ThemeDisplay.getBCP47LanguageId(),
				'Content-Type':
					'application/x-www-form-urlencoded;charset=UTF-8',
			}),
			method: 'POST',
		})
			.then((response) => response.json())
			.then((items) => {

				// Filter out results that are not a site.

				const itemsFilteredForSites = items.filter(({site}) => !!site);

				Promise.all(
					itemsFilteredForSites.map((site) =>
						fetch(
							`/o/headless-admin-taxonomy/v1.0/sites/${site.groupId}/taxonomy-vocabularies?page=0&pageSize=0`,
							CONFIGURATION
						).then((response) => response.json())
					)
				)
					.then((responses) => {
						const fetchedExternalReferenceCodes = [];

						setVocabularyTree(
							responses.map((response, index) => ({
								...itemsFilteredForSites[index],
								children: (response?.items || [])
									.filter(({siteId}) => {

										// Filter out global vocabularies for
										// non-global sites.

										const isGlobalSite =
											itemsFilteredForSites[index]
												.groupId ===
											Liferay.ThemeDisplay.getCompanyGroupId();

										if (
											!isGlobalSite &&
											siteId?.toString() ===
												Liferay.ThemeDisplay.getCompanyGroupId()
										) {
											return false;
										}

										return true;
									})
									.map((item) => {
										const externalReferenceCode = `${itemsFilteredForSites[index].externalReferenceCode}&&${item.externalReferenceCode}`;

										fetchedExternalReferenceCodes.push(
											externalReferenceCode
										); // Collect ExternalReferenceCodes to allow deselection of unavailable vocabularies

										return {
											externalReferenceCode,
											id: item.id.toString(),
											name: item.name,
										};
									}),
							}))
						);

						setUnavailableVocabularyExternalReferenceCodes(
							Array.from(
								initialSelectedVocabularyExternalReferenceCodesSet
							).filter(
								(initialSelectedExternalReferenceCode) =>
									!fetchedExternalReferenceCodes.includes(
										initialSelectedExternalReferenceCode
									)
							)
						);
					})
					.catch(() => setVocabularyTree([]));
			})
			.catch(() => setVocabularyTree([]))
			.finally(() => setVocabularyTreeLoading(false));
	};

	const _handleDeselectUnavailableVocabularyExternalReferenceCodes = () => {
		const newList = new Set(selectedKeys);

		unavailableVocabularyExternalReferenceCodes.forEach(
			(externalReferenceCode) => {
				newList.delete(externalReferenceCode);
			}
		);

		setSelectedKeys(newList);
	};

	const _handleSelectionChange = (value) => {
		setSelection(value);

		if (value === SELECT_OPTIONS.SELECT && !vocabularyTree) {
			_handleFetchVocabularyTree();
		}
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

			<ClayRadioGroup onChange={_handleSelectionChange} value={selection}>
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
					loading={vocabularyTreeLoading}
					selectedKeys={selectedKeys}
					setSelectedKeys={setSelectedKeys}
					vocabularyTree={vocabularyTree}
				/>
			)}
		</div>
	);
}

export default function ({
	initialSelectedVocabularyExternalReferenceCodes,
	learnResources,
	namespace,
	vocabularyExternalReferenceCodesInputName,
}) {
	return (
		<LearnResourcesContext.Provider value={learnResources}>
			<SelectVocabularies
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
