import ClayButton from '@clayui/button';
import React, {useState} from 'react';
import {Pendo, TrackingConsentValues} from 'shared/util/pendo';

interface ITrackingConsentBannerProps {
	onDecision: (consent: TrackingConsentValues) => void;
}

/**
 * Consent banner shown before any Pendo tracking starts. Mirrors the copy
 * and behavior of the DXP tracking script banner so users get the same
 * consent experience across products. Persists the decision itself and
 * reports it to the parent through `onDecision`.
 */
const TrackingConsentBanner: React.FC<ITrackingConsentBannerProps> = ({
	onDecision,
}) => {
	const [expanded, setExpanded] = useState(false);

	const handleDecision = (accepted: boolean) => () => {
		new Pendo().setUserConsent(accepted);

		onDecision(
			accepted
				? TrackingConsentValues.Accepted
				: TrackingConsentValues.Declined
		);
	};

	return (
		<div
			aria-label={Liferay.Language.get('product-analytics-banner-title')}
			className="bg-dark d-flex fixed-bottom flex-column flex-md-row justify-content-between p-4 text-white"
			role="dialog"
		>
			<div>
				<div className="font-weight-semi-bold mb-2">
					{Liferay.Language.get('product-analytics-banner-title')}
				</div>

				<p className="mb-1 small">
					{Liferay.Language.get(
						'product-analytics-banner-description'
					)}{' '}
					<a
						className="text-white"
						href="https://www.liferay.com/privacy-policy"
						rel="noopener noreferrer"
						target="_blank"
					>
						<u>
							{Liferay.Language.get('visit-our-privacy-policy')}
						</u>
					</a>
					{'.'}
				</p>

				{expanded && (
					<>
						<p className="mb-1 small">
							{Liferay.Language.get('show-more-accept-all')}
						</p>

						<p className="mb-1 small">
							{Liferay.Language.get('show-more-decline-all')}
						</p>
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
				>
					{Liferay.Language.get('accept-all')}
				</ClayButton>

				<ClayButton
					displayType="secondary"
					onClick={handleDecision(false)}
				>
					{Liferay.Language.get('decline-all')}
				</ClayButton>
			</div>
		</div>
	);
};

export default TrackingConsentBanner;
