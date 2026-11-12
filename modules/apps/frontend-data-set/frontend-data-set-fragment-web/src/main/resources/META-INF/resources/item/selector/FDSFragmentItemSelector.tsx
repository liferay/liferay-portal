/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import ClayModal from '@clayui/modal';
import {
	getDataSetResourceURL,
	hasDisplayedVisualizationMode,
} from '@liferay/frontend-data-set-admin-web';
import {FrontendDataSet} from '@liferay/frontend-data-set-web';
import React, {useState} from 'react';

import './FDSFragmentItemSelector.scss';

interface ISelectedItem {
	externalReferenceCode: string;
	id: string;
	label: string;
}

const views = [
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
			if (!hasDisplayedVisualizationMode(item)) {
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

const FDSFragmentItemSelector = ({
	className,
	classNameId,
	namespace,
}: {
	className: string;
	classNameId: string;
	namespace: string;
}) => {
	const getSelectedData = () => {
		const dataset = (window.frameElement as HTMLElement)?.dataset;

		const externalReferenceCode = dataset.selecteditemsercs;
		const id = dataset.selecteditemsids;
		const label = dataset.selecteditemslabels;

		if (!externalReferenceCode || !id || !label) {
			return null;
		}

		return {
			externalReferenceCode,
			id,
			label,
		};
	};

	const [selectedItem, setSelectedItem] = useState<ISelectedItem | null>(
		getSelectedData()
	);

	return (
		<div className="data-set-item-selector">
			<ClayModal.Body>
				<FrontendDataSet

					// {...FDS_DEFAULT_PROPS}

					apiURL={getDataSetResourceURL({
						params: {
							nestedFields:
								'dataSetToDataSetCardsSections, dataSetToDataSetTableSections, dataSetToDataSetListSections',
						},
					})}
					id={`${namespace}FDSFragmentItemSelector`}
					onSelectedItemsChange={(
						selectedItems: Array<ISelectedItem>
					) => {
						setSelectedItem(selectedItems[0]);
					}}
					selectedItems={[selectedItem]}
					selectedItemsKey="externalReferenceCode"
					selectionType="single"
					views={views}
				/>
			</ClayModal.Body>

			<ClayModal.Footer
				last={
					<ClayButton.Group spaced>
						<ClayButton
							className="btn-cancel"
							displayType="secondary"
						>
							{Liferay.Language.get('cancel')}
						</ClayButton>

						<ClayButton
							className="item-preview selector-button"
							data-value={`{
								"className": "${className}",
								"classNameId": "${classNameId}",
								"classPK": "${selectedItem?.id}",
								"externalReferenceCode": "${selectedItem?.externalReferenceCode}",
								"title": "${selectedItem?.label}"}`}
						>
							{Liferay.Language.get('save')}
						</ClayButton>
					</ClayButton.Group>
				}
			/>
		</div>
	);
};

export default FDSFragmentItemSelector;
