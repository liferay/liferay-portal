/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {useState} from 'react';

import {Header} from '../../../../../../components/Header/Header';
import {NewAppPageFooterButtons} from '../../../../../../components/NewAppPageFooterButtons/NewAppPageFooterButtons';
import {Section} from '../../../../../../components/Section/Section';
import {ProductLicenseTier} from '../../../../../../enums/Product';
import i18n from '../../../../../../i18n';
import {
	getPriceListByCatalogName,
	getPriceListIdPriceEntries,
	getProductById,
	getProductIdSkusPage,
	patchPriceEntry,
	patchSKUById,
	postPriceEntryIdTierPrice,
} from '../../../../../../utils/api';
import {isTrialSKU} from '../../../../../../utils/productUtils';
import {getSkuPrice} from '../../../../../../utils/util';
import {
	LicensePrice,
	PriceEntry,
	useAppContext,
} from '../AppContext/AppManageState';
import {ActionTypes} from '../AppContext/actionTypes';
import IconButton from './components/IconButton/IconButton';
import LicensePriceCard from './components/LicensePriceCard';

import './InformLicensingTermsPage.scss';
interface InformLicensingTermsPricePageProps {
	onClickBack: () => void;
	onClickContinue: () => void;
}

export function InformLicensingTermsPricePage({
	onClickBack,
	onClickContinue,
}: InformLicensingTermsPricePageProps) {
	const [{appLicensePrice, appProductId, appType}, dispatch] =
		useAppContext();
	const [isProcessing, setProcessing] = useState(false);

	const isDXP = appType.value === 'dxp';

	const handleAddPriceTier = (licenseTier: ProductLicenseTier) =>
		dispatch({
			payload: {licenseTier},
			type: ActionTypes.ADD_APP_LICENSE_PRICE,
		});

	const handleDeletePriceTier = (
		licenseTier: ProductLicenseTier,
		key: number
	) =>
		dispatch({
			payload: {
				key,
				licenseTier,
			},
			type: ActionTypes.DELETE_APP_LICENSE_PRICE,
		});

	const handleEditPriceTier = (
		licenseTier: ProductLicenseTier,
		index: number,
		price: LicensePrice
	) =>
		dispatch({
			payload: {
				index,
				licenseTier,
				price,
			},
			type: ActionTypes.UPDATE_APP_LICENSE_PRICES,
		});

	const processTier = async (priceEntry: PriceEntry) => {
		const skuName = priceEntry?.sku?.name;

		const keyAppLicensePrice = skuName.endsWith('d')
			? 'developer'
			: 'standard';

		const tiers = appLicensePrice[keyAppLicensePrice];

		for (const {key, value} of tiers || []) {
			const tierPrice = {
				minimumQuantity: key,
				price: value,
				priceEntryId: priceEntry?.priceEntryId,
			};

			await postPriceEntryIdTierPrice(
				priceEntry?.priceEntryId,
				tierPrice
			);
		}
	};

	const handlePostPriceEntryIdTierPrice = async (sku: SKU) => {
		const product = await getProductById({
			nestedFields: 'catalog',
			productId: appProductId,
		});

		const priceList = await getPriceListByCatalogName(
			product?.catalog?.name
		);

		const priceEntries = await getPriceListIdPriceEntries(
			priceList?.items[0]?.id
		);

		for (const priceEntry of priceEntries?.items || []) {
			if (priceEntry?.sku?.id !== sku?.id) {
				continue;
			}

			if (!priceEntry?.bulkPricing || priceEntry?.hasTierPrice) {
				await patchPriceEntry(
					{
						bulkPricing: true,
						hasTierPrice: false,
					},
					priceEntry?.priceEntryId
				);
			}

			await processTier(priceEntry);
		}
	};

	const submitLicensePrice = async () => {
		const skusJSON = await getProductIdSkusPage(appProductId);

		for (const sku of skusJSON?.items || []) {
			if (!isTrialSKU(sku)) {
				await handlePostPriceEntryIdTierPrice(sku);
				await patchSKUById(sku?.id, {
					...sku,
					price: getSkuPrice(appLicensePrice, sku),
				});
			}
		}
	};

	return (
		<div className="informing-licensing-terms-page-container">
			<Header
				description={i18n.translate(
					'define-the-licensing-approach-for-your-app-this-will-impact-the-license-renewal-experience'
				)}
				title={i18n.translate('select-licensing-terms')}
			/>

			<Section
				className="mb-6"
				label={i18n.translate('standard-license-prices')}
				required
				tooltip={i18n.translate(
					'standard-licenses-cover-the-following-dxp-environments-production-non-production-uat-and-backup-dr-for-both-standalone-and-virtual-cluster-servers'
				)}
				tooltipText={i18n.translate('more-info')}
			>
				<LicensePriceCard
					licensePrices={appLicensePrice.standard}
					onAdd={() =>
						handleAddPriceTier(ProductLicenseTier.STANDARD)
					}
					onChange={(index: number, price: LicensePrice) => {
						handleEditPriceTier(
							ProductLicenseTier.STANDARD,
							index,
							price
						);
					}}
					onDelete={(key: number) =>
						handleDeletePriceTier(ProductLicenseTier.STANDARD, key)
					}
				/>
			</Section>

			{isDXP && (
				<Section
					label={i18n.translate('developer-license-prices')}
					tooltip={i18n.translate(
						'developer-licenses-are-limited-to-5-unique-addresses-and-should-not-be-used-for-full-scale-production-deployments'
					)}
					tooltipText={i18n.translate('more-info')}
				>
					{appLicensePrice.developer.length ? (
						<LicensePriceCard
							licensePrices={appLicensePrice.developer}
							onAdd={() =>
								handleAddPriceTier(ProductLicenseTier.DEVELOPER)
							}
							onChange={(index: number, price: LicensePrice) =>
								handleEditPriceTier(
									ProductLicenseTier.DEVELOPER,
									index,
									price
								)
							}
							onDelete={(key: number) =>
								handleDeletePriceTier(
									ProductLicenseTier.DEVELOPER,
									key
								)
							}
						/>
					) : (
						<IconButton
							className="icon-button py-3 w-100"
							onClick={() =>
								handleAddPriceTier(ProductLicenseTier.DEVELOPER)
							}
						>
							{i18n.translate('add-developer-licenses')}
						</IconButton>
					)}
				</Section>
			)}

			<NewAppPageFooterButtons
				disableContinueButton={isProcessing || !appLicensePrice}
				isLoading={isProcessing}
				onClickBack={() => onClickBack()}
				onClickContinue={async () => {
					setProcessing(true);

					await submitLicensePrice();

					setProcessing(false);

					onClickContinue();
				}}
				showBackButton
			/>
		</div>
	);
}
