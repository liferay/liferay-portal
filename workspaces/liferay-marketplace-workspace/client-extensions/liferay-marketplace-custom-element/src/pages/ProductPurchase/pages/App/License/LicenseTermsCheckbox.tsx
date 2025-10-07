/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ClayCheckbox} from '@clayui/form';
import {useSelector} from '@xstate/store/react';

import {useMarketplaceContext} from '../../../../../context/MarketplaceContext';
import {ProductSpecificationKey} from '../../../../../enums/Product';
import {Liferay} from '../../../../../liferay/liferay';
import {useProductPurchaseOutletContext} from '../../../ProductPurchaseOutlet';
import {productPurchaseStore} from '../../../store/AppPurchaseStore';

const LicenseTermsCheckbox = () => {
	const {properties} = useMarketplaceContext();

	const eulaAgreement = useSelector(
		productPurchaseStore,
		(state) => state.context.payment.eulaAgreement
	);

	const {
		product: {productSpecifications},
	} = useProductPurchaseOutletContext();

	const appUsageTerms = productSpecifications?.find(
		(specification) =>
			specification?.specificationKey ===
			ProductSpecificationKey.APP_SUPPORT_USAGE_TERMS_URL
	);

	const formattedProtocolUrl = appUsageTerms?.value?.startsWith('https://')
		? appUsageTerms?.value
		: 'https://' + appUsageTerms?.value;

	return (
		<div className="align-items-start d-flex eula-container mt-4">
			<ClayCheckbox
				checked={eulaAgreement}
				className="mr-2"
				onChange={() =>
					productPurchaseStore.send({type: 'toggleEulaAgreement'})
				}
			/>
			<span className="ml-1">
				I have read and agree to the
				<a
					href={
						appUsageTerms?.value
							? formattedProtocolUrl
							: Liferay.ThemeDisplay.getLayoutURL().replace(
									'/get-app',
									`/license-agreement`
								)
					}
					rel="noopener noreferrer"
					target="_blank"
				>
					&nbsp;End User License Agreement&nbsp;
				</a>
				and the
				<a
					href={properties.eulaBaseURL}
					rel="noopener noreferrer"
					target="_blank"
				>
					&nbsp;Terms&nbsp;
				</a>
				of Service.
			</span>
		</div>
	);
};

export default LicenseTermsCheckbox;
