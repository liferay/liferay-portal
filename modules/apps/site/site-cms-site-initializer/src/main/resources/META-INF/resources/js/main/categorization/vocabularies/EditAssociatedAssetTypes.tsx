/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayAlert from '@clayui/alert';
import ClayForm, {ClayCheckbox, ClayToggle} from '@clayui/form';
import ClayIcon from '@clayui/icon';
import ClayMultiSelect from '@clayui/multi-select';
import ClayTable from '@clayui/table';
import {sub} from 'frontend-js-web';
import React, {useState} from 'react';

import {IVocabulary} from '../../../types/IVocabulary';

type Structure = {
	label?: string;
	required: boolean;
	value?: number;
};

const ALL_ASSET_TYPES: AssetType[] = [
	{
		required: false,
		subtype: '-1',
		type: Liferay.Language.get('all-asset-types'),
		typeId: '0',
	},
];

export default function EditAssociatedAssetTypes({
	assetTypes,
}: {
	assetTypes: AssetType[];
}) {
	const [allAssetTypesSelected, setAllAssetTypesSelected] =
		useState<boolean>(true);
	const [inputError, setInputError] = useState<string>('');

	const [selectedAssetTypes, setSelectedAssetTypes] =
		useState<AssetType[]>(ALL_ASSET_TYPES);

	const isChecked = (items: AssetType[], item: AssetType) => {
		return !!items.find((val) => val.typeId === item.typeId);
	};

	const _handleChangeAllAssetTypes = () => {
		setAllAssetTypesSelected(!allAssetTypesSelected);

		if (allAssetTypesSelected) {
			setSelectedAssetTypes([]);
		}
		else {
			setSelectedAssetTypes(ALL_ASSET_TYPES);
			setInputError('');
		}
	};

	const _handleChangeAssetTypes = (selectedItems: AssetType[]) => {
		setSelectedAssetTypes(
			selectedItems.filter((item, i, self) => i === self.indexOf(item))
		);

		if (selectedItems.length) {
			setInputError('');
		}
		else {
			setInputError(
				sub(
					Liferay.Language.get('the-x-field-is-required'),
					Liferay.Language.get('asset-types')
				)
			);
		}
	};

	const _handleChangeAssetTypeChecked = (item: AssetType) => {
		if (!isChecked(selectedAssetTypes, item)) {
			_handleChangeAssetTypes([
				...selectedAssetTypes,
				{
					required: false,
					subtype: '-1',
					type: item.type,
					typeId: item.typeId,
				},
			]);
		}
		else {
			_handleChangeAssetTypes(
				selectedAssetTypes.filter(
					(entry) => item.typeId !== entry.typeId
				)
			);
		}
	};

	const _handleChangeAssetTypeRequired = (item: AssetType) => {
		const updatedSelectedAssetTypes = selectedAssetTypes.map(
			(assetType) => {
				if (assetType.typeId === item.typeId) {
					return {
						...assetType,
						required: !item.required,
					};
				}
				else {
					return assetType;
				}
			}
		);

		setSelectedAssetTypes(updatedSelectedAssetTypes);
	};

	return (
		<div className="vertical-nav-content-wrapper">
			<ClayForm.Group className="c-gap-4 d-flex flex-column p-4">
				<div className="form-title">
					{Liferay.Language.get('associated-asset-types')}
				</div>

				<p className="text-secondary">
					{Liferay.Language.get(
						'choose-the-asset-types-this-vocabulary-is-associated-with-and-whether-it-is-required'
					)}
				</p>

				<div className={inputError ? 'has-error' : ''}>
					<label>{Liferay.Language.get('asset-types')}</label>

					<ClayMultiSelect
						disabled={allAssetTypesSelected}
						items={allAssetTypesSelected ? [] : selectedAssetTypes}
						onItemsChange={(items: AssetType[]) => {
							_handleChangeAssetTypes(items);
						}}
						placeholder={
							allAssetTypesSelected
								? Liferay.Language.get('all-asset-types')
								: ''
						}
						sourceItems={assetTypes}
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
											checked={isChecked(
												selectedAssetTypes,
												item
											)}
											className="invisible"
											onChange={() => {
												_handleChangeAssetTypeChecked(
													item
												);
											}}
										/>
									</div>

									<span className="asset-icon">
										<ClayIcon symbol={item.icon} />
									</span>

									<div className="autofit-col">
										<span>{item.label}</span>
									</div>
								</div>
							</ClayMultiSelect.Item>
						)}
					</ClayMultiSelect>

					{inputError && (
						<ClayAlert displayType="danger" variant="feedback">
							{inputError}
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

				{!!selectedAssetTypes.length && (
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
							{selectedAssetTypes.map((assetType: AssetType) => (
								<ClayTable.Row key={assetType.typeId}>
									<ClayTable.Cell>
										{assetType.icon && (
											<span className="asset-icon">
												<ClayIcon
													symbol={assetType.icon}
												/>
											</span>
										)}

										{assetType.type}
									</ClayTable.Cell>

									<ClayTable.Cell>
										<ClayToggle
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
		</div>
	);
}
