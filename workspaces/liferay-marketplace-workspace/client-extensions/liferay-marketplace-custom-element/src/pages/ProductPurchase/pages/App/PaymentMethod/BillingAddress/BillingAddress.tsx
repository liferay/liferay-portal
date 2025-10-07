/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {useSelector} from '@xstate/store/react';
import React, {useState} from 'react';
import {KeyedMutator} from 'swr';

import {RadioCard} from '../../../../../../components/RadioCard/RadioCard';
import {Section} from '../../../../../../components/Section/Section';
import useCommerceRegions from '../../../../../../hooks/useCommerceRegions';
import {Liferay} from '../../../../../../liferay/liferay';
import HeadlessAdminUser from '../../../../../../services/rest/HeadlessAdminUser';
import {Region} from '../../../../../../services/rest/HeadlessCommerceAdminAddress';
import {useProductPurchaseOutletContext} from '../../../../ProductPurchaseOutlet';
import {productPurchaseStore} from '../../../../store';
import BillingAddressForm from './BillinAddressForm';
import getPostalAddressDescription from './getPostalAddressDescription';

type BillingAddressProps = {
	addresses: BillingAddress[];
	mutateUserAccoutAddress: KeyedMutator<{items: BillingAddress[]}>;
	setBillingAddress: React.Dispatch<BillingAddress>;
};

const BillingAddress: React.FC<BillingAddressProps> = ({
	addresses,
	mutateUserAccoutAddress,
	setBillingAddress,
}) => {
	const {billingAddress} = useSelector(
		productPurchaseStore,
		(state) => state.context.payment
	);

	const billingAdressName = billingAddress.name || '';

	const {selectedAccount} = useProductPurchaseOutletContext();
	const [showNewAddressButton, setShowNewAddressButton] = useState(true);
	const {data: regionsResponse} = useCommerceRegions();
	const [selectedAddress, setSelectedAddress] =
		useState<string>(billingAdressName);

	const regions = regionsResponse?.items ?? [];

	const onSelectAddress = async ({
		address,
		title,
	}: {
		address: BillingAddress;
		title: string | undefined;
	}) => {
		setSelectedAddress(address.name as string);

		const postalAddress = addresses.find(
			(address) => address.name === title
		);

		const billingAddress = {
			city: postalAddress?.city,
			country: postalAddress?.countryISOCode,
			countryISOCode: postalAddress?.countryISOCode || 'US',
			name: postalAddress?.name,
			phoneNumber: postalAddress?.phoneNumber,
			regionISOCode: postalAddress?.regionISOCode,
			street1: postalAddress?.street1,
			street2: postalAddress?.street2,
			zip: postalAddress?.zip,
		};

		setBillingAddress(billingAddress);

		if (!showNewAddressButton) {
			setShowNewAddressButton(true);
		}
	};

	const saveAddress = async (billingAddress: BillingAddress) => {
		const getCountryNameByCode = (
			regions: Region[],
			countryCode?: string
		) => {
			const country = regions.find((region) => region.a2 === countryCode);

			return (
				country?.title_i18n[Liferay.ThemeDisplay.getLanguageId()] ||
				country?.title_i18n[
					Liferay.ThemeDisplay.getDefaultLanguageId()
				] ||
				country?.name
			);
		};

		const getRegionByCountryCode = (
			regions: Region[],
			regionISOCode?: string,
			countryCode?: string
		) => {
			const country = regions.find((region) => region.a2 === countryCode);
			const addressRegion = country?.regions.find(
				(region) => region.regionCode === regionISOCode
			);

			return addressRegion?.name;
		};

		const postAddress = await HeadlessAdminUser.postAddress(
			selectedAccount.id,
			{
				addressCountry: getCountryNameByCode(
					regions,
					billingAddress?.country
				),
				addressLocality: billingAddress.city,
				addressRegion: getRegionByCountryCode(
					regions,
					billingAddress.regionISOCode,
					billingAddress?.country
				),
				addressType: 'billing-and-shipping',
				name: billingAddress.name,
				phoneNumber: billingAddress.phoneNumber,
				postalCode: billingAddress.zip,
				primary: false,
				streetAddressLine1: billingAddress.street1,
				streetAddressLine2: billingAddress.street2,
			}
		);

		await mutateUserAccoutAddress((addressCache) => ({
			...addressCache,
			items: [...addresses, postAddress],
		}));

		setSelectedAddress(billingAddress.name as string);

		setBillingAddress(billingAddress);

		if (!showNewAddressButton) {
			setShowNewAddressButton(true);
		}
	};

	return (
		<Section
			className="billing-address-section"
			label="Billing Address"
			required
		>
			{addresses?.map((address, index) => {
				const {description, title} =
					getPostalAddressDescription(address);

				return (
					<RadioCard
						className="mb-4"
						description={description}
						key={index}
						onChange={() => onSelectAddress({address, title})}
						selected={selectedAddress === address.name}
						title={title}
					/>
				);
			})}

			<BillingAddressForm
				saveAddress={saveAddress}
				setBillingAddress={setBillingAddress}
				setSelectedAddress={setSelectedAddress}
				setShowNewAddressButton={setShowNewAddressButton}
				showNewAddressButton={showNewAddressButton}
			/>
		</Section>
	);
};

export default BillingAddress;
