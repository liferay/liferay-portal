/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import {ClayRadio, ClayRadioGroup} from '@clayui/form';
import ClayModal, {useModal} from '@clayui/modal';
import {useIsMounted} from '@liferay/frontend-js-react-web';
import React from 'react';

import {DELETION_TYPES} from '../constants/deletionTypes';

export default function DeletionModal({
	deletionType,
	onCloseModal,
	onDeleteItem,
	setDeletionType,
}) {
	const isMounted = useIsMounted();

	const {observer, onClose} = useModal({
		onClose: () => {
			if (isMounted()) {
				onCloseModal();
			}
		},
	});

	return (
		<ClayModal
			containerProps={{className: 'cadmin'}}
			observer={observer}
			size="lg"
		>
			<ClayModal.Header
				closeButtonAriaLabel={Liferay.Language.get('close')}
			>
				{Liferay.Language.get('delete-item')}
			</ClayModal.Header>

			<ClayModal.Body>
				<p className="font-weight-semi-bold">
					{Liferay.Language.get(
						'the-item-you-want-to-delete-has-children-that-also-can-be-removed'
					)}
				</p>

				<p className="text-secondary">
					{Liferay.Language.get('what-action-do-you-want-to-take')}
				</p>

				<ClayRadioGroup
					onChange={(type) => setDeletionType(type)}
					value={deletionType}
				>
					<ClayRadio
						label={Liferay.Language.get('only-delete-this-item')}
						value={DELETION_TYPES.single}
					/>

					<ClayRadio
						label={Liferay.Language.get('delete-item-and-children')}
						value={DELETION_TYPES.bulk}
					/>
				</ClayRadioGroup>
			</ClayModal.Body>

			<ClayModal.Footer
				last={
					<ClayButton.Group spaced>
						<ClayButton displayType="secondary" onClick={onClose}>
							{Liferay.Language.get('cancel')}
						</ClayButton>

						<ClayButton
							displayType="primary"
							onClick={onDeleteItem}
						>
							{Liferay.Language.get('delete')}
						</ClayButton>
					</ClayButton.Group>
				}
			/>
		</ClayModal>
	);
}
