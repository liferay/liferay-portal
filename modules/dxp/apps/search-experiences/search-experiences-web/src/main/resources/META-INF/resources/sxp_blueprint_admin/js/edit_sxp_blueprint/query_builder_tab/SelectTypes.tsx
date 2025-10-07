/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayAlert from '@clayui/alert';
import ClayButton from '@clayui/button';
import ClayIcon from '@clayui/icon';
import ClayList from '@clayui/list';
import ClaySticker from '@clayui/sticker';
import React, {useContext, useState} from 'react';

// @ts-ignore

import ThemeContext from '../../shared/ThemeContext';

// @ts-ignore

import SearchableTypesModal from './SearchableTypesModal';

// @ts-ignore

import SelectSubtypes from './SelectSubtypes';

export interface ISelectedSubtype {
	label: string;
	value: string;
}

interface ISelectedItem {
	subtypes: ISelectedSubtype[];
	type: string;
}

/**
 * Checks whether the subtype is missing. Since missing subtypes resort to
 * using the value for its view, label and value would be the same.
 */
export function isMissing(subtype: ISelectedSubtype) {
	return subtype.label === subtype.value;
}

/**
 * Grabs all the selected subtypes of a certain type.
 */
const getSelectedSubtypes = (
	selected: ISelectedItem[],
	className: string
): ISelectedSubtype[] => {
	return selected.find((item) => item.type === className)?.subtypes ?? [];
};

/**
 * Turns a flat list of types and subtypes into a nested array of the types
 * with its subtypes as children. This is called when setting up the 'selected'
 * state and allows for easier manipulation of the data.
 */
const setupSelected = (
	initialSelectedTypes: string[],
	assetSubtypesMap: {[key: string]: string} = {}
): ISelectedItem[] => {
	const selected: ISelectedItem[] = [];

	initialSelectedTypes.forEach((item) => {
		const selectedTypes = item.split('&&');

		if (selectedTypes.length < 3) {
			selected.push({subtypes: [], type: item});
		}
		else {
			const typeClassName = selectedTypes[0];

			const itemIndex = selected.findIndex(
				({type}) => type === typeClassName
			);

			if (itemIndex > -1) {
				const subtypesArray = selected[itemIndex].subtypes || [];

				subtypesArray.push({
					label: assetSubtypesMap[item] || item,
					value: item,
				});

				selected[itemIndex] = {
					subtypes: subtypesArray,
					type: typeClassName,
				};
			}
			else {
				selected.push({
					subtypes: [
						{label: assetSubtypesMap[item] || item, value: item},
					],
					type: typeClassName,
				});
			}
		}
	});

	return selected;
};

/**
 * Transforms the selected types and subtypes into a flat array that
 * the backend expects for submission.
 */
const transformSelected = (selected: ISelectedItem[]): string[] => {
	const searchableAssetTypes: string[] = [];

	selected.forEach(({subtypes, type}) => {
		if (subtypes.length) {
			searchableAssetTypes.push(...subtypes.map(({value}) => value));
		}
		else {
			searchableAssetTypes.push(type);
		}
	});

	return searchableAssetTypes;
};

