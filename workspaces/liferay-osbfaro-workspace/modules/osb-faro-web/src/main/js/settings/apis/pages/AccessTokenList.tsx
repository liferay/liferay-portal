import * as API from 'shared/api';
import Alerts, {AlertTypes} from 'shared/components/Alert';
import BasePage from 'settings/components/base-page/BasePage';
import Card from 'shared/components/Card';
import ClayButton from '@clayui/button';
import ClayLink from '@clayui/link';
import CopyButton from 'shared/components/CopyButton';
import GenerateTokenCard from '../components/GenerateTokenCard';
import Loading, {Align} from 'shared/components/Loading';
import moment from 'moment';
import React, {useState} from 'react';
import Table from 'shared/components/table';
import TokenCell from '../components/TokenCell';
import URLConstants from 'shared/util/url-constants';
import {AccessToken} from '../types';
import {addAlert} from 'shared/actions/alerts';
import {Alert} from 'shared/types';
import {ApisPath} from 'shared/util/url-constants';
import {close, modalTypes, open} from 'shared/actions/modals';
import {compose} from 'redux';
import {connect, ConnectedProps} from 'react-redux';
import {getCustomDateFormat} from 'shared/util/date';
import {ExpirationPeriod} from 'shared/util/constants';
import {formatDateToTimeZone, getDateNow} from 'shared/util/date';
import {RootState} from 'shared/store';
import {sub} from 'shared/util/lang';
import {
	withAdminPermission,
	withError,
	withLoading,
	withQuery,
} from 'shared/hoc';
import type {Column} from 'shared/components/table/Row';

export const isExpired = (expirationDate: string) =>
	moment.utc(expirationDate).isSameOrBefore(getDateNow());

const getTimestamp = (date: string | Date) =>
	Math.floor(new Date(date).getTime() / 1000);

const isIndefinite = ({
	createDate,
	expirationDate,
}: {
	createDate: string;
	expirationDate: string;
}) =>
	getTimestamp(expirationDate) - getTimestamp(createDate) ===
	Number(ExpirationPeriod.Indefinite);

const connector = connect(
	(store: RootState, {groupId}: {groupId: string}) => ({
		timeZoneId: store.getIn([
			'projects',
			groupId,
			'data',
			'timeZone',
			'timeZoneId',
		]),
	}),
	{addAlert, close, open}
);

type PropsFromRedux = ConnectedProps<typeof connector>;

const TokenList: React.FC<
	{
		groupId: string;
		refetch: () => Promise<any>;
		tokens: AccessToken[];
	} & PropsFromRedux
