/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayIcon from '@clayui/icon';
import i18n from '~/utils/I18n';

import './AttachmentMessage.css';

type MyLocationState = {
	attachmentName: string;
};

interface AttachmentMessagesProps {
	children?: React.ReactNode;
	icon: string;
	state?: MyLocationState;
	subtitle: string;
	title: string;
}

const AttachmentMessages: React.FC<AttachmentMessagesProps> = ({
	children,
	icon,
	state,
	subtitle,
	title,
}) => {
	return (
		<div className="uploader-confirmation-container">
			<div className="uploader-confirmation-box-containter">
				<div className="d-flex justify-content-center pb-4">
					<div className="uploader-icon">
						<ClayIcon symbol={icon} />
					</div>
				</div>

				<div className="d-flex justify-content-center pb-4 text-neutral-10">
					<h3
						className="mb-0 text-center"
						dangerouslySetInnerHTML={{
							__html: i18n.translate(title),
						}}
					/>
				</div>

				<div className="d-flex justify-content-center pb-5">
					{state ? (
						<p
							className="mb-0 text-center"
							dangerouslySetInnerHTML={{
								__html: i18n.sub(`${subtitle}`, [
									`<strong>${state?.attachmentName}</strong> <br>`,
								]),
							}}
						/>
					) : (
						<p className="mb-0 text-center">
							{i18n.translate(subtitle)}
						</p>
					)}
				</div>
				<div>
					<div className="d-flex justify-content-center">
						{children}
					</div>
				</div>
			</div>
		</div>
	);
};

export default AttachmentMessages;
