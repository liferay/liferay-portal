/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ClayButtonWithIcon} from '@clayui/button';
import ClayForm, {ClayInput} from '@clayui/form';
import ClayPanel from '@clayui/panel';
import React, {useEffect, useState} from 'react';

interface Props {
	category: TaxonomyCategory;
	setCategory: React.Dispatch<React.SetStateAction<TaxonomyCategory>>;
	spritemap: string;
}

const EditCategoryPropertiesTab = ({
	category,
	setCategory,
	spritemap,
}: Props) => {
	const [properties, setProperties] = useState<TaxonomyCategoryProperty[]>(
		category.taxonomyCategoryProperties &&
			!!category.taxonomyCategoryProperties.length
			? category.taxonomyCategoryProperties
			: [{key: '', value: ''}]
	);

	useEffect(() => {
		setCategory((prevCategory) => ({
			...prevCategory,
			taxonomyCategoryProperties: properties,
		}));
	}, [properties, setCategory]);

	const handleInputChange = (
		rowIndex: number,
		field: 'key' | 'value',
		newValue: string
	) => {
		setProperties((prevRows) =>
			prevRows.map((row, propertyIndex) =>
				propertyIndex === rowIndex ? {...row, [field]: newValue} : row
			)
		);
	};

	const addRow = () => {
		setProperties((prevProperties) => {
			return [
				...prevProperties,
				{
					key: '',
					value: '',
				},
			];
		});
	};

	const deleteRow = (rowIndex: number) => {
		setProperties((prevProperties) => {
			return prevProperties.filter((_row, index) => index !== rowIndex);
		});
	};

	const PropertyRow = (
		index: number,
		property?: TaxonomyCategoryProperty
	) => {
		return (
			<ClayInput.Group className="category-property-row" key={index}>
				<ClayInput.GroupItem>
					<label>{Liferay.Language.get('key')}</label>

					<ClayInput
						aria-label={Liferay.Language.get('key')}
						data-testid={`property-key-input-${index}`}
						onBlur={(event) =>
							handleInputChange(index, 'key', event.target.value)
						}
						onChange={(event) =>
							handleInputChange(index, 'key', event.target.value)
						}
						type="text"
						value={property?.key || ''}
					/>
				</ClayInput.GroupItem>

				<ClayInput.GroupItem>
					<div className="align-items-center d-flex flex-fill">
						<label>{Liferay.Language.get('value')}</label>

						<div className="category-property-row-buttons ml-auto">
							<ClayButtonWithIcon
								aria-label={Liferay.Language.get('delete-row')}
								className="category-property-row-button"
								data-testid={`delete-property-row-button-${index}`}
								disabled={properties.length <= 1}
								onClick={() => deleteRow(index)}
								spritemap={spritemap}
								symbol="hr"
							/>

							<ClayButtonWithIcon
								aria-label={Liferay.Language.get('add-row')}
								className="category-property-row-button"
								data-testid={`add-property-row-button-${index}`}
								onClick={() => addRow()}
								spritemap={spritemap}
								symbol="plus"
							/>
						</div>
					</div>

					<ClayInput
						aria-label={Liferay.Language.get('value')}
						data-testid={`property-value-input-${index}`}
						onBlur={(event) =>
							handleInputChange(
								index,
								'value',
								event.target.value
							)
						}
						onChange={(event) =>
							handleInputChange(
								index,
								'value',
								event.target.value
							)
						}
						type="text"
						value={property?.value || ''}
					/>
				</ClayInput.GroupItem>
			</ClayInput.Group>
		);
	};

	return (
		<div className="container-fluid container-fluid-max-md p-0 p-md-4">
			<ClayPanel
				aria-label="properties"
				className="mb-4"
				collapsable={false}
				displayType="secondary"
				role="group"
			>
				<ClayForm.Group
					className="c-gap-4 d-flex flex-column p-4"
					data-testid="edit-category-properties-form-group"
				>
					<h2 className="mb-0 py-2 text-6 text-dark">
						{Liferay.Language.get('properties')}
					</h2>

					<div>
						{Liferay.Language.get('edit-category-properties-help')}
					</div>

					<div
						className="autofit-col c-gap-3"
						data-testid="edit-category-properties-table"
					>
						{properties.length
							? properties.map(
									(
										property: TaxonomyCategoryProperty,
										index: number
									) => {
										return PropertyRow(index, property);
									}
								)
							: PropertyRow(0)}
					</div>
				</ClayForm.Group>
			</ClayPanel>
		</div>
	);
};

export default EditCategoryPropertiesTab;
