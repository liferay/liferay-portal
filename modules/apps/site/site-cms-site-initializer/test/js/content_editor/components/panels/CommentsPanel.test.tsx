/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';

// eslint-disable-next-line
import {checkAccessibility} from '@liferay/layout-js-components-web/test/__lib__/index';
import {render, screen, waitFor} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import React from 'react';

import CommentsPanel from '../../../../../src/main/resources/META-INF/resources/js/content_editor/components/panels/CommentsPanel';
import {Comment} from '../../../../../src/main/resources/META-INF/resources/js/content_editor/services/CommentService';
import {mockFetch} from '../../../__mocks__/frontend-js-web';

jest.mock('@ckeditor/ckeditor5-react', () => ({
	CKEditor: ({onReady}: any) => {
		const mockEditor = {
			getData: jest.fn().mockReturnValue('mocked data'),
		};
		onReady(mockEditor);

		return <input aria-label="Mocked CKEditor" />;
	},
}));

const initialComments = [
	{
		author: {
			fullName: 'Test User 1',
			portraitURL: '',
			userId: '1',
		},
		body: 'Parent comment',
		children: [
			{
				author: {
					fullName: 'Test User 2',
					portraitURL: '',
					userId: '2',
				},
				body: 'Child comment',
				children: [],
				className: 'Z7P6',
				commentId: '2',
				dateDescription: '55 Seconds Ago',
				edited: false,
				negativeVotes: 0,
				positiveVotes: 0,
				rootComment: true,
			},
		],
		className: 'Z7P5',
		commentId: '1',
		dateDescription: '18 Seconds Ago',
		edited: false,
		negativeVotes: 0,
		positiveVotes: 0,
		rootComment: true,
	},
] as Comment[];

const renderComponent = () => {
	return render(
		<CommentsPanel
			addCommentURL="addCommentURL"
			comments={initialComments}
			deleteCommentURL="deleteCommentURL"
			editCommentURL="editCommentURL"
			editorConfig={{}}
			getCommentsURL="getCommentsURL"
		/>
	);
};

const closeToast = async () => {
	await userEvent.click(screen.getByRole('button', {name: 'close'}));
};

describe('CommentsPanel', () => {
	beforeEach(() => {
		jest.clearAllMocks();
	});

	it('renders CommentsPanel', async () => {
		const {container} = renderComponent();

		await checkAccessibility({context: container});
	});

	it('deletes the child comment', async () => {
		renderComponent();

		expect(screen.getByText('Parent comment')).toBeInTheDocument;
		expect(screen.getByText('Child comment')).toBeInTheDocument;

		await userEvent.click(screen.getAllByText('delete')[1]);

		await waitFor(() => {
			expect(mockFetch).toBeCalledWith('deleteCommentURL', {
				body: {
					commentId: '2',
				},
				method: 'POST',
			});

			expect(
				screen.getByText('your-comment-has-been-deleted')
			).toBeInTheDocument();
		});

		await closeToast();

		expect(screen.getByText('Parent comment')).toBeInTheDocument;
		expect(screen.queryByText('Child comment')).not.toBeInTheDocument;
	});

	it('deletes the parent comment', async () => {
		renderComponent();

		expect(screen.getByText('Parent comment')).toBeInTheDocument;
		expect(screen.getByText('Child comment')).toBeInTheDocument;

		await userEvent.click(screen.getAllByText('delete')[0]);

		await waitFor(() => {
			expect(mockFetch).toBeCalledWith('deleteCommentURL', {
				body: {
					commentId: '1',
				},
				method: 'POST',
			});

			expect(
				screen.getByText('your-comment-has-been-deleted')
			).toBeInTheDocument();
		});

		await closeToast();

		expect(screen.queryByText('Parent comment')).not.toBeInTheDocument;
		expect(screen.queryByText('Child comment')).toBeInTheDocument;
	});

	it('shows a toast with the error when the request to delete a comment fails', async () => {
		const error = 'Unexpected error deleting a comment';

		(mockFetch as jest.Mock).mockRejectedValueOnce(new Error(error));

		renderComponent();

		await userEvent.click(screen.getAllByText('delete')[0]);

		await waitFor(() => {
			expect(mockFetch).toBeCalledWith('deleteCommentURL', {
				body: {
					commentId: '1',
				},
				method: 'POST',
			});

			expect(screen.getByText(error)).toBeInTheDocument();
		});

		await closeToast();
	});

	it('Votes when the thumb up button is pressed', async () => {
		renderComponent();

		const thumbUpButton = screen.getAllByTitle('rate-this-as-good')[0];

		await userEvent.click(thumbUpButton);

		expect(mockFetch).toBeCalledWith('/c/portal/rate_entry', {
			body: expect.objectContaining({score: 1}),
			method: 'POST',
		});
	});

	it('Votes when the thumb down button is pressed', async () => {
		renderComponent();

		const thumbDownButton = screen.getAllByTitle('rate-this-as-bad')[0];

		await userEvent.click(thumbDownButton);

		expect(mockFetch).toBeCalledWith('/c/portal/rate_entry', {
			body: expect.objectContaining({score: 0}),
			method: 'POST',
		});
	});
});
