import sendRequest from 'shared/util/request';
import {CSVType} from 'shared/components/download-report/utils';

export const fetchCSV = (url: string) => fetch(url);

export const fetchCount = ({
	groupId,
	type,
	...data
}: {
	assetId?: string;
	assetType?: string;
	channelId: string;
	filter?: string;
	fromDate?: string;
	groupId: string;
	individualId?: string;
	objectType?: string;
	query?: string;
	segmentId?: string;
	rangeKey?: string;
	toDate?: string;
	type: CSVType;
}) =>
	sendRequest({
		data,
		method: 'GET',
		path: `main/${groupId}/reports/export/csv/${type}/count`,
	});
