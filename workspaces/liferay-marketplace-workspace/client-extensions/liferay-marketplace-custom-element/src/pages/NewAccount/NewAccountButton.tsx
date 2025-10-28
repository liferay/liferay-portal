/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import Button from '@clayui/button';
import {useModal} from '@clayui/modal';

import withProviders from '../../hoc/withProviders';
import i18n from '../../i18n';
import CreateAccountModalForm from '../ProductPurchase/pages/CreateAccountModalForm';

import './NewAccountButton.scss';

const NewAccountButton = () => {
	const modal = useModal();

	return (
		<div className="align-items-center d-flex p-2 text-center w-100">
			<Button
				className="align-items-center d-flex justify-content-center new-account-button w-100"
				displayType="secondary"
				onClick={() => modal.onOpenChange(true)}
			>
				{i18n.translate('new-account')}
			</Button>

			<CreateAccountModalForm modal={modal} />
		</div>
	);
};

export default withProviders(NewAccountButton);
