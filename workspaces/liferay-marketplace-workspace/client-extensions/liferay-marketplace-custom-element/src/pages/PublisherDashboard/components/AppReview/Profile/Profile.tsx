/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayIcon from '@clayui/icon';

import {AppReviewProps} from '../AppReview';

const Profile = ({context}: AppReviewProps) => {
	return (
		<div className="align-items-center d-flex">
			{context.profile.file.preview ? (
				<img
					alt="App logo"
					className="app-review-logo-icon"
					src={context.profile.file.preview}
				/>
			) : (
				<ClayIcon
					aria-label="New App logo"
					className="app-review-logo-icon text-muted"
					symbol="picture"
				/>
			)}

			<div className="d-flex flex-column pl-5">
				<span className="app-review-name">{context.profile.name}</span>
				<span className="app-review-version">
					{context.version.version}
				</span>
			</div>
		</div>
	);
};

export default Profile;
