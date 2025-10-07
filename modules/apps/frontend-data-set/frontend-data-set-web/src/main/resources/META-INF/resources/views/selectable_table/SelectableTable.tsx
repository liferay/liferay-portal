/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ClayCheckbox} from '@clayui/form';
import ClayLoadingIndicator from '@clayui/loading-indicator';
import ClayTable from '@clayui/table';
import {getObjectValueFromPath, sub} from 'frontend-js-web';
import React, {useContext, useState} from 'react';

import FrontendDataSetContext from '../../FrontendDataSetContext';

function SelectableTable({
	dataLoading,
	items: itemsProp,
	schema,
	style,
}: {
	dataLoading: boolean;
	items: any[];
	schema: any;
	style: string;
}) {
	const {namespace, selectedItemsKey} = useContext(FrontendDataSetContext);

	const [items, setItems] = useState(itemsProp);

	function handleCheckboxChange(
		itemField: string,
		itemId: string | null,
		value?: boolean
	) {
		const updatedItems = items.map((item) => {
			const currentItemId = getObjectValueFromPath({
				object: item,
				path: selectedItemsKey,
			});
			if (!itemId || currentItemId === itemId) {
				return {
					...item,
					restrictionFields: item.restrictionFields.map(
						(currentField: any) => {
							if (itemField !== currentField.name) {
								return currentField;
							}

							return {
								...currentField,
								value:
									typeof value === 'boolean'
										? value
										: !currentField.value,
							};
						}
					),
				};
			}

			return item;
		});

		setItems(updatedItems);
	}

	if (dataLoading) {
		return <ClayLoadingIndicator className="mt-7" />;
	}

	if (!items || !items.length) {
		return null;
	}

	return (
		<div className={`table-style-${style}`}>
			<ClayTable borderless hover={false} responsive={false}>
				<ClayTable.Head>
					<ClayTable.Row>
						<ClayTable.Cell
							className="table-cell-expand-smaller"
							headingCell
							headingTitle
						>
							{schema.firstColumnLabel}
						</ClayTable.Cell>

						{items[0].restrictionFields.map((columnField: any) => {
							const checkedItems = items.reduce(
								(checked, item) => {
									const field = item.restrictionFields.find(
										(itemField: any) =>
											itemField.name === columnField.name
									);

									return checked + (field.value ? 1 : 0);
								},
								0
							);

							return (
								<ClayTable.Cell
									className="table-cell-expand-smaller"
									headingCell
									key={columnField.name}
								>
									<ClayCheckbox
										checked={checkedItems === items.length}
										className="mr-2"
										indeterminate={
											checkedItems > 0 &&
											checkedItems < items.length
										}
										label={columnField.label}
										name={`${columnField.name}_column`}
										onChange={() =>
											handleCheckboxChange(
												columnField.name,
												null,
												checkedItems === items.length
													? false
													: true
											)
										}
									/>
								</ClayTable.Cell>
							);
						})}
					</ClayTable.Row>
				</ClayTable.Head>

				<ClayTable.Body>
					{items.map((item, i) => {
						const itemId = getObjectValueFromPath({
							object: item,
							path: selectedItemsKey,
						});

						return (
							<ClayTable.Row key={i}>
								<ClayTable.Cell>
									{item[schema.firstColumnName]}
								</ClayTable.Cell>

								{item.restrictionFields.map((field: any) => {
									return (
										<ClayTable.Cell key={field.name}>
											<ClayCheckbox
												aria-label={sub(
													Liferay.Language.get(
														'select-x'
													),
													`${
														item[
															schema
																.firstColumnName
														]
													} ${field.label}`
												)}
												checked={field.value}
												name={namespace + itemId}
												onChange={() => {
													handleCheckboxChange(
														field.name,
														itemId
													);
												}}
												value={field.name}
											/>
										</ClayTable.Cell>
									);
								})}
							</ClayTable.Row>
						);
					})}
				</ClayTable.Body>
			</ClayTable>
		</div>
	);
}

export default SelectableTable;
