import {SegmentCategories, SegmentTypes} from 'shared/util/constants';

export const getSegmentCategoryLabel = (segmentCategory?: SegmentCategories) =>
	segmentCategory === SegmentCategories.Account
		? Liferay.Language.get('account')
		: Liferay.Language.get('individual');

export const getSegmentTypeLabel = (segmentType: SegmentTypes) =>
	segmentType === SegmentTypes.RealTime
		? Liferay.Language.get('real-time')
		: Liferay.Language.get('batch');
