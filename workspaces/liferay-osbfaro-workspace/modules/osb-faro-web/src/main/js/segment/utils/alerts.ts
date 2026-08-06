import {AlertTypes} from 'shared/components/Alert';
import {Segment} from 'shared/util/records';
import {SegmentStates} from 'shared/util/constants';

export const getSegmentAlerts = (segment: Segment) => {
	if (segment.state === SegmentStates.InProgress) {
		return [
			{
				alertType: AlertTypes.Info,
				message: Liferay.Language.get(
					'segment-data-is-processing-please-check-back-later'
				),
				stripe: true,
			},
		];
	}

	if (segment.state === SegmentStates.Disabled) {
		return [
			{
				alertType: AlertTypes.Danger,
				message: Liferay.Language.get(
					'this-segment-is-disabled-because-some-criteria-has-been-affected-by-removal-of-a-data-source.-to-continue-using-this-segment-please-update-the-criteria'
				),
				stripe: true,
			},
		];
	}

	return [];
};
