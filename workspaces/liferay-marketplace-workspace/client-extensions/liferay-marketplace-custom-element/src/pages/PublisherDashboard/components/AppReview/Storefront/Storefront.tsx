/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayIcon from '@clayui/icon';

import VideoThumbnail from '../../../../../components/VideoThumbnail';
import i18n from '../../../../../i18n';
import {AppReviewProps} from '../AppReview';
import AppReviewSection from '../AppReviewSection';

const Storefront = ({
	context,
	editNavigate,
	required = false,
}: AppReviewProps) => {
	return (
		<AppReviewSection
			editNavigate={editNavigate}
			required={required}
			title={i18n.translate('storefront')}
		>
			<div>
				<div className="app-review-storefront-container">
					<span className="storefront-section-title">Images</span>

					{context.storefront.images.map((image, index) => (
						<div className="d-flex mt-3" key={index}>
							<img draggable={false} src={image.preview} />

							<div className="d-flex flex-column ml-4">
								<ClayIcon
									className="icon-image"
									symbol="document-image"
								/>

								<span className="storefront-url-title">
									{image.fileName}
								</span>

								<span className="storefront-description">
									{image.imageDescription}
								</span>
							</div>
						</div>
					))}
				</div>

				<p className="app-review-storefront-important-note">
					{i18n.translate(
						'important-images-will-be-displayed-following-the-numerical-order-above'
					)}
				</p>

				{context.storefront.video.videoURL && (
					<div className="app-review-storefront-container mt-5">
						<span className="storefront-section-title">VIDEO</span>
						<div className="d-flex mt-3">
							<VideoThumbnail
								videoURL={context.storefront.video.videoURL}
							/>

							<div className="d-flex flex-column ml-4">
								<ClayIcon
									className="icon-image"
									symbol="video"
								/>

								<span className="storefront-url-title">
									{context.storefront.video.videoURL}
								</span>

								<span className="storefront-description">
									{context.storefront.video.description}
								</span>
							</div>
						</div>
					</div>
				)}
			</div>
		</AppReviewSection>
	);
};

export default Storefront;
