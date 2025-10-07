/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {useEffect} from 'react';
import {useNavigate} from 'react-router-dom';

import i18n from '../../../../i18n';
import {getProductPriceModel} from '../../../../utils/productUtils';
import {useProductPurchaseOutletContext} from '../../ProductPurchaseOutlet';
import ProductPurchaseAccountSelection from '../AccountSelection';

const AccountSelection = () => {
	const {
		accounts,
		actions: {nextStep},
		isSingleAccount,
		product,
		selectedAccount,
		setSelectedAccount,
	} = useProductPurchaseOutletContext();

	const navigate = useNavigate();

	useEffect(() => {
		const {isFreeApp} = getProductPriceModel(product);

		if (isFreeApp && isSingleAccount) {
			if (isSingleAccount && !selectedAccount) {
				setSelectedAccount(accounts[0]);
			}

			return navigate('summary', {replace: true});
		}

		if (isSingleAccount) {
			if (isSingleAccount && !selectedAccount) {
				setSelectedAccount(accounts[0]);
			}

			navigate('license', {replace: true});
		}
	}, [
		accounts,
		isSingleAccount,
		selectedAccount,
		product,
		navigate,
		setSelectedAccount,
	]);

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
