/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton, {ClayButtonWithIcon} from '@clayui/button';
import ClayLoadingIndicator from '@clayui/loading-indicator';
import React from 'react';

import {AttachmentFile} from './AttachmentBase';

interface FileProps {
	attachment: AttachmentFile | null;
	loading?: boolean;
	onDelete: () => void;
	readOnly: boolean;
}

export default function FileContainer({
	attachment,
	loading,
	onDelete,
	readOnly,
}: FileProps) {
	if (loading) {
		return (
			<ClayLoadingIndicator className="lfr-objects__attachment-loading" />
		);
	}
	else if (attachment) {
		return (
			<>
				<div className="lfr-objects__attachment-title">
					<ClayButton
						displayType="unstyled"
						onClick={() => {
							if (attachment.contentURL) {
								window.open(attachment.contentURL, '_blank');
							}
						}}
					>
						{Liferay.FeatureFlags['LPD-100522'] ? (
							<>
								<span className="font-weight-semi-bold small">
									{attachment.title}
								</span>

								{attachment.size && (
									<span className="d-block small text-secondary">
										{attachment.size}
									</span>
								)}
							</>
						) : (
							attachment.title
						)}
					</ClayButton>

					{Liferay.ThemeDisplay.isSignedIn() &&
						attachment.contentURL && (
							<div className="lfr-objects__attachment-download">
								<ClayButtonWithIcon
									aria-label={Liferay.Language.get(
										'download'
									)}
									borderless
									displayType="secondary"
									monospaced
									onClick={() =>
										window.open(
											attachment.contentURL,
											'_blank'
										)
									}
									symbol="download"
									title={Liferay.Language.get('download')}
								/>
							</div>
						)}

					{!readOnly && attachment.title && (
						<div className="lfr-objects__attachment-delete">
							<ClayButtonWithIcon
								aria-label={Liferay.Language.get('delete')}
								borderless
								displayType="secondary"
								monospaced
								onClick={() => onDelete()}
								symbol="times-circle-full"
								title={Liferay.Language.get('delete')}
							/>
						</div>
					)}
				</div>
			</>
		);
	}

	return null;
}
