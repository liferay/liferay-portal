/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import {useModal} from '@clayui/modal';
import {CommerceServiceProvider} from 'commerce-frontend-js';
import {openConfirmModal, openToast} from 'frontend-js-components-web';
import {dateUtils, sub} from 'frontend-js-web';
import React, {useCallback, useEffect, useState} from 'react';

import InfoBoxModalNotes from '../InfoBoxModalNotes';

const OrderNotesView = ({
	buttonDisplayType,
	elementId,
	hasManageOrderNotesPermission,
	hasManageOrderRestrictedNotesPermission,
	hasViewPermission,
	isOpen,
	label,
	namespace,
	orderId,
	spritemap,
}) => {
	const {observer, onOpenChange, open} = useModal();
	const [inputValue, setInputValue] = useState();
	const [isRestricted, setIsRestricted] = useState(false);
	const [isValid, setIsValid] = useState(true);
	const [notes, setNotes] = useState([]);
	const [updateNotes, setUpdateNotes] = useState(notes);

	const modifiedDate = notes.map((date) => date.modifiedDate).splice(-1);

	useEffect(() => {
		CommerceServiceProvider.DeliveryCartAPI('v1')
			.getCommentsByCartId(orderId)
			.then(({items}) => {
				setNotes(items.reverse());
			});
	}, [orderId, updateNotes]);

	const formatDate = (value) => {
		if (value.length) {
			return dateUtils.format(new Date(value), 'P p');
		}
	};

	const handleDelete = useCallback(
		(commentId) => {
			openConfirmModal({
				message: sub(
					Liferay.Language.get('are-you-sure-you-want-to-delete-this')
				),
				onConfirm: (isConfirmed) => {
					if (isConfirmed) {
						CommerceServiceProvider.DeliveryCartAPI('v1')
							.deleteCommentsByCartId(commentId)
							.then(() => {
								setUpdateNotes(
									notes.filter((note) => note !== commentId)
								);
								openToast({
									message: Liferay.Language.get(
										'your-request-completed-successfully'
									),
									type: 'success',
								});

								handleToggle(false);
								onOpenChange(false);
							})
							.catch((error) => {
								setIsValid(false);
								openToast({
									message:
										error.message ||
										error.title ||
										Liferay.Language.get(
											'an-error-occurred'
										),
									type: 'danger',
								});
							});
					}
				},
			});
		},
		[notes, onOpenChange, setIsValid]
	);

	const handleSubmit = async (event) => {
		event.preventDefault();

		CommerceServiceProvider.DeliveryCartAPI('v1')
			.createCommentsByCartId(orderId, {
				content: inputValue,
				restricted: isRestricted,
			})
			.then((newNote) => {
				setUpdateNotes(newNote);

				handleToggle(false);
				onOpenChange(false);
				openToast({
					message: Liferay.Language.get(
						'your-request-completed-successfully'
					),
					type: 'success',
				});
			})
			.catch((error) => {
				setIsValid(false);
				openToast({
					message:
						error.message ||
						Liferay.Language.get('an-error-occurred'),
					type: 'danger',
				});
			});
	};

	const handleToggle = (value) => {
		setIsRestricted(value);
	};

	return (
		<div className={namespace + 'info-box'} id={elementId}>
			{label && (
				<div className="align-items-center d-flex">
					<div className="h5 info-box-label m-0">{label}</div>

					{hasViewPermission && (
						<ClayButton
							aria-expanded={isOpen}
							aria-haspopup="dialog"
							className="ml-2"
							data-qa-id={`${label}-infoBoxButton`}
							displayType={buttonDisplayType}
							onClick={() => onOpenChange(true)}
							size="xs"
						>
							{Liferay.Language.get('open')}
						</ClayButton>
					)}
				</div>
			)}

			<div>
				<p className="info-box-value">{formatDate(modifiedDate)}</p>
			</div>

			<InfoBoxModalNotes
				handleDelete={handleDelete}
				handleSubmit={handleSubmit}
				handleToggle={handleToggle}
				hasManageOrderNotesPermission={hasManageOrderNotesPermission}
				hasManageOrderRestrictedNotesPermission={
					hasManageOrderRestrictedNotesPermission
				}
				id={`${namespace}InfoBoxModalNotes`}
				isRestricted={isRestricted}
				isValid={isValid}
				label={label}
				notes={notes}
				observer={observer}
				onOpenChange={onOpenChange}
				open={open}
				setInputValue={setInputValue}
				spritemap={spritemap}
			/>
		</div>
	);
};

export default OrderNotesView;
