/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React from 'react';

import AccountSticker from '../../../../common/components/AccountSticker';
import {TVisitor} from '../../../../common/utils/types';

export default function VisitorStickerRenderer({
	itemData,
}: {
	itemData: TVisitor;
}) {
	const hasName = itemData.firstName || itemData.lastName;

	return (
		<div className="d-flex inline-item">
			<AccountSticker
				logoURL={itemData.logoURL}
				name={
					itemData.firstName ??
					itemData.lastName ??
					Liferay.Language.get('anonymous')
				}
				shape="user-icon"
			/>

			<div className="ml-3">
				<div className="align-items-center font-weight-semi-bold visitors-full-name">
					{hasName ? (
						<>
							<span className="mb-0 mr-1">
								{itemData.firstName}
							</span>

							<span className="mb-0">{itemData.lastName}</span>
						</>
					) : (
						<span className="mb-0">
							{Liferay.Language.get('anonymous')}
						</span>
					)}
				</div>

				<div className="align-items-center">
					<span className="mb-0 mr-1">
						{itemData.activitiesCount}
					</span>

					<span className="mb-0">
						{Liferay.Language.get('actions')}
					</span>
				</div>

				<p className="email-text mb-0 text-secondary">
					{itemData.emailAddress}
				</p>
			</div>
		</div>
	);
}
