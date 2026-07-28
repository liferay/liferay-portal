/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {render, screen} from '@testing-library/react';
import React from 'react';

import ProjectsFDSPropsTransformer from '../../js/components/props_transformer/ProjectsFDSPropsTransformer';

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
