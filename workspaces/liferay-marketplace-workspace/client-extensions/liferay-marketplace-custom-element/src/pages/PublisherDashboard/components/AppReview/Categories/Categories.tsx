/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Tag} from '../../../../../components/Tag/Tag';
import i18n from '../../../../../i18n';
import {AppReviewProps} from '../AppReview';
import AppReviewSection from '../AppReviewSection';

const Categories = ({
	context,
	editNavigate,
	required = false,
}: AppReviewProps) => {
	return (
		<>
			<AppReviewSection title={i18n.translate('category')}>
				<div className="app-review-section-body">
					{context.profile.categories.label && (
						<Tag label={context.profile.categories.label} />
					)}
				</div>
			</AppReviewSection>

			<AppReviewSection
				editNavigate={editNavigate}
				required={required}
				title={i18n.translate('areas')}
			>
				<div className="app-review-section-body-tags">
					{context.profile.areas.map((area, index) => (
						<Tag key={index} label={area.label} />
					))}
				</div>
			</AppReviewSection>

			<AppReviewSection
				editNavigate={editNavigate}
				required={required}
				title={i18n.translate('tags')}
			>
				<div className="app-review-section-body-tags">
					{context.profile.tags.map((tag, index) => (
						<Tag key={index} label={tag.label} />
					))}
				</div>
			</AppReviewSection>
		</>
	);
};

export default Categories;
