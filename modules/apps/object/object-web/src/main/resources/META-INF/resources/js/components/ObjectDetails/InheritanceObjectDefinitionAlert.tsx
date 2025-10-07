/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayAlert from '@clayui/alert';
import {
	ILearnResourceContext,
	LearnMessage,
	LearnResourcesContext,
} from 'frontend-js-components-web';
import React from 'react';

interface InheritanceObjectDefinitionAlertProps {
	learnResources: ILearnResourceContext;
}

export function InheritanceObjectDefinitionAlert({
	learnResources,
}: InheritanceObjectDefinitionAlertProps) {
	return (
		<ClayAlert
			displayType="info"
			title={`${Liferay.Language.get('info')}:`}
		>
			{Liferay.Language.get(
				'inheritance-is-enabled-for-at-least-one-relationship-in-this-definition'
			)}
			&nbsp;
			<LearnResourcesContext.Provider value={learnResources}>
				<LearnMessage
					className="alert-link"
					resource="object-web"
					resourceKey="inheritance-relationships"
				/>
			</LearnResourcesContext.Provider>
		</ClayAlert>
	);
}
