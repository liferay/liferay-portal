/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import ClayLoadingIndicator from '@clayui/loading-indicator';
import ClayModal, {useModal} from '@clayui/modal';
import classNames from 'classnames';
import {navigate} from 'frontend-js-web';
import PropTypes from 'prop-types';
import React, {useCallback, useEffect, useRef, useState} from 'react';

import Iframe, {IframeOnOpen} from './Iframe';
import StatusMessage from './StatusMessage';

import './Modal.scss';

import type {EventHandler} from '../types';

export interface ModalButtonOnClick {
	(args: {processClose: () => void}): void;
}

export interface ModalContainerProps {
	className?: string;
}

export type ModalSize = 'full-screen' | 'lg' | 'md' | 'sm';

export type ModalStatus = 'danger' | 'info' | 'success' | 'warning';

export interface ModalProps {
	bodyComponent?: any; // TODO: what type is this????
	bodyHTML?: string;
	buttons?: {
		autoFocus?: boolean;
		displayType?:
			| 'danger'
			| 'info'
			| 'link'
			| 'primary'
			| 'secondary'
			| 'success'
			| 'unstyled'
			| 'warning'
			| null;
		formId?: string;
		id?: string;
		label?: string;
		onClick?: ModalButtonOnClick;
		type?: 'cancel' | 'submit';
	}[];
	center?: boolean;
	className?: string;
	containerProps?: ModalContainerProps;
	contentComponent?:
		| typeof React.Component
		| (({closeModal}: {closeModal: () => void}) => React.JSX.Element);
	customEvents?: {
		name: string;
		onEvent: EventHandler;
	}[];
	disableAutoClose?: boolean;
	disableHeader?: boolean;
	footerCssClass?: string;
	headerCssClass?: string;
	headerHTML?: string;
	height?: string;
	id?: string;
	iframeBodyCssClass?: string;
	iframeProps?: {
		[prop: string]: string;
	};
	onClose?: EventHandler;
	onOpen?: IframeOnOpen;
	role?: string;
	size?: ModalSize;
	status?: ModalStatus;
	title?: string;
	url?: URL | string;
	zIndex?: number;
}

