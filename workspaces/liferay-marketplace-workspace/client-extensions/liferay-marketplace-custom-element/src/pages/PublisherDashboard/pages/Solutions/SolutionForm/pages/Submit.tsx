/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Tooltip} from '../../../../../../components/Tooltip/Tooltip';
import {
	SolutionTypes,
	useSolutionContext,
} from '../../../../../../context/SolutionContext';
import i18n from '../../../../../../i18n';

import '../../components/ContentReview/ContentReview.scss';

import {ClayCheckbox} from '@clayui/form';
import DOMPurify from 'dompurify';

import en_US from '../../../../../../i18n/en_US';
import {ContentReview} from '../../components/ContentReview';

type SubmitProps = {
	readOnly?: boolean;
};

const Submit: React.FC<SubmitProps> = ({readOnly = false}) => {
	const [context, dispatch] = useSolutionContext();

	const {company, contactUs, details, header, profile, termsAndConditions} =
		context;

	return (
		<div className="mb-4 solutions-form-header">
			{!readOnly && (
				<>
					<span className="align-items-center d-flex">
						<h2 className="mb-0 mr-3">
							{i18n.translate('solution-submission')}
						</h2>

						<Tooltip
							tooltip={i18n.translate('more-info')}
							tooltipText={i18n.translate('more-info')}
						/>
					</span>

					<ContentReview.Separator className="mb-5 mt-2" />
				</>
			)}

			<ContentReview>
				<ContentReview.Section>
					{!readOnly && (
						<ContentReview.Header as="h2" path="../profile">
							<div className="align-items-center d-flex">
								<img
									alt=""
									className="mr-4 solution-preview-profile-logo"
									src={profile.file.preview}
								/>
								<h1 className="mb-0">{profile.name}</h1>
							</div>
						</ContentReview.Header>
					)}
					<ContentReview.Paragraph
						title={i18n.translate('description')}
					>
						{profile.description}
					</ContentReview.Paragraph>

					<ContentReview.Paragraph
						title={i18n.translate('categories')}
					>
						<div className="d-flex">
							{profile.categories.map((category) => (
								<div
									className="mr-3 solution-preview-profile-tags"
									key={category.value}
								>
									{category.label}
								</div>
							))}
						</div>
					</ContentReview.Paragraph>

					<ContentReview.Paragraph title={i18n.translate('tags')}>
						<div className="d-flex">
							{profile.tags.map((tag) => (
								<div
									className="mr-3 solution-preview-profile-tags"
									key={tag.value}
								>
									{tag.label}
								</div>
							))}
						</div>
					</ContentReview.Paragraph>
				</ContentReview.Section>

				<ContentReview.Separator />

				<ContentReview.Section>
					<ContentReview.Header
						as="h2"
						path={readOnly ? '' : '../header'}
					>
						{i18n.translate('header')}
					</ContentReview.Header>
					<ContentReview.Paragraph
						className="mb-5"
						title={i18n.translate(
							header.title as keyof typeof en_US
						)}
					>
						<p
							dangerouslySetInnerHTML={{
								__html: DOMPurify.sanitize(header.description),
							}}
						/>
					</ContentReview.Paragraph>

					<ContentReview.Paragraph className="mb-6">
						{header.contentType.type === 'upload-images' &&
							header.contentType.content.headerImages.map(
								(image, index) => (
									<ContentReview.ImageInfo
										icon="document-image"
										imageFile={image}
										key={index}
									/>
								)
							)}

						{header.contentType.type === 'embed-video-url' && (
							<div className="d-flex flex-row">
								<ContentReview.Video
									className="mr-3"
									videoDescription={
										header.contentType.content
											.headerVideoDescription
									}
									videoUrl={
										header.contentType.content
											.headerVideoUrl
									}
								/>
							</div>
						)}
					</ContentReview.Paragraph>

					<ContentReview.Paragraph>
						{i18n.translate(
							'important-images-will-be-displayed-following-the-numerical-order-above'
						)}
					</ContentReview.Paragraph>
				</ContentReview.Section>

				<ContentReview.Separator />

				{!!details.length && (
					<ContentReview.Section>
						<ContentReview.Header
							as="h2"
							path={readOnly ? '' : '../details'}
						>
							{i18n.translate('solution-details')}
						</ContentReview.Header>

						{details.map((block, index) => (
							<ContentReview.Block
								key={index}
								title={i18n.translate(
									block.type as keyof typeof en_US
								)}
							>
								<ContentReview.Paragraph
									title={i18n.translate('title')}
								>
									{i18n.translate(
										block.content
											.title as keyof typeof en_US
									)}
								</ContentReview.Paragraph>
								<ContentReview.Paragraph
									title={i18n.translate('description')}
								>
									<p
										dangerouslySetInnerHTML={{
											__html: DOMPurify.sanitize(
												block.content.description
											),
										}}
									/>
								</ContentReview.Paragraph>

								{block.type === 'text-images-block' &&
									block.content.files?.map(
										(file, fileIndex) => (
											<ContentReview.ImageInfo
												icon="document-image"
												imageFile={file}
												key={fileIndex}
											/>
										)
									)}

								{block.type === 'text-video-block' && (
									<div className="d-flex">
										<ContentReview.Video
											className="mr-3"
											videoDescription={
												block.content.videoDescription
											}
											videoUrl={block.content.videoUrl}
										/>
									</div>
								)}
							</ContentReview.Block>
						))}
					</ContentReview.Section>
				)}

				{!!details.length && <ContentReview.Separator />}

				<ContentReview.Section>
					<ContentReview.Header
						as="h2"
						className="mb-0"
						path={readOnly ? '' : '../company'}
					>
						{i18n.translate('company-profile')}
					</ContentReview.Header>

					<ContentReview.Paragraph
						title={i18n.translate('description')}
					>
						<p
							dangerouslySetInnerHTML={{
								__html: DOMPurify.sanitize(company.description),
							}}
						/>
					</ContentReview.Paragraph>
					<ContentReview.Paragraph>
						<ContentReview.SupportLink
							href={company.website}
							linkLabel={i18n.translate('publisher-website-url')}
							symbol="globe"
							type="url"
						/>

						<ContentReview.SupportLink
							href={company.email}
							linkLabel={i18n.translate('email')}
							symbol="envelope-closed"
							type="email"
						/>

						<ContentReview.SupportLink
							href={company.phone}
							linkLabel={i18n.translate('phone')}
							symbol="phone"
							type="phone"
						/>
					</ContentReview.Paragraph>
				</ContentReview.Section>

				<ContentReview.Separator />

				<ContentReview.Section>
					<ContentReview.Header
						as="h2"
						className="mb-0"
						path={readOnly ? '' : '../contact'}
					>
						{i18n.translate('contact-us')}
					</ContentReview.Header>
					<ContentReview.SupportLink
						href={contactUs}
						linkLabel={i18n.translate('email')}
						symbol="envelope-closed"
						type="email"
					/>
				</ContentReview.Section>
			</ContentReview>

			{!readOnly && (
				<div className="d-flex my-5">
					<ClayCheckbox
						checked={termsAndConditions}
						onChange={(event) => {
							dispatch({
								payload: event.target.checked,
								type: SolutionTypes.SET_TERMS_AND_CONDITIONS,
							});
						}}
					/>

					<p className="ml-4">
						<b>Attention: this cannot be undone.</b> I am aware I
						cannot edit any data or information regarding this
						solution submission until Liferay completes its review
						process and I agree with the Liferay Marketplace&nbsp;
						<a
							href="https://www.liferay.com/it/legal/marketplace-terms-of-service"
							rel="noopener"
							target="_blank"
						>
							terms
						</a>
						&nbsp;and&nbsp;
						<a
							href="https://www.liferay.com/privacy-policy"
							rel="noopener"
							target="_blank"
						>
							privacy
						</a>
						&nbsp;
					</p>
				</div>
			)}
		</div>
	);
};

export default Submit;
