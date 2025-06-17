/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import Button from '@clayui/button';
import ClayIcon from '@clayui/icon';
import {ReactNode} from 'react';

import i18n from '../../../../i18n';

type AppReviewSectionProps = {
	children: ReactNode;
	editNavigate?: () => void;
	isLastSection?: boolean;
	required?: boolean;
	title: string;
};

const AppReviewSection = ({
	children,
	editNavigate,
	isLastSection = false,
	required = false,
	title,
}: AppReviewSectionProps) => (
	<>
		<div className="app-review-section">
			<div className="d-flex justify-content-between">
				<div className="d-flex">
					<h3>{title}</h3>

					{required && (
						<span>
							<ClayIcon
								className="ml-1 required-asterisk-icon text-danger"
								symbol="asterisk"
							/>
						</span>
					)}
				</div>

				{editNavigate && (
					<Button
						className="edit-button"
						displayType="link"
						onClick={() => editNavigate?.()}
					>
						{i18n.translate('edit')}
					</Button>
				)}
			</div>
			{children}
		</div>

		{!isLastSection && <hr />}
	</>
);
export default AppReviewSection;
