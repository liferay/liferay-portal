/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import Autocomplete from '@clayui/autocomplete';
import ClayButton from '@clayui/button';
import ClayForm from '@clayui/form';
import ClayIcon from '@clayui/icon';
import ClayModal, {useModal} from '@clayui/modal';
import classnames from 'classnames';

// @ts-ignore

import {CommerceConstants, CommerceServiceProvider} from 'commerce-frontend-js';
import {openToast} from 'frontend-js-components-web';
import React, {useEffect, useMemo, useState} from 'react';

interface IAddressSubtypeAutocompleteInputProps {
	onChange(value: string): void;
	disabled: boolean;
	errorMessage?: string;
	externalReferenceCode: string;
	hasError?: boolean;
	initialValue?: string;
	namespace: string;
}

interface IAddressSubtypeAutocompleteProps {
	addressSubtypeConfiguration: {
		billing: string;
		billingAndShipping: string;
		shipping: string;
	};
	initialAddressId: string;
	initialAddressType: string;
	initialValue: string;
	namespace: string;
}

interface IListTypeEntry {
	id: number;
	key: string;
	name: string;
}

function AddressSubtypeAutocompleteInput({
	disabled = false,
	errorMessage = '',
	externalReferenceCode = '',
	hasError = false,
	initialValue = '',
	namespace,
	onChange,
}: IAddressSubtypeAutocompleteInputProps) {
	const [currentValue, setCurrentValue] = useState(initialValue);
	const [subtypes, setSubtypes] = useState<Array<IListTypeEntry>>([]);

	useEffect(() => {
		if (!externalReferenceCode) {
			setSubtypes([]);

			return;
		}

		CommerceServiceProvider.AdminListTypeAPI('v1')
			.getListTypeEntries(externalReferenceCode, {
				pageSize: -1,
			})
			.then((data: {items: IListTypeEntry[]}) => {
				setSubtypes(data.items);
			})
			.catch((error: any) => {
				setSubtypes([]);

				openToast({
					message:
						error.detail ||
						error.errorDescription ||
						Liferay.Language.get(
							'an-unexpected-system-error-occurred'
						),
					type: 'danger',
				});
			});
	}, [externalReferenceCode]);

	useEffect(() => {
		setCurrentValue(initialValue);
	}, [initialValue]);

	const filteredSubtypeItems = useMemo(
		() =>
			subtypes.filter(
				(item) =>
					item.name.match(new RegExp(currentValue, 'i')) !== null
			),
		[currentValue, subtypes]
	);

	return (
		<ClayForm.Group
			className={classnames({
				'has-error': hasError,
			})}
		>
			<label
				className="control-label"
				htmlFor="addressSubtypeAutocomplete"
			>
				{Liferay.Language.get('subtype')}
			</label>

			<input
				name={`${namespace}subtype`}
				type="hidden"
				value={currentValue}
			/>

			<Autocomplete
				aria-label={Liferay.Language.get('subtype')}
				className="mb-3"
				defaultValue={initialValue || ''}
				disabled={!externalReferenceCode || disabled}
				filterKey="name"
				id="addressSubtypeAutocomplete"
				items={filteredSubtypeItems}
				menuTrigger="focus"
				name={`${namespace}addressSubtype`}
				onChange={(value: string) => {
					setCurrentValue(value);

					onChange(value);
				}}
				onItemsChange={() => {}}
				placeholder={Liferay.Language.get('subtype')}
				value={
					subtypes.find((item) => item.key === currentValue)?.name ||
					currentValue
				}
			>
				{(item) => (
					<Autocomplete.Item key={item.key} textValue={item.key}>
						<div>{item.name}</div>
					</Autocomplete.Item>
				)}
			</Autocomplete>

			{hasError ? (
				<ClayForm.FeedbackItem>{errorMessage}</ClayForm.FeedbackItem>
			) : null}
		</ClayForm.Group>
	);
}

