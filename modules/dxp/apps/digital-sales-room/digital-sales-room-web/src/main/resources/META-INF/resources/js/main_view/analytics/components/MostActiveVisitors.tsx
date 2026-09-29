/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {FrontendDataSet} from '@liferay/frontend-data-set-web';
import React, {useEffect, useState} from 'react';

import VisitorStickerRenderer from './cell_renderers/VisitorStickerRenderer';

import './../../../../css/components/MostActiveVisitors.scss';
import useAnalyticsQuery from '../../../common/hooks/useAnalyticsQuery';
import {TVisitor} from '../../../common/utils/types';
import AnalyticsFrame from './AnalyticsFrame';
import Loader from './Loader';

const MostActiveVisitors = ({
	isAnalyticsEnabled,
	namespace,
}: {
	isAnalyticsEnabled: boolean;
	namespace: string;
}) => {
	const [data, setData] = useState<TVisitor[]>([]);
	const [element, setElement] = useState<HTMLElement | null>(null);

	const {isLoading, response} = useAnalyticsQuery({
		element,
		query: {
			paths: [{key: 'mostActiveVisitors', path: '/most-active-visitors'}],
		},
		settings: {isAnalyticsEnabled},
		variables: {
			rangeKey: 7,
			size: 10,
			start: 0,
		},
	});

	useEffect(() => {
		if (response) {
			setData(response.mostActiveVisitors?.mostActiveVisitors ?? []);
		}
	}, [response]);

	return (
		<AnalyticsFrame
			icon="user"
			title={Liferay.Language.get('most-active-visitors')}
		>
			<div className="most-active-visitors-container" ref={setElement}>
				{isAnalyticsEnabled ? (
					isLoading ? (
						<Loader />
					) : !data?.length ? (
						<p className="mt-3 text-center text-muted">
							{Liferay.Language.get('no-data-available')}
						</p>
					) : (
						<div className="most-active-visitors-fds">
							<FrontendDataSet
								customRenderers={{
									tableCell: [
										{
											component: VisitorStickerRenderer,
											name: 'visitorSticker',
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
														'visitorSticker',
													fieldName: 'title',
													label: '',
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

export default MostActiveVisitors;