function SelectTypes({
	assetSubtypesMap,
	initialSelectedTypes = [],
	onAssetSubtypesMapChange,
	onFetchSearchableTypes,
	onFrameworkConfigChange,
	searchableTypes = [],
}: {
	assetSubtypesMap: {
		[key: string]: string;
	};
	initialSelectedTypes?: string[];
	onAssetSubtypesMapChange: (subtypes: ISelectedSubtype[]) => void;
	onFetchSearchableTypes: () => Promise<{
		searchableTypes: {
			className: string;
			displayName: string;
			hasSubtype?: boolean;
		}[];
	}>;
	onFrameworkConfigChange: (config: {searchableAssetTypes: string[]}) => void;
	searchableTypes?: {
		className: string;
		displayName: string;
		hasSubtype?: boolean;
	}[];
}) {
	const {locale}: {locale: string} = useContext(ThemeContext);

	const [selected, setSelected] = useState(
		setupSelected(initialSelectedTypes, assetSubtypesMap)
	);

	const mainSearchableTypesSorted = searchableTypes.sort((a, b) =>
		a.displayName.localeCompare(b.displayName, locale.replaceAll('_', '-'))
	);

	const _anyMissingSubtypes = () =>
		selected.some(({subtypes}) =>
			subtypes.some((subtype) => isMissing(subtype))
		);

	const _getMissingTypes = () =>
		selected.filter(({type}) =>
			searchableTypes.every(({className}) => type !== className)
		);

	const _handleChangeSelected = (newSelected: ISelectedItem[]) => {
		setSelected(newSelected);

		onFrameworkConfigChange({
			searchableAssetTypes: transformSelected(newSelected),
		});
	};

	const _handleRemoveType = (className: string) => {
		const newSelected = selected.filter(({type}) => type !== className);

		_handleChangeSelected(newSelected);
	};

	const _handleRemoveSubtype = (subtypeValue: string) => {
		const newSelected = selected.map(({subtypes, type}) => ({
			subtypes: subtypes.filter(({value}) => value !== subtypeValue),
			type,
		}));

		_handleChangeSelected(newSelected);
	};

	const _handleChangeSubtypes =
		(type: string) => (subtypes: ISelectedSubtype[]) => {
			const newSelected = selected.map((item) => {

				// Handles changing the subtypes of one type

				if (item.type === type) {
					return {
						subtypes,
						type,
					};
				}

				return item;
			});

			_handleChangeSelected(newSelected);

			// If any new subtypes are in this array, they should be
			// added to the assetSubtypesMap.

			onAssetSubtypesMapChange(subtypes);
		};

	const _handleChangeTypes = (types: string[]) => {
		const newSelected = types.map((type) => {

			// Check if the type already exists in the selected array.
			// If it does, return the object, otherwise return a brand new object.

			const prevTypeObject = selected.find((item) => item.type === type);

			return prevTypeObject || {subtypes: [], type};
		});

		_handleChangeSelected(newSelected);
	};

	return (
		<>
			<SearchableTypesModal
				initialSelectedTypes={selected.map(({type}) => type)}
				onChangeTypes={_handleChangeTypes}
				onFetchSearchableTypes={onFetchSearchableTypes}
				searchableTypes={mainSearchableTypesSorted}
			>
				<ClayButton
					className="select-types-button"
					displayType="secondary"
					small
				>
					{Liferay.Language.get('select-asset-types')}
				</ClayButton>
			</SearchableTypesModal>

			{(_anyMissingSubtypes() || !!_getMissingTypes().length) && (
				<ClayAlert
					displayType="warning"
					title={Liferay.Language.get('warning') + ':'}
				>
					{Liferay.Language.get('missing-type-references-warning')}
				</ClayAlert>
			)}

			{!!selected.length && (
				<ClayList>
					<>
						{mainSearchableTypesSorted
							.filter(({className}) =>
								selected.some(({type}) => type === className)
							)
							.map(
								({
									className,
									displayName,
									hasSubtype = false,
								}) => (
									<ClayList.Item flex key={className}>
										<ClayList.ItemField expand>
											<ClayList.ItemTitle>
												{displayName}
											</ClayList.ItemTitle>

											{Liferay.FeatureFlags[
												'LPS-129412'
											] &&
												hasSubtype && (
													<SelectSubtypes
														className={className}
														onChangeSubtypes={_handleChangeSubtypes(
															className
														)}
														onRemoveSubtype={
															_handleRemoveSubtype
														}
														selectedSubtypes={getSelectedSubtypes(
															selected,
															className
														)}
													/>
												)}
										</ClayList.ItemField>

										<ClayList.ItemField>
											<ClayButton
												aria-label={Liferay.Language.get(
													'delete'
												)}
												className="c-m-auto secondary"
												displayType="unstyled"
												onClick={() =>
													_handleRemoveType(className)
												}
												size="sm"
											>
												<ClayIcon symbol="times-circle" />
											</ClayButton>
										</ClayList.ItemField>
									</ClayList.Item>
								)
							)}
					</>

					<>
						{_getMissingTypes().map(({type}) => (
							<ClayList.Item flex key={type}>
								<ClayList.ItemField expand>
									<div>
										<span className="list-group-title">
											{type}
										</span>

										<span className="c-ml-1 inline-item">
											<ClaySticker
												displayType="warning"
												size="sm"
											>
												<ClayIcon symbol="warning-full" />
											</ClaySticker>

											<strong className="text-2 text-warning">
												{Liferay.Language.get(
													'missing'
												)}
											</strong>
										</span>
									</div>
								</ClayList.ItemField>

								<ClayList.ItemField>
									<ClayButton
										aria-label={Liferay.Language.get(
											'delete'
										)}
										className="c-m-auto secondary"
										displayType="unstyled"
										onClick={() => _handleRemoveType(type)}
										size="sm"
									>
										<ClayIcon symbol="times-circle" />
									</ClayButton>
								</ClayList.ItemField>
							</ClayList.Item>
						))}
					</>
				</ClayList>
			)}
		</>
	);
}

export default React.memo(SelectTypes);
