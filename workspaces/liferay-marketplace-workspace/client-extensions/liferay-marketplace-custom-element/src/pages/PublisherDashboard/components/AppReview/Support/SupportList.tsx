/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayIcon from '@clayui/icon';

import ExternalLink from '../../../../../components/ExternalLink';
import {NewAppInitialState} from '../../../../../context/NewAppContext';
import i18n from '../../../../../i18n';

type SupportContent = {
	symbol: string;
	title: string;
	url?: string;
	urlPreffix?: 'tel' | 'mailto';
};

const SupportContent = ({symbol, title, url, urlPreffix}: SupportContent) => (
	<div className="border mt-5 p-4 rounded-lg">
		<div className="align-items-center d-flex">
			<div className="app-review-support-icon">
				<ClayIcon
					aria-label="Icon"
					className="app-review-support-icon-image"
					symbol={symbol}
				/>
			</div>

			<div className="app-review-support-info">
				<span className="app-review-support-info-text">{title}</span>

				{url && (
					<ExternalLink
						decoration="underline"
						displayType="primary"
						href={urlPreffix ? `${urlPreffix}:${url}` : url}
						showShortcut={!urlPreffix}
						weight="semi-bold"
					>
						{url}
					</ExternalLink>
				)}
			</div>
		</div>
	</div>
);

const SupportList = ({context}: {context: NewAppInitialState}) => {
	const {support} = context;

	return (
		<>
			<SupportContent
				symbol="link"
				title={i18n.translate('support-url')}
				url={support.url}
			/>

			<SupportContent
				symbol="globe"
				title={i18n.translate('publisher-website-url')}
				url={support.publisherWebsiteURL}
			/>

			<SupportContent
				symbol="envelope-open"
				title={i18n.translate('support-email-address')}
				url={support.email}
				urlPreffix="mailto"
			/>

			<SupportContent
				symbol="phone"
				title={i18n.translate('support-phone-number')}
				url={support.phone}
				urlPreffix="tel"
			/>

			<SupportContent
				symbol="info-book"
				title={i18n.translate('app-usage-terms-url')}
				url={support.appUsageTermsURL}
			/>

			<SupportContent
				symbol="order-form-tag"
				title={i18n.translate('app-documentation-url')}
				url={support.documentationURL}
			/>

			<SupportContent
				symbol="sites"
				title={i18n.translate('app-installation-guide-url')}
				url={support.installationGuideURL}
			/>
		</>
	);
};

export default SupportList;
