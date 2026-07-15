/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Text} from '@clayui/core';
import ClayIcon from '@clayui/icon';
import React from 'react';

interface ISectionHeader {
	description?: string;
	icon: string;
	title: string;
}

const SectionHeader: React.FC<ISectionHeader> = ({
	description,
	icon,
	title,
}) => {
	return (
		<>
			<div className="cms-dashboard__section-header">
				<span className="mr-2">
					<Text color="secondary" size={4}>
						<ClayIcon symbol={icon} />
					</Text>
				</span>

				<h3 className="m-0">
					<Text color="secondary" size={3} weight="semi-bold">
						{title.toUpperCase()}
					</Text>
				</h3>
			</div>

			{description && (
				<div className="mt-1">
					<Text color="secondary" size={3}>
						{description}
					</Text>
				</div>
			)}
		</>
	);
};

export {SectionHeader};
