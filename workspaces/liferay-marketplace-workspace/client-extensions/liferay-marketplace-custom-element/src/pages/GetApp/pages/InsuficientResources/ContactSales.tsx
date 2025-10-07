/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {useNavigate} from 'react-router-dom';

import {Header} from '../../../../components/Header/Header';
import {NewAppPageFooterButtons} from '../../../../components/NewAppPageFooterButtons/NewAppPageFooterButtons';
import i18n from '../../../../i18n';
import {Liferay} from '../../../../liferay/liferay';

const ContactSalesPage = () => {
	const navigate = useNavigate();

	return (
		<div>
			<Header
				description={i18n.translate(
					'the-selected-project-does-not-meet-the-necessary-resource-requirements-for-this-app-please-contact-sales-to-request-additional-resources'
				)}
				title={i18n.translate('insufficient-resource-requirements')}
			/>

			<NewAppPageFooterButtons
				backButtonText={i18n.translate('go-to-marketplace')}
				continueButtonText={i18n.translate('contact-sales')}
				onClickBack={() =>
					Liferay.Util.navigate(
						Liferay.ThemeDisplay.getLayoutURL().replace(
							'/get-app',
							''
						)
					)
				}
				onClickContinue={() => {
					navigate('form');
				}}
				showBackButton
				showContinueButton
			/>

			<div className="d-flex justify-content-end">
				<a className="font-weight-semi-bold" href="#">
					<ins>
						{i18n.translate(
							'learn-more-about-app-resource-requirements'
						)}
					</ins>
				</a>
			</div>
		</div>
	);
};

export default ContactSalesPage;
