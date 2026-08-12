/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React from 'react';

import {SectionHeader} from '../../common/SectionHeader';

export function WorkflowAndContentProgress() {
	return (
		<div className="mb-3 py-4">
			<SectionHeader
				icon="workflow"
				title={Liferay.Language.get('workflow-and-content-progress')}
			/>
		</div>
	);
}
