/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import classNames from 'classnames';
import {sub} from 'frontend-js-web';
import React, {
	useCallback,
	useContext,
	useEffect,
	useRef,
	useState,
} from 'react';

import {
	IRoomContext,
	IRoomStepProps,
	ISiteTemplate,
} from '../common/utils/types';
import {RoomContext} from './RoomInitializer';

function RoomSelectTemplateStep({
	numberOfSteps,
	setHandleStepSubmit,
	siteTemplates = [],
	step = 2,
}: IRoomStepProps) {
	const {dataContext, loading, setDataContext} =
		useContext<IRoomContext>(RoomContext);
	const [currentTemplate, setCurrentTemplate] =
		useState<ISiteTemplate | null>(null);
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
		(siteTemplate: ISiteTemplate) => {
			setCurrentTemplate(siteTemplate);
			setDataContext((prevState) => ({
				...prevState,
				templateKey: siteTemplate.uuid,
			}));
		},
		[setDataContext]
	);

	useEffect(() => {
		if (dataContext.templateKey && siteTemplates?.length) {
			setCurrentTemplate(
				siteTemplates.find(
					(siteTemplate) =>
						siteTemplate.uuid === dataContext.templateKey
				) || null
			);
		}
	}, [dataContext, siteTemplates]);

	useEffect(() => {
		setHandleStepSubmit(() => async (event: Event): Promise<boolean> => {
			event.preventDefault();

			if (dataContext.templateKey) {
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

							{siteTemplates.map((siteTemplate) => {
								return (
									<li
										className={classNames(
											'list-group-item list-group-item-action',
											{
												active:
													siteTemplate.uuid ===
													currentTemplate?.uuid,
											}
										)}
										data-qa-id={`template_${siteTemplate.uuid}`}
										key={`template_${siteTemplate.uuid}`}
										onClick={() => {
											if (!loading) {
												handleChange(siteTemplate);
											}
										}}
									>
										<div
											className="text-weight-semi-bold"
											data-qa-id={`templateName_${siteTemplate.uuid}`}
										>
											{Liferay.Util.escapeHTML(
												siteTemplate.name || ''
											)}
										</div>

										<div
											className="text-2 text-truncate"
											data-qa-id={`templateDescription_${siteTemplate.uuid}`}
										>
											{Liferay.Util.escapeHTML(
												siteTemplate.description || ''
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
								src={`${currentTemplate.friendlyURL}?preview=true`}
							></iframe>
							<div className="iframe-wrapper"></div>
						</>
					)}
				</div>
			</div>
		</div>
	);
}

export default RoomSelectTemplateStep;
