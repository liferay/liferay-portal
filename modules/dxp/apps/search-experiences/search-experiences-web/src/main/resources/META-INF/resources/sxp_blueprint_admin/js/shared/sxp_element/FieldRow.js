/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayAutocomplete from '@clayui/autocomplete';
import ClayButton from '@clayui/button';
import ClayDropDown from '@clayui/drop-down';
import ClayForm, {ClayInput, ClaySelect} from '@clayui/form';
import ClayIcon from '@clayui/icon';
import {FocusScope} from '@clayui/shared';
import {ClayTooltipProvider} from '@clayui/tooltip';
import fuzzy from 'fuzzy';
import React, {useContext, useEffect, useRef, useState} from 'react';

import isDefined from '../../utils/functions/is_defined';
import isEmpty from '../../utils/functions/is_empty';
import sortAlphabetically from '../../utils/functions/sort_alphabetically';
import sortByStringLength from '../../utils/functions/sort_by_string_length';
import ThemeContext from '../ThemeContext';

const USER_LANGUAGE_VARIABLE = '${context.language_id}';

/**
 * Used for displaying autocomplete results. Filters a list of index field
 * objects and sorts them by string length or alphabetically if lengths are the
 * same.
 * @param {Array} indexFields List of index field objects.
 * @param {String} field Field name to filter the field objects.
 * @returns
 */
function filterAndSortIndexFields(indexFields, field) {
	return indexFields
		.filter((indexField) =>
			indexField.name.toLowerCase().includes(field.toLowerCase())
		)
		.sort((a, b) => {
			const sort = sortByStringLength(a.name, b.name);

			if (sort === 0 || field === '') {
				return sortAlphabetically(a.name, b.name);
			}

			return sort;
		});
}

/**
 * Displays a single item in the autocomplete dropdown.
 * @param {object} indexField A field mapping object containing
 * 	{languageIdPosition, name, type}.
 * 	@see FieldMappingInfoResourceImpl#_addFieldMappingInfo
 * @param {String} match The current input value.
 * @param {Function} onClick
 */
function AutocompleteItem({indexField, match = '', onClick}) {
	const fuzzyMatch = fuzzy.match(match, indexField.name, {
		post: '</strong>',
		pre: '<strong>',
	});

	return (
		<ClayDropDown.Item onClick={onClick}>
			{fuzzyMatch ? (
				<span
					dangerouslySetInnerHTML={{
						__html: fuzzyMatch.rendered,
					}}
				/>
			) : (
				indexField.name
			)}

			{indexField.languageIdPosition > -1 && <ClayIcon symbol="globe" />}

			<span
				className="type"
				{...(indexField.type?.length > 7
					? {title: indexField.type}
					: {})}
			>
				{indexField.type}
			</span>
		</ClayDropDown.Item>
	);
}

/**
 * Displays an autocomplete input for selecting an indexed field.
 *
 * Example index field object:
 * {
 * 	languageIdPosition: -1,
 * 	name: 'ddmTemplateKey',
 * 	type: 'keyword'
 * }
 * @see FieldMappingInfoResourceImpl#_addFieldMappingInfo
 */
