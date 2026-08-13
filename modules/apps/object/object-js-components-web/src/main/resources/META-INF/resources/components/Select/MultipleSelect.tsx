/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayAutocomplete from '@clayui/autocomplete';
import ClayDropDown from '@clayui/drop-down';
import {ClayCheckbox} from '@clayui/form';
import ClayMultiSelect from '@clayui/multi-select';
import {FieldBase} from 'frontend-js-components-web';
import React, {FocusEvent, useEffect, useMemo, useState} from 'react';

import {stringIncludesQuery} from '../../utils/string';

interface MultipleSelectProps {
	className?: string;
	disabled?: boolean;
	error?: string;
	feedbackMessage?: string;
	id?: string;
	label?: string;
	onBlur?: (event: FocusEvent<HTMLInputElement>) => void;
	options: MultiSelectItem[];
	placeholder?: string;
	required?: boolean;
	search?: boolean;
	searchPlaceholder?: string;
	selectAllOption?: boolean;
	setOptions: (options: MultiSelectItem[]) => void;
}

export interface MultiSelectItemChild extends LabelValueObject {
	checked?: boolean;
}

export interface MultiSelectItem extends LabelValueObject {
	children: MultiSelectItemChild[];
}

export function MultipleSelect({
	className,
	disabled,
	error,
	feedbackMessage,
	id,
	label,
	onBlur,
	options,
	placeholder,
	required,
	search,
	searchPlaceholder,
	selectAllOption,
	setOptions,
}: MultipleSelectProps) {
	const [dropdownActive, setDropdownActive] = useState<boolean>(false);
	const [multiSelectItems, setMultiSelectItems] = useState<
		LabelValueObject[]
	>([]);
	const [query, setQuery] = useState('');
	const [selectAllChecked, setSelectAllChecked] = useState<boolean>(false);

	const filteredOptions = useMemo(() => {
		return (options as MultiSelectItem[]).map((option) => {
			return {
				...option,
				children: option.children.filter((child) =>
					stringIncludesQuery(child.label as string, query)
				),
			};
		});
	}, [query, options]);

	useEffect(() => {
		if (selectAllOption) {
			let firstRender = false;
			let allSelected = true;

			options.forEach(({children}) => {
				children.forEach((child) => {
					if (child.checked === undefined) {
						firstRender = true;
					}

					if (child.checked === false) {
						allSelected = false;
					}
				});
			});

			if (!firstRender) {
				setSelectAllChecked(allSelected);
			}
		}
	}, [options, selectAllOption]);

	useEffect(() => {
		const multiSelectOptions = [] as LabelValueObject[];

		// The multi select keys each selected label by its value, so a label
		// carried on its own leaves every key undefined, and React discards and
		// rebuilds the whole row on each change.

		(options as MultiSelectItem[]).forEach(({children}) => {
			return children.forEach(({checked, label, value}) => {
				if (checked) {
					multiSelectOptions.push({
						label,
						value,
					});
				}
			});
		});

		if (multiSelectOptions) {
			setMultiSelectItems(multiSelectOptions as LabelValueObject[]);
		}
	}, [options]);

	return (
		<FieldBase
			className={className}
			disabled={disabled}
			errorMessage={error}
			helpMessage={feedbackMessage}
			id={id}
			label={label}
			required={required}
		>
			<ClayAutocomplete onBlur={onBlur}>
				<ClayMultiSelect<MultiSelectItem>
					id={id}
					items={multiSelectItems as MultiSelectItem[]}
					loadingState={4}
					onChange={setQuery}
					onFocus={() => setDropdownActive((active) => !active)}
					onItemsChange={(items: MultiSelectItem[]) => {
						const newDropDownOptions = options?.map((option) => {
							const newChildren = option.children.map((child) => {
								const checkedItem = items.find(
									(item) => item.label === child.label
								);

								return {
									...child,
									checked: !!checkedItem,
								} as MultiSelectItemChild;
							});

							return {
								children: newChildren,
								label: option.label,
								value: option.value,
							} as MultiSelectItem;
						});

						if (newDropDownOptions) {
							setOptions(newDropDownOptions);
						}
					}}
					onKeyDown={(event) => event.preventDefault()}
					placeholder={placeholder}
				/>

				<ClayAutocomplete.DropDown
					active={dropdownActive}
					alignmentByViewport
					closeOnClickOutside
					onActiveChange={setDropdownActive}
				>
					{search && (
						<ClayDropDown.Search
							onChange={setQuery}
							placeholder={
								searchPlaceholder ??
								Liferay.Language.get('search')
							}
							value={query}
						/>
					)}

					{selectAllOption && (
						<ClayDropDown.Item>
							<ClayCheckbox
								checked={selectAllChecked}
								label={Liferay.Language.get('select-all')}
								onChange={({target: {checked}}) => {
									setOptions(
										options.map((option) => {
											return {
												...option,
												children: option.children.map(
													(child) => {
														return {
															...child,
															checked,
														};
													}
												),
											};
										})
									);
									setSelectAllChecked(checked);
								}}
							/>
						</ClayDropDown.Item>
					)}

					<ClayDropDown.ItemList items={filteredOptions}>
						{

							// @ts-ignore

							(itemGroup: MultiSelectItem) => (
								<ClayDropDown.Group
									header={itemGroup.label}
									items={itemGroup.children}
									key={itemGroup.value}
								>
									{(item) => (
										<ClayDropDown.Item key={item.value}>
											<ClayCheckbox
												checked={
													item.checked as boolean
												}
												label={item.label as string}
												onChange={({
													target: {checked},
												}) => {
													const newOptions =
														options.map(
															(option) => {
																return {
																	children:
																		option.children.map(
																			(
																				child
																			) => {
																				if (
																					child.value ===
																					item.value
																				) {
																					return {
																						...child,
																						checked,
																					};
																				}

																				return child;
																			}
																		),
																	label: option.label,
																	value: option.value,
																};
															}
														);
													setOptions(newOptions);

													if (!checked) {
														setSelectAllChecked(
															checked
														);
													}
												}}
											/>
										</ClayDropDown.Item>
									)}
								</ClayDropDown.Group>
							)
						}
					</ClayDropDown.ItemList>
				</ClayAutocomplete.DropDown>
			</ClayAutocomplete>
		</FieldBase>
	);
}
