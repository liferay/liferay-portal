/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayAutocomplete from '@clayui/autocomplete';
import ClayButton from '@clayui/button';
import ClayDropDown from '@clayui/drop-down';
import {ClayInput} from '@clayui/form';
import ClayMultiSelect from '@clayui/multi-select';
import {fetch} from 'frontend-js-web';
import React, {useEffect, useMemo, useState} from 'react';

import useDebounceCallback from '../../../hooks/useDebounceCallback';
import {
	ASSET_CATEGORY_EXTERNAL_REFERENCE_CODE,
	ASSET_CATEGORY_EXTERNAL_REFERENCE_CODES,
} from '../../../utils/constants';
import {DEFAULT_HEADERS} from '../../../utils/fetch/fetch_data';
import removeDuplicates from '../../../utils/functions/remove_duplicates';
import toNumber from '../../../utils/functions/to_number';
import {IDENTIFIER_TYPES} from '../../../utils/types/identifierTypes';
import CategorySelectorModal from './CategorySelectorModal';

export const FETCH_URLS = {
	getCategories: (id) =>
		`/o/headless-admin-taxonomy/v1.0/taxonomy-vocabularies/${id}/taxonomy-categories`,
	getCategory: (id) =>
		`/o/headless-admin-taxonomy/v1.0/taxonomy-categories/${id}/`,
	getSites: () => '/o/headless-admin-user/v1.0/my-user-account/sites',
	getSubCategories: (id) =>
		`/o/headless-admin-taxonomy/v1.0/taxonomy-categories/${id}/taxonomy-categories`,
	getVocabularies: (id) =>
		`/o/headless-admin-taxonomy/v1.0/sites/${id}/taxonomy-vocabularies`,
};

/**
 * Returns the name and ERC of the site by matching the siteId to the
 * site inside categoryTree.
 * @param {String} siteId Site ID to get the name of
 * @param {Object} categoryTree Contains the names and IDs of sites
 * @returns {Object}
 */
function getSiteInfo(siteId, categoryTree) {
	const site = categoryTree.find((site) => siteId === site.id);

	return {
		externalReferenceCode: site.externalReferenceCode,
		name: site.descriptiveName || site.name,
	};
}

function CategoryMenu({locator, onItemClick = () => {}, sourceItems}) {
	return (
		<ClayDropDown.ItemList>
			{sourceItems.map((item) => (
				<ClayDropDown.Item
					key={item[locator.value]}
					onClick={() => onItemClick(item)}
				>
					<div className="autofit-col">
						<span className="list-group-text">
							{item[locator.label]}
						</span>

						{item.description && (
							<span className="list-group-subtext">
								{item.description}
							</span>
						)}
					</div>
				</ClayDropDown.Item>
			))}
		</ClayDropDown.ItemList>
	);
}

/**
 * CategorySelectorInput uses APIs in order for the user to quickly find
 * asset categories. Click on the 'select' button to open a modal with
 * the category tree, or type in the input to enable autocomplete.
 *
 * For ERCs, manual entry is not allowed. ERCs are formatted as
 * `${siteERC}&&${categoryERC}`.
 *
 * The category selector renders automatically when:
 *
 * - The field has the 'number' type and its name contains 'asset_category_id'.
 *   For this case, the ID is formatted as a number for 'View Element JSON'.
 *
 * - The field has the 'text' type and its name contains
 *   'asset_category_external_reference_code'.
 *   For this case, the ERC is formatted as string for 'View Element JSON'.
 *
 * - The field has the 'multiselect' type and its name contains 'asset_category_ids'
 *   or 'asset_category_external_reference_codes'.
 *   For this case, the IDs/ERCs are formatted as an array of stringified values for
 *   'View Element JSON'.
 *
 */
