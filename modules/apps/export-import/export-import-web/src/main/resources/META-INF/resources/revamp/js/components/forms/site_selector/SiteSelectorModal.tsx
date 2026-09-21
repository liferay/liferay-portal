/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import ClayModal, {useModal} from '@clayui/modal';
import {FrontendDataSet, IView} from '@liferay/frontend-data-set-web';
import React, {useState} from 'react';

import {PreviewSite} from '../../../types/exportImportPreview';

const DELTAS = [{label: 20}, {label: 40}, {label: 60}];

const FDS_ID = 'exportImportSiteSelector';

const SORTS = [
	{
		active: true,
		default: true,
		direction: 'asc' as const,
		key: 'descriptiveName',
		label: Liferay.Language.get('title'),
	},
];

function getView(showExistsInInstance: boolean): IView {
	return {
		contentRenderer: 'table',
		default: true,
		label: Liferay.Language.get('table'),
		name: 'table',
		schema: {
			fields: [
				{
					expand: true,
					fieldName: 'descriptiveName',
					label: Liferay.Language.get('title'),
				},
				{
					fieldName: 'path',
					label: Liferay.Language.get('path'),
				},
				showExistsInInstance
					? {
							fieldName: 'existsInInstance',
							label: Liferay.Language.get('exists-in-instance'),
						}
					: {
							fieldName: 'childSiteCount',
							label: Liferay.Language.get('child-sites'),
						},
			],
		},
	};
}

function toRow(previewSite: PreviewSite) {
	return {
		...previewSite,
		id: previewSite.externalReferenceCode,
	};
}

export default function SiteSelectorModal({
	apiURL,
	onClose,
	onSubmit,
	previewSites,
	selectedExternalReferenceCodes,
	showExistsInInstance = false,
}: {
	apiURL?: string;
	onClose: () => void;
	onSubmit: (previewSites: PreviewSite[]) => void;
	previewSites?: PreviewSite[];
	selectedExternalReferenceCodes: string[];
	showExistsInInstance?: boolean;
}) {
	const {observer, onClose: closeModal} = useModal({onClose});

	const items = previewSites?.map(toRow);

	const [selectedItems, setSelectedItems] = useState<PreviewSite[]>(() =>
		selectedExternalReferenceCodes.map(
			(externalReferenceCode) =>
				items?.find(
					(item) =>
						item.externalReferenceCode === externalReferenceCode
				) ?? {externalReferenceCode}
		)
	);

	return (
		<ClayModal observer={observer} size="lg">
			<ClayModal.Header>
				{Liferay.Language.get('select-sites')}
			</ClayModal.Header>

			<ClayModal.Body>
				<FrontendDataSet
					apiURL={apiURL}
					id={FDS_ID}
					itemsActions={[]}
					onItemsPropSearch={(item, query) =>
						String(item.descriptiveName ?? '')
							.toLowerCase()
							.includes(query.toLowerCase())
					}
					onSelectedItemsChange={setSelectedItems}
					pagination={{deltas: DELTAS, initialDelta: DELTAS[0].label}}
					selectedItems={selectedItems}
					selectedItemsKey="externalReferenceCode"
					selectionType="multiple"
					showManagementBar
					showPagination
					showSearch
					showSelectAll
					style="stacked"
					views={[getView(showExistsInInstance)]}
					{...(items ? {items} : {sorts: SORTS})}
				/>
			</ClayModal.Body>

			<ClayModal.Footer
				last={
					<ClayButton.Group spaced>
						<ClayButton
							displayType="secondary"
							onClick={closeModal}
						>
							{Liferay.Language.get('cancel')}
						</ClayButton>

						<ClayButton
							onClick={() => {
								onSubmit(selectedItems);

								closeModal();
							}}
						>
							{Liferay.Language.get('select')}
						</ClayButton>
					</ClayButton.Group>
				}
			/>
		</ClayModal>
	);
}
