/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayIcon from '@clayui/icon';
import {ReactNode} from 'react';
import {Link} from 'react-router-dom';

type BackLinkProps = {
	children: ReactNode;
	path?: string;
};

export default function BackLink({children, path = '..'}: BackLinkProps) {
	return (
		<Link className="align-items-center d-flex text-dark" to={path}>
			<ClayIcon
				aria-label="arrow left"
				className="mr-2"
				symbol="order-arrow-left"
			/>

			<span className="h5 mt-1">{children}</span>
		</Link>
	);
}
