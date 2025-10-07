/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {useSelector} from '@xstate/store/react';
import classNames from 'classnames';
import {useMemo} from 'react';

import ProductPurchase from '../../components/ProductPurchase';
import {MarketplaceDeliveryProduct} from '../../entity/MarketplaceDeliveryProduct';
import {ProductPriceModel} from '../../enums/Product';
import useProductPurchaseCart from '../../hooks/useProductPurchaseCart';
import i18n from '../../i18n';
import {cartStore} from './store';

type ProductPurchasePriceProps = {
	product: DeliveryProduct;
	productPurchaseCart: ReturnType<typeof useProductPurchaseCart>;
};

const ProductPurchasePrice: React.FC<ProductPurchasePriceProps> = ({
	product,
}) => {
	const cart = useSelector(cartStore, ({context}) => context.cart);

	const marketplaceDeliveryProduct = useMemo(() => {
		return new MarketplaceDeliveryProduct(product);
	}, [product]);

	const getFormattedPrice = () => {
		const productPrice =
			cart?.summary?.totalFormatted ||
			marketplaceDeliveryProduct.getPrice();

		const vatText =
			marketplaceDeliveryProduct.getPriceModel() ===
			ProductPriceModel.PAID
				? `(${i18n.translate('excluding-vat')})`
				: '';

		return `${productPrice} ${vatText}`;
	};

	return (
		<ProductPurchase.Price
			className={classNames('mr-1 pr--2 py-2 text-nowrap')}
			price={getFormattedPrice()}
		>
			<div className="license-tag px-2">
				{marketplaceDeliveryProduct.getLicenseTagText()}
			</div>
		</ProductPurchase.Price>
	);
};

export default ProductPurchasePrice;
