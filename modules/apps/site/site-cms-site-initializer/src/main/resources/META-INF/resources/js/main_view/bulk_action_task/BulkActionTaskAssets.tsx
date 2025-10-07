/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayIcon from '@clayui/icon';
import React from 'react';

interface BulkActionTaskAssetsProps {
	assetsCount: number;
	executionStatus?: string;
}

const BulkActionTaskAssets: React.FC<BulkActionTaskAssetsProps> = ({
	assetsCount,
	executionStatus,
}) => {
	return (
		<div>
			{assetsCount > 0 && executionStatus === 'completed' && (
				<ClayIcon className="mr-2 text-success" symbol="check" />
			)}

			{assetsCount > 0 && executionStatus === 'failed' && (
				<ClayIcon
					className="mr-2 text-danger"
					symbol="times-circle-full"
				/>
			)}

			<span>{`${assetsCount} ${executionStatus ? Liferay.Language.get(executionStatus) : ''}`}</span>
		</div>
	);
};

export default BulkActionTaskAssets;
