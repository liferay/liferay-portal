/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayForm, {ClayInput} from '@clayui/form';
import ClayIcon from '@clayui/icon';
import PropTypes from 'prop-types';
import React from 'react';

const ErrorFeedback = ({error}) => {
	return (
		<ClayForm.FeedbackGroup>
			<ClayForm.FeedbackItem>
				<span>{error}</span>
			</ClayForm.FeedbackItem>
		</ClayForm.FeedbackGroup>
	);
};

ErrorFeedback.propTypes = {
	error: PropTypes.string.isRequired,
};

const HelpMessage = ({message}) => {
	return (
		<span
			className="inline-item-after lfr-portal-tooltip tooltip-icon"
			title={message}
		>
			<ClayIcon symbol="question-circle-full" />
		</span>
	);
};

HelpMessage.propTypes = {
	message: PropTypes.string.isRequired,
};

const Input = ({
	disabled,
	error,
	helpMessage,
	id,
	label,
	name,
	required,
	type = 'text',
	...restProps
}) => {
	const inputId = id || name;

	return (
		<ClayForm.Group className={error ? 'has-error' : ''}>
			<label className={disabled ? 'disabled' : ''} htmlFor={inputId}>
				{label}

				{required && <RequiredMark />}

				{helpMessage && <HelpMessage message={helpMessage} />}
			</label>

			<ClayInput
				{...restProps}
				className="form-control"
				component={type === 'textarea' ? 'textarea' : 'input'}
				disabled={disabled}
				id={inputId}
				name={name}
				type={type}
			/>

			{typeof error === 'string' && <ErrorFeedback error={error} />}
		</ClayForm.Group>
	);
};

Input.propTypes = {
	disabled: PropTypes.bool,
	error: PropTypes.oneOfType([PropTypes.bool, PropTypes.string]),
	helpMessage: PropTypes.string,
	id: PropTypes.string,
	label: PropTypes.string.isRequired,
	name: PropTypes.string.isRequired,
	required: PropTypes.bool,
	type: PropTypes.string,
};

const RequiredMark = () => {
	return (
		<>
			<span className="inline-item-after reference-mark text-warning">
				<ClayIcon symbol="asterisk" />
			</span>
			<span className="hide-accessible sr-only">
				{Liferay.Language.get('required')}
			</span>
		</>
	);
};

export {ErrorFeedback, Input, HelpMessage, RequiredMark};
