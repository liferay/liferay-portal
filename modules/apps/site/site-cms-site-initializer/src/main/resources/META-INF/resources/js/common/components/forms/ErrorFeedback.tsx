/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayForm from '@clayui/form';
import React from 'react';

const ErrorFeedback = ({message: message}: {message: string}) => (
	<ClayForm.FeedbackItem className="font-weight-normal">
		<ClayForm.FeedbackIndicator symbol="info-circle" />

		<strong>{Liferay.Language.get('error')}: </strong>

		{message}
	</ClayForm.FeedbackItem>
);

export default ErrorFeedback;
