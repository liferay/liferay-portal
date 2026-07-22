import ClayAlert from '@clayui/alert';
import ClayButton from '@clayui/button';
import ClayForm from '@clayui/form';
import ClayModal from '@clayui/modal';
import React, {useState} from 'react';
import {Align} from '@clayui/drop-down';
import {DropdownRangeKey} from '../dropdown-range-key/DropdownRangeKey';
import {pickBy} from 'lodash';
import {RangeSelectors} from 'shared/types';
import {setUriQueryValues} from 'shared/util/router';
import {Text} from '@clayui/core';
import {useHistoryAdapter} from 'shared/hooks/useHistoryAdapter';

export enum ReportType {
	CSV = 'CSV',
	PDF = 'PDF',
}

interface IDownloadReportModal {
	children?: React.ReactNode;
	dateRangeDescription?: string;
	disabled?: boolean;
	infoMessage: string;
	observer: any;
	onClose: () => void;
	onSubmit: (rangeSelectors?: RangeSelectors) => void;
	rangeSelectors?: RangeSelectors;
	showDateRange?: boolean;
	type?: ReportType;
}

export const DownloadReportModal: React.FC<IDownloadReportModal> = ({
	children,
	dateRangeDescription = Liferay.Language.get(
		'only-select-a-date-range-if-you-want-to-modify-the-current-date-filter'
	),
	disabled = false,
	infoMessage,
	observer,
	onClose,
	onSubmit,
	rangeSelectors: initialRangeSelectors,
	showDateRange = true,
	type,
}) => {
	const history = useHistoryAdapter();
	const [openAlert, setOpenAlert] = useState(true);
	const [submitDisabled, setSubmitDisabled] = useState(false);
	const [rangeSelectors, setRangeSelectors] = useState<
		RangeSelectors | undefined
	>(initialRangeSelectors);

	return (
		<ClayModal observer={observer}>
			<ClayForm>
				<div className="modal-content">
					<ClayModal.Header>
						{Liferay.Language.get('download-reports')}
					</ClayModal.Header>

					{openAlert && (
						<ClayAlert
							onClose={() => setOpenAlert(false)}
							title={Liferay.Language.get('info')}
							variant="stripe"
						>
							{infoMessage}
						</ClayAlert>
					)}

					<ClayModal.Body>
						{showDateRange && (
							<ClayForm.Group className="mb-0">
								<label htmlFor="timeRange">
									{Liferay.Language.get('date-range')}
								</label>

								<p>
									<Text size={3}>{dateRangeDescription}</Text>
								</p>

								<DropdownRangeKey
									alignmentPosition={Align.BottomLeft}
									legacy={false}
									onRangeSelectorChange={setRangeSelectors}
									rangeSelectors={rangeSelectors}
								/>
							</ClayForm.Group>
						)}

						{children}
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

								<ClayButton
									data-testid="submit"
									disabled={disabled || submitDisabled}
									onClick={() => {
										setSubmitDisabled(true);

										onClose();

										if (type === ReportType.CSV) {
											onSubmit(rangeSelectors);
											return;
										}

										if (rangeSelectors) {
											history.push(
												setUriQueryValues(
													pickBy({
														downloadReport: true,
														...rangeSelectors,
													})
												)
											);

											const observer =
												new MutationObserver(() => {
													const loadingElement =
														document.querySelectorAll(
															'.page-container .loading-animation'
														);

													if (
														!loadingElement.length
													) {
														observer.disconnect();

														onSubmit();
													}
												});

											observer.observe(document.body, {
												attributes: true,
												characterData: true,
												childList: true,
												subtree: true,
											});
										}
										else {
											onSubmit();
										}
									}}
								>
									{Liferay.Language.get('download')}
								</ClayButton>
							</ClayButton.Group>
						}
					/>
				</div>
			</ClayForm>
		</ClayModal>
	);
};
