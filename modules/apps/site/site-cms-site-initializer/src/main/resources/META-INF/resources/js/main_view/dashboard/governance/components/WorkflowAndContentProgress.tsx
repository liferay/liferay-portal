/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayLayout from '@clayui/layout';
import React from 'react';

import {SectionHeader} from '../../common/SectionHeader';
import {GovernanceAdditionalProps} from '../types';
import {ContentProgress} from './ContentProgress';

export function WorkflowAndContentProgress({
	additionalProps,
}: {
	additionalProps: GovernanceAdditionalProps;
}) {
	const title = Liferay.Language.get('workflow-and-content-progress');

	return (
		<div className="mb-3 py-4">
			<SectionHeader icon="workflow" title={title} />

			<ClayLayout.Row aria-label={title} className="mt-3" role="group">
				<ClayLayout.Col md={6}>
					<ContentProgress additionalProps={additionalProps} />
				</ClayLayout.Col>
			</ClayLayout.Row>
		</div>
	);
}
