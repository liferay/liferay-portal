/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {sub} from 'frontend-js-web';
import React from 'react';

interface BulkActionTaskDurationProps {
	duration: number;
}

const BulkActionTaskDuration: React.FC<BulkActionTaskDurationProps> = ({
	duration,
}) => {
	return <div>{sub(Liferay.Language.get('x-minutes'), duration)}</div>;
};

export default BulkActionTaskDuration;
