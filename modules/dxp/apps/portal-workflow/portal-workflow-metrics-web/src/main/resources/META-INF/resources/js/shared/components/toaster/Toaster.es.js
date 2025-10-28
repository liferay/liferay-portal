/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayAlert from '@clayui/alert';
import React from 'react';

const Toaster = ({removeToast, toasts = []}) => {
	return (
		<ClayAlert.ToastContainer>
			{toasts.map(({message, type, ...toast}, index) => (
				<ClayAlert
					{...toast}
					closeButtonAriaLabel={Liferay.Language.get('close')}
					displayType={type}
					key={index}
					onClose={() => {
						removeToast(index);
					}}
				>
					{message}
				</ClayAlert>
			))}
		</ClayAlert.ToastContainer>
	);
};

export default Toaster;
