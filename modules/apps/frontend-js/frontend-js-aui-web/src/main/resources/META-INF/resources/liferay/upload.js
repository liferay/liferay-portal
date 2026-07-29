/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

const SCRIPT_URL = document.currentScript.src;

AUI.add(
	'liferay-upload',
	(A) => {
		const AArray = A.Array;
		const Lang = A.Lang;
		const UploaderQueue = A.Uploader.Queue;

		const STATUS_CODE = Liferay.STATUS_CODE;
		const STRINGS = 'strings';

		const STR_PARAM_FALLBACK = 'uploader=fallback';

		const TPL_ERROR_MESSAGE = '<div class="alert alert-danger">{0}</div>';

		const TPL_FILE_LIST = [
			'<tpl for=".">',
			'<tpl if="!values.error">',
			'<li class="upload-file {[ values.temp ? "upload-complete pending-file selectable" : "" ]} {[ values.selected ? "selected" : "" ]}" data-fileId="{id}" data-fileName="{[ LString.escapeHTML(values.name) ]}" data-title="{[ LString.escapeHTML(values.title ? values.title : values.name) ]}" id="{id}">',
			'<div class="form-check form-check-card form-check-middle-left">',
			'<div class="custom-checkbox custom-control">',
			'<label>',
			'<input class="custom-control-input entry-selector select-file" data-fileName="{[ LString.escapeHTML(values.name) ]}" data-title="{[ LString.escapeHTML(values.title ? values.title : values.name) ]}" {[ !values.temp ? "disabled" : "" ]} id="{id}checkbox" name="{$ns}selectUploadedFile" type="{[ this.multipleFiles ? "checkbox" : "hidden" ]}" value="{[ LString.escapeHTML(values.name) ]}" />',
			'<span class="custom-control-label"></span>',
			'<div class="card card-horizontal">',
			'<div class="card-body">',
			'<div class="card-row">',
			'<div class="autofit-col">',
			'<span class="sticker sticker-rounded">',
			'<span class="sticker-overlay inline-item">',
			Liferay.Util.getLexiconIconTpl('document'),
			'</span>',
			'</span>',
			'</div>',
			'<div class="autofit-col autofit-col-expand autofit-col-gutters clamp-horizontal">',
			'<div class="clamp-container">',
			'<span class="file-title text-truncate" title="{[ LString.escapeHTML(values.title ? values.title : values.name) ]}">{[ LString.escapeHTML(values.title ? values.title : values.name) ]}</span>',
			'</div>',
			'<span class="progress">',
			'<span class="progress-bar progress-bar-animated" id="{id}progress"></span>',
			'</span>',
			'</div>',
			'<div class="autofit-col delete-button-col">',
			'<a class="delete-button lfr-button" href="javascript:void(0);" id="{id}deleteButton" title="{[ this.strings.deleteFileText ]}">',
			Liferay.Util.getLexiconIconTpl('times'),
			'</a>',
			'</div>',

			'<a class="cancel-button lfr-button" href="javascript:void(0);" id="{id}cancelButton">',
			Liferay.Util.getLexiconIconTpl('times'),
			'<span class="cancel-button-text">{[ this.strings.cancelFileText ]}</span>',
			'</a>',
			'</div>',
			'</div>',
			'</div>',
			'</label>',
			'</div>',
			'</div>',
			'</li>',
			'</tpl>',

			'<tpl if="values.error && this.multipleFiles">',
			'<li class="upload-error upload-file" data-fileId="{id}" id="{id}">',
			'<span class="file-title" title="{[ LString.escapeHTML(values.name) ]}">{[ LString.escapeHTML(values.name) ]}</span>',

			'<span class="error-message" title="{[ LString.escapeHTML(values.error) ]}">{[ LString.escapeHTML(values.error) ]}</span>',

			'<tpl if="values.messageListItems && (values.messageListItems.length > 0)">',
			'<ul class="error-list-items">',
			'<tpl for="messageListItems">',
			'<li>{[ LString.escapeHTML(values.type) ]}: <strong>{[ LString.escapeHTML(values.name) ]}</strong>',
			'<tpl if="info">',
			'<span class="error-info"">({[ LString.escapeHTML(values.info) ]})</span>',
			'</tpl>',
			'</li>',
			'</tpl>',
			'</ul>',
			'</tpl>',
			'</li>',
			'</tpl>',

			'<tpl if="values.error && !this.multipleFiles">',
			'<li class="alert alert-danger upload-error" data-fileId="{id}" id="{id}">',
			'<div class="h4 upload-error-message">{[ Lang.sub(this.strings.fileCannotBeSavedText, [LString.escapeHTML(values.name)]) ]}</div>',

			'<span class="error-message" title="{[ LString.escapeHTML(values.error) ]}">{[ LString.escapeHTML(values.error) ]}</span>',

			'<tpl if="values.messageListItems && (values.messageListItems.length > 0)">',
			'<ul class="error-list-items">',
			'<tpl for="messageListItems">',
			'<li>{[ LString.escapeHTML(values.type) ]}: <strong>{[ LString.escapeHTML(values.name) ]}</strong>',
			'<tpl if="info">',
			'<span class="error-info"">({[ LString.escapeHTML(values.info) ]})</span>',
			'</tpl>',
			'</li>',
			'</tpl>',
			'</ul>',
			'</tpl>',
			'</li>',
			'</tpl>',

			'<tpl if="values.warningMessages && (values.warningMessages.length > 0)">',
			'<li class="alert upload-error" data-fileId="{id}" id="{id}">',
			'<span class="error-message" title="{[ LString.escapeHTML(values.error ? this.strings.warningFailureText : this.strings.warningText) ]}">{[ values.error ? this.strings.warningFailureText : this.strings.warningText ]}</span>',

			'<ul class="error-list-items">',
			'<tpl for="warningMessages">',
			'<li>{[ LString.escapeHTML(values.type) ]} <strong>({size})</strong>:',
			'<tpl if="info">',
			'<span class="error-info"">{[ LString.escapeHTML(values.info) ]}</span>',
			'</tpl>',
			'</li>',
			'</tpl>',
			'</ul>',
			'</li>',
			'</tpl>',
			'</tpl>',
		];

		const TPL_UPLOAD = [
			'<div class="upload-target" id="{$ns}uploader">',
			'<div class="drag-drop-area" id="{$ns}uploaderContent">',
			'<tpl if="this.uploaderType == \'html5\'">',
			'<span class="drop-file-text">{[ this.dropFileText ]}<span class="small">{[ this.strings.orText ]}</span></span>',
			'</tpl>',

			'<span class="select-files-container" id="{$ns}selectFilesButton">',
			'<button class="btn btn-secondary" type="button">{[ this.selectFilesText ]}</button>',
			'</span>',
			'</div>',
			'</div>',

			'<div class="hide upload-list-info" id="{$ns}listInfo">',
			'<div class="h4">{[ this.strings.uploadsCompleteText ]}</div>',
			'</div>',

			'<div class="alert alert-warning hide pending-files-info" role="alert">',
			'<span class="alert-indicator">',
			Liferay.Util.getLexiconIconTpl('warning'),
			'</span>',
			'<strong class="lead">{[ this.strings.warningTitle ]}</strong>{[ this.strings.pendingFileText ]}',
			'</div>',

			'<div class="clearfix hide manage-upload-target" id="{$ns}manageUploadTarget">',
			'<tpl if="multipleFiles">',
			'<div class="form-check select-files">',
			'<input class="form-check-input select-all-files" id="{$ns}allRowIds" name="{$ns}allRowIds" type="checkbox" />',
			'<label class="form-check-label" for="{$ns}allRowIds"> {[ this.strings.selectAllText ]}</label>',
			'</div>',
			'</tpl>',

			'<a class="cancel-uploads hide lfr-button" href="javascript:void(0);">{[ this.cancelUploadsText ]}</a>',
			'<a class="clear-uploads hide lfr-button" href="javascript:void(0);">{[ this.strings.clearRecentUploadsText ]}</a>',
			'</div>',

			'<div class="upload-list" id="{$ns}fileList">',
			'<ul class="list-unstyled {[ this.multipleFiles ? "multiple-files" : "single-file" ]}" id="{$ns}fileListContent"></ul>',
			'</div>',
		];

		const UPLOADER_TYPE = A.Uploader.TYPE || 'none';

		/**
		 * OPTIONS
		 *
		 * Required
		 * container {string|object}: The container where the uploader will be placed.
		 * deleteFile {string}: The URL that will handle the deleting of the pending files.
		 * maxFileSize {number}: The maximum file size that can be uploaded.
		 * tempFileURL {string|object}: The URL or configuration of the service to retrieve the pending files.
		 * uploadFile {string}: The URL to where the file will be uploaded.
		 *
		 * Optional
		 * fallbackContainer {string|object}: A selector or DOM element of the container holding a fallback (in case flash is not supported).
		 * metadataContainer {string}: Metadata container.
		 * metadataExplanationContainer {string}: A container explaining how to save metadata.
		 * namespace {string}: A unique string so that the global callback methods don't collide.
		 */

		const Upload = A.Component.create({
			ATTRS: {
				deleteFile: {
					value: '',
				},

				fallback: {
					setter: A.one,
					value: null,
				},

				maxFileSize: {
					setter: Lang.toInt,
					value: Liferay.PropsValues
						.UPLOAD_SERVLET_REQUEST_IMPL_MAX_SIZE,
				},

				metadataContainer: {
					setter: A.one,
					value: null,
				},

				metadataExplanationContainer: {
					setter: A.one,
					value: null,
				},

				multipleFiles: {
					validator: Lang.isBoolean,
					value: true,
				},

				removeOnComplete: {
					validator: Lang.isBoolean,
					value: false,
				},

				render: {
					value: true,
				},

				restoreState: {
					validator: Lang.isBoolean,
					value: true,
				},

				rootElement: {
					setter: A.one,
					value: null,
				},

				simultaneousUploads: {
					validator: Lang.isNumber,
					value: 2,
				},

				strings: {
					value: {
						allFilesSelectedText:
							Liferay.Language.get('all-files-selected'),
						cancelFileText: Liferay.Language.get('cancel-upload'),
						cancelUploadsText:
							Liferay.Language.get('cancel-all-uploads'),
						clearRecentUploadsText: Liferay.Language.get(
							'clear-documents-already-saved'
						),
						deleteFileText: Liferay.Language.get('delete-file'),
						dropFileText: Liferay.Language.get(
							'drop-file-here-to-upload'
						),
						dropFilesText: Liferay.Language.get(
							'drop-files-here-to-upload'
						),
						fileCannotBeSavedText: Liferay.Language.get(
							'the-file-x-cannot-be-saved'
						),
						invalidFileNameText: Liferay.Language.get(
							'please-enter-a-file-with-a-valid-file-name'
						),
						invalidFileSizeText: Liferay.Language.get(
							'please-enter-a-file-with-a-valid-file-size-no-larger-than-x'
						),
						invalidUploadRequestSizeText: Liferay.Language.get(
							'request-is-larger-than-x-and-could-not-be-processed'
						),
						noFilesSelectedText:
							Liferay.Language.get('no-files-selected'),
						notAvailableText: Liferay.Language.get(
							'multiple-file-uploading-is-not-available'
						),
						orText: Liferay.Language.get('or'),
						pendingFileText: Liferay.Language.get(
							'these-files-have-been-previously-uploaded-but-not-actually-saved.-please-save-or-delete-them-before-they-are-removed'
						),
						selectAllText: Liferay.Language.get('select-all'),
						selectFileText: Liferay.Language.get('select-file'),
						selectFilesText: Liferay.Language.get('select-files'),
						unexpectedErrorOnDeleteText: Liferay.Language.get(
							'an-unexpected-error-occurred-while-deleting-the-file'
						),
						unexpectedErrorOnUploadText: Liferay.Language.get(
							'an-unexpected-error-occurred-while-uploading-your-file'
						),
						uploadingFileXofXText: Liferay.Language.get(
							'uploading-file-x-of-x'
						),
						uploadingText: Liferay.Language.get('uploading'),
						uploadsCompleteText: Liferay.Language.get(
							'all-files-ready-to-be-saved'
						),
						warningFailureText: Liferay.Language.get(
							'consider-that-the-following-data-would-not-have-been-imported-either'
						),
						warningText: Liferay.Language.get(
							'the-following-data-will-not-be-imported'
						),
						warningTitle: Liferay.Language.get('warning-colon'),
						xFilesReadyText: Liferay.Language.get(
							'x-files-ready-to-be-uploaded'
						),
						xFilesSelectedText:
							Liferay.Language.get('x-files-selected'),
						zeroByteSizeText: Liferay.Language.get(
							'the-file-contains-no-data-and-cannot-be-uploaded.-please-use-the-classic-uploader'
						),
					},
				},

				tempFileURL: {
					value: '',
				},

				tempRandomSuffix: {
					validator: Lang.isString,
					value: null,
				},

				uploadFile: {
					value: '',
				},
			},

			AUGMENTS: [Liferay.PortletBase],

			NAME: 'liferayupload',

			prototype: {
				_afterFilesSaved() {
					const instance = this;

					instance._updateMetadataContainer();
					instance._updateManageUploadDisplay();
				},

				_cancelAllFiles() {
					const instance = this;

					const strings = instance.get(STRINGS);

					const uploader = instance._uploader;

					const queue = uploader.queue;

					queue.pauseUpload();

					queue.queuedFiles.forEach((item) => {
						const li = A.one('#' + item.id);

						if (li && !li.hasClass('upload-complete')) {
							li.remove(true);
						}
					});

					A.all('.file-uploading').remove(true);

					queue.cancelUpload();

					uploader.queue = null;

					instance._cancelButton.hide();

					instance._filesTotal = 0;

					const cancelText = instance.get('multipleFiles')
						? strings.cancelUploadsText
						: strings.cancelFileText;

					instance._updateList(0, cancelText);
				},

				_clearUploads() {
					const instance = this;

					instance._fileListContent
						.all('.file-saved,.upload-error')
						.remove(true);

					instance._updateManageUploadDisplay();
				},

				_filesTotal: 0,

				_formatTempFiles(fileNames) {
					const instance = this;

					if (Array.isArray(fileNames) && fileNames.length) {
						const fileListContent = instance._fileListContent;

						instance._pendingFileInfo.show();
						instance._manageUploadTarget.show();

						const metadataExplanationContainer =
							instance._metadataExplanationContainer;

						if (metadataExplanationContainer) {
							metadataExplanationContainer.show();
						}

						const files = fileNames.map((item) => {
							const title = item;

							let tempTitle = title;

							const tempRandomSuffix =
								instance.get('tempRandomSuffix');

							if (tempRandomSuffix) {
								const lastIndexOfPeriod =
									title.lastIndexOf('.');
								const posTempRandomSuffix =
									title.indexOf(tempRandomSuffix);

								if (posTempRandomSuffix !== -1) {
									tempTitle = title.substr(
										0,
										posTempRandomSuffix
									);

									if (lastIndexOfPeriod > 0) {
										tempTitle +=
											title.substr(lastIndexOfPeriod);
									}
								}
							}

							return {
								id: A.guid(),
								name: item,
								temp: true,
								title: tempTitle,
							};
						});

						instance._fileListTPL.render(files, fileListContent);
					}
					else if (instance._allRowIdsCheckbox) {
						instance._allRowIdsCheckbox.attr('checked', true);
					}
				},

				_getValidFiles(data) {
					const instance = this;

					const strings = instance.get(STRINGS);

					const maxFileSize = instance.get('maxFileSize');
					const maxUploadRequestSize =
						Liferay.PropsValues
							.UPLOAD_SERVLET_REQUEST_IMPL_MAX_SIZE;

					return data.filter((item) => {
						const id = item.get('id') || A.guid();
						const name = item.get('name');
						const size = item.get('size') || 0;

						let error;
						let file;

						if (size === 0) {
							error = strings.zeroByteSizeText;
						}
						else if (name.length > 240) {
							error = strings.invalidFileNameText;
						}
						else if (maxFileSize > 0 && size > maxFileSize) {
							error = instance._invalidFileSizeText;
						}
						else if (
							maxUploadRequestSize > 0 &&
							size > maxUploadRequestSize
						) {
							error = instance._invalidUploadRequestSize;
						}

						if (error) {
							item.error = error;
						}
						else {
							file = item;
						}

						item.id = id;
						item.name = name;
						item.size = size;

						instance._queueFile(item);

						return file;
					});
				},

				_handleDeleteResponse(json, li) {
					const instance = this;

					if (!json.deleted) {
						const errorHTML = instance._fileListTPL.parse([
							{
								error: json.errorMessage,
								id: li.attr('data-fileId'),
								name: li.attr('data-fileName'),
							},
						]);

						li.replace(errorHTML);
					}

					li.remove(true);

					instance._updateManageUploadDisplay();
					instance._updateMetadataContainer();
					instance._updatePendingInfoContainer();
					instance._updateWarningContainer();

					Liferay.fire('tempFileRemoved');

					instance.fire('tempFileRemoved');
				},

				_handleDrop(event) {
					const instance = this;

					const dataTransfer = event._event.dataTransfer;

					const dragDropFiles =
						dataTransfer && AArray(dataTransfer.files);

					if (!dragDropFiles || !dragDropFiles.length) {
						return;
					}

					event.halt();

					const uploaderBoundingBox = instance._uploaderBoundingBox;

					const target = event.target;

					const uploader = instance._uploader;

					if (
						target === uploaderBoundingBox ||
						uploaderBoundingBox.contains(target)
					) {
						event.fileList = dragDropFiles.map((item) => {
							return new A.FileHTML5(item);
						});

						uploader.fire('fileselect', event);
					}
				},

				_handleFileClick(event) {
					const instance = this;

					const currentTarget = event.currentTarget;

					if (currentTarget.hasClass('select-file')) {
						instance._onSelectFileClick(currentTarget);
					}
					else if (currentTarget.hasClass('delete-button')) {
						instance._onDeleteFileClick(currentTarget);
					}
					else if (currentTarget.hasClass('cancel-button')) {
						instance._onCancelFileClick(currentTarget);
					}
				},

				_isUploading() {
					const instance = this;

					const queue = instance._uploader.queue;

					return !!(
						queue &&
						(!!queue.queuedFiles.length ||
							queue.numberOfUploads > 0 ||

							// eslint-disable-next-line @liferay/aui/no-object
							!A.Object.isEmpty(queue.currentFiles)) &&
						queue._currentState === UploaderQueue.UPLOADING
					);
				},

				_markSelected(node) {
					const fileItem = node.ancestor('.upload-file.selectable');

					fileItem.toggleClass('selected');
				},

				_onAllRowIdsClick() {
					const instance = this;

					import(
						Liferay.FrontendESM.buildURL(
							SCRIPT_URL,
							'frontend-js-web',
							'legacy'
						)
					).then(({checkAll}) => {
						checkAll(
							instance._fileListSelector,
							instance._selectUploadedFileCheckboxId,
							instance._allRowIdsCheckboxSelector
						);

						const uploadedFiles = instance._fileListContent.all(
							'.upload-file.upload-complete'
						);

						uploadedFiles.toggleClass(
							'selected',
							instance._allRowIdsCheckbox.attr('checked')
						);

						instance._updateMetadataContainer();
					});
				},

				_onAllUploadsComplete() {
					const instance = this;

					const strings = instance.get(STRINGS);

					const uploader = instance._uploader;

					instance._filesTotal = 0;

					uploader.set('enabled', true);

					uploader.set('fileList', []);

					instance._cancelButton.hide();

					if (instance.get('multipleFiles')) {
						instance._clearUploadsButton.toggle(
							!!instance._fileListContent.one(
								'.file-saved,.upload-error'
							)
						);
					}

					let uploadsCompleteText;

					if (
						instance._fileListContent.one(
							'.upload-file.upload-complete'
						) &&
						instance.get('multipleFiles')
					) {
						uploadsCompleteText = strings.uploadsCompleteText;
					}

					instance._updateList(0, uploadsCompleteText);

					const removeOnComplete = instance.get('removeOnComplete');

					if (removeOnComplete) {
						instance._listInfo.one('.h4').hide();

						instance._allRowIdsCheckbox.hide();
					}

					Liferay.fire('allUploadsComplete');
				},

				_onBeforeUnload(event) {
					const instance = this;

					if (instance._isUploading()) {
						event.preventDefault();
					}
				},

				_onCancelFileClick(currentTarget) {
					const instance = this;

					const strings = instance.get(STRINGS);

					const uploader = instance._uploader;

					const queue = uploader.queue;

					const li = currentTarget.ancestor('li');

					if (li) {
						if (queue) {
							const fileId = li.attr('data-fileId');

							const file =
								queue.currentFiles[fileId] ||
								AArray.find(queue.queuedFiles, (item) => {
									return item.id === fileId;
								});

							if (file) {
								queue.cancelUpload(file);

								instance._updateList(0, strings.cancelFileText);
							}

							if (
								!queue.queuedFiles.length &&
								queue.numberOfUploads <= 0
							) {
								uploader.queue = null;

								instance._cancelButton.hide();
							}
						}

						li.remove(true);

						instance._filesTotal -= 1;
					}
				},

				_onDeleteFileClick(currentTarget) {
					const instance = this;

					const strings = instance.get(STRINGS);

					const li = currentTarget.ancestor('li');

					li.hide();

					const failureResponse = {
						errorMessage: strings.unexpectedErrorOnDeleteText,
					};

					const deleteFile = instance.get('deleteFile');

					if (deleteFile) {
						const data = {
							fileName: li.attr('data-fileName'),
						};

						Liferay.Util.fetch(deleteFile, {
							body: Liferay.Util.objectToFormData(
								instance.ns(data)
							),
							method: 'POST',
						})
							.then((response) => response.json())
							.then((response) => {
								instance._handleDeleteResponse(response, li);
							})
							.catch(() => {
								li.show();

								instance._handleDeleteResponse(
									failureResponse,
									li
								);
							});
					}
					else {
						instance._handleDeleteResponse(failureResponse, li);
					}
				},

				_onFileSelect(event) {
					const instance = this;

					const fileList = event.fileList;

					const validFiles = instance._getValidFiles(fileList);

					const validFilesLength = validFiles.length;

					if (validFilesLength) {
						const uploader = instance._uploader;

						uploader.set('fileList', validFiles);

						instance._filesTotal += validFilesLength;

						instance._cancelButton.show();

						if (instance._isUploading()) {
							const uploadQueue = uploader.queue;

							validFiles.forEach(
								uploadQueue.addToQueueBottom,
								uploadQueue
							);
						}
						else {
							uploader.uploadAll();
						}
					}

					instance._pendingFileInfo.hide();
				},

				_onSelectFileClick(currentTarget) {
					const instance = this;

					if (instance.get('multipleFiles')) {
						import(
							Liferay.FrontendESM.buildURL(
								SCRIPT_URL,
								'frontend-js-web',
								'legacy'
							)
						).then(({checkAll}) => {
							checkAll(
								instance._fileListSelector,
								instance._selectUploadedFileCheckboxId,
								instance._allRowIdsCheckboxSelector
							);

							instance._markSelected(currentTarget);

							instance._updateMetadataContainer();
						});
					}
					else {
						instance._markSelected(currentTarget);

						instance._updateMetadataContainer();
					}
				},

				_onUploadComplete(event) {
					const instance = this;

					const strings = instance.get(STRINGS);

					const file = event.file;

					const fileId = file.id;

					const li = A.one('#' + fileId);

					let data = event.data;

					let input;

					let newLiNode;

					try {
						data = JSON.parse(data);
					}
					catch (error) {}

					if (
						data.status &&
						data.status >=
							STATUS_CODE.SC_DUPLICATE_FILE_EXCEPTION &&
						data.status < STATUS_CODE.INTERNAL_SERVER_ERROR
					) {
						file.error =
							data.message || strings.unexpectedErrorOnUploadText;

						file.messageListItems = data.messageListItems;
						file.warningMessages = data.warningMessages;

						newLiNode = instance._fileListTPL.parse([file]);

						if (li) {
							li.placeBefore(newLiNode);

							li.remove(true);
						}
						else {
							instance._fileListContent.prepend(newLiNode);
						}
					}
					else {
						if (li) {
							if (data.warningMessages) {
								file.selected = true;
								file.temp = true;
								file.warningMessages = data.warningMessages;

								newLiNode = instance._fileListTPL.parse([file]);

								li.placeBefore(newLiNode);

								li.remove(true);
							}
							else if (data.name) {
								file.selected = true;
								file.temp = true;
								file.name = data.name;
								file.title = data.title;

								newLiNode = A.Node.create(
									instance._fileListTPL.parse([file])
								);

								input = newLiNode.one('input');

								if (input) {
									input.attr('checked', true);
									input.attr('disabled', false);

									input.show();
								}

								li.placeBefore(newLiNode);

								li.remove(true);
							}
							else {
								li.replaceClass(
									'file-uploading',
									'pending-file upload-complete selectable selected'
								);

								input = li.one('input');

								if (input) {
									input.attr('checked', true);

									input.show();
								}
							}

							instance._updateManageUploadDisplay();
						}

						instance._updateMetadataContainer();
					}

					const removeOnComplete = instance.get('removeOnComplete');

					if (removeOnComplete) {
						li.remove(true);
					}

					instance.fire('uploadComplete', file);
				},

				_onUploadProgress(event) {
					const progress = A.one('#' + event.file.id + 'progress');

					if (progress) {
						const percentLoaded = Math.min(
							Math.ceil(event.percentLoaded / 3) * 3,
							100
						);

						progress.setStyle('width', percentLoaded + '%');
						progress.setAttribute('aria-valuenow', percentLoaded);
					}
				},

				_onUploadStart(event) {
					const instance = this;

					const strings = instance.get(STRINGS);

					const uploader = instance._uploader;

					const queue = uploader.queue;

					const filesQueued = queue ? queue.queuedFiles.length : 0;

					const filesTotal = instance._filesTotal;

					const position = filesTotal - filesQueued;

					let currentListText;

					if (instance.get('multipleFiles')) {
						currentListText = Lang.sub(
							strings.uploadingFileXofXText,
							[position, filesTotal]
						);
					}
					else {
						currentListText = strings.uploadingText;

						instance._fileListContent
							.all('.pending-file,.upload-error')
							.remove(true);
					}

					const fileIdSelector = '#' + event.file.id;

					A.on(
						'available',
						() => {
							A.one(fileIdSelector).addClass('file-uploading');
						},
						fileIdSelector
					);

					instance._listInfo.show();

					instance._updateList(0, currentListText);
				},

				_queueFile(file) {
					const instance = this;

					instance._fileListBuffer.push(file);

					instance._renderFileListTask();
				},

				_renderControls() {
					const instance = this;

					const multipleFiles = instance.get('multipleFiles');
					const strings = instance.get(STRINGS);

					const templateConfig = {
						$ns: instance.NS,
						cancelUploadsText: multipleFiles
							? strings.cancelUploadsText
							: strings.cancelFileText,
						dropFileText: multipleFiles
							? strings.dropFilesText
							: strings.dropFileText,
						multipleFiles,
						selectFilesText: multipleFiles
							? strings.selectFilesText
							: strings.selectFileText,
						strings,
						uploaderType: UPLOADER_TYPE,
					};

					instance._fileListTPL = new A.Template(
						TPL_FILE_LIST,
						templateConfig
					);

					// LPS-102399

					if (A.UA.ie) {
						instance._fileListTPL.tpls =
							instance._fileListTPL.tpls.map((tpl) => {
								if (tpl.tplFn) {
									const tplBodyRegex =
										/function anonymous\(values,parent\s*\) \{\s*(.*)\s*\}/;
									const tplFn = tpl.tplFn.toString();

									if (tplBodyRegex.test(tplFn)) {
										const tplBody = tplBodyRegex
											.exec(tplFn)[1]
											.replace(/values/g, 'parts');

										tpl.tplFn = new Function(
											'parts, parent',
											tplBody
										);
									}
								}

								return tpl;
							});
					}

					instance._selectUploadedFileCheckboxId =
						instance.ns('selectUploadedFile');

					const idNS = '#' + instance.NS;

					instance._allRowIdsCheckboxSelector = idNS + 'allRowIds';
					instance._fileListSelector = idNS + 'fileList';

					const uploadFragment = new A.Template(
						TPL_UPLOAD,
						templateConfig
					).render({
						multipleFiles,
					});

					instance._allRowIdsCheckbox = uploadFragment.one(
						instance._allRowIdsCheckboxSelector
					);
					instance._manageUploadTarget = uploadFragment.one(
						idNS + 'manageUploadTarget'
					);
					instance._cancelButton =
						uploadFragment.one('.cancel-uploads');
					instance._clearUploadsButton =
						uploadFragment.one('.clear-uploads');
					instance._fileList = uploadFragment.one(
						instance._fileListSelector
					);
					instance._fileListContent = uploadFragment.one(
						idNS + 'fileListContent'
					);
					instance._listInfo = uploadFragment.one(idNS + 'listInfo');
					instance._pendingFileInfo = uploadFragment.one(
						'.pending-files-info'
					);
					instance._selectFilesButton = uploadFragment.one(
						idNS + 'selectFilesButton'
					);
					instance._uploaderBoundingBox = uploadFragment.one(
						idNS + 'uploader'
					);
					instance._uploaderContentBox = uploadFragment.one(
						idNS + 'uploaderContent'
					);

					const tempFileURL = instance.get('tempFileURL');

					if (tempFileURL && instance.get('restoreState')) {
						if (Lang.isString(tempFileURL)) {
							Liferay.Util.fetch(tempFileURL)
								.then((response) => response.json())
								.then((response) =>
									instance._formatTempFiles(response)
								);
						}
						else {
							tempFileURL.method(
								tempFileURL.params,
								A.bind('_formatTempFiles', instance)
							);
						}
					}

					instance._uploadFragment = uploadFragment;

					instance._cancelButton.hide();
				},

				_renderFileList() {
					const instance = this;

					const fileListBuffer = instance._fileListBuffer;
					const fileListContent = instance._fileListContent;

					const fileListHTML =
						instance._fileListTPL.parse(fileListBuffer);

					const firstLi = fileListContent.one('li.upload-complete');

					if (firstLi) {
						firstLi.placeBefore(fileListHTML);
					}
					else {
						fileListContent.append(fileListHTML);
					}

					fileListBuffer.length = 0;
				},

				_renderUploader() {
					const instance = this;

					const timestampParam = '_LFR_UPLOADER_TS=' + Date.now();

					const uploader = new A.Uploader({
						boundingBox: instance._uploaderBoundingBox,
						contentBox: instance._uploaderContentBox,
						fileFieldName: 'file',
						multipleFiles: instance.get('multipleFiles'),
						on: {
							render() {
								instance
									.get('boundingBox')
									.setContent(instance._uploadFragment);
							},
						},
						selectFilesButton: instance._selectFilesButton,
						simLimit: instance.get('simultaneousUploads'),
						uploadURL: Liferay.Util.addParams(
							timestampParam,
							instance.get('uploadFile')
						),
					}).render();

					uploader.addTarget(instance);

					instance._uploader = uploader;
				},

				_updateList(listLength, message) {
					const instance = this;

					const strings = instance.get(STRINGS);

					const infoTitle = instance._listInfo.one('.h4');

					if (!instance.get('multipleFiles')) {
						infoTitle.html('');
					}
					else if (infoTitle) {
						const listText =
							message ||
							Lang.sub(strings.xFilesReadyText, [listLength]);

						infoTitle.html(listText);
					}
				},

				_updateManageUploadDisplay() {
					const instance = this;

					const fileListContent = instance._fileListContent;

					const hasSavedFiles = !!fileListContent.one(
						'.file-saved,.upload-error'
					);
					const hasUploadedFiles =
						!!fileListContent.one('.upload-complete');

					if (instance._allRowIdsCheckbox) {
						instance._allRowIdsCheckbox.toggle(hasUploadedFiles);
					}

					if (instance.get('multipleFiles')) {
						instance._clearUploadsButton.toggle(hasSavedFiles);
					}

					instance._manageUploadTarget.toggle(hasUploadedFiles);

					instance._listInfo.toggle(!!fileListContent.one('li'));
				},

				_updateMetadataContainer() {
					const instance = this;

					const strings = instance.get(STRINGS);

					const metadataContainer = instance._metadataContainer;
					const metadataExplanationContainer =
						instance._metadataExplanationContainer;

					if (metadataContainer && metadataExplanationContainer) {
						const totalFiles = instance._fileList.all(
							'li input[name=' +
								instance._selectUploadedFileCheckboxId +
								']'
						);

						const totalFilesCount = totalFiles.size();

						const selectedFiles = totalFiles.filter(':checked');

						const selectedFilesCount = selectedFiles.size();

						const hasSelectedFiles = selectedFilesCount > 0;

						if (metadataContainer) {
							metadataContainer.toggle(hasSelectedFiles);

							let selectedFilesText = strings.noFilesSelectedText;

							if (hasSelectedFiles) {
								if (selectedFilesCount === 1) {
									const titleNode = selectedFiles
										.item(0)
										.ancestor('[data-title]');

									if (titleNode) {
										selectedFilesText =
											titleNode.attr('data-title');
									}
								}
								else if (
									selectedFilesCount === totalFilesCount
								) {
									selectedFilesText =
										strings.allFilesSelectedText;
								}
								else if (selectedFilesCount > 1) {
									selectedFilesText = Lang.sub(
										strings.xFilesSelectedText,
										[selectedFilesCount]
									);
								}
							}

							const selectedFilesCountContainer =
								metadataContainer.one('.selected-files-count');

							if (selectedFilesCountContainer) {
								selectedFilesCountContainer.html(
									selectedFilesText
								);

								selectedFilesCountContainer.attr(
									'title',
									selectedFilesText
								);
							}
						}

						if (metadataExplanationContainer) {
							metadataExplanationContainer.toggle(
								!hasSelectedFiles && totalFilesCount > 0
							);
						}
					}
				},

				_updatePendingInfoContainer() {
					const instance = this;

					const totalFiles = instance._fileList.all(
						'li input[name=' +
							instance._selectUploadedFileCheckboxId +
							']'
					);

					if (!totalFiles.size()) {
						instance._pendingFileInfo.hide();
					}
				},

				_updateWarningContainer() {
					const instance = this;

					const totalFiles = instance._fileList.all(
						'li input[name=' +
							instance._selectUploadedFileCheckboxId +
							']'
					);

					const warningContainer =
						instance._fileList.one('.upload-error');

					if (!totalFiles.size() && warningContainer) {
						warningContainer.hide();
					}
				},

				bindUI() {
					const instance = this;

					if (instance._allRowIdsCheckbox) {
						instance._allRowIdsCheckbox.on(
							'click',
							instance._onAllRowIdsClick,
							instance
						);
					}

					instance._cancelButton.on(
						'click',
						instance._cancelAllFiles,
						instance
					);
					instance._clearUploadsButton.on(
						'click',
						instance._clearUploads,
						instance
					);

					A.getWin().on(
						'beforeunload',
						instance._onBeforeUnload,
						instance
					);

					instance._fileList.delegate(
						'click',
						instance._handleFileClick,
						'.select-file, li .delete-button, li .cancel-button',
						instance
					);

					Liferay.after(
						'filesSaved',
						instance._afterFilesSaved,
						instance
					);

					const uploader = instance._uploader;

					uploader.after(
						'fileselect',
						instance._onFileSelect,
						instance
					);

					uploader.on(
						'alluploadscomplete',
						instance._onAllUploadsComplete,
						instance
					);
					uploader.on(
						'fileuploadstart',
						instance._onUploadStart,
						instance
					);
					uploader.on(
						'uploadcomplete',
						instance._onUploadComplete,
						instance
					);
					uploader.on(
						'uploadprogress',
						instance._onUploadProgress,
						instance
					);

					const rootElement = instance.get('rootElement');

					const docElement = rootElement
						? rootElement
						: A.getDoc().get('documentElement');

					docElement.on('drop', instance._handleDrop, instance);

					const uploaderBoundingBox = instance._uploaderBoundingBox;

					const removeCssClassTask = A.debounce(() => {
						docElement.removeClass('upload-drop-intent');
						docElement.removeClass('upload-drop-active');
					}, 500);

					docElement.on('dragover', (event) => {
						const originalEvent = event._event;

						const dataTransfer = originalEvent.dataTransfer;

						if (
							dataTransfer &&
							AArray.indexOf(dataTransfer.types, 'Files') > -1
						) {
							event.halt();

							docElement.addClass('upload-drop-intent');

							const target = event.target;

							const inDropArea =
								target.compareTo(uploaderBoundingBox) ||
								uploaderBoundingBox.contains(target);

							let dropEffect = 'none';

							if (inDropArea) {
								dropEffect = 'copy';
							}

							docElement.toggleClass(
								'upload-drop-active',
								inDropArea
							);

							dataTransfer.dropEffect = dropEffect;
						}

						removeCssClassTask();
					});
				},

				initializer() {
					const instance = this;

					const strings = instance.get(STRINGS);

					const fallback = instance.get('fallback');

					const useFallback =
						location.hash.indexOf(STR_PARAM_FALLBACK) > -1 &&
						fallback;

					if (
						useFallback ||
						UPLOADER_TYPE === 'none' ||
						UPLOADER_TYPE === 'flash'
					) {
						if (fallback) {
							fallback.show();
						}
						else {
							instance
								.one('#fileUpload')
								.append(
									Lang.sub(TPL_ERROR_MESSAGE, [
										strings.notAvailableText,
									])
								);
						}

						instance._preventRenderHandle = instance.on(
							'render',
							(event) => {
								event.preventDefault();
							}
						);
					}
					else {
						const maxFileSize = Liferay.Util.formatStorage(
							instance.get('maxFileSize')
						);

						const maxUploadRequestSize = Liferay.Util.formatStorage(
							Liferay.PropsValues
								.UPLOAD_SERVLET_REQUEST_IMPL_MAX_SIZE
						);

						instance._invalidFileSizeText = Lang.sub(
							strings.invalidFileSizeText,
							[maxFileSize]
						);
						instance._invalidUploadRequestSize = Lang.sub(
							strings.invalidUploadRequestSizeText,
							[maxUploadRequestSize]
						);

						instance._metadataContainer =
							instance.get('metadataContainer');
						instance._metadataExplanationContainer = instance.get(
							'metadataExplanationContainer'
						);

						instance._fileListBuffer = [];
						instance._renderFileListTask = A.debounce(
							instance._renderFileList,
							10,
							instance
						);
					}

					instance._fallback = fallback;
				},

				renderUI() {
					const instance = this;

					instance._renderControls();
					instance._renderUploader();
				},
			},
		});

		Liferay.Upload = Upload;
	},
	'',
	{
		requires: [
			'aui-template-deprecated',
			'collection',
			'liferay-portlet-base',
			'uploader',
		],
	}
);
