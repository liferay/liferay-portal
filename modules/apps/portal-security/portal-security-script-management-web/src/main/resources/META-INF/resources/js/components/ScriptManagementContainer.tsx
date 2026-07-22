/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayAlert from '@clayui/alert';
import ClayButton from '@clayui/button';
import {Text} from '@clayui/core';
import {ClayCheckbox} from '@clayui/form';
import ClayModal, {useModal} from '@clayui/modal';
import {openToast} from 'frontend-js-components-web';
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
	scriptManagementConfigurationDefined: boolean;
}

export default function ScriptManagementContainer({
	allowScriptContentToBeExecutedOrIncluded,
	baseResourceURL,
	scriptManagementConfigurationDefined,
}: ScriptManagementContainerProps) {
	const [allowScriptContent, setAllowScriptContent] = useState(
		allowScriptContentToBeExecutedOrIncluded
	);
	const [groovyScriptUses, setGroovyScriptUses] = useState<
		GroovyScriptUseItem[]
	>([]);
	const [
		isScriptManagementConfigurationDefined,
		setIsScriptManagementConfigurationDefined,
	] = useState(scriptManagementConfigurationDefined);
	const [showGroovyScriptUsesModal, setShowGroovyScriptUsesModal] =
		useState<boolean>(false);

	const {observer, onClose} = useModal({
		onClose: () => {
			setAllowScriptContent(true);
			setGroovyScriptUses([]);
			setShowGroovyScriptUsesModal(false);
		},
	});

	const saveScriptManagementSystemConfiguration = async () => {
		const editScriptManagementConfigurationResponse = await fetch(
			createResourceURL(baseResourceURL, {
				allowScriptContentToBeExecutedOrIncluded: allowScriptContent,
				p_p_resource_id:
					'/system_settings/edit_script_management_configuration',
			}).toString()
		);

		openToast({
			message: editScriptManagementConfigurationResponse.ok
				? Liferay.Language.get('your-request-completed-successfully')
				: Liferay.Language.get('an-error-occurred'),
			type: editScriptManagementConfigurationResponse.ok
				? 'success'
				: 'danger',
		});

		if (
			editScriptManagementConfigurationResponse.ok &&
			!scriptManagementConfigurationDefined
		) {
			setIsScriptManagementConfigurationDefined(true);
		}
	};

	const handleSubmitSystemConfiguration = async () => {
		if (allowScriptContent) {
			saveScriptManagementSystemConfiguration();

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
			saveScriptManagementSystemConfiguration();

			return;
		}

		setGroovyScriptUses(groovyScriptUsesResponse);
		setShowGroovyScriptUsesModal(true);
	};

	return (
		<div className="lfr__script-management-container">
			<Text as="span" size={7} weight="bolder">
				{Liferay.Language.get('script-management')}
			</Text>

			{!isScriptManagementConfigurationDefined && (
				<ClayAlert
					displayType="info"
					title={`${Liferay.Language.get('info')}:`}
				>
					{Liferay.Language.get(
						'this-configuration-is-not-saved-yet.-the-values-shown-are-the-default'
					)}
				</ClayAlert>
			)}

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

			<ClayButton.Group key={1} spaced>
				<ClayButton
					displayType="primary"
					onClick={() => {
						handleSubmitSystemConfiguration();
					}}
					type="submit"
				>
					{Liferay.Language.get('save')}
				</ClayButton>

				<ClayButton
					displayType="secondary"
					onClick={() =>
						setAllowScriptContent(
							allowScriptContentToBeExecutedOrIncluded
						)
					}
				>
					{Liferay.Language.get('cancel')}
				</ClayButton>
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
