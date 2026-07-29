import {setUriQueryValues} from 'shared/util/router';
import {useHistory} from 'react-router-dom';
import {useQueryParams} from 'shared/hooks/useQueryParams';

type Segment = {
	id: string;
	name: string;
};

interface ISegmentFilter {
	segmentId?: string;
	segmentName?: string;
	setSegment: (segment: Segment | null) => void;
}

/**
 * Reads the dashboard segment filter from the URL query and writes it back
 * there. The URL is the single source of truth, so the pages rendering the
 * filter do not mirror the selection in component state.
 */

export const useSegmentFilter = (): ISegmentFilter => {
	const {segmentId, segmentName} = useQueryParams();
	const history = useHistory();

	return {
		segmentId,
		segmentName,
		setSegment: (segment) =>
			history.push(
				setUriQueryValues({
					segmentId: segment?.id ?? null,
					segmentName: segment?.name ?? null,
				})
			),
	};
};
