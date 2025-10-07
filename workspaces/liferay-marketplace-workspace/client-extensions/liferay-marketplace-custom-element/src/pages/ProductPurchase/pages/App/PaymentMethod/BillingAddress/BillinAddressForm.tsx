/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import ClayIcon from '@clayui/icon';
import React from 'react';

import Select from '../../../../../../components/Select/Select';
import useCommerceRegions from '../../../../../../hooks/useCommerceRegions';
import i18n from '../../../../../../i18n';
import {Liferay} from '../../../../../../liferay/liferay';

import './BillingAddress.scss';

import {zodResolver} from '@hookform/resolvers/zod';
import {useForm} from 'react-hook-form';

import FormInput from '../../../../../../components/Input/formInput';
import zodSchema from '../../../../../../schema/zod';

type BillingAddressProps = {
	saveAddress: (address: BillingAddress) => void;
	setBillingAddress: React.Dispatch<BillingAddress>;
	setSelectedAddress: React.Dispatch<string>;
	setShowNewAddressButton: React.Dispatch<boolean>;
	showNewAddressButton: boolean;
};

const defaultBillingAddress = {
	city: '',
	country: '',
	countryISOCode: '',
	name: '',
	phoneNumber: '',
	regionISOCode: '',
	street1: '',
	street2: '',
	zip: '',
};

const BillingAddressForm: React.FC<BillingAddressProps> = ({
	saveAddress,
	setBillingAddress,
	setSelectedAddress,
	setShowNewAddressButton,
	showNewAddressButton,
}) => {
	const {
		formState: {errors, isValid},
		handleSubmit,
		register,
		reset,
		setValue,
		watch,
	} = useForm({
		defaultValues: {
			city: '',
			country: '',
			countryISOCode: '',
			name: '',
			phoneNumber: '',
			regionISOCode: '',
			street1: '',
			street2: '',
			zip: '',
		},
		mode: 'onChange',
		resolver: zodResolver(zodSchema.billingAddress),
	});

	const inputProps = {
		errors,
		register,
		required: true,
	};

	const {country, regionISOCode} = watch();

	const {data: regionsResponse} = useCommerceRegions();

	const regions = regionsResponse?.items || [];

	const states =
		regions.find((region) => region.a2 === country)?.regions ?? [];

	const onSubmit = (form: any) => {
		saveAddress(form);
		reset();
	};

	if (showNewAddressButton) {
		return (
			<button
				className="align-items-center billing-address-section-card-new-address d-flex justify-content-center mt-4 rounded w-100"
				onClick={() => {
					setShowNewAddressButton(false);

					setBillingAddress({
						...defaultBillingAddress,
						countryISOCode: regions[0].a2,
					});
				}}
			>
				<ClayIcon symbol="plus" />

				<span>{i18n.translate('new-address')}</span>
			</button>
		);
	}

	return (
		<div className="billing-address-section-card-container h-auto mt-4 rounded">
			<div className="align-items-center billing-address-section-card-header d-flex justify-content-between">
				<small className="font-weight-bold">
					{i18n.translate('new-address')}
				</small>

				<ClayButton
					onClick={() => {
						setShowNewAddressButton(true);
						setSelectedAddress('');

						setBillingAddress(defaultBillingAddress);
					}}
				>
					{i18n.translate('cancel')}
				</ClayButton>
			</div>

			<div className="billing-address-section-container d-flex flex-column p-4 w-100">
				<FormInput
					{...inputProps}
					label="Full Name"
					name="name"
					required
				/>

				<FormInput
					{...inputProps}
					label="Address"
					name="street1"
					placeholder="Address 1"
					required
				/>

				<FormInput
					{...inputProps}
					name="street2"
					placeholder="Address 2"
				/>

				<Select
					boldLabel
					className="custom-input"
					label="Country"
					name="country"
					onChange={({target: {value}}) => {
						const states =
							regions.find((region) => region.a2 === value)
								?.regions ?? [];

						const regionISOCode =
							!!states.length && states[0].regionCode;

						setValue('country', value);
						setValue('countryISOCode', value);
						setValue('regionISOCode', regionISOCode as string);
					}}
					options={regions.map((region) => ({
						key: region.a2,
						name:
							region.title_i18n[
								Liferay.ThemeDisplay.getLanguageId()
							] ||
							region.title_i18n[
								Liferay.ThemeDisplay.getDefaultLanguageId()
							] ||
							region.name,
					}))}
					required
					value={country}
				/>

				<Select
					boldLabel
					className="custom-input"
					defaultOption={false}
					disabled={!states.length}
					label="State"
					name="regionISOCode"
					onChange={({target: {value}}) =>
						setValue('regionISOCode', value)
					}
					options={states.map((state) => ({
						key: state.regionCode,
						name: state.name,
					}))}
					required={!!states.length}
					value={regionISOCode}
				/>

				<FormInput {...inputProps} label="City" name="city" required />

				<FormInput
					{...inputProps}
					label="Zip/Area Code"
					name="zip"
					required
				/>

				<FormInput
					{...inputProps}
					label="Phone"
					name="phoneNumber"
					required
				/>

				<div className="d-flex justify-content-end">
					<ClayButton
						disabled={errors && !isValid}
						onClick={handleSubmit(onSubmit)}
					>
						{i18n.translate('save')}
					</ClayButton>
				</div>
			</div>
		</div>
	);
};

export default BillingAddressForm;
