/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {openConfirmModal, openModal} from 'frontend-js-components-web';
import {escapeHTML, fetch, sub} from 'frontend-js-web';

const RECENTLY_REMOVED_ATTACHMENTS = {
	multiple: Liferay.Language.get('x-recently-removed-attachments'),
	single: Liferay.Language.get('x-recently-removed-attachment'),
};

const CONFIRM_DISCARD_IMAGES = Liferay.Language.get(
	'uploads-are-in-progress-confirmation'
);

/**
 * MBPortlet handles the actions of replying or editing a
 * message board.
 */

class MBPortlet {
	constructor({
		constants,
		currentAction,
		getAttachmentsURL,
		namespace,
		replyToMessageId,
		rootNodeId,
		strings = {
			confirmDiscardImages: CONFIRM_DISCARD_IMAGES,
		},
		viewTrashAttachmentsURL,
		trashEnabled,
	}) {
		this._namespace = namespace;
		this._constants = constants;
		this._currentAction = currentAction;
		this._getAttachmentsURL = getAttachmentsURL;
		this._replyToMessageId = replyToMessageId;
		this._strings = strings;
		this._viewTrashAttachmentsURL = viewTrashAttachmentsURL;
		this._trashEnabled = trashEnabled;

		this.rootNode = document.getElementById(rootNodeId);

		this.workflowActionInputNode = document.getElementById(
			`${this._namespace}workflowAction`
		);

		this._events = [];
		this._attachEvents();
	}

	dispose() {
		this._events.forEach(({event, listener, target}) =>
			target.removeEventListener(event, listener)
		);

		this._events = [];
	}

	_addEventListener(target, event, fn) {
		target.addEventListener(event, fn);
		this._events.push({event, fn, target});
	}

	_attachEvents() {
		const publishButton = this.rootNode.querySelector(
			'.sheet-footer button[type="submit"]'
		);

		if (publishButton) {
			this._addEventListener(publishButton, 'click', () => {
				this.workflowActionInputNode.value =
					this._constants.ACTION_PUBLISH;
				this._saveFn();
			});
		}

		const saveDrafButton = document.getElementById(
			`${this._namespace}saveButton`
		);

		if (saveDrafButton) {
			this._addEventListener(saveDrafButton, 'click', () => {
				this.workflowActionInputNode.value =
					this._constants.ACTION_SAVE_DRAFT;
				this._saveFn();
			});
		}

		const advancedReplyLink =
			this.rootNode.querySelector('.advanced-reply');

		if (advancedReplyLink) {
			this._addEventListener(advancedReplyLink, 'click', () => {
				this._openAdvancedReply();
			});
		}

		const searchContainerId = `${this._namespace}messageAttachments`;

		Liferay.componentReady(searchContainerId).then((searchContainer) => {
			searchContainer
				.get('contentBox')
				.delegate(
					'click',
					this._confirmRemoveAttachment.bind(this),
					'.delete-attachment'
				);
		});

		this.searchContainerId = searchContainerId;

		const viewRemovedAttachmentsLink = document.getElementById(
			'view-removed-attachments-link'
		);

		if (viewRemovedAttachmentsLink) {
			this._addEventListener(viewRemovedAttachmentsLink, 'click', () => {
				openModal({
					id: this._namespace + 'openRemovedPageAttachments',
					onClose: this._updateRemovedAttachments.bind(this),
					title: Liferay.Language.get('removed-attachments'),
					url: this._viewTrashAttachmentsURL,
				});
			});
		}
	}

	/**
	 * Redirects to the advanced reply page
	 * keeping the current message.
	 *
	 */
	_openAdvancedReply() {
		const namespace = this._namespace;
		const replyToMessageId = this._replyToMessageId;

		const bodyInput = document.getElementById(`${namespace}body`);
		bodyInput.value =
			window[`${namespace}replyMessageBody${replyToMessageId}`].getHTML();

		const form = this.rootNode.querySelector(
			`[name="${namespace}advancedReplyFm${replyToMessageId}"]`
		);

		const advancedReplyInputNode = form.querySelector(
			`[name="${namespace}body"]`
		);

		advancedReplyInputNode.value = bodyInput.value;

		submitForm(form);
	}

