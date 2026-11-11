import React from 'react';
import {addAlert} from 'shared/actions/alerts';
import {Alert, RangeSelectors} from 'shared/types';
import {CSVType, formatDate, MAX_CSV_ENTRIES, useDownloadCSV} from './utils';
import {DownloadReportButton} from './DownloadReportButton';
import {DownloadReportModal, ReportType} from './DownloadReportModal';
import {fetchCount} from 'shared/api/csv';
import {RangeKeyTimeRanges} from 'shared/util/constants';
import {sub} from 'shared/util/lang';
import {toLocale} from 'shared/util/numbers';
import {useDispatch} from 'react-redux';
import {useModal} from '@clayui/modal';
import {useParams} from 'react-router-dom';
import {useUnsafeQueryRangeSelectors} from 'shared/hooks/useUnsafeQueryRangeSelectors';

export interface IDownloadReport {
	assetId?: string;
	assetType?: string;
	disabled: boolean;
	individualId?: string;
	type: CSVType;
	typeLang: string;
}

const formatRangeSelectors = (rangeSelectors?: RangeSelectors) => {
	if (rangeSelectors?.rangeKey === RangeKeyTimeRanges.CustomRange) {
		return {
			rangeEnd: formatDate(rangeSelectors.rangeEnd ?? ''),
			rangeKey: RangeKeyTimeRanges.CustomRange,
			rangeStart: formatDate(rangeSelectors.rangeStart ?? ''),
		};
	}

	return rangeSelectors;
};

const DownloadCSVReport: React.FC<IDownloadReport> = ({
	assetId,
	assetType,
	disabled,
	individualId,
	type,
	typeLang,
}) => {
	const dispatch = useDispatch();
	const generateURL = useDownloadCSV({
		assetId,
		assetType,
		individualId,
		type,
	});
	const {observer, onOpenChange, open} = useModal();
	const rangeSelectors = useUnsafeQueryRangeSelectors();
	const {channelId, groupId} = useParams();

	return (
		<div className="download-report">
			<DownloadReportButton
				disabled={disabled}
				onClick={() => onOpenChange(true)}
			/>

			{open && (
				<DownloadReportModal
					infoMessage={
						sub(
							Liferay.Language.get(
								'the-generated-CSV-file-will-respect-the-current-filter-and-search-results,-with-a-maximum-of-x-entries-supported-per-export.-please-ensure-that-any-desired-changes-have-been-successfully-applied-before-downloading-the-x-list'
							),
							[toLocale(MAX_CSV_ENTRIES), typeLang]
						) as string
					}
					observer={observer}
					onClose={() => onOpenChange(false)}
					onSubmit={async (rangeSelectors) => {
						try {
							const url = generateURL(rangeSelectors);
							const response = await fetch(url);

							if (!response.ok) {
								throw new Error();
							}

							const a = document.createElement('a');

							a.href = url;
							a.click();

							dispatch(
								addAlert({
									alertType: Alert.Types.Default,
									message: sub(
										Liferay.Language.get(
											'the-x-file-is-being-generated-and-your-download-will-start-soon'
										),
										['CSV']
									) as string,
								})
							);

							const formattedRangeSelector =
								formatRangeSelectors(rangeSelectors);

							const count = await fetchCount({
								assetId,
								assetType,
								channelId: channelId!,
								fromDate:
									formattedRangeSelector?.rangeStart ??
									undefined,
								groupId: groupId!,
								individualId,
								rangeKey: formattedRangeSelector?.rangeKey,
								toDate:
									formattedRangeSelector?.rangeEnd ??
									undefined,
								type,
							});

							if (count > MAX_CSV_ENTRIES) {
								dispatch(
									addAlert({
										alertType: Alert.Types.Warning,
										message: sub(
											Liferay.Language.get(
												'the-csv-file-reached-x-entries'
											),
											[toLocale(MAX_CSV_ENTRIES)]
										),
									})
								);
							}
						}
						catch (e) {
							dispatch(
								addAlert({
									alertType: Alert.Types.Error,
									message: Liferay.Language.get(
										'it-was-not-possible-to-generate-a-csv-file-at-this-moment.-please-try-again-later'
									),
								})
							);
						}
					}}
					rangeSelectors={rangeSelectors}
					type={ReportType.CSV}
				/>
			)}
		</div>
	);
};

export default DownloadCSVReport;
