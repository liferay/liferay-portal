/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {useNavigate} from 'react-router-dom';

import {Section} from '../../../../../components/Section/Section';
import {useNewAppContext} from '../../../../../context/NewAppContext';
import i18n from '../../../../../i18n';
import AppReview from '../../../components/AppReview/AppReview';

const SubmitNewBuild = () => {
	const [context] = useNewAppContext();
	const navigate = useNavigate();

	return (
		<Section
			disabled
			label={i18n.translate('app-submission')}
			required
			tooltip={i18n.translate('more-info')}
			tooltipText={i18n.translate('more-info')}
		>
			<hr />

			<div className="border p-5 rounded-lg">
				<AppReview.Profile context={context} />

				<hr />

				<AppReview.Build
					context={context}
					editNavigate={() => navigate('../')}
					isLastSection
					required
				/>
			</div>
		</Section>
	);
};

export default SubmitNewBuild;
