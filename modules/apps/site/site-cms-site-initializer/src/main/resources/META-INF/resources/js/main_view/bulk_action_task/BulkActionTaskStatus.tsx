/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import cx from 'classnames';
import React from 'react';

interface BulkActionTaskStatusProps {
	executionStatus: string;
}

const BulkActionTaskStatus: React.FC<BulkActionTaskStatusProps> = ({
	executionStatus,
}) => {
	let badge = 'warning';

	if (executionStatus === 'completed') {
		badge = 'success';
	}
	else if (executionStatus === 'failed') {
		badge = 'danger';
	}

	return (
		<span className={cx('label', `label-${badge}`)}>
			<span className="label-item label-item-expand">
				{Liferay.Language.get(executionStatus)}
			</span>
		</span>
	);
};

export default BulkActionTaskStatus;
