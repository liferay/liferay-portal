/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import {useNavigate} from 'react-router-dom';

import {Header} from '../../../../../components/Header/Header';
import i18n from '../../../../../i18n';
import {Liferay} from '../../../../../liferay/liferay';
import {getSiteURL} from '../../../../../utils/site';

const ContactSalesPage = () => {
	const navigate = useNavigate();

	return (
		<>
			<Header
				description={i18n.translate(
					'the-selected-project-does-not-meet-the-necessary-resource-requirements-for-this-app-please-contact-sales-to-request-additional-resources'
				)}
				title={i18n.translate('insufficient-resource-requirements')}
			/>

			<hr />

			<div className="d-flex justify-content-end">
				<ClayButton
					className="mr-4"
					displayType="secondary"
					onClick={() => Liferay.Util.navigate(getSiteURL())}
					outline
				>
					{i18n.translate('go-to-marketplace')}
				</ClayButton>

				<ClayButton onClick={() => navigate('form')}>
					{i18n.translate('contact-sales')}
				</ClayButton>
			</div>
		</>
	);
};

export default ContactSalesPage;
