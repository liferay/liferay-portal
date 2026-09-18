/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Plugin} from '@ckeditor/ckeditor5-core/dist/index.js';
import {EmailConfigurationHelper} from '@ckeditor/ckeditor5-email/dist/index.js';
import {ExportInlineStyles} from '@ckeditor/ckeditor5-export-inline-styles/dist/index.js';
import {EmptyBlock} from '@ckeditor/ckeditor5-html-support/dist/index.js';
import {Mention} from '@ckeditor/ckeditor5-mention/dist/index.js';
import {MergeFields} from '@ckeditor/ckeditor5-merge-fields/dist/index.js';
import {Table, TableLayout} from '@ckeditor/ckeditor5-table/dist/index.js';
import {Template} from '@ckeditor/ckeditor5-template/dist/index.js';
import {ButtonView} from '@ckeditor/ckeditor5-ui/dist/index.js';
import {
	EditorConfigTransformer,
	EditorTransformer,
} from '@liferay/js-api/editor';

const EXPORT_ICON =
	'<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 20 20"><path d="M10 13.5 5.5 9l1.4-1.4L9 9.7V2h2v7.7l2.1-2.1L14.5 9z"/><path d="M4 15h12v2H4z"/></svg>';

const MERGE_FIELDS_CONFIG = {
	dataSets: [
		{
			id: 'sample-recipient',
			label: 'Sample Recipient',
			values: {
				'company-name': 'Liferay',
				'email': 'jane.doe@example.com',
				'first-name': 'Jane',
				'last-name': 'Doe',
			},
		},
	],
	definitions: [
		{
			definitions: [
				{id: 'first-name', label: 'First Name'},
				{id: 'last-name', label: 'Last Name'},
				{id: 'email', label: 'Email Address'},
			],
			groupId: 'recipient',
			groupLabel: 'Recipient',
		},
		{id: 'company-name', label: 'Company Name'},
	],
};

const TEMPLATE_CONFIG = {
	definitions: [
		{
			data: '<p>Hi {{first-name}},</p><p>Welcome to {{company-name}}! We are excited to have you on board.</p>',
			description:
				'A short welcome message with merge fields for the recipient name and company.',
			title: 'Welcome Email',
		},
		{
			data: '<p>Best regards,<br>The {{company-name}} Team</p>',
			description: 'A simple email sign-off.',
			title: 'Signature',
		},
	],
};

class ExportInlineStylesPreview extends Plugin {
	init() {
		const editor = this.editor;

		editor.ui.componentFactory.add('exportInlineStylesPreview', () => {
			const button = new ButtonView();

			button.set({
				icon: EXPORT_ICON,
				label: 'Preview with Inline Styles',
				tooltip: true,
			});

			button.on('execute', async () => {
				const html = await editor.commands.execute(
					'exportInlineStyles',
					{}
				);

				window.open(
					URL.createObjectURL(new Blob([html], {type: 'text/html'})),
					'_blank'
				);
			});

			return button;
		});
	}
}

const editorConfigTransformer: EditorConfigTransformer<any> = (config) => {
	const toolbar = config.toolbar as any;
	const existingItems = Array.isArray(toolbar)
		? toolbar
		: toolbar?.items ?? [];

	const extraPlugins = [
		...(config.extraPlugins ?? []),
		Table,
		TableLayout,
		EmptyBlock,
		EmailConfigurationHelper,
		Mention,
		MergeFields,
		Template,
		ExportInlineStyles,
		ExportInlineStylesPreview,
	];

	const toolbarItems = [
		...existingItems,
		'|',
		'insertTable',
		'insertTableLayout',
		'insertMergeField',
		'previewMergeFields',
		'insertTemplate',
		'exportInlineStylesPreview',
	];

	return {
		...config,

		// These paths follow the __liferay__/css/<bundle>$<export>.css
		// convention Liferay's build tooling uses to expose a module's
		// bundled CSS exports; it's an internal resolution mechanism, not a
		// public API. A developer copying this pattern into their own
		// project must find the equivalent path for their own module by
		// inspecting its built __liferay__/css output (or the network
		// requests the browser makes for that module's CSS), not by
		// reusing the frontend-editor-ckeditor-web path above.

		exportInlineStyles: {
			stripCssClasses: true,
			stylesheets: [
				'/o/frontend-editor-ckeditor-web/__liferay__/css/ckeditor5$ckeditor5.css',
				'/o/frontend-editor-ckeditor-web/__liferay__/css/ckeditor5-premium-features$ckeditor5-premium-features.css',
			],
		},
		extraPlugins,
		mergeFields: MERGE_FIELDS_CONFIG,
		template: TEMPLATE_CONFIG,
		toolbar: {
			items: toolbarItems,
		},
	};
};

const editorTransformer: EditorTransformer<any> = {
	editorConfigTransformer,
};

export default editorTransformer;
