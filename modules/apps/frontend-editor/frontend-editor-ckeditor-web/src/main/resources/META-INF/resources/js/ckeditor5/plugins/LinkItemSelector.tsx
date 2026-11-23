/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Editor, Plugin} from '@ckeditor/ckeditor5-core';
import {
	Link,
	LinkUI,
	addLinkProtocolIfApplicable,
} from '@ckeditor/ckeditor5-link';
import {Config} from '@ckeditor/ckeditor5-utils';
import ClayButton from '@clayui/button';
import ClayForm, {ClayCheckbox, ClayInput} from '@clayui/form';
import ClayIcon from '@clayui/icon';
import ClayModal from '@clayui/modal';
import {openModal, openSelectionModal} from 'frontend-js-components-web';
import React, {useState} from 'react';

import {LiferayEditorConfig} from '../utils/types';

interface LinkDialogValues {
	displayedText?: string;
	openInNewTab: boolean;
	url: string;
}

function RequiredMark() {
	return (
		<>
			<span className="inline-item-after reference-mark text-warning">
				<ClayIcon symbol="asterisk" />
			</span>

			<span className="hide-accessible sr-only">
				{Liferay.Language.get('required')}
			</span>
		</>
	);
}

function FieldError() {
	return (
		<ClayForm.FeedbackGroup>
			<ClayForm.FeedbackItem>
				<ClayForm.FeedbackIndicator symbol="exclamation-full" />

				{Liferay.Language.get('this-field-is-required')}
			</ClayForm.FeedbackItem>
		</ClayForm.FeedbackGroup>
	);
}

function LinkDialog({
	browseURL,
	closeModal,
	id,
	initialValues,
	itemSelectorEventName,
	onSubmit,
}: {
	browseURL: string;
	closeModal: () => void;
	id: string;
	initialValues: LinkDialogValues;
	itemSelectorEventName?: string;
	onSubmit: (values: LinkDialogValues) => void;
}) {
	const [displayedText, setDisplayedText] = useState(
		initialValues.displayedText
	);
	const [openInNewTab, setOpenInNewTab] = useState(
		initialValues.openInNewTab
	);
	const [submitted, setSubmitted] = useState(false);
	const [url, setURL] = useState(initialValues.url);

	const displayedTextInvalid =
		displayedText !== undefined && !displayedText.trim();
	const urlInvalid = !url.trim();

	return (
		<ClayForm
			onSubmit={(event) => {
				event.preventDefault();

				setSubmitted(true);

				if (displayedTextInvalid || urlInvalid) {
					return;
				}

				onSubmit({displayedText, openInNewTab, url: url.trim()});

				closeModal();
			}}
		>
			<ClayModal.Header
				closeButtonAriaLabel={Liferay.Language.get('close')}
			>
				{Liferay.Language.get('link')}
			</ClayModal.Header>

			<ClayModal.Body>
				{displayedText !== undefined && (
					<ClayForm.Group
						className={
							submitted && displayedTextInvalid ? 'has-error' : ''
						}
					>
						<label htmlFor={`${id}_displayedText`}>
							{Liferay.Language.get('display-text')}

							<RequiredMark />
						</label>

						<ClayInput
							id={`${id}_displayedText`}
							onChange={(event) =>
								setDisplayedText(event.target.value)
							}
							type="text"
							value={displayedText}
						/>

						{submitted && displayedTextInvalid && <FieldError />}
					</ClayForm.Group>
				)}

				<ClayForm.Group
					className={submitted && urlInvalid ? 'has-error' : ''}
				>
					<label htmlFor={`${id}_url`}>
						{Liferay.Language.get('link-url')}

						<RequiredMark />
					</label>

					<ClayInput
						autoFocus
						id={`${id}_url`}
						onChange={(event) => setURL(event.target.value)}
						type="text"
						value={url}
					/>

					{submitted && urlInvalid && <FieldError />}
				</ClayForm.Group>

				<ClayForm.Group>
					<ClayCheckbox
						checked={openInNewTab}
						label={Liferay.Language.get('open-in-a-new-tab')}
						onChange={(event) =>
							setOpenInNewTab(event.target.checked)
						}
					/>
				</ClayForm.Group>

				<ClayButton
					displayType="secondary"
					onClick={() =>
						openSelectionModal<{value: string}>({
							onSelect: ({value}) => setURL(value),
							selectEventName: itemSelectorEventName,
							title: Liferay.Language.get(
								'select-web-content-asset'
							),
							url: browseURL,
							zIndex: Liferay.zIndex.WINDOW + 20,
						})
					}
				>
					{Liferay.Language.get('browse-server')}
				</ClayButton>
			</ClayModal.Body>

			<ClayModal.Footer
				last={
					<ClayButton.Group spaced>
						<ClayButton
							displayType="secondary"
							onClick={closeModal}
						>
							{Liferay.Language.get('cancel')}
						</ClayButton>

						<ClayButton type="submit">
							{Liferay.Language.get('done')}
						</ClayButton>
					</ClayButton.Group>
				}
			/>
		</ClayForm>
	);
}

