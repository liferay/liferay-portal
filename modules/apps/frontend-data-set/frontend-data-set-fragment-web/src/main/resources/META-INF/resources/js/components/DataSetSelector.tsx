/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ClayButtonWithIcon} from '@clayui/button';
import {ClayDropDownWithItems} from '@clayui/drop-down';
import ClayForm, {ClayInput} from '@clayui/form';
import {
	IDataSet,
	getDataSetResourceURL,
} from '@liferay/frontend-data-set-admin-web';
import {openItemSelectorModal} from '@liferay/frontend-js-item-selector-web';
import {useId} from 'frontend-js-components-web';
import {sub} from 'frontend-js-web';
import React from 'react';

interface IProps {
	onChange: (dataSet: Partial<IDataSet>) => void;
	value: Partial<IDataSet>;
}

const FDS_VIEWS = [
	{
		contentRenderer: 'list',
		name: 'list',
		schema: {
			description: 'description',
			sticker: 'sticker',
			symbol: 'symbol',
			title: 'label',
			tooltip: 'tooltip',
		},
		setItemComponentProps: ({item, props}: {item: any; props: any}) => {
			if (
				!item.dataSetToDataSetCardsSections.length &&
				!item.dataSetToDataSetTableSections.length &&
				!item.dataSetToDataSetListSections.length
			) {
				return {
					...props,
					item: {
						...item,
						sticker: {displayType: 'warning'},
						symbol: 'exclamation-circle',
						tooltip: Liferay.Language.get(
							'no-visualization-modes-have-been-defined'
						),
					},
				};
			}
			else {
				return {
					...props,
					item: {
						...item,
						sticker: {displayType: 'unstyled'},
						symbol: 'catalog',
					},
				};
			}
		},
	},
];

export default function DataSetSelector({onChange, value}: IProps) {
	const inputId = useId();

	const dataSetLabel = Liferay.Language.get('data-set');

	const isDataSetSelected = Object.keys(value).length !== 0;

	const selectButtonIcon = isDataSetSelected ? 'change' : 'plus';

	const selectButtonLabel = sub(
		isDataSetSelected
			? Liferay.Language.get('change-x')
			: Liferay.Language.get('select-x'),
		dataSetLabel
	);

	const optionsButtonLabel = sub(
		Liferay.Language.get('view-x-options'),
		dataSetLabel
	);

	return (
		<ClayForm.Group>
			<label htmlFor={inputId}>{dataSetLabel}</label>

			<ClayInput.Group small>
				<ClayInput.GroupItem>
					<ClayInput
						className="page-editor__item-selector__content-input"
						id={inputId}
						placeholder={sub(
							Liferay.Language.get('no-x-selected'),
							dataSetLabel
						)}
						readOnly
						sizing="sm"
						type="text"
						value={value.label || ''}
					/>
				</ClayInput.GroupItem>

				<ClayInput.GroupItem shrink>
					<ClayButtonWithIcon
						aria-label={selectButtonLabel}
						displayType="secondary"
						onClick={() => {
							openItemSelectorModal({
								apiURL: getDataSetResourceURL({
									params: {
										nestedFields:
											'dataSetToDataSetCardsSections, dataSetToDataSetTableSections, dataSetToDataSetListSections',
									},
								}),
								fdsProps: {
									id: 'dataSetsItemSelectorModal',
									views: FDS_VIEWS,
								},
								itemTypeLabel: dataSetLabel,
								items: value.externalReferenceCode
									? [value as IDataSet]
									: [],
								onItemsChange: (items: IDataSet[]) => {
									if (items && !!items.length) {
										onChange(items[0]);
									}
								},
							});
						}}
						size="sm"
						symbol={selectButtonIcon}
						title={selectButtonLabel}
					/>
				</ClayInput.GroupItem>

				{isDataSetSelected && (
					<ClayInput.GroupItem shrink>
						<ClayDropDownWithItems
							items={[
								{
									label: sub(
										Liferay.Language.get('remove-x'),
										dataSetLabel
									),
									onClick: () => onChange({}),
									symbolLeft: 'trash',
								},
							]}
							menuElementAttrs={{
								containerProps: {className: 'cadmin'},
							}}
							trigger={
								<ClayButtonWithIcon
									aria-label={optionsButtonLabel}
									displayType="secondary"
									size="sm"
									symbol="ellipsis-v"
									title={optionsButtonLabel}
								/>
							}
						/>
					</ClayInput.GroupItem>
				)}
			</ClayInput.Group>
		</ClayForm.Group>
	);
}
