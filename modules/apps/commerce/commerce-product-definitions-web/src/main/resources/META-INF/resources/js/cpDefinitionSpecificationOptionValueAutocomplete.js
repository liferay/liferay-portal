/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayAutocomplete from '@clayui/autocomplete';
import ClayForm, {ClayInput, ClaySelect} from '@clayui/form';
import ClayIcon from '@clayui/icon';
import {CommerceServiceProvider} from 'commerce-frontend-js';
import {fetch} from 'frontend-js-web';
import React, {useEffect, useState} from 'react';

const AdminCatalogResource = CommerceServiceProvider.AdminCatalogAPI('v1');

const CPDefinitionSpecificationOptionValueAutocomplete = ({
	catalogDefaultLanguageId,
	createNewSpecification,
	siteLanguage,
}) => {
	const [specifications, setSpecifications] = useState([]);
	const [listTypeDefinitions, setListTypeDefinitions] = useState([]);
	const [search, setSearch] = useState('');
	const [selectedItems, setSelectedItems] = useState([]);

	useEffect(() => {
		window.top.Liferay.fire('is-loading-modal', {isLoading: true});
	}, []);

	useEffect(() => {
		AdminCatalogResource.getSpecifications(search)
			.catch((error) => {
				const message =
					error.message ??
					Liferay.Language.get('an-unexpected-error-occurred');

				window.parent.Liferay.Util.openToast({
					message,
					type: 'danger',
				});
			})
			.then(({items}) => {
				setSpecifications(items);
				window.top.Liferay.fire('is-loading-modal', {isLoading: false});
			});
	}, [search]);

	useEffect(() => {
		if (selectedItems.length !== 0) {
			const allListTypeEntries = [];

			selectedItems.forEach((selectedItem, index) => {
				fetch(
					`/o/headless-admin-list-type/v1.0/list-type-definitions/${selectedItem}`
				)
					.catch((error) => {
						const message =
							error.message ??
							Liferay.Language.get(
								'an-unexpected-error-occurred'
							);

						window.parent.Liferay.Util.openToast({
							message,
							type: 'danger',
						});
					})
					.then((response) => response.json())
					.then(({listTypeEntries, name}) => {
						allListTypeEntries.push({
							listTypeEntries: listTypeEntries.sort(
								(current, next) => {
									return current.name > next.name ? 1 : -1;
								}
							),
							name,
						});

						if (selectedItems.length - 1 === index) {
							setListTypeDefinitions(allListTypeEntries);
						}
					});
			});
		}
		else {
			setListTypeDefinitions([]);
		}
	}, [selectedItems]);

	const handleSelectChange = (event) => {
		const selectedValue = event.target.value;

		const allListTypeEntries = [];

		listTypeDefinitions.map(({listTypeEntries}) =>
			allListTypeEntries.push(...listTypeEntries)
		);

		const selectedListTypeEntry = allListTypeEntries.find(
			(listTypeEntry) => listTypeEntry.key === selectedValue
		);
		if (selectedListTypeEntry) {
			Liferay.fire('list-type-entry-selected', {
				name_i18n: selectedListTypeEntry.name_i18n,
			});
		}
	};

	const handleSpecificationChange = (event) => {
		const labelValue = event.target.value;
		Liferay.fire('specification-created', {
			labelValue,
		});
	};

	const handleInputChange = (event) => {
		const inputValue = event.target.value;
		Liferay.fire('input-value-selected', {
			inputValue,
		});
	};

	listTypeDefinitions.sort((current, next) => {
		return current.name > next.name ? 1 : -1;
	});

	function getName(title) {
		if (title[catalogDefaultLanguageId]) {
			return title[catalogDefaultLanguageId];
		}
		else {
			return title[siteLanguage];
		}
	}

	return (
		<ClayForm.Group aria-required={true}>
			<label
				aria-required={true}
				className="control-label"
				htmlFor="autocomplete"
				id="autocomplete-label"
			>
				{Liferay.Language.get('specification')}

				<span className="reference-mark text-warning">
					<ClayIcon symbol="asterisk" />
				</span>

				<span className="hide-accessible sr-only">
					{Liferay.Language.get('required')}
				</span>
			</label>

			{createNewSpecification && (
				<ClayInput
					aria-required={true}
					id="specificationKey"
					name="specificationKey"
					onChange={handleSpecificationChange}
					placeholder={Liferay.Language.get('specification')}
					required={true}
				/>
			)}

			{!createNewSpecification && (
				<ClayAutocomplete
					allowsCustomValue={false}
					aria-labelledby="autocomplete-label"
					aria-required={true}
					id="autocomplete"
					items={specifications}
					menuTrigger="focus"
					onChange={setSearch}
					required={true}
					value={search}
				>
					{(
						{
							key: specificationKey,
							listTypeDefinitionIds,
							optionCategory = {},
							title,
						},
						index
					) => (
						<ClayAutocomplete.Item
							key={index}
							onClick={() => {
								setSelectedItems(listTypeDefinitionIds);
								Liferay.fire('specification-selected', {
									optionCategoryId: optionCategory.id ?? 0,
									specificationKey,
								});
							}}
							textValue={getName(title)}
						/>
					)}
				</ClayAutocomplete>
			)}

			<label
				aria-required={true}
				className="control-label"
				htmlFor="value"
				id="value-label"
			>
				{Liferay.Language.get('value')}

				<span className="reference-mark text-warning">
					<ClayIcon symbol="asterisk" />
				</span>

				<span className="hide-accessible sr-only">
					{Liferay.Language.get('required')}
				</span>
			</label>

			{!!listTypeDefinitions.length && (
				<ClaySelect
					name="listTypeEntriesSelect"
					onChange={handleSelectChange}
					required
				>
					<ClaySelect.Option
						aria-label={Liferay.Language.get('select-an-option')}
						label={Liferay.Language.get('select-an-option')}
						value=""
					/>

					{listTypeDefinitions.map((listTypeDefinition) => (
						<ClaySelect.OptGroup
							key={listTypeDefinition}
							label={listTypeDefinition.name}
						>
							{listTypeDefinition.listTypeEntries.map(
								(listTypeEntry) => (
									<ClaySelect.Option
										key={listTypeEntry}
										label={`${listTypeEntry.name}`}
										value={listTypeEntry.key}
									/>
								)
							)}
						</ClaySelect.OptGroup>
					))}
				</ClaySelect>
			)}

			{!listTypeDefinitions.length && (
				<ClayInput onChange={handleInputChange} required />
			)}
		</ClayForm.Group>
	);
};

export default CPDefinitionSpecificationOptionValueAutocomplete;