class LinkItemSelector extends Plugin {
	static get pluginName() {
		return 'LinkItemSelector' as const;
	}

	static get requires() {
		return [Link];
	}

	constructor(editor: Editor) {
		super(editor);

		const config: Config<LiferayEditorConfig> = editor.config;

		if (!config.get('filebrowserBrowseUrl')) {
			return;
		}

		config.define('link.decorators', {
			openInNewTab: {
				attributes: {target: '_blank'},
				label: Liferay.Language.get('open-in-a-new-tab'),
				mode: 'manual',
			},
			openInSameTab: {
				attributes: {target: '_self'},
				label: Liferay.Language.get('open-in-the-same-tab'),
				mode: 'manual',
			},
		});

		config.set(
			'link.toolbar',
			(config.get('link.toolbar') ?? []).filter(
				(item) => item !== 'linkProperties'
			)
		);
	}

	afterInit() {
		const editor = this.editor;

		const config: Config<LiferayEditorConfig> = editor.config;

		const browseURL = config.get('filebrowserBrowseUrl');

		if (!browseURL) {
			return;
		}

		const linkCommand = editor.commands.get('link')!;
		const linkUI = editor.plugins.get(LinkUI) as any;

		const openDialog = () => {
			const selectedLinkableText = linkUI._getSelectedLinkableText();

			linkUI._hideUI(false);

			openModal({
				contentComponent: ({closeModal}: {closeModal: () => void}) => (
					<LinkDialog
						browseURL={browseURL}
						closeModal={closeModal}
						id={`${editor.id}_link`}
						initialValues={{
							displayedText: selectedLinkableText,
							openInNewTab: Boolean(
								linkCommand.manualDecorators.get(
									'linkOpenInNewTab'
								)?.value
							),
							url: (linkCommand.value as string) || '',
						}}
						itemSelectorEventName={config.get(
							'itemSelectorEventName'
						)}
						onSubmit={({displayedText, openInNewTab, url}) =>
							editor.execute(
								'link',
								addLinkProtocolIfApplicable(
									url,
									config.get('link.defaultProtocol')
								),
								{
									linkOpenInNewTab: openInNewTab,
									linkOpenInSameTab: !openInNewTab,
								},
								displayedText === selectedLinkableText
									? undefined
									: displayedText
							)
						}
					/>
				),
				onClose: () => editor.editing.view.focus(),
				size: 'md',
				zIndex: Liferay.zIndex.WINDOW + 10,
			});
		};

		linkUI.decorate('_showUI');

		this.listenTo(
			linkUI,
			'_showUI',
			(event: any) => {
				if (linkCommand.value) {
					return;
				}

				event.stop();

				openDialog();
			},
			{priority: 'high'}
		);

		linkUI.decorate('_addFormView');

		this.listenTo(
			linkUI,
			'_addFormView',
			(event: any) => {
				event.stop();

				openDialog();
			},
			{priority: 'high'}
		);
	}
}

export default LinkItemSelector;
