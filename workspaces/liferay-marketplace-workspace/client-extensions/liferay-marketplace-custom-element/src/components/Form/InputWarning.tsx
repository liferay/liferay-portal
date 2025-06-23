/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayIcon from '@clayui/icon';
import ClayLabel from '@clayui/label';
import React, {ReactNode} from 'react';

const BaseWarning: React.FC<{children: ReactNode}> = ({children}) => {
	return (
		<ClayLabel className="label-tonal-danger mt-1 mx-0 p-0 rounded w-100">
			<div className="align-items-center badge d-flex m-0 warning">
				<span className="inline-item inline-item-before">
					<ClayIcon
						className="c-ml-2 c-mr-2"
						symbol="exclamation-full"
					/>
				</span>

				<span className="font-weight-normal text-paragraph">
					{children}
				</span>
			</div>
		</ClayLabel>
	);
};

export default BaseWarning;
