/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

AUI.add(
	'liferay-contacts-center',
	(A) => {
		const AArray = A.Array;

		const Lang = A.Lang;

		const Node = A.Node;

		const ParseContent = A.Plugin.ParseContent;

		const CSS_NOT_SELECTED_CHECKBOX =
			'input[type="checkbox"]:not(:disabled):not(:checked)';

		const CSS_SELECTED_CHECKBOX =
			'input[type="checkbox"]:not(:disabled):checked';

		const STR_UNDEFINED = 'undefined';

		const TPL_BLOCK_IMG =
			'<span>' +
			'<span class="taglib-text">' +
			Liferay.Language.get('blocked') +
			'</span>' +
			'</span>';

		const TPL_CONNECTED_IMG =
			'<span>' +
			'<span class="taglib-text">' +
			Liferay.Language.get('connected') +
			'</span>' +
			'</span>';

		const TPL_CONNECTION_REQUESTED_IMG =
			'<span>' +
			'<span class="taglib-text">' +
			Liferay.Language.get('connection-requested') +
			'</span>' +
			'</span>';

		const TPL_ENTRY_DATA =
			'{lastNameAnchor}' +
			'<div class="lfr-contact">' +
			'<div class="lfr-contact-checkbox">' +
			'<input type="checkbox" value="{entryId}" disabled="true" name="contact-ids-{entryId}" class="contact-ids" label="" />' +
			'</div>' +
			'<div class="lfr-contact-grid-item" data-viewSummaryURL="{viewSummaryURL}" data-contactId="{entryId}">' +
			'<div class="lfr-contact-thumb">' +
			'<img alt="{fullName}" src="{portraitURL}" />' +
			'</div>' +
			'<div class="lfr-contact-info">' +
			'<div class="lfr-contact-name">' +
			'<a>{fullName}</a>' +
			'</div>' +
			'<div class="lfr-contact-extra">' +
			'{emailAddress}' +
			'</div>' +
			'</div>' +
			'<div class="clear"></div>' +
			'</div>' +
			'</div>';

		const TPL_ENTRY_DETAIL_DATA =
			'<div class="contacts-profile external-contact">' +
			'<div class="lfr-detail-info">' +
			'{icon}' +
			'<div class="{cssClass} lfr-contact-info">' +
			'<div class="lfr-contact-name">' +
			'{fullName}' +
			'</div>' +
			'<div class="lfr-contact-extra">' +
			'<a href="mailto:{emailAddress}">{emailAddress}</a>' +
			'</div>' +
			'</div>' +
			'</div>' +
			'<div class="lfr-detail-info">' +
			'<div class="comments">' +
			'{comments}' +
			'</div>' +
			'</div>' +
			'</div>';

		const TPL_FOLLOWING_IMG =
			'<span>' +
			'<i class="icon-user"></i>' +
			'<span class="taglib-text">' +
			Liferay.Language.get('following') +
			'</span>' +
			'</span>';

		const TPL_ICON =
			'<div class="lfr-contact-thumb">' +
			'<img alt="{fullName}" src="{portraitURL}" />' +
			'</div>';

		const TPL_NO_RESULTS =
			'<div class="empty">' +
			Liferay.Language.get('there-are-no-results') +
			'</div>';

		const TPL_USER_DATA =
			'{lastNameAnchor}' +
			'<div class="lfr-contact">' +
			'<div class="lfr-contact-checkbox">' +
			'<input type="checkbox" value="{userId}" {disabled} {checked} name="contact-ids-{userId}" class="contact-ids" label="" />' +
			'</div>' +
			'<div class="lfr-contact-grid-item" data-viewSummaryURL="{viewSummaryURL}" data-userId="{userId}">' +
			'<div class="lfr-contact-thumb">' +
			'<img alt="{fullName}" src="{portraitURL}" />' +
			'</div>' +
			'<div class="lfr-contact-info">' +
			'<div class="lfr-contact-name">' +
			'<a>{lastName} {firstName}</a>' +
			'</div>' +
			'<div class="lfr-contact-extra">' +
			'{emailAddress}' +
			'</div>' +
			'</div>' +
			'<div class="clear"></div>' +
			'</div>' +
			'</div>';

		const TPL_USER_DETAIL_DATA =
			'<div class="lfr-contact-grid-item lfr-selected-user {cssClass}" data-viewSummaryURL="{viewSummaryURL}" id="user-{userId}">' +
			'<input type="hidden" value="{userId}" name="selected-user-{userId}" />' +
			'<div class="lfr-contact-thumb">' +
			'<img alt="{fullName}" src="{portraitURL}" />' +
			'</div>' +
			'<div class="lfr-contact-info">' +
			'<div class="lfr-contact-name">' +
			'<a>{lastName}, {firstName}</a>' +
			'</div>' +
			'<div class="lfr-contact-extra">' +
			'{emailAddress}' +
			'</div>' +
			'<div class="lfr-social-relations">' +
			'{connectionRequested}' +
			'{connected}' +
			'{following}' +
			'{block}' +
			'</div>' +
			'</div>' +
			'<div class="clear"></div>' +
			'</div>';

		const ContactsResult = A.Base.create(
			'contactsResult',
			A.Base,
			[A.AutoCompleteBase],
			{
				initializer(config) {
					this._listNode = A.one(config.listNode);

					this._bindUIACBase();
					this._syncUIACBase();
				},
			}
		);

		const ContactsCenter = A.Component.create({
			AUGMENTS: [Liferay.PortletBase],

			EXTENDS: A.Base,

			NAME: 'contactscenter',

			prototype: {
				_clearContactResult() {
					const instance = this;

					instance._detailUserView.empty();

					instance._selectedUsersView.empty();

					instance._messageContainer.empty();

					A.all('.lfr-contact .lfr-contact-checkbox input').set(
						'checked',
						false
					);

					instance._buttonAddConnectionUserIds.length = 0;
					instance._buttonBlockUserIds.length = 0;
					instance._buttonExportUserIds.length = 0;
					instance._buttonFollowUserIds.length = 0;
					instance._buttonRemoveConnectionUserIds.length = 0;
					instance._buttonUnBlockUserIds.length = 0;
					instance._buttonUnFollowUserIds.length = 0;

					A.all('.contacts-container .toolbar button').hide();

					if (instance._sendMessageButton) {
						instance._showButton(instance._sendMessageButton);
					}

					instance._contactCenterToolbar.hide();

					instance._numSelectedContacts = 0;

					instance._checkAll.set('checked', false);
				},

				_createContactList(config) {
					const instance = this;

					const contactsResult = config.contactsResult;
					const contactsResultURL = config.contactsResultURL;
					const contactsSearchInput = config.contactsSearchInput;

					const contactList = new ContactsResult({
						inputNode: contactsSearchInput,
						listNode: contactsResult,
						minQueryLength: 0,
						requestTemplate(query) {
							const data = {};

							data[instance._namespace + 'keywords'] = query;

							return data;
						},
						resultTextLocator(response) {
							let result = '';

							if (typeof response.toString !== STR_UNDEFINED) {
								result = response.toString();
							}
							else if (
								typeof response.responseText !== STR_UNDEFINED
							) {
								result = response.responseText;
							}

							return result;
						},
						source: instance._createDataSource(contactsResultURL),
					});

					contactList.on(
						'results',
						A.bind(instance._updateContactsResult, instance)
					);

					instance._contactList = contactList;
				},

				_createDataSource(url) {
					const instance = this;

					return new A.DataSource.IO({
						ioConfig: {
							method: 'POST',
						},
						on: {
							request(event) {
								const contactFilter = A.one(
									'#' + instance._namespace + 'filterBy'
								);

								let filterBy = '0';

								if (contactFilter) {
									filterBy = contactFilter.get('value');
								}

								const eventData = {};

								const data = event.request;

								eventData[instance._namespace + 'end'] =
									data[instance._namespace + 'end'] ||
									instance._maxResultCount;
								eventData[instance._namespace + 'filterBy'] =
									data[instance._namespace + 'filterBy'] ||
									filterBy;
								eventData[instance._namespace + 'keywords'] =
									data[instance._namespace + 'keywords'] ||
									'';
								eventData[instance._namespace + 'start'] =
									data[instance._namespace + 'start'] || 0;

								event.cfg.data = eventData;
							},
						},
						source: url,
					});
				},

				_deleteEntry(contact) {
					const instance = this;

					const config = instance._config;

					const confirmMessageText = Lang.sub(
						Liferay.Language.get(
							'are-you-sure-you-want-to-delete-x-from-your-contacts'
						),
						[contact.fullName]
					);

					Liferay.Util.openConfirmModal({
						message: confirmMessageText,
						onConfirm: (isConfirmed) => {
							if (isConfirmed) {
								const url =
									Liferay.Util.PortletURL.createActionURL(
										config.baseActionURL,
										{
											'entryId': contact.entryId,
											'jakarta.portlet.action':
												'deleteEntry',
											'p_p_state': 'NORMAL',
										}
									);

								Liferay.Util.fetch(url)
									.then((response) => {
										return response.text();
									})
									.then(() => {
										location.href = contact.redirect;
									})
									.catch(() => {
										instance.showMessage(false);
									});
							}
						},
					});
				},

				_editEntry(contact) {
					const instance = this;

					const config = instance._config;

					const url = Liferay.Util.PortletURL.createRenderURL(
						config.baseRenderURL,
						{
							entryId: contact.entryId,
							mvcPath: '/contacts_center/edit_entry.jsp',
							p_p_state: 'EXCLUSIVE',
							redirect: contact.redirect,
						}
					);

					instance.showPopup(
						Liferay.Language.get('update-contact'),
						url.toString()
					);
				},

				_getPopup() {
					const instance = this;

					if (!instance._popup) {
						const contactsPortlet = A.one('.contacts-portlet');

						instance._popup = Liferay.Util.Window.getWindow({
							dialog: {
								align: {
									node: contactsPortlet,
									points: ['tc', 'tc'],
								},
								constrain2view: true,
								cssClass: 'contact-dialog',
								modal: true,
								resizable: false,
								width: 500,
							},
						})
							.plug(A.Plugin.IO, {
								autoLoad: false,
							})
							.render();
					}
				},

				_getRequestTemplate(filterBy) {
					const instance = this;

					return function (query) {
						const data = {};

						data[instance._namespace + 'end'] =
							instance._maxResultCount;
						data[instance._namespace + 'filterBy'] = filterBy;
						data[instance._namespace + 'keywords'] = query;
						data[instance._namespace + 'start'] = 0;

						return data;
					};
				},

				_onCheckAll(event) {
					const instance = this;

					const config = instance._config;

					const checkBox = event.target;

					let contacts;

					if (checkBox.get('checked')) {
						contacts = instance._contactsCheckBox.all(
							CSS_NOT_SELECTED_CHECKBOX
						);

						if (contacts.size() > 0) {
							contacts.set('checked', true);

							let uri = config.getSelectedContactsURL;

							const userIds = contacts.val();

							uri =
								Liferay.Util.addParams(
									instance._namespace +
										'userIds=' +
										userIds.join(),
									uri
								) || uri;

							Liferay.Util.fetch(uri)
								.then((response) => {
									return response.json();
								})
								.then((data) => {
									instance.addContactResults(data);
								})
								.catch(() => {
									instance.showMessage(false);
								});
						}
					}
					else {
						contacts = instance._contactsCheckBox.all(
							CSS_SELECTED_CHECKBOX
						);

						if (contacts.size() > 0) {
							contacts.set('checked', false);

							instance.deleteContactResults(contacts.val());
						}
					}
				},

				_renderContact(data) {
					const instance = this;

					const user = data.user;

					return Lang.sub(TPL_USER_DETAIL_DATA, {
						block: user.block ? TPL_BLOCK_IMG : '',
						connected: user.connected ? TPL_CONNECTED_IMG : '',
						connectionRequested: user.connectionRequested
							? TPL_CONNECTION_REQUESTED_IMG
							: '',
						cssClass:
							instance._numSelectedContacts % 2 === 0
								? ''
								: 'alt',
						emailAddress: user.emailAddress,
						firstName: user.firstName,
						following: user.following ? TPL_FOLLOWING_IMG : '',
						fullName: user.fullName,
						lastName: user.lastName,
						portraitURL: user.portraitURL,
						userId: user.userId,
						viewSummaryURL: user.viewSummaryURL,
					});
				},

				_renderEntryDetailView(contact) {
					const instance = this;

					let icon = '';

					if (instance._showIcon) {
						icon = Lang.sub(TPL_ICON, {
							fullName: contact.fullName,
							portraitURL: contact.portraitURL,
						});
					}

					const contactSummary = Lang.sub(TPL_ENTRY_DETAIL_DATA, {
						comments: contact.comments,
						cssClass: instance._showIcon ? '' : 'no-icon',
						emailAddress: contact.emailAddress,
						fullName: contact.fullName,
						icon: instance._showIcon ? icon : '',
					});

					instance._detailUserView.setContent(contactSummary);
				},

				_renderEntryToolbar(contact) {
					const instance = this;

					instance._userToolbar.empty();

					instance._toolbar = new A.Toolbar({
						activeState: false,
						boundingBox: instance._userToolbar,
						children: [
							{
								icon: 'icon-edit',
								label: Liferay.Language.get('edit'),
								on: {
									click() {
										instance._editEntry(contact);
									},
								},
							},
							{
								icon: 'icon-remove',
								label: Liferay.Language.get('delete'),
								on: {
									click() {
										instance._deleteEntry(contact);
									},
								},
							},
						],
					}).render();
				},

				_renderResult(data, displayMessage, lastNameAnchor) {
					const count = data.count;
					const results = data.users;

					const options = data.options;

					const buffer = [];

					if (!results.length && displayMessage) {
						buffer.push(TPL_NO_RESULTS);
					}
					else {
						const selectedUsersNodes = A.all(
							'.lfr-contact-grid-item input'
						);

						let selectedUsersIds = [];

						if (selectedUsersNodes.size() > 0) {
							selectedUsersIds = selectedUsersNodes.val();
						}

						buffer.push(
							results
								.map((result) => {
									let displayLastNameAnchor = false;

									let nameAnchor;

									let name;

									if (result.portalUser) {
										name = result.lastName;
									}
									else {
										name = result.fullName;
									}

									if (name) {
										nameAnchor = name
											.substring(0, 1)
											.toUpperCase();
									}
									else {
										nameAnchor =
											Liferay.Language.get(
												'no-last-name'
											);
									}

									if (nameAnchor !== lastNameAnchor) {
										displayLastNameAnchor = true;

										lastNameAnchor = nameAnchor;
									}

									let str;

									if (result.portalUser) {
										str = Lang.sub(TPL_USER_DATA, {
											checked:
												selectedUsersIds.indexOf(
													result.userId
												) !== -1
													? 'checked="true"'
													: '',
											disabled:
												themeDisplay.getUserId() ===
												result.userId
													? 'disabled="true"'
													: '',
											emailAddress: result.emailAddress
												? result.emailAddress
												: '',
											firstName: result.firstName
												? result.firstName
												: '',
											fullName: result.fullName
												? result.fullName
												: '',
											lastName: result.lastName
												? result.lastName + ','
												: '',
											lastNameAnchor:
												displayLastNameAnchor
													? '<div class="last-name-anchor"><a>' +
														lastNameAnchor +
														'</a></div>'
													: '',
											portraitURL: result.portraitURL
												? result.portraitURL
												: '',
											userId: result.userId,
											viewSummaryURL:
												result.viewSummaryURL
													? result.viewSummaryURL
													: '',
										});
									}
									else {
										str = Lang.sub(TPL_ENTRY_DATA, {
											emailAddress: result.emailAddress
												? result.emailAddress
												: '',
											entryId: result.entryId,
											fullName: result.fullName
												? result.fullName
												: '',
											lastNameAnchor:
												displayLastNameAnchor
													? '<div class="last-name-anchor"><a>' +
														lastNameAnchor +
														'</a></div>'
													: '',
											portraitURL: result.portraitURL
												? result.portraitURL
												: '',
											viewSummaryURL:
												result.viewSummaryURL
													? result.viewSummaryURL
													: '',
										});
									}

									return str;
								})
								.join('')
						);
					}

					if (count > options.end) {
						buffer.push(
							'<div class="more-results">' +
								'<a href="javascript:void(0);" data-end="' +
								options.end +
								'" data-lastNameAnchor="' +
								lastNameAnchor +
								'">' +
								Liferay.Language.get('view-more') +
								' (' +
								(count - options.end) +
								')' +
								'</a>' +
								'</div>'
						);
					}

					return buffer;
				},

				_setVisibleDetailUserView() {
					const instance = this;

					instance._contactCenterToolbar.hide();

					instance._detailUserView.show();

					instance._selectedUsersView.hide();
				},

				_setVisibleSelectedUsersView() {
					const instance = this;

					instance._userToolbar.empty();

					instance._contactCenterToolbar.show();

					instance._detailUserView.hide();

					instance._selectedUsersView.show();
				},

				_showButton(node) {
					node.show();

					node.removeClass('hidden');
				},

				_updateContactsResult(event) {
					const instance = this;

					const data = JSON.parse(event.data.responseText);

					const buffer = instance._renderResult(data, true, ' ');

					event.currentTarget._listNode.html(buffer.join(''));

					instance._contactsCheckBox = A.one('.contacts-result');

					instance._checkAll.set(
						'checked',
						instance._contactsCheckBox
							.all(CSS_NOT_SELECTED_CHECKBOX)
							.size() <= 0
					);
				},

				_updateToolbarButtonsAdd(data) {
					const instance = this;

					const user = data.user;

					if (user.block) {
						if (instance._unblockButton) {
							instance._showButton(instance._unblockButton);

							instance._buttonUnBlockUserIds.push(user.userId);
						}
					}
					else {
						if (instance._blockButton) {
							instance._showButton(instance._blockButton);

							instance._buttonBlockUserIds.push(user.userId);
						}

						if (!user.connectionRequested) {
							if (user.connected) {
								if (instance._removeConnectionButton) {
									instance._showButton(
										instance._removeConnectionButton
									);

									instance._buttonRemoveConnectionUserIds.push(
										user.userId
									);
								}
							}
							else if (instance._addConnectionButton) {
								instance._showButton(
									instance._addConnectionButton
								);

								instance._buttonAddConnectionUserIds.push(
									user.userId
								);
							}
						}

						if (user.following) {
							if (instance._unfollowButton) {
								instance._showButton(instance._unfollowButton);

								instance._buttonUnFollowUserIds.push(
									user.userId
								);
							}
						}
						else if (instance._followButton) {
							instance._showButton(instance._followButton);

							instance._buttonFollowUserIds.push(user.userId);
						}

						if (instance._exportButton) {
							instance._showButton(instance._exportButton);

							instance._buttonExportUserIds.push(user.userId);
						}
					}
				},

				_updateToolbarButtonsRemove(userId) {
					const instance = this;

					const blockUserIdIndex =
						instance._buttonBlockUserIds.indexOf(userId);

					if (blockUserIdIndex >= 0) {
						AArray.remove(
							instance._buttonBlockUserIds,
							blockUserIdIndex
						);

						if (
							instance._blockButton &&
							instance._buttonBlockUserIds.length <= 0
						) {
							instance._blockButton.hide();
						}
					}

					const unblockUserIdIndex =
						instance._buttonUnBlockUserIds.indexOf(userId);

					if (unblockUserIdIndex >= 0) {
						AArray.remove(
							instance._buttonUnBlockUserIds,
							unblockUserIdIndex
						);

						if (
							instance._unblockButton &&
							instance._buttonUnBlockUserIds.length <= 0
						) {
							instance._unblockButton.hide();
						}
					}

					const addConnectionUserIdIndex =
						instance._buttonAddConnectionUserIds.indexOf(userId);

					if (addConnectionUserIdIndex >= 0) {
						AArray.remove(
							instance._buttonAddConnectionUserIds,
							addConnectionUserIdIndex
						);

						if (
							instance._addConnectionButton &&
							instance._buttonAddConnectionUserIds.length <= 0
						) {
							instance._addConnectionButton.hide();
						}
					}

					const removeConnectionUserIdIndex =
						instance._buttonRemoveConnectionUserIds.indexOf(userId);

					if (removeConnectionUserIdIndex >= 0) {
						AArray.remove(
							instance._buttonRemoveConnectionUserIds,
							removeConnectionUserIdIndex
						);

						if (
							instance._removeConnectionButton &&
							instance._buttonRemoveConnectionUserIds.length <= 0
						) {
							instance._removeConnectionButton.hide();
						}
					}

					const followUserIdIndex =
						instance._buttonFollowUserIds.indexOf(userId);

					if (followUserIdIndex >= 0) {
						AArray.remove(
							instance._buttonFollowUserIds,
							followUserIdIndex
						);

						if (
							instance._followButton &&
							instance._buttonFollowUserIds.length <= 0
						) {
							instance._followButton.hide();
						}
					}

					const unFollowUserIdIndex =
						instance._buttonUnFollowUserIds.indexOf(userId);

					if (unFollowUserIdIndex >= 0) {
						AArray.remove(
							instance._buttonUnFollowUserIds,
							unFollowUserIdIndex
						);

						if (
							instance._unfollowButton &&
							instance._buttonUnFollowUserIds.length <= 0
						) {
							instance._unfollowButton.hide();
						}
					}

					const exportUserIdIndex =
						instance._buttonExportUserIds.indexOf(userId);

					if (exportUserIdIndex >= 0) {
						AArray.remove(
							instance._buttonExportUserIds,
							exportUserIdIndex
						);

						if (
							instance._exportButton &&
							instance._buttonExportUserIds.length <= 0
						) {
							instance._exportButton.hide();
						}
					}
				},

				_updateUserIcons(user) {
					const contactsAction = A.one('.contacts-action');

					contactsAction.hide();

					const blockIcon = contactsAction.one('.block');
					const connectedIcon = contactsAction.one('.connected');
					const disabledIcon = contactsAction.one('.disabled');
					const followingIcon = contactsAction.one('.following');

					if (user.block) {
						blockIcon.show();
						connectedIcon.hide();
						disabledIcon.hide();
						followingIcon.hide();
					}
					else {
						blockIcon.hide();

						if (user.connectionRequested) {
							connectedIcon.hide();
							disabledIcon.show();
						}
						else if (user.connected) {
							connectedIcon.show();
							disabledIcon.hide();
						}
						else {
							connectedIcon.hide();
							disabledIcon.hide();
						}

						if (user.following) {
							followingIcon.show();
						}
						else {
							followingIcon.hide();
						}
					}

					contactsAction.show();
				},

				_updateUserToolBar(user) {
					const instance = this;

					instance._userToolbar.hide();

					const addConnectionButton = instance.byId(
						'addConnectionButton'
					);
					const blockButton = instance.byId('blockButton');
					const followButton = instance.byId('followButton');
					const removeConnectionButton = instance.byId(
						'removeConnectionButton'
					);
					const unblockButton = instance.byId('unblockButton');
					const unfollowButton = instance.byId('unfollowButton');

					if (user.block) {
						addConnectionButton.hide();
						blockButton.hide();
						followButton.hide();
						removeConnectionButton.hide();
						instance._showButton(unblockButton);
						unfollowButton.hide();
					}
					else {
						instance._showButton(blockButton);
						unblockButton.hide();

						if (user.connectionRequested) {
							addConnectionButton.hide();
							removeConnectionButton.hide();
						}
						else if (user.connected) {
							addConnectionButton.hide();
							instance._showButton(removeConnectionButton);
						}
						else {
							instance._showButton(addConnectionButton);
							removeConnectionButton.hide();
						}

						if (user.following) {
							followButton.hide();
							instance._showButton(unfollowButton);
						}
						else {
							instance._showButton(followButton);
							unfollowButton.hide();
						}
					}

					instance._userToolbar.show();
				},

				addContactResult(data) {
					const instance = this;

					instance._setVisibleSelectedUsersView();

					const contact = instance._renderContact(data);

					if (instance._numSelectedContacts <= 0) {
						instance._selectedUsersView.html(contact);

						instance._contactCenterToolbar.show();

						instance._userToolbar.empty();
					}
					else {
						instance._selectedUsersView.append(contact);
					}

					instance._numSelectedContacts++;

					instance._updateToolbarButtonsAdd(data);

					instance._messageContainer.empty();

					instance._checkAll.set(
						'checked',
						instance._contactsCheckBox
							.all(CSS_NOT_SELECTED_CHECKBOX)
							.size() <= 0
					);
				},

				addContactResults(data) {
					const instance = this;

					const contacts = data.contacts;

					contacts.map((contact) => {
						instance.addContactResult(contact);
					});
				},

				closePopup() {
					const instance = this;

					if (instance._popup) {
						instance._popup.hide();
					}
				},

				deleteContactResult(userId) {
					const instance = this;

					instance._setVisibleSelectedUsersView();

					const user = A.one('#user-' + userId);

					if (user) {
						user.remove(true);

						instance._numSelectedContacts--;

						instance._updateToolbarButtonsRemove(userId);

						if (instance._numSelectedContacts <= 0) {
							instance._clearContactResult();
						}

						instance._messageContainer.empty();

						instance._checkAll.set('checked', false);
					}
				},

				deleteContactResults(userIds) {
					const instance = this;

					userIds.map((userId) => {
						instance.deleteContactResult(userId);
					});
				},

				initializer(config) {
					const instance = this;

					instance._config = config;

					instance._namespace = config.namespace;

					instance._contactCenterToolbar = instance.byId(
						'contactCenterToolbarButtons'
					);
					instance._userToolbar = instance.byId('userToolbarButtons');

					instance._detailUserView = instance.byId('detailUserView');
					instance._selectedUsersView =
						instance.byId('selectedUsersView');

					instance._messageContainer =
						instance.byId('messageContainer');

					instance._addConnectionButton = instance.byId(
						'addConnectionButton'
					);
					instance._blockButton = instance.byId('blockButton');
					instance._exportButton = instance.byId('exportButton');
					instance._followButton = instance.byId('followButton');
					instance._removeConnectionButton = instance.byId(
						'removeConnectionButton'
					);
					instance._sendMessageButton =
						instance.byId('sendMessageButton');
					instance._unblockButton = instance.byId('unblockButton');
					instance._unfollowButton = instance.byId('unfollowButton');

					instance._contactsCheckBox = A.one('.contacts-result');

					instance._checkAll = instance.byId('checkAll');

					instance._checkAll.on(
						'click',
						instance._onCheckAll,
						instance
					);

					instance._buttonAddConnectionUserIds = [];
					instance._buttonBlockUserIds = [];
					instance._buttonExportUserIds = [];
					instance._buttonFollowUserIds = [];
					instance._buttonRemoveConnectionUserIds = [];
					instance._buttonUnBlockUserIds = [];
					instance._buttonUnFollowUserIds = [];

					instance._numSelectedContacts = 0;

					instance._createContactList(config);

					instance._defaultMessageError = config.defaultMessageError;
					instance._defaultMessageSuccess =
						config.defaultMessageSuccess;

					instance._maxResultCount = config.maxResultCount;

					instance._showIcon = config.showIcon;
				},

				renderContent(data, clear) {
					const instance = this;

					if (clear) {
						instance._clearContactResult();
					}

					instance._setVisibleDetailUserView();

					const content = Node.create(data);

					const contactSummary = instance.one(
						'#contactSummary',
						content
					);

					if (contactSummary) {
						instance._detailUserView.empty();

						instance._detailUserView.plug(ParseContent);

						instance._detailUserView.setContent(contactSummary);
					}

					const userToolbar = instance.one(
						'#contactsToolbar',
						content
					);

					if (userToolbar) {
						instance._userToolbar.empty();

						instance._userToolbar.plug(ParseContent);

						instance._userToolbar.setContent(userToolbar);
					}
				},

				renderEntry(data, lastNameAnchor) {
					const instance = this;

					const contact = data.contact;

					instance._renderEntryDetailView(contact);
					instance._renderEntryToolbar(contact);

					const contactList = data.contactList;

					const contactUserHTML = instance._renderResult(
						contactList,
						true,
						lastNameAnchor
					);

					const contactResultContent = A.one(
						'.contacts-portlet .contacts-result'
					);

					contactResultContent.html(contactUserHTML.join(''));

					instance.showMessage(true, data.message);
				},

				renderSelectedContacts(data, lastNameAnchor) {
					const instance = this;

					const contacts = data.contacts;

					if (contacts && !!contacts.length) {
						if (
							!instance._detailUserView.hasClass('hide') &&
							contacts.length === 1
						) {
							const user = contacts[0].user;

							instance._updateUserToolBar(user);
							instance._updateUserIcons(user);
						}
						else {
							instance._clearContactResult();

							contacts.map((contact) => {
								instance.addContactResult(contact);
							});
						}
					}

					const contactList = data.contactList;

					const contactUserHTML = instance._renderResult(
						contactList,
						true,
						lastNameAnchor
					);

					const contactResultContent = A.one(
						'.contacts-portlet .contacts-result'
					);

					contactResultContent.html(contactUserHTML.join(''));

					instance.showMessage(true, data.message);
				},

				showMessage(success, message) {
					const instance = this;

					if (instance._messageContainer) {
						if (success) {
							if (!message || message === '') {
								message = instance._defaultMessageSuccess;
							}

							instance._messageContainer.html(
								'<span class="alert alert-success">' +
									message +
									'</span>'
							);
						}
						else {
							if (!message || message === '') {
								message = instance._defaultMessageError;
							}

							instance._messageContainer.html(
								'<span class="alert alert-danger">' +
									message +
									'</span>'
							);
						}
					}
				},

				showMoreResult(responseData, lastNameAnchor) {
					const instance = this;

					const contactUserHTML = instance._renderResult(
						responseData,
						false,
						lastNameAnchor
					);

					const contactResultContent = A.one(
						'.contacts-portlet .contacts-result'
					);
					const moreResults = A.one(
						'.contacts-portlet .more-results'
					);

					moreResults.remove();

					contactResultContent.append(contactUserHTML.join(''));
				},

				showPopup(title, uri) {
					const instance = this;

					instance._getPopup();

					instance._popup.show();

					instance._popup.titleNode.html(title);

					instance._popup.io.set('uri', uri);

					instance._popup.io.start();
				},

				updateContacts(keywords, filterBy) {
					const instance = this;

					if (instance._contactList) {
						instance._contactList.sendRequest(
							keywords,
							instance._getRequestTemplate(filterBy)
						);
					}
				},
			},
		});

		Liferay.ContactsCenter = ContactsCenter;
	},
	'',
	{
		requires: [
			'aui-io-plugin-deprecated',
			'aui-toolbar',
			'autocomplete-base',
			'datasource-io',
			'json-parse',
			'liferay-portlet-base',
			'liferay-util-window',
		],
	}
);
