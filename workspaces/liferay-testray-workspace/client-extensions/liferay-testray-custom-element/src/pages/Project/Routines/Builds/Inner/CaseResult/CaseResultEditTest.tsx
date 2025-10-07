/* eslint-disable @liferay/portal/no-global-fetch */

/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayAlert from '@clayui/alert';
import {yupResolver} from '@hookform/resolvers/yup';
import {useForm} from 'react-hook-form';
import {useOutletContext, useParams} from 'react-router-dom';
import {KeyedMutator} from 'swr';
import {InferType} from 'yup';
import Form from '~/components/Form';
import Footer from '~/components/Form/Footer';
import Container from '~/components/Layout/Container';
import {withPagePermission} from '~/hoc/withPagePermission';
import useFormActions from '~/hooks/useFormActions';
import i18n from '~/i18n';
import yupSchema from '~/schema/yup';
import {Liferay} from '~/services/liferay';
import {
	MessageBoardMessage,
	TestrayCaseResult,
	testrayBuildImpl,
	testrayCaseResultImpl,
} from '~/services/rest';
import {CaseResultStatuses} from '~/util/statuses';

type CaseResultForm = InferType<typeof yupSchema.caseResult>;

type OutletContext = {
	caseResult: TestrayCaseResult;
	mbMessage: MessageBoardMessage;
	mutateCaseResult: KeyedMutator<TestrayCaseResult>;
};

const CaseResultEditTest = () => {
	const {
		form: {onClose, onError, onSave, onSubmit, submitting},
	} = useFormActions();

	const {buildId} = useParams();
	const {caseResultId} = useParams();

	const {caseResult, mbMessage, mutateCaseResult}: OutletContext =
		useOutletContext();

	const {
		formState: {errors},
		handleSubmit,
		register,
	} = useForm<CaseResultForm>({
		defaultValues: caseResult?.dueStatus
			? ({
					comment: mbMessage?.articleBody,
					dueStatus: [
						CaseResultStatuses.IN_PROGRESS,
						CaseResultStatuses.UNTESTED,
					].includes(caseResult?.dueStatus.key as CaseResultStatuses)
						? CaseResultStatuses.PASSED
						: caseResult?.dueStatus.key,
					issues: caseResult.issues,
				} as any)
			: {},
		resolver: yupResolver(yupSchema.caseResult),
	});

	const _onSubmit = async ({
		comment,
		dueStatus,
		issues = '',
	}: CaseResultForm) => {
		const _issues = issues
			.split(',')
			.map((name) => name.trim().toUpperCase())
			.filter(Boolean);

		try {
			const response = await onSubmit(
				{
					comment,
					dueStatus,
					id: caseResultId,
					issues: _issues.join(', '),
					mbMessageId: caseResult.mbMessageId,
					mbThreadId: caseResult.mbThreadId,
					userId: Liferay.ThemeDisplay.getUserId(),
				},
				{
					create: (data) => testrayCaseResultImpl.create(data),
					update: (id, data) =>
						testrayCaseResultImpl.update(id, data),
				}
			);

			if (buildId !== undefined) {
				testrayBuildImpl.updateBuildSummary(buildId);
			}

			if (_issues.length) {
				_issues.map((issue) => {
					Liferay.OAuth2Client.FromUserAgentApplication(
						'liferay-testray-etc-spring-boot-oaua'
					).fetch(`/jira/issues/${issue}`, {
						body: JSON.stringify({
							testrayCaseNames: [
								caseResult.r_caseToCaseResult_c_case?.name,
							],
						}),
						method: 'PUT',
					});
				});
			}

			mutateCaseResult({
				...response,
			});

			onSave();
		}
		catch (error) {
			onError(error);
		}
	};

	const inputProps = {
		errors,
		register,
		required: false,
	};

	return (
		<Container>
			<ClayAlert displayType="info">
				{i18n.translate(
					'clicking-save-will-assign-you-to-this-case-result'
				)}
			</ClayAlert>

			<Form.Select
				{...inputProps}
				className="container-fluid-max-md"
				defaultOption={false}
				label={i18n.translate('status')}
				name="dueStatus"
				options={[
					{
						label: i18n.translate('passed'),
						value: CaseResultStatuses.PASSED,
					},
					{
						label: i18n.translate('failed'),
						value: CaseResultStatuses.FAILED,
					},
					{
						label: i18n.translate('blocked'),
						value: CaseResultStatuses.BLOCKED,
					},
					{
						label: i18n.translate('test-fix'),
						value: CaseResultStatuses.TEST_FIX,
					},
				]}
				register={register}
				required
			/>

			<Form.Input
				{...inputProps}
				className="container-fluid-max-md"
				label={i18n.translate('issues')}
				name="issues"
			/>

			<Form.Input
				{...inputProps}
				className="container-fluid-max-md"
				label={i18n.translate('comment')}
				name="comment"
				type="textarea"
			/>

			<Footer
				onClose={onClose}
				onSubmit={handleSubmit(_onSubmit)}
				primaryButtonProps={{disabled: submitting}}
			/>
		</Container>
	);
};

export default withPagePermission(CaseResultEditTest, {
	restImpl: testrayCaseResultImpl,
});
