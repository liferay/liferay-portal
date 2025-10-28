/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayAlert from '@clayui/alert';
import ClayButton from '@clayui/button';
import {Body, Cell, Head, Row, Table, Text} from '@clayui/core';
import ClayLoadingIndicator from '@clayui/loading-indicator';
import ClayModal from '@clayui/modal';
import {stringUtils} from '@liferay/object-js-components-web';
import classNames from 'classnames';
import React from 'react';

import {
	modalImportWarningBodyTexts,
	modalImportWarningTitle,
} from './modalImportLanguageUtil';

import './ModalImportWarning.scss';

interface ModalImportWarningProps {
	errorMessage: string;
	existingObjectDefinitions?: ObjectDefinition[];
	handleImport: () => void;
	handleOnClose: () => void;
	importLoading: boolean;
	modalImportKey: string;
}

const tableHeaderItems = [
	{
		id: 'objectDefinition',
		name: Liferay.Language.get('object-definition'),
	},
];

export function ModalImportWarning({
	errorMessage,
	existingObjectDefinitions,
	handleImport,
	handleOnClose,
	importLoading,
	modalImportKey,
}: ModalImportWarningProps) {
	return (
		<>
			<ClayModal.Header
				closeButtonAriaLabel={Liferay.Language.get('close')}
			>
				{modalImportWarningTitle[modalImportKey]}
			</ClayModal.Header>

			<ClayModal.Body>
				{errorMessage && (
					<ClayAlert displayType="danger">{errorMessage}</ClayAlert>
				)}

				<div className="text-secondary">
					{modalImportWarningBodyTexts.map(
						(modalImportWarningBodyText, index) => {
							return (
								<Text as="p" color="secondary" key={index}>
									{modalImportWarningBodyText[modalImportKey]}
								</Text>
							);
						}
					)}

					{Liferay.FeatureFlags['LPD-34594'] &&
						!!existingObjectDefinitions?.length && (
							<>
								<Table
									columnsVisibility={false}
									headingNoWrap
									noWrap
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

									<Body
										defaultItems={existingObjectDefinitions}
									>
										{

											// @ts-ignore

											(objectDefinition) => (
												<Row>
													<Cell>
														{stringUtils.getLocalizableLabel(
															{
																fallbackLabel:
																	objectDefinition.name,
																fallbackLanguageId:
																	objectDefinition.defaultLanguageId,
																labels: objectDefinition.label,
															}
														)}
													</Cell>
												</Row>
											)
										}
									</Body>
								</Table>

								<Text as="p" color="secondary">
									{Liferay.Language.get(
										'before-importing-the-new-object-definition-you-may-want-to-back-up-its-entries-to-prevent-data-loss'
									)}
								</Text>
							</>
						)}

					<Text color="secondary">
						{Liferay.Language.get(
							'do-you-want-to-proceed-with-the-import-process'
						)}
					</Text>
				</div>
			</ClayModal.Body>

			<ClayModal.Footer
				last={
					<ClayButton.Group spaced>
						<ClayButton
							displayType="secondary"
							onClick={handleOnClose}
						>
							{Liferay.Language.get('cancel')}
						</ClayButton>

						<ClayButton
							className={classNames({
								'lfr-object__modal-import-warning-loading-button':
									importLoading,
							})}
							disabled={errorMessage !== '' || importLoading}
							displayType="warning"
							onClick={() => {
								handleImport();
							}}
							type="button"
						>
							{importLoading ? (
								<ClayLoadingIndicator
									displayType="light"
									size="sm"
								/>
							) : (
								Liferay.Language.get('continue')
							)}
						</ClayButton>
					</ClayButton.Group>
				}
			/>
		</>
	);
}
