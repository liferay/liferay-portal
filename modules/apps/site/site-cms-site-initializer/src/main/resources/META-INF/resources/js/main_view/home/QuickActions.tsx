/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayIcon from '@clayui/icon';
import {navigate} from 'frontend-js-web';
import React, {MouseEvent} from 'react';

import {AssetLibrary} from '../../common/types/AssetLibrary';
import createAssetAction, {
	AssetData,
} from '../props_transformer/actions/createAssetAction';

export type QuickActionAssetData = {
	action: 'createAsset' | 'createVocabulary';
	assetLibraries?: AssetLibrary[];
	redirect: string;
	title: string;
};

export default function QuickActions({
	quickActions,
}: {
	quickActions: QuickActionAssetData[];
}) {
	const handleActionClick = (
		event: MouseEvent<HTMLButtonElement>,
		quickAction: QuickActionAssetData
	) => {
		event.preventDefault();

		if (quickAction.action === 'createVocabulary') {
			navigate(quickAction.redirect);
		}
		else {
			createAssetAction({
				...(quickAction as AssetData),
			});
		}
	};

	return (
		<div className="cms-section">
			<div className="pb-2 pt-2 row">
				<div className="col">
					<span className="font-weight-semi-bold text-4">
						{Liferay.Language.get('quick-actions')}
					</span>
				</div>
			</div>

			<div className="row">
				{quickActions.map((quickAction: any) => (
					<div className="col" key={quickAction.href}>
						<button
							className="btn btn-secondary text-left w-100"
							onClick={(event) =>
								handleActionClick(event, quickAction)
							}
						>
							<ClayIcon
								className="mr-2"
								symbol={quickAction.icon}
							/>

							<span>{quickAction.title}</span>
						</button>
					</div>
				))}
			</div>
		</div>
	);
}
