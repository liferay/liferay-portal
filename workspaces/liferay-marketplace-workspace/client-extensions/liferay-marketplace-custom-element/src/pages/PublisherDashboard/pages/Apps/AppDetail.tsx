/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Section} from '../../../../components/Section/Section';
import {useNewAppContext} from '../../../../context/NewAppContext';
import i18n from '../../../../i18n';
import AppReview from '../../components/AppReview/AppReview';

const AppDetail = () => {
	const [context] = useNewAppContext();

	return (
		<div className="app-review-container">
			<Section
				disabled
				label={i18n.translate('app-submission')}
				tooltip={i18n.translate('more-info')}
				tooltipText={i18n.translate('more-info')}
			>
				<hr />
			</Section>

			<div className="border p-5 rounded-lg">
				<AppReview.Description context={context} />
				<AppReview.Categories context={context} />
				<AppReview.Build context={context} />
				<AppReview.Pricing context={context} />
				<AppReview.Licensing context={context} />
				<AppReview.Storefront context={context} />
				<AppReview.Support context={context} isLastSection />
			</div>
		</div>
	);
};

export default AppDetail;
