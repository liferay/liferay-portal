/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import {ClayInput} from '@clayui/form';
import ClayLink from '@clayui/link';
import {AIAssistantChat} from '@liferay/ai-hub-cell-js-components-web';
import {isCtrlOrMeta} from '@liferay/layout-js-components-web';
import {Toolbar} from '@liferay/site-cms-site-initializer';
import {sessionStorage, sub} from 'frontend-js-web';
import React, {useEffect, useId, useState} from 'react';

export default function EditorToolbar({
	backURL,
	formSubmitURL,
	groupId,
	isNew,
	title,
}: {
	backURL: string;
	formSubmitURL?: string;
	groupId: number;
	isNew: boolean;
	title: string;
}) {
	const [formId, setFormId] = useState<string | undefined>();

	const submitLabelId = useId();
	const submitTitle = getSubmitTitle(
		sub(Liferay.Language.get('save-x'), title)
	);

	function getForm(): HTMLFormElement {
		let form = document.querySelector('.lfr-main-form-container');

		if (!form) {
			form = document.querySelector('.lfr-layout-structure-item-form');
		}

		return form as HTMLFormElement;
	}

	useEffect(() => {
		const form = getForm();

		if (form) {
			setFormId(form.id);

			const handlePublishShortcut = (event: KeyboardEvent) => {
				const isShortcut =
					event.key === 'Enter' &&
					event.altKey &&
					isCtrlOrMeta(event);

				if (
					event.key === 'Enter' &&
					(event.target as HTMLElement).tagName === 'INPUT' &&
					!isShortcut
				) {
					event.preventDefault();
				}

				if (isShortcut) {
					(form as HTMLFormElement).submit();
				}
			};

			window.addEventListener('keydown', handlePublishShortcut);

			return () =>
				window.removeEventListener('keydown', handlePublishShortcut);
		}
	}, []);

	return (
		<Toolbar
			backURL={backURL}
			className="content-editor__toolbar position-fixed"
			title={title}
		>
			{Liferay.FeatureFlags['LPD-62272'] && (
				<>
					<Toolbar.Item>
						<AIAssistantChat
							context={{groupId}}
							hideTriggerLabel
							instructionDefinitionScope="cms"
							triggerRound
						/>
					</Toolbar.Item>

					<div className="ai-assistant__separator" />
				</>
			)}

			<Toolbar.Item>
				<ClayLink
					aria-label={Liferay.Language.get('cancel')}
					borderless
					button
					displayType="secondary"
					href={backURL}
					small
				>
					{Liferay.Language.get('cancel')}
				</ClayLink>
			</Toolbar.Item>

			<Toolbar.Item>
				<ClayButton
					aria-labelledby={submitLabelId}
					data-title={submitTitle}
					data-title-set-as-html
					form={formId}
					onClick={() => {
						const form = getForm();

						if (form && form.checkValidity()) {
							const {value} = form.querySelector(
								'[name^="ObjectField_title"]'
							) as HTMLInputElement;

							sessionStorage.setItem(
								'com.liferay.site.cmp.site.initializer.successMessage',
								sub(
									isNew
										? Liferay.Language.get(
												'x-was-created-successfully'
											)
										: Liferay.Language.get(
												'x-was-updated-successfully'
											),
									`<strong>${value}</strong>`
								),
								sessionStorage.TYPES.NECESSARY
							);
						}
					}}
					size="sm"
					type="submit"
				>
					{Liferay.Language.get('save')}
				</ClayButton>

				<span
					className="sr-only"
					dangerouslySetInnerHTML={{__html: submitTitle}}
					id={submitLabelId}
				/>

				<ClayInput
					form={formId}
					name="redirect"
					type="hidden"
					value={formSubmitURL || backURL}
				/>
			</Toolbar.Item>
		</Toolbar>
	);
}

function getSubmitTitle(title: string) {
	const isMac = Liferay.Browser?.isMac();

	return `
        <span class="d-block">
            ${title}
        </span>
        <kbd class="c-kbd c-kbd-dark mt-1">
            <kbd class="c-kbd">${isMac ? '⌘' : 'Ctrl'}</kbd>
            <span class="c-kbd-separator"> + </span>
            <kbd class="c-kbd">${isMac ? '⌥' : 'Alt'}</kbd>
            <span class="c-kbd-separator"> + </span>
            <kbd class="c-kbd">${Liferay.Language.get('enter')}</kbd>
        </kbd>`
		.replaceAll('\n', '')
		.replaceAll('\t', '');
}
