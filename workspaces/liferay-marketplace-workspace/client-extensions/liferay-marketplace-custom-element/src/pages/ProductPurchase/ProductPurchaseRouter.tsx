/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {HashRouter, Route, Routes} from 'react-router-dom';

import {useMarketplaceContext} from '../../context/MarketplaceContext';
import {
	ProductCategories,
	ProductSpecificationKey,
	ProductTypeVocabulary,
	SolutionTypes,
} from '../../enums/Product';
import withProviders from '../../hoc/withProviders';
import {useDeliveryProduct} from '../../hooks/data/useProduct';
import i18n from '../../i18n';
import {
	getProductCategoriesByVocabularyName,
	getSpecificationByKey,
} from '../../utils/productUtils';
import ProductPurchaseOutlet from './ProductPurchaseOutlet';
import ProductPurchaseAccountSelection from './steps/AccountSelection';
import SolutionProvisioningForm from './steps/Solution';
import ThankYou from './steps/ThankYou';

const productTypeRoutes = {
	[ProductTypeVocabulary.SOLUTION]: [
		{
			element: ProductPurchaseAccountSelection,
			index: true,
			title: i18n.translate('account-selection'),
		},
		{
			element: SolutionProvisioningForm,
			path: 'form',
			title: 'Form',
		},
	],
	[ProductTypeVocabulary.APP]: [
		{element: ProductPurchaseAccountSelection, index: true, title: 'Setup'},
	],
};

const ProductPurchaseRouter = () => {
	const {
		properties: {productId: pageProductId},
	} = useMarketplaceContext();

	// The productId that comes from the property can be used to hide the productId
	// search param is some places

	const productId =
		pageProductId ||
		(new URLSearchParams(window.location.search).get(
			'productId'
		) as unknown as string);

	const {data: product, isLoading} = useDeliveryProduct(productId);

	if (isLoading) {
		return null;
	}

	const productTypes = getProductCategoriesByVocabularyName(
		product?.categories || [],
		ProductCategories.MARKETPLACE_PRODUCT_TYPE
	);

	const productTypeCategory = productTypes[0] as ProductTypeVocabulary;

	const solutionTypeSpecification = getSpecificationByKey(
		ProductSpecificationKey.SOLUTION_TYPE,
		product as DeliveryProduct
	);

	const solutionTypeSpecificationValue =
		solutionTypeSpecification?.value as SolutionTypes;

	const routes = productTypeRoutes[productTypeCategory] || [];

	return (
		<HashRouter>
			<Routes>
				<Route
					element={
						<ProductPurchaseOutlet
							product={product as DeliveryProduct}
							routes={routes}
							solutionTypeSpecificationValue={
								solutionTypeSpecificationValue
							}
						/>
					}
				>
					{routes.map((route, index) => {
						const Element = route.element;

						return (
							<Route
								{...route}
								element={<Element />}
								key={index}
							/>
						);
					})}
				</Route>

				<Route
					element={
						<ThankYou
							product={product as DeliveryProduct}
							productTypeCategory={productTypeCategory}
							solutionTypeSpecificationValue={
								solutionTypeSpecificationValue
							}
						/>
					}
					path="thank-you"
				/>
			</Routes>
		</HashRouter>
	);
};
export default withProviders(ProductPurchaseRouter);
