/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayIcon from '@clayui/icon';
import ClaySticker from '@clayui/sticker';
import React from 'react';

import {NO_VALUE} from '../../../common/utils/constants';

type Assignee = {name: string};

const AssigneeRenderer = ({
	itemData,
}: {
	itemData: {
		embedded?: {assigneePerson?: Assignee; assigneeRoles?: Assignee[]};
	};
}) => {
	const {assigneePerson, assigneeRoles = []} = itemData.embedded ?? {};

	const name =
		assigneePerson?.name ??
		assigneeRoles.map((assigneeRole) => assigneeRole.name).join(', ');

	if (!name) {
		return <span className="text-secondary">{NO_VALUE}</span>;
	}

	return (
		<span className="align-items-center d-flex">
			<ClaySticker
				className="c-mr-2"
				displayType="secondary"
				shape="circle"
				size="sm"
			>
				<ClayIcon symbol={assigneePerson ? 'user' : 'users'} />
			</ClaySticker>

			{name}
		</span>
	);
};

export default AssigneeRenderer;
