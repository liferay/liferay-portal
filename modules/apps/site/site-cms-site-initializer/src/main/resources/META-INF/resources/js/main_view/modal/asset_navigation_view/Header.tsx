/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ClayButtonWithIcon} from '@clayui/button';
import ClayIcon from '@clayui/icon';
import ClayLink from '@clayui/link';
import React from 'react';

import {ISearchAssetObjectEntry} from '../../../common/types/AssetType';

export default function Header({
	handleClickComments,
	handleClickInfo,
	item,
	showInfoPanel,
}: {
	handleClickComments: () => void;
	handleClickInfo: () => void;
	item: ISearchAssetObjectEntry;
	showInfoPanel: boolean;
}) {
	const headerName = item.embedded?.title || item.embedded.file?.name;

	const file = item.embedded.file;
	const link = file?.link;

	return (
		<div className="autofit-row autofit-row-center">
			<div className="autofit-col autofit-col-expand">
				<div className="text-truncate">{headerName}</div>
			</div>

			<div className="align-items-center c-gap-2 d-flex">
				<ClayButtonWithIcon
					aria-label={Liferay.Language.get('show-comments')}
					borderless
					displayType="secondary"
					onClick={handleClickComments}
					symbol="message"
				/>

				{showInfoPanel && (
					<ClayButtonWithIcon
						aria-label={Liferay.Language.get('show-details')}
						borderless
						displayType="secondary"
						onClick={handleClickInfo}
						symbol="info-circle-open"
					/>
				)}

				{link?.href && (
					<div className="autofit-col pr-3">
						<ClayLink
							button
							displayType="primary"
							href={link.href}
							small
						>
							<span className="inline-item inline-item-before">
								<ClayIcon symbol="download" />
							</span>

							{Liferay.Language.get('download')}
						</ClayLink>
					</div>
				)}
			</div>
		</div>
	);
}
