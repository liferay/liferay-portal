/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {useNavigate} from 'react-router-dom';

import i18n from '../../../../i18n';
import {getProductPriceModel} from '../../../../utils/productUtils';
import {useProductPurchaseOutletContext} from '../../ProductPurchaseOutlet';
import ProductPurchaseAccountSelection from '../AccountSelection';

const AccountSelection = () => {
	const {
		actions: {nextStep},
		product,
		selectedAccount,
	} = useProductPurchaseOutletContext();

	const navigate = useNavigate();

	const {isFreeApp} = getProductPriceModel(product);

	return (
		<ProductPurchaseAccountSelection
			footerProps={{
				continueButtonProps: {
					children: i18n.translate('continue'),
					disabled: !selectedAccount,
					onClick: () => {
						if (isFreeApp) {
							return navigate('summary', {replace: true});
						}

						nextStep();
					},
				},
			}}
		/>
	);
};

export default AccountSelection;
