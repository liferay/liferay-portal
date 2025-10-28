/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayLoadingIndicator from '@clayui/loading-indicator';
import ClayModal, {useModal} from '@clayui/modal';
import {navigate} from 'frontend-js-web';
import PropTypes from 'prop-types';
import React, {useCallback, useEffect, useRef, useState} from 'react';

import {
	CLOSE_MODAL,
	IS_LOADING_MODAL,
	OPEN_MODAL,
} from '../utils/eventsDefinitions';
import {isPageInIframe} from '../utils/iframes';
import {INITIAL_MODAL_SIZE} from '../utils/modals/constants';
import {resolveModalHeight} from '../utils/modals/resolveModalHeight';

function Modal({
	disableHeader: disableHeaderProp,
	id,
	onClose: onCloseProp,
	status,
	title: titleProp,
	url: urlProp,
}) {
	const [visible, setVisible] = useState(false);
	const [loading, setLoading] = useState(false);
	const [onClose, setOnClose] = useState(null);
	const [title, setTitle] = useState(titleProp);
	const [url, setURL] = useState(urlProp);
	const [size, setSize] = useState(INITIAL_MODAL_SIZE);
	const [disableHeader, setDisableHeader] = useState(
		disableHeaderProp || true
	);

	const iframeRef = useRef(null);

	const doClose = useCallback(
		(successNotification) => {
			if (onClose) {
				onClose(successNotification);
			}
			else if (onCloseProp) {
				onCloseProp(successNotification);
			}

			setLoading(false);
			setVisible(false);
		},
		[onClose, onCloseProp]
	);

	const {observer, onClose: closeOnIframeRefresh} = useModal({
		onClose: doClose,
	});

	useEffect(() => {
		function handleOpenEvent(data) {
			if (id !== data.id || visible || isPageInIframe()) {
				return;
			}

			setLoading(true);
			setVisible(true);

			if (data.url) {
				setURL(data.url);
			}

			if (data.onClose) {
				setOnClose(() => data.onClose);
			}

			if (data.title) {
				setTitle(data.title);
			}

			setDisableHeader(data.disableHeader ?? true);

			setSize(data.size || INITIAL_MODAL_SIZE);
		}

		function handleCloseModal({
			redirectURL = '',
			successNotification = {},
			willIframeRefresh = true,
		}) {
			if (!visible) {
				return;
			}

			if (redirectURL) {
				navigate(redirectURL);
			}
			else if (willIframeRefresh) {
				closeOnIframeRefresh(successNotification);
			}
			else {
				doClose(successNotification);
			}
		}

		function handleSetLoading(data) {
			const {isLoading} = data;

			setLoading(isLoading || false);
		}

		function cleanUpListeners() {
			Liferay.detach(OPEN_MODAL, handleOpenEvent);
			Liferay.detach(CLOSE_MODAL, handleCloseModal);
			Liferay.detach(IS_LOADING_MODAL, handleSetLoading);
			Liferay.detach('destroyPortlet', cleanUpListeners);

			iframeRef.current?.removeEventListener('load', handleIFrameLoad);
		}

		if (Liferay.on) {
			Liferay.on(OPEN_MODAL, handleOpenEvent);
			Liferay.on(CLOSE_MODAL, handleCloseModal);
			Liferay.on(IS_LOADING_MODAL, handleSetLoading);
			Liferay.on('destroyPortlet', cleanUpListeners);
		}

		function handleIFrameLoad() {
			setLoading(false);
		}

		iframeRef.current?.addEventListener('load', handleIFrameLoad);

		return () => cleanUpListeners();
	}, [id, closeOnIframeRefresh, visible, doClose]);

	useEffect(() => {
		setOnClose(() => onClose);
	}, [onClose]);

	return (
		<>
			{visible && (
				<ClayModal
					className="clay-modal fds-modal"
					observer={observer}
					size={size}
					status={status}
				>
					{!disableHeader && (
						<ClayModal.Header
							closeButtonAriaLabel={Liferay.Language.get('close')}
							withTitle={!disableHeader}
						>
							{title}
						</ClayModal.Header>
					)}

					<div
						className="fds-modal-body modal-body modal-body-iframe"
						style={{
							height: resolveModalHeight(size),
							maxHeight: '100%',
						}}
					>
						<iframe ref={iframeRef} src={url} title={title} />

						{loading && (
							<div className="loader-container">
								<ClayLoadingIndicator />
							</div>
						)}
					</div>
				</ClayModal>
			)}
		</>
	);
}

Modal.propTypes = {
	closeOnSubmit: PropTypes.bool,
	disableHeader: PropTypes.bool,
	id: PropTypes.oneOfType([PropTypes.number, PropTypes.string]).isRequired,
	onClose: PropTypes.func,
	status: PropTypes.string,
	title: PropTypes.string,
	url: PropTypes.string,
};

export default Modal;
