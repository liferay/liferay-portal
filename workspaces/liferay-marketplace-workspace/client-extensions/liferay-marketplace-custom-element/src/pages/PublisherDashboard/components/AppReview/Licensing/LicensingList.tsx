/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayIcon from '@clayui/icon';

import {
	LicensingPrices,
	NewAppInitialState,
} from '../../../../../context/NewAppContext';
import {ProductPriceModel} from '../../../../../enums/Product';
import {currenciesCode, formatCurrency} from '../../../../../utils/currencies';
import {LICENSING_OPTIONS} from '../../../pages/NewAppFlow/constants';

type LicensePricesProps = {
	currency: any;
	currencyCode: string;
	prices: LicensingPrices;
};

const LicensePrices: React.FC<LicensePricesProps> = ({
	currency,
	currencyCode,
	prices,
}) => (
	<div>
		<span>
			{currencyCode}

			{currency.iconSrc ? (
				<img
					className="currency-selector-icon ml-2"
					src={currency.iconSrc}
				/>
			) : (
				<ClayIcon
					className="currency-selector-icon ml-2"
					symbol={currency.flag}
				/>
			)}
		</span>

		<div className="d-flex justify-content-between">
			{Object.entries(prices[currencyCode]).map(
				([priceType, values], index) => (
					<div key={index}>
						<h5 className="licesing-price-type pt-2">
							{priceType} License price
						</h5>

						{Object.entries(values).map(([unit, price], index) => (
							<div className="licensing-unit-price" key={index}>
								Quantity: <b>{unit}</b> - Unit Price:{' '}
								<b>
									{formatCurrency(
										Number(price),
										currencyCode
									)}{' '}
								</b>
							</div>
						))}
					</div>
				)
			)}
		</div>

		<hr />
	</div>
);

const LicensingList = ({context}: {context: NewAppInitialState}) => {
	const {
		licensing: {licenseType, prices},
		pricing: {priceModel},
	} = context;

	const licenseOption = LICENSING_OPTIONS.find(
		(licenseOption) => licenseOption.value === licenseType
	);

	return (
		<>
			<div className="border p-4 rounded-lg">
				{licenseOption && (
					<>
						<div className="align-items-center d-flex">
							<span className="font-weight-bold mr-2">
								{licenseOption?.title}
							</span>

							<ClayIcon
								color="#377CFF"
								symbol={licenseOption.icon}
							/>
						</div>

						<small className="secondary-text">
							{licenseOption?.description}
						</small>
					</>
				)}
			</div>

			{priceModel === ProductPriceModel.PAID && (
				<div className="border mt-4 p-4 rounded-lg">
					<div>
						{Object.keys(prices).map((key) => (
							<LicensePrices
								currency={currenciesCode.find(
									({code}) => code === key
								)}
								currencyCode={key}
								key={key}
								prices={prices}
							/>
						))}
					</div>
				</div>
			)}
		</>
	);
};

export default LicensingList;
