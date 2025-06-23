/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ICreationActionItem} from '../types';

const filterCreationActions = ({
	customActions,
	globalCollectionActions,
}: {
	customActions: Array<ICreationActionItem>;
	globalCollectionActions: any;
}): Array<ICreationActionItem> => {
	return customActions.filter((action: ICreationActionItem) => {
		if (
			!action.data?.permissionKey ||
			(action.data?.permissionKey &&
				globalCollectionActions &&
				Object.keys(globalCollectionActions).some((globalAction) => {
					if (action.data?.permissionKey) {
						return (
							globalAction.toLowerCase() ===
							action.data.permissionKey.toLowerCase()
						);
					}
				}))
		) {
			return action;
		}
	});
};

export default filterCreationActions;
