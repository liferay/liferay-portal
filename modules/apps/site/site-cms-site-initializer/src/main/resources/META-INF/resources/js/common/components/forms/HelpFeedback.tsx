/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayForm from '@clayui/form';
import React from 'react';

const HelpFeedback = ({feedback}: {feedback: string}) => (
	<ClayForm.FeedbackItem className="font-weight-normal text-secondary">
		{feedback}
	</ClayForm.FeedbackItem>
);

export default HelpFeedback;