function AddressSubtypeAutocomplete({
	addressSubtypeConfiguration = {
		billing: '',
		billingAndShipping: '',
		shipping: '',
	},
	initialAddressId = '0',
	initialAddressType,
	initialValue = '',
	namespace,
}: IAddressSubtypeAutocompleteProps) {
	const {
		observer,
		onOpenChange: onErrorModalOpenChange,
		open: openErrorModal,
	} = useModal({
		onClose: () => {
			const useAsBillingCheckbox = document.getElementById(
				`${namespace}use-as-billing`
			) as HTMLInputElement;

			if (useAsBillingCheckbox) {
				useAsBillingCheckbox.checked = false;
			}
		},
	});

	const [addressId, setAddressId] = useState(Number(initialAddressId));
	const [addressType, setAddressType] = useState(initialAddressType);
	const [modalCurrentValue, setModalCurrentValue] = useState('');
	const [currentValue, setCurrentValue] = useState(initialValue);
	const [hasError, setHasError] = useState(false);
	const [externalReferenceCode, setExternalReferenceCode] =
		useState<string>();

	useEffect(() => {
		if (
			[
				String(CommerceConstants.ADDRESS_TYPE_BILLING),
				'billing',
			].includes(String(addressType || initialAddressType))
		) {
			setExternalReferenceCode(addressSubtypeConfiguration.billing);

			return;
		}
		else if (
			[
				String(CommerceConstants.ADDRESS_TYPE_SHIPPING),
				'shipping',
			].includes(String(addressType || initialAddressType))
		) {
			setExternalReferenceCode(addressSubtypeConfiguration.shipping);

			return;
		}

		setExternalReferenceCode(
			addressSubtypeConfiguration.billingAndShipping
		);
	}, [
		addressSubtypeConfiguration.billing,
		addressSubtypeConfiguration.billingAndShipping,
		addressSubtypeConfiguration.shipping,
		addressType,
		initialAddressType,
	]);

	useEffect(() => {
		const commerceAddressSelect = document.getElementById(
			`${namespace}commerceAddress`
		) as HTMLSelectElement;
		const useAsBillingCheckbox = document.getElementById(
			`${namespace}use-as-billing`
		) as HTMLInputElement;

		const onCommerceAddressChange = (event: Event) => {
			setAddressId((event.target as any).value);

			const option = Array.from(
				(event.target as any).children as HTMLOptionsCollection
			).filter((item) => item.value === (event.target as any).value)[0];

			setAddressType(option.dataset.type as string);
			setCurrentValue(option.dataset.subtype || '');
			setHasError(false);
		};

		const onUseAsBillingCheckboxChange = (event: Event) => {
			if (!Number(addressId)) {
				setExternalReferenceCode(
					(event.target as HTMLInputElement).checked
						? addressSubtypeConfiguration.billingAndShipping
						: addressSubtypeConfiguration.shipping
				);

				if (currentValue) {
					setCurrentValue('');
					setHasError(true);
				}

				return;
			}

			if (
				Number(addressId) &&
				[
					String(CommerceConstants.ADDRESS_TYPE_SHIPPING),
					'shipping',
				].includes(String(addressType)) &&
				currentValue &&
				(event.target as HTMLInputElement).checked
			) {
				onErrorModalOpenChange(true);

				return;
			}
		};

		if (commerceAddressSelect) {
			commerceAddressSelect.addEventListener(
				'change',
				onCommerceAddressChange
			);
		}

		if (useAsBillingCheckbox) {
			useAsBillingCheckbox.addEventListener(
				'change',
				onUseAsBillingCheckboxChange
			);
		}

		return () => {
			if (commerceAddressSelect) {
				commerceAddressSelect.removeEventListener(
					'change',
					onCommerceAddressChange
				);
			}
			if (useAsBillingCheckbox) {
				useAsBillingCheckbox.removeEventListener(
					'change',
					onUseAsBillingCheckboxChange
				);
			}
		};
	}, [
		addressId,
		addressType,
		addressSubtypeConfiguration.billingAndShipping,
		addressSubtypeConfiguration.shipping,
		currentValue,
		initialAddressType,
		namespace,
		onErrorModalOpenChange,
	]);

	return addressSubtypeConfiguration.billing ||
		addressSubtypeConfiguration.billingAndShipping ||
		addressSubtypeConfiguration.shipping ? (
		<>
			<AddressSubtypeAutocompleteInput
				disabled={!externalReferenceCode || !!Number(addressId)}
				errorMessage={Liferay.Language.get(
					'your-previous-selection-is-not-valid-anymore'
				)}
				externalReferenceCode={externalReferenceCode || ''}
				hasError={hasError}
				initialValue={currentValue}
				namespace={namespace}
				onChange={(value) => {
					setCurrentValue(value);
					setHasError(false);
				}}
			/>

			{openErrorModal && (
				<ClayModal
					id={`${namespace}_modalChangeSubtype`}
					observer={observer}
				>
					<ClayModal.Header
						className="alert-warning"
						closeButtonAriaLabel={Liferay.Language.get('close')}
					>
						<ClayIcon className="mr-2" symbol="warning-full" />

						{Liferay.Language.get('reset-subtype')}
					</ClayModal.Header>

					<ClayModal.Body>
						<p>
							{Liferay.Language.get(
								'due-to-your-recent-selection,-the-previously-chosen-subtype-is-no-longer-valid'
							)}
						</p>

						<AddressSubtypeAutocompleteInput
							disabled={
								!addressSubtypeConfiguration.billingAndShipping
							}
							externalReferenceCode={
								addressSubtypeConfiguration.billingAndShipping ||
								''
							}
							namespace={namespace}
							onChange={(value) => {
								setModalCurrentValue(value);
							}}
						/>
					</ClayModal.Body>

					<ClayModal.Footer
						last={
							<ClayButton.Group spaced>
								<ClayButton
									aria-label={Liferay.Language.get('cancel')}
									displayType="secondary"
									onClick={() => {
										onErrorModalOpenChange(false);
									}}
								>
									{Liferay.Language.get('cancel')}
								</ClayButton>

								<ClayButton
									aria-label={Liferay.Language.get('save')}
									displayType="warning"
									onClick={() => {
										const continueButton =
											document.getElementById(
												`${namespace}continue`
											) as HTMLButtonElement;

										if (continueButton) {
											setCurrentValue(modalCurrentValue);

											setTimeout(() => {
												continueButton.click();
											}, 0);
										}
									}}
								>
									{Liferay.Language.get('save')}
								</ClayButton>
							</ClayButton.Group>
						}
					/>
				</ClayModal>
			)}
		</>
	) : null;
}

export default AddressSubtypeAutocomplete;