export default function Modal({
	bodyComponent,
	bodyHTML,
	buttons,
	center,
	className,
	containerProps = {
		className: 'cadmin',
	},
	contentComponent: ContentComponent,
	customEvents,
	disableAutoClose,
	disableHeader,
	footerCssClass,
	headerCssClass,
	headerHTML,
	height,
	id,
	iframeBodyCssClass,
	iframeProps = {},
	onClose,
	onOpen,
	role = 'dialog',
	size,
	status,
	title,
	url,
	zIndex,
}: ModalProps) {
	const [loading, setLoading] = useState(true);

	const {observer, onOpenChange, open} = useModal({

		/* eslint-disable-next-line @typescript-eslint/no-use-before-define */
		onClose: () => processClose(),
	});

	useEffect(() => {
		onOpenChange(true);
	}, [onOpenChange]);

	const eventHandlersRef = useRef<any[]>([]);

	const processClose = useCallback(() => {
		if (!open) {
			return;
		}

		onOpenChange(false);

		document.body.classList.remove('modal-open');

		const eventHandlers = eventHandlersRef.current;

		eventHandlers.forEach((eventHandler) => {
			eventHandler.detach();
		});

		eventHandlers.splice(0, eventHandlers.length);

		if (onClose) {
			onClose();
		}
	}, [eventHandlersRef, onClose, onOpenChange, open]);

	const onButtonClick = ({
		formId,
		onClick,
		type,
	}: {
		formId?: string;
		onClick?: ModalButtonOnClick;
		type?: 'cancel' | 'submit';
	}) => {
		const submitForm = (form: HTMLFormElement) => {
			if (form.requestSubmit) {
				form.requestSubmit();
			}
			else {
				const accepted = form.dispatchEvent(
					new Event('submit', {cancelable: true})
				);

				if (accepted) {
					form.submit();
				}
			}
		};

		if (type === 'cancel') {
			processClose();
		}
		else if (url && type === 'submit') {
			const iframe: HTMLIFrameElement | null = document.querySelector(
				'.liferay-modal iframe'
			);

			if (iframe) {
				const iframeDocument = iframe.contentWindow?.document;

				const forms = iframeDocument?.querySelectorAll('form');

				if (
					forms?.length !== 1 &&
					process.env.NODE_ENV === 'development'
				) {
					console.warn('There should be one form within a modal.');
				}

				if (formId) {
					const form = iframeDocument?.getElementById(
						formId
					) as HTMLFormElement | null;

					if (form) {
						submitForm(form);
					}
				}
				else if (forms && forms.length >= 1) {
					submitForm(forms[0]);
				}
			}
		}

		if (onClick) {
			onClick({processClose});
		}
	};

	const Body = ({
		component: BodyComponent,
		html,
	}: {
		component?: typeof React.Component;
		html?: string;
	}) => {

		/* eslint-disable-next-line react-compiler/react-compiler */
		const bodyRef = useRef<HTMLDivElement>(null);

		/* eslint-disable-next-line react-compiler/react-compiler */
		useEffect(() => {
			if (html) {
				const fragment = document
					.createRange()
					.createContextualFragment(html);

				if (bodyRef.current) {
					bodyRef.current.innerHTML = '';

					bodyRef.current.appendChild(fragment);
				}
			}

			if (onOpen) {
				onOpen({container: bodyRef.current ?? undefined, processClose});
			}
		}, [html]);

		return (
			<div className="liferay-modal-body" ref={bodyRef}>
				{BodyComponent && <BodyComponent closeModal={processClose} />}
			</div>
		);
	};

	useEffect(() => {
		const eventHandlers = eventHandlersRef.current;

		if (customEvents) {
			customEvents.forEach((customEvent) => {
				if (customEvent.name && customEvent.onEvent) {
					const eventHandler = Liferay.on(
						customEvent.name,
						(event) => {
							customEvent.onEvent(event);
						}
					);

					eventHandlers.push(eventHandler);
				}
			});
		}

		const closeEventHandler = Liferay.on('closeModal', (event) => {
			if (event.id && id && event.id !== id) {
				return;
			}

			processClose();

			if (event.redirect) {
				navigate(event.redirect);
			}
		});

		eventHandlers.push(closeEventHandler);

		return () => {
			eventHandlers.forEach((eventHandler) => {
				eventHandler.detach();
			});

			eventHandlers.splice(0, eventHandlers.length);
		};
	}, [customEvents, eventHandlersRef, id, onClose, onOpen, processClose]);

	return (
		<>
			{open && (
				<ClayModal
					center={center}
					className={classNames('liferay-modal', className)}
					containerProps={{...containerProps}}
					disableAutoClose={disableAutoClose}
					id={id}
					observer={observer}
					role={role}

					// @ts-ignore

					size={url && !size ? 'full-screen' : size}
					status={status}
					zIndex={zIndex}
				>
					{ContentComponent ? (
						<ContentComponent closeModal={processClose} />
					) : (
						<>
							{!disableHeader && (
								<ClayModal.Header
									className={headerCssClass}
									closeButtonAriaLabel={Liferay.Language.get(
										'close'
									)}
								>
									{headerHTML ? (
										<div
											dangerouslySetInnerHTML={{
												__html: headerHTML,
											}}
										></div>
									) : (
										title
									)}

									<span className="sr-only">
										{loading
											? `- ${Liferay.Language.get('loading')}`
											: ''}
									</span>
								</ClayModal.Header>
							)}

							<div
								className={classNames('modal-body', {
									'modal-body-iframe': url,
								})}
								style={{
									height,
								}}
							>
								{url && (
									<>
										{loading && <ClayLoadingIndicator />}

										<StatusMessage loading={loading} />

										<Iframe
											iframeBodyCssClass={
												iframeBodyCssClass
											}
											iframeProps={{
												id:
													(id && `${id}_iframe_`) ||
													'modalIframe',
												...iframeProps,
											}}
											onOpen={onOpen}
											processClose={processClose}
											title={title}
											updateLoading={(loading) => {
												setLoading(loading);
											}}
											url={url}
										/>
									</>
								)}

								{bodyHTML && <Body html={bodyHTML} />}

								{bodyComponent && (
									<Body component={bodyComponent} />
								)}
							</div>

							{buttons && (
								<ClayModal.Footer
									className={footerCssClass}
									last={
										<ClayButton.Group spaced>
											{buttons.map(
												(
													{
														displayType,
														formId,
														id,
														label,
														onClick,
														type,
														...otherProps
													},
													index
												) => (
													<ClayButton
														displayType={
															displayType
														}
														id={id}
														key={index}
														onClick={() => {
															onButtonClick({
																formId,
																onClick,
																type,
															});
														}}
														type={
															type === 'cancel'
																? 'button'
																: type
														}
														{...otherProps}
													>
														{label}
													</ClayButton>
												)
											)}
										</ClayButton.Group>
									}
								/>
							)}
						</>
					)}
				</ClayModal>
			)}
		</>
	);
}

Modal.propTypes = {
	bodyHTML: PropTypes.string,
	buttons: PropTypes.arrayOf(
		PropTypes.shape({
			displayType: PropTypes.oneOf([
				'danger',
				'info',
				'link',
				null,
				'primary',
				'secondary',
				'success',
				'unstyled',
				'warning',
			]),
			formId: PropTypes.string,
			id: PropTypes.string,
			label: PropTypes.string,
			onClick: PropTypes.func,
			type: PropTypes.oneOf(['cancel', 'submit']),
		})
	),
	center: PropTypes.bool,
	containerProps: PropTypes.object,
	contentComponent: PropTypes.elementType,
	customEvents: PropTypes.arrayOf(
		PropTypes.shape({
			name: PropTypes.string,
			onEvent: PropTypes.func,
		})
	),
	disableHeader: PropTypes.bool,
	headerHTML: PropTypes.string,
	height: PropTypes.string,
	id: PropTypes.string,
	iframeProps: PropTypes.object,
	onClose: PropTypes.func,
	onOpen: PropTypes.func,
	role: PropTypes.string,
	size: PropTypes.oneOf(['full-screen', 'lg', 'md', 'sm']),
	status: PropTypes.string,
	title: PropTypes.string,
	url: PropTypes.string,
};
