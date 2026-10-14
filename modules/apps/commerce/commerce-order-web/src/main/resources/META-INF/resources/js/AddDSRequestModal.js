/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayAutocomplete from '@clayui/autocomplete';
import ClayButton, {ClayButtonWithIcon} from '@clayui/button';
import ClayForm, {ClayCheckbox, ClayInput} from '@clayui/form';
import ClayIcon from '@clayui/icon';
import ClayList from '@clayui/list';
import ClayModal from '@clayui/modal';
import {openToast} from 'frontend-js-components-web';
import {escapeHTML, fetch, sub} from 'frontend-js-web';
import PropTypes from 'prop-types';
import React, {useEffect, useRef, useState} from 'react';

const EMAIL_MESSAGE_MAX_LENGTH = 10000;

const EMPTY_RECIPIENT = {emailAddress: '', name: '', userId: null};

function ErrorFeedback({error}) {
	return (
		<ClayForm.FeedbackGroup>
			<ClayForm.FeedbackItem>
				<span>{error}</span>
			</ClayForm.FeedbackItem>
		</ClayForm.FeedbackGroup>
	);
}

function moveRecipient(index, offset, recipients) {
	const movedRecipients = [...recipients];

	[movedRecipients[index], movedRecipients[index + offset]] = [
		movedRecipients[index + offset],
		movedRecipients[index],
	];

	return movedRecipients;
}

function RecipientUserAutocomplete({
	accountUsers,
	allowCountersigner,
	commerceOrderId,
	error,
	excludedUserIds,
	getDSRequestRecipientUsersURL,
	id,
	onSelectUser,
	portletNamespace,
	value,
}) {
	const [query, setQuery] = useState(value);
	const [recipientUsers, setRecipientUsers] = useState([]);

	const selectedEmailAddressRef = useRef(value);

	useEffect(() => {
		if (value === selectedEmailAddressRef.current) {
			return;
		}

		selectedEmailAddressRef.current = value;

		setQuery(value);
	}, [value]);

	useEffect(() => {
		if (!query || !allowCountersigner) {
			setRecipientUsers([]);

			return;
		}

		const timeoutId = setTimeout(() => {
			const url = new URL(
				getDSRequestRecipientUsersURL,
				window.location.href
			);

			url.searchParams.set(
				`${portletNamespace}commerceOrderId`,
				commerceOrderId
			);
			url.searchParams.set(`${portletNamespace}keywords`, query);

			fetch(url.toString())
				.then((response) => response.json())
				.then((users) =>
					setRecipientUsers(Array.isArray(users) ? users : [])
				)
				.catch(() => setRecipientUsers([]));
		}, 300);

		return () => clearTimeout(timeoutId);
	}, [
		allowCountersigner,
		commerceOrderId,
		getDSRequestRecipientUsersURL,
		portletNamespace,
		query,
	]);

	const lowerCaseQuery = query.toLowerCase();

	const matchingAccountUsers = accountUsers.filter(
		({emailAddress, name, userId}) =>
			!excludedUserIds.includes(userId) &&
			(emailAddress.toLowerCase().includes(lowerCaseQuery) ||
				name.toLowerCase().includes(lowerCaseQuery))
	);

	const accountUserIds = accountUsers.map(({userId}) => userId);

	const countersigners = recipientUsers
		.filter(
			({userId}) =>
				!accountUserIds.includes(userId) &&
				!excludedUserIds.includes(userId)
		)
		.map((user) => ({...user, countersigner: true}));

	return (
		<ClayForm.Group
			className={error ? 'has-error mb-0 w-100' : 'mb-0 w-100'}
		>
			<label htmlFor={id}>
				{Liferay.Language.get('recipient-email')}

				<RequiredMark />
			</label>

			<ClayAutocomplete
				filterKey="emailAddress"
				id={id}
				items={[...matchingAccountUsers, ...countersigners]}
				menuTrigger="focus"
				onChange={(nextQuery) => {
					setQuery(nextQuery);

					if (
						selectedEmailAddressRef.current &&
						nextQuery !== selectedEmailAddressRef.current
					) {
						selectedEmailAddressRef.current = '';

						onSelectUser(EMPTY_RECIPIENT);
					}
				}}
				placeholder={Liferay.Language.get('search-for')}
				value={query}
			>
				{(user) => (
					<ClayAutocomplete.Item
						key={user.userId}
						onClick={() => {
							selectedEmailAddressRef.current = user.emailAddress;

							setQuery(user.emailAddress);
							onSelectUser(user);
						}}
						textValue={user.emailAddress}
					>
						<div>
							{`${user.name} (${user.emailAddress})`}

							{user.countersigner && (
								<span className="ml-2 text-secondary">
									{Liferay.Language.get('countersigner')}
								</span>
							)}
						</div>
					</ClayAutocomplete.Item>
				)}
			</ClayAutocomplete>

			{error && <ErrorFeedback error={error} />}
		</ClayForm.Group>
	);
}

