/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayBreadcrumb from '@clayui/breadcrumb';
import {FrontendDataSet} from '@liferay/frontend-data-set-web';
import {openToast} from 'frontend-js-components-web';
import {sub} from 'frontend-js-web';
import React, {useEffect, useState} from 'react';

import AuthorCellRenderer from './cell_renderers/AuthorCellRenderer';
import TitleCellRenderer from './cell_renderers/TitleCellRenderer';
import {getInsightType} from './services/InsightTypeService';
import {InsightType} from './types/InsightType';

import './InsightDetailView.scss';

type BreadcrumbItem = {
	href?: string;
	label: string;
};

export default function InsightDetailView({
	apiURL,
	breadcrumbItems,
	externalReferenceCode,
	fdsId,
	views,
}: {
	apiURL: string;
	breadcrumbItems: BreadcrumbItem[];
	externalReferenceCode: string;
	fdsId: string;
	views: any[];
}) {
	const [data, setData] = useState<InsightType>({});

	useEffect(() => {
		if (!externalReferenceCode) {
			return;
		}

		getInsightType(externalReferenceCode)
			.then((json) => setData(json))
			.catch(() => {
				openToast({
					message: Liferay.Language.get(
						'unable-to-load-insight-details'
					),
					type: 'danger',
				});

				window.location.assign(breadcrumbItems[0]?.href ?? '');
			});
	}, [breadcrumbItems, externalReferenceCode]);

	return (
		<>
			<ClayBreadcrumb
				items={[
					...breadcrumbItems,
					{active: true, label: data.name ?? ''},
				]}
			/>

			<div className="insight-detail-info">
				<h2 className="mb-4 py-2 text-7 text-dark">
					{sub(
						Liferay.Language.get('x-from-x-pages'),
						data.name ?? '',
						String(data.affectedPagesCount ?? 0)
					)}
				</h2>

				<div className="mb-4">
					<p className="section-title">
						{Liferay.Language.get('description')}
					</p>

					<p className="text-3">{data.description}</p>
				</div>

				<div className="mb-4">
					<p className="section-title">
						{Liferay.Language.get('suggestion')}
					</p>

					<p className="text-3">{data.fixHint}</p>
				</div>
			</div>

			<div className="insight-detail-affected-pages">
				<h4 className="mb-3">
					{`${Liferay.Language.get('affected-pages')} (${
						data.affectedPagesCount ?? 0
					})`}
				</h4>

				<FrontendDataSet
					apiURL={apiURL}
					appURL={`${Liferay.ThemeDisplay.getPortalURL()}/o/frontend-data-set-taglib/app`}
					customRenderers={{
						tableCell: [
							{
								component: AuthorCellRenderer,
								name: 'authorCellRenderer',
								type: 'internal',
							},
							{
								component: TitleCellRenderer,
								name: 'titleCellRenderer',
								type: 'internal',
							},
						],
					}}
					id={fdsId}
					pagination={{initialDelta: 10}}
					showManagementBar={false}
					showPagination
					showSearch={false}
					views={views}
				/>
			</div>
		</>
	);
}
