/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayAlert from '@clayui/alert';
import ClayButton from '@clayui/button';
import ClayModal from '@clayui/modal';
import PropTypes from 'prop-types';
import React from 'react';

import {
	OTHER_REASON_VALUE,
	STATUS_ERROR,
	STATUS_LOGIN,
	STATUS_REPORT,
	STATUS_SUCCESS,
} from '../constants';
import {sub} from '../utils';
import Captcha from './Captcha';

const OTHER_REASON_MAX_LENGTH = 75;

const ModalContentForm = ({
	captchaURI,
	error,
	form = {},
	handleClose,
	namespace,
	handleInputChange,
	handleSubmit,
	isSending,
	pathTermsOfUse,
	reasons,
	selectedReason,
	signedIn,
}) => {
	return (
		<form
			aria-label={Liferay.Language.get('report-inappropriate-content')}
			onSubmit={handleSubmit}
		>
			<ClayModal.Body>
				{error && (
					<ClayAlert
						displayType="danger"
						title={Liferay.Language.get('error')}
					>
						{error}
					</ClayAlert>
				)}

				<p>
					{sub(
						Liferay.Language.get(
							'you-are-about-to-report-a-violation-of-our-x.-all-reports-are-strictly-confidential'
						),
						[
							<a href={pathTermsOfUse} key={pathTermsOfUse}>
								{Liferay.Language.get('terms-of-use')}
							</a>,
						],
						false
					)}
				</p>

				<div className="form-group">
					<label
						className="control-label"
						htmlFor={`${namespace}selectedReason`}
					>
						{Liferay.Language.get('reason-for-the-report')}
					</label>

					<select
						className="form-control"
						id={`${namespace}selectedReason`}
						name="selectedReason"
						onChange={handleInputChange}
						value={selectedReason}
					>
						{Object.entries(reasons).map(([value, text]) => (
							<option key={value} value={value}>
								{text}
							</option>
						))}

						<option value={OTHER_REASON_VALUE}>
							{Liferay.Language.get('other-reason')}
						</option>
					</select>
				</div>

				{selectedReason === OTHER_REASON_VALUE && (
					<div className="form-group">
						<label
							className="control-label"
							htmlFor={`${namespace}otherReason`}
						>
							{Liferay.Language.get('other-reason')}

							{` (${
								OTHER_REASON_MAX_LENGTH -
								(form.otherReason?.length ?? 0)
							} ${Liferay.Language.get('characters')})`}
						</label>

						<input
							autoFocus
							className="form-control"
							id={`${namespace}otherReason`}
							maxLength={OTHER_REASON_MAX_LENGTH}
							name="otherReason"
							onChange={handleInputChange}
							value={form.otherReason}
						/>
					</div>
				)}

				{!signedIn && (
					<div className="form-group">
						<label
							className="control-label"
							htmlFor={`${namespace}otherRreporterEmailAddresseason`}
						>
							{Liferay.Language.get('email-address')}
						</label>

						<input
							className="form-control"
							id={`${namespace}otherRreporterEmailAddresseason`}
							name="reporterEmailAddress"
							onChange={handleInputChange}
							type="email"
						/>
					</div>
				)}

				{captchaURI && <Captcha uri={captchaURI} />}
			</ClayModal.Body>

			<ClayModal.Footer
				last={
					<ClayButton.Group spaced>
						<ClayButton
							displayType="secondary"
							onClick={handleClose}
						>
							{Liferay.Language.get('cancel')}
						</ClayButton>

						<ClayButton
							disabled={isSending}
							displayType="primary"
							type="submit"
						>
							{Liferay.Language.get('report')}
						</ClayButton>
					</ClayButton.Group>
				}
			/>
		</form>
	);
};

const ModalBodySuccess = ({companyName}) => (
	<ClayModal.Body>
		<p>
			<strong>{Liferay.Language.get('thank-you-for-your-report')}</strong>
		</p>

		<p>
			{sub(
				Liferay.Language.get(
					'although-we-cannot-disclose-our-final-decision,-we-do-review-every-report-and-appreciate-your-effort-to-make-sure-x-is-a-safe-environment-for-everyone'
				),
				[companyName]
			)}
		</p>
	</ClayModal.Body>
);

const ModalBodyError = () => (
	<ClayModal.Body>
		<p>
			<strong>
				{Liferay.Language.get(
					'an-error-occurred-while-sending-the-report.-please-try-again-in-a-few-minutes'
				)}
			</strong>
		</p>
	</ClayModal.Body>
);

const ModalBodyLogin = () => (
	<ClayModal.Body>
		<p>
			<strong>
				{Liferay.Language.get(
					'please-sign-in-to-flag-this-as-inappropriate'
				)}
			</strong>
		</p>
	</ClayModal.Body>
);

const ModalBody = ({companyName, status}) => {
	switch (status) {
		case STATUS_LOGIN:
			return <ModalBodyLogin />;

		case STATUS_SUCCESS:
			return <ModalBodySuccess companyName={companyName} />;

		case STATUS_ERROR:
		default:
			return <ModalBodyError />;
	}
};

const FlagsModal = ({
	captchaURI,
	companyName,
	error,
	form,
	handleClose,
	handleInputChange,
	handleSubmit,
	isSending,
	namespace,
	observer,
	pathTermsOfUse,
	reasons,
	selectedReason,
	signedIn,
	status,
}) => {
	return (
		<ClayModal observer={observer} size="md">
			<ClayModal.Header
				closeButtonAriaLabel={Liferay.Language.get('close')}
			>
				{Liferay.Language.get('report-inappropriate-content')}
			</ClayModal.Header>

			{status === STATUS_REPORT ? (
				<ModalContentForm
					captchaURI={captchaURI}
					error={error}
					form={form}
					handleClose={handleClose}
					handleInputChange={handleInputChange}
					handleSubmit={handleSubmit}
					isSending={isSending}
					namespace={namespace}
					pathTermsOfUse={pathTermsOfUse}
					reasons={reasons}
					selectedReason={selectedReason}
					signedIn={signedIn}
				/>
			) : (
				<>
					<ModalBody
						companyName={companyName}
						handleClose={handleClose}
						status={status}
					/>
					<ClayModal.Footer
						last={
							<ClayButton
								displayType="secondary"
								onClick={handleClose}
							>
								{Liferay.Language.get('close')}
							</ClayButton>
						}
					/>
				</>
			)}
		</ClayModal>
	);
};

FlagsModal.propTypes = {
	captchaURI: PropTypes.string.isRequired,
	companyName: PropTypes.string.isRequired,
	error: PropTypes.string,
	handleClose: PropTypes.func.isRequired,
	handleInputChange: PropTypes.func.isRequired,
	handleSubmit: PropTypes.func.isRequired,
	isSending: PropTypes.bool.isRequired,
	pathTermsOfUse: PropTypes.string.isRequired,
	reasons: PropTypes.object.isRequired,
	selectedReason: PropTypes.string.isRequired,
	signedIn: PropTypes.bool.isRequired,
	status: PropTypes.oneOf([
		STATUS_ERROR,
		STATUS_LOGIN,
		STATUS_REPORT,
		STATUS_SUCCESS,
	]).isRequired,
};

export default FlagsModal;
