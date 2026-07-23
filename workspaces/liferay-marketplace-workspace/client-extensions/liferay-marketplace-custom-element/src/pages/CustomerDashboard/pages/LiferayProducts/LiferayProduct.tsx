/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {useEffect} from 'react';
import {useOutletContext} from 'react-router-dom';

import {breadcrumbStore} from '../../../../components/Breadcrumb/BreadcrumbStore';
import {OrderTypes} from '../../../../enums/Order';
import AIHubDetails from './Details/AIHubDetails';
import ActivationKeysDetails from './Details/ActivationKeysDetails';
import AnalyticsDetails from './Details/AnalyticsDetails';

const LiferayProduct = () => {
	const {placedOrder, product} = useOutletContext<any>();

	useEffect(() => {
		breadcrumbStore.send({
			replacements: {[placedOrder?.id]: product?.name || ''},
			type: 'setReplacements',
		});
	}, [placedOrder?.id, product?.name]);

	const orderTypeExternalReferenceCode =
		placedOrder?.orderTypeExternalReferenceCode;

	if (orderTypeExternalReferenceCode === OrderTypes.AI_HUB) {
		return <AIHubDetails />;
	}

	if (
		[
			OrderTypes.CMP,
			OrderTypes.CMP_BETA,
			OrderTypes.DSR,
			OrderTypes.DXP,
		].includes(orderTypeExternalReferenceCode)
	) {
		return <ActivationKeysDetails />;
	}

	return <AnalyticsDetails />;
};

export default LiferayProduct;
