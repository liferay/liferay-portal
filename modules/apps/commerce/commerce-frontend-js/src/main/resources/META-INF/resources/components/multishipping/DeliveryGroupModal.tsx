/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import ClayForm, {ClayInput} from '@clayui/form';
import ClayIcon from '@clayui/icon';
import ClayModal from '@clayui/modal';
import {Observer} from '@clayui/modal/lib/types';
import classnames from 'classnames';
import {sub} from 'frontend-js-web';
import React, {useCallback, useRef, useState} from 'react';

import AddressSelector from './AddressSelector';
import ErrorMessage from './ErrorMessage';
import {
	IAddressSubtypeConfiguration,
	IDeliveryGroup,
	IFieldError,
	IPostalAddress,
} from './Types';

const MANDATORY_FIELDS = ['name'];

interface IDeliveryGroupModalProps {
	onOpenModal(value: boolean): void;
	accountId: number;
	addressSubtypeConfiguration?: IAddressSubtypeConfiguration;
	deliveryGroup?: IDeliveryGroup;
	handleSubmit: any;
	hasManageAddressesPermission?: boolean;
	namespace?: string;
	observerModal: Observer;
	spritemap?: string;
}

const DeliveryGroupModal = ({
	accountId,
	addressSubtypeConfiguration,
	deliveryGroup,
	handleSubmit,
	hasManageAddressesPermission = true,
	namespace = 'DeliveryGroupModal',
	observerModal,
	onOpenModal,
	spritemap = '',
}: IDeliveryGroupModalProps) => {
	const formRef = useRef(null);
	const [deliveryGroupState, setDeliveryGroupState] =
		useState<IDeliveryGroup>(
			deliveryGroup || {
				addressId: 0,
				deliveryDate: '',
				id: 0,
				name: '',
			}
		);
	const [errors, setErrors] = useState<IFieldError>(
		deliveryGroup
			? {}
			: MANDATORY_FIELDS.reduce((map: IFieldError, field) => {
					map[field] = '';

					return map;
				}, {})
	);
	const [handleAddressSubmit, setHandleAddressSubmit] = useState(
		() =>
			async (event: Event): Promise<IPostalAddress> => {
				event.preventDefault();

				return {id: 0};
			}
	);
	const [isAddressFormValid, setIsAddressFormValid] = useState(false);

	const handleFieldChange = useCallback(
		({
			target: {name: fieldName, value},
		}: {
			target: {
				name: string;
				value: boolean | number | string | undefined;
			};
		}) => {
			setDeliveryGroupState((prevState) => ({
				...prevState,
				[fieldName]: value,
			}));

			if (MANDATORY_FIELDS.includes(fieldName)) {
				setErrors((prevState) => {
					if (!value) {
						return {
							...prevState,
							[fieldName]: Liferay.Language.get(
								'this-field-is-required'
							),
						};
					}
					else {
						delete prevState[fieldName];

						return {
							...prevState,
						};
					}
				});
			}
		},
		[]
	);

	const handleFormSubmit = useCallback(
		async (event: any) => {
			event.preventDefault();

			if (isAddressFormValid && !!deliveryGroupState.name?.length) {
				const address = await handleAddressSubmit(event);

				if (address && address.id) {
					const updatedDeliveryGroup = {
						...deliveryGroupState,
						address,
						addressId: address.id,
					};

					handleSubmit(updatedDeliveryGroup);
				}
			}
		},
		[
			deliveryGroupState,
			handleAddressSubmit,
			handleSubmit,
			isAddressFormValid,
		]
	);

	return (
		<ClayModal
			id={`${namespace}_modalDeliveryGroup`}
			observer={observerModal}
			spritemap={spritemap}
		>
			<ClayModal.Header
				closeButtonAriaLabel={Liferay.Language.get('close')}
			>
				{sub(
					deliveryGroup
						? Liferay.Language.get('edit-x')
						: Liferay.Language.get('add-x'),
					Liferay.Language.get('delivery-group')
				)}
			</ClayModal.Header>

			<ClayModal.Body>
				<ClayForm onSubmit={handleFormSubmit} ref={formRef}>
					<ClayForm.Group
						className={classnames({
							'has-error': !!errors.name,
						})}
					>
						<label htmlFor={`${namespace}name`}>
							{Liferay.Language.get('group-name')}

							<ClayIcon
								className="c-ml-1 reference-mark"
								symbol="asterisk"
							/>
						</label>

						<ClayInput
							id={`${namespace}name`}
							name="name"
							onChange={handleFieldChange}
							required={true}
							type="text"
							value={deliveryGroupState.name}
						/>

						<ErrorMessage errors={errors} name="name" />
					</ClayForm.Group>

					<div>
						<div className="h4 sheet-subtitle">
							{Liferay.Language.get('delivery-date')}
						</div>

						<ClayForm.Group>
							<label htmlFor={`${namespace}deliveryDate`}>
								{Liferay.Language.get('date')}
							</label>

							<ClayInput
								id={`${namespace}deliveryDate`}
								name="deliveryDate"
								onChange={handleFieldChange}
								type="date"
								value={
									deliveryGroupState.deliveryDate
										? new Date(
												deliveryGroupState.deliveryDate
											)
												.toISOString()
												.split('T')[0]
										: ''
								}
							/>
						</ClayForm.Group>
					</div>

					<div>
						<div className="h4 sheet-subtitle">
							{Liferay.Language.get('shipping-address')}
						</div>

						<AddressSelector
							accountId={accountId}
							addressId={deliveryGroupState.addressId || 0}
							addressSubtypeConfiguration={
								addressSubtypeConfiguration
							}
							addressType="shipping"
							hasManageAddressesPermission={
								hasManageAddressesPermission
							}
							label={Liferay.Language.get('shipping-address')}
							setHandleNameChange={(name: string) => {
								if (!deliveryGroupState.name && name) {
									setDeliveryGroupState((prevState) => ({
										...prevState,
										name,
									}));
								}
							}}
							setHandleSubmit={setHandleAddressSubmit}
							setIsFormValid={setIsAddressFormValid}
						/>
					</div>
				</ClayForm>
			</ClayModal.Body>

			<ClayModal.Footer
				last={
					<ClayButton.Group spaced>
						<ClayButton
							aria-label={Liferay.Language.get('cancel')}
							displayType="secondary"
							onClick={() => onOpenModal(false)}
						>
							{Liferay.Language.get('cancel')}
						</ClayButton>

						<ClayButton
							aria-label={Liferay.Language.get('save')}
							disabled={
								!isAddressFormValid ||
								!!Object.keys(errors).length
							}
							onClick={handleFormSubmit}
						>
							{Liferay.Language.get('save')}
						</ClayButton>
					</ClayButton.Group>
				}
			/>
		</ClayModal>
	);
};

export default DeliveryGroupModal;
