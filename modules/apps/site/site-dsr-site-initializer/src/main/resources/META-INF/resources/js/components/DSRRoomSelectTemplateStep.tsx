/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import classNames from 'classnames';
import {openToast} from 'frontend-js-components-web';
import {sub} from 'frontend-js-web';
import React, {
	useCallback,
	useContext,
	useEffect,
	useRef,
	useState,
} from 'react';

import ApiHelper from '../commons/ApiHelper';
import {DSRContext} from './DSRInitializer';
import {TDSRContext, TDSRRoomDetailsStepProps} from './DSRTypes';

type TDSRTemplateDTO = {
	banner?: {
		fileBase64?: string;
		fileURL: string;
		id: number;
	};
	clientLogo?: {
		fileBase64?: string;
		fileURL: string;
		id: number;
	};
	clientName: string;
	createDate: string;
	description?: string;
	externalReferenceCode: string;
	friendlyUrlPath: string;
	id: number;
	modifiedDate: string;
	name: string;
	ownerId: number;
	ownerName: string;
	primaryColor?: string;
	secondaryColor?: string;
	usages?: number;
};

export function getImageURL(base64: string) {
	if (base64) {
		return `data:image;base64,${base64}`;
	}

	return '';
}

async function getDigitalSalesRoomTemplates(): Promise<{
	items: TDSRTemplateDTO[];
}> {
	const {data, error} = await ApiHelper.get<{items: TDSRTemplateDTO[]}>(
		'/o/headless-digital-sales-room/v1.0/digital-sales-room-templates'
	);

	if (error) {
		throw new Error(error);
	}

	return data || {items: []};
}

function DSRRoomSelectTemplateStep({
	numberOfSteps,
	setHandleStepSubmit,
	step = 1,
}: TDSRRoomDetailsStepProps) {
	const {dataContext, loading, setDataContext, setLoading} =
		useContext<TDSRContext>(DSRContext);
	const [currentTemplate, setCurrentTemplate] =
		useState<TDSRTemplateDTO | null>(null);
	const [templates, setTemplates] = useState<Array<TDSRTemplateDTO>>([]);
	const modalRef = useRef<HTMLDivElement>(null);

	useEffect(() => {
		if (!modalRef.current) {
			return;
		}

		const modalContainer = modalRef.current.closest(
			'.modal-dialog'
		) as HTMLElement;

		if (!modalContainer) {
			return;
		}

		modalContainer.classList.add(
			'dsr-select-template-modal',
			'modal-full-screen'
		);

		return () =>
			modalContainer.classList.remove(
				'dsr-select-template-modal',
				'modal-full-screen'
			);
	}, []);

	const handleChange = useCallback(
		(template: TDSRTemplateDTO) => {
			setCurrentTemplate(template);
			setDataContext((prevState) => ({
				...prevState,
				banner: {
					base64: getImageURL(template.banner?.fileBase64 || ''),
				},
				clientLogo: {
					base64: getImageURL(template.clientLogo?.fileBase64 || ''),
				},
				primaryColor: (template.primaryColor || '').replace(/^#/, ''),
				secondaryColor: (template.secondaryColor || '').replace(
					/^#/,
					''
				),
				templateId: template.id,
			}));
		},
		[setDataContext]
	);

	useEffect(() => {
		if (setLoading) {
			setLoading(true);
		}

		getDigitalSalesRoomTemplates()
			.then((data) => {
				setTemplates(data.items);
			})
			.catch((error) => {
				openToast({
					message: (error as Error).message,
					type: 'danger',
				});
			})
			.finally(() => {
				if (setLoading) {
					setLoading(false);
				}
			});
	}, [setLoading]);

	useEffect(() => {
		if (dataContext.templateId && templates?.length) {
			setCurrentTemplate(
				templates.find(
					(template) => template.id === dataContext.templateId
				) || null
			);
		}
	}, [dataContext, templates]);

	useEffect(() => {
		setHandleStepSubmit(() => async (event: Event): Promise<boolean> => {
			event.preventDefault();

			if (dataContext.templateId) {
				return Promise.resolve(true);
			}

			return Promise.resolve(false);
		});
	}, [dataContext, setHandleStepSubmit]);

	return (
		<div className="row" ref={modalRef}>
			<div className="col-4 mh-100">
				<div className="d-flex flex-column mh-100 overflow-hidden">
					<div>
						<div
							className="mb-1 text-secondary"
							data-qa-id="stepLocator"
						>
							{sub(
								Liferay.Language.get('step-x-of-x'),
								step,
								numberOfSteps
							)}
						</div>

						<div
							className="mb-1 text-6 text-weight-bold"
							data-qa-id="stepTitle"
						>
							{Liferay.Language.get('choose-a-template')}
						</div>

						<div className="text-secondary">
							{Liferay.Language.get(
								'select-a-template-to-get-started'
							)}
						</div>
					</div>

					<div className="flex-fill mt-4 overflow-auto">
						<ul className="list-group">
							<li className="list-group-header">
								<h3
									className="list-group-header-title"
									data-qa-id="savedTemplates"
								>
									{Liferay.Language.get('saved-templates')}
								</h3>
							</li>

							{templates.map((template) => {
								return (
									<li
										className={classNames(
											'list-group-item list-group-item-action',
											{
												active:
													template.id ===
													currentTemplate?.id,
											}
										)}
										data-qa-id={`template_${template.id}`}
										key={`template_${template.id}`}
										onClick={() => {
											if (!loading) {
												handleChange(template);
											}
										}}
									>
										<div
											className="text-weight-semi-bold"
											data-qa-id={`templateName_${template.id}`}
										>
											{Liferay.Util.escapeHTML(
												template.name
											)}
										</div>

										<div
											className="text-2 text-truncate"
											data-qa-id={`templateDescription_${template.id}`}
										>
											{Liferay.Util.escapeHTML(
												template.description || ''
											)}
										</div>
									</li>
								);
							})}
						</ul>
					</div>
				</div>
			</div>

			<div className="col-8">
				<div
					className="dsr-template-preview"
					data-qa-id="templatePreview"
				>
					{currentTemplate && (
						<>
							<iframe
								data-qa-id="templatePreviewFrame"
								scrolling="no"
								src={`/web${currentTemplate.friendlyUrlPath}?preview=true`}
							></iframe>
							<div className="iframe-wrapper"></div>
						</>
					)}
				</div>
			</div>
		</div>
	);
}

export default DSRRoomSelectTemplateStep;
