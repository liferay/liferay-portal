/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayLoadingIndicator from '@clayui/loading-indicator';
import ClayModal, {useModal} from '@clayui/modal';
import PropTypes from 'prop-types';
import React, {useEffect, useState} from 'react';

import {
	CLOSE_MODAL,
	IS_LOADING_MODAL,
	OPEN_MODAL,
} from '../../utilities/eventsDefinitions';
import {liferayNavigate} from '../../utilities/index';
import {INITIAL_MODAL_SIZE} from '../../utilities/modals/constants';
import {resolveModalHeight} from '../../utilities/modals/index';

const isPageInIframe = () => window.location !== window.parent.location;

function Modal(props) {
	const [visible, setVisible] = useState(false);
	const [loading, setLoading] = useState(false);
	const [onClose, setOnClose] = useState(null);
	const [title, setTitle] = useState(props.title);
	const [url, setUrl] = useState(props.url);
	const [size, setSize] = useState(INITIAL_MODAL_SIZE);
	const [addToCart, setAddToCart] = useState(false);

	const {observer, onClose: close} = useModal({
		onClose: (notification) => {
			if (onClose) {
				onClose(notification);
			}

			setLoading(false);
			setVisible(false);
		},
	});

	useEffect(() => {
		function handleOpenEvent(data) {
			if (props.id !== data.id || visible || isPageInIframe()) {
				return;
			}

			setLoading(true);
			setVisible(true);

			if (data.url) {
				setUrl(data.url);
			}

			if (data.onClose) {
				setOnClose(() => data.onClose);
			}

			if (data.title) {
				setTitle(data.title);
			}

			if (data.size) {
				setSize(data.size);
			}

			if (data.addToCart) {
				setAddToCart(true);
			}
		}

		function handleCloseModal({
			redirectURL = '',
			successNotification = {},
		}) {
			if (!visible) {
				return;
			}

			if (redirectURL) {
				liferayNavigate(redirectURL);
			}
			else {
				close(successNotification);
			}

			if (addToCart) {
				setAddToCart(false);
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
		}

		if (Liferay.on) {
			Liferay.on(OPEN_MODAL, handleOpenEvent);
			Liferay.on(CLOSE_MODAL, handleCloseModal);
			Liferay.on(IS_LOADING_MODAL, handleSetLoading);
			Liferay.on('destroyPortlet', cleanUpListeners);
		}

		return () => cleanUpListeners();
	}, [addToCart, close, props.id, visible]);

	useEffect(() => {
		setOnClose(() => props.onClose);
	}, [props.onClose]);

	return (
		<>
			{visible && (
				<ClayModal
					className="commerce-modal"
					observer={observer}
					size={size}
					status={props.status}
				>
					{title && (
						<ClayModal.Header
							closeButtonAriaLabel={Liferay.Language.get('close')}
						>
							{title}
						</ClayModal.Header>
					)}

					<div
						className="modal-body modal-body-iframe"
						style={{
							height: resolveModalHeight(size),
							maxHeight: '100%',
						}}
					>
						<iframe
							data-add-to-cart={addToCart}
							onLoad={() => setLoading(false)}
							src={url}
							title={title}
						/>

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
	id: PropTypes.string.isRequired,
	onClose: PropTypes.func,
	portletId: PropTypes.string,
	size: PropTypes.string,
	status: PropTypes.string,
	title: PropTypes.string,
	url: PropTypes.string,
};

export default Modal;
