/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import ClayIcon from '@clayui/icon';
import ClayLayout from '@clayui/layout';
import getCN from 'classnames';
import React, {useState} from 'react';

function CustomPanel({
	children,
	classNames,
	headerContent,
	initialCollapse = false,
	title,
}: {
	children: any;
	classNames?: string;
	headerContent?: React.ReactNode;
	initialCollapse?: boolean;
	title?: string;
}) {
	const [collapse, setCollapse] = useState(initialCollapse);

	return (
		<div className={getCN('sheet', classNames)}>
			<ClayLayout.SheetHeader className="mb-3">
				<span className="text-6 text-weight-bold">{title}</span>

				<div className="float-right">
					{headerContent}

					<ClayButton
						aria-label={Liferay.Language.get('collapse')}
						className="component-action"
						displayType="unstyled"
						onClick={() => setCollapse(!collapse)}
					>
						<ClayIcon
							symbol={collapse ? 'angle-right' : 'angle-down'}
						/>
					</ClayButton>
				</div>
			</ClayLayout.SheetHeader>

			{!collapse && children}
		</div>
	);
}

export default CustomPanel;
