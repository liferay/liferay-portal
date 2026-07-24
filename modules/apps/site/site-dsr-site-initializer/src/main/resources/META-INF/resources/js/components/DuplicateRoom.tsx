/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayAlert from '@clayui/alert';
import ClayButton from '@clayui/button';
import ClayIcon from '@clayui/icon';
import ClayModal from '@clayui/modal';
import {FrontendDataSet} from '@liferay/frontend-data-set-web';
import {mimeTypeUtils} from 'frontend-js-web';
import React, {useCallback, useEffect, useState} from 'react';

import RoomService from '../common/services/RoomService';
import {
	displayErrorToast,
	displaySuccessToast,
} from '../common/utils/toastUtil';
import {IDuplicateRoomProps} from '../common/utils/types';

const DSR_DOCUMENTS_EXTERNAL_REFERENCE_CODE = 'DSR_DOCUMENTS';

const DocumentTitleCell = ({
	itemData,
	value,
}: {
	itemData: any;
	value: string;
}) => (
	<span className="align-items-center d-flex text-secondary">
		<ClayIcon
			className="mr-2"
			symbol={mimeTypeUtils.getIconFromMimeType(itemData.encodingFormat)}
		/>

		{value}
	</span>
);

const DocumentTypeCell = ({itemData}: {itemData: any}) => (
	<>{itemData.documentType?.name}</>
);

function DuplicateRoom({
	closeModal,
	loadData,
	name,
	roomId,
	siteId,
}: IDuplicateRoomProps) {
	const [documentsFolderId, setDocumentsFolderId] = useState<number | null>(
		null
	);
	const [loading, setLoading] = useState(false);
	const [selectedFileEntryIds, setSelectedFileEntryIds] = useState<number[]>(
		[]
	);

	useEffect(() => {
		RoomService.getDocumentsFolderId(
			siteId,
			DSR_DOCUMENTS_EXTERNAL_REFERENCE_CODE
		)
			.then(setDocumentsFolderId)
			.catch((error) => displayErrorToast((error as Error).message));
	}, [siteId]);

	const handleDuplicate = useCallback(async () => {
		setLoading(true);

		try {
			const duplicatedRoom = await RoomService.duplicateRoom(roomId, {
				fileEntryIds: selectedFileEntryIds,
				name: `${name} (Copy)`,
			});

			for (let i = 0; i < 10; i++) {
				await new Promise((resolve) => setTimeout(resolve, 2000));

				const room = await RoomService.getRoom(duplicatedRoom.id);

				if (room.initialized) {
					break;
				}
			}

			displaySuccessToast();

			closeModal();

			loadData();
		}
		catch (error) {
			displayErrorToast((error as Error).message);
		}
		finally {
			setLoading(false);
		}
	}, [closeModal, loadData, name, roomId, selectedFileEntryIds]);

	return (
		<>
			<ClayModal.Header>
				{Liferay.Language.get('duplicate-digital-sales-room')}
			</ClayModal.Header>

			<ClayModal.Body>
				<ClayAlert displayType="info">
					{Liferay.Language.get(
						'if-you-also-want-to-duplicate-documents-select-which-ones-to-include'
					)}
				</ClayAlert>

				{documentsFolderId !== null && (
					<div className="dsr-duplicate-room-documents">
						<FrontendDataSet
							apiURL={`/o/headless-delivery/v1.0/document-folders/${documentsFolderId}/documents`}
							bulkActions={[]}
							customRenderers={{
								tableCell: [
									{
										component: DocumentTitleCell,
										name: 'documentTitleCellRenderer',
										type: 'internal',
									},
									{
										component: DocumentTypeCell,
										name: 'documentTypeCellRenderer',
										type: 'internal',
									},
								],
							}}
							hideManagementBarInEmptyState={true}
							id="dsrDuplicateRoomDocuments"

							// @ts-ignore

							onSelectedItemsChange={(selectedItems: any) => {
								const items = JSON.parse(
									JSON.stringify(selectedItems)
								);

								setSelectedFileEntryIds(
									items.map((item: {id: number}) => item.id)
								);
							}}
							selectedItemsKey="id"
							selectionType="multiple"
							views={[
								{
									contentRenderer: 'table',
									label: Liferay.Language.get('table'),
									name: 'table',
									schema: {
										fields: [
											{
												contentRenderer:
													'documentTitleCellRenderer',
												fieldName: 'title',
												label: Liferay.Language.get(
													'title'
												),
												sortable: false,
											},
											{
												contentRenderer:
													'documentTypeCellRenderer',
												fieldName: 'documentType',
												label: Liferay.Language.get(
													'document-type'
												),
												sortable: false,
											},
										],
									},
									thumbnail: 'table',
								},
							]}
						/>
					</div>
				)}
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
							disabled={loading}
							onClick={handleDuplicate}
						>
							{Liferay.Language.get('duplicate')}
						</ClayButton>
					</ClayButton.Group>
				}
			/>
		</>
	);
}

export default DuplicateRoom;
