/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayIcon from '@clayui/icon';
import {ContentLabel} from '@clayui/label';
import ClaySticker from '@clayui/sticker';
import React from 'react';

interface Props {
	ariaLabel: string;
	className?: string;
	id?: string;
	value: string;
}

export default function DesignLibraryNameLabel({
	ariaLabel,
	className,
	id,
	value,
}: Props) {
	return (
		<ContentLabel
			aria-label={ariaLabel}
			className={className}
			displayType="content-5"
			id={id}
			large
			withClose={false}
		>
			<ContentLabel.ItemBefore>
				<ClaySticker className="bg-white rounded" size="xs">
					<ClayIcon symbol="books-brush" />
				</ClaySticker>
			</ContentLabel.ItemBefore>

			<ContentLabel.ItemExpand>{value}</ContentLabel.ItemExpand>
		</ContentLabel>
	);
}
