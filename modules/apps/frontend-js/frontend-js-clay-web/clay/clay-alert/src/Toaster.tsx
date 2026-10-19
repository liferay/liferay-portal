/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React, {useEffect, useState} from 'react';

import ClayAlert from './Alert';
import {ClayToastContainer} from './ToastContainer';

import type {IToastContainerProps} from './ToastContainer';
import type {ToastStore} from './createToastStore';

interface IProps extends Omit<IToastContainerProps, 'children'> {

	/**
	 * The store that owns the list of toasts to render.
	 */
	store: ToastStore;
}

function ClayToaster({store, ...otherProps}: IProps) {
	const [toasts, setToasts] = useState(store.getToasts);

	useEffect(() => {
		setToasts(store.getToasts());

		return store.subscribe(setToasts);
	}, [store]);

	return (
		<ClayToastContainer {...otherProps}>
			{toasts.map(({key, props}) => (
				<ClayAlert key={key} {...props} />
			))}
		</ClayToastContainer>
	);
}

ClayToaster.displayName = 'ClayToaster';

export {ClayToaster};
