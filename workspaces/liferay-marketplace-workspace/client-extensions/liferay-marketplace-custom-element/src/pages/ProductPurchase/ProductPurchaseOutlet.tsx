/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayIcon from '@clayui/icon';
import {useSelector} from '@xstate/store/react';
import classNames from 'classnames';
import {useEffect, useMemo, useState} from 'react';
import {
	Outlet,
	useLocation,
	useNavigate,
	useOutletContext,
} from 'react-router-dom';

import Loading from '../../components/Loading';
import ProductPurchase from '../../components/ProductPurchase';
import {MarketplaceDeliveryProduct} from '../../entity/MarketplaceDeliveryProduct';
import {SolutionTypes} from '../../enums/Product';
import useProductPurchaseCart from '../../hooks/useProductPurchaseCart';
import i18n from '../../i18n';
import {Liferay} from '../../liferay/liferay';
import marketplaceOAuth2 from '../../services/oauth/Marketplace';
import {scrollToMiddleOfPage} from '../../utils/browser';
import ProductPurchasePrice from './ProductPurchasePrice';
import {productTypeRoutes} from './ProductPurchaseRouter';
import useAccounts from './hooks/useAccounts';
import ProductPurchaseService from './services/ProductPurchase';
import ProductPurchaseApp from './services/ProductPurchaseApp';
import {productPurchaseStore} from './store/AppPurchaseStore';

type ProductPurchaseOutletProps = {
	product: DeliveryProduct;
	productTypeRoute: (typeof productTypeRoutes)['App'] &
		(typeof productTypeRoutes)['Solution'];
	solutionTypeSpecificationValue: SolutionTypes;
};

function sendRedirect(link: string) {
	window.location.href = link;
}

export type ProductPurchaseOutletContext = {
	actions: {
		nextStep: () => void;
		previousStep: () => void;
	};
	handlePurchase: (
		ProductPurchase: ProductPurchaseService | typeof ProductPurchaseService,
		cart?: Cart | undefined,
		cartOptions?: any
	) => Promise<void>;
	isSingleAccount: boolean;
	marketplaceDeliveryProduct: MarketplaceDeliveryProduct;
	product: DeliveryProduct;
	productPurchaseCart: ReturnType<typeof useProductPurchaseCart>;
	productTypeRoute: ProductPurchaseOutletProps['productTypeRoute'];
	solutionTypeSpecificationValue: SolutionTypes;
} & Omit<ReturnType<typeof useAccounts>, 'myUserAccount'>;

const ProductPurchaseOutlet: React.FC<ProductPurchaseOutletProps> = ({
	product,
	productTypeRoute,
	solutionTypeSpecificationValue,
}) => {
	const [isSubmitting, setSubmitting] = useState(false);
	const {accounts, selectedAccount, setSelectedAccount} = useAccounts();

	const {pathname} = useLocation();
	const navigate = useNavigate();

	const productPurchaseCart = useProductPurchaseCart(
		selectedAccount?.id,
		product,

		// Currently only the App Purchase uses the cart hook

		ProductPurchaseApp.getOrderTypeExternalReferenceCode(product)
	);

	const licenseType = useSelector(
		productPurchaseStore,
		(state) => state.context.licenseType
	);

	const marketplaceDeliveryProduct = useMemo(() => {
		return new MarketplaceDeliveryProduct(product);
	}, [product]);

	const {metadata, routes = []} = productTypeRoute || {};

	const steps = routes.map((route) => {
		const key = route.index ? '/' : `/${route.path}`;

		return {
			...route,
			active: pathname === key,
			key,
		};
	});

	const activeStepIndex = steps.findIndex(({active}) => active);

	const stepNavigate = (stepNumber: number) => {
		const step = steps[activeStepIndex + stepNumber];

		if (step) {
			navigate(step.path || '');

			scrollToMiddleOfPage();
		}
	};

	const handlePurchase = async (
		ProductPurchase: ProductPurchaseService | typeof ProductPurchaseService,
		cart: Cart,
		cartOptions: unknown
	) => {
		setSubmitting(true);

		try {
			const _productPurchase =
				ProductPurchase instanceof ProductPurchaseService
					? ProductPurchase
					: new ProductPurchase(selectedAccount, product);

			const order = await _productPurchase.createOrder(cart, cartOptions);

			const link = await _productPurchase.getNextStepsLink(order);

			if (licenseType === 'PAID') {
				await marketplaceOAuth2.taxCalculate(cart?.id);
			}

			if (link.startsWith('http')) {
				return sendRedirect(link);
			}

			navigate(link);
		}
		catch (error) {
			console.error(error);

			Liferay.Util.openToast({
				message: i18n.translate('an-unexpected-error-occurred'),
				type: 'danger',
			});
		}

		setSubmitting(false);
	};

	const isTinyDisplay = metadata?.tinyStepsDisplay;
	const isSingleAccount = accounts.length === 1;

	const context = {
		accounts,
		actions: {
			nextStep: () => stepNavigate(1),
			previousStep: () => stepNavigate(-1),
		},
		handlePurchase,
		isSingleAccount,
		marketplaceDeliveryProduct,
		product,
		productPurchaseCart,
		routes: steps,
		selectedAccount,
		setSelectedAccount,
		solutionTypeSpecificationValue,
	};

	useEffect(() => {
		if (selectedAccount?.taxId) {
			productPurchaseStore.send({
				taxId: selectedAccount.taxId,
				type: 'setAccountTaxId',
			});
		}
	}, [selectedAccount?.taxId]);

	return (
		<ProductPurchase className="my-7">
			{isSubmitting && (
				<Loading.FullScreen>
					Hang tight, <b>{product.name}</b> purchase is processed by{' '}
					<b>Marketplace</b>.
				</Loading.FullScreen>
			)}

			<ProductPurchase.Header
				product={product}
				rightNode={
					metadata.useCart ? (
						<ProductPurchasePrice
							product={product}
							productPurchaseCart={productPurchaseCart}
						/>
					) : null
				}
			>
				{marketplaceDeliveryProduct.isPerpetualLicense && (
					<div className="mt-2 text-black-50">
						<ClayIcon
							className="mr-1"
							color="#2E5AAC"
							symbol="exclamation-full"
						/>{' '}
						<small>
							A perpetual license never expires. Support is not
							included.
						</small>
					</div>
				)}

				<ProductPurchase.HeaderAccount account={selectedAccount} />
			</ProductPurchase.Header>

			{!isTinyDisplay && (
				<ProductPurchase.Steps
					className="mt-5 px-8"
					onClickIndicator={(step) => navigate(step.key)}
					steps={steps}
				/>
			)}

			<ProductPurchase.Body
				className={classNames('mt-7', {'mt-7': accounts.length === 1})}
			>
				{isTinyDisplay && (
					<ProductPurchase.CircleSteps
						className="my-5 px-8"
						steps={steps}
					/>
				)}

				<Outlet context={context} />
			</ProductPurchase.Body>
		</ProductPurchase>
	);
};

const useProductPurchaseOutletContext = () => {
	return useOutletContext<ProductPurchaseOutletContext>();
};

export {useProductPurchaseOutletContext};

export default ProductPurchaseOutlet;
