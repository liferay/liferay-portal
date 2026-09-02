import * as API from 'shared/api';
import ClayButton from '@clayui/button';
import ClayForm from '@clayui/form';
import ClayModal, {useModal} from '@clayui/modal';
import React from 'react';
import {addAlert} from 'shared/actions/alerts';
import {Alert, RangeSelectors} from 'shared/types';
import {CSVType, MAX_CSV_ENTRIES, useDownloadCSV} from './utils';
import {DownloadReportButton} from './DownloadReportButton';
import {sub} from 'shared/util/lang';
import {toLocale} from 'shared/util/numbers';
import {useDispatch} from 'react-redux';
import {useParams} from 'react-router-dom';

interface IDownloadStaticCSVReport {
	bordered?: boolean;
	children?: any;
	disabled: boolean;

	/**
	 * Resolves the filters and search query currently applied to the list
	 * being exported, so the CSV matches what is on screen. Callers that
	 * render a FrontendDataSet get these from its
	 * `additionalAPIURLParametersTransformer` (see assets/pages/List.tsx).
	 *
	 * This is a getter rather than a value because the data set reports its
	 * query outside React's render cycle, so callers keep it in a ref: the
	 * current value has to be read when the export is submitted, not when
	 * this component last rendered.
	 */
	getFDSQuery?: () => {filter: string; query: string};
	objectType?: string;
	rangeSelectors?: RangeSelectors;
	segmentId?: string;
	type: CSVType;
	typeLang: string;
}

export const DownloadStaticCSVReport: React.FC<IDownloadStaticCSVReport> = ({
	bordered,
	children,
	disabled,
	getFDSQuery,
	objectType,
	rangeSelectors,
	segmentId,
	type,
	typeLang,
}) => {
	const dispatch = useDispatch();
	const generateURL = useDownloadCSV({objectType, segmentId, type});
	const {observer, onOpenChange, open} = useModal();
	const {channelId, groupId} = useParams();

	return (
		<>
			{children ? (
				React.cloneElement(children, {
					disabled,
					onClick: () => onOpenChange(true),
				})
			) : (
				<DownloadReportButton
					bordered={bordered}
					disabled={disabled}
					onClick={() => onOpenChange(true)}
				/>
			)}

			{open && (
				<Modal
					isFDSExport={Boolean(getFDSQuery)}
					observer={observer}
					onClose={() => onOpenChange(false)}
					onSubmit={async () => {
						onOpenChange(false);

						try {
							const fdsQuery = getFDSQuery?.();

							const url = generateURL(rangeSelectors, {
								filter: fdsQuery?.filter,
								query: fdsQuery?.query,
							});
							const response = await API.csv.fetchCSV(url);

							if (!response.ok) {
								throw new Error();
							}

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

							const a = document.createElement('a');

							a.href = url;
							a.click();

							const count = await API.csv.fetchCount({
								channelId: channelId!,
								filter: fdsQuery?.filter,
								groupId: groupId!,
								objectType,
								query: fdsQuery?.query,
								segmentId,
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
					typeLang={typeLang}
				/>
			)}
		</>
	);
};

const Modal = ({
	isFDSExport,
	observer,
	onClose,
	onSubmit,
	typeLang,
}: {
	isFDSExport: boolean;
	observer: any;
	onClose: () => void;
	onSubmit: () => void;
	typeLang: string;
}) => (
	<ClayModal observer={observer}>
		<ClayForm
			onSubmit={(event) => {
				event.preventDefault();

				onSubmit();
			}}
		>
			<ClayModal.Header>
				{Liferay.Language.get('download-reports')}
			</ClayModal.Header>

			<ClayModal.Body>
				<p>
					{
						sub(
							isFDSExport
								? Liferay.Language.get(
										'the-generated-CSV-file-will-respect-the-current-filter-and-search-results,-with-a-maximum-of-x-entries-supported-per-export.-please-ensure-that-any-desired-changes-have-been-successfully-applied-before-downloading-the-x-list'
									)
								: Liferay.Language.get(
										'the-generated-csv-file-supports-up-to-x-entries-per-export-and-it-will-respect-the-current-ordering-and-search-results.-please-ensure-that-any-desired-changes-have-been-successfully-applied-before-downloading-the-x-list'
									),
							[toLocale(MAX_CSV_ENTRIES), typeLang]
						) as string
					}
				</p>
			</ClayModal.Body>

			<ClayModal.Footer
				last={
					<ClayButton.Group spaced>
						<ClayButton
							data-testid="cancel"
							displayType="secondary"
							onClick={onClose}
						>
							{Liferay.Language.get('cancel')}
						</ClayButton>

						<ClayButton data-testid="submit" type="submit">
							{Liferay.Language.get('download')}
						</ClayButton>
					</ClayButton.Group>
				}
			/>
		</ClayForm>
	</ClayModal>
);
