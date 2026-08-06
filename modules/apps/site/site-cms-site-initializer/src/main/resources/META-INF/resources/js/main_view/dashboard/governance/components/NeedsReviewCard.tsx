/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayLink from '@clayui/link';
import {FrontendDataSet} from '@liferay/frontend-data-set-web';
import React, {useMemo} from 'react';

import {ISearchAssetObjectEntry} from '../../../../common/types/AssetType';
import getAssetListFDSProps from '../../../props_transformer/getAssetListFDSProps';
import {GovernanceAdditionalProps} from '../types';

const EMPTY_STATE_IMAGE = '/states/cms_empty_state.svg';

const VIEWS = [
	{
		contentRenderer: 'table',
		default: true,
		label: Liferay.Language.get('table'),
		name: 'table',
		schema: {
			fields: [
				{
					actionId: 'actionLink',
					contentRenderer: 'assetRenderer',
					fieldName: 'embedded.title',
					label: Liferay.Language.get('title'),
				},
			],
		},
		thumbnail: 'table',
	},
];

interface INeedsReviewCardProps {
	additionalProps: GovernanceAdditionalProps;
	apiURL: string;
	description: string;
	emptyLabel: string;
	id: string;
	renderSubtitle: (item: ISearchAssetObjectEntry) => React.ReactNode;
	title: string;
	viewAllHref: string;
}

const NeedsReviewCard: React.FC<INeedsReviewCardProps> = ({
	additionalProps,
	apiURL,
	description,
	emptyLabel,
	id,
	renderSubtitle,
	title,
	viewAllHref,
}) => {
	const fdsProps = useMemo(
		() =>
			getAssetListFDSProps({
				additionalProps,
				apiURL,
				id,
				itemsActions: additionalProps.fdsActionDropdownItems,
				renderSubtitle,
			}),
		[additionalProps, apiURL, id, renderSubtitle]
	);

	return (
		<div className="border h-100 rounded-lg">
			<div className="bg-transparent border-bottom-0 pt-3 px-3">
				<div className="align-items-center d-flex justify-content-between">
					<span className="font-weight-semi-bold text-4">
						{title}
					</span>

					<ClayLink
						borderless
						className="font-weight-semi-bold text-3"
						href={viewAllHref}
						small
					>
						{Liferay.Language.get('view-all')}
					</ClayLink>
				</div>

				<span className="text-3 text-secondary">{description}</span>
			</div>

			<div className="cms-fds-fluid cms-needs-review custom-empty-state">
				<FrontendDataSet
					{...fdsProps}
					emptyState={{
						description: '',
						image: EMPTY_STATE_IMAGE,
						title: emptyLabel,
					}}
					id={id}
					showManagementBar={false}
					showPagination={false}
					showSearch={false}
					style="fluid"
					views={VIEWS}
				/>
			</div>
		</div>
	);
};

export default NeedsReviewCard;
