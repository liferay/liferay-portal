/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import ClayModal, {useModal} from '@clayui/modal';
import {API, openToast} from '@liferay/object-js-components-web';
import React, {useEffect, useState} from 'react';

interface ModalDeleteObjectEntryProps {
	byExternalReferenceCodePath: string;
}

interface ModalDeleteObjectEntryState {
	deletionErrorMessage: string | null;
	objectEntry: ObjectEntry | null;
	visible: boolean;
}

export default function ModalDeleteObjectEntry({
	byExternalReferenceCodePath,
}: ModalDeleteObjectEntryProps) {
	const [modalDeleteObjectEntryState, setModalDeleteObjectEntryState] =
		useState<ModalDeleteObjectEntryState>({
			deletionErrorMessage: null,
			objectEntry: null,
			visible: false,
		});

	const [deleteButtonDisabled, setDeleteButtonDisabled] =
		useState<boolean>(false);

	const {observer, onClose} = useModal({
		onClose: () => {
			setModalDeleteObjectEntryState({
				deletionErrorMessage: null,
				objectEntry: null,
				visible: false,
			});
		},
	});

	const onSubmit = async () => {
		try {
			await API.deleteItem(
				`${byExternalReferenceCodePath}/${modalDeleteObjectEntryState.objectEntry?.externalReferenceCode}`
			);

			openToast({
				message: Liferay.Language.get(
					'your-request-completed-successfully'
				),
				type: 'success',
			});

			onClose();

			setTimeout(() => window.location.reload(), 1000);
		}
		catch (error) {
			setModalDeleteObjectEntryState((prevState) => ({
				...prevState,
				deletionErrorMessage: (error as Error).message,
			}));
		}
	};

	useEffect(() => {
		const openModal = ({objectEntry}: {objectEntry: ObjectEntry}) => {
			setModalDeleteObjectEntryState((prevState) => ({
				...prevState,
				deletionErrorMessage: null,
				objectEntry,
				visible: true,
			}));
		};

		Liferay.on('openModalDeleteObjectEntry', openModal);

		return () =>
			Liferay.detach(
				'openModalDeleteObjectEntry',
				openModal as () => void
			);
	}, []);

	return modalDeleteObjectEntryState.visible ? (
		<ClayModal
			center
			observer={observer}
			status={
				modalDeleteObjectEntryState.deletionErrorMessage
					? 'warning'
					: 'danger'
			}
		>
			<ClayModal.Header>
				{modalDeleteObjectEntryState.deletionErrorMessage
					? Liferay.Language.get('deletion-not-possible')
					: Liferay.Language.get('delete-entry')}
			</ClayModal.Header>

			<ClayModal.Body>
				{modalDeleteObjectEntryState.deletionErrorMessage ??
					Liferay.Language.get(
						'it-may-affect-many-records-are-you-sure-you-want-to-delete-this-entry'
					)}
			</ClayModal.Body>

			<ClayModal.Footer
				last={
					<ClayButton.Group spaced>
						{!modalDeleteObjectEntryState.deletionErrorMessage && (
							<ClayButton
								displayType="secondary"
								onClick={onClose}
							>
								{Liferay.Language.get('cancel')}
							</ClayButton>
						)}

						<ClayButton
							disabled={deleteButtonDisabled}
							displayType={
								modalDeleteObjectEntryState.deletionErrorMessage
									? 'warning'
									: 'danger'
							}
							onClick={() => {
								setDeleteButtonDisabled(true);
								modalDeleteObjectEntryState.deletionErrorMessage
									? onClose()
									: onSubmit();
							}}
						>
							{modalDeleteObjectEntryState.deletionErrorMessage
								? Liferay.Language.get('close')
								: Liferay.Language.get('delete')}
						</ClayButton>
					</ClayButton.Group>
				}
			/>
		</ClayModal>
	) : null;
}
