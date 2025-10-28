/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayAlert from '@clayui/alert';
import ClayButton, {ClayButtonWithIcon} from '@clayui/button';
import ClayIcon from '@clayui/icon';
import ClayLoadingIndicator from '@clayui/loading-indicator';
import {ClayTooltipProvider} from '@clayui/tooltip';
import classnames from 'classnames';
import {navigate} from 'frontend-js-web';
import PropTypes from 'prop-types';
import React, {useCallback, useEffect, useState} from 'react';

import DownloadSpreadsheetModal from './DownloadSpreadsheetModal';
import {downloadFileFromBlob, fetchFile} from './downloadSpreadsheetUtils';

const initialToastState = {
	content: null,
	show: false,
	type: null,
};

const initialFeedbackState = {
	content: null,
	show: false,
	type: null,
};

let fetchController = null;

const DEFAULT_TIMEOUT_DELAY = 500;
const FEEDBACK_TIMEOUT_DELAY = 2000;

const DownloadSpreadsheetButton = ({fileURL, total}) => {
	const [loading, setLoading] = useState(false);
	const [toastMessage, setToastMessage] = useState(initialToastState);
	const [feedbackStatus, setFeedbackStatus] = useState(initialFeedbackState);
	const [showPendingRequestModal, setShowPendingRequestModal] =
		useState(false);
	const [tentativeNavigationPath, setTentativeNavigationPath] = useState('');
	const [cancelAndLeaveButtonText, setCancelAndLeaveButtonText] = useState(
		Liferay.Language.get('cancel-and-leave')
	);
	const [disableCancelAndLeaveButton, setDisableCancelAndLeaveButton] =
		useState(false);

	let defaultDelayTimeout = null;
	let feedbackDelayTimeout = null;

	const handleSuccess = () => {
		setToastMessage((current) => ({
			...current,
			content: Liferay.Language.get('xls-was-successfully-generated'),
			type: 'success',
		}));

		setFeedbackStatus((current) => ({
			...current,
			content: Liferay.Language.get('xls-generated'),
			type: 'success',
		}));
	};

	const handleError = ({abortedRequest, error}) => {
		const type = abortedRequest ? 'warning' : 'danger';

		const toastContent = abortedRequest
			? Liferay.Language.get('xls-generation-was-cancelled')
			: `${Liferay.Language.get(
					'xls-generation-failed.-try-again'
				)}. ${error}`;

		const feedbackContent = abortedRequest
			? Liferay.Language.get('xls-generation-was-cancelled')
			: Liferay.Language.get('an-error-occurred');

		setToastMessage((current) => ({
			...current,
			content: toastContent,
			type,
		}));

		setFeedbackStatus((current) => ({
			...current,
			content: feedbackContent,
			type,
		}));
	};

	const handleUIState = () => {
		defaultDelayTimeout = setTimeout(() => {
			setLoading(false);

			setFeedbackStatus((current) => ({
				...current,
				show: true,
			}));

			setToastMessage((current) => ({
				...current,
				show: true,
			}));
		}, DEFAULT_TIMEOUT_DELAY);

		feedbackDelayTimeout = setTimeout(() => {
			setFeedbackStatus(initialFeedbackState);
		}, FEEDBACK_TIMEOUT_DELAY);
	};

	const buttonTextKey = loading
		? Liferay.Language.get('generating-xls')
		: feedbackStatus.show
			? feedbackStatus.content
			: Liferay.Language.get('export-xls');

	const clayIconSymbol =
		feedbackStatus.type === 'success'
			? 'check-circle'
			: feedbackStatus.type === 'danger'
				? 'exclamation-circle'
				: feedbackStatus.type === 'warning'
					? 'warning'
					: 'download';

	const disabledGenerateButton =
		loading || feedbackStatus.show || !parseInt(total, 10);

	const handleClick = async () => {
		setLoading(true);

		try {
			fetchController = new AbortController();
			const blob = await fetchFile({
				controller: fetchController,
				url: fileURL,
			});

			downloadFileFromBlob(blob);

			handleSuccess();
		}
		catch (error) {
			const abortedRequest = error.name === 'AbortError' ? true : false;

			handleError({abortedRequest, error});
		}
		finally {
			handleUIState();
		}
	};

	const handleCancelRequest = () => {
		fetchController.abort();
	};

	const handleToastClose = () => {
		setToastMessage(initialToastState);
	};

	const secondaryClickModalHandler = useCallback(
		(onClose) => {
			setCancelAndLeaveButtonText(Liferay.Language.get('cancelling'));
			setDisableCancelAndLeaveButton(true);
			handleCancelRequest();

			setTimeout(() => {
				onClose();
				navigate(tentativeNavigationPath);
			}, DEFAULT_TIMEOUT_DELAY + FEEDBACK_TIMEOUT_DELAY);
		},
		[tentativeNavigationPath]
	);

	const preventHardReloadWhileGenerating = useCallback(
		(event) => {
			if (!loading) {
				return;
			}
			event.preventDefault();
			event.returnValue = '';
		},
		[loading]
	);

	useEffect(() => {
		const navigationEventHandler = Liferay.on('beforeNavigate', (event) => {
			if (!loading) {
				return;
			}

			Liferay.fire('closeApplicationsMenu');
			setShowPendingRequestModal(true);
			setTentativeNavigationPath(event.path);
			event.originalEvent.preventDefault();
		});

		window.addEventListener(
			'beforeunload',
			preventHardReloadWhileGenerating
		);

		return () => {
			clearTimeout(defaultDelayTimeout);
			clearTimeout(feedbackDelayTimeout);
			navigationEventHandler.detach();
			window.removeEventListener(
				'beforeunload',
				preventHardReloadWhileGenerating
			);
		};
	}, [
		defaultDelayTimeout,
		feedbackDelayTimeout,
		loading,
		preventHardReloadWhileGenerating,
	]);

	return (
		<>
			<ClayTooltipProvider>
				<ClayButton
					aria-label={Liferay.Language.get(
						'download-your-data-as-an-xls-file'
					)}
					borderless
					className={classnames('download-spreadsheet-button', {
						'download-spreadsheet-button--loading': loading,
						'text-danger':
							feedbackStatus.show &&
							feedbackStatus.type === 'danger',
						'text-success':
							feedbackStatus.show &&
							feedbackStatus.type === 'success',
						'text-warning':
							feedbackStatus.show &&
							feedbackStatus.type === 'warning',
					})}
					data-tooltip-align="top"
					disabled={disabledGenerateButton}
					displayType="secondary"
					onClick={handleClick}
					title={Liferay.Language.get(
						'download-your-data-as-an-xls-file'
					)}
				>
					<span className="inline-item inline-item-before">
						{loading ? (
							<ClayLoadingIndicator small />
						) : (
							<ClayIcon symbol={clayIconSymbol} />
						)}
					</span>

					{buttonTextKey}
				</ClayButton>
			</ClayTooltipProvider>

			{loading && (
				<ClayTooltipProvider>
					<ClayButtonWithIcon
						aria-label={Liferay.Language.get('cancel-export')}
						borderless
						className="c-ml-2"
						data-tooltip-align="top"
						displayType="secondary"
						onClick={handleCancelRequest}
						symbol="times-circle"
						title={Liferay.Language.get('cancel-export')}
					/>
				</ClayTooltipProvider>
			)}

			{toastMessage.show && (
				<ClayAlert.ToastContainer>
					<ClayAlert
						autoClose={5000}
						className="download-spreadsheet-button__alert"
						closeButtonAriaLabel={Liferay.Language.get('close')}
						displayType={toastMessage.type}
						onClose={handleToastClose}
					>
						{toastMessage.content}
					</ClayAlert>
				</ClayAlert.ToastContainer>
			)}

			<DownloadSpreadsheetModal
				disableSecondaryButton={disableCancelAndLeaveButton}
				secondaryButtonClickCallback={secondaryClickModalHandler}
				secondaryButtonText={cancelAndLeaveButtonText}
				setVisibilityCallback={setShowPendingRequestModal}
				show={showPendingRequestModal}
			/>
		</>
	);
};

DownloadSpreadsheetButton.propTypes = {
	fileURL: PropTypes.string.isRequired,
	total: PropTypes.number.isRequired,
};

export default DownloadSpreadsheetButton;
