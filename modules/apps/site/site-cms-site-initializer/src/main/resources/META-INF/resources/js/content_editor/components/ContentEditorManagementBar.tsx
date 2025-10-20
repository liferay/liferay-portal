/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '../../../css/content_editor/ContentEditorManagementBar.scss';

import ClayButton from '@clayui/button';
import {ClayInput} from '@clayui/form';
import ClayIcon from '@clayui/icon';
import ClayLink from '@clayui/link';
import {ManagementToolbar} from 'frontend-js-components-web';
import React, {useEffect, useState} from 'react';

export const EVENT_VALIDATE_FORM = 'contentEditor:validateForm';

const STATUS_DRAFT_CODE = 2;

export default function ContentEditorManagementBar({
	backURL,
	hasWorkflow,
	headerTitle,
}: {
	backURL: string;
	hasWorkflow: boolean;
	headerTitle: string;
}) {
	const [formId, setFormId] = useState<string | undefined>();

	useEffect(() => {
		let form = document.querySelector('.lfr-main-form-container');

		if (!form) {
			form = document.querySelector('.lfr-layout-structure-item-form');
		}

		if (form) {
			setFormId(form.id);
		}
	}, []);

	return (
		<ManagementToolbar.Container className="border content-editor__management-bar position-fixed">
			<ManagementToolbar.ItemList className="c-gap-3" expand>
				<ManagementToolbar.Item>
					<ClayLink
						aria-label={Liferay.Language.get('back')}
						borderless
						displayType="secondary"
						href={backURL}
						monospaced
						outline
						small
					>
						<ClayIcon symbol="angle-left" />
					</ClayLink>
				</ManagementToolbar.Item>

				<ManagementToolbar.Item className="nav-item-expand">
					<h2 className="font-weight-semi-bold m-0 text-5">
						{headerTitle}
					</h2>
				</ManagementToolbar.Item>

				<ManagementToolbar.Item>
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
				</ManagementToolbar.Item>

				<ManagementToolbar.Item>
					<ClayButton
						displayType="secondary"
						form={formId}
						name="status"
						size="sm"
						type="submit"
						value={STATUS_DRAFT_CODE}
					>
						{Liferay.Language.get('save-as-draft')}
					</ClayButton>
				</ManagementToolbar.Item>

				<ManagementToolbar.Item>
					<ClayButton
						displayType="primary"
						form={formId}
						onClick={(event) => {
							Liferay.fire(EVENT_VALIDATE_FORM, {event});
						}}
						size="sm"
						type="submit"
					>
						{hasWorkflow
							? Liferay.Language.get('submit-for-workflow')
							: Liferay.Language.get('publish')}
					</ClayButton>

					<ClayInput
						form={formId}
						name="redirect"
						type="hidden"
						value={backURL}
					/>
				</ManagementToolbar.Item>
			</ManagementToolbar.ItemList>
		</ManagementToolbar.Container>
	);
}
