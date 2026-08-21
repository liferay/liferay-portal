import ClayButton from '@clayui/button';
import ClayLink from '@clayui/link';
import React, {useEffect, useState} from 'react';
import {Pendo, TrackingConsentValues} from 'shared/util/pendo';
import {Text} from '@clayui/core';

interface ITrackingConsentBannerProps {
	onDecision: (consent: TrackingConsentValues) => void;
}

/**
 * Consent banner shown before any Pendo tracking starts. Mirrors the copy
 * and behavior of the DXP tracking script banner so users get the same
 * consent experience across products. The stored cookie is the only source
 * of truth: the banner reads it on every render, so it shows only while no
 * decision exists, and reports the decision to the parent through
 * `onDecision` — which is also what re-renders it once one is stored.
 */
const TrackingConsentBanner: React.FC<ITrackingConsentBannerProps> = ({
	onDecision,
}) => {
	const [expanded, setExpanded] = useState(false);

	const consent = new Pendo().getUserConsent();

	useEffect(() => {
		if (consent) {
			onDecision(consent);
		}
	}, [consent, onDecision]);

	const handleDecision = (accepted: boolean) => () => {
		new Pendo().setUserConsent(accepted);

		onDecision(
			accepted
				? TrackingConsentValues.Accepted
				: TrackingConsentValues.Declined
		);
	};

	if (consent) {
		return null;
	}

	return (
		<div
			aria-label={Liferay.Language.get('product-analytics-banner-title')}
			className="bg-dark d-flex fixed-bottom flex-column flex-md-row justify-content-between p-4 text-white"
			role="dialog"
		>
			<div>
				<div className="mb-2">
					<Text weight="semi-bold">
						{Liferay.Language.get('product-analytics-banner-title')}
					</Text>
				</div>

				<div className="mb-1">
					<Text size={3}>
						{Liferay.Language.get(
							'product-analytics-banner-description'
						)}

						<ClayLink
							className="ml-1 text-white"
							href="https://www.liferay.com/privacy-policy"
							target="_blank"
						>
							<u>
								{Liferay.Language.get(
									'visit-our-privacy-policy'
								)}
							</u>
						</ClayLink>
					</Text>
				</div>

				{expanded && (
					<>
						<div className="mb-1">
							<Text size={3}>
								{Liferay.Language.get('show-more-accept-all')}
							</Text>
						</div>

						<div className="mb-1">
							<Text size={3}>
								{Liferay.Language.get('show-more-decline-all')}
							</Text>
						</div>
					</>
				)}

				<ClayButton
					className="border-0 p-0 small text-white"
					displayType="link"
					onClick={() => setExpanded(!expanded)}
					size="sm"
				>
					<u>
						{expanded
							? Liferay.Language.get('show-less')
							: Liferay.Language.get('show-more')}
					</u>
				</ClayButton>
			</div>

			<div className="align-self-end align-self-md-center d-flex flex-shrink-0 ml-md-4 mt-3 mt-md-0">
				<ClayButton
					className="mr-2"
					displayType="secondary"
					onClick={handleDecision(true)}
					size="sm"
				>
					{Liferay.Language.get('accept-all')}
				</ClayButton>

				<ClayButton
					displayType="secondary"
					onClick={handleDecision(false)}
					size="sm"
				>
					{Liferay.Language.get('decline-all')}
				</ClayButton>
			</div>
		</div>
	);
};

export default TrackingConsentBanner;
