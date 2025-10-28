/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ComponentProps} from 'react';

import i18n from '../../../i18n';
import AccountEmailInfo from '../../../pages/CustomerDashboard/pages/Apps/App/Licenses/CreateLicense/AccountInfo';
import {ProductCardRevamp as ProductCard} from '../../../pages/GetApp/components/ProductCard/ProductCard';
import {normalizeURLProtocol} from '../../../utils/string';

type ProductPurchaseHeaderAccountProps = {
	account: Account;
};

type ProductPurchaseHeaderProps = {
	product: DeliveryProduct;
} & Partial<ComponentProps<typeof ProductCard>>;

const ProductPurchaseHeaderAccount: React.FC<
	ProductPurchaseHeaderAccountProps
> = ({account}) => {
	if (!account) {
		return null;
	}

	return (
		<>
			<hr />

			<div className="d-flex flex-row justify-content-between">
				<strong className="account-banner-title-text align-self-center">
					{i18n.translate('account-selected')}
				</strong>

				<AccountEmailInfo image={account.logoURL} name={account.name} />
			</div>
		</>
	);
};

const ProductPurchaseHeader: React.FC<ProductPurchaseHeaderProps> = ({
	product,
	...props
}) => (
	<ProductCard
		icon={normalizeURLProtocol(product.urlImage)}
		subtitle={product.catalogName}
		title={product.name}
		{...props}
	/>
);

export {ProductPurchaseHeaderAccount};

export default ProductPurchaseHeader;
