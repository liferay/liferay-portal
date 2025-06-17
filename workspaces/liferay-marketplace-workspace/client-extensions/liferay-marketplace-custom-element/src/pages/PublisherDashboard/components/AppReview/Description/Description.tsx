/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import DOMPurify from 'dompurify';

import i18n from '../../../../../i18n';
import {AppReviewProps} from '../AppReview';
import AppReviewSection from '../AppReviewSection';

const Description = ({
	context,
	editNavigate,
	required = false,
}: AppReviewProps) => {
	return (
		<AppReviewSection
			editNavigate={editNavigate}
			required={required}
			title={i18n.translate('description')}
		>
			<div className="app-review-section-body-description">
				<p
					className="app-review-section-body-description-paragraph"
					dangerouslySetInnerHTML={{
						__html: DOMPurify.sanitize(context.profile.description),
					}}
				/>
			</div>
		</AppReviewSection>
	);
};

export default Description;
