/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import ClayModal, {useModal} from '@clayui/modal';
import {FrontendDataSet, IView} from '@liferay/frontend-data-set-web';
import React, {useState} from 'react';

import {PreviewSite} from '../../../types/exportImportPreview';
import {ExportImportProcess} from '../../../types/exportImportProcess';

const SORTS = [
	{
		active: true,
		default: true,
		direction: 'asc' as const,
		key: 'descriptiveName',
		label: Liferay.Language.get('title'),
	},
];

function getView(process: ExportImportProcess): IView {
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
				process === 'import'
					? {
							fieldName: 'existsInInstance',
							label: Liferay.Language.get('exists-in-instance'),
						}
					: {
							fieldName: 'childSitesCount',
							label: Liferay.Language.get('child-sites'),
						},
			],
		},
	};
}

export default function SiteSelectorModal({
	apiURL,
	onClose,
	onSubmit,
	previewSites,
	process = 'export',
	selectedExternalReferenceCodes,
}: {
	apiURL?: string;
	onClose: () => void;
	onSubmit: (previewSites: PreviewSite[]) => void;
	previewSites?: PreviewSite[];
	process?: ExportImportProcess;
	selectedExternalReferenceCodes: string[];
}) {
	const {observer, onClose: closeModal} = useModal({onClose});

	const [selectedItems, setSelectedItems] = useState<PreviewSite[]>(() =>
		selectedExternalReferenceCodes.map(
			(externalReferenceCode) =>
				previewSites?.find(
					(previewSite) =>
						previewSite.externalReferenceCode ===
						externalReferenceCode
				) ?? {externalReferenceCode}
		)
	);

	return (
		<ClayModal observer={observer} size="full-screen">
			<ClayModal.Header
				closeButtonAriaLabel={Liferay.Language.get('close')}
			>
				{Liferay.Language.get('select-sites')}
			</ClayModal.Header>

			<ClayModal.Body className="p-0">
				<FrontendDataSet
					apiURL={apiURL}
					id={`exportImportSiteSelector_${process}`}
					onItemsPropSearch={(item, query) =>
						(item.descriptiveName ?? '')
							.toLowerCase()
							.includes(query.toLowerCase())
					}
					onSelectedItemsChange={setSelectedItems}
					pagination={{initialDelta: 20}}
					selectedItems={selectedItems}
					selectedItemsKey="externalReferenceCode"
					selectionType="multiple"
					style="fluid"
					views={[getView(process)]}
					{...(previewSites ? {items: previewSites} : {sorts: SORTS})}
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
