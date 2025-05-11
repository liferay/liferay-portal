/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Text} from '@clayui/core';
import {ReactNode} from 'react';

type HeaderProps = {
	description?: ReactNode | string;
	title?: ReactNode | string;
};

export function Header({description, title}: HeaderProps) {
	return (
		<div className="d-flex flex-column mb-4">
			<Text size={9} weight="semi-bold">
				{title}
			</Text>

			<span className="secondary-text">{description}</span>
		</div>
	);
}
