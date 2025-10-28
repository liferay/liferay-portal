/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayAlert from '@clayui/alert';
import ClayButton from '@clayui/button';
import {getUid} from 'data-engine-js-components-web';
import React, {useCallback, useContext, useState} from 'react';

const ToastContext = React.createContext();

export function ToastProvider({children}) {
	const [toastItems, setToastItems] = useState([]);

	const addToast = useCallback(
		(item) => {
			setToastItems((prevItems) => [
				...prevItems,
				{id: getUid(), ...item},
			]);
		},
		[setToastItems]
	);

	return (
		<ToastContext.Provider value={addToast}>
			<ClayAlert.ToastContainer>
				{toastItems.map(
					({action, id, message, title, ...otherProps}) => (
						<ClayAlert
							{...otherProps}
							autoClose={5000}
							closeButtonAriaLabel={Liferay.Language.get('close')}
							key={id}
							onClose={() =>
								setToastItems((prevItems) =>
									prevItems.filter((item) => item.id !== id)
								)
							}
							title={title}
						>
							{message}

							{action && (
								<ClayAlert.Footer>
									<ClayButton.Group>
										{action}
									</ClayButton.Group>
								</ClayAlert.Footer>
							)}
						</ClayAlert>
					)
				)}
			</ClayAlert.ToastContainer>

			{children}
		</ToastContext.Provider>
	);
}

export function useToast() {
	return useContext(ToastContext);
}