	/**
	 * Show a confimation modal if trash is enabled
	 *
	 * @param {Event} event The click event that triggered the remove action
	 */
	_confirmRemoveAttachment(event) {
		event.preventDefault();

		if (this._trashEnabled) {
			this._removeAttachment(event);
		}
		else {
			openConfirmModal({
				message: Liferay.Language.get(
					'are-you-sure-you-want-to-delete-this'
				),
				onConfirm: (isConfirmed) => {
					if (!isConfirmed) {
						return;
					}

					this._removeAttachment(event);
				},
			});
		}
	}

	/**
	 * Sends a request to remove the selected attachment.
	 *
	 * @param {Event} event The click event that triggered the remove action
	 */
	_removeAttachment(event) {
		const link = event.currentTarget;
		const deleteURL = link.getAttribute('href');

		fetch(deleteURL).then(() => {
			Liferay.componentReady(this.searchContainerId).then(
				(searchContainer) => {
					searchContainer.deleteRow(
						link.ancestor('tr'),
						link.getAttribute('data-rowid')
					);
					searchContainer.updateDataStore();
				}
			);

			this._updateRemovedAttachments();
		});
	}

	/**
	 * Save the message. Before doing that, checks if there are
	 * images that have not been uploaded yet. In that case,
	 * it removes them after asking confirmation to the user.
	 *
	 */
	_saveFn() {
		const tempImages = this.rootNode.querySelectorAll(
			'img[data-random-id]'
		);

		if (tempImages.length) {
			openConfirmModal({
				message: this._strings.confirmDiscardImages,
				onConfirm: (isConfirmed) => {
					if (isConfirmed) {
						tempImages.forEach((node) => {
							node.parentElement.remove();
						});

						this._submitMBForm();
					}
				},
			});
		}
		else {
			this._submitMBForm();
		}
	}

	/**
	 * Submits the message.
	 *
	 */
	_submitMBForm() {
		const namespace = this._namespace;
		const replyToMessageId = this._replyToMessageId;

		document.getElementById(`${namespace}${this._constants.CMD}`).value =
			this._currentAction;

		const bodyInput = document.getElementById(`${namespace}body`);

		if (replyToMessageId) {
			bodyInput.value =
				window[
					`${namespace}replyMessageBody${replyToMessageId}`
				].getHTML();

			submitForm(
				document[`${namespace}addQuickReplyFm${replyToMessageId}`]
			);
		}
		else {
			bodyInput.value = window[`${namespace}bodyEditor`].getHTML();

			submitForm(document[`${namespace}fm`]);
		}
	}

	/**
	 * Sends a request to retrieve the deleted attachments
	 *
	 */
	_updateRemovedAttachments() {
		fetch(this._getAttachmentsURL)
			.then((res) => res.json())
			.then((attachments) => {
				if (attachments.active.length) {
					Liferay.componentReady(this.searchContainerId).then(
						(searchContainer) => {
							const searchContainerData =
								searchContainer.getData();

							document
								.getElementById(
									this._namespace + 'fileAttachments'
								)
								.classList.remove('hide');

							attachments.active.forEach((attachment) => {
								if (
									searchContainerData.indexOf(
										attachment.id
									) === -1
								) {
									searchContainer.addRow(
										[
											escapeHTML(attachment.title),
											attachment.size,
											`<a class="delete-attachment" data-rowId="${
												attachment.id
											}" href="${
												attachment.deleteURL
											}">${Liferay.Language.get(
												'delete'
											)}</a>`,
										],
										attachment.id.toString()
									);

									searchContainer.updateDataStore();
								}
							});
						}
					);
				}

				const viewRemovedAttachmentsLink = document.getElementById(
					'view-removed-attachments-link'
				);

				if (!viewRemovedAttachmentsLink) {
					return;
				}

				if (attachments.deleted.length) {
					viewRemovedAttachmentsLink.style.display = 'initial';
					viewRemovedAttachmentsLink.innerText =
						sub(
							attachments.deleted.length > 1
								? RECENTLY_REMOVED_ATTACHMENTS.multiple
								: RECENTLY_REMOVED_ATTACHMENTS.single,
							attachments.deleted.length
						) + ' &raquo';
				}
				else {
					viewRemovedAttachmentsLink.style.display = 'none';
				}
			});
	}
}

export default MBPortlet;
