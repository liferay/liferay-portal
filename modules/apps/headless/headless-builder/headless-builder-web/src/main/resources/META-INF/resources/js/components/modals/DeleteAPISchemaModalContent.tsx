/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import ClayModal from '@clayui/modal';
import {openToast} from 'frontend-js-components-web';
import {fetch} from 'frontend-js-web';
import React from 'react';

interface DeleteAPISchemaModal {
	closeModal: voidReturn;
	itemData: BaseItem;
	loadData: voidReturn;
}

export function DeleteAPISchemaModalContent({
	closeModal,
	itemData,
	loadData,
}: DeleteAPISchemaModal) {
	async function handleDelete() {
		const deleteURL = itemData.actions.delete.href;

		fetch(deleteURL.replace('{id}', String(itemData.id)), {
			method: 'DELETE',
		})
			.then(({ok}) => {
				if (ok) {
					closeModal();
					loadData();
					openToast({
						message: Liferay.Language.get(
							'the-schema-was-deleted-successfully'
						),
						type: 'success',
					});
				}
				else {
					throw new Error();
				}
			})
			.catch(() => {
				openToast({
					message: Liferay.Language.get(
						'an-unexpected-error-occurred'
					),
					type: 'danger',
				});
			});
	}

	return (
		<>
			<ClayModal.Header>
				{Liferay.Language.get('delete-api-schema')}
			</ClayModal.Header>

			<div className="modal-body">
				<p>
					{Liferay.Language.get(
						'this-action-cannot-be-undone-and-will-erase-all-schema-information'
					)}
				</p>

				<p>
					{Liferay.Language.get(
						'linked-endpoints-may-change-their-behaviour'
					)}
				</p>
			</div>

			<ClayModal.Footer
				last={
					<ClayButton.Group spaced>
						<ClayButton
							displayType="secondary"
							id="modalCancelButton"
							onClick={closeModal}
							type="button"
						>
							{Liferay.Language.get('cancel')}
						</ClayButton>

						<ClayButton
							displayType="danger"
							id="modalDeleteButton"
							onClick={handleDelete}
							type="button"
						>
							{Liferay.Language.get('delete')}
						</ClayButton>
					</ClayButton.Group>
				}
			/>
		</>
	);
}
