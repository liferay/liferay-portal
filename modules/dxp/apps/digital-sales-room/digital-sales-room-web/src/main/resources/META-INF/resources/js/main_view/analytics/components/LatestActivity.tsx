/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {FrontendDataSet} from '@liferay/frontend-data-set-web';
import React, {useEffect, useState} from 'react';

import AccountSticker from '../../../common/components/AccountSticker';

import './../../../../css/components/LatestActivity.scss';
import useAnalyticsQuery from '../../../common/hooks/useAnalyticsQuery';
import {TLatestActivity} from '../../../common/utils/types';
import {BASE_URL} from '../utils/constants';
import AnalyticsFrame from './AnalyticsFrame';
import Loader from './Loader';
import {TimeDataRenderer} from './data_renderers/TimeDataRenderer';

const LatestActivity = ({
	isAnalyticsEnabled,
	namespace,
}: {
	isAnalyticsEnabled: boolean;
	namespace: string;
}) => {
	const [data, setData] = useState<TLatestActivity[]>([]);
	const [element, setElement] = useState<HTMLElement | null>(null);

	const {isLoading, response} = useAnalyticsQuery({
		element,
		query: {paths: [{key: 'events', path: '/events'}]},
		settings: {isAnalyticsEnabled},
		variables: {
			includeAnonymousUsers: false,
			page: 0,
			rangeKey: 7,
			size: 10,
		},
	});

	useEffect(() => {
		if (response) {
			const eventEntries = response.events?.eventEntries ?? [];

			setData(
				eventEntries.map((eventEntry: any) => ({
					action: eventEntry.name,
					createDate: eventEntry.createDate,
					name:
						eventEntry.individualName ??
						Liferay.Language.get('anonymous'),
				}))
			);
		}

		return () => {};
	}, [response, setData]);

	return (
		<AnalyticsFrame
			icon="bolt"
			title={Liferay.Language.get('latest-activity')}
			url={isAnalyticsEnabled ? `${BASE_URL}/view-timeline` : undefined}
		>
			<div className="latest-activity-container" ref={setElement}>
				{isAnalyticsEnabled ? (
					isLoading ? (
						<Loader />
					) : !data?.length ? (
						<p className="mt-3 text-center text-muted">
							{Liferay.Language.get('no-data-available')}
						</p>
					) : (
						<div className="latest-activity-fds">
							<FrontendDataSet
								customDataRenderers={{
									timeDataRenderer: TimeDataRenderer,
								}}
								customRenderers={{
									tableCell: [
										{
											component: ({
												itemData,
											}: {
												itemData: TLatestActivity;
											}) => (
												<div className="d-flex inline-item">
													<AccountSticker
														logoURL={
															itemData.logoURL
														}
														name={itemData.name}
														shape="user-icon"
													/>

													<p className="font-weight-semi-bold inline-item-after mb-0">
														{itemData.name}
													</p>
												</div>
											),
											name: 'userLatestActivity',
											type: 'internal',
										},
									],
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
														'userLatestActivity',
													fieldName: 'name',
													label: `${Liferay.Language.get('name')}`,
												},
												{
													fieldName: 'action',
													label: `${Liferay.Language.get('action')}`,
												},
												{
													contentRenderer:
														'timeDataRenderer',
													fieldName: 'createDate',
													label: `${Liferay.Language.get('time')}`,
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

export default LatestActivity;
