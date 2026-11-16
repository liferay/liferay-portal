/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ClayButtonWithIcon} from '@clayui/button';
import {openToast} from 'frontend-js-components-web';
import React, {useContext} from 'react';

import {DefinitionBuilderContext} from '../../../../DefinitionBuilderContext';
import {
	publishDefinitionRequest,
	retrieveDefinitionRequest,
	saveDefinitionRequest,
} from '../../../../util/fetchUtil';
import lang from '../../../../util/lang';
import toLocalDateTimeFormatted from '../../../util/toLocalDateTimeFormatted';
import {getVersionDetails} from './getVersionDetails';

import './VersionRow.scss';

interface RetrieveWorkflowDefinitionResponseProps {
	active: boolean;
	content: string;
	timeZoneId: string;
	title: string;
	title_i18n: Liferay.Language.FullyLocalizedValue<string>;
	version: string;
}

interface VersionRowProps {
	creatorName: string;
	dateCreated: string;
	setWorkflowDefinitionVersions: React.Dispatch<
		React.SetStateAction<WorkflowDefinitionVersion[]>
	>;
	timeZoneId: string;
	versionNumber: number;
}

export function VersionRow({
	creatorName,
	dateCreated,
	setWorkflowDefinitionVersions,
	timeZoneId,
	versionNumber,
}: VersionRowProps) {
	const {definitionName, setDefinitionName} = useContext(
		DefinitionBuilderContext
	);

	const versionCreationDate = toLocalDateTimeFormatted(
		dateCreated,
		Liferay.ThemeDisplay.getBCP47LanguageId(),
		timeZoneId
	);

	const restoreSuccess = async (response: Response) => {
		openToast({
			message: lang.sub(Liferay.Language.get('restored-to-revision-x'), [
				String(versionNumber),
			]),
			type: 'success',
		});

		const restoredWorkflowDefinition =
			(await response.json()) as WorkflowDefinition;

		setDefinitionName(restoredWorkflowDefinition.name);

		setWorkflowDefinitionVersions((prevValues) => [
			{
				creatorName: restoredWorkflowDefinition.creator?.name as string,
				dateCreated: restoredWorkflowDefinition.dateModified as string,
				version: String(
					parseInt(restoredWorkflowDefinition.version, 10)
				),
			},
			...prevValues,
		]);
	};

	const restoreFailed = () => {
		openToast({
			message: Liferay.Language.get('unable-to-restore-this-item'),
			type: 'danger',
		});
	};

	const restoreWorkflowDefinition = async (
		publishOrSaveWorkflowDefinitionRequest: (
			value: WorkflowDefinition
		) => Promise<Response>,
		requestBody: WorkflowDefinition
	) => {
		const publishOrSaveWorkflowDefinitionResponse =
			await publishOrSaveWorkflowDefinitionRequest(requestBody);

		if (!publishOrSaveWorkflowDefinitionResponse.ok) {
			restoreFailed();

			return;
		}

		restoreSuccess(publishOrSaveWorkflowDefinitionResponse);

		return;
	};

	const handleRestoreWorkflowDefinitionVersion = async () => {
		const retrieveWorkflowDefinitionResponse =
			await retrieveDefinitionRequest(definitionName, versionNumber);

		const {active, content, title, title_i18n, version} =
			(await retrieveWorkflowDefinitionResponse.json()) as RetrieveWorkflowDefinitionResponseProps;

		if (active) {
			await restoreWorkflowDefinition(publishDefinitionRequest, {
				active,
				content,
				name: definitionName,
				title,
				title_i18n,
				version,
			});
		}
		else {
			await restoreWorkflowDefinition(saveDefinitionRequest, {
				active,
				content,
				name: definitionName,
				title,
				title_i18n,
				version,
			});
		}
	};

	return (
		<>
			<div className="lfr-workflow__version-row-container">
				<div className="lfr-workflow__version-row-info-container">
					<label className="lfr-workflow__version-row-info-number">
						{Liferay.Language.get('version')} {versionNumber}
					</label>

					<span className="lfr-workflow__version-row-info-date-user">
						{getVersionDetails(creatorName, versionCreationDate)}
					</span>
				</div>

				<ClayButtonWithIcon
					aria-labelledby={Liferay.Language.get('restore')}
					className="lfr-workflow__version-row-restore-button"
					displayType="unstyled"
					onClick={() => handleRestoreWorkflowDefinitionVersion()}
					symbol="restore"
					title={Liferay.Language.get('restore')}
				/>
			</div>

			<div className="sheet-subtitle" />
		</>
	);
}
