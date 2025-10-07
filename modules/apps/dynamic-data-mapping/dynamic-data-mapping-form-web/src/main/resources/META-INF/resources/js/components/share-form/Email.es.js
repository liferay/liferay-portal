/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {useResource} from '@clayui/data-provider';
import ClayForm, {ClayInput} from '@clayui/form';
import ClayMultiSelect from '@clayui/multi-select';
import React, {useEffect, useRef, useState} from 'react';

function usePrevious(value) {
	const ref = useRef();

	useEffect(() => {
		ref.current = value;
	}, [value]);

	return ref.current;
}

function formatAutocompleteValue(data) {
	return `${data.fullName} (${data.emailAddress})`;
}

function isEmailAddressValid(email) {
	const emailRegex = /.+@.+\..+/i;

	return emailRegex.test(email.label);
}

const Email = ({
	addresses,
	autocompleteUserURL,
	emailContent,
	message,
	onMessageChanged,
	onMultiSelectItemsChanged,
	onSubjectChanged,
	portletNamespace,
	subject,
}) => {
	const [multiSelectValue, setMultiSelectValue] = useState('');

	const previousMultiSelectValue = usePrevious(multiSelectValue);

	const [networkStatus, setNetworkStatus] = useState(4);

	const {refetch, resource} = useResource({
		fetchPolicy: 'cache-first',
		link:
			autocompleteUserURL +
			'&' +
			portletNamespace +
			'query=' +
			multiSelectValue,
		onNetworkStatusChange: setNetworkStatus,
	});

	const error = networkStatus === 5;

	useEffect(() => {
		if (multiSelectValue && multiSelectValue !== previousMultiSelectValue) {
			refetch();
		}
	}, [multiSelectValue, previousMultiSelectValue, refetch]);

	return (
		<div className="share-form-modal-item-email">
			<ClayForm.Group>
				<label>{Liferay.Language.get('to[recipient]')}</label>

				<ClayInput.Group small stacked>
					<ClayInput.GroupItem>
						{!error && (
							<>
								<ClayMultiSelect
									clearAllTitle={Liferay.Language.get(
										'clear-all'
									)}
									closeButtonAriaLabel={Liferay.Language.get(
										'remove'
									)}
									items={addresses}
									loadingState={networkStatus}
									onChange={setMultiSelectValue}
									onClearAllButtonClick={() => {
										emailContent.current.addresses = [];

										onMultiSelectItemsChanged([]);
									}}
									onItemsChange={(newItems) => {
										if (!newItems.length) {
											emailContent.current.addresses =
												newItems;

											return onMultiSelectItemsChanged(
												newItems
											);
										}

										const validItens =
											newItems.filter(
												isEmailAddressValid
											);

										if (validItens.length) {
											emailContent.current.addresses =
												validItens;

											return onMultiSelectItemsChanged(
												validItens
											);
										}
									}}
									placeholder={Liferay.Language.get(
										'enter-a-list-of-email-addresses'
									)}
									sourceItems={
										resource
											? resource.map((data) => {
													return {
														label: data.emailAddress,
														value: formatAutocompleteValue(
															data
														),
													};
												})
											: []
									}
									value={multiSelectValue}
								/>
								<ClayForm.FeedbackGroup>
									<ClayForm.Text>
										{Liferay.Language.get(
											'type-a-comma-or-press-enter-to-input-email-addresses'
										)}
									</ClayForm.Text>
								</ClayForm.FeedbackGroup>
							</>
						)}
					</ClayInput.GroupItem>
				</ClayInput.Group>
			</ClayForm.Group>

			<ClayForm.Group>
				<label htmlFor="subject">
					{Liferay.Language.get('subject')}
				</label>

				<ClayInput.Group>
					<ClayInput.GroupItem>
						<ClayInput
							className="form-control"
							id="subject"
							onChange={({target: {value: newSubject}}) => {
								emailContent.current.subject = newSubject;

								onSubjectChanged(newSubject);
							}}
							type="text"
							value={subject}
						/>
					</ClayInput.GroupItem>
				</ClayInput.Group>
			</ClayForm.Group>

			<ClayForm.Group>
				<label htmlFor="message">
					{Liferay.Language.get('message')}
				</label>

				<ClayInput.Group>
					<ClayInput.GroupItem>
						<ClayInput
							className="form-control"
							component="textarea"
							id="message"
							onChange={({target: {value: newMessage}}) => {
								emailContent.current.message = newMessage;

								onMessageChanged(newMessage);
							}}
							type="text"
							value={message}
						/>
					</ClayInput.GroupItem>
				</ClayInput.Group>
			</ClayForm.Group>
		</div>
	);
};

export default Email;
