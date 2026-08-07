import ClayButton from '@clayui/button';
import React, {useState} from 'react';

interface ITrackingConsentBannerProps {
	onAcceptAll: () => void;
	onDeclineAll: () => void;
}

/**
 * Consent banner shown before any Pendo tracking starts. Mirrors the copy and
 * behavior of the DXP tracking script banner so users get the same consent
 * experience across products.
 */
const TrackingConsentBanner: React.FC<ITrackingConsentBannerProps> = ({
	onAcceptAll,
	onDeclineAll,
}) => {
	const [expanded, setExpanded] = useState(false);

	return (
		<div
			aria-label={Liferay.Language.get('product-analytics-banner-title')}
			className='tracking-consent-banner-root'
			role='dialog'
		>
			<div className='banner-content'>
				<div className='banner-title'>
					{Liferay.Language.get('product-analytics-banner-title')}
				</div>

				<p className='banner-description'>
					{Liferay.Language.get('product-analytics-banner-description')}

					<a
						href='https://www.liferay.com/privacy-policy'
						rel='noopener noreferrer'
						target='_blank'
					>
						{Liferay.Language.get('visit-our-privacy-policy')}
					</a>

					{'.'}
				</p>

				{expanded && (
					<>
						<p className='banner-description'>
							{Liferay.Language.get('show-more-accept-all')}
						</p>

						<p className='banner-description'>
							{Liferay.Language.get('show-more-decline-all')}
						</p>
					</>
				)}

				<ClayButton
					className='banner-show-more'
					displayType='link'
					onClick={() => setExpanded(!expanded)}
					size='sm'
				>
					{expanded
						? Liferay.Language.get('show-less')
						: Liferay.Language.get('show-more')}
				</ClayButton>
			</div>

			<div className='banner-buttons'>
				<ClayButton displayType='secondary' onClick={onAcceptAll}>
					{Liferay.Language.get('accept-all')}
				</ClayButton>

				<ClayButton displayType='secondary' onClick={onDeclineAll}>
					{Liferay.Language.get('decline-all')}
				</ClayButton>
			</div>
		</div>
	);
};

export default TrackingConsentBanner;
