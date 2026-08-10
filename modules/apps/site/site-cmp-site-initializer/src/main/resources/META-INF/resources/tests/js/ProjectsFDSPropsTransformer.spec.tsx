/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {deleteItemAction} from '@liferay/site-cms-site-initializer';
import {render, screen} from '@testing-library/react';
import React from 'react';

import ProjectsFDSPropsTransformer from '../../js/components/props_transformer/ProjectsFDSPropsTransformer';

jest.mock('@liferay/site-cms-site-initializer', () => ({
	SimpleActionLinkRenderer: jest.requireActual(
		'@liferay/site-cms-site-initializer/src/main/resources/META-INF/resources/js/main_view/props_transformer/cell_renderers/SimpleActionLinkRenderer'
	).default,
	addOnClickToCreationMenuItems: jest.fn(),
	deleteItemAction: jest.fn(),
}));

const liferayLanguageGet = Liferay.Language.get;

Liferay.Language.get = (key: string) =>
	key === 'delete-project-confirmation-body'
		? 'You are about to delete the project "{0}" and its tasks.'
		: liferayLanguageGet(key);

const PROJECT_TITLE = 'Summer Campaign';

const VIEW_PROJECT_HREF = '/group/guest/projects/view/1701';

const baseProps = {
	additionalProps: {
		fileMimeTypeCssClasses: {default: 'file-icon-color-0'},
		fileMimeTypeIcons: {default: 'document-default'},
		objectDefinitionCssClasses: {default: 'content-icon-custom-structure'},
		objectDefinitionIcons: {default: 'forms'},
	} as any,
	creationMenu: {primaryItems: []},
	itemsActions: [],
};

const viewAction = {
	data: {id: 'actionLink'},
	href: VIEW_PROJECT_HREF,
};

/**
 * Renders the project title cell the way Frontend Data Set does, using the
 * renderer the transformer registers for the title column.
 */
function renderTitleCell(itemActions: Record<string, unknown>) {
	const {customRenderers} = ProjectsFDSPropsTransformer(baseProps);

	const {component: TitleCell} = customRenderers.tableCell.find(
		({name}) => name === 'simpleActionLinkTableCellRenderer'
	)!;

	return render(
		<TitleCell
			actions={[viewAction]}
			itemData={{
				actions: itemActions,
				embedded: {
					id: 1701,
					systemProperties: {
						objectDefinitionBrief: {
							externalReferenceCode: 'L_CMP_PROJECT',
						},
					},
					title: PROJECT_TITLE,
				},
				entryClassName:
					'com.liferay.object.model.ObjectDefinition#CMPP',
			}}
			options={{actionId: 'actionLink'}}
			value={PROJECT_TITLE}
		/>
	);
}

describe('ProjectsFDSPropsTransformer', () => {
	it('escapes the project title in the delete confirmation message', async () => {
		const {onActionDropdownItemClick} =
			ProjectsFDSPropsTransformer(baseProps);

		await onActionDropdownItemClick({
			action: {data: {id: 'delete'}},
			itemData: {
				embedded: {title: '<script>alert(1)</script>'},
			} as any,
			loadData: jest.fn(),
		});

		const [confirmationMessage] = (deleteItemAction as jest.Mock).mock
			.calls[0];

		expect(confirmationMessage).toContain(
			'&lt;script&gt;alert(1)&lt;&#047;script&gt;'
		);
		expect(confirmationMessage).not.toContain('<script>');
	});

	it('links the project title for a user who can only view the project', () => {
		renderTitleCell({get: {}});

		expect(screen.getByRole('link', {name: PROJECT_TITLE})).toHaveAttribute(
			'href',
			VIEW_PROJECT_HREF
		);
	});

	it('links the project title for a user who can update the project', () => {
		renderTitleCell({get: {}, update: {}});

		expect(screen.getByRole('link', {name: PROJECT_TITLE})).toHaveAttribute(
			'href',
			VIEW_PROJECT_HREF
		);
	});
});
