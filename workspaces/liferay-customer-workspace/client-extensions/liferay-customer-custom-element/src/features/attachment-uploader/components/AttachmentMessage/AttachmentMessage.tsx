/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayIcon from '@clayui/icon';
import i18n from '~/utils/I18n';

import './AttachmentMessage.css';

interface IProps {
	children?: React.ReactNode;
	icon?: string;
	subtitle?: string;
	title: string;
}

const AttachmentMessage: React.FC<IProps> = ({
	children,
	icon,
	subtitle,
	title,
}) => {
	return (
		<div className="attachment-message">
			<div className="attachment-message-containter">
				{icon && (
					<div className="d-flex justify-content-center pb-4">
						<div className="attachment-message-icon">
							<ClayIcon symbol={icon} />
						</div>
					</div>
				)}

				<div className="d-flex justify-content-center pb-4 text-neutral-10">
					<h3 className="mb-0 text-center">
						{i18n.translate(title)}
					</h3>
				</div>

				{subtitle && (
					<div className="d-flex justify-content-center pb-5">
						<p className="mb-0 text-center">
							{i18n.translate(subtitle)}
						</p>
					</div>
				)}

				{children && (
					<div className="d-flex justify-content-center">
						{children}
					</div>
				)}
			</div>
		</div>
	);
};

export default AttachmentMessage;