function CategorySelectorInput({
	disabled,
	id,
	label,
	multiple = false,
	name,
	setFieldTouched,
	setFieldValue,
	value,
}) {
	const [categoryTree, setCategoryTree] = useState([]);

	const [inputValue, setInputValue] = useState(
		multiple ? '' : value.label || value.toString() || ''
	);
	const [matchingCategories, setMatchingCategories] = useState([]);
	const [autocompleteDropdownActive, setAutocompleteDropdownActive] =
		useState(false);

	const identifierType = useMemo(() => {
		return id.includes(ASSET_CATEGORY_EXTERNAL_REFERENCE_CODE) ||
			id.includes(ASSET_CATEGORY_EXTERNAL_REFERENCE_CODES)
			? IDENTIFIER_TYPES.EXTERNAL_REFERENCE_CODE
			: IDENTIFIER_TYPES.ID;
	}, [id]);

	const _handleSetMatchingCategories = (inputValue, categoryTree) => {
		const categories = [];
		const vocabularyIds = categoryTree
			.map(
				(site) =>
					site.children?.map((vocabulary) => vocabulary.id) || []
			)
			.flat();

		fetch(`/api/jsonws/invoke`, {
			body: new URLSearchParams({
				cmd: JSON.stringify({
					'/assetcategory/search': {
						end: 20,
						groupIds: categoryTree.map((site) => site.id),
						name: `%${inputValue.toLowerCase()}%`,
						start: 0,
						vocabularyIds,
					},
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
			.then((responseContent) => {
				responseContent.forEach(
					({
						categoryId,
						externalReferenceCode,
						groupId,
						name,
						path,
					}) => {

						// Collects id/erc, name, and a description of any categories
						// that match the query. Description has the names of the
						// category's site and vocabulary.

						const siteInfo = getSiteInfo(groupId, categoryTree);
						let label = `${name} (ID: ${categoryId})`;
						let value = categoryId;

						if (
							identifierType ===
							IDENTIFIER_TYPES.EXTERNAL_REFERENCE_CODE
						) {
							label = `${name} (ERC: ${externalReferenceCode})`;
							value = `${siteInfo.externalReferenceCode}&&${externalReferenceCode}`;
						}

						categories.push({
							description: `${siteInfo.name} - ${
								path.split(' > ')?.[0]
							}`, // First word in path is vocabulary name
							label,
							value,
						});
					}
				);
			})
			.catch(() => {}) // Catches response.json() parsing error from 404
			.finally(() => {
				if (
					typeof toNumber(inputValue) === 'number' &&
					identifierType === IDENTIFIER_TYPES.ID
				) {

					// If inputValue is a number, check if it is associated to a
					// category and add it to the list of `matchingCategories`.

					_handleFetchCategoryFromID(
						categories,
						inputValue,
						categoryTree
					);
				}
				else {
					setMatchingCategories(categories);
				}
			});
	};

	const [handleSetMatchingCategoriesDebounced] = useDebounceCallback(
		_handleSetMatchingCategories,
		300
	);

	const _handleBlur = () => {
		setFieldTouched(name);
	};

	const _handleFetchCategoryFromID = (
		categories = [],
		currentInputValue,
		categoryTree
	) => {
		fetch(FETCH_URLS.getCategory(currentInputValue), {
			headers: DEFAULT_HEADERS,
			method: 'GET',
		})
			.then((response) => response.json())
			.then(({id, name, parentTaxonomyVocabulary, siteId}) => {
				if (id) {

					// A match by category ID will be included at the very
					// beginning of the list. Description has the names of
					// the category's site and vocabulary.

					setMatchingCategories([
						{
							description: `${
								getSiteInfo(
									JSON.stringify(siteId), // Site ID is a number
									categoryTree
								).name
							} - ${parentTaxonomyVocabulary.name}`,
							label: `${name} (ID: ${id})`,
							value: id,
						},
						...categories,
					]);
				}
				else {
					setMatchingCategories(categories);
				}
			})
			.catch(() => {
				setMatchingCategories(categories);
			});
	};

	const _handleFieldValueChange = (newFieldValue) => {
		if (!multiple) {
			_handleSingleItemChange(newFieldValue);
		}
		else {
			setFieldValue(name, newFieldValue);
		}
	};

	const _handleFocus = () => {
		setAutocompleteDropdownActive(true);
	};

	const _handleKeyDown = (event) => {
		if (event.key === 'Enter') {
			event.preventDefault();

			if (!multiple && !!matchingCategories[0]) {
				_handleSingleItemChange(matchingCategories[0]);
			}
		}
	};

	const _handleSingleInputValueChange = (event) => {
		const newValue = event.target.value;

		setInputValue(newValue);

		if (newValue.trim()) {
			handleSetMatchingCategoriesDebounced(newValue.trim(), categoryTree);

			if (identifierType === IDENTIFIER_TYPES.ID) {
				const newValueNumber = toNumber(newValue);

				setFieldValue(
					name,
					typeof newValueNumber === 'number' ? newValueNumber : ''
				);
			}
			else {
				setFieldValue(name, '');
			}
		}
		else {
			setMatchingCategories([]);
			setFieldValue(name, '');
		}
	};

	const _handleSingleItemChange = (item) => {
		if (identifierType === IDENTIFIER_TYPES.EXTERNAL_REFERENCE_CODE) {
			setFieldValue(name, item.value);
			setInputValue(item.value);
		}
		else {
			setFieldValue(name, {label: item.label, value: item.value});
			setInputValue(item.label);
		}
		setMatchingCategories([]);
	};

	const _handleMultiInputValueChange = (newValue) => {
		setInputValue(newValue);

		if (newValue.trim()) {
			handleSetMatchingCategoriesDebounced(newValue.trim(), categoryTree);
		}
		else {
			setMatchingCategories([]);
		}
	};

	const _handleMultiItemsChange = (items) => {
		if (identifierType === IDENTIFIER_TYPES.EXTERNAL_REFERENCE_CODE) {
			setFieldValue(name, _filterMultiItemsByERC(items));
		}
		else {
			setFieldValue(name, _filterMultiItemsByID(items));
		}
	};

	const _filterMultiItemsByERC = (items) => {
		const uniqueArray = [];

		removeDuplicates(items, 'value').map(({label, value}) => {
			if (value !== label) {

				// Case: External Reference Code was chosen through selector or
				// autocomplete list and already has a proper name.

				uniqueArray.push({label, value});
			}
			else {

				// Case: User might be selecting the first item from matchingCategories
				// instead. Add if available and not already added.

				if (
					!!matchingCategories[0] &&
					!uniqueArray.some(
						(item) => matchingCategories[0].value === item.value
					)
				) {
					uniqueArray.push({
						label: matchingCategories[0].label,
						value: matchingCategories[0].value,
					});
				}
			}
		});

		return uniqueArray;
	};

	const _filterMultiItemsByID = (items) => {
		const uniqueArray = [];

		removeDuplicates(items, 'value').map(({label, value}) => {
			if (typeof toNumber(value) === 'number') {
				if (value === label) {

					// Case: ID is manually input as a number but does not have
					// a proper name. Attempt to search for a proper name from
					// original 'items' and from matching categories. Otherwise,
					// save as is.

					const uniqueItem =
						items.find(
							(item) =>
								value === item.value && value !== item.label
						) ||
						matchingCategories.find((item) => item.value === value);

					uniqueArray.push({
						label: uniqueItem?.label || label,
						value,
					});
				}
				else {

					// Case: ID was chosen through selector or autocomplete list
					// and already has a proper name.

					uniqueArray.push({label, value});
				}
			}
			else {

				// Case: ID is manually input and is not a valid number. Add the
				// first item from matchingCategories instead (if available
				// and not already added).

				if (
					!!matchingCategories[0] &&
					!uniqueArray.some(
						(item) => matchingCategories[0].value === item.value
					)
				) {
					uniqueArray.push({
						label: matchingCategories[0].label,
						value: matchingCategories[0].value,
					});
				}
			}
		});

		return uniqueArray;
	};

	useEffect(() => {

		// Fetches the site information that the user has access to + all
		// vocabularies associated with those sites. This information will
		// be the start of the category tree, in which the children of the
		// vocabulary get added on as the tree gets expanded (in modal).

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

				// Filter out results that are not a site and convert groupId to id (number).

				const itemsFilteredForSites = items
					.filter(({site}) => !!site)
					.map((item) => ({
						...item,
						id: Number(item.groupId),
					}));

				// Add global site to the list of sites if it is not already included.

				const itemsWithGlobalSite = itemsFilteredForSites.some(
					({id}) =>
						id.toString() ===
						Liferay.ThemeDisplay.getCompanyGroupId().toString()
				)
					? itemsFilteredForSites
					: [
							{
								descriptiveName: Liferay.Language.get('global'),
								id: Number(
									Liferay.ThemeDisplay.getCompanyGroupId()
								),
							},
							...itemsFilteredForSites,
						];

				const tree = [];

				itemsWithGlobalSite.forEach((site, siteIndex) => {
					fetch(FETCH_URLS.getVocabularies(site.id), {
						headers: DEFAULT_HEADERS,
						method: 'GET',
					})
						.then((response) => response.json())
						.then((vocabularies) => {
							tree[siteIndex] = {
								children: vocabularies.items
									.filter(({siteId}) => {

										// Filter out global vocabularies for
										// non-global sites.

										const isGlobalSite =
											site.id ===
											Number(
												Liferay.ThemeDisplay.getCompanyGroupId()
											);

										if (
											!isGlobalSite &&
											siteId?.toString() ===
												Liferay.ThemeDisplay.getCompanyGroupId()
										) {
											return false;
										}

										return true;
									})
									.map(
										({
											id,
											name,
											numberOfTaxonomyCategories,
										}) => ({

											// In certain responses, 'id' is a number,
											// so JSON.stringify for consistency.

											id: JSON.stringify(id),
											name,
											numberOfTaxonomyCategories,
										})
									),
								descriptiveName: site.descriptiveName,
								externalReferenceCode:
									site.externalReferenceCode,
								id: JSON.stringify(site.id),
								name: site.name,
							};
						})
						.catch(() => {
							tree[siteIndex] = {
								descriptiveName: site.descriptiveName,
								externalReferenceCode:
									site.externalReferenceCode,
								id: JSON.stringify(site.id),
								name: site.name,
							};
						});
				});

				setCategoryTree(tree);
			})
			.catch(() => setCategoryTree([]));
	}, []);

	return (
		<ClayInput.Group className="item-selector-input" small>
			<ClayInput.GroupItem>
				{multiple ? (
					<ClayMultiSelect
						aria-label={label}
						disabled={disabled}
						id={id}
						items={value || []}
						loadingState={4}
						onBlur={_handleBlur}
						onChange={_handleMultiInputValueChange}
						onItemsChange={_handleMultiItemsChange}
						onKeyDown={_handleKeyDown}
						sourceItems={matchingCategories}
						value={inputValue}
					>
						{(item) => (
							<ClayMultiSelect.Item
								key={item.value}
								textValue={item.label}
							>
								<div className="autofit-col">
									<span className="list-group-text">
										{item.label}
									</span>

									{item.description && (
										<span className="list-group-subtext">
											{item.description}
										</span>
									)}
								</div>
							</ClayMultiSelect.Item>
						)}
					</ClayMultiSelect>
				) : (
					<ClayAutocomplete>
						<ClayAutocomplete.Input
							aria-label={label}
							disabled={disabled}
							id={id}
							onBlur={_handleBlur}
							onChange={_handleSingleInputValueChange}
							onFocus={_handleFocus}
							onKeyDown={_handleKeyDown}
							value={inputValue}
						/>

						<ClayAutocomplete.DropDown
							active={
								autocompleteDropdownActive &&
								!!matchingCategories.length
							}
							closeOnClickOutside
							onSetActive={setAutocompleteDropdownActive}
						>
							<CategoryMenu
								locator={{label: 'label', value: 'value'}}
								onItemClick={_handleSingleItemChange}
								sourceItems={matchingCategories}
							/>
						</ClayAutocomplete.DropDown>
					</ClayAutocomplete>
				)}
			</ClayInput.GroupItem>

			<ClayInput.GroupItem shrink>
				<CategorySelectorModal
					identifierType={identifierType}
					multiple={multiple}
					onChangeTree={setCategoryTree}
					onChangeValue={_handleFieldValueChange}
					tree={categoryTree}
					value={value}
				>
					<ClayButton
						aria-label={Liferay.Language.get('select')}
						disabled={disabled}
						displayType="secondary"
						size="sm"
					>
						{Liferay.Language.get('select')}
					</ClayButton>
				</CategorySelectorModal>
			</ClayInput.GroupItem>
		</ClayInput.Group>
	);
}

export default CategorySelectorInput;
