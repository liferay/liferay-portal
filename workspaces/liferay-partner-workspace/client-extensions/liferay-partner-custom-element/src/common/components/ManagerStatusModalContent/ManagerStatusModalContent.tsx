/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import {ClayInput} from '@clayui/form';
import ClayModal from '@clayui/modal';
import {Dispatch, SetStateAction, useState} from 'react';

import LiferayPicklist from '../../../common/interfaces/liferayPicklist';
import {Liferay} from '../../../common/services/liferay';

interface IProps {
	displayModalStatus: LiferayPicklist;
	id: string;
	onClose: () => void;
	patchStatus: (
		mdfStatus: LiferayPicklist,
		id: string
	) => Promise<LiferayPicklist | undefined>;
	setIsSubmitting: Dispatch<SetStateAction<boolean>>;
	setPatchedStatus: Dispatch<
		React.SetStateAction<LiferayPicklist | undefined>
	>;
}

export default function ManagerStatusModalContent({
	displayModalStatus,
	id,
	onClose,
	patchStatus,
	setIsSubmitting,
	setPatchedStatus,
}: IProps) {
	const [textareaValue, setTextareaValue] = useState('');

	const handleOnClick = async () => {
		if (textareaValue) {
			const commentParagraph = document
				?.querySelector('iframe')
				?.contentWindow?.document.querySelector(
					'body.portlet-page-comments p'
				);

			const postCommentButton = document.querySelector(
				'button.btn-comment'
			) as HTMLButtonElement;

			if (commentParagraph && postCommentButton) {
				commentParagraph.innerHTML = textareaValue;
				postCommentButton.disabled = false;
				postCommentButton.click();
			}

			setIsSubmitting(true);

			const newStatus = await patchStatus(displayModalStatus, id);

			if (newStatus) {
				setPatchedStatus(newStatus);
				onClose();
			}
		}
		else {
			Liferay.Util.openToast({
				message:
					'The MDF Status cannot be changed without a motivation.',
				type: 'danger',
			});
		}
	};

	return (
		<>
			<ClayModal.Header
				closeButtonAriaLabel={Liferay.Language.get('close')}
			>
				Status Change
			</ClayModal.Header>
			<ClayModal.Body>
				<label className="fw-bold" htmlFor="descriptionTextArea">
					Status change motivation
				</label>
				<ClayInput
					component="textarea"
					id="descriptionTextArea"
					onChange={(event) => {
						setTextareaValue(event.target.value);
					}}
					placeholder="Describre here..."
					type="text"
				/>
			</ClayModal.Body>
			<ClayModal.Footer
				last={
					<ClayButton.Group spaced>
						<ClayButton displayType="secondary" onClick={onClose}>
							Cancel
						</ClayButton>
						<ClayButton
							className="inline-item"
							disabled={Boolean(!textareaValue)}
							displayType="primary"
							onClick={handleOnClick}
							type="button"
						>
							{displayModalStatus.name}
						</ClayButton>
					</ClayButton.Group>
				}
			/>
		</>
	);
}
