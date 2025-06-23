/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {useSearchParams} from 'react-router-dom';

import Page from '../../../../components/Page';
import InfoCard from '../../components/InfoCard';
import useAppsMetrics from '../../hooks/useAppsMetrics';
import {percentage} from '../../util';
import AdministratorAppsListView from './AdministratorAppsListView';

export default function Apps() {
	const [searchParams] = useSearchParams();
	const {
		approved = 0,
		approvedBeforeLastWeek = 0,
		approvedLastWeek = 0,
		inReview = 0,
		inReviewBeforeLastWeek = 0,
		inReviewLastWeek = 0,
		products = 0,
	} = useAppsMetrics('week');

	return (
		<>
			<div className="d-flex flex-wrap mb-3">
				<InfoCard
					className="mr-3"
					expanded
					growth={percentage(
						products,
						inReviewLastWeek - inReviewBeforeLastWeek
					)}
					growthContext={`+${inReviewLastWeek - inReviewBeforeLastWeek} this week`}
					symbol="squares-clock"
					title="App Awaiting Review"
					value={inReview}
				/>

				<InfoCard
					expanded
					growth={percentage(
						products,
						approvedLastWeek - approvedBeforeLastWeek
					)}
					growthContext={`+${approvedLastWeek - approvedBeforeLastWeek} this week`}
					symbol="squares"
					title="Recently Published"
					value={approved}
				/>
			</div>

			<Page
				pageRendererProps={{className: 'border py-2 rounded-lg'}}
				title="Apps"
			>
				<AdministratorAppsListView
					filter={searchParams.get('filter') as string}
					isSortable
					managementToolbarProps={{
						searchVisible: true,
						visible: true,
					}}
				/>
			</Page>
		</>
	);
}
