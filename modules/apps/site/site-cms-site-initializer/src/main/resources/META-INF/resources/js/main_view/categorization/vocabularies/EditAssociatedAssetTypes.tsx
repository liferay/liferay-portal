/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayAlert from '@clayui/alert';
import ClayForm, {ClayCheckbox, ClayToggle} from '@clayui/form';
import ClayMultiSelect from '@clayui/multi-select';
import ClayPanel from '@clayui/panel';
import ClayTable from '@clayui/table';
import {sub} from 'frontend-js-web';
import React, {useEffect, useState} from 'react';

import {IVocabulary} from '../../../common/types/IVocabulary';

type Structure = {
	label?: string;
	required: boolean;
	value?: number;
};

const ALL_ASSET_TYPES: AssetType[] = [
	{
		required: false,
		type: 'AllAssetTypes',
		typeId: 0,
	},
];

const ALL_STRUCTURES: Structure[] = [
	{
		label: Liferay.Language.get('all-asset-types'),
		required: false,
		value: 0,
	},
];

export default function EditAssociatedAssetTypes({
	assetTypeInputError,
	availableAssetTypes,
	initialAssetTypes,
	onChangeVocabulary,
	setAssetTypeChange,
	setAssetTypeInputError,
	vocabulary,
}: {
	assetTypeInputError: string;
	availableAssetTypes: AssetType[];
	initialAssetTypes: AssetType[];
	onChangeVocabulary: Function;
	setAssetTypeChange: Function;
	setAssetTypeInputError: Function;
	vocabulary: IVocabulary;
}) {
	const [allAssetTypesSelected, setAllAssetTypesSelected] =
		useState<boolean>(true);
	const availableStructures = availableAssetTypes.map((assetType) => ({
		label: assetType.type,
		required: assetType.required,
		value: assetType.typeId,
	}));

	const [selectedItems, setSelectedItems] =
		useState<Structure[]>(ALL_STRUCTURES);

	useEffect(() => {
		if (vocabulary.assetTypes.length) {
			if (vocabulary.assetTypes[0].typeId === 0) {
				setSelectedItems(ALL_STRUCTURES);
			}
			else {
				setAllAssetTypesSelected(false);

				setSelectedItems(
					vocabulary.assetTypes.map((assetType: AssetType) => ({
						label: assetType.type,
						required: assetType.required,
						value: assetType.typeId,
					}))
				);
			}
		}
	}, [vocabulary]);

	useEffect(() => {
		if (selectedItems.length) {
			setAssetTypeInputError('');
		}
		else {
			setAssetTypeInputError(
				sub(
					Liferay.Language.get('the-x-field-is-required'),
					Liferay.Language.get('asset-types')
				)
			);
		}

		if (selectedItems?.find((item) => item.value === 0)) {
			setAssetTypeChange(false);
		}
		else if (
			initialAssetTypes?.some(
				(assetType) =>
					!selectedItems.find(
						(item) => item.value === assetType.typeId
					)
			)
		) {
			setAssetTypeChange(true);
		}
		else {
			setAssetTypeChange(false);
		}
	}, [
		initialAssetTypes,
		selectedItems,
		setAssetTypeChange,
		setAssetTypeInputError,
	]);

	const isChecked = (item: Structure) => {
		return !!selectedItems.find((val) => val.value === item.value);
	};

	const _handleChangeAllAssetTypes = () => {
		setAllAssetTypesSelected(!allAssetTypesSelected);

		if (allAssetTypesSelected) {
			setSelectedItems([]);
			onChangeVocabulary(() => ({
				...vocabulary,
				assetTypes: [],
			}));
		}
		else {
			setSelectedItems(ALL_STRUCTURES);
			onChangeVocabulary(() => ({
				...vocabulary,
				assetTypes: [ALL_ASSET_TYPES],
			}));
		}
	};

	const _handleChangeAssetTypes = (newSelectedItems: Structure[]) => {
		setSelectedItems(
			newSelectedItems.map((structure) => ({
				...structure,
				required: structure.required ? structure.required : false,
			}))
		);

		onChangeVocabulary(() => ({
			...vocabulary,
			assetTypes: newSelectedItems.map((structure) => ({
				required: structure.required ? structure.required : false,
				type:
					structure.label === Liferay.Language.get('all-asset-types')
						? 'AllAssetTypes'
						: structure.label,
				typeId: structure.value,
			})),
		}));
	};

	const _handleChangeAssetTypeChecked = (item: Structure) => {
		if (!isChecked(item)) {
			_handleChangeAssetTypes([
				...selectedItems,
				{
					label: item.label,
					required: false,
					value: item.value,
				},
			]);
		}
		else {
			_handleChangeAssetTypes(
				selectedItems.filter((entry) => item.value !== entry.value)
			);
		}
	};

	const _handleChangeAssetTypeRequired = (item: Structure) => {
		const updatedSelectedAssetTypes = selectedItems.map((assetType) => {
			if (assetType.value === item.value) {
				return {
					...assetType,
					required: !item.required,
				};
			}
			else {
				return assetType;
			}
		});

		setSelectedItems(updatedSelectedAssetTypes);

		onChangeVocabulary(() => ({
			...vocabulary,
			assetTypes: updatedSelectedAssetTypes.map((structure) => ({
				required: structure.required ? structure.required : false,
				type:
					structure.label === Liferay.Language.get('all-asset-types')
						? 'AllAssetTypes'
						: structure.label,
				typeId: structure.value,
			})),
		}));
	};

	return (
		<div className="container-fluid container-fluid-max-md p-0 p-md-4">
			<ClayPanel
				aria-label="associated-asset-types"
				className="mb-4"
				collapsable={false}
				displayType="secondary"
				role="group"
			>
				<ClayForm.Group className="c-gap-4 d-flex flex-column p-4">
					<h2 className="mb-0 py-2 text-6 text-dark">
						{Liferay.Language.get('associated-asset-types')}
					</h2>

					<p className="text-secondary">
						{Liferay.Language.get(
							'choose-the-asset-types-this-vocabulary-is-associated-with-and-whether-it-is-required'
						)}
					</p>

					<div className={assetTypeInputError ? 'has-error' : ''}>
						<label>{Liferay.Language.get('asset-types')}</label>

						<ClayMultiSelect
							aria-label="Asset Type Selector"
							disabled={allAssetTypesSelected}
							items={allAssetTypesSelected ? [] : selectedItems}
							onItemsChange={(items: Structure[]) => {
								_handleChangeAssetTypes(items);
							}}
							placeholder={
								allAssetTypesSelected
									? Liferay.Language.get('all-asset-types')
									: ''
							}
							sourceItems={availableStructures}
						>
							{(item: any) => (
								<ClayMultiSelect.Item
									key={item.value}
									onClick={() => {
										_handleChangeAssetTypeChecked(item);
									}}
									textValue={item.label}
								>
									<div className="autofit-row autofit-row-center">
										<div className="autofit-col mr-3">
											<ClayCheckbox
												aria-label={item.label}
												checked={isChecked(item)}
												className="invisible"
												onChange={() => {
													_handleChangeAssetTypeChecked(
														item
													);
												}}
											/>
										</div>

										<div className="autofit-col">
											<span>{item.label}</span>
										</div>
									</div>
								</ClayMultiSelect.Item>
							)}
						</ClayMultiSelect>

						{assetTypeInputError && (
							<ClayAlert displayType="danger" variant="feedback">
								{assetTypeInputError}
							</ClayAlert>
						)}
					</div>

					<ClayCheckbox
						checked={allAssetTypesSelected}
						label={Liferay.Language.get(
							'make-this-vocabulary-available-in-all-asset-types'
						)}
						onChange={_handleChangeAllAssetTypes}
					/>

					{!!selectedItems.length && (
						<ClayTable striped>
							<ClayTable.Head>
								<ClayTable.Row>
									<ClayTable.Cell className="text-secondary">
										{Liferay.Language.get('title')}
									</ClayTable.Cell>

									<ClayTable.Cell className="text-secondary">
										{Liferay.Language.get('required')}
									</ClayTable.Cell>
								</ClayTable.Row>
							</ClayTable.Head>

							<ClayTable.Body>
								{selectedItems.map((assetType: Structure) => (
									<ClayTable.Row key={assetType.value}>
										<ClayTable.Cell>
											{assetType.label as String}
										</ClayTable.Cell>

										<ClayTable.Cell>
											<ClayToggle
												aria-label={Liferay.Language.get(
													'toggle-asset-type'
												)}
												onToggle={() => {
													_handleChangeAssetTypeRequired(
														assetType
													);
												}}
												toggled={assetType.required}
											/>
										</ClayTable.Cell>
									</ClayTable.Row>
								))}
							</ClayTable.Body>
						</ClayTable>
					)}
				</ClayForm.Group>
			</ClayPanel>
		</div>
	);
}
