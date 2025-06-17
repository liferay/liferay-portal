/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import i18n from '../../../../../i18n';
import {AppReviewProps} from '../AppReview';
import AppReviewSection from '../AppReviewSection';
import SupportList from './SupportList';

const Support = ({
	context,
	editNavigate,
	isLastSection,
	required = false,
}: AppReviewProps) => {
	return (
		<AppReviewSection
			editNavigate={editNavigate}
			isLastSection={isLastSection}
			required={required}
			title={i18n.translate('support-and-help')}
		>
			<SupportList context={context} />
		</AppReviewSection>
	);
};

export default Support;
