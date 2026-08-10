/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {FrontendDataSet} from '@liferay/frontend-data-set-web';
import React from 'react';

const ContentRenderersFrontendDataSet = () => {
	const items = [
		{
			numberActionLink: 0,
			numberDefault: 0,
			title: 'Item with 0',
		},
		{
			numberActionLink: 1,
			numberDefault: 1,
			title: 'Item with 1',
		},
		{
			numberActionLink: 2,
			numberDefault: 2,
			title: 'Item with 2',
		},
		{
			numberActionLink: 3,
			numberDefault: 3,
			title: 'Item with 3',
		},
	];

	return (
		<FrontendDataSet
			id="ContentRenderersFrontendDataSet"
			items={items}
			itemsActions={[
				{
					data: {
						id: 'edit',
					},
					icon: 'pencil',
					label: Liferay.Language.get('edit'),
					onClick: () => {},
				},
			]}
			onItemsPropSearch={(item, search) => item.title.includes(search)}
			pagination={{
				deltas: [{label: 2}, {label: 4}],
				initialDelta: 2,
			}}
			searchAsYouType={true}
			showPagination
			views={[
				{
					contentRenderer: 'table',
					name: 'table',
					schema: {
						fields: [
							{
								fieldName: 'title',
								label: 'Title',
							},
							{
								actionId: 'edit',
								contentRenderer: 'actionLink',
								fieldName: 'numberActionLink',
								label: 'Number (actionLink)',
							},
							{
								fieldName: 'numberDefault',
								label: 'Number (default)',
							},
						],
					},
				},
			]}
		/>
	);
};

export default ContentRenderersFrontendDataSet;
