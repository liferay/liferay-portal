/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayIcon from '@clayui/icon';
import ClayLayout from '@clayui/layout';
import ClaySticker from '@clayui/sticker';
import {sub} from 'frontend-js-web';
import React from 'react';

import {FileData} from './MultipleFileUploader';

export default function FailedFiles({
	errorMessage,
	failedFiles,
}: {
	errorMessage: string;
	failedFiles: FileData[];
}) {
	return (
		<div className="has-error">
			<p className="text-3 text-danger text-weight-semi-bold">
				<ClayIcon className="mr-1" symbol="times-circle-full" />

				{sub(errorMessage, failedFiles.length)}
			</p>

			{failedFiles.map((fileData) => (
				<>
					<ClayLayout.ContentRow
						className="align-items-center border-left-0 border-right-0 border-top-0 form-control mt-2 rounded-0"
						key={fileData.name}
						padded
					>
						<ClayLayout.ContentCol>
							<ClaySticker
								className="sticker-border-secondary"
								displayType="secondary"
								size="lg"
							>
								<ClayIcon symbol="document" />
							</ClaySticker>
						</ClayLayout.ContentCol>

						<ClayLayout.ContentCol className="text-3" expand>
							<span className="text-weight-semi-bold">
								{fileData.name}
							</span>

							{fileData.size && (
								<span className="text-secondary">
									{Liferay.Util.formatStorage(fileData.size, {
										addSpaceBeforeSuffix: true,
									})}
								</span>
							)}
						</ClayLayout.ContentCol>
					</ClayLayout.ContentRow>

					{fileData.errorMessage && (
						<span className="text-3 text-danger">
							<span className="text-weight-semi-bold">
								{Liferay.Language.get('error-colon')}{' '}
							</span>

							{fileData.errorMessage}
						</span>
					)}
				</>
			))}
		</div>
	);
}
