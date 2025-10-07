/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import {ComponentProps, ReactElement} from 'react';

import i18n from '../../../i18n';
import {Liferay} from '../../../liferay/liferay';
import {useProductPurchaseOutletContext} from '../../../pages/ProductPurchase/ProductPurchaseOutlet';
import {getSiteURL} from '../../../utils/site';

type ProductPurchaseFooterProps = {
	backButtonProps?: ComponentProps<typeof ClayButton>;
	cancelButtonProps?: ComponentProps<typeof ClayButton>;
	continueButtonProps?: ComponentProps<typeof ClayButton>;
	termsAndConditions?: ReactElement;
};

const ProductPurchaseFooter: React.FC<ProductPurchaseFooterProps> = ({
	backButtonProps,
	cancelButtonProps,
	continueButtonProps,
	termsAndConditions,
}) => {
	const {productPurchaseCart} = useProductPurchaseOutletContext();

	return (
		<div className="d-flex flex-column mt-6 w-100">
			<div className="align-items-center d-flex justify-content-between mt-6 w-100">
				<ClayButton
					className="font-weight-bold"
					displayType="unstyled"
					onClick={() => {
						if (productPurchaseCart.cart.id) {
							productPurchaseCart.removeCart(
								productPurchaseCart.cart.id
							);
						}
						Liferay.Util.navigate(getSiteURL() || '/');
					}}
					{...cancelButtonProps}
				>
					{cancelButtonProps?.children || i18n.translate('cancel')}
				</ClayButton>

				<div>
					<ClayButton displayType="secondary" {...backButtonProps}>
						{backButtonProps?.children || i18n.translate('back')}
					</ClayButton>

					<ClayButton className="ml-4" {...continueButtonProps}>
						{continueButtonProps?.children ||
							i18n.translate('continue')}
					</ClayButton>
				</div>
			</div>

			{termsAndConditions && (
				<div className="d-flex justify-content-end pt-3 text-black-50 w-100">
					{termsAndConditions}
				</div>
			)}
		</div>
	);
};

export default ProductPurchaseFooter;
