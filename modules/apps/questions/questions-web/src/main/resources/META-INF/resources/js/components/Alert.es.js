/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayAlert from '@clayui/alert';
import React, {useEffect, useState} from 'react';

export default function Alert({displayType = 'danger', info}) {
	const [alert, setAlert] = useState(null);

	useEffect(() => {
		if (info && (info.title || info.message)) {
			setAlert(info);
		}
	}, [info]);

	if (!alert) {
		return null;
	}

	return (
		<ClayAlert.ToastContainer>
			<ClayAlert
				autoClose={5000}
				closeButtonAriaLabel={Liferay.Language.get('close')}
				displayType={displayType}
				onClose={() => {
					setAlert(null);
				}}
				title={alert.title || alert.message}
			>
				{alert.title && alert.message}
			</ClayAlert>
		</ClayAlert.ToastContainer>
	);
}
