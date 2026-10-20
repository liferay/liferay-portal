/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import {useModal} from '@clayui/modal';
import {CommerceServiceProvider, commerceEvents} from 'commerce-frontend-js';
import {openToast} from 'frontend-js-components-web';
import {sub} from 'frontend-js-web';
import React, {useCallback, useEffect, useState} from 'react';

import InfoBoxModal from '../InfoBoxModal';
import {formatValue, isEditable} from '../util';

const DefaultView = ({
	additionalProps,
	buttonDisplayType,
	elementId,
	field,
	fieldValue,
	fieldValueType,
	hasUpdatePermission,
	isOpen,
	label,
	namespace,
	orderId,
	readOnly,
	spritemap,
}) => {
	const {observer, onOpenChange, open} = useModal();
	const [inputValue, setInputValue] = useState(
		additionalProps?.value ? additionalProps?.value : fieldValue
	);
	const [currentValue, setCurrentValue] = useState(inputValue);
	const [parseRequest, setParseRequest] = useState(
		() => (field, inputValue) => {
			return {
				[field]: inputValue,
			};
		}
	);
	const [parseResponse, setParseResponse] = useState(
		() => (field, response) => {
			if (response) {
				return response[field];
			}

			return null;
		}
	);
	const [value, setValue] = useState(fieldValue);

	const hasTerms = () => {
		return isOpen ? true : !!additionalProps?.termDescription;
	};

	const submitOrder = useCallback(
		(inputValue) => {
			const updateOrder = isOpen
				? CommerceServiceProvider.DeliveryCartAPI('v1').updateCartById
				: CommerceServiceProvider.DeliveryOrderAPI('v1')
						.updatePlacedOrderById;

			updateOrder(orderId, parseRequest(field, inputValue))
				.then((order) => {
					setCurrentValue(inputValue);
					setValue(parseResponse(field, order));

					onOpenChange(false);

					Liferay.fire(commerceEvents.ORDER_INFORMATION_ALTERED, {
						order,
					});
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
		[
			field,
			isOpen,
			onOpenChange,
			orderId,
			parseRequest,
			parseResponse,
			setCurrentValue,
			setValue,
		]
	);

	const [handleSubmit, setHandleSubmit] = useState(() => async (event) => {
		event.preventDefault();

		await submitOrder(inputValue);
	});

	useEffect(() => {
		setHandleSubmit(() => async (event) => {
			event.preventDefault();

			await submitOrder(inputValue);
		});
	}, [inputValue, setHandleSubmit, submitOrder]);

	return (
		<>
			<div className={`${namespace}info-box my-3`} id={elementId}>
				<div className="align-items-center d-flex">
					{label ? (
						<div className="h5 info-box-label m-0">{label}</div>
					) : null}

					{hasUpdatePermission &&
					!readOnly &&
					isEditable(field, isOpen) &&
					hasTerms() ? (
						<ClayButton
							aria-expanded={isOpen}
							aria-haspopup="dialog"
							aria-label={
								value
									? sub(Liferay.Language.get('edit-x'), label)
									: sub(Liferay.Language.get('add-x'), label)
							}
							className="ml-2"
							data-qa-id={`${label}-infoBoxButton`}
							displayType={buttonDisplayType}
							onClick={() => {
								setInputValue(currentValue);

								onOpenChange(true);
							}}
							size="xs"
						>
							{additionalProps?.termDescription && !isOpen
								? Liferay.Language.get('read[verb]')
								: value
									? Liferay.Language.get('edit')
									: Liferay.Language.get('add')}
						</ClayButton>
					) : null}
				</div>

				<div className="info-box-value mt-1">
					{value ? (
						<span data-qa-id="infoBoxValue">
							{formatValue(value, fieldValueType)}
						</span>
					) : (
						<ClayButton
							aria-label={Liferay.Language.get('not-set')}
							className="border-bottom border-dashed btn-sm p-0 small text-decoration-none text-secondary"
							displayType="link"
							onClick={() =>
								hasUpdatePermission &&
								!readOnly &&
								isEditable(field, isOpen) &&
								onOpenChange(true)
							}
						>
							{Liferay.Language.get('not-set')}
						</ClayButton>
					)}
				</div>
			</div>

			{hasUpdatePermission &&
			!readOnly &&
			isEditable(field, isOpen) &&
			hasTerms() ? (
				<InfoBoxModal
					additionalProps={additionalProps}
					field={field}
					fieldValueType={fieldValueType}
					handleSubmit={handleSubmit}
					id={`${namespace}infoBoxModal`}
					inputValue={inputValue}
					isOpen={isOpen}
					label={label}
					observer={observer}
					onOpenChange={onOpenChange}
					open={open}
					orderId={orderId}
					setHandleSubmit={setHandleSubmit}
					setInputValue={setInputValue}
					setParseRequest={setParseRequest}
					setParseResponse={setParseResponse}
					spritemap={spritemap}
					submitOrder={submitOrder}
				/>
			) : null}
		</>
	);
};

export default DefaultView;
