/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import ClayModal, {useModal} from '@clayui/modal';
import ClayTable from '@clayui/table';
import React from 'react';

const ImportPreviewModal = ({
	closeModal,
	fieldsSelections,
	fileContent,
	startImport,
}) => {
	const dbFieldsSelected = Object.keys(fieldsSelections).sort();

	const {observer, onClose} = useModal({
		onClose: () => {
			closeModal();
		},
	});

	return (
		<ClayModal observer={observer} size="lg">
			<ClayModal.Header
				closeButtonAriaLabel={Liferay.Language.get('close')}
			>
				{Liferay.Language.get('preview')}
			</ClayModal.Header>

			<ClayModal.Body className="p-0" scrollable>
				<ClayTable borderless hover={false}>
					<ClayTable.Head>
						<ClayTable.Row>
							{dbFieldsSelected.map((dbFieldName) => (
								<ClayTable.Cell headingCell key={dbFieldName}>
									{dbFieldName}
								</ClayTable.Cell>
							))}
						</ClayTable.Row>
					</ClayTable.Head>

					<ClayTable.Body>
						{fileContent.map((rowData, index) => (
							<ClayTable.Row
								key={`${JSON.stringify(rowData)}_${index}`}
							>
								{dbFieldsSelected.map((dbField) => {
									const fileField = fieldsSelections[dbField];

									return (
										<ClayTable.Cell
											key={dbField}
											truncate="true"
										>
											{JSON.stringify(rowData[fileField])}
										</ClayTable.Cell>
									);
								})}
							</ClayTable.Row>
						))}
					</ClayTable.Body>
				</ClayTable>
			</ClayModal.Body>

			<ClayModal.Footer
				last={
					<ClayButton.Group spaced>
						<ClayButton displayType="secondary" onClick={onClose}>
							{Liferay.Language.get('cancel')}
						</ClayButton>

						<ClayButton
							data-testid="start-import"
							displayType="primary"
							onClick={startImport}
							type="submit"
						>
							{Liferay.Language.get('start-import')}
						</ClayButton>
					</ClayButton.Group>
				}
			/>
		</ClayModal>
	);
};

export default ImportPreviewModal;
