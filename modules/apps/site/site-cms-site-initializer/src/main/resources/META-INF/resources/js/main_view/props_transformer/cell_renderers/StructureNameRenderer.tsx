/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayIcon from '@clayui/icon';
import React from 'react';

import SimpleActionLinkRenderer, {ActionItem} from './SimpleActionLinkRenderer';

const StructureNameRenderer = ({
	actions,
	itemData,
	options,
	value,
}: {
	actions: ActionItem[];
	itemData: {system?: boolean} & Record<string, unknown>;
	options: {actionId: string};
	value: string;
}) => {
	if (itemData.system) {
		return (
			<div className="table-list-title">
				{value}

				<ClayIcon
					aria-label={Liferay.Language.get(
						'system-default-structure'
					)}
					className="c-ml-2 lfr-portal-tooltip text-secondary"
					data-title={Liferay.Language.get(
						'system-default-structure'
					)}
					symbol="lock"
				/>
			</div>
		);
	}

	return (
		<SimpleActionLinkRenderer
			actions={actions}
			itemData={itemData}
			options={options}
			value={value}
		/>
	);
};

export default StructureNameRenderer;
