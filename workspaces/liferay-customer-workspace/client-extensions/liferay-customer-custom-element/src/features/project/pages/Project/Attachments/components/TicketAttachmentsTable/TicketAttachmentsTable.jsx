/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {useModal} from '@clayui/core';
import {useEffect, useState} from 'react';
import i18n from '~/utils/I18n';
import ActionTable from '~/components/ActionTable';
import useJiraTicketURL from '~/hooks/useJiraTicketURL';
import {getTicketAttachments} from '~/services/liferay/api';
import useMyUserAccountByAccountExternalReferenceCode from '~/features/project/pages/Project/TeamMembers/components/TeamMembersTable/hooks/useMyUserAccountByAccountExternalReferenceCode';
import DeleteTicketAttachmentModal from './components/DeleteTicketAttachmentModal/DeleteTicketAttachmentModal';
import TicketAttachmentsTableEmpty from './components/TicketAttachmentsTableEmpty';
import OptionsColumn from './components/columns/OptionsColumn';
import useDelete from './hooks/useDeleteTicketAttachment';
import useDownload from './hooks/useDownloadTicketAttachment';
import usePagination from './hooks/usePaginationTicketAttachments';
import useSort from './hooks/useSortTicketAttachments';
import getAttachmentDownloadUrl from './utils/getAttachmentDownloadUrl';
import getAttachmentFormattedDateTime from './utils/getAttachmentFormattedDateTime';
import {getColumns} from './utils/getColumns';

import './TicketAttachmentsTable.css';

const TicketAttachmentsTable = ({
	koroneikiAccount,
	loading: koroneikiAccountLoading,
}) => {
	const {
		data: myUserAccountData,
		loading,
	} = useMyUserAccountByAccountExternalReferenceCode(
		koroneikiAccount?.accountKey,
		koroneikiAccountLoading
	);
	const loggedUserAccount = myUserAccountData?.myUserAccount;
	const [ticketAttachments, setTicketAttachments] = useState([]);
	const [selectedTicketAttachment, setSelectedTicketAttachment] = useState();

	const {handleSortChange, sortConfig} = useSort();
	const {
		paginationConfig,
		sortedTicketAttachmentsFilteredPerPage,
	} = usePagination(sortConfig, ticketAttachments);
	const {onDownload} = useDownload();
	const {isDeleting, onDelete} = useDelete();
	const {observer, onOpenChange, open} = useModal();

	useEffect(() => {
		const fetchTicketAttachments = async () => {
			const ticketAttachmentsResponse = await getTicketAttachments(
				`accountKey eq '${koroneikiAccount?.accountKey}' and (state eq 0 or state eq null) and status/any(s:s eq 0)`
			);

			const ticketAttachments = ticketAttachmentsResponse.items.map(
				async (ticketAttachment) => {
					return {
						accountKey: ticketAttachment.accountKey,
						creatorId: ticketAttachment.creator.id,
						creatorName: ticketAttachment.creator.name,
						dateCreated: ticketAttachment.dateCreated,
						downloadUrl: await getAttachmentDownloadUrl(ticketAttachment.id),
						fileName: ticketAttachment.fileName,
						fileSize: ticketAttachment.fileSize,
						storageBucket: ticketAttachment.storageBucket,
						ticketAttachmentId: ticketAttachment.id,
						ticketId: ticketAttachment.jiraIssueKey,
					};
				}
			);

			setTicketAttachments(await Promise.all(ticketAttachments));
		};
		fetchTicketAttachments();
	}, [
		koroneikiAccount?.accountKey,
		loggedUserAccount?.id,
		paginationConfig.activePage,
		paginationConfig.itemsPerPage,
		isDeleting,
	]);

	return (
		<>
			{open && (
				<DeleteTicketAttachmentModal
					modalTitle={i18n.translate('delete-attached-file')}
					observer={observer}
					onClose={() => onOpenChange(false)}
					onDelete={() => {
						onDelete(selectedTicketAttachment?.ticketAttachmentId);
						onOpenChange(false);
						Liferay.Util.openToast({
							message: i18n.translate('was-deleted-successfully'),
							title: selectedTicketAttachment?.fileName,
							type: 'success',
						});
					}}
					removing={isDeleting}
				>
					<p className="my-0 text-neutral-10">
						{i18n.translate(
							'are-you-sure-you-want-to-delete-this-attached-file'
						)}
					</p>

					<p className="font-weight-bold mt-4 text-neutral-10">
						- {selectedTicketAttachment?.fileName}
					</p>
				</DeleteTicketAttachmentModal>
			)}

			{sortedTicketAttachmentsFilteredPerPage &&
			paginationConfig.totalCount > 0 &&
			!loading ? (
				<div className="cp-ticket-attachments-table-wrapper">
					<ActionTable
						className="border-0"
						columns={getColumns()}
						handleSortChange={handleSortChange}
						hasPagination
						hasSorting
						isLoading={loading}
						paginationConfig={paginationConfig}
						rows={sortedTicketAttachmentsFilteredPerPage?.map(
							(ticketAttachment) => ({
								attached: (
									<div className="d-flex flex-column">
										<div className="m-0 text-neutral-10 text-truncate">
											{getAttachmentFormattedDateTime(
												ticketAttachment?.dateCreated
											)}
										</div>

										<div className="m-0 text-neutral-7 text-paragraph-sm text-truncate">
											{`${i18n.translate('by')} ${
												ticketAttachment?.creatorName
											}`}
										</div>
									</div>
								),
								fileName: (
									<a
										className="m-0 text-truncate"
										href={ticketAttachment?.downloadUrl}
									>
										{ticketAttachment?.fileName}
									</a>
								),
								fileSize: (
									<div className="m-0 text-neutral-10 text-paragraph text-truncate">
										{ticketAttachment?.fileSize}
									</div>
								),
								options: (
									<OptionsColumn
										hasDeletePermissions={
											loggedUserAccount
												?.selectedAccountSummary
												.hasAdministratorRole ||
											loggedUserAccount?.id ===
												ticketAttachment.creatorId
										}
										onDelete={onDelete}
										onDownload={onDownload}
										onOpenChange={onOpenChange}
										setSelectedTicketAttachment={
											setSelectedTicketAttachment
										}
										ticketAttachment={ticketAttachment}
									/>
								),
								ticket: (
									<a
										className="m-0 text-truncate"
										href={`${useJiraTicketURL(ticketAttachment?.ticketId)}`}
									>
										{'#' + ticketAttachment?.ticketId}
									</a>
								),
							})
						)}
					/>
				</div>
			) : (
				!sortedTicketAttachmentsFilteredPerPage ||
				(paginationConfig.totalCount === 0 && !loading && (
					<TicketAttachmentsTableEmpty
						description={i18n.translate(
							'there-are-no-items-to-display'
						)}
						title={i18n.translate('no-attachments-yet')}
					/>
				))
			)}
		</>
	);
};

export default TicketAttachmentsTable;
