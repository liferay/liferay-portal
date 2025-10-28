/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import {Body, Cell, Head, Row, Table, Text} from '@clayui/core';
import ClayIcon from '@clayui/icon';
import ClayLoadingIndicator from '@clayui/loading-indicator';
import ClayModal from '@clayui/modal';
import {API, errorsUtils, stringUtils} from '@liferay/object-js-components-web';
import React, {useEffect, useState} from 'react';

import './ModalImportFailed.scss';

interface ModalImportFailedProps {
	error: API.ErrorDetails;
	handleOnclose: () => void;
	importedObjectDefinitions: ObjectDefinition[];
}

type ImportedObjectDefinitionsStatus = {
	errorType?: string;
	label: string;
	success: boolean;
};

type FailedObjectDefinitions = {
	error: {
		type: string;
	};
	objectDefinitionName: string;
};

const tableHeaderItems = [
	{
		id: 'objectDefinition',
		name: Liferay.Language.get('object-definition'),
	},
	{
		id: 'importStatus',
		name: Liferay.Language.get('import-status'),
	},
];

export function ModalImportFailed({
	error,
	handleOnclose,
	importedObjectDefinitions,
}: ModalImportFailedProps) {
	const [
		importedObjectDefinitionsStatus,
		setImportedObjectDefinitionsStatus,
	] = useState<ImportedObjectDefinitionsStatus[]>();

	const [loading, setLoading] = useState(false);

	useEffect(() => {
		setLoading(true);

		const failedObjectDefinitions = JSON.parse(
			error.message
		) as FailedObjectDefinitions[];

		if (failedObjectDefinitions.length) {
			const failedObjectDefinitionsMap = new Map<string, string>();

			failedObjectDefinitions.forEach((failedObjectDefinition) => {
				failedObjectDefinitionsMap.set(
					failedObjectDefinition.objectDefinitionName,
					failedObjectDefinition.error.type
				);
			});

			const newImportedObjectDefinitionsStatus =
				importedObjectDefinitions.map((importedObjectDefinition) => {
					const failedObjectDefinition =
						failedObjectDefinitionsMap.has(
							importedObjectDefinition.name
						);

					return {
						errorType: failedObjectDefinition
							? failedObjectDefinitionsMap.get(
									importedObjectDefinition.name
								)
							: undefined,
						label: stringUtils.getLocalizableLabel({
							fallbackLabel: importedObjectDefinition.name,
							fallbackLanguageId:
								importedObjectDefinition.defaultLanguageId,
							labels: importedObjectDefinition.label,
						}),
						success: !failedObjectDefinition,
					} as ImportedObjectDefinitionsStatus;
				});

			setImportedObjectDefinitionsStatus(
				newImportedObjectDefinitionsStatus
			);
		}

		setLoading(false);
	}, [error.message, importedObjectDefinitions]);

	return (
		<>
			<ClayModal.Header
				closeButtonAriaLabel={Liferay.Language.get('close')}
			>
				{Liferay.Language.get('object-definitions-failed-to-import')}
			</ClayModal.Header>

			<ClayModal.Body>
				{loading ? (
					<ClayLoadingIndicator displayType="secondary" size="sm" />
				) : (
					<Table
						columnsVisibility={false}
						headingNoWrap
						noWrap
						responsive
						striped={false}
					>
						<Head items={tableHeaderItems}>
							{

								// @ts-ignore

								(column) => (
									<Cell expanded key={column.id}>
										{column.name}
									</Cell>
								)
							}
						</Head>

						<Body items={importedObjectDefinitionsStatus}>
							{

								// @ts-ignore

								({errorType, label, success}) => (
									<Row>
										<Cell>
											<div className="lfr-object__modal-import-failed-table-body">
												<Text>{label}</Text>
											</div>
										</Cell>

										<Cell truncate wrap>
											<div className="lfr-object__modal-import-failed-table-body-status">
												<ClayIcon
													color={
														success
															? '#287D3C'
															: '#DA1414'
													}
													symbol={
														success
															? 'check'
															: 'exclamation-full'
													}
												/>

												<Text
													color={
														success
															? 'success'
															: 'danger'
													}
												>
													{success
														? Liferay.Language.get(
																'import-succeeded'
															)
														: errorType
															? errorsUtils
																	.ERRORS[
																	errorType
																]
															: Liferay.Language.get(
																	'failed-to-import'
																)}
												</Text>
											</div>
										</Cell>
									</Row>
								)
							}
						</Body>
					</Table>
				)}
			</ClayModal.Body>

			<ClayModal.Footer
				last={
					<ClayButton
						displayType="warning"
						onClick={() => handleOnclose()}
					>
						{Liferay.Language.get('done')}
					</ClayButton>
				}
			/>
		</>
	);
}
