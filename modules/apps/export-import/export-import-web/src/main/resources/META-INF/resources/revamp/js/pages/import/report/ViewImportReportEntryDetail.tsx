/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import ClayLayout from '@clayui/layout';
import ClayLink from '@clayui/link';
import ClayLoadingIndicator from '@clayui/loading-indicator';
import {openModal} from 'frontend-js-components-web';
import {escapeHTML, fetch} from 'frontend-js-web';
import React, {useEffect, useState} from 'react';

import formatDate from '../../../utils/formatDate';

function DetailViewDefinitionCol({
	body,
	title,
	...colProps
}: {
	body: React.ReactNode;
	title?: string;
} & React.ComponentProps<typeof ClayLayout.Col>): JSX.Element {
	return (
		<ClayLayout.Col {...colProps}>
			{title && (
				<div>
					<strong>{title}</strong>
				</div>
			)}

			<div className="sheet-text">{body}</div>
		</ClayLayout.Col>
	);
}

function openStackTraceModal(stackTraceMessage?: string) {
	const modalProps = stackTraceMessage
		? {
				bodyHTML: `<div class="bg-dark border border-light p-4 rounded"><p class="text-white">${escapeHTML(stackTraceMessage)}</p></div>`,
				size: 'full-screen' as const,
				title: Liferay.Language.get('stack-trace'),
			}
		: {
				bodyHTML: Liferay.Language.get(
					'this-error-was-detected-as-a-controlled-validation-error-during-the-import-process-and-did-not-originate-from-a-system-exception.-because-of-this-no-stack-trace-was-generated'
				),
				status: 'info' as const,
				title: Liferay.Language.get('no-stack-trace-available'),
			};

	openModal({
		...modalProps,
		buttons: [
			{
				displayType: 'secondary',
				label: Liferay.Language.get('close'),
				onClick: ({processClose}: {processClose: Function}) => {
					processClose();
				},
			},
		],
	});
}

interface ReportEntryDetail {
	classExternalReferenceCode: string;
	classPK: number;
	creator: {
		name: string;
	};
	dateCreated: string;
	dateModified: string;
	errorMessage: string;
	errorStacktrace: string;
	id: number;
	modelName: string;
	scope: {
		externalReferenceCode: string;
		key: string;
		label: string;
		type: 'AssetLibrary' | 'Site' | 'Space';
	};
	type: {
		code: number;
		label: string;
	};
}

export function ViewImportReportEntryDetail({
	apiURL,
	backURL,
}: {
	apiURL: string;
	backURL: string;
}) {
	const [isLoading, setIsLoading] = useState(true);
	const [reportEntryDetail, setReportEntryDetail] =
		useState<ReportEntryDetail>({} as ReportEntryDetail);

	useEffect(() => {
		fetch(apiURL).then((response) => {
			response.json().then((data: ReportEntryDetail) => {
				setReportEntryDetail({
					...data,
					dateCreated:
						data.dateCreated && formatDate(data.dateCreated),
				});
				setIsLoading(false);
			});
		});
	}, [apiURL]);

	const {
		classExternalReferenceCode,
		classPK,
		creator,
		dateCreated,
		errorMessage,
		errorStacktrace,
		id,
		modelName,
		scope,
		type,
	} = reportEntryDetail;

	return (
		<ClayLayout.ContainerFluid>
			{isLoading ? (
				<div className="align-items-center d-flex justify-content-center mt-4">
					<ClayLoadingIndicator />
				</div>
			) : (
				<>
					<ClayLayout.Sheet className="mt-4">
						<ClayLayout.SheetHeader>
							<h2 className="sheet-title">{modelName}</h2>

							<div className="sheet-text">
								{`${dateCreated} · ${creator.name}`}
							</div>
						</ClayLayout.SheetHeader>

						<ClayLayout.SheetSection className="mb-2">
							<span className="sheet-subtitle text-secondary">
								{Liferay.Language.get('report-entry-details')}
							</span>

							<ClayLayout.Row>
								<DetailViewDefinitionCol
									body={modelName}
									md={4}
									title={Liferay.Language.get('entity-type')}
								/>

								<DetailViewDefinitionCol
									body={id}
									md={4}
									title={Liferay.Language.get('id')}
								/>

								<DetailViewDefinitionCol
									body={type.label}
									md={4}
									title={Liferay.Language.get('type')}
								/>
							</ClayLayout.Row>

							<ClayLayout.Row>
								<DetailViewDefinitionCol
									body={
										<textarea
											className="form-control lfr-textarea"
											readOnly
											rows={5}
											value={errorMessage}
										/>
									}
									title={Liferay.Language.get(
										'error-message'
									)}
								/>
							</ClayLayout.Row>

							<ClayLayout.Row>
								<DetailViewDefinitionCol
									body={
										<ClayButton
											displayType="secondary"
											onClick={() =>
												openStackTraceModal(
													errorStacktrace
												)
											}
										>
											{Liferay.Language.get(
												'view-stack-trace'
											)}
										</ClayButton>
									}
								/>
							</ClayLayout.Row>
						</ClayLayout.SheetSection>

						<ClayLayout.SheetSection>
							<span className="sheet-subtitle text-secondary">
								{Liferay.Language.get('failed-event')}
							</span>

							<ClayLayout.Row>
								<DetailViewDefinitionCol
									body={classExternalReferenceCode}
									md={6}
									title={Liferay.Language.get(
										'external-reference-code'
									)}
								/>

								<DetailViewDefinitionCol
									body={classPK}
									md={6}
									title={Liferay.Language.get('id')}
								/>
							</ClayLayout.Row>

							<ClayLayout.Row>
								<DetailViewDefinitionCol
									body={scope ? scope.type : 'Company'}
									md={6}
									title={Liferay.Language.get('scope')}
								/>

								{scope && (
									<DetailViewDefinitionCol
										body={scope.label}
										md={6}
										title={Liferay.Language.get('site')}
									/>
								)}
							</ClayLayout.Row>
						</ClayLayout.SheetSection>
					</ClayLayout.Sheet>
					<ClayLayout.SheetFooter>
						<ClayLink button displayType="secondary" href={backURL}>
							{Liferay.Language.get('back')}
						</ClayLink>
					</ClayLayout.SheetFooter>
				</>
			)}
		</ClayLayout.ContainerFluid>
	);
}
