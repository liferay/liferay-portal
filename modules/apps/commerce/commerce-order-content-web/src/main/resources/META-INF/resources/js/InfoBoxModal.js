/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import ClayForm from '@clayui/form';
import ClayModal from '@clayui/modal';
import React, {useState} from 'react';

import InfoBoxModalAddressInput from './info_box/modal/InfoBoxModalAddressInput';
import InfoBoxModalDateInput from './info_box/modal/InfoBoxModalDateInput';
import InfoBoxModalPaymentMethodInput from './info_box/modal/InfoBoxModalPaymentMethodInput';
import InfoBoxModalShippingMethodInput from './info_box/modal/InfoBoxModalShippingMethodInput';
import InfoBoxModalTermInput from './info_box/modal/InfoBoxModalTermInput';
import InfoBoxModalTextInput from './info_box/modal/InfoBoxModalTextInput';

const getInputRendered = (field, fieldValueType) => {
	if (fieldValueType === 'date') {
		return InfoBoxModalDateInput;
	}

	if (field === 'billingAddress' || field === 'shippingAddress') {
		return InfoBoxModalAddressInput;
	}

	if (field === 'deliveryTermId' || field === 'paymentTermId') {
		return InfoBoxModalTermInput;
	}

	if (field === 'paymentMethod') {
		return InfoBoxModalPaymentMethodInput;
	}

	if (field === 'shippingMethod') {
		return InfoBoxModalShippingMethodInput;
	}

	return InfoBoxModalTextInput;
};

const InfoBoxModal = ({
	additionalProps,
	field,
	fieldValueType,
	handleSubmit,
	id,
	inputValue,
	isOpen,
	label,
	observer,
	onOpenChange,
	open,
	orderId,
	setHandleSubmit,
	setInputValue,
	setParseRequest,
	setParseResponse,
	spritemap,
	submitOrder,
}) => {
	const [isValid, setIsValid] = useState(true);
	const InputRenderer = getInputRendered(field, fieldValueType);

	return (
		<>
			{open && (
				<ClayModal
					id={id}
					observer={observer}
					size="lg"
					spritemap={spritemap}
				>
					<ClayForm onSubmit={handleSubmit}>
						<ClayModal.Header
							closeButtonAriaLabel={Liferay.Language.get('close')}
						>
							{label}
						</ClayModal.Header>

						<ClayModal.Body>
							<ClayForm.Group>
								<InputRenderer
									additionalProps={additionalProps}
									field={field}
									inputValue={inputValue}
									isOpen={isOpen}
									label={label}
									orderId={orderId}
									setHandleSubmit={setHandleSubmit}
									setInputValue={setInputValue}
									setIsValid={setIsValid}
									setParseRequest={setParseRequest}
									setParseResponse={setParseResponse}
									spritemap={spritemap}
									submitOrder={submitOrder}
								/>
							</ClayForm.Group>
						</ClayModal.Body>

						{(field === 'deliveryTermId' ||
							field === 'paymentTermId') &&
						!isOpen ? (
							<></>
						) : (
							<ClayModal.Footer
								last={
									<ClayButton.Group spaced>
										<ClayButton
											displayType="secondary"
											onClick={() => onOpenChange(false)}
										>
											{Liferay.Language.get('cancel')}
										</ClayButton>

										<ClayButton
											disabled={!isValid}
											type="submit"
										>
											{Liferay.Language.get('save')}
										</ClayButton>
									</ClayButton.Group>
								}
							/>
						)}
					</ClayForm>
				</ClayModal>
			)}
		</>
	);
};

export default InfoBoxModal;
