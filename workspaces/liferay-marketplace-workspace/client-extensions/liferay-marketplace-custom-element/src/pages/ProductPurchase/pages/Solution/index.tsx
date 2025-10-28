/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {useEffect} from 'react';
import {useNavigate, useOutletContext} from 'react-router-dom';

import {SolutionTypes} from '../../../../enums/Product';
import {ProductPurchaseOutletContext} from '../../ProductPurchaseOutlet';
import AnalyticsProvisioning from './AnalyticsProvisioningForm';
import PreBuiltTrialProvisioning from './PreBuiltTrialProvisioningForm';

import './index.scss';

const SolutionProvisioningForm = () => {
	const {accounts, selectedAccount, solutionTypeSpecificationValue} =
		useOutletContext<ProductPurchaseOutletContext>();

	const navigate = useNavigate();

	useEffect(() => {
		if (accounts.length > 1 && !selectedAccount) {
			navigate('/', {replace: true});
		}
	}, [selectedAccount, accounts.length, navigate]);

	if (solutionTypeSpecificationValue === SolutionTypes.ANALYTICS) {
		return <AnalyticsProvisioning />;
	}

	if (solutionTypeSpecificationValue === SolutionTypes.PRE_BUILT_TRIAL) {
		return <PreBuiltTrialProvisioning />;
	}

	return null;
};

export default SolutionProvisioningForm;
