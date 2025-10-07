/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ClayButtonWithIcon} from '@clayui/button';
import Panel from '@clayui/panel';
import React from 'react';

import {Section} from '../../../../../components/Section/Section';
import {
	LicensePrice,
	LicensingPrices,
	NewAppTypes,
	useNewAppContext,
} from '../../../../../context/NewAppContext';
import {
	ProductLicenseTier,
	ProductTypeLicenseOptions,
} from '../../../../../enums/Product';
import i18n from '../../../../../i18n';
import {currenciesCode} from '../../../../../utils/currencies';
import {CurrencyFlag} from '../pages/Licensing/components/CurrencyModal';
import IconButton from './IconButton';
import LicensePriceCard from './LicensePriceCard';

const licensePrices = [
	{
		description:
			'Standard licenses cover the following DXP environments: production, non-production (UAT) and backup (DR) for both standalone and virtual cluster servers.',
		label: 'Standard License Prices',
		required: true,
		type: ProductLicenseTier.STANDARD,
	},
	{
		description:
			'Developer licenses are limited to 5 unique addresses and should not be used for full scale production deployments.',
		label: i18n.translate('developer-license-prices'),
		required: false,
		type: ProductLicenseTier.DEVELOPER,
	},
];

type LicensePricePanelProps = {
	currencyCode: string;
	tierPrices: LicensingPrices;
};

const LicensePricePanel: React.FC<LicensePricePanelProps> = ({
	currencyCode,
	tierPrices,
}) => {
	const [
		{
			build: {appType},
		},
		dispatch,
	] = useNewAppContext();

	const currentCurrency =
		currenciesCode.find(({code}) => code === currencyCode) ||
		currenciesCode[0];

	const handleAddPriceTier = (
		currency: string,
		licenseTier: ProductLicenseTier
	) =>
		dispatch({
			payload: {
				currency,
				licenseTier,
			},
			type: NewAppTypes.SET_LICENSING_ADD_PRICE,
		});

	const handleEditPriceTier = (
		currency: string,
		index: number,
		licenseTier: ProductLicenseTier,
		price: LicensePrice
	) =>
		dispatch({
			payload: {
				currency,
				index,
				licenseTier,
				price: price.value,
				quantity: price.key,
			},
			type: NewAppTypes.SET_LICENSING_UPDATE_PRICES,
		});

	const handleDeletePriceTier = (
		currency: string,
		licenseTier: ProductLicenseTier,
		key: number
	) =>
		dispatch({
			payload: {
				currency,
				key,
				licenseTier,
			},
			type: NewAppTypes.SET_LICENSING_DELETE_PRICE,
		});

	return (
		<Panel
			collapsable
			defaultExpanded
			displayTitle={
				<div className="align-items-center d-flex justify-content-between w-100">
					<div className="align-items-center d-flex">
						<span className="mr-2">{currencyCode}</span>

						<CurrencyFlag {...currentCurrency} />
					</div>

					{currencyCode !== 'USD' && (
						<ClayButtonWithIcon
							aria-label={`Delete all prices for ${currencyCode}`}
							className="h-auto ml-auto"
							displayType="unstyled"
							onClick={() =>
								dispatch({
									payload: {currency: currencyCode},
									type: NewAppTypes.SET_LICENSING_DELETE_CURRENCY,
								})
							}
							symbol="trash"
							title="Delete all prices"
						/>
					)}
				</div>
			}
			displayType="secondary"
			showCollapseIcon={true}
		>
			<Panel.Body>
				{licensePrices.map(
					(
						{description, label, required, type: licenseType},
						index
					) => {
						const showSection =
							ProductTypeLicenseOptions[appType]?.includes(
								licenseType
							);

						if (!showSection) {
							return null;
						}

						const licensePrices = tierPrices[licenseType] || {};

						return (
							<Section
								className="mb-6"
								key={index}
								label={label}
								required={required}
								tooltip={description}
								tooltipText="More Info"
							>
								{Object.keys(licensePrices).length ? (
									<LicensePriceCard
										currency={currencyCode}
										licensePrices={licensePrices}
										licenseTier={licenseType}
										onAdd={(currency) =>
											handleAddPriceTier(
												currency,
												licenseType
											)
										}
										onChange={(index, price, currency) =>
											handleEditPriceTier(
												currency,
												index,
												licenseType,
												price as LicensePrice
											)
										}
										onDelete={(key, currency) =>
											handleDeletePriceTier(
												currency,
												licenseType,
												key
											)
										}
									/>
								) : (
									<IconButton
										className="license-icon-button py-3 w-100"
										displayType={null}
										onClick={() =>
											handleAddPriceTier(
												currencyCode,
												licenseType
											)
										}
									>
										<span className="text-capitalize">{`Add ${licenseType} Licenses`}</span>
									</IconButton>
								)}
							</Section>
						);
					}
				)}
			</Panel.Body>
		</Panel>
	);
};

export default LicensePricePanel;