> = ({addAlert, close, groupId, open, refetch, timeZoneId, tokens}) => {
	const [loading, setLoading] = useState(false);
	const [onCloseAlert, setOnCloseAlert] = useState(false);

	const handleError = () => {
		setLoading(false);

		addAlert({
			alertType: Alert.Types.Error,
			message: Liferay.Language.get('error'),
			timeout: false,
		});
	};

	const handleSuccess = (message: string) => {
		setLoading(false);

		addAlert({
			alertType: Alert.Types.Success,
			message,
		});

		refetch();
	};

	const hasActiveToken = tokens.find(
		(token) => !isExpired(token.expirationDate)
	);

	return (
		<div className="col-xl-8 pl-0">
			{!!tokens.length && !hasActiveToken && !onCloseAlert && (
				<Alerts
					iconSymbol="warning-full"
					onClose={() => setOnCloseAlert(true)}
					title={Liferay.Language.get('warning')}
					type={AlertTypes.Warning}
				>
					{Liferay.Language.get('expired-token-warning')}
				</Alerts>
			)}

			{(!tokens.length || !hasActiveToken) && (
				<GenerateTokenCard
					groupId={groupId}
					onError={handleError}
					onSuccess={handleSuccess}
					token={tokens[0]?.token}
				/>
			)}

			<Card>
				<Card.Body>
					<div className="align-items-start d-flex flex-column justify-content-between">
						<div className="h4 mb-4">
							{Liferay.Language.get('token-information')}
						</div>

						<div className="h5">
							{Liferay.Language.get('root-endpoint')}
						</div>

						<span className="text-secondary">
							{window.location.origin + ApisPath}
						</span>
					</div>
				</Card.Body>

				{!!tokens.length && (
					<Table
						className="mb-0"
						columns={
							[
								{
									accessor: 'token',
									cellRenderer: TokenCell,
									label: Liferay.Language.get('token'),
									sortable: false,
								},
								{
									accessor: 'createDate',
									dataFormatter: (val: string) =>
										formatDateToTimeZone(
											val,
											getCustomDateFormat(),
											timeZoneId
										),
									label: Liferay.Language.get('date-created'),
									sortable: false,
								},
								{
									accessor: 'expirationDate',
									cellRenderer: ({
										data,
									}: {
										data: AccessToken;
									}) => {
										if (isIndefinite(data)) {
											return (
												<td>
													{Liferay.Language.get(
														'indefinite'
													)}
												</td>
											);
										}

										return (
											<td>
												{formatDateToTimeZone(
													data.expirationDate,
													getCustomDateFormat(),
													timeZoneId
												)}
											</td>
										);
									},
									label: Liferay.Language.get('expiration'),
									sortable: false,
								},
							].filter(Boolean) as Column[]
						}
						items={tokens}
						renderInlineRowActions={({
							data: {expirationDate, token},
						}) => {
							if (isExpired(expirationDate)) return null;

							return (
								<>
									<CopyButton
										displayType="secondary"
										text={token}
									/>

									<ClayButton
										className="button-root"
										disabled={loading}
										displayType="secondary"
										onClick={() => {
											open(
												modalTypes.CONFIRMATION_MODAL,
												{
													message: (
														<div className="text-secondary">
															<div>
																<strong>
																	{Liferay.Language.get(
																		'are-you-sure-you-want-to-revoke-this-token'
																	)}
																</strong>
															</div>

															{Liferay.Language.get(
																'you-will-need-to-generate-a-new-token-to-continue-using-this-api'
															)}
														</div>
													),
													modalVariant:
														'modal-warning',
													onClose: close,
													onSubmit: () => {
														setLoading(true);

														API.apiTokens
															.revoke({
																groupId,
																token,
															})
															.then(() =>
																handleSuccess(
																	Liferay.Language.get(
																		'token-successfully-revoked'
																	)
																)
															)
															.catch(handleError);
													},
													submitButtonDisplay:
														'warning',
													title: Liferay.Language.get(
														'revoke-token'
													),
													titleIcon: 'warning-full',
												}
											);
										}}
									>
										{Liferay.Language.get('revoke')}

										{loading && (
											<Loading align={Align.Right} />
										)}
									</ClayButton>
								</>
							);
						}}
						rowIdentifier="token"
					/>
				)}
			</Card>
		</div>
	);
};

const ListWithData = compose<any>(
	connector,
	withQuery(
		API.apiTokens.search,
		({groupId}: {groupId: string}) => ({groupId}),
		({
			data,
			...otherParams
		}: {
			data: AccessToken[];
			[key: string]: any;
		}) => ({
			tokens: data,
			...otherParams,
		})
	),
	withLoading(),
	withError({page: false})
)(TokenList);

interface IAccessTokenListProps {
	groupId: string;
}

export const AccessTokenList: React.FC<IAccessTokenListProps> = ({groupId}) => (
	<BasePage
		className="access-token-list-root"
		pageDescription={sub(
			Liferay.Language.get(
				'access-this-workspaces-data-via-api-using-an-access-token.-a-full-list-of-endpoints-is-available-in-the-x'
			),
			[
				<ClayLink
					href={URLConstants.APIOverviewDocumentationLink}
					key="API_OVERVIEW_DOCUMENTATION"
					target="_blank"
				>
					{Liferay.Language.get('documentation').toLowerCase()}
				</ClayLink>,
			],
			false
		)}
		pageTitle={Liferay.Language.get('access-tokens')}
	>
		<ListWithData groupId={groupId} />
	</BasePage>
);

export default withAdminPermission(AccessTokenList);
