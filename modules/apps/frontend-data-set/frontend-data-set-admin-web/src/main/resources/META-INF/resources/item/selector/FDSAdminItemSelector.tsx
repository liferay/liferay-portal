/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import ClayModal from '@clayui/modal';
import {FrontendDataSet} from '@liferay/frontend-data-set-web';
import React, {useState} from 'react';

import {FDS_DEFAULT_PROPS} from '../../js/utils/constants';

import './FDSAdminItemSelector.scss';
import getDataSetResourceURL from '../../js/utils/getDataSetResourceURL';

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
			symbol: 'symbol',
			title: 'label',
		},
	},
];

const FDSAdminItemSelector = ({
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
		<div className="fds-admin-item-selector">
			<ClayModal.Body>
				<FrontendDataSet
					{...FDS_DEFAULT_PROPS}
					apiURL={getDataSetResourceURL({})}
					id={`${namespace}FDSAdminItemSelector`}
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

export default FDSAdminItemSelector;