function FieldRow({
	boost = 1,
	disabled,
	field,
	id,
	index = 0,
	indexFields = [],
	languageIdPosition,
	locale,
	onBlur,
	onChange,
	onDelete,
	showBoost,
}) {
	const {availableLanguages} = useContext(ThemeContext);

	const inputRef = useRef();

	const [filteredIndexFields, setFilteredIndexFields] = useState(
		filterAndSortIndexFields(indexFields, field)
	);
	const [showDropDown, setShowDropDown] = useState(false);

	useEffect(() => {
		setFilteredIndexFields(filterAndSortIndexFields(indexFields, field));
	}, [indexFields, field]);

	const _getIndexField = (fieldName) => {
		return indexFields.find((item) => item.name === fieldName);
	};

	const _handleFieldChange = (event) => {
		const indexField = _getIndexField(event.target.value) || {};

		let languageIdPosition = isDefined(indexField.languageIdPosition)
			? indexField.languageIdPosition
			: -1;

		if (indexField.locale && languageIdPosition === -1) {
			languageIdPosition = event.target.value.length;
		}

		onChange({
			field: event.target.value,
			languageIdPosition,
			locale: languageIdPosition > -1 ? '' : undefined,
		});
	};

	const _handleAutocompleteItemClick = (indexField) => () => {

		// Special case if the same field name has both a localized and
		// non-localized option.
		//
		// For example 'title':
		// {name: 'title', type: 'text', languageIdPosition: 5}
		// {name: 'title', type: 'text', languageIdPosition: -1}
		//
		// Reset the locale to "no localization" if the non-localized field
		// is autocompleted to.

		if (indexField.languageIdPosition === -1) {
			onChange({
				field: indexField.name,
				languageIdPosition: indexField.languageIdPosition,
				locale: '',
			});
		}
		else {
			onChange({
				field: indexField.name,
				languageIdPosition: indexField.languageIdPosition,
			});
		}

		inputRef.current.focus();

		setShowDropDown(false);
	};

	const _handleFieldKeyDown = (event) => {
		_handleKeyDown(event);

		if (event.key === 'Tab' || (event.shiftKey && event.key === 'Tab')) {
			return;
		}

		setShowDropDown(true);
	};

	const _handleBoostChange = (event) => onChange({boost: event.target.value});

	const _handleLocaleChange = (event) =>
		onChange({locale: event.target.value});

	const _handleKeyDown = (event) => {
		if (event.key === 'Enter') {
			event.preventDefault();
		}
	};

	const _isLocalizable = () => languageIdPosition > -1 || !isEmpty(locale);

	return (
		<ClayForm.Group>
			<ClayInput.Group small>
				<ClayInput.GroupItem>
					<FocusScope>
						<ClayAutocomplete>
							<ClayAutocomplete.Input
								aria-label={Liferay.Language.get('field')}
								autoComplete="off"
								disabled={disabled}
								id={id}
								onBlur={onBlur}
								onChange={_handleFieldChange}
								onFocus={() => setShowDropDown(true)}
								onKeyDown={_handleFieldKeyDown}
								ref={inputRef}
								sizing="sm"
								value={field}
							/>

							<ClayAutocomplete.DropDown
								active={
									showDropDown && filteredIndexFields.length
								}
								onSetActive={setShowDropDown}
							>
								<ClayDropDown.ItemList className="sxp-field-row-dropdown-root">
									{filteredIndexFields.map(
										(indexField, index) => (
											<AutocompleteItem
												indexField={indexField}
												key={index}
												match={field}
												onClick={_handleAutocompleteItemClick(
													indexField
												)}
											/>
										)
									)}
								</ClayDropDown.ItemList>
							</ClayAutocomplete.DropDown>
						</ClayAutocomplete>
					</FocusScope>
				</ClayInput.GroupItem>

				{_isLocalizable() && (
					<ClayInput.GroupItem>
						<ClaySelect
							aria-label={Liferay.Language.get('locale')}
							className="form-control-sm"
							disabled={disabled}
							id={`${id}_locale`}
							onBlur={onBlur}
							onChange={_handleLocaleChange}
							onKeyDown={_handleKeyDown}
							value={locale}
						>
							<ClaySelect.Option
								key={`no-localization-${index}`}
								label={Liferay.Language.get('no-localization')}
								value=""
							/>

							<ClaySelect.Option
								key={`users-language-${index}`}
								label={Liferay.Language.get('users-language')}
								value={USER_LANGUAGE_VARIABLE}
							/>

							{Object.keys(availableLanguages).map((locale) => (
								<ClaySelect.Option
									key={`${index}-${locale}`}
									label={availableLanguages[locale]}
									value={locale}
								/>
							))}
						</ClaySelect>
					</ClayInput.GroupItem>
				)}

				{showBoost && (
					<ClayTooltipProvider>
						<ClayInput.GroupItem shrink>
							<ClayInput
								aria-label={Liferay.Language.get('boost')}
								className="field-boost-input"
								data-tooltip-align="top"
								disabled={disabled}
								id={`${id}_boost`}
								min="0"
								onBlur={onBlur}
								onChange={_handleBoostChange}
								onKeyDown={_handleKeyDown}
								title={Liferay.Language.get('boost')}
								type="number"
								value={boost}
							/>
						</ClayInput.GroupItem>
					</ClayTooltipProvider>
				)}

				{onDelete && (
					<ClayInput.GroupItem shrink>
						<ClayButton
							aria-label={Liferay.Language.get('delete')}
							disabled={disabled}
							displayType="unstyled"
							monospaced
							onClick={onDelete}
							size="sm"
						>
							<ClayIcon symbol="times-circle" />
						</ClayButton>
					</ClayInput.GroupItem>
				)}
			</ClayInput.Group>
		</ClayForm.Group>
	);
}

export default FieldRow;
