import BaseCard from 'shared/components/base-card';
import Card from 'shared/components/Card';
import ClayLink from '@clayui/link';
import React from 'react';
import {compose} from 'redux';
import {GeomapCard} from 'shared/components/geo-map/GeomapCard';
import {HOC_CARD_PROPTYPES} from 'shared/util/proptypes';
import {PropTypes} from 'prop-types';
import {withEmpty, withError, withLoading} from 'shared/hoc/util';

/**
 * HOC
 * @description Locations Card Data
 */
const withLocationsCard = (
	withLocations,
	withCountries,
	{documentationTitle, documentationUrl, reportContainer, title}
) => {
	const LocationsGeoMap = compose(
		withLocations(),
		withCountries(),
		withLoading(),
		withError({page: false}),
		withEmpty({
			description: (
				<>
					<span className='mr-1'>
						{Liferay.Language.get(
							'check-back-later-to-verify-if-data-has-been-received-from-your-data-sources'
						)}
					</span>

					<ClayLink
						href={documentationUrl}
						key='DOCUMENTATION'
						target='_blank'
					>
						{documentationTitle}
					</ClayLink>
				</>
			),
			title
		})
	)(GeomapCard);

	LocationsGeoMap.propTypes = HOC_CARD_PROPTYPES;

	const defaultProps = {
		className: 'analytics-locations-card',
		metricLabel: Liferay.Language.get('views')
	};

	const propTypes = {
		metricLabel: PropTypes.string
	};

	const LocationsCard = ({
		className,
		label,
		legacyDropdownRangeKey,
		metricLabel
	}) => (
		<BaseCard
			className={className}
			label={label}
			legacyDropdownRangeKey={legacyDropdownRangeKey}
			minHeight={536}
			reportContainer={reportContainer}
		>
			{({accountId, experienceId, filters, interval, rangeSelectors, router}) => (
				<Card.Body>
					<LocationsGeoMap
						accountId={accountId}
						experienceId={experienceId}
						filters={filters}
						interval={interval}
						metricLabel={metricLabel}
						rangeSelectors={rangeSelectors}
						router={router}
					/>
				</Card.Body>
			)}
		</BaseCard>
	);

	LocationsCard.defaultProps = defaultProps;
	LocationsCard.propTypes = propTypes;

	return LocationsCard;
};

export {withLocationsCard};
export default withLocationsCard;
