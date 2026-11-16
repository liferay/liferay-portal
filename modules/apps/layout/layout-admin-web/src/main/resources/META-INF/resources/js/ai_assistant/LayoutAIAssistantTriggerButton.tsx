/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {
	AIAssistantTriggerButton,
	CONTENT_CHANGED_EVENT,
} from '@liferay/ai-hub-cell-js-components-web';
import React, {useEffect} from 'react';

import {
	AI_ASSISTANT_TRIGGER_ID,
	GENERATE_PAGE_CHATBOT_ERC,
} from './aiAssistant';

function getPage(
	data: string | undefined
): {siteExternalReferenceCode?: string} | null {
	if (!data) {
		return null;
	}

	try {
		return JSON.parse(JSON.parse(data).data);
	}
	catch {
		return null;
	}
}

type Props = {
	portletNamespace: string;
	siteExternalReferenceCode: string;
};

export default function LayoutAIAssistantTriggerButton({
	portletNamespace,
	siteExternalReferenceCode,
}: Props) {
	useEffect(() => {
		const contentChangedHandler = Liferay.on(
			CONTENT_CHANGED_EVENT,
			({data}: {data?: string}) => {
				const page = getPage(data);

				if (
					page &&
					page.siteExternalReferenceCode !== siteExternalReferenceCode
				) {
					return;
				}

				Liferay.Portlet.refresh(`#p_p_id${portletNamespace}`);
			}
		);

		return () => {
			contentChangedHandler.detach();
		};
	}, [portletNamespace, siteExternalReferenceCode]);

	return (
		<AIAssistantTriggerButton
			chatbotExternalReferenceCode={GENERATE_PAGE_CHATBOT_ERC}
			context={{siteExternalReferenceCode}}
			defaultExpanded
			greeting={Liferay.Language.get('hi-i-can-help-you-generate-pages')}
			hideLabel
			instructionDefinitionScope=""
			presentation="dropdown"
			pushContainer="#main-content"
			triggerId={AI_ASSISTANT_TRIGGER_ID}
		/>
	);
}
