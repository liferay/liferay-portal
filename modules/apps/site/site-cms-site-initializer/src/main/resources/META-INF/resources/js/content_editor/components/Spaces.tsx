/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayForm from '@clayui/form';
import ClayIcon from '@clayui/icon';
import React from 'react';

import {SpaceSticker} from '../../index';

const DEFAULT_NAME = Liferay.Language.get('space');

export default function Spaces({
	displayType,
	name,
	size = 'sm',
}: Pick<
	React.ComponentProps<typeof SpaceSticker>,
	'displayType' | 'name' | 'size'
>) {
	return (
		<ClayForm.Group className="align-items-center c-gap-3 d-flex m-0">
			<ClayIcon className="mt-0 text-secondary" symbol="box-container" />

			<label className="m-0">{Liferay.Language.get('space')}</label>

			<div className="align-items-center d-flex ml-2">
				<SpaceSticker
					displayType={displayType}
					name={name || DEFAULT_NAME}
					size={size}
				/>
			</div>
		</ClayForm.Group>
	);
}
