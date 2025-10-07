/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Context} from '@clayui/modal';
import {Size} from '@clayui/modal/lib/types';
import {ReactElement, useContext} from 'react';

export type ModalOptions = {
	body: string | ReactElement;
	center?: boolean;
	footer?: (any | ReactElement)[];
	header?: string | ReactElement;
	size?: Size | 'md';
	status?: 'danger' | 'info' | 'success' | 'warning';
};

const useModalContext = () => {
	const [state, dispatch] = useContext(Context);

	return {
		onClose: state.onClose,
		onOpenModal: ({center = true, ...payload}: ModalOptions) => {
			dispatch({
				payload: {
					...payload,
					center,
					size: payload.size as Size,
				},
				type: 1,
			});
		},
		state,
	};
};

export default useModalContext;
