/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {openToast} from 'frontend-js-components-web';
import {
	fetch,
	getFormElement,
	objectToFormData,
	openWindow,
	runScriptsInElement,
	setFormValues,
	toggleDisabled,
} from 'frontend-js-web';

function hideEl(elementId) {
	const element = document.getElementById(elementId);

	if (element) {
		element.classList.add('hide');
	}
}

export default function Comments({
	constants,
	editorURL,
	formName,
	hideControls,
	loginURL,
	messageId,
	namespace,
	paginationURL,
	portletDisplayId,
	randomNamespace,
	ratingsEnabled,
	refreshPageOnReply,
	subscriptionClassName,
	userId,
}) {
	const Util = Liferay.Util;
	const form = document[`${namespace}${randomNamespace}${formName}`];

	if (messageId) {
		document
			.getElementById(`${randomNamespace}messageScroll${messageId}`)
			.scrollIntoView();
	}

	const moreCommentsTrigger = document.getElementById(
		`${namespace}moreCommentsTrigger`
	);

	const indexElement = getFormElement(form, 'index');
	const rootIndexPageElement = getFormElement(form, 'rootIndexPage');

	if (moreCommentsTrigger && indexElement && rootIndexPageElement) {
		moreCommentsTrigger.addEventListener('click', () => {
			const data = Util.ns(namespace, {
				className: getFormElement(form, 'className').value,
				classPK: getFormElement(form, 'classPK').value,
				hideControls,
				index: indexElement.value,
				randomNamespace,
				ratingsEnabled,
				rootIndexPage: rootIndexPageElement.value,
				userId,
			});

			fetch(paginationURL, {
				body: objectToFormData(data),
				method: 'POST',
			})
				.then((response) => {
					return response.text();
				})
				.then((response) => {
					const moreCommentsContainer = document.getElementById(
						`${namespace}moreCommentsContainer`
					);

					if (moreCommentsContainer) {
						const newCommentsContainer =
							document.createElement('div');

						newCommentsContainer.innerHTML = response;

						moreCommentsContainer.insertAdjacentElement(
							'beforebegin',
							newCommentsContainer
						);

						runScriptsInElement(newCommentsContainer);
					}
				})
				.catch(() => {
					openToast({
						message: Liferay.Language.get(
							'your-request-failed-to-complete'
						),
						type: 'danger',
					});
				});
		});
	}

	function onMessagePosted(response, refreshPage) {
		Liferay.onceAfter(`${portletDisplayId}:portletRefreshed`, () => {
			const randomNamespaceNodes = document.querySelectorAll(
				`input[id^="${namespace}"][id$="randomNamespace"]`
			);

			Array.prototype.forEach.call(
				randomNamespaceNodes,
				(node, index) => {
					const randomId = node.value;

					if (index === 0) {
						openToast({
							message: Liferay.Language.get(
								'your-request-completed-successfully'
							),
							type: 'success',
						});
					}

					const currentMessageSelector = `${randomId}message_${response.commentId}`;

					const targetNode = document.getElementById(
						currentMessageSelector
					);

					if (targetNode) {
						location.hash = '#' + currentMessageSelector;
					}
				}
			);
		});

		const className = getFormElement(form, 'className').value;
		const classPK = getFormElement(form, 'classPK').value;

		if (response.commentId) {
			const messageTextNode = document.querySelector(
				`input[name^="${namespace}${randomNamespace}body"]`
			);

			if (messageTextNode) {
				Liferay.fire('messagePosted', {
					className,
					classPK,
					commentId: response.commentId,
					externalReferenceCode: response.externalReferenceCode,
					text: messageTextNode.value,
				});
			}
		}

		if (refreshPage) {
			window.location.reload();
		}
		else {
			const data = Liferay.Util.ns(namespace, {
				className,
				classPK,
				skipEditorLoading: false,
			});

			Liferay.Portlet.refresh(`#p_p_id_${portletDisplayId}_`, data, true);
		}
	}

	function sendMessage(form, refreshPage) {
		const commentButtons = form.querySelectorAll('.btn-comment');

		toggleDisabled(commentButtons, true);

		const formData = new FormData(form);

		formData.append('doAsUserId', themeDisplay.getDoAsUserIdEncoded());

		fetch(form.action, {
			body: formData,
			method: 'POST',
		})
			.then((response) => {
				let promise;

				const contentType = response.headers.get('content-type');

				if (
					contentType &&
					contentType.indexOf('application/json') !== -1
				) {
					promise = response.json();
				}
				else {
					promise = response.text();
				}

				return promise;
			})
			.then((response) => {
				const exception = response.exception;

				if (!exception) {
					Liferay.onceAfter(
						`${portletDisplayId}:messagePosted`,
						() => {
							onMessagePosted(response, refreshPage);
						}
					);

					Liferay.fire(`${portletDisplayId}:messagePosted`, response);
				}
				else {
					let errorKey = Liferay.Language.get(
						'your-request-failed-to-complete'
					);

					if (
						exception.indexOf('DiscussionMaxCommentsException') > -1
					) {
						errorKey = Liferay.Language.get(
							'maximum-number-of-comments-has-been-reached'
						);
					}
					else if (exception.indexOf('MessageBodyException') > -1) {
						errorKey = Liferay.Language.get(
							'please-enter-a-valid-message'
						);
					}
					else if (
						exception.indexOf('NoSuchMessageException') > -1
					) {
						errorKey = Liferay.Language.get(
							'the-message-could-not-be-found'
						);
					}
					else if (exception.indexOf('PrincipalException') > -1) {
						errorKey = Liferay.Language.get(
							'you-do-not-have-the-required-permissions'
						);
					}
					else if (
						exception.indexOf('RequiredMessageException') > -1
					) {
						errorKey = Liferay.Language.get(
							'you-cannot-delete-a-root-message-that-has-more-than-one-immediate-reply'
						);
					}

					openToast({
						message: errorKey,
						type: 'danger',
					});
				}

				toggleDisabled(commentButtons, false);
			})
			.catch(() => {
				openToast({
					message: Liferay.Language.get(
						'your-request-failed-to-complete'
					),
					type: 'danger',
				});

				toggleDisabled(commentButtons, false);
			});
	}

	function showEditor(formId, options) {
		const editor = window[`${namespace}${options.name}`];

		const editorWrapper =
			editor && document.querySelector(`#${formId} .editor-wrapper`);

		if (!editorWrapper || !editorWrapper.childNodes.length) {
			fetch(editorURL, {
				body: objectToFormData(Util.ns(namespace, options)),
				method: 'POST',
			})
				.then((response) => {
					return response.text();
				})
				.then((response) => {
					const editorWrapper = document.querySelector(
						`#${formId} .editor-wrapper`
					);

					if (editorWrapper) {
						editorWrapper.innerHTML = response;

						runScriptsInElement(editorWrapper);
					}

					toggleDisabled(
						'#' + options.name.replace('Body', 'Button'),
						options.contents === ''
					);

					window[`${randomNamespace}showEl`](formId);
				})
				.catch(() => {
					openToast({
						message: Liferay.Language.get(
							'your-request-failed-to-complete'
						),
						type: 'danger',
					});
				});
		}
	}

	window[`${namespace}${randomNamespace}0ReplyOnChange`] = function (html) {
		toggleDisabled(
			`#${namespace}${randomNamespace}postReplyButton0`,
			html.trim() === ''
		);
	};

	window[`${randomNamespace}afterLogin`] = function (
		emailAddress,
		anonymousAccount
	) {
		setFormValues(form, {
			emailAddress,
		});

		sendMessage(form, !anonymousAccount);
	};

	window[`${randomNamespace}deleteMessage`] = function (i) {
		const commentIdElement = getFormElement(form, 'commentId' + i);

		if (commentIdElement) {
			setFormValues(form, {
				cmd: constants.DELETE,
				commentId: commentIdElement.value,
			});

			sendMessage(form);
		}
	};

	window[`${randomNamespace}hideEditor`] = function (editorName, formId) {
		const editor = window[`${namespace}${editorName}`];

		if (editor) {
			editor.destroy();
		}

		hideEl(formId);
	};

	window[`${randomNamespace}postReply`] = function (i) {
		const editorInstance =
			window[`${namespace}${randomNamespace}postReplyBody${i}`];

		const parentCommentIdElement = getFormElement(
			form,
			'parentCommentId' + i
		);

		if (parentCommentIdElement) {
			setFormValues(form, {
				body: editorInstance.getHTML(),
				cmd: constants.ADD,
				parentCommentId: parentCommentIdElement.value,
			});
		}

		if (!themeDisplay.isSignedIn()) {
			window.namespace = namespace;
			window.randomNamespace = randomNamespace;

			openWindow({
				dialog: {
					height: 450,
					width: 560,
				},
				id: `${namespace}signInDialog`,
				title: Liferay.Language.get('sign-in'),
				uri: loginURL,
			});
		}
		else {
			sendMessage(form, refreshPageOnReply);

			editorInstance.dispose();
		}
	};

	window[`${randomNamespace}showEl`] = function (elementId) {
		const element = document.getElementById(elementId);

		if (element) {
			element.classList.remove('hide');
		}
	};

	window[`${randomNamespace}showPostReplyEditor`] = function (index) {
		showEditor(`${namespace}${randomNamespace}postReplyForm${index}`, {
			name: `${randomNamespace}postReplyBody${index}`,
			onChangeMethod: `${randomNamespace}${index}ReplyOnChange`,
			placeholder: 'type-your-comment-here',
		});

		window[`${randomNamespace}hideEditor`](
			`${randomNamespace}editReplyBody${index}`,
			`${namespace}${randomNamespace}editForm${index}`
		);

		window[`${randomNamespace}showEl`](
			`${namespace}${randomNamespace}discussionMessage${index}`
		);
	};

	window[`${randomNamespace}showEditReplyEditor`] = function (index) {
		const discussionId = `${namespace}${randomNamespace}discussionMessage${index}`;

		const discussionIdElement = document.getElementById(discussionId);

		if (discussionIdElement) {
			showEditor(`${namespace}${randomNamespace}editForm${index}`, {
				contents: discussionIdElement.innerHTML,
				name: `${randomNamespace}editReplyBody${index}`,
				onChangeMethod: `${randomNamespace}${index}EditOnChange`,
			});

			window[`${randomNamespace}hideEditor`](
				`${randomNamespace}postReplyBody${index}`,
				`${namespace}${randomNamespace}postReplyForm${index}`
			);

			hideEl(discussionId);
		}
	};

	window[`${randomNamespace}subscribeToComments`] = function (subscribe) {
		setFormValues(form, {
			[`${randomNamespace}className`]: subscriptionClassName,
			cmd: subscribe
				? constants.SUBSCRIBE_TO_COMMENTS
				: constants.UNSUBSCRIBE_FROM_COMMENTS,
		});

		sendMessage(form);
	};

	window[`${randomNamespace}updateMessage`] = function (i, pending) {
		const editorInstance =
			window[`${namespace}${randomNamespace}editReplyBody${i}`];

		const commentIdElement = getFormElement(form, `commentId${i}`);

		if (commentIdElement) {
			if (pending) {
				setFormValues(form, {
					workflowAction: constants.ACTION_SAVE_DRAFT,
				});
			}

			setFormValues(form, {
				body: editorInstance.getHTML(),
				cmd: constants.UPDATE,
				commentId: commentIdElement.value,
			});

			sendMessage(form);
		}

		editorInstance.dispose();
	};
}
