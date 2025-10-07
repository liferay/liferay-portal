/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

(function () {
	const STR_FILE_ENTRY_RETURN_TYPE =
		'com.liferay.item.selector.criteria.FileEntryItemSelectorReturnType';

	const STR_VIDEO_HTML_RETURN_TYPE =
		'com.liferay.item.selector.criteria.VideoEmbeddableHTMLItemSelectorReturnType';

	const TPL_AUDIO_SCRIPT =
		'boundingBox: "#" + mediaId,' + 'oggUrl: "{oggUrl}",' + 'url: "{url}"';

	const TPL_VIDEO_SCRIPT =
		'boundingBox: "#" + mediaId,' +
		'height: {height},' +
		'ogvUrl: "{ogvUrl}",' +
		'poster: "{poster}",' +
		'url: "{url}",' +
		'width: {width}';

	const defaultVideoHeight = 300;
	const defaultVideoWidth = 400;

	CKEDITOR.plugins.add('itemselector', {
		_bindBrowseButton(
			editor,
			dialogDefinition,
			tabName,
			commandName,
			targetField
		) {
			const tab = dialogDefinition.getContents(tabName);

			if (tab) {
				const browseButton = tab.get('browse');

				if (browseButton) {
					browseButton.onClick = function () {
						editor.execCommand(commandName, (newVal) => {
							dialogDefinition.dialog.setValueOf(
								tabName,
								targetField,
								newVal
							);
						});
					};
				}
			}
		},

		_checkImageWidth(editor, editorContent, imageSrc) {
			if (!editorContent) {
				return;
			}

			const editorContentDocument =
				!editor.window.$.AlloyEditor &&
				!editorContent.id.endsWith('BalloonEditor')
					? editorContent.querySelector('iframe').contentDocument
					: editorContent;

			const imgElement = editorContentDocument.querySelector(
				`img[src='${imageSrc}']`
			);

			if (imgElement) {
				imgElement.onload = function () {
					if (this.width === 0) {
						this.setAttribute('width', '150px');
					}
				};
			}
		},

		_commitAudioValue(value, node) {
			const instance = this;

			node.setAttribute('data-document-url', value);

			const audioUrl = Liferay.Util.addParams(
				'audioPreview=1&type=mp3',
				value
			);

			node.setAttribute('data-audio-url', audioUrl);

			const audioOggUrl = Liferay.Util.addParams(
				'audioPreview=1&type=ogg',
				value
			);

			node.setAttribute('data-audio-ogg-url', audioOggUrl);

			return instance._audioTPL.output({
				oggUrl: audioOggUrl,
				url: audioUrl,
			});
		},

		_commitMediaValue(value, editor, type) {
			const instance = this;

			const mediaPlugin = editor.plugins.media;

			if (mediaPlugin) {
				mediaPlugin.onOkCallback(
					{
						commitContent: instance._getCommitMediaValueFn(
							value,
							editor,
							type
						),
					},
					editor,
					type
				);
			}
		},

		_commitVideoHtmlValue(editor, html) {
			const parsedHTML = new DOMParser().parseFromString(
				html,
				'text/html'
			);
			const iFrame = parsedHTML.getElementsByTagName('iframe');
			const url = iFrame[0].src;

			editor.plugins.videoembed.onOkVideoHtml(editor, html, url);
		},

		_commitVideoValue(value, node, extraStyles) {
			const instance = this;

			node.setAttribute('data-document-url', value);

			const videoUrl = Liferay.Util.addParams(
				'videoPreview=1&type=mp4',
				value
			);

			node.setAttribute('data-video-url', videoUrl);

			const videoOgvUrl = Liferay.Util.addParams(
				'videoPreview=1&type=ogv',
				value
			);

			node.setAttribute('data-video-ogv-url', videoOgvUrl);

			const videoHeight = defaultVideoHeight;

			node.setAttribute('data-height', videoHeight);

			const videoWidth = defaultVideoWidth;

			node.setAttribute('data-width', videoWidth);

			const poster = Liferay.Util.addParams('videoThumbnail=1', value);

			node.setAttribute('data-poster', poster);

			extraStyles.backgroundImage = 'url(' + poster + ')';
			extraStyles.height = videoHeight + 'px';
			extraStyles.width = videoWidth + 'px';

			return instance._videoTPL.output({
				height: videoHeight,
				ogvUrl: videoOgvUrl,
				poster,
				url: videoUrl,
				width: videoWidth,
			});
		},

		_getCommitMediaValueFn(value, editor, type) {
			const instance = this;

			const commitValueFn = function (node, extraStyles) {
				let mediaScript;

				if (type === 'audio') {
					mediaScript = instance._commitAudioValue(
						value,
						node,
						extraStyles
					);
				}
				else if (type === 'video') {
					mediaScript = instance._commitVideoValue(
						value,
						node,
						extraStyles
					);
				}

				const mediaPlugin = editor.plugins.media;

				if (mediaPlugin) {
					mediaPlugin.applyMediaScript(node, type, mediaScript);
				}
			};

			return commitValueFn;
		},

		_getItemSrc(editor, selectedItem) {
			let itemSrc;

			try {
				itemSrc = JSON.parse(selectedItem.value);
			}
			catch (error) {
				itemSrc = selectedItem;
			}

			if (itemSrc.value && itemSrc.value.html) {
				itemSrc = selectedItem.value.html;
			}
			else if (itemSrc.html) {
				itemSrc = itemSrc.html;
			}
			else if (itemSrc.value) {
				itemSrc = itemSrc.value;
			}
			else if (itemSrc.url) {
				itemSrc = itemSrc.url;
			}

			if (selectedItem.returnType === STR_FILE_ENTRY_RETURN_TYPE) {
				try {
					const itemValue = JSON.parse(selectedItem.value);

					itemSrc = editor.config.attachmentURLPrefix
						? editor.config.attachmentURLPrefix +
							encodeURIComponent(itemValue.title)
						: itemValue.url;
				}
				catch (error) {}
			}

			return itemSrc;
		},

		_isEmptySelection(editor) {
			const selection = editor.getSelection();

			const ranges = selection.getRanges();

			return (
				selection.getType() === CKEDITOR.SELECTION_NONE ||
				(ranges.length === 1 && ranges[0].collapsed)
			);
		},

		_onSelectedAudioChange(editor, callback, selectedItem) {
			const instance = this;

			if (selectedItem) {
				const audioSrc = instance._getItemSrc(editor, selectedItem);

				if (audioSrc) {
					if (typeof callback === 'function') {
						callback(audioSrc);
					}
					else {
						instance._commitMediaValue(audioSrc, editor, 'audio');
					}
				}
			}
		},

		_onSelectedImageChange(editor, callback, selectedItem) {
			const instance = this;

			if (selectedItem) {
				const imageSrc = instance._getItemSrc(editor, selectedItem);

				if (imageSrc) {
					const editorContent = editor.window.$.AlloyEditor
						? document.getElementById(`${editor.name}Container`)
						: document.getElementById(`cke_${editor.name}`);

					if (typeof callback === 'function') {
						callback(imageSrc, selectedItem);

						instance._checkImageWidth(
							editor,
							editorContent,
							imageSrc
						);
					}
					else {
						const editorContentHeight =
							editorContent?.getBoundingClientRect().height;

						const imgElement = new Image();

						imgElement.src = imageSrc;

						imgElement.onload = function () {
							if (imgElement.height > editorContentHeight) {
								imgElement.height = editorContentHeight;
							}

							let elementOuterHtml = imgElement.outerHTML;

							if (instance._isEmptySelection(editor)) {
								elementOuterHtml += '<br />';
							}

							editor.insertHtml(elementOuterHtml);

							editor.focus();

							instance._checkImageWidth(
								editor,
								editorContent,
								imageSrc
							);
						};
					}
				}
			}
		},

		_onSelectedLinkChange(editor, callback, selectedItem) {
			if (selectedItem) {
				const linkUrl = selectedItem.value;

				if (typeof callback === 'function') {
					callback(linkUrl, selectedItem);
				}
			}
		},

		_onSelectedVideoChange(editor, callback, selectedItem) {
			const instance = this;

			if (selectedItem) {
				const videoSrc = instance._getItemSrc(editor, selectedItem);

				if (videoSrc) {
					if (typeof callback === 'function') {
						callback(videoSrc);
					}
					else {
						if (
							selectedItem.returnType ===
							STR_VIDEO_HTML_RETURN_TYPE
						) {
							instance._commitVideoHtmlValue(editor, videoSrc);
						}
						else {
							editor.plugins.videoembed.onOkVideo(editor, {
								type: 'video',
								url: videoSrc,
							});
						}
					}
				}
			}
		},

		_openSelectionModal(editor, url, callback) {
			Liferay.Util.openSelectionModal({
				onSelect: callback,
				selectEventName: editor.name + 'selectItem',
				title: Liferay.Language.get('select-item'),
				url,
				zIndex: CKEDITOR.getNextZIndex(),
			});
		},

		init(editor) {
			const instance = this;

			instance._audioTPL = new CKEDITOR.template(TPL_AUDIO_SCRIPT);
			instance._videoTPL = new CKEDITOR.template(TPL_VIDEO_SCRIPT);

			editor.addCommand('audioselector', {
				canUndo: false,
				exec(editor, callback) {
					const onSelectedAudioChangeFn = AUI().bind(
						'_onSelectedAudioChange',
						instance,
						editor,
						callback
					);

					instance._openSelectionModal(
						editor,
						editor.config.filebrowserAudioBrowseUrl,
						onSelectedAudioChangeFn
					);
				},
			});

			editor.addCommand('imageselector', {
				canUndo: false,
				exec(editor, callback) {
					const onSelectedImageChangeFn = AUI().bind(
						'_onSelectedImageChange',
						instance,
						editor,
						callback
					);

					instance._openSelectionModal(
						editor,
						editor.config.filebrowserImageBrowseUrl,
						onSelectedImageChangeFn
					);
				},
			});

			editor.addCommand('linkselector', {
				canUndo: false,
				exec(editor, callback) {
					const onSelectedLinkChangeFn = AUI().bind(
						'_onSelectedLinkChange',
						instance,
						editor,
						callback
					);

					instance._openSelectionModal(
						editor,
						editor.config.filebrowserBrowseUrl,
						onSelectedLinkChangeFn
					);
				},
			});

			editor.addCommand('videoselector', {
				canUndo: false,
				exec(editor, callback) {
					const onSelectedVideoChangeFn = AUI().bind(
						'_onSelectedVideoChange',
						instance,
						editor,
						callback
					);

					instance._openSelectionModal(
						editor,
						editor.config.filebrowserVideoBrowseUrl,
						onSelectedVideoChangeFn
					);
				},
			});

			if (editor.ui.addButton) {
				editor.ui.addButton('ImageSelector', {
					command: 'imageselector',
					icon: instance.path + 'assets/image.png',
					label: editor.lang.common.image,
				});

				editor.ui.addButton('AudioSelector', {
					command: 'audioselector',
					icon: instance.path + 'assets/audio.png',
					label: Liferay.Language.get('audio'),
				});

				editor.ui.addButton('VideoSelector', {
					command: 'videoselector',
					icon: instance.path + 'assets/video.png',
					label: Liferay.Language.get('video'),
				});
			}

			CKEDITOR.on('dialogDefinition', (event) => {
				const dialogName = event.data.name;

				const dialogDefinition = event.data.definition;

				if (dialogName === 'audio') {
					instance._bindBrowseButton(
						event.editor,
						dialogDefinition,
						'info',
						'audioselector',
						'url'
					);
				}
				else if (dialogName === 'image') {
					instance._bindBrowseButton(
						event.editor,
						dialogDefinition,
						'Link',
						'linkselector',
						'txtUrl'
					);

					dialogDefinition.getContents('info').remove('browse');

					dialogDefinition.onLoad = function () {
						this.getContentElement('info', 'txtUrl')
							.getInputElement()
							.setAttribute('readOnly', true);
					};
				}
				else if (dialogName === 'image2') {
					instance._bindBrowseButton(
						event.editor,
						dialogDefinition,
						'info',
						'imageselector',
						'src'
					);
				}
				else if (dialogName === 'video') {
					instance._bindBrowseButton(
						event.editor,
						dialogDefinition,
						'info',
						'videoselector',
						'poster'
					);
				}
				else if (dialogName === 'link') {
					instance._bindBrowseButton(
						event.editor,
						dialogDefinition,
						'info',
						'linkselector',
						'url'
					);
				}
			});
		},
	});
})();
