/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import {Text} from '@clayui/core';
import {ClayCheckbox} from '@clayui/form';
import ClayLink from '@clayui/link';
import ClayModal, {useModal} from '@clayui/modal';
import {createResourceURL, fetch} from 'frontend-js-web';
import React, {useState} from 'react';

import {GroovyScriptUsesModalContent} from './GroovyScriptUsesModalContent';

import './ScriptManagementContainer.scss';

export type GroovyScriptUseItem = {
	companyWebId: string;
	sourceName: string;
	sourceURL: string;
};

interface ScriptManagementContainerProps {
	allowScriptContentToBeExecutedOrIncluded: boolean;
	baseResourceURL: string;
	formName: string;
	namespace: string;
	redirectURL: string;
}

export default function ScriptManagementContainer({
	allowScriptContentToBeExecutedOrIncluded,
	baseResourceURL,
	formName,
	namespace,
	redirectURL,
}: ScriptManagementContainerProps) {
	const [allowScriptContent, setAllowScriptContent] = useState(
		allowScriptContentToBeExecutedOrIncluded
	);
	const [groovyScriptUses, setGroovyScriptUses] = useState<
		GroovyScriptUseItem[]
	>([]);
	const [showGroovyScriptUsesModal, setShowGroovyScriptUsesModal] =
		useState<boolean>(false);

	const {observer, onClose} = useModal({
		onClose: () => {
			setAllowScriptContent(true);
			setGroovyScriptUses([]);
			setShowGroovyScriptUsesModal(false);
		},
	});

	const submitConfiguration = () => {
		const formElement = document.getElementById(
			formName
		) as HTMLFormElement | null;

		formElement?.requestSubmit();
	};

	const handleSubmitSystemConfiguration = async () => {
		if (allowScriptContent) {
			submitConfiguration();

			return;
		}

		const getGroovyScriptUsesResponse = await fetch(
			createResourceURL(baseResourceURL, {
				p_p_resource_id: '/system_settings/get_groovy_script_uses',
			}).toString()
		);

		const groovyScriptUsesResponse =
			(await getGroovyScriptUsesResponse.json()) as GroovyScriptUseItem[];

		if (!groovyScriptUsesResponse.length) {
			submitConfiguration();

			return;
		}

		setGroovyScriptUses(groovyScriptUsesResponse);
		setShowGroovyScriptUsesModal(true);
	};

	return (
		<div className="lfr__script-management-container">
			<div className="lfr__script-management-checkbox-container">
				<ClayCheckbox
					aria-label={Liferay.Language.get(
						'allow-administrator-to-create-and-execute-code-in-liferay'
					)}
					checked={allowScriptContent}
					label={Liferay.Language.get(
						'allow-administrator-to-create-and-execute-code-in-liferay'
					)}
					onChange={() => setAllowScriptContent(!allowScriptContent)}
				/>

				<Text color="secondary">
					{Liferay.Language.get(
						'administrators-can-create-and-execute-code-in-their-virtual-instance'
					)}
				</Text>
			</div>

			<input
				name={`${namespace}allowScriptContentToBeExecutedOrIncluded`}
				type="hidden"
				value={allowScriptContent ? 'true' : 'false'}
			/>

			<ClayButton.Group spaced>
				<ClayButton
					displayType="primary"
					onClick={() => {
						handleSubmitSystemConfiguration();
					}}
				>
					{Liferay.Language.get('save')}
				</ClayButton>

				<ClayLink
					className="btn btn-cancel btn-secondary"
					href={redirectURL}
				>
					{Liferay.Language.get('cancel')}
				</ClayLink>
			</ClayButton.Group>

			{showGroovyScriptUsesModal && (
				<ClayModal
					center
					observer={observer}
					size="lg"
					status="warning"
				>
					<GroovyScriptUsesModalContent
						groovyScriptUses={groovyScriptUses}
						handleOnClose={onClose}
					/>
				</ClayModal>
			)}
		</div>
	);
}
