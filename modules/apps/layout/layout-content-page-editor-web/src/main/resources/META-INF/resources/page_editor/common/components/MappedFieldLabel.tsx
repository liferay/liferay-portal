/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayIcon from '@clayui/icon';
import classNames from 'classnames';
import React from 'react';

export default function MappedFieldLabel({className}: {className?: string}) {
	return (
		<p
			className={classNames(
				'align-items-center d-flex font-weight-bold page-editor__mapped-field-label text-2',
				className
			)}
		>
			<ClayIcon
				className="flex-shrink-0 mr-2 mt-0 page-editor__mapped-field-label__icon text-4"
				symbol="dynamic-data-mapping"
			/>

			{Liferay.Language.get('mapped')}
		</p>
	);
}
