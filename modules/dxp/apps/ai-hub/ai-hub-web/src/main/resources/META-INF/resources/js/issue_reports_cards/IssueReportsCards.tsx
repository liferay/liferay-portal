/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React from 'react';

import Card from './components/Card';

interface IssueReportsCardsProps {
	criticalIssuesCount: number;
	dislikeRatingPercent: number;
	positiveRatingPercent: number;
}

export default function IssueReportsCards({
	criticalIssuesCount,
	dislikeRatingPercent,
	positiveRatingPercent,
}: IssueReportsCardsProps) {
	return (
		<section className="container-fluid issue-reports-user-activity mb-4">
			<h2 className="h4 mb-3">{Liferay.Language.get('user-activity')}</h2>

			<div className="row">
				<div className="col-12 col-md-4 mb-3">
					<Card
						label={Liferay.Language.get('positive-rating')}
						symbol="thumbs-up"
						value={`${positiveRatingPercent}%`}
					/>
				</div>

				<div className="col-12 col-md-4 mb-3">
					<Card
						label={Liferay.Language.get('dislike-rating')}
						symbol="thumbs-down"
						value={`${dislikeRatingPercent}%`}
					/>
				</div>

				<div className="col-12 col-md-4 mb-3">
					<Card
						label={Liferay.Language.get('critical-issues')}
						symbol="exclamation-full"
						value={String(criticalIssuesCount)}
					/>
				</div>
			</div>
		</section>
	);
}
