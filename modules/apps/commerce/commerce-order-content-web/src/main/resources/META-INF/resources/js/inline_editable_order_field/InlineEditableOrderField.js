/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton, {ClayButtonWithIcon} from '@clayui/button';
import ClayForm, {ClayInput} from '@clayui/form';
import ClayModal, {useModal} from '@clayui/modal';
import {CommerceServiceProvider} from 'commerce-frontend-js';
import {openToast} from 'frontend-js-components-web';
import {sub} from 'frontend-js-web';
import React, {useCallback, useState} from 'react';

const InlineEditableOrderField = ({
	field,
	fieldHelpMessage,
	fieldValue,
	hasPermission,
	isOpenOrder,
	label,
	namespace,
	orderId,
	spritemap,
}) => {
	const {observer, onOpenChange, open} = useModal();
	const [currentValue, setCurrentValue] = useState(fieldValue);
	const [inputValue, setInputValue] = useState(currentValue);

	const onSubmit = useCallback(
		(event) => {
			event.preventDefault();

			const submit = isOpenOrder
				? CommerceServiceProvider.DeliveryCartAPI('v1').updateCartById
				: CommerceServiceProvider.DeliveryOrderAPI('v1')
						.updatePlacedOrderById;

			submit(orderId, {[field]: inputValue})
				.then(({[field]: editedField}) => {
					setCurrentValue(editedField);

					onOpenChange(false);
				})
				.catch((error) => {
					openToast({
						message:
							error.message ||
							Liferay.Language.get(
								'an-unexpected-error-occurred'
							),
						type: 'danger',
					});
				});
		},

		// eslint-disable-next-line react-hooks/exhaustive-deps
		[inputValue, isOpenOrder, onOpenChange, orderId]
	);

	return (
		<>
			<div
				className="align-items-center d-flex"
				id={`${namespace}inlineEditableOrderField`}
			>
				<span className="text-black-50">{label && `${label}`}</span>

				{currentValue ? (
					<span className="font-weight-bold ml-2">
						{currentValue}
					</span>
				) : (
					<ClayButton
						aria-label={Liferay.Language.get('not-set')}
						className="border-bottom border-dashed btn-sm ml-2 p-0 small text-black-50 text-decoration-none"
						displayType="link"
						onClick={() =>
							hasPermission &&
							field !== 'externalReferenceCode' &&
							onOpenChange(true)
						}
					>
						{Liferay.Language.get('not-set')}
					</ClayButton>
				)}

				<ClayButtonWithIcon
					className="btn-sm ml-1 text-black-50"
					displayType="link"
					symbol="info-circle"
					title={fieldHelpMessage}
				/>

				{hasPermission && field !== 'externalReferenceCode' && (
					<span>
						<ClayButtonWithIcon
							className="btn-sm text-black-50"
							displayType="link"
							onClick={() => onOpenChange(true)}
							symbol="pencil"
							title={Liferay.Language.get('edit')}
						/>
					</span>
				)}
			</div>

			{open && (
				<ClayModal
					id={`${namespace}inlineEditableOrderFieldModal`}
					observer={observer}
					onOpenChange={onOpenChange}
					spritemap={spritemap}
				>
					<ClayForm onSubmit={onSubmit}>
						<ClayModal.Header
							closeButtonAriaLabel={Liferay.Language.get('close')}
						>
							{sub(Liferay.Language.get('edit-x'), label)}
						</ClayModal.Header>

						<ClayModal.Body>
							<ClayForm.Group>
								<label htmlFor={`${namespace}${field}`}>
									{label}
								</label>

								<ClayInput
									id={`${namespace}${field}`}
									name={`${namespace}${field}`}
									onChange={({target}) =>
										setInputValue(target?.value)
									}
									type="text"
									value={inputValue}
								/>
							</ClayForm.Group>
						</ClayModal.Body>

						<ClayModal.Footer
							last={
								<ClayButton.Group spaced>
									<ClayButton
										displayType="secondary"
										onClick={() => onOpenChange(false)}
									>
										{Liferay.Language.get('cancel')}
									</ClayButton>

									<ClayButton type="submit">
										{Liferay.Language.get('save')}
									</ClayButton>
								</ClayButton.Group>
							}
						/>
					</ClayForm>
				</ClayModal>
			)}
		</>
	);
};

export default InlineEditableOrderField;
