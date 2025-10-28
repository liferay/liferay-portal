/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import {Text} from '@clayui/core';
import ClayModal, {useModal} from '@clayui/modal';
import {API} from '@liferay/object-js-components-web';
import {openToast} from 'frontend-js-components-web';
import React, {useEffect, useState} from 'react';

export interface IModalState extends Partial<ListTypeEntry> {
	header?: string;
	itemKey?: string;
	listTypeEntryId?: number;
	reloadIframeWindow?: () => void;
	setValues?: (values: Partial<ListTypeDefinition>) => void;
	values?: Partial<ListTypeDefinition>;
}

function ModalDeleteListType() {
	const [
		{
			header,
			itemKey,
			listTypeEntryId,
			reloadIframeWindow,
			setValues,
			values,
		},
		setState,
	] = useState<IModalState>({});

	const [APIError, setAPIError] = useState<string>('');

	const resetModal = () => {
		setAPIError('');
		setState({});
	};

	const {observer, onClose} = useModal({
		onClose: resetModal,
	});

	useEffect(() => {
		const openModal = (modalProps: Partial<IModalState>) => {
			const newModalProps = {...modalProps};

			setState(newModalProps);
		};

		Liferay.on('openModalDeleteListType', openModal);

		return () =>
			Liferay.detach('openModalDeleteListType', openModal as () => void);
	}, []);

	useEffect(() => {
		if (APIError) {
			openToast({
				message: APIError,
				type: 'danger',
			});
		}
		setAPIError('');
	}, [APIError]);

	const handleDelete = async () => {
		try {
			await API.deleteListTypeEntry(listTypeEntryId as number);
			openToast({
				message: Liferay.Language.get(
					'the-picklist-item-was-deleted-successfully'
				),
				type: 'success',
			});

			if (values && setValues) {
				const {listTypeEntries} = values;
				const newListTypeEntries = listTypeEntries?.filter(
					(listTypeEntry) => listTypeEntry.key !== itemKey
				);

				setValues({
					...values,
					listTypeEntries: newListTypeEntries as ListTypeEntry[],
				});
			}

			onClose();
			if (reloadIframeWindow) {
				reloadIframeWindow();
			}
		}
		catch (error) {
			setAPIError((error as Error).message);
		}
	};

	return listTypeEntryId ? (
		<ClayModal center observer={observer} status="danger">
			<ClayModal.Header
				closeButtonAriaLabel={Liferay.Language.get('close')}
			>
				{header}
			</ClayModal.Header>

			<ClayModal.Body>
				<Text as="p">
					{Liferay.Language.get(
						'this-action-cannot-be-undone-and-will-permanently-delete-this-item'
					)}
				</Text>

				<Text as="p">
					{Liferay.Language.get('it-may-affect-many-records')}
				</Text>

				<Text as="p">
					{Liferay.Language.get('do-you-want-to-proceed')}
				</Text>
			</ClayModal.Body>

			<ClayModal.Footer
				last={
					<ClayButton.Group spaced>
						<ClayButton
							displayType="secondary"
							onClick={() => onClose()}
						>
							{Liferay.Language.get('cancel')}
						</ClayButton>

						<ClayButton
							displayType="danger"
							onClick={handleDelete}
							type="submit"
						>
							{Liferay.Language.get('delete')}
						</ClayButton>
					</ClayButton.Group>
				}
			/>
		</ClayModal>
	) : null;
}

export default ModalDeleteListType;
