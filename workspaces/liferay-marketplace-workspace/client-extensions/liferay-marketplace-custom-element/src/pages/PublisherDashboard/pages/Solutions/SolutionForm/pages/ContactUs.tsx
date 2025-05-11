/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import Form from '../../../../../../components/MarketplaceForm';
import {
	SolutionTypes,
	useSolutionContext,
} from '../../../../../../context/SolutionContext';
import i18n from '../../../../../../i18n';

const ContactUs = () => {
	const [{contactUs}, dispatch] = useSolutionContext();

	return (
		<div>
			<h3>{i18n.translate('contact-us')}</h3>

			<hr />

			<Form.Label
				className="mt-3"
				htmlFor="email"
				info={i18n.translate('email')}
				required
			>
				{i18n.translate('email')}
			</Form.Label>

			<Form.Input
				name="email"
				onChange={(event) =>
					dispatch({
						payload: event.target.value,
						type: SolutionTypes.SET_CONTACT_US,
					})
				}
				placeholder="name@yourdomain.com"
				type="name@yourdomain.com"
				value={contactUs}
			/>
		</div>
	);
};

export default ContactUs;
