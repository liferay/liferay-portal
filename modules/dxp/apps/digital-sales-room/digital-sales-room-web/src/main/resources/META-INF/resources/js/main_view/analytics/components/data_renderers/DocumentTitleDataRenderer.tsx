/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayIcon from '@clayui/icon';
import ClaySticker from '@clayui/sticker';
import classNames from 'classnames';
import React from 'react';

import {TRoomDocumentsStatistics} from '../../../../common/utils/types';

export function DocumentTitleDataRenderer({
	itemData,
}: {
	itemData: TRoomDocumentsStatistics;
}) {
	const {title = '', type = ''} = itemData;

	return (
		<div>
			<ClaySticker
				className={classNames(
					'c-mr-2',
					'flex-shrink-0',
					'inline-item',
					'inline-item-before',
					type === 'pdf' ? 'file-icon-color-0' : 'file-icon-color-6'
				)}
				inline
				size="lg"
			>
				<ClayIcon
					aria-label={Liferay.Language.get(type)}
					symbol={type === 'pdf' ? 'document-pdf' : 'document-text'}
				/>
			</ClaySticker>

			<span
				aria-label={Liferay.Language.get(title)}
				className="table-list-title"
			>
				{Liferay.Language.get(title)}
			</span>
		</div>
	);
}
