/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import ClayForm, {ClayInput} from '@clayui/form';
import ClayModal from '@clayui/modal';
import classNames from 'classnames';
import {openToast} from 'frontend-js-components-web';
import {fetch, sub} from 'frontend-js-web';
import React, {useState} from 'react';

interface DeleteAPIApplicationModal {
	closeModal: voidReturn;
	itemData: APIApplicationItem;
	loadData: voidReturn;
}

export function DeleteAPIApplicationModalContent({
	closeModal,
	itemData,
	loadData,
}: DeleteAPIApplicationModal) {
	const [confirmed, setConfirmed] = useState(false);
	const [displayError, setDisplayError] = useState(false);

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
							'api-application-was-deleted'
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

	const handleConfirmationInput = (value: string) => {
		if (value === itemData.title) {
			setDisplayError(false);
			setConfirmed(true);
		}
		else if (value === '') {
			setDisplayError(false);
			setConfirmed(false);
		}
		else {
			setDisplayError(true);
			setConfirmed(false);
		}
	};

	return (
		<>
			<ClayModal.Header
				closeButtonAriaLabel={Liferay.Language.get('close')}
			>
				{Liferay.Language.get('delete-api-application')}
			</ClayModal.Header>

			<div className="modal-body">
				<p>
					{Liferay.Language.get(
						'this-action-cannot-be-undone-and-will-permanently-delete-all-the-related-schemas-and-endpoints-within-this-api'
					)}
				</p>

				<p
					dangerouslySetInnerHTML={{
						__html: sub(
							Liferay.Language.get(
								'please-type-the-api-title-x-to-confirm'
							),
							`<strong id="titleConfirmationElement">${itemData.title}</strong>`
						),
					}}
				/>

				<ClayForm.Group
					className={classNames({
						'has-error': displayError,
					})}
				>
					<ClayInput
						onChange={({target: {value}}) => {
							handleConfirmationInput(value);
						}}
					/>

					<ClayForm.FeedbackGroup>
						{displayError && (
							<ClayForm.FeedbackItem className="mt-2">
								<ClayForm.FeedbackIndicator symbol="exclamation-full" />

								{Liferay.Language.get(
									'please-type-the-api-title-mentioned-above'
								)}
							</ClayForm.FeedbackItem>
						)}
					</ClayForm.FeedbackGroup>
				</ClayForm.Group>
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
							disabled={!confirmed}
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
