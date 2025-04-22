/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton, {ClayButtonWithIcon} from '@clayui/button';
import ClayToolbar from '@clayui/toolbar';
import React from 'react';

interface EditFolderProps {
	description?: string;
	name: string;
	space: string;
}

const EditFolder: React.FC<EditFolderProps> = ({description, name, space}) => {
	return (
		<div className="edit-folder">
			<ClayToolbar className="container-fluid" light>
				<ClayToolbar.Nav>
					<ClayToolbar.Item>
						<ClayButtonWithIcon
							aria-label={Liferay.Language.get('back')}
							borderless
							displayType="secondary"
							monospaced
							size="sm"
							symbol="angle-left"
							title={Liferay.Language.get('back')}
						/>
					</ClayToolbar.Item>

					<ClayToolbar.Item className="text-left" expand>
						<h2 className="font-weight-semi-bold m-0 text-5">
							{name}
						</h2>
					</ClayToolbar.Item>

					<ClayToolbar.Item>
						<ClayButton
							borderless
							displayType="secondary"
							size="sm"
						>
							{Liferay.Language.get('cancel')}
						</ClayButton>
					</ClayToolbar.Item>

					<ClayToolbar.Item>
						<ClayButton displayType="primary" size="sm">
							{Liferay.Language.get('save')}
						</ClayButton>
					</ClayToolbar.Item>
				</ClayToolbar.Nav>
			</ClayToolbar>

			Edit Folder Page {name} {description} {space}
		</div>
	);
};

export default EditFolder;
