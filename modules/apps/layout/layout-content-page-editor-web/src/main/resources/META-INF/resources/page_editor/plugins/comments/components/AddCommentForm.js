/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {openToast, useId} from 'frontend-js-components-web';
import PropTypes from 'prop-types';
import React, {useState} from 'react';

import {useDispatch} from '../../../app/contexts/StoreContext';
import addFragmentComment from '../../../app/thunks/addFragmentComment';
import CommentForm from './CommentForm';

export default function AddCommentForm({fragmentEntryLinkId}) {
	const [addingComment, setAddingComment] = useState(false);
	const dispatch = useDispatch();
	const pageEditorCommentEditorId = useId();
	const [showButtons, setShowButtons] = useState(false);
	const [textareaContent, setTextareaContent] = useState('');

	const _handleCancelButtonClick = () => {
		setShowButtons(false);
		setTextareaContent('');
	};

	const _handleFormFocus = () => {
		setShowButtons(true);
	};

	const _handleCommentButtonClick = () => {
		setAddingComment(true);

		dispatch(
			addFragmentComment({
				body: textareaContent,
				fragmentEntryLinkId,
			})
		)
			.then(() => {
				setAddingComment(false);
				setShowButtons(false);
				setTextareaContent('');
			})
			.catch(() => {
				openToast({
					message: Liferay.Language.get(
						'the-comment-could-not-be-saved'
					),
					type: 'danger',
				});

				setAddingComment(false);
			});
	};

	const _handleTextareaChange = (content) => {
		if (content) {
			setTextareaContent(content);
		}
	};

	return (
		<div className="page-editor__fragment-comment__form pt-2 px-3">
			<CommentForm
				id={pageEditorCommentEditorId}
				loading={addingComment}
				onCancelButtonClick={_handleCancelButtonClick}
				onFormFocus={_handleFormFocus}
				onSubmitButtonClick={_handleCommentButtonClick}
				onTextareaChange={_handleTextareaChange}
				showButtons={showButtons}
				submitButtonLabel={Liferay.Language.get('comment[verb]')}
				textareaContent={textareaContent}
			/>
		</div>
	);
}

AddCommentForm.propTypes = {
	fragmentEntryLinkId: PropTypes.string.isRequired,
};
