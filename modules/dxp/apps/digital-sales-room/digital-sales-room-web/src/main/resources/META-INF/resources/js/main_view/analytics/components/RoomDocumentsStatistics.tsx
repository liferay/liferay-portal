/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {FrontendDataSet} from '@liferay/frontend-data-set-web';
import React, {useEffect, useState} from 'react';

import {TRoomDocumentsStatistics} from '../../../common/utils/types';
import {DocumentTitleDataRenderer} from './data_renderers/DocumentTitleDataRenderer';
import {LastViewedDataRenderer} from './data_renderers/LastViewedDataRenderer';

import '../../../../css/components/RoomDocumentsStatistics.scss';
import useAnalyticsQuery from '../../../common/hooks/useAnalyticsQuery';
import AnalyticsFrame from './AnalyticsFrame';
import Loader from './Loader';

const RoomDocumentsStatistics = ({
	isAnalyticsEnabled,
	namespace,
}: {
	isAnalyticsEnabled: boolean;
	namespace: string;
}) => {
	const [data, setData] = useState<TRoomDocumentsStatistics[]>([]);
	const [element, setElement] = useState<HTMLElement | null>(null);

	const {isLoading, response} = useAnalyticsQuery({
		element,
		query: {paths: [{key: 'documents', path: '/documents-metric'}]},
		settings: {isAnalyticsEnabled},
		variables: {
			keywords: '',
			rangeEnd: null,
			rangeKey: 7,
			rangeStart: null,
			size: 20,
			sortColumn: 'downloadsMetric',
			sortType: 'DESC',
			start: 0,
		},
	});

	useEffect(() => {
		if (response) {
			const documentMetrics = response.documents?.documentMetrics ?? [];

			setData(
				documentMetrics.map((documentMetric: any) => {
					const url: string = documentMetric.urls?.[0] ?? '';
					const extension = url.includes('.')
						? url.split('.').pop() ?? ''
						: '';

					const lastViewedValue =
						documentMetric.lastViewedMetric?.value;

					return {
						download: documentMetric.downloadsMetric?.value ?? 0,
						lastViewed: lastViewedValue
							? new Date(lastViewedValue).toISOString()
							: '',
						title: documentMetric.assetTitle ?? '',
						totalViews:
							documentMetric.impressionMadeMetric?.value ?? 0,
						type: extension || 'document',
						userInvolved:
							documentMetric.usersInvolvedMetric?.value ?? 0,
					};
				})
			);
		}

		return () => {};
	}, [response, setData]);

	return (
		<AnalyticsFrame
			icon="documents-and-media"
			title={Liferay.Language.get('most-engaged-documents')}
		>
			<div
				className="room-documents-statistics-container"
				ref={setElement}
			>
				{isAnalyticsEnabled ? (
					isLoading ? (
						<Loader />
					) : !data?.length ? (
						<p className="mt-3 text-center text-muted">
							{Liferay.Language.get('no-data-available')}
						</p>
					) : (
						<div className="room-document-statistics-fds">
							<FrontendDataSet
								customDataRenderers={{
									documentNameDataRenderer:
										DocumentTitleDataRenderer,
									lastViewedDataRenderer:
										LastViewedDataRenderer,
								}}
								id={namespace}
								items={data}
								showManagementBar={false}
								showPagination={false}
								showSearch={false}
								showSelectAll={false}
								views={[
									{
										contentRenderer: 'table',
										label: Liferay.Language.get('table'),
										name: 'table',
										schema: {
											fields: [
												{
													contentRenderer:
														'documentNameDataRenderer',
													fieldName: 'title',
													label: Liferay.Language.get(
														'title'
													),
												},
												{
													fieldName: 'totalViews',
													label: Liferay.Language.get(
														'total-views'
													),
												},
												{
													contentRenderer:
														'lastViewedDataRenderer',
													fieldName: 'lastViewed',
													label: Liferay.Language.get(
														'last-viewed'
													),
												},
												{
													fieldName: 'download',
													label: Liferay.Language.get(
														'download'
													),
												},
												{
													fieldName: 'userInvolved',
													label: Liferay.Language.get(
														'user-involved'
													),
												},
											],
										},
										thumbnail: 'table',
									},
								]}
							/>
						</div>
					)
				) : (
					<div className="dsr-analytics-empty-message">
						<p className="mb-0 text-center text-muted">
							{Liferay.Language.get(
								'analytics-cloud-is-not-configured'
							)}
						</p>
					</div>
				)}
			</div>
		</AnalyticsFrame>
	);
};

export default RoomDocumentsStatistics;
