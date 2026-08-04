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
import CommentService, {
	Comment,
} from '../../../../../src/main/resources/META-INF/resources/js/content_editor/services/CommentService';
import {mockFetch} from '../../../__mocks__/frontend-js-web';

jest.mock(
	'../../../../../src/main/resources/META-INF/resources/js/content_editor/services/CommentService',
	() => ({
		__esModule: true,
		default: {
			addComment: jest.fn(),
			deleteComment: jest.fn(),
			editComment: jest.fn(),
			getComments: jest.fn(),
		},
	})
);

jest.mock('@ckeditor/ckeditor5-react', () => ({
	CKEditor: ({onChange, onReady}: any) => {
		const mockEditor = {
			getData: jest.fn().mockReturnValue('mocked data'),
			on: jest.fn(),
			setData: jest.fn(),
		};
		onReady(mockEditor);

		return (
			<input
				aria-label="Mocked CKEditor"
				onChange={(event) => {
					if (onChange) {
						onChange(event, mockEditor);
					}
				}}
			/>
		);
	},
}));

const initialComments: Comment[] = [
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
				hasDeletePermission: true,
				hasUpdatePermission: true,
				negativeVotes: 0,
				positiveVotes: 0,
				rootComment: true,
			},
		],
		className: 'Z7P5',
		commentId: '1',
		dateDescription: '18 Seconds Ago',
		edited: false,
		hasDeletePermission: true,
		hasUpdatePermission: true,
		negativeVotes: 0,
		positiveVotes: 0,
		rootComment: true,
	},
];

const renderComponent = (
	addCommentURL = 'addCommentURL',
	readOnly = false,
	comments = initialComments
) => {
	return render(
		<CommentsPanel
			addCommentURL={addCommentURL}
			comments={comments}
			deleteCommentURL="deleteCommentURL"
			editCommentURL="editCommentURL"
			editorConfig={{}}
			getCommentsURL="getCommentsURL"
			readOnly={readOnly}
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

	it('hides the comment actions, reply, and ratings when read only', () => {
		renderComponent('addCommentURL', true);

		expect(screen.getByText('Parent comment')).toBeInTheDocument();
		expect(screen.getByText('Child comment')).toBeInTheDocument();

		expect(screen.queryByTitle('actions')).not.toBeInTheDocument();
		expect(screen.queryByText('delete')).not.toBeInTheDocument();
		expect(screen.queryByText('edit')).not.toBeInTheDocument();
		expect(screen.queryByText('reply')).not.toBeInTheDocument();
		expect(
			screen.queryByTitle('rate-this-as-good')
		).not.toBeInTheDocument();
		expect(screen.queryByTitle('rate-this-as-bad')).not.toBeInTheDocument();
	});

	it('hides the edit action when the comment cannot be updated', async () => {
		renderComponent('addCommentURL', false, [
			{...initialComments[0], children: [], hasUpdatePermission: false},
		]);

		await userEvent.click(screen.getByTitle('actions'));

		expect(screen.getByText('delete')).toBeInTheDocument();
		expect(screen.queryByText('edit')).not.toBeInTheDocument();
	});

	it('deletes the child comment', async () => {
		(CommentService.deleteComment as jest.Mock).mockResolvedValue({
			data: {},
		});

		renderComponent();

		expect(screen.getByText('Parent comment')).toBeInTheDocument;
		expect(screen.getByText('Child comment')).toBeInTheDocument;

		await userEvent.click(screen.getAllByText('delete')[1]);

		await waitFor(() => {
			expect(CommentService.deleteComment).toBeCalledWith(
				expect.objectContaining({
					commentId: '2',
					url: 'deleteCommentURL',
				})
			);

			expect(
				screen.getByText('your-comment-has-been-deleted')
			).toBeInTheDocument();
		});

		await closeToast();

		expect(screen.getByText('Parent comment')).toBeInTheDocument;
		expect(screen.queryByText('Child comment')).not.toBeInTheDocument;
	});

	it('deletes the parent comment', async () => {
		(CommentService.deleteComment as jest.Mock).mockResolvedValue({
			data: {},
		});

		renderComponent();

		expect(screen.getByText('Parent comment')).toBeInTheDocument;
		expect(screen.getByText('Child comment')).toBeInTheDocument;

		await userEvent.click(screen.getAllByText('delete')[0]);

		await waitFor(() => {
			expect(CommentService.deleteComment).toBeCalledWith(
				expect.objectContaining({
					commentId: '1',
					url: 'deleteCommentURL',
				})
			);

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

		(CommentService.deleteComment as jest.Mock).mockResolvedValue({
			error,
		});

		renderComponent();

		await userEvent.click(screen.getAllByText('delete')[0]);

		await waitFor(() => {
			expect(CommentService.deleteComment).toBeCalledWith(
				expect.objectContaining({
					commentId: '1',
					url: 'deleteCommentURL',
				})
			);

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

	it('Adds a new comment and fires the messagePosted event', async () => {
		const addCommentSpy = jest
			.spyOn(CommentService, 'addComment')
			.mockResolvedValue({
				data: {
					author: {
						fullName: 'Test User',
						portraitURL: '',
						userId: '1',
					},
					body: 'mocked data',
					children: [],
					className: 'com.liferay.portal.kernel.model.Comment',
					commentId: '345',
					dateDescription: 'Just now',
					edited: false,
					hasDeletePermission: true,
					hasUpdatePermission: true,
					negativeVotes: 0,
					positiveVotes: 0,
					rootComment: true,
				},
				error: null,
			});
		const addCommentURL = 'http://addCommentURL?classPK=123';
		const liferayFireSpy = jest.spyOn(Liferay, 'fire');

		renderComponent(addCommentURL);

		await userEvent.type(
			screen.getByLabelText('Mocked CKEditor'),
			'Test comment'
		);

		await userEvent.click(screen.getByRole('button', {name: /save/i}));

		await waitFor(() => {
			expect(addCommentSpy).toBeCalledWith(
				expect.objectContaining({
					content: 'mocked data',
					url: addCommentURL,
				})
			);

			expect(liferayFireSpy).toHaveBeenCalledWith(
				'messagePosted',
				expect.objectContaining({
					classPK: '123',
					commentId: 345,
					text: 'mocked data',
				})
			);
		});
	});

	it('fetches comments if they are not provided as props', async () => {
		(CommentService.getComments as jest.Mock).mockResolvedValue({
			data: initialComments,
		});

		render(
			<CommentsPanel
				addCommentURL="addCommentURL"
				deleteCommentURL="deleteCommentURL"
				editCommentURL="editCommentURL"
				editorConfig={{}}
				getCommentsURL="getCommentsURL"
			/>
		);

		await waitFor(() => {
			expect(CommentService.getComments).toHaveBeenCalledWith({
				url: 'getCommentsURL',
			});
		});

		expect(screen.getByText('Parent comment')).toBeInTheDocument();
		expect(screen.getByText('Child comment')).toBeInTheDocument();
	});
});
