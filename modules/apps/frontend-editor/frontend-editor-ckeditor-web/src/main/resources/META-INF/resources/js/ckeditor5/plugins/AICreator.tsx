/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ButtonView, Command, Config, Plugin} from 'ckeditor5';
import {openModal} from 'frontend-js-components-web';

import getIcon from '../utils/getIcon';
import {LiferayEditorConfig} from '../utils/types';

const ON_CLICK_POPOVER_CONTENT = `
<div class="arrow"></div>
<div class="inline-scroller">
	<div class="popover-header">
		${Liferay.Language.get('configure-openai')}
	</div>
	<div class="popover-body">
		${Liferay.Language.get(
			'authentication-is-needed-to-use-this-feature.-contact-your-administrator-to-add-an-api-key-in-instance-or-site-settings'
		)}
	</div>
</div>
`;

class AICreator extends Plugin {
	init() {
		const editor = this.editor;

		let aiCreatorButton: HTMLElement | null = null;
		let popover: HTMLElement | null = null;

		function hidePopover() {
			if (popover && document.body.contains(popover)) {
				document.body.removeChild(popover);
				popover = null;

				if (aiCreatorButton) {
					aiCreatorButton.focus();
				}
			}
		}

		function showPopover() {
			hidePopover();

			if (!aiCreatorButton) {
				return;
			}

			popover = document.createElement('div');
			popover.className = 'clay-popover-top fade popover show';
			popover.innerHTML = ON_CLICK_POPOVER_CONTENT;
			popover.setAttribute('role', 'alert');
			popover.setAttribute('tabindex', '0');

			document.body.appendChild(popover);

			requestAnimationFrame(() => {
				const buttonRect = aiCreatorButton!.getBoundingClientRect();
				const popoverRect = popover!.getBoundingClientRect();

				if (popover) {
					popover.style.top = `${buttonRect.top - popoverRect.height}px`;
					popover.style.left = `${Math.floor(
						buttonRect.left +
							buttonRect.width / 2 -
							popoverRect.width / 2
					)}px`;
				}
			});
		}

		const config: Config<LiferayEditorConfig> = editor.config;

		const aiCreatorOpenAIURL = config.get('aiCreatorOpenAIURL') as string;
		const aiCreatorPortletNamespace = config.get(
			'aiCreatorPortletNamespace'
		);
		const contentsLanguage = config.get('contentsLanguage') as string;
		const isAICreatorOpenAIAPIKey = config.get('isAICreatorOpenAIAPIKey');

		editor.commands.add('openAICreatorDialog', new Command(editor));
		editor.commands.add(
			'openAICreatorConfigurationPopover',
			new Command(editor)
		);

		const openAICreatorDialogCommand = editor.commands.get(
			'openAICreatorDialog'
		)!;
		const openAICreatorConfigurationPopoverCommand = editor.commands.get(
			'openAICreatorConfigurationPopover'
		)!;

		openAICreatorDialogCommand.execute = () => {
			const closeModalHandler = Liferay.on('closeModal', (event) => {
				closeModalHandler.detach();

				if (event.text) {
					editor.model.change((writer) => {
						let insertPosition =
							editor.model.document.selection.getFirstPosition();

						if (!insertPosition) {
							const root = editor.model.document.getRoot();
							if (!root) {
								return;
							}
							insertPosition = writer.createPositionAt(
								root,
								'end'
							);
						}

						writer.insertText(event.text, insertPosition);
					});
				}
			});

			const url = new URL(aiCreatorOpenAIURL);

			url.searchParams.set(
				`${aiCreatorPortletNamespace}languageId`,
				contentsLanguage
			);

			openModal({
				height: '550px',
				onClose: () => closeModalHandler.detach(),
				size: 'lg',
				title: Liferay.Language.get('ai-creator'),
				url: url.toString(),
			});
		};

		openAICreatorConfigurationPopoverCommand.execute = () => {
			showPopover();

			const removePopover = () => {
				popover?.removeEventListener('blur', removePopover);
				hidePopover();
			};

			requestAnimationFrame(() => {
				popover?.focus();
				popover?.addEventListener('blur', removePopover);
			});
		};

		editor.ui.componentFactory.add('aiCreator', () => {
			const buttonView = new ButtonView();

			buttonView.set({
				icon: getIcon({symbol: 'stars'}),
				label: Liferay.Language.get('ai-creator'),
				tooltip: Liferay.Language.get('create-ai-content'),
				withText: true,
			});

			buttonView
				.bind('isEnabled')
				.to(openAICreatorDialogCommand, 'isEnabled');

			buttonView.on('render', () => {
				aiCreatorButton = buttonView.element!;
				aiCreatorButton.classList.add('lfr-portal-tooltip');
				aiCreatorButton.removeAttribute('aria-labelledby');
				aiCreatorButton.setAttribute(
					'aria-label',
					Liferay.Language.get('create-ai-content')
				);
			});

			buttonView.on('execute', () => {
				isAICreatorOpenAIAPIKey
					? openAICreatorDialogCommand.execute()
					: openAICreatorConfigurationPopoverCommand.execute();
			});

			return buttonView;
		});
	}
}

export default AICreator;