function RequiredMark() {
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
}

function validate({emailSubject, recipients}) {
	const errors = {};

	if (!emailSubject.trim()) {
		errors.emailSubject = Liferay.Language.get('this-field-is-required');
	}

	const recipientErrors = recipients.map(({userId}) =>
		userId ? null : Liferay.Language.get('this-field-is-required')
	);

	if (recipientErrors.some(Boolean)) {
		errors.recipients = recipientErrors;
	}

	return errors;
}

export default function AddDSRequestModal({
	addDSRequestModalProps,
	addDSRequestURL,
	attachment,
	closeModal,
}) {
	const {
		accountUsers,
		buyer,
		commerceOrderId,
		getDSRequestRecipientUsersURL,
		portletNamespace,
	} = addDSRequestModalProps;

	const [emailMessage, setEmailMessage] = useState('');
	const [emailSubject, setEmailSubject] = useState('');
	const [errors, setErrors] = useState({});
	const [expireAfter, setExpireAfter] = useState(120);
	const rowKeyRef = useRef(0);

	const createRecipient = (user) => ({
		emailAddress: user.emailAddress,
		name: user.name,
		rowKey: rowKeyRef.current++,
		userId: user.userId,
	});

	const [recipients, setRecipients] = useState(() => [
		createRecipient(buyer || EMPTY_RECIPIENT),
	]);
	const [sequential, setSequential] = useState(true);
	const [submitting, setSubmitting] = useState(false);

	const accountUserIds = [
		...accountUsers.map(({userId}) => userId),
		buyer?.userId,
	];

	const countersignerIndex = recipients.findIndex(
		({userId}) => userId && !accountUserIds.includes(userId)
	);

	const setRecipient = (recipientIndex, user) =>
		setRecipients(
			recipients.map((recipient, index) =>
				index === recipientIndex
					? {
							emailAddress: user.emailAddress,
							name: user.name,
							rowKey: recipient.rowKey,
							userId: user.userId,
						}
					: recipient
			)
		);

	const onSubmit = (event) => {
		event.preventDefault();

		const nextErrors = validate({
			emailSubject,
			recipients,
		});

		setErrors(nextErrors);

		if (Object.keys(nextErrors).length) {
			return;
		}

		const formData = new FormData();

		formData.append(`${portletNamespace}emailMessage`, emailMessage);
		formData.append(`${portletNamespace}emailSubject`, emailSubject);
		formData.append(`${portletNamespace}expireAfter`, expireAfter);
		formData.append(`${portletNamespace}sequential`, sequential);

		recipients.forEach(({userId}) =>
			formData.append(`${portletNamespace}recipientUserIds`, userId)
		);

		setSubmitting(true);

		fetch(addDSRequestURL, {body: formData, method: 'POST'})
			.then((response) => {
				if (!response.ok) {
					throw new Error(response.statusText);
				}

				return response.json();
			})
			.then(({errorMessage}) => {
				if (errorMessage) {
					throw {errorMessage};
				}

				closeModal();

				openToast({
					message: Liferay.Language.get(
						'your-request-completed-successfully'
					),
					type: 'success',
				});

				window.location.reload();
			})
			.catch((error) => {
				setSubmitting(false);

				openToast({
					message: escapeHTML(
						error?.errorMessage ||
							Liferay.Language.get('an-unexpected-error-occurred')
					),
					type: 'danger',
				});
			});
	};

	return (
		<ClayForm onSubmit={onSubmit}>
			<ClayModal.Header>
				{Liferay.Language.get('request-signature')}
			</ClayModal.Header>

			<ClayModal.Body>
				<ClayList className="mt-1">
					<ClayList.Header>
						{Liferay.Language.get('documents-added')}
					</ClayList.Header>

					<ClayList.Item flex>
						<ClayList.ItemField expand>
							<ClayList.ItemTitle>
								{attachment.title}
							</ClayList.ItemTitle>
						</ClayList.ItemField>
					</ClayList.Item>
				</ClayList>

				{recipients.map((recipient, index) => (
					<ClayForm.Group key={recipient.rowKey}>
						<ClayInput.Group>
							<ClayInput.GroupItem>
								<ClayForm.Group className="mb-0 w-100">
									<label
										className="disabled"
										htmlFor={`${portletNamespace}recipientName${index}`}
									>
										{Liferay.Language.get(
											'recipient-full-name'
										)}

										<RequiredMark />
									</label>

									<ClayInput
										disabled
										id={`${portletNamespace}recipientName${index}`}
										placeholder={Liferay.Language.get(
											'recipient-full-name'
										)}
										value={recipient.name}
									/>
								</ClayForm.Group>
							</ClayInput.GroupItem>

							<ClayInput.GroupItem>
								<RecipientUserAutocomplete
									accountUsers={[
										...(buyer ? [buyer] : []),
										...accountUsers.filter(
											({userId}) =>
												userId !== buyer?.userId
										),
									]}
									allowCountersigner={
										countersignerIndex === -1 ||
										countersignerIndex === index
									}
									commerceOrderId={commerceOrderId}
									error={errors.recipients?.[index]}
									excludedUserIds={recipients
										.filter(
											(_, recipientIndex) =>
												recipientIndex !== index
										)
										.map(({userId}) => userId)}
									getDSRequestRecipientUsersURL={
										getDSRequestRecipientUsersURL
									}
									id={`${portletNamespace}recipientEmail${index}`}
									onSelectUser={(user) =>
										setRecipient(index, user)
									}
									portletNamespace={portletNamespace}
									value={recipient.emailAddress}
								/>
							</ClayInput.GroupItem>

							{sequential && (
								<>
									<ClayInput.GroupItem
										className="align-self-end"
										shrink
									>
										<ClayButtonWithIcon
											aria-label={Liferay.Language.get(
												'move-up'
											)}
											disabled={!index}
											displayType="secondary"
											onClick={() =>
												setRecipients(
													moveRecipient(
														index,
														-1,
														recipients
													)
												)
											}
											symbol="angle-up"
											title={Liferay.Language.get(
												'move-up'
											)}
										/>
									</ClayInput.GroupItem>

									<ClayInput.GroupItem
										className="align-self-end"
										shrink
									>
										<ClayButtonWithIcon
											aria-label={Liferay.Language.get(
												'move-down'
											)}
											disabled={
												index === recipients.length - 1
											}
											displayType="secondary"
											onClick={() =>
												setRecipients(
													moveRecipient(
														index,
														1,
														recipients
													)
												)
											}
											symbol="angle-down"
											title={Liferay.Language.get(
												'move-down'
											)}
										/>
									</ClayInput.GroupItem>
								</>
							)}

							<ClayInput.GroupItem
								className="align-self-end"
								shrink
							>
								<ClayButtonWithIcon
									aria-label={Liferay.Language.get('remove')}
									disabled={recipients.length === 1}
									displayType="secondary"
									onClick={() =>
										setRecipients(
											recipients.filter(
												(_, recipientIndex) =>
													recipientIndex !== index
											)
										)
									}
									symbol="trash"
									title={Liferay.Language.get('remove')}
								/>
							</ClayInput.GroupItem>
						</ClayInput.Group>
					</ClayForm.Group>
				))}

				<ClayButton
					className="mb-3"
					displayType="unstyled"
					onClick={() =>
						setRecipients([
							...recipients,
							createRecipient(EMPTY_RECIPIENT),
						])
					}
				>
					<span className="inline-item inline-item-before">
						<ClayIcon symbol="plus" />
					</span>

					<span>{Liferay.Language.get('add-recipient')}</span>
				</ClayButton>

				<ClayForm.Group>
					<ClayCheckbox
						checked={sequential}
						label={Liferay.Language.get(
							'recipients-sign-in-the-order-listed'
						)}
						onChange={() => setSequential(!sequential)}
					/>
				</ClayForm.Group>

				<ClayForm.Group
					className={errors.emailSubject ? 'has-error' : undefined}
				>
					<label htmlFor={`${portletNamespace}emailSubject`}>
						{Liferay.Language.get('email-subject')}

						<RequiredMark />
					</label>

					<ClayInput
						id={`${portletNamespace}emailSubject`}
						onChange={(event) =>
							setEmailSubject(event.target.value)
						}
						placeholder={Liferay.Language.get(
							'please-sign-this-document'
						)}
						value={emailSubject}
					/>

					{errors.emailSubject && (
						<ErrorFeedback error={errors.emailSubject} />
					)}
				</ClayForm.Group>

				<ClayForm.Group>
					<label htmlFor={`${portletNamespace}emailMessage`}>
						{Liferay.Language.get('email-message')}
					</label>

					<ClayInput
						component="textarea"
						id={`${portletNamespace}emailMessage`}
						maxLength={EMAIL_MESSAGE_MAX_LENGTH}
						onChange={(event) =>
							setEmailMessage(event.target.value)
						}
						placeholder={Liferay.Language.get('email-message')}
						value={emailMessage}
					/>

					<ClayForm.FeedbackGroup>
						<ClayForm.FeedbackItem>
							<ClayForm.Text>
								{sub(
									Liferay.Language.get(
										'x-characters-remaining'
									),
									EMAIL_MESSAGE_MAX_LENGTH -
										emailMessage.length
								)}
							</ClayForm.Text>
						</ClayForm.FeedbackItem>
					</ClayForm.FeedbackGroup>
				</ClayForm.Group>

				<ClayForm.Group>
					<label htmlFor={`${portletNamespace}expireAfter`}>
						{Liferay.Language.get('days-until-expiration')}
					</label>

					<ClayInput
						id={`${portletNamespace}expireAfter`}
						min="0"
						onChange={(event) => setExpireAfter(event.target.value)}
						type="number"
						value={expireAfter}
					/>
				</ClayForm.Group>
			</ClayModal.Body>

			<ClayModal.Footer
				last={
					<ClayButton.Group spaced>
						<ClayButton
							displayType="secondary"
							onClick={closeModal}
						>
							{Liferay.Language.get('cancel')}
						</ClayButton>

						<ClayButton disabled={submitting} type="submit">
							{Liferay.Language.get('send')}
						</ClayButton>
					</ClayButton.Group>
				}
			/>
		</ClayForm>
	);
}

AddDSRequestModal.propTypes = {
	addDSRequestModalProps: PropTypes.shape({
		accountUsers: PropTypes.arrayOf(
			PropTypes.shape({
				emailAddress: PropTypes.string,
				name: PropTypes.string,
				userId: PropTypes.number,
			})
		),
		buyer: PropTypes.shape({
			emailAddress: PropTypes.string,
			name: PropTypes.string,
			userId: PropTypes.number,
		}),
		commerceOrderId: PropTypes.number,
		getDSRequestRecipientUsersURL: PropTypes.string,
		portletNamespace: PropTypes.string,
	}).isRequired,
	addDSRequestURL: PropTypes.string.isRequired,
	attachment: PropTypes.shape({
		id: PropTypes.oneOfType([PropTypes.number, PropTypes.string]),
		title: PropTypes.string,
	}).isRequired,
	closeModal: PropTypes.func.isRequired,
};
