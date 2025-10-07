/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {useSelector} from '@xstate/store/react';

import {Input} from '../../../../../components/Input/Input';
import {Section} from '../../../../../components/Section/Section';
import i18n from '../../../../../i18n';
import {useProductPurchaseOutletContext} from '../../../ProductPurchaseOutlet';
import {productPurchaseStore} from '../../../store/AppPurchaseStore';

const TaxIdDisplay = () => {
	const {selectedAccount} = useProductPurchaseOutletContext();

	const contextTaxId = useSelector(
		productPurchaseStore,
		({context}) => context.payment.taxId
	);

	return (
		<Section label={i18n.translate('vat-id')}>
			<Input
				defaultValue={selectedAccount.taxId}
				disabled={!!selectedAccount?.taxId}
				onChange={({target: {value}}) => {
					productPurchaseStore.send({
						taxId: value,
						type: 'setAccountTaxId',
					});
				}}
				required
				value={contextTaxId}
			/>
		</Section>
	);
};

export default TaxIdDisplay;
