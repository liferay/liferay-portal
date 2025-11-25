/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {useEffect, useState} from 'react';

import {SERVICE_NETWORK_STATUS_TYPES} from '../config/constants/serviceNetworkStatusTypes';
import {config} from '../config/index';
import {useSelector} from '../contexts/StoreContext';
import selectHasAnyUpdatePermission from '../selectors/selectHasAnyUpdatePermission';

export default function useDisabledDiscardDraft() {
	const canDiscardDraft = useSelector(selectHasAnyUpdatePermission);
	const [enableDiscard, setEnableDiscard] = useState(() => canDiscardDraft);

	const draft = useSelector((state) => state.draft);
	const network = useSelector((state) => state.network);

	useEffect(() => {
		if (!canDiscardDraft) {
			return;
		}

		setEnableDiscard(
			network.status === SERVICE_NETWORK_STATUS_TYPES.draftSaved ||
				draft ||
				config.isConversionDraft
		);
	}, [canDiscardDraft, network, draft]);

	return !enableDiscard;
}
