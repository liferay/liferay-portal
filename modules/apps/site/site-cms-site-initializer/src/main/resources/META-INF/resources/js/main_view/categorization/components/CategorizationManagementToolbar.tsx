/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import ClayIcon from '@clayui/icon';
import {ManagementToolbar} from 'frontend-js-components-web';
import {navigate} from 'frontend-js-web';
import React from 'react';

interface Props {
	backURL: string | URL;
	handleSave: () => void;
	handleSaveAndAddAnother?: () => void;
	showSaveAndAddAnotherButton?: boolean;
	title: string;
}

const CategorizationManagementToolbar = ({
	backURL,
	handleSave,
	handleSaveAndAddAnother,
	showSaveAndAddAnotherButton = false,
	title,
}: Props) => {
	return (
		<>
			<ManagementToolbar.Container>
				<ManagementToolbar.ItemList className="c-gap-3" expand>
					<ManagementToolbar.Item>
						<ClayButton
							aria-label={Liferay.Language.get('back')}
							className="btn btn-monospaced btn-outline-borderless btn-outline-secondary btn-sm"
							onClick={() => navigate(backURL)}
						>
							<ClayIcon symbol="angle-left" />
						</ClayButton>
					</ManagementToolbar.Item>

					<ManagementToolbar.Item className="nav-item-expand">
						<h2 className="font-weight-semi-bold m-0 text-5">
							{title}
						</h2>
					</ManagementToolbar.Item>

					<ManagementToolbar.Item>
						<ClayButton
							className="btn btn-outline-borderless btn-outline-secondary btn-sm"
							data-testid="cancel-button"
							onClick={() => navigate(backURL)}
						>
							{Liferay.Language.get('cancel')}
						</ClayButton>
					</ManagementToolbar.Item>

					{showSaveAndAddAnotherButton && handleSaveAndAddAnother && (
						<ManagementToolbar.Item>
							<ClayButton
								data-testid="save-and-add-another-button"
								onClick={handleSaveAndAddAnother}
								outline={true}
								size="sm"
							>
								{Liferay.Language.get('save-and-add-another')}
							</ClayButton>
						</ManagementToolbar.Item>
					)}

					<ManagementToolbar.Item>
						<ClayButton
							data-testid="save-button"
							displayType="primary"
							onClick={handleSave}
							size="sm"
						>
							{Liferay.Language.get('save')}
						</ClayButton>
					</ManagementToolbar.Item>
				</ManagementToolbar.ItemList>
			</ManagementToolbar.Container>
		</>
	);
};

export default CategorizationManagementToolbar;
